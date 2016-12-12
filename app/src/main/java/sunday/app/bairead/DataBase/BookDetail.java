package sunday.app.bairead.DataBase;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.HashMap;

public class BookDetail {

    /**
     * Html meta标签对应的我们有用的属性
     */
    public static class Meta {
        final static public String NAME = "og:novel:book_name";
        final static public String AUTHOR = "og:novel:author";
        final static public String DESCRIPTION = "og:description";
        final static public String IMAGE = "og:image";
    }


    public static class Builder {
        private String name;
        private String author;
        private String coverImageLink;
        private String description;

        public Builder(HashMap metaMap) {
            name = (String) metaMap.get(Meta.NAME);
            author = (String) metaMap.get(Meta.AUTHOR);
            description = (String) metaMap.get(Meta.DESCRIPTION);
            coverImageLink = (String) metaMap.get(Meta.IMAGE);
        }

        public BookDetail build() {
            return new BookDetail(this);
        }

    }

    /**
     * 数据库对应每本书一个唯一id
     * */
    long id;
    /**
     * 书名
     */
    public String name;
    /**
     * 作者
     */
    public String author;

    /**
     * 封面图片链接
     **/
    public String coverImageLink;

    /**
     * 简介
     **/
    public String description;

    private BookDetail(Builder builder) {
        this.name = builder.name;
        this.author = builder.author;
        this.coverImageLink = builder.coverImageLink;
        this.description = builder.description;
    }

}
