package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.media.DrmInitData;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.base.BasePager;
import com.xueersi.parentsmeeting.http.HttpCallBack;
import com.xueersi.parentsmeeting.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoActivity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamPkTeamInfoEntity;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.page.TeamPKAQResultPager;
import com.xueersi.parentsmeeting.modules.livevideo.page.TeamPkAwardPager;
import com.xueersi.parentsmeeting.modules.livevideo.page.TeamPkResultPager;
import com.xueersi.parentsmeeting.modules.livevideo.page.TeamPkTeamSelectPager;
import com.xueersi.parentsmeeting.modules.livevideo.page.TeamPkTeamSelectingPager;
import com.xueersi.parentsmeeting.modules.loginregisters.business.UserBll;
import com.xueersi.xesalib.utils.uikit.ScreenUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.xueersi.parentsmeeting.modules.livevideo.R;

import okhttp3.Call;

/**
 * Created by chenkun on 2018/4/12
 * 战队PK 相关业务处理
 */
public class TeamPKBll {
    private Activity activity;
    //战队PK rootView
    private RelativeLayout rlTeamPkContent;
    private RelativeLayout mRootView;
    private LiveBll mLiveBll;
    private LiveHttpManager mHttpManager;
    private LiveGetInfo roomInitInfo;
    private static  final  String TAG = "TeamPKBll";
    private LiveHttpResponseParser mHttpResponseParser;
    private TeamPkTeamInfoEntity teamInfoEntity;
    private BasePager mFoucesPager;

    public TeamPKBll(Activity activity) {
        this.activity = activity;
    }

    public void setHttpManager(LiveHttpManager liveHttpManager){
        mHttpManager = liveHttpManager;
    }

    public void  setLiveBll (LiveBll bll){
        mLiveBll = bll;
    }

    public void setRoomInitInfo(LiveGetInfo roomInfo){
        roomInitInfo = roomInfo;
    }


    public void  setRootView(RelativeLayout rootView){
        this.mRootView = rootView;
    }

    public  void attachToRootView(){
        rlTeamPkContent = new RelativeLayout(activity);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.
                LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mRootView.addView(rlTeamPkContent, params);
        showPkStateLayout();
        initData();
       if(!isTeamSelected()){
           getTeamInfo();
       }
    }

    /**
     * 判断是否已经分好对了
     * @return
     */
    private boolean isTeamSelected() {
        return  false;
    }

    private void initData() {
        mHttpResponseParser = new LiveHttpResponseParser(activity);
    }

    /**
     * 获取战队信息
     */
    private void getTeamInfo() {

      // 获取 enstuid 的方式：  String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
      //  stuCouId  在默认参数中
        mHttpManager.getTeamInfo(roomInitInfo.getId(),roomInitInfo.getStudentLiveInfo().getClassId(), roomInitInfo.getStudentLiveInfo().getTeamId(), new HttpCallBack() {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                Log.e(TAG,"=====>getTeamInfo onPmSuccess:"+responseEntity.getJsonObject().toString());
                teamInfoEntity = mHttpResponseParser.parseTeamInfo(responseEntity);
                Log.e(TAG,"=====>getTeamInfo onPmSuccess:"+ teamInfoEntity.getKey()+":"
                        + teamInfoEntity.getTeamLogoList().size()+":"+ teamInfoEntity.getTeamMembers().size());
               showTeamSelectScene();
            }

            @Override
            public void onFailure(Call call, IOException e) {
                super.onFailure(call, e);
                Log.e(TAG,"=====>getTeamInfo onFailure:");

            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                super.onPmError(responseEntity);
                Log.e(TAG,"=====>getTeamInfo onPmError:");

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
    public void showTeamSelectScene() {
        TeamPkTeamSelectPager teamSelectPager = new TeamPkTeamSelectPager(activity, this);
        teamSelectPager.setData(teamInfoEntity);
        rlTeamPkContent.removeAllViews();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        int screenWidth = ScreenUtils.getScreenWidth();
        int wradio = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * screenWidth / LiveVideoActivity.VIDEO_WIDTH);
        params.rightMargin = wradio;
        rlTeamPkContent.addView(teamSelectPager.getRootView(), params);
        mFoucesPager = teamSelectPager;
    }

    /**
     * 中途进入战斗选择
     */
    public void enterTeamSelectScene() {
        TeamPkTeamSelectPager teamSelectPager = new TeamPkTeamSelectPager(activity, this);
        rlTeamPkContent.removeAllViews();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        int screenWidth = ScreenUtils.getScreenWidth();
        int wradio = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * screenWidth / LiveVideoActivity.VIDEO_WIDTH);
        params.rightMargin = wradio;
        rlTeamPkContent.addView(teamSelectPager.getRootView(), params);

        teamSelectPager.showTeamSelectedScene();
    }


    /**
     * 显示开宝箱
     */
    public void showAwardGetScene() {
        TeamPkAwardPager awardGetPager = new TeamPkAwardPager(activity);
        rlTeamPkContent.removeAllViews();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        int screenWidth = ScreenUtils.getScreenWidth();
        int wradio = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * screenWidth / LiveVideoActivity.VIDEO_WIDTH);
        params.rightMargin = wradio;
        rlTeamPkContent.addView(awardGetPager.getRootView(), params);
    }

    /**
     * 显示分队进行中
     */
    public void showTeamSelecting() {
        TeamPkTeamSelectingPager selectingPager = new TeamPkTeamSelectingPager(activity, this);
        rlTeamPkContent.removeAllViews();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        int screenWidth = ScreenUtils.getScreenWidth();
        int wradio = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * screenWidth / LiveVideoActivity.VIDEO_WIDTH);
        params.rightMargin = wradio;
        rlTeamPkContent.addView(selectingPager.getRootView(), params);
    }

    /**
     * 展示pk 结果场景
     */
    public void showPkResultScene() {
        TeamPkResultPager resultPager = new TeamPkResultPager(activity, this);
        rlTeamPkContent.removeAllViews();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        int screenWidth = ScreenUtils.getScreenWidth();
        int wradio = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * screenWidth / LiveVideoActivity.VIDEO_WIDTH);
        params.rightMargin = wradio;
        rlTeamPkContent.addView(resultPager.getRootView(), params);
    }

   /* //测试聊天区域UI

    private void addMessageView() {
        TeamPKMessageTestPager testPager = new TeamPKMessageTestPager(activity);
        rlTeamPkContent.addView(testPager.getRootView());
        testPager.setVideoWidthAndHeight(1920, 1080);
        showPkStateLayout();
    }*/


    /**
     * 展示聊天 区域上方 战队pk 状态UI
     */
    private void showPkStateLayout() {
        // step 1
        ViewGroup viewGroup = (ViewGroup) activity.getWindow().getDecorView();
        View pkStateRootView = viewGroup.findViewById(R.id.tpkL_teampk_pkstate_root);
        if(pkStateRootView != null){
            pkStateRootView.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 显示实时答题 奖励
     */
    public void showAnswerQuestionAward() {

        TeamPKAQResultPager aqAwardPager = new TeamPKAQResultPager(activity);
        rlTeamPkContent.removeAllViews();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        int screenWidth = ScreenUtils.getScreenWidth();
        int wradio = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * screenWidth / LiveVideoActivity.VIDEO_WIDTH);
        params.rightMargin = wradio;
        rlTeamPkContent.addView(aqAwardPager.getRootView(), params);
       // addMessageView();
    }

    /**
     * 从asset 文件中获取 lottie  脚本
     *
     * @param path
     * @return
     */
    protected String getJsonFromAssets(String path) {
        String jsonStr = null;
        BufferedReader reader = null;
        try {
            InputStream in = activity.getAssets().open(path);
            reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            reader.close();
            jsonStr = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return jsonStr;
    }

}
