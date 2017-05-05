package sunday.app.bairead.data.setting;

/**
 * Created by zhongfei.sun on 2017/5/2.
 */

public class Chapter {
    private final String linkHead;
    private final String linkEnd = ".html";
    /**
     * 仅用于章节排序
     */
    private long webNum;
    private String chapterText;
    private String chapterTitle;

    /**
     * 最终的网络地址和章节目录页拼接起来
     */
    public Chapter(String chapterLink, long num, String title) {
        linkHead = chapterLink;
        webNum = num;
        chapterTitle = title;
    }

    public String getLink() {
        return linkHead + webNum + linkEnd;
    }

    public String getText() {
        return chapterText;
    }

    public void setText(String chapterText) {
        this.chapterText = chapterText;
    }

    public long getNum() {
        return webNum;
    }

    public String getTitle() {
        return chapterTitle;
    }
}
