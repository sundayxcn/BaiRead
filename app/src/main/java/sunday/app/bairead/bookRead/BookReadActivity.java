package sunday.app.bairead.bookRead;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;

import sunday.app.bairead.R;
import sunday.app.bairead.base.BaseActivity;
import sunday.app.bairead.bookRead.cache.BookReadSize;
import sunday.app.bairead.bookRead.cache.BookReadText;
import sunday.app.bairead.bookRead.cache.BookSimpleCache;
import sunday.app.bairead.bookRead.cache.IBookChapterCache;
import sunday.app.bairead.bookRead.view.BookReadSettingPanelView;
import sunday.app.bairead.bookRead.view.BookReadView;
import sunday.app.bairead.data.BookRepository;
import sunday.app.bairead.data.setting.BookInfo;
import sunday.app.bairead.download.BookDownService;
import sunday.app.bairead.parse.ParseBookChapter;
import sunday.app.bairead.parse.ParseChapterText;
import sunday.app.bairead.utils.ActivityUtils;
import sunday.app.bairead.utils.FileManager;
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
    private IBookChapterCache mBookChapterCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_read_activity);
        mBookReadView = (BookReadView) findViewById(R.id.book_read_parent);

        Intent intent = getIntent();
        long bookId = intent.getLongExtra(BookReadContract.READ_EXTRA_ID, 0);
        BookInfo bookInfo = BookRepository.getInstance(getApplicationContext()).getBook(bookId);
        mPreferenceSetting = PreferenceSetting.getInstance(getApplicationContext());
        mBookChapterCache = new BookSimpleCache(new BookDownService(FileManager.getInstance()),
                new ParseBookChapter(),
                new ParseChapterText());
        mBookReadPresenter = new BookReadPresenter(BookRepository.getInstance(getApplicationContext()),
                mBookChapterCache,
                mPreferenceSetting,
                bookInfo,
                this
        );
        mBookReadView.setPresenter(mBookReadPresenter);
        mBookReadPresenter.start();


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBookChapterCache.stop();
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
