package sunday.app.bairead.bookRead.cache;

import rx.Observable;
import sunday.app.bairead.data.setting.BookInfo;
import sunday.app.bairead.data.setting.Chapter;

/**
 * Created by zhongfei.sun on 2017/5/2.
 */

public interface IBookChapterCache {
    //String getChapterText(int index);
    void start(BookInfo bookInfo);
    void stop();
    void setIndex(int index);
    void remove(int index);
    Observable<Chapter> downloadChapter(int index);
}
