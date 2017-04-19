package sunday.app.bairead.data;

import java.util.List;

import rx.Observable;
import sunday.app.bairead.data.setting.BookInfo;

/**
 * Created by zhongfei.sun on 2017/4/11.
 */

public interface BookDataSource {

    /**
     * @param refresh 清除缓存/重新从数据库加载书籍
     * **/
    Observable<List<BookInfo>> loadBooks(boolean refresh);

    void addBook(BookInfo bookInfo);

    void updateBook(BookInfo bookInfo);

    void deleteBook(BookInfo bookInfo);

    void deleteBooks(List<BookInfo> list);

    BookInfo getBook(long id);

    void clear();

}
