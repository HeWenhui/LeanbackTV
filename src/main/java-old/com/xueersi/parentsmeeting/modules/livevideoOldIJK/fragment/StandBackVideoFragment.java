package com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.business.AppBll;
import com.xueersi.common.business.UserBll;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.analytics.umsagent.UmsAgentTrayPreference;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.ShareDataConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.SpeechBulletScreen.business.SpeechBulletScreenPalyBackBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveStandFrameAnim;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LivePlaybackMediaController;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LivePlaybackStandMediaController;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business.ArtsAnswerResultPlayBackBll;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by linyuqiang on 2018/7/23.
 * 站立直播的回放
 */
public class StandBackVideoFragment extends LiveBackVideoFragment {
    LiveStandFrameAnim liveStandFrameAnim;
    String ACHIEVE_LAYOUT_RIGHT = "1";
    {
        mLayoutVideo = R.layout.fram_live_stand_back_video;
    }
    boolean isInit = false;

    @Override
    protected void initBll() {
        if (isInit) {
            StandBackVideoFragment.super.initBll();
            return;
        }
        isInit = true;
        liveStandFrameAnim = new LiveStandFrameAnim(activity);
        liveStandFrameAnim.check(new AbstractBusinessDataCallBack() {
            @Override
            public void onDataSucess(Object... objData) {
                View vsLiveStandUpdate = mContentView.findViewById(R.id.vs_live_stand_update);
                if (vsLiveStandUpdate != null) {
                    ViewGroup group = (ViewGroup) vsLiveStandUpdate.getParent();
                    group.removeView(vsLiveStandUpdate);
                } else {
                    vsLiveStandUpdate = mContentView.findViewById(R.id.rl_live_stand_update);
                    ViewGroup group = (ViewGroup) vsLiveStandUpdate.getParent();
                    group.removeView(vsLiveStandUpdate);
                }
                Map<String, String> mParams = new HashMap<>();
                mParams.put("logtype", "check_onDataSucess");
                mParams.put("isFinishing", "" + activity.isFinishing());
//                Loger.d(activity, TAG, mParams, true);
                UmsAgentManager.umsAgentDebug(activity, TAG, mParams);
                if (activity.isFinishing()) {
                    return;
                }
                StandBackVideoFragment.super.initBll();
            }
        });
    }

    @Override
    protected LivePlaybackMediaController createLivePlaybackMediaController() {
        LivePlaybackStandMediaController livePlaybackStandMediaController = new LivePlaybackStandMediaController(activity, liveBackPlayVideoFragment, mIsLand.get());
        return livePlaybackStandMediaController;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (liveStandFrameAnim != null) {
            liveStandFrameAnim.onDestroy();
        }
    }
    @Override
    protected void onPlayOpenSuccess() {
        super.onPlayOpenSuccess();
        userHeadVisible();
    }
    protected void userHeadVisible() {
       String LAYOUT_SUMMER_SIZE =  UmsAgentTrayPreference.getInstance().getString(ShareDataConfig.SP_EN_ENGLISH_STAND_SUMMERCOURS_EWARESIZE,"0");

        //直播
        if (liveBackBll.getLiveType() == LiveVideoConfig.LIVE_TYPE_LIVE && ACHIEVE_LAYOUT_RIGHT.equals(LAYOUT_SUMMER_SIZE) && !isTutorVideo) {
            // 英语
            if (liveBackBll.getIsArts() == 1 && liveBackBll.getPattern() == 2 && llUserHeadImage!=null) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)llUserHeadImage.getLayoutParams();
                layoutParams.rightMargin = LiveVideoPoint.getInstance().screenWidth -  LiveVideoPoint.getInstance().x4 + SizeUtils.Dp2Px(activity,10);
                layoutParams.topMargin = SizeUtils.Dp2Px(activity,10);
                llUserHeadImage.setLayoutParams(layoutParams);
                llUserHeadImage.setVisibility(View.VISIBLE);
                ImageLoader.with(activity).load(UserBll.getInstance().getMyUserInfoEntity().getHeadImg()).into(civUserHeadImage);

            }
        }  else if( llUserHeadImage!=null){
            llUserHeadImage.setVisibility(View.GONE);

        }
    }
}
