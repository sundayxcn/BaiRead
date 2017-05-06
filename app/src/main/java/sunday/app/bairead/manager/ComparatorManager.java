package sunday.app.bairead.manager;


import java.util.Comparator;

import sunday.app.bairead.data.setting.BookInfo;
import sunday.app.bairead.utils.PreferenceKey;
import sunday.app.bairead.utils.TimeFormat;

/**
 * Created by Administrator on 2017/3/27.
 */

public class ComparatorManager {
    public static Comparator<BookInfo> getComparator(@PreferenceKey.KeyInt int order) {
        if(order == PreferenceKey.ORDER_UPDATE_TIME){
            return new Comparator<BookInfo>() {
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
        }else if(order == PreferenceKey.ORDER_AUTHOR){
            return new Comparator<BookInfo>() {
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
        }else if(order == PreferenceKey.ORDER_CHAPTER_COUNT){
            return new Comparator<BookInfo>() {
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
        }else{
            return new Comparator<BookInfo>() {
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
        }

    }
}
