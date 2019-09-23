package com.xueersi.parentsmeeting.modules.livevideo.nbh5courseware.business;

import android.app.Activity;
import android.view.View;

import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.media.BackMediaPlayerControl;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.NbCourseWareEntity;
import com.xueersi.parentsmeeting.modules.livevideo.event.NbCourseEvent;
import com.xueersi.ui.dialog.VerifyCancelAlertDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

/**
 * Created by linyuqiang on 2018/7/17.
 */

public class NBH5PlayBackBll extends LiveBackBaseBll {
    H5CoursewareBll h5CoursewareBll;
    VideoQuestionEntity currentQuestion;

    public NBH5PlayBackBll(Activity activity, LiveBackBll liveBackBll) {
        super(activity, liveBackBll);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onCreate(VideoLivePlayBackEntity mVideoEntity, LiveGetInfo liveGetInfo, HashMap<String, Object> businessShareParamMap) {

    }

    @Override
    public int[] getCategorys() {
        return new int[]{LocalCourseConfig.CATEGORY_H5COURSE_WARE, LocalCourseConfig.CATEGORY_NB_ADDEXPERIMENT};
    }

    @Override
    public void onQuestionEnd(VideoQuestionEntity questionEntity) {
        if (h5CoursewareBll != null) {
            if (isNbAddExperiment(questionEntity)) {
               /* NbCourseWareEntity entity = new NbCourseWareEntity(liveBackBll.getRommInitData().getId(),questionEntity.getH5Play_url(),false);
                entity.setExperimentId(questionEntity.getvQuestionID());
                entity.setPlayBack(true);
                entity.setNbAddExperiment(true);
                h5CoursewareBll.onH5Courseware(entity, "off");*/
            } else {
                NbCourseWareEntity entity = new NbCourseWareEntity(liveBackBll.getRommInitData().getId(), questionEntity.getH5Play_url(), NbCourseWareEntity.NB_FREE_EXPERIMENT);
                h5CoursewareBll.onH5Courseware(entity, "off");
            }
        }
    }

    /**
     * 是否是Nb 加试实验
     *
     * @param questionEntity
     * @return
     */
    private boolean isNbAddExperiment(VideoQuestionEntity questionEntity) {
        // TODO: 2019/5/5 对接common 中的LocalCourseConfig
        return questionEntity != null && questionEntity.getvCategory() == LocalCourseConfig.CATEGORY_NB_ADDEXPERIMENT;
    }


    @Override
    public void showQuestion(VideoQuestionEntity oldQuestionEntity, final VideoQuestionEntity questionEntity, LiveBackBll.ShowQuestion showQuestion) {
        mRootView.setVisibility(View.VISIBLE);
        int vCategory = questionEntity.getvCategory();
        switch (vCategory) {
            case LocalCourseConfig.CATEGORY_H5COURSE_WARE: {
                VerifyCancelAlertDialog verifyCancelAlertDialog = new VerifyCancelAlertDialog(activity, activity.getApplication(), false,
                        VerifyCancelAlertDialog.TITLE_MESSAGE_VERIRY_CANCEL_TYPE);
                verifyCancelAlertDialog.initInfo("测试提醒", "老师发布了一套测试题，是否现在开始答题？");
                verifyCancelAlertDialog.setVerifyBtnListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (h5CoursewareBll == null) {
                            h5CoursewareBll = new H5CoursewareBll(mContext, liveBackBll.getRommInitData());
                            h5CoursewareBll.setIsPlayback(true);
                            h5CoursewareBll.initView(mRootView);
                        }
                        NbCourseWareEntity entity = new NbCourseWareEntity(liveBackBll.getRommInitData().getId(), questionEntity.getH5Play_url(), NbCourseWareEntity.NB_FREE_EXPERIMENT);
                        h5CoursewareBll.onH5Courseware(entity, "on");
                    }
                });
                verifyCancelAlertDialog.setCancelBtnListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BackMediaPlayerControl mediaPlayerControl = getInstance(BackMediaPlayerControl.class);
                        mediaPlayerControl.seekTo(questionEntity.getvEndTime() * 1000);
                        mediaPlayerControl.start();
                    }
                });
                verifyCancelAlertDialog.showDialog();
            }
            break;
            case LocalCourseConfig.CATEGORY_NB_ADDEXPERIMENT:
                currentQuestion = questionEntity;
                // nb 加试  实验
                VerifyCancelAlertDialog verifyCancelAlertDialog = new VerifyCancelAlertDialog(activity, activity.getApplication(), false,
                        VerifyCancelAlertDialog.TITLE_MESSAGE_VERIRY_CANCEL_TYPE);
                verifyCancelAlertDialog.initInfo("测试提醒", "老师发布了一套测试题，是否现在开始答题？");
                verifyCancelAlertDialog.setVerifyBtnListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (h5CoursewareBll == null) {
                            h5CoursewareBll = new H5CoursewareBll(mContext, liveBackBll.getRommInitData());
                            h5CoursewareBll.setIsPlayback(true);
                            h5CoursewareBll.initView(mRootView);
                        }
                        NbCourseWareEntity entity = new NbCourseWareEntity(liveBackBll.getRommInitData().getId(), questionEntity.getH5Play_url(),
                                NbCourseWareEntity.NB_ADD_EXPERIMENT);
                        entity.setNbAddExperiment(NbCourseWareEntity.NB_ADD_EXPERIMENT);
                        entity.setPlayBack(true);
                        entity.setExperimentId(questionEntity.getvQuestionID());
                        h5CoursewareBll.onH5Courseware(entity, "on");
                        //nb 加试 实验 展示试题时 暂停视频播放
                        BackMediaPlayerControl mediaPlayerControl = getInstance(BackMediaPlayerControl.class);
                        if (mediaPlayerControl != null) {
                            mediaPlayerControl.pause();
                        }
                    }
                });

                verifyCancelAlertDialog.setCancelBtnListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BackMediaPlayerControl mediaPlayerControl = getInstance(BackMediaPlayerControl.class);
                        mediaPlayerControl.seekTo(questionEntity.getvEndTime() * 1000);
                        mediaPlayerControl.start();
                    }
                });
                verifyCancelAlertDialog.showDialog();
                break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNbH5PageClose(NbCourseEvent event) {
        if (event.getEventType() == NbCourseEvent.EVENT_TYPE_NBH5_CLOSE) {
            BackMediaPlayerControl mediaPlayerControl = getInstance(BackMediaPlayerControl.class);
            // Nb 加试实验 关闭页面 调转到试题结束 时间点
            if (mediaPlayerControl != null && !mediaPlayerControl.isPlaying()) {
                if (currentQuestion != null) {
                    mediaPlayerControl.seekTo(currentQuestion.getvEndTime() * 1000);
                    mediaPlayerControl.start();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (h5CoursewareBll != null) {
            h5CoursewareBll.onDestroy();
        }
        EventBus.getDefault().unregister(this);
    }
}
