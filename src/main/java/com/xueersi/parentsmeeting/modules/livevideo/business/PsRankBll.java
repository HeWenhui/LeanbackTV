package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.page.RankPager;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;


import java.util.ArrayList;

/**
 * Created by David on 2018/7/18.
 */

public class PsRankBll {
    /** 组内排名的根布局*/
    private RelativeLayout rlRankingContent;
    /** 组内排名的界面*/
    public RankPager mRankPager;
    Activity liveVideoActivity;
    private LiveBll mLiveBll;
    BaseLiveMediaControllerBottom liveMediaControllerBottom;
    LiveMediaController mMediaController;
    Button rl_livevideo_common_rank;
    RelativeLayout relativeLayout;
    LiveGetInfo mGetInfo;

    public PsRankBll(Activity liveVideoActivity) {
        this.liveVideoActivity = liveVideoActivity;
    }

    public void setLiveMediaController(final LiveMediaController mMediaController, BaseLiveMediaControllerBottom liveMediaControllerBottom) {
        this.liveMediaControllerBottom = liveMediaControllerBottom;
        this.mMediaController = mMediaController;
    }

    public void setLiveBll(LiveBll liveBll) {
        this.mLiveBll = liveBll;
    }

    public void setGetInfo(LiveGetInfo getInfo) {
        this.mGetInfo = getInfo;
    }

    public void initView(final RelativeLayout bottomContent, ViewGroup.LayoutParams lp2) {
        rl_livevideo_common_rank = (Button) liveMediaControllerBottom.findViewById(R.id.rl_livevideo_common_rank);
        if (rl_livevideo_common_rank == null) {
            return;
        }
        rl_livevideo_common_rank.setVisibility(View.VISIBLE);
        relativeLayout = new RelativeLayout(liveVideoActivity);
        relativeLayout.setId(R.id.rl_livevideo_ranking);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        bottomContent.addView(relativeLayout, lp);
        // 添加小组排名的pager
        mRankPager = new RankPager(liveVideoActivity,mLiveBll,mMediaController,rl_livevideo_common_rank);
        relativeLayout.removeAllViews();
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        relativeLayout.addView(mRankPager.getRootView(), params);
    }

    public void setVideoLayout(int width, int height) {
        if (relativeLayout == null) {
            return;
        }
        final View contentView = liveVideoActivity.findViewById(android.R.id.content);
        final View actionBarOverlayLayout = (View) contentView.getParent();
        Rect r = new Rect();
        actionBarOverlayLayout.getWindowVisibleDisplayFrame(r);
        int screenWidth = (r.right - r.left);
        int screenHeight = ScreenUtils.getScreenHeight();
        if (width > 0) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) relativeLayout.getLayoutParams();
            int wradio = (int) (LiveVideoConfig.VIDEO_HEAD_WIDTH * width / LiveVideoConfig.VIDEO_WIDTH);
            wradio += (screenWidth - width) / 2;
            if (wradio != params.width) {
                //Loger.e(TAG, "setVideoWidthAndHeight:screenWidth=" + screenWidth + ",width=" + width + "," + height
                // + ",wradio=" + wradio + "," + params.width);
                params.width = wradio;
//                relativeLayout.setLayoutParams(params);
                LayoutParamsUtil.setViewLayoutParams(relativeLayout, params);
            }
        }
        if (height > 0) {

        }
    }
}
