package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.module.videoplayer.media.ControllerBottomInter;
import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController;
import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController.MediaPlayerControl;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

import java.util.ArrayList;


/**
 * 直播播放器控制栏底部区域
 */
public class BaseLiveMediaControllerBottom extends FrameLayout implements ControllerBottomInter {
    String TAG = "MediaControllerBottom";
    protected Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
    /** 播放器的控制监听 */
    protected MediaPlayerControl mPlayer;
    protected LiveMediaController controller;
    protected Context mContext;
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
    ArrayList<MediaChildViewClick> mediaChildViewClicks = new ArrayList<>();
    private LinearLayout llMarkPopMenu;
    private View vMarkGuide;
    private AnimatorSet mAnimatorsetIn;
    private AnimatorSet mAnimatorSetOut;

    public BaseLiveMediaControllerBottom(Context context, LiveMediaController controller, MediaPlayerControl player) {
        super(context);
        mContext = context;
        mPlayer = player;
        this.controller = controller;
        initResources();
        ProxUtil.getProxUtil().put(context, RegMediaChildViewClick.class, new RegMediaChildViewClick() {
            @Override
            public void regMediaViewClick(MediaChildViewClick mediaChildViewClick) {
                mediaChildViewClicks.add(mediaChildViewClick);
            }

            @Override
            public void remMediaViewClick(MediaChildViewClick mediaChildViewClick) {
                mediaChildViewClicks.remove(mediaChildViewClick);
            }
        });
    }

    protected void initResources() {
        inflateLayout();
        findViewItems();

        int screenHight = ScreenUtils.getScreenHeight();
        //防止 getScreenHight 失败 导致UI 显示异常
        screenHight = screenHight > 0?screenHight:1000;
        ObjectAnimator  transIn = ObjectAnimator.ofFloat(this,"translationY",screenHight,0);
        ObjectAnimator  alphaIn =  ObjectAnimator.ofFloat(this,"alpha",0.0f,1.0f);
        mAnimatorsetIn = new AnimatorSet();
        mAnimatorsetIn.setDuration(300);
        mAnimatorsetIn.playTogether(transIn,alphaIn);


        ObjectAnimator  transOut = ObjectAnimator.ofFloat(this,"translationY",0,screenHight);
        ObjectAnimator  alphaOut =  ObjectAnimator.ofFloat(this,"alpha",1.0f,0.0f);
        mAnimatorSetOut = new AnimatorSet();
        mAnimatorSetOut.setDuration(300);
        mAnimatorSetOut.playTogether(transOut,alphaOut);

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
        llMarkPopMenu=findViewById(R.id.ll_livevideo_controller_mark_pop_menu);
        vMarkGuide=findViewById(R.id.ll_livevideo_bottom_controller_mark_guide);
        if (btMark != null) {
            btMark.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    XESToastUtils.showToast(mContext, "正在加载视频");
                }
            });
        }
    }
    public LinearLayout getLlMarkPopMenu(){
        return llMarkPopMenu;
    }
    public View getvMarkGuide(){
        return vMarkGuide;
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
        mAnimatorsetIn.start();
    }

    @Override
    public void onHide() {
         mAnimatorSetOut.start();
        if(llMarkPopMenu!=null){
            llMarkPopMenu.setVisibility(GONE);
        }

    }

    public void setController(LiveMediaController controller) {
        this.controller = controller;
    }

    public void onChildViewClick(View child) {
        for (MediaChildViewClick childViewClick : mediaChildViewClicks) {
            childViewClick.onMediaViewClick(child);
        }
    }

    public interface RegMediaChildViewClick {
        void regMediaViewClick(MediaChildViewClick mediaChildViewClick);

        void remMediaViewClick(MediaChildViewClick mediaChildViewClick);
    }

    public interface MediaChildViewClick {
        void onMediaViewClick(View child);
    }

    public LiveMediaController getController() {
        return controller;
    }
}
