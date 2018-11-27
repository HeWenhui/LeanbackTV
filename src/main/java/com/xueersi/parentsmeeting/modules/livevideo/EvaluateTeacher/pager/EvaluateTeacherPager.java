package com.xueersi.parentsmeeting.modules.livevideo.EvaluateTeacher.pager;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;

/**
 * Created by：WangDe on 2018/11/27 15:58
 */
public class EvaluateTeacherPager extends LiveBasePager {
    public EvaluateTeacherPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.pop_livevideo_evaluate_teacher,null);
        TextView textView = mView.findViewById(R.id.textView);
        return mView;
    }

    @Override
    public void initData() {

    }

    @Override
    public boolean onUserBackPressed() {
        return super.onUserBackPressed();
    }
}
