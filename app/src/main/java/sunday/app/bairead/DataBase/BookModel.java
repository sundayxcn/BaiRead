package sunday.app.bairead.DataBase;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;

import sunday.app.bairead.UI.DeferredHandler;

/**
 * Created by sunday on 2016/12/13.
 */

public class BookModel {
    private Context mContext;

    public interface CallBack{
        void addBookDataFinish(BookInfo bookInfo,boolean success);
        void deleteBookDataFinish(BookInfo bookInfo,boolean success);
    }


    private CallBack callBack;

    private static final HandlerThread sWorkerThread = new HandlerThread("baiRead-loader");
    static {
        sWorkerThread.start();
    }

    private final DeferredHandler mHandler = new DeferredHandler();
    private static final Handler sWorker = new Handler(sWorkerThread.getLooper());

    public BookModel(Context context){
        mContext = context;
    }


    public void setCallBack(CallBack callBack){
        this.callBack = callBack;
    }

    /** Runs the specified runnable immediately if called from the main thread, otherwise it is
     * posted on the main thread handler. */
    private void runOnMainThread(Runnable r) {
        runOnMainThread(r, 0);
    }

    private void runOnMainThread(Runnable r, int type) {
        if (sWorkerThread.getThreadId() == Process.myTid()) {
            // If we are on the worker thread, post onto the main handler
            mHandler.post(r);
        } else {
            r.run();
        }
    }

    /** Runs the specified runnable immediately if called from the worker thread, otherwise it is
     * posted on the worker thread handler. */
    private static void runOnWorkerThread(Runnable r) {
        if (sWorkerThread.getThreadId() == Process.myTid()) {
            r.run();
        } else {
            // If we are not on the worker thread, then post to the worker handler
            sWorker.post(r);
        }
    }


    public void addBook(final BookInfo bookInfo){
        final ContentValues detailValues = new ContentValues();
        final ContentValues chapterValues = new ContentValues();
        final ContentResolver cr = mContext.getContentResolver();
        BaiReadApplication application = (BaiReadApplication) mContext.getApplicationContext();

        final long id = application.getBookContentProvider().generateNewId();
        detailValues.put(BookSetting.Detail._ID,id);
        bookInfo.bookDetail.setId(id);
        bookInfo.bookDetail.onAddToDatabase(detailValues);


        chapterValues.put(BookSetting.Chapter.ID,id);
        bookInfo.bookChapter.onAddToDatabase(chapterValues);

        runOnWorkerThread(new Runnable() {
            @Override
            public void run() {

                Uri uri = cr.insert(BookSetting.Detail.CONTENT_URI,detailValues);

                Uri uri2 = cr.insert(BookSetting.Chapter.CONTENT_URI,chapterValues);

                boolean success = (uri != null && uri2 != null);
                postAddCallBack(bookInfo,success);

            }
        });
    }


    public void startLoad(){

    }

    public void postAddCallBack(final BookInfo bookInfo, final boolean success){
        if(callBack != null) {
            runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    callBack.addBookDataFinish(bookInfo,success);
                }
            });
        }
    }

    public void postDeleteCallBack(final BookInfo bookInfo, final boolean success){
        if(callBack != null) {
            runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    callBack.deleteBookDataFinish(bookInfo,success);
                }
            });
        }
    }

    public void deleteBook(final BookInfo bookInfo){

        final long id = bookInfo.bookDetail.getId();

        runOnWorkerThread(new Runnable() {
            @Override
            public void run() {
                ContentResolver cr = mContext.getContentResolver();
                Uri uri = BookSetting.Detail.getContentUri(id,true);
                cr.delete(uri,null,null);

                postDeleteCallBack(bookInfo,(uri != null));

                /*删除和本书相关的所有来源信息
                 * */
                uri = BookSetting.Chapter.CONTENT_URI;
                String where = "nameId = ?";
                cr.delete(uri,where,new String[]{String.valueOf(id)});

                /*删除和本书相关的所有书签
                 * */
                uri = BookSetting.Mark.CONTENT_URI;
                cr.delete(uri,where,new String[]{String.valueOf(id)});


            }
        });

    }


    public void addBookMark(BookMark bookMark){
        long nameId = bookMark.getChapterIndex();
        int chapterIndex = bookMark.getChapterIndex();
        final ContentResolver cr = mContext.getContentResolver();
        final ContentValues values = new ContentValues();
        values.put(BookSetting.Mark.ID,nameId);
        values.put(BookSetting.Mark.INDEX,chapterIndex);

        runOnWorkerThread(new Runnable() {
            @Override
            public void run() {
                cr.insert(BookSetting.Mark.CONTENT_URI,values);
            }
        });
    }

    public void deleteBookMark(BookMark bookMark){
        final int index = bookMark.getChapterIndex();
        runOnWorkerThread(new Runnable() {
            @Override
            public void run() {
                String where = "chapterIndex = ?";
                ContentResolver cr = mContext.getContentResolver();
                Uri uri = BookSetting.Mark.CONTENT_URI;
                cr.delete(uri,where,new String[]{String.valueOf(index)});
            }
        });
    }

    /**
     * 判断是否在书架中
     * */
    public boolean isCase(BookDetail bookDetail){
        String name = bookDetail.getName();
        String author = bookDetail.getAuthor();
        final ContentResolver cr = mContext.getContentResolver();
        Uri uri = BookSetting.Detail.CONTENT_URI;
        Cursor cursor = cr.query(uri,null,"name = ? and author = ?",new String[]{name,author},null);
        if(cursor.getCount() > 0){
            cursor.close();
            return true;
        }
        return false;

    }


}
