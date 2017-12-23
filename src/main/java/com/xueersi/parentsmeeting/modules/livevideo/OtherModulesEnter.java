package com.xueersi.parentsmeeting.modules.livevideo;

import android.app.Activity;
import android.content.Context;

import com.xueersi.parentsmeeting.modules.livevideo.activity.AuditClassLiveActivity;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoActivity;
import com.xueersi.parentsmeeting.route.ReflexCenter;

/**
 * Created by lyqai on 2017/10/9.
 */

public class OtherModulesEnter {
    public static void intentTo(Activity mContext, String courseId, String groupId, String classId, String url) {
        ReflexCenter.invokeMethodWithParams("com.xueersi.parentsmeeting.modules.xesmall.XesMallEnter",
                "intentTo", new Class[]{Context.class, String.class, String.class, String.class}, new Object[]{mContext, courseId, groupId, classId, url});
    }

    public static void intentToAuditClassActivity(AuditClassLiveActivity auditClassLiveActivity, String mVSectionID) {
        ReflexCenter.invokeMethodWithParams("com.xueersi.parentsmeeting.modules.chat.ChatEnter",
                "intentToAuditClassActivity", new Class[]{Activity.class, String.class}, new Object[]{auditClassLiveActivity, mVSectionID});
    }

    public static void intentToGradeActivityLive(Activity activity, String selectGrade) {
        ReflexCenter.invokeMethodWithParams("com.xueersi.parentsmeeting.modules.xesmall.XesMallEnter",
                "intentToGradeActivityLive", new Class[]{Context.class, String.class}, new Object[]{activity, selectGrade});
    }

    public static void requestGoldTotal(Context mContext) {
        ReflexCenter.invokeMethodWithParams("com.xueersi.parentsmeeting.modules.personals.PersonalsEnter",
                "requestGoldTotal", new Class[]{Context.class}, new Object[]{mContext});
    }
}
