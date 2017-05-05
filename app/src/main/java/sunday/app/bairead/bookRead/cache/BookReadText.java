package sunday.app.bairead.bookRead.cache;

import sunday.app.bairead.data.setting.BookChapter;
import sunday.app.bairead.data.setting.Chapter;

/**
 * Created by zhongfei.sun on 2017/4/19.
 */

public class BookReadText {
    public String text;
    public String title;

    public BookReadText(Chapter chapter) {
        text = chapter.getText();
        title = chapter.getTitle();
    }
}
