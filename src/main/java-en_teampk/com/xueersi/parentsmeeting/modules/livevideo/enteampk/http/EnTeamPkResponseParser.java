package com.xueersi.parentsmeeting.modules.livevideo.enteampk.http;

import android.content.Context;

import com.tencent.bugly.crashreport.CrashReport;
import com.xueersi.common.business.sharebusiness.config.LiveVideoBusinessConfig;
import com.xueersi.common.http.HttpResponseParser;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.logerhelper.MobAgent;
import com.xueersi.common.logerhelper.XesMobAgent;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.config.EnglishPk;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.EnTeamPkRankEntity;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.PkTeamEntity;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.TeamMemberEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.AddPersonAndTeamEnergyEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.AllRankEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ArtsExtLiveInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassChestEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassmateEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.CoursewareInfoEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.DeviceDetectionEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.GoldTeamStatus;
import com.xueersi.parentsmeeting.modules.livevideo.entity.HalfBodyLiveStudyInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LearnReportEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo.FollowTypeEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo.StudentLiveInfoEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo.TestInfoEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic.RoomStatusEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic.TopicEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.MoreChoice;
import com.xueersi.parentsmeeting.modules.livevideo.entity.MyRankEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity.PlayserverEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RankEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.SpeechEvalEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StarAndGoldEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StudentChestEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StudentCoinAndTotalEnergyEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StudentPkResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StudyInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TalkConfHost;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamEnergyAndContributionStarEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamPkAdversaryEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamPkTeamInfoEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity.ExcellentListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity.LikeListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity.LikeProbabilityEntity;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity.MinimarketListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity.PraiseListStudentEntity;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity.PraiseListTeamEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.ScienceStaticConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class EnTeamPkResponseParser extends HttpResponseParser {
    static String TAG = "EnTeamPkResponseParser";

    public EnTeamPkResponseParser() {
    }

    public ArrayList<InetSocketAddress> parseTcpDispatch(ResponseEntity responseEntity) {
        ArrayList<InetSocketAddress> addresses = new ArrayList<>();
        try {
            JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
            JSONArray array = jsonObject.getJSONArray("data");
            for (int i = 0; i < array.length(); i++) {
                String hostAndIpStr = array.getString(i);
                String[] hostAndIp = hostAndIpStr.split(":");
                InetSocketAddress inetSocketAddress = new InetSocketAddress(hostAndIp[0], Integer.parseInt(hostAndIp[1]));
                addresses.add(inetSocketAddress);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return addresses;
    }

    public ArrayList<TeamMemberEntity> parseGetStuActiveTeam(ResponseEntity responseEntity) {
        ArrayList<TeamMemberEntity> entities = new ArrayList<>();
        try {
            JSONArray array = (JSONArray) responseEntity.getJsonObject();
            for (int i = 0; i < array.length(); i++) {
                JSONObject teamObj = array.getJSONObject(i);
                TeamMemberEntity teamMemberEntity = new TeamMemberEntity();
                teamMemberEntity.id = teamObj.optInt("stu_id");
                teamMemberEntity.name = teamObj.optString("stu_name");
                teamMemberEntity.headurl = teamObj.optString("stu_head");
                entities.add(teamMemberEntity);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return entities;
    }
}
