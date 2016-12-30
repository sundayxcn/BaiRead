package sunday.app.bairead.DataBase;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.util.Log;

import java.util.ArrayList;

import sunday.app.bairead.Download.SearchManager;
import sunday.app.bairead.Parse.BookChapterParse;
import sunday.app.bairead.Parse.JsoupParse;
import sunday.app.bairead.Tool.FileManager;
import sunday.app.bairead.UI.DeferredHandler;

/**
 * Created by sunday on 2016/12/13.
 */

public class BookModel {

    private static final HandlerThread sWorkerThread = new HandlerThread("baiRead-loader");
    static {
        sWorkerThread.start();
    }
    public interface CallBack{
        void loadFinish(ArrayList<BookInfo> list);
        void addBookDataFinish(BookInfo bookInfo,boolean success);
        void deleteBookDataFinish(BookInfo bookInfo,boolean success);
    }

    private Context mContext;
    private CallBack callBack;
    private final DeferredHandler mHandler = new DeferredHandler();
    private static final Handler sWorker = new Handler(sWorkerThread.getLooper());
    private ArrayList<BookInfo> mBookInfoList = new ArrayList<>();



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
                if(success) {
                    mBookInfoList.add(bookInfo);
                }
                postAddCallBack(bookInfo,success);

            }
        });
    }


    public void startLoad(){
//        runOnWorkerThread(new Runnable() {
//            @Override
//            public void run() {
                ArrayList<BookInfo> list = loadAllBook();
                if(callBack != null){
                    callBack.loadFinish(list);
                }
//
//            }
//        });

    }

    public ArrayList<BookInfo> loadAllBook(){
        mBookInfoList.clear();
        ArrayList<BookInfo> bookList = mBookInfoList;
        final ContentResolver cr = mContext.getContentResolver();
        Uri uri = BookSetting.Detail.CONTENT_URI;
        Cursor cursor = cr.query(uri,null,null,null,null);

        final int detailId = cursor.getColumnIndexOrThrow(BookSetting.Detail._ID);
        final int detailName = cursor.getColumnIndexOrThrow(BookSetting.Detail.NAME);
        final int detailAuthor = cursor.getColumnIndexOrThrow(BookSetting.Detail.AUTHOR);
        final int detailCoverImageLink = cursor.getColumnIndexOrThrow(BookSetting.Detail.COVER_IMAGE_LINK);
        final int detailDescription = cursor.getColumnIndexOrThrow(BookSetting.Detail.DESCRIPTION);
        final int detailChapterLatest = cursor.getColumnIndexOrThrow(BookSetting.Detail.CHAPTER_LATEST);
        final int detailUpdateTime = cursor.getColumnIndexOrThrow(BookSetting.Detail.UPDATE_TIME);
        //final int detailType = cursor.getColumnIndexOrThrow(BookSetting.Detail.TYPE);

        try {
            while (cursor.moveToNext()){

                long id = cursor.getLong(detailId);
                String name = cursor.getString(detailName);
                String author = cursor.getString(detailAuthor);
                String coverImageLink = cursor.getString(detailCoverImageLink);
                String description = cursor.getString(detailDescription);
                String chapterLatest = cursor.getString(detailChapterLatest);
                String updateTime  = cursor.getString(detailUpdateTime);

                BookInfo bookInfo = new BookInfo();
                bookInfo.bookDetail =  new BookDetail.Builder().setId(id)
                        .setAuthor(author)
                        .setName(name)
                        .setCoverImageLink(coverImageLink)
                        .setDescription(description)
                        .setUpdateTime(updateTime)
                        .setChapterLatest(chapterLatest)
                        .build();
                bookList.add(bookInfo);
            }
        }catch (Exception e){
            bookList.clear();
            e.printStackTrace();
        }finally {
            cursor.close();
        }

        uri = BookSetting.Chapter.CONTENT_URI;
        cursor = cr.query(uri,null,"current = 1",null,null);
        final int chapterId = cursor.getColumnIndexOrThrow(BookSetting.Chapter.ID);
        final int chapterLink = cursor.getColumnIndexOrThrow(BookSetting.Chapter.LINK);
        //final int chapterCount = cursor.getColumnIndexOrThrow(BookSetting.Chapter.COUNT);
        final int chapterIndex = cursor.getColumnIndexOrThrow(BookSetting.Chapter.INDEX);

        try {
            while (cursor.moveToNext()){

                long id = cursor.getLong(chapterId);
                String link = cursor.getString(chapterLink);
                //int count = cursor.getInt(chapterCount);
                int index = cursor.getInt(chapterIndex);

                BookInfo bookInfo = getBookInfo(bookList,id);
                if(bookInfo == null){
                    new Throwable("loadAllBook-error bookChapter");
                    Log.e("sunday","loadAllBook-error bookChapter");
                }else {
                    String fileName = FileManager.PATH+"/"+bookInfo.bookDetail.getName()+"/"+ BookChapter.FileName;
                    BookChapter bookChapter = JsoupParse.from(fileName,new BookChapterParse());
                    bookChapter.setChapterIndex(index);
                    bookInfo.bookChapter = bookChapter;
                }
            }
        }catch (Exception e){
            bookList.clear();
            e.printStackTrace();
        }finally {
            cursor.close();
        }


        return bookList;
    }

    private  BookInfo getBookInfo(ArrayList<BookInfo> list,long id){
        for(BookInfo bookInfo : list ){
            if(bookInfo.bookDetail.getId() == id){
                return bookInfo;
            }
        }

        return null;
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

        runOnWorkerThread(new Runnable() {
            @Override
            public void run() {


                long id = bookInfo.bookDetail.getId();

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
                mBookInfoList.remove(bookInfo);

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
//        if(cursor.getCount() > 0){
//            cursor.close();
//            return true;
//        }
        return cursor.moveToFirst();
//        return false;

    }


    public BookInfo getBookInfo(long id){
        int count = mBookInfoList.size();
        for(int i = 0; i< count;i++) {
            BookInfo bookInfo = mBookInfoList.get(i);
           if(bookInfo.bookDetail.getId() == id){
               return bookInfo;
           }
        }
        return null;
    }

    public void updateBookChapter(BookChapter bookChapter){
        final ContentResolver cr = mContext.getContentResolver();
        Uri uri = BookSetting.Chapter.CONTENT_URI;
        ContentValues values = new ContentValues();
        int index = bookChapter.getChapterIndex();
        values.put(BookSetting.Chapter.INDEX,index);
        String where = BookSetting.Chapter.ID +" = ?" +" and "+BookSetting.Chapter.CURRENT +" = 1";
        cr.update(uri,values,where,new String[]{String.valueOf(bookChapter.getId())});
    }


}
