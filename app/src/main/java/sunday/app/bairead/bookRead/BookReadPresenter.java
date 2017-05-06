package sunday.app.bairead.bookRead;

import android.support.annotation.NonNull;
import android.widget.ListView;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import sunday.app.bairead.R;
import sunday.app.bairead.bookRead.adapter.MarkAdapter;
import sunday.app.bairead.bookRead.cache.BookReadSize;
import sunday.app.bairead.bookRead.cache.BookReadText;
import sunday.app.bairead.data.BookRepository;
import sunday.app.bairead.data.setting.BookChapter;
import sunday.app.bairead.data.setting.BookInfo;
import sunday.app.bairead.data.setting.BookMarkInfo;
import sunday.app.bairead.data.setting.Chapter;
import sunday.app.bairead.manager.BookInfoManager;
import sunday.app.bairead.bookRead.cache.IBookChapterCache;
import sunday.app.bairead.utils.PreferenceSetting;

/**
 * Created by sunday on 2017/3/6.
 */

public class BookReadPresenter implements BookReadContract.Presenter{

    private BookRepository mBookRepository;
    private BookInfo mBookInfo;
    private BookReadContract.View mBookReadView;
    private IBookChapterCache mBookChapterCache;
    private PreferenceSetting mPreferenceSetting;

    public BookReadPresenter(@NonNull BookRepository bookRepository,
                             @NonNull IBookChapterCache bookChapterCache,
                             @NonNull PreferenceSetting preferenceSetting,
                             @NonNull BookInfo bookInfo,
                             @NonNull BookReadContract.View view
    ) {
        mBookRepository = bookRepository;
        mBookChapterCache = bookChapterCache;
        mBookInfo = bookInfo;
        mPreferenceSetting = preferenceSetting;
        mBookReadView = view;
        mBookReadView.setPresenter(this);
    }

    @Override
    public void start() {
        mBookReadView.textSizeChange(getBookReadSize());
        if(mBookInfo.bookChapter.getChapterList() == null){
            mBookReadView.showLoading();
            BookInfoManager.getInstance().
                    loadChapter(mBookInfo).
                    subscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread()).
                    subscribe(new Subscriber<BookChapter>() {
                @Override
                public void onCompleted() {
                }

                @Override
                public void onError(Throwable e) {
                    mBookReadView.showToast(0);
                }

                @Override
                public void onNext(BookChapter bookChapter) {
                    mBookChapterCache.start(mBookInfo);
                    loadChapter(mBookInfo.bookChapter.getChapterIndex());
                }
            });
        }else{
            mBookChapterCache.start(mBookInfo);
            loadChapter(mBookInfo.bookChapter.getChapterIndex());
        }
    }


    @Override
    public void updateBookChapterIndex(int index) {
        mBookInfo.bookChapter.setChapterIndex(index);
        mBookRepository.updateBook(mBookInfo);
    }

    @Override
    public void updateBookChapterPage() {
        mBookRepository.updateBook(mBookInfo);
    }

    @Override
    public void goToChapterMenu() {
        mBookReadView.showChapterMenu();
    }

    @Override
    public void goToMarkMenu() {
        mBookReadView.showMarkMenu();
    }

    @Override
    public void showSetting() {
        mBookReadView.showSetting();
    }

    @Override
    public void addBookMark() {
        if (!isBookcase()) {
            mBookReadView.showToast(R.string.case_add_tips);
        } else {
            mBookRepository.addBookMark(createBookMarkInfo(mBookInfo));
            mBookReadView.showToast(R.string.mark_success);
        }

    }

    private boolean isBookcase() {
        long bookId = mBookInfo.bookDetail.getId();
        BookInfo bookInfo = mBookRepository.getBook(bookId);
        return bookInfo != null;
    }

    @Override
    public void addBook() {
        if (!isBookcase()) {
            mBookReadView.showToast(R.string.case_add_tips);
        } else {
            mBookRepository.addBook(mBookInfo);
            mBookReadView.showToast(R.string.case_add_success);
        }
    }

    @Override
    public void downBook() {
        if (!isBookcase()) {
            mBookReadView.showToast(R.string.case_add_tips);
        } else {
            mBookReadView.showToast(R.string.cache_book);
        }
    }

    public void updateTextSize(BookReadSize bookReadSize) {
        mPreferenceSetting.putIntValue(PreferenceSetting.KEY_TEXT_SIZE, bookReadSize.textSize);
        mPreferenceSetting.putIntValue(PreferenceSetting.KEY_LINE_SIZE, bookReadSize.lineSize);
        mPreferenceSetting.putIntValue(PreferenceSetting.KEY_MARGIN_SIZE, bookReadSize.marginSize);
        mBookReadView.textSizeChange(bookReadSize);
    }

    private BookMarkInfo createBookMarkInfo(BookInfo bookInfo) {
        BookMarkInfo bookMarkInfo = new BookMarkInfo();
        bookMarkInfo.setNameId(bookInfo.bookDetail.getId());
        int chapterIndex = bookInfo.bookChapter.getChapterIndex();
        bookMarkInfo.chapterIndex = chapterIndex;
        //bookMarkInfo.text = BookChapterCacheNew.getInstance().getMarkText(chapterIndex);
        //bookMarkInfo.title = BookChapterCacheNew.getInstance().getMarkTitle(chapterIndex);
        return bookMarkInfo;
    }


    @Override
    public void loadBookMark(ListView listView) {
        mBookRepository.loadBookMarks().
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(bookMarkInfos -> listView.setAdapter(new MarkAdapter(listView.getContext(), bookMarkInfos)));
    }

    @Override
    public void deleteBookMark(BookMarkInfo bookMarkInfo) {
        mBookRepository.deleteBookMark(bookMarkInfo);
    }

    @Override
    public void clearBookMark() {
    }


    @Override
    public BookInfo getBookInfo() {
        return mBookInfo;
    }

    public BookReadSize getBookReadSize() {
        int textSize = mPreferenceSetting.getIntValue(PreferenceSetting.KEY_TEXT_SIZE, 45);
        int lineSize = mPreferenceSetting.getIntValue(PreferenceSetting.KEY_LINE_SIZE, 45);
        int marginSize = mPreferenceSetting.getIntValue(PreferenceSetting.KEY_MARGIN_SIZE, 20);
        return new BookReadSize(textSize,lineSize,marginSize);
    }

    @Override
    public void setChapterIndex(int index) {
        mBookChapterCache.setIndex(index);
        loadChapter(index);
    }

    @Override
    public void chapterNext() {
        int chapterIndex = mBookInfo.bookChapter.getChapterIndex() + 1;
        if (chapterIndex >= mBookInfo.bookChapter.getChapterCount()) {
            mBookReadView.showToast(R.string.last_chapter);
        } else {
            loadChapter(chapterIndex);
        }
    }

    @Override
    public void chapterPrev() {
        int chapterIndex = mBookInfo.bookChapter.getChapterIndex() - 1;
        if (chapterIndex < 0) {
            mBookReadView.showToast(R.string.first_chapter);
        } else {
            loadChapter(chapterIndex);
        }
    }

    private void loadChapter(int index){
        updateBookChapterIndex(index);
        Chapter chapter = mBookInfo.bookChapter.getChapter(index);
        if(chapter.getText() != null){
            mBookReadView.showChapter(new BookReadText(chapter));
            mBookChapterCache.remove(index);
        }else{
            mBookReadView.showLoading();
            mBookChapterCache.downloadChapter(index).
                    subscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread()).
                    subscribe(new Subscriber<Chapter>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                }

                @Override
                public void onNext(Chapter chapter) {
                    mBookReadView.hideLoading();
                    mBookReadView.showChapter(new BookReadText(chapter));
                }
            });
        }

    }

}
