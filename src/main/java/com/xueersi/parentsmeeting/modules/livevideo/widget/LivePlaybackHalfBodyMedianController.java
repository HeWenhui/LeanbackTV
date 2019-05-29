package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.view.View;

import com.xueersi.parentsmeeting.module.videoplayer.media.BackMediaPlayerControl;

/**
*半身直播 直播回放 播放控制器
*@author chekun
*created  at 2018/11/15 15:53
*/
public class LivePlaybackHalfBodyMedianController extends LivePlaybackMediaController {
    
    public LivePlaybackHalfBodyMedianController(Context context, BackMediaPlayerControl player, boolean mIsLand) {
        super(context, player, mIsLand);
    }

    @Override
    protected View inflateLayout(){
        // TODO: 2018/11/15  返回对应布局
        return  null;
    }

    @Override
    protected void addBottom() {
        //super.addBottom();
        // TODO: 2018/11/15 添加底部控制栏
    }
}
