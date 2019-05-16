package com.xueersi.parentsmeeting.modules.livevideo.primaryclass.business;

import android.app.Activity;

import com.xueersi.common.business.UserBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.http.PrimaryClassHttp;

public class PrimaryClassIrcBll extends LiveBaseBll {
    PrimaryClassHttp primaryClassHttp;

    public PrimaryClassIrcBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        String classId = getInfo.getStudentLiveInfo().getClassId();
        getPrimaryClassHttp().reportUserAppStatus(classId,getInfo.getStuId(),"1");
        getPrimaryClassHttp().getMyTeamInfo(classId,getInfo.getStuId(),UserBll.getInstance().getMyUserInfoEntity().getPsimId());
    }

    public PrimaryClassHttp getPrimaryClassHttp() {
        if (primaryClassHttp == null) {
            primaryClassHttp = new PrimaryClassHttp(getHttpManager());
        }
        return primaryClassHttp;
    }
}
