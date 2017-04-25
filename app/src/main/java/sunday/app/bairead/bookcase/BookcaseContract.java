package sunday.app.bairead.bookcase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sunday.app.bairead.base.BaseView;
import sunday.app.bairead.base.BookOperatorPresenter;
import sunday.app.bairead.data.setting.BookInfo;

/**
 * Created by zhongfei.sun on 2017/4/11.
 */

public interface BookcaseContract {

    interface View extends BaseView<Presenter>{

        void showLoadingError();

        void showBooks(List<BookInfo> list);

        void showNoBooks();

        void showUpdateBook(List<BookInfo> list);

        void refresh(BookInfo bookInfo);
    }

    interface Presenter extends BookOperatorPresenter{

        void loadBooks(boolean refresh);

        void updateBooks(List<BookInfo> list);

        void updateBook(long id);

        void orderBooks(List<BookInfo> list);

        void topBooks(Map<Long,Boolean> map);
    }
}
