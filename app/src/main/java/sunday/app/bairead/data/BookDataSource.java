package sunday.app.bairead.data;

import java.util.List;

import rx.Observable;
import sunday.app.bairead.data.setting.BookInfo;
import sunday.app.bairead.data.setting.BookMarkInfo;

/**
 * Created by zhongfei.sun on 2017/4/11.
 */

public interface BookDataSource {


    Observable<List<BookInfo>> loadBooks();

    void addBook(BookInfo bookInfo);

    void updateBook(BookInfo bookInfo);

    void deleteBook(BookInfo bookInfo);

    void deleteBooks(List<BookInfo> list);

    BookInfo getBook(long id);


    Observable<List<BookMarkInfo>> loadBookMarks();

    void addBookMark(BookMarkInfo bookMarkInfo);

    void deleteBookMark(BookMarkInfo bookMarkInfo);

    void clear();

}
