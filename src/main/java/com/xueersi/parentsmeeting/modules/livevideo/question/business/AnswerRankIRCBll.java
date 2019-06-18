package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.app.Activity;

import com.alibaba.fastjson.JSON;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RankUserEntity;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by linyuqiang on 2018/7/5.
 */
public class AnswerRankIRCBll extends LiveBaseBll implements NoticeAction {
    AnswerRankBll mAnswerRankBll;

    public AnswerRankIRCBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
        putInstance(AnswerRankIRCBll.class, this);
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        if (mGetInfo.getStudentLiveInfo() != null
                && mGetInfo.getIs_show_ranks().equals("1")) {
            mAnswerRankBll = new AnswerRankBll(activity, contextLiveAndBackDebug);
            mAnswerRankBll.initView(mRootView);
            mAnswerRankBll.setLiveHttpManager(getHttpManager());
            mAnswerRankBll.setClassId(mGetInfo.getStudentLiveInfo().getClassId());
            mAnswerRankBll.setTeamId(mGetInfo.getStudentLiveInfo().getTeamId());
            mAnswerRankBll.setIsShow(mGetInfo.getIs_show_ranks());
        } else {
            mLiveBll.removeBusinessBll(this);
        }
    }

    public AnswerRankBll getAnswerRankBll() {
        return mAnswerRankBll;
    }

    @Override
    public void onNotice(String sourceNick, String target, JSONObject object, int type) {
        switch (type) {
            case XESCODE.STOPQUESTION:
                setNonce(object.optString("nonce"));
                break;
            case XESCODE.EXAM_START: {
                String num = object.optString("num", "0");
                setTestId(num);
            }
            break;
            case XESCODE.EXAM_STOP: {
                setNonce(object.optString("nonce"));
            }
            break;
            case XESCODE.RANK_TEA_MESSAGE: {
                try {
                    List<RankUserEntity> lst = JSON.parseArray(object.optString("stuInfo"), RankUserEntity.class);
                    if (mAnswerRankBll != null) {
                        mAnswerRankBll.showRankList(lst, -1);
                    }
                } catch (Exception e) {
                    logger.i("=====notice " + e.getMessage());
                }
            }
            break;
            default:
                break;
        }
    }

    @Override
    public int[] getNoticeFilter() {
        return new int[]{
                XESCODE.STOPQUESTION, XESCODE.EXAM_START, XESCODE.EXAM_STOP, XESCODE.RANK_TEA_MESSAGE
        };
    }

    public void setTestId(String testId) {
        if (mAnswerRankBll != null) {
            mAnswerRankBll.setTestId(testId);
        }
    }

    public void setNonce(String nonce) {
        if (mAnswerRankBll != null) {
            mAnswerRankBll.setNonce(nonce);
        }
    }

    public void setType(String type) {
        if (mAnswerRankBll != null) {
            mAnswerRankBll.setType(type);
        }
    }

    @Override
    public void setVideoLayout(LiveVideoPoint liveVideoPoint) {
        if (mAnswerRankBll != null) {
            mAnswerRankBll.setVideoLayout(liveVideoPoint);
        }
    }
}
