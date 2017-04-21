package sunday.app.bairead.bookRead;

import android.widget.ListView;

import java.util.List;

import rx.Observable;
import sunday.app.bairead.base.BaseCache;
import sunday.app.bairead.base.BasePresenter;
import sunday.app.bairead.base.BaseView;
import sunday.app.bairead.bookRead.cache.BookChapterCacheNew;
import sunday.app.bairead.bookRead.cache.BookReadSize;
import sunday.app.bairead.bookRead.cache.BookReadText;
import sunday.app.bairead.data.setting.BookChapter;
import sunday.app.bairead.data.setting.BookInfo;
import sunday.app.bairead.data.setting.BookMarkInfo;

/**
 * Created by zhongfei.sun on 2017/4/19.
 */

public interface BookReadContract {
    String READ_ACTION = "sunday.app.bairead.readBook";
    String READ_EXTRA_ID = "bookId";

    interface ViewMenu extends BaseView<Presenter> {

        void setChapterIndex();

    }

    interface View extends BaseView<Presenter> {

        void showChapter(BookReadText bookReadText);

        void hideLoading();
    }



    interface Presenter extends BasePresenter {

        void setChapterIndex(int index);

        void chapterNext();

        void chapterPrev();

        void updateBookChapterIndex();

        void updateBookChapterPage();

        void addBookMark();

        void loadBookMark(ListView listView);

        void deleteBookMark(BookMarkInfo bookMarkInfo);

        void clearBookMark();

        void addBook();

        void downBook();

        BookInfo getBookInfo();

    }

    interface IBookChapterCacheListener{
        void updateStart();
        void updateFinish();
        void updateReadTextSuccess(BookReadText readText);
        void updateReadTextFailed(int errorCode);
    }

    interface ChapterCache extends BaseCache {

        void prevChapter(int index);

        void nextChapter(int index);

        void setBookChapterCacheListener(IBookChapterCacheListener bookChapterCacheListener);
    }
}
