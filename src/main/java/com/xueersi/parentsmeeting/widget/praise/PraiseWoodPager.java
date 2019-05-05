package com.xueersi.parentsmeeting.widget.praise;

import android.content.Context;
import android.view.View;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.widget.praise.config.PraiseConfig;
import com.xueersi.parentsmeeting.widget.praise.entity.PraiseEntity;

/**
 * 表扬榜-辅导老师 類型1
 *
 * @param hua
 */
public class PraiseWoodPager extends PraiseBasePager {


    public PraiseWoodPager(Context context, PraiseEntity praiseEntity) {
        super(context, praiseEntity);
    }

    @Override
    protected void setPriseType() {
        super.setPriseType();
        // 课清表扬榜
        if (mPraiseEntity.getPraiseType() == PraiseConfig.PRAISE_TYPE_PRACTICE) {
            tvTitle.setVisibility(View.GONE);
            ivTitle.setVisibility(View.VISIBLE);
            ivTitle.setBackgroundResource(R.drawable.bg_page_livevideo_praise_list_wood_title_1);
            // 口述题表扬榜
        } else if (mPraiseEntity.getPraiseType() == PraiseConfig.PRAISE_TYPE_TALK) {
            tvTitle.setVisibility(View.GONE);
            ivTitle.setVisibility(View.VISIBLE);
            ivTitle.setBackgroundResource(R.drawable.bg_page_livevideo_praise_list_wood_title_2);
            // 互动题表扬榜
        } else if (mPraiseEntity.getPraiseType() == PraiseConfig.PRAISE_TYPE_QUESITON) {
            tvTitle.setVisibility(View.GONE);
            ivTitle.setVisibility(View.VISIBLE);
            ivTitle.setBackgroundResource(R.drawable.bg_page_livevideo_praise_list_wood_title_3);
            // 其他表扬榜
        } else {
            tvTitle.setVisibility(View.VISIBLE);
            ivTitle.setVisibility(View.GONE);
            tvTitle.setText(mPraiseEntity.getPraiseName());
        }
    }

    @Override
    protected void setReslutType() {
        super.setReslutType();
        if(mPraiseEntity.getResultType() == PraiseConfig.RESULT_PRAISE){
            tvSubTitle.setTextColor(getColor(R.color.COLOR_FD6368));
            tvSubTitle.setText("恭喜入榜，再接再厉哦");
        } else {
            tvSubTitle.setTextColor(getColor(R.color.COLOR_70A1DE));
            tvSubTitle.setText("不要灰心，下次要上榜哦");
        }
    }
}
