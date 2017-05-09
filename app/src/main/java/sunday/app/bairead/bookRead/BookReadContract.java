package sunday.app.bairead.bookRead;

import android.widget.ListView;

import sunday.app.bairead.base.BasePresenter;
import sunday.app.bairead.base.BaseView;
import sunday.app.bairead.bookRead.cache.BookReadSize;
import sunday.app.bairead.bookRead.cache.BookReadText;
import sunday.app.bairead.data.setting.BookInfo;
import sunday.app.bairead.data.setting.BookMarkInfo;

/**
 * Created by zhongfei.sun on 2017/4/19.
 */

public interface BookReadContract {

    String READ_EXTRA_ID = "bookId";

    interface ViewSub extends BaseView<Presenter> {
    }

    interface ViewRead extends ViewSub {

        void showChapter(BookReadText bookReadText);

        void textSizeChange(BookReadSize bookReadSize);

        void showSetting(ViewSetting view);

        void hideSetting(ViewSetting view);

    }

    interface ViewSetting extends ViewSub {
        void initTextSize(BookReadSize bookReadSize);
    }

    interface ViewMenu extends ViewSub {

    }

    interface View extends BaseView<Presenter> {
        void showChapter(BookReadText bookReadText);

        void textSizeChange(BookReadSize bookReadSize);

        void showSetting();

        void hideSetting();

        void showChapterMenu();

        void showMarkMenu();

    }


    interface Presenter extends BasePresenter {

        void setChapterIndex(int index);

        void chapterNext();

        void chapterPrev();

        void addBookMark();

        void loadBookMark(ListView listView);

        void deleteBookMark(BookMarkInfo bookMarkInfo);

        void clearBookMark();

        void addBook();

        void downBook();

        void updateTextSize(BookReadSize bookReadSize);

        void updateBookChapterIndex(int index);

        void updateBookChapterPage(int page);

        void goToChapterMenu();

        void goToMarkMenu();

        void showSetting();

        BookInfo getBookInfo();

        void stop();
    }
}
