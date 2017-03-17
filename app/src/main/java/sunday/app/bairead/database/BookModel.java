package sunday.app.bairead.database;

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

import sunday.app.bairead.parse.ParseChapter;
import sunday.app.bairead.parse.ParseXml;
import sunday.app.bairead.tool.DeferredHandler;
import sunday.app.bairead.tool.FileManager;
import sunday.app.bairead.tool.Temp;

/**
 * Created by sunday on 2016/12/13.
 */

public class BookModel {

    private static final HandlerThread sWorkerThread = new HandlerThread("baiRead-loader");
    static {
        sWorkerThread.start();
    }
//    public interface CallBack{
//        void loadBookFinish(ArrayList<BookInfo> list);
//        void loadBookMarkFinish(ArrayList<BookMarkInfo> list);
//        void addBookDataFinish(BookInfo bookInfo,boolean success);
//        void deleteBookDataFinish(BookInfo bookInfo,boolean success);
//    }

    private Context mContext;
    //private CallBack callBack;
    private final DeferredHandler mHandler = new DeferredHandler();
    private static final Handler sWorker = new Handler(sWorkerThread.getLooper());
    private ArrayList<BookInfo> mBookInfoList = new ArrayList<>();



    public BookModel(Context context){
        mContext = context;
    }


//    public void setCallBack(CallBack callBack){
//        this.callBack = callBack;
//    }

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
        bookInfo.bookChapter.setId(id);
        bookInfo.bookChapter.onAddToDatabase(chapterValues);
        mBookInfoList.add(bookInfo);

        runOnWorkerThread(new Runnable() {
            @Override
            public void run() {

                Uri uri = cr.insert(BookSetting.Detail.CONTENT_URI,detailValues);

                Uri uri2 = cr.insert(BookSetting.Chapter.CONTENT_URI,chapterValues);

                //boolean success = (uri != null && uri2 != null);
                //if(success) {
                //    mBookInfoList.add(bookInfo);
                //}
                //postAddCallBack(bookInfo,success);

            }
        });
    }


    public void updateBook(BookInfo bookInfo){
        final ContentResolver cr = mContext.getContentResolver();
        Uri uri = BookSetting.Detail.CONTENT_URI;
        ContentValues values = new ContentValues();
        String latest = bookInfo.bookDetail.getChapterLatest();
        String updateTime = bookInfo.bookDetail.getUpdateTime();
        boolean topCase = bookInfo.bookDetail.isTopCase();
        values.put(BookSetting.Detail.CHAPTER_LATEST,latest);
        values.put(BookSetting.Detail.UPDATE_TIME,updateTime);
        values.put(BookSetting.Detail.TOP_CASE,topCase ? 1: 0);
        String where = BookSetting.Detail._ID +" = ?" ;
        cr.update(uri,values,where,new String[]{String.valueOf(bookInfo.bookDetail.getId())});

        //final ContentResolver cr = mContext.getContentResolver();
        uri = BookSetting.Chapter.CONTENT_URI;
        values = new ContentValues();
        int chapterCount = bookInfo.bookChapter.getChapterCount();
        int chapterIndex = bookInfo.bookChapter.getChapterIndex();
        int chaperPage = bookInfo.bookChapter.getChapterPage();
        values.put(BookSetting.Chapter.COUNT,chapterCount);
        values.put(BookSetting.Chapter.INDEX,chapterIndex);
        values.put(BookSetting.Chapter.PAGE,chaperPage);
        where = BookSetting.Chapter.ID +" = ?" ;
        cr.update(uri,values,where,new String[]{String.valueOf(bookInfo.bookDetail.getId())});

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
        final int detailType = cursor.getColumnIndexOrThrow(BookSetting.Detail.TYPE);
        final int detailStatus = cursor.getColumnIndexOrThrow(BookSetting.Detail.STATUS);
        final int detailTopCase = cursor.getColumnIndexOrThrow(BookSetting.Detail.TOP_CASE);
        try {
            while (cursor.moveToNext()){

                long id = cursor.getLong(detailId);
                String name = cursor.getString(detailName);
                String author = cursor.getString(detailAuthor);
                String coverImageLink = cursor.getString(detailCoverImageLink);
                String description = cursor.getString(detailDescription);
                String chapterLatest = cursor.getString(detailChapterLatest);
                String updateTime  = cursor.getString(detailUpdateTime);
                int type = cursor.getInt(detailType);
                int status = cursor.getInt(detailStatus);
                int topCase = cursor.getInt(detailTopCase);
                BookInfo bookInfo = new BookInfo();
                bookInfo.bookDetail =  new BookDetail.Builder().setId(id)
                        .setAuthor(author)
                        .setName(name)
                        .setCoverImageLink(coverImageLink)
                        .setDescription(description)
                        .setUpdateTime(updateTime)
                        .setChapterLatest(chapterLatest)
                        .setType(type)
                        .setStatus(status)
                        .setTopCase(topCase)
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
        final int chapterCount = cursor.getColumnIndexOrThrow(BookSetting.Chapter.COUNT);
        final int chapterIndex = cursor.getColumnIndexOrThrow(BookSetting.Chapter.INDEX);
        final int chapterPage = cursor.getColumnIndexOrThrow(BookSetting.Chapter.PAGE);
        try {
            while (cursor.moveToNext()){

                long id = cursor.getLong(chapterId);
                String link = cursor.getString(chapterLink);
                int count = cursor.getInt(chapterCount);
                int index = cursor.getInt(chapterIndex);
                int page = cursor.getInt(chapterPage);
                BookInfo bookInfo = getBookInfo(bookList,id);
                if(bookInfo == null){
                    new Throwable("loadAllBook-error bookChapter");
                    Log.e("sunday","loadAllBook-error bookChapter");
                }else {

//                    String fileName = FileManager.PATH+"/"+bookInfo.bookDetail.getName()+"/"+ BookChapter.FileName;
//                    BookChapter bookChapter = ParseXml.createParse(ParseChapter.class).parse(fileName);
//                    if(bookChapter == null){
//                        bookList.remove(bookInfo);
//                        //此处弹出重建书籍缓存的提示
//                    }else {
                        BookChapter bookChapter = new BookChapter.Builder().
                                setChapterIndex(index).
                                setChapterLink(link).
                                setChapterPage(page).
                                setChapterCount(count).
                                build();
                        bookChapter.setId(id);
                        bookInfo.bookChapter = bookChapter;
//                    }
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


    public void deleteBook(final BookInfo bookInfo){

        runOnWorkerThread(new Runnable() {
            @Override
            public void run() {


                long id = bookInfo.bookDetail.getId();

                ContentResolver cr = mContext.getContentResolver();
                Uri uri = BookSetting.Detail.getContentUri(id,true);
                cr.delete(uri,null,null);

                //postDeleteCallBack(bookInfo,(uri != null));

                /*删除和本书相关的所有来源信息
                 * */
                uri = BookSetting.Chapter.CONTENT_URI;
                String where = BookSetting.Chapter.ID + "= ?";
                cr.delete(uri,where,new String[]{String.valueOf(id)});

                /*删除和本书相关的所有书签
                 * */
                uri = BookSetting.Mark.CONTENT_URI;
                cr.delete(uri,where,new String[]{String.valueOf(id)});
                mBookInfoList.remove(bookInfo);

            }
        });

    }


    public void addBookMark(BookMarkInfo bookMark){
        long nameId = bookMark.nameId;
        int chapterIndex = bookMark.chapterIndex;
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





    public void deleteBookAllMark(final long bookId){
        runOnWorkerThread(new Runnable() {
            @Override
            public void run() {
                String where = BookSetting.Mark.ID + " = ?";
                ContentResolver cr = mContext.getContentResolver();
                Uri uri = BookSetting.Mark.CONTENT_URI;
                cr.delete(uri,where,new String[]{String.valueOf(bookId)});
                cr.delete(uri,null,null);
            }
        });
    }

    public void deleteBookMark(BookMarkInfo bookMark){
        final int index = bookMark.chapterIndex;
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

    public ArrayList<BookMarkInfo> loadBookMark(long bookId){
        ArrayList<BookMarkInfo> list = new ArrayList<>();
        final ContentResolver cr = mContext.getContentResolver();
        Uri uri = BookSetting.Mark.CONTENT_URI;
        String select = BookSetting.Mark.ID + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(bookId)};
        Cursor cursor = cr.query(uri,null,select,selectionArgs,null);

        final int markId = cursor.getColumnIndexOrThrow(BookSetting.Mark.ID);
        final int markIndex = cursor.getColumnIndexOrThrow(BookSetting.Mark.INDEX);
        try {
            while (cursor.moveToNext()){

                long id = cursor.getLong(markId);
                int chapterIndex = cursor.getInt(markIndex);
                BookMarkInfo bookMarkInfo = new BookMarkInfo();
                bookMarkInfo.nameId = id;
                bookMarkInfo.chapterIndex = chapterIndex;
                list.add(bookMarkInfo);
            }
        }catch (Exception e){
            list.clear();
            e.printStackTrace();
        }finally {
            cursor.close();
        }
        return list;
    }


    /**
     * 判断是否在书架中
     * */
    public boolean isBookCase(BookDetail bookDetail){
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


    public boolean isBookCase(long id){
        int count = mBookInfoList.size();
        for(int i = 0; i< count;i++) {
            BookInfo bookInfo = mBookInfoList.get(i);
            if(bookInfo.bookDetail.getId() == id){
                return true;
            }
        }
        return false;
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

        //return null;
    }

    public ArrayList<BookInfo> getBookList(){
        return mBookInfoList;
    }

    public void updateBookChapter(BookChapter bookChapter){
        final ContentResolver cr = mContext.getContentResolver();
        final Uri uri = BookSetting.Chapter.CONTENT_URI;
        final ContentValues values = new ContentValues();
        int index = bookChapter.getChapterIndex();
        int page = bookChapter.getChapterPage();
        final long id = bookChapter.getId();
        values.put(BookSetting.Chapter.INDEX,index);
        values.put(BookSetting.Chapter.PAGE,page);
        final String where = BookSetting.Chapter.ID +" = ?" +" and "+BookSetting.Chapter.CURRENT +" = 1";
        runOnWorkerThread(new Runnable() {
            @Override
            public void run() {
                cr.update(uri,values,where,new String[]{String.valueOf(id)});
            }
        });

    }

}
