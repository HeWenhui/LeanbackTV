package com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xueersi.common.util.FontCache;
import com.xueersi.parentsmeeting.modules.livevideo.R;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author linyuqiang
 * @date 2019/2/23
 * 语音评测结果页-站立直播
 */
public class StandSpeechResult {

    /**
     * 帧动画自己头像那一部分
     *
     * @param mContext
     * @param userName
     * @return
     */
    public static View resultViewName(Context mContext, String strGold, Typeface fontFace, String userName) {
        View layout_live_stand_red_mine1 = LayoutInflater.from(mContext).inflate(R.layout
                .layout_live_stand_red_mine2, null);
        TextView tv_livevideo_redpackage_name = layout_live_stand_red_mine1.findViewById(R.id
                .tv_livevideo_redpackage_name);
        tv_livevideo_redpackage_name.setText("" + userName);
        TextView tv_livevideo_redpackage_num = layout_live_stand_red_mine1.findViewById(R.id
                .tv_livevideo_redpackage_num);
        ImageView iv_livevideo_redpackage_num = layout_live_stand_red_mine1.findViewById(R.id
                .iv_livevideo_redpackage_num);
        tv_livevideo_redpackage_num.setText(strGold);
        tv_livevideo_redpackage_name.setTextSize(TypedValue.COMPLEX_UNIT_PX, 23f);
        tv_livevideo_redpackage_num.setTextSize(TypedValue.COMPLEX_UNIT_PX, 18f);
        tv_livevideo_redpackage_name.setTextColor(0xff97091D);
        tv_livevideo_redpackage_num.setTextColor(0xff97091D);
        tv_livevideo_redpackage_name.setTypeface(fontFace);
        iv_livevideo_redpackage_num.setImageResource(R.drawable.bg_live_stand_red_gold_big);
        int width = 122;
        int height = 72;
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
        layout_live_stand_red_mine1.measure(widthMeasureSpec, heightMeasureSpec);
        layout_live_stand_red_mine1.layout(0, 0, width, height);
        return layout_live_stand_red_mine1;
    }

    /**
     * 帧动画自己头像那一部分-不带金币
     *
     * @param mContext
     * @param userName
     * @return
     */
    public static View resultViewNameEnergy(Context mContext, String userName) {
        Typeface fontFace = FontCache.getTypeface(mContext, "fangzhengcuyuan.ttf");
        View layout_live_stand_red_mine1 = LayoutInflater.from(mContext).inflate(R.layout
                .layout_live_stand_speech_head, null);
        TextView tv_livevideo_redpackage_name = layout_live_stand_red_mine1.findViewById(R.id
                .tv_livevideo_redpackage_name);
        tv_livevideo_redpackage_name.setText("" + userName);
        tv_livevideo_redpackage_name.setTextSize(TypedValue.COMPLEX_UNIT_PX, 23f);
        tv_livevideo_redpackage_name.setTextColor(0xff97091D);
        tv_livevideo_redpackage_name.setTypeface(fontFace);
        int width = 122;
        int height = 72;
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
        layout_live_stand_red_mine1.measure(widthMeasureSpec, heightMeasureSpec);
        layout_live_stand_red_mine1.layout(0, 0, width, height);
        return layout_live_stand_red_mine1;
    }

    /**
     * 结果页金币能量那一部分
     *
     * @param mContext
     * @param group
     * @param gold
     * @param energy
     * @param score
     * @return
     */
    public static View resultViewScore(Context mContext, ViewGroup group, int gold, int energy, int score) {
        final View resultMine = LayoutInflater.from(mContext).inflate(R.layout.layout_livevideo_stand_speech_mine,
                group, false);
        InputStream inputStream = null;
        try {
            inputStream = mContext.getAssets().open
                    ("live_stand/frame_anim/redpackage/9_teams_bg/package_team_bg_00035.png");
            final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            resultMine.setBackgroundDrawable(new BitmapDrawable(bitmap));
            resultMine.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {

                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                    bitmap.recycle();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        LinearLayout llLivevideoSpeectevalResultMine = resultMine.findViewById(R.id
                .ll_livevideo_speecteval_result_mine);
//        bg_livevideo_speecteval_result_number_0
        //有用，误删
        int[] scoreRes = {R.drawable.bg_livevideo_speecteval_result_number_0,
                R.drawable.bg_livevideo_speecteval_result_number_1,
                R.drawable.bg_livevideo_speecteval_result_number_2,
                R.drawable.bg_livevideo_speecteval_result_number_3,
                R.drawable.bg_livevideo_speecteval_result_number_4,
                R.drawable.bg_livevideo_speecteval_result_number_5,
                R.drawable.bg_livevideo_speecteval_result_number_6,
                R.drawable.bg_livevideo_speecteval_result_number_7,
                R.drawable.bg_livevideo_speecteval_result_number_8,
                R.drawable.bg_livevideo_speecteval_result_number_9,};
        for (int i = 0; i < ("" + score).length(); i++) {
            char c = ("" + score).charAt(i);
            ImageView imageView = new ImageView(mContext);
            int res = -1;
            if (c - '0' < scoreRes.length) {
                res = scoreRes[c - '0'];
            }
            if (res == -1) {
                String name = "bg_livevideo_speecteval_result_number_" + c;
                imageView.setImageResource(mContext.getResources().getIdentifier(name, "drawable", mContext
                        .getPackageName()));
            } else {
                imageView.setImageResource(res);
            }
            llLivevideoSpeectevalResultMine.addView(imageView);
        }
        ImageView imageViewScore = new ImageView(mContext);
        imageViewScore.setImageResource(R.drawable.bg_livevideo_speecteval_result_number_unit);
        llLivevideoSpeectevalResultMine.addView(imageViewScore);
        return resultMine;
    }

    public static View resultViewScoreEnergy(Context mContext, ViewGroup group, int gold, int energy, int score) {
        final View resultMine = LayoutInflater.from(mContext).inflate(R.layout.layout_livevideo_stand_speech_mine_energy,
                group, false);
        TextView tv_livevideo_speecteval_result_gold = resultMine.findViewById(R.id.tv_livevideo_speecteval_result_gold);
        tv_livevideo_speecteval_result_gold.setText("+" + gold);
        TextView tv_livevideo_speecteval_result_energy = resultMine.findViewById(R.id.tv_livevideo_speecteval_result_energy);
        tv_livevideo_speecteval_result_energy.setText("+" + energy);
        InputStream inputStream = null;
        try {
            inputStream = mContext.getAssets().open
                    ("live_stand/frame_anim/redpackage/9_teams_bg/package_team_bg_00035.png");
            final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            resultMine.setBackgroundDrawable(new BitmapDrawable(bitmap));
            resultMine.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {

                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                    bitmap.recycle();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        LinearLayout llLivevideoSpeectevalResultMine = resultMine.findViewById(R.id
                .ll_livevideo_speecteval_result_mine);
//        bg_livevideo_speecteval_result_number_0
        //有用，误删
        int[] scoreRes = {R.drawable.bg_livevideo_speecteval_result_number_0,
                R.drawable.bg_livevideo_speecteval_result_number_1,
                R.drawable.bg_livevideo_speecteval_result_number_2,
                R.drawable.bg_livevideo_speecteval_result_number_3,
                R.drawable.bg_livevideo_speecteval_result_number_4,
                R.drawable.bg_livevideo_speecteval_result_number_5,
                R.drawable.bg_livevideo_speecteval_result_number_6,
                R.drawable.bg_livevideo_speecteval_result_number_7,
                R.drawable.bg_livevideo_speecteval_result_number_8,
                R.drawable.bg_livevideo_speecteval_result_number_9,};
        for (int i = 0; i < ("" + score).length(); i++) {
            char c = ("" + score).charAt(i);
            ImageView imageView = new ImageView(mContext);
            int res = -1;
            if (c - '0' < scoreRes.length) {
                res = scoreRes[c - '0'];
            }
            if (res == -1) {
                String name = "bg_livevideo_speecteval_result_number_" + c;
                imageView.setImageResource(mContext.getResources().getIdentifier(name, "drawable", mContext
                        .getPackageName()));
            } else {
                imageView.setImageResource(res);
            }
            llLivevideoSpeectevalResultMine.addView(imageView);
        }
        ImageView imageViewScore = new ImageView(mContext);
        imageViewScore.setImageResource(R.drawable.bg_livevideo_speecteval_result_number_unit);
        llLivevideoSpeectevalResultMine.addView(imageViewScore);
        return resultMine;
    }
}
