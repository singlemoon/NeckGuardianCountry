package com.neckguardian.activity.Mine_Settings.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.neckguardian.R;
import com.neckguardian.sign.State;
import com.simo.utils.SPPrivateUtils;

/**
 * Created by 孤月悬空 on 2016/1/22.
 */
public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {
    private String[] ringList;
    private Context context;
    private LayoutInflater mLayoutInflater;
    private OnClickItemListener onClickItemListener;

    public MusicAdapter(Context context, String[] ringList, OnClickItemListener onClickItemListener) {
        mLayoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.ringList = ringList;
        this.onClickItemListener = onClickItemListener;
        Log.i("MusicAdapter", String.valueOf(ringList.length));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mLayoutInflater.inflate(R.layout.item_music, null));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.ringName.setText(ringList[position]);
        if (ringList[position].equals(SPPrivateUtils.getString(context, State.bellName, "QQ飞车"))) {
            holder.checkedRing.setChecked(true);
        } else {
            holder.checkedRing.setChecked(false);
        }
        holder.ringLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickItemListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ringList.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout ringLayout = null;
        private TextView ringName = null;
        private RadioButton checkedRing = null;

        public ViewHolder(View itemView) {
            super(itemView);
            ringLayout = (LinearLayout) itemView.findViewById(R.id.ring_layout);
            ringName = (TextView) itemView.findViewById(R.id.ring_name);
            checkedRing = (RadioButton) itemView.findViewById(R.id.checked_ring);
        }
    }

    public interface OnClickItemListener {
        void onItemClick(int position);
    }
}
