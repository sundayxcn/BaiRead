package sunday.app.bairead.download;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import sunday.app.bairead.database.BookInfo;
import sunday.app.bairead.database.WebInfo;

/**
 * Created by sunday on 2017/3/9.
 */

public class BookSearch {
    public static final String FILE_NAME = "search.html";
    private ISearchListener searchListener;

    public interface ISearchListener{
        void searchFinish(BookInfo bookInfo);
    }

    public BookSearch(ISearchListener searchListener){
        this.searchListener = searchListener;
    }

    /**
     * 搜索指定网站
     * */
    public void searchTopWeb(final String name) {
        String webName = WebInfo.TOP_WEB[0][0];
        String webLink = WebInfo.TOP_WEB[0][1];
        String webSearchLink = WebInfo.TOP_WEB[0][2];
        final WebInfo webInfo = new WebInfo(webName,webLink,webSearchLink);
        OKhttpManager.getInstance().connectUrl(new BookSearchListener(name) {
            @Override
            public void onFinish(HashMap<String, String> map) {
                if(map != null) {
                    Iterator iter = map.entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry entry = (Map.Entry) iter.next();
                        String bookName = (String) entry.getKey();
                        String link = (String) entry.getValue();
                        downloadChapterLink(bookName, link);
                    }
                }else{
                    searchListener.searchFinish(null);
                }
            }

            @Override
            public String getLink() {
                return webInfo.getLink() + name;
            }

            @Override
            public void onStart() {

            }

        });
    }

    private void downloadChapterLink(String bookName,final String link){
        OKhttpManager.getInstance().connectUrl(new BookDownLoadListener(bookName) {
            @Override
            public void onFinish(BookInfo bookInfo) {
                searchListener.searchFinish(bookInfo);
            }

            @Override
            public String getLink() {
                return link;
            }

            @Override
            public void onStart() {

            }
        });
    }
}
