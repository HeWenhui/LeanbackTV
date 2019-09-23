package com.xueersi.parentsmeeting.modules.livevideo.nbh5courseware.business;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.ActivityChangeLand;
import com.xueersi.parentsmeeting.modules.livevideo.business.ContextLiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.config.NbCourseWareConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LivePagerBack;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveAppUserInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.NbCourseWareEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.NbLoginEntity;
import com.xueersi.parentsmeeting.modules.livevideo.event.NbCourseEvent;
import com.xueersi.parentsmeeting.modules.livevideo.http.NBHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.NbHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.nbh5courseware.pager.NbH5CoursewareX5Pager;
import com.xueersi.parentsmeeting.modules.livevideo.nbh5courseware.pager.NbH5ExamX5Pager;
import com.xueersi.parentsmeeting.modules.livevideo.nbh5courseware.pager.NbH5FreeX5Pager;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.NbCourseLog;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveMainHandler;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.ui.dialog.VerifyCancelAlertDialog;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

/**
 * Created by linyuqiang on 2017/3/25.
 * h5课件业务类
 */
public class H5CoursewareBll implements H5CoursewareAction, LivePagerBack, NbPresenter {
    String TAG = "H5CoursewareBll";
    Context context;
    Handler handler = LiveMainHandler.getMainHandler();
    NbH5PagerAction h5CoursewarePager;

    private LogToFile logToFile;
    RelativeLayout bottomContent;
    ActivityChangeLand activityChangeLand;

    private NBHttpManager mNbHttpManager;
    /**
     * 乐步登录信息
     **/
    private NbLoginEntity mNbLoginEntity;

    private String mLiveId = "";

    private String stuCouId = "";

    /** 乐不方登录token **/
    private String nbToken = "";

    /** 当前加实 id **/
    private NbCourseWareEntity mNbCourseInfo;
    /** 加试信息结果回调 **/
    private HttpCallBack mTestInfoCallBack;
    /**
     * 当前实验id
     */
    private String mExperimentId;

    /**
     * 是否是回放
     **/
    private boolean isPlayback;
    private View endTipView;

    private LiveAndBackDebug liveAndBackDebug;
    private LiveBaseBll mMsgSender;
    private final LiveGetInfo mRoomInitData;

    public H5CoursewareBll(Context context, LiveGetInfo roomInitData) {
        logToFile = new LogToFile(context, TAG);
        this.context = context;
        activityChangeLand = ProxUtil.getProxUtil().get(context, ActivityChangeLand.class);
        mNbHttpManager = new NBHttpManager(context);
        mLiveId = roomInitData.getId();
        stuCouId = roomInitData.getStuCouId();
        liveAndBackDebug = new ContextLiveAndBackDebug(context);
        mRoomInitData = roomInitData;
    }

    public void setIsPlayback(boolean isPlayback) {
        this.isPlayback = isPlayback;
    }

    public boolean isPlayback() {
        return isPlayback;
    }


    private void nBLogin() {
        final String userid = LiveAppUserInfo.getInstance().getStuId();
        String nickName = LiveAppUserInfo.getInstance().getNickName();
        mNbHttpManager.nbLogin(mLiveId, userid, nickName, NbCourseWareConfig.USER_TYPE_STU, new HttpCallBack() {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                mNbLoginEntity = NbHttpResponseParser.parseNbLogin(responseEntity);
                nbToken = mNbLoginEntity.getToken();
                if (mNbCourseInfo != null && mTestInfoCallBack != null) {
                    mNbCourseInfo.setNbToken(nbToken);
                    getNBTestInfo(mNbCourseInfo, mTestInfoCallBack);
                }
                NbCourseLog.nbLogin(liveAndBackDebug, "1", "");
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
                if (mNbCourseInfo != null && mTestInfoCallBack != null) {
                    mTestInfoCallBack.onPmFailure(error, msg);
                }
                NbCourseLog.nbLogin(liveAndBackDebug, "0", "nb_登录失败");
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                super.onPmError(responseEntity);
                if (mNbCourseInfo != null && mTestInfoCallBack != null) {
                    mTestInfoCallBack.onPmError(responseEntity);
                }
                NbCourseLog.nbLogin(liveAndBackDebug, "0", "nb_登录失败");
            }
        });
    }


    public void initView(RelativeLayout bottomContent) {
        this.bottomContent = bottomContent;
        if (h5CoursewarePager != null) {
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
                    .LayoutParams.MATCH_PARENT);
            bottomContent.addView(h5CoursewarePager.getRootView(), lp);
        }
    }


    @Override
    public void onH5Courseware(final NbCourseWareEntity entity, final String status) {

        handler.post(new Runnable() {

            @Override
            public void run() {
                if ("on".equals(status)) {

                    if (h5CoursewarePager != null) {
                        if (entity.isNbExperiment() == NbCourseWareEntity.NB_ADD_EXPERIMENT) {
                            //NB 加试 实验 比较id 是否相同 去重(notice和topic)
                            if (entity.getExperimentId() != null && entity.getExperimentId().equals(mExperimentId)) {
                                logToFile.i("onH5Courseware:mExperimentId.equals");
                                return;
                            } else {
                                bottomContent.removeView(h5CoursewarePager.getRootView());
                            }
                        } else {
                            //普通实验比较 Url 是否相同 去重(notice和topic)
                            if (h5CoursewarePager.getUrl().equals(entity.getUrl())) {
                                logToFile.i("onH5Courseware:url.equals");
                                return;
                            } else {
                                logToFile.i("onH5Courseware:url=" + h5CoursewarePager.getUrl());
                                bottomContent.removeView(h5CoursewarePager.getRootView());
                            }
                        }
                    }

                    if (entity.isNbExperiment() == NbCourseWareEntity.NB_ADD_EXPERIMENT) {
                        entity.setNbToken(nbToken);
                        mExperimentId = entity.getExperimentId();
                        h5CoursewarePager = new NbH5ExamX5Pager(context, entity, H5CoursewareBll.this,
                                H5CoursewareBll.this);
                        NbCourseLog.sno2(liveAndBackDebug, entity.getExperimentId(), "on");
                        NbCourseLog.reciveStartCmd(liveAndBackDebug, entity.getExperimentId());
                    } else if (entity.isNbExperiment() == NbCourseWareEntity.NB_FREE_EXPERIMENT) {
                        entity.setNbToken(nbToken);
                        mExperimentId = entity.getExperimentId();
                        h5CoursewarePager = new NbH5FreeX5Pager(context, entity, H5CoursewareBll.this,
                                H5CoursewareBll.this);
                        NbCourseLog.sno2(liveAndBackDebug, entity.getExperimentId(), "on");
                        NbCourseLog.reciveStartCmd(liveAndBackDebug, entity.getExperimentId());
                    } else {
                        h5CoursewarePager = new NbH5CoursewareX5Pager(context, entity, H5CoursewareBll.this);
                    }
                    ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT);
                    bottomContent.addView(h5CoursewarePager.getRootView(), lp);
                    if (activityChangeLand != null) {
                        activityChangeLand.setAutoOrientation(false);
                        Activity activity = (Activity) context;
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    }
                } else {
                    if (h5CoursewarePager != null) {
                        //线上老版本 物理实现处理逻辑
                        if (activityChangeLand != null) {
                            activityChangeLand.setAutoOrientation(true);
                        }
                        if (entity.isNbExperiment() == NbCourseWareEntity.NB_ADD_EXPERIMENT) {
                            NbCourseLog.sno2(liveAndBackDebug, entity.getExperimentId(), "off");
                            NbCourseLog.reciveEndCmd(liveAndBackDebug, entity.getExperimentId());
                            //提交答案
                            h5CoursewarePager.submitData();
                            showEndTip();
                        } else {
                          /*  bottomContent.removeView(h5CoursewarePager.getRootView());
                            h5CoursewarePager.destroy();
                            h5CoursewarePager = null;*/
                            closePager();
                        }
                    }
                }
            }
        });
    }

    /**
     * 显示实验结束UI
     */
    private void showEndTip() {
        if (endTipView == null) {
            endTipView = View.inflate(context, R.layout.page_livevideo_nb_endtip, null);
        }
        if (endTipView.getParent() == null) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            bottomContent.addView(endTipView, params);
            bottomContent.postDelayed(new Runnable() {
                @Override
                public void run() {
                    closeEndTip();
                }
            }, 2000);
        }
    }

    private void closeEndTip() {
        if (endTipView != null) {
            bottomContent.removeView(endTipView);
        }
    }

    @Override
    public void uploadNbResult(String resultStr, String isForce, HttpCallBack requestCallBack) {
        mNbHttpManager.upLoadNbReuslt(mLiveId, LiveAppUserInfo.getInstance().getStuId(),
                stuCouId, resultStr,
                isForce, isPlayback ? "1" : "0", requestCallBack);
    }


    @Override
    public void getNBTestInfo(NbCourseWareEntity testInfo, HttpCallBack requestCallBack) {
        mNbCourseInfo = testInfo;
        mTestInfoCallBack = requestCallBack;
        if (!TextUtils.isEmpty(nbToken)) {
            if (mNbCourseInfo.isNbExperiment() == NbCourseWareEntity.NB_ADD_EXPERIMENT) {
                mNbHttpManager.getNbTestInfo(mLiveId, LiveAppUserInfo.getInstance().getStuId(),
                        mNbCourseInfo.getExperimentId(), nbToken, requestCallBack);
            } else {
                if (requestCallBack != null && h5CoursewarePager != null) {
                    h5CoursewarePager.loadUrl();
                }
            }
        } else {
            nBLogin();
        }
    }

    @Override
    public void closePager() {
        if (h5CoursewarePager != null) {
            h5CoursewarePager.destroy();
            bottomContent.removeView(h5CoursewarePager.getRootView());
            h5CoursewarePager = null;
            EventBus.getDefault().post(new NbCourseEvent(NbCourseEvent.EVENT_TYPE_NBH5_CLOSE));
        }
    }

//    @Override
//    public void login(NbCourseWareEntity testInfo, HttpCallBack requestCallBack) {
//        mNbCourseInfo = testInfo;
//        if (TextUtils.isEmpty(nbToken)) {
//            nBLogin();
//        }
//    }

    @Override
    public void sendSubmitSuccessMsg(String stuId, String experimentId) {
        if (mMsgSender != null) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", "" + XESCODE.NB_ADDEXPERIMENT_SUBMIT_SUCCESS);
                jsonObject.put("stuId", LiveAppUserInfo.getInstance().getStuId());
                jsonObject.put("experimentId", mExperimentId);
                mMsgSender.sendNoticeToMain(jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBack(LiveBasePager liveBasePager) {
        //页面本身不消费 系统返回键点击时间
        if (h5CoursewarePager != null && !h5CoursewarePager.onBack()) {
            VerifyCancelAlertDialog cancelDialog = new VerifyCancelAlertDialog(context, ContextManager.getApplication(), false,
                    VerifyCancelAlertDialog.TITLE_MESSAGE_VERIRY_CANCEL_TYPE);
            cancelDialog.setVerifyBtnListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closePager();
                    NbCourseLog.clickCloseExperiment(liveAndBackDebug, mExperimentId, isPlayback);
                }
            });
            cancelDialog.initInfo("确定关闭实验吗?", "关闭后将回到直播间,无法重进");
            cancelDialog.showDialog();
        }
    }

    public void onDestroy() {

    }


    /**
     * 设置聊天消息发送器
     */
    public void setIRCMsgSender(LiveBaseBll sender) {
        mMsgSender = sender;
    }

}

