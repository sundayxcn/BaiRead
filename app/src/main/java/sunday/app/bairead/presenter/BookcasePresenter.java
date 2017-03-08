package sunday.app.bairead.presenter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;

import java.util.ArrayList;

import sunday.app.bairead.DataBase.BaiReadApplication;
import sunday.app.bairead.DataBase.BookInfo;
import sunday.app.bairead.DataBase.BookModel;
import sunday.app.bairead.Download.BookDownload;
import sunday.app.bairead.Tool.FileManager;
import sunday.app.bairead.UI.BookReadActivity;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by Administrator on 2017/3/5.
 */

public class BookcasePresenter{

    private Context context;
    private BookModel bookModel;
    Handler handler = new Handler();

    public interface IBookcasePresenterListener{
        void loadBookStart();
        void loadBookFinish(ArrayList<BookInfo> bookList);
        void onCheckNewChapter(BookInfo bookInfo);
        void onCheckFinish();
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

    private int bookCount = 0;

    private  synchronized boolean checkFinish(){
        bookCount--;
        return bookCount <=0;
    }

    private synchronized void checkBookInit(int size){
        bookCount = size;
    }


    public void runOnMainThread(Runnable runnable){
        handler.post(runnable);
    }

    public void checkNewChapter(ArrayList<BookInfo> list){
        final ArrayList<BookInfo> listAdapterList = list;
        checkBookInit(list.size());
        BookDownload bookDownload = new BookDownload(new BookDownload.DownloadListener() {
            @Override
            public void onNewChapter(final BookInfo bookInfo) {
                if(checkFinish() || bookInfo == null){
                    runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            bookcasePresenterListener.onCheckFinish();
                        }
                    });

                }
                for(BookInfo cBookInfo : listAdapterList){
                    if(cBookInfo.bookDetail.getId() == bookInfo.bookDetail.getId()){
                        if(cBookInfo.bookChapter.getChapterCount() != bookInfo.bookChapter.getChapterCount()){
                            cBookInfo.bookChapter.setChapterList(bookInfo.bookChapter.getChapterList());
                            cBookInfo.bookDetail.setUpdateTime(bookInfo.bookDetail.getUpdateTime());
                            cBookInfo.bookDetail.setChapterLatest(bookInfo.bookChapter.getLastChapter().getTitle());
                            bookModel.updateBook(bookInfo);
                            runOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    bookcasePresenterListener.onCheckNewChapter(bookInfo);
                                }
                            });
                        }

                        return;
                    }
                }
            }
        });

        for(BookInfo bookInfo : list) {
            bookDownload.updateNewChapter(bookInfo);
        }
    }

    public void deleteBook(BookInfo bookInfo){

        bookModel.deleteBook(bookInfo);
        //删除本地缓存
        FileManager.deleteFolder(FileManager.PATH + "/" + bookInfo.bookDetail.getName());
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
