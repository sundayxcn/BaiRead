package sunday.app.bairead.Tool;

/**
 * Created by sunday on 2016/12/8.
 */

public class BookWeb {
    /**
     * 站内搜索使用百度引擎的SID
     * 访问方式var url = "http://www.baidu.com/s?ie=utf-8&cus_sid=14724046118796340648&tn=SE_pscse_053x7tyx&wd="
     * + encodeURIComponent(书名);
     * */
    public static final String[] SID = {
            /**
             * 笔趣阁 http://www.biquge.com/
            */
            "287293036948159515",
            /**
             * 新笔趣阁 http://www.xxbiquge.com/
            */
            "8823758711381329060",
            /**
             * 顶点小说 http://www.23wx.com/
            */
            "15772447660171623812",
            /**
             * 假顶点小说 http://www.23us.so/
            */
            "17233375349940438896",
            /**
             * 无错小说 http://www.quledu.com/
            */
            "14724046118796340648",
    };

}
