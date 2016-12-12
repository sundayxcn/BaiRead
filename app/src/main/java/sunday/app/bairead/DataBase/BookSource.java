package sunday.app.bairead.DataBase;

/**
 * Created by sunday on 2016/12/12.
 * 监听正版网站更新，
 * 搜索结果只显示一条有最新章节的源，加入书架后，自动切换源
 * 在书籍文件夹内生成一个txt文件用于保存每个章节的源和对应的章节目录，
 * 按行读取，第一行为第一章
 */

public class BookSource {

    /**
     * 章节目录页
     */
    public String chapterLink;

    /**
     * 最后更新时间
     */
    public String chapterTime;

    /**
     * 书签
     * */
    public int bookMark;

    /**
     * 章节总数
     * */
    public int chapterCount;

    /**
     * 阅读章节
     * */
    public int chapterIndex;

    /**
     * 当前阅读源
     * */
    public boolean current;
}
