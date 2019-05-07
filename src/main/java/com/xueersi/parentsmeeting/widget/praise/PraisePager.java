package com.xueersi.parentsmeeting.widget.praise;

import android.content.Context;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.page.LiveBasePager;
import com.xueersi.parentsmeeting.widget.praise.config.PraiseConfig;
import com.xueersi.parentsmeeting.widget.praise.entity.PraiseEntity;

import android.view.View;
import android.widget.RelativeLayout;

public class PraisePager {

    Context mContext;
    PraiseEntity praiseEntity;
    PraiseBasePager pager;

    public PraisePager(Context context, PraiseEntity praiseEntity) {
        this.mContext = context;
        this.praiseEntity = praiseEntity;
        if (praiseEntity.getPraiseStyle() == PraiseConfig.PRAISE_DARK) {
            pager = new PraiseDarkPager(mContext, praiseEntity);
        } else if (praiseEntity.getPraiseStyle() == PraiseConfig.PRAISE_LOVELY) {
            pager = new PraiseLovelyPager(mContext, praiseEntity);
        } else if (praiseEntity.getPraiseStyle() == PraiseConfig.PRAISE_CHINA) {
            pager = new PraiseChinaPager(mContext, praiseEntity);
        } else {
            pager = new PraiseWoodPager(mContext, praiseEntity);
        }
    }

    public void showPraisePager(RelativeLayout bottomContent) {
        View view = getRootView();
        if (bottomContent != null && view != null) {
            bottomContent.addView(view);
            setClosePraise(bottomContent);
        }
    }
    public void closePraisePager(){
        if (pager != null) {
            pager.closePraisePager();
        }
    }

    private void setClosePraise(final RelativeLayout bottomContent){
        if (pager != null) {
            pager.setOnPagerClose(new LiveBasePager.OnPagerClose() {
                @Override
                public void onClose(LiveBasePager basePager) {
                    bottomContent.removeView(basePager.getRootView());
                }
            });
        }
    }

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
