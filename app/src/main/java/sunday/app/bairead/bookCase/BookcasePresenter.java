package sunday.app.bairead.bookCase;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import sunday.app.bairead.base.BaiReadApplication;
import sunday.app.bairead.database.BookInfo;
import sunday.app.bairead.database.BookModel;
import sunday.app.bairead.download.BookDownLoad;
import sunday.app.bairead.utils.FileManager;
import sunday.app.bairead.bookRead.BookReadActivity;
import sunday.app.bairead.utils.ThreadManager;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by Administrator on 2017/3/5.
 */

public class BookcasePresenter{

    private Context context;
    private BookModel bookModel;
    Handler handler = new Handler(Looper.getMainLooper());

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
                final ArrayList<BookInfo> bookInfoArrayList = bookModel.loadAllBook();
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        bookcasePresenterListener.loadBookFinish(bookInfoArrayList);
                    }
                });

            }
        });
    }

    private void checkFinish(){
        if(atomicInteger.decrementAndGet() <= 0){
            runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    bookcasePresenterListener.onCheckFinish();
                }
            });
        }
    }

    private synchronized void checkBookInit(int size){
        atomicInteger = new AtomicInteger(size);
    }

    private AtomicInteger atomicInteger;


    public void runOnMainThread(Runnable runnable){
        handler.post(runnable);
    }

    public void checkNewChapter(ArrayList<BookInfo> list) {
        final ArrayList<BookInfo> listAdapterList = list;
        bookcasePresenterListener.onCheckStart();
        checkBookInit(listAdapterList.size());
        BookDownLoad bookDownload = new BookDownLoad();
        for (final BookInfo bookInfo : list) {
            bookDownload.updateBookInfoAsync(new BookDownLoad.DownloadListener<BookInfo>(){
                @Override
                public String getFileName() {
                    String bookName = bookInfo.bookDetail.getName();
                    BookDownLoad.createFileDir(bookName);
                    return BookDownLoad.getFullChapterFileName(bookName);
                }

                @Override
                public String getLink() {
                    return bookInfo.bookChapter.getChapterLink();
                }

                @Override
                public void onStart() {

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


    public void updateBook(final BookInfo bookInfo){
        ThreadManager.getInstance().work(new Runnable() {
            @Override
            public void run() {
                bookModel.updateBook(bookInfo);
            }
        });

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
