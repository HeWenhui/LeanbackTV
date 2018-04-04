package com.xueersi.parentsmeeting.modules.livevideo.activity.item;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RolePlayerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.widget.CountDownHeadImageView;
import com.xueersi.xesalib.utils.listener.OnAlphaTouchListener;



/**
 * 语音类接收的消息
 *
 * @author ZouHao
 */
public class RolePlayerOtherItem extends RolePlayerItem {


    String TAG = "VoiceComeItem";

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

    public RolePlayerOtherItem(Context context) {
        super(context);
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
    }

    @Override
    public void bindListener() {

        // 单击语音播放
        vVoiceMain.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(mEntity.getWebVoiceUrl())) {
                    //只有当这个URL不为空时才可以点击播放
                    voiceClick();
                }
            }
        });
        vVoiceMain.setOnTouchListener(new OnAlphaTouchListener());
    }

    private void voiceClick() {

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
//            AudioPlayerManager.get(ContextManager.getApplication()).start(mEntity.getLocalResourceUrl(), new PlayerCallback() {
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
    public void updateViews(RolePlayerEntity.RolePlayerMessage entity, int position, Object objTag) {
        super.updateViews(entity, position, objTag);
        updateUserHeadImage(civUserHead, entity.getRolePlayer().getHeadImg()); // 绑定用户头像

        // 播放语音
        ivVoiceAnimtor.setBackgroundResource(R.drawable.bg_chat_voice_from_playing_img_blue);
        tvMessageContent.setText(entity.getReadMsg());
        tvUserNickName.setText(entity.getRolePlayer().getNickName() + "(" + entity.getRolePlayer().getRoleName() + ")");

        switch (entity.getMsgStatus()) {
            case RolePlayerEntity.RolePlayerMessageStatus.WAIT_NORMAL:
                ivVoiceAnimtor.setBackgroundResource(R.drawable.bg_chat_voice_from_playing_img_blue);
                break;
            case RolePlayerEntity.RolePlayerMessageStatus.BEGIN_ROLEPLAY:
                ivVoiceAnimtor.setBackgroundResource(R.drawable.animlst_homework_voice_left_anim_blue);
                AnimationDrawable animationDrawable = null;
                animationDrawable = (AnimationDrawable) ivVoiceAnimtor.getBackground();
                if (animationDrawable != null && !animationDrawable.isRunning()) {
                    animationDrawable.start();
                }
                break;
            case RolePlayerEntity.RolePlayerMessageStatus.END_ROLEPLAY:
                ivVoiceAnimtor.setBackgroundResource(R.drawable.bg_chat_voice_from_playing_img_blue);
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
}
