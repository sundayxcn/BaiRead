package sunday.app.bairead.Download;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import sunday.app.bairead.DataBase.BookDetail;
import sunday.app.bairead.Parse.JsoupParse;
import sunday.app.bairead.Tool.FileManager;
import sunday.app.bairead.Tool.SearchHtmlParse;
import sunday.app.bairead.Tool.SearchInfoParse;
import sunday.app.bairead.UI.SearchFragment;

/**
 * 访问百度搜索书籍
 * Created by sunday on 2016/12/1.
 */
public class SearchManager extends OKhttpManager {

    public static final String SEARCH_DIR = "search";
    public static final String BAIDU = "http://www.baidu.com/s?tn=baiduhome_pg&rn=50&wd=";
    public static final String URL_CUT = "http://www.baidu.com/link?url=";
    private SearchFragment searchFragment;

    public SearchManager(SearchFragment fragment){
        searchFragment = fragment;
    }



    private String bookName = "重生完美时代";

    private String url;

    @Override
    public void connectStart(String url) {
        FileManager.getInstance().createDir(bookName+"/"+SEARCH_DIR);
    }

    //private int index = 100;

    @Override
    public void connectEnd(String url,byte[] body) {
        String cUrl = url.substring(URL_CUT.length()) +".txt";
        String fileName = FileManager.PATH + "/" + FileManager.DIR + "/" + bookName+"/"+SEARCH_DIR + "/"+cUrl;
        FileManager.getInstance().writeByte(fileName,body);
        //String text = new String(body);
        //Spanned spanned = Html.fromHtml(text);
        //TextView textView = new TextView(searchFragment.getActivity());
        //textView.setText(spanned);

    }

    public void downloadEnd(){
        String dirName = FileManager.PATH + "/" + FileManager.DIR + "/" + bookName+"/"+SEARCH_DIR;
        File dir = new File(dirName);
        File[] files = dir.listFiles();
        final SearchInfoParse searchInfoParse = new SearchInfoParse();
        for(final File file : files){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    BookDetail bookDetail = JsoupParse.from(file,searchInfoParse);
                    if(bookDetail.isValid()) {

                        searchFragment.refreshSearchResult(bookDetail);
                    }
                }
            }).start();

        }

    }


    public void search(final String name) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                bookName = name;
                try {
                    bookName = name;
                    Document document = Jsoup.connect(BAIDU + bookName).get();
                    SearchHtmlParse searchHtmlParse = new SearchHtmlParse();
                    ArrayList<String> searchLinkList = searchHtmlParse.parse(document);
                    for(String link:searchLinkList){
                        connectUrl(link);
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }finally {
                    downloadEnd();
                }
            }
        }).start();

    }

    public void searchTopWeb(final String name) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                bookName = name;
                try {
                    bookName = name;
                    Document document = Jsoup.connect(BAIDU + bookName).get();
                    SearchHtmlParse searchHtmlParse = new SearchHtmlParse();
                    ArrayList<String> searchLinkList = searchHtmlParse.parse(document);
                    for(String link:searchLinkList){
                        connectUrl(link);
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }finally {
                    downloadEnd();
                }
            }
        }).start();

    }

}
