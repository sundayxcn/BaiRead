package sunday.app.bairead.activity;

import android.graphics.Point;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import sunday.app.bairead.R;
import sunday.app.bairead.download.BookChapterCache;
import sunday.app.bairead.view.BookReadSettingPanelView;
import sunday.app.bairead.view.BookTextView;
import sunday.app.bairead.presenter.BookReadPresenter;

/**
 * Created by sunday on 2016/12/21.
 */

public class BookReadActivity extends BaseActivity implements BookReadPresenter.IBookReadPresenterListener , BookTextView.IChapterChangeListener{

    public static final String EXTRAS_BOOK_ID = "BookId";
    public static final Point READ_POINT = new Point();

    private TextView mBookTitleTView;
    private BookTextView mBookTextTView;
    private BookReadSettingPanelView settingPanel;

    private BookReadPresenter bookReadPresenter;

    @Override
    public void onLoadStart() {
        showProgressDialog();
    }

    @Override
    public void onLoadFinish() {
        hideProgressDialog();
    }

    @Override
    public void onLoadError() {
        hideProgressDialog();
        showToast("网络未连接，没有下载章节，请检查网络后重试");

    }

    @Override
    public void onReadTextChange(BookChapterCache.ReadText readText) {
        hideProgressDialog();
        mBookTitleTView.setText(readText.title);
        mBookTextTView.setText(readText.text);
    }

    @Override
    public void onReadSizeChange(BookTextView.ReadSize readSize) {
        mBookTextTView.setReadSize(readSize);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_read_activity);

        long bookId = getIntent().getExtras().getLong(EXTRAS_BOOK_ID, 0);

        mBookTitleTView = (TextView) findViewById(R.id.book_read_activity_book_title);
        mBookTextTView = (BookTextView) findViewById(R.id.book_read_activity_book_text);
        mBookTextTView.setReadSize(BookReadPresenter.getReadSize(this));
        mBookTextTView.setOnChangeListener(this);
        bookReadPresenter = new BookReadPresenter(this,this,bookId);

        getWindowManager().getDefaultDisplay().getSize(READ_POINT);

        settingPanel = (BookReadSettingPanelView) findViewById(R.id.book_read_setting_panel);
        settingPanel.setVisibility(View.INVISIBLE);
        settingPanel.setReadPresenter(bookReadPresenter);
    }


    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        bookReadPresenter.init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
                float x = event.getX();
                float y = event.getY();
                if (y > (BookReadActivity.READ_POINT.y / 3 * 2)) {
                    mBookTextTView.readNext(true);
                } else if (y < BookReadActivity.READ_POINT.y / 3) {
                    mBookTextTView.readNext(false);
                } else {
                    if(settingPanel.isShow()){
                        settingPanel.hide();
                    }else{
                        settingPanel.show();
                    }
                }
                break;
            default:
                break;

        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (settingPanel.isShow()) {
            settingPanel.hide();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onChapterNext() {
        bookReadPresenter.ChapterNext();
    }

    @Override
    public void onChapterPrev() {
        bookReadPresenter.ChapterPrev();
    }
}
