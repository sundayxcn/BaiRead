package sunday.app.bairead.Tool;

import java.util.ArrayList;

/**
 * 访问百度搜索书籍
 * Created by sunday on 2016/12/1.
 */
public class SearchManager extends OKhttpManager{

    public static final String SEARCH_TXT = "search.txt";
    public static final String BAIDU = "http://www.baidu.com/s?tn=baiduhome_pg&rn=50&wd=";


    //private B searchBook;

    private ArrayList<SearchLink> searchLinkList;


    class SearchLink{
        /**
         * 章节列表页
         * */
        String webLink;

        /**
         * 站点名称
         * */
        String webName;
    }


    @Override
    public void downloadEnd(String fileName) {
        if (fileName != null) {
            JsoupParse jsoupParse = new JsoupParse();
            searchLinkList = jsoupParse.loadSearchHtml(fileName);
        }
    }


    public void search(String bookName) {
        OKhttpManager.getInstance().connectHtml(bookName);
    }

}
