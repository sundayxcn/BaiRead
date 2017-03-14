package sunday.app.bairead.presenter;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;

import sunday.app.bairead.database.BaiReadApplication;
import sunday.app.bairead.database.BookChapter;
import sunday.app.bairead.database.BookInfo;
import sunday.app.bairead.database.BookMarkInfo;
import sunday.app.bairead.download.BookChapterCache;
import sunday.app.bairead.tool.PreferenceSetting;
import sunday.app.bairead.tool.Temp;
import sunday.app.bairead.tool.ThreadManager;
import sunday.app.bairead.view.BookTextView;

/**
 * Created by sunday on 2017/3/6.
 */

public class BookReadPresenter implements BookChapterCache.ChapterListener {

    @Override
    public void initStart() {
        runMainUiThread(new Runnable() {
            @Override
            public void run() {
                bookReadPresenterListener.onLoadStart();
            }
        });

    }

    @Override
    public void initEnd() {
        runMainUiThread(new Runnable() {
            @Override
            public void run() {
                bookReadPresenterListener.onLoadFinish();
            }
        });
    }

    @Override
    public void cacheStart() {
        runMainUiThread(new Runnable() {
            @Override
            public void run() {
                bookReadPresenterListener.onLoadStart();
            }
        });
    }

    @Override
    public void cacheEnd(final BookChapterCache.ReadText readText) {
        runMainUiThread(new Runnable() {
            @Override
            public void run() {
                bookReadPresenterListener.onReadTextChange(readText);
                bookReadPresenterListener.onLoadFinish();
            }
        });
    }

    @Override
    public void cacheError() {
        runMainUiThread(new Runnable() {
            @Override
            public void run() {
                bookReadPresenterListener.onLoadError();
            }
        });
    }

    public interface IBookReadPresenterListener{
        void onLoadStart();
        void onLoadFinish();
        void onLoadError();
        void onReadTextChange(BookChapterCache.ReadText readText);
        void onReadSizeChange(BookTextView.ReadSize readSize);
    }

    private Context context;
    private Handler handler = new Handler();


    private BookInfo bookInfo;

    private ArrayList<BookMarkInfo> bookMarkList;

    private IBookReadPresenterListener bookReadPresenterListener;

    public BookReadPresenter(Context c,IBookReadPresenterListener bookReadPresenterListener,long bookId){
        context = c;
        this.bookReadPresenterListener = bookReadPresenterListener;
        BaiReadApplication baiReadApplication = (BaiReadApplication)c.getApplicationContext();
        //没有设置ID，表示没有加入书架，就是在线阅读，不用缓存
        if(bookId == 0){
            bookInfo = Temp.getInstance().getBookInfo();
            Temp.getInstance().clearBookInfo();
        }else {
            bookInfo = baiReadApplication.getBookModel().getBookInfo(bookId);
        }
    }

    public void init(){
        BookChapterCache bookChapterCache = BookChapterCache.getInstance();
        bookChapterCache.setBookInfo(bookInfo,this);
        bookChapterCache.initChapterRead();
        ThreadManager.getInstance().work(new Runnable() {
            @Override
            public void run() {
                BaiReadApplication baiReadApplication = (BaiReadApplication)context.getApplicationContext();
                bookMarkList = baiReadApplication.getBookModel().loadBookMark(bookInfo.bookDetail.getId());
                for(BookMarkInfo info : bookMarkList){
                    info.text = BookChapterCache.getInstance().getMarkText(info.chapterIndex);
                    info.title = BookChapterCache.getInstance().getMarkTitle(info.chapterIndex);
                }
            }
        });
    }


    private void runMainUiThread(Runnable runnable){
        handler.post(runnable);
    }

    public static BookTextView.ReadSize getReadSize(Context context){
        int textSize = PreferenceSetting.getInstance(context).getIntValue(PreferenceSetting.KEY_TEXT_SIZE,50);
        int lineSize = PreferenceSetting.getInstance(context).getIntValue(PreferenceSetting.KEY_LINE_SIZE,45);
        int marginSize = PreferenceSetting.getInstance(context).getIntValue(PreferenceSetting.KEY_MARGIN_SIZE,0);
        return new BookTextView.ReadSize(textSize,lineSize,marginSize);
    }

    public void setReadSize(BookTextView.ReadSize readSize){
        PreferenceSetting.getInstance(context).putIntValue(PreferenceSetting.KEY_TEXT_SIZE,readSize.textSize);
        PreferenceSetting.getInstance(context).putIntValue(PreferenceSetting.KEY_LINE_SIZE,readSize.lineSize);
        PreferenceSetting.getInstance(context).putIntValue(PreferenceSetting.KEY_MARGIN_SIZE,readSize.marginSize);
        bookReadPresenterListener.onReadSizeChange(readSize);
    }

     /**
     * 正序返回true
     * @return
     * */
    public boolean getChapterOrder(){
        int order = PreferenceSetting.getInstance(context).getIntValue(PreferenceSetting.KEY_CHAPTER_ORDER,0);
        return order == 0;
    }

    public void setChapterOrder(boolean order){
        int o = order ? 0 : 1;
        PreferenceSetting.getInstance(context).putIntValue(PreferenceSetting.KEY_CHAPTER_ORDER,o);
    }


    public void ChapterNext(){
        Log.e("sunday","ChapterNext--begin");
        BookChapterCache.getInstance().nextChapter(context);
        Log.e("sunday","ChapterNext--middeb");
        updateDataBookIndex();
        Log.e("sunday","ChapterNext--End");
    }

    public void ChapterPrev(){
        BookChapterCache.getInstance().prevChapter(context);
        updateDataBookIndex();
    }

    public void updateDataBookIndex(){
        BaiReadApplication baiReadApplication = (BaiReadApplication)context.getApplicationContext();
        baiReadApplication.getBookModel().updateBookChapter(bookInfo.bookChapter);
    }

    public String getBookName(){
        //String name = bookInfo.bookDetail.getName();
        return bookInfo.bookDetail.getName();
    }

    public ArrayList<BookChapter.Chapter> getChapterList(){
        return bookInfo.bookChapter.getChapterList();
    }

    /**
     * 如果存在本地html文件就表示已经下载缓存了
     * @param chapterIndex 章节序号
     * @return
     * */
    public boolean isChapterCache(int chapterIndex){
        return false;
    }

    /**
     * 指定章节重新缓存
     * @param chapterIndex 章节序号
     * */
    public void setChapterIndex(int chapterIndex){
        bookReadPresenterListener.onLoadStart();
        BookChapterCache.getInstance().setChapter(chapterIndex);
        updateDataBookIndex();
    }

    public ArrayList<BookMarkInfo> getBookMarkList(){
        return bookMarkList;
    }


    public void addBookMark(){
        BaiReadApplication baiReadApplication = (BaiReadApplication)context.getApplicationContext();
        BookMarkInfo bookMarkInfo = new BookMarkInfo();
        bookMarkInfo.setNameId(bookInfo.bookDetail.getId());
        int chapterIndex = bookInfo.bookChapter.getChapterIndex();
        bookMarkInfo.chapterIndex = chapterIndex;
        bookMarkInfo.text = BookChapterCache.getInstance().getMarkText(chapterIndex);
        bookMarkInfo.title = BookChapterCache.getInstance().getMarkTitle(chapterIndex);
        bookMarkList.add(bookMarkInfo);
        baiReadApplication.getBookModel().addBookMark(bookMarkInfo);
    }

    public void deleteBookMark(){
        BaiReadApplication baiReadApplication = (BaiReadApplication)context.getApplicationContext();
        baiReadApplication.getBookModel().deleteBookAllMark(bookInfo.bookDetail.getId());
    }

    public void deleteBookMark(BookMarkInfo bookMarkInfo){
        BaiReadApplication baiReadApplication = (BaiReadApplication)context.getApplicationContext();
        baiReadApplication.getBookModel().deleteBookMark(bookMarkInfo);
    }

}
