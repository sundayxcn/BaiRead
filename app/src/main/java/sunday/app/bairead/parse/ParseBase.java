package sunday.app.bairead.parse;

import android.support.annotation.NonNull;
import android.support.annotation.StringDef;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by zhongfei.sun on 2017/5/3.
 */

public abstract class ParseBase<T> {
    public static final String UTF8 = "UTF-8";
    public static final String GB2312 = "gb2312";
    public static final String GBK = "gbk";
    protected Document document;

    public ParseBase<T> from(String fileName, @Charset String charset) throws IOException {
        File file = new File(fileName);
        document = Jsoup.parse(file, charset);
        return this;
    }

    public ParseBase<T> from(String fileName) throws IOException {
        File file = new File(fileName);
        String charset = getCharset(file);
        return from(fileName, charset);
    }

    public abstract T parse();

    private String getCharset(File file) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader buffReader = new BufferedReader(reader);
            String str;
            while ((str = buffReader.readLine()) != null) {
                String charset = getCharset(str);
                if (charset.length() > 0) {
                    return charset;
                }
            }

        } catch (FileNotFoundException e) {
            return GBK;
        } catch (IOException e) {
            return GBK;
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return UTF8;
    }

    private String getCharset(@NonNull String string) {
        if (string.contains("Content-Type")) {
            if (string.contains(GBK)) {
                return GBK;
            } else if (string.contains(GB2312)) {
                return GB2312;
            } else {
                return UTF8;
            }
        } else {
            return "";
        }

    }

    @StringDef({UTF8, GB2312, GBK})
    public @interface Charset {

    }

}
