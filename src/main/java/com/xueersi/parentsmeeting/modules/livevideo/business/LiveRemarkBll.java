package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;

import com.xueersi.parentsmeeting.cloud.XesCloudUploadBusiness;
import com.xueersi.parentsmeeting.cloud.config.CloudDir;
import com.xueersi.parentsmeeting.cloud.config.XesCloudConfig;
import com.xueersi.parentsmeeting.cloud.entity.CloudUploadEntity;
import com.xueersi.parentsmeeting.cloud.entity.XesCloudResult;
import com.xueersi.parentsmeeting.cloud.listener.XesStsUploadListener;
import com.xueersi.parentsmeeting.config.FileConfig;
import com.xueersi.parentsmeeting.http.HttpCallBack;
import com.xueersi.parentsmeeting.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoActivity;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveVideoView;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveTextureView;
import com.xueersi.parentsmeeting.modules.videoplayer.media.MIJKMediaPlayer;
import com.xueersi.parentsmeeting.modules.videoplayer.media.VideoView;
import com.xueersi.parentsmeeting.modules.videoplayer.media.XESMediaPlayer;
import com.xueersi.xesalib.utils.app.XESToastUtils;
import com.xueersi.xesalib.utils.log.Loger;
import com.xueersi.xesalib.utils.uikit.ImageUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Timer;
import java.util.TimerTask;

import tv.danmaku.ijk.media.player.FrameInfo;

/**
 * Created by Tang on 2018/3/5.
 */

public class LiveRemarkBll {
    private Context mContext;
    private MIJKMediaPlayer mPlayer;
    private String TAG = "LiveRemarkBll";
    private Timer mTimer;
    private long offSet;
    private LiveMediaControllerBottom mLiveMediaControllerBottom;
    private long sysTimeOffset;
    private VideoView mVideoView;
    private int displayHeight;
    private int displayWidth;
    private int wradio;
    private double videoWidth;
    private LiveHttpManager mHttpManager;
    private XesCloudUploadBusiness mCloudUploadBusiness;

    public LiveRemarkBll(Context context, XESMediaPlayer player) {
        mContext = context;
        mPlayer = (MIJKMediaPlayer) player;
        initData();
    }

    public void setLiveMediaControllerBottom(LiveMediaControllerBottom liveMediaControllerBottom) {
        mLiveMediaControllerBottom = liveMediaControllerBottom;
        mLiveMediaControllerBottom.getBtMark().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
//                new Thread(){
//                    @Override
//                    public void run() {
//                        Bitmap bitmap = mPlayer.getFrameAtTime(0, 0);
//                        Loger.d(TAG, "getFrameAtTime:bitmap=" + bitmap);
//                        XESToastUtils.showToast(mContext, "" + (bitmap == null));
//                    }
//                }.start();
                final LiveTextureView liveTextureView = (LiveTextureView) ((Activity) mContext).findViewById(R.id.ltv_course_video_video_texture);
                if (liveTextureView == null) {
                    return;
                }
                final LiveVideoView liveVideoView = (LiveVideoView) ((Activity) mContext).findViewById(R.id.vv_course_video_video);
//                liveVideoView.setVisibility(View.INVISIBLE);
                mPlayer.setSurface(liveTextureView.surface);
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPlayer.setDisplay(liveVideoView.getSurfaceHolder());
                        v.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mPlayer.setSurface(liveTextureView.surface);
                                v.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Bitmap bitmap = liveTextureView.getBitmap();
                                        XESToastUtils.showToast(mContext, "" + (bitmap == null));
                                        if (bitmap != null) {
                                            File saveDir = new File(Environment.getExternalStorageDirectory(), "parentsmeeting/save");
                                            if (!saveDir.exists()) {
                                                saveDir.mkdirs();
                                            }
                                            File file = new File(saveDir, "" + System.currentTimeMillis() + ".png");
                                            ImageUtils.save(bitmap, file, Bitmap.CompressFormat.JPEG);
                                        }
                                        mPlayer.setDisplay(liveVideoView.getSurfaceHolder());
                                    }
                                }, 100);
                            }
                        }, 100);
                    }
                }, 100);
//                Bitmap bitmap = liveVideoView2.getBitmap();
//                XESToastUtils.showToast(mContext, "" + (bitmap == null));
//                mPlayer.setSurface(liveVideoView.getSurfaceHolder().getSurface());
//                if (bitmap != null) {
//                    File saveDir = new File(Environment.getExternalStorageDirectory(), "parentsmeeting/save");
//                    if (!saveDir.exists()) {
//                        saveDir.mkdirs();
//                    }
//                    File file = new File(saveDir, "" + System.currentTimeMillis() + ".png");
////                        canvas.drawBitmap(bitmap, 0, 0, null);
//                    ImageUtils.save(bitmap, file, Bitmap.CompressFormat.JPEG);
//                }
            }
        });
    }

    private void initData() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (mPlayer == null) {
                    return;
                }
                long tcpSpeed = mPlayer.getTcpSpeed();
                float vdfps = mPlayer.getVideoDecodeFramesPerSecond();
                if (Math.round(vdfps) == 12) {
                    //mTimer.cancel();
                    Loger.i(TAG, "dfps   " + vdfps);
                    FrameInfo frameInfo = mPlayer.native_getFrameInfo();
                    offSet = System.currentTimeMillis() - frameInfo.pkt;
                    Loger.i(TAG, "nowtime  " + frameInfo.nowTime + "   dts     " + frameInfo.pkt_dts
                            + "   pkt   " + frameInfo.pkt + "  cache:" + mPlayer.getVideoCachedDuration());
//                    mLiveMediaControllerBottom.getBtMark().setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            reMark();
//                        }
//                    });
                    mTimer.cancel();
                } else {
//                    mLiveMediaControllerBottom.getBtMark().setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            XESToastUtils.showToast(mContext, "正在缓冲视频，请稍后再标记");
//                        }
//                    });
                }

            }
        };
        mTimer = new Timer();
        mTimer.schedule(task, 1000, 1000);
        mCloudUploadBusiness = new XesCloudUploadBusiness(mContext);
        mHttpManager = new LiveHttpManager(mContext);
    }

    public void hideBtMark() {
        if (mLiveMediaControllerBottom != null) {
            mLiveMediaControllerBottom.getBtMark().setVisibility(View.GONE);
        }
    }

    public void showBtMark() {
        if (mLiveMediaControllerBottom != null) {
            mLiveMediaControllerBottom.getBtMark().setVisibility(View.VISIBLE);
        }
    }

    public void setSysTimeOffset(long sysTimeOffset) {
        this.sysTimeOffset = sysTimeOffset;
    }

    public void setVideoView(VideoView videoView) {
        mVideoView = videoView;
    }

    private void reMark() {
        Bitmap bmp = getPicture();
        String fileName = savePicture(bmp);
        final long time = mPlayer.native_getFrameInfo().pkt - mPlayer.getVideoCachedDuration() + offSet;
        if (!TextUtils.isEmpty(fileName)) {
            CloudUploadEntity entity = new CloudUploadEntity();
            entity.setFilePath(fileName);
            entity.setType(XesCloudConfig.UPLOAD_IMAGE);
            entity.setCloudPath(CloudDir.LIVE_MARK);
            mCloudUploadBusiness.asyncUpload(entity, new XesStsUploadListener() {
                @Override
                public void onProgress(XesCloudResult result, int percent) {
                    Loger.i(TAG, "progress " + percent);
                }

                @Override
                public void onSuccess(XesCloudResult result) {
                    Loger.i(TAG, "upCloud Sucess");
                    mHttpManager.saveLiveMark("" + time, result.getHttpPath(), new HttpCallBack() {
                        @Override
                        public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                            XESToastUtils.showToast(mContext, "标记成功");
                        }
                    });
                }

                @Override
                public void onError(XesCloudResult result) {
                    Loger.i(TAG, result.getErrorMsg());
                }
            });
        } else {
            XESToastUtils.showToast(mContext, "标记失败");
        }

    }

    public void setLayout(int width, int height) {
        int screenWidth = getScreenParam();
        displayHeight = height;
        displayWidth = screenWidth;
        if (width > 0) {
            wradio = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * width / LiveVideoActivity.VIDEO_WIDTH);
            wradio += (screenWidth - width) / 2;
            if (displayWidth - wradio != videoWidth) {
                videoWidth = displayWidth - wradio;
            }
        }
    }

    private int getScreenParam() {
        final View contentView = ((Activity) mContext).findViewById(android.R.id.content);
        final View actionBarOverlayLayout = (View) contentView.getParent();
        Rect r = new Rect();
        actionBarOverlayLayout.getWindowVisibleDisplayFrame(r);
        return (r.right - r.left);
    }

    private Bitmap getPicture() {
        int w = mVideoView.getWidth();
        int h = mVideoView.getHeight();
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);

        c.drawColor(Color.WHITE);
        /** 如果不设置canvas画布为白色，则生成透明 */

        mVideoView.draw(c);
        mVideoView.setDrawingCacheEnabled(true);
        mVideoView.buildDrawingCache();
        return mVideoView.getDrawingCache();
        //canvas.drawBitmap(bmp);

        //return bmp;
    }

    private String savePicture(Bitmap bmp) {
        String fileName = System.currentTimeMillis() + "_mark.jpg";
        try {
            File appDir = new File(Environment.getExternalStorageDirectory(), FileConfig.downPathImageDir);
            if (!appDir.exists()) {
                appDir.mkdir();
            }

            File file = new File(appDir, fileName);

            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
