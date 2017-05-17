package sunday.app.bairead.utils;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import sunday.app.bairead.bookDetail.BookDetailActivity;
import sunday.app.bairead.bookRead.BookReadActivity;
import sunday.app.bairead.bookRead.BookReadContract;
import sunday.app.bairead.data.setting.BookInfo;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by zhongfei.sun on 2017/4/11.
 */

public class ActivityUtils {


    public static void addFragmentToActivity(@NonNull FragmentManager fragmentManager,
                                             @NonNull Fragment fragment,
                                             int fragmentId){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(fragmentId,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public static void readBook(Context context, long bookId){
        Intent intent = new Intent();
        intent.setClass(context.getApplicationContext(), BookReadActivity.class);
        intent.putExtra(BookReadContract.READ_EXTRA_ID, bookId);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void gotoBookDetail(Context context, BookInfo bookInfo){
        Temp.getInstance().setBookInfo(bookInfo);
        Intent intent = new Intent();
        intent.setClass(context, BookDetailActivity.class);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
