package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.videoplayer.media.LiveMediaController;
import com.xueersi.parentsmeeting.modules.videoplayer.media.LiveMediaController.MediaPlayerControl;
import com.xueersi.xesalib.utils.log.Loger;

/**
 * 直播播放器控制栏底部区域
 */
public class LiveStandMediaControllerBottom extends BaseLiveMediaControllerBottom {
    String TAG = "LiveMediaControllerBottom";
    View tranLiveView;
    View mainLiveView;
    String mode = LiveTopic.MODE_TRANING;

    public LiveStandMediaControllerBottom(Context context, LiveMediaController controller, MediaPlayerControl player) {
        super(context, controller, player);
    }

    @Override
    protected void initResources() {
        super.initResources();
    }

    /** 播放器的布局界面 */
    public View inflateLayout() {
        View view;
//        return LayoutInflater.from(mContext).inflate(R.layout.layout_livestand_mediacontroller_bottom, this);
        if (LiveTopic.MODE_CLASS.equals(mode)) {
            if (mainLiveView != null) {
                view = mainLiveView;
//                ViewGroup group = (ViewGroup) view.getParent();
//                if (group != null) {
//                    group.removeAllViews();
//                }
                addView(view);
            } else {
                view = LayoutInflater.from(mContext).inflate(R.layout.layout_livestand_mediacontroller_bottom, this);
                mainLiveView = getChildAt(0);
            }
        } else {
            if (tranLiveView != null) {
                view = tranLiveView;
//                ViewGroup group = (ViewGroup) view.getParent();
//                if (group != null) {
//                    group.removeAllViews();
//                }
                addView(view);
            } else {
                view = LayoutInflater.from(mContext).inflate(R.layout.layout_livemediacontroller_bottom, this);
                tranLiveView = getChildAt(0);
            }
        }
        Loger.d(TAG, "inflateLayout:mode=" + mode + ",mainLiveView=" + mainLiveView + ",tranLiveView=" + tranLiveView);
        return view;
    }

    public void onModeChange(String mode) {
        this.mode = mode;
        removeAllViews();
        inflateLayout();
        findViewItems();
    }

    @Override
    public void onHide() {
        super.onHide();
    }

}
