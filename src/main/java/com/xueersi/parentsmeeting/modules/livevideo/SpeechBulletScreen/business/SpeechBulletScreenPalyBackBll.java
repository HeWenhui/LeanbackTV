package com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.business;

import android.app.Activity;
import android.util.Log;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Zhang Yuansun on 2018/8/7.
 */

public class SpeechBulletScreenPalyBackBll extends LiveBackBaseBll{
    SpeechBulletScreenBll mSpeechBulletScreenAction;
    ArrayList<VoiceBarrageMsgEntity> barrageList;
    ArrayList<VoiceBarrageMsgEntity.VoiceBarrageItemEntity> allBarrages = new ArrayList<>();
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
        if (mSpeechBulletScreenAction == null) {
            SpeechBulletScreenBll speechBulletScreenBll = new SpeechBulletScreenBll(activity);
            speechBulletScreenBll.initView(mRootView);
            mSpeechBulletScreenAction = speechBulletScreenBll;
        }
        mSpeechBulletScreenAction.onStartSpeechBulletScreenPlayBack();
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
    public void getVoiceBarrageMsg(String liveId, String stuCouId, final AbstractBusinessDataCallBack callBack){
        Log.i(TAG,"getVoiceBarrageMsg: liveId ="+liveId+"   stuCouId="+stuCouId);
        //不弹出接口请求错误提示
        getCourseHttpManager().getVoiceBarrageMsg(liveId, stuCouId, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                Log.i(TAG, "onDataSucess: json="+responseEntity.getJsonObject());
                callBack.onDataSucess(responseEntity);
            }
            @Override
            public void onPmFailure(Throwable error, String msg) {
                Log.i(TAG, "onPmFailure: msg="+msg);
                callBack.onDataFail(0, msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                Log.i(TAG, "onPmError: json="+responseEntity.getJsonObject());
                callBack.onDataFail(1, responseEntity.getErrorMsg());
            }
        });
    }

    private long currentPositon = -1;

    /**
     * 视频进度条变化时，判断是否要显示弹幕
     */
    @Override
    public void onPositionChanged(long position) {
        Log.i(TAG, "onPositionChanged: position="+position);

        if (currentPositon == position) {
            //过滤1秒传来两个回调的情况
            return;
        }
        currentPositon =position;
        for (int i=0; i<allBarrages.size(); i++) {
            VoiceBarrageMsgEntity.VoiceBarrageItemEntity voiceBarrageItemEntity = allBarrages.get(i);
            if (voiceBarrageItemEntity.getRelativeTime()== position) {
                if (voiceBarrageItemEntity.getStuId()!= null && voiceBarrageItemEntity.getStuId().equals(liveGetInfo.getStuId())) {
                    mSpeechBulletScreenAction.addPlayBackDanmaku("我", voiceBarrageItemEntity.getMsg(), voiceBarrageItemEntity.getHeadImgPath(), false);
                }
                else {
                    mSpeechBulletScreenAction.addPlayBackDanmaku(voiceBarrageItemEntity.getName(), voiceBarrageItemEntity.getMsg(), voiceBarrageItemEntity.getHeadImgPath(), true);
                }
            }
        }
    }

    /**
     * 计算每条弹幕的刷新时间
     */
    public void calculateBarrageTime(){
        Log.i(TAG,"calculateBarrageTime()");
        List<VideoQuestionEntity> lstVideoQuestion = mVideoEntity.getLstVideoQuestion();
        if (lstVideoQuestion == null || lstVideoQuestion.size() == 0) {
            return;
        }
        if (barrageList == null || barrageList.size() == 0) {
            return;
        }
        String eventId;
        int startTime = 0;
        for (int i=0; i<lstVideoQuestion.size(); i++) {
            VideoQuestionEntity videoQuestionEntity = lstVideoQuestion.get(i);
            if (LocalCourseConfig.CATEGORY_BULLETSCREEN == videoQuestionEntity.getvCategory()) {
                eventId = videoQuestionEntity.getvQuestionID();
                startTime = videoQuestionEntity.getvQuestionInsretTime();
                for (int j=0; j<barrageList.size(); j++ ){
                    VoiceBarrageMsgEntity voiceBarrageMsgEntity = barrageList.get(j);
                    if (eventId.equals(voiceBarrageMsgEntity.getVoiceId())) {
                        ArrayList<VoiceBarrageMsgEntity.VoiceBarrageItemEntity> voiceBarrageItemEntities = voiceBarrageMsgEntity.getVoiceBarrageItemEntities();
                        for (int k=0; k<voiceBarrageItemEntities.size(); k++ ) {
                            VoiceBarrageMsgEntity.VoiceBarrageItemEntity voiceBarrageItemEntity = voiceBarrageItemEntities.get(k);
                            voiceBarrageItemEntity.setRelativeTime(voiceBarrageItemEntity.getRelativeTime()+startTime);
                            allBarrages.add(voiceBarrageItemEntity);
                            Log.i(TAG,"add barrage: time="+(voiceBarrageItemEntity.getRelativeTime())+" msg="+voiceBarrageItemEntity.getMsg());
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onPausePlayer() {
        super.onPausePlayer();
        if (mSpeechBulletScreenAction!=null) {
            mSpeechBulletScreenAction.pauseDanmaku();
        }
    }

    @Override
    public void onStartPlayer() {
        super.onStartPlayer();
        if (mSpeechBulletScreenAction!=null) {
            mSpeechBulletScreenAction.resumeDanmaku();
        }
    }

    @Override
    public void setSpeed(float speed) {
        super.setSpeed(speed);
        if (mSpeechBulletScreenAction!=null) {
            mSpeechBulletScreenAction.setDanmakuSpeed(speed);
        }
    }
}
