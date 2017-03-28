package sunday.app.bairead.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by sunday on 2017/3/1.
 */

public class PreferenceSetting {


    public static final String KEY_FIRST_RUN = "firstRun";

    public static final String KEY_FIRST_READ = "firstRead";
    /*
    * 阅读文字大小
    * */
    public static final String KEY_TEXT_SIZE = "textSize";
    /*
    * 阅读文字行间空白
    * */
    public static final String KEY_LINE_SIZE = "lineSize";
    /*
    * 左右边距空白
    * */
    public static final String KEY_MARGIN_SIZE = "marginSize";
    /*
     * 章节列表
     * 0：正序
     * 1：逆序
     * */
    public static final String KEY_CHAPTER_ORDER = "chapterOrder";

    public static final String KEY_CASE_LIST_ORDER = "bookCaseOrder";

    public static final int KEY_VALUE_CASE_LIST_ORDER_DEFAULT = 0;

    public static final int KEY_VALUE_CASE_LIST_ORDER_UPDATE_TIME = KEY_VALUE_CASE_LIST_ORDER_DEFAULT + 1;

    public static final int KEY_VALUE_CASE_LIST_ORDER_AUTHOR = KEY_VALUE_CASE_LIST_ORDER_UPDATE_TIME + 1;

    public static final int KEY_VALUE_CASE_LIST_ORDER_CHAPTER_COUNT = KEY_VALUE_CASE_LIST_ORDER_AUTHOR + 1;


    public static SharedPreferences sharedPreferences;

    public static PreferenceSetting getInstance(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return new PreferenceSetting(sharedPreferences);
    }

    PreferenceSetting(SharedPreferences sharedPrefer){
        sharedPreferences = sharedPrefer;
    }

    public void putIntValue(String key,int value){
        sharedPreferences.edit().putInt(key,value).commit();
    }

    public void putBooleanValue(String key,boolean value){
        sharedPreferences.edit().putBoolean(key,value).commit();
    }


    public int getIntValue(String key,int defaultValue){
        return sharedPreferences.getInt(key,defaultValue);
    }

    public int getIntValue(String key){
        return sharedPreferences.getInt(key,0);
    }


    public  boolean isFirstRun(){
        return sharedPreferences.getBoolean(KEY_FIRST_RUN,true);
    }

    public  boolean isFirstRead(){
        return sharedPreferences.getBoolean(KEY_FIRST_READ,true);
    }

    public  void setFirstRunFalse(){
        sharedPreferences.edit().putBoolean(KEY_FIRST_RUN,false).commit();
    }

    public void clear(){
        sharedPreferences.edit().clear().commit();
    }

}
