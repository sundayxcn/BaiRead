package sunday.app.bairead.bookRead;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;

import sunday.app.bairead.R;
import sunday.app.bairead.base.BaseActivity;
import sunday.app.bairead.bookRead.cache.BookChapterCacheNew;
import sunday.app.bairead.bookRead.cache.BookReadSize;
import sunday.app.bairead.bookRead.cache.BookReadText;
import sunday.app.bairead.bookRead.view.BookReadSettingPanelView;
import sunday.app.bairead.bookRead.view.BookReadView;
import sunday.app.bairead.data.BookRepository;
import sunday.app.bairead.data.setting.BookInfo;
import sunday.app.bairead.utils.ActivityUtils;
import sunday.app.bairead.utils.PreferenceSetting;

/**
 * Created by sunday on 2016/12/21.
 */

public class BookReadActivity extends BaseActivity implements BookReadContract.View {

    private BookReadPresenter mBookReadPresenter;
    private BookReadContract.ViewRead mBookReadView;
    private BookReadContract.ViewSetting mBookReadSetting;
    private PreferenceSetting mPreferenceSetting;
    private BookReadSize bookReadSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_read_activity);
        mBookReadView = (BookReadView) findViewById(R.id.book_read_parent);

        Intent intent = getIntent();
        long bookId = intent.getLongExtra(BookReadContract.READ_EXTRA_ID, 0);
        BookInfo bookInfo = BookRepository.getInstance(getApplicationContext()).getBook(bookId);
        mPreferenceSetting = PreferenceSetting.getInstance(getApplicationContext());
        mBookReadPresenter = new BookReadPresenter(BookRepository.getInstance(getApplicationContext()),
                BookChapterCacheNew.getInstance(),
                mPreferenceSetting,
                bookInfo,
                this
        );

        mBookReadView.setPresenter(mBookReadPresenter);
        BookChapterCacheNew.getInstance().setBookChapterCacheListener(mBookReadPresenter);

        mBookReadPresenter.start();


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //bookReadPresenter.updateDataBookPage();
        BookChapterCacheNew.getInstance().stop();
    }

    @Override
    public void showChapter(BookReadText bookReadText) {
        mBookReadView.showChapter(bookReadText);
    }

    @Override
    public void textSizeChange(BookReadSize bookReadSize) {
        this.bookReadSize = bookReadSize;
        mBookReadView.textSizeChange(bookReadSize);
    }

    @Override
    public void showSetting() {
        if(mBookReadSetting == null) {
            mBookReadSetting = (BookReadSettingPanelView) LayoutInflater.from(this).inflate(R.layout.book_read_setting_panel, null, false);
            mBookReadSetting.setPresenter(mBookReadPresenter);
            mBookReadSetting.initTextSize(bookReadSize);
        }

        mBookReadView.showSetting(mBookReadSetting);
    }

    @Override
    public void hideSetting() {
        mBookReadView.hideSetting(mBookReadSetting);
    }

    @Override
    public void showChapterMenu() {
        BookReadChapterFragment bookReadChapterFragment = new BookReadChapterFragment();
        bookReadChapterFragment.setPresenter(mBookReadPresenter);
        bookReadChapterFragment.setPreferenceSetting(PreferenceSetting.getInstance(getApplicationContext()));
        ActivityUtils.addFragmentToActivity(getFragmentManager(), bookReadChapterFragment,
                R.id.book_read_parent);
    }

    @Override
    public void showMarkMenu() {
        BookReadMarkFragment bookReadMarkFragment = new BookReadMarkFragment();
        bookReadMarkFragment.setPresenter(mBookReadPresenter);
        ActivityUtils.addFragmentToActivity(getFragmentManager(),
                bookReadMarkFragment,
                R.id.book_read_parent);
    }

    @Override
    public void setPresenter(BookReadContract.Presenter presenter) {

    }

    @Override
    public void showLoading() {
        showProgressDialog();
    }

    @Override
    public void hideLoading() {
        hideProgressDialog();
    }
}
