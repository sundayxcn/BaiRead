package sunday.app.bairead.UI;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Spanned;

import sunday.app.bairead.DataBase.BaiReadApplication;
import sunday.app.bairead.DataBase.BookInfo;
import sunday.app.bairead.DataBase.BookModel;
import sunday.app.bairead.Download.BookCacheManager;
import sunday.app.bairead.R;
import sunday.app.bairead.View.BookTextView;

/**
 * Created by sunday on 2016/12/21.
 */

public class BookReadActivity extends Activity implements BookCacheManager.ChapterListener{

    private BookTextView mBookTextTview;

    private BookInfo bookInfo;

    public static Point FULL_SCREEN_POINT = new Point();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_read_fragment);
        getWindowManager().getDefaultDisplay().getSize(FULL_SCREEN_POINT);
        BaiReadApplication application = (BaiReadApplication) getApplication();
        BookModel bookModel = application.getBookModel();

        long bookId = getIntent().getExtras().getInt("BookId");
        bookInfo = bookModel.getBookInfo(bookId);
        mBookTextTview = (BookTextView) findViewById(R.id.book_read_fragment_book_text);
        BookCacheManager.getInstance().getChapterText(bookInfo,this);
    }

    @Override
    public void end(Spanned text) {
        mBookTextTview.setChapterText(text);
    }
}
