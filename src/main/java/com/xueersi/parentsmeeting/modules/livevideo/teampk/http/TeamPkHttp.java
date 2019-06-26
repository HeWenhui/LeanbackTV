package com.xueersi.parentsmeeting.modules.livevideo.teampk.http;

import android.content.Context;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.HttpRequestParams;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamPkTeamInfoEntity;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.config.PrimaryClassConfig;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;

public class TeamPkHttp {
    String TAG = "TeamPkHttp";
    private Logger logger = LiveLoggerFactory.getLogger(TAG);
    private LiveHttpManager liveHttpManager;
    private TeamPKHttpResponseParser teamPKHttpResponseParser;

    public TeamPkHttp(Context context, LiveHttpManager liveHttpManager) {
        this.liveHttpManager = liveHttpManager;
        teamPKHttpResponseParser = new TeamPKHttpResponseParser(context);
    }

    public TeamPKHttpResponseParser getTeamPKHttpResponseParser() {
        return teamPKHttpResponseParser;
    }

    public void getMyTeamInfo(String classId, String stuId, String psuser, HttpCallBack requestCallBack) {
        final HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("classId", "" + classId);
        params.addBodyParam("stuId", "" + stuId);
        params.addBodyParam("psuser", "" + psuser);
        liveHttpManager.setDefaultParameter(params);
        liveHttpManager.sendPost(PrimaryClassConfig.URL_LIVE_GET_MY_TEAM_INFO, params, requestCallBack);
    }


    /**
     * 获取分队信息
     *
     * @param classId
     * @param teamId
     * @param requestCallBack
     */
    public void getTeamInfo(String id, String classId, String teamId, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        liveHttpManager.setDefaultParameter(params);
        params.addBodyParam("classId", classId);
        params.addBodyParam("teamId", teamId);
        liveHttpManager.sendPost(liveHttpManager.getLiveVideoSAConfigInner().URL_TEMPK_PKTEAMINFO, params, requestCallBack);
    }

    /**
     * 学生获取自己宝箱
     *
     * @param isWin
     * @param classId
     * @param teamId
     * @param stuId
     * @param isAIPartner 是否是 Ai伴侣直播间
     */
    public void getStuChest(int isWin, String classId, String teamId, String stuId, String liveId, boolean
            isAIPartner, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        liveHttpManager.setDefaultParameter(params);
        params.addBodyParam("classId", classId);
        params.addBodyParam("teamId", teamId);
        params.addBodyParam("isWin", isWin + "");
        params.addBodyParam("stuId", stuId);
        params.addBodyParam("isAIPartner", isAIPartner ? "1" : "0");
        liveHttpManager.sendPost(liveHttpManager.getLiveVideoSAConfigInner().URL_TEMPK_GETSTUCHESTURL + "/" + liveId, params, requestCallBack);

    }

    /**
     * 每题pk 结果
     *
     * @param liveId
     * @param teamId
     * @param classId
     * @param requestCallBack
     */
    public void stuPKResult(String liveId, String teamId, String classId, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        liveHttpManager.setDefaultParameter(params);
        params.addBodyParam("classId", classId);
        params.addBodyParam("teamId", teamId);
        liveHttpManager.sendPost(liveHttpManager.getLiveVideoSAConfigInner().URL_TEMPK_STUPKRESULT + "/" + liveId, params, requestCallBack);
    }

    /**
     * 获取战队开宝箱结果
     *
     * @param liveId
     * @param stuId
     * @param teamId
     * @param classId
     * @param isAIPartner
     */
    public void getClassChestResult(String liveId, String stuId, String teamId, String classId, boolean isAIPartner,
                                    HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        liveHttpManager.setDefaultParameter(params);
        params.addBodyParam("classId", classId);
        params.addBodyParam("teamId", teamId);
        params.addBodyParam("stuId", stuId);
        params.addBodyParam("isAIPartner", isAIPartner ? "1" : "0");
        liveHttpManager.sendPost(liveHttpManager.getLiveVideoSAConfigInner().URL_TEMPK_GETCLASSCHESTRESULT + "/" + liveId, params, requestCallBack);
    }

    /**
     * 投票 能量
     *
     * @param liveId
     * @param teamId
     * @param classId
     * @param stuId
     * @param requestCallBack
     */
    public void addPersonAndTeamEnergy(String liveId, int addEnergy, String teamId, String classId, String stuId,
                                       String releaseId,HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        liveHttpManager.setDefaultParameter(params);
        params.addBodyParam("classId", classId);
        params.addBodyParam("teamId", teamId);
        params.addBodyParam("stuId", stuId);
        params.addBodyParam("addEnergy", addEnergy + "");
        params.addBodyParam("releaseId", releaseId + "");
        liveHttpManager.sendPost(liveHttpManager.getLiveVideoSAConfigInner().URL_TEMPK_ADDPERSONANDTEAMENERGY + "/" + liveId, params, requestCallBack);
    }


    /**
     * 理科战队pk  获取战队成员信息
     *
     * @param classId
     * @param teamId
     * @param httpCallBack
     */
    public void getTeamMates(String classId, String teamId, HttpCallBack httpCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        liveHttpManager.setDefaultParameter(params);
        params.addBodyParam("classId", classId);
        params.addBodyParam("teamId", teamId);
        liveHttpManager.sendPost(liveHttpManager.getLiveVideoSAConfigInner().URL_TEAMPK_GETTEAMMATES, params, httpCallBack);
    }

    /**
     * 获取pk 对手信息
     *
     * @param classId
     * @param teamId
     */
    public void getPkAdversary(String classId, String teamId, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        liveHttpManager.setDefaultParameter(params);
        params.addBodyParam("classId", classId);
        params.addBodyParam("teamId", teamId);
        liveHttpManager.sendPost(liveHttpManager.getLiveVideoSAConfigInner().URL_TEMPK_MATCHTEAM, params, requestCallBack);
    }


    /**
     * 每题战队能量 和贡献之星
     *
     * @param liveId
     * @param teamId
     * @param classId
     * @param stuId
     * @param tests
     * @param ctId            互动课件或者互动题时 testPlan= ''; 测试卷请求时testId= ' '
     * @param requestCallBack
     */
    public void teamEnergyNumAndContributionmulStar(String liveId, String teamId, String classId, String stuId,
                                                    String tests,
                                                    String ctId, String pSrc, HttpCallBack requestCallBack) {

        HttpRequestParams params = new HttpRequestParams();
        liveHttpManager.setDefaultParameter(params);
        params.addBodyParam("classId", classId);
        params.addBodyParam("teamId", teamId);
        params.addBodyParam("stuId", stuId);
        params.addBodyParam("tests", tests + "");
        params.addBodyParam("ctId", ctId + "");
        params.addBodyParam("pSrc", pSrc + "");
        liveHttpManager.sendPost(liveHttpManager.getLiveVideoSAConfigInner().URL_TEMPK_TEAMENERGYNUMANDCONTRIBUTIONSTARMUL + "/" + liveId, params,
                requestCallBack);
    }

    /**
     * 每题战队能量 和贡献之星
     *
     * @param liveId
     * @param teamId
     * @param classId
     * @param stuId
     * @param testId
     * @param testPlan        互动课件或者互动题时 testPlan= ''; 测试卷请求时testId= ' '
     * @param requestCallBack
     */
    public void teamEnergyNumAndContributionStar(String liveId, String teamId, String classId, String stuId, String
            testId,
                                                 String testPlan, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        liveHttpManager.setDefaultParameter(params);
        params.addBodyParam("classId", classId);
        params.addBodyParam("teamId", teamId);
        params.addBodyParam("stuId", stuId);
        params.addBodyParam("testId", testId);
        params.addBodyParam("testPlan", testPlan);
        liveHttpManager.sendPost(liveHttpManager.getLiveVideoSAConfigInner().URL_TEMPK_TEAMENERGYNUMANDCONTRIBUTIONSTAR + "/" + liveId, params,
                requestCallBack);
    }


    /**
     * 请求 学生当前场次 的总能量值 和自己金币 及对手总能量值
     *
     * @param liveId
     * @param teamId
     * @param classId
     * @param stuId
     * @param requestCallBack
     */
    public void liveStuGoldAndTotalEnergy(String liveId, String teamId, String classId, String stuId, HttpCallBack
            requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        liveHttpManager.setDefaultParameter(params);
        params.addBodyParam("classId", classId);
        params.addBodyParam("teamId", teamId);
        params.addBodyParam("stuId", stuId);
        liveHttpManager.sendPost(liveHttpManager.getLiveVideoSAConfigInner().URL_TEMPK_LIVESTUGOLDANDTOTALENERGY + "/" + liveId, params, requestCallBack);
    }

    /**
     * 小理战队PK 二期 获取明星榜
     *
     * @param liveId
     * @param classId
     * @param teamId
     * @param requestCallBack
     */
    public void getTeamPkStarStudents(String liveId, String classId, String courseId, String teamId, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        liveHttpManager.setDefaultParameter(params);
        params.addBodyParam("classId", classId);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("courseId", courseId);
        params.addBodyParam("teamId", teamId);
        liveHttpManager.sendPost(liveHttpManager.getLiveVideoSAConfigInner().URL_TEMPK_GETSTARSTUDENTS, params, requestCallBack);
    }

    /**
     * 小理战队PK 二期 获取黑马榜
     *
     * @param liveId
     * @param classId
     * @param requestCallBack
     */
    public void getTeamPkProgressStudent(String liveId, String classId, String courseId, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("classId", classId);
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("courseId", courseId);
        liveHttpManager.setDefaultParameter(params);
        liveHttpManager.sendPost(liveHttpManager.getLiveVideoSAConfigInner().URL_TEMPK_GETPROGRESSSTU, params, requestCallBack);
    }

    public TeamPkTeamInfoEntity setOldTeamPkTeamInfo(ResponseEntity responseEntity) {
        TeamPkTeamInfoEntity teamInfoEntity = teamPKHttpResponseParser.parseTeamInfoPrimary(responseEntity);
        if (teamInfoEntity != null && teamInfoEntity.getTeamInfo() != null) {
            teamPKHttpResponseParser.setFromLocal(true);
            teamInfoEntity.getTeamInfo().setFromLocal(true);
        }
        return teamInfoEntity;
    }
}
