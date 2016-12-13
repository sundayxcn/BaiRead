package sunday.app.bairead.DataBase;

/**
 * Created by sunday on 2016/12/13.
 * 书签
 */

public class BookMark{
    private int chapterIndex;
    private long nameId;

    public long getNameId() {
        return nameId;
    }

    public void setNameId(long nameId) {
        this.nameId = nameId;
    }

    public int getChapterIndex() {
        return chapterIndex;
    }

    public void setChapterIndex(int chapterIndex) {
        this.chapterIndex = chapterIndex;
    }
}
