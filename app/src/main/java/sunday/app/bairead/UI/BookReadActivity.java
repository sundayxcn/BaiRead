package sunday.app.bairead.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import sunday.app.bairead.DataBase.BaiReadApplication;
import sunday.app.bairead.DataBase.BookChapter;
import sunday.app.bairead.DataBase.BookInfo;
import sunday.app.bairead.DataBase.BookModel;
import sunday.app.bairead.Download.BookChapterCache;
import sunday.app.bairead.R;
import sunday.app.bairead.View.BookTextView;
import sunday.app.bairead.View.ChapterListView;

/**
 * Created by sunday on 2016/12/21.
 */

public class BookReadActivity extends Activity implements BookChapterCache.ChapterListener, View.OnClickListener {

    public static final int HANDLE_MESSAGE_CHAPTER_NEXT = 100;
    public static final int HANDLE_MESSAGE_CHAPTER_PREV = 200;
    public static final int HANDLE_MESSAGE_CHAPTER_INDEX = 300;
    public static final String EXTRAS_BOOK_ID = "BookId";
    public static final Point READ_POINT = new Point();
    Handler handler = new Handler();
    private TextView mBookTitleTView;
    private BookTextView mBookTextTview;
    private BookInfo bookInfo;
    private BookModel bookModel;
    private AlertDialog alertDialog;


    //private

    //public Handler handler;
    private BookChapterCache bookChapterCache = BookChapterCache.getInstance();
    private RelativeLayout settingPanel;
    private ChapterListView chapterListView;
    private Runnable showSettingRunnable = new Runnable() {
        @Override
        public void run() {
            hideSetting();
        }
    };
    private AdapterView.OnItemClickListener chapterOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            bookChapterCache.setChapter(position);
            hideChapterList();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_read_activity);

        BaiReadApplication application = (BaiReadApplication) getApplication();
        bookModel = application.getBookModel();

        long bookId = getIntent().getExtras().getLong(EXTRAS_BOOK_ID, 0);
        bookInfo = bookModel.getBookInfo(bookId);


        mBookTitleTView = (TextView) findViewById(R.id.book_read_activity_book_title);

        mBookTextTview = (BookTextView) findViewById(R.id.book_read_activity_book_text);
        mBookTextTview.setReadHandler(new ReadHandler());

        getWindowManager().getDefaultDisplay().getSize(READ_POINT);

        bookChapterCache.setBookInfo(bookInfo, this);
        bookChapterCache.initChapterRead();


        setupPanelView();

    }

    private void setupPanelView() {
        settingPanel = (RelativeLayout) findViewById(R.id.book_read_setting_panel);
        settingPanel.setVisibility(View.INVISIBLE);
        RelativeLayout settingTopPanel = (RelativeLayout) findViewById(R.id.book_read_setting_panel_top_panel);
        LinearLayout settingBottomPanel = (LinearLayout) findViewById(R.id.book_read_setting_panel_bottom_panel);
        setOnClick(settingTopPanel);
        setOnClick(settingBottomPanel);
    }

    private void setOnClick(ViewGroup viewGroup) {
        int count = viewGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            View v = viewGroup.getChildAt(i);
            v.setOnClickListener(this);
        }
    }

    @Override
    public void end(BookChapter.Chapter chapter) {
        bookModel.updateBookChapter(bookInfo.bookChapter);
        final String text = chapter.getText();
        final String title = chapter.getTitle();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //alertDialog.hide();
                mBookTextTview.setChapterText(text);
                mBookTitleTView.setText(title);
            }
        });

    }

    //private void hide

    private void showChapterList() {
        chapterListView = new ChapterListView(this);
        chapterListView.setBookInfo(bookInfo);
        chapterListView.setOnItemClickListener(chapterOnItemClickListener);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(0, 0, 200, 0);
        settingPanel.addView(chapterListView, layoutParams);
    }

    private void hideChapterList() {
        settingPanel.removeView(chapterListView);
        chapterListView = null;
    }

    @Override
    public void onClick(View v) {
        showSettingShort();
        switch (v.getId()) {
            case R.id.book_read_setting_panel_chapter_menu:
                showChapterList();
                showSettingLong();
                break;
            case R.id.book_read_setting_panel_book_mark:

                //break;
            case R.id.book_read_setting_panel_text_font:
                //break;
            case R.id.book_read_setting_panel_more:
                //break;
            case R.id.book_read_setting_panel_source:
                //break;
                Toast.makeText(BookReadActivity.this, "等待开发", Toast.LENGTH_SHORT).show();
            default:
                break;
        }
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
                    mBookTextTview.readNext(true);
                } else if (y < BookReadActivity.READ_POINT.y / 3) {
                    mBookTextTview.readNext(false);
                } else {
                    if (settingPanel.getVisibility() == View.VISIBLE) {
                        hideSetting();
                    } else {
                        showSettingShort();
                    }
                }
                break;
            default:
                break;

        }
        return true;
    }

    private void showSettingShort() {
        settingPanel.setVisibility(View.VISIBLE);
        handler.removeCallbacks(showSettingRunnable);
        handler.postDelayed(showSettingRunnable, 2000);
    }

    private void showSettingLong() {
        settingPanel.setVisibility(View.VISIBLE);
        handler.removeCallbacks(showSettingRunnable);
    }

    private void hideSetting() {
        settingPanel.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onBackPressed() {
        if (chapterListView != null) {
            hideChapterList();
            showSettingShort();
        } else {
            super.onBackPressed();
        }
    }

    public class ReadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            switch (msg.what) {
                case HANDLE_MESSAGE_CHAPTER_NEXT:
                    bookChapterCache.nextChapter(BookReadActivity.this);
                    break;
                case HANDLE_MESSAGE_CHAPTER_PREV:
                    bookChapterCache.prevChapter(BookReadActivity.this);
                    break;

                default:

            }
        }
    }
}
