package com.xueersi.parentsmeeting.modules.livevideo.business.foruminteraction;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.HttpRequestParams;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveEventBus;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.TopicAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveAppUserInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.event.TeacherPraiseEvent;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveScienceHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.message.business.SendMessageReg;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.teampk.business.TeamPkPraiseBll;

import org.json.JSONObject;

/**
 * @ProjectName: xueersiwangxiao
 * @Package: com.xueersi.parentsmeeting.modules.livevideo.business.foruminteraction
 * @ClassName: ForumInteractionIRCBll
 * @Description: 讨论区互动
 * @Author: WangDe
 * @CreateDate: 2019/10/29 11:39
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/10/29 11:39
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class ForumInteractionIRCBll extends LiveBaseBll implements NoticeAction , TopicAction {


    private LiveAppUserInfo userInfo;
    private LiveScienceHttpManager httpManager;
    private HttpRequestParams params;
    /** 互动id*/
    private String interactionId = "";
    private boolean isOpenInteraction;
    private boolean onShow;
    private  String eventid = LiveVideoConfig.LIVE_FORUM_INTERACTION;
    private boolean isFirstTopic = true;


    public ForumInteractionIRCBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
        userInfo = LiveAppUserInfo.getInstance();
        httpManager = new LiveScienceHttpManager(getHttpManager());
        SendMessageReg sendMessageReg = getInstance(SendMessageReg.class);
        if (sendMessageReg != null) {
            sendMessageReg.addOnSendMsg(new SendMessageReg.OnSendMsg() {
                @Override
                public void onSendMsg(String msg) {
                    ForumInteractionIRCBll.this.onSendMsg(msg);
                }
            });
        }
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
    }

    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {
        switch (type) {
            case XESCODE.FORUM_INTERACTION:{
                String open =  data.optString("open","close");
                interactionId = data.optString("interactionId");
                if ("open".equals(open)){
                    isOpenInteraction = true;
                    setDefaultParams();
                    StableLogHashMap logHashMap = defaultLogMap("discussOn");
                    logHashMap.addSno("1.11");
                    logHashMap.addExY().addNonce(interactionId);
                    umsAgentDebugInter(eventid,logHashMap.getData());
                }else {
                    isOpenInteraction = false;
                    StableLogHashMap logHashMap = defaultLogMap("discussOff");
                    logHashMap.addSno("1.31");
                    logHashMap.addExY().addNonce(interactionId);
                    umsAgentDebugInter(eventid,logHashMap.getData());
                }
                break;
            }
            case XESCODE.FORUM_INTERACTION_PRAISE:{
                StableLogHashMap logHashMap = defaultLogMap("discussPraise");
                logHashMap.addSno("2.11");
                logHashMap.addExY();
                umsAgentDebugInter(eventid,logHashMap.getData());
                post(new Runnable() {
                    @Override
                    public void run() {
                        if (onShow){
                            return;
                        }
                        onShow = true;
                        ForumPraisePager praisePager = new ForumPraisePager(activity);
                        addView(praisePager.getRootView());
                        praisePager.setOnPagerClose(new LiveBasePager.OnPagerClose() {
                            @Override
                            public void onClose(LiveBasePager basePager) {
                                onShow =false;
                                removeView(basePager.getRootView());
                            }
                        });
                    }
                });
                break;
            }
        }
    }

    private void onSendMsg(String msg){
//        getHttpManager().sendPost();
        if (LiveTopic.MODE_TRANING.equals(mLiveBll.getMode())){
            isOpenInteraction = false;
            return;
        }
        if (isOpenInteraction){
            params.addBodyParam("interactionId",interactionId);
            params.addBodyParam("message",msg);
            logger.d(params.getBodyParams().toString());
            httpManager.sendMessageToTeacher(params, new HttpCallBack(false) {
                @Override
                public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                    StableLogHashMap logHashMap = defaultLogMap("discussUpload");
                    logHashMap.addSno("1.12");
                    logHashMap.addExY();
                    umsAgentDebugInter(eventid,logHashMap.getData());
                }

                @Override
                public void onPmFailure(Throwable error, String msg) {
                    super.onPmFailure(error, msg);
                    StableLogHashMap logHashMap = defaultLogMap("discussUpload");
                    logHashMap.addSno("1.12");
                    logHashMap.addExN();
                    umsAgentDebugInter(eventid,logHashMap.getData());
                }

                @Override
                public void onPmError(ResponseEntity responseEntity) {
                    super.onPmError(responseEntity);
                    StableLogHashMap logHashMap = defaultLogMap("discussUpload");
                    logHashMap.addSno("1.12");
                    logHashMap.addExN();
                    umsAgentDebugInter(eventid,logHashMap.getData());
                }
            });

        }
    }

    private void setDefaultParams(){
        params = new HttpRequestParams();
        params.addBodyParam("bizId",""+mLiveBll.getLiveType());
        params.addBodyParam("planId",mLiveBll.getLiveId());
        params.addBodyParam("stuIrcId",mLiveBll.getNickname());
        params.addBodyParam("psId",""+ userInfo.getPsimId());
        params.addBodyParam("imgPath",""+ userInfo.getHeadImg());
        params.addBodyParam("name", userInfo.getShowName());
        params.addBodyParam("stuId", userInfo.getStuId());
        params.addBodyParam("classId",mGetInfo.getStudentLiveInfo().getClassId());
        params.addBodyParam("teamId",mGetInfo.getStudentLiveInfo().getTeamId());

    }
    @Override
    public int[] getNoticeFilter() {
        return new int[]{XESCODE.FORUM_INTERACTION,XESCODE.FORUM_INTERACTION_PRAISE};
    }

    @Override
    public void onTopic(LiveTopic liveTopic, JSONObject jsonObject, boolean modeChange) {
        if (liveTopic != null){
            String open =  liveTopic.getMainRoomstatus().getOnChatInteract();
            interactionId = liveTopic.getMainRoomstatus().getChatInteractionId();
            if ("open".equals(open)){
                isOpenInteraction = true;
                setDefaultParams();
                if (isFirstTopic){
                    StableLogHashMap logHashMap = defaultLogMap("discussOn");
                    logHashMap.addSno("1.11");
                    logHashMap.addExY().addNonce("");
                    umsAgentDebugInter(eventid,logHashMap.getData());
                }
            }else {
                isOpenInteraction = false;
            }
        }
        isFirstTopic = false;
    }

    private StableLogHashMap defaultLogMap(String type){
        StableLogHashMap logHashMap = new StableLogHashMap(type);
        logHashMap.addStable("2");
        logHashMap.put("liveid",mLiveId);
        logHashMap.put("interactionid",interactionId);
        logHashMap.put("gradeid",""+mGetInfo.getGrade());
        logHashMap.put("courseid",mLiveBll.getCourseId());
        logHashMap.put("userid",userInfo.getStuId());
        logHashMap.put("subjectid",mGetInfo.getSubject_digits());
        return logHashMap;
    }

}
