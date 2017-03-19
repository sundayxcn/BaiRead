package sunday.app.bairead.parse;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by sunday on 2017/3/17.
 * 返回的网站是百度加密的，通过访问网站进入对应的小说目录页，分析Detail得到真正的地址
 *
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
