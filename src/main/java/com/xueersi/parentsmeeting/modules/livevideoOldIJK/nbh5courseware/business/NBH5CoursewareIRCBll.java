package com.xueersi.parentsmeeting.modules.livevideoOldIJK.nbh5courseware.business;

import android.app.Activity;
import android.util.Log;
import android.widget.RelativeLayout;

import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.TopicAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.NbCourseWareEntity;

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
        Log.e("nbTrac","======>IRConTopic_old:"+jsonObject.toString());
        if (jsonObject.has("h5_Experiment")) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    initNbAction();
                    try {
                        JSONObject h5_Experiment = jsonObject.getJSONObject("h5_Experiment");
                        String play_url = h5_Experiment.optString("play_url");
                        String status = h5_Experiment.optString("status", "off");
                        if (StringUtils.isEmpty(play_url)) {
                            status = "off";
                        }
                        NbCourseWareEntity entity = new NbCourseWareEntity(mLiveId,play_url,false);
                        h5CoursewareAction.onH5Courseware(entity, status);


                    } catch (Exception e) {

                    }
                }
            });
        }else if(jsonObject.has("nb_Experiment")){
            //Nb 加实 Topic
            initNbAction();
            try {
                JSONObject nb_Experiment = jsonObject.getJSONObject("nb_Experiment");
                String status = nb_Experiment.optString("status", "off");
                String experimentId = nb_Experiment.optString("experimentId");
                String experimentType = nb_Experiment.optString("experimentType");
                NbCourseWareEntity entity = new NbCourseWareEntity(mLiveId,"",true);
                entity.setExperimentId(experimentId);
                entity.setExperimentType(experimentType);
                h5CoursewareAction.onH5Courseware(entity, status);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initNbAction() {
        if (h5CoursewareAction == null) {
            h5CoursewareAction = new H5CoursewareBll(activity,mGetInfo);
            h5CoursewareAction.setIsPlayback(false);
            h5CoursewareAction.initView(mRootView);
            h5CoursewareAction.setIRCMsgSender(this);
        }
    }

    @Override
    public void onNotice(String sourceNick, String target, final JSONObject object, int type) {
        Log.e("nbTrac","======>IRConNotice_old:"+type+":"+object.toString());
        switch (type) {
            case XESCODE.H5_START: {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        initNbAction();
                        try {
                            String play_url = object.getString("play_url");
                            NbCourseWareEntity entity = new NbCourseWareEntity(mLiveId,play_url,false);
                            h5CoursewareAction.onH5Courseware(entity, "on");
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
                        initNbAction();
                        try {
                            NbCourseWareEntity entity = new NbCourseWareEntity(mLiveId,"",false);
                            h5CoursewareAction.onH5Courseware(entity, "off");
                        } catch (Exception e) {

                        }
                    }
                });
            }
            break;
            //NB加试
            case XESCODE.NB_EXAM:
                  initNbAction();
                  String experimentId = object.optString("experimentId");
                  String experimentType = object.optString("experimentType");
                  String status = object.optString("status");
                  NbCourseWareEntity entity = new NbCourseWareEntity(mLiveId,"",true);
                  entity.setExperimentId(experimentId);
                  entity.setExperimentType(experimentType);
                  h5CoursewareAction.onH5Courseware(entity, status);
                break;
            default:
                break;
        }
    }

    @Override
    public int[] getNoticeFilter() {
        return new int[]{XESCODE.H5_START, XESCODE.H5_STOP,XESCODE.NB_EXAM};
    }

    @Override
    public void initView(RelativeLayout bottomContent, AtomicBoolean mIsLand) {
        if (h5CoursewareAction != null) {
            h5CoursewareAction.initView(bottomContent);
        }
    }

    @Override
    public void onDestory() {
        super.onDestory();
        if(h5CoursewareAction != null){
            h5CoursewareAction.onDestory();
        }
    }
}
