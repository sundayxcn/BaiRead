package sunday.app.bairead.Download;

import android.text.Html;
import android.text.Spanned;

import sunday.app.bairead.DataBase.BookChapter;
import sunday.app.bairead.DataBase.BookInfo;
import sunday.app.bairead.Download.OKhttpManager;
import sunday.app.bairead.Parse.BookTextParse;
import sunday.app.bairead.Parse.JsoupParse;
import sunday.app.bairead.Tool.FileManager;

/**
 * Created by sunday on 2016/12/8.
 */

public class BookCacheManager extends OKhttpManager {
    public static final String DIR = "chapterCache";

    private static BookCacheManager bookCacheManager;

    public interface ChapterListener{
        void end(Spanned text);
    }

    private ChapterListener chapterListener;

    private BookCacheManager(){

    }

    public static BookCacheManager getInstance(){
        if(bookCacheManager == null){
            bookCacheManager = new BookCacheManager();
        }
        return bookCacheManager;
    }

    /**
     *
     * 下载所有章节
     * */
    public static void downloadAllApater(BookInfo bookInfo){

    }

    /**
     * 下载指定章节
     * */
    public void downloadChapter(BookInfo bookInfo){
        int index = bookInfo.bookChapter.getChapterIndex();
        BookChapter.ChapterText chapterText = bookInfo.bookChapter.getChapterText(index);
        String url = chapterText.getLink();
        String dir = FileManager.PATH + "/" + bookInfo.bookDetail.getName()+"/"+DIR;
        FileManager.createDir(dir);
        String fileName = dir+"/"+chapterText.getNum()+ ".html";
        connectUrl(url, fileName, new ConnectListener() {
            @Override
            public void start(String url) {

            }

            @Override
            public void end(String fileName) {
                if(chapterListener != null){
                    String text = JsoupParse.from(fileName,new BookTextParse());
                    chapterListener.end(Html.fromHtml(text));
                }
            }
        });
    }

    public void getChapterText(BookInfo bookInfo,ChapterListener chapterListener){
        this.chapterListener = chapterListener;
        downloadChapter(bookInfo);
    }

}
