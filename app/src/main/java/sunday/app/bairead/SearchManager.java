package sunday.app.bairead;

import sunday.app.bairead.DataBase.SearchBook;
import sunday.app.bairead.Tool.OKhttpManager;

/**
 * 访问百度搜索书籍
 * Created by sunday on 2016/12/1.
 */
public class SearchManager implements DownLoadManager.DownLoadSearchListener {


    public static final String BAIDU = "https://www.baidu.com/s?tn=99853826_hao_pg&rn=50&wd=";


    private SearchBook searchBook;


    @Override
    public void start(String bookName) {
        searchBook = new SearchBook();
    }

    @Override
    public void end(String fileName) {
        if (fileName != null) {

        }
    }


    public void search(String bookName) {
        String web = BAIDU + bookName;
        OKhttpManager.getInstance().connectHtml(web, this);
    }

}
