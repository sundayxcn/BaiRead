package sunday.app.bairead.Tool;

import android.util.Log;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;

import sunday.app.bairead.DataBase.BookDetail;

/**
 * Created by sunday on 2016/12/6.
 */

public abstract class DocumentParse {
    public abstract <T> T parse(Document document);
}

 class  SearchHtmlParse extends DocumentParse {
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



 class SearchInfoParse extends DocumentParse {
    //private ArrayList<SearchLinkInfo> list = new ArrayList<>();
    @Override
    public BookDetail parse(Document document) {
        //获取meta标签属性-start
        HashMap<String ,String> metaMap = new HashMap<>();
        Elements elements =  document.select("meta");
        for(Element element : elements){
            Elements es1 = element.getElementsByAttribute("property");
            Elements es2 = element.getElementsByAttribute("content");
            boolean empty = es1.isEmpty() || es2.isEmpty() ;
            if(!empty) {
                String key = es1.attr("property");
                String value = es2.attr("content");
                metaMap.put(key,value);
            }
        }
        //获取meta标签属性-end
        //获取网站名称-start
        Elements elements2 = document.select("title");
        String[] ar = elements2.get(0).text().split("_");
        String title = ar[ar.length - 1];
        //获取网站名称-end

        BookDetail.Builder builder = new BookDetail.Builder(metaMap)
                .setSourceName(title);
        //builder.setSourceName();

        return builder.build();
    }

}
