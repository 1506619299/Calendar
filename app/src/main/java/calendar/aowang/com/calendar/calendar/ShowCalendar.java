package calendar.aowang.com.calendar.calendar;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import calendar.aowang.com.calendar.R;

/**
 * 日期控件类
 *
 * @author 魏陈强
 */
public class ShowCalendar extends PopupWindow {

	public static final int NOMAL = 0;
	public static final int WEEK = 1;
	private static final int DATE = 1;
	private static final int MONTH = 2;

	private static boolean showing = false;
	public String[] datefromto = new String[2];
	public String date = Constants.calDate;// 设置控件时间
	private int model = NOMAL;
	private int currentCalendar;
	private FrameLayout calendarLayout;
	private KCalendar calendarDate;
	private MonthView calendarMonth;
	private TextView popupwindow_calendar_month;
	private RelativeLayout popupwindow_calendar_last;
	private RelativeLayout popupwindow_calendar_next;
	private Button popupwindow_calendar_bt_enter;
	private LayoutParams lp;
	private String today;
	private Context mContext;
	private Handler handler;
	private Boolean firstLoad = true;

	public ShowCalendar(Context mContext, View parent, int model,
						int direction, String fromDate, String toDate) {
		this.model = model;
		datefromto[0] = fromDate;
		datefromto[1] = toDate;
		if (showing) {
			return;
		}
		showing = true;
		if (model == WEEK) {
			calendarDate = new KCalendar(mContext, KCalendar.WEEK, direction);
			initView(mContext, parent);
			calendarDate.setOnWeekClickListener(new KCalendar.OnWeekClickListener() {
				@Override
				public void onWeekClick(String[] dateFromTo) {
					datefromto = dateFromTo;
				}
			});
		}
	}


//    public ShowCalendar(Context mContext, View parent, int model, String fromDate, String toDate) {
//        this(mContext,parent,model,START,fromDate,toDate);
////        this.model = model;
////        datefromto[0] = fromDate;
////        datefromto[1] = toDate;
////        if (showing) {
////            return;
////        }
////        showing = true;
////        if (model == WEEK) {
////            calendarDate = new KCalendar(mContext, KCalendar.WEEK);
////            initView(mContext, parent);
////            calendarDate.setOnWeekClickListener(new KCalendar.OnWeekClickListener() {
////                @Override
////                public void onWeekClick(String[] dateFromTo) {
////                    datefromto = dateFromTo;
////                }
////            });
////        }
//    }


//    public ShowCalendar(Context mContext, View parent, int model) {
//        this.model = model;
//        if (showing) {
//            return;
//        }
//        showing = true;
//        if (model == WEEK) {
//            calendarDate = new KCalendar(mContext, KCalendar.WEEK);
//            initView(mContext, parent);
//            calendarDate.setOnWeekClickListener(new KCalendar.OnWeekClickListener() {
//                @Override
//                public void onWeekClick(String[] dateFromTo) {
//                    datefromto = dateFromTo;
//                }
//            });
//        }
//    }

	public ShowCalendar(Context mContext, View parent) {
		if (showing) {
			return;
		}
		showing = true;
		calendarDate = new KCalendar(mContext);

		initView(mContext, parent);
	}

	private void initView(final Context mContext, View parent) {
		checkDate();
		this.mContext = mContext;
		final View view = View.inflate(mContext, R.layout.popupwindow_calendar, null);
		ImageView imageView = (ImageView) view.findViewById(R.id.calender_background);
		imageView.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in_pigmanager));
		LinearLayout ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
		ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.scale_in));

		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.MATCH_PARENT);
		setBackgroundDrawable(new BitmapDrawable());
		setFocusable(true);
		setOutsideTouchable(true);
		setContentView(view);
		showAtLocation(parent, Gravity.LEFT | Gravity.BOTTOM, 0, 0);
		update();
		// 上月监听按钮
		popupwindow_calendar_last = (RelativeLayout) view.findViewById(R.id.popupwindow_calendar_last);
		// 下月监听按钮
		popupwindow_calendar_next = (RelativeLayout) view.findViewById(R.id.popupwindow_calendar_next);
		//年月时间显示textview
		popupwindow_calendar_month = (TextView) view.findViewById(R.id.popupwindow_calendar_month);
		//日期选择控件布局
		calendarLayout = (FrameLayout) view.findViewById(R.id.framelayout_calendar);
		//完成按钮
		popupwindow_calendar_bt_enter = (Button) view.findViewById(R.id.popupwindow_calendar_bt_enter);


		lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		calendarLayout.addView(calendarDate, lp);
		showDateCalendar();
		currentCalendar = DATE;

		calendarMonth = new MonthView(mContext);

		handler = new Handler();
		//第一次加载的时候，年月显示textview延迟300ms加载动画，其他情况，有切换日期月份面板就加载动画
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				popupwindow_calendar_month.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.text_shock));
				firstLoad = false;
			}
		}, 500);

		popupwindow_calendar_month.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (currentCalendar == DATE) {
					calendarDate.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.calenar_key_scale_out));
					calendarLayout.removeAllViews();
					calendarLayout.addView(calendarMonth, lp);
					calendarMonth.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.calenar_key_scale_in));
					showMonthCalendar();

					currentCalendar = MONTH;
				}
			}
		});
	}

	private void showDateCalendar() {
		popupwindow_calendar_month.setText(calendarDate.getCalendarYear() + "年"
				+ calendarDate.getCalendarMonth() + "月");
		if (!firstLoad) {
			popupwindow_calendar_month.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.text_shock));
		}
		if (null != date) {

			int years = Integer.parseInt(date.substring(0, date.indexOf("-")));
			int month = Integer.parseInt(date.substring(date.indexOf("-") + 1,
					date.lastIndexOf("-")));
			popupwindow_calendar_month.setText(years + "年" + month + "月");

			calendarDate.showCalendar(years, month);

			if (model == WEEK && datefromto[0] != null && datefromto[1] != null)
				calendarDate.setDateFromTo(datefromto);
			else
				calendarDate.setCalendarDayBgColor(date, R.drawable.calendar_red_circle);
		}

//		List<String> list = new ArrayList<String>(); // 设置标记列表
//		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//		today = sDateFormat.format(new java.util.Date());
//		// 标记当前日期，如果为空，则标记今天
//		if (null != date)
//			list.add(date);
//		else
//			list.add(today);
//		calendarDate.addMarks(list, 0);

		// 监听所选中的日期
		calendarDate.setOnCalendarClickListener(new KCalendar.OnCalendarClickListener() {

			@Override
			public void onCalendarClick(int row, int col, String dateFormat) {
				// TODO Auto-generated method stub
//				int month = Integer.parseInt(dateFormat.substring(
//						dateFormat.indexOf("-") + 1,
//						dateFormat.lastIndexOf("-")));
//
//				if (calendarDate.getCalendarMonth() - month == 1// 跨年跳转
//						|| calendarDate.getCalendarMonth() - month == -11) {
//					calendarDate.lastMonth();
//
//				} else if (month - calendarDate.getCalendarMonth() == 1 // 跨年跳转
//						|| month - calendarDate.getCalendarMonth() == -11) {
//					calendarDate.nextMonth();
//
//				} else {
				date = dateFormat;// 最后返回给全局 date
				Constants.calDate = date;// 设置时间全局变量
//				}
			}
		});

		// 监听当前月份
		calendarDate.setOnCalendarDateChangedListener(new KCalendar.OnCalendarDateChangedListener() {
			public void onCalendarDateChanged(int year, int month) {
				popupwindow_calendar_month.setText(year + "年" + month + "月");
			}
		});


		popupwindow_calendar_last
				.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						calendarDate.lastMonth();
					}
				});


		popupwindow_calendar_next
				.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						calendarDate.nextMonth();
					}
				});

		// 关闭窗口
		popupwindow_calendar_bt_enter.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (date != null || datefromto[0] != null)
					dismiss();
			}
		});
	}

	private void showMonthCalendar() {
		calendarMonth.setYearMonthBgColor(calendarDate.getCalendarYear(), calendarDate.getCalendarMonth(),
				R.drawable.calendar_red_circle);

		popupwindow_calendar_month.setText(calendarMonth.getCurrentYear() + "年");
		popupwindow_calendar_month.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.text_shock));

		calendarMonth.setOnMonthClickListener(new MonthView.OnMonthClickListener() {
			@Override
			public void onMonthClick(int year, int month) {
				if (date != null) {
					//如果所选年月不是当前年月，清除原来选择日期
					if (calendarMonth.getCurrentYear() != Integer.parseInt(date.substring(0, date.indexOf("-"))) ||
							calendarMonth.getCurrentMonth() != Integer.parseInt(date.substring(date.indexOf("-") + 1, date.lastIndexOf("-")))) {
						calendarDate.removeCalendarDayBgColor(date);
						date = null;
					}
				}
				calendarMonth.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.calenar_key_scale_out));
				calendarLayout.removeAllViews();
				calendarLayout.addView(calendarDate, lp);
				calendarDate.showCalendar(year, month);
				calendarDate.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.calenar_key_scale_in));
				showDateCalendar();
				currentCalendar = DATE;
			}
		});
		//滑动月份选择器，年月显示textview显示当前年份
		calendarMonth.setOnYearChangeListener(new MonthView.OnYearChangeListener() {
			@Override
			public void onYearChange(int year) {
				popupwindow_calendar_month.setText(year + "年");
			}
		});

		//上一年
		popupwindow_calendar_last
				.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						calendarMonth.lastYear();
					}
				});

		//下一年
		popupwindow_calendar_next
				.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						calendarMonth.nextYear();
					}
				});
		//选择月份的时候完成按钮不能点击
		popupwindow_calendar_bt_enter.setOnClickListener(null);
	}

	private void checkDate() {
//		Pattern pattern = Pattern.compile("^([0-9]{4})((0([1-9]{1}))|(1[0-2]))(([0-2]([0-9]{1}))|(3[0|1]))(([0-1]([0-9]{1}))|(2[0-4]))([0-5]([0-9]{1}))([0-5]([0-9]{1}))");
//		Matcher matcher = pattern.matcher(date);
//		if(!matcher.matches())
//			date = null;
		if (!isValidDate(date))
			date = null;
	}

	public boolean isValidDate(String str) {
		if (TextUtils.isEmpty(str)) return false;
		boolean convertSuccess = true;
		// 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			// 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
			format.setLenient(false);
			format.parse(str);
		} catch (ParseException e) {
			// e.printStackTrace();
			// 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
			convertSuccess = false;
		}
		return convertSuccess;
	}

	public String[] getDateFromTo() {
		return datefromto;
	}

	@Override
	public void dismiss() {
		showing = false;
		super.dismiss();
	}
}