package sunday.app.bairead.parse;

import android.support.annotation.StringDef;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;

/**
 * Created by zhongfei.sun on 2017/5/3.
 */

public abstract class ParseBase<T> {
     public static final String UTF8 = "UTF-8";
     public static final String GB2312 = "gb2312";
     public static final String GBK = "gbk";

    @StringDef({UTF8,GB2312,GBK})
    public @interface Charset{

    }

    protected Document document;

    public ParseBase<T> from(String fileName,@Charset String charset) throws IOException{
        File file = new File(fileName);
        document =  Jsoup.parse(file, charset);
        return this;
    }

    public ParseBase<T> from(String fileName) throws IOException{
        return from(fileName,UTF8);
    }

    public abstract T parse();

}
