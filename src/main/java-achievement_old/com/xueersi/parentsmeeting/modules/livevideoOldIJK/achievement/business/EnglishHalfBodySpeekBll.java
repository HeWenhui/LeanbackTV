package com.xueersi.parentsmeeting.modules.livevideoOldIJK.achievement.business;

import android.app.Activity;
import android.widget.RelativeLayout;

import com.tal.speech.language.TalLanguage;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.AudioRequest;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 半身直播 英语大声说
 * @author chenkun
 * @version 1.0, 2018/10/25 下午3:07
 */

public class EnglishHalfBodySpeekBll extends BaseEnglishStandSpeekBll implements EnglishSpeekAction{

    Activity activity;

    public EnglishHalfBodySpeekBll(Activity activity){

        this.activity = activity;

    }


    @Override
    public void onDBStart() {

    }

    @Override
    public void onDBStop() {

    }

    @Override
    public TalLanguage getTalLanguage() {
        return null;
    }

    @Override
    public void praise(int answer) {

    }

    @Override
    public void remind(int answer) {

    }

    @Override
    public void onModeChange(String mode, boolean audioRequest) {

    }

    @Override
    public void start() {

    }

    @Override
    public void stop(AudioRequest.OnAudioRequest onAudioRequest) {

    }

    @Override
    public void destory() {

    }

    public boolean initView(RelativeLayout mRootView, String mode, TalLanguage talLanguage, AtomicBoolean audioRequest, RelativeLayout mContentView) {
        return false;
    }

    public void setTotalOpeningLength(LiveGetInfo.TotalOpeningLength totalOpeningLength) {

    }

    public void setLiveBll(LiveAchievementIRCBll liveAchievementIRCBll) {

    }

    public void setmShareDataManager(ShareDataManager mShareDataManager) {

    }
}
