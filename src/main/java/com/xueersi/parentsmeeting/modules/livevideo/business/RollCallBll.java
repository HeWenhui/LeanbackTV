package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoActivity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassSignEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassmateEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.page.ClassSignPager;
import com.xueersi.parentsmeeting.modules.livevideo.page.ClassmateSignPager;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.xesalib.utils.log.Loger;
import com.xueersi.xesalib.utils.uikit.ScreenUtils;

import java.io.File;

/**
 * Created by linyuqiang on 2016/9/23.
 */
public class RollCallBll implements RollCallAction, Handler.Callback {



    /**
     * 可签到时间  课前15 分钟
     */
    private static final int SIGN_AVALIABLE_TIME_RANG = 15 * 60 * 1000;

    String TAG = "RollCallBll";
    /**
     * 控制点名滚动功能
     */
    public static final boolean IS_SHOW_CLASSMATE_SIGN = true;

    /**
     * 自动显示签到面板（不再由教师端 发起）
     */
    private boolean autoSign = false;

    /**是否开启自动签到功能*/
    public static final boolean OPEN_AUTO_SIGN = true;

    private Activity activity;
    private LiveBll mLiveBll;
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
    private long TIME_WATHER_DELAY = 3000;

    /**
     * 自动签到模式时  签到页面自动显示 延时
     */
    private long autoShowSignDelay;
    /**
     * 自动签到模式时  签到页面自动关闭 延时
     */
    private long autoCloseSignDelay;


    public RollCallBll(Activity activity) {
        mLogtf = new LogToFile(TAG, new File(Environment.getExternalStorageDirectory(), "parentsmeeting/log/" + TAG
                + ".txt"));
        mLogtf.clear();
        this.activity = activity;
    }

    public void setLiveBll(LiveBll mLiveBll) {
        this.mLiveBll = mLiveBll;
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
        }
        return false;
    }

    int count = 0;

    public void onLiveInit(int livetype, LiveGetInfo getInfo) {
        classSignStop.setTraning(LiveTopic.MODE_TRANING.equals(getInfo.getLiveTopic().getMode()));
        //autoSign = getInfo != null && getInfo.getIsArts() == 1;

        if (OPEN_AUTO_SIGN){
            // 理科直播 自动签到
            boolean isAutoSign = getInfo != null && getInfo.getIsArts() != 1 && livetype == 3;
            setAutoSign(isAutoSign);
        }

        //        if (IS_SHOW_CLASSMATE_SIGN) {
//            mHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    count++;
//                    List<String> headImgUrl = mGetInfo.getHeadImgUrl();
//                    if (!headImgUrl.isEmpty()) {
//                        ClassmateEntity classmateEntity = new ClassmateEntity();
//                        int id = 11022;
//                        classmateEntity.setId(id);
//                        classmateEntity.setName("lyq");
//                        int index = id % headImgUrl.size();
//                        String host = headImgUrl.get(index);
//                        if (host.endsWith("/")) {
//                            host = host.substring(0, host.length() - 1);
//                        }
//                        classmateEntity.setImg(host + "/" + (id / 10000) + "/" + id + ".jpg");
//                        onClassmateRollCall(classmateEntity);
//                        if (count % 20 == 0) {
//                            mHandler.postDelayed(this, 5000);
//                        } else if (count % 25 == 0) {
//                            mHandler.postDelayed(this, 15200);
//                        } else {
//                            mHandler.postDelayed(this, 900);
//                        }
//                    }
//                }
//            }, 900);
//        }
    }

    public void initView(final RelativeLayout bottomContent) {
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
                classmateSignPager = new ClassmateSignPager(activity, mLiveBll);
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
//            classmateSignPager.getRootView().setLayoutParams(params);
            LayoutParamsUtil.setViewLayoutParams(classmateSignPager.getRootView(), params);
        }
    }

    public void onModeChange(final String mode, final boolean isPresent) {
        if (LiveTopic.MODE_CLASS.equals(mode)) {
            classSignStop.setTraning(false);
            if (classmateSignPager != null) {
                //Loger.e("RollCallBll","=====> onModeChange stop classMateSignPager");
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
                //Loger.e("RollCallBll","=====> onRollCall stop classMateSignPager");
                classmateSignPager.stop();
            }
        }

    }

    @Override
    public void onRollCall(final ClassSignEntity classSignEntity) {
        mLogtf.d("onRollCall:classSignEntity=" + classSignEntity.getStatus());
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
        //Loger.e("RollCallBll","===>onClassmateRollCall:"+classSignStop.getStopSign()+":"+classSignStop.isTraning());
        if (classSignStop.getStopSign() || !classSignStop.isTraning()) {
            return;
        }
        mVPlayVideoControlHandler.post(new Runnable() {
            @Override
            public void run() {
                if (classmateSignPager != null) {
                    classmateSignPager.addClassmage(classmateEntity);
                    // Loger.e("RollCallBll","===>onClassmateRollCall: classmateSignPager addClassMage");
                }
            }
        });
    }

    @Override
    public void stopRollCall() {
        mLogtf.d("stopRollCall");
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
    public void autoSign( final ClassSignEntity classSignEntity, long classStartTime, long nowTime) {
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
            },autoShowSignDelay);


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
        Loger.e("RollCallBll", "=====>closeUserSign called");
        mVPlayVideoControlHandler.post(new Runnable() {
            @Override
            public void run() {
                //自动签到关闭 班级签到状态
                if (autoSign && classmateSignPager != null) {
                    Loger.e("RollCallBll", "=====> stopRollCall() stop classMateSignPager");
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
}
