package com.xueersi.parentsmeeting.widget.praise;

import android.content.Context;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.widget.praise.config.PraiseConfig;
import com.xueersi.parentsmeeting.widget.praise.entity.PraiseEntity;
import android.view.View;
public class PraisePager {

    Context mContext;
    PraiseEntity praiseEntity;
    PraiseBasePager pager;
    public PraisePager(Context context, PraiseEntity praiseEntity){
        this.mContext = context;
        this.praiseEntity = praiseEntity;
        if (praiseEntity.getPraiseStyle() == PraiseConfig.PRAISE_DARK) {
         pager= new PraiseDarkPager(mContext,praiseEntity);
        } else if (praiseEntity.getPraiseStyle() == PraiseConfig.PRAISE_LOVELY) {
            pager= new PraiseLovelyPager(mContext,praiseEntity);
        } else if (praiseEntity.getPraiseStyle() == PraiseConfig.PRAISE_CHINA) {
            pager= new PraiseChinaPager(mContext,praiseEntity);
        } else {
            pager= new PraiseWoodPager(mContext,praiseEntity);
        }
    }

    public View getRootView(){
     return    pager.getRootView();
    }

}
