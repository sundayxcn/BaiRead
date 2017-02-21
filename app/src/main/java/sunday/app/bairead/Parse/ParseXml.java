package sunday.app.bairead.Parse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;

/**
 * Created by sunday on 2017/1/16.
 */

public abstract class ParseXml {

    enum Charset{
        UTF8,
        GB2312,
        GBK;
        @Override
        public String toString() {
            if (this == UTF8) {
                return "UTF-8";
            } else if (this == GB2312) {
                return "gb2312";
            } else if(this == GBK){
                return "gbk";
            }
            return null;
        }
    }

    public abstract <T> T parse(String fileName);

    public Document getDocument(String fileName){
        try {
            File input = new File(fileName);
            return Jsoup.parse(input, Charset.UTF8.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Document getDocument(String fileName,Charset charset){
        try {
            File input = new File(fileName);
            return Jsoup.parse(input, charset.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static <T extends ParseXml> T createParse(Class<T> clz){
        ParseXml parseXml = null;
        try{
            parseXml = (ParseXml) Class.forName(clz.getName()).newInstance();
        }catch (Exception e){
            e.printStackTrace();
        }

        return  (T) parseXml;
    }


}
