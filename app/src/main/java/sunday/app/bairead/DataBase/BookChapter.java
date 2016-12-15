package sunday.app.bairead.DataBase;

import android.content.ContentValues;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

/**
 * Created by sunday on 2016/12/12.
 * 监听正版网站更新，
 * 搜索结果只显示一条有最新章节的源，加入书架后，自动切换源
 * 在书籍文件夹内生成一个txt文件用于保存每个章节的源和对应的章节目录，
 * 按行读取，第一行为第一章
 */

public class BookChapter{

    /**
     * 章节目录页
     */
    private String chapterLink;

    /**
     * 章节总数
     * */
    private int chapterCount;

    /**
     * 阅读章节
     * */
    private int chapterIndex;

    /**
     * 当前阅读源
     * */
    private boolean current;


    private ArrayList<ChapterText> mChapterList;


    public static class Builder{
        private String chapterLink;
        private int chapterCount;
        private int chapterIndex;
        private boolean current;
        public Builder(){

        }

        public Builder setChapterLink(String chapterLink) {
            this.chapterLink = chapterLink;
            return this;
        }

        public Builder setChapterCount(int chapterCount) {
            this.chapterCount = chapterCount;
            return this;
        }

        public Builder setChapterIndex(int chapterIndex) {
            this.chapterIndex = chapterIndex;
            return this;
        }

        public Builder setCurrent(boolean current) {
            this.current = current;
            return this;
        }

        public BookChapter build(){
            return new BookChapter(this);
        }

    }

    private BookChapter(Builder builder){
        this.chapterLink = builder.chapterLink;
        this.chapterCount = builder.chapterCount;
        this.chapterIndex = builder.chapterIndex;
        this.current = builder.current;
    }

    public int getChapterIndex() {
        return chapterIndex;
    }

    public int getChapterCount() {
        return chapterCount;
    }

    public String getChapterLink() {
        return chapterLink;
    }

    public boolean isCurrent() {
        return current;
    }


    public static class ChapterText{
        /**
         * 最终的网络地址和章节目录页拼接起来
         * */
        public ChapterText(String chapterLink,long num,String text){
            linkHead = chapterLink;
            webNum = num;
            chapterText = text;
        }
        private final String linkHead;
        private final String linkEnd = ".html";
        private long webNum;
        private String chapterText;
        public String getLink(){
            return linkHead + webNum + linkEnd;
        }

        public String getText(){
            return chapterText;
        }

        public long getNum(){
            return webNum;
        }
    }


    public ChapterText getChapterIndex(int index){
        return mChapterList.get(index);
    }

    public void setChapterList(ArrayList<ChapterText> list){
        mChapterList = list;
    }

    public void onAddToDatabase(ContentValues values){
        values.put(BookSetting.Chapter.Link,chapterLink);
        values.put(BookSetting.Chapter.INDEX,chapterIndex);
        values.put(BookSetting.Chapter.COUNT,chapterCount);

        /*
         *current 为真表示使用的是当前来源
         */
        int source = current ? 1 : 0;
        values.put(BookSetting.Chapter.CURRENT,source);
    }
}
