package sunday.app.bairead.bookRead;

import android.content.Context;
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
import sunday.app.bairead.utils.FileManager;
import sunday.app.bairead.utils.PreferenceSetting;

/**
 * Created by sunday on 2017/3/6.
 */

public class BookReadPresenter implements BookReadContract.ReadPresenter {

    private BookRepository mBookRepository;
    private IBookChapterCache mBookChapterCache;
    private BookInfo mBookInfo;

    private BookReadContract.ViewRead mBookReadView;
    private BookReadContract.ReadSetting mReadSetting;

    public BookReadPresenter(@NonNull BookRepository bookRepository,
                             @NonNull IBookChapterCache bookChapterCache,
                             @NonNull BookReadContract.ReadSetting readSetting,
                             @NonNull BookInfo bookInfo,
                             @NonNull BookReadContract.ViewRead view
    ) {
        mBookRepository = bookRepository;
        mBookChapterCache = bookChapterCache;
        mBookInfo = bookInfo;
        mReadSetting = readSetting;
        mBookReadView = view;
        mBookReadView.setPresenter(this);
    }

    @Override
    public void start() {
        mBookReadView.textSizeChange(mReadSetting.getReadSize());
        mBookReadView.setPage(mBookInfo.bookChapter.getChapterPage());
        if (mBookInfo.bookChapter.getChapterList() == null) {
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
                            FileManager.deleteFile(BookInfoManager.getInstance().getFullChapterFileName(mBookInfo.bookDetail.getName()));
                            mBookReadView.showToast(R.string.chapter_load_error);
                        }

                        @Override
                        public void onNext(BookChapter bookChapter) {
                            mBookChapterCache.start(mBookInfo);
                            loadChapter(mBookInfo.bookChapter.getChapterIndex());
                        }
                    });
        } else {
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
    public void updateBookChapterPage(int page) {
        mBookInfo.bookChapter.setChapterPage(page);
        //mBookRepository.updateBook(mBookInfo);
    }


    @Override
    public void showSetting() {

    }


    @Override
    public void updateTextSize() {
        BookReadSize bookReadSize = mReadSetting.getReadSize();
        mBookReadView.textSizeChange(bookReadSize);
    }


    @Override
    public void stop() {
        mBookRepository.updateBook(mBookInfo);
        mBookChapterCache.stop();
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
            //保存page
            mBookRepository.updateBook(mBookInfo);
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

    private void loadChapter(int index) {
        updateBookChapterIndex(index);
        Chapter chapter = mBookInfo.bookChapter.getChapter(index);
        if (chapter.getText() != null) {
            mBookReadView.showChapter(new BookReadText(chapter));
            mBookChapterCache.remove(index);
        } else {
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
