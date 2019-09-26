package com.xueersi.parentsmeeting.modules.livevideo.fragment;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveStandFrameAnim;

import java.util.HashMap;
import java.util.Map;

public class ExperStandRecordFragmentBase extends ExperienceRecordFragmentBase {
    private String TAG = "ExperStandRecordFragmentBase";

    {
        mLayoutVideo = R.layout.frag_exper_stand_live_back_video;
    }

    LiveStandFrameAnim liveStandFrameAnim;
    boolean isInit = false;

    @Override
    protected void initlizeBlls() {
        if (isInit) {
            ExperStandRecordFragmentBase.super.initlizeBlls();
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
                ExperStandRecordFragmentBase.super.initlizeBlls();
            }
        });
    }

    @Override
    protected void addBusiness(Activity activity) {
        super.addBusiness(activity);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (liveStandFrameAnim != null) {
            liveStandFrameAnim.onDestroy();
            liveStandFrameAnim = null;
        }
    }
}
