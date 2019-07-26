package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.business.UserBll;
import com.xueersi.lib.framework.utils.EventBusUtil;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.achievement.business.UpdateAchievement;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.redpackage.business.RedPackageAction;
import com.xueersi.parentsmeeting.modules.livevideo.redpackage.entity.RedPackageEvent;
import com.xueersi.parentsmeeting.modules.livevideo.studyreport.business.StudyReportAction;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by David on 2018/6/29.
 */

public class PScienceRedPackageBll implements RedPackageAction, Handler.Callback {
    String TAG = "PScienceRedPackageBll";
    private WeakHandler mVPlayVideoControlHandler = new WeakHandler(this);
    private LogToFile mLogtf;
    private Activity activity;
    /** 直播id */
    private String mVSectionID;
    private ReceiveGold receiveGold;
    /** 红包的布局 */
    private RelativeLayout rlRedpacketContent;
    boolean isLive;
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
    public boolean handleMessage(Message msg) {
        return false;
    }

    @Override
    public void onReadPackage(final int operateId, final OnReceivePackage onReceivePackage) {
        mVPlayVideoControlHandler.post(new Runnable() {
            @Override
            public void run() {
                showRedPacket(operateId, onReceivePackage);
            }
        });
    }

    private void onGetPackage(VideoResultEntity entity) {
        rlRedpacketContent.removeAllViews();
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
        rlRedpacketContent.removeAllViews();
    }

    public void initView(RelativeLayout bottomContent) {
        //红包
        if (rlRedpacketContent != null) {
            bottomContent.addView(rlRedpacketContent, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        } else {
            rlRedpacketContent = new RelativeLayout(activity);
            rlRedpacketContent.setId(R.id.rl_livevideo_content_readpackage);
            bottomContent.addView(rlRedpacketContent, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }

    /**
     * 显示红包
     */
    private void showRedPacket(final int operateId, final OnReceivePackage onReceivePackage) {
        mLogtf.d("showRedPacket:operateId=" + operateId);
        rlRedpacketContent.removeAllViews();
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_primary_redpacket, rlRedpacketContent, false);
        view.setBackgroundColor(activity.getResources().getColor(R.color.mediacontroller_bg));
        view.setTag(operateId);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rlRedpacketContent.addView(view, params);
        ImageView btnRedPacket = (ImageView) view.findViewById(R.id.iv_livevideo_redpackage_cofirm);
        btnRedPacket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendReceiveGold(operateId, mVSectionID);
            }
        });
        view.findViewById(R.id.iv_livevideo_redpackage_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlRedpacketContent.removeAllViews();
            }
        });
        activity.getWindow().getDecorView().requestLayout();
        activity.getWindow().getDecorView().invalidate();
//        String msg = 8 + "";
//        View view = activity.getLayoutInflater().inflate(R.layout.dialog_primary_redpacket_success, rlRedpacketContent, false);
//        view.setBackgroundColor(activity.getResources().getColor(R.color.mediacontroller_bg));
//        SpannableString msp = new SpannableString(msg);
//        float screenDensity = ScreenUtils.getScreenDensity();
//        // 字体
//        msp.setSpan(new AbsoluteSizeSpan((int) (50 * screenDensity)), 0, 1,
//                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        TextView tvGoldHint = (TextView) view.findViewById(R.id.tv_livevideo_redpackage_gold);
//        tvGoldHint.setText(msp);
//        rlRedpacketContent.addView(view);
//        view.findViewById(R.id.iv_livevideo_redpackage_close).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                rlRedpacketContent.removeAllViews();
//            }
//        });
    }

    private void sendReceiveGold(final int operateId, String sectionID) {
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        receiveGold.sendReceiveGold(operateId, sectionID, new AbstractBusinessDataCallBack() {
            @Override
            public void onDataSucess(Object... objData) {
                VideoResultEntity entity = (VideoResultEntity) objData[0];
                onGetPackage(entity);
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
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_primary_redpacket_success, rlRedpacketContent, false);
        view.setBackgroundColor(activity.getResources().getColor(R.color.mediacontroller_bg));
//        SpannableString msp = new SpannableString(msg);
//        float screenDensity = ScreenUtils.getScreenDensity();
//        // 字体
//        msp.setSpan(new AbsoluteSizeSpan((int) (50 * screenDensity)), 0, msg.length(),
//                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 将字体文件保存在assets/fonts/目录下，在程序中通过如下方式实例化自定义字体：
        Typeface typeFace = Typeface.createFromAsset(activity.getAssets(), "fangzhengcuyuan.ttf");
        TextView tvGoldHint = (TextView) view.findViewById(R.id.tv_livevideo_redpackage_gold);
        // 应用字体
        tvGoldHint.setTypeface(typeFace);
        tvGoldHint.setText(msg);
        rlRedpacketContent.addView(view);
        final TextView tvAutoclose = (TextView) view.findViewById(R.id.tv_livevideo_redpackage_autoclose);
        final AtomicInteger count = new AtomicInteger(3);
        postDelayedIfNotFinish(new Runnable() {
            @Override
            public void run() {
                count.set(count.get() - 1);
                if (count.get() == 0) {
                    rlRedpacketContent.removeAllViews();
                } else {
                    if (rlRedpacketContent.getChildCount() > 0) {
                        tvAutoclose.setText(count.get() + "秒钟后自动关闭");
                        postDelayedIfNotFinish(this, 1000);
                    }
                }
            }
        }, 1000);
        view.findViewById(R.id.iv_livevideo_redpackage_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlRedpacketContent.removeAllViews();
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
        View popupWindow_view = activity.getLayoutInflater().inflate(R.layout.pop_question_redpacket_other, null,
                false);
        initQuestionAnswerReslut(popupWindow_view);
    }

    /**
     * 创建互动题作答，抢红包结果提示PopupWindow
     */
    protected void initQuestionAnswerReslut(final View popupWindow_view) {
        rlRedpacketContent.addView(popupWindow_view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
                .MATCH_PARENT);
        popupWindow_view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                rlRedpacketContent.removeView(popupWindow_view);
            }
        });
        mVPlayVideoControlHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                rlRedpacketContent.removeView(popupWindow_view);
            }
        }, 3000);
    }

    public void postDelayedIfNotFinish(Runnable r, long delayMillis) {
        if (activity.isFinishing()) {
            return;
        }
        mVPlayVideoControlHandler.postDelayed(r, delayMillis);
    }
}
