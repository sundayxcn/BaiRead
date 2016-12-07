package sunday.app.bairead.Tool;

import android.util.Log;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by sunday on 2016/12/6.
 */

public abstract class DocumentParse {
    public abstract void parse(Document document);
}

 class  SearchHtmlParse extends DocumentParse{
     private ArrayList<SearchLink> list = new ArrayList<>();

     @Override
     public void parse(Document document) {
         //document.baseUri();
         String s = "result";
         Elements elements = null;
         elements = document.getElementsByClass(s);
         for(Element element : elements) {
             String linkHref = element.select("a[href]").attr("href");
             list.add(new SearchLink(linkHref));
         }
     }

     public ArrayList<SearchLink> result(){
         return list;
     }

 }
//    private ArrayList<SearchManager.SearchLink> list = new ArrayList<>();
//
//     @Override
//     public void parse(String fileName) {
//
//     }
//
//     public ArrayList<SearchManager.SearchLink> result(){
//         return list;
//     }
// }
