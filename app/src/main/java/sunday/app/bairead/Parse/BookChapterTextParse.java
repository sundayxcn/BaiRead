package sunday.app.bairead.Parse;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BookChapterTextParse extends HtmlParse{

    @Override
    public String parse(Document document) {
        String s = "content_read";
        Elements elements = document.getElementsByClass(s);
        for (Element element : elements) {
            String idString = element.select("div[id]").attr("id");
            if (idString.equals("content")) {
                String text = element.select("div[id]").get(0).toString();
                return text;
            }
        }
        return null;
    }
}
