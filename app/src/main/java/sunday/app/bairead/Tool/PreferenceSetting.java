package sunday.app.bairead.Tool;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by sunday on 2017/3/1.
 */

public class PreferenceSetting {

    /*
    * 阅读文字大小
    * */
    public static final String KEY_TEXT_SIZE = "textSize";
    /*
    * 阅读文字行间空白
    * */
    public static final String KEY_LINE_SIZE = "lineSize";
    /*
     * 章节列表
     * 0：正序
     * 1：逆序
     * */
    public static final String KEY_CHAPTER_ORDER = "chapterOrder";


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

    public int getIntValue(String key,int defaultValue){
        return sharedPreferences.getInt(key,defaultValue);
    }

}
