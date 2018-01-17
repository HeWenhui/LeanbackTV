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
import com.xueersi.parentsmeeting.modules.livevideo.entity.ThumbsUpListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ThumbsUpProbabilityEntity;
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

    /** 直播底部布局*/
    private RelativeLayout rBottomContent;
    /** 表扬榜根布局 */
    private RelativeLayout rPraiseListContent;
    /** 表扬榜页面 */
    private PraiseListPager mPraiseList;
    /** 点赞概率标识 */
    private int thumbsUpProbability = 0;

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

    /**
     * 显示优秀榜
     *
     * @param honorListEntity
     */
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
    /**
     * 显示点赞榜
     *
     * @param thumbsUpListEntity
     */
    @Override
    public void onThumbsUpList(final ThumbsUpListEntity thumbsUpListEntity) {
        mLogtf.d("onThumbsUpList");
        Drawable d=rBottomContent.getBackground();

        mVPlayVideoControlHandler.post(new Runnable() {
            @Override
            public void run() {
                mPraiseList = new PraiseListPager(activity, thumbsUpListEntity, mLiveBll,PraiseListBll.this, mVPlayVideoControlHandler);
                rPraiseListContent.removeAllViews();
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                rPraiseListContent.addView(mPraiseList.getRootView(), params);
                activity.getWindow().getDecorView().requestLayout();
                activity.getWindow().getDecorView().invalidate();
            }
        });
    }

    /**
     * 显示进步榜
     *
     * @param progressListEntity
     */
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

    /**
     * 显示老师表扬横幅
     *
     * @param stuName
     * @param tecName
     */
    @Override
    public void showPraiseScroll(final String stuName, final String tecName) {
        mLogtf.d("showPraiseScroll");
        if(mPraiseList!=null)
            mVPlayVideoControlHandler.post(new Runnable() {
                @Override
                public void run() {
                    mPraiseList.startScrollAnimation(stuName,tecName);
                }
            });
    }

    /**
     * 收到给我点赞的消息
     *
     * @param stuNames
     */
    @Override
    public void receiveThumbsUpNotice(ArrayList<String> stuNames) {
        mLogtf.d("receiveThumbsUpNotice");
        if(mPraiseList!=null)
            mPraiseList.receiveThumbsUpNotice(stuNames);
    }

    /**
     * 显示感谢点赞的Toast
     *
     */
    @Override
    public void showThumbsUpToast() {
        mLogtf.d("showThumbsUpToast");
        if(mPraiseList!=null)
            mPraiseList.showThumbsUpToast();
    }

    /**
     * 关闭榜单
     */
    @Override
    public void closePraiseList() {
        mLogtf.d("closePraiseList");
        //停止点赞弹幕线程
        if(mPraiseList!=null)
            mPraiseList.setDanmakuStop(true);
        if(mPraiseList!=null)
            mPraiseList.releaseSoundPool();
        //rBottomContent.setClickable(false);
        mVPlayVideoControlHandler.post(new Runnable() {
            @Override
            public void run() {
                rPraiseListContent.removeAllViews();
            }
        });
    }

    /**
     * 设置点赞概率标识
     *
     * @param thumbsUpProbabilityEntity
     */
    @Override
    public void setThumbsUpProbability(ThumbsUpProbabilityEntity thumbsUpProbabilityEntity) {
        mLogtf.d("setThumbsUpProbability");
        thumbsUpProbability = thumbsUpProbabilityEntity.getProbability();
    }

    /**
     * 获取点赞概率标识
     */
    @Override
    public int getThumbsUpProbability( ) {
        mLogtf.d("getThumbsUpProbability");
        return thumbsUpProbability;
    }

    public int getDisplayHeight() {
        return displayHeight;
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

    public void destory(){
        if(mPraiseList!=null)
            mPraiseList.setDanmakuStop(true);
        if(mPraiseList!=null)
            mPraiseList.releaseSoundPool();
    }
}
