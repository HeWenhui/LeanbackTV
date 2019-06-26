package com.xueersi.parentsmeeting.modules.livevideoOldIJK.business;

import android.app.Activity;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StudyInfo;

/**
 * Created by linyuqiang on 2017/7/21.
 */

public class AuditClassBll implements AuditClassAction {
    TextView tv_livevideo_student_check_time;
    TextView tv_livevideo_student_online;
    TextView tv_livevideo_student_radio;
    TextView tv_livevideo_student_team;

    public AuditClassBll(Activity activity) {
        tv_livevideo_student_check_time = (TextView) activity.findViewById(R.id.tv_livevideo_student_check_time);
        tv_livevideo_student_online = (TextView) activity.findViewById(R.id.tv_livevideo_student_online);
        tv_livevideo_student_radio = (TextView) activity.findViewById(R.id.tv_livevideo_student_radio);
        tv_livevideo_student_team = (TextView) activity.findViewById(R.id.tv_livevideo_student_team);
    }

    public void onGetStudyInfo(StudyInfo studyInfo) {
        tv_livevideo_student_check_time.setText(studyInfo.getSignTime());
        tv_livevideo_student_online.setText(studyInfo.getOnlineTime());
        tv_livevideo_student_radio.setText(studyInfo.getTestRate());
        tv_livevideo_student_team.setText(studyInfo.getMyRank());
    }
}
