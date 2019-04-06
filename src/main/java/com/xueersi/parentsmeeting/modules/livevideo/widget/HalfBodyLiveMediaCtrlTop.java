package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;

/**
*半身直播 顶部控制栏
*@author chekun
*created  at 2018/12/4 16:28
*/
public class HalfBodyLiveMediaCtrlTop extends BaseLiveMediaControllerTop {

    String mode = LiveTopic.MODE_TRANING;
    private View mainLiveView;
    private View tranLiveView;

    public HalfBodyLiveMediaCtrlTop(Context context, LiveMediaController controller, LiveMediaController
            .MediaPlayerControl mPlayer) {
        super(context, controller, mPlayer);
    }

    @Override
    protected View inflateLayout() {
         View view;
        if (LiveTopic.MODE_CLASS.equals(mode)) {
            if (mainLiveView == null) {
                mainLiveView = LayoutInflater.from(mContext).inflate(R.layout.layout_live_halfbody_mediacontroller_top,
                         this, false);
            }
            view = mainLiveView;
            addView(view);
        } else {
            if(tranLiveView == null){
                tranLiveView = LayoutInflater.from(mContext).inflate(R.layout.layout_livemediacontroller_top,this, false);
            }
            view = tranLiveView;
            addView(view);
        }
        return  view;
    }

    @Override
    public void setAutoOrientation(boolean autoOrientation) {
        if(LiveTopic.MODE_TRANING.equals(mode)){
            super.setAutoOrientation(autoOrientation);
        }
    }

    @Override
    public void setFileName(String name) {
        if(LiveTopic.MODE_TRANING.equals(mode)){
             super.setFileName(name);
        }
    }

    @Override
    public void setMarkPointsOp(boolean isShow, OnClickListener listener) {
        if(LiveTopic.MODE_TRANING.equals(mode)){
            super.setMarkPointsOp(isShow,listener);
        }
    }

    /**
     * 主辅导且切换
     * @param mode
     * @param getInfo
     */
    public void onModeChange(String mode,LiveGetInfo getInfo){
        this.mode = mode;
        removeAllViewsInLayout();
        inflateLayout();
        findViewItems();
    }
}
