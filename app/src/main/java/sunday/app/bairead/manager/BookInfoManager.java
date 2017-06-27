package sunday.app.bairead.manager;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileNotFoundException;
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
     **/
    @Override
    public Observable<BookInfo> updateNewChapter(final BookInfo bookInfo) {
        return Observable.create(subscriber -> {
            String url = bookInfo.bookChapter.getChapterLink();
            String fileName = getFullChapterFileName(bookInfo.bookDetail.getName());
            try {
                mBookDownload.downloadHtml(fileName, url);
                BookInfo newBookInfo = new BookInfo();
                newBookInfo.bookDetail = mBookDetailParse.from(fileName, ParseBase.GB2312).parse();
                newBookInfo.bookChapter = mBookChapterParse.from(fileName).parse();
                subscriber.onNext(newBookInfo);
            } catch (IOException e) {
                subscriber.onError(e);
            } catch (NullPointerException e) {
                subscriber.onError(e);
            }
        });
    }

    @Override
    public Observable<BookChapter> loadChapter(BookInfo bookInfo) {
        return Observable.create(subscriber -> {
            try {
                String bookName = bookInfo.bookDetail.getName();
                String fileName = getFullChapterFileName(bookName);
                if (!isDetailExists(bookName)) {
                    mBookDownload.downloadHtml(fileName, bookInfo.bookChapter.getChapterLink());
                }
                BookChapter bookChapter = mBookChapterParse.from(fileName, ParseBase.GB2312).parse();
                bookInfo.bookChapter.setChapterList(bookChapter.getChapterList());
                subscriber.onNext(bookChapter);
            } catch (IOException e) {
                subscriber.onError(e);
            }
        });
    }

    public boolean isDetailExists(String bookName) {
        String fileDir = createChapterFileDir(bookName);
        File file = new File(fileDir + "/" + BookChapter.FileName);
        return file.exists();
    }

    public boolean isChapterExists(String bookName, Chapter chapter) {
        String fileName = new StringBuffer().
                append(FileManager.PATH).
                append("/").
                append(bookName).
                append("/").
                append(BookSimpleCache.DIR).
                append("/").
                append(chapter.getNum()).
                append(".html").
                toString();

        File file = new File(fileName);
        return file.exists();
    }

    public String createChapterFileDir(String bookName) {
        return FileManager.createDir(FileManager.PATH + "/" + bookName);
    }

    public String getFullChapterFileName(String bookName) {
        return FileManager.PATH + "/" + bookName + "/" + BookChapter.FileName;
    }
}
