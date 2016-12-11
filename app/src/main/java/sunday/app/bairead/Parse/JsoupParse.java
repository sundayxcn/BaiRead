package sunday.app.bairead.Parse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import sunday.app.bairead.DataBase.BookDetail;
import sunday.app.bairead.Tool.DocumentParse;

/**
 * Created by sunday on 2016/9/13.
 */


public  class JsoupParse{
    public static final String TAG = "snuday";
    public static final String UTF8 = "UTF-8";
    public static final String GB2312 = "gb2312";


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

}
