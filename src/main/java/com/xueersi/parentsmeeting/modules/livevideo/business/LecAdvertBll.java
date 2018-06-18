package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.content.Context;
import android.content.Intent;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.parentsmeeting.event.MiniEvent;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LecAdvertEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.page.LecAdvertPager;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.LecAdvertLog;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * Created by lyqai on 2018/1/15.
 */

public class LecAdvertBll implements LecAdvertAction, LecAdvertPagerClose {
    String TAG = "LecAdvertBll";
    String eventid = LiveVideoConfig.LEC_ADS;
    Context context;
    RelativeLayout bottomContent;
    LecAdvertPager lecAdvertager;
    ArrayList<LecAdvertEntity> entities = new ArrayList<>();
    LiveBll liveBll;
    String liveid;

    public LecAdvertBll(Context context) {
        this.context = context;
    }

    public void setLiveBll(LiveBll liveBll) {
        this.liveBll = liveBll;
    }

    public void setLiveid(String liveid) {
        this.liveid = liveid;
    }

    public void initView(RelativeLayout bottomContent, boolean isLand) {
        this.bottomContent = bottomContent;
        if (lecAdvertager != null) {
            ViewGroup group = (ViewGroup) lecAdvertager.getRootView().getParent();
            if (group != null) {
                group.removeView(lecAdvertager.getRootView());
            }
            if (!isLand) {
                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                bottomContent.addView(lecAdvertager.getRootView(), lp);
            } else {
                int step = lecAdvertager.getStep();
                if (step == 1) {
                    ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    bottomContent.addView(lecAdvertager.getRootView(), lp);
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
        liveBll.umsAgentDebugSys(eventid, logHashMap.getData());
        bottomContent.post(new Runnable() {
            @Override
            public void run() {
                if (lecAdvertager != null) {
                    entities.add(lecAdvertEntity);
                    return;
                }
//                PageDataLoadEntity mPageDataLoadEntity = new PageDataLoadEntity(lecAdvertager.getRootView(), R.id.fl_livelec_advert_content, DataErrorManager.IMG_TIP_BUTTON);
//                PageDataLoadManager.newInstance().loadDataStyle(mPageDataLoadEntity.beginLoading());
                liveBll.getAdOnLL(lecAdvertEntity, new AbstractBusinessDataCallBack() {
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
                            logHashMap.put("extra","此广告已报名");
                            liveBll.umsAgentDebugSys(eventid, logHashMap.getData());
                            return;
                        }
                        if("0".equals(lecAdvertEntity.limit)){
                            // 已报满的情况也不弹出广告
                            StableLogHashMap logHashMap = new StableLogHashMap("interactiveAdsShown");
                            logHashMap.put("adsid", "" + lecAdvertEntity.id);
                            logHashMap.addSno("4").addStable("1").addExN();
                            logHashMap.addNonce("" + lecAdvertEntity.nonce);
                            logHashMap.put("extra","此广告已报满");
                            liveBll.umsAgentDebugSys(eventid, logHashMap.getData());
                            return;
                        }
                        lecAdvertager = new LecAdvertPager(context, lecAdvertEntity, LecAdvertBll.this, liveid, liveBll);
                        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        bottomContent.addView(lecAdvertager.getRootView(), lp);
                        lecAdvertager.initStep1();
                        LecAdvertLog.sno4(lecAdvertEntity, liveBll);
                        // 添加成功弹出广告的日志
                        StableLogHashMap logHashMap = new StableLogHashMap("interactiveAdsShown");
                        logHashMap.put("adsid", "" + lecAdvertEntity.id);
                        logHashMap.addSno("4").addStable("1").addExY();
                        logHashMap.addNonce("" + lecAdvertEntity.nonce);
                        logHashMap.put("extra","成功弹出广告");
                        liveBll.umsAgentDebugSys(eventid, logHashMap.getData());
                    }

                    @Override
                    public void onDataFail(int errStatus, String failMsg) {
                        // 添加不弹出广告的日志
                        StableLogHashMap logHashMap = new StableLogHashMap("interactiveAdsShown");
                        logHashMap.put("adsid", "" + lecAdvertEntity.id);
                        logHashMap.addSno("4").addStable("1").addExN();
                        logHashMap.addNonce("" + lecAdvertEntity.nonce);
                        logHashMap.put("extra","接口返回数据失败");
                        liveBll.umsAgentDebugSys(eventid, logHashMap.getData());
                    }
                });
            }
        });
    }

    @Override
    public void close() {
        if (lecAdvertager != null) {
            bottomContent.removeView(lecAdvertager.getRootView());
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
        Loger.d(TAG, "onPaySuccess:lecAdvertEntity=" + lecAdvertEntity.course_id);
    }
}
