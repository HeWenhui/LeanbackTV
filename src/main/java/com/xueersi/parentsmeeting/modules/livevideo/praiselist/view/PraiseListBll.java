package com.xueersi.parentsmeeting.modules.livevideo.praiselist.view;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.business.WeakHandler;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.HonorListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ProgressListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ThumbsUpListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ThumbsUpProbabilityEntity;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.contract.PraiseListPresenter;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.contract.PraiseListView;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.presenter.PraiseListIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.page.PraiseListPager;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Zhang Yuansun on 2018/1/2.
 */

public class PraiseListBll implements PraiseListView {
    public static final String TAG = "PraiseListBll";
    private Activity activity;
    private PraiseListPresenter mPresenter;
    private LiveAndBackDebug liveAndBackDebug;
    private LogToFile mLogtf;
    private int displayWidth, displayHeight, videoWidth;
    private int wradio = 0;
    private WeakHandler mWeakHandler = new WeakHandler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            return false;
        }
    });

    /**
     * 直播底部布局
     */
    private RelativeLayout mRootView;
    /**
     * 表扬榜根布局
     */
    private RelativeLayout rlPraiseListContent;
    /**
     * 表扬榜页面
     */
    private PraiseListPager mPraiseList;
    private String nonce = "";

    public PraiseListBll(Activity activity) {
        mLogtf = new LogToFile(activity, TAG);
        mLogtf.clear();
        this.activity = activity;
        LiveVideoPoint liveVideoPoint = LiveVideoPoint.getInstance();
        setVideoLayout(liveVideoPoint);
    }

    @Override
    public void initView(final RelativeLayout bottomContent) {
        mWeakHandler.post(new Runnable() {
            @Override
            public void run() {
                mRootView = bottomContent;
                //表扬榜
                if (rlPraiseListContent != null) {
                    //设置主视图参数
                    RelativeLayout.LayoutParams mainParam = new RelativeLayout.LayoutParams(videoWidth, displayHeight);
                    mainParam.addRule(RelativeLayout.CENTER_VERTICAL);
                    rlPraiseListContent.setLayoutParams(mainParam);
                    bottomContent.addView(rlPraiseListContent);
                } else {
                    rlPraiseListContent = new RelativeLayout(activity);
                    rlPraiseListContent.setId(R.id.rl_livevideo_content_praiselist);
                    //设置主视图参数
                    RelativeLayout.LayoutParams mainParam = new RelativeLayout.LayoutParams(videoWidth, displayHeight);
                    mainParam.addRule(RelativeLayout.CENTER_VERTICAL);
                    rlPraiseListContent.setLayoutParams(mainParam);
                    bottomContent.addView(rlPraiseListContent);
                }
            }
        });

    }

    @Override
    public void setPresenter(PraiseListPresenter presenter) {
        this.mPresenter = presenter;
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
        mWeakHandler.post(new Runnable() {
            @Override
            public void run() {
                //rBottomContent.setClickable(true);
                mPraiseList = new PraiseListPager(activity, honorListEntity, mPresenter);
                rlPraiseListContent.removeAllViews();
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                rlPraiseListContent.addView(mPraiseList.getRootView(), params);
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
        mWeakHandler.post(new Runnable() {
            @Override
            public void run() {
                //rBottomContent.setClickable(true);
                mPraiseList = new PraiseListPager(activity, thumbsUpListEntity, mPresenter);
                rlPraiseListContent.removeAllViews();
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                rlPraiseListContent.addView(mPraiseList.getRootView(), params);
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
        mWeakHandler.post(new Runnable() {
            @Override
            public void run() {
                //rBottomContent.setClickable(true);
                mPraiseList = new PraiseListPager(activity, progressListEntity, mPresenter);
                rlPraiseListContent.removeAllViews();
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                rlPraiseListContent.addView(mPraiseList.getRootView(), params);
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
            mWeakHandler.post(new Runnable() {
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
     * @param thumbsUpProbabilityEntity
     */
    @Override
    public void receiveThumbsUpNotice(final ArrayList<String> stuNames, final ThumbsUpProbabilityEntity thumbsUpProbabilityEntity) {
        mLogtf.d("receiveThumbsUpNotice");
        if (mPraiseList != null)
            mWeakHandler.post(new Runnable() {
                @Override
                public void run() {
                    mPraiseList.receiveThumbsUpNotice(stuNames, thumbsUpProbabilityEntity);
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
            mWeakHandler.post(new Runnable() {
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
        if (mPraiseList != null)
            mPraiseList.onDestroy();
        //rBottomContent.setClickable(false);
        mWeakHandler.post(new Runnable() {
            @Override
            public void run() {
                if (rlPraiseListContent != null)
                    rlPraiseListContent.removeAllViews();
            }
        });
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
            mWeakHandler.post(new Runnable() {
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
        mWeakHandler.post(new Runnable() {
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
                if (rlPraiseListContent != null) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rlPraiseListContent.getLayoutParams();
                    params.height = displayHeight;
                    params.width = videoWidth;
                    rlPraiseListContent.setLayoutParams(params);
                }
            }
        });
    }

    private int getScreenParam() {
        LiveVideoPoint liveVideoPoint = LiveVideoPoint.getInstance();
        return liveVideoPoint.screenWidth;
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
