package sunday.app.bairead.bookDetail;

import android.content.Context;

import sunday.app.bairead.base.BaiReadApplication;
import sunday.app.bairead.data.BookRepository;
import sunday.app.bairead.data.setting.BookInfo;
import sunday.app.bairead.utils.ActivityUtils;

/**
 * Created by sunday on 2017/3/7.
 */

public class BookDetailPresenter implements BookDetailContract.Presenter{

    private BookDetailContract.View mView;

    public BookDetailPresenter(BookDetailContract.View view){
        mView = view;
    }

    public static void readBook(Context context,BookInfo bookInfo){
        ActivityUtils.readBook(context,bookInfo.bookDetail.getId());
    }

    public static void addToBookCase(Context context,BookInfo bookInfo){
        BookRepository.getInstance(context).addBook(bookInfo);
    }

    public static void cacheBook(BookInfo bookInfo){
        //BookChapterCache.getInstance().downloadAllChpater(bookInfo);
    }

    @Override
    public void start() {

    }
}
