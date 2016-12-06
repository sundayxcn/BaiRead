package sunday.app.bairead.Tool;

import java.util.ArrayList;

import sunday.app.bairead.UI.SearchFragment;

/**
 * 访问百度搜索书籍
 * Created by sunday on 2016/12/1.
 */
public class SearchManager extends OKhttpManager {

    public static final String SEARCH_TXT = "search.txt";
    public static final String BAIDU = "http://www.baidu.com/s?tn=baiduhome_pg&rn=50&wd=";

    private SearchFragment searchFragment;

    public SearchManager(SearchFragment fragment){
        searchFragment = fragment;
    }

    @Override
    public void downloadEnd(String fileName) {
        if (fileName != null) {
            SearchHtmlParse searchHtmlParse = new SearchHtmlParse();
            JsoupParse.from(fileName, searchHtmlParse);
            ArrayList<SearchLink> searchLinkList = searchHtmlParse.result();
            searchFragment.refreshSearchResult(searchLinkList);
        }else{
            searchFragment.refreshSearchResult(null);
        }
    }


    public void search(String bookName) {
        OKhttpManager.getInstance().connectHtml(bookName);
    }

}
