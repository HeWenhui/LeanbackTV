package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController;
import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController.MediaPlayerControl;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveUIStateListener;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;

import java.util.ArrayList;

/**
 * 直播播放器控制栏底部区域
 */
public class LiveStandMediaControllerBottom extends BaseLiveMediaControllerBottom {
    String TAG = "LiveMediaControllerBottom";
    View tranLiveView;
    View mainLiveView;
    String mode = LiveTopic.MODE_TRANING;
    private Button btRaiseHands;
    ArrayList<LiveUIStateListener> onViewChanges = new ArrayList<>();

    public LiveStandMediaControllerBottom(Context context, LiveMediaController controller, MediaPlayerControl player) {
        super(context, controller, player);
    }


    @Override
    protected void initResources() {
        super.initResources();
    }

    /** 播放器的布局界面 */
    @Override
    public View inflateLayout() {
        View view;
//        return LayoutInflater.from(mContext).inflate(R.layout.layout_livestand_mediacontroller_bottom, this);
        if (LiveTopic.MODE_CLASS.equals(mode)) {
            if (mainLiveView == null) {
                mainLiveView = LayoutInflater.from(mContext).inflate(R.layout.layout_livestand_mediacontroller_bottom, this, false);
            }
            view = mainLiveView;
            addView(view);
        } else {
            if (tranLiveView == null) {
                tranLiveView = LayoutInflater.from(mContext).inflate(R.layout.layout_livemediacontroller_bottom, this, false);
            }
            view = tranLiveView;
            addView(view);
        }
        logger.d( "inflateLayout:mode=" + mode + ",mainLiveView=" + mainLiveView + ",tranLiveView=" + tranLiveView);
        return view;
    }

    public void onModeChange(String mode, LiveGetInfo getInfo) {
        this.mode = mode;
        removeAllViews();
        inflateLayout();
        findViewItems();
        btRaiseHands = findViewById(R.id.bt_livevideo_voicechat_raise_hands);
        if (btRaiseHands != null && getInfo != null) {
            boolean allowLinkMic = getInfo.isAllowLinkMic();
            btRaiseHands.setVisibility(allowLinkMic ? VISIBLE : GONE);
        }
        noticeUIChange();
    }

    /**
     * 通知UI 状态改变
     */
    protected void noticeUIChange() {
        for (LiveUIStateListener listener : onViewChanges) {
            listener.onViewChange(this);
        }
    }

    public void addOnViewChange(LiveUIStateListener listener) {
        if (!onViewChanges.contains(listener)) {
            onViewChanges.add(listener);
        }
    }
    /*public interface OnViewChange {
        void onViewChange(BaseLiveMediaControllerBottom baseLiveMediaControllerBottom);
    }*/
}
