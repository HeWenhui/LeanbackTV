package com.xueersi.parentsmeeting.modules.livevideo.primaryclass.business;

import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.entity.TeamMember;

public interface PrimaryClassInter {
    void reportNaughtyBoy(TeamMember entity, ReportNaughtyBoy reportNaughtyBoy);

    public interface ReportNaughtyBoy {
        void onReport(TeamMember entity);
    }
}
