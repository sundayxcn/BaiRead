package sunday.app.bairead.Download;

import android.text.Html;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Response;
import sunday.app.bairead.DataBase.BookChapter;
import sunday.app.bairead.DataBase.BookInfo;
import sunday.app.bairead.Parse.BookChapterTextParse;
import sunday.app.bairead.Parse.JsoupParse;
import sunday.app.bairead.Tool.FileManager;

/**
 * Created by sunday on 2016/12/8.
 * 使用消费者-生产者模型来缓存内存章节，缓存大小 MAX_CACHE = 10
 * mChapterCacheMap 为资源锁
 */

public class BookChapterCache {

    public static final String DIR = "chapterCache";
    private static int MAX_CACHE = 10;
    private static BookChapterCache bookCacheManager;

    private BookInfo bookinfo;

    private HashMap<Integer, BookChapter.Chapter> mChapterCacheMap = new HashMap<>();

    private String fullDir;
    private ChapterListener chapterListener;
    private Product product = new Product();
    private boolean isProductRun;

    private BookChapterCache() {

    }

    public static BookChapterCache getInstance() {
        if (bookCacheManager == null) {
            bookCacheManager = new BookChapterCache();
        }
        return bookCacheManager;
    }

    /**
     * 下载所有章节到本地
     */
    public void downloadAllChpater(final BookInfo bookInfo) {
        new Thread(){
            @Override
            public void run() {
                //super.run();
                ArrayList<BookChapter.Chapter> list = bookInfo.bookChapter.getChapterList();
                for(BookChapter.Chapter chapter : list){
                    final String fileName = fullDir + "/" + chapter.getNum() + ".html";
                    if(!isChapterExists(fileName)){
                        final String url = chapter.getLink();
                        Response response = OKhttpManager.getInstance().connectUrl(url);
                        try {
                            FileManager.writeByte(fileName, response.body().bytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                Log.e("sunday","缓存完成");
            }
        }.start();
    }

    public void setBookInfo(BookInfo bookinfo, ChapterListener chapterListener) {
        this.bookinfo = bookinfo;
        this.chapterListener = chapterListener;

        fullDir = FileManager.PATH + "/" + bookinfo.bookDetail.getName() + "/" + DIR;
        FileManager.createDir(fullDir);
    }

    /**
     * 每次进入一本书之后，根据info重新缓存章节
     * */
    public void initChapterRead() {
        synchronized(mChapterCacheMap) {
            mChapterCacheMap.clear();
            mChapterCacheMap.notifyAll();
        }
        if(!product.isAlive()){
            startCache();
        }else{
            int index = bookinfo.bookChapter.getChapterIndex();
            product.setIndex(index);
        }
    }

    public void nextChapter() {
        int index = bookinfo.bookChapter.getChapterIndex() + 1;
        bookinfo.bookChapter.setChapterIndex(index);
        updateChapterCache(index);

    }

    public void prevChapter() {
        int index = bookinfo.bookChapter.getChapterIndex() - 1;
        BookChapter.Chapter chapter = bookinfo.bookChapter.getChapter(index);
        if (chapter.getText() != null) {
            chapterListener.end(chapter);
        }
    }

    /**
     * 消费者
     * 生产一个章节之后，收到通知，如果生产的章节是要显示的章节则显示，并且从缓存中移除
     */
    public void updateChapterCache(int index) {
        if (index == bookinfo.bookChapter.getChapterIndex()) {
            synchronized (mChapterCacheMap) {
                mChapterCacheMap.remove(index);
                mChapterCacheMap.notifyAll();
            }
            BookChapter.Chapter chapter = bookinfo.bookChapter.getCurrentChapter();
            if (chapterListener != null) {
                chapterListener.end(chapter);
            }
        }
    }

    private boolean isChapterExists(int chapterIndex) {
        String fileName = fullDir + "/" + bookinfo.bookChapter.getChapter(chapterIndex).getNum() + ".html";
        File file = new File(fileName);
        return file.exists();
    }

    private boolean isChapterExists(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }

    //private  Thread customer  = new Customer();
    private void startCache() {
        product.setIndex(bookinfo.bookChapter.getChapterIndex());
        product.start();
        //customer.start();
    }

    public interface ChapterListener {
        void end(BookChapter.Chapter chapter);
    }

//    public void stopCache(){
//        isProductRun = false;
//    }

    /**
     * 生产者-内存缓存
     */
    class Product extends Thread {
        private int index;

        @Override
        public void run() {
            //super.run();
            while (isProductRun) {
                synchronized (mChapterCacheMap) {
                    try {

                        while (mChapterCacheMap.size() == MAX_CACHE) {
                            Log.e("sunday", "章节缓存已满10章");
                            mChapterCacheMap.wait();
                        }
                        final BookChapter.Chapter chapter = bookinfo.bookChapter.getChapter(index);
                        final String fileName = fullDir + "/" + chapter.getNum() + ".html";
                        boolean currentFile = isChapterExists(index);
                        if (!currentFile) {
                            String url = chapter.getLink();
                            Response response = OKhttpManager.getInstance().connectUrl(url);
                            FileManager.writeByte(fileName, response.body().bytes());
                        }

                        String text = JsoupParse.from(fileName, new BookChapterTextParse());
                        text = String.valueOf(Html.fromHtml(text));
                        chapter.setText(text);
                        Integer integer = Integer.valueOf(index);
                        mChapterCacheMap.put(integer, chapter);
                        mChapterCacheMap.notify();
                        //生产一个章节之后，发出通知，如果生产的章节是要显示的章节则显示
                        updateChapterCache(index);
                        Log.e("sunday", "已缓存章节:" + chapter.getTitle());
                        index++;

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public void setIndex(int index) {
            this.index = index;
            isProductRun = true;
        }
    }
}
