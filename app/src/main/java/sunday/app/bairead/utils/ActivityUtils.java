package sunday.app.bairead.utils;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.annotation.NonNull;

/**
 * Created by zhongfei.sun on 2017/4/11.
 */

public class ActivityUtils {


    public static void addFragmentToActivity(@NonNull FragmentManager fragmentManager,
                                             @NonNull Fragment fragment,
                                             int fragmentId){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(fragmentId,fragment);
        fragmentTransaction.commit();
    }

}
