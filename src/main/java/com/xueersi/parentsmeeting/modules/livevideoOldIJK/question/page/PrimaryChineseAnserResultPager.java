package com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.page;

import android.content.Context;
import android.view.View;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.page.LiveBasePager;

/**
 * Created by ZhangYuansun on 2019/3/6
 * <p>
 * 小学语文 互动题结果页
 */
public class PrimaryChineseAnserResultPager extends LiveBasePager {
    public PrimaryChineseAnserResultPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.page_livevideo_primaryscience_anwserresult, null);
        return mView;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {

    }
}
