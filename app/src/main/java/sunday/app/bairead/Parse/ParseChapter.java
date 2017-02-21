package sunday.app.bairead.Parse;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import sunday.app.bairead.DataBase.BookChapter;
import sunday.app.bairead.DataBase.BookDetail;

/**
 * Created by sunday on 2017/1/16.
 */

public class ParseChapter extends ParseXml {
    @Override
    public BookChapter parse(String fileName) {
        Document document = getDocument(fileName,Charset.GB2312);

        if(document == null) return null;

        final String linkHead = getChapterLink(document);
        Elements elements =  document.select("dd");

        ArrayList<BookChapter.Chapter> list = new ArrayList<>();
        for(Element element : elements){
            String linkHref = element.select("a[href]").attr("href");
            String chapterTitle = element.select("a[href]").get(0).text();
            String[] cs = linkHref.split("/|\\.");
            String s = cs[cs.length-2];
            list.add(new BookChapter.Chapter(linkHead,Long.valueOf(s),chapterTitle));
        }
        sortAndRemoveDuplicate(list);
        BookChapter.Builder builder = new BookChapter.Builder().
                setChapterCount(list.size()).
                setChapterLink(linkHead)
                .setCurrent(true);
        BookChapter bookChapter = builder.build();
        bookChapter.setChapterList(list);
        return bookChapter;
    }


    private String getChapterLink(Document document){

        //获取meta标签属性-start
        Elements elements0 =  document.select("meta");
        for(Element element : elements0){
            Elements es1 = element.getElementsByAttribute("property");
            Elements es2 = element.getElementsByAttribute("content");
            boolean empty = es1.isEmpty() || es2.isEmpty() ;
            if(!empty) {
                String key = es1.attr("property");
                String value = es2.attr("content");
                if(key.equals(BookDetail.Meta.CHAPTER_URL)){
                    return value;
                }
            }
        }
        return null;
    }

    private void sortAndRemoveDuplicate(ArrayList<BookChapter.Chapter> list){
        Collections.sort(list, new Comparator<BookChapter.Chapter>() {
            @Override
            public int compare(BookChapter.Chapter o1, BookChapter.Chapter o2) {
                if(o1.getNum() < o2.getNum()){
                    return -1;
                }else{
                    return 1;
                }
            }
        });

        for (int i = 0; i < list.size() - 1; i++) {
            if (list.get(i).getNum() == list.get(i+1).getNum()) {
                list.remove(i);
                i--;
            }
        }
    }
}
