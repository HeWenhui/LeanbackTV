package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.content.Context;
import android.util.Log;

/**
 * 半身直播家长旁听核心业务类 ：处理 连接聊天服务器,视频播放
 * @author chenkun
 * @version 1.0, 2018/10/29 下午2:11
 */

public class HalfBodyAuditClassLiveBll extends AuditClassLiveBll {

    private static final String Tag = "HalfBodyAuditClassLiveBll";

    public HalfBodyAuditClassLiveBll(Context context, String vStuCourseID, String courseId, String vSectionID, int
            form) {
        super(context, vStuCourseID, courseId, vSectionID, form);
    }


    @Override
    public synchronized void getStudentLiveInfo() {
        super.getStudentLiveInfo();
        // TODO: 2018/10/29  获取学生当前学习状态 信息
        Log.e(Tag,"=======>getStudentLiveInfo called");

    }
}
