package sunday.app.bairead.bookSearch;

import android.content.Context;

import java.util.List;

import sunday.app.bairead.base.BasePresenter;
import sunday.app.bairead.base.BaseView;
import sunday.app.bairead.data.setting.BookInfo;

/**
 * Created by zhongfei.sun on 2017/5/5.
 */

public class BookSearchContract {
    interface View extends BaseView<Present>{
        void showHistory(List<String> titleList);
        void showResult(BookInfo bookInfo);
        void clearSearch();
    }

    interface Present extends BasePresenter{
        void search(String book);
        void addHistory(String title);
        void clearHistory();
        void stop();
        void goBookDetail(Context context,BookInfo bookInfo);

    }
}
