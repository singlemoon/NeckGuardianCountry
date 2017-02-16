package com.neckguardian.activity.Home.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.neckguardian.R;
import com.timqi.sectorprogressview.ColorfulRingProgressView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 日期的适配器
 * Created by 孤月悬空 on 2015/12/31.
 */
public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {

    private List<Map<String, Object>> historyMemory = null;
    private LayoutInflater mLayoutInflater = null;
    private Context context;
    private OnClickItemListener onClickItemListener;
    private int lastPosition;
    private SimpleDateFormat dateFormat;
    private boolean flag = false;   //用来判断是否为第一次设置lastPosition

    private static final String TAG = "CalendarAdapter";

    public CalendarAdapter(Context context, List<Map<String, Object>> historyMemory, OnClickItemListener onClickItemListener) {
        mLayoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.historyMemory = historyMemory;
        this.onClickItemListener = onClickItemListener;

        dateFormat = new SimpleDateFormat("MM-dd");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mLayoutInflater.inflate(R.layout.item_calendar, null));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Map<String, Object> map = historyMemory.get(position);
        Calendar cal = Calendar.getInstance();
        String currentDay = dateFormat.format(new Date());
        String dayOfMonth = (String) map.get("dateNum");
        String dayAndMonth[] = dayOfMonth.split("-");
        int day = Integer.parseInt(dayAndMonth[1]);
        float percent = ((int) map.get("currentEnergy") * 1f / (int) map.get("targetEnergy")) * 100;

//        Log.i(TAG+"current", currentDay+"");
//        Log.i(TAG + "date", dayOfMonth);

        holder.datePro.setPercent(percent);
        holder.dayNum.setBackgroundResource(R.color.white);
        holder.dayNum.setText(day + "");
        holder.dayNum.setTextColor(context.getResources().getColor(R.color.text_color_black));

        if (currentDay.equals(dayOfMonth)) {
            if (!flag) {
                holder.dayNum.setTextColor(context.getResources().getColor(R.color.white));
                holder.dayNum.setBackgroundResource(R.drawable.bg_item_cal);
                lastPosition = position;
                flag = true;
            } else {
                holder.dayNum.setTextColor(context.getResources().getColor(R.color.bg_title));
            }
        }

        if (position == lastPosition) {
//            Log.i(TAG, lastPosition + "++++++++++++++++++" + position);

            holder.dayNum.setBackgroundResource(R.drawable.bg_item_cal);
            holder.dayNum.setTextColor(context.getResources().getColor(R.color.white));
        }

        holder.calendarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastPosition = position;
//                Log.i(TAG,lastPosition + "---------------" + position);
                onClickItemListener.onItemClick(position);

            }
        });
    }

    @Override
    public int getItemCount() {
        return historyMemory.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout calendarLayout = null;
        private ColorfulRingProgressView datePro = null;
        private TextView dayNum = null;

        public ViewHolder(View itemView) {
            super(itemView);
            calendarLayout = (LinearLayout) itemView.findViewById(R.id.calendar_layout);
            datePro = (ColorfulRingProgressView) itemView.findViewById(R.id.date_pro);
            dayNum = (TextView) itemView.findViewById(R.id.day_num);
        }

        public TextView getDateNum() {
            return dayNum;
        }
    }

    public interface OnClickItemListener {
        void onItemClick(int position);
    }

    public void setLastPosition(int lastPosition) {
        this.lastPosition = lastPosition;
    }
}
