package com.xueersi.parentsmeeting.modules.livevideoOldIJK.lecadvert.business;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;
import android.view.View;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.framework.utils.JsonUtil;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.media.VideoView;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LecAdvertEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.MoreChoice;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.page.LecAdvertPager;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by linyuqiang on 2018/7/18.
 * 直播回放讲座
 */
public class LecAdvertPlayBackBll extends LiveBackBaseBll implements LecBackAdvertHttp {
    LecBackAdvertBll lecAdvertAction;
    LecBackAdvertPopBll lecBackAdvertPopBll;

    public LecAdvertPlayBackBll(Activity activity, LiveBackBll liveBackBll) {
        super(activity, liveBackBll);
        lecAdvertAction = new LecBackAdvertBll(activity);
        lecBackAdvertPopBll = new LecBackAdvertPopBll(activity);
        lecAdvertAction.setLecBackAdvertPopBll(lecBackAdvertPopBll);
        lecAdvertAction.getLecAdvertPager();
    }

    @Override
    public void onCreate(VideoLivePlayBackEntity mVideoEntity, LiveGetInfo liveGetInfo, HashMap<String, Object> businessShareParamMap) {
        lecAdvertAction.setmVideoEntity(mVideoEntity);
        lecAdvertAction.setLecBackAdvertHttp(this);
        lecBackAdvertPopBll.setmVideoEntity(mVideoEntity);
        lecBackAdvertPopBll.setLecBackAdvertHttp(this);
        VideoView videoView = (VideoView) businessShareParamMap.get("videoView");
        lecBackAdvertPopBll.setVideoView(videoView);
    }

    @Override
    public void initView() {
        lecAdvertAction.initView(mRootView, mIsLand);
        lecBackAdvertPopBll.initView(mRootView, mIsLand);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        lecBackAdvertPopBll.onConfigurationChanged(newConfig);
    }

    public LecAdvertPager getLecAdvertPager() {
        return lecAdvertAction.getLecAdvertPager();
    }

    @Override
    protected void onRestart() {
        lecBackAdvertPopBll.onRestart();
    }

    @Override
    protected void onStop() {
        lecBackAdvertPopBll.onStop();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        lecBackAdvertPopBll.onNewIntent(intent);
    }

    @Override
    public int[] getCategorys() {
        return new int[]{LocalCourseConfig.CATEGORY_LEC_ADVERT};
    }

    @Override
    public void showQuestion(VideoQuestionEntity oldQuestionEntity, final VideoQuestionEntity questionEntity, LiveBackBll.ShowQuestion showQuestion) {
        mRootView.setVisibility(View.VISIBLE);
        int vCategory = questionEntity.getvCategory();
        switch (vCategory) {
            case LocalCourseConfig.CATEGORY_LEC_ADVERT: {
                lecAdvertAction.showLecAdvertPager(questionEntity);
            }
            break;
            default:
                break;
        }
    }

    @Override
    public void getAdOnLL(String liveId, final LecAdvertEntity lecAdvertEntity, final AbstractBusinessDataCallBack callBack) {
        String enstuId = LiveAppUserInfo.getInstance().getEnstuId();
        getCourseHttpManager().getAdOnLL(enstuId, liveId, lecAdvertEntity.course_id, new HttpCallBack() {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                int isLearn = jsonObject.optInt("isLearn", 0);
                lecAdvertEntity.isLearn = isLearn;
                if (isLearn == 0) {
                    lecAdvertEntity.limit = jsonObject.optString("limit");
                    lecAdvertEntity.signUpUrl = jsonObject.optString("signUpUrl");
                    lecAdvertEntity.saleName = jsonObject.optString("saleName");
                    lecAdvertEntity.courseId = jsonObject.optString("courseId");
                    lecAdvertEntity.classId = jsonObject.optString("classId");
                }
                callBack.onDataSucess();
            }
        });
    }

    @Override
    public void getMoreCourseChoices(String liveid, final AbstractBusinessDataCallBack getDataCallBack) {
        getCourseHttpManager().getMoreCourseChoices(liveid, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                Log.e("Duncan", "playbackresponseEntity:" + responseEntity);
                MoreChoice choiceEntity = JsonUtil.getEntityFromJson(responseEntity.getJsonObject().toString(), MoreChoice.class);
                if (choiceEntity != null) {
                    getDataCallBack.onDataSucess(choiceEntity);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lecAdvertAction.onDestroy();
        lecBackAdvertPopBll.onDestroy();
    }
}
