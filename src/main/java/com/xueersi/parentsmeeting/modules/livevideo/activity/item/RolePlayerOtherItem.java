package com.xueersi.parentsmeeting.modules.livevideo.activity.item;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.RolePlayerBll;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LivePlayBackMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RolePlayerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.RolePlayLog;
import com.xueersi.parentsmeeting.modules.livevideo.widget.CountDownHeadImageView;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.xesalib.utils.audio.safeaudioplayer.AudioPlayerManager;
import com.xueersi.xesalib.utils.audio.safeaudioplayer.PlayerCallback;
import com.xueersi.xesalib.utils.listener.OnAlphaTouchListener;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;
import com.xueersi.lib.framework.utils.NetWorkHelper;

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
    //private LottieAnimationView lavMessageDZ;
    private AudioPlayerManager mAudioPlayerManager;//音频播放管理类
    private final LiveBll mLiveBll;//只为记录日志用
    private boolean mIsPlaying = false;//标记当前语音正在播放,true 表示正在播放； flase 表示已经停止播放
    private boolean mIsVideoUnClick = true;//标记当前语音是否可点击；true 不可点击 false 可点击；默认true


    public RolePlayerOtherItem(Context context, RolePlayerBll bll, LiveBll liveBll) {
        super(context, bll);
        mLiveBll = liveBll;
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
                    Loger.i("RolePlayerDemoTest", "数据为空");
                    return;
                }

                if (mIsPlaying) {
                    Loger.i("RolePlayerDemoTest", "语音正在播放中，请不要重复点击");
                    return;
                }
                if (!TextUtils.isEmpty(mEntity.getWebVoiceUrl()) && NetWorkHelper.isNetworkAvailable(mContext)) {
                    //只有当这个URL不为空时且有网才可以点击播放
                    Loger.i("RolePlayerDemoTest", "点击他人语音：url = " + mEntity.getWebVoiceUrl());

                    voiceClick();
                } else {
                    XESToastUtils.showToast(mContext, "没有检测到音频文件");
                    if (mEntity != null) {
                        Loger.i("RolePlayerDemoTest", "点击他人语音：url = " + mEntity.getWebVoiceUrl() + " NetWorkHelper" +
                                ".isNetworkAvailable(mContext) = " + NetWorkHelper.isNetworkAvailable(mContext));
                    }

                }
            }
        });
        //vVoiceMain.setOnTouchListener(new OnAlphaTouchListener());
    }

    private void voiceClick() {
        //点击语音的时候记录日志
        Loger.i("RolePlayerDemoTestlog", " 点击播放音频，记录日志 ");
        RolePlayLog.sno8(mLiveBll, mEntity, mContext);
        vVoiceMain.setBackgroundResource(R.drawable.livevideo_roleplay_bubble_other_reading);
        ivVoiceAnimtor.setBackgroundResource(R.drawable.animlst_livevideo_roleplayer_other_voice_white_anim);
        AnimationDrawable animationDrawable = null;
        animationDrawable = (AnimationDrawable) ivVoiceAnimtor.getBackground();
        if (animationDrawable != null && !animationDrawable.isRunning()) {
            animationDrawable.start();
        }
        tvMessageContent.setTextColor(Color.WHITE);

        mAudioPlayerManager = AudioPlayerManager.get(ContextManager.getApplication());
        //播放
        mAudioPlayerManager.start(mEntity.getWebVoiceUrl(), new PlayerCallback() {
            @Override
            public void onCompletion(Object o, AudioPlayerManager audioPlayerManager) {
                Loger.i("RolePlayerDemoTest", "完成播放");
                mIsPlaying = false;
                ivVoiceAnimtor.setBackgroundResource(R.drawable.yuyin_you_huifang_3);
                vVoiceMain.setBackgroundResource(R.drawable.selector_live_roleplayer_self_item_bubble);
                tvMessageContent.setTextColor(Color.parseColor("#333333"));
            }

            @Override
            public void onStop(Object dataSource, AudioPlayerManager manager) {
                super.onStop(dataSource, manager);
                Loger.i("RolePlayerDemoTest", "停止播放");
                mIsPlaying = false;
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        //如果是子线程的回调，会报出异常Only the original thread that created a view hierarchy can touch its views.
                        ivVoiceAnimtor.setBackgroundResource(R.drawable.yuyin_you_huifang_3);
                        vVoiceMain.setBackgroundResource(R.drawable.selector_live_roleplayer_self_item_bubble);
                        tvMessageContent.setTextColor(Color.parseColor("#333333"));
                    }
                });

            }

            @Override
            public void onPreparing(Object dataSource, AudioPlayerManager manager) {
                Loger.i("RolePlayerDemoTest", "准备播放");
                mIsPlaying = true;

            }

            @Override
            public void onError(String msg, Object dataSource, AudioPlayerManager manager) {
                super.onError(msg, dataSource, manager);
                mIsPlaying = false;
                ivVoiceAnimtor.setBackgroundResource(R.drawable.yuyin_you_huifang_3);
                vVoiceMain.setBackgroundResource(R.drawable.selector_live_roleplayer_self_item_bubble);
                tvMessageContent.setTextColor(Color.parseColor("#333333"));
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
       /* if(entity.getMsgStatus() == RolePlayerEntity.RolePlayerMessageStatus.STOP_UPDATE){
            rlMessageDZ.setVisibility(View.GONE);
            ivMessageDZ.setVisibility(View.GONE);
            Loger.i("RolePlayerDemoTest", "停止刷新");
            return;
        }*/
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
                mIsPlaying = true;
                vVoiceMain.setBackgroundResource(R.drawable.selector_live_roleplayer_other_item_bubble);
                ivVoiceAnimtor.setBackgroundResource(R.drawable.yuyin_you_huifang_3);

                break;
            case RolePlayerEntity.RolePlayerMessageStatus.BEGIN_ROLEPLAY:
                // Loger.i("RolePlayerDemoTest", "开始朗读");
                mIsPlaying = true;
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
                Loger.i("RolePlayerDemoTest", "END_ROLEPLAY:显示星星");
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
                Loger.i("RolePlayerDemoTest", "CANCEL_DZ:显示星星");
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
     *
     * @param entity
     */
    private void setDZbtClick(final RolePlayerEntity.RolePlayerMessage entity) {

        ivMessageDZ.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (entity.isDZ()) {
                    Loger.i("RolePlayerDemoTest", "已经点过赞了。。。");
                    return;
                }
                Loger.i("RolePlayerDemoTest", "给他人点赞");

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
             /*   lavMessageDZ.addAnimatorListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                        scaleAnimation.start();
                        Loger.i("RolePlayerDemoTest", "onAnimationStart");

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        Loger.i("RolePlayerDemoTest", "onAnimationEnd");
                        lavMessageDZ.setVisibility(View.GONE);
                        ivMessageDZ.setVisibility(View.VISIBLE);
                        ivMessageDZ.setImageResource(R.drawable.livevideo_roleplay_result_ic_focsed);
                        ivMessageDZ.setOnClickListener(null);
                        scaleAnimation.cancel();
                        if (RolePlayerEntity.RolePlayerMessageStatus.CANCEL_DZ == mEntity.getMsgStatus()) {
                            ivMessageDZ.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                        Loger.i("RolePlayerDemoTest", "onAnimationCancel");
                        lavMessageDZ.setVisibility(View.GONE);
                        ivMessageDZ.setVisibility(View.GONE);
                        ivMessageDZ.setOnClickListener(null);
                        scaleAnimation.cancel();

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }


                });*/
                //lavMessageDZ.playAnimation();
            }
        });
    }

    public void stopVoicePlay() {
        mIsPlaying = false;
        if (mAudioPlayerManager != null) {
            mAudioPlayerManager.releaseEveryThing();
            Loger.i("RolePlayerDemoTest", "roleplay已结束，停止播放他人音频");
        }
    }

    /**
     * 设置语音可否点击
     *
     * @param isVideoUnClick true为不可点击；false为可点击，默认true不可点击
     */
    public void setVideoUnClick(boolean isVideoUnClick) {
        mIsVideoUnClick = isVideoUnClick;
        mEntity.setUnClick(mIsVideoUnClick);
        //changeYuyinClickable();

    }

    private void changeYuyinClickable() {
        vVoiceMain.setClickable(mIsVideoUnClick ? false : true);
    }
}
