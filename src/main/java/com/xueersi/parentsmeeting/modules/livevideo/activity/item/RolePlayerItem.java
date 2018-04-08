package com.xueersi.parentsmeeting.modules.livevideo.activity.item;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;

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

    /**
     * 聊天气泡占屏幕的比例
     */
    protected final float mBubbleWidth = 0.7f;

    public RolePlayerItem(Context context) {
        this.mContext = context;
    }

    @Override
    public void updateViews(RolePlayerEntity.RolePlayerMessage entity, int position, Object objTag) {
        mEntity = entity;
        mPosition = position;

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

}
