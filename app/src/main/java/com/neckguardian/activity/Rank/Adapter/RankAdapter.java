package com.neckguardian.activity.Rank.Adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.model.User;
import com.neckguardian.R;
import com.neckguardian.service.RankUserService;
import com.neckguardian.sign.State;
import com.poliveira.parallaxrecyclerview.ParallaxRecyclerAdapter;
import com.simo.utils.SPPrivateUtils;
import com.simo.utils.T;
import com.simo.utils.myView.GlideCircleTransform;

import java.util.List;

/**
 * Recycler的适配器类
 * Created by 孤月悬空 on 2015/12/23.
 */
public class RankAdapter extends ParallaxRecyclerAdapter<User> {

    private Context context;
    private List<User> users = null;
    private LayoutInflater mLayoutInflater;
    private static final String TAG = "RankAdapter";

    public RankAdapter(Context context, List<User> users) {
        super(users);
        mLayoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.users = users;
    }


    @Override
    public void onBindViewHolderImpl(RecyclerView.ViewHolder viewHolder, ParallaxRecyclerAdapter<User> parallaxRecyclerAdapter, final int position) {
//        Log.i(TAG, users.get(position).toString());
        final ViewHolder rankViewHolder = (ViewHolder) viewHolder;

        if (users.get(position).getNeckMax().length() > 4) {
//            Log.i(TAG, String.valueOf(position));
            rankViewHolder.neckMax.setTextColor(context.getResources().getColor(R.color.rank_top_three));
        } else {
//            Log.i(TAG, String.valueOf(position));
            rankViewHolder.neckMax.setTextColor(context.getResources().getColor(R.color.rank_others));
        }
        rankViewHolder.rankNumTxt.setText(String.valueOf(users.get(position).getRankNumber()));
        Glide.with(context)
                .load(users.get(position).getPic())
                .transform(new GlideCircleTransform(context))
                .into(rankViewHolder.pic);
//        rankViewHolder.pic.setImageResource(R.mipmap.nologin_h);
        if (users.get(position).getNickName().length() < 6) {
            rankViewHolder.nickName.setText(users.get(position).getNickName());
        } else {
            String nickName = users.get(position).getNickName().substring(0, 5) + "...";
            rankViewHolder.nickName.setText(nickName);
        }
        rankViewHolder.neckMax.setText(users.get(position).getNeckMax());
        rankViewHolder.counts.setText(users.get(position).getCounts());
        rankViewHolder.heart.setChecked(SPPrivateUtils.getBoolean(context, State.isHeartChecked + users.get(position).getUserId(), false));
        rankViewHolder.clickArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = users.get(position).getUserId();
                if (!SPPrivateUtils.getBoolean(context, State.isHeartChecked + userId, false)) {
                    Message msg = new Message();
                    msg.what = RankAdapter.GET_RESULT;
                    msg.obj = rankViewHolder;
                    msg.arg1 = position;
                    mHandler.sendMessage(msg);
                    rankViewHolder.heart.setChecked(true);
                    SPPrivateUtils.put(context, State.isHeartChecked + userId, true);
                }
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolderImpl(ViewGroup viewGroup, ParallaxRecyclerAdapter<User> parallaxRecyclerAdapter, int i) {
        return new ViewHolder(mLayoutInflater.inflate(R.layout.item_rank, null));
    }

    @Override
    public int getItemCountImpl(ParallaxRecyclerAdapter<User> parallaxRecyclerAdapter) {
        return users.size();
    }


    private static final int GET_RESULT = 888;      //联网判断是否点赞成功
    private static final int CHANGE_COUNTS = 999;   //修改成功，修改赞个个数
    private static final int NOT_CHANGE = 1111;     //失败，将爱心变为未选中状态

    private Handler mHandler = new MyHandler();

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final ViewHolder viewHolder = (ViewHolder) msg.obj;
            final int position = msg.arg1;
//            Log.i(TAG + "position", String.valueOf(position));
            switch (msg.what) {
                case GET_RESULT:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            updateCount(viewHolder, position);
                        }
                    }).start();
                    break;
                case CHANGE_COUNTS:
                    viewHolder.counts.setText(String.valueOf(Integer.parseInt(users.get(position).getCounts()) + 1));
                    break;
                case NOT_CHANGE:
                    viewHolder.heart.setChecked(false);
                default:
                    break;
            }

        }
    }

    private void updateCount(ViewHolder viewHolder, int position) {
        RankUserService service = new RankUserService();
        String result = "";
        result = service.isChanged(users.get(position).getUserId());
        Message msg = new Message();
        msg.obj = viewHolder;
//        Log.i(TAG, users.get(position).toString());
        if (result.equals("success")) {
            msg.what = RankAdapter.CHANGE_COUNTS;
            msg.arg1 = position;
            mHandler.sendMessage(msg);
        } else if (result.equals("no")) {
            msg.what = RankAdapter.NOT_CHANGE;
            msg.arg1 = position;
            mHandler.sendMessage(msg);
            T.showShort(context, "服务器连接失败，点赞失败！");
        } else if (result.equalsIgnoreCase("error")) {
            msg.what = RankAdapter.NOT_CHANGE;
            msg.arg1 = position;
            mHandler.sendMessage(msg);
            T.showShort(context, "服务器发生未知错误，请稍后重试！");
        } else {
            msg.what = RankAdapter.NOT_CHANGE;
            msg.arg1 = position;
            mHandler.sendMessage(msg);
            T.showShort(context, "系统发生未知错误，请稍后重试！");
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView rankNumTxt = null;
        private ImageView pic = null;
        private RadioButton heart = null;
        private TextView nickName = null;
        private TextView neckMax = null;
        private TextView counts = null;
        private LinearLayout clickArea = null;

        public ViewHolder(View itemView) {
            super(itemView);
            rankNumTxt = (TextView) itemView.findViewById(R.id.rank_num_txt);
            pic = (ImageView) itemView.findViewById(R.id.pic);
            heart = (RadioButton) itemView.findViewById(R.id.heart);
            nickName = (TextView) itemView.findViewById(R.id.nick_name);
            neckMax = (TextView) itemView.findViewById(R.id.neck_max);
            counts = (TextView) itemView.findViewById(R.id.counts);
            clickArea = (LinearLayout) itemView.findViewById(R.id.click_area);
        }
    }
}
