package sunday.app.bairead.Parse;

import org.jsoup.nodes.Document;

/**
 * Created by Administrator on 2016/12/10.
 */

public abstract class HtmlParse {
    public abstract <T> T parse(Document document);
}


