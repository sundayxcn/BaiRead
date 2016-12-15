package sunday.app.bairead.DataBase;

/**
 * Created by sunday on 2016/12/13.
 */

public class BookInfo {
    public BookDetail bookDetail;

    /**
     * 每一个detail可以有多个chapter源，此处对应当前源信息
     * */
    public BookChapter bookChapter;
}
