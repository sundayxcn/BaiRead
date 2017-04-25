package sunday.app.bairead.bookRead.cache;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import sunday.app.bairead.bookRead.BookReadContract;
import sunday.app.bairead.data.setting.BookChapter;
import sunday.app.bairead.data.setting.BookInfo;
import sunday.app.bairead.download.BookDownLoad;
import sunday.app.bairead.download.OKhttpManager;
import sunday.app.bairead.parse.ParseChapter;
import sunday.app.bairead.parse.ParseChapterText;
import sunday.app.bairead.parse.ParseXml;
import sunday.app.bairead.utils.FileManager;
import sunday.app.bairead.utils.ThreadManager;

/**
 * Created by zhongfei.sun on 2017/3/28.
 */

public class BookChapterCacheNew implements BookReadContract.ChapterCache {

    public static final String DIR = "chapterCache";
    public static final int CACHE_COUNT = 6;
    static final String TEMP_TEXT_NAME = FileManager.PATH + "/" + "temp" + "/" + "tempChapterText.html";
    BookReadContract.IBookChapterCacheListener bookChapterCacheListener;
    Product product;
    /**
     * 生产者 - 内存缓存
     */
    boolean isProductRun;
    LinkedBlockingQueue<Integer> linkedBlockingQueue = new LinkedBlockingQueue(CACHE_COUNT);
    private BookInfo bookInfo;
    private ArrayList<BookChapter.Chapter> chapterArrayList;
    private boolean online;
    private String fullDir;
    private Handler handler = new Handler(Looper.getMainLooper());

    public static BookChapterCacheNew getInstance() {
        return BookChapterCacheHolder.sInstance;
    }

    @Override
    public void start(BookInfo bookInfo) {
        this.bookInfo = bookInfo;
        if (bookInfo.bookDetail.getId() == 0) {
            online = true;
        } else {
            fullDir = FileManager.PATH + "/" + bookInfo.bookDetail.getName() + "/" + DIR;
            //FileManager.createDir(fullDir);
        }
        chapterArrayList = bookInfo.bookChapter.getChapterList();
        //首次进入
        if (chapterArrayList == null || chapterArrayList.size() == 0) {

            Observable.create((Observable.OnSubscribe<BookChapter>) subscriber -> subscriber.onNext(getChapter(bookInfo))).
                    subscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread())
                    .subscribe(bookChapter -> {
                        if (bookChapter == null) {

                        } else {
                            chapterArrayList = bookChapter.getChapterList();
                            initChapter();
                        }
                    });
        } else {
            initChapter();
        }
    }

    /**
     * 默认缓存到本地
     *
     * @param
     */
    public boolean isOnline() {
        return online;
    }

    public String getBookName() {
        return bookInfo.bookDetail.getName();
    }

    private void initChapter() {
        int index = bookInfo.bookChapter.getChapterIndex();
        loadReadText(index);
        if (product == null || !product.isAlive()) {
            product = new Product();
            product.setIndex(index + 1);
            product.start();
        } else if (product.isAlive()) {
            product.setIndex(index + 1);
        }
    }

    public void stop() {

        linkedBlockingQueue.clear();
        isProductRun = false;
        product = null;
        //BookChapterCacheHolder.sInstance = null;
        //try{
        //product.interrupt();
        //}catch (InterruptedIOException e){
        //    e.printStackTrace();
        //}
        bookChapterCacheListener = null;
    }

    public void prevChapter(int index) {
        updateChapterIndex(index);
        loadReadText(index);
    }

    public void nextChapter(int index) {
        updateChapterIndex(index);
        loadReadText(index);
        try {
            if (linkedBlockingQueue.size() > 0) {
                linkedBlockingQueue.take();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setIndex(int index) {
        updateChapterIndex(index);
        initChapter();
    }

    @Override
    public void cacheAllChapter() {

    }

    @Override
    public void setBookChapterCacheListener(BookReadContract.IBookChapterCacheListener bookChapterCacheListener) {
        this.bookChapterCacheListener = bookChapterCacheListener;
    }

    public ArrayList<BookChapter.Chapter> getChapterArrayList() {
        return chapterArrayList;
    }

    public int getChapterIndex() {
        return bookInfo.bookChapter.getChapterIndex();
    }

    /**
     * 从章节列表和书签列表调用
     */
    public void setChapterIndex(int index) {
        updateChapterIndex(index);
        initChapter();
    }

    public void updateChapterIndex(int index) {
        bookInfo.bookChapter.setChapterIndex(index);
    }

    public void loadReadText(int chapterIndex) {
        BookChapter.Chapter chapter = chapterArrayList.get(chapterIndex);
        if (chapter.getText() == null) {
            updateChapterText(chapterIndex);
        } else {
            runSuccessRunnable(chapterIndex);
        }
    }

    private BookChapter getChapter(BookInfo bookInfo) {
        BookChapter chapter;
        if (isOnline()) {
            chapter = getChapterByOnline(bookInfo, null);
        } else {
            chapter = getChapterByCurrent(bookInfo);
        }

        if (chapter != null) {
            chapter.setId(bookInfo.bookDetail.getId());
            chapter.setChapterIndex(bookInfo.bookChapter.getChapterIndex());
            chapter.setChapterPage(bookInfo.bookChapter.getChapterPage());
        }

        bookInfo.bookChapter = chapter;

        return bookInfo.bookChapter;
    }

    private BookChapter getChapterByOnline(BookInfo bookInfo, String fileName) {
        BookDownLoad bookDownLoad = new BookDownLoad();
        BookInfo newBookInfo = bookDownLoad.updateBookInfo(bookInfo, fileName);
        if (newBookInfo != null) {
            return newBookInfo.bookChapter;
        } else {
            return null;
        }
    }

    private BookChapter getChapterByCurrent(BookInfo bookInfo) {
        String fileName = BookDownLoad.getFullChapterFileName(bookInfo.bookDetail.getName());
        File file = new File(fileName);
        if (file.exists()) {
            return ParseXml.createParse(ParseChapter.class).from(fileName).parse();
        } else {
            return getChapterByOnline(bookInfo, fileName);
        }

    }

    private void updateChapterText(int chapterIndex) {
        if (isOnline()) {
            updateChapterTextByOnline(chapterIndex);
        } else {
            updateChapterTextByCurrent(chapterIndex);
        }
    }

    private void updateChapterTextByOnline(final int chapterIndex) {
        BookDownLoad.getInstance().updateBookChapterTextAsync(new BookDownLoad.DownloadListener<String>() {
            @Override
            public String getFileName() {
                return online ? TEMP_TEXT_NAME : getChapterTextFileName(chapterIndex);
            }

            @Override
            public String getLink() {
                return chapterArrayList.get(chapterIndex).getLink();
            }

            @Override
            public long getId() {
                return bookInfo.bookDetail.getId();
            }

            @Override
            public void onStart() {
                runStartRunnable(chapterIndex);
            }

            @Override
            public void onError(int errorCode) {
                runFailedRunnable(chapterIndex, errorCode);
            }

            @Override
            public void onResult(String result) {
                chapterArrayList.get(chapterIndex).setText(result);
                runSuccessRunnable(chapterIndex);
                runFinishRunnable(chapterIndex);
            }
        });
    }

    private void updateChapterTextByCurrent(int chapterIndex) {
        String fileName = getChapterTextFileName(chapterIndex);
        File file = new File(fileName);
        if (file.exists()) {
            String text = ParseXml.createParse(ParseChapterText.class).from(fileName).parse();
            //有一种情况是下载到一半网络中断造成文件异常。所以此处异常处理
            if (text == null) {
                FileManager.deleteFile(fileName);
                updateChapterTextByOnline(chapterIndex);
            } else {
                chapterArrayList.get(chapterIndex).setText(text);
                runSuccessRunnable(chapterIndex);
                runFinishRunnable(chapterIndex);
            }
        } else {
            updateChapterTextByOnline(chapterIndex);
        }
    }

    private String getChapterText(int chapterIndex) {
        if (isOnline()) {
            return getChapterTextByOnline(chapterIndex, null);
        } else {
            return getChapterTextByCurrent(chapterIndex);
        }
    }

    private String getChapterTextByOnline(int chapterIndex, String fileName) {
        String url = bookInfo.bookChapter.getChapter(chapterIndex).getLink();
        return BookDownLoad.getInstance().updateBookChapterText(url, fileName);
    }

    private String getChapterTextByCurrent(int chapterIndex) {
        String fileName = getChapterTextFileName(chapterIndex);
        String text;
        if (new File(fileName).exists()) {
            text = ParseXml.createParse(ParseChapterText.class).from(fileName).parse();
        } else {
            text = getChapterTextByOnline(chapterIndex, fileName);
        }
        return text;
    }

    public String getChapterTextFileName(int index) {
        return fullDir + "/" + chapterArrayList.get(index).getNum() + ".html";
    }

    public boolean isChapterExists(BookChapter.Chapter chapter) {
        String fileName = fullDir + "/" + chapter.getNum() + ".html";
        File file = new File(fileName);
        return file.exists();
    }

    public void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }

    private void runStartRunnable(final int chapterIndex) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (chapterIndex == bookInfo.bookChapter.getChapterIndex()) {
                    bookChapterCacheListener.updateStart();
                }
            }
        });
    }

    private void runFinishRunnable(final int chapterIndex) {
        //finishRunnable.setChapterIndex(chapterIndex);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (chapterIndex == bookInfo.bookChapter.getChapterIndex()) {
                    bookChapterCacheListener.updateFinish();
                }
            }
        });
    }

    private void runSuccessRunnable(final int chapterIndex) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (chapterIndex == bookInfo.bookChapter.getChapterIndex()) {
                    BookChapter.Chapter chapter = chapterArrayList.get(chapterIndex);
                    BookReadText readText = new BookReadText(chapter);
                    bookChapterCacheListener.updateReadTextSuccess(readText);
                }
            }
        });
    }

    private void runFailedRunnable(final int chapterIndex, final int errorCode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (chapterIndex == bookInfo.bookChapter.getChapterIndex()) {
                    bookChapterCacheListener.updateReadTextFailed(errorCode);
                }
            }
        });
    }

    public String getMarkTitle(int chapterIndex) {
        return bookInfo.bookChapter.getChapter(chapterIndex).getTitle();
    }

    public String getMarkText(int chapterIndex) {
        String textT = getChapterText(chapterIndex);
        int length = textT.length() > 100 ? 100 : textT.length();
        return textT.substring(0, length);
    }

    /**
     * 下载所有章节到本地
     */
    public void downloadAllChpater(final BookInfo bookInfo) {
        ThreadManager.getInstance().work(new Runnable() {
            @Override
            public void run() {
                ArrayList<BookChapter.Chapter> list = bookInfo.bookChapter.getChapterList();
                //首次进入书架，并没有读取章节
                if (list == null) {
                    bookInfo.bookChapter = getChapter(bookInfo);
                    list = bookInfo.bookChapter.getChapterList();
                }
                for (BookChapter.Chapter chapter : list) {
                    final String fileName = fullDir + "/" + chapter.getNum() + ".html";
                    if (!isChapterExists(chapter)) {
                        final String url = chapter.getLink();
                        Response response = OKhttpManager.getInstance().connectUrl(url);
                        try {
                            FileManager.writeByte(fileName, response.body().bytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            response.body().close();
                        }
                    }
                }
                Log.e("sunday", "缓存完成");
            }
        });

    }

    private static class BookChapterCacheHolder {
        private static final BookChapterCacheNew sInstance = new BookChapterCacheNew();
    }

    class Product extends Thread {
        private int index;

        @Override
        public void run() {
            //super.run();
            while (isProductRun) {
                if (index >= chapterArrayList.size()) {
                    isProductRun = false;
                    Log.e("sunday", "product Chapter size=" + CACHE_COUNT);
                } else {
                    String text = getChapterText(index);
                    BookChapter.Chapter chapter = chapterArrayList.get(index);
                    chapter.setText(text);
                    try {
                        linkedBlockingQueue.put(index);
                        index++;
                        Log.e("sunday", "product Chapter title=" + chapter.getTitle());
                    } catch (InterruptedException e) {
                        Log.e("sunday", "product error");
                        e.printStackTrace();
                    }
                }
            }
        }

        public void setIndex(final int index) {
            linkedBlockingQueue.clear();
            this.index = index;
            isProductRun = true;

        }
    }

}
