package sunday.app.bairead.Tool;

import android.util.Log;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;

import sunday.app.bairead.DataBase.BookDetail;

/**
 * Created by sunday on 2016/12/6.
 */

public abstract class DocumentParse {
    public abstract <T> T parse(Document document);
}


