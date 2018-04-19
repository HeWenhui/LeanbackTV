package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoActivity;
import com.xueersi.parentsmeeting.modules.livevideo.page.TeamPkAwardGetPager;
import com.xueersi.parentsmeeting.modules.livevideo.page.TeamPkResultPager;
import com.xueersi.parentsmeeting.modules.livevideo.page.TeamPkTeamSelectPager;
import com.xueersi.xesalib.utils.uikit.ScreenUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by chenkun on 2018/4/12
 * 战队PK 相关业务处理
 */
public class TeamPKBll {
    private Activity activity;
    //战队PK rootView
    private RelativeLayout rlTeamPkContent;

    public  TeamPKBll(Activity activity) {
        this.activity = activity;
    }

    public void initView(RelativeLayout bottomContent) {
        rlTeamPkContent = new RelativeLayout(activity);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.
                LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        bottomContent.addView(rlTeamPkContent, params);
//        Log.e("ck","=======>initView sss:"+teamSelectPager.getRootView());
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
     * 显示获奖 场景
     */
    public void showAwardGetScene(){
       TeamPkAwardGetPager  awardGetPager = new TeamPkAwardGetPager(activity);
        rlTeamPkContent.removeAllViews();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        int screenWidth = ScreenUtils.getScreenWidth();
        int wradio = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * screenWidth / LiveVideoActivity.VIDEO_WIDTH);
        params.rightMargin = wradio;
        rlTeamPkContent.addView(awardGetPager.getRootView(), params);
    }


    /**
     * 展示pk 结果场景
     */
    public void showPkResultScene(){
        TeamPkResultPager resultPager =  new TeamPkResultPager(activity);
        rlTeamPkContent.removeAllViews();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        int screenWidth = ScreenUtils.getScreenWidth();
        int wradio = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * screenWidth / LiveVideoActivity.VIDEO_WIDTH);
        params.rightMargin = wradio;
        rlTeamPkContent.addView(resultPager.getRootView(), params);
    }


    public void showTeamPkView() {
       /* rlTeamPkContent.removeAllViews();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        int screenWidth = ScreenUtils.getScreenWidth();
        int wradio = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * screenWidth / LiveVideoActivity.VIDEO_WIDTH);
        params.rightMargin = wradio;
        rlTeamPkContent.addView(teamPkPager.getRootView(), params);*/
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
