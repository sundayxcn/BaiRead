package sunday.app.bairead.data.remote;


import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import sunday.app.bairead.data.BookDataSource;
import sunday.app.bairead.data.setting.BookInfo;
import sunday.app.bairead.data.setting.BookMarkInfo;

/**
 * Created by zhongfei.sun on 2017/4/11.
 */

public class BookRemoteData implements BookDataSource {


    public static BookRemoteData INSTANCE = null;

    private ArrayList<BookInfo> mBookInfoList = new ArrayList<>();

    public static BookRemoteData getInstance(@NonNull Context context){
        if(INSTANCE == null){
            INSTANCE = new BookRemoteData(context);
        }
        return INSTANCE;
    }

    private BookRemoteData(@NonNull Context context){

    }

    @Override
    public Observable<List<BookInfo>> loadBooks() {
        return Observable.create(subscriber -> subscriber.onNext(mBookInfoList));
    }

    @Override
    public void addBook(BookInfo bookInfo) {
        mBookInfoList.add(bookInfo);
    }

    @Override
    public void updateBook(BookInfo bookInfo) {

    }

    @Override
    public void deleteBook(BookInfo bookInfo) {
        mBookInfoList.remove(bookInfo);
    }

    @Override
    public void deleteBooks(List<BookInfo> list) {
        mBookInfoList.removeAll(list);
    }

    @Override
    public BookInfo getBook(long id) {
        for(BookInfo bookInfo : mBookInfoList){
            if(bookInfo.bookDetail.getId() == id){
                return bookInfo;
            }
        }
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
        mBookInfoList.clear();
    }
}
