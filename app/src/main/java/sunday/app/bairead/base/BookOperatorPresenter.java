package sunday.app.bairead.base;

import java.util.List;

import sunday.app.bairead.data.setting.BookInfo;


/**
 * Created by zhongfei.sun on 2017/4/12.
 */

public interface BookOperatorPresenter extends BasePresenter {

    void deleteBook(long id);

    void deleteBooks(List<Long> list);

    void cacheBook(long id);

    void cacheBooks(List<Long> list);

    void addBook(BookInfo bookInfo);

    void readBook(BookInfo bookInfo);

    BookInfo getBook(long id);
}
