package com.xueersi.parentsmeeting.modules.livevideo.activity.item;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.framework.utils.NetWorkHelper;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.parentsmeeting.module.audio.safeaudioplayer.AudioPlayerManager;
import com.xueersi.parentsmeeting.module.audio.safeaudioplayer.PlayerCallback;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.utils.BetterMeUtil;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.RolePlayerBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.RolePlayerBllThree;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RolePlayerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.RolePlayMachinePager;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.RolePlayLog;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveMainHandler;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.CountDownHeadImageView;


/**
 * 语音类接收的消息
 *
 * @author ZouHao
 */
public class RolePlayerOtherItemThree extends RolePlayerItemThree {


    String TAG = "VoiceComeItem";

    /**
     * 头像
     */
    private CountDownHeadImageView civUserHead;

    /**
     * 段位
     */
    private ImageView ivUserSegment;

    /**
     * 用户的昵称
     */
    private TextView tvUserNickName;

    /**
     * 表示语音播放状态的背景动画
     */
    private ImageView ivVoiceAnimtor;

    /**
     * 语音主体界面
     */
    private View vVoiceMain;

    /**
     * 语音的内容
     */
    private TextView tvMessageContent;

    /**
     * 点赞布局
     */
    private RelativeLayout rlMessageDZ;
    /**
     * 点赞默认图
     */
    private ImageView ivMessageDZ;
    /**
     * 点赞动图
     */
    private AudioPlayerManager mAudioPlayerManager;//音频播放管理类
    private final LiveAndBackDebug mLiveBll;//只为记录日志用
    private boolean mIsPlaying = false;//标记当前语音正在播放,true 表示正在播放； flase 表示已经停止播放

    private final Handler mReadHandler;
    private int mPosition;
    private  boolean isRolePlay;//true 人机 false 多人

    public RolePlayerOtherItemThree(Context context, RolePlayerBllThree bll, Handler handler, boolean isRolePlay) {
        super(context, bll);
        mReadHandler = handler;
        mLiveBll = ProxUtil.getProxUtil().get(context, LiveAndBackDebug.class);
        this.isRolePlay = isRolePlay;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_live_roleplayer_other_voice;
    }

    @Override
    public void initViews(View root) {
        civUserHead = root.findViewById(R.id.civ_live_roleplayer_message_user_head);
        ivUserSegment = root.findViewById(R.id.iv_live_roleplayer_message_user_segment);
        tvUserNickName = root.findViewById(R.id.tv_live_roleplayer_message_username);
        ivVoiceAnimtor = root.findViewById(R.id.iv_live_roleplayer_message_voice_main);
        vVoiceMain = root.findViewById(R.id.rl_live_roleplayer_message_voice_main);
        tvMessageContent = root.findViewById(R.id.tv_live_roleplayer_message_voice_content);
        rlMessageDZ = root.findViewById(R.id.rl_live_roleplayer_message_dz);
        ivMessageDZ = root.findViewById(R.id.iv_live_roleplayer_message_dz);
        //lavMessageDZ = root.findViewById(R.id.lav_live_roleplayer_message_dz);
        initStartView(root);
    }

    @Override
    public void bindListener() {
        // 单击语音播放
        vVoiceMain.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (mEntity == null) {
                    logger.i( "数据为空");
                    return;
                }
                if(mEntity.getMsgStatus() != RolePlayerEntity.RolePlayerMessageStatus.CANCEL_DZ){
                    logger.i("roleplay还未结束，不可点击对话");
                    return;
                }
                if (mIsPlaying) {
                    logger.i( "语音正在播放中，请不要重复点击");
                    return;
                }
                if (!TextUtils.isEmpty(mEntity.getWebVoiceUrl()) && NetWorkHelper.isNetworkAvailable(mContext)) {
                    //只有当这个URL不为空时且有网才可以点击播放
                    logger.i( "点击他人语音：url = " + mEntity.getWebVoiceUrl());

                    voiceClick();
                } else {
                    XESToastUtils.showToast(mContext, "没有检测到音频文件");
                    if (mEntity != null) {
                        logger.i( "点击他人语音：url = " + mEntity.getWebVoiceUrl() + " NetWorkHelper" +
                                ".isNetworkAvailable(mContext) = " + NetWorkHelper.isNetworkAvailable(mContext));
                    }

                }
            }
        });
        //vVoiceMain.setOnTouchListener(new OnAlphaTouchListener());
    }

    private void voiceClick() {
        //点击语音的时候记录日志
        logger.i( " 点击播放音频，记录日志 ");
        RolePlayLog.sno8(mLiveBll, mEntity, mContext);
        playAudio(isRolePlay);


    }
    /**
     * 播放音频
     * @param isRolePlay roleplay正在进行
     */
    private synchronized void playAudio(final boolean isRolePlay) {
        logger.i( "开始播放音频");
        startPlayMachineAudio();

        if(isRolePlay){
            //防止两个连在一起的机器音频播放的时候，由于同步的问题，导致，播放出错，音频和播放动画不同步的问题
            if(mAudioPlayerManager != null){
                logger.i("有正在播放的音频，不执行");
                return;
            }
        }else {
            //通过点击对话播放的
            if(mAudioPlayerManager != null){
                logger.i("先停掉f正在播放的音频，优先播放现在的");
                mAudioPlayerManager.stop();
                mAudioPlayerManager.release();
                mAudioPlayerManager = null;
            }


        }
        sendCurItemIndex();
        mAudioPlayerManager = AudioPlayerManager.get(ContextManager.getApplication());
        //播放
        mAudioPlayerManager.start(mEntity.getWebVoiceUrl(), new PlayerCallback() {
            @Override
            public void onCompletion(Object o, AudioPlayerManager audioPlayerManager) {
                logger.i( "完成播放");
                mAudioPlayerManager.release();
                mAudioPlayerManager = null;
                if(isRolePlay){
                    logger.i("机器播完，开启下一条");
                    nextMsg();
                }
                mIsPlaying = false;
                recoverMsgUiStatus();
            }

            @Override
            public void onStop(Object dataSource, AudioPlayerManager manager) {
                super.onStop(dataSource, manager);
                logger.i("停止播放");
                mIsPlaying = false;
                recoverMsgUiStatus();

            }

            @Override
            public void onPreparing(Object dataSource, AudioPlayerManager manager) {
                logger.i("准备播放");
                mIsPlaying = true;

            }

            @Override
            public void onError(String msg, Object dataSource, AudioPlayerManager manager) {
                super.onError(msg, dataSource, manager);
                XESToastUtils.showToast(mContext,"音频播放失败");
                if(isRolePlay){
                    logger.i( "机器播完出错:msg = "+msg+" dataSource = "+dataSource);
                    nextMsg();
                }
                mIsPlaying = false;
                recoverMsgUiStatus();
                mAudioPlayerManager = null;
            }
        });
    }
    /**
     * 机器播完之后，通知跳到下一条
     */
    private void nextMsg() {
        if(mReadHandler != null){
            Message temp = mReadHandler.obtainMessage();
            temp.what = RolePlayMachinePager.READ_MESSAGE;
            mReadHandler.sendMessage(temp);
        }
    }
    private void startPlayMachineAudio() {
        vVoiceMain.setBackgroundResource(R.drawable.livevideo_roleplay_bubble_other_reading);
        ivVoiceAnimtor.setBackgroundResource(R.drawable.animlst_livevideo_roleplayer_other_voice_white_anim);
        AnimationDrawable animationDrawable = null;
        animationDrawable = (AnimationDrawable) ivVoiceAnimtor.getBackground();
        if (animationDrawable != null && !animationDrawable.isRunning()) {
            animationDrawable.start();
        }
        tvMessageContent.setTextColor(Color.WHITE);
    }
    /**
     * 恢复对话的样式
     */
    private void recoverMsgUiStatus() {
        //如果是子线程的回调，会报出异常Only the original thread that created a view hierarchy can touch its views.
        if (Looper.myLooper() != Looper.getMainLooper()) {
            LiveMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    ivVoiceAnimtor.setBackgroundResource(R.drawable.yuyin_you_huifang_3);
                    vVoiceMain.setBackgroundResource(R.drawable.selector_live_roleplayer_self_item_bubble);
                    tvMessageContent.setTextColor(Color.parseColor("#333333"));
                }
            });
        }else {
            ivVoiceAnimtor.setBackgroundResource(R.drawable.yuyin_you_huifang_3);
            vVoiceMain.setBackgroundResource(R.drawable.selector_live_roleplayer_self_item_bubble);
            tvMessageContent.setTextColor(Color.parseColor("#333333"));
        }


    }
    private void sendCurItemIndex() {
        if(mReadHandler != null){
            Message message = new Message();
            message.what = RolePlayerEntity.RolePlayerMessageStatus.CUR_PLAYING_ITEM_INDEX;
            message.obj = mPosition;
            mReadHandler.sendMessage(message);
        }
    }

    @Override
    public void updateViews(final RolePlayerEntity.RolePlayerMessage entity, int position, Object objTag) {
       /* if(entity.getMsgStatus() == RolePlayerEntity.RolePlayerMessageStatus.STOP_UPDATE){
            rlMessageDZ.setVisibility(View.GONE);
            ivMessageDZ.setVisibility(View.GONE);
            Loger.i("RolePlayerDemoTest", "停止刷新");
            return;
        }*/
        super.updateViews(entity, position, objTag);
        mPosition = position;
        updateUserHeadImage(civUserHead, entity.getRolePlayer().getHeadImg()); // 绑定用户头像
        civUserHead.setBorderWidth(SizeUtils.Dp2Px(mContext, 0));

        int segmentType = (entity.getRolePlayer().getSegment_type());
        int star = entity.getRolePlayer().getStar();
        BetterMeUtil.addSegment(ivUserSegment,segmentType,star);

        // 播放语音
        ivVoiceAnimtor.setBackgroundResource(R.drawable.bg_chat_voice_from_playing_img_blue);
        tvMessageContent.setText(entity.getReadMsg());
        tvUserNickName.setText(entity.getRolePlayer().getNickName());
        tvMessageContent.setTextColor(Color.parseColor("#333333"));
        rlMessageDZ.setVisibility(View.GONE);
        setDZbtClick(entity);
        switch (entity.getMsgStatus()) {
            case RolePlayerEntity.RolePlayerMessageStatus.WAIT_NORMAL:
                //  Loger.i("RolePlayerDemoTest", "等待朗读");
                mIsPlaying = true;
                vVoiceMain.setBackgroundResource(R.drawable.selector_live_roleplayer_other_item_bubble);
                ivVoiceAnimtor.setBackgroundResource(R.drawable.yuyin_you_huifang_3);

                break;
            case RolePlayerEntity.RolePlayerMessageStatus.BEGIN_ROLEPLAY:
                // Loger.i("RolePlayerDemoTest", "开始朗读");
                mIsPlaying = true;
                rlMessageDZ.setVisibility(View.VISIBLE);
                ivMessageDZ.setVisibility(View.VISIBLE);
                ivMessageDZ.setImageResource(R.drawable.livevideo_roleplay_result_ic_normal);
                vVoiceMain.setBackgroundResource(R.drawable.livevideo_roleplay_bubble_other_reading);
                tvMessageContent.setTextColor(Color.WHITE);
                ivVoiceAnimtor.setBackgroundResource(R.drawable.animlst_livevideo_roleplayer_other_voice_white_anim);
                AnimationDrawable animationDrawable = null;
                animationDrawable = (AnimationDrawable) ivVoiceAnimtor.getBackground();
                if (animationDrawable != null && !animationDrawable.isRunning()) {
                    animationDrawable.start();
                }
                playAudio(isRolePlay);
                break;
            case RolePlayerEntity.RolePlayerMessageStatus.END_ROLEPLAY:
                // Loger.i("RolePlayerDemoTest", "结束朗读");
                vVoiceMain.setBackgroundResource(R.drawable.selector_live_roleplayer_other_item_bubble);
                ivVoiceAnimtor.setBackgroundResource(R.drawable.yuyin_you_huifang_3);
                rlMessageDZ.setVisibility(View.VISIBLE);
                ivMessageDZ.setVisibility(View.VISIBLE);
                if (!entity.isDZ()) {
                    ivMessageDZ.setImageResource(R.drawable.livevideo_roleplay_result_ic_normal);

                } else {
                    ivMessageDZ.setImageResource(R.drawable.livevideo_roleplay_result_ic_focsed);
                    ivMessageDZ.setOnClickListener(null);
                }
                showSpeechStar();
                logger.i( "END_ROLEPLAY:显示星星");
                break;

            case RolePlayerEntity.RolePlayerMessageStatus.CANCEL_DZ:
                mIsPlaying = false;
                // Loger.i("RolePlayerDemoTest", "取消点赞按钮");
                ivMessageDZ.setImageResource(R.drawable.livevideo_roleplay_result_ic_normal);
                rlMessageDZ.setVisibility(View.GONE);
                ivMessageDZ.setVisibility(View.GONE);
                vVoiceMain.setBackgroundResource(R.drawable.selector_live_roleplayer_other_item_bubble);
                ivVoiceAnimtor.setBackgroundResource(R.drawable.yuyin_you_huifang_3);
                tvMessageContent.setTextColor(Color.parseColor("#333333"));
                showSpeechStar();
                logger.i( "CANCEL_DZ:显示星星");
                break;
            default:
                break;
        }


    }

    /**
     * 点赞按钮的点击事件
     *
     * @param entity
     */
    private void setDZbtClick(final RolePlayerEntity.RolePlayerMessage entity) {

        ivMessageDZ.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (entity.isDZ()) {
                    logger.i( "已经点过赞了。。。");
                    return;
                }
                logger.i( "给他人点赞");

                bllRolePlayerBll.toOtherDZ(mEntity.getRolePlayer().getRoleId(), mEntity.getPosition());
                entity.setDZ(true);

                //x轴缩放动画
                ObjectAnimator startYScale = ObjectAnimator.ofFloat(ivMessageDZ, ImageView.SCALE_Y, 1.0f, 1.5f, 1.0f);
                startYScale.setDuration(500);
                startYScale.start();
                //y轴缩放动画
                ObjectAnimator startXScale = ObjectAnimator.ofFloat(ivMessageDZ, ImageView.SCALE_X, 1.0f, 1.5f, 1.0f);
                startXScale.setDuration(500);
                startXScale.start();
                //旋转动画
                ObjectAnimator rotate = ObjectAnimator.ofFloat(ivMessageDZ, "rotation", 0, 10, 0, -10, 0, 5, 10, 0);
                rotate.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {

                    }

                });
                rotate.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ivMessageDZ.setImageResource(R.drawable.livevideo_roleplay_result_ic_focsed);

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        ivMessageDZ.setImageResource(R.drawable.livevideo_roleplay_result_ic_focsed);
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                rotate.setDuration(500);
                rotate.start();

            }
        });
    }

    public void stopVoicePlay() {
        mIsPlaying = false;
        if (mAudioPlayerManager != null) {
            mAudioPlayerManager.releaseEveryThing();
            logger.i( "roleplay已结束，停止播放他人音频");
        }
    }



    public void relaseAudioPlay() {

        if(mAudioPlayerManager != null){
            mAudioPlayerManager.stop();
            mAudioPlayerManager.release();
        }
    }
}
