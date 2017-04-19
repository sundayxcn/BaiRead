package sunday.app.bairead.data.setting;

/**
 * Created by sunday on 2016/12/13.
 */

public class BookInfo{
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
