package sunday.app.bairead.bookRead.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import sunday.app.bairead.R;
import sunday.app.bairead.bookRead.BookReadActivity;
import sunday.app.bairead.bookRead.BookReadContract;
import sunday.app.bairead.bookRead.cache.BookReadSize;
import sunday.app.bairead.bookRead.cache.BookReadText;
import sunday.app.bairead.utils.PreferenceSetting;

/**
 * Created by zhongfei.sun on 2017/4/19.
 */

public class BookReadView extends RelativeLayout implements BookReadContract.View, BookTextView.IChapterChangeListener, BookReadSettingPanelView.IReadSizeListener {

    TextView mBookTitle;
    BookTextView mBookText;
    TextView mBookPage;
    BookReadSettingPanelView mSettingPanelView;

    private BookReadContract.Presenter mPresenter;

    private PreferenceSetting mPreferenceSetting;

    public BookReadView(Context context) {
        super(context);
    }

    public BookReadView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void setPreferenceSetting(@NonNull PreferenceSetting preferenceSetting) {
        mPreferenceSetting = preferenceSetting;
        BookReadSize bookReadSize = getBookReadSize();
        mBookText.setReadSize(bookReadSize);
        mSettingPanelView.setBookReadSize(bookReadSize);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mBookTitle = (TextView) findViewById(R.id.book_read_activity_book_title);
        mBookText = (BookTextView) findViewById(R.id.book_read_activity_book_text);
        mBookPage = (TextView) findViewById(R.id.book_read_activity_book_page);
        mSettingPanelView = (BookReadSettingPanelView) findViewById(R.id.book_read_setting_panel);
    }

    @Override
    public void showChapter(BookReadText bookReadText) {
        mBookTitle.setText(bookReadText.title);
        mBookText.setText(bookReadText.text);
    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void setPresenter(BookReadContract.Presenter presenter) {
        mPresenter = presenter;
        mSettingPanelView.setPresenter(presenter);
        int page = mPresenter.getBookInfo().bookChapter.getChapterPage();
        mBookText.initPage(page);
        mBookText.setOnChangeListener(this);
        mSettingPanelView.setOnReadSizeChange(this);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void showToast(@StringRes @NonNull int resId) {
        Toast.makeText(getContext(), resId, Toast.LENGTH_SHORT).show();
    }


    private int displayHeight = getResources().getDisplayMetrics().heightPixels;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
                float x = event.getX();
                float y = event.getY();
                if (y > (displayHeight / 3 * 2)) {
                    mBookText.readNext(true);
                } else if (y < displayHeight / 3) {
                    mBookText.readNext(false);
                } else {
                    if (mSettingPanelView.isShow()) {
                        mSettingPanelView.hide();
                    } else {
                        mSettingPanelView.show();
                    }
                }
                break;
            default:
                break;

        }
        return true;
    }


    @Override
    public void onChapterNext() {
        mPresenter.chapterNext();
    }

    @Override
    public void onChapterPrev() {
        mPresenter.chapterPrev();
    }

    @Override
    public void onPageChange(int page, int pageCount) {
        page++;
        String text = page + "/" + pageCount;
        mBookPage.setText(text);
        mPresenter.updateBookChapterPage();
    }

    @Override
    public void onReadSize(BookReadSize bookReadSize) {
        updateTextSize(bookReadSize);
        mBookText.setReadSize(bookReadSize);
    }

    public void updateTextSize(BookReadSize bookReadSize) {
        mPreferenceSetting.putIntValue(PreferenceSetting.KEY_TEXT_SIZE, bookReadSize.textSize);
        mPreferenceSetting.putIntValue(PreferenceSetting.KEY_LINE_SIZE, bookReadSize.lineSize);
        mPreferenceSetting.putIntValue(PreferenceSetting.KEY_MARGIN_SIZE, bookReadSize.marginSize);
    }

    public BookReadSize getBookReadSize() {
        int textSize = mPreferenceSetting.getIntValue(PreferenceSetting.KEY_TEXT_SIZE, 50);
        int lineSize = mPreferenceSetting.getIntValue(PreferenceSetting.KEY_LINE_SIZE, 45);
        int marginSize = mPreferenceSetting.getIntValue(PreferenceSetting.KEY_MARGIN_SIZE, 0);
        return new BookReadSize(textSize, lineSize, marginSize);
    }


    public boolean onBackPressed() {
        if (mSettingPanelView.isShow()) {
            mSettingPanelView.hide();
            return true;
        } else {
            return false;
        }
    }
}
