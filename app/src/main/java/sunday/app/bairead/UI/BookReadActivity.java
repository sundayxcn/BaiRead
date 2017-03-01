package sunday.app.bairead.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import sunday.app.bairead.DataBase.BaiReadApplication;
import sunday.app.bairead.DataBase.BookChapter;
import sunday.app.bairead.DataBase.BookInfo;
import sunday.app.bairead.DataBase.BookModel;
import sunday.app.bairead.Download.BookChapterCache;
import sunday.app.bairead.R;
import sunday.app.bairead.Tool.PreferenceSetting;
import sunday.app.bairead.View.BookReadSettingPanelView;
import sunday.app.bairead.View.BookTextView;

/**
 * Created by sunday on 2016/12/21.
 */

public class BookReadActivity extends Activity implements BookChapterCache.ChapterListener{

    public static final int HANDLE_MESSAGE_CHAPTER_NEXT = 100;
    public static final int HANDLE_MESSAGE_CHAPTER_PREV = 200;
    public static final int HANDLE_MESSAGE_CHAPTER_INDEX = 300;
    public static final String EXTRAS_BOOK_ID = "BookId";
    public static final Point READ_POINT = new Point();

    private TextView mBookTitleTView;
    private BookTextView mBookTextTview;
    private BookInfo bookInfo;
    private BookModel bookModel;
    private AlertDialog alertDialog;

    private BookChapterCache bookChapterCache = BookChapterCache.getInstance();
    private BookReadSettingPanelView settingPanel;


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
        int textSize = PreferenceSetting.getInstance(this).getIntValue(PreferenceSetting.KEY_TEXT_SIZE,50);
        int lineSize = PreferenceSetting.getInstance(this).getIntValue(PreferenceSetting.KEY_LINE_SIZE,45);
        mBookTextTview.setTextSize(textSize,lineSize);
        mBookTextTview.setReadHandler(new ReadHandler());

        getWindowManager().getDefaultDisplay().getSize(READ_POINT);

        bookChapterCache.setBookInfo(bookInfo, this);
        bookChapterCache.initChapterRead();

        settingPanel = (BookReadSettingPanelView) findViewById(R.id.book_read_setting_panel);
        settingPanel.setOnChangeListener(bookId, new BookReadSettingPanelView.OnChangeListener() {
            @Override
            public void chapterChange(int chapterIndex) {
                bookChapterCache.setChapter(chapterIndex);
            }

            @Override
            public void textSizeChange(int textSize, int lineSize) {
                mBookTextTview.setTextSize(textSize,lineSize);
                mBookTextTview.postInvalidate();
            }
        });
        settingPanel.setVisibility(View.INVISIBLE);
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

    public class ReadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            switch (msg.what) {
                case HANDLE_MESSAGE_CHAPTER_NEXT:
                    boolean isLast = bookChapterCache.nextChapter(BookReadActivity.this);
                    mBookTextTview.setLast(isLast);
                    break;
                case HANDLE_MESSAGE_CHAPTER_PREV:
                    boolean isBegin = bookChapterCache.prevChapter(BookReadActivity.this);
                    mBookTextTview.setBegin(isBegin);
                    break;

                default:

            }
        }
    }
}
