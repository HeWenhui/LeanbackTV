package com.xueersi.parentsmeeting.modules.livevideo.weight;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.xueersi.common.config.AppConfig;
import com.xueersi.lib.framework.utils.TimeUtils;
import com.xueersi.lib.log.Loger;
import com.xueersi.parentsmeeting.module.videoplayer.media.CommonGestures;
import com.xueersi.parentsmeeting.module.videoplayer.media.ControllerBottomInter;
import com.xueersi.parentsmeeting.module.videoplayer.media.IPlayBackMediaCtr;
import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController;
import com.xueersi.parentsmeeting.modules.livevideo.R;

public class ExperMediaCtrl extends LiveMediaController implements IPlayBackMediaCtr {
    String TAG = "MediaController2";
    /**
     * 总时长
     */
    protected long mDuration;
    /**
     * 当前播放的时间点位
     */
    protected long mCurrentPosition;

    /**
     * 用来记录开始播放时的播放点位
     */
    private long mSeekBeginPosition = 0;

    /**
     * 是否正在手动进度条
     */
    private boolean mDragging;

    /**
     * 是否边手动进度边进行跳转，true为实时跳转 false为停下来再跳转（实时跳转适合用在播放本地视频）
     */
    private boolean mInstantSeeking = false;

    /**
     * 下方控制区布局
     */
    protected ViewGroup mControlsLayout;

    /**
     * 消息提示栏中的文本控件
     */
    protected TextView mTvMessage;

    /**
     * 上方信息栏的播放文件名显示控件
     */
    protected TextView mFileName;

    /**
     * 当前操作的提示文本
     */
    protected TextView mOperationInfo;
    /**
     * 音量或亮度调整时当前值的前景图片
     */
    private ImageView mVolLumNum;
    /**
     * 音量或亮度调整时当前值的背景图片
     */
    private ImageView mVolLumBg;

    /**
     * 快进快退显示的布局区
     */
    protected View mOperationSeekQuick;
    /**
     * 快进快退的增减进度值
     */
    private TextView mTvSeekIncrement;
    /**
     * 快进快退的当前移动到的进度
     */
    protected TextView mTvSeekCurrentTime;

    /**
     * 音量管理
     */
    private AudioManager mAM;
    /**
     * 最大音量
     */
    private int mMaxVolume;
    /**
     * 屏幕亮度
     */
    private float mBrightness = 0.01f;
    /**
     * 当前音量
     */
    private int mVolume = 0;

    /**
     * 控制栏在竖屏时是否拉伸全屏高度
     */
    private boolean isDrawHeight = false;

    private boolean canSeek = true;
    private static long clickCurrentTime = 0l;
    protected ControllerBottomInter controllerBottom;

    /**
     * 控制栏的定位方式
     */
    private int mAnchorLocation = Gravity.TOP;

    public ExperMediaCtrl(Context context, MediaPlayerControl player) {
        super(context, player);
        updatePausePlay();
    }

    /**
     * 播放器的布局界面
     */
    protected View inflateLayout() {
        return ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                R.layout.exper_recode_mediacontroller, this);
    }

    /**
     * 初始化控制界面上的控制部件
     */
    protected void findViewItems() {
        mMediaController = findViewById(com.xueersi.parentsmeeting.module.player.R.id.rl_video_mediacontroller); // 整个控制区

        mFileName = (TextView) findViewById(com.xueersi.parentsmeeting.module.player.R.id.tv_video_mediacontroller_filename); // 当前视频的名称

        mControlsLayout = (ViewGroup) findViewById(com.xueersi.parentsmeeting.module.player.R.id.ll_video_mediacontroller_controls); // 下方控制栏区域
        mTvMessage = (TextView) findViewById(com.xueersi.parentsmeeting.module.player.R.id.tv_video_mediacontroller_message);// 信息提示栏区域的文本显示框
        mOperationInfo = (TextView) findViewById(com.xueersi.parentsmeeting.module.player.R.id.tv_video_mediacontroller_operation_info); // 操作提示文本区
        mOperationVolLum = findViewById(com.xueersi.parentsmeeting.module.player.R.id.icde_video_mediacontroller_operation_volume_brightness); // 音量亮度提示区
        mVolLumBg = (ImageView) findViewById(com.xueersi.parentsmeeting.module.player.R.id.iv_video_mediacontroller_operation_volume_brightness_bg); // 进度背景
        mVolLumNum = (ImageView) findViewById(com.xueersi.parentsmeeting.module.player.R.id.iv_video_mediacontroller_operation_volume_brightness_percent); //
        /** 进度框显示 */
        mOperationSeekQuick = findViewById(com.xueersi.parentsmeeting.module.player.R.id.icde_video_mediacontroller_operation_seek_quick); // 进度提示框
        mTvSeekIncrement = (TextView) findViewById(com.xueersi.parentsmeeting.module.player.R.id.tv_video_mediacontroller_operation_seek_quick_increment); //
        // 进度增减量
        mTvSeekCurrentTime = (TextView) findViewById(com.xueersi.parentsmeeting.module.player.R.id.tv_video_mediacontroller_operation_seek_quick_current_time); // 目标进度地
    }

    @Override
    public View getTitleRightBtn() {
        return null;
    }

    public void setCanSeek(boolean canSeek) {
        this.canSeek = canSeek;
    }

    /**
     * 释放整个控制栏
     */
    @Override
    public void release() {
        ViewGroup group = (ViewGroup) getParent();
        if (group != null) {
            group.removeView(this);
        }
    }

    protected int brightnessIconResid = com.xueersi.parentsmeeting.module.player.R.drawable.ic_video_brightness_bg;

    protected int volumnIconId = com.xueersi.parentsmeeting.module.player.R.drawable.ic_video_volumn_bg;

    /**
     * 显示亮度控制图片
     */
    protected void setBrightnessScale(float scale) {
        setGraphicOperationProgress(brightnessIconResid, scale);
    }

    /**
     * 显示音量控制图片
     */
    protected void setVolumeScale(float scale) {
        setGraphicOperationProgress(volumnIconId, scale);
    }

    /**
     * 通过图片来显示操作的音量和亮度的当前进度
     */
    private void setGraphicOperationProgress(int bgID, float scale) {
        mVolLumBg.setImageResource(bgID);
        mOperationInfo.setVisibility(View.GONE);
        mOperationVolLum.setVisibility(View.VISIBLE);
        ViewGroup.LayoutParams lp = mVolLumNum.getLayoutParams();

        lp.width = (int) (findViewById(com.xueersi.parentsmeeting.module.player.R.id.iv_video_mediacontroller_operation_volume_brightness_full)
                .getLayoutParams().width * scale);
        mVolLumNum.setLayoutParams(lp);
    }

    /**
     * 设置快进快退显示的数据
     */
    private void setSeekQuickOperationProgress(String strIncrement, String strCurrentTime) {
        mOperationInfo.setVisibility(View.GONE);
        mOperationSeekQuick.setVisibility(View.VISIBLE);
        mTvSeekIncrement.setText(strIncrement);
        mTvSeekCurrentTime.setText(strCurrentTime);
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == MSG_SHOW_PROGRESS) {
            // 显示进度区域
            long pos = setProgress();
            if (!mDragging && mShowing) {
                msg = mHandler.obtainMessage(MSG_SHOW_PROGRESS);
                mHandler.sendMessageDelayed(msg, 1000 - (pos % 1000));
                updatePausePlay();
            }
            return true;
        }
        return super.handleMessage(msg);
    }

    /**
     * 更新当前进度显示
     */
    private long setProgress() {

        long position = mPlayer.getCurrentPosition();
        mCurrentPosition = position;
        long duration = mPlayer.getDuration();
        mDuration = duration;
        return setProgress(duration, position);
    }

    /**
     * 更新当前进度显示
     */
    private long setProgress(long duration, long position) {
        if (position < 0) {
            position = 0;
        }
        if ((position + 800) > duration) {
            position = duration;
        }
        int percent = mPlayer.getBufferPercentage();
        // mProgress.setSecondaryProgress(percent * 10);
        mDuration = duration;
        if (controllerBottom != null) {
            controllerBottom.setProgress(mDuration, position);
        }
        return position;
    }

    /**
     * 更新进度提示信息
     */
    protected void seekControlByPosition() {
        if (mCurrentPosition < 0) {
            mCurrentPosition = 0;
        }
        if (mCurrentPosition > mDuration) {
            mCurrentPosition = mDuration;
        }

        // float changeFloat = mCurrentPosition - mPlayer.getCurrentPosition();
        float changeFloat = mCurrentPosition - mSeekBeginPosition;

        String changeString;
        if (changeFloat >= 0) {
            changeString = "+" + TimeUtils.generateTime((long) Math.abs(changeFloat));
        } else {
            changeString = "-" + TimeUtils.generateTime((long) Math.abs(changeFloat));
        }

        /** 设置显示的快进快退时间样式 */

        setSeekQuickOperationProgress(changeString, TimeUtils.generateTime(mCurrentPosition) + "/"
                + TimeUtils.generateTime(mDuration));
    }

    /**
     * 捆绑手势
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mHandler.removeMessages(MSG_HIDE_SYSTEM_UI);
        mHandler.sendEmptyMessageDelayed(MSG_HIDE_SYSTEM_UI, DEFAULT_TIME_OUT);
        return mGestures.onTouchEvent(event) || super.onTouchEvent(event);
    }

    boolean isSeekto = true;
    /**
     * 手势触摸操作接口实现
     */
    protected CommonGestures.GestureTouchListener mTouchListener = new CommonGestures.GestureTouchListener() {
        /** 手势开始 */
        @Override
        public void onGestureBegin() {
            // 初始化系统亮度
            mBrightness = mContext.getWindow().getAttributes().screenBrightness;
            // 初始化系统音量
            mVolume = mAM.getStreamVolume(AudioManager.STREAM_MUSIC);

            // 初始化播放点位
            mCurrentPosition = mPlayer.getCurrentPosition();
            mDuration = mPlayer.getDuration();
            mSeekBeginPosition = mPlayer.getCurrentPosition();

            if (mBrightness <= 0.00f) {
                mBrightness = 0.50f;
            }
            if (mBrightness < 0.01f) {
                mBrightness = 0.01f;
            }
            if (mVolume < 0) {
                mVolume = 0;
            }
        }

        /** 手势结束 */
        @Override
        public void onGestureEnd() {
            mOperationVolLum.setVisibility(View.GONE); // 隐藏亮度或者音量显示的控制栏
            mOperationSeekQuick.setVisibility(View.GONE); // 隐藏快进快退的控制界面
            mDragging = false; // 使进度条可以更新了
        }

        /** 左侧区域上下滑动 */
        @Override
        public void onLeftSlide(float percent) {
            // 调整亮度
            setBrightness(mBrightness + percent);
            setBrightnessScale(mContext.getWindow().getAttributes().screenBrightness);
        }

        /** 右侧区域上下滑动 */
        @Override
        public void onRightSlide(float percent) {
            // 调整音量
            int v = (int) (percent * mMaxVolume) + mVolume;
            setVolume(v);
        }

        /** 轻点屏幕单击 */
        @Override
        public void onSingleTap() {
            if (mShowing)
            // 隐藏控制栏
            {
                hide();
            } else
            // 显示控制栏
            {
                show();
            }
            if (mPlayer.getBufferPercentage() >= 100) {
                mPlayer.removeLoadingView();
            }
        }

        /** 双击屏幕 */
        @Override
        public void onDoubleTap() {

        }

        /** 手指伸缩操作 */
        @Override
        public void onScale(float scaleFactor, int state) {

        }

        /** 左右滑动 */
        @Override
        public void onSeekControl(float percent) {
            if (isSeekto && AppConfig.DEBUG) {
                mDragging = true; // 使进度条不在自动更新
                // 快进快退
                float changeNumber = percent * mDuration / 5;
                // mChangeNumber += changeNumber;
                boolean left = false;
                if (changeNumber < 0) {
                    left = true;
                } else {
                    left = false;
                }

                mCurrentPosition = mPlayer.getCurrentPosition();
                mCurrentPosition += changeNumber;
                setProgress(mDuration, mCurrentPosition);
                seekControlByPosition();
            }
        }

        /** 跳转到快进快退的停下来的点位 */
        @Override
        public void onSeekTo() {
            mPlayer.seekTo(mCurrentPosition);
        }

        @Override
        public boolean canSeek() {
            return canSeek;
        }
    };

    /**
     * 设置音量
     */
    private void setVolume(int v) {
        if (v > mMaxVolume) {
            v = mMaxVolume;
        } else if (v < 0) {
            v = 0;
        }
        mAM.setStreamVolume(AudioManager.STREAM_MUSIC, v, 0);
        setVolumeScale((float) v / mMaxVolume);
    }

    /**
     * 设置亮度
     */
    private void setBrightness(float f) {
        WindowManager.LayoutParams lp = mContext.getWindow().getAttributes();
        lp.screenBrightness = f;
        if (lp.screenBrightness > 1.0f) {
            lp.screenBrightness = 1.0f;
        } else if (lp.screenBrightness < 0.01f) {
            lp.screenBrightness = 0.01f;
        }
        mContext.getWindow().setAttributes(lp);
    }

    /**
     * 操作某些手机的轨迹球也会显示出控制栏
     */
    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        show(DEFAULT_TIME_OUT);
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 实体键监听
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_MUTE:
                // 静音键,返回系统
                return super.dispatchKeyEvent(event);
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                // 音量上下键，调用自己的音量设置
                mVolume = mAM.getStreamVolume(AudioManager.STREAM_MUSIC);
                int step = keyCode == KeyEvent.KEYCODE_VOLUME_UP ? 1 : -1;
                setVolume(mVolume + step);
                mHandler.removeMessages(MSG_HIDE_OPERATION_VOLLUM);
                mHandler.sendEmptyMessageDelayed(MSG_HIDE_OPERATION_VOLLUM, 500);
                return true;
        }

        if (event.getRepeatCount() == 0
                && (keyCode == KeyEvent.KEYCODE_HEADSETHOOK || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE ||
                keyCode == KeyEvent.KEYCODE_SPACE)) {
            // 如果点击多媒体按钮的播放键或者空格键，也将暂停视频并显示控制栏
            doPauseResume();
            show(DEFAULT_TIME_OUT);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP) {
            // 点击实体停止键暂停视频
            if (mPlayer.isPlaying()) {
                mPlayer.pause();
                updatePausePlay();
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_UP) {
                mPlayer.stop(); // 返回按钮
                return true;
            }
        } else {
            // 其它按钮一律直接显示控制栏
            show(DEFAULT_TIME_OUT);
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * 隐藏和显示系统状态栏
     */
    @TargetApi(11)
    @Override
    public void showSystemUi(boolean visible) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            int flag = visible ? View.SYSTEM_UI_FLAG_VISIBLE : View.SYSTEM_UI_FLAG_FULLSCREEN;
//          setSystemUiVisibility(flag);
            Loger.d(TAG, "showSystemUi:visible=" + visible);
        }
    }

    /**
     * 切换播放和暂停的样式
     */
    private void updatePausePlay() {
        if (controllerBottom != null) {
            controllerBottom.updatePausePlay(mPlayer.isPlaying());
        }
    }

    /**
     * 暂停或者播放
     */
    private void doPauseResume() {
        doPauseResume(!mPlayer.isPlaying());
    }

    /**
     * 播放进度栏SeekBar操作监听
     */
    private SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
        /** 是否已播放停止 */
        private boolean wasStopped = false;

        /** 开始手动进度条 */
        @Override
        public void onStartTrackingTouch(SeekBar bar) {
            mDragging = true;
            // 长时间显示控制栏
            show(3600000);
            mHandler.removeMessages(MSG_SHOW_PROGRESS);
            wasStopped = !mPlayer.isPlaying();
            if (mInstantSeeking) {
                // 实时跳转，手动中不停的跳转
                mAM.setStreamMute(AudioManager.STREAM_MUSIC, true);
                if (wasStopped) {
                    mPlayer.start();
                }
            }
        }

        /** 进度条进度改变 */
        @Override
        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
            if (!fromuser) {
                return;
            }
            long newposition = (mDuration * progress) / 1000;
            String time = TimeUtils.generateTime(newposition);
            if (mInstantSeeking) {
                mPlayer.seekTo(newposition);
            }
            mCurrentPosition = newposition;
            seekControlByPosition();
            // setOperationInfo(time, 1500);
        }

        /** 停止手动 */
        @Override
        public void onStopTrackingTouch(SeekBar bar) {
            if (!mInstantSeeking) {
                mPlayer.seekTo((mDuration * bar.getProgress()) / 1000);
            } else if (wasStopped) {
                mPlayer.pause();
            }
            mTouchListener.onGestureEnd();
            show(DEFAULT_TIME_OUT);
            mHandler.removeMessages(MSG_SHOW_PROGRESS);
            mAM.setStreamMute(AudioManager.STREAM_MUSIC, false);
            mDragging = false;
            mHandler.sendEmptyMessageDelayed(MSG_SHOW_PROGRESS, 1000);
        }
    };

    /**
     * 设置横竖屏切换按钮是否显示
     */
    public void setAutoOrientation(boolean autoOrientation) {
        if (controllerBottom != null) {
            controllerBottom.setAutoOrientation(autoOrientation);
        }
    }

    /**
     * 设置播放下一个是否显示
     */
    public void setPlayNextVisable(boolean playNextVisable) {
        if (controllerBottom != null) {
            controllerBottom.setPlayNextVisable(playNextVisable);
        }
    }

    public void setVideoStatus(int code, int status, String values) {
        if (controllerBottom != null) {
            controllerBottom.setVideoStatus(code, status, values);
        }
    }

    @Override
    public void setSetSpeedVisable(boolean setSpeedVisable) {
        controllerBottom.setSetSpeedVisable(setSpeedVisable);
    }

    @Override
    public void onStartTrackingTouch(SeekBar bar) {
        mSeekListener.onStartTrackingTouch(bar);
    }

    @Override
    public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
        mSeekListener.onProgressChanged(bar, progress, fromuser);
    }

    @Override
    public void onStopTrackingTouch(SeekBar bar) {
        mSeekListener.onStopTrackingTouch(bar);
    }

    /**
     * 控制栏的定位方式
     */
    public int getmAnchorLocation() {
        return mAnchorLocation;
    }

    /**
     * 控制栏的定位方式
     */
    public void setmAnchorLocation(int mAnchorLocation) {
        this.mAnchorLocation = mAnchorLocation;
    }

    /**
     * 控制栏在竖屏时是否拉伸全屏高度
     */
    public void setDrawHeight(boolean drawHeight) {
        isDrawHeight = drawHeight;
    }
}
