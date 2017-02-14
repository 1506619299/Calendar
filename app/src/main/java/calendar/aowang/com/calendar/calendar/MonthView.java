package calendar.aowang.com.calendar.calendar;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import calendar.aowang.com.calendar.R;
import calendar.aowang.com.calendar.func;

/**
 * Created by asus on 2016/4/13.
 */
public class MonthView extends ViewFlipper implements OnGestureListener{

    private static final int COLOR_BG_MONTH = Color.parseColor("#ffeeeeee"); // 月份标题背景颜色
    public static final int COLOR_MONTH_TX = Color.parseColor("#aa564b4b"); // 当前月日历数字颜色
    private static final int MONTH_COLS = 4;//每行4个月份
    private Integer[] monthList = new Integer[]{1,2,3,4,5,6,7,8,9,10,11,12};

    private Animation push_left_in; // 动画-左进
    private Animation push_left_out; // 动画-左出
    private Animation push_right_in; // 动画-右进
    private Animation push_right_out; // 动画-右出

    private LinearLayout firstCalendar; // 第一个日历
    private LinearLayout secondCalendar; // 第二个日历
    private LinearLayout currentCalendar; // 当前显示的日历

    private int lastSelectYear;
    private int currentMonth;
    private int currentYear;
    private int currentMonthBgColor;
    private LinearLayout monthColLayout;

    private GestureDetector gd; // 手势监听器
    private OnMonthClickListener onMonthClickListener;
    private OnYearChangeListener onYearChangeListener;
    private Handler handler;

    public MonthView(Context context) {
        super(context);
        init();
    }

    public MonthView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        // 初始化日历翻动动画
        handler = new Handler();
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

        gd = new GestureDetector(getContext(),this);

        //布局参数
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        int marginPx =  func.dip2px(getContext(), 10);
        layout.setMargins(marginPx,marginPx,marginPx,marginPx);
        // 初始化第一个日历
        firstCalendar = new LinearLayout(getContext());
        firstCalendar.setOrientation(LinearLayout.VERTICAL);
        firstCalendar.setLayoutParams(layout);
        firstCalendar.setBackgroundColor(COLOR_BG_MONTH);
        // 初始化第二个日历
        secondCalendar = new LinearLayout(getContext());
        secondCalendar.setOrientation(LinearLayout.VERTICAL);
        secondCalendar.setLayoutParams(layout);
        secondCalendar.setBackgroundColor(COLOR_BG_MONTH);
        // 设置默认日历为第一个日历
        currentCalendar = firstCalendar;
        // 加入ViewFlipper
        addView(firstCalendar);
        addView(secondCalendar);

        // 绘制线条框架
        drawFrame(firstCalendar);
        drawFrame(secondCalendar);
        // 填充展示日历
        setCalendarMonth();
    }

    private void drawFrame(LinearLayout oneCalendar) {
        // 添加月份线性布局
        for(int i = 0; i < monthList.length/MONTH_COLS; i++) {
            LinearLayout monthRow = new LinearLayout(getContext());
            monthRow.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f);
            layout.setMargins(0, 0, 0, 0);
            monthRow.setLayoutParams(layout);
            oneCalendar.addView(monthRow);

            // 添加周末TextView
            for (int j = 0; j < MONTH_COLS; j++) {
                monthColLayout = new LinearLayout(getContext());

                TextView monthCol = new TextView(getContext());
                monthCol.setGravity(Gravity.CENTER);

                monthColLayout.setGravity(Gravity.CENTER);
                monthColLayout.setBackgroundResource(R.mipmap.calendar_day_bg);
                monthColLayout.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1f));
                monthColLayout.addView(monthCol);
                monthRow.addView(monthColLayout);
                monthColLayout.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
                        currentMonth = monthList[row * MONTH_COLS + col];
                        lastSelectYear = currentYear;
                        setCalendarMonth();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (onMonthClickListener != null) {
                                    onMonthClickListener.onMonthClick(currentYear, currentMonth);
                                }
                            }
                        },200);

                    }
                });
            }
        }
    }

    /**
     * 填充日历(包含月份、标记、背景等)
     */
    private void setCalendarMonth() {
        for(int i = 0; i < monthList.length/MONTH_COLS; i++) {
            for (int j = 0; j < MONTH_COLS; j++) {
                TextView col = getMonthCol(i, j);
                col.setText(monthList[i * MONTH_COLS + j] + "月");
                col.setTextColor(COLOR_MONTH_TX);
                col.setTextSize(16);
                col.setBackgroundColor(Color.TRANSPARENT);
                if(currentYear == lastSelectYear && monthList[i * MONTH_COLS + j] == currentMonth){
                    col.setTextColor(Color.WHITE);
                    col.setBackgroundResource(currentMonthBgColor);
                }
            }
        }
    }

    /**
     * 根据行列号获得包装每一个日子的RelativeLayout
     *
     * @param row 行
     * @param col 列
     * @return RelativeLayout
     */
    private TextView getMonthCol(int row, int col) {
        return (TextView)((LinearLayout) ((LinearLayout) currentCalendar.getChildAt(row)).getChildAt(col)).getChildAt(0);
    }


    public void setYearMonthBgColor(int year, int month, int color) {
        this.lastSelectYear = year;
        this.currentYear = year;
        this.currentMonth = month;
        this.currentMonthBgColor = color;
        setCalendarMonth();
    }

    /**
     * 下一月日历
     */
    public synchronized void nextYear() {
        // 改变日历上下顺序
        if (currentCalendar == firstCalendar) {
            currentCalendar = secondCalendar;
        } else {
            currentCalendar = firstCalendar;
        }
        // 设置动画
        setInAnimation(push_left_in);
        setOutAnimation(push_left_out);
        // 改变日历年份月份
        currentYear++;
        if(onYearChangeListener != null)
            onYearChangeListener.onYearChange(currentYear);
        // 填充日历
        setCalendarMonth();
        // 下翻到下一月
        showNext();
    }

    /**
     * 上一月日历
     */
    public synchronized void lastYear() {
        if (currentCalendar == firstCalendar) {
            currentCalendar = secondCalendar;
        } else {
            currentCalendar = firstCalendar;
        }
        setInAnimation(push_right_in);
        setOutAnimation(push_right_out);

        currentYear--;

        if(onYearChangeListener != null)
            onYearChangeListener.onYearChange(currentYear);

        setCalendarMonth();
        showPrevious();
    }

    public int getCurrentYear() {
        return currentYear;
    }

    public int getCurrentMonth() {
        return currentMonth;
    }

    public void setOnMonthClickListener(OnMonthClickListener onMonthClickListener) {
        this.onMonthClickListener = onMonthClickListener;
    }

    public void setOnYearChangeListener(OnYearChangeListener onYearChangeListener) {
        this.onYearChangeListener = onYearChangeListener;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (gd != null) {
            if (gd.onTouchEvent(ev))
                return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.gd.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        // 向左/上滑动
        if (e1.getX() - e2.getX() > 20) {
            nextYear();
        }
        // 向右/下滑动
        else if (e1.getX() - e2.getX() < -20) {
            lastYear();
        }
        return false;
    }

    /**
     * month点击回调接口
     */
    public interface OnMonthClickListener{
        void onMonthClick(int year, int month);
    }
    /**
     * 年份变动接口
     */
    public interface OnYearChangeListener{
        void onYearChange(int year);
    }

}
