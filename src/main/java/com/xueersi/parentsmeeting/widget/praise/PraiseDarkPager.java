package com.xueersi.parentsmeeting.widget.praise;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.widget.praise.business.OnPraisePageListener;
import com.xueersi.parentsmeeting.widget.praise.config.PraiseConfig;
import com.xueersi.parentsmeeting.widget.praise.entity.PraiseEntity;

/**
 * 表扬榜-辅导老师 類型1
 *
 * @param hua
 */
public class PraiseDarkPager extends PraiseBasePager {


    public PraiseDarkPager(Context context, PraiseEntity praiseEntity,OnPraisePageListener listener,RelativeLayout bottomContent) {
        super(context, praiseEntity,listener,bottomContent);
    }

    @Override
    protected void setPriseType() {
        super.setPriseType();
        // 课清表扬榜
        if (mPraiseEntity.getPraiseType() == PraiseConfig.PRAISE_TYPE_PRACTICE) {
            tvTitle.setVisibility(View.GONE);
            ivTitle.setVisibility(View.VISIBLE);
            ivTitle.setBackgroundResource(R.drawable.bg_page_livevideo_praise_list_dark_title_1);
            // 口述题表扬榜
        } else if (mPraiseEntity.getPraiseType() == PraiseConfig.PRAISE_TYPE_TALK) {
            tvTitle.setVisibility(View.GONE);
            ivTitle.setVisibility(View.VISIBLE);
            ivTitle.setBackgroundResource(R.drawable.bg_page_livevideo_praise_list_dark_title_2);
            // 互动题表扬榜
        } else if (mPraiseEntity.getPraiseType() == PraiseConfig.PRAISE_TYPE_QUESITON) {
            tvTitle.setVisibility(View.GONE);
            ivTitle.setVisibility(View.VISIBLE);
            ivTitle.setBackgroundResource(R.drawable.bg_page_livevideo_praise_list_dark_title_3);
            // 其他表扬榜
        }
    }

    @Override
    protected void setReslutType() {
        super.setReslutType();
        if(mPraiseEntity.getResultType() == PraiseConfig.RESULT_PRAISE){
            tvSubTitle.setText("恭喜入榜，再接再厉哦");
            tvSubTitle.setBackgroundResource(R.drawable.bg_page_livevideo_praise_list_dark_sub_title_1);
        } else {
            tvSubTitle.setText("不要灰心，下次要上榜哦");
            tvSubTitle.setBackgroundResource(R.drawable.bg_page_livevideo_praise_list_dark_sub_title_2);

        }
    }
}
