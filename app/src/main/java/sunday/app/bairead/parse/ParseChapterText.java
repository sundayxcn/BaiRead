package sunday.app.bairead.parse;

import android.text.Html;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by sunday on 2017/1/17.
 */

public class ParseChapterText extends ParseBase<String> {

    public static final String TYPE_CONTENT_READ = "content_read";
    public static final String TYPE_BOOK_READ = "content";


    public ParseBase<String> from(String fileName) throws IOException {
        return from(fileName,GB2312);
    }

    @Override
    public String parse() {

        if(document == null) return null;
        Elements elements = document.getElementsByClass(TYPE_CONTENT_READ);
        if(elements.size() == 0){
            elements = document.getElementsByClass(TYPE_BOOK_READ);
        }

        for (Element element : elements) {
            String idString = element.select("div[id]").attr("id");
            if (idString.equals("content")) {
                String text = element.select("div[id]").get(0).toString();
                return String.valueOf(Html.fromHtml(text)).trim();
            }
        }
        return null;
    }
}
