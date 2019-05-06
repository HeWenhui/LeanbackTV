package com.xueersi.parentsmeeting.widget.praise.business;

import android.app.Activity;
import android.view.View;

import com.xueersi.common.business.UserBll;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
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
import com.xueersi.parentsmeeting.widget.praise.entity.PraiseEntity;
import com.xueersi.ui.dialog.VerifyCancelAlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Zhang Yuansun on 2018/7/27.
 */

public class PraiseTutorBll extends LiveBaseBll implements NoticeAction, TopicAction {


    public PraiseTutorBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
    }

    @Override
    public void onModeChange(String oldMode, String mode, boolean isPresent) {
        //模式切换为主讲，关闭表扬榜

    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
    }

    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {
        UmsAgentManager.umsAgentDebug(mContext,"tutor_practice_notice","type"+type+"/sourceNick"+sourceNick
                +"target"+target+"data:"+data.toString());
    }

    @Override
    public int[] getNoticeFilter() {
        return new int[]{
               XESCODE.TUTOR_ROOM_PRAISE_OPEN,
                XESCODE.TUTOR_ROOM_PRAISE_LIKE
        };
    }

    @Override
    public void onTopic(LiveTopic liveTopic, JSONObject jsonObject, boolean modeChange) {

    }

    private void getPraiseTutorData(String rankId){
        String classId = "";
        String courseId = "";
        String tutorId = "";
        if (mGetInfo.getStudentLiveInfo() != null) {
            classId = mGetInfo.getStudentLiveInfo().getClassId();
            courseId= mGetInfo.getStudentLiveInfo().getCourseId();
            tutorId = mGetInfo.getTeacherId();
        }
        getHttpManager().getPraoseTutorList(rankId, mLiveId, courseId,tutorId,new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                PraiseEntity entity  = getHttpResponseParser().parseTutorPraiseEntity( responseEntity);
            }
        });


    }@Override
    public void setVideoLayout(LiveVideoPoint liveVideoPoint) {

    }
}
