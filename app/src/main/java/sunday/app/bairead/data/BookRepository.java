package sunday.app.bairead.data;

import android.content.Context;

import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import sunday.app.bairead.data.local.BookLocalData;
import sunday.app.bairead.data.remote.BookRemoteData;
import sunday.app.bairead.data.setting.BookInfo;
import sunday.app.bairead.data.setting.BookMarkInfo;

/**
 * Created by zhongfei.sun on 2017/4/11.
 */

public class BookRepository implements BookDataSource{

    public static BookRepository INSTANCE = null;

    private final BookDataSource mBookLocalData;
    private final BookDataSource mBookRemoteData;

    private BookRepository(BookDataSource bookLocalData,BookDataSource bookRemoteData){
        mBookLocalData = bookLocalData;
        mBookRemoteData = bookRemoteData;
    }

    private static BookRepository getInstance(BookDataSource bookLocalData,BookDataSource bookRemoteData){
        if (INSTANCE == null) {
            INSTANCE = new BookRepository(bookLocalData, bookRemoteData);
        }
        return INSTANCE;
    }

    public static BookRepository getInstance(Context context){
        BookDataSource bookLocalSource = BookLocalData.getInstance(context);
        BookDataSource bookRemoteSource = BookRemoteData.getInstance(context);
        return BookRepository.getInstance(bookLocalSource,bookRemoteSource);
    }

    @Override
    public Observable<List<BookInfo>> loadBooks(boolean refresh) {
        if(refresh){
            mBookRemoteData.clear();
            return mBookLocalData.loadBooks(refresh).filter(bookInfos -> {
                for(BookInfo bookInfo : bookInfos){
                    mBookRemoteData.addBook(bookInfo);
                }
                return true;
            });
        }else{
            return mBookRemoteData.loadBooks(refresh);
        }

    }


    @Override
    public void addBook(BookInfo bookInfo) {
        mBookLocalData.addBook(bookInfo);
        mBookRemoteData.addBook(bookInfo);
    }

    @Override
    public void updateBook(BookInfo bookInfo) {
        mBookLocalData.updateBook(bookInfo);
    }

    @Override
    public void deleteBook(BookInfo bookInfo) {
        mBookLocalData.deleteBook(bookInfo);
        mBookRemoteData.deleteBook(bookInfo);
    }

    @Override
    public void deleteBooks(List<BookInfo> list) {
        mBookLocalData.deleteBooks(list);
        mBookRemoteData.deleteBooks(list);
    }

    @Override
    public BookInfo getBook(long id) {
        return mBookRemoteData.getBook(id);
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
        //mBookRemoteData.clear();
    }

}
