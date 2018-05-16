package com.xueersi.parentsmeeting.modules.livevideo.activity.item;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.RolePlayerBll;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LivePlayBackMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RolePlayerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.widget.CountDownHeadImageView;
import com.xueersi.xesalib.utils.app.ContextManager;
import com.xueersi.xesalib.utils.app.XESToastUtils;
import com.xueersi.xesalib.utils.audio.safeaudioplayer.AudioPlayerManager;
import com.xueersi.xesalib.utils.audio.safeaudioplayer.PlayerCallback;
import com.xueersi.xesalib.utils.listener.OnAlphaTouchListener;
import com.xueersi.xesalib.utils.log.Loger;
import com.xueersi.xesalib.utils.network.NetWorkHelper;

import ren.yale.android.cachewebviewlib.utils.NetworkUtils;


/**
 * 语音类接收的消息
 *
 * @author ZouHao
 */
public class RolePlayerOtherItem extends RolePlayerItem {


    String TAG = "VoiceComeItem";

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
    private LottieAnimationView lavMessageDZ;


    public RolePlayerOtherItem(Context context, RolePlayerBll bll) {
        super(context, bll);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_live_roleplayer_other_voice;
    }

    @Override
    public void initViews(View root) {
        civUserHead = root.findViewById(R.id.civ_live_roleplayer_message_user_head);
        tvUserNickName = root.findViewById(R.id.tv_live_roleplayer_message_username);
        ivVoiceAnimtor = root.findViewById(R.id.iv_live_roleplayer_message_voice_main);
        vVoiceMain = root.findViewById(R.id.rl_live_roleplayer_message_voice_main);
        tvMessageContent = root.findViewById(R.id.tv_live_roleplayer_message_voice_content);
        rlMessageDZ = root.findViewById(R.id.rl_live_roleplayer_message_dz);
        ivMessageDZ = root.findViewById(R.id.iv_live_roleplayer_message_dz);
        lavMessageDZ = root.findViewById(R.id.lav_live_roleplayer_message_dz);
        initStartView(root);
    }

    @Override
    public void bindListener() {

        // 单击语音播放
        vVoiceMain.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(mEntity.getWebVoiceUrl()) && NetWorkHelper.isNetworkAvailable(mContext)) {
                    //只有当这个URL不为空时且有网才可以点击播放
                    Loger.i("RolePlayerDemoTest", "点击他人语音：url = " + mEntity.getWebVoiceUrl());

                    voiceClick();
                } else {
                    XESToastUtils.showToast(mContext, "没有检测到音频文件");
                    Loger.i("RolePlayerDemoTest", "点击他人语音：url 为空");
                }
            }
        });
        //vVoiceMain.setOnTouchListener(new OnAlphaTouchListener());
    }

    private void voiceClick() {

        //播放
        AudioPlayerManager.get(ContextManager.getApplication()).start(mEntity.getWebVoiceUrl(), new PlayerCallback() {
            @Override
            public void onCompletion(Object o, AudioPlayerManager audioPlayerManager) {
                ivVoiceAnimtor.setBackgroundResource(R.drawable.yuyin_you_huifang_3);
            }

            @Override
            public void onStop(Object dataSource, AudioPlayerManager manager) {
                super.onStop(dataSource, manager);
                ivVoiceAnimtor.setBackgroundResource(R.drawable.yuyin_you_huifang_3);
            }

            @Override
            public void onPreparing(Object dataSource, AudioPlayerManager manager) {
                ivVoiceAnimtor.setBackgroundResource(R.drawable.animlst_livevideo_roleplayer_other_voice_white_anim);
                AnimationDrawable animationDrawable = null;
                animationDrawable = (AnimationDrawable) ivVoiceAnimtor.getBackground();
                if (animationDrawable != null && !animationDrawable.isRunning()) {
                    animationDrawable.start();
                }
            }
        });

        //播放网络音频
//        if(mEntity.isVoiceIsplay()){
//            AudioPlayerManager.get(ContextManager.getApplication()).stop();
//            AudioPlayerManager.get(ContextManager.getApplication()).setDataSource("");
//            mEntity.setVoiceIsplay(false);
//            return;
//        }
//        if (TextUtils.isEmpty(mEntity.getLocalResourceUrl())
//                || !new File(mEntity.getLocalResourceUrl()).exists()) {
//            ivVoiceAnimtor.setBackgroundResource(R.drawable.bg_chat_voice_from_playing_img_blue);
//            XESToastUtils.showToast(mContext, "语音正在下载中" + (TextUtils.isEmpty(mEntity.getLocalResourceUrl
//                    ()) ? "" : mEntity.getLocalResourceUrl()));
//        } else {
//            ivVoiceAnimtor.setBackgroundResource(R.drawable.bg_chat_voice_from_download_img_blue);
//            ivVoiceAnimtor.setBackgroundResource(R.drawable.animlst_homework_voice_left_anim_blue);
//            final AnimationDrawable animationDrawable = (AnimationDrawable) ivVoiceAnimtor.getBackground();
//            animationDrawable.start();
//            mEntity.setVoiceIsplay(true);
//            AudioPlayerManager.get(ContextManager.getApplication()).start(mEntity.getLocalResourceUrl(), new
// PlayerCallback() {
//                @Override
//                public void onCompletion(Object dataSource, AudioPlayerManager manager) {
//                    mEntity.setVoiceIsplay(false);
//                    animationDrawable.stop();
//                    animationDrawable.selectDrawable(0);
//                    updateViews(mEntity, mVoicePosition, null);
//                    if(manager.getState()!=AudioPlayerManager.State.error) {
//                        handler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (mLstMessageData.size() > mPosition + 1) {
//                                    MessageEntity messageEntity = mLstMessageData.get(mPosition + 1);
//                                    int type = (int) messageEntity.getDataType(AppBll.getInstance().getAppInfoEntity
//                                            ().getChildName());
//                                    if (type == MessageEntity.MessageViewType.SELF_VOICE) {
//
//                                    } else if (type == MessageEntity.MessageViewType.COME_VOICE) {
//                                        if (messageEntity.voiceComeItem != null) {
//                                            messageEntity.voiceComeItem.voiceClick();
//                                            Loger.i(TAG, "playComplete:(equals)nextItem=" + (messageEntity
//                                                    .voiceComeItem.mEntity == messageEntity));
//                                        } else {
//                                            Loger.i(TAG, "playComplete:(equals)nextItem.voiceSelfItem=null");
//                                        }
//                                    } else {
//                                        Loger.i(TAG, "playComplete:(equals)nextItem.type=" + type);
//                                    }
//                                } else {
//                                    Loger.i(TAG, "playComplete:(equals)last");
//                                }
//                            }
//                        });
//                    }
//                }
//                @Override
//                public void onStop(Object dataSource, AudioPlayerManager manager) {
//                    mEntity.setVoiceIsplay(false);
//                    animationDrawable.stop();
//                    animationDrawable.selectDrawable(0);
//                    updateViews(mEntity, mVoicePosition, null);
//                }
//
//                @Override
//                public void onError(String msg, Object dataSource, AudioPlayerManager manager) {
//                    super.onError(msg, dataSource, manager);
//                    Loger.i("====voice play error");
//                    if(msg!=null) {
//                        IMLoger.info(mContext, "语音播放错误：" + msg+"  语音地址："+mEntity.getSmallResourceUrl());
//                    }
//                }
//            });
    }

    @Override
    public void updateViews(final RolePlayerEntity.RolePlayerMessage entity, int position, Object objTag) {
        super.updateViews(entity, position, objTag);
        updateUserHeadImage(civUserHead, entity.getRolePlayer().getHeadImg()); // 绑定用户头像

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
                vVoiceMain.setBackgroundResource(R.drawable.selector_live_roleplayer_other_item_bubble);
                ivVoiceAnimtor.setBackgroundResource(R.drawable.yuyin_you_huifang_3);
                break;
            case RolePlayerEntity.RolePlayerMessageStatus.BEGIN_ROLEPLAY:
               // Loger.i("RolePlayerDemoTest", "开始朗读");
                rlMessageDZ.setVisibility(View.VISIBLE);
                ivMessageDZ.setVisibility(View.VISIBLE);
                vVoiceMain.setBackgroundResource(R.drawable.livevideo_roleplay_bubble_other_reading);
                tvMessageContent.setTextColor(Color.WHITE);
                ivVoiceAnimtor.setBackgroundResource(R.drawable.animlst_livevideo_roleplayer_other_voice_white_anim);
                AnimationDrawable animationDrawable = null;
                animationDrawable = (AnimationDrawable) ivVoiceAnimtor.getBackground();
                if (animationDrawable != null && !animationDrawable.isRunning()) {
                    animationDrawable.start();
                }
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
                break;

            case RolePlayerEntity.RolePlayerMessageStatus.CANCEL_DZ:
               // Loger.i("RolePlayerDemoTest", "取消点赞按钮");
                ivMessageDZ.setImageResource(R.drawable.livevideo_roleplay_result_ic_normal);
                rlMessageDZ.setVisibility(View.GONE);
                ivMessageDZ.setVisibility(View.GONE);
                vVoiceMain.setBackgroundResource(R.drawable.selector_live_roleplayer_other_item_bubble);
                ivVoiceAnimtor.setBackgroundResource(R.drawable.yuyin_you_huifang_3);
                tvMessageContent.setTextColor(Color.parseColor("#333333"));
                break;
            default:
                break;
        }

//        if (mIsDownload || entity.getLocalResourceUrl() == null) {
//            // 如果本地的资源路径为空时，表示未从网络下载过，开始下载
//            EventBus.getDefault().post(new ChatMessageVoiceDownload(entity, probarReceiving));
//        } else {
//            probarReceiving.setVisibility(View.GONE);
//            if (entity.getReadType() == ReadType.UNREAD && probarReceiving.getVisibility() == View.GONE) {
//                ivUnReadPoint.setVisibility(View.VISIBLE);
//            } else {
//                ivUnReadPoint.setVisibility(View.GONE);
//            }
//
//        }
//
//        AnimationDrawable animationDrawable = null;
//        if (entity.isVoiceIsplay() && AudioPlayer.mVoiceUrl != null && AudioPlayer.mVoiceUrl.equals(entity
//                .getLocalResourceUrl())) {
//            ivVoiceAnimtor.setBackgroundResource(R.drawable.animlst_homework_voice_left_anim_blue);
//            animationDrawable = (AnimationDrawable) ivVoiceAnimtor.getBackground();
//            if (animationDrawable != null && !animationDrawable.isRunning())
//                animationDrawable.start();
//
//        } else {
//            ivVoiceAnimtor.setBackgroundResource(R.drawable.bg_chat_voice_from_playing_img_blue);
//        }
//
//        if (entity.isTeacher()) {
//            vVoiceMain.setBackgroundResource(R.drawable.bg_bubble_chat_left_red);
//        } else {
//            vVoiceMain.setBackgroundResource(R.drawable.bg_chat_bubble_left_white);
//        }
    }

    /**
     * 点赞按钮的点击事件
     * @param entity
     */
    private void setDZbtClick(final RolePlayerEntity.RolePlayerMessage entity) {

        ivMessageDZ.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Loger.i("RolePlayerDemoTest", "给他人点赞");
                bllRolePlayerBll.toOtherDZ(mEntity.getRolePlayer().getRoleId(), mEntity.getPosition());
                ivMessageDZ.setVisibility(View.GONE);
                lavMessageDZ.setVisibility(View.VISIBLE);
                entity.setDZ(true);
                lavMessageDZ.addAnimatorListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        lavMessageDZ.setVisibility(View.GONE);
                        ivMessageDZ.setVisibility(View.VISIBLE);
                        ivMessageDZ.setImageResource(R.drawable.livevideo_roleplay_result_ic_focsed);
                        ivMessageDZ.setOnClickListener(null);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
                lavMessageDZ.playAnimation();
            }
        });
    }
}
