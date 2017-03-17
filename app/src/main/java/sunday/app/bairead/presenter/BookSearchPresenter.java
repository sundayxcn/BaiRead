package sunday.app.bairead.presenter;

import android.content.Context;
import android.os.Handler;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import sunday.app.bairead.activity.BookDetailActivity;
import sunday.app.bairead.database.BookInfo;
import sunday.app.bairead.database.WebInfo;
import sunday.app.bairead.download.BookDownLoad;
import sunday.app.bairead.tool.FileManager;
import sunday.app.bairead.tool.Temp;
import sunday.app.bairead.tool.ThreadManager;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by sunday on 2017/3/6.
 */

public class BookSearchPresenter {
    public static final String fileName = "searchHistory.txt";

    public interface IBookSearchListener{
        void historyAddFinish(String name);
        void historyLoadFinish(ArrayList<String> list);
        void bookSearchStart();
        void bookSearchFinish(ArrayList<BookDownLoad.SearchResult> searchResultArrayList);
        void bookSearchError();

    }

    public interface IBookInfoListener{
        void bookInfoStart();
        void bookInfoFinish(BookInfo bookInfo);
        void bookInfoError();
    }


    private Handler handler = new Handler();
    private IBookSearchListener bookSearchListener;
    private ArrayList<String> historyList;
    private ArrayList<BookDownLoad.SearchResult> bookSearchList;


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
            final String fullName = context.getCacheDir().getAbsolutePath() + "/" + fileName;
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


    public void searchBook(Context context,final String name){
        BookDownLoad bookDownLoad = new BookDownLoad();
        bookDownLoad.updateSearchAsync(new BookDownLoad.DownloadListener<ArrayList<BookDownLoad.SearchResult>>() {

            @Override
            public String getFileName() {
                return FileManager.SEARCH_FILE;
            }

            @Override
            public String getLink() {
                //String webName = WebInfo.TOP_WEB[0][0];
                //String webLink = WebInfo.TOP_WEB[0][1];
                String webSearchLink = WebInfo.TOP_WEB[0][2];
                //final WebInfo webInfo = new WebInfo(webName,webLink,webSearchLink);
                //return webInfo.getLink() + name ;
                return webSearchLink + name;
                //return webLink;
            }

            @Override
            public long getId() {
                return 0;
            }

            @Override
            public void onStart() {
                runMainUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bookSearchListener.bookSearchStart();
                    }
                });

            }

            @Override
            public void onError(int errorCode) {
                runMainUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bookSearchListener.bookSearchError();
                    }
                });

            }

            @Override
            public void onResult(ArrayList< BookDownLoad.SearchResult> arrayList) {
                final ArrayList<BookDownLoad.SearchResult> list = arrayList;
                runMainUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bookSearchListener.bookSearchFinish(list);
                    }
                });

            }
        });
    }

    public void updateBookDetail(final BookDownLoad.SearchResult searchResult,final IBookInfoListener bookInfoListener){
        BookDownLoad bookDownLoad = new BookDownLoad();
        bookDownLoad.updateBookInfoAsync(new BookDownLoad.DownloadListener<BookInfo>() {
            @Override
            public long getId() {
                return 0;
            }

            @Override
            public void onResult(final BookInfo newBookInfo) {
                runMainUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bookInfoListener.bookInfoFinish(newBookInfo);
                    }
                });

            }

            @Override
            public String getFileName() {
                return BookDownLoad.TEMP_FILE_NAME;
            }

            @Override
            public String getLink() {
                return searchResult.link;
            }

            @Override
            public void onStart() {
                runMainUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bookInfoListener.bookInfoStart();
                    }
                });

            }

            @Override
            public void onError(int errorCode) {
                runMainUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bookInfoListener.bookInfoError();
                    }
                });

            }
        });
    }


    public void runMainUiThread(Runnable runnable){
        handler.post(runnable);
    }

}
