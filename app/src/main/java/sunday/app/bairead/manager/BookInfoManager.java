package sunday.app.bairead.manager;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.IOException;

import rx.Observable;
import sunday.app.bairead.bookRead.cache.BookSimpleCache;
import sunday.app.bairead.bookcase.BookcaseContract;
import sunday.app.bairead.data.setting.BookChapter;
import sunday.app.bairead.data.setting.BookDetail;
import sunday.app.bairead.data.setting.BookInfo;
import sunday.app.bairead.data.setting.Chapter;
import sunday.app.bairead.download.BookDownService;
import sunday.app.bairead.download.IBookDownload;
import sunday.app.bairead.parse.ParseBase;
import sunday.app.bairead.parse.ParseBookChapter;
import sunday.app.bairead.parse.ParseBookDetail;
import sunday.app.bairead.utils.FileManager;

/**
 * Created by zhongfei.sun on 2017/5/2.
 */

public class BookInfoManager implements BookcaseContract.IBookInfoManager {

    private static BookInfoManager INSTANCE = null;

    private ParseBase<BookDetail> mBookDetailParse;
    private ParseBase<BookChapter> mBookChapterParse;
    private IBookDownload mBookDownload;

    private BookInfoManager(@NonNull ParseBase<BookDetail> bookDetailParse,
                            @NonNull ParseBase<BookChapter> bookChapterParse,
                            @NonNull IBookDownload bookDownload) {
        mBookDetailParse = bookDetailParse;
        mBookChapterParse = bookChapterParse;
        mBookDownload = bookDownload;
    }


    public static final BookInfoManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BookInfoManager(
                    new ParseBookDetail(),
                    new ParseBookChapter(),
                    new BookDownService());
        }
        return INSTANCE;
    }

    /**
     * 返回一个新的bookinfo实例
     * present通过判断章节数目决定是否重新指向
     * **/
    @Override
    public Observable<BookInfo> updateNewChapter(BookInfo bookInfo) {
        return Observable.create(subscriber -> {
            String url = bookInfo.bookChapter.getChapterLink();
            String fileName = getFullChapterFileName(bookInfo.bookDetail.getName());
            try {
                mBookDownload.downloadHtml(fileName,url);
                BookInfo newBookInfo = new BookInfo();
                newBookInfo.bookDetail =  mBookDetailParse.from(fileName,ParseBase.GB2312).parse();
                newBookInfo.bookChapter = mBookChapterParse.from(fileName).parse();
                subscriber.onNext(newBookInfo);
            } catch (IOException e) {
                subscriber.onError(e);
            }catch (NullPointerException e){
                subscriber.onError(e);
            }
        });
    }

    @Override
    public Observable<BookChapter> loadChapter(BookInfo bookInfo) {
        return Observable.create(subscriber -> {
            try {
                String fileName = getFullChapterFileName(bookInfo.bookDetail.getName());
                BookChapter bookChapter = mBookChapterParse.from(fileName,ParseBase.GB2312).parse();
                bookInfo.bookChapter.setChapterList(bookChapter.getChapterList());
                subscriber.onNext(bookChapter);
            } catch (IOException e) {
                subscriber.onError(e);
            }
        });
    }

    public boolean isChapterExists(String bookName,Chapter chapter) {
        String fileName = FileManager.PATH +
                "/" +
                bookName +
                "/" +
                BookSimpleCache.DIR +
                "/" +
                chapter.getNum() +
                ".html";

        File file = new File(fileName);
        return file.exists();
    }

    public String getFullChapterFileName(String bookName){
        return FileManager.PATH + "/" + bookName + "/" + BookChapter.FileName;
    }
}
