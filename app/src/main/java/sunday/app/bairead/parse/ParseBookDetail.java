package sunday.app.bairead.parse;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

import sunday.app.bairead.data.setting.BookDetail;

/**
 * Created by zhongfei.sun on 2017/5/2.
 */

public class ParseBookDetail extends ParseBase<BookDetail>{


    @Override
    public BookDetail parse() {
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

        String readUrl = metaMap.get(BookDetail.Meta.CHAPTER_URL);
        if(readUrl == null || readUrl.length() == 0){
            return  null;
        }else {
            BookDetail.Builder builder = new BookDetail.Builder(metaMap);
            return builder.build();
        }
    }
}
