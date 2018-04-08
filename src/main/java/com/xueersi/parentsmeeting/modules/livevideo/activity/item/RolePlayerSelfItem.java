package com.xueersi.parentsmeeting.modules.livevideo.activity.item;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RolePlayerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.widget.CountDownHeadImageView;
import com.xueersi.parentsmeeting.modules.loginregisters.business.UserBll;
import com.xueersi.xesalib.utils.listener.OnAlphaTouchListener;
import com.xueersi.xesalib.utils.uikit.SizeUtils;


/**
 * Roleplayer 自己扮演的角色
 *
 * @author ZouHao
 */
public class RolePlayerSelfItem extends RolePlayerItem {
    /** 头像 */
    private CountDownHeadImageView civUserHead;

    /** 用户的昵称 */
    private TextView tvUserNickName;

    /** 表示语音播放状态的背景动画 */
    private ImageView ivVoiceAnimtor;

    /** 语音主体界面 */
    private View vVoiceMain;

    /** 语音的内容 */
    private TextView tvMessageContent;

    private TextView tvCountTime;

    public RolePlayerSelfItem(Context context) {
        super(context);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_live_roleplayer_self_voice;
    }

    @Override
    public void initViews(View root) {
        civUserHead = root.findViewById(R.id.civ_live_roleplayer_message_user_head);
        tvUserNickName = root.findViewById(R.id.tv_live_roleplayer_message_username);
        ivVoiceAnimtor = root.findViewById(R.id.iv_live_roleplayer_message_voice_main);
        vVoiceMain = root.findViewById(R.id.rl_live_roleplayer_message_voice_main);
        tvMessageContent = root.findViewById(R.id.tv_live_roleplayer_message_voice_content);
        tvCountTime = root.findViewById(R.id.tv_live_roleplayer_message_counttime);
    }

    @Override
    public void bindListener() {

        // 单击语音播放
        vVoiceMain.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                voiceClick();
            }
        });
        vVoiceMain.setOnTouchListener(new OnAlphaTouchListener());
    }

    private void voiceClick() {
//        if (mEntity.isVoiceIsplay()) {
//            AudioPlayerManager.get(ContextManager.getApplication()).stop();
//            AudioPlayerManager.get(ContextManager.getApplication()).setDataSource("");
//            mEntity.setVoiceIsplay(false);
//            return;
//        }
//        if (TextUtils.isEmpty(mEntity.getLocalResourceUrl())
//                || !new File(mEntity.getLocalResourceUrl()).exists()) {
//            ivVoiceAnimtor.setBackgroundResource(R.drawable.bg_chat_voice_to_playing_img);
//            XESToastUtils.showToast(mContext, "语音正在下载中" + (TextUtils.isEmpty(mEntity.getLocalResourceUrl
//                    ()) ? "" : mEntity.getLocalResourceUrl()));
//        } else {
//
//            ivVoiceAnimtor.setImageResource(R.drawable.bg_chat_voice_to_download_img);
//            ivVoiceAnimtor.setBackgroundResource(R.drawable.animlst_homework_voice_right_anim);
//            final AnimationDrawable selfVoiceAnimationDrawable = (AnimationDrawable) ivVoiceAnimtor.getBackground();
//            selfVoiceAnimationDrawable.start();
//            mEntity.setVoiceIsplay(true);
//            AudioPlayerManager.get(ContextManager.getApplication()).start(mEntity.getLocalResourceUrl(),
//                    new PlayerCallback() {
//                        @Override
//                        public void onCompletion(Object dataSource, AudioPlayerManager manager) {
//                            mEntity.setVoiceIsplay(false);
//                            selfVoiceAnimationDrawable.stop();
//                            selfVoiceAnimationDrawable.selectDrawable(0);
//                            updateViews(mEntity, mPosition, null);
//                            if (manager.getState() != AudioPlayerManager.State.error) {
//                                handler.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        if (mLstMessageData.size() > mPosition + 1) {
//                                            MessageEntity messageEntity = mLstMessageData.get(mPosition + 1);
//                                            int type = (int) messageEntity.getDataType(AppBll.getInstance().getAppInfoEntity
//                                                    ().getChildName());
//                                            if (type == MessageEntity.MessageViewType.SELF_VOICE) {
//                                                if (messageEntity.voiceSelfItem != null) {
//                                                    messageEntity.voiceSelfItem.voiceClick();
//                                                    Loger.i(TAG, "playComplete:(equals)nextItem=" + (messageEntity
//                                                            .voiceSelfItem.mEntity == messageEntity));
//                                                } else {
//                                                    Loger.i(TAG, "playComplete:(equals)nextItem.voiceSelfItem=null");
//                                                }
//                                            } else if (type == MessageEntity.MessageViewType.COME_VOICE) {
//
//                                            } else {
//                                                Loger.i(TAG, "playComplete:(equals)nextItem.type=" + type);
//                                            }
//                                        } else {
//                                            Loger.i(TAG, "playComplete:(equals)last");
//                                        }
//                                    }
//                                });
//                            }
//                        }
//
//                        @Override
//                        public void onStop(Object dataSource, AudioPlayerManager manager) {
//                            super.onStop(dataSource, manager);
//                            mEntity.setVoiceIsplay(false);
//                            selfVoiceAnimationDrawable.stop();
//                            selfVoiceAnimationDrawable.selectDrawable(0);
//                            updateViews(mEntity, mPosition, null);
//                        }
//
//                        @Override
//                        public void onError(String msg, Object dataSource, AudioPlayerManager manager) {
//                            super.onError(msg, dataSource, manager);
//                            if (msg != null) {
//                                IMLoger.info(mContext, "语音播放错误：" + msg + "  语音地址：" + mEntity.getSmallResourceUrl());
//                            }
//                        }
//                    }
//            );
    }

    @Override
    public void updateViews(final RolePlayerEntity.RolePlayerMessage entity, int position, Object objTag) {
        super.updateViews(entity, position, objTag);
        updateUserHeadImage(civUserHead, UserBll.getInstance().getMyUserInfoEntity()
                .getHeadImg());
        tvMessageContent.setText(entity.getReadMsg());
        tvUserNickName.setText(entity.getRolePlayer().getNickName());


        switch (entity.getMsgStatus()) {
            case RolePlayerEntity.RolePlayerMessageStatus.WAIT_NORMAL:
                ivVoiceAnimtor.setBackgroundResource(R.drawable.bg_chat_voice_to_playing_img);
                tvCountTime.setVisibility(View.INVISIBLE);
                break;
            case RolePlayerEntity.RolePlayerMessageStatus.BEGIN_ROLEPLAY:
                ivVoiceAnimtor.setBackgroundResource(R.drawable.animlst_homework_voice_right_anim);
                AnimationDrawable selfVoiceAnimationDrawable = null;
                selfVoiceAnimationDrawable = (AnimationDrawable) ivVoiceAnimtor.getBackground();
                if (selfVoiceAnimationDrawable != null && !selfVoiceAnimationDrawable.isRunning()) {
                    selfVoiceAnimationDrawable.start();
                }
                tvCountTime.setText(entity.getMaxReadTime() + "");
                civUserHead.setFinishBorderColor(Color.parseColor("#C8E7D4"));
                civUserHead.setUnFinishBorderColor(Color.parseColor("#36BC9B"));
                if (entity.getMaxReadTime() <= 3) {
                    tvCountTime.setVisibility(View.VISIBLE);
                }
                civUserHead.startCountDown(entity.getMaxReadTime() * 1000, entity.getEndReadTime() * 1000, new CountDownHeadImageView.countDownTimeImpl() {
                    @Override
                    public void countTime(long time) {
                        tvCountTime.setText(time + "");
                        if (time <= 3) {
                            tvCountTime.setVisibility(View.VISIBLE);
                        }
                        entity.setEndReadTime((int) time);

                    }
                });


                break;
            case RolePlayerEntity.RolePlayerMessageStatus.END_ROLEPLAY:
                ivVoiceAnimtor.setBackgroundResource(R.drawable.bg_chat_voice_to_playing_img);
                civUserHead.invalidate();
                tvCountTime.setText("");
                tvCountTime.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }

//        // 初始化状态
//        probarSending.setVisibility(View.GONE);
//        ivSendMessageError.setVisibility(View.GONE);
//
//        if (entity.getLocalResourceUrl() == null) {
//            // 如果本地的资源路径为空时，表示未从网络下载过，开始下载
//            EventBus.getDefault().post(new ChatEvent.ChatMessageVoiceDownload(entity, probarSending));
//        } else {
//            probarSending.setVisibility(View.GONE);
//        }
//
//        switch (entity.getSendType()) {
//            case SendType.SUCCESS:
//                break;
//            case SendType.LOADING:
//                // 正在发送消息中的状态
//                probarSending.setVisibility(View.VISIBLE);
//                break;
//            case SendType.FAILED:
//                // 消息发送失败
//                ivSendMessageError.setVisibility(View.VISIBLE);
//                break;
//        }
//
//        tvVoiceDuration.setText(entity.getMessageContent() + "\"");
//        setVoiceWidthStyle();
//        ivVoiceAnimtor.setBackgroundResource(R.drawable.bg_chat_voice_to_playing_img);
//        AnimationDrawable selfVoiceAnimationDrawable = null;
//        if (entity.isVoiceIsplay() && AudioPlayer.mVoiceUrl != null && AudioPlayer.mVoiceUrl.equals(entity
//                .getLocalResourceUrl())) {
//            ivVoiceAnimtor.setBackgroundResource(R.drawable.animlst_homework_voice_right_anim);
//            selfVoiceAnimationDrawable = (AnimationDrawable) ivVoiceAnimtor.getBackground();
//            if (selfVoiceAnimationDrawable != null && !selfVoiceAnimationDrawable.isRunning())
//                selfVoiceAnimationDrawable.start();
//        } else {
//            ivVoiceAnimtor.setImageResource(R.drawable.bg_chat_voice_to_playing_img);
//
//        }

    }


}
