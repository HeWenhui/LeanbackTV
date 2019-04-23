package com.xueersi.parentsmeeting.modules.livevideoOldIJK.SpeechBulletScreen.business;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.ViewGroup;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.business.VoiceBarrageMsgEntity;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.SpeechBulletScreen.page.SpeechBulletScreenPlayBackPager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.WeakHandler;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Zhang Yuansun on 2018/8/7.
 */

public class SpeechBulletScreenPalyBackBll extends LiveBackBaseBll {
    private ArrayList<VoiceBarrageMsgEntity> barrageList;
    private ArrayList<VoiceBarrageMsgEntity.VoiceBarrageItemEntity> allBarrages = new ArrayList<>();
    /**
     * 当前视频播放进度
     */
    private long currentPositon = -1;
    /**
     * 回放弹幕的界面
     */
    private SpeechBulletScreenPlayBackPager mSpeechBulPlaybackPager;
    private WeakHandler mWeakHandler = new WeakHandler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            return false;
        }
    });

    public SpeechBulletScreenPalyBackBll(Activity activity, LiveBackBll liveBackBll) {
        super(activity, liveBackBll);
    }

    @Override
    public void onCreate(VideoLivePlayBackEntity mVideoEntity, LiveGetInfo liveGetInfo, HashMap<String, Object> businessShareParamMap) {
        super.onCreate(mVideoEntity, liveGetInfo, businessShareParamMap);
    }

    @Override
    public void initView() {
        super.initView();
        mWeakHandler.post(new Runnable() {
            @Override
            public void run() {
                mSpeechBulPlaybackPager = new SpeechBulletScreenPlayBackPager(activity);
                mRootView.addView(mSpeechBulPlaybackPager.getRootView(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//                RelativeLayout.LayoutParams rp = (RelativeLayout.LayoutParams) mSpeechBulPlaybackPager.getRootView().getLayoutParams();
//                rp.setMargins(0, SizeUtils.Dp2Px(mContext, 17), 0, 0);
//                mSpeechBulPlaybackPager.getRootView().setLayoutParams(rp);
            }
        });
        getVoiceBarrageMsg(liveGetInfo.getId(), liveGetInfo.getStuCouId(), new AbstractBusinessDataCallBack() {
            @Override
            public void onDataSucess(Object... objData) {
                barrageList = getCourseHttpResponseParser().parseVoiceBarrageMsg((ResponseEntity) objData[0]);
                calculateBarrageTime();
            }
        });
    }

    /**
     * 弹幕数据接口请求
     */
    public void getVoiceBarrageMsg(String liveId, String stuCouId, final AbstractBusinessDataCallBack callBack) {
        logger.i("getVoiceBarrageMsg: liveId =" + liveId + "   stuCouId=" + stuCouId);
        //不弹出接口请求错误提示
        getCourseHttpManager().getVoiceBarrageMsg(liveId, stuCouId, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                callBack.onDataSucess(responseEntity);
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                callBack.onDataFail(0, msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                callBack.onDataFail(1, responseEntity.getErrorMsg());
            }
        });
    }

    /**
     * 视频进度条变化时，判断是否要显示弹幕
     */
    @Override
    public void onPositionChanged(long position) {
        logger.i("onPositionChanged: position=" + position);
        if (currentPositon == position) {
            //过滤1秒传来两个回调的情况
            return;
        }
        currentPositon = position;
        for (int i = 0; i < allBarrages.size(); i++) {
            VoiceBarrageMsgEntity.VoiceBarrageItemEntity voiceBarrageItemEntity = allBarrages.get(i);
            if (voiceBarrageItemEntity.getRelativeTime() == position) {
                if (voiceBarrageItemEntity.getStuId() != null && voiceBarrageItemEntity.getStuId().equals(liveGetInfo.getStuId())) {
                    mSpeechBulPlaybackPager.addDanmaKuFlowers("我", voiceBarrageItemEntity.getMsg(), voiceBarrageItemEntity.getHeadImgPath(), false);
                } else {
                    mSpeechBulPlaybackPager.addDanmaKuFlowers(voiceBarrageItemEntity.getName(), voiceBarrageItemEntity.getMsg(), voiceBarrageItemEntity.getHeadImgPath(), true);
                }
            }
        }
    }

    /**
     * 计算每条弹幕的刷新时间
     */
    public void calculateBarrageTime() {
        logger.i("calculateBarrageTime()");
        List<VideoQuestionEntity> lstVideoQuestion = mVideoEntity.getLstVideoQuestion();
        if (lstVideoQuestion == null || lstVideoQuestion.size() == 0) {
            return;
        }
        if (barrageList == null || barrageList.size() == 0) {
            return;
        }
        String eventId;
        int startTime = 0;
        for (int i = 0; i < lstVideoQuestion.size(); i++) {
            VideoQuestionEntity videoQuestionEntity = lstVideoQuestion.get(i);
            if (LocalCourseConfig.CATEGORY_BULLETSCREEN == videoQuestionEntity.getvCategory()) {
                eventId = videoQuestionEntity.getvQuestionID();
                startTime = videoQuestionEntity.getvQuestionInsretTime();
                for (int j = 0; j < barrageList.size(); j++) {
                    VoiceBarrageMsgEntity voiceBarrageMsgEntity = barrageList.get(j);
                    if (eventId.equals(voiceBarrageMsgEntity.getVoiceId())) {
                        ArrayList<VoiceBarrageMsgEntity.VoiceBarrageItemEntity> voiceBarrageItemEntities = voiceBarrageMsgEntity.getVoiceBarrageItemEntities();
                        for (int k = 0; k < voiceBarrageItemEntities.size(); k++) {
                            VoiceBarrageMsgEntity.VoiceBarrageItemEntity voiceBarrageItemEntity = voiceBarrageItemEntities.get(k);
                            voiceBarrageItemEntity.setRelativeTime(voiceBarrageItemEntity.getRelativeTime() + startTime);
                            allBarrages.add(voiceBarrageItemEntity);
                            logger.i("add barrage: time=" + (voiceBarrageItemEntity.getRelativeTime()) + " msg=" + voiceBarrageItemEntity.getMsg());
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onPausePlayer() {
        super.onPausePlayer();
        if (mSpeechBulPlaybackPager != null) {
            mSpeechBulPlaybackPager.pauseDanmaku();
        }
    }

    @Override
    public void onStartPlayer() {
        super.onStartPlayer();
        if (mSpeechBulPlaybackPager != null) {
            mSpeechBulPlaybackPager.resumeDanmaku();
        }
    }

    @Override
    public void setSpeed(float speed) {
        super.setSpeed(speed);
        if (mSpeechBulPlaybackPager != null) {
            mSpeechBulPlaybackPager.setDanmakuSpeed(speed);
        }
    }
}
