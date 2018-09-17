package com.xueersi.parentsmeeting.modules.livevideo.praiselist.page;

import android.content.Context;
import android.view.View;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.business.PraiseInteractionBll;

/**
 * 初高中点赞互动
 */

public class PraiseInteractionPager extends BasePager {


    public PraiseInteractionPager(Context context, PraiseInteractionBll praiseInteractionBll) {
        super(context);
    }


    @Override
    public View initView() {
        final View view = View.inflate(mContext, R.layout.page_livevideo_praise_interaction, null);

        return view;
    }


    @Override
    public void initData() {
    }


    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
