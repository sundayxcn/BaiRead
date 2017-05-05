package sunday.app.bairead.data.setting;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by sunday on 2016/12/13.
 */

public class BookInfo{
    public static final String TAG = "BookInfo";
    public BookDetail bookDetail;

    /**
     * 每一个detail可以有多个chapter源，此处对应当前源信息
     * */
    public BookChapter bookChapter;

    @Override
    public boolean equals(Object o) {
        if(o instanceof BookInfo){
            BookInfo bookInfo = (BookInfo) o;
            if(bookDetail.getName().equals(bookInfo.bookDetail.getName())
                    &&bookDetail.getAuthor().equals(bookInfo.bookDetail.getAuthor())){
                return true;
            }else{
                return false;
            }
        }
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        //return super.hashCode();
        return bookDetail.getName().hashCode();
    }

    public void update(BookInfo bookInfo){
        this.bookDetail.setChapterLatest(bookInfo.bookDetail.getChapterLatest());
        this.bookDetail.setUpdateTime(bookInfo.bookDetail.getUpdateTime());
        ArrayList<Chapter> list = bookInfo.bookChapter.getChapterList();
        if(list == null || list.size() == 0){
            Log.e(TAG,"bookInfo copy update warning chapterList == null");
        }else {
            this.bookChapter.setChapterList(list);
        }
    }
}


class BookBase{
    /**
     * 书籍唯一ID
     */
    protected long id;

    public long getId() {
        return id;
    }

    public void setId(long nameId) {
        id = nameId;
    }

}
