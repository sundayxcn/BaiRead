package sunday.app.bairead.bookRead;

import android.content.Context;

import sunday.app.bairead.bookRead.cache.BookReadSize;
import sunday.app.bairead.utils.PreferenceKey;
import sunday.app.bairead.utils.PreferenceSetting;

/**
 * Created by zhongfei.sun on 2017/5/23.
 */

public class BookReadSizeSetting implements BookReadContract.ReadSetting {
    private PreferenceSetting mPreferenceSetting;


    BookReadSizeSetting(Context context){
        mPreferenceSetting = PreferenceSetting.getInstance(context);
    }


    @Override
    public BookReadSize getReadSize() {
        int textSize = mPreferenceSetting.getIntValue(PreferenceSetting.KEY_TEXT_SIZE, 45);
        int lineSize = mPreferenceSetting.getIntValue(PreferenceSetting.KEY_LINE_SIZE, 45);
        int marginSize = mPreferenceSetting.getIntValue(PreferenceSetting.KEY_MARGIN_SIZE, 20);
        return new BookReadSize(textSize,lineSize,marginSize);
    }

    @Override
    public void setReadSize(BookReadSize bookReadSize) {
        mPreferenceSetting.putIntValue(PreferenceSetting.KEY_TEXT_SIZE, bookReadSize.textSize);
        mPreferenceSetting.putIntValue(PreferenceSetting.KEY_LINE_SIZE, bookReadSize.lineSize);
        mPreferenceSetting.putIntValue(PreferenceSetting.KEY_MARGIN_SIZE, bookReadSize.marginSize);
    }

    @Override
    public boolean isDefaultChapterOrder() {
        int order = mPreferenceSetting.getIntValue(PreferenceSetting.KEY_CHAPTER_ORDER, PreferenceKey.CHAPTER_ORDER_DEFAULT);
        return order == PreferenceKey.CHAPTER_ORDER_DEFAULT;
    }

    @Override
    public void changeChapterOrder(boolean defaultOrder) {
        if (defaultOrder) {
            mPreferenceSetting.putIntValue(PreferenceSetting.KEY_CHAPTER_ORDER, PreferenceKey.CHAPTER_ORDER_DEFAULT);
        } else {
            mPreferenceSetting.putIntValue(PreferenceSetting.KEY_CHAPTER_ORDER, PreferenceKey.CHAPTER_ORDER_REVERSE);
        }
    }

}
