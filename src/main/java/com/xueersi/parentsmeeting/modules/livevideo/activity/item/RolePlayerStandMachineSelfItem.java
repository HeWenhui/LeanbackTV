package com.xueersi.parentsmeeting.modules.livevideo.activity.item;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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

import com.xueersi.common.business.UserBll;
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
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.RolePlayLog;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.CountDownHeadImageView;


/**
 * Roleplayer 自己扮演的角色
 *
 * @author ZouHao
 */
public class RolePlayerStandMachineSelfItem extends RolePlayerItem {
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
    private AudioPlayerManager mAudioPlayerManager;//音频播放管理类
    private final LiveAndBackDebug liveAndBackDebug;//只为记录日志调用
    private boolean mIsPlaying = false;//标记当前语音正在播放,true 表示正在播放； flase 表示已经停止播放

    /**
     * 标记当前对话是不是准备开始
     */
    boolean mIsWaittingNormal;
    private final Handler mReadHandler;
    private int mPosition;

    /**
     * 测评
     */
    //private TextView tvSpeechTip;
    public RolePlayerStandMachineSelfItem(Context context, RolePlayerBll bll,Handler handler) {
        super(context, bll);
        liveAndBackDebug = ProxUtil.getProxUtil().get(context, LiveAndBackDebug.class);
        mReadHandler = handler;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_live_roleplayer_stand_self_voice;
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
                if (mEntity == null) {
                    logger.i( "数据为空");
                    return;
                }

                if(mEntity.getMsgStatus() == RolePlayerEntity.RolePlayerMessageStatus.BEGIN_ROLEPLAY){
                    logger.i("正在roleplay,不可点击自己对话");
                    return;
                }
                if(mEntity.getMsgStatus() != RolePlayerEntity.RolePlayerMessageStatus.CANCEL_DZ){
                    logger.i( "roleplay还未结束，不可点击对话");
                    return;
                }
                if (mIsPlaying) {
                    logger.i( "语音正在播放中，请不要重复点击");
                    return;
                }
                if (!TextUtils.isEmpty(mEntity.getWebVoiceUrl()) && NetWorkHelper.isNetworkAvailable(mContext)) {
                    logger.i( "点击自己语音：url  = " + mEntity.getWebVoiceUrl());
                    voiceClick();
                } else {
                    XESToastUtils.showToast(mContext, "没有检测到音频文件");
                    if (mEntity != null) {
                        logger.i( "点击自己语音：url = " + mEntity.getWebVoiceUrl() + " NetWorkHelper" +
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
        //speechPhoneScoreWhenClick();//点击对评测之后的文本变色
        RolePlayLog.sno8(liveAndBackDebug, mEntity, mContext);
        ivVoiceAnimtor.setBackgroundResource(R.drawable.animlst_livevideo_roleplayer_self_voice_white_anim);
        vVoiceMain.setBackgroundResource(R.drawable.livevideo_roleplay_stand_bubble_me_reading);
        tvMessageContent.setTextColor(Color.WHITE);
        AnimationDrawable selfVoiceAnimationDrawable = null;
        selfVoiceAnimationDrawable = (AnimationDrawable) ivVoiceAnimtor.getBackground();
        if (selfVoiceAnimationDrawable != null && !selfVoiceAnimationDrawable.isRunning()) {
            selfVoiceAnimationDrawable.start();
        }
        sendCurItemIndex();
        //播放
        mAudioPlayerManager = AudioPlayerManager.get(ContextManager.getApplication());
        mAudioPlayerManager.start(mEntity.getWebVoiceUrl(), new PlayerCallback() {
            @Override
            public void onCompletion(Object o, AudioPlayerManager audioPlayerManager) {
                logger.i( "完成播放");
                mIsPlaying = false;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        //如果是子线程的回调，会报出异常Only the original thread that created a view hierarchy can touch its views.
                        ivVoiceAnimtor.setBackgroundResource(R.drawable.yuyin_zuo_huifang_3_lan);
                        vVoiceMain.setBackgroundResource(R.drawable.selector_live_roleplayer_self_item_bubble);
                        tvMessageContent.setTextColor(Color.parseColor("#4E5BC1"));
                        //speechPhoneScore();
                    }
                });
                //speechPhoneScore();
            }

            @Override
            public void onStop(Object dataSource, AudioPlayerManager manager) {
                super.onStop(dataSource, manager);
                logger.i( "停止播放");
                mIsPlaying = false;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        //如果是子线程的回调，会报出异常Only the original thread that created a view hierarchy can touch its views.
                        ivVoiceAnimtor.setBackgroundResource(R.drawable.yuyin_zuo_huifang_3_lan);
                        vVoiceMain.setBackgroundResource(R.drawable.selector_live_roleplayer_self_item_bubble);
                        tvMessageContent.setTextColor(Color.parseColor("#4E5BC1"));
                        //speechPhoneScore();
                    }
                });
            }

            @Override
            public void onPreparing(Object dataSource, AudioPlayerManager manager) {
                logger.i( "准备播放");
                mIsPlaying = true;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        //如果是子线程的回调，会报出异常Only the original thread that created a view hierarchy can touch its views.
                        tvMessageContent.setTextColor(Color.WHITE);
                        //speechPhoneScore();
                    }
                });

            }

            @Override
            public void onError(String msg, Object dataSource, AudioPlayerManager manager) {
                super.onError(msg, dataSource, manager);
                mIsPlaying = false;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        //如果是子线程的回调，会报出异常Only the original thread that created a view hierarchy can touch its views.
                        ivVoiceAnimtor.setBackgroundResource(R.drawable.yuyin_zuo_huifang_3_lan);
                        vVoiceMain.setBackgroundResource(R.drawable.selector_live_roleplayer_self_item_bubble);
                        tvMessageContent.setTextColor(Color.parseColor("#4E5BC1"));
                        //speechPhoneScore();
                    }
                });

                //speechPhoneScore();
            }

        });

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
    public void updateViews(final RolePlayerEntity.RolePlayerMessage entity,
                            int position, Object objTag) {
        super.updateViews(entity, position, objTag);
        mPosition = position;
        String imgUrl = entity.getRolePlayer().getHeadImg();
        if(TextUtils.isEmpty(imgUrl)){
            imgUrl = UserBll.getInstance().getMyUserInfoEntity().getHeadImg();
        }
        updateUserHeadImage(civUserHead, imgUrl);
        civUserHead.setBorderWidth(SizeUtils.Dp2Px(mContext, 0));
        civUserHead.setBorderColor(Color.WHITE);
        rlMain.setVisibility(View.VISIBLE);
        tvMessageContent.setText(entity.getReadMsg());
        tvMessageContent.setTextColor(Color.parseColor("#4E5BC1"));
        tvUserNickName.setText(entity.getRolePlayer().getNickName());
        civUserHead.setBeginCountdownTime(false);
        // tvSpeechTip.setVisibility(View.INVISIBLE);

        switch (entity.getMsgStatus()) {
            case RolePlayerEntity.RolePlayerMessageStatus.WAIT_NORMAL:
                mIsWaittingNormal = true;
                mIsPlaying = true;
                vVoiceMain.setBackgroundResource(R.drawable.selector_live_roleplayer_self_item_bubble);

                ivVoiceAnimtor.setBackgroundResource(R.drawable.yuyin_zuo_huifang_3_lan);
                tvCountTime.setVisibility(View.INVISIBLE);
                civUserHead.invalidate();
                tvMessageContent.setTextColor(Color.parseColor("#4E5BC1"));

                break;
            case RolePlayerEntity.RolePlayerMessageStatus.BEGIN_ROLEPLAY:
                mIsPlaying = true;
                vVoiceMain.setBackgroundResource(R.drawable.livevideo_roleplay_stand_bubble_me_reading);
                tvMessageContent.setTextColor(Color.WHITE);
                ivVoiceAnimtor.setBackgroundResource(R.drawable.animlst_livevideo_roleplayer_self_voice_white_anim);
                AnimationDrawable selfVoiceAnimationDrawable = null;
                selfVoiceAnimationDrawable = (AnimationDrawable) ivVoiceAnimtor.getBackground();
                if (selfVoiceAnimationDrawable != null && !selfVoiceAnimationDrawable.isRunning()) {
                    selfVoiceAnimationDrawable.start();
                }
                tvCountTime.setText(entity.getMaxReadTime() + "");

                civUserHead.setBorderWidth(SizeUtils.Dp2Px(mContext, 3));
                civUserHead.setFinishBorderColor(Color.parseColor("#C8E7D4"));
                civUserHead.setUnFinishBorderColor(Color.parseColor("#F2658D"));
                civUserHead.startCountDown(entity.getMaxReadTime() * 1000, entity.getEndReadTime() * 1000, new
                        CountDownHeadImageView.countDownTimeImpl() {
                            @Override
                            public void countTime(long time) {
                                tvCountTime.setText(time + "");
                                if (time <= 3) {

                                    tvCountTime.setVisibility(View.VISIBLE);
                                }
                                //logger.i( mPosition + " / " + time);
                                entity.setEndReadTime((int) time);

                            }
                        });
                //civUserHead.invalidate();

                break;
            case RolePlayerEntity.RolePlayerMessageStatus.END_ROLEPLAY:
                logger.i( "结束roleplay");
                vVoiceMain.setBackgroundResource(R.drawable.selector_live_roleplayer_self_item_bubble);
                ivVoiceAnimtor.setBackgroundResource(R.drawable.yuyin_zuo_huifang_3_lan);
                //重置头像
                civUserHead.restore();
                tvCountTime.setText("");
                tvCountTime.setVisibility(View.INVISIBLE);
                showSpeechStar();
                if(mAudioPlayerManager != null){
                    mAudioPlayerManager.stop();
                    mAudioPlayerManager.release();
                    mAudioPlayerManager = null;
                }
                //speechPhoneScore();
                break;
            case RolePlayerEntity.RolePlayerMessageStatus.END_SPEECH:
                logger.i( "测评有得分刚结束");
                //测评有得分刚结束
                vVoiceMain.setBackgroundResource(R.drawable.selector_live_roleplayer_self_item_bubble);
                ivVoiceAnimtor.setBackgroundResource(R.drawable.yuyin_zuo_huifang_3_lan);
                //重置头像
                civUserHead.restore();
                tvCountTime.setText("");
                tvCountTime.setVisibility(View.INVISIBLE);
                showSpeechStar();
                //测试完毕后状态改成END_ROLEPLAY
                entity.setMsgStatus(RolePlayerEntity.RolePlayerMessageStatus.END_ROLEPLAY);
                //speechPhoneScore();


                break;
            case RolePlayerEntity.RolePlayerMessageStatus.EMPTY:
                logger.i( "空roleplay");
                rlMain.setVisibility(View.INVISIBLE);
                //重置头像
                civUserHead.restore();
                break;

            case RolePlayerEntity.RolePlayerMessageStatus.CANCEL_DZ:

                logger.i( "取消点赞");
                mIsPlaying = false;
                vVoiceMain.setBackgroundResource(R.drawable.selector_live_roleplayer_self_item_bubble);
                ivVoiceAnimtor.setBackgroundResource(R.drawable.yuyin_zuo_huifang_3_lan);
                //重置头像
                civUserHead.restore();
                tvCountTime.setText("");
                tvCountTime.setVisibility(View.INVISIBLE);
                showSpeechStar();
                //speechPhoneScore();
                break;
            default:
                break;
        }


    }

    /**
     * 对音素分变色
     */

   /* private void speechPhoneScore() {
        tvMessageContent.setTextColor(mContext.getResources().getColor(R.color
                .COLOR_333333));
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
    }*/

    /**
     * 对音素分变色
     */

    private void speechPhoneScoreWhenClick() {
        tvMessageContent.setTextColor(mContext.getResources().getColor(R.color
                .COLOR_FFFFFF));
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
                                .COLOR_19F164)), left, right, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else if (mEntity.getLstPhoneScore().get(i).getScore() < 30) {
                        // 显示红色
                        spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color
                                .COLOR_FF4444)), left, right, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    } else {
                        // 显示白色
                        spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color
                                .COLOR_FFFFFF)), left, right, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }

            tvMessageContent.setText(spannable);
        }
    }

    /**
     * 当roleplay界面销毁的时候，同时不再播放音频
     */
    public void stopVoicePlay() {
        mIsPlaying = false;//重置播放标记为未播放状态
        if (mAudioPlayerManager != null) {
            mAudioPlayerManager.stop();
            logger.i( "roleplay已结束，停止播放自己音频");
        }
    }


    public void relaseAudioPlay() {

        if(mAudioPlayerManager != null){
            mAudioPlayerManager.stop();
            mAudioPlayerManager.release();
        }
    }
}


