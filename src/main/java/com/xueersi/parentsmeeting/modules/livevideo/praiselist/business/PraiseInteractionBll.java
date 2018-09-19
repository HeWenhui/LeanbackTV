package com.xueersi.parentsmeeting.modules.livevideo.praiselist.business;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.TopicAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.http.ArtsPraiseHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.page.PraiseInteractionPager;

import org.json.JSONObject;


/**
 * 初高中理科点赞互动
 */

public class PraiseInteractionBll extends LiveBaseBll implements NoticeAction, TopicAction {

    private LiveBll2 mLiveBll;
    private RelativeLayout rlPraiseContentView;
    private PraiseInteractionPager praiseInteractionPager;
    private LiveHttpManager mHttpManager;
    private LiveGetInfo mRoomInitData;
    private ArtsPraiseHttpResponseParser mParser;

    public PraiseInteractionBll(Context context, LiveBll2 liveBll) {
        super((Activity) context, liveBll);
        logger.d("PraiseInteractionBll construct");
        mLiveBll = liveBll;
    }

    public void attachToRootView() {
        rlPraiseContentView = new RelativeLayout(mContext);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.
                LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mRootView.addView(rlPraiseContentView, params);
    }

    /**
     * 显示点赞互动
     */
    private void showPraisePager() {
        praiseInteractionPager = new PraiseInteractionPager(mContext, this);
        rlPraiseContentView.removeAllViews();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        int rightMargin = getRightMargin();
        params.rightMargin = rightMargin;
        rlPraiseContentView.addView(praiseInteractionPager.getRootView(), params);

        praiseInteractionPager.startPraisBtnEnterAnimation();
    }

    private int getRightMargin() {
        return LiveVideoPoint.getInstance().getRightMargin();
    }


    @Override
    public void onStop() {
        if (praiseInteractionPager != null) {
            praiseInteractionPager.onStop();
        }
    }

    @Override
    public void onResume() {
        if (praiseInteractionPager != null) {
            praiseInteractionPager.onResume();
        }
    }

    @Override
    public void onDestory() {
        if (praiseInteractionPager != null) {
            praiseInteractionPager.onDestroy();
        }
    }


    public void closePager() {
        if (rlPraiseContentView != null) {
            mRootView.post(new Runnable() {
                @Override
                public void run() {
                    rlPraiseContentView.removeAllViews();
                }
            });
        }
        praiseInteractionPager = null;
    }


    /**
     * notice 指令集
     */
    private int[] noticeCodes = {
            XESCODE.OPENBARRAGE,
    };

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        logger.d("onLiveInited");
        if (getInfo != null) {
            mHttpManager = getHttpManager();
            mRoomInitData = getInfo;
            attachToRootView();
        }
    }

    @Override
    public int[] getNoticeFilter() {
        return noticeCodes;
    }

    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {
        String name = Thread.currentThread().getName();
        logger.d(" onNotice=" + rlPraiseContentView + ",name=" + name);
        switch (type) {
            case XESCODE.OPENBARRAGE:
                rlPraiseContentView.post(new Runnable() {
                    @Override
                    public void run() {
                        showPraisePager();
                    }
                });

                break;
            default:
                break;
        }
    }

    @Override
    public void onTopic(LiveTopic liveTopic, JSONObject jsonObject, boolean modeChange) {
    }
}
