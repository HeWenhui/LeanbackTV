package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoActivity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RankUserEntity;
import com.xueersi.parentsmeeting.modules.loginregisters.business.UserBll;
import com.xueersi.xesalib.utils.uikit.ScreenUtils;
import com.xueersi.xesalib.utils.uikit.SizeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tang on 2018/1/3.
 */

public class AnswerRankBll {
    private RelativeLayout bottomContent;
    private Context mContext;
    private LinearLayout llRankList;
    private LinearLayout llCurRow;
    private List<RankUserEntity> mLst;
    private int videoWidth,videoHeight;
    public AnswerRankBll(Context context,RelativeLayout bottomContent){
        mContext=context;
        this.bottomContent=bottomContent;
        mLst=new ArrayList<>();
    }
    public void showRankList(List<RankUserEntity> lst){
        if(llRankList==null){
            llRankList=new LinearLayout(mContext);
            llRankList.setOrientation(LinearLayout.VERTICAL);
            RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            llRankList.setLayoutParams(params);
            llRankList.setBackgroundColor(Color.parseColor("#343b46"));
            TextView textView=new TextView(mContext);
            textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setGravity(Gravity.CENTER);
            textView.setText("答题进行中...");
            textView.setBackgroundColor(Color.parseColor("#1affffff"));
            textView.setTextColor(Color.parseColor("#ffffff"));
            textView.setTextSize(13);
            textView.setPadding(0,SizeUtils.Dp2Px(mContext,3),0,SizeUtils.Dp2Px(mContext,3));
            llRankList.addView(textView);
            bottomContent.addView(llRankList,params);
        }
        for(int i=mLst.size();i<lst.size();i++){
            if(i%2==0){
                LinearLayout linearLayout=new LinearLayout(mContext);
                LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0,SizeUtils.Dp2Px(mContext,7), 0,0);
                linearLayout.setPadding(0,0,SizeUtils.Dp2Px(mContext,5),0);
                linearLayout.setLayoutParams(params);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                llRankList.addView(linearLayout);
                llCurRow=linearLayout;
            }
            llCurRow.addView(getItemView(lst.get(i),i));
        }
    }
    private View getItemView(RankUserEntity entity,int i){
        View root=View.inflate(mContext, R.layout.item_live_rank_list,null);
        ImageView ivCrown=(ImageView)root.findViewById(R.id.iv_live_rank_list_crown);
        switch (i){
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
        ImageView ivHead=(ImageView)root.findViewById(R.id.iv_live_rank_list_head);
        if(entity.getId().equals(UserBll.getInstance().getMyUserInfoEntity().getStuId())){
            ivHead.setImageResource(R.drawable.livevideo_ic_hands_me);
        }else{
            ivHead.setImageResource(R.drawable.livevideo_ic_hands_normal);
        }
        TextView textView=(TextView)root.findViewById(R.id.tv_live_rank_list_name);
        textView.setText(entity.getName());

        int wradio=0,topMargin=0;
        int width=videoWidth==0?ScreenUtils.getScreenWidth():videoWidth;
        if (width > 0) {
            wradio = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * width / LiveVideoActivity.VIDEO_WIDTH);
            wradio += (ScreenUtils.getScreenWidth() - width) / 2;
        }
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(
                (wradio-SizeUtils.Dp2Px(mContext,5))/2
                , ViewGroup.LayoutParams.WRAP_CONTENT);
        //params.weight=1;
        root.setLayoutParams(params);
        return root;
    }
    public void setVideoLayout(int width,int height){
        if(videoWidth==width&&videoHeight==height){
            return;
        }
        videoHeight=height;
        videoWidth=width;
        int screenWidth = ScreenUtils.getScreenWidth();
        int screenHeight = ScreenUtils.getScreenHeight();
        int wradio=0,topMargin=0,bottomMargin=0;
        if (width > 0) {
            wradio = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * width / LiveVideoActivity.VIDEO_WIDTH);
            wradio += (screenWidth - width) / 2;
        }
        if (height > 0) {
            topMargin = (int) ((LiveVideoActivity.VIDEO_HEIGHT - LiveVideoActivity.VIDEO_HEAD_HEIGHT) * height /
                    LiveVideoActivity.VIDEO_HEIGHT);
            topMargin = height - topMargin + (screenHeight - height) / 2;
            topMargin=screenHeight-topMargin;
            bottomMargin=(screenHeight-videoHeight)/2;
        }
        if(llRankList!=null){
            RelativeLayout.LayoutParams params=(RelativeLayout.LayoutParams) llRankList.getLayoutParams();
            params.width=wradio;
            params.height=topMargin;
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.setMargins(0,0,0,bottomMargin);
            llRankList.setLayoutParams(params);
        }
    }
}
