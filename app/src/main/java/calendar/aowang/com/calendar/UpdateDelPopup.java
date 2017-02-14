package calendar.aowang.com.calendar;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

/**
 * Created by asus on 2016/6/7.
 */
public class UpdateDelPopup extends PopupWindow {

    private int x;
    private int y;

    private LinearLayout updateLL;
    private LinearLayout deleteLL;
    private UpdateAndDelListener listener;

    private int groupPosition;
    private int childPosition;

    public UpdateDelPopup(Context context, View parent, int x, int y,
                          int groupPosition, int childPosition,
                          UpdateAndDelListener listener) {
        super(context);

        this.x = x;
        this.y = y;
        this.groupPosition = groupPosition;
        this.childPosition = childPosition;
        this.listener = listener;

        initView(context, parent);
    }

    private void initView(Context context, View parent) {
        View view = View.inflate(context, R.layout.update_del_popup, null);
        setContentView(view);
        setWidth(func.dip2px(context, 80));
        setHeight(func.dip2px(context, 60));
        setBackgroundDrawable(new BitmapDrawable());
        setFocusable(true);
        setOutsideTouchable(true);
        setContentView(view);
        showAtLocation(parent, Gravity.LEFT | Gravity.TOP, x, y);
        update();

        updateLL = (LinearLayout) view.findViewById(R.id.popup_update_ll);
        deleteLL = (LinearLayout) view.findViewById(R.id.popup_del_ll);

        updateLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.update(groupPosition, childPosition);
            }
        });

        deleteLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.delete(groupPosition, childPosition);
            }
        });
    }

    public interface UpdateAndDelListener {
        void update(int groupPosition, int childPosition);

        void delete(int groupPosition, int childPosition);
    }
}
