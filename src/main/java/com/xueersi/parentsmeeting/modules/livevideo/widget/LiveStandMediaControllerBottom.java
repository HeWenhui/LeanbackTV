package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.videoplayer.media.LiveMediaController;
import com.xueersi.parentsmeeting.modules.videoplayer.media.LiveMediaController.MediaPlayerControl;
import com.xueersi.xesalib.utils.log.Loger;

import java.io.IOException;
import java.io.InputStream;

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
