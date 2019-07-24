package com.xueersi.parentsmeeting.modules.livevideo.achievement.business;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.achievement.page.EnAchievePager;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.AimRealTimeValEntity;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.BetterMeEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveViewAction;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.EnTeamPkRankEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StarAndGoldEntity;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.StartInteractLog;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

import java.util.ArrayList;

/**
 * Created by linyuqiang on 2018/11/6.
 * 小英本场成就
 */
public class LiveAchievementEngBll implements StarInteractAction, EnPkInteractAction ,BetterMeInteractAction{
    private String TAG = "LiveAchievementEngBll";
    protected Logger logger = LiveLoggerFactory.getLogger(this.getClass().getSimpleName());
    private LiveGetInfo mLiveGetInfo;
    private Activity activity;
    private EnAchievePager enAchievePager;
    private LiveAndBackDebug liveAndBackDebug;
    private LiveAchievementHttp liveAchievementHttp;
    private int starCount;

    public LiveAchievementEngBll(Activity activity, int liveType, LiveGetInfo mLiveGetInfo, boolean mIsLand) {
        this.activity = activity;
        this.mLiveGetInfo = mLiveGetInfo;
        starCount = mLiveGetInfo.getStarCount();
        mLiveGetInfo.getStarCount();
        liveAndBackDebug = ProxUtil.getProxUtil().get(activity, LiveAndBackDebug.class);
    }

    public void initView(LiveViewAction liveViewAction) {
        RelativeLayout relativeLayout = liveViewAction.findViewById(R.id.rl_livevideo_star_content);
        relativeLayout.setVisibility(View.VISIBLE);
        ViewGroup.LayoutParams layoutParams = relativeLayout.getLayoutParams();
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        relativeLayout.setLayoutParams(layoutParams);
        relativeLayout.setBackgroundColor(Color.TRANSPARENT);
        enAchievePager = new EnAchievePager(activity, relativeLayout, mLiveGetInfo);
        relativeLayout.addView(enAchievePager.getRootView());
    }

    public void setLiveAchievementHttp(LiveAchievementHttp liveAchievementHttp) {
        this.liveAchievementHttp = liveAchievementHttp;
    }

    private String mStarid;
    ArrayList<String> data;
    /**
     * 星星互动开始
     */
    private boolean statInteractStart = false;
    /**
     * 是不是像老师发送过，目前没用
     */
    boolean isSend = false;
    String myMsg;

    @Override
    public void onStarStart(ArrayList<String> data, String starid, String answer, String nonce) {
        this.mStarid = starid;
        this.data = data;
        statInteractStart = true;
        if ("".equals(answer)) {
            myMsg = null;
        } else {
            isSend = true;
            myMsg = answer;
        }
        StartInteractLog.starOpen(liveAndBackDebug, answer, mStarid, nonce);
    }

    @Override
    public void onStarStop(final String id, ArrayList<String> answer, String nonce) {
        statInteractStart = false;
        isSend = false;
        final String myAnswer = this.myMsg;
        this.myMsg = null;
        if (!answer.isEmpty() && myAnswer != null) {
            int receive = -1;
            for (int i = 0; i < answer.size(); i++) {
                if (myAnswer.equals(answer.get(i))) {
                    receive = i;
                    break;
                }
            }
            StartInteractLog.starClose(liveAndBackDebug, id, receive, myAnswer, mStarid, starCount);
            if (receive > -1) {
                liveAchievementHttp.setStuStarCount(1000, id, new AbstractBusinessDataCallBack() {
                    @Override
                    public void onDataSucess(Object... objData) {
                        starCount++;
                        mLiveGetInfo.setStarCount(mLiveGetInfo.getStarCount() + 1);
                        if (enAchievePager != null) {
                            enAchievePager.onStarAdd(1, 0, 0);
                        }
                        StartInteractLog.setStuStarCount(liveAndBackDebug, id, myAnswer, mStarid, starCount);
                    }

                    @Override
                    public void onDataFail(int errStatus, String failMsg) {
                        StartInteractLog.setStuStarCount(liveAndBackDebug, id, myAnswer, starCount, errStatus, failMsg);
                    }
                });
            }
        }
    }

    @Override
    public void onSendMsg(String msg) {
        if (statInteractStart) {
//            if (!isSend) {
//                if ("1".equals(msg) || "2".equals(msg)) {
//                    myMsg = msg;
//                    liveBll.sendStat(msg);
//                    isSend = true;
//                }
//            }
            for (int i = 0; i < data.size(); i++) {
                String str = data.get(i);
                if (str.equalsIgnoreCase(msg)) {
                    myMsg = msg.toLowerCase();
                    liveAchievementHttp.sendStat(i);
                    isSend = true;
                    StartInteractLog.sendStarAnswer(liveAndBackDebug, msg, mStarid);
                    break;
                }
            }
        }
    }

    @Override
    public void onGetStar(StarAndGoldEntity starAndGoldEntity) {
        if (enAchievePager != null) {
            enAchievePager.onGetStar(starAndGoldEntity);
        }
    }

    @Override
    public void onStarAdd(int star, float x, float y) {
        if (enAchievePager != null) {
            enAchievePager.onStarAdd(star, x, y);
        }
    }

    public void setVideoLayout(LiveVideoPoint liveVideoPoint) {
        if (enAchievePager != null) {
            enAchievePager.setVideoLayout(liveVideoPoint);
        }
    }

    @Override
    public void onEnglishPk() {
        if (enAchievePager != null) {
            enAchievePager.onEnglishPk();
        }
    }

    @Override
    public void updateEnpk(EnTeamPkRankEntity enTeamPkRankEntity) {
        if (enAchievePager != null) {
            enAchievePager.updateEnpk(enTeamPkRankEntity);
        }
    }

    @Override
    public void onBetterMeUpdate(AimRealTimeValEntity aimRealTimeValEntity, boolean isShowBubble) {
        if (enAchievePager != null) {
            enAchievePager.onBetterMeUpdate(aimRealTimeValEntity, isShowBubble);
        }
    }

    @Override
    public void onReceiveBetterMe(BetterMeEntity betterMeEntity, boolean isShowBubble) {
        if (enAchievePager != null) {
            enAchievePager.onReceiveBetterMe(betterMeEntity, isShowBubble);
        }
    }
}