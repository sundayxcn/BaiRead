package sunday.app.bairead.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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


    public static final int HANDLE_MESSAGE_CHAPTER_NEXT = 100;
    public static final int HANDLE_MESSAGE_CHAPTER_PREV = 200;

    private BookModel bookModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_read_fragment);
        getWindowManager().getDefaultDisplay().getSize(FULL_SCREEN_POINT);
        BaiReadApplication application = (BaiReadApplication) getApplication();
        bookModel = application.getBookModel();

        long bookId = getIntent().getExtras().getInt("BookId");
        bookInfo= bookModel.getBookInfo(bookId);
        mBookTextTview = (BookTextView) findViewById(R.id.book_read_fragment_book_text);

        mBookTextTview.setReadHandler(new ReadHandler());
        getChapterText();
        }

@Override
public void end(final Spanned text) {
        runOnUiThread(new Runnable() {
@Override
public void run() {
        alertDialog.hide();
        mBookTextTview.setChapterText(text);
        bookModel.updateBookChapter(bookInfo.bookChapter);
        }
        });

        }

public void readNextChapter(){
        int index = bookInfo.bookChapter.getChapterIndex();
        bookInfo.bookChapter.setChapterIndex(++index);
        getChapterText();
        }

public void readPrevChapter(){
        int index = bookInfo.bookChapter.getChapterIndex();
        bookInfo.bookChapter.setChapterIndex(--index);
        getChapterText();
        }

private AlertDialog alertDialog;
private void getChapterText(){
        if(alertDialog == null){
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setMessage("下载中请稍后");
        alertDialog = builder.create();
        }
        alertDialog.show();
        BookCacheManager.getInstance().getChapterText(bookInfo,this);
        }


public class ReadHandler extends Handler{
    @Override
    public void handleMessage(Message msg) {
        //super.handleMessage(msg);
        switch(msg.what){
            case HANDLE_MESSAGE_CHAPTER_NEXT:
                readNextChapter();
                break;
            case HANDLE_MESSAGE_CHAPTER_PREV:
                readPrevChapter();
                break;
            default:

        }
    }
}

}
