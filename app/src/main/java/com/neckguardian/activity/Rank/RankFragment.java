package com.neckguardian.activity.Rank;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.model.User;
import com.neckguardian.R;
import com.neckguardian.activity.Rank.Adapter.RankAdapter;
import com.neckguardian.service.RankUserService;
import com.neckguardian.sign.State;
import com.poliveira.parallaxrecyclerview.ParallaxRecyclerAdapter;
import com.simo.utils.DividerItemDecoration;
import com.simo.utils.NetUtils;
import com.simo.utils.SPPrivateUtils;
import com.simo.utils.T;
import com.simo.utils.UIUtils;
import com.simo.utils.myView.GlideCircleTransform;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 排名的Fragment
 * Created by 孤月悬空 on 2015/12/23.
 */
public class RankFragment extends Fragment {
    private View rankView = null;
    private SwipeRefreshLayout refreshRankLayout = null;
    private RecyclerView recyclerRank = null;
    private RankAdapter rankAdapter = null;
    private TextView titleText = null;
    private RoundedImageView imgHold = null;
    private TextView textHold = null;
    private LinearLayout occupyLayout = null;
    private LinearLayout titleLayout = null;
    private LinearLayout headerLayout = null;

    private String TAG = "RankFragment";
    private List<User> userList;
    private List<User> user_got;
    private List<View> item;
    private Context context;

    private boolean isLoading = false;
    private boolean isActivate = true;

    //自己的那一栏的空间
    private ImageView rankPic = null;
    private TextView ranking = null;
    private RadioButton rankHeart = null;
    private TextView rankNickName = null;
    private TextView rankNeckMax = null;
    private TextView rankCounts = null;
    private LinearLayout rankClickArea = null;

//    private int headImg[] = {R.mipmap.head_img,
//                                R.mipmap.head_img,
//                                R.mipmap.head_img,
//                                R.mipmap.head_img,
//                                R.mipmap.head_img,
//                                R.mipmap.head_img,
//                                R.mipmap.head_img};
//    private String nickName[] = {"Type something", "Type something", "Type something", "Type something", "Type something", "Type something", "Type something"};
//    private int point[] = {12560, 12561, 12562, 12562, 12564, 12565, 12566};
//    private int favour[] = {5, 5, 5, 5, 5, 5, 5};

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        titleText = (TextView) rankView.findViewById(R.id.title_text);
        imgHold = (RoundedImageView) rankView.findViewById(R.id.img_hold);
        textHold = (TextView) rankView.findViewById(R.id.text_hold);
        recyclerRank = (RecyclerView) rankView.findViewById(R.id.recycler_rank);
        refreshRankLayout = (SwipeRefreshLayout) rankView.findViewById(R.id.refresh_rank_layout);
        occupyLayout = (LinearLayout) rankView.findViewById(R.id.occupy_layout);
        titleLayout = (LinearLayout) rankView.findViewById(R.id.title_layout);
        headerLayout = (LinearLayout) rankView.findViewById(R.id.header_layout);

        rankPic = (ImageView) rankView.findViewById(R.id.rank_pic);
        rankNickName = (TextView) rankView.findViewById(R.id.rank_nick_name);
        ranking = (TextView) rankView.findViewById(R.id.ranking);
        rankNeckMax = (TextView) rankView.findViewById(R.id.rank_neck_max);
        rankCounts = (TextView) rankView.findViewById(R.id.rank_counts);
        rankHeart = (RadioButton) rankView.findViewById(R.id.rank_heart);
        rankClickArea = (LinearLayout) rankView.findViewById(R.id.rank_click_area);

        context = getActivity();
        userList = new ArrayList<>();
        item = new ArrayList<>();

        UIUtils.hideTitleBar(this.getActivity(), R.id.fragment_rank_layout);   //如果安卓系统在4.4以上，隐藏状态栏
//        UIUtils.steepToolBar(this.getActivity());   //如果安卓系统在4.4以上，隐藏状态栏

        init();
    }

    float headerLayoutY;

    private void init() {
//        List<User> testList = new ArrayList<>();
//        for (int i = 0; i < 30; i++) {
//            User user = new User();
//            if (i == 0) {
//                user.setNickName("Simo");
//            } else {
//                user.setNickName("这么长的名字塞得下吗");
//            }
//            user.setRankNumber(i + 1);
//            user.setNeckMax(i * 444 + "");
//            user.setCounts(i * 3 + "");
//            testList.add(user);
//        }
        titleText.setText(getResources().getText(R.string.title_rank));
        headerLayoutY = getRawSize(310);
        refreshRankLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                if (isNetEnable()) {
                    if (isLogin()) {
                        mHandler.sendEmptyMessage(GET_USERS);
                    } else {
                        mHandler.sendEmptyMessage(TOAST_1);
                        mHandler.sendEmptyMessage(NOLOADING);
                    }
                } else {
                    mHandler.sendEmptyMessage(NOLOADING);
                }
            }
        });

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());   //获得线性布局管理器
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);   //设置布局管理的组件排列方向为垂直排布

        if (isNetEnable()) {
            if (isLogin()) {
                mHandler.sendEmptyMessage(GET_USERS);
            } else {
                mHandler.sendEmptyMessage(TOAST_1);
            }
        } else {
            mHandler.sendEmptyMessage(TOAST_2);
        }
//        initMySelf(testList);
        recyclerRank.setLayoutManager(linearLayoutManager); //给RecyclerView设置布局管理器
        rankAdapter = new RankAdapter(this.getActivity(), userList);    //创建适配器
        View view = LayoutInflater.from(context).inflate(R.layout.header_rank, recyclerRank, false);
        rankAdapter.setParallaxHeader(view, recyclerRank);
        rankAdapter.setOnParallaxScroll(new ParallaxRecyclerAdapter.OnParallaxScroll() {

            /**
             * 滑动监听
             * @param v     头部所占百分比
             * @param v1    x
             * @param view
             */
            @Override
            public void onParallaxScroll(float v, float v1, View view) {
                Log.i(TAG, "headerLayoutY = " + v1);
                titleLayout.setAlpha(0.2f + v1 / 400);
                occupyLayout.setAlpha((350 - v1) / 350);
                Log.i(TAG, "headerLayoutY - v1 = " + (headerLayoutY - v1));
                headerLayout.setY(headerLayoutY - v1);
            }
        });
        recyclerRank.setAdapter(rankAdapter);       //设置适配器
        recyclerRank.addItemDecoration(new DividerItemDecoration(this.getActivity(), LinearLayoutManager.VERTICAL));    //设置分割线
    }

    private void initMySelf(List<User> users) {
        for (final User user : users) {
            if (user.getNickName().equals("Fiona")) {
                headerLayout.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(user.getPic())
                        .transform(new GlideCircleTransform(context))
                        .into(rankPic);
                if (user.getNickName().length() > 5) {
                    String nickName = user.getNickName().substring(0, 5) + "...";
                    rankNickName.setText(nickName);
                } else {
                    rankNickName.setText(user.getNickName());
                }

                ranking.setText("第" + user.getRankNumber() + "名");
                rankNeckMax.setText(user.getNeckMax());
                if (rankNeckMax.length() < 5) {
                    rankNeckMax.setTextColor(getResources().getColor(R.color.rank_others));
                } else {
                    rankNeckMax.setTextColor(getResources().getColor(R.color.rank_top_three));
                }
                rankCounts.setText(user.getCounts());
                rankHeart.setChecked(true);
                rankClickArea.setClickable(false);
            }
        }
    }


    /**
     * 获取用户信息
     */
    private void getUsers() {
        mHandler.sendEmptyMessage(LOADING);     //发送消息，刷新列表
        Log.w(TAG, "----------------------准备获取用户信息----------------------");
        //获取用户
        RankUserService userService = new RankUserService();
        user_got = userService.getUsers();
        mHandler.sendEmptyMessage(NOLOADING);   //停止刷新
        if (!isActivate) {
            Log.i(TAG, "本Activity已经被关闭，不进行任何操作");
        } else if (user_got == null) {
            Log.e(TAG, "系统异常，请稍后再试");
        } else if (user_got.size() > 0) {
            for (User user : user_got) {
                Log.i(TAG, user.toString());
            }
            mHandler.sendEmptyMessage(SHOWINFO);
        } else {
            Log.e(TAG, "网络异常，请稍后再试");

        }
        Log.i(TAG, "----------------------用户信息获取结束----------------------");
    }

    /**
     * 显示用户信息
     */
    private void showRank() {
        if (!isLoading) {
            userList.clear();
        }
        userList.addAll(user_got);
        initMySelf(userList);
        Log.i(TAG, userList.get(0).getPic());
        Glide.with(context)
                .load(userList.get(0).getPic())
                .transform(new GlideCircleTransform(context))
                .into(imgHold);
        textHold.setText(userList.get(0).getNickName() + "占领了你的封面");

        rankAdapter.notifyDataSetChanged();
    }

    private final static int GET_USERS = 111;
    private final static int SHOWINFO = 222;
    private final static int LOADING = 333;
    private final static int NOLOADING = 444;
    private final static int TOAST_1 = 555;
    private final static int TOAST_2 = 666;
    private Handler mHandler = new MyHandler(this);

    private class MyHandler extends Handler {
        private WeakReference<RankFragment> mFragment;

        public MyHandler(RankFragment rankFragment) {
            mFragment = new WeakReference<RankFragment>(rankFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final RankFragment fragment = mFragment.get();
            if (fragment != null) {
                switch (msg.what) {
                    case GET_USERS:
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                fragment.getUsers();
                            }
                        }).start();
                        break;
                    case SHOWINFO:
                        showRank();
                        break;
                    case LOADING:
                        loading();
                        break;
                    case NOLOADING:
                        noLoading();
                        break;
                    case TOAST_1:
                        T.showShort(context, "请先登录");
                        break;
                    case TOAST_2:
                        T.showShort(context, "无网络连接");
                    default:
                        break;
                }
            }
        }
    }

    /**
     * //使SwipRefreshLayout可以被刷新
     */
    private void loading() {
        if (refreshRankLayout != null && !refreshRankLayout.isRefreshing()) {
            refreshRankLayout.post(new Runnable() {
                @Override
                public void run() {
                    refreshRankLayout.setRefreshing(true);     //使SwipRefreshLayout可以被刷新
                }
            });
        }
    }

    /**
     * 停止刷新
     */
    private void noLoading() {
        if (refreshRankLayout != null && refreshRankLayout.isRefreshing()) {
            refreshRankLayout.setRefreshing(false);
        }
    }

    /**
     * 网络是否可用
     *
     * @return
     */
    private boolean isNetEnable() {
        if (NetUtils.isConnected(context, true)) {
            if (SPPrivateUtils.getBoolean(context, State.useWifi, false)
                    && NetUtils.isWifi(context)) {
                return true;
            } else if (SPPrivateUtils.getBoolean(context, State.useAllNet, true)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 将宽高设为dip
     *
     * @param value
     * @return
     */
    public float getRawSize(float value) {
        Resources res = this.getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, res.getDisplayMetrics());
    }

    /**
     * 是否已经登录
     *
     * @return
     */
    private boolean isLogin() {
        if (SPPrivateUtils.getBoolean(context, State.isLogin, false)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rankView = inflater.inflate(R.layout.fragment_rank, container, false); //设置需要显示的layout，把这个当做Activity来用
        return rankView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isActivate = false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
