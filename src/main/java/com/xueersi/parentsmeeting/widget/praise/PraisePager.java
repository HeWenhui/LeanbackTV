package com.xueersi.parentsmeeting.widget.praise;

import android.content.Context;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.page.LiveBasePager;
import com.xueersi.parentsmeeting.widget.praise.business.OnPraisePageListener;
import com.xueersi.parentsmeeting.widget.praise.config.PraiseConfig;
import com.xueersi.parentsmeeting.widget.praise.entity.PraiseEntity;

import android.view.View;
import android.widget.RelativeLayout;

public class PraisePager {

    Context mContext;
    PraiseEntity praiseEntity;
    PraiseBasePager pager;
    OnPraisePageListener onPraisePageListener;

    public PraisePager(Context context, PraiseEntity praiseEntity, OnPraisePageListener listener,RelativeLayout bottomContent) {
        this.onPraisePageListener = listener;
        this.mContext = context;
        this.praiseEntity = praiseEntity;
        if (praiseEntity.getPraiseStyle() == PraiseConfig.PRAISE_DARK) {
            pager = new PraiseDarkPager(mContext, praiseEntity, listener,bottomContent);
        } else if (praiseEntity.getPraiseStyle() == PraiseConfig.PRAISE_LOVELY) {
            pager = new PraiseLovelyPager(mContext, praiseEntity, listener,bottomContent);
        } else if (praiseEntity.getPraiseStyle() == PraiseConfig.PRAISE_CHINA) {
            pager = new PraiseChinaPager(mContext, praiseEntity, listener,bottomContent);
        } else {
            pager = new PraiseWoodPager(mContext, praiseEntity, listener,bottomContent);
        }
    }

    /**
     * 显示表扬榜
     *
     * @param bottomContent
     */
    public void showPraisePager(RelativeLayout bottomContent) {
        View view = getRootView();
        if (bottomContent != null && view != null) {
            bottomContent.addView(view);
            setClosePraise(bottomContent);
        }
    }

    /**
     * 关闭表扬榜
     */
    public void closePraisePager() {
        if (pager != null) {
            pager.closePraisePager();
        }
    }

    /**
     * 设置关闭表扬榜监听
     *
     * @param bottomContent
     */
    private void setClosePraise(final RelativeLayout bottomContent) {
        if (pager != null && bottomContent != null) {
            pager.setOnPagerClose(new LiveBasePager.OnPagerClose() {
                @Override
                public void onClose(LiveBasePager basePager) {
                    bottomContent.removeView(basePager.getRootView());
                }
            });
        }
    }

    /**
     * 设置点赞语
     *
     * @param num
     */
    public void setPraiseTotal(int num) {
        if (pager != null) {
            pager.setPraiseTotal(num);
        }
    }

    /**
     * 展示鼓励语
     */
    public void showEncouraging() {
        if (pager != null) {
            pager.showEncouraging();
        }
    }

    public View getRootView() {
        if (pager != null) {
            return pager.getRootView();
        }
        return null;
    }

}
