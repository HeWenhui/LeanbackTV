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
        tvMessageItem = root.findViewById(R.id.tv_livevideo_message_item);
        tvMessageItem.setTextSize(TypedValue.COMPLEX_UNIT_PX, messageSize);
        standLiveHeadView = root.findViewById(R.id.slhv_livevideo_message_head);
        standLiveHeadView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                logger.d("onAnimationEnd:progerss=" + standLiveHeadView.getProgress());

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
                logger.d("onCompositionLoaded:composition=" + composition);
                if (composition == null) {
                    return;
                }
                if (mComposition != null) {
                    return;
                }
                mComposition = composition;
                standLiveHeadView.setImageAssetsFolder("live_stand/chat/images");
                standLiveHeadView.setComposition(composition);
            }
        });
    }

    @Override
    public void bindListener() {

    }

    @Override
    public void updateViews(LiveMessageEntity entity, int position, Object objTag) {
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
        standLiveHeadView.setIsMine(entity.getType() == LiveMessageEntity.MESSAGE_MINE);
        standLiveHeadView.setSystem(false);
        standLiveHeadView.setEntity(entity);
        entity.setStandLiveHeadView(standLiveHeadView);
        if (!entity.isPlayAnimation()) {
            entity.setPlayAnimation(true);
            standLiveHeadView.playAnimation();
        } else {
            standLiveHeadView.setProgress(1.0f);
        }
    }
}
