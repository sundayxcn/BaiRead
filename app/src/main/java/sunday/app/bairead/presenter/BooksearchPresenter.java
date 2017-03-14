package sunday.app.bairead.presenter;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import sunday.app.bairead.database.BookInfo;
import sunday.app.bairead.download.BookSearch;
import sunday.app.bairead.tool.FileManager;
import sunday.app.bairead.tool.ThreadManager;

/**
 * Created by sunday on 2017/3/6.
 */

public class BookSearchPresenter {
    public static final String fileName = "searchHistory.txt";
     
    public interface IBookSearchListener{
        void historyAddFinish(String name);
        void historyLoadFinish(ArrayList<String> list);
        void bookSearchStart(ArrayList<BookInfo> bookInfoArrayList);
        void bookSearchFinish();
    }

    private Handler handler = new Handler();
    private IBookSearchListener bookSearchListener;
    private ArrayList<String> historyList;
    private ArrayList<BookInfo> bookSearchList;


    public BookSearchPresenter(IBookSearchListener bookSearchListener){
        this.bookSearchListener = bookSearchListener;
    }

    public void readSearchHistory(Context context) {
        File file = context.getCacheDir();
        if (!file.exists()) {
            file.mkdirs();
        }
        final String fullName = file.getAbsolutePath() + "/" + fileName;
        ThreadManager.getInstance().work(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> list = FileManager.readFileByLine(fullName);
                Collections.reverse(list);
                historyList = list;
                runMainUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bookSearchListener.historyLoadFinish(historyList);
                    }
                });

            }
        });
    }

    public void addSearchHistory(Context context,final String name){
        if(historyList.contains(name)){
        }else {
            File file = context.getCacheDir();
            if (!file.exists()) {
                file.mkdirs();
            }
            final String fullName = file.getAbsolutePath() + "/" + fileName;
            ThreadManager.getInstance().work(new Runnable() {
                @Override
                public void run() {
                    FileManager.writeFileByLine(fullName, name);
                    runMainUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bookSearchListener.historyAddFinish(name);
                        }
                    });

                }
            });
        }
    }

    public void clearHistory(Context context){
        File file = context.getCacheDir();
        final String fullName = file.getAbsolutePath() + "/" + fileName;
        if(file.exists()) {
            ThreadManager.getInstance().work(new Runnable() {
                @Override
                public void run() {
                    FileManager.deleteFile(fullName);
                }
            });
        }
    }


    public void searchBook(String name){
        bookSearchList = new ArrayList<>();
        bookSearchListener.bookSearchStart(bookSearchList);
        BookSearch bookSearch = new BookSearch(new BookSearch.ISearchListener() {
            @Override
            public void searchFinish(final BookInfo bookInfo) {
                runMainUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("sunday","bookName = "+bookInfo.bookDetail.getName());
                        bookSearchList.add(bookInfo);
                        bookSearchListener.bookSearchFinish();
                    }
                });
            }
        });
        bookSearch.searchTopWeb(name);
    }

    public void runMainUiThread(Runnable runnable){
        handler.post(runnable);
    }

}
