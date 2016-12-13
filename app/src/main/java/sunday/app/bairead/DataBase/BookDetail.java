package sunday.app.bairead.DataBase;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.HashMap;

public class BookDetail extends BookInfo{

    /**
     * Html meta标签对应的我们有用的属性
     */
    public static class Meta {
        final static public String NAME = "og:novel:book_name";
        final static public String AUTHOR = "og:novel:author";
        final static public String DESCRIPTION = "og:description";
        final static public String IMAGE = "og:image";
        final static public String CHAPTER_LATEST = "og:novel:latest_chapter_name";
        final static public String UPDATE_TIME = "og:novel:update_time";
    }


    public static class Builder {
        private String name;
        private String author;
        private String coverImageLink;
        private String description;
        private String chapterLatest;
        private String updateTime;


        public Builder(HashMap metaMap) {
            name = (String) metaMap.get(Meta.NAME);
            author = (String) metaMap.get(Meta.AUTHOR);
            description = (String) metaMap.get(Meta.DESCRIPTION);
            coverImageLink = (String) metaMap.get(Meta.IMAGE);
            chapterLatest = (String) metaMap.get(Meta.CHAPTER_LATEST);
            updateTime = (String) metaMap.get(Meta.UPDATE_TIME);
        }

        public BookDetail build() {
            return new BookDetail(this);
        }

    }


    /**
     * 书名
     */
    private String name;
    /**
     * 作者
     */
    private String author;

    /**
     * 封面图片链接
     **/
    private String coverImageLink;

    /**
     * 简介
     **/
    private String description;

    /**
     * 最新章节
     * */
    private String chapterLatest;

    /**
     * 最后更新时间
     * */
    private String updateTime;

    private BookDetail(Builder builder) {
        this.name = builder.name;
        this.author = builder.author;
        this.coverImageLink = builder.coverImageLink;
        this.description = builder.description;
        this.chapterLatest = builder.chapterLatest;
        this.updateTime = builder.updateTime;
    }

    public String getName(){
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public String getChapterLatest() {
        return chapterLatest;
    }

    public String getCoverImageLink() {
        return coverImageLink;
    }

    public String getDescription() {
        return description;
    }

    public String getUpdateTime() {
        return updateTime;
    }

}
