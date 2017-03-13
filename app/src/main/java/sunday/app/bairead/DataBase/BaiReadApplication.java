package sunday.app.bairead.database;

import android.app.Application;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by sunday on 2016/12/13.
 */

public class BaiReadApplication extends Application {

    private BookModel bookModel;
    private BookContentProvider bookContentProvider;

    public static final String TECENT_BUGLY_APP_ID = "babf2b978b";
    public static final String ALIBAICHUAN_APP_ID = "23691798";
    public static final String UMENG_APP_ID = "58c554c2aed17906550017a0";
    @Override
    public void onCreate() {
        super.onCreate();
        /**
            第三个参数为SDK调试模式开关，调试模式的行为特性如下：
            输出详细的Bugly SDK的Log；
            每一条Crash都会被立即上报；
            自定义日志将会在Logcat中输出。
            建议在测试阶段建议设置成true，发布时设置为false。
         **/
//        CrashReport.initCrashReport(getApplicationContext(), TECENT_BUGLY_APP_ID, true);
//
//
//        FeedbackAPI.init(this, ALIBAICHUAN_APP_ID);

        bookModel = new BookModel(this);
        //registerReceiver()
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public void setBookContentProvider(BookContentProvider bookContentProvider) {
        this.bookContentProvider = bookContentProvider;
    }

    public BookModel getBookModel() {
        return bookModel;
    }

    public BookContentProvider getBookContentProvider() {
        return bookContentProvider;
    }
}
