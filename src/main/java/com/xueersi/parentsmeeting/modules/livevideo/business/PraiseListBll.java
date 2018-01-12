package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoActivity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.HonorListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LikeListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LikeProbabilityEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ProgressListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.page.PraiseListPager;
import com.xueersi.xesalib.utils.uikit.ScreenUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Zhang Yuansun on 2018/1/2.
 */

public class PraiseListBll implements PraiseListAction, Handler.Callback {

    public static final String TAG = "PraiseListBll";

    private WeakHandler mVPlayVideoControlHandler = new WeakHandler(this);
    private LogToFile mLogtf;
    private Activity activity;
    private LiveBll mLiveBll;
    private LiveHttpResponseParser liveHttpResponseParser;
    private int displayWidth, displayHeight, videoWidth;

    /** 点赞概率 */
    private int likeProbability = 0;

    public int getDisplayHeight(){
        return this.displayHeight;
    }

    private RelativeLayout rBottomContent;
    /** 表扬榜的布局 */
    private RelativeLayout rPraiseListContent;
    /** 表扬榜 */
    private PraiseListPager mPraiseList;


    public PraiseListBll(Activity activity) {
        mLogtf = new LogToFile(TAG, new File(Environment.getExternalStorageDirectory(), "parentsmeeting/log/" + TAG
                + ".txt"));
        mLogtf.clear();
        this.activity = activity;
        liveHttpResponseParser = new LiveHttpResponseParser(activity);
        setVideoLayout(ScreenUtils.getScreenWidth(), ScreenUtils.getScreenHeight());
    }

    @Override
    public boolean handleMessage(Message message) {
        return false;
    }

    public void setLiveBll(LiveBll mLiveBll) {
        this.mLiveBll = mLiveBll;
    }

    public void initView(RelativeLayout bottomContent) {

        rBottomContent = bottomContent;
        //表扬榜
        if (rPraiseListContent != null) {
            //设置主视图参数
            RelativeLayout.LayoutParams mainParam=new RelativeLayout.LayoutParams(videoWidth, displayHeight);
            mainParam.addRule(RelativeLayout.CENTER_VERTICAL);
            rPraiseListContent.setLayoutParams(mainParam);
            bottomContent.addView(rPraiseListContent);
        }

        else{
            rPraiseListContent = new RelativeLayout(activity);
            rPraiseListContent.setId(R.id.rl_livevideo_content_praiselist);
            //设置主视图参数
            RelativeLayout.LayoutParams mainParam=new RelativeLayout.LayoutParams(videoWidth, displayHeight);
            mainParam.addRule(RelativeLayout.CENTER_VERTICAL);
            rPraiseListContent.setLayoutParams(mainParam);
            bottomContent.addView(rPraiseListContent);
        }
    }

    @Override
    public void onHonerList(final HonorListEntity honorListEntity) {
        mLogtf.d("onHonerList");
        mVPlayVideoControlHandler.post(new Runnable() {
            @Override
            public void run() {
                mPraiseList = new PraiseListPager(activity, honorListEntity, mLiveBll,PraiseListBll.this, mVPlayVideoControlHandler);
                rPraiseListContent.removeAllViews();
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                rPraiseListContent.addView(mPraiseList.getRootView(), params);
                activity.getWindow().getDecorView().requestLayout();
                activity.getWindow().getDecorView().invalidate();
            }
        });
    }

    @Override
    public void onLikeList(final LikeListEntity likeListEntity) {
        mLogtf.d("onLikeList");
        Drawable d=rBottomContent.getBackground();

        mVPlayVideoControlHandler.post(new Runnable() {
            @Override
            public void run() {
                mPraiseList = new PraiseListPager(activity, likeListEntity, mLiveBll,PraiseListBll.this, mVPlayVideoControlHandler);
                rPraiseListContent.removeAllViews();
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                rPraiseListContent.addView(mPraiseList.getRootView(), params);
                activity.getWindow().getDecorView().requestLayout();
                activity.getWindow().getDecorView().invalidate();
            }
        });
    }

    @Override
    public void onProgressList(final ProgressListEntity progressListEntity) {
        mLogtf.d("onProgressList");
        mVPlayVideoControlHandler.post(new Runnable() {
            @Override
            public void run() {
                mPraiseList = new PraiseListPager(activity, progressListEntity, mLiveBll,PraiseListBll.this, mVPlayVideoControlHandler);
                rPraiseListContent.removeAllViews();
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                rPraiseListContent.addView(mPraiseList.getRootView(), params);
                activity.getWindow().getDecorView().requestLayout();
                activity.getWindow().getDecorView().invalidate();
            }
        });
    }

    @Override
    public void closePraiseList() {
        mLogtf.d("onClosePraiseList");
        //停止控制轮播消息的线程
        mPraiseList.setRunning(false);
        //rBottomContent.setClickable(false);
        rPraiseListContent.removeAllViews();

    }

    @Override
    public void setLikeProbability(LikeProbabilityEntity likeProbabilityEntity) {
        mLogtf.d("setLikeProbability");
        likeProbability = likeProbabilityEntity.getProbability();

    }

    @Override
    public int getLikeProbability() {
        mLogtf.d("getLikeProbability");
        return  likeProbability;
    }

    @Override
    public void showPraiseScroll(String stuName,String tecName) {
        mLogtf.d("showPraiseScroll");
        if(mPraiseList!=null)
            mPraiseList.startScrollAnimation(stuName,tecName);
    }

    @Override
    public void receiveLikeMessage(ArrayList<String> stuNames) {
        mLogtf.d("receiveLikeMessage");
        if(mPraiseList!=null)
            mPraiseList.receiveLikeMessage(stuNames);
    }


    /**
     * 播放器区域变化时更新视图
     * @param width
     * @param height
     */
    public void setVideoLayout(int width, int height) {
        if (displayWidth == width && displayHeight == height) {
            return;
        }
        displayHeight = height;
        displayWidth = width;

        int screenWidth = ScreenUtils.getScreenWidth();
        int screenHeight = ScreenUtils.getScreenHeight();
        int wradio = 0, topMargin = 0, bottomMargin = 0;
        if (width > 0) {
            wradio = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * width / LiveVideoActivity.VIDEO_WIDTH);
            wradio += (screenWidth - width) / 2;
            videoWidth = displayWidth - wradio;
        }
        if(rPraiseListContent!=null){
            RelativeLayout.LayoutParams params=(RelativeLayout.LayoutParams) rPraiseListContent.getLayoutParams();
            params.height=displayHeight;
            params.width=videoWidth;
            rPraiseListContent.setLayoutParams(params);
        }

    }
}
