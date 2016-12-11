package sunday.app.bairead.Tool;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class  SearchHtmlParse extends DocumentParse {
    private ArrayList<String> list = new ArrayList<>();

    @Override
    public ArrayList<String> parse(Document document) {
        String s = "result";
        Elements elements = document.getElementsByClass(s);
        for (Element element : elements) {
            String linkHref = element.select("a[href]").attr("href");
            //SearchLinkInfo searchLink = new SearchLinkInfo();
            //searchLink.sourceName = linkHref;
            list.add(linkHref);
        }
        return list;
    }
}
