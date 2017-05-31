package sunday.app.bairead.bookRead.view;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import sunday.app.bairead.R;
import sunday.app.bairead.bookRead.BookReadActivity;
import sunday.app.bairead.bookRead.BookReadContract;
import sunday.app.bairead.bookRead.cache.BookReadSize;
import sunday.app.bairead.bookRead.cache.BookReadText;
import sunday.app.bairead.utils.PreferenceSetting;

/**
 * Created by zhongfei.sun on 2017/4/19.
 */

public class BookReadView extends RelativeLayout implements BookReadContract.ViewRead, BookTextView.IChapterChangeListener{

    TextView mBookTitle;
    BookTextView mBookText;
    TextView mBookPage;

    private BookReadContract.ReadPresenter mPresenter;

    public BookReadView(Context context) {
        super(context);
    }

    public BookReadView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mBookTitle = (TextView) findViewById(R.id.book_read_activity_book_title);
        mBookText = (BookTextView) findViewById(R.id.book_read_activity_book_text);
        mBookText.setOnChangeListener(this);
        mBookPage = (TextView) findViewById(R.id.book_read_activity_book_page);
        //mSettingPanelView = (BookReadSettingPanelView) findViewById(R.id.book_read_setting_panel);
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
                    getContext().sendBroadcast(new Intent(BookReadActivity.ACTION_VIEW_SETTING));
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
        mPresenter.updateBookChapterPage(page);
        page++;
        String text = page + "/" + pageCount;
        mBookPage.setText(text);

    }


    @Override
    public void showChapter(BookReadText bookReadText) {
        mBookTitle.setText(bookReadText.title);
        mBookText.setText(bookReadText.text);
    }

    @Override
    public void textSizeChange(BookReadSize bookReadSize) {
        mBookText.setReadSize(bookReadSize);
    }


    @Override
    public void setPage(int page) {
        mBookText.initPage(page);
    }


    @Override
    public void setPresenter(BookReadContract.ReadPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showToast(@NonNull @StringRes int resId) {

    }
}
