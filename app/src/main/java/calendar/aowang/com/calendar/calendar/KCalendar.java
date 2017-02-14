package calendar.aowang.com.calendar.calendar;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import calendar.aowang.com.calendar.R;
import calendar.aowang.com.calendar.func;

/**
 * 日历控件
 *
 * @author huangyin
 */
@SuppressWarnings("deprecation")
public class KCalendar extends ViewFlipper implements
        GestureDetector.OnGestureListener {

    public static final int START = 1;
    public static final int END = 2;

    private int direction = START;

    public static final int NOMAL = 0;//普通显示模式
    public static final int WEEK = 1; //显示周次

    public static final int DOUBLE = 2;//选择一个区间
    public static final int SINGLE = 3;//选择单个日期
    public static final int COLOR_BG_WEEK_TITLE = Color.parseColor("#ffeeeeee"); // 星期标题背景颜色
    public static final int COLOR_TX_WEEK_TITLE = Color.parseColor("#ffcc3333"); // 星期标题文字颜色
    public static final int COLOR_TX_THIS_MONTH_DAY = Color
            .parseColor("#aa564b4b"); // 当前月日历数字颜色
    public static final int COLOR_TX_OTHER_MONTH_DAY = Color
            .parseColor("#ffcccccc"); // 其他月日历数字颜色
    public static final int COLOR_TX_THIS_DAY = Color.parseColor("#ff008000"); // 当天日历数字颜色
    public static final int COLOR_BG_THIS_DAY = Color.parseColor("#ffcccccc"); // 当天日历背景颜色
    public static final int COLOR_BG_CALENDAR = Color.parseColor("#ffeeeeee"); // 日历背景色
    RelativeLayout col;// 日历上的列
    private int selectModel = SINGLE;//日期选择模式，默认只选择一个，（DOUBLE表示选择去区间）
    private int showModel = NOMAL; //日期显示模式
    private GestureDetector gd; // 手势监听器
    private Animation push_left_in; // 动画-左进
    private Animation push_left_out; // 动画-左出
    private Animation push_right_in; // 动画-右进
    private Animation push_right_out; // 动画-右出
    private final int ROWS_TOTAL = 6; // 日历的行数
    private int COLS_TOTAL = 7; // 日历的列数
    private String[][] dates = new String[6][7]; // 当前日历日期
    private float tb;
    private OnCalendarClickListener onCalendarClickListener; // 日历点击回调
    private OnCalendarDateChangedListener onCalendarDateChangedListener; // 日历翻页回调
    private OnWeekClickListener onWeekClickListener; // 周次点击回调
    private String[] weekday = new String[]{"一", "二", "三", "四", "五", "六", "日"}; // 星期标题
    private ArrayList<Integer> weekSelectList = new ArrayList<>();//年份+周次 例如：201621  2016年21周
    private String[] dateFromTo = new String[2];
    private int calendarYear; // 日历年份
    private int calendarMonth; // 日历月份
    private Date thisday = new Date(); // 今天
    private Date calendarday; // 日历这个月第一天(1号)
    private LinearLayout firstCalendar; // 第一个日历
    private LinearLayout secondCalendar; // 第二个日历
    private LinearLayout currentCalendar; // 当前显示的日历
    private Map<String, Integer> marksMap = new HashMap<String, Integer>(); // 储存某个日子被标注(Integer
    // 为bitmap
    // res
    // id)
    private Map<String, Integer> dayBgColorMap = new HashMap<String, Integer>(); // 储存某个日子的背景色
    private String clickDate;

    public KCalendar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public KCalendar(Context context) {
        super(context);
        init();
    }

    /**
     * 构造函数
     *
     * @param context 上下文
     * @param model   显示模式，正常或者显示周次, 当显示周次的时候，日期选择模式必须是选择区间
     */
//    public KCalendar(Context context, int model) {
//        this(context,model,START);
////        if (model == WEEK) {
////            this.showModel = model;
////            this.selectModel = DOUBLE;
////            weekday = new String[]{"周次","一", "二", "三", "四", "五", "六", "日"}; // 星期标题
////            COLS_TOTAL = 8;// 日历的列数
////        } else if (model == DOUBLE) {
////            this.selectModel = DOUBLE;
////        }
////        init();
//    }

    public KCalendar(Context context, int model, int direction) {
        super(context);
        this.direction = direction;
        if (model == WEEK) {
            this.showModel = model;
            this.selectModel = DOUBLE;
            weekday = new String[]{"周次","一", "二", "三", "四", "五", "六", "日"}; // 星期标题
            COLS_TOTAL = 8;// 日历的列数
        } else if (model == DOUBLE) {
            this.selectModel = DOUBLE;
        }
        init();
    }

    // 2或4
    private static String addZero(int i, int count) {
        if (count == 2) {
            if (i < 10) {
                return "0" + i;
            }
        } else if (count == 4) {
            if (i < 10) {
                return "000" + i;
            } else if (i < 100 && i > 10) {
                return "00" + i;
            } else if (i < 1000 && i > 100) {
                return "0" + i;
            }
        }
        return "" + i;
    }

//    /**
//     * 获得指定日期的前一天
//     *
//     * @param specifiedDay
//     * @return
//     * @throws Exception
//     */
//    public static String getSpecifiedDayBefore(String specifiedDay) {
//        Calendar c = Calendar.getInstance();
//        Date date = null;
//        try {
//            date = new SimpleDateFormat("yy-MM-dd").parse(specifiedDay);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        c.setTime(date);
//        int day = c.get(Calendar.DATE);
//        c.set(Calendar.DATE, day - 1);
//
//        String dayBefore = new SimpleDateFormat("yyyy-MM-dd").format(c
//                .getTime());
//        return dayBefore;
//    }

    private void init() {
        setBackgroundColor(COLOR_BG_CALENDAR);
        // 实例化收拾监听器
        gd = new GestureDetector(this);
        // 初始化日历翻动动画
        push_left_in = AnimationUtils.loadAnimation(getContext(),
                R.anim.push_left_in_pigmanager);
        push_left_out = AnimationUtils.loadAnimation(getContext(),
                R.anim.push_left_out_pigmanager);
        push_right_in = AnimationUtils.loadAnimation(getContext(),
                R.anim.push_right_in_pigmanager);
        push_right_out = AnimationUtils.loadAnimation(getContext(),
                R.anim.push_right_out_pigmanager);
        push_left_in.setDuration(400);
        push_left_out.setDuration(400);
        push_right_in.setDuration(400);
        push_right_out.setDuration(400);
        // 初始化第一个日历
        firstCalendar = new LinearLayout(getContext());
        firstCalendar.setOrientation(LinearLayout.VERTICAL);
        firstCalendar.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
        // 初始化第二个日历
        secondCalendar = new LinearLayout(getContext());
        secondCalendar.setOrientation(LinearLayout.VERTICAL);
        secondCalendar.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
        // 设置默认日历为第一个日历
        currentCalendar = firstCalendar;
        // 加入ViewFlipper
        addView(firstCalendar);
        addView(secondCalendar);
        // 设置日历上的日子(1号)
        calendarYear = thisday.getYear() + 1900;
        calendarMonth = thisday.getMonth();
        calendarday = new Date(calendarYear - 1900, calendarMonth, 1);
        // 绘制线条框架
        drawFrame(firstCalendar);
        drawFrame(secondCalendar);
        // 填充展示日历
        setCalendarNum();
    }

    private void drawFrame(LinearLayout oneCalendar) {
        // 添加周末线性布局
        LinearLayout title = new LinearLayout(getContext());
        title.setBackgroundColor(COLOR_BG_WEEK_TITLE);
        title.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0,
                0.5f);
        Resources res = getResources();
        tb = res.getDimension(R.dimen.historyscore_tb);
        layout.setMargins(0, 0, 0, (int) (tb * 1.2));
        title.setLayoutParams(layout);
        oneCalendar.addView(title);

        // 添加周末TextView
        for (int i = 0; i < COLS_TOTAL; i++) {
            TextView view = new TextView(getContext());
            view.setGravity(Gravity.CENTER);
            view.setText(weekday[i]);
            view.setTextColor(COLOR_TX_WEEK_TITLE);
            view.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1));
            title.addView(view);
        }

        // 添加日期布局
        LinearLayout content = new LinearLayout(getContext());
        LinearLayout dateLayout = new LinearLayout(getContext());
        if (showModel == NOMAL) {
            content.setOrientation(LinearLayout.VERTICAL);
        }

        //绘制周次
        if (showModel == WEEK) {
            content.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout weekLayout = new LinearLayout(getContext());
            weekLayout.setOrientation(LinearLayout.VERTICAL);
            weekLayout.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1));
            for (int i = 0; i < ROWS_TOTAL; i++) {
                final RelativeLayout view = new RelativeLayout(getContext());
                view.setGravity(Gravity.CENTER);
                view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));
                view.setBackgroundResource(R.mipmap.calendar_day_bg);
                weekLayout.addView(view);
                //周次点击事件
                final int finalI = i;
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int m;
                        for(m = 0; m < 7; m++){
                            if(dates[finalI][m] != null){
                                break;
                            }
                        }
                        if(m == 7){
                            return;
                        }
                        String week = ((TextView) (view.getChildAt(0))).getText().toString();
                        if (Integer.valueOf(week) < 10)
                            week = 0 + week;
                        String year = "";
                        for(int k = 0; k < 7; k++){
                            if(dates[finalI][k] != null) {
                                year = dates[finalI][k].substring(0, 4);
                                break;
                            }
                        }

                        int weekNum = Integer.valueOf(year + week);
                        dayBgColorMap.clear();
                        clickDate = null;
                        selectWeek(weekSelectList, weekNum);

                        if (weekSelectList.size() > 0) {
                            int yearFrom = (int) Math.floor(weekSelectList.get(0) / 100);
                            int weekFrom = weekSelectList.get(0) % 100;
                            String dateFrom = getStartDayOfWeekNo(yearFrom, weekFrom);
                            dateFromTo[0] = dateFrom;

                            int yearTo = (int) Math.floor(weekSelectList.get(weekSelectList.size() - 1) / 100);
                            int weekTo = weekSelectList.get(weekSelectList.size() - 1) % 100;
                            String dateTo = getEndDayOfWeekNo(yearTo, weekTo);
                            dateFromTo[1] = dateTo;
                        }else {
                            dateFromTo[0] = null;
                            dateFromTo[1] = null;
                        }
                        setCalendarNum();

                        if (onWeekClickListener != null) {
                            onWeekClickListener.onWeekClick(dateFromTo);
                        }
                    }
                });
            }

            dateLayout.setOrientation(LinearLayout.VERTICAL);
            dateLayout.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 7));
            content.addView(weekLayout);
            content.addView(dateLayout);
        }
        content.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 7f));
        oneCalendar.addView(content);

        // 添加日期TextView
        for (int i = 0; i < ROWS_TOTAL; i++) {
            LinearLayout row = new LinearLayout(getContext());
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setLayoutParams(new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, 0, 1));
            if (showModel == NOMAL)
                content.addView(row);
            else if (showModel == WEEK)
                dateLayout.addView(row);
            // 绘制日历上的列
            for (int j = 0; j < 7; j++) {
                col = new RelativeLayout(getContext());
                col.setLayoutParams(new LinearLayout.LayoutParams(0,
                        LayoutParams.MATCH_PARENT, 1));
                col.setBackgroundResource(R.mipmap.calendar_day_bg);
                col.setGravity(Gravity.CENTER);
                row.addView(col);
                // 给每一个日子加上监听
                final int finalI = i;
                final int finalJ = j;
                col.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(dates[finalI][finalJ] == null){
                            return;
                        }
                        ViewGroup parent = (ViewGroup) v.getParent();
                        int row = 0, col = 0;

                        // 获取列坐标
                        for (int i = 0; i < parent.getChildCount(); i++) {
                            if (v.equals(parent.getChildAt(i))) {
                                col = i;
                                break;
                            }
                        }
                        // 获取行坐标
                        ViewGroup pparent = (ViewGroup) parent.getParent();
                        for (int i = 0; i < pparent.getChildCount(); i++) {
                            if (parent.equals(pparent.getChildAt(i))) {
                                row = i;
                                break;
                            }
                        }

                        if(showModel == WEEK){
                            weekSelectList.clear();
                            setWeekView();
//                            for (int i = 0; i < dateFromTo.length; i++)
//                                dateFromTo[i] = null;
                        } else if (selectModel == SINGLE) {
                            String curDate = func.getCurTime();
                            GetiIntervalDays intervalDays = new GetiIntervalDays(curDate, dates[row][col]);
                            if (intervalDays.getIntervalDays() > 0) {
                                Toast.makeText(getContext(), "选择日期不能超过今天", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                        boolean isOtherPage = false;
                        dayBgColorMap.clear();
                        dayBgColorMap.put(dates[row][col], R.drawable.calendar_red_circle);

                        if(selectModel == DOUBLE) {
                            if (dateFromTo[0] == null) {
                                dateFromTo[0] = null;
                                dateFromTo[1] = null;

                                clickDate = dates[row][col];
                                dateFromTo[0] = dates[row][col];
                                dateFromTo[1] = dates[row][col];
                            } else {
                                GetiIntervalDays days0 = new GetiIntervalDays(dateFromTo[0], dates[row][col]);
                                GetiIntervalDays days1 = new GetiIntervalDays(dates[row][col], dateFromTo[1]);
                                if(days0.getIntervalDays() <= 0 ){
                                    dateFromTo[0] = dates[row][col];
                                }
                                if(days1.getIntervalDays() <= 0 ){
                                    dateFromTo[1] = dates[row][col];
                                }
                                if(days0.getIntervalDays() >= 0 && days1.getIntervalDays() >= 0 ){
                                    if(direction == START)
                                        dateFromTo[0] = dates[row][col];
                                    if(direction == END)
                                        dateFromTo[1] = dates[row][col];
                                }

                                clickDate = dates[row][col];
                            }
                            List<String> dateFromToList = getDatesBetweenTwoDate(dateFromTo[0],dateFromTo[1]);
                            for(String date: dateFromToList)
                                dayBgColorMap.put(date, R.drawable.calendar_red_circle);
                        }

                        if (onCalendarClickListener != null) {
                            if (selectModel == DOUBLE)
                                onWeekClickListener.onWeekClick(dateFromTo);
                            else
                                onCalendarClickListener.onCalendarClick(row, col, dates[row][col]);
                        }

                        String dateFormat = dates[row][col];

                        int month = Integer.parseInt(dateFormat.substring(
                                dateFormat.indexOf("-") + 1,
                                dateFormat.lastIndexOf("-")));

                        if (getCalendarMonth() - month == 1// 跨年跳转
                                || getCalendarMonth() - month == -11) {
                            isOtherPage = true;
                            lastMonth();

                        } else if (month - getCalendarMonth() == 1 // 跨年跳转
                                || month - getCalendarMonth() == -11) {
                            isOtherPage = true;
                            nextMonth();
                        }

                        if(!isOtherPage){
                            setCalendarDate();
                        }
                    }
                });
            }
        }
    }

    private void selectWeek(ArrayList<Integer> weekSelectList, int weekNum) {
        if (weekSelectList.size() == 0) {
            weekSelectList.add(0, weekNum);
            return;
        }

        //点击第一个前面
        if (weekNum < weekSelectList.get(0)) {
            double minYear = Math.floor(weekSelectList.get(0) / 100);
            int minWeek = weekSelectList.get(0) % 100;
            double year = Math.floor(weekNum / 100);
            int week = weekNum % 100;
            int position = 0;
            for (int k = (int) year; k <= minYear; k++) {
                if (k == year && k == minYear) {
                    for (int n = week; n < minWeek; n++) {
                        weekSelectList.add(position, k * 100 + n);
                        position += 1;
                    }
                } else if (k == year && k < minYear) {
                    int curYearWeek = getYearMaxWeek(String.valueOf(k));
                    for (int n = week; n <= curYearWeek; n++) {
                        weekSelectList.add(position, k * 100 + n);
                        position += 1;
                    }
                } else if (k > year && k < minYear) {
                    int curYearWeek = getYearMaxWeek(String.valueOf(k));
                    for (int n = 1; n <= curYearWeek; n++) {
                        weekSelectList.add(position, k * 100 + n);
                        position += 1;
                    }
                } else if (k > year && k == minYear) {
                    for (int n = 1; n < minWeek; n++) {
                        weekSelectList.add(position, k * 100 + n);
                        position += 1;
                    }
                }
            }
            return;
        }
        //点击第一个
        if (weekNum == weekSelectList.get(0)) {
            weekSelectList.remove(0);
            return;
        }
        //点击最后一个
        if (weekSelectList.size() >= 2 && weekNum == weekSelectList.get(weekSelectList.size() - 1)) {
            weekSelectList.remove(weekSelectList.size() - 1);
            return;
        }

        //点击第一个后面
        if (weekNum > weekSelectList.get(0)) {
            double minYear = Math.floor(weekSelectList.get(0) / 100);
            int minWeek = weekSelectList.get(0) % 100;
            double selectYear = Math.floor(weekNum / 100);
            int selectWeek = weekNum % 100;
            weekSelectList.clear();
            for (int k = (int) minYear; k <= selectYear; k++) {
                if (k == minYear && k == selectYear) {
                    for (int n = minWeek; n <= selectWeek; n++) {
                        weekSelectList.add(k * 100 + n);
                    }
                } else if (k == minYear && k < selectYear) {
                    int curYearWeek = getYearMaxWeek(String.valueOf(k));
                    for (int n = minWeek; n <= curYearWeek; n++) {
                        weekSelectList.add(k * 100 + n);
                    }
                } else if (k > minYear && k < selectYear) {
                    int curYearWeek = getYearMaxWeek(String.valueOf(k));
                    for (int n = 1; n <= curYearWeek; n++) {
                        weekSelectList.add(k * 100 + n);
                    }
                } else if (k > minYear && k == selectYear) {
                    for (int n = 0; n <= selectWeek; n++) {
                        weekSelectList.add(k * 100 + n);
                    }
                }
            }
            return;
        }
    }

    private void setCalendarNum() {
        setCalendarDate();
        if (showModel == WEEK)
            setWeekView();
    }

    /**
     * 填充日历(包含日期、标记、背景等)
     */
    private void setCalendarDate() {
        // 根据日历的日子获取这一天是星期几
        int weekday = calendarday.getDay();
        if(weekday == 0)
            weekday = 7;
        // 每个月第一天
        int firstDay = 1;
        // 每个月中间号,根据循环会自动++
        int day = firstDay;
        // 每个月的最后一天
        int lastDay = getDateNum(calendarday.getYear(), calendarday.getMonth());
        // 下个月第一天
        int nextMonthDay = 1;
        int lastMonthDay = 1;

        // 填充每一个空格
        for (int i = 0; i < ROWS_TOTAL; i++) {
            for (int j = 0; j < 7; j++) {
                // 这个月第一天不是礼拜一,则需要绘制上个月的剩余几天
                if (i == 0 && j == 0 && weekday != 1) {
                    int year = 0;
                    int month = 0;
                    int lastMonthDays = 0;
                    // 如果这个月是1月，上一个月就是去年的12月
                    if (calendarday.getMonth() == 0) {
                        year = calendarday.getYear() - 1;
                        month = Calendar.DECEMBER;
                    } else {
                        year = calendarday.getYear();
                        month = calendarday.getMonth() - 1;
                    }
                    // 上个月的最后一天是几号
                    lastMonthDays = getDateNum(year, month);
                    // 第一个格子展示的是几号
                    int firstShowDay = lastMonthDays - weekday + 2;
                    // 上月
                    if(calendarday.getMonth() != Calendar.JANUARY) {
                        for (int k = 0; k < weekday - 1; k++) {
                            lastMonthDay = firstShowDay + k;
                            RelativeLayout group = getDateView(0, k);
                            group.setGravity(Gravity.CENTER);
                            TextView view = null;
                            if (group.getChildCount() > 0) {
                                view = (TextView) group.getChildAt(0);
                            } else {
                                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                                params.addRule(RelativeLayout.CENTER_IN_PARENT);
                                view = new TextView(getContext());
                                view.setLayoutParams(params);
                                view.setGravity(Gravity.CENTER);
                                group.addView(view);
                            }
                            view.setText(Integer.toString(lastMonthDay));
                            view.setTextColor(COLOR_TX_OTHER_MONTH_DAY);
                            dates[0][k] = format(new Date(year, month, lastMonthDay));
                            // 设置日期背景色
                            if (dayBgColorMap.get(dates[0][k]) != null) {
                                view.setBackgroundResource(dayBgColorMap.get(dates[0][k]));
                            } else {
                                view.setBackgroundColor(Color.TRANSPARENT);
                            }
                            // 设置标记
                            setMarker(group, 0, k);
                        }
                    }
                    j = weekday - 2;
                    // 这个月第一天是礼拜一，不用绘制上个月的日期，直接绘制这个月的日期
                } else {
                    RelativeLayout group = getDateView(i, j);
                    group.setGravity(Gravity.CENTER);
                    TextView view = null;
                    if (group.getChildCount() > 0) {
                        view = (TextView) group.getChildAt(0);
                    } else {
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                        params.addRule(RelativeLayout.CENTER_IN_PARENT);
                        view = new TextView(getContext());
                        view.setLayoutParams(params);
                        view.setGravity(Gravity.CENTER);
                        group.addView(view);
                    }

                    // 本月
                    if (day <= lastDay) {
                        dates[i][j] = format(new Date(calendarday.getYear(),
                                calendarday.getMonth(), day));
                        view.setText(Integer.toString(day));
                        // 当天
                        if (thisday.getDate() == day
                                && thisday.getMonth() == calendarday.getMonth()
                                && thisday.getYear() == calendarday.getYear()) {
                            view.setText("今天");
                            view.setTextColor(COLOR_TX_WEEK_TITLE);
                            view.setBackgroundColor(Color.TRANSPARENT);
                        } else {
                            view.setTextColor(COLOR_TX_THIS_MONTH_DAY);
                            view.setBackgroundColor(Color.TRANSPARENT);
                        }
                        // 上面首先设置了一下默认的"当天"背景色，当有特殊需求时，才给当日填充背景色
                        // 设置日期背景色
                        if (dayBgColorMap.get(dates[i][j]) != null) {
                            view.setTextColor(Color.WHITE);
                            view.setBackgroundResource(dayBgColorMap.get(dates[i][j]));
                        }
                        // 设置标记
                        setMarker(group, i, j);
                        day++;
                        // 下个月
                    } else  if(calendarday.getMonth() != Calendar.DECEMBER){
//                        if (calendarday.getMonth() == Calendar.DECEMBER) {
//                            dates[i][j] = format(new Date(
//                                    calendarday.getYear() + 1,
//                                    Calendar.JANUARY, nextMonthDay));
//                        } else {
                            dates[i][j] = format(new Date(
                                    calendarday.getYear(),
                                    calendarday.getMonth() + 1, nextMonthDay));
//                        }
                        view.setText(Integer.toString(nextMonthDay));
                        view.setTextColor(COLOR_TX_OTHER_MONTH_DAY);
                        // 设置日期背景色
                        if (dayBgColorMap.get(dates[i][j]) != null) {
                            view.setBackgroundResource(dayBgColorMap.get(dates[i][j]));
                        } else {
                            view.setBackgroundColor(Color.TRANSPARENT);
                        }
                        // 设置标记
                        setMarker(group, i, j);
                        nextMonthDay++;
                    }
                }
            }
        }
    }

    private void setWeekView() {
        int dayBgColorMapSizeOld = dayBgColorMap.size();
        for (int i = 0; i < ROWS_TOTAL; i++) {
            RelativeLayout weekLayout = getWeekView(i);
            TextView view = null;
            if (weekLayout.getChildCount() > 0) {
                view = (TextView) weekLayout.getChildAt(0);
            } else {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                view = new TextView(getContext());
                view.setGravity(Gravity.CENTER);
                view.setLayoutParams(params);
                view.setTextColor(Color.BLUE);
                view.setBackgroundColor(Color.TRANSPARENT);
                weekLayout.addView(view);
            }
            int week = 0;
            String year = "";
            for(int j = 0; j < 7; j++){
                if(dates[i][j] != null){
                    year = dates[i][j].substring(0, 4);
                    week = getWeekNumOfYearDay(dates[i][j]);
                    break;
                }
            }
            if(week == 0)
                view.setText("");
            else
                view.setText(String.valueOf(week));
            String weekString = String.valueOf(week);
            if (week < 10)
                weekString = "0" + week;
            int weekNum = Integer.valueOf(year + weekString);
            if (weekSelectList.size() > 0 && weekSelectList.contains(weekNum) && week != 0) {
                view.setBackgroundColor(Color.RED);
                for (int j = 0; j < 7; j++)
                    dayBgColorMap.put(dates[i][j], R.drawable.calendar_red_circle);
            } else {
                view.setBackgroundColor(Color.TRANSPARENT);
                for (int j = 0; j < 7; j++)
                    dayBgColorMap.remove(dates[i][j]);
            }
        }
        List<String> dateFromToList = getDatesBetweenTwoDate(dateFromTo[0],dateFromTo[1]);
        for(String date: dateFromToList)
            dayBgColorMap.put(date, R.drawable.calendar_red_circle);
        int dayBgColorMapSizeNow = dayBgColorMap.size();
        if(dayBgColorMapSizeNow != dayBgColorMapSizeOld)
            setCalendarDate();
    }

    /**
     * 根据具体的某年某月，展示一个日历
     *
     * @param year  年
     * @param month 月
     */
    public void showCalendar(int year, int month) {
        refreshCalendar();
        calendarYear = year;
        calendarMonth = month - 1;
        calendarday = new Date(calendarYear - 1900, calendarMonth, 1);
        setCalendarNum();
    }

    /**
     * 根据当前月，展示一个日历
     */
    public void showCalendar() {
        Date now = new Date();
        calendarYear = now.getYear() + 1900;
        calendarMonth = now.getMonth();
        calendarday = new Date(calendarYear - 1900, calendarMonth, 1);
        setCalendarNum();
    }

    /**
     * 下一月日历
     */
    public synchronized void nextMonth() {
        refreshCalendar();
        // 改变日历上下顺序
        if (currentCalendar == firstCalendar) {
            currentCalendar = secondCalendar;
        } else {
            currentCalendar = firstCalendar;
        }
        // 设置动画
        setInAnimation(push_left_in);
        setOutAnimation(push_left_out);
        // 改变日历日期
        if (calendarMonth == Calendar.DECEMBER) {
            calendarYear++;
            calendarMonth = Calendar.JANUARY;
        } else {
            calendarMonth++;
        }
        calendarday = new Date(calendarYear - 1900, calendarMonth, 1);
        // 填充日历
        setCalendarNum();
        // 下翻到下一月
        showNext();
        // 回调
        if (onCalendarDateChangedListener != null) {
            onCalendarDateChangedListener.onCalendarDateChanged(calendarYear,
                    calendarMonth + 1);
        }
    }

    /**
     * 上一月日历
     */
    public synchronized void lastMonth() {
        refreshCalendar();
        if (currentCalendar == firstCalendar) {
            currentCalendar = secondCalendar;
        } else {
            currentCalendar = firstCalendar;
        }
        setInAnimation(push_right_in);
        setOutAnimation(push_right_out);
        if (calendarMonth == Calendar.JANUARY) {
            calendarYear--;
            calendarMonth = Calendar.DECEMBER;
        } else {
            calendarMonth--;
        }
        calendarday = new Date(calendarYear - 1900, calendarMonth, 1);
        setCalendarNum();
        showPrevious();
        if (onCalendarDateChangedListener != null) {
            onCalendarDateChangedListener.onCalendarDateChanged(calendarYear,
                    calendarMonth + 1);
        }
    }

    private void refreshCalendar(){
        if(getWeekView(0)== null){
            return;
        }
        for(int i = 0; i < ROWS_TOTAL; i++){
            RelativeLayout groupWeek = getWeekView(i);
            TextView weekView = (TextView) groupWeek.getChildAt(0);
            if(weekView != null)
                weekView.setText("");
            for (int j = 0; j < 7; j++){
                dates[i][j] = null;
                RelativeLayout group = getDateView(i, j);
                TextView view = (TextView) group.getChildAt(0);
                if(view != null)
                    view.setText("");

            }
        }
    }

    /**
     * 获取日历当前年份
     */
    public int getCalendarYear() {
        return calendarday.getYear() + 1900;
    }

    /**
     * 获取日历当前月份
     */
    public int getCalendarMonth() {
        return calendarday.getMonth() + 1;
    }

    /**
     * 在日历上做一个标记
     *
     * @param date 日期
     * @param id   bitmap res id
     */
    public void addMark(Date date, int id) {
        addMark(format(date), id);
    }

    /**
     * 在日历上做一个标记
     *
     * @param date 日期
     * @param id   bitmap res id
     */
    void addMark(String date, int id) {
        marksMap.put(date, id);
        setCalendarDate();
    }

    /**
     * 在日历上做一组标记
     *
     * @param date 日期
     * @param id   bitmap res id
     */
    public void addMarks(Date[] date, int id) {
        for (int i = 0; i < date.length; i++) {
            marksMap.put(format(date[i]), id);
        }
        setCalendarDate();
    }

    /**
     * 在日历上做一组标记
     *
     * @param date 日期
     * @param id   bitmap res id
     */
    public void addMarks(List<String> date, int id) {
        for (int i = 0; i < date.size(); i++) {
            marksMap.put(date.get(i), id);
        }
        setCalendarDate();
    }

    /**
     * 移除日历上的标记
     */
    public void removeMark(Date date) {
        removeMark(format(date));
    }

    /**
     * 移除日历上的标记
     */
    public void removeMark(String date) {
        marksMap.remove(date);
        setCalendarDate();
    }

    /**
     * 移除日历上的所有标记
     */
    public void removeAllMarks() {
        marksMap.clear();
        setCalendarDate();
    }

    /**
     * 设置日历具体某个日期的背景色
     *
     * @param date  日期
     * @param color 背景色
     */
    public void setCalendarDayBgColor(Date date, int color) {
        setCalendarDayBgColor(format(date), color);
    }

    /**
     * 设置日历具体某个日期的背景色
     *
     * @param date  日期
     * @param color 背景色
     */
    public void setCalendarDayBgColor(String date, int color) {
        dayBgColorMap.put(date, color);
        setCalendarDate();
    }

    public void setDateFromTo(String[] datefromto) {
        this.dateFromTo = datefromto;
        List<String> dateFromToList = getDatesBetweenTwoDate(dateFromTo[0],dateFromTo[1]);
        for(String date: dateFromToList)
            dayBgColorMap.put(date, R.drawable.calendar_red_circle);
        setCalendarDate();
    }

    /**
     * 设置日历一组日期的背景色
     *
     * @param date  日期
     * @param color 背景色
     */
    public void setCalendarDaysBgColor(List<String> date, int color) {
        for (int i = 0; i < date.size(); i++) {
            dayBgColorMap.put(date.get(i), color);
        }
        setCalendarDate();
    }

    /**
     * 设置日历一组日期的背景色
     *
     * @param date  日期
     * @param color 背景色
     */
    public void setCalendarDayBgColor(String[] date, int color) {
        for (int i = 0; i < date.length; i++) {
            dayBgColorMap.put(date[i], color);
        }
        setCalendarDate();
    }

    /**
     * 移除日历具体某个日期的背景色
     *
     * @param date 日期
     */
    public void removeCalendarDayBgColor(Date date) {
        removeCalendarDayBgColor(format(date));
    }

    /**
     * 移除日历具体某个日期的背景色
     *
     * @param date 日期
     */
    public void removeCalendarDayBgColor(String date) {
        dayBgColorMap.remove(date);
        setCalendarDate();
    }

    /**
     * 移除日历具体某个日期的背景色
     */
    public void removeAllBgColor() {
        dayBgColorMap.clear();
        setCalendarDate();
    }

    /**
     * 根据行列号获得包装每一个日子的LinearLayout
     *
     * @param row
     * @param col
     * @return
     */
    public String getDate(int row, int col) {
        return dates[row][col];
    }

    /**
     * 某天是否被标记了
     *
     * @param date
     * @return 是否被标记布尔值
     */
    public boolean hasMarked(String date) {
        return marksMap.get(date) == null ? false : true;
    }

    /**
     * 清除所有标记以及背景
     */
    public void clearAll() {
        marksMap.clear();
        dayBgColorMap.clear();
    }

    /***********************************************
     * private methods
     **********************************************/
    // 设置标记
    private void setMarker(RelativeLayout group, int i, int j) {
        int childCount = group.getChildCount();
        if (marksMap.get(dates[i][j]) != null) {
            if (childCount < 2) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        (int) (tb * 0.7), (int) (tb * 0.7));
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                params.setMargins(0, 0, 1, 1);
                ImageView markView = new ImageView(getContext());
                markView.setImageResource(marksMap.get(dates[i][j]));
                markView.setLayoutParams(params);
                markView.setBackgroundResource(R.mipmap.calendar_bg_tag);
                group.addView(markView);
            }
        } else {
            if (childCount > 1) {
                group.removeView(group.getChildAt(1));
            }
        }

    }

    /**
     * 计算某年某月有多少天
     *
     * @param year  年
     * @param month 月
     * @return
     */
    private int getDateNum(int year, int month) {
        Calendar time = Calendar.getInstance();
        time.clear();
        time.set(Calendar.YEAR, year + 1900);
        time.set(Calendar.MONTH, month);
        return time.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * 根据行列号获得包装每一个日子的RelativeLayout
     *
     * @param row 行
     * @param col 列
     * @return RelativeLayout
     */
    private RelativeLayout getDateView(int row, int col) {
        if (showModel == NOMAL)
            return (RelativeLayout) ((LinearLayout) ((LinearLayout) currentCalendar
                    .getChildAt(1)).getChildAt(row)).getChildAt(col);
        else if (showModel == WEEK)
            return (RelativeLayout) ((LinearLayout) ((LinearLayout) ((LinearLayout) currentCalendar.
                    getChildAt(1)).getChildAt(1)).getChildAt(row)).getChildAt(col);
        return null;
    }

    /**
     * 根据行列号获得包装每一个周次的RelativeLayout
     *
     * @param row 行
     * @return RelativeLayout
     */
    private RelativeLayout getWeekView(int row) {
        if (showModel == WEEK)
            return (RelativeLayout) ((LinearLayout) ((LinearLayout) currentCalendar
                    .getChildAt(1)).getChildAt(0)).getChildAt(row);
        return null;
    }

    /**
     * 将Date转化成字符串->2013-3-3
     *
     * @param d 日期
     */
    private String format(Date d) {
        return addZero(d.getYear() + 1900, 4) + "-"
                + addZero(d.getMonth() + 1, 2) + "-" + addZero(d.getDate(), 2);
    }

    /***********************************************
     * Override methods
     **********************************************/
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (gd != null) {
            if (gd.onTouchEvent(ev))
                return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    public boolean onTouchEvent(MotionEvent event) {
        return this.gd.onTouchEvent(event);
    }

    public boolean onDown(MotionEvent e) {
        return false;
    }

    public void onShowPress(MotionEvent e) {
    }

    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        return false;
    }

    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        // 向左/上滑动
        if (e1.getX() - e2.getX() > 20) {
            nextMonth();
        }
        // 向右/下滑动
        else if (e1.getX() - e2.getX() < -20) {
            lastMonth();
        }
        return false;
    }

    /***********************************************
     * get/set methods
     **********************************************/

    public OnCalendarClickListener getOnCalendarClickListener() {
        return onCalendarClickListener;
    }

    public void setOnCalendarClickListener(
            OnCalendarClickListener onCalendarClickListener) {
        this.onCalendarClickListener = onCalendarClickListener;
    }

    public void setOnWeekClickListener(OnWeekClickListener onWeekClickListener) {
        this.onWeekClickListener = onWeekClickListener;
    }

    public OnCalendarDateChangedListener getOnCalendarDateChangedListener() {
        return onCalendarDateChangedListener;
    }

    public void setOnCalendarDateChangedListener(
            OnCalendarDateChangedListener onCalendarDateChangedListener) {
        this.onCalendarDateChangedListener = onCalendarDateChangedListener;
    }

    public Date getThisday() {
        return thisday;
    }

    public void setThisday(Date thisday) {
        this.thisday = thisday;
    }

    public Map<String, Integer> getDayBgColorMap() {
        return dayBgColorMap;
    }

    public void setDayBgColorMap(Map<String, Integer> dayBgColorMap) {
        this.dayBgColorMap = dayBgColorMap;
    }

//    /**
//     * 获得指定日期的后一天
//     *
//     * @param specifiedDay
//     * @return
//     */
//    public String getSpecifiedDayAfter(String specifiedDay) {
//        Calendar calendar = Calendar.getInstance();
//        Date date = null;
//        try {
//            date = new SimpleDateFormat("yy-MM-dd").parse(specifiedDay);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        calendar.setTime(date);
//        calendar.add(Calendar.DATE, 1);
//
//        String dayAfter = new SimpleDateFormat("yyyy-MM-dd")
//                .format(calendar.getTime());
//        return dayAfter;
//    }

    /*****************************************
     * 计算指定日期某年的第几周(当年第一天算起)2017-01-01是第一周
     * @return interger
     * @throws ParseException
     ****************************************/
    private int getWeekNumOfYearDay(String strDate) {
        String yearFirstDay = strDate.substring(0,4)+"-01-01";
        if(Integer.parseInt(strDate.substring(5,7)) == 12 && getWeekNumOfYearDayReal(strDate) == 1){
            return getWeekNumOfYearDayReal(strDate.substring(0,4)+"-12-24")+1;
        }
        if(getWeekNumOfYearDayReal(yearFirstDay)>1){
            if(Integer.parseInt(strDate.substring(5,7)) == 1 && getWeekNumOfYearDayReal(strDate) > 50){
                return 1;
            }
            return getWeekNumOfYearDayReal(strDate) + 1;
        }
        return getWeekNumOfYearDayReal(strDate);
    }

    /*****************************************
     * 计算指定日期某年的第几周2017-01-01是第53周
     * @return interger
     * @throws ParseException
     ****************************************/
    private int getWeekNumOfYearDayReal(String strDate) {
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate = null;
        try {
            curDate = format.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(curDate);
        calendar.add(Calendar.DATE, -1);

        int iWeekNum = calendar.get(Calendar.WEEK_OF_YEAR);
        return iWeekNum;
    }

    /**
     * 某年的最大周次
     *
     * @param year 年份
     * @return
     * @throws ParseException
     */
    private int getYearMaxWeek(String year) {
        int week31 = getWeekNumOfYearDay(year + "-12-31");
        int week24 = getWeekNumOfYearDay(year + "-12-24");

        if(week31 == 1){
            return week24 + 1;
        }else
            return week31;
    }


    /**
     * get start date of given week no of update_del_popup year
     *
     * @param year
     * @param weekNo
     * @return
     */
    public String getStartDayOfWeekNo(int year, int weekNo) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.WEEK_OF_YEAR, weekNo);
        String yearFirstDay = year + "-01-01";
        if (getWeekNumOfYearDayReal(yearFirstDay) > 1) {
            if (cal.get(Calendar.MONTH) + 1 == 1 && weekNo == 1) {
                return yearFirstDay;
            } else {
                weekNo = weekNo -1;
            }
        }
        return getStartDayOfWeekNoReal(year, weekNo);
    }

    public String getStartDayOfWeekNoReal(int year, int weekNo) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.WEEK_OF_YEAR, weekNo);

        String monday = cal.get(Calendar.YEAR) + "-" + String.format("%02d", (cal.get(Calendar.MONTH) + 1)) + "-" +
                String.format("%02d", cal.get(Calendar.DAY_OF_MONTH));

//        String monday = getSpecifiedDayAfter(sunday);

        return monday;

    }

    /**
     * get the end day of given week no of update_del_popup year.
     *
     * @param year
     * @param weekNo
     * @return
     */
    public String getEndDayOfWeekNo(int year, int weekNo) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.WEEK_OF_YEAR, weekNo);
        String yearFirstDay = year + "-01-01";
        if(weekNo == getYearMaxWeek(String.valueOf(year))){
            return year+"-12-31";
        }
        if (getWeekNumOfYearDayReal(yearFirstDay) > 1) {
            if (cal.get(Calendar.MONTH) + 1 == 1 && weekNo == 1) {
                year = year - 1;
                weekNo = getWeekNumOfYearDayReal(yearFirstDay);
            }else {
                weekNo = weekNo -1;
            }
        }
        return getEndDayOfWeekNoReal(year, weekNo);
    }

    public String getEndDayOfWeekNoReal(int year, int weekNo) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.WEEK_OF_YEAR, weekNo);
        cal.add(Calendar.DATE, 1);

        String sunday = cal.get(Calendar.YEAR) + "-" + String.format("%02d", (cal.get(Calendar.MONTH) + 1)) + "-" +
                String.format("%02d", cal.get(Calendar.DAY_OF_MONTH));

//        String sunday = getSpecifiedDayAfter(saturday);

        return sunday;
    }

    /**
     * 根据开始时间和结束时间返回时间段内的时间集合
     *
     * @param begin
     * @param end
     * @return List
     */
    public List<String> getDatesBetweenTwoDate(String begin, String end){
        List<String> lDate = new ArrayList<String>();
        if(end == null){
            lDate.add(begin);
            return lDate;
        }else if(begin == null){
            lDate.add(end);
            return lDate;
        }

        lDate.add(begin);// 把开始时间加入集合
        Calendar cal = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date beginDate = sdf.parse(begin);
            java.util.Date endDate = sdf.parse(end);
            cal.setTime(beginDate);
            boolean bContinue = true;
            while (bContinue) {
                // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
                cal.add(Calendar.DAY_OF_MONTH, 1);
                // 测试此日期是否在指定日期之后

                if (endDate.after(cal.getTime())) {
                    String str = sdf.format(cal.getTime());
                    lDate.add(str);
                } else {
                    break;
                }
            }
            lDate.add(end);// 把结束时间加入集合
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return lDate;
    }

    /**
     * onClick接口回调
     */
    public interface OnCalendarClickListener {
        void onCalendarClick(int row, int col, String dateFormat);
    }

    /**
     * ondateChange接口回调
     */
    public interface OnCalendarDateChangedListener {
        void onCalendarDateChanged(int year, int month);
    }

    /**
     * onClick接口回调
     */
    public interface OnWeekClickListener {
        void onWeekClick(String[] dateFromTo);
    }

}