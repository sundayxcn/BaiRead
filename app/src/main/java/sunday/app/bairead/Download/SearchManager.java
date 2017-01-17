package sunday.app.bairead.Download;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;
import sunday.app.bairead.DataBase.BookChapter;
import sunday.app.bairead.DataBase.BookInfo;
import sunday.app.bairead.DataBase.WebInfo;
import sunday.app.bairead.Parse.ParseChapter;
import sunday.app.bairead.Parse.ParseDetail;
import sunday.app.bairead.Parse.ParseSearch;
import sunday.app.bairead.Parse.ParseXml;
import sunday.app.bairead.Tool.FileManager;
import sunday.app.bairead.UI.SearchFragment;

/**
 * 访问百度搜索书籍
 * Created by sunday on 2016/12/1.
 */
public class SearchManager extends OKhttpManager {

    public static final String SEARCH_DIR = "search";

    private SearchFragment searchFragment;
    private String bookName;

    public SearchManager(SearchFragment fragment){
        searchFragment = fragment;
    }

    /**
     * 搜索指定网站
     * */
    public void searchTopWeb(final String name) {
        bookName = name;
        String webName = WebInfo.TOP_WEB[0][0];
        String webLink = WebInfo.TOP_WEB[0][1];
        String webSearchLink = WebInfo.TOP_WEB[0][2];
        WebInfo webInfo = new WebInfo(webName,webLink,webSearchLink);

        final String fileDir = FileManager.PATH +"/"+bookName+"/"+SEARCH_DIR;
        final String fileName = fileDir + "/"+"search.html";

        connectUrl(webInfo.getLink() + bookName,new ConnectListener() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                FileManager.writeByte(fileName,response.body().bytes());
                HashMap<String,String> hashMap =  ParseXml.createParse(ParseSearch.class).parse(fileName);
                String chapterLink = hashMap.get(bookName);
                downloadChapterLink(chapterLink);
            }

            @Override
            public void start(String url) {

                FileManager.createDir(fileDir);
            }

        });
    }

    private void downloadChapterLink(String link){
        if(link != null){
            final String chapterFile = FileManager.PATH +"/"+bookName+"/" + BookChapter.FileName;
            connectUrl(link, new ConnectListener() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if(response.networkResponse() != null) {
                        FileManager.writeByte(chapterFile, response.body().bytes());
                        BookInfo bookInfo = new BookInfo();
                        bookInfo.bookDetail = ParseXml.createParse(ParseDetail.class).parse(chapterFile);
                        bookInfo.bookChapter = ParseXml.createParse(ParseChapter.class).parse(chapterFile);
                        searchFragment.refreshSearchResult(bookInfo);
                    }
                }

                @Override
                public void start(String url) {

                }

            });
        }
    }


    public void debugDetail(String name){
        final String chapterFile = FileManager.PATH +"/"+name+"/" + BookChapter.FileName;
        BookInfo bookInfo = new BookInfo();
        bookInfo.bookDetail = ParseXml.createParse(ParseDetail.class).parse(chapterFile);
        bookInfo.bookChapter = ParseXml.createParse(ParseChapter.class).parse(chapterFile);
        searchFragment.refreshSearchResult(bookInfo);
    }


}
