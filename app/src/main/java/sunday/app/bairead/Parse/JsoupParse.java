package sunday.app.bairead.Parse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;

/**
 * Created by sunday on 2016/9/13.
 */


public  class JsoupParse{
    public static final String TAG = "snuday";


    enum Charset{
        UTF8,
        GB2312;

        @Override
        public String toString() {
            if (this == UTF8) {
                return "UTF-8";
            } else if (this == GB2312) {
                return "gb2312";
            }
            return null;
        }
    }


    public static <T> T from(String fileName, HtmlParse documentParse) {
        try {
            File input = new File(fileName);
            Document mDocument = Jsoup.parse(input, Charset.UTF8.toString());
            T data = documentParse.parse(mDocument);
            return data;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T from(File file, HtmlParse documentParse) {
        try {
            Document mDocument = Jsoup.parse(file, Charset.UTF8.toString());
            T data = documentParse.parse(mDocument);
            return data;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
