package com.xueersi.parentsmeeting.modules.livevideo.page.item;

import android.animation.Animator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.text.util.Linkify;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.OnCompositionLoadedListener;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.BaseLiveMessagePager;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.widget.StandLiveHeadView;
import com.xueersi.ui.adapter.AdapterItemInterface;

/**
 * 站立直播非系统消息
 *
 * @author linyuqiang
 * @date 2018/5/10
 */
public class StandLiveMessOtherItem implements AdapterItemInterface<LiveMessageEntity> {
    static String TAG = "StandLiveMessSysItem";
    protected Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
    TextView tvMessageItem;
    StandLiveHeadView standLiveHeadView;
    LottieComposition mComposition;
    /** 聊天字体大小，最多13个汉字 */
    private int messageSize = 0;
    String fileName;
    Context mContext;
    public int urlclick;
    BaseLiveMessagePager.TextUrlClick textUrlClick;

    public StandLiveMessOtherItem(Context mContext, String fileName, int messageSize, int urlclick, BaseLiveMessagePager.TextUrlClick textUrlClick) {
        this.mContext = mContext;
        this.fileName = fileName;
        this.messageSize = messageSize;
        this.urlclick = urlclick;
        this.textUrlClick = textUrlClick;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_livevideo_stand_message;
    }

    @Override
    public void initViews(View root) {
        logger.d( "initViews");
        tvMessageItem = (TextView) root.findViewById(R.id.tv_livevideo_message_item);
        tvMessageItem.setTextSize(TypedValue.COMPLEX_UNIT_PX, messageSize);
        standLiveHeadView = root.findViewById(R.id.slhv_livevideo_message_head);
        standLiveHeadView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                Log.d(TAG, "onAnimationEnd:progerss=" + standLiveHeadView.getProgress());
//                                standLiveHeadView.setProgress(1.0f);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        initlottieAnim();
    }

    private void initlottieAnim() {
        LottieComposition.Factory.fromAssetFileName(mContext, fileName, new OnCompositionLoadedListener() {
            @Override
            public void onCompositionLoaded(@Nullable LottieComposition composition) {
                Log.d(TAG, "onCompositionLoaded:composition=" + composition);
                if (composition == null) {
                    return;
                }
                if (mComposition != null) {
                    return;
                }
                mComposition = composition;
                standLiveHeadView.setImageAssetsFolder("live_stand/head_segment/images");
                standLiveHeadView.setComposition(composition);
            }
        });
    }

    @Override
    public void bindListener() {

    }

    @Override
    public void updateViews(LiveMessageEntity entity, int position, Object objTag) {
        String sender = entity.getSender();
//                        switch (entity.getType()) {
//                            case LiveMessageEntity.MESSAGE_MINE:
//                            case LiveMessageEntity.MESSAGE_TEACHER:
//                            case LiveMessageEntity.MESSAGE_TIP:
//                            case LiveMessageEntity.MESSAGE_CLASS:
//                                color = nameColors[entity.getType()];
//                                break;
//                            default:
//                                color = nameColors[0];
//                                break;
//                        }
        if (urlclick == 1 && LiveMessageEntity.MESSAGE_TEACHER == entity.getType()) {
            tvMessageItem.setAutoLinkMask(Linkify.WEB_URLS);
            tvMessageItem.setText(entity.getText());
            textUrlClick.onUrlClick(tvMessageItem);
            CharSequence text = tvMessageItem.getText();
            tvMessageItem.setText(text);
        } else {
            tvMessageItem.setAutoLinkMask(0);
            tvMessageItem.setText(entity.getText());
        }
//                        boolean deng = standLiveHeadView.getEntity() == entity;
//                        logger.d( "updateViews:deng=" + deng + ",text=" + entity.getText());
//                        if (!deng) {
//                            standLiveHeadView.setIsMine(entity.getType() == LiveMessageEntity.MESSAGE_MINE);
////                        entity.setHeadUrl(getInfo.getHeadImgPath());
//                            standLiveHeadView.setName(entity.getSender());
//                            standLiveHeadView.setHead(entity.getHeadUrl());
//                            if (!entity.isPlayAnimation()) {
//                                entity.setPlayAnimation(true);
//                                standLiveHeadView.playAnimation();
//                            } else {
//                                standLiveHeadView.setProgress(1.0f);
//                            }
//                        } else {
//                            if (!entity.isPlayAnimation()) {
//                                logger.d( "updateViews:isPlayAnimation=false");
//                                entity.setPlayAnimation(true);
//                                standLiveHeadView.playAnimation();
//                            } else {
//                                standLiveHeadView.setProgress(1.0f);
//                            }
//                        }
//                        if (!entity.isPlayAnimation()) {
//                            logger.d( "updateViews:isPlayAnimation=false");
//                            entity.setPlayAnimation(true);
//                            standLiveHeadView.playAnimation();
//                        } else {
//                            logger.d( "updateViews:equal=" + (standLiveHeadView.getEntity() != entity));
//                            if (standLiveHeadView.getEntity() != entity) {
////                                standLiveHeadView.setProgress(1.0f);
//                                standLiveHeadView.playAnimation();
//                            } else {
////                                standLiveHeadView.resumeAnimation();
//                            }
//                        }
        boolean deng = standLiveHeadView.getEntity() == entity;
//                            if (deng) {
//                                return;
//                            }
        logger.d( "updateViews:deng=" + deng + ",progress=" + standLiveHeadView.getProgress() + ",standLiveHeadView=" + standLiveHeadView.getEntity() + ",text=" + entity.getText());
        standLiveHeadView.setIsMine(entity.getType() == LiveMessageEntity.MESSAGE_MINE);
//                        entity.setHeadUrl(getInfo.getHeadImgPath());
        standLiveHeadView.setName(entity.getSender());
        standLiveHeadView.setHead(entity.getHeadUrl());
        if (!entity.isPlayAnimation()) {
            entity.setPlayAnimation(true);
            standLiveHeadView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    standLiveHeadView.playAnimation();
                }
            }, 10);
        } else {
            standLiveHeadView.setProgress(1.0f);
        }
        entity.setStandLiveHeadView(standLiveHeadView);
        standLiveHeadView.setEntity(entity);
    }

}
