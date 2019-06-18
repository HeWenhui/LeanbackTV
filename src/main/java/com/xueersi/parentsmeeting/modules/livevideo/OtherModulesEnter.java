package com.xueersi.parentsmeeting.modules.livevideo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.xueersi.common.route.ReflexCenter;
import com.xueersi.parentsmeeting.modules.livevideo.activity.AuditClassLiveActivity;
import com.xueersi.parentsmeeting.modules.livevideo.activity.AuditClassRoomActivity;

/**
 * Created by lyqai on 2017/10/9.
 */

public class OtherModulesEnter {
    public static void intentTo(Activity mContext, String courseId, String groupId, String classId, String url) {
        ReflexCenter.invokeMethodWithParams("com.xueersi.parentsmeeting.modules.xesmall.XesMallEnter",
                "intentTo", new Class[]{Context.class, String.class, String.class, String.class}, new Object[]{mContext, courseId, groupId, classId, url});
    }

    public static void intentToAuditClassActivity(AuditClassLiveActivity auditClassLiveActivity, String mVSectionID, String stuCouId, Bundle bundle) {
//        ReflexCenter.invokeMethodWithParams("com.xueersi.parentsmeeting.modules.pschat.ChatEnter",
//                "intentToAuditClassActivity", new Class[]{Activity.class, String.class, String.class, Bundle.class}, new Object[]{auditClassLiveActivity, mVSectionID, stuCouId, bundle});
        AuditClassRoomActivity.intentTo(auditClassLiveActivity, mVSectionID, stuCouId, bundle);
    }

//    public static void intentToAuditClassActivity(com.xueersi.parentsmeeting.modules.livevideoOldIJK.activity.AuditClassLiveActivity auditClassLiveActivity, String mVSectionID, String stuCouId, Bundle bundle) {
////        ReflexCenter.invokeMethodWithParams("com.xueersi.parentsmeeting.modules.pschat.ChatEnter",
////                "intentToAuditClassActivity", new Class[]{Activity.class, String.class, String.class, Bundle.class}, new Object[]{auditClassLiveActivity, mVSectionID, stuCouId, bundle});
//        AuditClassRoomActivity.intentTo(auditClassLiveActivity, mVSectionID, stuCouId, bundle);
//    }


    public static void requestGoldTotal(Context mContext) {
        ReflexCenter.invokeMethodWithParams("com.xueersi.parentsmeeting.modules.personals.PersonalsEnter",
                "requestGoldTotal", new Class[]{Context.class}, new Object[]{mContext});
    }

    // 04.09 跳转订单支付页面
    public static void intentToOrderConfirmActivity(Activity activity, String courseIds, Integer productType, String whereFrom){
        ReflexCenter.invokeMethodWithParams("com.xueersi.parentsmeeting.modules.xesmall.XesMallEnter",
                "intentToOrderConfirmActivity", new Class[]{Context.class, String.class,Integer.TYPE,String.class}, new Object[]{activity, courseIds,productType,whereFrom});
    }


}
