package sunday.app.bairead.bookcase;

import android.content.Intent;
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

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import sunday.app.bairead.bookRead.cache.BookChapterCacheNew;
import sunday.app.bairead.data.BookRepository;
import sunday.app.bairead.data.setting.BookInfo;
import sunday.app.bairead.download.BookDown;
import sunday.app.bairead.download.BookDownLoad;
import sunday.app.bairead.utils.ActivityUtils;
import sunday.app.bairead.utils.FileManager;
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
            BookDown.getInstance().updateNewChapter(bookInfo).
                    subscribeOn(Schedulers.newThread()).
                    observeOn(AndroidSchedulers.mainThread()).
                    subscribe(bookInfo1 -> {
                        if(!bookInfo1.bookDetail.getChapterLatest().equals(bookInfo.bookDetail.getChapterLatest())){
                            bookInfo.bookChapter.setChapterList(bookInfo1.bookChapter.getChapterList());
                            bookInfo.bookDetail.setUpdateTime(bookInfo1.bookDetail.getUpdateTime());
                            bookInfo.bookDetail.setChapterLatest(bookInfo1.bookChapter.getLastChapter().getTitle());
                            mBookRepository.updateBook(bookInfo);
                            mBookcaseView.refresh(bookInfo);
                        }
                        checkFinish();
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
            BookChapterCacheNew.getInstance().downloadAllChpater(bookInfo);
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
