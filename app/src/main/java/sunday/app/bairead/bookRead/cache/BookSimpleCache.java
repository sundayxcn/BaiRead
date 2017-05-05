package sunday.app.bairead.bookRead.cache;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

import rx.Observable;
import sunday.app.bairead.data.setting.BookChapter;
import sunday.app.bairead.data.setting.BookInfo;
import sunday.app.bairead.data.setting.Chapter;
import sunday.app.bairead.download.IBookDownload;
import sunday.app.bairead.parse.ParseBase;
import sunday.app.bairead.utils.FileManager;

/**
 * Created by zhongfei.sun on 2017/5/3.
 */

public class BookSimpleCache implements IBookChapterCache {

    public static final String DIR = "chapterCache";

    public static final int CACHE_COUNT = 6;

    private IBookDownload mBookDownload;

    private ParseBase<BookChapter> mBookChapterParse;

    private ParseBase<String> mChapterTextParse;

    private BookInfo mBookInfo;

    private String fullDir;

    private int endIndex;

    Product product;
    /**
     * 生产者 - 内存缓存
     */
    boolean isProductRun;
    private LinkedBlockingQueue mChapterQueue;

    public BookSimpleCache(@NonNull IBookDownload bookDownload,
                           @NonNull ParseBase<BookChapter> bookChapterParse,
                           @NonNull ParseBase<String> chapterTextParse){
        mChapterQueue = new LinkedBlockingQueue(CACHE_COUNT);
        mBookDownload = bookDownload;
        mBookChapterParse = bookChapterParse;
        mChapterTextParse = chapterTextParse;

    }

    @Override
    public void start(BookInfo bookInfo) {
        mBookInfo = bookInfo;
        fullDir = FileManager.PATH + "/" + mBookInfo.bookDetail.getName() + "/" + DIR;
        endIndex = mBookInfo.bookChapter.getChapterCount() - 1 ;
        int index = bookInfo.bookChapter.getChapterIndex();
        if (product == null || !product.isAlive()) {
            product = new Product();
            product.setIndex(index);
            product.start();
        } else if (product.isAlive()) {
            product.setIndex(index);
        }
    }

    @Override
    public void stop() {
        mChapterQueue.clear();
        isProductRun = false;
        product = null;
    }

    @Override
    public void setIndex(int index) {
        if (product == null || !product.isAlive()) {
            product = new Product();
            product.setIndex(index);
            product.start();
        } else if (product.isAlive()) {
            product.setIndex(index);
        }
    }

    @Override
    public void remove(int index) {
        Chapter chapter = mBookInfo.bookChapter.getChapter(index);
        if(mChapterQueue.remove(chapter)){
        }
    }

    @Override
    public Observable<Chapter> downloadChapter(int index) {
        return Observable.create(subscriber -> {
            Chapter chapter = mBookInfo.bookChapter.getChapter(index);
            String fileName = getFullName(chapter);
            try {
            if(!isChapterExists(chapter)){
                mBookDownload.downloadHtml(fileName,chapter.getLink());
            }
            String text = mChapterTextParse.from(fileName).parse();
            chapter.setText(text);
            } catch (IOException e) {
                subscriber.onError(e);
            }
            subscriber.onNext(chapter);
        });
    }


    public String getFullName(Chapter chapter){
        return fullDir + "/" + chapter.getNum() + ".html";
    }

    public String getDetailFileName(){
        return FileManager.PATH + "/" + mBookInfo.bookDetail.getName() + "/" + BookChapter.FileName;
    }

    public boolean isChapterExists(Chapter chapter) {
        String fileName = getFullName(chapter);
        File file = new File(fileName);
        return file.exists();
    }

    class Product extends Thread {
        private int index;

        @Override
        public void run() {
            //super.run();
            while (isProductRun) {
                if (index > endIndex) {
                    isProductRun = false;
                    Log.e("sunday", "product endIndex" );
                } else {
                    Chapter chapter = null;
                    try {
                        chapter = mBookInfo.bookChapter.getChapter(index);
                        String fileName = getFullName(chapter);
                        if (!isChapterExists(chapter)) {
                            String url = chapter.getLink();
                            mBookDownload.downloadHtml(fileName, url);
                        }
                        String text = mChapterTextParse.from(fileName).parse();
                        chapter.setText(text);
                        mChapterQueue.put(chapter);
                        index++;
                    }catch (IOException e){
                        //Log.e("sunday", "product Chapter title=" + chapter.getTitle());
                    }catch (NullPointerException e){

                    }catch (InterruptedException e){

                    }
                }
            }
        }

        public void setIndex(final int index) {
            mChapterQueue.clear();
            this.index = index;
            isProductRun = true;

        }
    }
}
