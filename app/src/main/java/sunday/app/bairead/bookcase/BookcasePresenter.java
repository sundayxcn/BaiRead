package sunday.app.bairead.bookcase;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import sunday.app.bairead.R;
import sunday.app.bairead.data.BookRepository;
import sunday.app.bairead.data.setting.BookInfo;
import sunday.app.bairead.utils.PreferenceKey;
import sunday.app.bairead.utils.PreferenceSetting;

/**
 * Created by zhongfei.sun on 2017/4/11.
 */

public class BookcasePresenter implements BookcaseContract.Presenter {

    private BookRepository mBookRepository;
    private BookcaseContract.View mBookcaseView;
    private PreferenceSetting mPreferenceSetting;


    public BookcasePresenter(@NonNull BookRepository bookRepository,
                             @NonNull PreferenceSetting preferenceSetting,
                             @NonNull BookcaseContract.View view){
        mBookRepository = bookRepository;
        mBookcaseView = view;
        mPreferenceSetting = preferenceSetting;

        mBookcaseView.setPresenter(this);
    }


    @Override
    public void loadBooks(boolean refresh) {
        mBookcaseView.showLoading();
        mBookRepository.loadBooks(refresh).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(list -> {
                    if (list.size() > 0) {
                        orderBooks(list);
                        mBookcaseView.showBooks(list);
                    } else {
                        mBookcaseView.showNoBooks();
                    }
                });
    }


    private void checkFinish(){
        Log.e("sunday","atomicInteger.decrementAndGet() = "+atomicInteger.decrementAndGet());
        if(atomicInteger.decrementAndGet() <= 0){
            mBookcaseView.hideLoading();
        }
    }

    private synchronized void checkBookInit(int size){
        atomicInteger = new AtomicInteger(size);
    }

    private AtomicInteger atomicInteger;


    /**
     * 检查章节更新
     * **/
    @Override
    public void updateBooks(List<BookInfo> list) {
        checkBookInit(list.size());
        for(final BookInfo bookInfo : list){
            BookInfoManager.getInstance().
                    updateNewChapter(bookInfo).
                    subscribeOn(Schedulers.newThread()).
                    observeOn(AndroidSchedulers.mainThread()).
                    subscribe(new Observer<BookInfo>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            if(e instanceof NullPointerException){
                                if(e.getMessage().contains("okhttp3.ResponseBody")){
                                    mBookcaseView.showToast(R.string.network_exception);
                                }
                            }
                            mBookcaseView.hideLoading();
                        }

                        @Override
                        public void onNext(BookInfo newBookInfo) {
                                if(!newBookInfo.bookDetail.getChapterLatest().equals(bookInfo.bookDetail.getChapterLatest())){
                                    bookInfo.update(newBookInfo);
                                    mBookRepository.updateBook(bookInfo);
                                    mBookcaseView.refresh(bookInfo);
                                }
                                checkFinish();
                        }
                    });
        }

    }

    @Override
    public void updateBook(long id) {

    }

    @Override
    public void orderBooks(List<BookInfo> list) {
        int order = getOrderKey();
        Comparator<BookInfo> comparator = ComparatorManager.getComparator(order);
        Collections.sort(list,comparator);
    }

    private @PreferenceKey.KeyInt int getOrderKey(){
        return mPreferenceSetting.getIntValue(PreferenceSetting.KEY_CASE_LIST_ORDER);
    }


    @Override
    public void deleteBook(long id) {
        BookInfo bookInfo = mBookRepository.getBook(id);
        mBookRepository.deleteBook(bookInfo);
    }

    @Override
    public void deleteBooks(List<Long> list) {
        List<BookInfo> bookInfos = new ArrayList<>();
        for(long id : list){
            bookInfos.add(mBookRepository.getBook(id));
        }
        mBookRepository.deleteBooks(bookInfos);
    }

    @Override
    public void cacheBook(long id) {
        
    }

    @Override
    public void cacheBooks(List<Long> list) {
        for (long id : list) {
            BookInfo bookInfo = mBookRepository.getBook(id);
        }
    }

    @Override
    public void addBook(BookInfo bookInfo) {
        mBookRepository.addBook(bookInfo);
    }

    @Override
    public void readBook(BookInfo bookInfo) {
        //ActivityUtils.readBook(get);
    }

    @Override
    public BookInfo getBook(long id) {
        return mBookRepository.getBook(id);
    }

    @Override
    public void topBooks(Map<Long,Boolean> map){
        Set set = map.keySet();
        Iterator iter = set.iterator();
        while (iter.hasNext()) {
            long key = (long) iter.next();
            boolean check = map.get(key);
            mBookRepository.getBook(key).bookDetail.setTopCase(check);
        }
        //需要排序
        loadBooks(false);
    }


    @Override
    public void start() {
        loadBooks(true);
    }


}
