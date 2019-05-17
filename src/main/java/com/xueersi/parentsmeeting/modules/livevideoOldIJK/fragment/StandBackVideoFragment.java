package com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.business.AppBll;
import com.xueersi.common.business.UserBll;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
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

    {
        mLayoutVideo = R.layout.fram_live_stand_back_video;
    }

    @Override
    protected void initBll() {
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
            liveStandFrameAnim.onDestory();
        }
    }

    @Override
    protected void addBusiness(Activity activity) {
        super.addBusiness(activity);
        //直播
        if (liveBackBll.getLiveType() == LiveVideoConfig.LIVE_TYPE_LIVE) {
            // 英语
            if (liveBackBll.getIsArts() == 1 && liveBackBll.getPattern() == 2) {
                llUserHeadImage.setVisibility(View.VISIBLE);
                ImageLoader.with(activity).load(UserBll.getInstance().getMyUserInfoEntity().getHeadImg()).into(civUserHeadImage);

            }
        }
    }
}
