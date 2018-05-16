package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.base.BasePager;
import com.xueersi.parentsmeeting.http.HttpCallBack;
import com.xueersi.parentsmeeting.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoActivity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassChestEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StudentCoinAndTotalEnergyEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StudentPkResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamEnergyAndContributionStarEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamPkAdversaryEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamPkTeamInfoEntity;
import com.xueersi.parentsmeeting.modules.livevideo.event.LiveRoomH5CloseEvent;
import com.xueersi.parentsmeeting.modules.livevideo.event.NativeVoteRusltulCloseEvent;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.page.TeamPKAQResultPager;
import com.xueersi.parentsmeeting.modules.livevideo.page.TeamPkAwardPager;
import com.xueersi.parentsmeeting.modules.livevideo.page.TeamPkResultPager;
import com.xueersi.parentsmeeting.modules.livevideo.page.TeamPkTeamSelectPager;
import com.xueersi.parentsmeeting.modules.livevideo.page.TeamPkTeamSelectingPager;
import com.xueersi.parentsmeeting.modules.livevideo.widget.TeamPKStateLayout;
import com.xueersi.xesalib.utils.log.Loger;
import com.xueersi.xesalib.utils.uikit.ScreenUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.xueersi.parentsmeeting.modules.livevideo.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import okhttp3.Call;

/**
 * @author chekun
 * created  at 2018/4/12
 * 战队PK 相关业务处理
 */
public class TeamPKBll {
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
    private RelativeLayout mRootView;
    private LiveBll mLiveBll;
    private LiveHttpManager mHttpManager;
    private LiveGetInfo roomInitInfo;
    private LiveHttpResponseParser mHttpResponseParser;
    private TeamPkTeamInfoEntity teamInfoEntity;
    private BasePager mFocusPager;

    /**
     * pk对手
     */
    private static final int PK_RESULT_TYPE_ADVERSARY = 1;
    /**
     * 学生 当场次答题pk 结果
     */
    private static final int PK_RESULT_TYPE_FINAL_PKRESULT = 2;
    /**
     * 学生 每题的PK 结果
     */
    private static final int PK_RESULT_TYPE_PKRESULT = 3;
    private boolean isTopicHandled = false;

    private boolean isWin;
    private TeamPKStateLayout pkStateRootView;
    /**
     * 直播间内答题 H5 答题结果页面关闭事件队列
     */
    private List<LiveRoomH5CloseEvent> h5CloseEvents;

    public TeamPKBll(Activity activity) {
        this.mActivity = activity;
    }

    public TeamPKBll(Activity activity, RelativeLayout rootView) {
        this.mActivity = activity;
        this.mRootView = rootView;
    }

    public void setHttpManager(LiveHttpManager liveHttpManager) {
        mHttpManager = liveHttpManager;
    }

    public LiveHttpManager getmHttpManager() {
        return mHttpManager;
    }


    public void setLiveBll(LiveBll bll) {
        mLiveBll = bll;
    }

    public LiveBll getLiveBll() {
        return mLiveBll;
    }


    public void setRoomInitInfo(LiveGetInfo roomInfo) {
        roomInitInfo = roomInfo;
    }

    public LiveGetInfo getRoomInitInfo() {
        return roomInitInfo;
    }


    public void attachToRootView() {
        initData();
        rlTeamPkContent = new RelativeLayout(mActivity);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.
                LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mRootView.addView(rlTeamPkContent, params);
        showPkStateLayout();
    }

    /**
     * 显示 场次答题PK 结果
     * notice  topic  中通知调用
     */
    public void showPkResult() {
        getStuPkResult();
    }

    private void getStuPkResult() {
        mHttpManager.stuPKResult(mLiveBll.getLiveId(), roomInitInfo.getStudentLiveInfo().getTeamId(),
                roomInitInfo.getStudentLiveInfo().getClassId(), new HttpCallBack() {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        StudentPkResultEntity resultEntity = mHttpResponseParser.parseStuPkResult(responseEntity);
                        showPkResultScene(resultEntity, PK_RESULT_TYPE_FINAL_PKRESULT);
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        super.onFailure(call, e);
                    }
                });
    }

    /**
     * 显示开宝箱场景
     */
    public void showOpenBoxScene(boolean isWin) {
        this.isWin = isWin;
        showAwardGetScene(CHEST_TYPE_STUDENT, null, isWin);
    }

    /**
     * 从topic 中恢复 开宝箱场景
     */
    public void resumeOpenBoxScene() {
        // 请求接口 获取胜负关系
        mHttpManager.liveStuGoldAndTotalEnergy(mLiveBll.getLiveId(),
                roomInitInfo.getStudentLiveInfo().getTeamId(),
                roomInitInfo.getStudentLiveInfo().getClassId(),
                roomInitInfo.getStuId(), new HttpCallBack() {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        StudentCoinAndTotalEnergyEntity energyEntity = mHttpResponseParser.parseStuCoinAndTotalEnergy
                                (responseEntity);
                        if (energyEntity != null) {
                            showOpenBoxScene(energyEntity.getMyEnergy() >= energyEntity.getCompetitorEnergy());
                        }
                    }

                    @Override
                    public void onPmFailure(Throwable error, String msg) {
                        super.onPmFailure(error, msg);
                    }
                });
    }

    /**
     * 显示 战队宝箱领取情况
     */
    public void showClassChest() {
        getClassChestResult();
    }

    /**
     * 获取战队开宝箱结果
     */
    private void getClassChestResult() {
        mHttpManager.getClassChestResult(mLiveBll.getLiveId(), roomInitInfo.getStuId(),
                roomInitInfo.getStudentLiveInfo().getTeamId(), roomInitInfo.getStudentLiveInfo().getClassId()
                , new HttpCallBack() {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        ClassChestEntity classChestEntity = mHttpResponseParser.parseClassChest(responseEntity);
                        showAwardGetScene(CHEST_TYPE_CLASS, classChestEntity, isWin);
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        super.onFailure(call, e);
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        super.onPmError(responseEntity);
                    }
                });

    }

    /**
     * 获取每题的 pk 结果
     *
     * @param event
     */
    private void getEnergyNumAndContributionStar(LiveRoomH5CloseEvent event) {
        String testId = "";
        String testPlan = "";
        if (event.getH5Type() == LiveRoomH5CloseEvent.H5_TYPE_EXAM) {
            testPlan = event.getId();
        } else {
            testId = event.getId();
        }
        //Loger.e("TeamPkBll", "======>getEnergyNumAndContributionStar: called:" + testId + ":" + testPlan);
        mHttpManager.teamEnergyNumAndContributionStar(mLiveBll.getLiveId(),
                roomInitInfo.getStudentLiveInfo().getTeamId(),
                roomInitInfo.getStudentLiveInfo().getClassId(), roomInitInfo.getStuId(), testId, testPlan, new
                        HttpCallBack() {
                            @Override
                            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                                TeamEnergyAndContributionStarEntity entity = mHttpResponseParser
                                        .parseTeanEnergyAndContribution(responseEntity);
                                showPkResultScene(entity, PK_RESULT_TYPE_PKRESULT);
                            }
                        });
    }

    public void setTopicHandled(boolean topicHandled) {
        isTopicHandled = topicHandled;
    }

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


    /**
     * 开启分队仪式
     */
    public void startTeamSelect() {
        Loger.e("teamPkBll", "====>startTeamSelect:");
        getTeamInfo();
    }

    public void stopTeamSelect() {
        if (mFocusPager != null && mFocusPager instanceof TeamPkTeamSelectPager) {
            ((TeamPkTeamSelectPager) mFocusPager).closeTeamSelectPager();
        } else if (mFocusPager != null && mFocusPager instanceof TeamPkTeamSelectingPager) {
            ((TeamPkTeamSelectingPager) mFocusPager).closeTeamSelectPager();
        }
    }

    /**
     * 获取战队信息
     */
    private void getTeamInfo() {
        mHttpManager.getTeamInfo(roomInitInfo.getId(), roomInitInfo.getStudentLiveInfo().getClassId(),
                roomInitInfo.getStudentLiveInfo().getTeamId(), new HttpCallBack() {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        //  Loger.e(TAG, "=====>getTeamInfo onPmSuccess:" + responseEntity.getJsonObject().toString());
                        teamInfoEntity = mHttpResponseParser.parseTeamInfo(responseEntity);
                        showTeamSelectScene();
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        super.onPmError(responseEntity);
                    }
                });
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
        if (mFocusPager == null || !(mFocusPager instanceof TeamPkTeamSelectingPager)) {
            Loger.e("teamPkBll", "====>showTeamSelectScene:" + mFocusPager);
            TeamPkTeamSelectPager teamSelectPager = new TeamPkTeamSelectPager(mActivity, this);
            addPager(teamSelectPager);
            teamSelectPager.startTeamSelect();
        }
    }

    /**
     * 中途进入战斗选择
     */
    public void enterTeamSelectScene() {
        mHttpManager.getTeamInfo(roomInitInfo.getId(), roomInitInfo.getStudentLiveInfo().getClassId(),
                roomInitInfo.getStudentLiveInfo().getTeamId(), new HttpCallBack() {
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


    /**
     * 显示 战队已选中 UI
     */
    private void showTeamSelectedSence() {
        if (mFocusPager == null || !(mFocusPager instanceof TeamPkTeamSelectPager)) {
            TeamPkTeamSelectPager teamSelectPager = new TeamPkTeamSelectPager(mActivity, this);
            addPager(teamSelectPager);
            teamSelectPager.setData(teamInfoEntity);
            teamSelectPager.showTeamSelectedScene(true);
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
        Loger.e("teampkBll", "======>showAwardGetScene called");
        //   if (mFocusPager == null || !(mFocusPager instanceof TeamPkAwardPager)) {
        if (mFocusPager == null || !(mFocusPager instanceof TeamPkAwardPager)) {
            Loger.e("teampkBll", "======>showAwardGetScene called 11111");
            TeamPkAwardPager awardGetPager = new TeamPkAwardPager(mActivity, this);
            addPager(awardGetPager);
            if (type == CHEST_TYPE_CLASS) {
                awardGetPager.showClassChest((ClassChestEntity) data, isWin);
            } else if (type == CHEST_TYPE_STUDENT) {
                awardGetPager.showBoxLoop(isWin);
                Loger.e("teampkBll", "======>showAwardGetScene called 3333");
            }
        } else if (mFocusPager != null && (mFocusPager instanceof TeamPkAwardPager)) {
            //由开宝箱直接切换到幸运之星页面
            if (type == CHEST_TYPE_CLASS) {
                ((TeamPkAwardPager) mFocusPager).closeAwardPager();
                rlTeamPkContent.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        TeamPkAwardPager awardGetPager = new TeamPkAwardPager(mActivity, TeamPKBll.this);
                        addPager(awardGetPager);
                        awardGetPager.showClassChest((ClassChestEntity) data, isWin);
                    }
                }, 1000);
            }
        }
    }

    /**
     * 显示分队进行中
     */
    public void showTeamSelecting() {
        if (mFocusPager == null || !(mFocusPager instanceof TeamPkTeamSelectPager)) {
            Loger.e("teamPkBll", "====>showTeamSelecting:");
            rlTeamPkContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver
                    .OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    TeamPkTeamSelectingPager selectingPager = new TeamPkTeamSelectingPager(mActivity, TeamPKBll
                            .this);
                    addPager(selectingPager);
                    rlTeamPkContent.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            });
        }
    }

    /**
     * 展示pk 信息场景
     *
     * @param data
     */
    public void showPkResultScene(Object data, int Type) {
        if (mFocusPager == null || !(mFocusPager instanceof TeamPkResultPager)) {
            TeamPkResultPager resultPager = new TeamPkResultPager(mActivity, this);
            addPager(resultPager);
            switch (Type) {
                case PK_RESULT_TYPE_ADVERSARY:
                    resultPager.showPkAdversary((TeamPkAdversaryEntity) data);
                    break;
                case PK_RESULT_TYPE_FINAL_PKRESULT:
                    resultPager.showFinalPkResult(((StudentPkResultEntity) data));
                    break;
                case PK_RESULT_TYPE_PKRESULT:
                    resultPager.showCurrentResult((TeamEnergyAndContributionStarEntity) data);
                    break;
                default:
                    break;
            }
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
                    rlTeamPkContent.removeView(mFocusPager.getRootView());
                    mFocusPager = null;
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
        }
        // step 2  初始化 又测 pk 状态栏
        updatePkStateLayout(false);
    }

    /**
     * 刷新pk状态栏
     *
     * @param showPopWindow 是否展示顶部  进度描述:领先，打平 .....
     */
    public void updatePkStateLayout(boolean showPopWindow) {
        getPkState(showPopWindow);
    }

    private void getPkState(final boolean showPopWindow) {
        Loger.e("TeamPkBll", "=====> getPkState:" + roomInitInfo.getStuId());
        mHttpManager.liveStuGoldAndTotalEnergy(mLiveBll.getLiveId(),
                roomInitInfo.getStudentLiveInfo().getTeamId(),
                roomInitInfo.getStudentLiveInfo().getClassId(),
                roomInitInfo.getStuId(), new HttpCallBack() {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        StudentCoinAndTotalEnergyEntity energyEntity = mHttpResponseParser.
                                parseStuCoinAndTotalEnergy(responseEntity);
                        if (pkStateRootView != null && energyEntity != null) {
                            pkStateRootView.bindData(energyEntity.getStuLiveGold(),
                                    energyEntity.getMyEnergy(), energyEntity.getCompetitorEnergy(), showPopWindow);
                        }
                    }

                    @Override
                    public void onPmFailure(Throwable error, String msg) {
                        super.onPmFailure(error, msg);
                    }
                });
    }

    /**
     * 显示实时答题 奖励
     */
    public void showAnswerQuestionAward(int goldNum, int energyNum) {
        TeamPKAQResultPager aqAwardPager = new TeamPKAQResultPager(mActivity,
                TeamPKAQResultPager.AWARD_TYPE_QUESTION, this);
        addPager(aqAwardPager);
        aqAwardPager.setData(goldNum, energyNum);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVoteResultUIColse(NativeVoteRusltulCloseEvent event) {
        int addEnergy = event.isStuVoted() ? VOTE_ADD_ENERGY : 0;
        showVoteEnergyAnim(addEnergy);
    }

    /**
     * 展示 投票加能量 动画
     */
    private void showVoteEnergyAnim(int addEnergy) {
        Loger.e("LiveBll", "========> showVoteEnergyAnim");
        TeamPKAQResultPager aqAwardPager = new TeamPKAQResultPager(mActivity, TeamPKAQResultPager.AWARD_TYPE_VOTE,
                this);
        addPager(aqAwardPager);
        aqAwardPager.setData(0, addEnergy);
        //上报服务器 增加加能量
        mHttpManager.addPersonAndTeamEnergy(mLiveBll.getLiveId(), addEnergy,
                roomInitInfo.getStudentLiveInfo().getTeamId(),
                roomInitInfo.getStudentLiveInfo().getClassId(), roomInitInfo.getStuId(), new HttpCallBack() {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {

                    }
                });
    }

    /**
     * @return
     */
    private int getRightMargin() {
        int screenWidth = ScreenUtils.getScreenWidth();
        return (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * screenWidth / LiveVideoActivity.VIDEO_WIDTH);
    }

    private void addPager(BasePager aqAwardPager) {
        rlTeamPkContent.removeAllViews();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        int rightMargin = getRightMargin();
        params.rightMargin = rightMargin;
        rlTeamPkContent.addView(aqAwardPager.getRootView(), params);
        mFocusPager = aqAwardPager;
    }


    public void onDestroy() {
        if (mFocusPager != null) {
            mFocusPager.onDestroy();
            mFocusPager = null;
        }
        isTopicHandled = false;
        EventBus.getDefault().unregister(this);
    }

    /**
     * activity stop
     */
    public void onStop() {
        if (mFocusPager != null) {
            mFocusPager.onStop();
        }
    }

    /**
     * activity resume
     */
    public void onReusme() {
        if (mFocusPager != null) {
            mFocusPager.onResume();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRoomH5CloseEvent(final LiveRoomH5CloseEvent event) {
        Loger.e("TeamPkBll", "=======>:onRoomH5CloseEvent:" + event.getId() + ":"
                + event.getmGoldNum() + ":" + event.getmEnergyNum() + ":" + event.isCloseByTeacher());
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
                showAnswerQuestionAward(cacheEvent.getmGoldNum(), cacheEvent.getmEnergyNum());
                //step  2 显示pk 结果
                final LiveRoomH5CloseEvent finalCacheEvent = cacheEvent;
                rlTeamPkContent.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getEnergyNumAndContributionStar(finalCacheEvent);
                    }
                }, 3000);
            } else {
                cacheEvent = h5CloseEvents.get(0);
                showAnswerQuestionAward(cacheEvent.getmGoldNum(), cacheEvent.getmEnergyNum());
            }
        } else {
            //未展示答题结果
            if (event.isCloseByTeacher()) {
                getEnergyNumAndContributionStar(event);
            } else {
                h5CloseEvents.add(event);
            }
        }
    }


    /**
     * 显示当前的pk 结果
     */
    public void showCurrentPkResult() {
        if (h5CloseEvents == null || h5CloseEvents.size() == 0) {
            return;
        }
        Loger.e("TeamPkBll", "======>showCurrentPkResult: called");
        LiveRoomH5CloseEvent cacheEvent = h5CloseEvents.remove(0);
        getEnergyNumAndContributionStar(cacheEvent);
    }


    /**
     * 开始pk对手选择
     */
    public void startSelectAdversary() {
        getPkAdversary();
    }

    /**
     * 获取PK 对手信息
     */
    private void getPkAdversary() {
        mHttpManager.getPkAdversary(roomInitInfo.getStudentLiveInfo().getClassId(),
                roomInitInfo.getStudentLiveInfo().getTeamId(), new HttpCallBack() {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        TeamPkAdversaryEntity pkAdversaryEntity = mHttpResponseParser.
                                parsePkAdversary(responseEntity);
                        showPkResultScene(pkAdversaryEntity, PK_RESULT_TYPE_ADVERSARY);
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        super.onPmError(responseEntity);
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        super.onFailure(call, e);
                    }
                }
        );
    }

    /**
     * 结束队伍选择
     */
    public void stopSelectAdversary() {
        if (mFocusPager != null && mFocusPager instanceof TeamPkResultPager) {
            ((TeamPkResultPager) mFocusPager).closePkResultPager();
        }
    }

    /**
     * 关闭 每题的 pk 结果展示
     */
    public void closeCurrentPkResult() {
        if (mFocusPager != null && mFocusPager instanceof TeamPkResultPager) {
            ((TeamPkResultPager) mFocusPager).closePkResultPager();
        }
    }

    /**
     * 上传服务端 学生分队准备ok
     */
    public void sendStudentReady() {
        if (mLiveBll != null) {
            mLiveBll.sendStudentReady();
        }
    }
}
