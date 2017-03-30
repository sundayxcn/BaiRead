package sunday.app.bairead.bookRead;

import android.graphics.Point;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;
import android.widget.LinearLayout;
import android.widget.TextView;

import sunday.app.bairead.R;
import sunday.app.bairead.activity.BaseActivity;
import sunday.app.bairead.utils.PreferenceSetting;
import sunday.app.bairead.view.BookTextView;

/**
 * Created by sunday on 2016/12/21.
 */

public class BookReadActivity extends BaseActivity implements BookReadPresenter.IBookReadPresenterListener,BookChapterCacheNew.IBookChapterCacheListener, BookTextView.IChapterChangeListener{

    public static final String EXTRAS_BOOK_ID = "BookId";
    public static final Point READ_POINT = new Point();

    private TextView mBookTitleTView;
    private TextView mBookTextPageTView;
    private BookTextView mBookTextTView;
    private BookReadSettingPanelView settingPanel;

    private BookReadPresenter bookReadPresenter;

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
        bookReadPresenter.init();
        BookChapterCacheNew.getInstance().init(this,bookId,this);

        if(PreferenceSetting.getInstance(this).isFirstRead()){
            ViewStub viewStub = (ViewStub) findViewById(R.id.book_read_activity_guide_layout_viewstub);
            viewStub.inflate();
            PreferenceSetting.getInstance(this).putBooleanValue(PreferenceSetting.KEY_FIRST_READ,false);
            final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.book_read_activity_guide);
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    linearLayout.setVisibility(View.GONE);
                }
            });
        }

        int page = bookReadPresenter.getChapterPage();
        mBookTextTView.initPage(page);

        mBookTextPageTView = (TextView) findViewById(R.id.book_read_activity_book_page);

        getWindowManager().getDefaultDisplay().getSize(READ_POINT);

        settingPanel = (BookReadSettingPanelView) findViewById(R.id.book_read_setting_panel);
        settingPanel.setVisibility(View.INVISIBLE);
        settingPanel.setReadPresenter(bookReadPresenter);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        bookReadPresenter.updateDataBookPage();
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
        if(!BookChapterCacheNew.getInstance().nextChapter()) {
            showToast(R.string.last_chapter);
        }else{
            bookReadPresenter.updateDataBookIndex();
        }
    }

    @Override
    public void onChapterPrev() {
        if(!BookChapterCacheNew.getInstance().prevChapter()) {
            showToast(R.string.first_chapter);
        }else{
            bookReadPresenter.updateDataBookIndex();
        }
    }

    @Override
    public void onPageChange(int page, int pageCount) {
        bookReadPresenter.setChapterPage(page);
        //page从0开始，所以+1来显示
        page++;
        String text = page + "/" + pageCount ;
        mBookTextPageTView.setText(text);
    }

    @Override
    public void updateStart() {
        showProgressDialog();
    }

    @Override
    public void updateFinish() {
        hideProgressDialog();
    }

    @Override
    public void updateReadTextSuccess(BookChapterCacheNew.ReadText readText) {
        mBookTitleTView.setText(readText.title);
        mBookTextTView.setText(readText.text);
    }

    @Override
    public void updateReadTextFailed(int errorCode) {
        //if(errorCode == BookDownLoad.ERROR_NetworkFailed) {
            showTipsDialog(R.string.network_connect_failed);
        //}
    }

    @Override
    public void onReadSizeChange(BookTextView.ReadSize readSize) {
        mBookTextTView.setReadSize(readSize);
    }
}
