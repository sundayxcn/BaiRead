package sunday.app.bairead.presenter;

import android.content.Context;

import sunday.app.bairead.DataBase.BaiReadApplication;
import sunday.app.bairead.DataBase.BookInfo;
import sunday.app.bairead.DataBase.BookModel;

/**
 * Created by sunday on 2017/3/7.
 */

public class BookDetailPresenter {

    private Context context;
    public interface IBookDetailListener{

    }

    private  IBookDetailListener bookDetailListener;
    public BookDetailPresenter(Context c,IBookDetailListener bookDetailListener){
        context = c;
        this.bookDetailListener = bookDetailListener;
    }

    public void readBook(BookInfo bookInfo){
        BookcasePresenter.readBook(context,bookInfo);
    }

    public void addToBookCase(BookInfo bookInfo){
        BaiReadApplication application  = (BaiReadApplication) context.getApplicationContext();
        BookModel bookModel = application.getBookModel();
        bookModel.addBook(bookInfo);
    }

    public void cacheBook(BookInfo bookInfo){

    }
}
