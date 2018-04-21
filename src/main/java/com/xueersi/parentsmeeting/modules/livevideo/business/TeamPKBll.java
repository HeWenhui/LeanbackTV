package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoActivity;
import com.xueersi.parentsmeeting.modules.livevideo.page.TeamPKAQResultPager;
import com.xueersi.parentsmeeting.modules.livevideo.page.TeamPkAwardPager;
import com.xueersi.parentsmeeting.modules.livevideo.page.TeamPkResultPager;
import com.xueersi.parentsmeeting.modules.livevideo.page.TeamPkTeamSelectPager;
import com.xueersi.parentsmeeting.modules.livevideo.page.TeamPkTeamSelectingPager;
import com.xueersi.parentsmeeting.modules.videoplayer.media.VideoView;
import com.xueersi.xesalib.utils.uikit.ScreenUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import com.xueersi.parentsmeeting.modules.livevideo.R;
/**
 * Created by chenkun on 2018/4/12
 * 战队PK 相关业务处理
 */
public class TeamPKBll {
    private Activity activity;
    //战队PK rootView
    private RelativeLayout rlTeamPkContent;
    private final VideoView videoView1;

    public  TeamPKBll(Activity activity , VideoView videoView) {
        this.activity = activity;
        videoView1 = videoView;
    }

    public void initView(RelativeLayout bottomContent) {
        rlTeamPkContent = new RelativeLayout(activity);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.
                LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        bottomContent.addView(rlTeamPkContent, params);
//        Log.e("ck","=======>initView sss:"+teamSelectPager.getRootView());

        // 聊天区域测试

       // LiveMessagePager mLiveMessagePager = new LiveMessagePager();

    }


    /**
     * 显示分队仪式 场景
     */
    public void showTeamSelectScene(){
        TeamPkTeamSelectPager teamSelectPager = new TeamPkTeamSelectPager(activity,this);
        rlTeamPkContent.removeAllViews();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        int screenWidth = ScreenUtils.getScreenWidth();
        int wradio = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * screenWidth / LiveVideoActivity.VIDEO_WIDTH);
        params.rightMargin = wradio;
        rlTeamPkContent.addView(teamSelectPager.getRootView(), params);
    }

    /**
     * 中途进入战斗选择
     */
    public void enterTeamSelectScene(){
        TeamPkTeamSelectPager teamSelectPager = new TeamPkTeamSelectPager(activity,this);
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
     * 显示获奖 场景
     */
    public void showAwardGetScene(){
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
    public void showTeamSelecting(){
        TeamPkTeamSelectingPager selectingPager = new TeamPkTeamSelectingPager(activity,this);
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
    public void showPkResultScene(){
        TeamPkResultPager resultPager =  new TeamPkResultPager(activity,this);
        rlTeamPkContent.removeAllViews();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        int screenWidth = ScreenUtils.getScreenWidth();
        int wradio = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * screenWidth / LiveVideoActivity.VIDEO_WIDTH);
        params.rightMargin = wradio;
        rlTeamPkContent.addView(resultPager.getRootView(), params);

        //addMessageView();
    }

    //测试聊天区域UI
/*
    private void addMessageView(){
        TeamPKMessageTestPager testPager = new TeamPKMessageTestPager(activity);
        rlTeamPkContent.addView(testPager.getRootView());
        testPager.setVideoWidthAndHeight( 1920,1080);
        //  testPager.
        showPkStateLayout();
    }
    */


    /**
     * 展示聊天 区域上方 战队pk 状态UI
     */
    private void showPkStateLayout(){
        // step 1
        ViewGroup viewGroup = (ViewGroup) activity.getWindow().getDecorView();
        View pkStateRootView = viewGroup.findViewById(R.id.tpkL_teampk_pkstate_root);
        pkStateRootView.setVisibility(View.VISIBLE);

    }


    /**
     * 显示实时答题 奖励
     */
    public void showAnswerQuestionAward(){

        TeamPKAQResultPager aqAwardPager =  new TeamPKAQResultPager(activity);
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
     * @param videoWidth  视频宽度
     * @param videoHeight 视频高度
     */
    public void setVideoLayout(int videoWidth, int videoHeight) {

        if (videoWidth > 0) {
            final View contentView = activity.findViewById(android.R.id.content);
            final View actionBarOverlayLayout = (View) contentView.getParent();
            Rect r = new Rect();
            actionBarOverlayLayout.getWindowVisibleDisplayFrame(r);
            int screenWidth = (r.right - r.left);
            //计算 右边距
            int wradio = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * videoWidth / LiveVideoActivity.VIDEO_WIDTH);
            wradio += (screenWidth - videoWidth) / 2;
            // 设置 pk 布局 右边距
          /*  if (teamPkPager != null) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)
                        teamPkPager.getRootView().getLayoutParams();
                if (wradio != params.rightMargin) {
                    params.rightMargin = wradio;
                    LayoutParamsUtil.setViewLayoutParams(teamPkPager.getRootView(), params);
                }
            }*/
        }
    }

    /**
     * 从asset 文件中获取 lottie  脚本
     * @param path
     * @return
     */
    protected  String getJsonFromAssets(String path) {
        String jsonStr = null;
        BufferedReader reader = null;
        try {
            InputStream in =  activity.getAssets().open(path);
            reader = new BufferedReader( new InputStreamReader(in));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null){
                 sb.append(line);
                 sb.append("\n");
            }
            reader.close();
            jsonStr = sb.toString();
        }catch (Exception e){
           e.printStackTrace();
        }finally {
            if(reader != null){
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
