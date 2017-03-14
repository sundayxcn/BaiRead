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

    private ISearchListener searchListener;
    public interface ISearchListener{
        void searchFinish(BookInfo bookInfo);
        void searchStart();
        void searchError();
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
        OKhttpManager.getInstance().connectUrl(new BookSearchListener() {
            @Override
            public void onError() {
                if(searchListener != null){
                    searchListener.searchError();
                }
            }

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
                String link = webInfo.getLink()  + name;
                return link;
            }

            @Override
            public void onStart() {
                if(searchListener != null){
                    searchListener.searchStart();
                }
            }

        });
    }

    private void downloadChapterLink(String bookName,final String link){
        OKhttpManager.getInstance().connectUrl(new BookDownLoadListener(bookName, BookDownLoadListener.Type.ParseSearch) {
            @Override
            public void onFinish(BookInfo bookInfo) {
                searchListener.searchFinish(bookInfo);
            }

            @Override
            public void onError() {
                searchListener.searchFinish(null);
            }

            @Override
            public String getLink() {
                return link;
            }

            @Override
            public void onStart() {
                    if(searchListener != null){
                        searchListener.searchStart();
                    }
            }

        });
    }
}
