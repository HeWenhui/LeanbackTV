package com.xueersi.parentsmeeting.modules.livevideo.enteampk.business;

import com.xueersi.common.base.AbstractBusinessDataCallBack;

public interface EnTeamPkHttp {
    @Deprecated
    void getSelfTeamInfo(AbstractBusinessDataCallBack abstractBusinessDataCallBack);

    void reportStuInfo(AbstractBusinessDataCallBack abstractBusinessDataCallBack);

    void updataEnglishPkGroup(AbstractBusinessDataCallBack abstractBusinessDataCallBack);

    void getEnglishPkGroup(AbstractBusinessDataCallBack abstractBusinessDataCallBack);
}
