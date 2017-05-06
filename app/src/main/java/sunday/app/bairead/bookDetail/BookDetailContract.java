package sunday.app.bairead.bookDetail;


import android.content.Context;

import sunday.app.bairead.base.BasePresenter;
import sunday.app.bairead.base.BaseView;
import sunday.app.bairead.data.setting.BookInfo;

/**
 * Created by Administrator on 2017/5/5.
 */

public class BookDetailContract {

    interface View extends BaseView<Presenter>{
        void disableCase();
    }

    interface Presenter extends BasePresenter{
        void readBook(Context context, BookInfo bookInfo);
        void addToCase(Context context,BookInfo bookInfo);
        void cacheBook(Context context,BookInfo bookInfo);
    }
}
