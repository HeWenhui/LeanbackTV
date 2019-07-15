package com.xueersi.parentsmeeting.modules.livevideo.dispatcher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.xueersi.common.business.AppBll;
import com.xueersi.common.business.sharebusiness.config.LiveVideoBusinessConfig;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.route.module.moduleInterface.AbsDispatcher;
import com.xueersi.common.route.module.startParam.ParamKey;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoSectionEntity;
import com.xueersi.parentsmeeting.modules.livevideo.LiveVideoEnter;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoLoadActivity;

import org.json.JSONObject;

/**
 * Created by dqq on 2019/7/11.
 */
public class LiveVideoDispatcher extends AbsDispatcher {

    /***直播体验课*/
    public static final int TYPE_EXP_LIVE = 4;

    //TODO 旁听
    public static final int TYPE_AUDIT_CLASS_ROOM = 5;

    public interface LiveNewStatus {
        int LIVE_UNBEGIN = 1;//待开始
        int LIVE_LIVING = 2;//进行中
        int LIVE_WAIT_PLAYBACK = 3;//等待回放
        int LIVE_CAN_PLAYBACK = 4;//未完成
        int LIVE_CAN_PLAYBACK_PLUS = 5;//已完成
    }

    private Activity activity;


    @Override
    public void dispatch(Activity srcActivity, Bundle bundle, int requestCode) {
        if (bundle == null) {
            return;
        }

        if (bundle.containsKey(ParamKey.EXTRAKEY_JSONPARAM)) {
            activity = srcActivity;
            DispatcherBll dispatcherBll = new DispatcherBll(srcActivity);
            String paramsJson = bundle.getString(ParamKey.EXTRAKEY_JSONPARAM);
            if (TextUtils.isEmpty(paramsJson)) {
                XESToastUtils.showToast(activity, "数据异常");
                return;
            }

            try {
                JSONObject jsonObject = new JSONObject(paramsJson);
                int status = jsonObject.optInt("status");
                String vStuCourseId = jsonObject.optString("stuCouId");
                String courseId = jsonObject.optString("courseId");
                String vChapterId = jsonObject.optString("planId");
                String chapterName = jsonObject.optString("planName");
                int type = jsonObject.optInt("type");
                if (type == TYPE_EXP_LIVE) {
                    VideoSectionEntity sectionEntity = new VideoSectionEntity();
                    sectionEntity.setvSectionName(chapterName);
                    sectionEntity.setvChapterName(chapterName);
                    dispatcherBll.deductStuGolds(sectionEntity, vChapterId, "");
                    return;
                } else if (type == TYPE_AUDIT_CLASS_ROOM) {
                    LiveVideoEnter.intentToAuditClassActivity(srcActivity, vStuCourseId, vChapterId);
                    return;
                }
                switch (status) {
                    case LiveNewStatus.LIVE_UNBEGIN://未开始
                        break;
                    case LiveNewStatus.LIVE_LIVING://进行中
                        startLivePlay(vStuCourseId, courseId, vChapterId);
                        break;
                    case LiveNewStatus.LIVE_WAIT_PLAYBACK://等待回放
                    case LiveNewStatus.LIVE_CAN_PLAYBACK: //未完成
                    case LiveNewStatus.LIVE_CAN_PLAYBACK_PLUS: { //已完成
                        VideoSectionEntity sectionEntity = new VideoSectionEntity();
                        sectionEntity.setvSectionName(chapterName);
                        sectionEntity.setvChapterName(chapterName);
                        sectionEntity.setvChapterID(vChapterId);
                        sectionEntity.setvSectionID(vChapterId);
                        sectionEntity.setVisitTimeKey(LocalCourseConfig.LIVETYPE_LIVE + "-" + sectionEntity
                                .getvSectionID());
                        sectionEntity.setvCoursseID(courseId);
                        sectionEntity.setvStuCourseID(vStuCourseId);
                        // 扣除金币
                        dispatcherBll.deductStuGold(sectionEntity, vStuCourseId);
                    }
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                XESToastUtils.showToast(activity, "数据异常");
            }
        } else {
            Intent intent = new Intent(srcActivity, LiveVideoLoadActivity.class);
            intent.putExtras(bundle);
            srcActivity.startActivityForResult(intent, requestCode);
        }
    }

    private void startLivePlay(final String vStuCourseID, final String courseId, final String sectionId) {
        if (!AppBll.getInstance(activity).canSeeVideo(new AppBll.OnSelectListener() {

            @Override
            public void onSelect(boolean goon) {
                if (goon) {
                    startLivePlayActivity(vStuCourseID, courseId, sectionId);
                }
            }
        })) {
            AppBll.getInstance(activity.getApplicationContext());
            return;
        }
        AppBll.getInstance(activity.getApplicationContext());
        startLivePlayActivity(vStuCourseID, courseId, sectionId);
    }

    private void startLivePlayActivity(String vStuCourseID, String courseId, String sectionId) {
        LiveVideoEnter.intentToLiveVideoActivity(activity, vStuCourseID, courseId, sectionId,
                LiveVideoBusinessConfig.ENTER_FROM_2);
    }

}
