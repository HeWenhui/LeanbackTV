package com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience.recommodcourse;

import android.content.Context;
import android.view.View;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;

public class RecommodCoursePager extends BasePager {

    public RecommodCoursePager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_stand_experience_recommond_course, null);

        return view;
    }

    @Override
    public void initData() {

    }
}
