package sunday.app.bairead.Download;

import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
    private ISearchListener searchListener;

    public interface ISearchListener{
        void searchFinish(BookInfo bookInfo);
    }

    public SearchManager(ISearchListener searchListener){
        this.searchListener = searchListener;
    }


    /**
     * 搜索指定网站
     * */
    public void searchTopWeb(final String name) {
        String webName = WebInfo.TOP_WEB[0][0];
        String webLink = WebInfo.TOP_WEB[0][1];
        String webSearchLink = WebInfo.TOP_WEB[0][2];
        WebInfo webInfo = new WebInfo(webName,webLink,webSearchLink);

        final String fileName = getFullName(name,"search.html");

        connectUrl(webInfo.getLink() + name,new ConnectListener() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("sunday","onFailure");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                FileManager.writeByte(fileName,response.body().bytes());
                response.body().close();
                HashMap<String,String> hashMap =  ParseXml.createParse(ParseSearch.class).parse(fileName);
                //String chapterLink = hashMap.get(bookName);
                Iterator iter = hashMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    String bookName = (String) entry.getKey();
                    String link = (String) entry.getValue();
                    downloadChapterLink(bookName,link);
                }

            }

            @Override
            public void start(String url) {
                //createFileDir(name);
            }

        });
    }


    public String getFullName(String bookName,String fileName){
        return createFileDir(bookName) + "/" + fileName;
    }

    public String createFileDir(String bookName){
         return FileManager.createDir(FileManager.PATH +"/"+bookName);
    }

    private void downloadChapterLink(final String bookName, String link){
        if(link != null){
            final String chapterFile = getFullName(bookName,BookChapter.FileName);
            connectUrl(link, new ConnectListener() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("sunday","onFailure");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    //if(response.networkResponse() != null) {
                        FileManager.writeByte(chapterFile, response.body().bytes());
                        response.body().close();
                        BookInfo bookInfo = new BookInfo();
                        bookInfo.bookDetail = ParseXml.createParse(ParseDetail.class).parse(chapterFile);
                        bookInfo.bookChapter = ParseXml.createParse(ParseChapter.class).parse(chapterFile);
                        searchListener.searchFinish(bookInfo);
                    //}
                }

                @Override
                public void start(String url) {
                    //createFileDir(bookName);
                }

            });
        //sunday-change for dont find book
        }else{
            searchListener.searchFinish(null);
        }
        //sunday-change for dont find book
    }


    public void debugDetail(String name){
        final String chapterFile = FileManager.PATH +"/"+name+"/" + BookChapter.FileName;
        BookInfo bookInfo = new BookInfo();
        bookInfo.bookDetail = ParseXml.createParse(ParseDetail.class).parse(chapterFile);
        bookInfo.bookChapter = ParseXml.createParse(ParseChapter.class).parse(chapterFile);
        searchListener.searchFinish(bookInfo);
        //searchFragment.refreshSearchResult(bookInfo);
    }


}
