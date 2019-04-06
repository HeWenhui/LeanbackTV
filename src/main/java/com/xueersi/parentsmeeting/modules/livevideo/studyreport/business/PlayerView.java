package com.xueersi.parentsmeeting.modules.livevideo.studyreport.business;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.view.ViewStub;

import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.module.videoplayer.media.PlayerService;
import com.xueersi.parentsmeeting.module.videoplayer.media.XESMediaPlayer;
import com.xueersi.parentsmeeting.module.videoplayer.ps.PSIJK;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveTextureView;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveVideoView;

//import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class PlayerView {
    Logger logger = LoggerFactory.getLogger("PlayerView");

    public void getBitmap(final PlayerService vPlayer, Activity activity, final OnGetBitmap onGetBitmap) {
        final LiveTextureView liveTextureView;
        ViewStub viewStub = activity.findViewById(R.id.vs_course_video_video_texture);
        if (viewStub != null) {
            liveTextureView = (LiveTextureView) viewStub.inflate();
        } else {
            liveTextureView = activity.findViewById(R.id.ltv_course_video_video_texture);
        }
        if (liveTextureView != null) {
            liveTextureView.vPlayer = vPlayer;
            final LiveVideoView liveVideoView = activity.findViewById(R.id.vv_course_video_video);
            liveTextureView.setLayoutParams(liveVideoView.getLayoutParams());
            XESMediaPlayer xesMediaPlayer = vPlayer.getPlayer();
//            if (xesMediaPlayer instanceof IjkMediaPlayer) {
//                final IjkMediaPlayer ijkMediaPlayer = (IjkMediaPlayer) xesMediaPlayer;
//                ijkMediaPlayer.setSurface(liveTextureView.surface);
//                final Handler handler = new Handler(Looper.getMainLooper());
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (vPlayer.isInitialized()) {
//                            XESMediaPlayer xesMediaPlayer = vPlayer.getPlayer();
//                            if (xesMediaPlayer instanceof IjkMediaPlayer) {
//                                final IjkMediaPlayer ijkMediaPlayer = (IjkMediaPlayer) xesMediaPlayer;
//                                ijkMediaPlayer.setDisplay(liveVideoView.getSurfaceHolder());
//                                logger.d("onGetBitmap:setDisplay:liveVideoView");
//                            }
//                        }
//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (vPlayer.isInitialized()) {
//                                    XESMediaPlayer xesMediaPlayer = vPlayer.getPlayer();
//                                    if (xesMediaPlayer instanceof IjkMediaPlayer) {
//                                        final IjkMediaPlayer ijkMediaPlayer = (IjkMediaPlayer) xesMediaPlayer;
//                                        ijkMediaPlayer.setSurface(liveTextureView.surface);
//                                        logger.d("onGetBitmap:setDisplay:liveTextureView");
//                                        handler.postDelayed(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                Bitmap bitmap = liveTextureView.getBitmap();
//                                                vPlayer.setDisplay(liveVideoView.getHolder());
//                                                logger.d("onGetBitmap:bitmap=null?" + (bitmap == null));
//                                                onGetBitmap.onGetBitmap(bitmap);
//                                            }
//                                        }, 200);
//                                    } else {
//                                        logger.d("onGetBitmap:null1");
//                                        onGetBitmap.onGetBitmap(null);
//                                    }
//                                } else {
//                                    logger.d("onGetBitmap:null2");
//                                    onGetBitmap.onGetBitmap(null);
//                                }
//                            }
//                        }, 200);
//                    }
//                }, 100);
//            } else {
//                logger.d("onGetBitmap:null3");
//                onGetBitmap.onGetBitmap(null);
//            }

            if (xesMediaPlayer instanceof PSIJK) {
                final PSIJK mPlayer = (PSIJK) xesMediaPlayer;
                mPlayer.setSurface(liveTextureView.surface);
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (vPlayer.isInitialized()) {
                            XESMediaPlayer xesMediaPlayer = vPlayer.getPlayer();
                            if (xesMediaPlayer instanceof PSIJK) {
                                final PSIJK ijkMediaPlayer = (PSIJK) xesMediaPlayer;
                                ijkMediaPlayer.setDisplay(liveVideoView.getSurfaceHolder());
                                logger.d("onGetBitmap:setDisplay:liveVideoView");
                            }
                        }
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (vPlayer.isInitialized()) {
                                    XESMediaPlayer xesMediaPlayer = vPlayer.getPlayer();
                                    if (xesMediaPlayer instanceof PSIJK) {
                                        final PSIJK mediaPlayer = (PSIJK) xesMediaPlayer;
                                        mediaPlayer.setSurface(liveTextureView.surface);
                                        logger.d("onGetBitmap:setDisplay:liveTextureView");
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Bitmap bitmap = liveTextureView.getBitmap();
                                                vPlayer.setDisplay(liveVideoView.getHolder());
                                                logger.d("onGetBitmap:bitmap=null?" + (bitmap == null));
                                                onGetBitmap.onGetBitmap(bitmap);
                                            }
                                        }, 200);
                                    } else {
                                        logger.d("onGetBitmap:null1");
                                        onGetBitmap.onGetBitmap(null);
                                    }
                                } else {
                                    logger.d("onGetBitmap:null2");
                                    onGetBitmap.onGetBitmap(null);
                                }
                            }
                        }, 200);
                    }
                }, 100);
            } else {
                logger.d("onGetBitmap:null3");
                onGetBitmap.onGetBitmap(null);
            }

        } else {
            logger.d("onGetBitmap:null4");
            onGetBitmap.onGetBitmap(null);
        }
    }

    public interface OnGetBitmap {
        void onGetBitmap(Bitmap bitmap);
    }
}
