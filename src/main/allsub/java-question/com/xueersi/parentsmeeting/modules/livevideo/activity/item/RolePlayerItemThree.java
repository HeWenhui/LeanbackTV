package com.xueersi.parentsmeeting.modules.livevideo.activity.item;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.RolePlayerBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.RolePlayerBllThree;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RolePlayerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.widget.CountDownHeadImageView;
import com.xueersi.ui.adapter.AdapterItemInterface;


/**
 * Roleplayer对话内容
 */
public abstract class RolePlayerItemThree implements AdapterItemInterface<RolePlayerEntity.RolePlayerMessage> {
    protected Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
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

    protected RolePlayerBllThree bllRolePlayerBll;


    public RolePlayerItemThree(Context context, RolePlayerBllThree rolePlayerBll) {
        this.mContext = context;
        this.bllRolePlayerBll = rolePlayerBll;
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
        ivSpeechStart1.setVisibility(View.INVISIBLE);
        ivSpeechStart2.setVisibility(View.INVISIBLE);
        ivSpeechStart3.setVisibility(View.INVISIBLE);
        ivSpeechStart4.setVisibility(View.INVISIBLE);
        ivSpeechStart5.setVisibility(View.INVISIBLE);
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
        logger.i("showSpeechStar:mEntity.getStars() = " + mEntity.getStars());
        if (mEntity.getStars() >= 1) {
            ivSpeechStart1.setVisibility(View.VISIBLE);
        }
        if (mEntity.getStars() >= 2) {
            ivSpeechStart2.setVisibility(View.VISIBLE);
        }
        if (mEntity.getStars() >= 3) {
            ivSpeechStart3.setVisibility(View.VISIBLE);
        }
        if (mEntity.getStars() >= 4) {
            ivSpeechStart4.setVisibility(View.VISIBLE);
        }
        if (mEntity.getStars() >= 5) {
            ivSpeechStart5.setVisibility(View.VISIBLE);
        }
    }

}
