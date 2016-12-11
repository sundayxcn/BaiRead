package sunday.app.bairead.Parse;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;

import sunday.app.bairead.DataBase.BookDetail;
import sunday.app.bairead.Tool.DocumentParse;

/**
 * Created by Administrator on 2016/12/10.
 */

public abstract class HtmlParse {
    public abstract <T> T parse(Document document);
}

class BookDetailParse extends HtmlParse {
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

        BookDetail.Builder builder = new BookDetail.Builder(metaMap)
                .setSourceName(title);
        //builder.setSourceName();

        return builder.build();
    }

}

class BookChapterListParse extends HtmlParse{

    @Override
    public <T> T parse(Document document) {
        return null;
    }
}

class BookTextParse extends HtmlParse{

    @Override
    public <T> T parse(Document document) {
        return null;
    }
}


 class BaiduSearchParse extends DocumentParse {
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

