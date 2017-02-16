package com.neckguardian.game.exercise;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.model.GameRecord;
import com.neckguardian.R;
import com.neckguardian.game.exercise.adapter.HistoryAdapter;
import com.neckguardian.sign.State;
import com.simo.BaseActivity;
import com.simo.sqllite.DatabaseOperate;
import com.simo.utils.SPPrivateUtils;
import com.simo.utils.myView.GlideCircleTransform;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 查看历史界面
 * Created by 孤月悬空 on 2016/3/5.
 */
public class GameHistory extends BaseActivity {

    private TextView titleText = null;
    private RoundedImageView headImgGame = null;
    private TextView nickNameGame = null;
    private RecyclerView timeLine = null;
    private HistoryAdapter historyAdapter = null;

    private Context context = null;
    private static final String TAG = "GameHistory";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_game_history);

        titleText = (TextView) super.findViewById(R.id.title_text);
        headImgGame = (RoundedImageView) super.findViewById(R.id.head_img_game);
        nickNameGame = (TextView) super.findViewById(R.id.nick_name_game);
        timeLine = (RecyclerView) super.findViewById(R.id.time_line);

        context = GameHistory.this;
        titleText.setText(getResources().getText(R.string.game_history));

        init();
    }

    private void init() {
        if (SPPrivateUtils.getBoolean(context, State.isLogin, false)) {
            Glide.with(context)
                    .load(SPPrivateUtils.get(context, State.headImg, State.headImg))
                    .transform(new GlideCircleTransform(context))
                    .into(headImgGame);
            nickNameGame.setText(SPPrivateUtils.getString(context, State.nickName, "请登录"));
        } else {
            headImgGame.setImageResource(R.mipmap.nologin_n);
            nickNameGame.setText("请先登录");
        }

        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        historyAdapter = new HistoryAdapter(context, initTimeList());
        timeLine.setLayoutManager(manager);
        timeLine.setAdapter(historyAdapter);
    }

    private List<Map<String, String>> initTimeList() {
        SimpleDateFormat date = new SimpleDateFormat("aaa  hh:mm");
        SimpleDateFormat format = new SimpleDateFormat("MM.dd");
        DatabaseOperate db = new DatabaseOperate(context);
//        db.deleteAllRecord();
        List<Map<String, String>> list = new ArrayList<>();
        DecimalFormat dFormat = new DecimalFormat("00");
//        for (int i = 0; i < 10; i++) {
//            GameRecord record = new GameRecord();
//            record.setShow_time("03.0" + (i + 1));
//            record.setGame_time("上午" + i + ":" + i * 6);
//            record.setGame_name("平衡大师");
//            record.setGame_use_time(i * 8);
//            record.setCompleteness(i * 9);
//            db.insertGameRecord(record);
//        }
        List<GameRecord> recordList = db.queryRecord();
        for (int i = 0; i < recordList.size(); i++) {
            Map<String, String> map = new HashMap<>();
//            map.put("show_time", format.format(new Date()));
//            map.put("game_time", date.format(new Date()));
            map.put("show_time", recordList.get(i).getShow_time());
            map.put("game_time", recordList.get(i).getGame_time());
            map.put("game_name", recordList.get(i).getGame_name());
            map.put("game_use_time", "游戏时间" + recordList.get(i).getGame_use_time() + "s");
            map.put("completeness", "完成度" + recordList.get(i).getCompleteness() + "%");
            list.add(map);
        }
        Log.e(TAG, date.format(new Date()));
        return list;
    }

//    private List<Map<String, String>> initTimeList() {
//        SimpleDateFormat date = new SimpleDateFormat("aaa  hh:mm");
//        SimpleDateFormat format = new SimpleDateFormat("MM.dd");
//        List<Map<String, String>> list = new ArrayList<>();
//        DecimalFormat dFormat = new DecimalFormat("00");
//        for (int i = 0; i < 9; i++) {
//            Map<String, String> map = new HashMap<>();
////            map.put("show_time", format.format(new Date()));
////            map.put("game_time", date.format(new Date()));
//            map.put("show_time", "03.0" + (i + 1));
//            map.put("game_time", "上午" + i + ":" + i * 6);
//            map.put("game_name", "平衡大师");
//            map.put("game_use_time", "游戏时间" + dFormat.format(i * 8) + "s");
//            map.put("completeness", "完成度" + dFormat.format(i * 8) + "%");
//            list.add(map);
//        }
//        Log.e(TAG, date.format(new Date()));
//        return list;
//    }

    public void doClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                exit();
                break;
            default:
                break;
        }
    }

    private void exit() {
        finish();
    }
}
