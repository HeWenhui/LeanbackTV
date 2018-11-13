package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;

/**
 * 半身直播 底部 控制栏
 *
 * @author chenkun
 * @version 1.0, 2018/10/25 上午10:33
 */

public class LiveHalfBodyMediaControllerTop extends BaseLiveMediaControllerTop {

    private String mode = LiveTopic.MODE_TRANING;
    View tranLiveView;
    View mainLiveView;

    public LiveHalfBodyMediaControllerTop(Context context, LiveMediaController controller, LiveMediaController
            .MediaPlayerControl mPlayer) {
        super(context, controller, mPlayer);
    }

    @Override
    protected View inflateLayout() {

        View  view;
        if (LiveTopic.MODE_CLASS.equals(mode)) {
            Log.e(TAG,"=====>inflateLayout called 11111:"+ mainLiveView);
            if(mainLiveView == null){
                mainLiveView = LayoutInflater.from(mContext).inflate(R.layout.layout_live_halfbody_mediacontroller_top,
                        this,false);
            }
            view = mainLiveView;
            addView(view);
        }else{
            Log.e(TAG,"=====>inflateLayout called 2222:"+tranLiveView);
            if (tranLiveView == null) {
                tranLiveView = LayoutInflater.from(mContext).inflate(R.layout.layout_livemediacontroller_top,
                        this, false);
            }
            view = tranLiveView;
            addView(view);

        }
        return view;
    }


    /**
     * 模式发生变话
     * @param mode
     * @param getInfo
     */
    public void onModeChange(String mode, LiveGetInfo getInfo){
          this.mode = mode;

          removeAllViews();

          inflateLayout();

          findViewItems();
    }

    @Override
    public void setFileName(String name) {
        super.setFileName(name);
        Log.e("HalfBodyTop","======>setFileName:"+name);
    }
}
