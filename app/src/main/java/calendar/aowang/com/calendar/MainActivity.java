package calendar.aowang.com.calendar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import calendar.aowang.com.calendar.calendar.Constants;
import calendar.aowang.com.calendar.calendar.KCalendar;
import calendar.aowang.com.calendar.calendar.ShowCalendar;

public class MainActivity extends AppCompatActivity implements UpdateDelPopup.UpdateAndDelListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView calendar = (TextView) findViewById(R.id.calender);
        final TextView text = (TextView) findViewById(R.id.text);
        final TextView text1 = (TextView) findViewById(R.id.text1);

        if (text != null) {
            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Constants.calDate = text.getText().toString();
                    final ShowCalendar showCalendar = new ShowCalendar(MainActivity.this, text, ShowCalendar.WEEK,
                            KCalendar.START, text.getText().toString(), text1.getText().toString());
                    showCalendar.setOnDismissListener(new ShowCalendar.OnDismissListener() {
                                public void onDismiss() {
                                    String[] dt_start = showCalendar.getDateFromTo();
                                    text.setText(dt_start[0]);
                                    text1.setText(dt_start[1]);
                                }
                            });
                }
            });

            text1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Constants.calDate = text1.getText().toString();
                    final ShowCalendar showCalendar = new ShowCalendar(MainActivity.this, text, ShowCalendar.WEEK,
                            KCalendar.END, text.getText().toString(), text1.getText().toString());
                    showCalendar.setOnDismissListener(new ShowCalendar.OnDismissListener() {
                        public void onDismiss() {
                            String[] dt_start = showCalendar.getDateFromTo();
                            text.setText(dt_start[0]);
                            text1.setText(dt_start[1]);
                        }
                    });
                }
            });
        }

        if (calendar != null) {
            calendar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Constants.calDate = calendar.getText().toString();
                    final ShowCalendar showCalendar = new ShowCalendar(MainActivity.this, calendar);
                    showCalendar.setOnDismissListener(new ShowCalendar.OnDismissListener() {
                        public void onDismiss() {
                            calendar.setText(Constants.calDate);
                        }
                    });
                }
            });
        }

        final ImageView img = (ImageView) findViewById(R.id.edit);

        if (img != null) {
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int[] location = new int[2];
                    img.getLocationOnScreen(location);
                    UpdateDelPopup popup = new UpdateDelPopup(MainActivity.this, img, location[0] - func.dip2px(MainActivity.this, 80),
                            location[1] + img.getHeight() / 2 - func.dip2px(MainActivity.this, 30),
                            0, 0, MainActivity.this);
                }
            });
        }
    }

    @Override
    public void update(int groupPosition, int childPosition) {

    }

    @Override
    public void delete(int groupPosition, int childPosition) {

    }
}
