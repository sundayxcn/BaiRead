package sunday.app.bairead.tool;

import sunday.app.bairead.database.BookInfo;

/**
 * Created by sunday on 2017/3/7.
 */

public class Temp {

    private static Temp temp;
    private BookInfo bookInfo;

    public void setBookInfo(BookInfo info){
        bookInfo = info;
    }

    public BookInfo getBookInfo(){
        return bookInfo;
    }

    public void clearBookInfo(){
        bookInfo = null;
    }

    public static Temp getInstance(){
        if(temp == null){
            temp = new Temp();
        }

        return temp;
    }
}
