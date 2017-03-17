package sunday.app.bairead.tool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/3/9.
 */

public class TimeFormat {


    public static final int HOUR_TO_MILL =  60 * 60 * 1000;
    public static final int DAY_LENGTH = 10;

    public static String[] timeFormat = {
            "yyyy-MM-dd",
            "yyyy/MM/dd",
            "yyyy-MM-dd hh:mm:ss",
            "yyyy/MM/dd hh:mm:ss",
    };



    /**
     * 获取时间格式
     * */
    private static String getFormatString(String timeString){
            if(timeString.contains("-")){
                if(timeString.length() > DAY_LENGTH){
                    return  "yyyy-MM-dd hh:mm:ss";
                }else{
                    return  "yyyy-MM-dd";
                }
            }else if(timeString.contains("/")){
                if(timeString.length() > DAY_LENGTH){
                    return  "yyyy/MM/dd hh:mm:ss";
                }else{
                    return  "yyyy/MM/dd";
                }
            }else{
                return timeString;
            }
    }


    /**
     * 返回时间戳
     * */
    public static long getStampTime(String timeString){
        String format = getFormatString(timeString);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            Date date = simpleDateFormat.parse(timeString);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 将解析得到的时间字符串转换为更新时间。例：
     * 更新时间 2017-3-8
     * 系统时间 2017-3-9
     * @return 1天前
     * */
    public static String getTimeString(String timeString){
        String string;
        String format = getFormatString(timeString);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            Date date = simpleDateFormat.parse(timeString);
            long timestamp  = date.getTime();
            long currentTime = System.currentTimeMillis();
            long time = currentTime - timestamp;
            int hour = (int) (time / HOUR_TO_MILL);
            int day = hour / 24;
            if(day >=  365){
                string = "1年前";
            }else if(day >= 30){
                string = day / 30 + "月前";
            }else if(day >= 1){
                string = day +"天前";
            }else{
                string  = hour +"小时";
            }
        } catch (ParseException e) {
            e.printStackTrace();
            string = timeString;
        }
        return string;
    }
}
