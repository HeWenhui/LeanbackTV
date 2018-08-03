package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.videoplayer.media.ControllerBottomInter;
import com.xueersi.parentsmeeting.modules.videoplayer.media.LiveMediaController;
import com.xueersi.parentsmeeting.modules.videoplayer.media.LiveMediaController.MediaPlayerControl;
import com.xueersi.xesalib.utils.app.XESToastUtils;

/**
 * 直播播放器控制栏底部区域
 */
public class BaseLiveMediaControllerBottom extends FrameLayout implements ControllerBottomInter {
    String TAG = "MediaControllerBottom";
    /** 播放器的控制监听 */
    protected MediaPlayerControl mPlayer;
    protected LiveMediaController controller;
    protected Context mContext;
    /** 顶部动画向下出现 */
    private Animation mAnimSlideInTop;
    /** 顶部动画向上隐藏 */
    private Animation mAnimSlideOutTop;
    /** 聊天，默认开启 */
    private Button btMesOpen;
    /** 聊天常用语 */
    private Button btMsgCommon;
    private ListView lvCommonWord;
    /** 献花，默认关闭 */
    private Button btMessageFlowers;
    /** 聊天，默认打开 */
    private CheckBox cbMessageClock;
    /** 标记疑问点按钮 */
    public Button btMark;

    public BaseLiveMediaControllerBottom(Context context, LiveMediaController controller, MediaPlayerControl player) {
        super(context);
        mContext = context;
        mPlayer = player;
        this.controller = controller;
        initResources();
    }

    protected void initResources() {
        inflateLayout();
        findViewItems();
        mAnimSlideInTop = AnimationUtils.loadAnimation(mContext, R.anim.anim_mediactrl_slide_in_top);
        mAnimSlideOutTop = AnimationUtils.loadAnimation(mContext, R.anim.anim_mediactrl_slide_out_top);
        mAnimSlideInTop.setFillAfter(true);
        mAnimSlideOutTop.setFillAfter(true);
    }

    /** 播放器的布局界面 */
    public View inflateLayout() {

        return LayoutInflater.from(mContext).inflate(R.layout.layout_livemediacontroller_bottom, this);

    }

    /** 初始化控制界面上的控制部件 */
    protected void findViewItems() {
        btMesOpen = (Button) findViewById(R.id.bt_livevideo_message_open);
        btMsgCommon = (Button) findViewById(R.id.bt_livevideo_message_common);
        btMessageFlowers = (Button) findViewById(R.id.bt_livevideo_message_flowers);
        cbMessageClock = (CheckBox) findViewById(R.id.cb_livevideo_message_clock);
        lvCommonWord = (ListView) findViewById(R.id.lv_livevideo_common_word);
        btMark = (Button) findViewById(R.id.bt_livevideo_mark);
        if (btMark != null) {
            btMark.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    XESToastUtils.showToast(mContext, "正在加载视频");
                }
            });
        }
    }

    public Button getBtMesOpen() {
        return btMesOpen;
    }

    public Button getBtMsgCommon() {
        return btMsgCommon;
    }

    public ListView getLvCommonWord() {
        return lvCommonWord;
    }

    public Button getBtMessageFlowers() {
        return btMessageFlowers;
    }

    public CheckBox getCbMessageClock() {
        return cbMessageClock;
    }

    public Button getBtMark() {
        return btMark;
    }

    @Override
    public void setProgress(long duration, long position) {

    }

    /** 设置横竖屏切换按钮是否显示 */
    @Override
    public void setAutoOrientation(boolean autoOrientation) {

    }

    @Override
    public void setPlayNextVisable(boolean playNextVisable) {

    }

    @Override
    public void setSetSpeedVisable(boolean setSpeedVisable) {

    }

    /** 切换播放和暂停的样式 */
    @Override
    public void updatePausePlay(boolean isPlaying) {

    }

    @Override
    public void onShow() {
        setVisibility(View.VISIBLE);
        startAnimation(mAnimSlideInTop);
    }

    @Override
    public void onHide() {
        startAnimation(mAnimSlideOutTop);
    }

    public void setController(LiveMediaController controller) {
        this.controller = controller;
    }

    public void onChildViewClick(View child) {
        if (mPlayer instanceof MediaChildViewClick) {
            MediaChildViewClick liveVideoActivity = (MediaChildViewClick) mPlayer;
            liveVideoActivity.onMediaViewClick(child);
        }
    }

    public interface MediaChildViewClick {
        void onMediaViewClick(View child);
    }

    public LiveMediaController getController() {
        return controller;
    }
}
