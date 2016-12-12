package sunday.app.bairead.Parse;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

import sunday.app.bairead.DataBase.BookDetail;

public class BookDetailParse extends HtmlParse {
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
        if(ar.length < 2){
            ar = elements2.get(0).text().split("-");
        }
        String title = ar[ar.length - 1];
        //获取网站名称-end

        BookDetail.Builder builder = new BookDetail.Builder(metaMap);

        return builder.build();
    }

}
