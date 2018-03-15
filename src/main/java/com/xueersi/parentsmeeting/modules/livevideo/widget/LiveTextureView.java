package com.xueersi.parentsmeeting.modules.livevideo.widget;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;

import com.xueersi.parentsmeeting.modules.videoplayer.media.PlayerService;
import com.xueersi.parentsmeeting.modules.videoplayer.media.VideoView;

/**
 * 使用，可以视频截图
 * Created by linyuqiang on 2017/8/2.
 */
public class LiveTextureView extends TextureView implements TextureView.SurfaceTextureListener {
    public Surface surface;
    public PlayerService vPlayer;

    public LiveTextureView(Context context) {
        super(context);
        setSurfaceTextureListener(this);
    }

    public LiveTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        surface = new Surface(surfaceTexture);
    }

    @Override
    public Canvas lockCanvas() {
        return super.lockCanvas();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        surface = null;
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}
