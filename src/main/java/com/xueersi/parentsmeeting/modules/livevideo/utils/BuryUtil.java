package com.xueersi.parentsmeeting.modules.livevideo.utils;

import android.app.Activity;

import com.xrs.bury.xrsbury.XrsBury;
import com.xueersi.common.data.AppCacheData;

/**
 * Created by dqq on 2019/5/31.
 */
public class BuryUtil {

    private static final String TAG = "BuryUtil";

    public static void show(int strBuryId, Object params) {
        Activity activity = AppCacheData.getHomeActivity();
        try {
            if (activity != null) {
                XrsBury.showBury(activity.getResources().getString(strBuryId), params);
            }
        } catch (Exception e) {
            e.printStackTrace();
//            MobAgent.httpResponseParserError(TAG, "show", e.getMessage());
        }
    }

    public static void show(int strBuryId, Object... params) {
        Activity activity = AppCacheData.getHomeActivity();
        try {
            if (activity != null) {
                XrsBury.showBury(activity.getResources().getString(strBuryId), params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void pageStartBury(int strBuryId, Object... params) {
        Activity activity = AppCacheData.getHomeActivity();
        try {
            if (activity != null) {
                XrsBury.pageStartBury(activity.getResources().getString(strBuryId), params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void pageEndBury(int strBuryId, Object... params) {
        Activity activity = AppCacheData.getHomeActivity();
        try {
            if (activity != null) {
                XrsBury.pageEndBury(activity.getResources().getString(strBuryId), params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void click(int strBuryId, Object... params) {
        Activity activity = AppCacheData.getHomeActivity();
        try {
            if (activity != null) {
                XrsBury.clickBury(activity.getResources().getString(strBuryId), params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
