package com.xueersi.parentsmeeting.modules.livevideo.business.foruminteraction;

import android.app.Activity;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.HttpRequestParams;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.TopicAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveAppUserInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveScienceHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.message.business.SendMessageReg;

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
    private String interactionId;

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
        setDefaultParams();
    }

    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {
        switch (type) {
            case XESCODE.FORUM_INTERACTION:{
                break;
            }
            case XESCODE.FORUM_INTERACTION_THUMBS_UP:{
                break;
            }
        }
    }

    private void onSendMsg(String msg){
//        getHttpManager().sendPost();
        params.addBodyParam("interactionId",interactionId);
        params.addBodyParam("message",msg);
        httpManager.sendMessageToTeacher(params, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {

            }
        });
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
        return new int[]{XESCODE.FORUM_INTERACTION,XESCODE.FORUM_INTERACTION_THUMBS_UP};
    }

    @Override
    public void onTopic(LiveTopic liveTopic, JSONObject jsonObject, boolean modeChange) {

    }
}
