package com.xueersi.parentsmeeting.modules.livevideo.message.business;

import android.app.Activity;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.message.pager.LiveBackVideoMessagePager;

import java.util.concurrent.atomic.AtomicBoolean;

//直播回放，在聊天区加上MMD皮肤
public class LiveMessageBackBll extends LiveBackBaseBll {
    //是否是小英，使用MMD皮肤
    private boolean isSmallEnglish = false;

    public LiveMessageBackBll(Activity activity, LiveBackBll liveBackBll) {
        super(activity, liveBackBll);
    }

    @Override
    public void initView(RelativeLayout bottomContent, AtomicBoolean mIsLand) {
        super.initView(bottomContent, mIsLand);
        if (liveGetInfo != null) {
            isSmallEnglish = liveGetInfo.getSmallEnglish();
        }
        if (isSmallEnglish) {
            LiveBackVideoMessagePager liveBackVideoMessagePager = new LiveBackVideoMessagePager((mContext));
            bottomContent.addView(liveBackVideoMessagePager.getRootView(), 0, liveBackVideoMessagePager
                    .getBoardParams());
        }
    }
}
