package sunday.app.bairead.Parse;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 百度站内搜索解析
 * */
public class BaiduSearchParse extends HtmlParse {

   @Override
   public HashMap<String,String> parse(Document document) {
       String s = "result-game-item-detail";
       HashMap<String,String> hashMap = new HashMap<>();
       Elements elements = document.getElementsByClass(s);
       for (Element element : elements) {
           String linkHref = element.select("a[href]").attr("href");
           String title = element.select("a[title]").attr("title");
           hashMap.put(title,linkHref);
           //SearchLinkInfo searchLink = new SearchLinkInfo();
           //searchLink.sourceName = linkHref;
           //list.add(linkHref);
           //if()

           //return linkHref;
       }
       return hashMap;
   }
}
