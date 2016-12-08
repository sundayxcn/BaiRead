package sunday.app.bairead.Tool;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import sunday.app.bairead.DataBase.BookDetail;

/**
 * Created by sunday on 2016/9/13.
 */


public  class JsoupParse{
    public static final String TAG = "snuday";
    public static final String UTF8 = "UTF-8";
    public static final String GB2312 = "gb2312";

//    protected ExecutorService mExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
//
//    protected Document mDocument;
//
//    public ArrayList<SearchLinkInfo> loadSearchHtml(String fileName){
//        return new ArrayList<>();
//    }

//    public interface ParseListener{
//        void ParseOver(<T> T);
//    }
//
//
//    private ParseListener parseListener;
//
//    public void setParseListener(ParseListener parseListener){
//        this.parseListener = parseListener;
//    }

    public static <T> T from(String fileName, DocumentParse documentParse) {
        try {
            File input = new File(fileName);
            Document mDocument = Jsoup.parse(input, GB2312);
            T data = documentParse.parse(mDocument);
            return data;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T from(File file, DocumentParse documentParse) {
        try {
            Document mDocument = Jsoup.parse(file, GB2312);
            T data = documentParse.parse(mDocument);
            return data;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


//    public static ArrayList<SearchLinkInfo> parseSearchHtml(String dirPath){
//
//        ArrayList<SearchLinkInfo> searchLinkInfos = new ArrayList();
//        try {
//            File fileDir = new File(dirPath);
//            File[] files = fileDir.listFiles();
//            HashMap<String ,String> metaMap = new HashMap<>();
//
//            for(File file : files) {
//                Document document = Jsoup.parse(file, GB2312);
//                Elements elements =  document.select("meta");
//                for(Element element : elements){
//                    Elements es1 = element.getElementsByAttribute("property");
//                    Elements es2 = element.getElementsByAttribute("content");
//                    boolean empty = es1.isEmpty() || es2.isEmpty() ;
//                    if(!empty) {
//                        String key = es1.attr("property");
//                        String value = es2.attr("content");
//                        metaMap.put(key,value);
//                    }
//                }
//
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            //return null;
//        }
//
//        return searchLinkInfos;
//    }


    public void parseEnd(){

    }



//    public Document loadCurrentHtml(String web,String format) {
//        try {
//            File input = new File(FileManager.FileName);
//            mDocument = Jsoup.parse(input, GB2312, web);
//            return mDocument;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }




}
