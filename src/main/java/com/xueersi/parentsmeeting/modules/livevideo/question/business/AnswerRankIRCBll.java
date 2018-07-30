package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.app.Activity;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;

import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by lyqai on 2018/7/5.
 */

public class AnswerRankIRCBll extends LiveBaseBll implements NoticeAction {
    AnswerRankBll mAnswerRankBll;

    public AnswerRankIRCBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
        mAnswerRankBll = new AnswerRankBll(context, liveBll);
        putInstance(AnswerRankIRCBll.class, this);
    }

    @Override
    public void initView(RelativeLayout bottomContent, AtomicBoolean mIsLand) {
        mAnswerRankBll.initView(bottomContent);
    }

    public AnswerRankBll getAnswerRankBll() {
        return mAnswerRankBll;
    }

    @Override
    public void onNotice(JSONObject object, int type) {
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
            default:
                break;
        }
    }

    @Override
    public int[] getNoticeFilter() {
        return new int[]{
                XESCODE.STOPQUESTION, XESCODE.EXAM_START, XESCODE.EXAM_STOP
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
