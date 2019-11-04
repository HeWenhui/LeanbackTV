package com.xueersi.parentsmeeting.modules.livevideo.enter;

import android.app.Activity;
import android.os.Bundle;

import com.xueersi.common.util.LoadFileCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.LiveAssetsLoadUtil;
import com.xueersi.parentsmeeting.modules.livevideo.LiveVideoEnter;
import com.xueersi.parentsmeeting.modules.livevideo.activity.AIExperienceLiveVideoActivity;
import com.xueersi.parentsmeeting.modules.livevideo.activity.ExperienceActivity;
import com.xueersi.parentsmeeting.modules.livevideo.activity.ExperienceLiveVideoActivity;
import com.xueersi.parentsmeeting.modules.livevideo.activity.HalfBodyLiveExperienceActivity;

public class ExperEnter {
    public static boolean intentToLiveBackExperience(final Activity context, final Bundle bundle, final String where) {
        LiveAssetsLoadUtil.loadAssertsResource(context, new LoadFileCallBack() {
            @Override
            public void start() {

            }

            @Override
            public void success() {
//                ExperienceThreeScreenActivity.intentTo(context, bundle, where, VIDEO_REQUEST);
                ExperienceActivity.intentTo(context, bundle, where, LiveVideoEnter.VIDEO_REQUEST);
            }

            @Override
            public void progress(float progress, int type) {

            }

            @Override
            public void fail(int errorCode, String errorMsg) {

            }
        });
        return true;
    }

    /**
     * 跳转到半身直播体验课
     *
     * @param context
     * @param bundle
     */
    public static boolean intentToHalfBodyExperience(final Activity context, final Bundle bundle, final String where) {


        LiveAssetsLoadUtil.loadAssertsResource(context, new LoadFileCallBack() {
            @Override
            public void start() {

            }

            @Override
            public void success() {
                HalfBodyLiveExperienceActivity.intentTo(context, bundle, where, LiveVideoEnter.VIDEO_REQUEST);
            }

            @Override
            public void progress(float progress, int type) {

            }

            @Override
            public void fail(int errorCode, String errorMsg) {

            }
        });
        return true;
    }

    /**
     * 跳转到三分屏体验直播播放器
     *
     * @param context
     * @param bundle
     */
    public static boolean intentToExperience(final Activity context, final Bundle bundle, final String where) {


        LiveAssetsLoadUtil.loadAssertsResource(context, new LoadFileCallBack() {
            @Override
            public void start() {

            }

            @Override
            public void success() {
                ExperienceLiveVideoActivity.intentTo(context, bundle, where, LiveVideoEnter.VIDEO_REQUEST);
            }

            @Override
            public void progress(float progress, int type) {

            }

            @Override
            public void fail(int errorCode, String errorMsg) {

            }
        });
        return true;
    }

    /**
     * 跳转到三分屏AI体验直播播放器
     *
     * @param context
     * @param bundle
     */
    public static boolean intentToAIExperience(final Activity context, final Bundle bundle, final String where) {

        LiveAssetsLoadUtil.loadAssertsResource(context, new LoadFileCallBack() {
            @Override
            public void start() {

            }

            @Override
            public void success() {
                AIExperienceLiveVideoActivity.intentTo(context, bundle, where, LiveVideoEnter.VIDEO_REQUEST);
            }

            @Override
            public void progress(float progress, int type) {

            }

            @Override
            public void fail(int errorCode, String errorMsg) {

            }
        });

        return true;
    }
}
