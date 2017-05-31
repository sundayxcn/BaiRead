package sunday.app.bairead.bookRead;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import sunday.app.bairead.R;
import sunday.app.bairead.base.BaseActivity;
import sunday.app.bairead.bookRead.cache.BookSimpleCache;
import sunday.app.bairead.bookRead.cache.IBookChapterCache;
import sunday.app.bairead.bookRead.view.BookReadView;
import sunday.app.bairead.data.BookRepository;
import sunday.app.bairead.data.setting.BookInfo;
import sunday.app.bairead.download.BookDownService;
import sunday.app.bairead.parse.ParseBookChapter;
import sunday.app.bairead.parse.ParseChapterText;
import sunday.app.bairead.utils.ActivityUtils;
import sunday.app.bairead.utils.PreferenceSetting;
import sunday.app.bairead.utils.Temp;

/**
 * Created by sunday on 2016/12/21.
 */

public class BookReadActivity extends BaseActivity{

    private BookReadContract.ReadPresenter mBookReadPresenter;
    private BookReadContract.SettingPresenter mBookSettingPresenter;
    private BookReadView mBookReadView;
    private BookReadSizeSetting mBookReadSetting;
    private IBookChapterCache mBookChapterCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_read_activity);
        mBookReadView = (BookReadView) findViewById(R.id.book_read_parent);
        Log.e("sunday","BookReadActivity-onCreate");
        registerBroadcast();
        Intent intent = getIntent();
        long bookId = intent.getLongExtra(BookReadContract.READ_EXTRA_ID, 0);
        BookInfo bookInfo = BookRepository.getInstance(getApplicationContext()).getBook(bookId);
        if(bookId == 0) {
            bookInfo = Temp.getInstance().getBookInfo();
        }
        mBookReadSetting = new BookReadSizeSetting(getApplicationContext());
        mBookChapterCache = new BookSimpleCache(new BookDownService(),
                new ParseBookChapter(),
                new ParseChapterText());
        mBookReadPresenter = new BookReadPresenter(BookRepository.getInstance(getApplicationContext()),
                mBookChapterCache,
                mBookReadSetting,
                bookInfo,
                mBookReadView
        );
        mBookReadPresenter.start();
        //ViewGroup viewGroup = (ViewGroup) mBookReadView;
        mBookSettingPresenter = new BookSettingPresenter(BookRepository.getInstance(getApplicationContext()),
                bookInfo,
                mBookReadView,
                mBookReadSetting
                );

    }

    public static final String ACTION_CHAPTER = "ChapterMenu";
    public static final String ACTION_MARK = "MarkMenu";
    public static final String ACTION_READ_SIZE = "ReadSize";
    public static final String ACTION_VIEW_SETTING = "ViewSetting";
    public static final String ACTION_CHAPTER_INDEX = "ChapterIndex";
    public static final String ACTION_CHAPTER_EXTRA = "chapterIndex";
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ACTION_CHAPTER:
                    goToChapterMenu();
                    break;
                case ACTION_MARK:
                    goToMarkMenu();
                    break;
                case ACTION_READ_SIZE:
                    mBookReadPresenter.updateTextSize();
                    break;
                case ACTION_VIEW_SETTING:
                    mBookSettingPresenter.showSetting();
                    break;
                case ACTION_CHAPTER_INDEX:
                    int index = intent.getIntExtra(ACTION_CHAPTER_EXTRA,0);
                    mBookReadPresenter.setChapterIndex(index);
                    break;
                default:
                    break;
            }
        }
    };
    public void registerBroadcast(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_CHAPTER);
        intentFilter.addAction(ACTION_MARK);
        intentFilter.addAction(ACTION_READ_SIZE);
        intentFilter.addAction(ACTION_VIEW_SETTING);
        intentFilter.addAction(ACTION_CHAPTER_INDEX);
        registerReceiver(broadcastReceiver,intentFilter);
    }

    public void unRegister(){
        unregisterReceiver(broadcastReceiver);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegister();
        mBookReadPresenter.stop();
    }


    public void goToChapterMenu() {
        BookReadChapterFragment bookReadChapterFragment = new BookReadChapterFragment();
        bookReadChapterFragment.setPreferenceSetting(PreferenceSetting.getInstance(getApplicationContext()));
        ActivityUtils.addFragmentToActivity(getFragmentManager(), bookReadChapterFragment,
                R.id.book_read_parent);
    }


    public void goToMarkMenu() {
        BookReadMarkFragment bookReadMarkFragment = new BookReadMarkFragment();
        ActivityUtils.addFragmentToActivity(getFragmentManager(),
                bookReadMarkFragment,
                R.id.book_read_parent);
    }

}
