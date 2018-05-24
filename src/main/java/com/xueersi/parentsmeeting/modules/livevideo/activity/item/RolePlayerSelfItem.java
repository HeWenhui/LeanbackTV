package com.xueersi.parentsmeeting.modules.livevideo.activity.item;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.RolePlayerBll;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RolePlayerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.widget.CountDownHeadImageView;
import com.xueersi.parentsmeeting.modules.loginregisters.business.UserBll;
import com.xueersi.xesalib.utils.app.ContextManager;
import com.xueersi.xesalib.utils.app.XESToastUtils;
import com.xueersi.xesalib.utils.audio.safeaudioplayer.AudioPlayerManager;
import com.xueersi.xesalib.utils.audio.safeaudioplayer.PlayerCallback;
import com.xueersi.xesalib.utils.listener.OnAlphaTouchListener;
import com.xueersi.xesalib.utils.log.Loger;
import com.xueersi.xesalib.utils.network.NetWorkHelper;
import com.xueersi.xesalib.utils.uikit.SizeUtils;


/**
 * Roleplayer 自己扮演的角色
 *
 * @author ZouHao
 */
public class RolePlayerSelfItem extends RolePlayerItem {
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

    private TextView tvCountTime;

    /**
     * 主布局
     */
    private RelativeLayout rlMain;

    /**
     * 测评
     */
    //private TextView tvSpeechTip;
    public RolePlayerSelfItem(Context context, RolePlayerBll bll) {
        super(context, bll);
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
        //tvSpeechTip = root.findViewById(R.id.tv_live_roleplayer_message_speech_tip);
        rlMain = root.findViewById(R.id.rl_live_roleplayer_message_main);
        initStartView(root);
    }

    @Override
    public void bindListener() {

        // 单击语音播放
        vVoiceMain.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mEntity.getWebVoiceUrl()) && NetWorkHelper.isNetworkAvailable(mContext)) {
                    Loger.i("RolePlayerDemoTest", "点击自己语音：url  = " + mEntity.getWebVoiceUrl());
                    voiceClick();
                } else {
                    XESToastUtils.showToast(mContext, "没有检测到音频文件");
                    if (mEntity != null) {
                        Loger.i("RolePlayerDemoTest", "点击自己语音：url = " + mEntity.getWebVoiceUrl() + " NetWorkHelper" +
                                ".isNetworkAvailable(mContext) = " + NetWorkHelper.isNetworkAvailable(mContext));
                    }
                }
            }
        });
        //vVoiceMain.setOnTouchListener(new OnAlphaTouchListener());
    }

    private void voiceClick() {
        //点击语音的时候记录日志
        //RolePlayLog.sno8(mEntity,mContext,null);
        ivVoiceAnimtor.setBackgroundResource(R.drawable.animlst_livevideo_roleplayer_self_voice_white_anim);
        vVoiceMain.setBackgroundResource(R.drawable.livevideo_roleplay_bubble_me_reading);
        AnimationDrawable selfVoiceAnimationDrawable = null;
        selfVoiceAnimationDrawable = (AnimationDrawable) ivVoiceAnimtor.getBackground();
        if (selfVoiceAnimationDrawable != null && !selfVoiceAnimationDrawable.isRunning()) {
            selfVoiceAnimationDrawable.start();
        }
        //播放
        AudioPlayerManager.get(ContextManager.getApplication()).start(mEntity.getWebVoiceUrl(), new PlayerCallback() {
            @Override
            public void onCompletion(Object o, AudioPlayerManager audioPlayerManager) {
                Loger.i("RolePlayerDemoTest", "完成播放");
                ivVoiceAnimtor.setBackgroundResource(R.drawable.yuyin_zuo_huifang_3);
                vVoiceMain.setBackgroundResource(R.drawable.selector_live_roleplayer_self_item_bubble);
            }

            @Override
            public void onStop(Object dataSource, AudioPlayerManager manager) {
                super.onStop(dataSource, manager);
                Loger.i("RolePlayerDemoTest", "停止播放");
                ivVoiceAnimtor.setBackgroundResource(R.drawable.yuyin_zuo_huifang_3);
                vVoiceMain.setBackgroundResource(R.drawable.selector_live_roleplayer_self_item_bubble);
            }

            @Override
            public void onPreparing(Object dataSource, AudioPlayerManager manager) {
                Loger.i("RolePlayerDemoTest", "准备播放");

            }

            @Override
            public void onError(String msg, Object dataSource, AudioPlayerManager manager) {
                super.onError(msg, dataSource, manager);
                ivVoiceAnimtor.setBackgroundResource(R.drawable.yuyin_zuo_huifang_3);
                vVoiceMain.setBackgroundResource(R.drawable.selector_live_roleplayer_self_item_bubble);
            }
        });
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
//                                            int type = (int) messageEntity.getDataType(AppBll.getInstance()
// .getAppInfoEntity
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
    public void updateViews(final RolePlayerEntity.RolePlayerMessage entity,
                            int position, Object objTag) {
        super.updateViews(entity, position, objTag);
        updateUserHeadImage(civUserHead, UserBll.getInstance().getMyUserInfoEntity()
                .getHeadImg());
        rlMain.setVisibility(View.VISIBLE);
        tvMessageContent.setText(entity.getReadMsg());
        tvMessageContent.setTextColor(Color.parseColor("#333333"));
        tvUserNickName.setText(entity.getRolePlayer().getNickName());
        civUserHead.setBeginCountdownTime(false);
        // tvSpeechTip.setVisibility(View.INVISIBLE);

        switch (entity.getMsgStatus()) {
            case RolePlayerEntity.RolePlayerMessageStatus.WAIT_NORMAL:
                vVoiceMain.setBackgroundResource(R.drawable.selector_live_roleplayer_self_item_bubble);

                ivVoiceAnimtor.setBackgroundResource(R.drawable.yuyin_zuo_huifang_3);
                tvCountTime.setVisibility(View.INVISIBLE);
                civUserHead.invalidate();
                break;
            case RolePlayerEntity.RolePlayerMessageStatus.BEGIN_ROLEPLAY:
                vVoiceMain.setBackgroundResource(R.drawable.livevideo_roleplay_bubble_me_reading);
                tvMessageContent.setTextColor(Color.WHITE);
                ivVoiceAnimtor.setBackgroundResource(R.drawable.animlst_livevideo_roleplayer_self_voice_white_anim);
                AnimationDrawable selfVoiceAnimationDrawable = null;
                selfVoiceAnimationDrawable = (AnimationDrawable) ivVoiceAnimtor.getBackground();
                if (selfVoiceAnimationDrawable != null && !selfVoiceAnimationDrawable.isRunning()) {
                    selfVoiceAnimationDrawable.start();
                }
                tvCountTime.setText(entity.getMaxReadTime() + "");


                civUserHead.setFinishBorderColor(Color.parseColor("#C8E7D4"));
                civUserHead.setUnFinishBorderColor(Color.parseColor("#36BC9B"));
                civUserHead.startCountDown(entity.getMaxReadTime() * 1000, entity.getEndReadTime() * 1000, new
                        CountDownHeadImageView.countDownTimeImpl() {
                            @Override
                            public void countTime(long time) {
                                tvCountTime.setText(time + "");
                                if (time <= 3) {

                                    tvCountTime.setVisibility(View.VISIBLE);
                                }
                                Loger.i("RolePlayerSelfItemTest", mPosition + " / " + time);
                                entity.setEndReadTime((int) time);

                            }
                        });
                //civUserHead.invalidate();

                break;
            case RolePlayerEntity.RolePlayerMessageStatus.END_ROLEPLAY:
                Loger.i("RolePlayerSelfItemTest", "结束roleplay");
                vVoiceMain.setBackgroundResource(R.drawable.selector_live_roleplayer_self_item_bubble);
                ivVoiceAnimtor.setBackgroundResource(R.drawable.yuyin_zuo_huifang_3);
                //重置头像
                civUserHead.restore();
                tvCountTime.setText("");
                tvCountTime.setVisibility(View.INVISIBLE);
                showSpeechStar();
                speechPhoneScore();
                break;
            case RolePlayerEntity.RolePlayerMessageStatus.END_SPEECH:
                Loger.i("RolePlayerSelfItemTest", "测评有得分刚结束");
                //测评有得分刚结束
                vVoiceMain.setBackgroundResource(R.drawable.selector_live_roleplayer_self_item_bubble);
                ivVoiceAnimtor.setBackgroundResource(R.drawable.yuyin_zuo_huifang_3);
                //重置头像
                civUserHead.restore();
                tvCountTime.setText("");
                tvCountTime.setVisibility(View.INVISIBLE);
                showSpeechStar();
//                if (entity.getSpeechScore() >= 75 && entity.getSpeechScore() < 90) {
//                    tvSpeechTip.setVisibility(View.VISIBLE);
//                    tvSpeechTip.setText("Well done!");
//                } else if (entity.getSpeechScore() >= 90) {
//                    tvSpeechTip.setVisibility(View.VISIBLE);
//                    tvSpeechTip.setText("Fantastic!");
//                }
//                if (tvSpeechTip.getVisibility() == View.VISIBLE) {
//                    tvSpeechTip.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            tvSpeechTip.setVisibility(View.INVISIBLE);
//                        }
//                    }, 5000);
//                }
                //测试完毕后状态改成END_ROLEPLAY
                entity.setMsgStatus(RolePlayerEntity.RolePlayerMessageStatus.END_ROLEPLAY);
                speechPhoneScore();


                break;
            case RolePlayerEntity.RolePlayerMessageStatus.EMPTY:
                Loger.i("RolePlayerSelfItemTest", "空roleplay");
                rlMain.setVisibility(View.INVISIBLE);
                //重置头像
                civUserHead.restore();
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

    /**
     * 对音素分变色
     */

    private void speechPhoneScore() {
        String[] textArray;
        if (mEntity.getLstPhoneScore().isEmpty()) {
            if (mEntity.getSpeechScore() >= 75) {
                tvMessageContent.setTextColor(mContext.getResources().getColor(R.color.COLOR_53C058));
            } else if (mEntity.getSpeechScore() < 30) {
                tvMessageContent.setTextColor(mContext.getResources().getColor(R.color.COLOR_F13232));
            } else {
                tvMessageContent.setTextColor(mContext.getResources().getColor(R.color.COLOR_333333));
            }
        } else {
            int lastSub = 0;
            String subtemText = mEntity.getReadMsg();
            String upText = mEntity.getReadMsg().toUpperCase();
            //句子不带人名 hello boy
            SpannableStringBuilder spannable = new SpannableStringBuilder(subtemText);
            for (int i = 0; i < mEntity.getLstPhoneScore().size(); i++) {
                String word = mEntity.getLstPhoneScore().get(i).getWord();
                int index = upText.indexOf(word);
                int left = index + lastSub;
                int right = left + word.length();
                Log.i("RolePlayerTestDemo", word + " : " + mEntity.getLstPhoneScore().get(i).getScore());
                if (index != -1) {
                    subtemText = subtemText.substring(index);
                    upText = upText.substring(index);
                    lastSub += index;
                    if (mEntity.getLstPhoneScore().get(i).getScore() >= 75) {
                        //显示绿色
                        spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color
                                .COLOR_53C058)), left, right, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else if (mEntity.getLstPhoneScore().get(i).getScore() < 30) {
                        // 显示红色
                        spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color
                                .COLOR_F13232)), left, right, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else {
                        // 显示黑色
                        spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color
                                .COLOR_333333)), left, right, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }
            tvMessageContent.setText(spannable);
        }
    }
}


