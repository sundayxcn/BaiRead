package sunday.app.bairead.bookRead.cache;

/**
 * Created by zhongfei.sun on 2017/4/19.
 */

public class BookReadSize {
    public static final int TYPE_TEXT = 0;
    public static final int TYPE_LINE = TYPE_TEXT + 1;
    public static final int TYPE_MAEGIN = TYPE_LINE + 1;

    public int textSize;
    public int lineSize;
    public int marginSize;

    public BookReadSize(int textSize, int lineSize, int marginSize) {
        this.textSize = textSize;
        this.lineSize = lineSize;
        this.marginSize = marginSize;
    }
}
