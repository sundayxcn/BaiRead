package sunday.app.bairead.bookSearch;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import sunday.app.bairead.R;
import sunday.app.bairead.data.setting.BookChapter;
import sunday.app.bairead.data.setting.BookDetail;
import sunday.app.bairead.data.setting.BookInfo;
import sunday.app.bairead.download.IBookDownload;
import sunday.app.bairead.parse.ParseBase;
import sunday.app.bairead.utils.ActivityUtils;
import sunday.app.bairead.utils.FileManager;


/**
 * Created by sunday on 2017/3/6.
 */

public class BookSearchPresenter implements BookSearchContract.Present {
    public static final String SEARCH_FILE = "searchHistory.txt";

    private BookSearchContract.View mView;
    private IBookDownload mBookDownload;
    private ParseBase<List<String>> mSearchParse;
    private ParseBase<BookDetail> mDetailParse;
    private ParseBase<BookChapter> mChapterParse;
    private List<String> mHistoryList;
    private AtomicInteger atomicInteger;

    public BookSearchPresenter(@NonNull BookSearchContract.View view,
                               @NonNull IBookDownload bookDownload,
                               @NonNull ParseBase<List<String>> searchParse,
                               @NonNull ParseBase<BookDetail> detailParse,
                               @NonNull ParseBase<BookChapter> chapterParse
    ) {
        mView = view;
        mBookDownload = bookDownload;
        mSearchParse = searchParse;
        mDetailParse = detailParse;
        mChapterParse = chapterParse;
    }

    private void checkStart(int count) {
        atomicInteger = new AtomicInteger(count);
    }

    private void checkFinish() {
        int i = atomicInteger.decrementAndGet();
        if (i <= 0) {
            mView.hideLoading();
        }
    }

    /**
     * 将百度搜索结果的网站长地址生成临时文件名称
     */
    private String getBaiduLinkCodeString(String link) {
        String[] cs = link.split("\\.");
        String cs2 = cs[cs.length - 1];
        String name = cs2.substring(cs2.length() - 20, cs2.length() - 1);
        return FileManager.TEMP_DIR + "/" + name + ".html";
    }

    @Override
    public void start() {
        String fileName = FileManager.DIR + "/" + SEARCH_FILE;
        try {
            mHistoryList = FileManager.readFileByLine(fileName);
            mView.showHistory(mHistoryList);
        } catch (FileNotFoundException e) {
            mHistoryList = new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            mHistoryList = new ArrayList<>();
        }
    }

    @Override
    public void search(String book) {
        final String url = "http://www.baidu.com/s?q1=" + book + "&q2=&q3=&q4=&gpc=stf&ft=&q5=1&q6=&tn=baiduadv";
        mView.showLoading();
        mView.clearSearch();
        Observable.create((Observable.OnSubscribe<List<String>>) subscriber -> {
            try {
                mBookDownload.downloadHtml(FileManager.TEMP_BAIDU_SEARCH_FILE, url);
                List<String> list = mSearchParse.from(FileManager.TEMP_BAIDU_SEARCH_FILE).parse();
                checkStart(list.size());
                subscriber.onNext(list);
            } catch (IOException e) {
                subscriber.onError(e);
            }catch (NullPointerException e){
                subscriber.onError(e);
            }
        }).flatMap(strings -> Observable.from(strings)).map(url1 -> {
            String fileName = getBaiduLinkCodeString(url1);
            try {
                mBookDownload.downloadHtml(fileName, url1);
                BookInfo bookInfo = new BookInfo();
                bookInfo.bookDetail = mDetailParse.from(fileName).parse();
                bookInfo.bookChapter = mChapterParse.from(fileName).parse();
                return bookInfo;
            } catch (IOException e) {
                e.printStackTrace();
            }catch (NullPointerException e){
                e.printStackTrace();
            }
            return null;
        }).filter(bookInfo -> {
                    checkFinish();
                    return (bookInfo != null && bookInfo.bookDetail != null);
                }
        ).
        subscribeOn(Schedulers.io()).
        observeOn(AndroidSchedulers.mainThread()).
        subscribe(new Subscriber<BookInfo>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                if(e instanceof NullPointerException) {
                    //搜索失败
                    mView.hideLoading();
                    mView.showToast(R.string.network_exception);
                }else{
                    checkFinish();
                }
            }

            @Override
            public void onNext(BookInfo bookInfo) {
                mView.showResult(bookInfo);
                checkFinish();
            }
        });
    }

    @Override
    public void addHistory(String title) {
        mHistoryList.add(title);
        Observable.create((Observable.OnSubscribe<String>) subscriber -> {
            String fileName = FileManager.DIR + "/" + SEARCH_FILE;
            try {
                FileManager.writeFileByLine(fileName, title);
                subscriber.onCompleted();
            } catch (IOException e) {
                subscriber.onError(e);
            }
        }).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                mView.showHistory(mHistoryList);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof FileNotFoundException) {

                }
                e.printStackTrace();
            }

            @Override
            public void onNext(String s) {

            }
        });

    }

    @Override
    public void clearHistory() {
        mHistoryList.clear();
        mView.showHistory(mHistoryList);
    }

    @Override
    public void stop() {
        clearTempFile();
    }

    public void clearTempFile() {
        FileManager.clearTempFolder();
    }

    @Override
    public void goBookDetail(Context context, BookInfo bookInfo) {
        ActivityUtils.gotoBookDetail(context, bookInfo);
    }
}
