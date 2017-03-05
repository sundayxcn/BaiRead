package sunday.app.bairead.presenter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;

import java.util.ArrayList;

import sunday.app.bairead.DataBase.BaiReadApplication;
import sunday.app.bairead.DataBase.BookInfo;
import sunday.app.bairead.DataBase.BookModel;
import sunday.app.bairead.UI.BookReadActivity;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by Administrator on 2017/3/5.
 */

public class BookcasePresenter {

    private Context context;
    private BookModel bookModel;

    public interface IBookcasePresenterListener{
        void loadBookStart();
        void loadBookFinish(ArrayList<BookInfo> bookList);
        void onNewChapterBook(ArrayList<BookInfo> bookList);
    }

    private IBookcasePresenterListener bookcasePresenterListener;
    public BookcasePresenter(Context c, IBookcasePresenterListener bookcasePresenter){
        context = c;
        BaiReadApplication application = (BaiReadApplication) context.getApplicationContext();
        bookModel = application.getBookModel();
        bookcasePresenterListener = bookcasePresenter;
    }

    public void init(){
        new AsyncTask<Void,Void,ArrayList<BookInfo>>(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                bookcasePresenterListener.loadBookStart();
            }

            @Override
            protected ArrayList<BookInfo> doInBackground(Void... params) {
                return bookModel.loadAllBook();
            }

            @Override
            protected void onPostExecute(ArrayList<BookInfo> bookInfo) {
                super.onPostExecute(bookInfo);
                bookcasePresenterListener.loadBookFinish(bookInfo);
            }
        }.execute();
    }

    public void checkNewChapter(){
        //bookcasePresenterListener.onNewChapterBook();
    }

    public void deleteBook(BookInfo bookInfo){

    }

    public static void readBook(Context context,BookInfo bookInfo){
        Intent intent = new Intent();
        intent.setClass(context, BookReadActivity.class);
        long bookId = bookInfo.bookDetail.getId();
        intent.putExtra(BookReadActivity.EXTRAS_BOOK_ID, bookId);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
