package sunday.app.bairead.presenter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

import java.util.ArrayList;

import sunday.app.bairead.database.BaiReadApplication;
import sunday.app.bairead.database.BookInfo;
import sunday.app.bairead.database.BookModel;
import sunday.app.bairead.download.BookDownLoad;
import sunday.app.bairead.tool.FileManager;
import sunday.app.bairead.activity.BookReadActivity;
import sunday.app.bairead.tool.ThreadManager;

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
        void onCheckStart();
        void loadError();
    }

    private IBookcasePresenterListener bookcasePresenterListener;
    public BookcasePresenter(Context c, IBookcasePresenterListener bookcasePresenter){
        context = c;
        BaiReadApplication application = (BaiReadApplication) context.getApplicationContext();
        bookModel = application.getBookModel();
        bookcasePresenterListener = bookcasePresenter;
    }

    public void init(){
        bookcasePresenterListener.loadBookStart();
        ThreadManager.getInstance().work(new Runnable() {
            @Override
            public void run() {
                final ArrayList<BookInfo> bookInfo = bookModel.loadAllBook();
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        bookcasePresenterListener.loadBookFinish(bookInfo);
                    }
                });

            }
        });
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
        bookcasePresenterListener.onCheckStart();
        BookDownLoad bookDownload = new BookDownLoad(new BookDownLoad.DownloadListener() {
            @Override
            public void onError() {
                bookcasePresenterListener.loadError();
            }

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
                        if(!cBookInfo.bookDetail.getChapterLatest().equals(bookInfo.bookDetail.getChapterLatest())){
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
        final String fileName = bookInfo.bookDetail.getName();
        ThreadManager.getInstance().work(new Runnable() {
            @Override
            public void run() {
                //删除本地缓存
                FileManager.deleteFolder(FileManager.PATH + "/" + fileName);
            }
        });

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
