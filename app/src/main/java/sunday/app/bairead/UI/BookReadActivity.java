package sunday.app.bairead.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import sunday.app.bairead.DataBase.BaiReadApplication;
import sunday.app.bairead.DataBase.BookChapter;
import sunday.app.bairead.DataBase.BookInfo;
import sunday.app.bairead.DataBase.BookModel;
import sunday.app.bairead.Download.BookChapterCache;
import sunday.app.bairead.R;
import sunday.app.bairead.View.BookTextView;

/**
 * Created by sunday on 2016/12/21.
 */

public class BookReadActivity extends Activity implements BookChapterCache.ChapterListener {

    public static final int HANDLE_MESSAGE_CHAPTER_NEXT = 100;
    public static final int HANDLE_MESSAGE_CHAPTER_PREV = 200;
    private TextView mBookTitleTView;
    private BookTextView mBookTextTview;
    private BookInfo bookInfo;
    private BookModel bookModel;
    private AlertDialog alertDialog;

    private BookChapterCache bookChapterCache = BookChapterCache.getInstance();

    public static final String EXTRAS_BOOK_ID = "BookId";

    public static final Point READ_POINT = new Point();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_read_activity);

        BaiReadApplication application = (BaiReadApplication) getApplication();
        bookModel = application.getBookModel();

        long bookId = getIntent().getExtras().getLong(EXTRAS_BOOK_ID,0);
        bookInfo = bookModel.getBookInfo(bookId);


        mBookTitleTView = (TextView) findViewById(R.id.book_read_activity_book_title);

        mBookTextTview = (BookTextView) findViewById(R.id.book_read_activity_book_text);
        mBookTextTview.setReadHandler(new ReadHandler());

        getWindowManager().getDefaultDisplay().getSize(READ_POINT);

        bookChapterCache.setBookInfo(bookInfo,this);
        bookChapterCache.initChapterRead();

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
