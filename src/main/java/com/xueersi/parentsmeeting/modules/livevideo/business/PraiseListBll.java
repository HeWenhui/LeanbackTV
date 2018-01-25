package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoActivity;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.HonorListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ThumbsUpListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ThumbsUpProbabilityEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ProgressListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.PraiseListPager;
import com.xueersi.xesalib.utils.uikit.ScreenUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Zhang Yuansun on 2018/1/2.
 */

public class PraiseListBll implements PraiseListAction, Handler.Callback {

    public static final String TAG = "PraiseListBll";

    private WeakHandler mVPlayVideoControlHandler = new WeakHandler(Looper.getMainLooper(),this);
    private LogToFile mLogtf;
    private Activity activity;
    private LiveBll mLiveBll;
    private int displayWidth, displayHeight, videoWidth;

    public int getDisplayHeight() {
        return displayHeight;
    }

    /** 直播底部布局*/
    private RelativeLayout rBottomContent;
    /** 表扬榜根布局 */
    private RelativeLayout rPraiseListContent;
    /** 表扬榜页面 */
    private PraiseListPager mPraiseList;
    /** 点赞概率标识 */
    private int thumbsUpProbability = 0;
    private String nonce = "";
    /** 表扬榜是否正在展示 */
    private boolean isShowing = false;
    /** 当前榜单类型 */
    private int mPraiseListType = 0;

    public PraiseListBll(Activity activity) {
        mLogtf = new LogToFile(TAG, new File(Environment.getExternalStorageDirectory(), "parentsmeeting/log/" + TAG
                + ".txt"));
        mLogtf.clear();
        this.activity = activity;
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
     * 收到显示榜单的消息
     *
     * @param listType
     * @param nonce
     */
    @Override
    public void onReceivePraiseList(int listType, String nonce) {
        mPraiseListType = listType;
        this.nonce = nonce;
        umsAgentDebug(listType);
    }

    /**
     * 显示优秀榜
     *
     * @param honorListEntity
     */
    @Override
    public void onHonerList(final HonorListEntity honorListEntity) {
        mLogtf.d("onHonerList");
        closePraiseList();
        isShowing = true;
        mVPlayVideoControlHandler.post(new Runnable() {
            @Override
            public void run() {
                //rBottomContent.setClickable(true);
                mPraiseList = new PraiseListPager(activity, honorListEntity, mLiveBll,PraiseListBll.this, mVPlayVideoControlHandler);
                //rPraiseListContent.removeAllViews();
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                rPraiseListContent.addView(mPraiseList.getRootView(), params);
                activity.getWindow().getDecorView().requestLayout();
                activity.getWindow().getDecorView().invalidate();
            }
        });
        umsAgentDebug3(PraiseListPager.PRAISE_LIST_TYPE_HONOR, "Y");
    }
    /**
     * 显示点赞榜
     *
     * @param thumbsUpListEntity
     */
    @Override
    public void onThumbsUpList(final ThumbsUpListEntity thumbsUpListEntity) {
        mLogtf.d("onThumbsUpList");
        closePraiseList();
        isShowing = true;
        mVPlayVideoControlHandler.post(new Runnable() {
            @Override
            public void run() {
                //rBottomContent.setClickable(true);
                mPraiseList = new PraiseListPager(activity, thumbsUpListEntity, mLiveBll,PraiseListBll.this, mVPlayVideoControlHandler);
                //rPraiseListContent.removeAllViews();
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                rPraiseListContent.addView(mPraiseList.getRootView(), params);
                activity.getWindow().getDecorView().requestLayout();
                activity.getWindow().getDecorView().invalidate();
            }
        });
        umsAgentDebug3(PraiseListPager.PRAISE_LIST_TYPE_THUMBS_UP, "Y");
    }

    /**
     * 显示进步榜
     *
     * @param progressListEntity
     */
    @Override
    public void onProgressList(final ProgressListEntity progressListEntity) {
        mLogtf.d("onProgressList");
        closePraiseList();
        isShowing = true;
        mVPlayVideoControlHandler.post(new Runnable() {
            @Override
            public void run() {
                //rBottomContent.setClickable(true);
                mPraiseList = new PraiseListPager(activity, progressListEntity, mLiveBll,PraiseListBll.this, mVPlayVideoControlHandler);
                //rPraiseListContent.removeAllViews();
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                rPraiseListContent.addView(mPraiseList.getRootView(), params);
                activity.getWindow().getDecorView().requestLayout();
                activity.getWindow().getDecorView().invalidate();
            }
        });
        umsAgentDebug3(PraiseListPager.PRAISE_LIST_TYPE_PROGRESS, "Y");
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
    public void receiveThumbsUpNotice(final ArrayList<String> stuNames) {
        mLogtf.d("receiveThumbsUpNotice");
        if(mPraiseList!=null)
            mVPlayVideoControlHandler.post(new Runnable() {
                @Override
                public void run() {
                    mPraiseList.receiveThumbsUpNotice(stuNames);
                }
            });
    }

    /**
     * 显示感谢点赞的Toast
     *
     */
    @Override
    public void showThumbsUpToast() {
        mLogtf.d("showThumbsUpToast");
        if(mPraiseList!=null)
            mVPlayVideoControlHandler.post(new Runnable() {
                @Override
                public void run() {
                    mPraiseList.showThumbsUpToast();
                }
            });
    }

    /**
     * 关闭榜单
     */
    @Override
    public void closePraiseList() {
        mLogtf.d("closePraiseList");
        //停止点赞弹幕线程
        isShowing = false;
        mPraiseListType = 0;
        if(mPraiseList!=null)
            mPraiseList.setDanmakuStop(true);
        if(mPraiseList!=null)
            mPraiseList.releaseSoundPool();
        //rBottomContent.setClickable(false);
        mVPlayVideoControlHandler.post(new Runnable() {
            @Override
            public void run() {
                if(rPraiseListContent!=null)
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

    /**
     * 设置点赞按钮是否可点击
     *
     * @param enabled
     */
    @Override
    public void setThumbsUpBtnEnabled(final boolean enabled) {
        mLogtf.d("setThumbsUpBtnEnabled");
        if(mPraiseList!=null)
            mVPlayVideoControlHandler.post(new Runnable() {
                @Override
                public void run() {
                    mPraiseList.setThumbsUpBtnEnabled(enabled);
                }
            });
    }

    /**
     * 播放器区域变化时更新视图
     *
     * @param width
     * @param height
     */
    @Override
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

    /**
     * Activity退出
     *
     */
    @Override
    public void destory(){
        if(mPraiseList!=null)
            mPraiseList.setDanmakuStop(true);
        if(mPraiseList!=null)
            mPraiseList.releaseSoundPool();
    }

    /**
     * 判断榜单是否正在显示中
     *
     */
    @Override
    public boolean isShowing() {
        return isShowing;
    }

    /**
     * 设置当前榜单类型
     *
     * @param listType
     */
    @Override
    public void setCurrentListType(int listType) {
        mPraiseListType = listType;
    }

    /**
     * 获取当前榜单类型
     *
     */
    @Override
    public int getCurrentListType() {
        return mPraiseListType;
    }

    /**
     * 系统日志
     *
     * @param listtype
     */
    public void umsAgentDebug(int listtype){
        HashMap<String,String> map=new HashMap<>();
        map.put("logtype","receivePraiseList");
        map.put("listtype",listtype+"");
        map.put("sno","3");
        map.put("stable","2");
        map.put("ex","Y");
        mLiveBll.umsAgentSystemWithTeacherRole(LiveVideoConfig.LIVE_PRAISE_LIST,map);
    }

    /**
     * 交互日志
     *
     * @param listtype
     */
    public void umsAgentDebug2(int listtype){
        HashMap<String,String> map=new HashMap<>();
        map.put("logtype","praisePraiseList");
        map.put("listtype",listtype+"");
        map.put("stable","2");
        map.put("expect","1");
        map.put("sno","5");
        map.put("ex","Y");
        mLiveBll.umsAgentInteractionWithTeacherRole(LiveVideoConfig.LIVE_PRAISE_LIST,map);
    }

    /**
     * 展现日志
     *
     * @param listtype
     * @param ex
     */
    public void umsAgentDebug3(int listtype, String ex){
        HashMap<String,String> map=new HashMap<>();
        map.put("logtype","showPraiseList");
        map.put("listtype",listtype+"");
        map.put("sno","4");
        map.put("stable","1");
        map.put("nonce",nonce);
        map.put("ex",ex);
        mLiveBll.umsAgentShowWithTeacherRole(LiveVideoConfig.LIVE_PRAISE_LIST,map);
    }
}
