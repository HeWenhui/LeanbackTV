package com.xueersi.parentsmeeting.modules.livevideo.primaryclass.business;

import android.app.Activity;
import android.widget.RelativeLayout;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.business.UserBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.TopicAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.entity.PrimaryClassEntity;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.entity.TeamInfo;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.entity.TeamMember;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.http.PrimaryClassHttp;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.pager.PrimaryItemPager;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.pager.PrimaryItemView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicBoolean;

public class PrimaryClassIrcBll extends LiveBaseBll implements NoticeAction, TopicAction {
    PrimaryClassHttp primaryClassHttp;
    PrimaryItemView primaryItemView;
    PrimaryClassEntity primaryClassEntity;
    String classId;

    public PrimaryClassIrcBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
    }

    @Override
    public void onLiveInited(final LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        classId = getInfo.getStudentLiveInfo().getClassId();
        getPrimaryClassHttp().reportUserAppStatus(classId, getInfo.getStuId(), "1");
        getMyTeamInfo();
    }

    private void getMyTeamInfo() {
        getPrimaryClassHttp().getMyTeamInfo(classId, mGetInfo.getStuId(), UserBll.getInstance().getMyUserInfoEntity().getPsimId(), new AbstractBusinessDataCallBack() {
            @Override
            public void onDataSucess(Object... objData) {
                if (primaryClassEntity != null) {
                    return;
                }
                primaryClassEntity = (PrimaryClassEntity) objData[0];
                if (primaryItemView != null) {
                    primaryItemView.onTeam(mGetInfo.getStuId(), primaryClassEntity);
                }
            }
        });
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
    }

    @Override
    public void initView(RelativeLayout bottomContent, AtomicBoolean mIsLand) {
        super.initView(bottomContent, mIsLand);
        PrimaryClassInterIml primaryClassInterIml = new PrimaryClassInterIml();
        PrimaryItemPager primaryItemPager = new PrimaryItemPager(activity, mContentView);
        primaryItemPager.setPrimaryClassInter(primaryClassInterIml);
        rlMessageBottom.addView(primaryItemPager.getRootView());
        primaryItemView = primaryItemPager;
    }

    class PrimaryClassInterIml implements PrimaryClassInter {

        @Override
        public void reportNaughtyBoy(final TeamMember entity, final ReportNaughtyBoy reportNaughtyBoy) {
            TeamInfo teamInfo = primaryClassEntity.getTeamInfo();
            getPrimaryClassHttp().reportNaughtyBoy(classId, mGetInfo.getStuId(), mGetInfo.getStuName(), "" + entity.getStuId(), entity.getStuName(), "", primaryClassEntity.getRoomId(), teamInfo.getTeamName(), teamInfo.getTeamId(), new AbstractBusinessDataCallBack() {
                @Override
                public void onDataSucess(Object... objData) {
                    reportNaughtyBoy.onReport(entity);
                }
            });
        }
    }

    public PrimaryClassHttp getPrimaryClassHttp() {
        if (primaryClassHttp == null) {
            primaryClassHttp = new PrimaryClassHttp(getHttpManager());
        }
        return primaryClassHttp;
    }

    @Override
    public void onTopic(LiveTopic liveTopic, JSONObject jsonObject, boolean modeChange) {
        try {
            JSONObject mmedia_status = jsonObject.getJSONObject("mmedia_status");
            boolean audio_status = mmedia_status.getBoolean("audio_status");
            boolean video_status = mmedia_status.getBoolean("video_status");
            primaryItemView.onMessage(audio_status, video_status);
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
                if (primaryClassEntity == null) {
                    getMyTeamInfo();
                }
            }
            break;
            case XESCODE.TEAM_PK_GROUP: {
                try {
                    String status = data.getString("status");
                    if (primaryClassEntity == null) {
                        getMyTeamInfo();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            break;
        }
    }

    @Override
    public int[] getNoticeFilter() {
        return new int[]{XESCODE.TEAM_PK_MESSAGE, XESCODE.TEAM_PK_GROUP, XESCODE.CLASSBEGIN};
    }

}
