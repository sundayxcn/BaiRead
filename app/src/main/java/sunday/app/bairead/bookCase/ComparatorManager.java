package sunday.app.bairead.bookCase;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import sunday.app.bairead.R;
import sunday.app.bairead.database.BookInfo;
import sunday.app.bairead.utils.PreferenceSetting;
import sunday.app.bairead.utils.TimeFormat;

/**
 * Created by Administrator on 2017/3/27.
 */

public class ComparatorManager {


    class ComparatorPref{
        int prefValue;
        Comparator<BookInfo> comparator;
        ComparatorPref(int prefValue,Comparator<BookInfo> comparator){
            this.prefValue = prefValue;
            this.comparator = comparator;
        }
    }

    private HashMap<Integer,ComparatorPref> hashMap;
    public ComparatorManager(){
        hashMap = new HashMap<>();
        hashMap.put(R.id.action_order_add_book,new ComparatorPref(PreferenceSetting.KEY_VALUE_CASE_LIST_ORDER_DEFAULT,comparatorDefault));
        hashMap.put(R.id.action_order_update_time,new ComparatorPref(PreferenceSetting.KEY_VALUE_CASE_LIST_ORDER_UPDATE_TIME,comparatorUpdateTime));
        hashMap.put(R.id.action_order_chapter_count,new ComparatorPref(PreferenceSetting.KEY_VALUE_CASE_LIST_ORDER_CHAPTER_COUNT,comparatorChapterCount));
        hashMap.put(R.id.action_order_author,new ComparatorPref(PreferenceSetting.KEY_VALUE_CASE_LIST_ORDER_AUTHOR,comparatorAuthor));
    }


    public int getOrder(int menuId){
        try {
            return hashMap.get(menuId).prefValue;
        }catch (Exception e){
            e.printStackTrace();
            return PreferenceSetting.KEY_VALUE_CASE_LIST_ORDER_DEFAULT;
        }
    }

    public Comparator<BookInfo> getComparator(int order){
        Iterator iter = hashMap.entrySet().iterator();
        while (iter.hasNext()) {
            ComparatorPref comparatorPref = (ComparatorPref) iter.next();
            if(comparatorPref.prefValue == order){
                return comparatorPref.comparator;
            }
        }
        return comparatorDefault;
    }


    private Comparator<BookInfo> comparatorDefault = new java.util.Comparator<BookInfo>() {
        @Override
        public int compare(BookInfo a, BookInfo b) {
            if (a.bookDetail.topCase == b.bookDetail.topCase) {
                return a.bookDetail.getId() < b.bookDetail.getId() ? -1 : 1;
            } else if (a.bookDetail.topCase) {
                return -1;
            } else {
                return 1;
            }

        }
    };
    private Comparator<BookInfo> comparatorUpdateTime = new java.util.Comparator<BookInfo>() {
        @Override
        public int compare(BookInfo a, BookInfo b) {

            if (a.bookDetail.topCase == b.bookDetail.topCase) {
                long aTime = TimeFormat.getStampTime(a.bookDetail.getUpdateTime());
                long bTime = TimeFormat.getStampTime(b.bookDetail.getUpdateTime());
                return aTime > bTime ? -1 : 1;
            } else if (a.bookDetail.topCase) {
                return -1;
            } else {
                return 1;
            }
        }
    };
    private Comparator<BookInfo> comparatorChapterCount = new java.util.Comparator<BookInfo>() {
        @Override
        public int compare(BookInfo a, BookInfo b) {

            if (a.bookDetail.topCase == b.bookDetail.topCase) {
                return a.bookChapter.getChapterCount() > b.bookChapter.getChapterCount() ? -1 : 1;
            } else if (a.bookDetail.topCase) {
                return -1;
            } else {
                return 1;
            }
        }
    };
    private Comparator<BookInfo> comparatorAuthor = new java.util.Comparator<BookInfo>() {
        @Override
        public int compare(BookInfo a, BookInfo b) {
            if (a.bookDetail.topCase == b.bookDetail.topCase) {
                return a.bookDetail.getAuthor().compareTo(b.bookDetail.getAuthor());
            } else if (a.bookDetail.topCase) {
                return -1;
            } else {
                return 1;
            }
        }
    };

}
