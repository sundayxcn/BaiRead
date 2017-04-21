package sunday.app.bairead.utils;

import android.support.annotation.IntDef;

/**
 * Created by zhongfei.sun on 2017/4/11.
 */

public class PreferenceKey {

    public static final int ORDER_DEFAULT = 0;
    public static final int ORDER_UPDATE_TIME = 1;
    public static final int ORDER_AUTHOR = 2;
    public static final int ORDER_CHAPTER_COUNT = 3;




    @IntDef({ORDER_DEFAULT,ORDER_UPDATE_TIME,ORDER_AUTHOR,ORDER_CHAPTER_COUNT})
    public @interface KeyInt{
    }

    @KeyInt public static final int CHAPTER_ORDER_DEFAULT = 0;
    @KeyInt public static final int CHAPTER_ORDER_REVERSE = 1;

}
