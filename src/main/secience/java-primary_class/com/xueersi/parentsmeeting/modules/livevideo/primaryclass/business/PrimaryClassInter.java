package com.xueersi.parentsmeeting.modules.livevideo.primaryclass.business;

import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamMate;

public interface PrimaryClassInter {
    void reportNaughtyBoy(TeamMate entity, ReportNaughtyBoy reportNaughtyBoy);

    void getMyTeamInfo();

    public interface ReportNaughtyBoy {
        void onReport(TeamMate entity);

        void onReportError(TeamMate entity);
    }
}
