package com.xueersi.parentsmeeting.modules.livevideoOldIJK.message.business;

import android.app.Activity;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.message.pager.LiveBackVideoMessagePager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.LayoutParamsUtil;

/**
 * 全身直播回放，在聊天区加上MMD皮肤
 */
public class LiveMessageBackBll extends LiveBackBaseBll {
    //是否是小英，使用MMD皮肤
    private boolean isSmallEnglish = false;

    LiveBackVideoMessagePager liveBackVideoMessagePager;

    public LiveMessageBackBll(Activity activity, LiveBackBll liveBackBll) {
        super(activity, liveBackBll);
    }

    @Override
    public void initView() {
        super.initView();

        if (liveGetInfo != null) {
            isSmallEnglish = liveGetInfo.getSmallEnglish();
            if (isSmallEnglish) {
                liveBackVideoMessagePager = new LiveBackVideoMessagePager((mContext));
                mRootViewBottom.addView(liveBackVideoMessagePager.getRootView(), 0,
                        liveBackVideoMessagePager.getBoardParams());
            }
        }
    }

    @Override
    public void setVideoLayout(LiveVideoPoint liveVideoPoint) {
        super.setVideoLayout(liveVideoPoint);
        if (liveBackVideoMessagePager != null) {
            RelativeLayout viewGroup = (RelativeLayout) liveBackVideoMessagePager.getRootView();
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewGroup.getLayoutParams();
            int wradio = liveVideoPoint.x4 - liveVideoPoint.x3;
            if (wradio != params.width || params.rightMargin != liveVideoPoint.screenWidth - liveVideoPoint.x4) {
                //logger.e( "setVideoWidthAndHeight:screenWidth=" + screenWidth + ",width=" + width + "," + height
                // + ",wradio=" + wradio + "," + params.width);
                params.width = wradio;
                params.rightMargin = liveVideoPoint.screenWidth - liveVideoPoint.x4;
//                relativeLayout.setLayoutParams(params);
                LayoutParamsUtil.setViewLayoutParams(viewGroup, params);
            }
        }
    }
}