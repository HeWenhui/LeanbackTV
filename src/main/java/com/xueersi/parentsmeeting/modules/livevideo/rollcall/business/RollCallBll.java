package com.xueersi.parentsmeeting.modules.livevideo.rollcall.business;

import android.app.Activity;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.common.business.UserBll;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.logerhelper.MobAgent;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoActivity;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.business.WeakHandler;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassSignEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassmateEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.rollcall.page.ClassSignPager;
import com.xueersi.parentsmeeting.modules.livevideo.rollcall.page.ClassmateSignPager;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

/**
 * @author linyuqiang
 * Created by linyuqiang on 2016/9/23.
 */
public class RollCallBll extends LiveBaseBll implements NoticeAction, RollCallAction, Handler.Callback {


    /**
     * 可签到时间  课前15 分钟
     */
    private static final int SIGN_AVALIABLE_TIME_RANG = 15 * 60 * 1000;

    private static final String TAG = "RollCallBll";
    /**
     * 控制点名滚动功能
     */
    public static final boolean IS_SHOW_CLASSMATE_SIGN = true;

    /**
     * 自动显示签到面板（不再由教师端 发起）
     */
    private boolean autoSign = false;

    /**
     * 是否开启自动签到功能
     */
    public static final boolean OPEN_AUTO_SIGN = true;

    private Activity activity;

    private WeakHandler mVPlayVideoControlHandler = new WeakHandler(this);
    /**
     * 停止点名
     */
    private ClassmateSignPager.ClassSignStop classSignStop = new ClassmateSignPager.ClassSignStop();
    /**
     * 同学点名签到
     */
    private ClassmateSignPager classmateSignPager;
    /**
     * 点名
     */
    private ClassSignPager mClassSignPager;
    private LogToFile mLogtf;
    /**
     * 显示学生签到
     */
    private static final int SHOW_USERSIGN = 6;
    /**
     * 隐藏学生签到
     */
    private static final int NO_USERSIGN = 7;
    /**
     * 当前是否正在显示签到
     */
    private boolean mIsShowUserSign = false;
    /**
     * 点名的布局
     */
    private RelativeLayout rlRollCallContent;
    /**
     * 时间点检测轮询 时间间隔
     */
    private static final long TIME_WATHER_DELAY = 3000;

    /**
     * 自动签到模式时  签到页面自动显示 延时
     */
    private long autoShowSignDelay;
    /**
     * 自动签到模式时  签到页面自动关闭 延时
     */
    private long autoCloseSignDelay;


    private LiveGetInfo mGetInfo;


    public RollCallBll(Activity context, LiveBll2 liveBll, RelativeLayout rootView) {
        super(context, liveBll, rootView);
        this.activity = context;

    }


    //todo 后期删除

    public RollCallBll(Activity activity) {
        this(activity, null, null);
        mLogtf = new LogToFile(TAG, new File(Environment.getExternalStorageDirectory(), "parentsmeeting/log/" + TAG
                + ".txt"));
        mLogtf.clear();
        this.activity = activity;
    }



    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case SHOW_USERSIGN: {
                break;
            }
            case NO_USERSIGN: {
                break;
            }
            default:
                break;
        }
        return false;
    }

    int count = 0;


    public void initView(final RelativeLayout bottomContent) {
        Loger.e("RollCallBll", "======>:bottomContent" + bottomContent + ":" + mRootView);
        mVPlayVideoControlHandler.post(new Runnable() {
            @Override
            public void run() {
                int screenWidth = ScreenUtils.getScreenWidth();
                int wradio = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * screenWidth / LiveVideoActivity.VIDEO_WIDTH);
                rlRollCallContent = new RelativeLayout(activity);
                bottomContent.addView(rlRollCallContent, new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                        .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                //点名
                RelativeLayout rlClassmateContent = new RelativeLayout(activity);
                classmateSignPager = new ClassmateSignPager(activity);
                classmateSignPager.setClassSignStop(classSignStop);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
                        .WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.rightMargin = wradio;
                if (IS_SHOW_CLASSMATE_SIGN) {
                    classmateSignPager.start();
                    rlClassmateContent.addView(classmateSignPager.getRootView(), params);
                }
                bottomContent.addView(rlClassmateContent, new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                        .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        });
    }

    public void onPlayOpenSuccess(ViewGroup.LayoutParams lp) {
        if (IS_SHOW_CLASSMATE_SIGN && classmateSignPager != null) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) classmateSignPager.getRootView()
                    .getLayoutParams();
            int screenWidth = ScreenUtils.getScreenWidth();
            int wradio = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * screenWidth / LiveVideoActivity.VIDEO_WIDTH) +
                    (screenWidth - lp.width) / 2;
            params.rightMargin = wradio;
            LayoutParamsUtil.setViewLayoutParams(classmateSignPager.getRootView(), params);
        }
    }

    public void onModeChange(final String mode, final boolean isPresent) {
        if (LiveTopic.MODE_CLASS.equals(mode)) {
            classSignStop.setTraning(false);
            if (classmateSignPager != null) {
                classmateSignPager.stop();
            }
        } else {
            classSignStop.setTraning(true);
        }
    }

    @Override
    public void onRollCall(boolean stop) {
        if (!autoSign) {
            classSignStop.setStopSign(stop);
            if (stop && classmateSignPager != null) {
                classmateSignPager.stop();
            }
        }

    }

    @Override
    public void onRollCall(final ClassSignEntity classSignEntity) {
        if (!autoSign) {
            mVPlayVideoControlHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mClassSignPager != null) {
                        mClassSignPager.updateStatus(classSignEntity.getStatus());
                        return;
                    }
                    mIsShowUserSign = true;
                    mClassSignPager = new ClassSignPager(activity, RollCallBll.this, classSignEntity, mLiveBll);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
                            .WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.CENTER_IN_PARENT);
                    rlRollCallContent.addView(mClassSignPager.getRootView(), params);
                    activity.getWindow().getDecorView().requestLayout();
                    activity.getWindow().getDecorView().invalidate();
                }
            });
            mVPlayVideoControlHandler.sendEmptyMessage(SHOW_USERSIGN);
        }
    }


    @Override
    public void onClassmateRollCall(final ClassmateEntity classmateEntity) {
        if (classSignStop.getStopSign() || !classSignStop.isTraning()) {
            return;
        }
        mVPlayVideoControlHandler.post(new Runnable() {
            @Override
            public void run() {
                if (classmateSignPager != null) {
                    classmateSignPager.addClassmage(classmateEntity);
                }
            }
        });
    }

    @Override
    public void stopRollCall() {
        mIsShowUserSign = false;
        mVPlayVideoControlHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mClassSignPager != null) {
                    rlRollCallContent.removeView(mClassSignPager.getRootView());
                    mClassSignPager = null;
                }
            }
        });
        mVPlayVideoControlHandler.sendEmptyMessage(NO_USERSIGN);
    }

    @Override
    public void forceCloseRollCall() {
        closeUserSign();
    }


    @Override
    public void userSign(ClassSignEntity classSignEntity, final HttpCallBack callBack){
        if (classSignEntity.getStatus() != 1) {
            stopRollCall();
        }else{

            String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
            String classId = "";
            if (mGetInfo.getStudentLiveInfo() != null) {
                classId = mGetInfo.getStudentLiveInfo().getClassId();
            }

           getHttpManager().userSign(enstuId, mLiveBll.getLiveId(), classId, mGetInfo.getTeacherId()
                   , new HttpCallBack(false) {
               @Override
               public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                   mGetInfo.getStudentLiveInfo().setSignStatus(2);
                   callBack.onPmSuccess(responseEntity);

                   try {
                       mGetInfo.getStudentLiveInfo().setSignStatus(2);
                       JSONObject jsonObject = new JSONObject();
                       jsonObject.put("type", "" + XESCODE.CLASS_MATEROLLCALL);
                       jsonObject.put("id", "" + mGetInfo.getStuId());
                       jsonObject.put("name", "" + mGetInfo.getStuName());
                       jsonObject.put("path", "" + mGetInfo.getHeadImgPath());
                       jsonObject.put("Version", "" + mGetInfo.getHeadImgVersion());
                       sendNotice(jsonObject,null);
                       //mLogtf.d("onRollCallSuccess ok");
                   } catch (Exception e) {
                       //mLogtf.e("onRollCallSuccess", e);
                   }
               }

               @Override
               public void onPmFailure(Throwable error, String msg) {
                   callBack.onPmFailure(error, msg);
                   //logToFile.e("onPmFailure:msg=" + msg, error);

               }

               @Override
               public void onPmError(ResponseEntity responseEntity) {
                   callBack.onPmError(responseEntity);
               }
           });
        }
    }


    /**
     * 是否是 可签到 时间段
     *
     * @param classBeginTime 课程开始时间
     * @param nowTime        服务器当前时间  单位秒
     * @return
     */
    public boolean isTimeAvaliable(long classBeginTime, long nowTime) {
        Loger.e("RollCallBll", "====>isTimeAvaliable:" + classBeginTime);
        boolean result = false;
        try {
            if (classBeginTime > 0 && nowTime > 0) {
                result = classBeginTime > nowTime;
                // 大于15 分钟进入直播间
                long autoShowSignTime = classBeginTime - SIGN_AVALIABLE_TIME_RANG;
                autoShowSignDelay = autoShowSignTime - nowTime < 0 ? 0 : autoShowSignTime - nowTime;
                //自动关闭时间
                autoCloseSignDelay = classBeginTime - nowTime < 0 ? 0 : classBeginTime - nowTime;
                //autoCloseSignDelay = 2*60*1000;
                Loger.e("RollCallBll", "====> isTimeAvaliable :+ " + result + ":" + autoShowSignDelay + ":" +
                        autoCloseSignDelay);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param autoSign
     */
    private void setAutoSign(boolean autoSign) {
        this.autoSign = autoSign;
        classSignStop.setAtuoSign(autoSign);
    }


    @Override
    public void autoSign(final ClassSignEntity classSignEntity, long classStartTime, long nowTime) {
        boolean timeAvaliable = isTimeAvaliable(classStartTime, nowTime);
        if (timeAvaliable && autoSign) {
            mVPlayVideoControlHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mClassSignPager != null) {
                        mClassSignPager.updateStatus(classSignEntity.getStatus());
                        return;
                    }
                    mIsShowUserSign = true;
                    mClassSignPager = new ClassSignPager(activity, RollCallBll.this, classSignEntity, mLiveBll);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
                            .WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.CENTER_IN_PARENT);
                    rlRollCallContent.addView(mClassSignPager.getRootView(), params);
                    activity.getWindow().getDecorView().requestLayout();
                    activity.getWindow().getDecorView().invalidate();
                }
            }, autoShowSignDelay);


            //自动签到 到上课时间自动关闭签到面板
            if (autoCloseSignTask != null) {
                mVPlayVideoControlHandler.removeCallbacks(autoCloseSignTask);
                mVPlayVideoControlHandler.postDelayed(autoCloseSignTask, autoCloseSignDelay);
            }
        }
    }


    /**
     * 移除签到相关页面
     */
    private void closeUserSign() {
        mVPlayVideoControlHandler.post(new Runnable() {
            @Override
            public void run() {
                //自动签到关闭 班级签到状态
                if (autoSign && classmateSignPager != null) {
                    classmateSignPager.stop();
                    rlRollCallContent.removeView(classmateSignPager.getRootView());
                    classmateSignPager = null;
                }

                if (mClassSignPager != null) {
                    rlRollCallContent.removeView(mClassSignPager.getRootView());
                    mClassSignPager = null;
                }
                mVPlayVideoControlHandler.removeCallbacksAndMessages(null);
            }
        });
    }

    /**
     * 签到面板自动关闭 任务
     */
    Runnable autoCloseSignTask = new Runnable() {
        @Override
        public void run() {
            closeUserSign();
        }
    };


    /////// 通信接口 相关///////

    private int[] noticeCodes = {
            XESCODE.CLASSBEGIN,
            XESCODE.ROLLCALL,
            XESCODE.STOPROLLCALL,
            XESCODE.CLASS_MATEROLLCALL,

    };

    @Override
    public void onNotice(JSONObject data, int type) {
        Loger.e("=====>RollCallBll","=======>onNotice:" + type);
        try {
            switch (type) {
                case XESCODE.CLASSBEGIN:
                    //开始上课  结束签到相关逻辑
                    forceCloseRollCall();
                    break;
                case XESCODE.ROLLCALL:

                    onRollCall(false);
                    if (mGetInfo.getStudentLiveInfo().getSignStatus() != Config.SIGN_STATE_CODE_SIGNED) {
                        ClassSignEntity classSignEntity = new ClassSignEntity();
                        classSignEntity.setStuName(mGetInfo.getStuName());
                        classSignEntity.setTeacherName(mGetInfo.getTeacherName());
                        classSignEntity.setTeacherIMG(mGetInfo.getTeacherIMG());
                        classSignEntity.setStatus(1);
                        onRollCall(classSignEntity);
                    }
                    break;
                case XESCODE.STOPROLLCALL:

                    onRollCall(true);
                    //noinspection AlibabaUndefineMagicConstant
                    if (mGetInfo.getStudentLiveInfo().getSignStatus() != Config.SIGN_STATE_CODE_SIGNED) {
                        mGetInfo.getStudentLiveInfo().setSignStatus(3);
                        ClassSignEntity classSignEntity = new ClassSignEntity();
                        classSignEntity.setStuName(mGetInfo.getStuName());
                        classSignEntity.setTeacherName(mGetInfo.getTeacherName());
                        classSignEntity.setTeacherIMG(mGetInfo.getTeacherIMG());
                        classSignEntity.setStatus(mGetInfo.getStudentLiveInfo().getSignStatus());
                        onRollCall(classSignEntity);
                    }
                    break;

                case XESCODE.CLASS_MATEROLLCALL:

                    if (RollCallBll.IS_SHOW_CLASSMATE_SIGN) {
                        List<String> headImgUrl = mGetInfo.getHeadImgUrl();
                        ClassmateEntity classmateEntity = new ClassmateEntity();
                        String id = data.optString("id");
                        classmateEntity.setId(id);
                        classmateEntity.setName(data.getString("name"));
                        if (!headImgUrl.isEmpty()) {
                            try {
                                String img = headImgUrl.get(0) + "/" + data.getString("path") + "/" +
                                        mGetInfo.getImgSizeType() + "?" + data.getString("Version");
                                classmateEntity.setImg(img);
                            } catch (JSONException e) {
                                MobAgent.httpResponseParserError(TAG, "onNotice:setImg", e.getMessage());
                            }
                        }
                        onClassmateRollCall(classmateEntity);
                    }
                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int[] getNoticeFilter() {

        return noticeCodes;
    }


    @Override
    public void onLiveInited(LiveGetInfo data) {
        super.onLiveInited(data);
        Loger.e("RollCallBll", "======>onLiveInited called:" + data + ":" + mRootView);
        mGetInfo = data;
        initView((RelativeLayout) mRootView);
        classSignStop.setTraning(LiveTopic.MODE_TRANING.equals(data.getLiveTopic().getMode()));

        if (OPEN_AUTO_SIGN) {
            // 理科直播 自动签到
            boolean isAutoSign = data != null && data.getIsArts() != 1 && mLiveBll.getLiveType() == LiveBll.LIVE_TYPE_LIVE;
            setAutoSign(isAutoSign);
        }

        initUserSignState(data);

    }

    private void initUserSignState(LiveGetInfo data) {

        //理科自动签到
        if (RollCallBll.OPEN_AUTO_SIGN && data.getIsArts() != 1
                && data.getStudentLiveInfo().getSignStatus() != Config.SIGN_STATE_CODE_SIGNED) {
            ClassSignEntity classSignEntity = new ClassSignEntity();
            classSignEntity.setStuName(data.getStuName());
            classSignEntity.setTeacherName(data.getTeacherName());
            classSignEntity.setTeacherIMG(data.getTeacherIMG());
            classSignEntity.setStatus(1);
            long classBeginTime = data.getsTime() * 1000;
            long nowTime = (long) (data.getNowTime() * 1000);
            autoSign(classSignEntity, classBeginTime, nowTime);
        } else {
            if (data.getStudentLiveInfo().getSignStatus() != Config.SIGN_STATE_CODE_SIGN_UNSTART && data.getStudentLiveInfo().getSignStatus()
                    != Config.SIGN_STATE_CODE_SIGNED) {
                ClassSignEntity classSignEntity = new ClassSignEntity();
                classSignEntity.setStuName(data.getStuName());
                classSignEntity.setTeacherName(data.getTeacherName());
                classSignEntity.setTeacherIMG(data.getTeacherIMG());
                classSignEntity.setStatus(data.getStudentLiveInfo().getSignStatus());
                onRollCall(classSignEntity);
            }
        }
    }

    @Override
    public void onDestory() {
        super.onDestory();
        forceCloseRollCall();
    }
}
