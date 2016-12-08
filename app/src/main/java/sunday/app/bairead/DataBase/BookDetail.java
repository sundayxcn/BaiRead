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
        final static public String URL = "og:url";
        final static public String TIME = "og:novel:update_time";
        final static public String CHAPTERlATEST = "og:novel:latest_chapter_name";

    }


    public static class Builder {
        private String name;
        private String author;
        private String coverImageLink;
        private Bitmap coverImage;
        private String description;
        private String chapterLatest;
        private String chapterUrl;
        private String chapterTime;
        private String sourceName;

        public Builder(HashMap metaMap) {
            name = (String) metaMap.get(Meta.NAME);
            author = (String) metaMap.get(Meta.AUTHOR);
            chapterTime = (String) metaMap.get(Meta.TIME);
            description = (String) metaMap.get(Meta.DESCRIPTION);
            coverImageLink = (String) metaMap.get(Meta.IMAGE);
            chapterUrl = (String) metaMap.get(Meta.URL);
            chapterLatest = (String) metaMap.get(Meta.CHAPTERlATEST);
        }

        public Builder setImage(Bitmap bitmap) {
            coverImage = bitmap;
            return this;
        }

        public Builder setChapterLatest(String chapterLatest) {
            this.chapterLatest = chapterLatest;
            return this;
        }

        public Builder setSourceName(String sourceName) {
            this.sourceName = sourceName;
            return this;
        }

        public BookDetail build() {
            return new BookDetail(this);
        }

    }

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
     * 封面图片
     */
    public Bitmap coverImage;

    /**
     * 简介
     **/
    public String description;

    /**
     * 最新章节名
     */
    public String chapterLatest;

    /**
     * 章节目录页
     */
    public String chapterUrl;

    /**
     * 最后更新时间
     */
    public String chapterTime;

    /**
     * 站点名称
     */
    public String sourceName;


//  public static BookDetail build(Meta meta){
//    BookDetail detail = new BookDetail();
//    detail.name = meta.n
//  }


    private BookDetail(Builder builder) {
        this.name = builder.name;
        this.author = builder.author;
        this.coverImageLink = builder.coverImageLink;
        this.coverImage = builder.coverImage;
        this.description = builder.description;
        this.chapterLatest = builder.chapterLatest;
        this.chapterUrl = builder.chapterUrl;
        this.chapterTime = builder.chapterTime;
        this.sourceName = builder.sourceName;
    }

    public boolean isValid(){
        return (chapterLatest != null && !chapterLatest.equals("")) || (chapterTime != null && !chapterTime.equals(""));
    }

}
