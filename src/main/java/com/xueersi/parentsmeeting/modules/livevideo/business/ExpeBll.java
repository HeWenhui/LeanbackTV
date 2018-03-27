package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.graphics.Rect;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.base.AbstractBusinessDataCallBack;
import com.xueersi.parentsmeeting.base.BaseApplication;
import com.xueersi.parentsmeeting.browser.activity.BrowserActivity;
import com.xueersi.parentsmeeting.business.AppBll;
import com.xueersi.parentsmeeting.modules.livevideo.OtherModulesEnter;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.sharedata.ShareDataManager;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoActivity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.widget.ExpeAlertDialog;
import com.xueersi.xesalib.utils.file.FileUtils;
import com.xueersi.xesalib.utils.string.StringUtils;
import com.xueersi.xesalib.utils.time.TimeUtils;
import com.xueersi.xesalib.utils.uikit.ScreenUtils;

import org.json.JSONObject;

import java.io.File;

/**
 * Created by linyuqiang on 2017/7/13.
 * 体验直播
 */
public class ExpeBll {
    LiveVideoActivity activity;
    LiveBll liveBll;
    /** 直播时间-当天据算，live_expe_time按单个场次计算 */
    private static final String EXPE_TIME = LiveVideoConfig.LIVE_EXPE_TIME;
    protected ShareDataManager mShareDataManager;
    /** 直播id */
    private String mVSectionID;
    private long startTime;
    Runnable runnable;
    Handler handler = new Handler(Looper.getMainLooper());
    String dayKey = "";
    String buyCourseUrl;
    /** 每天可以观看时长 */
    private long userModeTotalTime;
    /** 每天剩余观看时长 */
    private long userModeTime;
    long enterTime;
    View view;

    public ExpeBll(LiveVideoActivity activity, LiveBll liveBll) {
        this.activity = activity;
        this.liveBll = liveBll;
    }

    public void setVSectionID(String mVSectionID) {
        this.mVSectionID = mVSectionID;
    }

    public void setShareDataManager(LiveGetInfo getInfo, LiveGetInfo.StudentLiveInfoEntity studentLiveInfo, ShareDataManager mShareDataManager) {
        enterTime = System.currentTimeMillis();
        userModeTotalTime = studentLiveInfo.getUserModeTotalTime() * 1000;
        userModeTime = studentLiveInfo.getUserModeTime() * 1000;
        this.mShareDataManager = mShareDataManager;
        buyCourseUrl = getInfo.getStudentLiveInfo().getBuyCourseUrl();
        File dir = new File(Environment.getExternalStorageDirectory(), "parentsmeeting/live/"
                + AppBll.getInstance().getAppInfoEntity().getChildName());
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, EXPE_TIME);
        long time = 0;
        try {
            String shareString = mShareDataManager.getString(EXPE_TIME, "", ShareDataManager.SHAREDATA_USER);
            String sdString = FileUtils.readFile2String(file.getPath(), "UTF-8");
            String saveString;
            if (!StringUtils.isEmpty(shareString)) {
                saveString = shareString;
            } else if (!StringUtils.isEmpty(sdString)) {
                saveString = sdString;
            } else {
                saveString = "{}";
            }
            dayKey = TimeUtils.long2String((long) (getInfo.getNowTime() * 1000), TimeUtils.dateFormatyyyyMMdd);
            JSONObject jsonObject = new JSONObject(saveString);
            JSONObject timeObject = jsonObject.optJSONObject(dayKey);
            if (timeObject != null) {
                long lastTime = userModeTotalTime - userModeTime;//服务器已经观看
                time = timeObject.optLong("time", 0);//本地已经观看
                if (lastTime > time) {
                    time = lastTime;
                    timeObject.put("time", time);
                }
            } else {
                timeObject = new JSONObject();
                time = userModeTotalTime - userModeTime;
                timeObject.put("time", time);
                jsonObject.put(dayKey, timeObject);
            }
            FileUtils.writeFileFromString(file, jsonObject.toString(), false);
            mShareDataManager.put(EXPE_TIME, jsonObject.toString(), ShareDataManager.SHAREDATA_USER);
        } catch (Exception e) {
            MobclickAgent.reportError(activity, new Error("" + mVSectionID, e));
            mShareDataManager.put(EXPE_TIME, "{}", ShareDataManager.SHAREDATA_USER);
            file.delete();
        }
        startTime = time;
    }

//    private void updateTime() {
//        startTime += 1000;
//        if (startTime % 60000 / 1000 % 20 == 0) {
//            saveTime();
//        }
//        if ((System.currentTimeMillis() - enterTime) / 1000 % 60 == 0) {
//            liveBll.userModeTime(new AbstractBusinessDataCallBack() {
//                @Override
//                public void onDataSucess(Object... objData) {
//
//                }
//            });
//        }
//    }

//    private void saveTime() {
//        File dir = new File(Environment.getExternalStorageDirectory(), "parentsmeeting/live/"
//                + AppBll.getInstance().getAppInfoEntity().getChildName());
//        if (!dir.exists()) {
//            dir.mkdirs();
//        }
//        File file = new File(dir, EXPE_TIME);
//        try {
//            String shareString = mShareDataManager.getString(EXPE_TIME, "", ShareDataManager.SHAREDATA_USER);
//            String sdString = FileUtils.readFile2String(file.getPath(), "UTF-8");
//            String saveString;
//            if (!StringUtils.isEmpty(shareString)) {
//                saveString = shareString;
//            } else if (!StringUtils.isEmpty(sdString)) {
//                saveString = sdString;
//            } else {
//                saveString = "{}";
//            }
//            JSONObject jsonObject = new JSONObject(saveString);
//            JSONObject timeObject = jsonObject.optJSONObject(dayKey);
//            if (timeObject != null) {
//                timeObject.put("time", startTime);
//            } else {
//                timeObject = new JSONObject();
//                timeObject.put("time", 0);
//                jsonObject.put(dayKey, timeObject);
//            }
//            FileUtils.writeFileFromString(file, jsonObject.toString(), false);
//            mShareDataManager.put(EXPE_TIME, jsonObject.toString(), ShareDataManager.SHAREDATA_USER);
//        } catch (Exception e) {
//            MobclickAgent.reportError(activity, new Error("" + mVSectionID, e));
//            mShareDataManager.put(EXPE_TIME, "{}", ShareDataManager.SHAREDATA_USER);
//            file.delete();
//        }
//    }

    public void initView(RelativeLayout bottomContent) {
        view = LayoutInflater.from(activity).inflate(R.layout.item_livevideo_expe, bottomContent, false);
        Button bt_livevideo_expe_enroll = (Button) view.findViewById(R.id.bt_livevideo_expe_enroll);
        final TextView textView = (TextView) view.findViewById(R.id.tv_livevideo_expe_time);
        long time = userModeTotalTime - startTime;
//        setTimeText(textView, time);
        int screenWidth = ScreenUtils.getScreenWidth();
        int wradio = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * screenWidth / LiveVideoActivity.VIDEO_WIDTH);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) view.getLayoutParams();
        lp.rightMargin = wradio;
        bottomContent.addView(view, lp);
//        if (StringUtils.isEmpty(buyCourseUrl)) {
//            bt_livevideo_expe_enroll.setText("继续选课");
//        } else {
//            bt_livevideo_expe_enroll.setText("立即报名");
//        }
//        bt_livevideo_expe_enroll.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (StringUtils.isEmpty(buyCourseUrl)) {
//                    OtherModulesEnter.intentToGradeActivityLive(activity, "");
//                } else {
//                    BrowserActivity.openBrowser(activity, buyCourseUrl);
//                }
//            }
//        });
//        runnable = new Runnable() {
//            @Override
//            public void run() {
//                if (!activity.isFinishing()) {
//                    updateTime();
//                    long time = userModeTotalTime - startTime;
//                    if (time >= 0) {
//                        setTimeText(textView, time);
//                    }
//                    if (time <= 0) {
//                        activity.stopPlay();
//                        activity.stopIRC();
//                        ExpeAlertDialog verifyCancelAlertDialog = new ExpeAlertDialog(activity, (BaseApplication) activity.getApplication(), false,
//                                buyCourseUrl);
//                        verifyCancelAlertDialog.initInfo("您今天的30分钟试听时间已用光 ，购买课程后继续听课，可享受全套的教学服务").showDialog();
//                        return;
//                    }
//                    handler.postDelayed(this, 1000);
//                }
//            }
//        };
//        handler.postDelayed(runnable, 1000);
    }

//    public void setVideoLayout(int width, int height) {
//        final View contentView = activity.findViewById(android.R.id.content);
//        final View actionBarOverlayLayout = (View) contentView.getParent();
//        Rect r = new Rect();
//        actionBarOverlayLayout.getWindowVisibleDisplayFrame(r);
//        int screenWidth = (r.right - r.left);
//        if (width > 0) {
//            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
//            int wradio = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * width / LiveVideoActivity.VIDEO_WIDTH);
//            wradio += (screenWidth - width) / 2;
//            if (wradio != params.rightMargin) {
//                params.rightMargin = wradio;
////                view.setLayoutParams(params);
//                LayoutParamsUtil.setViewLayoutParams(view, params);
//            }
//        }
//    }

//    private void setTimeText(TextView textView, long time) {
//        SpannableStringBuilder stringBuilder = new SpannableStringBuilder("你还可以试听");
//        {
//            SpannableString originalSpan = new SpannableString("" + time / 60000);
//            ForegroundColorSpan colorSpan = new ForegroundColorSpan(activity.getResources().getColor(R.color.COLOR_E74C3C));
//            originalSpan.setSpan(colorSpan, 0, originalSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            stringBuilder.append(originalSpan);
//        }
//        stringBuilder.append("分");
//        {
//            SpannableString originalSpan = new SpannableString("" + time % 60000 / 1000);
//            ForegroundColorSpan colorSpan = new ForegroundColorSpan(activity.getResources().getColor(R.color.COLOR_E74C3C));
//            originalSpan.setSpan(colorSpan, 0, originalSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            stringBuilder.append(originalSpan);
//        }
//        stringBuilder.append("秒");
//        textView.setText(stringBuilder);
//    }

//    public boolean onResume() {
//        long time = userModeTotalTime - startTime;
//        if (time <= 0) {
//            ExpeAlertDialog verifyCancelAlertDialog = new ExpeAlertDialog(activity, (BaseApplication) activity.getApplication(), false,
//                    buyCourseUrl);
//            verifyCancelAlertDialog.initInfo("您今天的30分钟试听时间已用光 ，购买课程后继续听课，可享受全套的教学服务").showDialog();
//            return false;
//        }
//        if (runnable != null) {
//            handler.post(runnable);
//        }
//        return true;
//    }

//    public void onPause() {
//        if (runnable != null) {
//            handler.removeCallbacks(runnable);
//            saveTime();
//        }
//    }

//    public void onModeChange(final String mode) {
//        if (LiveTopic.MODE_TRANING.equals(mode)) {
//            final Handler handler = new Handler(Looper.getMainLooper());
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    ExpeAlertDialog verifyCancelAlertDialog = new ExpeAlertDialog(activity, (BaseApplication) activity.getApplication(), false,
//                            buyCourseUrl);
//                    verifyCancelAlertDialog.initInfo("所有班级已切换到辅导老师小班教学模式，\n购买课程后继续听课，享受小班教学服务").showDialog();
//                }
//            });
//        }
//    }
}
