package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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
import com.xueersi.xesalib.utils.uikit.ScreenUtils;

import java.io.IOException;

import com.xueersi.parentsmeeting.modules.livevideo.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import okhttp3.Call;

/**
 * Created by chenkun on 2018/4/12
 * 战队PK 相关业务处理
 */
public class TeamPKBll {
    public static final String TEAMPK_URL_FIFTE = "http://addenergyandgold.com/";
    private static final int CHEST_TYPE_CLASS = 1; //开宝箱类型 班级宝箱列表
    private static final int CHEST_TYPE_STUDENT = 2; //开宝箱类型 学生自己宝箱
    private static final int VOTE_ADD_ENERGY = 3; //投票题 奖励能量
    private Activity activity;
    //战队PK rootView
    private RelativeLayout rlTeamPkContent;
    private RelativeLayout mRootView;
    private LiveBll mLiveBll;
    private LiveHttpManager mHttpManager;
    private LiveGetInfo roomInitInfo;
    private static final String TAG = "TeamPKBll";
    private LiveHttpResponseParser mHttpResponseParser;
    private TeamPkTeamInfoEntity teamInfoEntity;
    private BasePager mFoucesPager;
    private static final int PK_RESULT_TYPE_ADVERSARY = 1;
    private static final int PK_RESULT_TYPE_FINAL_PKRESULT = 2;//学生 当场次答题pk 结果
    private static final int PK_RESULT_TYPE_PKRESULT = 3;// 学生 每题的PK 结果
    private boolean isTopicHandled = false;

    private boolean isWin;
    private TeamPKStateLayout pkStateRootView;

    public TeamPKBll(Activity activity) {
        this.activity = activity;
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

    public LiveBll getmLiveBll() {
        return mLiveBll;
    }


    public void setRoomInitInfo(LiveGetInfo roomInfo) {
        roomInitInfo = roomInfo;
    }

    public LiveGetInfo getRoomInitInfo() {
        return roomInitInfo;
    }

    public void setRootView(RelativeLayout rootView) {
        this.mRootView = rootView;
    }

    public void attachToRootView() {
        initData();
        rlTeamPkContent = new RelativeLayout(activity);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.
                LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mRootView.addView(rlTeamPkContent, params);
        showPkStateLayout();
    /*   if(!isTeamSelected()){
           getTeamInfo();
       }*/
        // test();
    }


    /**
     * 测试相关接口
     */
    private void test() {
        //getPkAdversary();
        //getStuChest();
        //getClassChestResult();
        //showOpenBoxScene(false);
        //showClassChest(false);
        //showPkResult();
        //showVoteEnergyAnim();
        //测试 lottie 动画
        TeamPkResultPager resultPager = new TeamPkResultPager(activity, this);
        rlTeamPkContent.removeAllViews();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        int screenWidth = ScreenUtils.getScreenWidth();
        int wradio = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * screenWidth / LiveVideoActivity.VIDEO_WIDTH);
        params.rightMargin = wradio;
        rlTeamPkContent.addView(resultPager.getRootView(), params);

        resultPager.showCurrentResult((TeamEnergyAndContributionStarEntity) null);
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
                roomInitInfo.getStudentLiveInfo().getTeamId(), roomInitInfo.getStudentLiveInfo().getClassId(), new HttpCallBack() {
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
     */
    private void getEnergyNumAndContributionStar(String testId, String testPlan) {

        mHttpManager.teamEnergyNumAndContributionStar(mLiveBll.getLiveId(),
                roomInitInfo.getStudentLiveInfo().getTeamId(),
                roomInitInfo.getStudentLiveInfo().getClassId(), roomInitInfo.getStuId(), testId, testPlan, new HttpCallBack() {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        TeamEnergyAndContributionStarEntity entity = mHttpResponseParser.parseTeanEnergyAndContribution(responseEntity);
                        showPkResultScene(entity, PK_RESULT_TYPE_PKRESULT);
                    }
                });
    }

    /**
     * 判断是否已经分好对了
     *
     * @return
     */
    private boolean isTeamSelected() {
        return false;
    }


    public void setTopicHandled(boolean topicHandled) {
        isTopicHandled = topicHandled;
    }

    public boolean isTopicHandled() {
        return isTopicHandled;
    }

    private void initData() {
        mHttpResponseParser = new LiveHttpResponseParser(activity);
        EventBus.getDefault().register(this);

    }

    public LiveHttpResponseParser getmHttpResponseParser() {
        return mHttpResponseParser;
    }


    /**
     * 开启分队仪式
     */
    public void startTeamSelect() {
        Log.e("teamPkBll", "====>startTeamSelect:");
        getTeamInfo();
    }

    public void stopTeamSelect() {
        if (mFoucesPager != null && mFoucesPager instanceof TeamPkTeamSelectPager) {
            ((TeamPkTeamSelectPager) mFoucesPager).closeTeamSelectPager();
        } else if (mFoucesPager != null && mFoucesPager instanceof TeamPkTeamSelectingPager) {
            ((TeamPkTeamSelectingPager) mFoucesPager).closeTeamSelectPager();
        }
    }

    /**
     * 获取战队信息
     */
    private void getTeamInfo() {
        // 获取 enstuid 的方式：  String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        //  stuCouId  在默认参数中
        mHttpManager.getTeamInfo(roomInitInfo.getId(), roomInitInfo.getStudentLiveInfo().getClassId(), roomInitInfo.getStudentLiveInfo().getTeamId(), new HttpCallBack() {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                Log.e(TAG, "=====>getTeamInfo onPmSuccess:" + responseEntity.getJsonObject().toString());
                teamInfoEntity = mHttpResponseParser.parseTeamInfo(responseEntity);
                Log.e(TAG, "=====>getTeamInfo onPmSuccess:" + teamInfoEntity.getKey() + ":"
                        + teamInfoEntity.getTeamLogoList().size() + ":" + teamInfoEntity.getTeamMembers().size());
                showTeamSelectScene();
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


    public void initView(RelativeLayout bottomContent) {
        rlTeamPkContent = new RelativeLayout(activity);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.
                LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        bottomContent.addView(rlTeamPkContent, params);
        // 聊天区域测试
        // LiveMessagePager mLiveMessagePager = new LiveMessagePager();
    }

    /**
     * 显示分队仪式 场景
     */
    private void showTeamSelectScene() {
        if (mFoucesPager == null || !(mFoucesPager instanceof TeamPkTeamSelectingPager)) {
            Log.e("teamPkBll", "====>showTeamSelectScene:" + mFoucesPager);
            TeamPkTeamSelectPager teamSelectPager = new TeamPkTeamSelectPager(activity, this);
            mFoucesPager = teamSelectPager;
            teamSelectPager.setData(teamInfoEntity);
            rlTeamPkContent.removeAllViews();
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            int screenWidth = ScreenUtils.getScreenWidth();
            int wradio = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * screenWidth / LiveVideoActivity.VIDEO_WIDTH);
            params.rightMargin = wradio;
            rlTeamPkContent.addView(teamSelectPager.getRootView(), params);
            teamSelectPager.startTeamSelect();
        }
    }

    /**
     * 中途进入战斗选择
     */
    public void enterTeamSelectScene() {
        mHttpManager.getTeamInfo(roomInitInfo.getId(), roomInitInfo.getStudentLiveInfo().getClassId(), roomInitInfo.getStudentLiveInfo().getTeamId(), new HttpCallBack() {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                Log.e(TAG, "=====>getTeamInfo onPmSuccess:" + responseEntity.getJsonObject().toString());
                teamInfoEntity = mHttpResponseParser.parseTeamInfo(responseEntity);
                Log.e(TAG, "=====>getTeamInfo onPmSuccess:" + teamInfoEntity.getKey() + ":"
                        + teamInfoEntity.getTeamLogoList().size() + ":" + teamInfoEntity.getTeamMembers().size());
                showTeamSelectedSence();
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
     * 显示 战队已选中 UI
     */
    private void showTeamSelectedSence() {
        if (mFoucesPager == null || !(mFoucesPager instanceof TeamPkTeamSelectPager)) {
            TeamPkTeamSelectPager teamSelectPager = new TeamPkTeamSelectPager(activity, this);
            rlTeamPkContent.removeAllViews();
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            int screenWidth = ScreenUtils.getScreenWidth();
            int wradio = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * screenWidth / LiveVideoActivity.VIDEO_WIDTH);
            params.rightMargin = wradio;
            rlTeamPkContent.addView(teamSelectPager.getRootView(), params);
            mFoucesPager = teamSelectPager;
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
    public void showAwardGetScene(int type, Object data, boolean isWin) {
        Log.e("teampkBll", "======>showAwardGetScene called");
        if (mFoucesPager == null || !(mFoucesPager instanceof TeamPkAwardPager)) {
            Log.e("teampkBll", "======>showAwardGetScene called 11111");
            TeamPkAwardPager awardGetPager = new TeamPkAwardPager(activity, this);
            rlTeamPkContent.removeAllViews();
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            int screenWidth = ScreenUtils.getScreenWidth();
            int wradio = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * screenWidth / LiveVideoActivity.VIDEO_WIDTH);
            params.rightMargin = wradio;
            rlTeamPkContent.addView(awardGetPager.getRootView(), params);
            mFoucesPager = awardGetPager;
            if (type == CHEST_TYPE_CLASS) {
                awardGetPager.showClassChest((ClassChestEntity) data, isWin);
            } else if (type == CHEST_TYPE_STUDENT) {
                awardGetPager.showBoxLoop(isWin);
                Log.e("teampkBll", "======>showAwardGetScene called 3333");
            }
        }
    }

    /**
     * 显示分队进行中
     */
    public void showTeamSelecting() {
        if (mFoucesPager == null || !(mFoucesPager instanceof TeamPkTeamSelectPager)) {
            Log.e("teamPkBll", "====>showTeamSelecting:");
            TeamPkTeamSelectingPager selectingPager = new TeamPkTeamSelectingPager(activity, this);
            mFoucesPager = selectingPager;
            rlTeamPkContent.removeAllViews();
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            int screenWidth = ScreenUtils.getScreenWidth();
            int wradio = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * screenWidth / LiveVideoActivity.VIDEO_WIDTH);
            params.rightMargin = wradio;
            rlTeamPkContent.addView(selectingPager.getRootView(), params);
        }

    }

    /**
     * 展示pk 信息场景
     *
     * @param data
     */
    public void showPkResultScene(Object data, int Type) {
        if (mFoucesPager == null || !(mFoucesPager instanceof TeamPkResultPager)) {
            TeamPkResultPager resultPager = new TeamPkResultPager(activity, this);
            rlTeamPkContent.removeAllViews();
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            int screenWidth = ScreenUtils.getScreenWidth();
            int wradio = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * screenWidth / LiveVideoActivity.VIDEO_WIDTH);
            params.rightMargin = wradio;
            rlTeamPkContent.addView(resultPager.getRootView(), params);
            mFoucesPager = resultPager;
            switch (Type) {
                case PK_RESULT_TYPE_ADVERSARY:
                    resultPager.showPkAdversary((TeamPkAdversaryEntity) data);
                    break;
                case PK_RESULT_TYPE_FINAL_PKRESULT:
                    //Log.e("PkResult", "======> TeamPkBll show finalPkResult:" + data);
                    resultPager.showFinalPkResult(((StudentPkResultEntity) data));
                    break;
                case PK_RESULT_TYPE_PKRESULT:
                    resultPager.showCurrentResult((TeamEnergyAndContributionStarEntity) data);
                    break;
            }
        }
    }

    /**
     * 关闭当前页面
     */
    public void closeCurrentPager() {
        if (mFoucesPager != null) {
            rlTeamPkContent.post(new Runnable() {
                @Override
                public void run() {
                    rlTeamPkContent.removeView(mFoucesPager.getRootView());
                    mFoucesPager = null;
                }
            });
        }
    }

    /**
     * 展示聊天 区域上方 战队pk 状态UI
     */
    private void showPkStateLayout() {
        // step 1
        ViewGroup viewGroup = (ViewGroup) activity.getWindow().getDecorView();
        pkStateRootView = viewGroup.findViewById(R.id.tpkL_teampk_pkstate_root);
        if (pkStateRootView != null) {
            pkStateRootView.setVisibility(View.VISIBLE);
        }
        // step 2  初始化 又测 pk 状态栏
        getPkState();
    }

    private void getPkState() {
        mHttpManager.liveStuGoldAndTotalEnergy(mLiveBll.getLiveId(),
                roomInitInfo.getStudentLiveInfo().getTeamId(),
                roomInitInfo.getStudentLiveInfo().getClassId(),
                roomInitInfo.getStuId(), new HttpCallBack() {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        StudentCoinAndTotalEnergyEntity energyEntity = mHttpResponseParser.parseStuCoinAndTotalEnergy(responseEntity);
                        if (pkStateRootView != null && energyEntity != null) {
                            //Log.e("TeamPkBll","======>getPkState:"+energyEntity.getMyEnergy()+":"+energyEntity.getCompetitorEnergy()+":"+energyEntity.getStuLiveGold());
                            pkStateRootView.bindData(energyEntity.getStuLiveGold(), energyEntity.getMyEnergy(), energyEntity.getCompetitorEnergy());
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
        TeamPKAQResultPager aqAwardPager = new TeamPKAQResultPager(activity, TeamPKAQResultPager.AWARD_TYPE_QUESTION);
        rlTeamPkContent.removeAllViews();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        int screenWidth = ScreenUtils.getScreenWidth();
        int wradio = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * screenWidth / LiveVideoActivity.VIDEO_WIDTH);
        params.rightMargin = wradio;
        rlTeamPkContent.addView(aqAwardPager.getRootView(), params);
        mFoucesPager = aqAwardPager;
        aqAwardPager.setData(goldNum, energyNum);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVoteResultUIColse(NativeVoteRusltulCloseEvent event) {
        showVoteEnergyAnim();
    }

    /**
     * 展示 投票加能量 动画
     */
    public void showVoteEnergyAnim() {
        Log.e("LiveBll", "========> showVoteEnergyAnim");
        TeamPKAQResultPager aqAwardPager = new TeamPKAQResultPager(activity, TeamPKAQResultPager.AWARD_TYPE_VOTE);
        rlTeamPkContent.removeAllViews();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        int screenWidth = ScreenUtils.getScreenWidth();
        int wradio = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * screenWidth / LiveVideoActivity.VIDEO_WIDTH);
        params.rightMargin = wradio;
        rlTeamPkContent.addView(aqAwardPager.getRootView(), params);
        mFoucesPager = aqAwardPager;
        aqAwardPager.setData(0, VOTE_ADD_ENERGY);
    }


    public void onDestroy() {
        if (mFoucesPager != null) {
            mFoucesPager.onDestroy();
            mFoucesPager = null;
        }
        isTopicHandled = false;
        EventBus.getDefault().unregister(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRoomH5CloseEvent(final LiveRoomH5CloseEvent event) {
        Log.e("TeamPkBll", "=======>:onRoomH5CloseEvent:" + event.getId() + ":" + event.getmGoldNum() + ":" + event.getmEnergyNum());
        if (event.getmEnergyNum() != -1 && event.getmGoldNum() != -1) {
            showAnswerQuestionAward(event.getmGoldNum(), event.getmEnergyNum());
            rlTeamPkContent.postDelayed(new Runnable() {
                @Override
                public void run() {
                    String testId = "";
                    String testPlan = "";
                    if (event.getH5Type() == LiveRoomH5CloseEvent.H5_TYPE_EXAM) {
                        testPlan = event.getId();
                    } else {
                        testId = event.getId();
                    }
                    getEnergyNumAndContributionStar(testId, testPlan);
                }
            }, 3000);

        } else {
            String testId = "";
            String testPlan = "";
            if (event.getH5Type() == LiveRoomH5CloseEvent.H5_TYPE_EXAM) {
                testPlan = event.getId();
            } else {
                testId = event.getId();
            }
            getEnergyNumAndContributionStar(testId, testPlan);
        }

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
                        TeamPkAdversaryEntity pkAdversaryEntity = mHttpResponseParser.parsePkAdversary(responseEntity);
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
        if (mFoucesPager != null && mFoucesPager instanceof TeamPkResultPager) {
            ((TeamPkResultPager) mFoucesPager).closePkResultPager();
        }
    }

    /**
     * 关闭 每题的 pk 结果展示
     */
    public void closeCurrentPkResult() {
        if (mFoucesPager != null && mFoucesPager instanceof TeamPkResultPager) {
            ((TeamPkResultPager) mFoucesPager).closePkResultPager();
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
