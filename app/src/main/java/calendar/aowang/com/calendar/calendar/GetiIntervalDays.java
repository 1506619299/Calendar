/*
 * 计算两个时间间隔天数
 * 传入两个时间参数
 * 转换统一时间格式
 * 转换为毫秒，计算差并转换为天数
 */
package calendar.aowang.com.calendar.calendar;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class GetiIntervalDays {
    private String startTime;
    private String endTime;

    public GetiIntervalDays(String startTime, String endTime) {
        super();
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public GetiIntervalDays() {

    }

    @SuppressLint("SimpleDateFormat")
    public double getIntervalDays() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = null;
        Date date2 = null;
        try {
            date1 = sDateFormat.parse(startTime);
            date2 = sDateFormat.parse(endTime);
            GregorianCalendar cal1 = new GregorianCalendar();

            GregorianCalendar cal2 = new GregorianCalendar();

            cal1.setTime(date1);

            cal2.setTime(date2);
            //将毫秒转换为天数，这里因为只保留到天，所以转换后应该也是Int型，不过没影响了
            double dayCount = (cal2.getTimeInMillis() - cal1.getTimeInMillis()) / (1000 * 3600 * 24);

            return dayCount;

        } catch (ParseException e) {

            e.printStackTrace();

        }
        return 0;

    }

}
