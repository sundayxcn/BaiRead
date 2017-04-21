package sunday.app.bairead.base;

import sunday.app.bairead.data.setting.BookInfo;

/**
 * Created by zhongfei.sun on 2017/4/19.
 */

public interface BaseCache {
    void start(BookInfo bookInfo);
    void stop();
}
