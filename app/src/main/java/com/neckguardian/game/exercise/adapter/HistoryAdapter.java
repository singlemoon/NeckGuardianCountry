package com.neckguardian.game.exercise.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.neckguardian.R;

import java.util.List;
import java.util.Map;

/**
 * Created by 孤月悬空 on 2016/3/10.
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private Context context;
    private List<Map<String, String>> list;
    private LayoutInflater mLayoutInflater;

    private static final String TAG = "HistoryAdapter";

    public HistoryAdapter(Context context, List<Map<String, String>> list) {
        mLayoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.list = list;
        Log.i(TAG, "list.size() = " + list.size());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mLayoutInflater.inflate(R.layout.item_history, null));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.show_time.setText(list.get(list.size() - position - 1).get("show_time"));
        holder.game_time.setText(list.get(list.size() - position - 1).get("game_time"));
        holder.game_name.setText(list.get(list.size() - position - 1).get("game_name"));
        holder.game_use_time.setText(list.get(list.size() - position - 1).get("game_use_time"));
        holder.completeness.setText(list.get(list.size() - position - 1).get("completeness"));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView show_time = null;
        TextView game_time = null;
        TextView game_name = null;
        TextView game_use_time = null;
        TextView completeness = null;

        public ViewHolder(View itemView) {
            super(itemView);
            show_time = (TextView) itemView.findViewById(R.id.show_time);
            game_time = (TextView) itemView.findViewById(R.id.game_time);
            game_name = (TextView) itemView.findViewById(R.id.game_name);
            game_use_time = (TextView) itemView.findViewById(R.id.game_use_time);
            completeness = (TextView) itemView.findViewById(R.id.completeness);
        }
    }
}
