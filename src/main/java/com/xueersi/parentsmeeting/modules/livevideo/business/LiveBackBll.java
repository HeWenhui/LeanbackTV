package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.xueersi.common.business.AppBll;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.entity.MyUserInfoEntity;
import com.xueersi.common.logerhelper.MobEnumUtil;
import com.xueersi.common.logerhelper.XesMobAgent;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.analytics.umsagent.UmsConstants;
import com.xueersi.lib.framework.utils.TimeUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.media.PlayerService;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LivePlayBackVideoActivity;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;

import java.util.List;
import java.util.Map;

/**
 * Created by lyqai on 2018/7/17.
 */
public class LiveBackBll implements LiveAndBackDebug {
    private String TAG = "LiveBackBll";
    Activity activity;
    /** 购课id */
    protected String stuCourId;
    /** 视频节对象 */
    VideoLivePlayBackEntity mVideoEntity;
    /** 从哪个页面跳转 */
    String where;
    int isArts;
    /** 区分文理appid */
    String appID = UmsConstants.LIVE_APP_ID_BACK;
    private LiveVideoSAConfig liveVideoSAConfig;
    boolean IS_SCIENCE = false;
    /** 播放器核心服务 */
    protected PlayerService vPlayer;
    /** 互动题 */
    private VideoQuestionEntity mQuestionEntity;
    /** 当前界面是否横屏 */
    protected boolean mIsLand = false;
    /** 显示互动题 */
    private static final int SHOW_QUESTION = 0;
    /** 没有互动题 */
    private static final int NO_QUESTION = 1;

    public LiveBackBll(Activity activity, VideoLivePlayBackEntity mVideoEntity) {
        this.activity = activity;
        this.mVideoEntity = mVideoEntity;
        isArts = activity.getIntent().getIntExtra("isArts", 0);
        if ("PublicLiveDetailActivity".equals(where)) {
            appID = UmsConstants.OPERAT_APP_ID;
        } else {
            if (isArts == 1) {
                appID = UmsConstants.ARTS_APP_ID_BACK;
                IS_SCIENCE = false;
                liveVideoSAConfig = new LiveVideoSAConfig(ShareBusinessConfig.LIVE_LIBARTS, false);
            } else {
                appID = UmsConstants.LIVE_APP_ID_BACK;
                IS_SCIENCE = true;
                liveVideoSAConfig = new LiveVideoSAConfig(ShareBusinessConfig.LIVE_SCIENCE, true);
            }
        }
    }

    public String getStuCourId() {
        return stuCourId;
    }

//    private Handler mPlayVideoControlHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case SHOW_QUESTION:
//                    if (resultFailed) {
//                        Loger.d(TAG, "handleMessage:SHOW_QUESTION.msg=" + msg.obj + ",resultFailed=" + resultFailed);
//                        return;
//                    }
//                    mIsShowQuestion = true;
//                    if (mMediaController != null) {
//                        mMediaController.setWindowLayoutType();
//                        mMediaController.release();
//                        Loger.d(TAG, "handleMessage:SHOW_QUESTION:msg=" + msg.obj);
//                    }
//                    break;
//                case NO_QUESTION:
//                    if (mVideoCourseQuestionPager != null) {
//                        mVideoCourseQuestionPager.hideInputMode();
//                    }
//                    Object obj = msg.obj;
//                    Loger.d(TAG, "handleMessage:NO_QUESTION=" + msg.arg1 + "," + (obj == mQuestionEntity));
//                    setQuestionEntity(null);
//                    questionViewGone("NO_QUESTION");
//                    if (mPopupWindow != null) {
//                        mPopupWindow.dismiss();
//                        mPopupWindow = null;
//                    }
//                    break;
//                default:
//                    break;
//            }
//        }
//    };

    /** 扫描是否有需要弹出的互动题 */
//    public void scanQuestion(long position) {
//
//        if (!mIsLand || vPlayer == null || !vPlayer.isPlaying()) {
//            // 如果不为横屏，没有正在播放，或正在显示互动题都退出扫描
//            return;
//        }
//        // 互动题结束，隐藏互动题
//        if (mQuestionEntity != null && mQuestionEntity.getvEndTime() != 0
//                && mQuestionEntity.getvEndTime() == TimeUtils.gennerSecond(position)) {
//            // 如果是互动题，则提示时间结束
//            if (LocalCourseConfig.CATEGORY_QUESTION == mQuestionEntity.getvCategory()
//                    && !mQuestionEntity.isAnswered()) {
//                XESToastUtils.showToast(activity, "答题时间结束...");
//                Message msg = mPlayVideoControlHandler.obtainMessage(NO_QUESTION, 1, 1, mQuestionEntity);
//                mPlayVideoControlHandler.sendMessage(msg);
//                if (voiceAnswerPager != null) {
//                    if (voiceAnswerPager.getBaseVideoQuestionEntity().getvQuestionID().equals(mQuestionEntity.getvQuestionID())) {
//                        Loger.d(TAG, "scanQuestion:stopVoiceAnswerPager1");
//                        voiceAnswerPager.setEnd();
//                        voiceAnswerPager.stopPlayer();
//                        voiceAnswerPager = null;
////                        voiceAnswerPager = null;
//                    }
//                }
//            }
//        }
//        VideoQuestionEntity oldQuestionEntity = mQuestionEntity;
//        getPlayQuetion(TimeUtils.gennerSecond(position));
//        if (mQuestionEntity != null && voiceAnswerPager != null) {
//            if (!voiceAnswerPager.getBaseVideoQuestionEntity().getvQuestionID().equals(mQuestionEntity.getvQuestionID())) {
//                Loger.d(TAG, "scanQuestion:stopVoiceAnswerPager2");
//                voiceAnswerPager.setEnd();
//                stopVoiceAnswerPager();
//            }
//        }
//        showQuestion(oldQuestionEntity);
//    }

    /**
     * 获取互动题
     *
     * @param playPosition
     */
//    private void getPlayQuetion(int playPosition) {
//        List<VideoQuestionEntity> lstVideoQuestion = mVideoEntity.getLstVideoQuestion();
//        if (lstVideoQuestion == null || lstVideoQuestion.size() == 0) {
//            return;
//        }
//        int startTime, endTime;
//
//        boolean hasQuestionShow = false;
//        for (int i = 0; i < lstVideoQuestion.size(); i++) {
//            VideoQuestionEntity videoQuestionEntity = lstVideoQuestion.get(i);
//            if (videoQuestionEntity.isAnswered()) {
//                continue;
//            }
//            startTime = videoQuestionEntity.getvQuestionInsretTime();
//            endTime = videoQuestionEntity.getvEndTime();
//            // 红包只有开始时间
//            if (LocalCourseConfig.CATEGORY_REDPACKET == videoQuestionEntity.getvCategory()) {
//                if (startTime == playPosition) {
//                    mQuestionEntity = videoQuestionEntity;
//                    hasQuestionShow = true;
//                    break;
//                }
//            } else if (LocalCourseConfig.CATEGORY_QUESTION == videoQuestionEntity.getvCategory()) {
//                if (LocalCourseConfig.QUESTION_TYPE_SPEECH.equals(videoQuestionEntity.getvQuestionType())) {//语音评测。在那个点弹出
//                    // 在开始时间和结束时间之间
//                    if (startTime <= playPosition && playPosition < endTime) {
////                    if (startTime == playPosition) {
//                        mQuestionEntity = videoQuestionEntity;
//                        hasQuestionShow = true;
//                        break;
//                    }
//                } else {
//                    // 互动题在开始时间和结束时间之间
//                    if (startTime <= playPosition && playPosition < endTime) {
//                        mQuestionEntity = videoQuestionEntity;
//                        hasQuestionShow = true;
//                        break;
//                    }
//                }
//            } else if (LocalCourseConfig.CATEGORY_EXAM == videoQuestionEntity.getvCategory()) {
//                // 在开始时间和结束时间之间
//                if (startTime <= playPosition && playPosition < endTime) {
////                if (startTime == playPosition) {
//                    mQuestionEntity = videoQuestionEntity;
//                    hasQuestionShow = true;
//                    break;
//                }
//            } else if (LocalCourseConfig.CATEGORY_H5COURSE_WARE == videoQuestionEntity.getvCategory()) {
//                // 在开始时间和结束时间之间
//                if (startTime <= playPosition && playPosition < endTime) {
////                if (startTime == playPosition) {
//                    mQuestionEntity = videoQuestionEntity;
//                    hasQuestionShow = true;
//                    break;
//                }
//            } else if (LocalCourseConfig.CATEGORY_ENGLISH_H5COURSE_WARE == videoQuestionEntity.getvCategory()) {
//                // 在开始时间和结束时间之间
//                if (startTime <= playPosition && playPosition < endTime) {
////                if (startTime == playPosition) {
//                    mQuestionEntity = videoQuestionEntity;
//                    hasQuestionShow = true;
//                    break;
//                }
//            } else if (LocalCourseConfig.CATEGORY_LEC_ADVERT == videoQuestionEntity.getvCategory()) {
//                // 在开始时间和结束时间之间
//                if (startTime == playPosition) {
////                if (startTime == playPosition) {
//                    mQuestionEntity = videoQuestionEntity;
//                    hasQuestionShow = true;
//                    break;
//                }
//            }
//        }
////        Loger.i(TAG, "getPlayQuetion:playPosition=" + playPosition + ",hasQuestionShow=" + hasQuestionShow + ",
//// mQuestionEntity=" + (mQuestionEntity != null));
//        if (mQuestionEntity != null) {
//            if (LocalCourseConfig.CATEGORY_EXAM == mQuestionEntity.getvCategory()) {
//                if (mQuestionEntity.getvEndTime() < playPosition) {
//                    if (examQuestionPlaybackPager != null) {
//                        examQuestionPlaybackPager.examSubmitAll();
//                        if (vPlayer != null) {
//                            vPlayer.pause();
//                        }
//                        Loger.i(TAG, "getPlayQuetion:examSubmitAll:playPosition=" + playPosition);
//                    }
//                }
//                return;
//            } else if (LocalCourseConfig.CATEGORY_QUESTION == mQuestionEntity.getvCategory()) {
//                if (LocalCourseConfig.QUESTION_TYPE_SPEECH.equals(mQuestionEntity.getvQuestionType())) {
//                    if (mQuestionEntity.getvEndTime() < playPosition) {
//                        if (speechQuestionPlaybackPager != null) {
//                            speechQuestionPlaybackPager.examSubmitAll();
//                            if (vPlayer != null) {
//                                vPlayer.pause();
//                            }
//                            Loger.i(TAG, "getPlayQuetion:examSubmitAll:playPosition=" + playPosition);
//                        }
//                    }
//                    return;
//                } else {
//                    if (mQuestionEntity.getvEndTime() < playPosition) {
//                        if (questionWebPager != null && mQuestionEntity.getvQuestionID().equals(questionWebPager
//                                .getTestId())) {
//                            questionWebPager.examSubmitAll();
//                            if (vPlayer != null) {
//                                vPlayer.pause();
//                            }
//                            Loger.i(TAG, "getPlayQuetion:examSubmitAll2:playPosition=" + playPosition);
//                            return;
//                        }
//                    }
//                }
//            } else if (LocalCourseConfig.CATEGORY_H5COURSE_WARE == mQuestionEntity.getvCategory()) {
//                if (mQuestionEntity.getvEndTime() < playPosition) {
//                    stopH5Exam();
//                }
//                return;
//            } else if (LocalCourseConfig.CATEGORY_ENGLISH_H5COURSE_WARE == mQuestionEntity.getvCategory()) {
//                if (mQuestionEntity.getvEndTime() < playPosition) {
//                    if (englishH5CoursewarePager != null) {
//                        englishH5CoursewarePager.submitData();
//                        if (vPlayer != null) {
//                            vPlayer.pause();
//                        }
//                        Loger.i(TAG, "getPlayQuetion:submitData:playPosition=" + playPosition);
//                    }
//                }
//                return;
//            }
//        }
//        // 如果没有互动题则移除
//        if (!hasQuestionShow && mQuestionEntity != null) {
//            startTime = mQuestionEntity.getvQuestionInsretTime();
//            //播放器seekto的误差
//            Loger.i(TAG, "getPlayQuetion:isClick=" + mQuestionEntity.isClick() + ",playPosition=" + playPosition + "," +
//                    "startTime=" + startTime);
//            if (mQuestionEntity.isClick()) {
//                if (startTime - playPosition >= 0 && startTime - playPosition < 5) {
//                    return;
//                }
//            }
//            Message msg = mPlayVideoControlHandler.obtainMessage(NO_QUESTION, 8, 8, mQuestionEntity);
//            mPlayVideoControlHandler.sendMessage(msg);
//        }
//    }
//
//    private void showQuestion(VideoQuestionEntity oldQuestionEntity) {
//        if (mQuestionEntity != oldQuestionEntity && !mQuestionEntity.isAnswered()) {
//            if (LocalCourseConfig.CATEGORY_EXAM == mQuestionEntity.getvCategory()) {
//                if (vPlayer != null) {
//                    vPlayer.pause();
//                }
//                mQuestionEntity.setAnswered(true);
//                LivePlayBackVideoActivity.LivePlayBackAlertDialog verifyCancelAlertDialog = new LivePlayBackVideoActivity.LivePlayBackAlertDialog();
//                verifyCancelAlertDialog.initInfo("测试提醒", "老师发布了一套测试题，是否现在开始答题？");
//                verifyCancelAlertDialog.setVerifyBtnListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
////                        if (vPlayer != null) {
////                            vPlayer.start();
////                        }
//                        showExam();
//                    }
//                });
//                verifyCancelAlertDialog.setCancelBtnListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
////                        mQuestionEntity.setAnswered(false);
//                        Message msg = mPlayVideoControlHandler.obtainMessage(NO_QUESTION, 2, 2, mQuestionEntity);
//                        mPlayVideoControlHandler.sendMessage(msg);
//                        seekTo(mQuestionEntity.getvEndTime() * 1000);
//                        start();
//                    }
//                });
//                verifyCancelAlertDialog.showDialog();
//                return;
//            } else {
//                if (LocalCourseConfig.CATEGORY_QUESTION == mQuestionEntity.getvCategory()
//                        && LocalCourseConfig.QUESTION_TYPE_SPEECH.equals(mQuestionEntity.getvQuestionType())) {
//                    if (vPlayer != null) {
//                        vPlayer.pause();
//                    }
//                    mQuestionEntity.setAnswered(true);
//                    LivePlayBackVideoActivity.LivePlayBackAlertDialog verifyCancelAlertDialog = new LivePlayBackVideoActivity.LivePlayBackAlertDialog();
//                    verifyCancelAlertDialog.initInfo("测试提醒", "老师发布了语音测试题，是否现在开始答题？");
//                    verifyCancelAlertDialog.setVerifyBtnListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
////                            if (vPlayer != null) {
////                                vPlayer.start();
////                            }
//                            showSpeech();
//                        }
//                    });
//                    verifyCancelAlertDialog.setCancelBtnListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
////                            mQuestionEntity.setAnswered(false);
//                            Message msg = mPlayVideoControlHandler.obtainMessage(NO_QUESTION, 3, 3, mQuestionEntity);
//                            mPlayVideoControlHandler.sendMessage(msg);
//                            // TODO mQuestionEntity==null
//                            if (mQuestionEntity != null) {
//                                seekTo(mQuestionEntity.getvEndTime() * 1000);
//                                start();
//                            } else {
//                                Loger.e(LivePlayBackVideoActivity.this, TAG, "seekTo", new Exception("seekTo", questionEntityNullEx), true);
//                            }
//                        }
//                    });
//                    verifyCancelAlertDialog.showDialog();
//                    return;
//                } else if (LocalCourseConfig.CATEGORY_H5COURSE_WARE == mQuestionEntity.getvCategory()) {
//                    if (vPlayer != null) {
//                        vPlayer.pause();
//                    }
//                    mQuestionEntity.setAnswered(true);
//                    LivePlayBackVideoActivity.LivePlayBackAlertDialog verifyCancelAlertDialog = new LivePlayBackVideoActivity.LivePlayBackAlertDialog();
//                    verifyCancelAlertDialog.initInfo("互动实验提醒", "老师发布了互动实验，是否参与互动？");
//                    verifyCancelAlertDialog.setVerifyBtnListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
////                            if (vPlayer != null) {
////                                vPlayer.start();
////                            }
//                            showH5CoursewarePager();
//                        }
//                    });
//                    verifyCancelAlertDialog.setCancelBtnListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
////                            mQuestionEntity.setAnswered(false);
//                            Message msg = mPlayVideoControlHandler.obtainMessage(NO_QUESTION, 4, 4, mQuestionEntity);
//                            mPlayVideoControlHandler.sendMessage(msg);
//                            seekTo(mQuestionEntity.getvEndTime() * 1000);
//                            start();
//                        }
//                    });
//                    verifyCancelAlertDialog.showDialog();
//                    return;
//                } else if (LocalCourseConfig.CATEGORY_ENGLISH_H5COURSE_WARE == mQuestionEntity.getvCategory()) {
//                    if (vPlayer != null) {
//                        vPlayer.pause();
//                    }
//                    mQuestionEntity.setAnswered(true);
//                    LivePlayBackVideoActivity.LivePlayBackAlertDialog verifyCancelAlertDialog = new LivePlayBackVideoActivity.LivePlayBackAlertDialog();
//                    verifyCancelAlertDialog.initInfo("课件提醒", "老师发布了课件，是否参与互动？");
//                    verifyCancelAlertDialog.setVerifyBtnListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            if (mQuestionEntity == null) {
//                                if (vPlayer != null) {
//                                    vPlayer.start();
//                                }
//                                return;
//                            }
//                            if ("1".equals(mQuestionEntity.getIsVoice())) {
//                                try {
//                                    showVoiceAnswer(mQuestionEntity);
//                                    if (vPlayer != null) {
//                                        vPlayer.start();
//                                    }
//                                } catch (Exception e) {
//                                    showEnglishH5CoursewarePager();
//                                }
//                            } else {
//                                showEnglishH5CoursewarePager();
//                            }
//                            Message msg = mPlayVideoControlHandler.obtainMessage(SHOW_QUESTION, "showEnglishH5VoiceAnswer");
//                            mPlayVideoControlHandler.sendMessage(msg);
////                            showEnglishH5CoursewarePager();
//                        }
//                    });
//                    verifyCancelAlertDialog.setCancelBtnListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
////                            mQuestionEntity.setAnswered(false);
//                            Message msg = mPlayVideoControlHandler.obtainMessage(NO_QUESTION, 5, 5, mQuestionEntity);
//                            mPlayVideoControlHandler.sendMessage(msg);
//                            if (mQuestionEntity != null) {
//                                seekTo(mQuestionEntity.getvEndTime() * 1000);
//                            }
//                            start();
//                        }
//                    });
//                    verifyCancelAlertDialog.showDialog();
//                    return;
//                }
//            }
//        }
//        // 有交互信息并且没有互动题
//        if (mQuestionEntity != null && !mQuestionEntity.isAnswered() && !mIsShowQuestion) {
//            // 互动题
//            if (LocalCourseConfig.CATEGORY_QUESTION == mQuestionEntity.getvCategory()) {
//                if (!(mMediaController != null && mMediaController.isShow())) {
//                    // 红包隐藏
//                    redPacketHide();
//                    showQestion();
//                    XesMobAgent.playVideoStatisticsMessage(MobEnumUtil.QUESTION_LIVEPLAYBACK, MobEnumUtil.QUESTION_SHOW,
//                            XesMobAgent.XES_VIDEO_INTERACTIVE);
//                }
//                // 红包
//            } else if (LocalCourseConfig.CATEGORY_REDPACKET == mQuestionEntity.getvCategory()) {
//                if (("" + mRedPacketId).equals(mQuestionEntity.getvQuestionID())) {
//                    return;
//                }
//                mRedPacketId = mQuestionEntity.getvQuestionID();
//                showRedPacket(mQuestionEntity);
//                XesMobAgent.playVideoStatisticsMessage(MobEnumUtil.REDPACKET_LIVEPLAYBACK, MobEnumUtil.REDPACKET_SHOW,
//                        XesMobAgent.XES_VIDEO_INTERACTIVE);
//            } else if (LocalCourseConfig.CATEGORY_EXAM == mQuestionEntity.getvCategory()) {
//                // 红包隐藏
//                redPacketHide();
//                showExam();
//            } else if (LocalCourseConfig.CATEGORY_LEC_ADVERT == mQuestionEntity.getvCategory()) {
//                mQuestionEntity.setAnswered(true);
//                showLecAdvertPager(mQuestionEntity);
//            }
//            // 互动题结束
//        }
//    }

    @Override
    public void umsAgentDebugSys(String eventId, Map<String, String> mData) {
        MyUserInfoEntity userInfoEntity = UserBll.getInstance().getMyUserInfoEntity();
        mData.put("uid", userInfoEntity.getStuId());
        mData.put("uname", AppBll.getInstance().getAppInfoEntity().getChildName());
        mData.put("courseid", mVideoEntity.getCourseId());
        mData.put("liveid", mVideoEntity.getLiveId());
        if ("PublicLiveDetailActivity".equals(where)) {
            mData.put("livetype", "" + 2);
        } else {
            mData.put("livetype", "" + 3);
        }
        mData.put("clits", "" + System.currentTimeMillis());
//        Loger.d(mContext, eventId, mData, true);
        UmsAgentManager.umsAgentDebug(activity, appID, eventId, mData);
    }

    @Override
    public void umsAgentDebugInter(String eventId, final Map<String, String> mData) {
        MyUserInfoEntity userInfoEntity = UserBll.getInstance().getMyUserInfoEntity();
        mData.put("uid", userInfoEntity.getStuId());
        mData.put("uname", AppBll.getInstance().getAppInfoEntity().getChildName());
        mData.put("courseid", mVideoEntity.getCourseId());
        mData.put("liveid", mVideoEntity.getLiveId());
        if ("PublicLiveDetailActivity".equals(where)) {
            mData.put("livetype", "" + 2);
        } else {
            mData.put("livetype", "" + 3);
        }
        mData.put("eventid", "" + eventId);
        mData.put("clits", "" + System.currentTimeMillis());
        UmsAgentManager.umsAgentOtherBusiness(activity, appID, UmsConstants.uploadBehavior, mData);
    }

    @Override
    public void umsAgentDebugPv(String eventId, final Map<String, String> mData) {
        MyUserInfoEntity userInfoEntity = UserBll.getInstance().getMyUserInfoEntity();
        mData.put("uid", userInfoEntity.getStuId());
        mData.put("uname", AppBll.getInstance().getAppInfoEntity().getChildName());
        mData.put("courseid", mVideoEntity.getCourseId());
        mData.put("liveid", mVideoEntity.getLiveId());
        if ("PublicLiveDetailActivity".equals(where)) {
            mData.put("livetype", "" + 2);
        } else {
            mData.put("livetype", "" + 3);
        }
        mData.put("eventid", "" + eventId);
        mData.put("clits", "" + System.currentTimeMillis());
        UmsAgentManager.umsAgentOtherBusiness(activity, appID, UmsConstants.uploadShow, mData);
    }

}
