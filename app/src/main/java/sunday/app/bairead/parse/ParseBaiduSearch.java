package sunday.app.bairead.parse;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by sunday on 2017/3/17.
 */

public class ParseBaiduSearch extends ParseXml {
    @Override
    public ArrayList<String> parse() {
        ArrayList<String> list = new ArrayList<>();
        Elements elements = document.select("[class=result c-container ]");
        for(Element element : elements){
            String linkHref = element.select("a[href]").attr("href");
            list.add(linkHref);
        }

        return list;
    }
}
