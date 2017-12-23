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
import com.xueersi.xesalib.utils.uikit.ScreenUtils;

import java.io.File;

/**
 * Created by linyuqiang on 2016/9/23.
 */
public class RollCallBll implements RollCallAction, Handler.Callback {
    String TAG = "RollCallBll";
    /** 控制点名滚动功能 */
    public static final boolean IS_SHOW_CLASSMATE_SIGN = true;
    private Activity activity;
    private LiveBll mLiveBll;
    private WeakHandler mVPlayVideoControlHandler = new WeakHandler(this);
    /** 停止点名 */
    private ClassmateSignPager.ClassSignStop classSignStop = new ClassmateSignPager.ClassSignStop();
    /** 同学点名签到 */
    private ClassmateSignPager classmateSignPager;
    /** 点名 */
    private ClassSignPager mClassSignPager;
    private LogToFile mLogtf;
    /** 显示学生签到 */
    private static final int SHOW_USERSIGN = 6;
    /** 隐藏学生签到 */
    private static final int NO_USERSIGN = 7;
    /** 当前是否正在显示签到 */
    private boolean mIsShowUserSign = false;
    /** 点名的布局 */
    private RelativeLayout rlRollCallContent;

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

    public void onLiveInit(LiveGetInfo getInfo) {
        classSignStop.setTraning(LiveTopic.MODE_TRANING.equals(getInfo.getLiveTopic().getMode()));
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

    public void initView(RelativeLayout bottomContent) {
        int screenWidth = ScreenUtils.getScreenWidth();
        int wradio = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * screenWidth / LiveVideoActivity.VIDEO_WIDTH);
        rlRollCallContent = new RelativeLayout(activity);
        bottomContent.addView(rlRollCallContent, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        //点名
        RelativeLayout rlClassmateContent = new RelativeLayout(activity);
        classmateSignPager = new ClassmateSignPager(activity, mLiveBll);
        classmateSignPager.setClassSignStop(classSignStop);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.rightMargin = wradio;
        if (IS_SHOW_CLASSMATE_SIGN) {
            classmateSignPager.start();
            rlClassmateContent.addView(classmateSignPager.getRootView(), params);
        }
        bottomContent.addView(rlClassmateContent, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public void onPlayOpenSuccess(ViewGroup.LayoutParams lp) {
        if (IS_SHOW_CLASSMATE_SIGN) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) classmateSignPager.getRootView().getLayoutParams();
            int screenWidth = ScreenUtils.getScreenWidth();
            int wradio = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * screenWidth / LiveVideoActivity.VIDEO_WIDTH) + (screenWidth - lp.width) / 2;
            params.rightMargin = wradio;
//            classmateSignPager.getRootView().setLayoutParams(params);
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
        classSignStop.setStopSign(stop);
        if (stop && classmateSignPager != null) {
            classmateSignPager.stop();
        }
    }

    @Override
    public void onRollCall(final ClassSignEntity classSignEntity) {
        mLogtf.d("onRollCall:classSignEntity=" + classSignEntity.getStatus());
        mVPlayVideoControlHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mClassSignPager != null) {
                    mClassSignPager.updateStatus(classSignEntity.getStatus());
                    return;
                }
                mIsShowUserSign = true;
                mClassSignPager = new ClassSignPager(activity, RollCallBll.this, classSignEntity, mLiveBll);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.CENTER_IN_PARENT);
                rlRollCallContent.addView(mClassSignPager.getRootView(), params);
                activity.getWindow().getDecorView().requestLayout();
                activity.getWindow().getDecorView().invalidate();
            }
        });
        mVPlayVideoControlHandler.sendEmptyMessage(SHOW_USERSIGN);
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
}
