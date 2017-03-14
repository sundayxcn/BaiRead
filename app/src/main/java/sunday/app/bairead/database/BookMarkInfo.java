package sunday.app.bairead.database;

/**
 * Created by sunday on 2016/12/13.
 * 书签
 */

public class BookMarkInfo {

    long nameId;
    public int chapterIndex;
    public String title;
    public String text;
    public void setNameId(long id){
        nameId = id;
    }
}
