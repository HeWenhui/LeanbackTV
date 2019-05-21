package com.xueersi.parentsmeeting.modules.livevideo.primaryclass.business;

import android.app.Activity;
import android.text.TextUtils;
import android.widget.RelativeLayout;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.business.UserBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveEventBus;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.TopicAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamMate;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamPkTeamInfoEntity;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.http.PrimaryClassHttp;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.pager.PrimaryItemPager;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.pager.PrimaryItemView;
import com.xueersi.parentsmeeting.modules.livevideo.teampk.event.TeamPkTeamInfoEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

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
        getPrimaryClassHttp().reportUserAppStatus(classId, getInfo.getStuId(), "1");
//        getMyTeamInfo();
        LiveEventBus.getDefault(mContext).register(this);
    }

    private void getMyTeamInfo() {
        getPrimaryClassHttp().getMyTeamInfo(classId, mGetInfo.getStuId(), UserBll.getInstance().getMyUserInfoEntity().getPsimId(), new AbstractBusinessDataCallBack() {
            @Override
            public void onDataSucess(Object... objData) {
                if (teamPkTeamInfoEntity != null) {
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
    public void onScineceAnswerResutlEvent(TeamPkTeamInfoEvent event) {
        logger.e("onScineceAnswerResutlEvent" + event);
        LiveEventBus.getDefault(mContext).unregister(this);
        if (teamPkTeamInfoEntity != null) {
            return;
        }
        teamPkTeamInfoEntity = event.getTeamInfoEntity();
        if (primaryItemView != null) {
            primaryItemView.onTeam(mGetInfo.getStuId(), teamPkTeamInfoEntity.getTeamInfo());
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
        rlMessageBottom.addView(primaryItemPager.getRootView());
        primaryItemView = primaryItemPager;
    }

    class PrimaryClassInterIml implements PrimaryClassInter {

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
                boolean audio_status = mmedia_status.getBoolean("audio_status");
                boolean video_status = mmedia_status.getBoolean("video_status");
                primaryItemView.onMessage(audio_status, video_status);
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
