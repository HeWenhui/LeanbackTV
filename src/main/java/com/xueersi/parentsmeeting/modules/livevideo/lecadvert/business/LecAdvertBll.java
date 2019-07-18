package com.xueersi.parentsmeeting.modules.livevideo.lecadvert.business;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.ViewGroup;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.business.ActivityChangeLand;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveViewAction;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LecAdvertEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.page.LecAdvertPager;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.LecAdvertLog;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

import java.util.ArrayList;

/**
 * 讲座广告
 * Created by linyuqiang on 2018/1/15.
 */
public class LecAdvertBll implements LecAdvertAction, LecAdvertPagerClose {
    private String TAG = "LecAdvertBll";
    protected Logger logger = LoggerFactory.getLogger(TAG);
    private String eventid = LiveVideoConfig.LEC_ADS;
    private Context context;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private LiveViewAction liveViewAction;
    private LecAdvertPager lecAdvertager;
    private ArrayList<LecAdvertEntity> entities = new ArrayList<>();
    private LecAdvertHttp lecAdvertHttp;
    private LiveAndBackDebug liveAndBackDebug;
    private String liveid;

    public LecAdvertBll(Activity context) {
        this.context = context;
        liveAndBackDebug = ProxUtil.getProxUtil().get(context, LiveAndBackDebug.class);
    }

    public void setLecAdvertHttp(LecAdvertHttp lecAdvertHttp) {
        this.lecAdvertHttp = lecAdvertHttp;
    }

    public void setLiveid(String liveid) {
        this.liveid = liveid;
    }

    public void initView(LiveViewAction liveViewAction, boolean isLand) {
        this.liveViewAction = liveViewAction;
        if (lecAdvertager != null) {
            ViewGroup group = (ViewGroup) lecAdvertager.getRootView().getParent();
            if (group != null) {
                group.removeView(lecAdvertager.getRootView());
            }
            if (!isLand) {
                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                liveViewAction.addView(lecAdvertager.getRootView(), lp);
            } else {
                int step = lecAdvertager.getStep();
                if (step == 1) {
                    ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    liveViewAction.addView(lecAdvertager.getRootView(), lp);
                }
            }
        }
    }

    @Override
    public void start(final LecAdvertEntity lecAdvertEntity) {
        StableLogHashMap logHashMap = new StableLogHashMap("publishAdsMsgReceived");
        logHashMap.put("adsid", "" + lecAdvertEntity.id);
        logHashMap.addSno("3").addStable("2");
        logHashMap.addNonce("" + lecAdvertEntity.nonce);
        liveAndBackDebug.umsAgentDebugSys(eventid, logHashMap.getData());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (lecAdvertager != null) {
                    entities.add(lecAdvertEntity);
                    return;
                }
//                PageDataLoadEntity mPageDataLoadEntity = new PageDataLoadEntity(lecAdvertager.getRootView(), R.id.fl_livelec_advert_content, DataErrorManager.IMG_TIP_BUTTON);
//                PageDataLoadManager.newInstance().loadDataStyle(mPageDataLoadEntity.beginLoading());
                lecAdvertHttp.getAdOnLL(lecAdvertEntity, new AbstractBusinessDataCallBack() {
                    @Override
                    public void onDataSucess(Object... objData) {
                        // 刷新广告列表数据
                        Intent intent = new Intent();
                        intent.setAction("refreshadvertisementlist");
                        context.sendBroadcast(intent);
                        if (lecAdvertager != null) {
                            return;
                        }
                        if (lecAdvertEntity.isLearn == 1) {
                            // 添加不弹出广告的日志
                            StableLogHashMap logHashMap = new StableLogHashMap("interactiveAdsShown");
                            logHashMap.put("adsid", "" + lecAdvertEntity.id);
                            logHashMap.addSno("4").addStable("1").addExN();
                            logHashMap.addNonce("" + lecAdvertEntity.nonce);
                            logHashMap.put("extra", "此广告已报名");
                            liveAndBackDebug.umsAgentDebugSys(eventid, logHashMap.getData());
                            return;
                        }
                        if ("0".equals(lecAdvertEntity.limit)) {
                            // 已报满的情况也不弹出广告
                            StableLogHashMap logHashMap = new StableLogHashMap("interactiveAdsShown");
                            logHashMap.put("adsid", "" + lecAdvertEntity.id);
                            logHashMap.addSno("4").addStable("1").addExN();
                            logHashMap.addNonce("" + lecAdvertEntity.nonce);
                            logHashMap.put("extra", "此广告已报满");
                            liveAndBackDebug.umsAgentDebugSys(eventid, logHashMap.getData());
                            return;
                        }
                        lecAdvertager = new LecAdvertPager(context, lecAdvertEntity, LecAdvertBll.this, liveid);
                        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        liveViewAction.addView(lecAdvertager.getRootView(), lp);
                        lecAdvertager.initStep1();
                        LecAdvertLog.sno4(lecAdvertEntity, liveAndBackDebug);
                        // 添加成功弹出广告的日志
                        StableLogHashMap logHashMap = new StableLogHashMap("interactiveAdsShown");
                        logHashMap.put("adsid", "" + lecAdvertEntity.id);
                        logHashMap.addSno("4").addStable("1").addExY();
                        logHashMap.addNonce("" + lecAdvertEntity.nonce);
                        logHashMap.put("extra", "成功弹出广告");
                        liveAndBackDebug.umsAgentDebugSys(eventid, logHashMap.getData());
                    }

                    @Override
                    public void onDataFail(int errStatus, String failMsg) {
                        // 添加不弹出广告的日志
                        StableLogHashMap logHashMap = new StableLogHashMap("interactiveAdsShown");
                        logHashMap.put("adsid", "" + lecAdvertEntity.id);
                        logHashMap.addSno("4").addStable("1").addExN();
                        logHashMap.addNonce("" + lecAdvertEntity.nonce);
                        logHashMap.put("extra", "接口返回数据失败");
                        liveAndBackDebug.umsAgentDebugSys(eventid, logHashMap.getData());
                    }
                });
            }
        });
    }

    @Override
    public void close(boolean land) {
        if (lecAdvertager != null) {
            liveViewAction.removeView(lecAdvertager.getRootView());
            lecAdvertager = null;
            if (context instanceof ActivityChangeLand && entities.isEmpty()) {
                ActivityChangeLand activityChangeLand = (ActivityChangeLand) context;
                activityChangeLand.setAutoOrientation(true);
            }
        }
        if (!entities.isEmpty()) {
            LecAdvertEntity lecAdvertEntity = entities.remove(0);
            start(lecAdvertEntity);
        }
    }

    @Override
    public void onPaySuccess(LecAdvertEntity lecAdvertEntity) {
        logger.d("onPaySuccess:lecAdvertEntity=" + lecAdvertEntity.course_id);
    }

}
