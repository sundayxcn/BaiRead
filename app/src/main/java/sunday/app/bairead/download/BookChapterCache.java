package sunday.app.bairead.download;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Response;
import sunday.app.bairead.database.BookChapter;
import sunday.app.bairead.database.BookInfo;
import sunday.app.bairead.parse.ParseChapter;
import sunday.app.bairead.parse.ParseChapterText;
import sunday.app.bairead.parse.ParseXml;
import sunday.app.bairead.utils.FileManager;
import sunday.app.bairead.utils.NewChapterShow;
import sunday.app.bairead.utils.ThreadManager;

/**
 * Created by sunday on 2016/12/8.
 * 使用消费者-生产者模型来缓存内存章节，缓存大小 MAX_CACHE = 10
 * mChapterCacheMap 为资源锁
 */

public class BookChapterCache {

    public static final String DIR = "chapterCache";
    private static int MAX_CACHE = 10;
    private BookInfo bookinfo;

    private HashMap<Integer, BookChapter.Chapter> mChapterCacheMap = new HashMap<>();

    private String fullDir;
    private ChapterListener chapterListener;
    private Product product = new Product();
    private boolean isProductRun;
    private boolean online;

    private BookChapterCache() {

    }

    public static BookChapterCache getInstance() {
        return BookChapterCacheHolder.sInstance;
    }

    private static boolean isChapterExists(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }

    public BookInfo getBookInfo() {
        return bookinfo;
    }

    public boolean isOnline() {
        return online;
    }

    /**
     * 默认缓存到本地
     *
     * @param isOnline true 不缓存到本地
     */
    public void setOnline(boolean isOnline) {
        online = isOnline;
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
                if(list == null){
                    bookInfo.bookChapter = getChapter(bookInfo);
                    list = bookInfo.bookChapter.getChapterList();
                }
                for (BookChapter.Chapter chapter : list) {
                    final String fileName = fullDir + "/" + chapter.getNum() + ".html";
                    if (!isChapterExists(fileName)) {
                        final String url = chapter.getLink();
                        Response response = OKhttpManager.getInstance().connectUrl(url);
                        try {
                            FileManager.writeByte(fileName, response.body().bytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }finally {
                            response.body().close();
                        }
                    }
                }
                Log.e("sunday", "缓存完成");
            }
        });

    }

    public void setBookInfo(BookInfo bookinfo, ChapterListener chapterListener) {
        this.bookinfo = bookinfo;
        this.chapterListener = chapterListener;

        if (bookinfo.bookDetail.getId() == 0) {
            setOnline(true);
        } else {
            fullDir = FileManager.PATH + "/" + bookinfo.bookDetail.getName() + "/" + DIR;
            FileManager.createDir(fullDir);
        }

    }


    /**
     * 每次进入一本书之后，根据info重新缓存章节
     */
    public void initChapterRead() {
        NewChapterShow.getInstance().removeNewChapter(bookinfo.bookDetail.getId());
        chapterListener.initStart();
        ThreadManager.getInstance().work(new Runnable() {
            @Override
            public void run() {
                if (bookinfo.bookChapter.getChapterList() == null || bookinfo.bookChapter.getChapterCount() == 0){
                    bookinfo.bookChapter = getChapter(bookinfo);
                }

                /*
                 * 网络中断导致获取失败后，清除本地章节link缓存
                 * */
                if(bookinfo.bookChapter == null || bookinfo.bookChapter.getChapterCount() == 0){
                    String fileName = BookDownLoad.getFullChapterFileName(bookinfo.bookDetail.getName());
                    FileManager.deleteFile(fileName);
                    chapterListener.cacheError();
                }else{
                    init();
                }
                chapterListener.initEnd();
            }
        });
    }

    private void init() {
        synchronized (mChapterCacheMap) {
            mChapterCacheMap.clear();
            mChapterCacheMap.notifyAll();
        }
        if (!product.isAlive()) {
            startCache();
        } else {
            int index = bookinfo.bookChapter.getChapterIndex();
//            //缓存当前章节的前后5章
//            if (index - MAX_CACHE / 2 < 0) {
//                index = 0;
//            } else {
//                index = index - MAX_CACHE / 2;
//            }
            //缓存当前章节的后10章
            product.setIndex(index);
        }
    }

    public boolean nextChapter(Context context) {
        final int index = bookinfo.bookChapter.getChapterIndex() + 1;
        if (index == bookinfo.bookChapter.getChapterCount()) {
            Toast.makeText(context, "已到最后一章", Toast.LENGTH_SHORT).show();
            return false;
        }
        updateChapter(index);
        return true;
    }


    public static class ReadText {
        public String text;
        public String title;

        public ReadText(BookChapter.Chapter chapter) {
            text = chapter.getText();
            title = chapter.getTitle();
        }
    }

    private void updateChapter(final int index){
        bookinfo.bookChapter.setChapterIndex(index);
        final BookChapter.Chapter chapter = bookinfo.bookChapter.getChapter(index);
        if (chapter.getText() == null) {
            chapterListener.cacheStart();
            ThreadManager.getInstance().work(new Runnable() {
                @Override
                public void run() {
                    String text = getChapterText(index);
                    /*
                    网络中断导致获取不到，如果有缓存则清除缓存，重新下载
                    * */
                    if(text == null){
                        String fileName = getChapterTextFileName(index);
                        FileManager.deleteFile(fileName);
                        chapterListener.cacheError();
                    }else {
                        chapter.setText(text);
                        ReadText readText = new ReadText(chapter);
                        chapterListener.cacheEnd(readText);
                    }
                }
            });
        }

        updateChapterCache(index);

    }

    public boolean prevChapter(Context context) {
        final int index = bookinfo.bookChapter.getChapterIndex() - 1;
        if (index < 0) {
            Toast.makeText(context, "已到第一章", Toast.LENGTH_SHORT).show();
            return false;
        }
        updateChapter(index);
        return true;
    }

    public void setChapter(int index) {
        bookinfo.bookChapter.setChapterIndex(index);
        initChapterRead();
    }


    /**
     * 消费者
     * 生产一个章节之后，收到通知，如果生产的章节是要显示的章节则显示，并且从缓存中移除
     */
    public void updateChapterCache(final int index) {
        if (index == bookinfo.bookChapter.getChapterIndex()) {
            ThreadManager.getInstance().work(new Runnable() {
                @Override
                public void run() {
                    synchronized (mChapterCacheMap) {
                        mChapterCacheMap.remove(index);
                        mChapterCacheMap.notifyAll();
                    }
                }
            });
            BookChapter.Chapter chapter = bookinfo.bookChapter.getCurrentChapter();
            if (chapterListener != null) {
                ReadText readText = new ReadText(chapter);
                chapterListener.cacheEnd(readText);
            }
        }
    }

    public boolean isChapterExists(int chapterIndex) {
        String fileName = fullDir + "/" + bookinfo.bookChapter.getChapter(chapterIndex).getNum() + ".html";
        File file = new File(fileName);
        return file.exists();
    }

    public boolean isChapterExists(BookChapter.Chapter chapter) {
        String fileName = fullDir + "/" +chapter.getNum() + ".html";
        File file = new File(fileName);
        return file.exists();
    }


//    public File getChapterTextFile(int index) {
//        return new File(getChapterTextFileName(index));
//    }

    public String getChapterTextFileName(int index) {
        return fullDir + "/" + bookinfo.bookChapter.getChapter(index).getNum() + ".html";
    }

    //private  Thread customer  = new Customer();
    private void startCache() {
        product.setIndex(bookinfo.bookChapter.getChapterIndex());
        product.start();
        //customer.start();
    }

    public String getMarkTitle(int chapterIndex) {
        return bookinfo.bookChapter.getChapter(chapterIndex).getTitle();
    }

    public void stopCache(){
        isProductRun = false;
    }

    public String getMarkText(int chapterIndex) {
        String textT = getChapterText(chapterIndex);
        int length = textT.length() > 100 ? 100 : textT.length();
        return textT.substring(0, length);
    }

    private String getChapterText(int chapterIndex) {
        if (isOnline()) {
            return getChapterTextByOnline(chapterIndex, null);
        } else {
            return getChapterTextByCurrent(chapterIndex);
        }
    }

    private String getChapterTextByOnline(int chapterIndex, String fileName) {
        String url = bookinfo.bookChapter.getChapter(chapterIndex).getLink();
        return new BookDownLoad().updateBookChapterText(url, fileName);
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

    private BookChapter getChapter(BookInfo bookInfo) {
        BookChapter chapter;
        if (isOnline()) {
            chapter = getChapterByOnline(bookInfo, null);
        } else {
            chapter = getChapterByCurrent(bookInfo);
        }
        if(chapter != null) {
            chapter.setId(bookInfo.bookDetail.getId());
            chapter.setChapterIndex(bookInfo.bookChapter.getChapterIndex());
            chapter.setChapterPage(bookInfo.bookChapter.getChapterPage());
        }
        return chapter;
    }

    private BookChapter getChapterByOnline(BookInfo bookInfo, String fileName) {
        BookDownLoad bookDownLoad = new BookDownLoad();
        BookInfo newBookInfo = bookDownLoad.updateBookInfo(bookInfo, fileName);
        if(newBookInfo != null){
            return newBookInfo.bookChapter;
        }else {
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


    public interface ChapterListener {
        void initStart();

        void initEnd();

        void cacheStart();

        void cacheEnd(ReadText readText);

        void cacheError();
    }

    private static class BookChapterCacheHolder {
        private static final BookChapterCache sInstance = new BookChapterCache();
    }

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
                        int maxIndex = bookinfo.bookChapter.getChapterCount();
                        while (mChapterCacheMap.size() == MAX_CACHE || index == maxIndex) {
                            Log.e("sunday", "章节缓存已满10章");
                            mChapterCacheMap.wait();
                        }

                        BookChapter.Chapter chapter = bookinfo.bookChapter.getChapter(index);

                        //存在一个bug，本地文件没有内容，可能是网络中断产生的问题
                        //解决方式：检查文件有效性，如果无效，删除掉，重新下载

                        String text = getChapterText(index);
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
