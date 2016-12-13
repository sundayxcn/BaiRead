package sunday.app.bairead.Download;

import java.util.HashMap;

import sunday.app.bairead.DataBase.BookChapter;
import sunday.app.bairead.DataBase.BookDetail;
import sunday.app.bairead.DataBase.WebInfo;
import sunday.app.bairead.Parse.BaiduSearchParse;
import sunday.app.bairead.Parse.BookChapterParse;
import sunday.app.bairead.Parse.BookDetailParse;
import sunday.app.bairead.Parse.JsoupParse;
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
    private BookDetail bookDetail;
    private BookChapter bookChapter;

    public SearchManager(SearchFragment fragment){
        searchFragment = fragment;
    }

    public void searchTopWeb(final String name) {
        bookName = name;
        String webName = WebInfo.TOP_WEB[0][0];
        String webLink = WebInfo.TOP_WEB[0][1];
        String webSearchLink = WebInfo.TOP_WEB[0][2];
        WebInfo webInfo = new WebInfo(webName,webLink,webSearchLink);

        String fileDir = FileManager.PATH +"/"+bookName+"/"+SEARCH_DIR;
        FileManager.getInstance().createDir(fileDir);
        String fileName = fileDir + "/"+"search.html";

        connectUrl(webInfo.getLink() + bookName, fileName,new ConnectListener() {
            @Override
            public void start(String url) {

            }

            @Override
            public void end(String fileName) {
                HashMap<String,String> hashMap =  JsoupParse.from(fileName,new BaiduSearchParse());
                String chapterLink = hashMap.get(bookName);
                downloadChapterLink(chapterLink);
            }
        });
    }

    private void downloadChapterLink(String link){
        if(link != null){
            final String chapterFile = FileManager.PATH +"/"+bookName+"/" + "chapter.html";
            connectUrl(link, chapterFile, new ConnectListener() {
                @Override
                public void start(String url) {

                }

                @Override
                public void end(String fileName) {
                    bookDetail = JsoupParse.from(chapterFile,new BookDetailParse());
                    searchFragment.refreshSearchResult(bookDetail);
                    bookChapter  = JsoupParse.from(chapterFile,new BookChapterParse());
                }
            });
        }
    }


    public void debugDetail(String name){
        final String chapterFile = FileManager.PATH +"/"+name+"/" + "chapter.html";
        bookDetail = JsoupParse.from(chapterFile,new BookDetailParse());
        searchFragment.refreshSearchResult(bookDetail);
        bookChapter  = JsoupParse.from(chapterFile,new BookChapterParse());
    }


}
