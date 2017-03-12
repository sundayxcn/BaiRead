package sunday.app.bairead.parse;

import android.util.Log;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

/**
 * Created by sunday on 2017/1/16.
 */

public class ParseSearch extends ParseXml {
    @Override
    public HashMap<String,String> parse() {

        if(document == null) return null;

        String s = "result-game-item-detail";
        HashMap<String,String> hashMap = new HashMap<>();
        Elements elements = document.getElementsByClass(s);
        for (Element element : elements) {
            String linkHref = element.select("a[href]").attr("href");
            String title = element.select("a[title]").attr("title");
            Log.e("sunday","title="+title);
            hashMap.put(title,linkHref);
        }
        return hashMap;
    }
}
