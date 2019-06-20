package com.xueersi.parentsmeeting.modules.livevideo.chpk.business;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.tencent.bugly.crashreport.CrashReport;
import com.xueersi.common.base.BasePager;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.entity.EnglishH5Entity;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.chpk.page.PkAnswerResultPager;
import com.xueersi.parentsmeeting.modules.livevideo.chpk.page.PkDispatchTeamPager;
import com.xueersi.parentsmeeting.modules.livevideo.chpk.page.PkOpenAwardPager;
import com.xueersi.parentsmeeting.modules.livevideo.chpk.page.PkTeamResultPager;
import com.xueersi.parentsmeeting.modules.livevideo.chpk.page.PkTeamSelectPager;
import com.xueersi.parentsmeeting.modules.livevideo.config.HalfBodyLiveConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.TeamPkConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveEventBus;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.TopicAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassChestEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StudentCoinAndTotalEnergyEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StudentPkResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamEnergyAndContributionStarEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamMate;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamPkAdversaryEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamPkTeamInfoEntity;
import com.xueersi.parentsmeeting.modules.livevideo.event.LiveRoomH5CloseEvent;
import com.xueersi.parentsmeeting.modules.livevideo.event.NativeVoteRusltulCloseEvent;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.redpackage.entity.RedPackageEvent;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.TeamPkLog;
import com.xueersi.parentsmeeting.modules.livevideo.teampk.event.TeamPkTeamInfoEvent;
import com.xueersi.parentsmeeting.modules.livevideo.teampk.http.TeamPkHttp;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.TeamPkStateLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * @author chekun
 * created  at 2018/4/12
 * 战队PK 相关业务处理
 */
public class ChinesePkBll extends LiveBaseBll implements NoticeAction, TopicAction {


    public static final String TEAMPK_URL_FIFTE = "http://addenergyandgold.com/";
    /**
     * 开宝箱类型 班级宝箱列表
     */
    private static final int CHEST_TYPE_CLASS = 1;
    /**
     * 开宝箱类型 学生自己宝箱
     */
    private static final int CHEST_TYPE_STUDENT = 2;
    /**
     * 投票题 奖励能量
     */
    private static final int VOTE_ADD_ENERGY = 3;
    private Activity mActivity;
    /**
     * 战队PK rootView
     */
    private RelativeLayout rlTeamPkContent;
    private TeamPkHttp teamPkHttp;
    private LiveHttpManager mHttpManager;
    private LiveGetInfo roomInitInfo;
    private boolean primaryClass = false;
    private LiveHttpResponseParser mHttpResponseParser;
    private TeamPkTeamInfoEntity teamInfoEntity;
    private BasePager mFocusPager;

    private static final String OPEN_STATE_OPEN = "1";

    private static final String OPEN_STATE_CLOSE = "0";
    @Deprecated
    private boolean isTopicHandled = false;

    private boolean isWin;

    private boolean newCourseWare;
    private EnglishH5Entity englishH5Entity;
    private TeamPkStateLayout pkStateRootView;
    /**
     * 直播间内答题 H5 答题结果页面关闭事件队列
     */
    private List<LiveRoomH5CloseEvent> h5CloseEvents;

    /**
     * 当前pk状态
     */
    private StudentCoinAndTotalEnergyEntity mCurrentPkState;

    /**
     * 是否是带碎片的直播间
     */
    private boolean isAIPartner;


    /**
     * 当前老师模式
     */
    private String mTeacherMode = LiveTopic.MODE_TRANING;
    /**
     * 战队成员信息
     **/
    private List<TeamMate> mTeamMates;
    private String savedTestId = "";
    private String savedTestPlan = "";

    public ChinesePkBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
        mActivity = context;
    }

    public LiveBll2 getLiveBll() {
        return mLiveBll;
    }


    public void setRoomInitInfo(LiveGetInfo roomInfo) {
        roomInitInfo = roomInfo;
    }

    public LiveGetInfo getRoomInitInfo() {
        return roomInitInfo;
    }


    private void attachToRootView() {
        initData();
        rlTeamPkContent = new RelativeLayout(mActivity);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
                .MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mRootView.addView(rlTeamPkContent, params);
        showPkStateLayout();
        registLayotListener();

    }


    /**
     * 是否是半身直播 直播间
     *
     * @return
     */
    public boolean isHalfBodyLiveRoom() {
        return roomInitInfo != null && (roomInitInfo.getPattern() == HalfBodyLiveConfig.LIVE_TYPE_HALFBODY || primaryClass);
    }


    /**
     * 显示 场次答题PK 结果
     * notice  topic  中通知调用
     */
    public void showPkResult() {
        HttpCallBack callback = new HttpCallBack() {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                StudentPkResultEntity resultEntity = mHttpResponseParser.parseStuPkResult(responseEntity);
                ChinesePkBll.this.isWin = resultEntity.getMyTeamResultInfo().getEnergy() >= resultEntity.getCompetitorResultInfo().getEnergy();
                showPkFinallyResult(resultEntity);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                super.onFailure(call, e);
            }
        };

        mHttpManager.stuCHPKResult(isHalfBodyLiveRoom(), mLiveBll.getLiveId(),
                getNewTeamId("stuCHPKResult"),
                roomInitInfo.getStudentLiveInfo().getClassId(),
                savedTestId,
                savedTestPlan,
                roomInitInfo.getUseSkin(),
                callback);
    }

    /**
     * 显示开宝箱场景
     */
    public void showOpenBoxScene(boolean isWin) {
        showAwardGetScene(CHEST_TYPE_STUDENT, null, isWin);
    }

    public boolean isWin() {
        return isWin;
    }

    /**
     * 显示 战队宝箱领取情况
     */
    public void showClassChest() {

        HttpCallBack callback = new HttpCallBack() {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                ClassChestEntity classChestEntity = mHttpResponseParser.parseClassChest(responseEntity);
                showAwardGetScene(CHEST_TYPE_CLASS, classChestEntity, isWin);
//                        TeamPkLog.showClassGoldInfo(mLiveBll, classChestEntity.isMe());
            }

            @Override
            public void onFailure(Call call, IOException e) {
                super.onFailure(call, e);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                super.onPmError(responseEntity);
            }
        };

        mHttpManager.getCHClassChestResult(isHalfBodyLiveRoom(), mLiveBll.getLiveId(),
                roomInitInfo.getStuId(),
                getNewTeamId("getCHClassChestResult"),
                roomInitInfo.getStudentLiveInfo().getClassId()
                , isAIPartner, roomInitInfo.getUseSkin(), callback);

    }

    @Deprecated
    public void setTopicHandled(boolean topicHandled) {
        isTopicHandled = topicHandled;
    }

    @Deprecated
    public boolean isTopicHandled() {
        return isTopicHandled;
    }

    private void initData() {
        mHttpResponseParser = new LiveHttpResponseParser(mActivity);
        EventBus.getDefault().register(this);

    }

    public LiveHttpResponseParser getmHttpResponseParser() {
        return mHttpResponseParser;
    }

    private boolean prepareSelcting;

    /**
     * 开启分队仪式
     */
    public void startTeamSelect(final boolean primary, final boolean showTeamSelectScene) {
        logger.e("====>startTeamSelect:");

        prepareSelcting = true;

        HttpCallBack callBack = new HttpCallBack() {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                if (primary) {
                    TeamPkTeamInfoEntity teamInfoEntityres = mHttpResponseParser.parseTeamInfoPrimary(responseEntity);
                    if (teamInfoEntityres == null) {
                        return;
                    }
                    teamInfoEntity = teamInfoEntityres;
                } else {
                    teamInfoEntity = mHttpResponseParser.parseTeamInfo(responseEntity);
                }
                if (showTeamSelectScene) {
                    showTeamSelectScene();
                }
                prepareSelcting = false;
                if (primary) {
                    LiveEventBus.getDefault(mContext).post(new TeamPkTeamInfoEvent(teamInfoEntity, responseEntity));
                    logger.d("getTeamInfo:runnables=" + runnables.size());
                    while (!runnables.isEmpty()) {
                        Runnable runnable = runnables.remove(0);
                        runnable.run();
                    }
                }
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                super.onPmError(responseEntity);
                prepareSelcting = false;
            }
        };
        if (primary) {
            getTeamPkHttp().getMyTeamInfo(roomInitInfo.getStudentLiveInfo().getClassId(),
                    roomInitInfo.getStuId(), UserBll.getInstance().getMyUserInfoEntity().getPsimId(), callBack);
        } else {
            mHttpManager.getCHTeamInfo(isHalfBodyLiveRoom(), roomInitInfo.getId(), roomInitInfo.getStudentLiveInfo().getClassId(), roomInitInfo.getStudentLiveInfo().getTeamId(), roomInitInfo.getUseSkin(), callBack);
        }
    }

    public TeamPkHttp getTeamPkHttp() {
        if (teamPkHttp == null) {
            teamPkHttp = new TeamPkHttp(mHttpManager);
        }
        return teamPkHttp;
    }

    public void stopTeamSelect() {
        if (mFocusPager != null && mFocusPager instanceof PkTeamSelectPager) {
            ((PkTeamSelectPager) mFocusPager).closeTeamSelectPager();
        } else if (mFocusPager != null && mFocusPager instanceof PkDispatchTeamPager) {
            ((PkDispatchTeamPager) mFocusPager).closeTeamSelectPager();
        }
    }

    public void initView(RelativeLayout bottomContent) {
        rlTeamPkContent = new RelativeLayout(mActivity);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.
                LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        bottomContent.addView(rlTeamPkContent, params);

    }

    /**
     * 显示分队仪式 场景
     */
    private void showTeamSelectScene() {
        if (mFocusPager == null || !(mFocusPager instanceof PkDispatchTeamPager)) {

            if (mFocusPager != null && mFocusPager instanceof PkTeamSelectPager) {
                return;
            }

            PkTeamSelectPager teamSelectPager = new PkTeamSelectPager(mActivity, this, roomInitInfo);
            addPager(teamSelectPager);
            teamSelectPager.setData(teamInfoEntity);
            teamSelectPager.startTeamSelect();
//            TeamPkLog.showCreateTeam(mLiveBll);
        }
    }

    /**
     * 显示 战队已选中 UI
     */
    private void showTeamSelectedSence() {
        if (mFocusPager == null || !(mFocusPager instanceof PkTeamSelectPager)) {
            PkTeamSelectPager teamSelectPager = new PkTeamSelectPager(mActivity, this, roomInitInfo);
            addPager(teamSelectPager);
            teamSelectPager.setData(teamInfoEntity);
            teamSelectPager.showTeamSelectedScene(true);
//            TeamPkLog.showCreateTeam(mLiveBll);
        }
    }

    /**
     * 显示开宝箱
     *
     * @param type
     * @param data
     * @param isWin
     */
    public void showAwardGetScene(int type, final Object data, final boolean isWin) {
        logger.e("======>showAwardGetScene called");
        //   if (mFocusPager == null || !(mFocusPager instanceof PkOpenAwardPager)) {
        if (mFocusPager == null || !(mFocusPager instanceof PkOpenAwardPager)) {
            logger.e("======>showAwardGetScene called 11111");

            if (type == CHEST_TYPE_CLASS) {
                //从pk结果页面直接跳到 贡献之星
                if (mFocusPager != null && mFocusPager instanceof PkTeamResultPager) {
                    ((PkTeamResultPager) mFocusPager).closePkResultPager();
                    rlTeamPkContent.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            PkOpenAwardPager awardGetPager = new PkOpenAwardPager(mActivity, ChinesePkBll.this);
                            addPager(awardGetPager);
                            awardGetPager.showClassChest((ClassChestEntity) data);
                        }
                    }, 1000);
                } else {
                    PkOpenAwardPager awardGetPager = new PkOpenAwardPager(mActivity, this);
                    addPager(awardGetPager);
                    awardGetPager.showClassChest((ClassChestEntity) data);
                }

            } else if (type == CHEST_TYPE_STUDENT) {
                rlTeamPkContent.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        PkOpenAwardPager awardGetPager = new PkOpenAwardPager(mActivity, ChinesePkBll.this);
                        addPager(awardGetPager);
                        awardGetPager.showBoxLoop();
                        logger.e("======>showAwardGetScene called 3333");
                    }
                }, 1000);
            }
        } else if (mFocusPager != null && (mFocusPager instanceof PkOpenAwardPager)) {
            //由开宝箱直接切换到幸运之星页面
            if (type == CHEST_TYPE_CLASS) {
                ((PkOpenAwardPager) mFocusPager).closeAwardPager();
                rlTeamPkContent.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        PkOpenAwardPager awardGetPager = new PkOpenAwardPager(mActivity, ChinesePkBll.this);
                        addPager(awardGetPager);
                        awardGetPager.showClassChest((ClassChestEntity) data);
                    }
                }, 1000);
            }
        }
    }

    /**
     * 显示分队进行中
     */
    public void showTeamSelecting() {
        if (prepareSelcting) {
            return;
        }

        if (mFocusPager != null && (mFocusPager instanceof PkTeamSelectPager)) {
            return;
        }

        PkDispatchTeamPager selectingPager = new PkDispatchTeamPager(mActivity, ChinesePkBll.this);
        addPager(selectingPager);
    }

    public void showPkSelectStart(final TeamPkAdversaryEntity data) {
        if (mFocusPager != null && (mFocusPager instanceof PkTeamResultPager)) {
            return;
        }

        PkTeamResultPager resultPager = new PkTeamResultPager(mActivity, ChinesePkBll.this);
        addPager(resultPager);
        resultPager.showPkAdversary(data);
    }

    /**
     * 学生 当场次答题pk 结果
     *
     * @param data
     */
    public void showPkFinallyResult(StudentPkResultEntity data) {
        if (mFocusPager != null && (mFocusPager instanceof PkTeamResultPager)) {
            return;
        }

        PkTeamResultPager resultPager = new PkTeamResultPager(mActivity, this);
        addPager(resultPager);
        resultPager.showFinalPkResult(data);
    }

    /**
     * 学生 每题的PK 结果
     */
    public void showPkEveryResult(TeamEnergyAndContributionStarEntity data) {
        if (mFocusPager != null && (mFocusPager instanceof PkTeamResultPager)) {
            return;
        }

        if (data != null) {
            PkTeamResultPager resultPager = new PkTeamResultPager(mActivity, this);
            addPager(resultPager);
            resultPager.showCurrentResult(data);
        }
    }

    /**
     * 关闭当前页面
     */
    public void closeCurrentPager() {
        if (mFocusPager != null) {
            rlTeamPkContent.post(new Runnable() {
                @Override
                public void run() {
                    if (mFocusPager != null) {
                        rlTeamPkContent.removeView(mFocusPager.getRootView());
                        mFocusPager = null;
                    }
                }
            });
        }
    }

    /**
     * 展示聊天 区域上方 战队pk 状态UI
     */
    private void showPkStateLayout() {
        // step 1
        ViewGroup viewGroup = (ViewGroup) mActivity.getWindow().getDecorView();
        pkStateRootView = viewGroup.findViewById(R.id.tpkL_teampk_pkstate_root);
        if (pkStateRootView != null) {
            pkStateRootView.setVisibility(View.VISIBLE);

            if (mCurrentPkState != null) {
                pkStateRootView.bindData(mCurrentPkState, false);
            }
        }

        // step 2  初始化 又测 pk 状态栏
        updatePkStateLayout(false);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRedPackageEvent(RedPackageEvent event) {
        if (event.getStateCode() == RedPackageEvent.STATE_CODE_SUCCESS) {
            if (mLiveBll.getLiveId().equals(event.getLiveId())) {
                updatePkStateLayout(false);
            }
        }
    }

    ArrayList<Runnable> runnables = new ArrayList<>();

    /**
     * 刷新pk状态栏
     *
     * @param showPopWindow 是否展示顶部  进度描述:领先，打平 .....
     */
    public void updatePkStateLayout(final boolean showPopWindow) {
        if (primaryClass) {
            if (teamInfoEntity != null) {
                getPkState(showPopWindow);
            } else {
                runnables.add(new Runnable() {
                    @Override
                    public void run() {
                        getPkState(showPopWindow);
                    }
                });
            }
        } else {
            getPkState(showPopWindow);
        }
    }


    /**
     * 显示实时答题 奖励
     */
    public void showAnswerQuestionAward(int goldNum, int energyNum, String id) {
        PkAnswerResultPager aqAwardPager = new PkAnswerResultPager(mActivity,
                PkAnswerResultPager.AWARD_TYPE_QUESTION, this);
        addPager(aqAwardPager);
        aqAwardPager.setData(goldNum, energyNum);
//        TeamPkLog.showAddPower(mLiveBll, id, energyNum + "");
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVoteResultUIColse(NativeVoteRusltulCloseEvent event) {
        int addEnergy = event.isStuVoted() ? VOTE_ADD_ENERGY : 0;
        showVoteEnergyAnim(addEnergy, event.getVoteId());
    }


    /**
     * 当时是否是全屏模式
     *
     * @return
     */
    public boolean isFullScreenMode() {
        boolean result = isHalfBodyLiveRoom() && mTeacherMode != null && mTeacherMode.equals(LiveTopic.MODE_CLASS);
        //Log.e("TeamPk","======>isFullScreenMode:"+result+":"+mTeacherMode);
        return result;
    }


    /**
     * @return
     */
    private int getRightMargin() {
        int returnValue = 0;
        if (!isFullScreenMode()) {
            returnValue = LiveVideoPoint.getInstance().getRightMargin();
        }
        return returnValue;
    }

    private void addPager(final BasePager aqAwardPager) {
        rlTeamPkContent.removeAllViews();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        int rightMargin = getRightMargin();
        params.rightMargin = rightMargin;
        rlTeamPkContent.addView(aqAwardPager.getRootView(), params);
        mFocusPager = aqAwardPager;
//        aqAwardPager.getRootView().setBackgroundColor(Color.BLACK);
    }

    private void registLayotListener() {
        rlTeamPkContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (!isFullScreenMode()) {
                    if (mFocusPager != null) {
                        int rightMargin = LiveVideoPoint.getInstance().getRightMargin();
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mFocusPager.getRootView()
                                .getLayoutParams();
                        if (rightMargin != params.rightMargin) {
                            params.rightMargin = rightMargin;
                            LayoutParamsUtil.setViewLayoutParams(mFocusPager.getRootView(), params);
                        }
                    }
                }
            }
        });
    }


    public void setVideoLayout(int width, int height) {
        final View contentView = mActivity.findViewById(android.R.id.content);
        final View actionBarOverlayLayout = (View) contentView.getParent();
        Rect r = new Rect();
        actionBarOverlayLayout.getWindowVisibleDisplayFrame(r);
        int screenWidth = (r.right - r.left);
        if (width > 0 && mFocusPager != null) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mFocusPager.getRootView()
                    .getLayoutParams();
            int wradio = (int) (LiveVideoConfig.VIDEO_HEAD_WIDTH * width / LiveVideoConfig.VIDEO_WIDTH);
            wradio += (screenWidth - width) / 2;
            if (wradio != params.rightMargin) {
                params.rightMargin = wradio;
                LayoutParamsUtil.setViewLayoutParams(mFocusPager.getRootView(), params);
            }
        }

    }


    /**
     * activity stop
     */
    @Override
    public void onStop() {
        if (mFocusPager != null) {
            mFocusPager.onStop();
        }
        logger.e("======>onStop");
    }


    @Override
    public void onPause() {
        super.onPause();
        logger.e("======>onStop");
    }

    @Override
    public void onResume() {

        if (mFocusPager != null) {
            mFocusPager.onResume();
        }

        logger.e("======>onResume");
    }


    @Override
    public void onDestory() {
        super.onDestory();
        logger.e("======>onDestory");
        if (mFocusPager != null) {
            mFocusPager.onDestroy();
            mFocusPager = null;
        }
        isTopicHandled = false;
        EventBus.getDefault().unregister(this);
    }


    private boolean isAvailable;

    @Override
    public void onLiveInited(LiveGetInfo data) {
        if (data != null && "1".equals(data.getIsAllowTeamPk())) {
            primaryClass = data.getPattern() == HalfBodyLiveConfig.LIVE_TYPE_HALFBODY_CLASS;
            mHttpManager = getHttpManager();
            setRoomInitInfo(data);
            attachToRootView();
            roomInitInfo = data;
            isAIPartner = roomInitInfo.getIsAIPartner() == 1;
            isAvailable = true;
            getTeamMates();
//            if(AppConfig.DEBUG){
//                showTeamSelecting();
//                startTeamSelect(true);
//            }
        } else {
            //不显示战队pk时，原来的战队Pk的位置由图片占据。
            showImgReplacePk();
        }
        this.mTeacherMode = mLiveBll.getMode();
    }


    /**
     * 获取战队成员信息
     */
    private void getTeamMates() {
        if (primaryClass) {
            getTeamPkHttp().getMyTeamInfo(roomInitInfo.getStudentLiveInfo().getClassId(),
                    roomInitInfo.getStuId(), UserBll.getInstance().getMyUserInfoEntity().getPsimId(), new HttpCallBack() {
                        @Override
                        public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                            TeamPkTeamInfoEntity teamInfoEntityres = mHttpResponseParser.parseTeamInfoPrimary(responseEntity);
                            if (teamInfoEntityres == null) {
                                return;
                            }
                            teamInfoEntity = teamInfoEntityres;
                            TeamPkTeamInfoEntity.TeamInfoEntity teamInfo = teamInfoEntity.getTeamInfo();
                            mTeamMates = teamInfo.getResult();
                            if (mTeamMates != null) {
                                logger.d("getTeamMates:mTeamMates=" + mTeamMates.size());
                            } else {
                                logger.d("getTeamMates:mTeamMates=null");
                            }
                            LiveEventBus.getDefault(mContext).post(new TeamPkTeamInfoEvent(teamInfoEntity, responseEntity));
                            logger.d("getMyTeamInfo:runnables=" + runnables.size());
                            while (!runnables.isEmpty()) {
                                Runnable runnable = runnables.remove(0);
                                runnable.run();
                            }
                        }
                    });
        } else {
            mHttpManager.getTeamMates(roomInitInfo.getStudentLiveInfo().getClassId(), roomInitInfo.getStudentLiveInfo()
                    .getTeamId(), new HttpCallBack() {
                @Override
                public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                    mTeamMates = mHttpResponseParser.parseTeamMates(responseEntity);
                }

                @Override
                public void onPmError(ResponseEntity responseEntity) {
                    super.onPmError(responseEntity);
                }

                @Override
                public void onPmFailure(Throwable error, String msg) {
                    super.onPmFailure(error, msg);
                }
            });
        }
    }

    /**
     * 不显示战队pk时，原来的战队Pk的位置由图片占据。
     */
    private void showImgReplacePk() {
        ViewGroup viewGroup = (ViewGroup) mActivity.getWindow().getDecorView();
        View view = viewGroup.findViewById(R.id.iv_livevideo_small_chinese_pk_background);
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRoomH5CloseEvent(final LiveRoomH5CloseEvent event) {
        logger.e("=======>:onRoomH5CloseEvent:" + event.getId() + ":" + event.getmGoldNum() + ":" + event.getmEnergyNum() + ":" + event.isCloseByTeacher());
        englishH5Entity = event.getEnglishH5Entity();
        newCourseWare = englishH5Entity != null && englishH5Entity.getNewEnglishH5();
        if (event.getH5Type() == LiveRoomH5CloseEvent.H5_TYPE_EXAM) {
            savedTestPlan = event.getId();
            savedTestId = "";
        } else {
            savedTestId = event.getId();
            savedTestPlan = "";
        }


        if (h5CloseEvents == null) {
            h5CloseEvents = new ArrayList<LiveRoomH5CloseEvent>();
        }

        // 只有答题结果页面才会初始化 energyNum 和 goldNum
        if (event.getmEnergyNum() != -1 && event.getmGoldNum() != -1) {
            h5CloseEvents.add(event);
            LiveRoomH5CloseEvent cacheEvent = null;

            if (h5CloseEvents.get(0).isCloseByTeacher()) {

                cacheEvent = h5CloseEvents.remove(0);

                //step  1 显示飞星动画
                showAnswerQuestionAward(cacheEvent.getmGoldNum(), cacheEvent.getmEnergyNum(), event.getId());

                //step  2 显示pk 结果
                final LiveRoomH5CloseEvent finalCacheEvent = cacheEvent;

                rlTeamPkContent.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getEnergyNumAndContributionStar();
                    }
                }, 3000);
            } else {
                cacheEvent = h5CloseEvents.get(0);
                showAnswerQuestionAward(cacheEvent.getmGoldNum(), cacheEvent.getmEnergyNum(), event.getId());
            }
        } else {

            //未展示答题结果
            if (event.isCloseByTeacher()) {
                rlTeamPkContent.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getEnergyNumAndContributionStar();
                    }
                }, 3000);
            } else {
                h5CloseEvents.add(event);
            }
        }
    }


    /**
     * 显示当前的pk 结果
     */
    public void showCurrentPkResult() {
        if (h5CloseEvents == null || h5CloseEvents.isEmpty()) {
            return;
        }
        LiveRoomH5CloseEvent cacheEvent = h5CloseEvents.remove(0);
        getEnergyNumAndContributionStar();
    }


    /**
     * 结束队伍选择
     */
    public void stopSelectAdversary() {
        if (mFocusPager != null && mFocusPager instanceof PkTeamResultPager) {
            ((PkTeamResultPager) mFocusPager).closePkResultPager();
        }
    }

    /**
     * 关闭 每题的 pk 结果展示
     */
    public void closeCurrentPkResult() {
        if (mFocusPager != null && mFocusPager instanceof PkTeamResultPager) {
            ((PkTeamResultPager) mFocusPager).closePkResultPager();
        }
    }

    /**
     * 上传服务端 学生分队准备ok
     */
    public void sendStudentReady() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "" + XESCODE.TEAM_PK_STUDENT_READY);
            jsonObject.put("stuId", "" + roomInitInfo.getStuId());
            sendNotice(jsonObject, mLiveBll.getCounTeacherStr());
            sendNotice(jsonObject, mLiveBll.getMainTeacherStr());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    ///////////////////////////消息通讯相关///////////////////////////////////////
    private int[] noticeCodes = {
            XESCODE.STOPQUESTION,
            XESCODE.EXAM_STOP, XESCODE.ENGLISH_H5_COURSEWARE,
            XESCODE.TEAM_PK_TEAM_SELECT, XESCODE.TEAM_PK_GROUP, XESCODE.TEAM_PK_SELECT_PKADVERSARY,
            XESCODE.TEAM_PK_PUBLIC_PK_RESULT,
            XESCODE.TEAM_PK_PUBLIC_CONTRIBUTION_STAR,
            XESCODE.TEAM_PK_EXIT_PK_RESULT,
            XESCODE.MULTIPLE_H5_COURSEWARE,
    };


    @Override
    public int[] getNoticeFilter() {
        return noticeCodes;
    }

    @Override
    public void onNotice(final String sourceNick, final String target, final JSONObject data, final int type) {
        logger.e("=======>onNotice :" + type);
        if (!isAvailable) {
            return;
        }

        Runnable action = new Runnable() {
            @Override
            public void run() {
                onNoticeReal(sourceNick, target, data, type);
            }
        };

        rlTeamPkContent.post(action);
    }

    @Override
    public void onTopic(final LiveTopic data, final JSONObject jsonObject, final boolean modeChange) {
        if (!isAvailable) {
            return;
        }

        Runnable action = new Runnable() {
            @Override
            public void run() {
                onTopicReal(data, jsonObject, modeChange);
            }
        };

        rlTeamPkContent.post(action);
    }

    @Override
    public void onModeChange(final String oldMode, final String mode, final boolean isPresent) {
        super.onModeChange(oldMode, mode, isPresent);
        if (!isAvailable) {
            return;
        }

        Runnable action = new Runnable() {
            @Override
            public void run() {
                onModeChangeReal(oldMode, mode, isPresent);
            }
        };
        rlTeamPkContent.post(action);
    }

    private void onNoticeReal(String sourceNick, String target, JSONObject data, int type) {
        String nonce = "";
        String open = "";

        switch (type) {
            case XESCODE.STOPQUESTION:
            case XESCODE.EXAM_STOP:
            case XESCODE.ENGLISH_H5_COURSEWARE:
            case XESCODE.MULTIPLE_H5_COURSEWARE:
                showCurrentPkResult();
                break;
            case XESCODE.TEAM_PK_GROUP: {
                String status = data.optString("status");
                nonce = data.optString("nonce", "");
//                teamSelectByNotice = true;
                if ("on".equals(status)) {
                    startTeamSelect(true, true);
                    TeamPkLog.receiveCreateTeam(mLiveBll, nonce, true);
                } else if ("off".equals(status)) {
                    //自动结束，不取消分队，但是需要去掉快速入口
                    if (mFocusPager instanceof PkDispatchTeamPager) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                closeCurrentPager();
                            }
                        });
                    }
//                    stopTeamSelect();
//                    TeamPkLog.receiveCreateTeam(mLiveBll, nonce, false);
                }
                break;
            }
            case XESCODE.TEAM_PK_TEAM_SELECT:

                open = data.optString("open");
                nonce = data.optString("nonce", "");
                if (OPEN_STATE_OPEN.equals(open)) {
                    startTeamSelect(false, true);
                } else if (OPEN_STATE_CLOSE.equals(open)) {
                    stopTeamSelect();
                }

                break;

            case XESCODE.TEAM_PK_SELECT_PKADVERSARY:
                open = data.optString("open");
                nonce = data.optString("nonce", "");
                if (OPEN_STATE_OPEN.equals(open)) {
                    startSelectAdversary();
                } else if (OPEN_STATE_CLOSE.equals(open)) {
                    stopSelectAdversary();
                }
                break;

            case XESCODE.TEAM_PK_PUBLIC_PK_RESULT:
                nonce = data.optString("nonce", "");
                showPkResult();
                break;
            case XESCODE.TEAM_PK_PUBLIC_CONTRIBUTION_STAR:
                nonce = data.optString("nonce", "");
                showClassChest();
                break;

            case XESCODE.TEAM_PK_EXIT_PK_RESULT:
                closeCurrentPkResult();
                break;
            default:
                break;
        }
    }

    private void onTopicReal(LiveTopic data, JSONObject jsonObject, boolean modeChange) {
        // 战队pk  topic 逻辑
        LiveTopic.TeamPkEntity teamPkEntity = data.getTeamPkEntity();

        if (teamPkEntity == null || !isAvailable) {
            return;
        }

        //恢复战队pk 相关状态
        int openBoxStateCode = 0;
        int alloteamStateCode = 0;
        int allotpkmanStateCode = 0;

        if (LiveTopic.MODE_CLASS.equals(mLiveBll.getMode()) && teamPkEntity.getRoomInfo1() != null) {
            openBoxStateCode = teamPkEntity.getRoomInfo1().getOpenbox();
            alloteamStateCode = teamPkEntity.getRoomInfo1().getAlloteam();
            allotpkmanStateCode = teamPkEntity.getRoomInfo1().getAllotpkman();
            logger.e("====>onTopic teampk main_teacher_info:" + openBoxStateCode + ":" +
                    alloteamStateCode + ":" + allotpkmanStateCode);
        } else {
            if (teamPkEntity.getRoomInfo2() != null) {
                openBoxStateCode = teamPkEntity.getRoomInfo2().getOpenbox();
                alloteamStateCode = teamPkEntity.getRoomInfo2().getAlloteam();
                allotpkmanStateCode = teamPkEntity.getRoomInfo2().getAllotpkman();
                logger.e("====>onTopic teampk assist_teacher_info:" + openBoxStateCode + ":" +
                        alloteamStateCode + ":" + allotpkmanStateCode);
            }
        }

        String status = "off";
        if (primaryClass) {
            try {
                JSONObject room_2 = jsonObject.getJSONObject("room_2");
                status = room_2.getString("split_team_status");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (!isTopicHandled() && alloteamStateCode == 1 || "on".equals(status)) {
            setTopicHandled(true);
            showTeamSelecting();
            return;
        }
        if (allotpkmanStateCode == 1 && !isTopicHandled()) {
            setTopicHandled(true);
            startSelectAdversary();
            logger.e("====>onTopic startSelectAdversary:");
            return;
        }

        if (openBoxStateCode == 1 && !isTopicHandled()) {
            setTopicHandled(true);
            showPkResult();
            logger.e("====>onTopic showPkResult:");
            return;
        }
        setTopicHandled(true);
    }

    private void onModeChangeReal(String oldMode, String mode, boolean isPresent) {
        if (mFocusPager != null && mFocusPager instanceof PkTeamSelectPager) {
            ((PkTeamSelectPager) mFocusPager).closeTeamSelectPager();
        }

        this.mTeacherMode = mode;
        if (isHalfBodyLiveRoom()) {
            //延时5秒 适配切屏动画
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showPkStateLayout();
                }
            }, 5000);
        }
    }

    /**
     * 是否是AlPartner 直播间
     *
     * @return
     */
    public boolean isAIPartner() {
        return isAIPartner;
    }

    private void getPkState(final boolean showPopWindow) {

        HttpCallBack callback = new HttpCallBack() {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                mCurrentPkState = mHttpResponseParser.parseStuCoinAndTotalEnergy(responseEntity);
                if (pkStateRootView != null && mCurrentPkState != null) {
                    pkStateRootView.bindData(mCurrentPkState, showPopWindow);
                }
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                super.onPmError(responseEntity);
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
            }
        };

        mHttpManager.liveCHStuGoldAndTotalEnergy(isHalfBodyLiveRoom(), mLiveBll.getLiveId(),
                getNewTeamId("liveCHStuGoldAndTotalEnergy"),
                roomInitInfo.getStudentLiveInfo().getClassId(),
                roomInitInfo.getStuId(), roomInitInfo.getUseSkin(), callback);
    }

    /**
     * 开始pk对手选择
     */
    public void startSelectAdversary() {

        HttpCallBack callback = new HttpCallBack() {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {

                TeamPkAdversaryEntity pkAdversaryEntity = mHttpResponseParser.parsePkAdversary(responseEntity);

                if (mLiveBll != null && pkAdversaryEntity.getOpponent() != null) {
                    long teamId = Long.parseLong(pkAdversaryEntity.getOpponent().getTeamId());
                    long classId = Long.parseLong(pkAdversaryEntity.getOpponent().getClassId());
                    boolean isComputer = (teamId < 0 && classId < 0);
                }

                showPkSelectStart(pkAdversaryEntity);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                super.onPmError(responseEntity);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                super.onFailure(call, e);
            }
        };
        String teamId = getNewTeamId("getPkAdversary");
        if (TeamPkConfig.DEAF_TEAM_ID.equals(teamId)) {
            runnables.add(new Runnable() {
                @Override
                public void run() {
                    startSelectAdversary();
                }
            });
            startTeamSelect(true, false);
            return;
        }
        mHttpManager.getCHPkAdversary(isHalfBodyLiveRoom(), roomInitInfo.getStudentLiveInfo().getClassId(), getNewTeamId(teamId), roomInitInfo.getUseSkin(), callback);

    }

    /**
     * 展示 投票加能量 动画
     */
    private void showVoteEnergyAnim(int addEnergy, String voteId) {
        logger.e("========> showVoteEnergyAnim:" + voteId + ":" + addEnergy);
        PkAnswerResultPager aqAwardPager = new PkAnswerResultPager(mActivity, PkAnswerResultPager.AWARD_TYPE_VOTE,
                this);
        addPager(aqAwardPager);
        aqAwardPager.setData(0, addEnergy);
//        TeamPkLog.showAddPower(mLiveBll, voteId, addEnergy + "");


        //上报服务器 增加加能量
        mHttpManager.addCHPersonAndTeamEnergy(isHalfBodyLiveRoom(), mLiveBll.getLiveId(), addEnergy,
                getNewTeamId("addCHPersonAndTeamEnergy"),
                roomInitInfo.getStudentLiveInfo().getClassId(), roomInitInfo.getStuId(), roomInitInfo.getUseSkin(), new HttpCallBack() {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {

                    }
                });
    }

    /**
     * 中途进入战斗选择
     */
    public void enterTeamSelectScene() {
//        TeamPkLog.clickFastEnter(mLiveBll);
        if (primaryClass) {
            getTeamPkHttp().getMyTeamInfo(roomInitInfo.getStudentLiveInfo().getClassId(),
                    roomInitInfo.getStuId(), UserBll.getInstance().getMyUserInfoEntity().getPsimId(), new HttpCallBack() {
                        @Override
                        public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                            TeamPkTeamInfoEntity teamInfoEntityres = mHttpResponseParser.parseTeamInfoPrimary(responseEntity);
                            if (teamInfoEntityres == null) {
                                return;
                            }
                            teamInfoEntity = teamInfoEntityres;
                            showTeamSelectedSence();
                        }
                    });
        } else {
            mHttpManager.getCHTeamInfo(isHalfBodyLiveRoom(), roomInitInfo.getId(), roomInitInfo.getStudentLiveInfo().getClassId(),
                    roomInitInfo.getStudentLiveInfo().getTeamId(), roomInitInfo.getUseSkin(), new HttpCallBack() {
                        @Override
                        public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                            teamInfoEntity = mHttpResponseParser.parseTeamInfo(responseEntity);
                            showTeamSelectedSence();
                        }

                        @Override
                        public void onPmError(ResponseEntity responseEntity) {
                            super.onPmError(responseEntity);
                        }
                    });
        }
    }

    public void requestStuChest(int isWin, HttpCallBack callBack) {
        mHttpManager.getCHStuChest(isHalfBodyLiveRoom(), isWin, roomInitInfo.getStudentLiveInfo().getClassId(), getNewTeamId("requestStuChest"), roomInitInfo.getStuId(), mLiveBll.getLiveId(), isAIPartner(), roomInitInfo.getUseSkin(), callBack);
    }


    /**
     * 获取每题的 pk 结果
     */
    private void getEnergyNumAndContributionStar() {

        if (newCourseWare) {

            HttpCallBack callback = new HttpCallBack() {
                @Override
                public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                    TeamEnergyAndContributionStarEntity entity = mHttpResponseParser.parseTeanEnergyAndContribution(responseEntity);
                    showPkEveryResult(entity);
                }
            };

            mHttpManager.teamCHEnergyNumAndContributionmulStar(isHalfBodyLiveRoom(), mLiveBll.getLiveId(),
                    getNewTeamId("teamCHEnergyNumAndContributionmulStar"),
                    roomInitInfo.getStudentLiveInfo().getClassId(),
                    roomInitInfo.getStuId(),
                    englishH5Entity.getReleasedPageInfos(),
                    englishH5Entity.getClassTestId(),
                    englishH5Entity.getPackageSource(),
                    roomInitInfo.getUseSkin(),
                    callback);

        } else {
            HttpCallBack callback = new HttpCallBack() {
                @Override
                public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                    TeamEnergyAndContributionStarEntity entity = mHttpResponseParser.parseTeanEnergyAndContribution(responseEntity);
                    showPkEveryResult(entity);
                }
            };

            mHttpManager.teamCHEnergyNumAndContributionStar(isHalfBodyLiveRoom(), mLiveBll.getLiveId(),
                    getNewTeamId("teamCHEnergyNumAndContributionStar"),
                    roomInitInfo.getStudentLiveInfo().getClassId(),
                    roomInitInfo.getStuId(),
                    savedTestId,
                    savedTestPlan,
                    roomInitInfo.getUseSkin(),
                    callback);
        }
    }

    public String getNewTeamId(String method) {
        String teamId;
        if (primaryClass) {
            if (teamInfoEntity == null) {
                return TeamPkConfig.DEAF_TEAM_ID;
            }
            try {
                teamId = teamInfoEntity.getTeamInfo().getTeamId();
            } catch (Exception e) {
                teamId = TeamPkConfig.DEAF_TEAM_ID;
                CrashReport.postCatchedException(new LiveException(TAG + ":" + method, e));
            }
        } else {
            teamId = roomInitInfo.getStudentLiveInfo().getTeamId();
        }
        mLogtf.d("getNewTeamId:primaryClass=" + primaryClass + ",method=" + method + ",teamId=" + teamId);
        return teamId;
    }
}
