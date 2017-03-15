package sunday.app.bairead.parse;

import android.util.Log;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;

import sunday.app.bairead.download.BookDownLoad;

/**
 * Created by sunday on 2017/1/16.
 */

public class ParseSearch extends ParseXml {

    public static final int SPAN_AUTHOR = 1;
    public static final int SPAN_TYPE = 3;
    public static final int SPAN_UPDATE = 5;
    public static final int SPAN_SER = 7;

    @Override
    public ArrayList<BookDownLoad.SearchResult> parse() {

        if(document == null) return null;
        ArrayList<BookDownLoad.SearchResult> searchResults = new ArrayList<>();
        String s = "result-game-item-detail";
        Elements elements = document.getElementsByClass(s);
        for (Element element : elements) {
            String linkHref = element.select("a[href]").attr("href");
            String title = element.select("a[title]").attr("title");
            Elements elements1 = element.select("[class=result-game-item-info]").select("span");
            BookDownLoad.SearchResult searchResult = new BookDownLoad.SearchResult();
            searchResult.title = title;
            searchResult.link = linkHref;
            searchResult.author= elements1.get(SPAN_AUTHOR).text();
            searchResult.type = elements1.get(SPAN_TYPE).text();
            searchResult.updateTime = elements1.get(SPAN_UPDATE).text();
            if(elements1.get(SPAN_SER).text().contains("更新中")) {
                searchResult.serialise = true;
            }
            Log.e("sunday","title="+title);
            searchResults.add(searchResult);
        }

        return searchResults;
    }




}
