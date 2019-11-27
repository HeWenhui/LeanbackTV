package com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.bll;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveViewAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.business.WeakHandler;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.pager.LightLiveRedPackageView;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.redpackage.business.RedPackageAction;
import com.xueersi.parentsmeeting.modules.livevideo.redpackage.business.RedPackageBll;
import com.xueersi.parentsmeeting.modules.livevideo.redpackage.entity.RedPackageEvent;
import com.xueersi.parentsmeeting.modules.livevideo.redpackage.pager.SmallChineseRedPackagePager;
import com.xueersi.parentsmeeting.modules.livevideo.redpackage.pager.SmallEnglishRedPackagePager;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @ProjectName: xueersiwangxiao
 * @Package: com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.bll
 * @ClassName: LightLiveRedPackageBll
 * @Description: 轻直播红包
 * @Author: WangDe
 * @CreateDate: 2019/11/27 15:40
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/11/27 15:40
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class LightLiveRedPackageBll implements RedPackageAction, Handler.Callback {
    private Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
    private static final String TAG = "RedPackageBll";
    private WeakHandler mVPlayVideoControlHandler = new WeakHandler(this);
    private LogToFile mLogtf;
    private Activity activity;
    private RedPackageAction.ReceiveGold receiveGold;
    /**
     * 直播id
     */
    private String mVSectionID;
    private RelativeLayout mContentView;
    /**
     * 红包的布局
     */
    private RelativeLayout rlRedpacketContent;
    /**
     * 轻直播
     */
    LightLiveRedPackageView lightLiveRedPackageView;
    AtomicBoolean mIsLand;

    public LightLiveRedPackageBll(Activity activity, LiveGetInfo liveGetInfo) {
        mLogtf = new LogToFile(activity, TAG);
        mLogtf.clear();
        this.activity = activity;
    }

    public void setReceiveGold(RedPackageAction.ReceiveGold receiveGold) {
        this.receiveGold = receiveGold;
    }

    public void setVSectionID(String mVSectionID) {
        this.mVSectionID = mVSectionID;
    }

    @Override
    public boolean handleMessage(Message msg) {
        return false;
    }

    @Override
    public void onReadPackage(final int operateId, final OnReceivePackage onReceivePackage) {
        logger.i(String.valueOf(operateId));
        mVPlayVideoControlHandler.post(new Runnable() {
            @Override
            public void run() {
                showRedPacket(operateId, onReceivePackage);
            }
        });
    }

    private void onGetPackageFailure(int operateId) {
    }

    private void onGetPackageError(int operateId) {
        rlRedpacketContent.removeAllViews();
    }

    public void initView(RelativeLayout bottomContent, RelativeLayout mContentView, AtomicBoolean mIsLand) {
        this.mContentView = mContentView;
        this.mIsLand = mIsLand;
        if (rlRedpacketContent == null) {
            rlRedpacketContent = new RelativeLayout(activity);
            rlRedpacketContent.setId(R.id.rl_livevideo_content_readpackage);
        }
        //竖屏
        if (!mIsLand.get()) {
            RelativeLayout contentLayout = mContentView.findViewById(R.id.rl_course_video_live_content);
            LinearLayout llOtherContent = mContentView.findViewById(R.id.ll_course_video_live_other_content);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
//            params.addRule(RelativeLayout.ALIGN_TOP,R.id.rl_live_video_frag);
            params.topMargin =  llOtherContent.getTop();
            contentLayout.addView(rlRedpacketContent,params);
        } else {
            bottomContent.addView(rlRedpacketContent, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }

    /**
     * 显示红包
     */
    private void showRedPacket(final int operateId, final OnReceivePackage onReceivePackage) {
        mLogtf.d("showRedPacket:operateId=" + operateId);
        rlRedpacketContent.removeAllViews();
        View view = null;
        RelativeLayout.LayoutParams params = null;
        //小英
        if (LiveVideoConfig.isLightLive) {
//            if (lightLiveRedPackageView != null) {
//                rlRedpacketContent.removeView(lightLiveRedPackageView.getRootView());
//            }
            lightLiveRedPackageView = new LightLiveRedPackageView(activity,  operateId);
            lightLiveRedPackageView.setOnPagerClose(new LiveBasePager.OnPagerClose() {
                @Override
                public void onClose(LiveBasePager basePager) {
                    if (lightLiveRedPackageView == basePager) {
                        rlRedpacketContent.removeView(basePager.getRootView());
                        lightLiveRedPackageView = null;
                    }
                }
            });
            lightLiveRedPackageView.setReceiveGold(new com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.mvp.ReceiveGold() {
                @Override
                public void sendReceiveGold(int operateId, OnRedPackageSend onRedPackageSend) {
                    LightLiveRedPackageBll.this.sendReceiveGold(operateId, mVSectionID);
                }
            });
            view = lightLiveRedPackageView.getRootView();
            params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout
                    .LayoutParams.MATCH_PARENT);
        }
        if(!mIsLand.get()){
            //修正显示
            LinearLayout llOtherContent = mContentView.findViewById(R.id.ll_course_video_live_other_content);
            RelativeLayout.LayoutParams rlParams = (RelativeLayout.LayoutParams)rlRedpacketContent.getLayoutParams();
//            params.addRule(RelativeLayout.ALIGN_TOP,R.id.rl_live_video_frag);
            rlParams.topMargin =  llOtherContent.getTop();
            rlRedpacketContent.setLayoutParams(rlParams);
        }
        rlRedpacketContent.addView(view, params);
        rlRedpacketContent.bringToFront();
        activity.getWindow().getDecorView().requestLayout();
        activity.getWindow().getDecorView().invalidate();
    }

    private void sendReceiveGold(final int operateId, String sectionID) {
        receiveGold.sendReceiveGold(operateId, sectionID, new AbstractBusinessDataCallBack() {
            @Override
            public void onDataSucess(Object... objData) {
                VideoResultEntity entity = (VideoResultEntity) objData[0];
                // 广播 领取红包成功事件
                EventBus.getDefault().post(new RedPackageEvent(mVSectionID, entity.getGoldNum(),
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

    public void postDelayedIfNotFinish(Runnable r, long delayMillis) {
        if (activity.isFinishing()) {
            return;
        }
        mVPlayVideoControlHandler.postDelayed(r, delayMillis);
    }

    public void onDestroy() {
        if (mVPlayVideoControlHandler != null) {
            mVPlayVideoControlHandler.removeCallbacksAndMessages(null);
        }
    }
}
