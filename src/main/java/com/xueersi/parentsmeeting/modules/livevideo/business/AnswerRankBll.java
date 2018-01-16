package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.support.constraint.solver.widgets.ConstraintHorizontalLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xueersi.parentsmeeting.http.HttpCallBack;
import com.xueersi.parentsmeeting.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoActivity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.FullMarkListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RankUserEntity;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.widget.SlowHorizontalScrollView;
import com.xueersi.parentsmeeting.modules.loginregisters.business.UserBll;
import com.xueersi.xesalib.adapter.RCommonAdapter;
import com.xueersi.xesalib.adapter.RItemViewInterface;
import com.xueersi.xesalib.adapter.ViewHolder;
import com.xueersi.xesalib.utils.uikit.ScreenUtils;
import com.xueersi.xesalib.utils.uikit.SizeUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * Created by Tang on 2018/1/3.
 * 领奖台相关业务类
 */

public class AnswerRankBll {
    private RelativeLayout bottomContent;
    private Context mContext;
    private LinearLayout llRankList;
    private LinearLayout llCurRow;
    private List<RankUserEntity> mLst;
    private int displayWidth, displayHeight, videoWidth;
    private View rlFullMarkList;
    private HttpCallBack fullMarkListCallBack;
    private LiveHttpManager mLiveHttpManager;
    private String classId;
    private String teamId;
    private String testId;
    private String type;
    private String isShow;
    private SoundPool mSoundPool;
    private TextView tvStatus;
    private RCommonAdapter mAdapter;

    public String getIsShow() {
        return isShow;
    }

    public void setIsShow(String isShow) {
        this.isShow = isShow;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setLiveHttpManager(LiveHttpManager liveHttpManager) {
        mLiveHttpManager = liveHttpManager;
    }

    public AnswerRankBll(Context context, RelativeLayout bottomContent) {
        mContext = context;
        this.bottomContent = bottomContent;
        mLst = new ArrayList<>();
        setVideoLayout(ScreenUtils.getScreenWidth(), ScreenUtils.getScreenHeight());
        fullMarkListCallBack=new HttpCallBack() {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                List<FullMarkListEntity> lst=null;
                try {
                    JSONObject jsonObject=(JSONObject) responseEntity.getJsonObject();
                    JSONObject data=JSON.parseObject(jsonObject.getString("ranks"));
                    lst = JSON.parseArray(data.getString("ranks"), FullMarkListEntity.class);
                }catch (Exception e){
                    e.printStackTrace();
                }
                if(lst==null){
                    lst=new ArrayList<>();
                }
                showFullMarkList(lst);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                super.onPmError(responseEntity);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                super.onFailure(call, e);
            }
        };
    }

    /**
     * 显示上墙列表
     * @param lst
     */
    public void showRankList(final List<RankUserEntity> lst) {
        if(mSoundPool==null) {
            mSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
            mSoundPool.load(mContext, R.raw.sound_full_mark_list, 1);
        }
        if("0".equals(isShow)){
            return;
        }
        if (lst.size() < mLst.size()) {
            return;
        }
        bottomContent.setClickable(true);
        if (llRankList == null) {
            llRankList = new LinearLayout(mContext);
            llRankList.setClickable(true);
            llRankList.setOrientation(LinearLayout.VERTICAL);
            int topMargin = (int) ((LiveVideoActivity.VIDEO_HEIGHT - LiveVideoActivity.VIDEO_HEAD_HEIGHT) * displayHeight /
                    LiveVideoActivity.VIDEO_HEIGHT);
            topMargin = displayHeight - topMargin + (ScreenUtils.getScreenHeight() - displayHeight) / 2;
            topMargin = ScreenUtils.getScreenHeight() - topMargin;
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(displayWidth-videoWidth, topMargin);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.setMargins(0,0,0,(ScreenUtils.getScreenHeight()-displayHeight)/2);
            llRankList.setLayoutParams(params);
            llRankList.setBackgroundColor(Color.parseColor("#343b46"));
            tvStatus = new TextView(mContext);
            tvStatus.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvStatus.setGravity(Gravity.CENTER);
            tvStatus.setText("答题进行中...");
            tvStatus.setBackgroundColor(Color.parseColor("#1affffff"));
            tvStatus.setTextColor(Color.parseColor("#ffffff"));
            tvStatus.setTextSize(13);
            tvStatus.setPadding(0, SizeUtils.Dp2Px(mContext, 3), 0, SizeUtils.Dp2Px(mContext, 3));
            llRankList.addView(tvStatus);
            llRankList.addView(getRecyclerView());
            bottomContent.addView(llRankList,1, params);
        }
        ((Activity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = mLst.size(); i < lst.size(); i++) {
                    /*if (i % 2 == 0) {
                        LinearLayout linearLayout = new LinearLayout(mContext);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.setMargins(0, SizeUtils.Dp2Px(mContext, 7), 0, 0);
                        linearLayout.setPadding(0, 0, SizeUtils.Dp2Px(mContext, 5), 0);
                        linearLayout.setLayoutParams(params);
                        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                        llRankList.addView(linearLayout);
                        llCurRow = linearLayout;
                    }
                    llCurRow.addView(getRankListItemView(lst.get(i), i));*/
                    mLst.add(lst.get(i));
                }
                mAdapter.notifyDataSetChanged();
            }
        });

    }

    /**
     * 隐藏上墙列表
     */
    public void hideRankList() {
        if (bottomContent != null && llRankList != null) {
            try {
                llRankList.removeAllViews();
                bottomContent.removeView(llRankList);
                llRankList=null;
                mAdapter=null;
                bottomContent.setClickable(false);
                mLst.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private View getRecyclerView(){
        RecyclerView recyclerView=new RecyclerView(mContext);
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        recyclerView.setLayoutParams(params);
        recyclerView.setLayoutManager(new GridLayoutManager(mContext,2));
        mAdapter=new RCommonAdapter<>(mContext,mLst);
        mAdapter.addItemViewDelegate(new RankItem());
        recyclerView.setAdapter(mAdapter);
        return recyclerView;
    }
    private class RankItem implements RItemViewInterface<RankUserEntity>{
        ImageView ivCrown;
        TextView tvName;
        ImageView ivHead;
        @Override
        public int getItemLayoutId() {
            return R.layout.item_live_rank_list;
        }

        @Override
        public boolean isShowView(RankUserEntity item, int position) {
            return true;
        }

        @Override
        public void initView(ViewHolder holder, int position) {
            ivCrown = (ImageView) holder.getView(R.id.iv_live_rank_list_crown);
            tvName=(TextView)holder.getView(R.id.tv_live_rank_list_name);
            ivHead=(ImageView)holder.getView(R.id.iv_live_rank_list_head);
        }

        @Override
        public void convert(ViewHolder holder, RankUserEntity entity, int position) {
            switch (position) {
                case 0:
                    ivCrown.setVisibility(View.VISIBLE);
                    ivCrown.setImageResource(R.drawable.livevideo_ic_first_normal);
                    break;
                case 1:
                    ivCrown.setVisibility(View.VISIBLE);
                    ivCrown.setImageResource(R.drawable.livevideo_ic_second_normal);
                    break;
                case 2:
                    ivCrown.setVisibility(View.VISIBLE);
                    ivCrown.setImageResource(R.drawable.livevideo_ic_third_normal);
                    break;
                default:
                    ivCrown.setVisibility(View.GONE);
                    break;
            }
            if (entity.getId().equals(UserBll.getInstance().getMyUserInfoEntity().getStuId())) {
                ivHead.setImageResource(R.drawable.livevideo_ic_hands_me);
                tvName.setTextColor(Color.parseColor("#ffedce"));
            } else {
                ivHead.setImageResource(R.drawable.livevideo_ic_hands_normal);
                tvName.setTextColor(Color.parseColor("#ffffff"));
            }
            tvName.setText(entity.getName());
        }
    }
    /**
     * 显示满分榜视图
     * @param lst
     */
    public void showFullMarkList(List<FullMarkListEntity> lst) {
        if(rlFullMarkList!=null){
            return;
        }
        rlFullMarkList = View.inflate(mContext, R.layout.layout_full_mark_list, null);
        //设置四个榜单区域参数
        int dp11=SizeUtils.Dp2Px(mContext,11);
        LinearLayout.LayoutParams llParam=new LinearLayout.LayoutParams(videoWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.setMargins(0,dp11,0,0);
        LinearLayout ll1 = (LinearLayout) rlFullMarkList.findViewById(R.id.ll_full_mark_list_no1_line);
        LinearLayout ll2 = (LinearLayout) rlFullMarkList.findViewById(R.id.ll_full_mark_list_no2_line);
        ll2.setPadding(dp11,0,dp11,0);
        LinearLayout ll3 = (LinearLayout) rlFullMarkList.findViewById(R.id.ll_full_mark_list_no3_line);
        LinearLayout ll4 = (LinearLayout) rlFullMarkList.findViewById(R.id.ll_full_mark_list_no4_line);
        ll4.setPadding(dp11,0,dp11,0);
        ll1.setLayoutParams(llParam);
        ll2.setLayoutParams(llParam);
        ll3.setLayoutParams(llParam);
        ll4.setLayoutParams(llParam);
        //scrollview禁止滚动
        final SlowHorizontalScrollView sv=(SlowHorizontalScrollView) rlFullMarkList.findViewById(R.id.sv_live_full_mark_list);
        sv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        //前三名显示区域
        TextView[] trophys=setTrophyParam();
        if(trophys==null||trophys.length==0){
            trophys=new TextView[3];
        }
        for (int i = 0; i < lst.size(); i++) {
            if (i == 0) {
                trophys[0].setText(lst.get(i).getStuName()+"\n"+lst.get(i).getAnswer_time());
                continue;
            }
            if (i == 1) {
                trophys[1].setText(lst.get(i).getStuName()+"\n"+lst.get(i).getAnswer_time());
                continue;
            }
            if (i == 2) {
                trophys[2].setText(lst.get(i).getStuName()+"\n"+lst.get(i).getAnswer_time());
                continue;
            }
            View v=new View(mContext);
            LinearLayout.LayoutParams vParams =new LinearLayout.LayoutParams(0,1);
            vParams.weight=1;
            v.setLayoutParams(vParams);
            if (i < 9) {
                ll1.addView(getFullMarkListItem(lst.get(i)));
                ll1.addView(v);
            } else if (i < 14) {
                ll2.addView(getFullMarkListItem(lst.get(i)));
                ll2.addView(v);
            } else if (i < 20) {
                ll3.addView(getFullMarkListItem(lst.get(i)));
                ll3.addView(v);
            } else if(i<25){
                ll4.addView(getFullMarkListItem(lst.get(i)));
                ll4.addView(v);
            }
        }
        //设置主视图参数
        RelativeLayout.LayoutParams mainParam=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mainParam.setMargins(0,(ScreenUtils.getScreenHeight()-displayHeight)/2,displayWidth-videoWidth,(ScreenUtils.getScreenHeight()-displayHeight)/2);
        mainParam.addRule(RelativeLayout.CENTER_VERTICAL);
        rlFullMarkList.setLayoutParams(mainParam);
        if(lst.size()>14){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    sv.smoothScrollToSlow(videoWidth,0,500);
                }
            },2000);
        }

        bottomContent.addView(rlFullMarkList);
        if(tvStatus!=null){
            tvStatus.setText("答题结束");
        }
        playVoice();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideFullMarkList();
            }
        },4500);
    }
    /**
     * 隐藏满分榜视图
     */
    public void hideFullMarkList(){
        if(bottomContent!=null&& rlFullMarkList !=null){
            try{
                bottomContent.removeView(rlFullMarkList);
                rlFullMarkList =null;
                hideRankList();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 满分榜气泡视图
     * @param entity
     * @return
     */
    private View getFullMarkListItem(FullMarkListEntity entity){
        TextView tv = new TextView(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(SizeUtils.Dp2Px(mContext,60), ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(SizeUtils.Dp2Px(mContext, 0), 0, SizeUtils.Dp2Px(mContext, 0), 0);
        tv.setLayoutParams(params);
        tv.setGravity(Gravity.CENTER);
        tv.setEllipsize(TextUtils.TruncateAt.END);
        tv.setPadding(SizeUtils.Dp2Px(mContext, 5), SizeUtils.Dp2Px(mContext, 1), SizeUtils.Dp2Px(mContext, 5), SizeUtils.Dp2Px(mContext, 1));
        if(entity.getId().equals(UserBll.getInstance().getMyUserInfoEntity().getStuId())) {
            tv.setBackgroundResource(R.drawable.shape_corners_10dp_7f8cd1);
        }else{
            tv.setBackgroundResource(R.drawable.shape_corners_10dp_b0c7de);
        }
        tv.setTextColor(Color.parseColor("#ffffff"));
        tv.setMaxLines(1);
        tv.setMaxEms(4);
        //tv.setMaxWidth((videoWidth-12*SizeUtils.Dp2Px(mContext,8)/6));
        tv.setTextSize(11);
        tv.setText(entity.getStuName());
        return tv;
    }



    /**
     * 播放器区域变化时更新视图
     * @param width
     * @param height
     */
    public void setVideoLayout(int width, int height) {
        if (displayWidth == width && displayHeight == height) {
            return;
        }
        displayHeight = height;
        displayWidth = width;

        int screenWidth = ScreenUtils.getScreenWidth();
        int screenHeight = ScreenUtils.getScreenHeight();
        int wradio = 0, topMargin = 0, bottomMargin = 0;
        if (width > 0) {
            wradio = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * width / LiveVideoActivity.VIDEO_WIDTH);
            wradio += (screenWidth - width) / 2;
            videoWidth = displayWidth - wradio;
        }
        if(rlFullMarkList !=null){
            RelativeLayout.LayoutParams params=(RelativeLayout.LayoutParams) rlFullMarkList.getLayoutParams();
            params.height=displayHeight;
            params.width=videoWidth;
            rlFullMarkList.setLayoutParams(params);
        }
        if (height > 0) {
            topMargin = (int) ((LiveVideoActivity.VIDEO_HEIGHT - LiveVideoActivity.VIDEO_HEAD_HEIGHT) * height /
                    LiveVideoActivity.VIDEO_HEIGHT);
            topMargin = height - topMargin + (screenHeight - height) / 2;
            topMargin = screenHeight - topMargin;
            bottomMargin = (screenHeight - displayHeight) / 2;
        }
        if (llRankList != null) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) llRankList.getLayoutParams();
            params.width = wradio;
            params.height = topMargin;
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.setMargins(0, 0, 0, bottomMargin);
            llRankList.setLayoutParams(params);
        }
        setTrophyParam();
    }
    public void getFullMarkListQuestion(HttpCallBack callBack){
        if("1".equals(isShow)) {
            mLiveHttpManager.getFullMarkListQuestion(testId, classId, teamId, callBack);
        }
    }
    public void getFullMarkListTest(HttpCallBack callBack){
        if("1".equals(isShow)) {
            mLiveHttpManager.getFullMarkListTest(classId, teamId, testId, callBack);
        }
    }
    public void getFullMarkListH5(HttpCallBack callBack){
        if("1".equals(isShow)) {
            mLiveHttpManager.getFullMarkListH5(classId, teamId, testId, type, callBack);
        }
    }
    private TextView[] setTrophyParam(){
        if(rlFullMarkList ==null){
            return null;
        }
        TextView tvNo1 = (TextView) rlFullMarkList.findViewById(R.id.tv_full_mark_list_no1);
        TextView tvNo2 = (TextView) rlFullMarkList.findViewById(R.id.tv_full_mark_list_no2);
        TextView tvNo3 = (TextView) rlFullMarkList.findViewById(R.id.tv_full_mark_list_no3);
        tvNo1.setTextSize(10);
        tvNo2.setTextSize(10);
        tvNo3.setTextSize(10);
        tvNo1.setMaxLines(2);
        tvNo2.setMaxLines(2);
        tvNo3.setMaxLines(2);
        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams((int)(videoWidth*0.086f), ViewGroup.LayoutParams.WRAP_CONTENT);
        params1.setMargins((int) (0.115f * videoWidth), 0, 0, (int) (0.148f * displayHeight));
        params1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params1.addRule(RelativeLayout.RIGHT_OF,R.id.tv_full_mark_list_no2);
        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams((int)(videoWidth*0.086f), ViewGroup.LayoutParams.WRAP_CONTENT);
        params2.setMargins((int) (0.255f * videoWidth), 0, 0, (int) (0.12f * displayHeight));
        params2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params2.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams((int)(videoWidth*0.086f), ViewGroup.LayoutParams.WRAP_CONTENT);
        params3.setMargins((int) (0.124f * videoWidth), 0, 0, (int) (0.101f * displayHeight));
        params3.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params3.addRule(RelativeLayout.RIGHT_OF,R.id.tv_full_mark_list_no1);
        tvNo1.setLayoutParams(params1);
        tvNo2.setLayoutParams(params2);
        tvNo3.setLayoutParams(params3);
        return new TextView[]{tvNo1,tvNo2,tvNo3};
    }
    private void playVoice(){
        if(mSoundPool!=null) {
            mSoundPool.play(1, 1, 1, 10, 0, 1);
        }
    }
}
