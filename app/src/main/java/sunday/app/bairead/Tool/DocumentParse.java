package sunday.app.bairead.Tool;

import org.jsoup.nodes.Document;

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
