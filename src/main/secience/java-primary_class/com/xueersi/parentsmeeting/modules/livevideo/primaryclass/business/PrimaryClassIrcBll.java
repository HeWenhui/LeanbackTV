package com.xueersi.parentsmeeting.modules.livevideo.primaryclass.business;

import android.app.Activity;
import android.widget.RelativeLayout;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.business.UserBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.http.PrimaryClassHttp;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.pager.PrimaryItemPager;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.pager.PrimaryItemView;

import java.util.concurrent.atomic.AtomicBoolean;

public class PrimaryClassIrcBll extends LiveBaseBll {
    PrimaryClassHttp primaryClassHttp;
    PrimaryItemView primaryItemView;

    public PrimaryClassIrcBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        String classId = getInfo.getStudentLiveInfo().getClassId();
        getPrimaryClassHttp().reportUserAppStatus(classId, getInfo.getStuId(), "1");
        getPrimaryClassHttp().getMyTeamInfo(classId, getInfo.getStuId(), UserBll.getInstance().getMyUserInfoEntity().getPsimId(), new AbstractBusinessDataCallBack() {
            @Override
            public void onDataSucess(Object... objData) {

            }
        });
    }

    @Override
    public void initView(RelativeLayout bottomContent, AtomicBoolean mIsLand) {
        super.initView(bottomContent, mIsLand);
        PrimaryItemPager primaryItemPager = new PrimaryItemPager(activity, mContentView);
        rlMessageBottom.addView(primaryItemPager.getRootView());
        primaryItemView = primaryItemPager;
    }

    public PrimaryClassHttp getPrimaryClassHttp() {
        if (primaryClassHttp == null) {
            primaryClassHttp = new PrimaryClassHttp(getHttpManager());
        }
        return primaryClassHttp;
    }
}
