package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StudyInfo;

/**
 * 半身直播  家长旁听 业务bll
 *
 * @author chenkun
 * @version 1.0, 2018/10/29 下午4:04
 */

public class HalfBodyAuditClassBll implements AuditClassAction {


    private TextView tvCheckTime;
    private TextView tvOnlineTime;
    private RecyclerView rclAnwerState;

    public HalfBodyAuditClassBll(Activity activity){

      initView(activity);


    }

    private void initView(Activity activity) {

        tvCheckTime = activity.findViewById(R.id.tv_livevideo_student_check_time);
        tvOnlineTime = activity.findViewById(R.id.tv_livevideo_student_online);
        rclAnwerState = activity.findViewById(R.id.rcl_livevideo_auditclass_anwser_state);

    }

    @Override
    public void onGetStudyInfo(StudyInfo studyInfo) {

        tvCheckTime.setText(studyInfo.getSignTime());
        tvOnlineTime.setText(studyInfo.getOnlineTime());
    }
}
