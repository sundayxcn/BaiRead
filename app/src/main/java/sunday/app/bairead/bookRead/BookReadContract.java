package sunday.app.bairead.bookRead;

import android.widget.ListView;

import sunday.app.bairead.base.BasePresenter;
import sunday.app.bairead.base.BaseView;
import sunday.app.bairead.bookRead.cache.BookReadSize;
import sunday.app.bairead.bookRead.cache.BookReadText;
import sunday.app.bairead.data.setting.BookChapter;
import sunday.app.bairead.data.setting.BookInfo;
import sunday.app.bairead.data.setting.BookMarkInfo;

/**
 * Created by zhongfei.sun on 2017/4/19.
 */

public interface BookReadContract {

    String READ_EXTRA_ID = "bookId";

    interface ViewRead extends BaseView<ReadPresenter> {

        void showChapter(BookReadText bookReadText);

        void textSizeChange(BookReadSize bookReadSize);

        void setPage(int page);
    }

    interface ReadPresenter extends BasePresenter {

        void setChapterIndex(int index);

        void chapterNext();

        void chapterPrev();

        void updateTextSize();

        void updateBookChapterIndex(int index);

        void updateBookChapterPage(int page);

        void stop();

        void showSetting();

    }


    interface ViewSetting extends BaseView<SettingPresenter> {
        void setReadSize(BookReadSize bookReadSize);
    }

    interface SettingPresenter extends BasePresenter {
        void addBookMark();

        void loadBookMark(ListView listView);

        void deleteBookMark(BookMarkInfo bookMarkInfo);

        void clearBookMark();

        void addBook();

        void downBook();

        void showSetting();

        void hideSetting();

        void stop();

        void updateReadSize(BookReadSize bookReadSize);

    }

    interface ChapterPresenter extends BasePresenter{
        void changeOrder();
        void setChapterIndex(int index);
    }

    interface ChapterView extends BaseView<ChapterPresenter>{
        void showOrder(boolean defaultOrder);
        void showChapter(BookInfo bookInfo);
    }


    interface ReadSetting{
        BookReadSize getReadSize();
        void setReadSize(BookReadSize bookReadSize);

        boolean isDefaultChapterOrder();

        void changeChapterOrder(boolean defaultOrder);
    }
}
