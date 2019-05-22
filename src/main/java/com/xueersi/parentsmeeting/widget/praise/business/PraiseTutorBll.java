package com.xueersi.parentsmeeting.widget.praise.business;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;

import com.xueersi.common.business.UserBll;
import com.xueersi.common.config.AppConfig;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.analytics.umsagent.UmsAgentTrayPreference;
import com.xueersi.lib.log.Loger;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.ShareDataConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity.ExcellentListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity.LikeListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity.LikeProbabilityEntity;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity.MinimarketListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity.PraiseListDanmakuEntity;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.TopicAction;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.praiselist.contract.PraiseListPresenter;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.praiselist.contract.PraiseListView;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.praiselist.page.PraiseListPager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.praiselist.view.PraiseListBll;
import com.xueersi.parentsmeeting.widget.praise.PraisePager;
import com.xueersi.parentsmeeting.widget.praise.config.PraiseConfig;
import com.xueersi.parentsmeeting.widget.praise.entity.PraiseEntity;
import com.xueersi.ui.dialog.VerifyCancelAlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Zhang Yuansun on 2018/7/27.
 */

public class PraiseTutorBll extends LiveBaseBll implements NoticeAction, TopicAction {

    RelativeLayout bottomContent;
    PraisePager praisePager;
    boolean isTopic = false;
    String likeId = "";
    boolean isCloase = true;
    public PraiseTutorBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
    }

    @Override
    public void onModeChange(String oldMode, String mode, boolean isPresent) {
        // 模式切换为主讲，关闭表扬榜
        if (praisePager != null && LiveTopic.MODE_CLASS.equals(mode)) {
            praisePager.closePraisePager();
        } else if (LiveTopic.MODE_CLASS.equals(oldMode) && LiveTopic.MODE_TRANING.equals(mode)){
            if (!TextUtils.isEmpty(getLikeId()) && !isCloase){
                getPraiseTutorData(getLikeId());
            }
        }
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
    }

    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {
        UmsAgentManager.umsAgentDebug(mContext,"tutor_practice_notice","type:" + type  + "sourceNic:"+sourceNick + "target:" + target + "data:" + data.toString());
        switch (type) {
            // 开启和发布榜单
            case XESCODE.TUTOR_ROOM_PRAISE_OPEN:
                praiseShowOrHide(data);
                break;
            // 点赞
            case XESCODE.TUTOR_ROOM_PRAISE_LIKE:
                showEncouraging();
                break;
            // 获取点赞总数
            case XESCODE.TUTOR_ROOM_PRAISE_LIKE_TOTAL:
                setPraiseTotal(data);
                break;
            default:
                break;
        }
    }

    private void setPraiseTotal(JSONObject data) {
        if (praisePager != null && data != null) {
            praisePager.setPraiseTotal(data.optInt("likes"));
        }
    }

    private void praiseShowOrHide(JSONObject data) {
        if (data == null) {
            return;
        }
        String open = data.optString("open");
        String listId = data.optString("listId");
        if (XESCODE.ON.equals(open)) {
            getPraiseTutorData(listId);
            setCloase(false);
            setLikeId(listId);
        } else if (XESCODE.OFF.equals(open)) {
            setCloase(true);
            setLikeId("");
            if (praisePager != null) {
                praisePager.closePraisePager();
            }
        }
    }

    @Override
    public int[] getNoticeFilter() {
        return new int[]{
                XESCODE.TUTOR_ROOM_PRAISE_OPEN,
                XESCODE.TUTOR_ROOM_PRAISE_LIKE,
                XESCODE.TUTOR_ROOM_PRAISE_LIKE_TOTAL
        };
    }

    public boolean isTopic() {
        return isTopic;
    }

    public void setTopic(boolean topic) {
        isTopic = topic;
    }

    private void showEncouraging() {
        if (praisePager != null) {
            praisePager.showEncouraging();
        }
    }

    @Override
    public void initView(RelativeLayout bottomContent, AtomicBoolean mIsLand) {
        super.initView(bottomContent, mIsLand);
        this.bottomContent = bottomContent;
    }

    @Override
    public void onTopic(LiveTopic liveTopic, JSONObject jsonObject, boolean modeChange) {
        Loger.d( "tutor_practice_onTopic", "liveTopic" + liveTopic + "/jsonObject"
                + "modeChange" + modeChange + "jsonObject:" + jsonObject.toString());
//        if(LiveTopic.MODE_TRANING.equals(mLiveBll.getMode())) {
//
//        }

        if (jsonObject != null && LiveTopic.MODE_TRANING.equals(mLiveBll.getMode())) {
            JSONObject room2Json = jsonObject.optJSONObject("room_2");
            if (room2Json != null) {
                JSONObject praiseListJson = room2Json.optJSONObject("praiseList");
                String id = praiseListJson.optString("id");
                if(isTopic()){
                    return;
                }
                setTopic(true);
                if (XESCODE.ON.equals(praiseListJson.optString("status"))) {
                    getPraiseTutorData(praiseListJson.optString("id"));
                    setLikeId(praiseListJson.optString("id"));
                    setCloase(false);
                }
            }
        }
    }


    private synchronized void getPraiseTutorData(final String rankId) {
        String classId = "";
        String courseId = "";
        String tutorId = "";
        if (mGetInfo.getStudentLiveInfo() != null) {
            classId = mGetInfo.getStudentLiveInfo().getClassId();
            courseId = mGetInfo.getStudentLiveInfo().getCourseId();
            tutorId = mGetInfo.getTeacherId();
        }
        getHttpManager().getPraoseTutorList(rankId, mLiveId, courseId, tutorId, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                PraiseEntity entity = getHttpResponseParser().parseTutorPraiseEntity(responseEntity);
                praisePager = new PraisePager(mContext, entity, listener,bottomContent);
                praisePager.showPraisePager(bottomContent);
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                mLogtf.d("getLikeList => onPmFailure: error = " + error + ", msg=" + msg);
                VerifyCancelAlertDialog vcDialog = new VerifyCancelAlertDialog(mContext, mBaseApplication, false,
                        VerifyCancelAlertDialog.MESSAGE_VERIFY_CANCEL_TYPE);
                vcDialog.initInfo("当前网络不佳，请刷新获取榜单！").showDialog();
                vcDialog.setVerifyBtnListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getPraiseTutorData(rankId);
                    }
                });
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                mLogtf.d("getLikeList => onPmError: errorMsg = " + responseEntity.getErrorMsg());
              //  showToast("" + responseEntity.getErrorMsg());
                VerifyCancelAlertDialog vcDialog = new VerifyCancelAlertDialog(mContext, mBaseApplication, false,
                        VerifyCancelAlertDialog.MESSAGE_VERIFY_CANCEL_TYPE);
                vcDialog.initInfo("当前网络不佳，请刷新获取榜单！").showDialog();
                vcDialog.setVerifyBtnListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getPraiseTutorData(rankId);
                    }
                });
            }
        });
    }

    public void sendLikeNum(int likes) {
        try {
            mLogtf.d("sendLikeNum: likes = " + likes + ", mCounTeacherStr = " + mLiveBll.getCounTeacherStr());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "" + XESCODE.TUTOR_ROOM_PRAISE_SENT_LIKE);
            jsonObject.put("likes", likes + "");
            jsonObject.put("stuId", mGetInfo.getStuId());
            jsonObject.put("stuName", mGetInfo.getStuName());
            sendNotice(jsonObject, mLiveBll.getCounTeacherStr());
        } catch (Exception e) {
            mLogtf.e("sendLikeNum", e);
        }
    }

    @Override
    public void setVideoLayout(LiveVideoPoint liveVideoPoint) {

    }

    OnPraisePageListener listener = new OnPraisePageListener() {
        @Override
        public void onPraiseClick(int num) {
            sendLikeNum(num);
        }

        @Override
        public void onPracticeClose() {
        //    setCloase(true);
        }

    };

    public String getLikeId() {
        return likeId;
    }

    public void setLikeId(String likeId) {
        this.likeId = likeId;
    }

    public boolean isCloase() {
        return isCloase;
    }

    public void setCloase(boolean cloase) {
        isCloase = cloase;
    }
}
