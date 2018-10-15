package com.xueersi.parentsmeeting.modules.livevideo.praiselist.business;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.business.WeakHandler;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.HonorListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ThumbsUpListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ThumbsUpProbabilityEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ProgressListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.page.PraiseListPager;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Zhang Yuansun on 2018/1/2.
 */

public class PraiseListBll implements PraiseListAction, Handler.Callback {

    public static final String TAG = "PraiseListBll";

    private LiveAndBackDebug liveAndBackDebug;
    private WeakHandler mVPlayVideoControlHandler = new WeakHandler(Looper.getMainLooper(), this);
    private LogToFile mLogtf;
    private Activity activity;
    private PraiseListIRCBll mLiveBll;
    private int displayWidth, displayHeight, videoWidth;
    int wradio = 0;

    public int getDisplayHeight() {
        return displayHeight;
    }

    /** 直播底部布局 */
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
        mLogtf = new LogToFile(activity, TAG);
        mLogtf.clear();
        this.activity = activity;
        LiveVideoPoint liveVideoPoint = LiveVideoPoint.getInstance();
        setVideoLayout(liveVideoPoint);
    }

    @Override
    public boolean handleMessage(Message message) {
        return false;
    }

    public void setLiveBll(PraiseListIRCBll mLiveBll) {
        this.mLiveBll = mLiveBll;
    }

    public void initView(final RelativeLayout bottomContent) {

        mVPlayVideoControlHandler.post(new Runnable() {
            @Override
            public void run() {
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
        });
    }

    /**
     * 收到显示榜单的消息
     *
     * @param listType
     * @param nonce
     */
    @Override
    public void onReceivePraiseList(int listType, String nonce) {
        this.nonce = nonce;

        StableLogHashMap logHashMap = new StableLogHashMap("receivePraiseList");
        logHashMap.put("logtype", "receivePraiseList");
        logHashMap.put("listtype", listType + "");
        logHashMap.put("sno", "3");
        logHashMap.put("stable", "2");
        logHashMap.put("ex", "Y");
        umsAgentDebugSys(LiveVideoConfig.LIVE_PRAISE_LIST, logHashMap.getData());
    }

    /**
     * 显示优秀榜
     *
     * @param honorListEntity
     */
    @Override
    public void onHonerList(final HonorListEntity honorListEntity) {
        mLogtf.d("onHonerList");
        //closePraiseList();
        isShowing = true;
        mVPlayVideoControlHandler.post(new Runnable() {
            @Override
            public void run() {
                //rBottomContent.setClickable(true);
                mPraiseList = new PraiseListPager(activity, honorListEntity, mLiveBll, PraiseListBll.this, mVPlayVideoControlHandler);
                rPraiseListContent.removeAllViews();
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                rPraiseListContent.addView(mPraiseList.getRootView(), params);
                activity.getWindow().getDecorView().requestLayout();
                activity.getWindow().getDecorView().invalidate();
            }
        });

        StableLogHashMap logHashMap = new StableLogHashMap("showPraiseList");
        logHashMap.put("listtype", PraiseListPager.PRAISE_LIST_TYPE_HONOR + "");
        logHashMap.put("sno", "4");
        logHashMap.put("stable", "1");
        logHashMap.put("nonce", nonce);
        logHashMap.put("ex", "Y");
        umsAgentDebugPv(LiveVideoConfig.LIVE_PRAISE_LIST, logHashMap.getData());
    }

    /**
     * 显示点赞榜
     *
     * @param thumbsUpListEntity
     */
    @Override
    public void onThumbsUpList(final ThumbsUpListEntity thumbsUpListEntity) {
        mLogtf.d("onThumbsUpList");
        //closePraiseList();
        isShowing = true;
        mVPlayVideoControlHandler.post(new Runnable() {
            @Override
            public void run() {
                //rBottomContent.setClickable(true);
                mPraiseList = new PraiseListPager(activity, thumbsUpListEntity, mLiveBll, PraiseListBll.this, mVPlayVideoControlHandler);
                rPraiseListContent.removeAllViews();
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                rPraiseListContent.addView(mPraiseList.getRootView(), params);
                activity.getWindow().getDecorView().requestLayout();
                activity.getWindow().getDecorView().invalidate();
            }
        });

        StableLogHashMap logHashMap = new StableLogHashMap("showPraiseList");
        logHashMap.put("listtype", PraiseListPager.PRAISE_LIST_TYPE_THUMBS_UP + "");
        logHashMap.put("sno", "4");
        logHashMap.put("stable", "1");
        logHashMap.put("nonce", nonce);
        logHashMap.put("ex", "Y");
        umsAgentDebugPv(LiveVideoConfig.LIVE_PRAISE_LIST, logHashMap.getData());
    }

    /**
     * 显示进步榜
     *
     * @param progressListEntity
     */
    @Override
    public void onProgressList(final ProgressListEntity progressListEntity) {
        mLogtf.d("onProgressList");
        //closePraiseList();
        isShowing = true;
        mVPlayVideoControlHandler.post(new Runnable() {
            @Override
            public void run() {
                //rBottomContent.setClickable(true);
                mPraiseList = new PraiseListPager(activity, progressListEntity, mLiveBll, PraiseListBll.this, mVPlayVideoControlHandler);
                rPraiseListContent.removeAllViews();
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                rPraiseListContent.addView(mPraiseList.getRootView(), params);
                activity.getWindow().getDecorView().requestLayout();
                activity.getWindow().getDecorView().invalidate();
            }
        });

        StableLogHashMap logHashMap = new StableLogHashMap("showPraiseList");
        logHashMap.put("listtype", PraiseListPager.PRAISE_LIST_TYPE_PROGRESS + "");
        logHashMap.put("sno", "4");
        logHashMap.put("stable", "1");
        logHashMap.put("nonce", nonce);
        logHashMap.put("ex", "Y");
        umsAgentDebugPv(LiveVideoConfig.LIVE_PRAISE_LIST, logHashMap.getData());
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
        if (mPraiseList != null)
            mVPlayVideoControlHandler.post(new Runnable() {
                @Override
                public void run() {
                    mPraiseList.startScrollAnimation(stuName, tecName);
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
        if (mPraiseList != null)
            mVPlayVideoControlHandler.post(new Runnable() {
                @Override
                public void run() {
                    mPraiseList.receiveThumbsUpNotice(stuNames);
                }
            });
    }

    /**
     * 显示感谢点赞的Toast
     */
    @Override
    public void showThumbsUpToast() {
        mLogtf.d("showThumbsUpToast");
        if (mPraiseList != null)
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
        if (mPraiseList != null)
            mPraiseList.setDanmakuStop(true);
        if (mPraiseList != null)
            mPraiseList.releaseSoundPool();
        //rBottomContent.setClickable(false);
        mVPlayVideoControlHandler.post(new Runnable() {
            @Override
            public void run() {
                if (rPraiseListContent != null)
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
    public int getThumbsUpProbability() {
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
        if (mPraiseList != null)
            mVPlayVideoControlHandler.post(new Runnable() {
                @Override
                public void run() {
                    mPraiseList.setThumbsUpBtnEnabled(enabled);
                }
            });
    }

    /**
     * 播放器区域变化时更新视图
     */
    @Override
    public void setVideoLayout(final LiveVideoPoint liveVideoPoint) {
        mVPlayVideoControlHandler.post(new Runnable() {
            @Override
            public void run() {
                int screenWidth = getScreenParam();
                displayHeight = liveVideoPoint.screenHeight;
                displayWidth = screenWidth;
                int screenHeight = ScreenUtils.getScreenHeight();
                wradio = liveVideoPoint.getRightMargin();
                if (displayWidth - wradio == videoWidth) {
                    return;
                } else {
                    videoWidth = displayWidth - wradio;
                }
                if (rPraiseListContent != null){
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rPraiseListContent.getLayoutParams();
                    params.height= displayHeight;
                    params.width=videoWidth;
                    rPraiseListContent.setLayoutParams(params);
                }
            }
        });
    }

    private int getScreenParam() {
        LiveVideoPoint liveVideoPoint = LiveVideoPoint.getInstance();
        return liveVideoPoint.screenWidth;
    }

    /**
     * Activity退出
     */
    @Override
    public void destory() {
        if (mPraiseList != null)
            mPraiseList.setDanmakuStop(true);
        if (mPraiseList != null)
            mPraiseList.releaseSoundPool();
    }

    /**
     * 判断榜单是否正在显示中
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
     */
    @Override
    public int getCurrentListType() {
        return mPraiseListType;
    }


    public void umsAgentDebugSys(String eventId, final Map<String, String> mData) {
        if (liveAndBackDebug == null) {
            liveAndBackDebug = ProxUtil.getProxUtil().get(activity, LiveAndBackDebug.class);
        }
        liveAndBackDebug.umsAgentDebugSys(eventId, mData);
    }

    public void umsAgentDebugInter(String eventId, final Map<String, String> mData) {
        if (liveAndBackDebug == null) {
            liveAndBackDebug = ProxUtil.getProxUtil().get(activity, LiveAndBackDebug.class);
        }
        liveAndBackDebug.umsAgentDebugInter(eventId, mData);
    }

    public void umsAgentDebugPv(String eventId, final Map<String, String> mData) {
        if (liveAndBackDebug == null) {
            liveAndBackDebug = ProxUtil.getProxUtil().get(activity, LiveAndBackDebug.class);
        }
        liveAndBackDebug.umsAgentDebugPv(eventId, mData);
    }
}