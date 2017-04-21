package sunday.app.bairead.base;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

/**
 * Created by zhongfei.sun on 2017/4/11.
 */

public interface BaseView<T> {
    void setPresenter(T presenter);
    void showLoading();
    void showToast(@NonNull @StringRes int resId);
}
