package com.xueersi.parentsmeeting.modules.livevideoOldIJK.business;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.page.LivePublicPraisePager;

import org.json.JSONObject;

/**
 * Created by linyuqiang on 2016/9/23.
 */
public class PraiseOrEncourageBll implements PraiseOrEncourageAction, Handler.Callback {
    Activity activity;
    private WeakHandler mVPlayVideoControlHandler = new WeakHandler(this);
    /** 表扬 */
    private LivePublicPraisePager mLivePublicPraisePager;

    public PraiseOrEncourageBll(Activity activity) {
        this.activity = activity;
    }

    public void initView(RelativeLayout bottomContent) {
        RelativeLayout rlPublicPraise = new RelativeLayout(activity);
        mLivePublicPraisePager = new LivePublicPraisePager(activity);
        rlPublicPraise.addView(mLivePublicPraisePager.getRootView(), new ViewGroup.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        bottomContent.addView(rlPublicPraise, params);
    }

    public void onLiveInit(LiveGetInfo getInfo) {
        if (mLivePublicPraisePager != null) {
            mLivePublicPraisePager.setGetInfo(getInfo);
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        return false;
    }

    @Override
    public void onPraiseOrEncourage(final JSONObject jsonObject) {
        if (mLivePublicPraisePager != null) {
            mVPlayVideoControlHandler.post(new Runnable() {
                @Override
                public void run() {
                    mLivePublicPraisePager.onPraiseOrEncourage(new LivePublicPraisePager.JsonType(LivePublicPraisePager.JsonType.TYPE_PRAISE, jsonObject), true);
                }
            });
        }
    }

    public void addFighting(JSONObject jsonObject) {
        if (mLivePublicPraisePager != null) {
            mLivePublicPraisePager.onPraiseOrEncourage(new LivePublicPraisePager.JsonType(LivePublicPraisePager.JsonType.TYPE_FIGHT, jsonObject), true);
        }
    }
}
