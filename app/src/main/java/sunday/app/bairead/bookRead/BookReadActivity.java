package sunday.app.bairead.bookRead;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;

import butterknife.ButterKnife;
import sunday.app.bairead.R;
import sunday.app.bairead.base.BaseActivity;
import sunday.app.bairead.bookRead.cache.BookChapterCacheNew;
import sunday.app.bairead.bookRead.view.BookReadView;
import sunday.app.bairead.data.BookRepository;
import sunday.app.bairead.data.setting.BookInfo;
import sunday.app.bairead.utils.PreferenceSetting;

/**
 * Created by sunday on 2016/12/21.
 */

public class BookReadActivity extends BaseActivity {

    private BookReadPresenter bookReadPresenter;
    private BookReadView bookReadView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_read_activity);

        bookReadView = (BookReadView) findViewById(R.id.book_read_parent);
        Intent intent = getIntent();
        long bookId = intent.getLongExtra(BookReadContract.READ_EXTRA_ID, 0);

        bookReadView.setPreferenceSetting(PreferenceSetting.getInstance(getApplicationContext()));

        BookInfo bookInfo = BookRepository.getInstance(getApplicationContext()).getBook(bookId);

        bookReadPresenter = new BookReadPresenter(BookRepository.getInstance(getApplicationContext()),
                BookChapterCacheNew.getInstance(),
                PreferenceSetting.getInstance(getApplicationContext()),
                bookInfo,
                bookReadView
        );
        BookChapterCacheNew.getInstance().setBookChapterCacheListener(bookReadPresenter);



        bookReadPresenter.start();


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //bookReadPresenter.updateDataBookPage();
        BookChapterCacheNew.getInstance().stop();
    }

    @Override
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount() > 0){
            getFragmentManager().popBackStack();
        }else if (bookReadView.onBackPressed()) {
        } else {
            super.onBackPressed();
        }
    }

}
