package sunday.app.bairead;

import java.lang.reflect.Array;
import java.util.ArrayList;

import sunday.app.bairead.DataBase.SearchBook;
import sunday.app.bairead.Tool.JsoupParse;
import sunday.app.bairead.Tool.OKhttpManager;

/**
 * 访问百度搜索书籍
 * Created by sunday on 2016/12/1.
 */
public class SearchManager implements DownLoadManager.DownLoadSearchListener {

    public static final String SEARCH_TXT = "search.txt";
    public static final String BAIDU = "http://www.baidu.com/s?tn=99853826_hao_pg&rn=50&wd=";


    private SearchBook searchBook;

    private ArrayList<String> searchLinkList;


    class SearchLink{
        /**
         * 章节列表页
         * */
        String webLink;


    }

    @Override
    public void start(String bookName) {

    }

    @Override
    public void end(String fileName) {
        if (fileName != null) {
            JsoupParse jsoupParse = new JsoupParse();
            searchLinkList = jsoupParse.loadSearchHtml(fileName);
        }
    }


    public void search(String bookName) {
        OKhttpManager.getInstance().connectHtml(bookName, this);
    }

}
