package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoActivity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.FullMarkListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RankUserEntity;
import com.xueersi.parentsmeeting.modules.loginregisters.business.UserBll;
import com.xueersi.xesalib.utils.uikit.ScreenUtils;
import com.xueersi.xesalib.utils.uikit.SizeUtils;

import java.util.ArrayList;
import java.util.List;

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
    private View root;

    public AnswerRankBll(Context context, RelativeLayout bottomContent) {
        mContext = context;
        this.bottomContent = bottomContent;
        mLst = new ArrayList<>();
        setVideoLayout(ScreenUtils.getScreenWidth(), ScreenUtils.getScreenHeight());
    }

    /**
     * 显示上墙列表
     * @param lst
     */
    public void showRankList(List<RankUserEntity> lst) {
        if (lst.size() != 0 && lst.size() <= mLst.size()) {
            return;
        }
        if (llRankList == null) {
            llRankList = new LinearLayout(mContext);
            llRankList.setOrientation(LinearLayout.VERTICAL);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.setMargins(0,0,0,(ScreenUtils.getScreenHeight()-displayHeight)/2);
            llRankList.setLayoutParams(params);
            llRankList.setBackgroundColor(Color.parseColor("#343b46"));
            TextView textView = new TextView(mContext);
            textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setGravity(Gravity.CENTER);
            textView.setText("答题进行中...");
            textView.setBackgroundColor(Color.parseColor("#1affffff"));
            textView.setTextColor(Color.parseColor("#ffffff"));
            textView.setTextSize(13);
            textView.setPadding(0, SizeUtils.Dp2Px(mContext, 3), 0, SizeUtils.Dp2Px(mContext, 3));
            llRankList.addView(textView);
            bottomContent.addView(llRankList, params);
        }
        for (int i = mLst.size(); i < lst.size(); i++) {
            if (i % 2 == 0) {
                LinearLayout linearLayout = new LinearLayout(mContext);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, SizeUtils.Dp2Px(mContext, 7), 0, 0);
                linearLayout.setPadding(0, 0, SizeUtils.Dp2Px(mContext, 5), 0);
                linearLayout.setLayoutParams(params);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                llRankList.addView(linearLayout);
                llCurRow = linearLayout;
            }
            llCurRow.addView(getRankListItemView(lst.get(i), i));
        }
        mLst = lst;
    }

    /**
     * 隐藏上墙列表
     */
    public void hideRankList() {
        if (bottomContent != null && llRankList != null) {
            try {
                bottomContent.removeView(llRankList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 显示满分榜视图
     * @param lst
     */
    public void showFullMarkList(List<FullMarkListEntity> lst) {
        root = View.inflate(mContext, R.layout.layout_full_mark_list, null);
        //设置四个榜单区域参数
        int dp11=SizeUtils.Dp2Px(mContext,11);
        LinearLayout.LayoutParams llParam=new LinearLayout.LayoutParams(videoWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.setMargins(0,dp11,0,0);
        LinearLayout ll1 = (LinearLayout) root.findViewById(R.id.ll_full_mark_list_no1_line);
        LinearLayout ll2 = (LinearLayout) root.findViewById(R.id.ll_full_mark_list_no2_line);
        ll2.setPadding(dp11,0,dp11,0);
        LinearLayout ll3 = (LinearLayout) root.findViewById(R.id.ll_full_mark_list_no3_line);
        LinearLayout ll4 = (LinearLayout) root.findViewById(R.id.ll_full_mark_list_no4_line);
        ll4.setPadding(dp11,0,dp11,0);
        ll1.setLayoutParams(llParam);
        ll2.setLayoutParams(llParam);
        ll3.setLayoutParams(llParam);
        ll4.setLayoutParams(llParam);
        //scrollview禁止滚动
        final HorizontalScrollView sv=(HorizontalScrollView) root.findViewById(R.id.sv_live_full_mark_list);
        sv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        //前三名显示区域
        TextView tvNo1 = (TextView) root.findViewById(R.id.tv_full_mark_list_no1);
        TextView tvNo2 = (TextView) root.findViewById(R.id.tv_full_mark_list_no2);
        TextView tvNo3 = (TextView) root.findViewById(R.id.tv_full_mark_list_no3);
        tvNo1.setTextSize(11);
        tvNo2.setTextSize(11);
        tvNo3.setTextSize(11);
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
        for (int i = 0; i < lst.size(); i++) {
            if (i == 0) {
                tvNo1.setLayoutParams(params1);
                tvNo1.setText("abc\n3分16秒");
                continue;
            }
            if (i == 1) {
                tvNo2.setLayoutParams(params2);
                tvNo2.setText("abc\n3分17秒");
                continue;
            }
            if (i == 2) {
                tvNo3.setLayoutParams(params3);
                tvNo3.setText("abc\n3分18秒");
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
            } else {
                ll4.addView(getFullMarkListItem(lst.get(i)));
                ll4.addView(v);
            }
        }
        //设置主视图参数
        RelativeLayout.LayoutParams mainParam=new RelativeLayout.LayoutParams(videoWidth,displayHeight);
        mainParam.addRule(RelativeLayout.CENTER_VERTICAL);
        root.setLayoutParams(mainParam);
        if(lst.size()>14){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    sv.smoothScrollTo(videoWidth,0);
                }
            },3000);
        }

        bottomContent.addView(root);
    }
    /**
     * 隐藏满分榜视图
     */
    public void hideFullMarkList(){
        if(bottomContent!=null&&root!=null){
            try{
                bottomContent.removeView(root);
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
        tv.setBackgroundResource(R.drawable.shape_corners_10dp_b0c7de);
        tv.setTextColor(Color.parseColor("#ffffff"));
        tv.setMaxLines(1);
        tv.setMaxEms(4);
        //tv.setMaxWidth((videoWidth-12*SizeUtils.Dp2Px(mContext,8)/6));
        tv.setTextSize(11);
        tv.setText(entity.getStuName());
        return tv;
    }

    /**
     * 上墙列表item视图
     * @param entity
     * @param i
     * @return
     */
    private View getRankListItemView(RankUserEntity entity, int i) {
        View root = View.inflate(mContext, R.layout.item_live_rank_list, null);
        ImageView ivCrown = (ImageView) root.findViewById(R.id.iv_live_rank_list_crown);
        switch (i) {
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
        TextView textView = (TextView) root.findViewById(R.id.tv_live_rank_list_name);
        textView.setText(entity.getName());
        ImageView ivHead = (ImageView) root.findViewById(R.id.iv_live_rank_list_head);
        if (entity.getId().equals(UserBll.getInstance().getMyUserInfoEntity().getStuId())) {
            ivHead.setImageResource(R.drawable.livevideo_ic_hands_me);
            textView.setTextColor(Color.parseColor("#20abff"));
        } else {
            ivHead.setImageResource(R.drawable.livevideo_ic_hands_normal);
            textView.setTextColor(Color.parseColor("#ffffff"));
        }


        int wradio = 0, topMargin = 0;
        int width = displayWidth == 0 ? ScreenUtils.getScreenWidth() : displayWidth;
        if (width > 0) {
            wradio = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * width / LiveVideoActivity.VIDEO_WIDTH);
            wradio += (ScreenUtils.getScreenWidth() - width) / 2;
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                (wradio - SizeUtils.Dp2Px(mContext, 5)) / 2
                , ViewGroup.LayoutParams.WRAP_CONTENT);
        //params.weight=1;
        root.setLayoutParams(params);
        return root;
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
        if(root!=null){
            RelativeLayout.LayoutParams params=(RelativeLayout.LayoutParams) root.getLayoutParams();
            params.height=displayHeight;
            params.width=videoWidth;
            root.setLayoutParams(params);
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
    }
}
