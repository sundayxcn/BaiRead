package sunday.app.bairead.data.local;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import sunday.app.bairead.data.BookDataSource;
import sunday.app.bairead.data.setting.BookChapter;
import sunday.app.bairead.data.setting.BookDetail;
import sunday.app.bairead.data.setting.BookInfo;
import sunday.app.bairead.data.setting.BookMarkInfo;
import sunday.app.bairead.utils.ThreadManager;

/**
 * Created by zhongfei.sun on 2017/4/11.
 */

public class BookLocalData implements BookDataSource {

    public static final String TAG = "BookLocalData";
    public static BookLocalData INSTANCE = null;
    private Context mContext;

    public static BookLocalData getInstance(@NonNull Context context){
        if(INSTANCE == null){
            INSTANCE = new BookLocalData(context);
        }
        return INSTANCE;
    }


    private BookLocalData(@NonNull Context context){
        mContext = context.getApplicationContext();
    }

    @Override
    public Observable<List<BookInfo>> loadBooks(boolean refresh) {
        return Observable.create(new Observable.OnSubscribe<List<BookInfo>>() {
            @Override
            public void call(Subscriber<? super List<BookInfo>> subscriber) {
                List<BookInfo> bookList = new ArrayList<>();
                loadBookDetails(bookList);
                loadBookChapters(bookList);
                subscriber.onNext(bookList);
            }
        });
    }


    private void loadBookDetails(List<BookInfo> list){
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
                list.add(bookInfo);
            }
        }catch (Exception e){
            list.clear();
            e.printStackTrace();
        }finally {
            cursor.close();
        }
    }

    private void loadBookChapters(List<BookInfo> list){
        final ContentResolver cr = mContext.getContentResolver();
        Uri uri = BookSetting.Chapter.CONTENT_URI;
        Cursor cursor = cr.query(uri,null,"current = 1",null,null);
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
                BookInfo bookInfo = null;
                for(BookInfo info : list){
                    if(info.bookDetail.getId() == id){
                        bookInfo = info;
                        break;
                    }
                }
                if(bookInfo == null){
                    new Throwable("loadAllBook-error bookChapter");
                    Log.e("sunday","loadAllBook-error bookChapter");
                }else {
                    BookChapter bookChapter = new BookChapter.Builder().
                            setChapterIndex(index).
                            setChapterLink(link).
                            setChapterPage(page).
                            setChapterCount(count).
                            build();
                    bookChapter.setId(id);
                    bookInfo.bookChapter = bookChapter;
                }
            }
        }catch (Exception e){
            list.clear();
            e.printStackTrace();
        }finally {
            cursor.close();
        }
    }

    @Override
    public void addBook(final BookInfo bookInfo) {

        ThreadManager.getInstance().work(() -> {
            final ContentValues detailValues = new ContentValues();
            final ContentValues chapterValues = new ContentValues();
            final ContentResolver cr = mContext.getContentResolver();
            final long id = BookContentProvider.getInstance().generateNewId();
            detailValues.put(BookSetting.Detail._ID,id);
            bookInfo.bookDetail.setId(id);
            bookInfo.bookDetail.onAddToDatabase(detailValues);
            Uri uri = cr.insert(BookSetting.Detail.CONTENT_URI,detailValues);

            chapterValues.put(BookSetting.Chapter.ID,id);
            bookInfo.bookChapter.setId(id);
            bookInfo.bookChapter.onAddToDatabase(chapterValues);
            Uri uri2 = cr.insert(BookSetting.Chapter.CONTENT_URI,chapterValues);

        });

//        Flowable.create(new FlowableOnSubscribe<Uri>() {
//            @Override
//            public void subscribe(FlowableEmitter<Uri> e) throws Exception {
//                final ContentValues detailValues = new ContentValues();
//                final ContentValues chapterValues = new ContentValues();
//                final ContentResolver cr = mContext.getContentResolver();
//                final long id = BookContentProvider.getInstance().generateNewId();
//                detailValues.put(sunday.app.bairead.data.local.BookSetting.Detail._ID,id);
//                bookInfo.bookDetail.setId(id);
//                bookInfo.bookDetail.onAddToDatabase(detailValues);
//                Uri uri = cr.insert(sunday.app.bairead.data.local.BookSetting.Detail.CONTENT_URI,detailValues);
//                e.onNext(uri);
//                chapterValues.put(sunday.app.bairead.data.local.BookSetting.Chapter.ID,id);
//                bookInfo.bookChapter.setId(id);
//                bookInfo.bookChapter.onAddToDatabase(chapterValues);
//                Uri uri2 = cr.insert(sunday.app.bairead.data.local.BookSetting.Chapter.CONTENT_URI,chapterValues);
//                e.onNext(uri2);
//            }
//        },BackpressureStrategy.BUFFER).
//                subscribeOn(Schedulers.io()).
//                subscribe(new Consumer<Uri>() {
//            @Override
//            public void accept(Uri uri) throws Exception {
//                if(uri == null){
//                    Log.w(TAG,"add-book failed");
//                }
//            }
//        });


    }

    @Override
    public void updateBook(BookInfo bookInfo) {
        final ContentResolver cr = mContext.getContentResolver();
        Uri uri = sunday.app.bairead.data.local.BookSetting.Detail.CONTENT_URI;
        ContentValues values = new ContentValues();
        String latest = bookInfo.bookDetail.getChapterLatest();
        String updateTime = bookInfo.bookDetail.getUpdateTime();
        boolean topCase = bookInfo.bookDetail.isTopCase();
        values.put(sunday.app.bairead.data.local.BookSetting.Detail.CHAPTER_LATEST,latest);
        values.put(sunday.app.bairead.data.local.BookSetting.Detail.UPDATE_TIME,updateTime);
        values.put(sunday.app.bairead.data.local.BookSetting.Detail.TOP_CASE,topCase ? 1: 0);
        String where = sunday.app.bairead.data.local.BookSetting.Detail._ID +" = ?" ;
        cr.update(uri,values,where,new String[]{String.valueOf(bookInfo.bookDetail.getId())});

        //final ContentResolver cr = mContext.getContentResolver();
        uri = sunday.app.bairead.data.local.BookSetting.Chapter.CONTENT_URI;
        values = new ContentValues();
        int chapterCount = bookInfo.bookChapter.getChapterCount();
        int chapterIndex = bookInfo.bookChapter.getChapterIndex();
        int chaperPage = bookInfo.bookChapter.getChapterPage();
        values.put(sunday.app.bairead.data.local.BookSetting.Chapter.COUNT,chapterCount);
        values.put(sunday.app.bairead.data.local.BookSetting.Chapter.INDEX,chapterIndex);
        values.put(sunday.app.bairead.data.local.BookSetting.Chapter.PAGE,chaperPage);
        where = sunday.app.bairead.data.local.BookSetting.Chapter.ID +" = ?" ;
        cr.update(uri,values,where,new String[]{String.valueOf(bookInfo.bookDetail.getId())});
    }

    @Override
    public void deleteBook(BookInfo bookInfo) {
        ContentResolver cr = mContext.getContentResolver();
        long id = bookInfo.bookDetail.getId();
        Uri uri = sunday.app.bairead.data.local.BookSetting.Detail.getContentUri(id,true);
        cr.delete(uri,null,null);

        //postDeleteCallBack(bookInfo,(uri != null));

                /*删除和本书相关的所有来源信息
                 * */
        uri = sunday.app.bairead.data.local.BookSetting.Chapter.CONTENT_URI;
        String where = sunday.app.bairead.data.local.BookSetting.Chapter.ID + "= ?";
        cr.delete(uri,where,new String[]{String.valueOf(id)});

                /*删除和本书相关的所有书签
                 * */
        uri = sunday.app.bairead.data.local.BookSetting.Mark.CONTENT_URI;
        cr.delete(uri,where,new String[]{String.valueOf(id)});
    }

    @Override
    public void deleteBooks(List<BookInfo> list) {
        for(BookInfo bookInfo : list){
            deleteBook(bookInfo);
        }
    }

    @Override
    public BookInfo getBook(long id) {
        return null;
    }

    @Override
    public Observable<List<BookMarkInfo>> loadBookMarks() {
        return null;
    }

    @Override
    public void addBookMark(BookMarkInfo bookMarkInfo) {

    }

    @Override
    public void deleteBookMark(BookMarkInfo bookMarkInfo) {

    }

    @Override
    public void clear() {

    }
}
