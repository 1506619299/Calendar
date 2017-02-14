package calendar.aowang.com.calendar;

import android.content.Context;

import java.text.SimpleDateFormat;

/**
 * Created by asus on 2016/6/5.
 */
public class func {

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    // 获取当月一号
    public static String getCurMonthFirstDay() {
        String curDate = getCurDate();
        String year = curDate.substring(0, curDate.indexOf("-"));
        String month = curDate.substring(curDate.indexOf("-") + 1,
                curDate.lastIndexOf("-"));
        return year + "-" + month + "-01";
    }

    public static String getCurDate() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String curDate = sDateFormat.format(new java.util.Date());
        return curDate;
    }

    // 获取当前时间
    public static String getCurTime() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String curDate = sDateFormat.format(new java.util.Date());
        return curDate;
    }
}
