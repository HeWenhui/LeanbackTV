package com.xueersi.parentsmeeting.modules.livevideo.activity.item;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RolePlayerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.widget.CountDownHeadImageView;
import com.xueersi.xesalib.adapter.AdapterItemInterface;
import com.xueersi.xesalib.utils.app.ContextManager;
import com.xueersi.xesalib.utils.uikit.SizeUtils;
import com.xueersi.xesalib.utils.uikit.imageloader.ImageLoader;


/**
 * Roleplayer对话内容
 */
public abstract class RolePlayerItem implements AdapterItemInterface<RolePlayerEntity.RolePlayerMessage> {

    /**
     * 对话内容
     */
    protected RolePlayerEntity.RolePlayerMessage mEntity;

    /**
     * 当前在listView中的position
     */
    protected int mPosition;
    protected Context mContext;

    /** 评测的星星 */
    private ImageView ivSpeechStart1;
    /** 评测的星星 */
    private ImageView ivSpeechStart2;
    /** 评测的星星 */
    private ImageView ivSpeechStart3;
    /** 评测的星星 */
    private ImageView ivSpeechStart4;
    /** 评测的星星 */
    private ImageView ivSpeechStart5;


    public RolePlayerItem(Context context) {
        this.mContext = context;
    }

    protected void initStartView(View root) {
        ivSpeechStart1 = root.findViewById(R.id.iv_live_roleplayer_message_speech_start1);
        ivSpeechStart2 = root.findViewById(R.id.iv_live_roleplayer_message_speech_start2);
        ivSpeechStart3 = root.findViewById(R.id.iv_live_roleplayer_message_speech_start3);
        ivSpeechStart4 = root.findViewById(R.id.iv_live_roleplayer_message_speech_start4);
        ivSpeechStart5 = root.findViewById(R.id.iv_live_roleplayer_message_speech_start5);
    }

    @Override
    public void updateViews(RolePlayerEntity.RolePlayerMessage entity, int position, Object objTag) {
        mEntity = entity;
        mPosition = position;
        ivSpeechStart1.setVisibility(View.GONE);
        ivSpeechStart2.setVisibility(View.GONE);
        ivSpeechStart3.setVisibility(View.GONE);
        ivSpeechStart4.setVisibility(View.GONE);
        ivSpeechStart5.setVisibility(View.GONE);
    }

    /**
     * 更新头像信息
     *
     * @param civHeadImage
     * @param imgURL
     */
    protected void updateUserHeadImage(final CountDownHeadImageView civHeadImage, final String imgURL) {
        if (TextUtils.isEmpty(imgURL)) {
            // 如果图片URL为空则直接加载默认图片，因为图片加载框架对空字符串的路径加载会加载到其它图片上，故这样解决
            civHeadImage.setImageResource(R.drawable.ic_default_head_square);
            return;
        }

        civHeadImage.setBorderWidth(SizeUtils.Dp2Px(mContext, 3));
        civHeadImage.setBorderColor(Color.WHITE);
        ImageLoader.with(ContextManager.getApplication()).load(imgURL).error(R.drawable.ic_default_head_square).placeHolder(R.drawable
                .ic_default_head_square)
                .into(civHeadImage);

    }

    /**
     * 显示评价星星
     */
    protected void showSpeechStar() {
        if (mEntity.getSpeechScore() >= 1 && mEntity.getSpeechScore() < 40) {
            ivSpeechStart1.setVisibility(View.VISIBLE);
        }
        if (mEntity.getSpeechScore() >= 40 && mEntity.getSpeechScore() < 60) {
            ivSpeechStart2.setVisibility(View.VISIBLE);
        }
        if (mEntity.getSpeechScore() >= 60 && mEntity.getSpeechScore() < 75) {
            ivSpeechStart3.setVisibility(View.VISIBLE);
        }
        if (mEntity.getSpeechScore() >= 75 && mEntity.getSpeechScore() < 90) {
            ivSpeechStart4.setVisibility(View.VISIBLE);
        }
        if (mEntity.getSpeechScore() >= 90) {
            ivSpeechStart5.setVisibility(View.VISIBLE);
        }
    }

}
