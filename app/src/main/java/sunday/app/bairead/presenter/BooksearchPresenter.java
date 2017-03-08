package sunday.app.bairead.presenter;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import sunday.app.bairead.database.BookInfo;
import sunday.app.bairead.download.SearchManager;
import sunday.app.bairead.tool.FileManager;

/**
 * Created by sunday on 2017/3/6.
 */

public class BookSearchPresenter {
    public static final String fileName = "searchHistory.txt";


    public interface IBookSearchListener{
        void historyAddFinish(String name);
        void historyLoadFinish(ArrayList<String> list);
        void bookSearchFinish(ArrayList<BookInfo> bookInfoArrayList);
    }

    private Handler handler = new Handler();
    private IBookSearchListener bookSearchListener;
    private ArrayList<String> historyList;
    private ArrayList<BookInfo> booksearchList;


    public BookSearchPresenter(IBookSearchListener bookSearchListener){
        this.bookSearchListener = bookSearchListener;
    }

    public void readSearchHistory(Context context) {
        File file = context.getCacheDir();
        if (!file.exists()) {
            file.mkdirs();
        }
        final String fullName = file.getAbsolutePath() + "/" + fileName;
        new AsyncTask<Void, Void, ArrayList<String>>() {
            @Override
            protected ArrayList<String> doInBackground(Void... params) {
                return FileManager.readFileByLine(fullName);
            }

            @Override
            protected void onPostExecute(ArrayList<String> strings) {
                super.onPostExecute(strings);
                Collections.reverse(strings);
                historyList = strings;
                bookSearchListener.historyLoadFinish(historyList);
            }
        }.execute();
    }

    public void addSearchHistory(Context context,final String name){
        if(historyList.contains(name)){
        }else {
            File file = context.getCacheDir();
            if (!file.exists()) {
                file.mkdirs();
            }
            final String fullName = file.getAbsolutePath() + "/" + fileName;
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    FileManager.writeFileByLine(fullName, name);
                    return name;
                }

                @Override
                protected void onPostExecute(String string) {
                    super.onPostExecute(string);
                    bookSearchListener.historyAddFinish(string);
                }
            }.execute();
        }
    }

    public void searchBook(String name){
        booksearchList = new ArrayList<>();
            SearchManager searchManager = new SearchManager(new SearchManager.ISearchListener() {
                @Override
                public void searchFinish(final BookInfo bookInfo) {
                    runMainUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("sunday","bookName = "+bookInfo.bookDetail.getName());
                            booksearchList.add(bookInfo);
                            bookSearchListener.bookSearchFinish(booksearchList);
                        }
                    });
                }
            });
            searchManager.searchTopWeb(name);
    }

    public void runMainUiThread(Runnable runnable){
        handler.post(runnable);
    }

}
