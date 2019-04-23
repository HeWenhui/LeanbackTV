package com.xueersi.parentsmeeting.modules.livevideo.activity.item;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.framework.utils.NetWorkHelper;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.parentsmeeting.module.audio.safeaudioplayer.AudioPlayerManager;
import com.xueersi.parentsmeeting.module.audio.safeaudioplayer.PlayerCallback;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.RolePlayerBll;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RolePlayerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.RolePlayMachinePager;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.RolePlayLog;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.CountDownHeadImageView;

public class RolePlayerStandMachineOtherItem extends RolePlayerItem {
    private final LiveAndBackDebug mLiveBll;//只为记录日志用
    /**
     * 头像
     */
    private CountDownHeadImageView civUserHead;

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
     * 音频播放管理类
     */
    private AudioPlayerManager mAudioPlayerManager;

    /**
     * 标记当前语音正在播放,true 表示正在播放； flase 表示已经停止播放
     */
    private boolean mIsPlaying = false;
    Handler mReadHandler ;
    private int mPostion;

    public RolePlayerStandMachineOtherItem(Context context, RolePlayerBll rolePlayerBll, Handler readHandler) {
        super(context, rolePlayerBll);
        mLiveBll = ProxUtil.getProxUtil().get(context, LiveAndBackDebug.class);
        mReadHandler = readHandler;
    }


    @Override
    public int getLayoutResId() {
        return R.layout.item_live_roleplayer_stand_other_voice;
    }

    @Override
    public void initViews(View root) {
        civUserHead = root.findViewById(R.id.civ_live_roleplayer_message_user_head);
        tvUserNickName = root.findViewById(R.id.tv_live_roleplayer_message_username);
        ivVoiceAnimtor = root.findViewById(R.id.iv_live_roleplayer_message_voice_main);
        vVoiceMain = root.findViewById(R.id.rl_live_roleplayer_message_voice_main);
        tvMessageContent = root.findViewById(R.id.tv_live_roleplayer_message_voice_content);
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
                    logger.i("点击他人语音：url = " + mEntity.getWebVoiceUrl());

                    voiceClick();
                } else {
                    XESToastUtils.showToast(mContext, "没有检测到音频文件");
                    if (mEntity != null) {
                        logger.i("点击他人语音：url = " + mEntity.getWebVoiceUrl() + " NetWorkHelper" +
                                ".isNetworkAvailable(mContext) = " + NetWorkHelper.isNetworkAvailable(mContext));
                    }

                }
            }
        });
    }


    private void voiceClick() {
        //点击语音的时候记录日志
        logger.i( " 点击播放音频，记录日志 ");

        RolePlayLog.sno8(mLiveBll, mEntity, mContext);
        playAudio(false);
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
                logger.i("先停掉正在播放的音频，优先播放现在的");
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
                if(isRolePlay){
                    logger.i( "机器播完出错:msg = "+msg+" dataSource = "+dataSource);
                    nextMsg();
                }
                mIsPlaying = false;
                recoverMsgUiStatus();
                //mAudioPlayerManager = null;
            }
        });
    }

    private void sendCurItemIndex() {
        if(mReadHandler != null){
            Message message = new Message();
            message.what = RolePlayerEntity.RolePlayerMessageStatus.CUR_PLAYING_ITEM_INDEX;
            message.obj = mPostion;
            mReadHandler.sendMessage(message);
        }
    }
    /**
     * 开始播放机器音频，改变对话样式
     */
    private void startPlayMachineAudio() {
        vVoiceMain.setBackgroundResource(R.drawable.livevideo_roleplay_stand_bubble_other_reading);
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
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    ivVoiceAnimtor.setBackgroundResource(R.drawable.yuyin_you_huifang_3_lan);
                    vVoiceMain.setBackgroundResource(R.drawable.selector_live_roleplayer_other_item_bubble);
                    tvMessageContent.setTextColor(Color.parseColor("#4E5BC1"));
                }
            });
        }else {
            ivVoiceAnimtor.setBackgroundResource(R.drawable.yuyin_you_huifang_3_lan);
            vVoiceMain.setBackgroundResource(R.drawable.selector_live_roleplayer_other_item_bubble);
            tvMessageContent.setTextColor(Color.parseColor("#4E5BC1"));
        }


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

    @Override
    public void updateViews(RolePlayerEntity.RolePlayerMessage entity, int position, Object objTag) {
        super.updateViews(entity, position, objTag);
        logger.i( "updateViews entity = " + entity.getWebVoiceUrl());
        mPostion = position;
        mEntity = entity;

        updateUserHeadImage(civUserHead, entity.getRolePlayer().getHeadImg()); // 绑定用户头像
        civUserHead.setBorderWidth(SizeUtils.Dp2Px(mContext, 0));
        civUserHead.setBorderColor(Color.WHITE);
        // 播放语音
        ivVoiceAnimtor.setBackgroundResource(R.drawable.yuyin_you_huifang_3_lan);
        tvMessageContent.setText(entity.getReadMsg());
        tvUserNickName.setText(entity.getRolePlayer().getNickName());
        tvMessageContent.setTextColor(Color.parseColor("#4E5BC1"));
        switch (entity.getMsgStatus()) {
            case RolePlayerEntity.RolePlayerMessageStatus.WAIT_NORMAL:
                //  logger.i("RolePlayerDemoTest", "等待朗读");
                mIsPlaying = true;
                vVoiceMain.setBackgroundResource(R.drawable.selector_live_roleplayer_other_item_bubble);
                ivVoiceAnimtor.setBackgroundResource(R.drawable.yuyin_you_huifang_3_lan);

                break;
            case RolePlayerEntity.RolePlayerMessageStatus.BEGIN_ROLEPLAY:
                // logger.i("RolePlayerDemoTest", "开始朗读");
                mIsPlaying = true;
                vVoiceMain.setBackgroundResource(R.drawable.livevideo_roleplay_stand_bubble_other_reading);
                tvMessageContent.setTextColor(Color.WHITE);
                ivVoiceAnimtor.setBackgroundResource(R.drawable.animlst_livevideo_roleplayer_other_voice_white_anim);
                //正在roleplay中
                playAudio(true);

                break;
            case RolePlayerEntity.RolePlayerMessageStatus.END_ROLEPLAY:
                logger.i( "结束roleplay朗读");
                vVoiceMain.setBackgroundResource(R.drawable.selector_live_roleplayer_other_item_bubble);
                ivVoiceAnimtor.setBackgroundResource(R.drawable.yuyin_you_huifang_3_lan);

                showSpeechStar();
                if(mAudioPlayerManager != null){
                    mAudioPlayerManager.stop();
                    mAudioPlayerManager.release();
                    mAudioPlayerManager = null;
                    logger.i( "END_ROLEPLAY，停止机器播放");
                }

                break;

            case RolePlayerEntity.RolePlayerMessageStatus.CANCEL_DZ:
                mIsPlaying = false;
                // logger.i("RolePlayerDemoTest", "取消点赞按钮");
                vVoiceMain.setBackgroundResource(R.drawable.selector_live_roleplayer_other_item_bubble);
                ivVoiceAnimtor.setBackgroundResource(R.drawable.yuyin_you_huifang_3_lan);
                tvMessageContent.setTextColor(Color.parseColor("#4E5BC1"));
                showSpeechStar();
                logger.i("CANCEL_DZ:显示星星");
                break;
            default:
                break;
        }
    }

    /**
     * 停止当前音频的播放，并释放
     */
    public void relaseAudioPlay() {

        if(mAudioPlayerManager != null){
            mAudioPlayerManager.stop();
            mAudioPlayerManager.release();
        }
        mReadHandler = null;
    }

    @Override
    protected void showSpeechStar() {
        //super.showSpeechStar();
    }
}