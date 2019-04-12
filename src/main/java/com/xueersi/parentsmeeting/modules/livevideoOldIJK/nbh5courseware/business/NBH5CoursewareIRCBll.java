package com.xueersi.parentsmeeting.modules.livevideoOldIJK.nbh5courseware.business;

import android.app.Activity;
import android.widget.RelativeLayout;

import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.TopicAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;

import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by lyqai on 2018/7/6.
 * nb实验网页
 */
public class NBH5CoursewareIRCBll extends LiveBaseBll implements NoticeAction, TopicAction {
    H5CoursewareBll h5CoursewareAction;

    public NBH5CoursewareIRCBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
    }

    @Override
    public void onTopic(LiveTopic liveTopic, final JSONObject jsonObject, boolean modeChange) {
        if (jsonObject.has("h5_Experiment")) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (h5CoursewareAction == null) {
                        h5CoursewareAction = new H5CoursewareBll(activity);
                        h5CoursewareAction.initView(mRootView);
                    }
                    try {
                        JSONObject h5_Experiment = jsonObject.getJSONObject("h5_Experiment");
                        String play_url = h5_Experiment.optString("play_url");
                        String status = h5_Experiment.optString("status", "off");
                        if (StringUtils.isEmpty(play_url)) {
                            status = "off";
                        }
                        h5CoursewareAction.onH5Courseware(play_url, status);
                    } catch (Exception e) {

                    }
                }
            });
        }
    }

    @Override
    public void onNotice(String sourceNick, String target, final JSONObject object, int type) {
        switch (type) {
            case XESCODE.H5_START: {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (h5CoursewareAction == null) {
                            h5CoursewareAction = new H5CoursewareBll(activity);
                            h5CoursewareAction.initView(mRootView);
                        }
                        try {
                            String play_url = object.getString("play_url");
                            h5CoursewareAction.onH5Courseware(play_url, "on");
                        } catch (Exception e) {

                        }
                    }
                });
            }
            break;
            case XESCODE.H5_STOP: {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (h5CoursewareAction == null) {
                            h5CoursewareAction = new H5CoursewareBll(activity);
                            h5CoursewareAction.initView(mRootView);
                        }
                        try {
                            h5CoursewareAction.onH5Courseware("", "off");
                        } catch (Exception e) {

                        }
                    }
                });
            }
            break;
            default:
                break;
        }
    }

    @Override
    public int[] getNoticeFilter() {
        return new int[]{XESCODE.H5_START, XESCODE.H5_STOP};
    }

    @Override
    public void initView(RelativeLayout bottomContent, AtomicBoolean mIsLand) {
        if (h5CoursewareAction != null) {
            h5CoursewareAction.initView(bottomContent);
        }
    }

}
