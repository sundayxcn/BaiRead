package sunday.app.bairead.bookRead;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import okhttp3.Response;
import sunday.app.bairead.database.BaiReadApplication;
import sunday.app.bairead.database.BookChapter;
import sunday.app.bairead.database.BookInfo;
import sunday.app.bairead.download.BookDownLoad;
import sunday.app.bairead.download.OKhttpManager;
import sunday.app.bairead.parse.ParseChapter;
import sunday.app.bairead.parse.ParseChapterText;
import sunday.app.bairead.parse.ParseXml;
import sunday.app.bairead.utils.FileManager;
import sunday.app.bairead.utils.Temp;
import sunday.app.bairead.utils.ThreadManager;

/**
 * Created by zhongfei.sun on 2017/3/28.
 */

public class BookChapterCacheNew {

    public interface IBookChapterCacheListener{
        void updateStart();
        void updateFinish();
        void updateReadTextSuccess(ReadText readText);
        void updateReadTextFailed(int errorCode);
    }



    private static class BookChapterCacheHolder {
        private static final BookChapterCacheNew sInstance = new BookChapterCacheNew();
    }
    public static final String DIR = "chapterCache";
    static final String TEMP_TEXT_NAME = FileManager.PATH + "/" +"temp" + "/" +"tempChapterText.html";
    public static final int CACHE_COUNT = 6;
    private BookInfo bookInfo;
    private ArrayList<BookChapter.Chapter> chapterArrayList;
    private IBookChapterCacheListener bookChapterCacheListener;
    private boolean online;
    private String fullDir;
    public static BookChapterCacheNew getInstance(){
        return BookChapterCacheHolder.sInstance;
    }

    public static class ReadText {
        public String text;
        public String title;

        public ReadText(BookChapter.Chapter chapter) {
            text = chapter.getText();
            title = chapter.getTitle();
        }
    }

    /**
     * 默认缓存到本地
     * @param
     */
    public boolean isOnline() {
        return online;
    }

    public String getBookName(){
        return bookInfo.bookDetail.getName();
    }

    public void init(Context context ,long bookId ,IBookChapterCacheListener listener){
        BaiReadApplication baiReadApplication = (BaiReadApplication)context.getApplicationContext();
        //没有设置ID，表示没有加入书架，就是在线阅读，不用缓存
        if(bookId == 0){
            bookInfo = Temp.getInstance().getBookInfo();
            //Temp.getInstance().clearBookInfo();
        }else {
            bookInfo = baiReadApplication.getBookModel().getBookInfo(bookId);
        }

        this.bookChapterCacheListener = listener;
        if (bookInfo.bookDetail.getId() == 0) {
            online = true;
        } else {
            fullDir = FileManager.PATH + "/" + bookInfo.bookDetail.getName() + "/" + DIR;
            FileManager.createDir(fullDir);
        }
        chapterArrayList = bookInfo.bookChapter.getChapterList();
        //首次进入
        if(chapterArrayList == null || chapterArrayList.size() == 0) {
            bookChapterCacheListener.updateStart();
            ThreadManager.getInstance().work(new Runnable() {
                @Override
                public void run() {
                    bookInfo.bookChapter = getChapter(bookInfo);
                    if(bookInfo.bookChapter ==  null){
                        runFailedRunnable(0,BookDownLoad.ERROR_NetworkFailed);
                    }else {
                        chapterArrayList = bookInfo.bookChapter.getChapterList();
                        initChapter();
                    }
                }
            });
        }else{
            initChapter();
        }
    }


    Product product;
    private void initChapter(){
        int index = bookInfo.bookChapter.getChapterIndex();
        loadReadText(index);
        if(product == null || !product.isAlive()){
            product = new Product();
            product.setIndex(index+1);
            product.start();
        }else if(product.isAlive()){
            product.setIndex(index+1);
        }
    }


    public void closeCache(){

        linkedBlockingQueue.clear();
        isProductRun = false;
        product = null;
        //BookChapterCacheHolder.sInstance = null;
        //try{
        //product.interrupt();
        //}catch (InterruptedIOException e){
        //    e.printStackTrace();
        //}
    }


    public boolean prevChapter() {
        int chapterIndex = bookInfo.bookChapter.getChapterIndex();
        final int index = chapterIndex - 1;
        if (index < 0) {
            return false;
        }else {
            updateChapterIndex(index);
            loadReadText(index);
            return true;
        }
    }

    public boolean nextChapter() {
        int chapterCount = bookInfo.bookChapter.getChapterCount();
        int index = bookInfo.bookChapter.getChapterIndex() + 1;
        if (index == chapterCount) {
            return false;
        }else {
            updateChapterIndex(index);
            loadReadText(index);
            try {
                if(linkedBlockingQueue.size() > 0) {
                    linkedBlockingQueue.take();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        }
    }


    public ArrayList<BookChapter.Chapter> getChapterArrayList(){
        return chapterArrayList;
    }


    /**
     * 从章节列表和书签列表调用
     * */
    public void setChapterIndex(int index){
        updateChapterIndex(index);
        initChapter();
    }


    public void updateChapterIndex(int index){
        bookInfo.bookChapter.setChapterIndex(index);
    }

    public void loadReadText(int chapterIndex){
        BookChapter.Chapter chapter = chapterArrayList.get(chapterIndex);
        if(chapter.getText() == null){
            updateChapterText(chapterIndex);
        }else{
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
                return online ?  TEMP_TEXT_NAME : getChapterTextFileName(chapterIndex);
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
                runFailedRunnable(chapterIndex,errorCode);
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
            if(text == null) {
                FileManager.deleteFile(fileName);
                updateChapterTextByOnline(chapterIndex);
            }else{
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
        String fileName = fullDir + "/" +chapter.getNum() + ".html";
        File file = new File(fileName);
        return file.exists();
    }

    private Handler handler = new Handler(Looper.getMainLooper());

    public void runOnUiThread(Runnable runnable){
        handler.post(runnable);
    }


    private void runStartRunnable(final int chapterIndex){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(chapterIndex == bookInfo.bookChapter.getChapterIndex()) {
                    bookChapterCacheListener.updateStart();
                }
            }
        });
    }

    private void runFinishRunnable(final int chapterIndex){
        //finishRunnable.setChapterIndex(chapterIndex);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(chapterIndex == bookInfo.bookChapter.getChapterIndex()) {
                   bookChapterCacheListener.updateFinish();
                }
            }
        });
    }

    private void runSuccessRunnable(final int chapterIndex){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(chapterIndex == bookInfo.bookChapter.getChapterIndex()) {
                    BookChapter.Chapter chapter = chapterArrayList.get(chapterIndex);
                    ReadText readText = new ReadText(chapter);
                    bookChapterCacheListener.updateReadTextSuccess(readText);
                }
            }
        });
    }

    private void runFailedRunnable(final int chapterIndex,final int errorCode){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(chapterIndex == bookInfo.bookChapter.getChapterIndex()) {
                    bookChapterCacheListener.updateReadTextFailed(errorCode);
                }
            }
        });
    }


    /**
     * 生产者 - 内存缓存
     */
    boolean isProductRun;
    LinkedBlockingQueue<Integer> linkedBlockingQueue = new LinkedBlockingQueue(CACHE_COUNT);

    class Product extends Thread {
        private int index;

        @Override
        public void run() {
            //super.run();
            while (isProductRun) {
                if(index >= chapterArrayList.size()){
                    isProductRun = false;
                    Log.e("sunday","product Chapter size=" + CACHE_COUNT);
                }else {
                    String text = getChapterText(index);
                    BookChapter.Chapter chapter = chapterArrayList.get(index);
                    chapter.setText(text);
                    try {
                        linkedBlockingQueue.put(index);
                        index++;
                        Log.e("sunday","product Chapter title="+chapter.getTitle());
                    } catch (InterruptedException e) {
                        Log.e("sunday","product error");
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
                if(list == null){
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
                        }finally {
                            response.body().close();
                        }
                    }
                }
                Log.e("sunday", "缓存完成");
            }
        });

    }

}
