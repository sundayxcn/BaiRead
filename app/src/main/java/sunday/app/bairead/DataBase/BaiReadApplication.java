package sunday.app.bairead.DataBase;

import android.app.Application;

/**
 * Created by sunday on 2016/12/13.
 */

public class BaiReadApplication extends Application {

    private BookModel bookModel;
    private BookContentProvider bookContentProvider;

    @Override
    public void onCreate() {
        super.onCreate();
        bookModel = new BookModel(this);
        //registerReceiver()
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public void setBookContentProvider(BookContentProvider bookContentProvider) {
        this.bookContentProvider = bookContentProvider;
    }

    public BookModel getBookModel() {
        return bookModel;
    }

    public BookContentProvider getBookContentProvider() {
        return bookContentProvider;
    }
}
