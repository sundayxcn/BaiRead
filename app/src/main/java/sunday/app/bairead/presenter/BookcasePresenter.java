package sunday.app.bairead.presenter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
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
        void onCheckStart();
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
        Log.e("sunday","bookCount="+bookCount);
        bookCount--;
        if(bookCount <=0){
            runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    bookcasePresenterListener.onCheckFinish();
                }
            });
            return true;
        }else {
            return false;
        }
    }

    private synchronized void checkBookInit(int size){
        bookCount = size;
    }


    public void runOnMainThread(Runnable runnable){
        handler.post(runnable);
    }

    public void checkNewChapter(ArrayList<BookInfo> list) {
        final ArrayList<BookInfo> listAdapterList = list;
        BookDownLoad bookDownload = new BookDownLoad();
        for (final BookInfo bookInfo : list) {
            bookDownload.updateBookInfoAsync(bookInfo, new BookDownLoad.DownloadListener<BookInfo>(){
                @Override
                public String getFileName() {
                    return BookDownLoad.getFullChapterFileName(bookInfo.bookDetail.getName());
                }

                @Override
                public String getLink() {
                    return bookInfo.bookChapter.getChapterLink();
                }

                @Override
                public void onStart() {
                    bookcasePresenterListener.onCheckStart();
                    checkBookInit(listAdapterList.size());
                }

                @Override
                public long getId() {
                    return bookInfo.bookDetail.getId();
                }

                @Override
                public void onResult(BookInfo newBookInfo) {
                    if (!bookInfo.bookDetail.getChapterLatest().equals(newBookInfo.bookDetail.getChapterLatest())) {
                        bookInfo.bookChapter.setChapterList(newBookInfo.bookChapter.getChapterList());
                        bookInfo.bookDetail.setUpdateTime(newBookInfo.bookDetail.getUpdateTime());
                        bookInfo.bookDetail.setChapterLatest(newBookInfo.bookChapter.getLastChapter().getTitle());
                        bookModel.updateBook(bookInfo);
                        runOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                bookcasePresenterListener.onCheckNewChapter(bookInfo);
                            }
                        });
                    }
                    checkFinish();
                }


                @Override
                public void onError(int errorCode) {
                    checkFinish();
                }
            });
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
