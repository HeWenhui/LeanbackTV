package com.xueersi.parentsmeeting.modules.livevideo.primaryclass.business;

import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamMate;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.entity.TeamMember;

public interface PrimaryClassInter {
    void reportNaughtyBoy(TeamMate entity, ReportNaughtyBoy reportNaughtyBoy);

    public interface ReportNaughtyBoy {
        void onReport(TeamMate entity);

        void onReportError(TeamMate entity);
    }
}
