package com.xueersi.parentsmeeting.modules.livevideo.EvaluateTeacher.bussiness;

import android.app.Activity;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by：WangDe on 2018/11/27 16:12
 */
public class EvaluateTeacherBll extends LiveBaseBll {

    public EvaluateTeacherBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
    }

    @Override
    public void initView(RelativeLayout bottomContent, AtomicBoolean mIsLand) {
        super.initView(bottomContent, mIsLand);
    }
}
