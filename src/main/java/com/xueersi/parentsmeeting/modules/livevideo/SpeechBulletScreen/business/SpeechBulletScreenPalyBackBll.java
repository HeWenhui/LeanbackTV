package com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.business;

import android.app.Activity;
import android.util.Log;
import android.widget.RelativeLayout;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.framework.utils.TimeUtils;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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

//        String testJson = "[\n" +
//                "            {\n" +
//                "                \"voiceId\": \"2684_1533546461143\",\n" +
//                "                \"msgData\": [\n" +
//                "                    {\n" +
//                "                        \"stuId\": \"57312\",\n" +
//                "                        \"msg\": \"白日依山尽\",\n" +
//                "                        \"relativeTime\": 38,\n" +
//                "                        \"name\": \"小二\",\n" +
//                "                        \"headImgPath\": \"http://xesfile.xesimg.com/user/h/def10001.png\"\n" +
//                "                    },\n" +
//                "                    {\n" +
//                "                        \"stuId\": \"57313\",\n" +
//                "                        \"msg\": \"白日黄河入海流\",\n" +
//                "                        \"relativeTime\": 55,\n" +
//                "                        \"name\": \"三三第三个第三个\",\n" +
//                "                        \"headImgPath\": \"http://xesfile.xesimg.com/user/h/def10002.png\"\n" +
//                "                    },\n" +
//                "                    {\n" +
//                "                        \"stuId\": \"57315\",\n" +
//                "                        \"msg\": \"白日偶数答案是什么\",\n" +
//                "                        \"relativeTime\": 101,\n" +
//                "                        \"name\": \"小五第五个\",\n" +
//                "                        \"headImgPath\": \"http://xesfile.xesimg.com/user/h/def10002.png\"\n" +
//                "                    }\n" +
//                "                ]\n" +
//                "            },\n" +
//                "            {\n" +
//                "                \"voiceId\": \"2684_1533546601552\",\n" +
//                "                \"msgData\": [\n" +
//                "                    {\n" +
//                "                        \"stuId\": \"57313\",\n" +
//                "                        \"msg\": \"白日黄河入海流\",\n" +
//                "                        \"relativeTime\": 55,\n" +
//                "                        \"name\": \"三三第三个第三个\",\n" +
//                "                        \"headImgPath\": \"http://xesfile.xesimg.com/user/h/def10002.png\"\n" +
//                "                    },\n" +
//                "                    {\n" +
//                "                        \"stuId\": \"57315\",\n" +
//                "                        \"msg\": \"白日偶数答案是什么\",\n" +
//                "                        \"relativeTime\": 101,\n" +
//                "                        \"name\": \"小五第五个\",\n" +
//                "                        \"headImgPath\": \"http://xesfile.xesimg.com/user/h/def10002.png\"\n" +
//                "                    },\n" +
//                "                    {\n" +
//                "                        \"stuId\": \"57313\",\n" +
//                "                        \"msg\": \"黄河入海流\",\n" +
//                "                        \"relativeTime\": 46,\n" +
//                "                        \"name\": \"三三第三个第三个\",\n" +
//                "                        \"headImgPath\": \"http://xesfile.xesimg.com/user/h/def10002.png\"\n" +
//                "                    }\n" +
//                "                ]\n" +
//                "            }\n" +
//                "]";
//        ResponseEntity testResponce = new ResponseEntity();
//        testResponce.setJsonObject(testJson);
//        barrageList =  getCourseHttpResponseParser().parseVoiceBarrageMsg(testResponce);
//        calculateBarrageTime();
    }

    public void getVoiceBarrageMsg(String liveId, String stuCouId, final AbstractBusinessDataCallBack callBack){
        Log.i(TAG,"getVoiceBarrageMsg: liveId ="+liveId+"   stuCouId="+stuCouId);
        getCourseHttpManager().getVoiceBarrageMsg(liveId, stuCouId, new HttpCallBack() {
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

    @Override
    public void onPositionChanged(long position) {
        Log.i(TAG, "onPositionChanged: position="+position);
        for (int i=0; i<allBarrages.size(); i++) {
            VoiceBarrageMsgEntity.VoiceBarrageItemEntity voiceBarrageItemEntity = allBarrages.get(i);
            if (voiceBarrageItemEntity.getRelativeTime()== position) {
                mSpeechBulletScreenAction.addPlayBackDanmaku(voiceBarrageItemEntity.getName(), voiceBarrageItemEntity.getMsg(), voiceBarrageItemEntity.getHeadImgPath(), true);
            }
        }
    }

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
     //       Log.i(TAG,"videoQuestionEntity.getvQuestionID():"+i+"    "+videoQuestionEntity.getvQuestionID());
            if (LocalCourseConfig.CATEGORY_BULLETSCREEN == videoQuestionEntity.getvCategory()) {
                eventId = videoQuestionEntity.getvQuestionID();
                startTime = TimeUtils.gennerSecond(videoQuestionEntity.getvQuestionInsretTime());
                for (int j=0; j<barrageList.size(); j++ ){
                    VoiceBarrageMsgEntity voiceBarrageMsgEntity = barrageList.get(j);
                    if (eventId.equals(voiceBarrageMsgEntity.getVoiceId())) {
                        ArrayList<VoiceBarrageMsgEntity.VoiceBarrageItemEntity> voiceBarrageItemEntities = voiceBarrageMsgEntity.getVoiceBarrageItemEntities();
                        for (int k=0; k<voiceBarrageItemEntities.size(); k++ ) {
                            VoiceBarrageMsgEntity.VoiceBarrageItemEntity voiceBarrageItemEntity = voiceBarrageItemEntities.get(k);
                            voiceBarrageItemEntity.setRelativeTime(voiceBarrageItemEntity.getRelativeTime()+startTime);
                            allBarrages.add(voiceBarrageItemEntity);
                            Log.i(TAG,"add barrage: time="+(voiceBarrageItemEntity.getRelativeTime()+startTime)+" msg="+voiceBarrageItemEntity.getMsg());
                        }
                    }
                }
            }
        }
//        for (int j=0; j<barrageList.size(); j++ ){
//            VoiceBarrageMsgEntity voiceBarrageMsgEntity = barrageList.get(j);
//            ArrayList<VoiceBarrageMsgEntity.VoiceBarrageItemEntity> voiceBarrageItemEntities = voiceBarrageMsgEntity.getVoiceBarrageItemEntities();
//            for (int k=0; k<voiceBarrageItemEntities.size(); k++ ) {
//                VoiceBarrageMsgEntity.VoiceBarrageItemEntity voiceBarrageItemEntity = voiceBarrageItemEntities.get(k);
//                voiceBarrageItemEntity.setRelativeTime(voiceBarrageItemEntity.getRelativeTime()+startTime);
//                allBarrages.add(voiceBarrageItemEntity);
//                Log.i(TAG,"add barrage: time="+voiceBarrageItemEntity.getRelativeTime()+" msg="+voiceBarrageItemEntity.getMsg());
//            }
//        }
    }
}
