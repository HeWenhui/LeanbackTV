package com.xueersi.parentsmeeting.modules.livevideo.utils;

import android.app.Activity;
import android.content.Context;

import com.xrs.bury.xrsbury.XrsBury;
import com.xueersi.common.base.BaseApplication;
import com.xueersi.common.data.AppCacheData;

/**
 * Created by dqq on 2019/5/31.
 */
public class BuryUtil {

    private static final String TAG = "BuryUtil";

    public static void show(int strBuryId, Object params) {
        Context context = BaseApplication.getContext();
        try {
            if (context != null) {
                XrsBury.showBury(context.getResources().getString(strBuryId), params);
            }
        } catch (Exception e) {
            e.printStackTrace();
//            MobAgent.httpResponseParserError(TAG, "show", e.getMessage());
        }
    }

    public static void show(int strBuryId, Object... params) {
        Context context = BaseApplication.getContext();
        try {
            if (context != null) {
                XrsBury.showBury(context.getResources().getString(strBuryId), params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void pageStartBury(int strBuryId, Object... params) {
        Context context = BaseApplication.getContext();
        try {
            if (context != null) {
                XrsBury.pageStartBury(context.getResources().getString(strBuryId), params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void pageEndBury(int strBuryId, Object... params) {
        Context context = BaseApplication.getContext();
        try {
            if (context != null) {
                XrsBury.pageEndBury(context.getResources().getString(strBuryId), params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void click(int strBuryId, Object... params) {
        Context context = BaseApplication.getContext();
        try {
            if (context != null) {
                XrsBury.clickBury(context.getResources().getString(strBuryId), params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
