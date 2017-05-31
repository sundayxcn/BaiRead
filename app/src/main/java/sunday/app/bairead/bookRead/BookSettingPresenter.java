package sunday.app.bairead.bookRead;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import sunday.app.bairead.R;
import sunday.app.bairead.bookRead.adapter.MarkAdapter;
import sunday.app.bairead.bookRead.cache.BookReadSize;
import sunday.app.bairead.bookRead.view.BookReadSettingPanelView;
import sunday.app.bairead.data.BookRepository;
import sunday.app.bairead.data.setting.BookInfo;
import sunday.app.bairead.data.setting.BookMarkInfo;
import sunday.app.bairead.utils.ActivityUtils;
import sunday.app.bairead.utils.PreferenceSetting;

/**
 * Created by zhongfei.sun on 2017/5/23.
 */

public class BookSettingPresenter implements BookReadContract.SettingPresenter {

    private ViewGroup mRootView;
    private BookInfo mBookInfo;
    private BookRepository mBookRepository;
    private BookReadContract.ViewSetting mSettingView;
    private BookReadContract.ReadSetting mReadSetting;

    public BookSettingPresenter(@NonNull BookRepository bookRepository,
                                BookInfo bookInfo,
                                ViewGroup viewGroup,
                                BookReadContract.ReadSetting readSetting
    ) {
        mBookRepository = bookRepository;
        mBookInfo = bookInfo;
        mRootView = viewGroup;
        mReadSetting = readSetting;
    }


    @Override
    public void start() {

    }

    @Override
    public void addBookMark() {
        if (!isBookcase()) {
            mSettingView.showToast(R.string.case_add_tips);
        } else {
            mBookRepository.addBookMark(createBookMarkInfo(mBookInfo));
            mSettingView.showToast(R.string.mark_success);
        }
    }

    @Override
    public void loadBookMark(ListView listView) {
        mBookRepository.loadBookMarks().
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(bookMarkInfos -> listView.setAdapter(new MarkAdapter(listView.getContext(), bookMarkInfos)));
    }

    @Override
    public void deleteBookMark(BookMarkInfo bookMarkInfo) {
        mBookRepository.deleteBookMark(bookMarkInfo);
    }

    @Override
    public void clearBookMark() {

    }

    @Override
    public void addBook() {
        if (!isBookcase()) {
            mSettingView.showToast(R.string.case_add_tips);
        } else {
            mBookRepository.addBook(mBookInfo);
            mSettingView.showToast(R.string.case_add_success);
        }
    }

    @Override
    public void downBook() {
        if (!isBookcase()) {
            mSettingView.showToast(R.string.case_add_tips);
        } else {
            mSettingView.showToast(R.string.cache_book);
        }
    }

    @Override
    public void showSetting() {
        if (mSettingView == null) {
            mSettingView = (BookReadSettingPanelView) LayoutInflater.from(mRootView.getContext()).inflate(R.layout.book_read_setting_panel, null, false);
            mSettingView.setReadSize(mReadSetting.getReadSize());
            mSettingView.setPresenter(this);
        }
        if (mRootView.indexOfChild((View) mSettingView) > -1) {
            mRootView.removeView((View) mSettingView);
        } else {
            mRootView.addView((View) mSettingView);
        }
    }

    @Override
    public void hideSetting() {
        mRootView.removeView((View) mSettingView);
    }

    @Override
    public void stop() {

    }


    @Override
    public void updateReadSize(BookReadSize bookReadSize) {
        mReadSetting.setReadSize(bookReadSize);
        mRootView.getContext().sendBroadcast(new Intent(BookReadActivity.ACTION_READ_SIZE));
    }


    private boolean isBookcase() {
        long bookId = mBookInfo.bookDetail.getId();
        BookInfo bookInfo = mBookRepository.getBook(bookId);
        return bookInfo != null;
    }

    private BookMarkInfo createBookMarkInfo(BookInfo bookInfo) {
        BookMarkInfo bookMarkInfo = new BookMarkInfo();
        bookMarkInfo.setNameId(bookInfo.bookDetail.getId());
        int chapterIndex = bookInfo.bookChapter.getChapterIndex();
        bookMarkInfo.chapterIndex = chapterIndex;
        //bookMarkInfo.text = BookChapterCacheNew.getInstance().getMarkText(chapterIndex);
        //bookMarkInfo.title = BookChapterCacheNew.getInstance().getMarkTitle(chapterIndex);
        return bookMarkInfo;
    }
}
