package sunday.app.bairead.DataBase;

/**
 * Created by sunday on 2016/12/13.
 */

public class BookInfo {

    /**
     * 数据库对应每本书一个唯一id
     * */
    private long id;

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
