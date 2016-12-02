package sunday.app.bairead.DataBase;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by sunday on 2016/12/1.
 * 图书搜索每个链接需要得到的信息
 */
public class SearchBook {
    /**
     * 章节目录链接
     * */
    String chapterLink;
    /**
     * 封面图片链接
     * **/
    String coverImageLink;
    /**
     * 封面图片
     * */
    Bitmap coverImage;
    /**
     * 书名
     * */
    String bookName;
    /**
     * 作者
     * */
    String author;
    /**
     * 最新章节序号
     * */
    int chapterLatest;

}
