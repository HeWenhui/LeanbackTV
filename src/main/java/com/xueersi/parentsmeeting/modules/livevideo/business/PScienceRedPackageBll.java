package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.lib.framework.utils.EventBusUtil;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.redpackage.business.RedPackageAction;
import com.xueersi.parentsmeeting.modules.livevideo.redpackage.entity.RedPackageEvent;
import com.xueersi.parentsmeeting.modules.livevideo.studyreport.business.StudyReportAction;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveMainHandler;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by David on 2018/6/29.
 */
public class PScienceRedPackageBll implements RedPackageAction {
    String TAG = "PScienceRedPackageBll";
    private Handler mHandler = LiveMainHandler.getMainHandler();
    private LogToFile mLogtf;
    private Activity activity;
    /** 直播id */
    private String mVSectionID;
    private ReceiveGold receiveGold;
    private LiveViewAction liveViewAction;
    private ArrayList<View> redViews = new ArrayList<>();
    private boolean isLive;
    private LiveGetInfo mGetInfo;

    public PScienceRedPackageBll(Activity activity, LiveGetInfo liveGetInfo, boolean isLive) {
        mLogtf = new LogToFile(activity, TAG);
        mLogtf.clear();
        this.activity = activity;
        this.isLive = isLive;
        this.mGetInfo = liveGetInfo;
    }

    public void setVSectionID(String mVSectionID) {
        this.mVSectionID = mVSectionID;
    }

    public void setReceiveGold(ReceiveGold receiveGold) {
        this.receiveGold = receiveGold;
    }

    @Override
    public void onReadPackage(final int operateId, final OnReceivePackage onReceivePackage) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                showRedPacket(operateId, onReceivePackage);
            }
        });
    }

    @Override
    public void onRemoveRedPackage() {

    }

    private void onGetPackage(VideoResultEntity entity, View view) {
        redViews.remove(view);
        liveViewAction.removeView(view);
        if (!isLive && entity.getResultType() == 0) {
            initRedPacketOtherResult();
        } else {
            initRedPacketResult(entity.getGoldNum());
        }
//        initRedPacketResult(entity.getGoldNum());
    }

    private void onGetPackageFailure(int operateId) {
    }

    private void onGetPackageError(int operateId) {
        while (!redViews.isEmpty()) {
            View view = redViews.remove(0);
            liveViewAction.removeView(view);
        }
    }

    public void initView(RelativeLayout bottomContent, LiveViewAction liveViewAction) {
        this.liveViewAction = liveViewAction;
    }

    /**
     * 显示红包
     */
    private void showRedPacket(final int operateId, final OnReceivePackage onReceivePackage) {
        mLogtf.d("showRedPacket:operateId=" + operateId);
        final View view = liveViewAction.inflateView(R.layout.dialog_primary_redpacket);
        redViews.add(view);
        view.setBackgroundColor(activity.getResources().getColor(R.color.mediacontroller_bg));
        view.setTag(operateId);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        liveViewAction.addView(view, params);
        ImageView btnRedPacket = view.findViewById(R.id.iv_livevideo_redpackage_cofirm);
        btnRedPacket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendReceiveGold(operateId, mVSectionID, view);
            }
        });
        view.findViewById(R.id.iv_livevideo_redpackage_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redViews.remove(view);
                liveViewAction.removeView(view);
            }
        });
    }

    private void sendReceiveGold(final int operateId, String sectionID, final View view) {
        receiveGold.sendReceiveGold(operateId, sectionID, new AbstractBusinessDataCallBack() {
            @Override
            public void onDataSucess(Object... objData) {
                VideoResultEntity entity = (VideoResultEntity) objData[0];
                onGetPackage(entity, view);
                // 广播 领取红包成功事件
                EventBusUtil.post(new RedPackageEvent(mVSectionID, entity.getGoldNum(),
                        operateId + "", RedPackageEvent.STATE_CODE_SUCCESS));
            }

            @Override
            public void onDataFail(int errStatus, String failMsg) {
                super.onDataFail(errStatus, failMsg);
                if (errStatus == 0) {
                    onGetPackageFailure(operateId);
                } else {
                    onGetPackageError(operateId);
                }
            }
        });
    }

    /**
     * 获取红包成功
     *
     * @param goldNum 金币数量
     */
    private void initRedPacketResult(int goldNum) {
        String msg = goldNum + "";
        final View view = liveViewAction.inflateView(R.layout.dialog_primary_redpacket_success);
        view.setBackgroundColor(activity.getResources().getColor(R.color.mediacontroller_bg));
//        SpannableString msp = new SpannableString(msg);
//        float screenDensity = ScreenUtils.getScreenDensity();
//        // 字体
//        msp.setSpan(new AbsoluteSizeSpan((int) (50 * screenDensity)), 0, msg.length(),
//                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 将字体文件保存在assets/fonts/目录下，在程序中通过如下方式实例化自定义字体：
        TextView tvGoldHint = view.findViewById(R.id.tv_livevideo_redpackage_gold);
        tvGoldHint.setText(msg);
        liveViewAction.addView(view);
        final TextView tvAutoclose = view.findViewById(R.id.tv_livevideo_redpackage_autoclose);
        final AtomicInteger count = new AtomicInteger(3);
        postDelayedIfNotFinish(new Runnable() {
            @Override
            public void run() {
                count.set(count.get() - 1);
                if (count.get() == 0) {
                    liveViewAction.removeView(view);
                } else {
                    if (view.getParent() != null) {
                        tvAutoclose.setText(count.get() + "秒钟后自动关闭");
                        postDelayedIfNotFinish(this, 1000);
                    }
                }
            }
        }, 1000);
        view.findViewById(R.id.iv_livevideo_redpackage_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liveViewAction.removeView(view);
            }
        });
        StudyReportAction studyReportAction = ProxUtil.getProxUtil().get(activity, StudyReportAction.class);
        if (studyReportAction != null) {
            studyReportAction.cutImage(LiveVideoConfig.STUDY_REPORT.TYPE_RED_PACKAGE, view, false, true);
        }
    }

    /**
     * 已获取红包
     */
    private void initRedPacketOtherResult() {
        View popupWindow_view = liveViewAction.inflateView(R.layout.pop_question_redpacket_other);
        initQuestionAnswerReslut(popupWindow_view);
    }

    /**
     * 创建互动题作答，抢红包结果提示PopupWindow
     */
    protected void initQuestionAnswerReslut(final View popupWindow_view) {
        liveViewAction.addView(popupWindow_view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
                .MATCH_PARENT);
        popupWindow_view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                liveViewAction.removeView(popupWindow_view);
            }
        });
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                liveViewAction.removeView(popupWindow_view);
            }
        }, 3000);
    }

    public void postDelayedIfNotFinish(Runnable r, long delayMillis) {
        if (activity.isFinishing()) {
            return;
        }
        mHandler.postDelayed(r, delayMillis);
    }
}
