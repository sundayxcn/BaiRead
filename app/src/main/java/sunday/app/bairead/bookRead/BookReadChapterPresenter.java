package sunday.app.bairead.bookRead;

import android.support.annotation.NonNull;

import sunday.app.bairead.data.setting.BookInfo;

/**
 * Created by zhongfei.sun on 2017/6/2.
 */

public class BookReadChapterPresenter implements BookReadContract.ChapterPresenter {

    private BookReadContract.ReadSetting mReadSetting;
    private BookReadContract.ChapterView mView;
    private BookInfo mBookInfo;
    public BookReadChapterPresenter(@NonNull BookReadContract.ReadSetting readSetting,
                                    @NonNull BookReadContract.ChapterView view,
                                    @NonNull BookInfo bookInfo
                                    ){
        mReadSetting = readSetting;
        mView = view;
        mBookInfo = bookInfo;
        mView.setPresenter(this);
    }

    @Override
    public void start() {
        mView.showOrder(mReadSetting.isDefaultChapterOrder());
        mView.showChapter(mBookInfo);
    }

    @Override
    public void changeOrder() {
        boolean isDefault =  mReadSetting.isDefaultChapterOrder();
        mReadSetting.changeChapterOrder(!isDefault);
        mView.showOrder(mReadSetting.isDefaultChapterOrder());
    }

    @Override
    public void setChapterIndex(int index) {

    }
}
