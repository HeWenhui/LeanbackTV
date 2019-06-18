package com.xueersi.parentsmeeting.modules.livevideoOldIJK.primaryclass.business;

import android.app.Activity;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.tencent.bugly.crashreport.CrashReport;
import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.business.UserBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveEventBus;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PkAddEnergy;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PkUpdatePkState;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamMate;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamPkTeamInfoEntity;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.business.PrimaryClassInter;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.business.PrimaryPermissionCheck;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.http.PrimaryClassHttp;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.pager.PrimaryItemPager;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.pager.PrimaryItemView;
import com.xueersi.parentsmeeting.modules.livevideo.teampk.event.TeamPkTeamInfoEvent;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.TopicAction;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class PrimaryClassIrcBll extends LiveBaseBll implements NoticeAction, TopicAction {
    PrimaryClassHttp primaryClassHttp;
    PrimaryItemView primaryItemView;
    TeamPkTeamInfoEntity teamPkTeamInfoEntity;
    String classId;

    public PrimaryClassIrcBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
    }

    @Override
    public void onLiveInited(final LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        classId = getInfo.getStudentLiveInfo().getClassId();
        permissionCheck();
//        getMyTeamInfo();
        LiveEventBus.getDefault(mContext).register(this);
        primaryItemView.onLiveInited(getInfo);
    }

    private void permissionCheck() {
        mRootView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mRootView.getViewTreeObserver().removeOnPreDrawListener(this);
                int status = PrimaryPermissionCheck.getStatus(activity, new PrimaryPermissionCheck.OnPermissionFinish() {
                    @Override
                    public void onFinish(boolean allOk) {
                        getPrimaryClassHttp().reportUserAppStatus(classId, mGetInfo.getStuId(), allOk ? 1 : 0);
                        if (primaryItemView != null) {
                            primaryItemView.onCheckPermission();
                        }
                    }
                });
                if (status == 1) {
                    getPrimaryClassHttp().reportUserAppStatus(classId, mGetInfo.getStuId(), 1);
                }
                return false;
            }
        });
    }

    private void getMyTeamInfo() {
        getPrimaryClassHttp().getMyTeamInfo(classId, mGetInfo.getStuId(), UserBll.getInstance().getMyUserInfoEntity().getPsimId(), new AbstractBusinessDataCallBack() {
            @Override
            public void onDataSucess(Object... objData) {
                if (teamPkTeamInfoEntity != null) {
                    TeamPkTeamInfoEntity teamPkTeamInfoEntity2 = (TeamPkTeamInfoEntity) objData[0];
                    try {
                        List<TeamMate> result1 = teamPkTeamInfoEntity.getTeamInfo().getResult();
                        List<TeamMate> result2 = teamPkTeamInfoEntity2.getTeamInfo().getResult();
                        if (result1.size() != result2.size()) {
                            primaryItemView.updateTeam(teamPkTeamInfoEntity2.getTeamInfo());
                        }
                    } catch (Exception e) {
                        logger.e("onTeamPkTeamInfoEvent:event=" + e);
                        CrashReport.postCatchedException(new LiveException(TAG, e));
                    }
                    teamPkTeamInfoEntity = (TeamPkTeamInfoEntity) objData[0];
                    return;
                }
                teamPkTeamInfoEntity = (TeamPkTeamInfoEntity) objData[0];
                if (primaryItemView != null) {
                    primaryItemView.onTeam(mGetInfo.getStuId(), teamPkTeamInfoEntity.getTeamInfo());
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTeamPkTeamInfoEvent(TeamPkTeamInfoEvent event) {
        logger.d("onTeamPkTeamInfoEvent:event=" + event);
        if (teamPkTeamInfoEntity != null) {
            TeamPkTeamInfoEntity teamPkTeamInfoEntity2 = event.getTeamInfoEntity();
            try {
                List<TeamMate> result1 = teamPkTeamInfoEntity.getTeamInfo().getResult();
                List<TeamMate> result2 = teamPkTeamInfoEntity2.getTeamInfo().getResult();
                if (result1.size() != result2.size()) {
                    primaryItemView.updateTeam(teamPkTeamInfoEntity2.getTeamInfo());
                }
            } catch (Exception e) {
                logger.e("onTeamPkTeamInfoEvent:event=" + e);
                CrashReport.postCatchedException(new LiveException(TAG, e));
            }
            teamPkTeamInfoEntity = teamPkTeamInfoEntity2;
            return;
        }
        teamPkTeamInfoEntity = event.getTeamInfoEntity();
        if (primaryItemView != null) {
            primaryItemView.onTeam(mGetInfo.getStuId(), teamPkTeamInfoEntity.getTeamInfo());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updatePkState(PkUpdatePkState event) {
        float ratio = event.getRatio();
        logger.e("updatePkState:enent=" + ratio);
        if (primaryItemView != null) {
            primaryItemView.updatePkState(ratio);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPkAddEnergy(PkAddEnergy event) {
        boolean first = event.isFirst();
        int energy = event.getEnergy();
        logger.e("onPkAddEnergy:enent=" + energy);
        if (primaryItemView != null) {
            primaryItemView.onAddEnergy(first, energy);
        }
    }

    @Override
    public void onModeChange(String oldMode, String mode, boolean isPresent) {
        super.onModeChange(oldMode, mode, isPresent);
        if (primaryItemView != null) {
            primaryItemView.onModeChange(mode);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (primaryItemView != null) {
            primaryItemView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (primaryItemView != null) {
            primaryItemView.onPause();
        }
    }

    @Override
    public void onDestory() {
        super.onDestory();
        if (primaryItemView != null) {
            primaryItemView.onDestroy();
        }
        LiveEventBus.getDefault(mContext).unregister(this);
    }

    @Override
    public void initView(RelativeLayout bottomContent, AtomicBoolean mIsLand) {
        super.initView(bottomContent, mIsLand);
        PrimaryClassInterIml primaryClassInterIml = new PrimaryClassInterIml();
        PrimaryItemPager primaryItemPager = new PrimaryItemPager(activity, mContentView, mLiveBll.getMode());
        primaryItemPager.setPrimaryClassInter(primaryClassInterIml);
        primaryItemView = primaryItemPager;
    }

    class PrimaryClassInterIml implements PrimaryClassInter {
        @Override
        public void getMyTeamInfo() {
            PrimaryClassIrcBll.this.getMyTeamInfo();
        }

        @Override
        public void reportNaughtyBoy(final TeamMate entity, final ReportNaughtyBoy reportNaughtyBoy) {
            TeamPkTeamInfoEntity.TeamInfoEntity teamInfo = teamPkTeamInfoEntity.getTeamInfo();
            getPrimaryClassHttp().reportNaughtyBoy(classId, mGetInfo.getStuId(), mGetInfo.getStuName(), "" + entity.getId(), entity.getName(), "", teamPkTeamInfoEntity.getTeamInfo().getRoomid(), teamInfo.getTeamName(), teamPkTeamInfoEntity.getTeamInfo().getTeamId(), new AbstractBusinessDataCallBack() {
                @Override
                public void onDataSucess(Object... objData) {
                    reportNaughtyBoy.onReport(entity);
                }

                @Override
                public void onDataFail(int errStatus, String failMsg) {
                    super.onDataFail(errStatus, failMsg);
                    reportNaughtyBoy.onReportError(entity);
                }
            });
        }
    }

    public PrimaryClassHttp getPrimaryClassHttp() {
        if (primaryClassHttp == null) {
            primaryClassHttp = new PrimaryClassHttp(activity, getHttpManager());
        }
        return primaryClassHttp;
    }

    @Override
    public void onTopic(LiveTopic liveTopic, JSONObject jsonObject, boolean modeChange) {
        try {
            if (jsonObject.has("mmedia_status")) {
                JSONObject mmedia_status = jsonObject.getJSONObject("mmedia_status");
                boolean video_status = mmedia_status.getBoolean("video_status");
                boolean audio_status = mmedia_status.getBoolean("audio_status");
                primaryItemView.onMessage(video_status, audio_status);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {
        switch (type) {
            case XESCODE.TEAM_PK_MESSAGE: {
                try {
                    int mmType = data.getInt("mmType");
                    boolean status = data.optBoolean("status", false);
                    primaryItemView.onMessage(mmType, status);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            break;
            case XESCODE.CLASSBEGIN: {
                if (teamPkTeamInfoEntity == null) {
                    getMyTeamInfo();
                }
            }
            break;
//            case XESCODE.TEAM_PK_GROUP: {
//                try {
//                    String status = data.getString("status");
//                    if (teamPkTeamInfoEntity == null) {
//                        getMyTeamInfo();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//            break;
        }
    }

    @Override
    public int[] getNoticeFilter() {
        return new int[]{XESCODE.TEAM_PK_MESSAGE, XESCODE.TEAM_PK_GROUP, XESCODE.CLASSBEGIN};
    }

}
