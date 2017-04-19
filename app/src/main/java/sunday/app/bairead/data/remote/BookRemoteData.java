package sunday.app.bairead.data.remote;


import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import sunday.app.bairead.data.BookDataSource;
import sunday.app.bairead.data.setting.BookInfo;

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
    public Observable<List<BookInfo>> loadBooks(boolean refresh) {
        return Observable.create(new Observable.OnSubscribe<List<BookInfo>>() {
            @Override
            public void call(Subscriber<? super List<BookInfo>> subscriber) {
                subscriber.onNext(mBookInfoList);
            }
        });
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
    public void clear() {
        mBookInfoList.clear();
    }
}
