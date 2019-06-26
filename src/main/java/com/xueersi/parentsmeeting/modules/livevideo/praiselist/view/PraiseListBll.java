package com.xueersi.parentsmeeting.modules.livevideo.praiselist.view;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.business.WeakHandler;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.contract.PraiseListPresenter;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.contract.PraiseListView;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity.ExcellentListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity.LikeListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity.MinimarketListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity.PraiseListDanmakuEntity;
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
    private int displayWidth, displayHeight, videoWidth, wradio = 0;
    private LiveAndBackDebug liveAndBackDebug;
    private LogToFile mLogtf;
    private WeakHandler mWeakHandler = new WeakHandler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            return false;
        }
    });
    private int mListType = 0;

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
    private PraiseListPager mPraiseListPager;
    private String nonce = "";

//    private void test() {
//        LinearLayout llTest = new LinearLayout(activity);
//        rlPraiseListContent.addView(llTest);
//        Button btnTest1 = new Button(activity);
//        btnTest1.setText("优秀榜");
//        Button btnTest2 = new Button(activity);
//        btnTest2.setText("计算小超市榜");
//        Button btnTest3 = new Button(activity);
//        btnTest3.setText("点赞榜");
//        Button btnTest4 = new Button(activity);
//        btnTest4.setText("关闭榜单");
//        llTest.addView(btnTest1);
//        llTest.addView(btnTest2);
//        llTest.addView(btnTest3);
//        llTest.addView(btnTest4);
//        btnTest1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mPresenter.getExcellentList();
//                mPresenter.getLikeProbability();
//            }
//        });
//        btnTest2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mPresenter.getMiniMarketList();
//                mPresenter.getLikeProbability();
//            }
//        });
//        btnTest3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mPresenter.getLikeList();
//                mPresenter.getLikeProbability();
//            }
//        });
//        btnTest4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                closePraiseList();
//            }
//        });
//    }

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
//                test();
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
     * @param excellentListEntity
     */
    @Override
    public void onExcellentList(final ExcellentListEntity excellentListEntity) {
        mLogtf.d("onExcellentList");
        if (mListType == PraiseListPager.PRAISE_LIST_TYPE_EXECELLENT) {
            //如果当前榜单类型和新开启榜单类型相同，则退出。
            return;
        }
        mListType = PraiseListPager.PRAISE_LIST_TYPE_EXECELLENT;
        mWeakHandler.post(new Runnable() {
            @Override
            public void run() {
                //rBottomContent.setClickable(true);
                mPraiseListPager = new PraiseListPager(activity, excellentListEntity, mPresenter, PraiseListBll.this);
                rlPraiseListContent.removeAllViews();
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                rlPraiseListContent.addView(mPraiseListPager.getRootView(), params);
                activity.getWindow().getDecorView().requestLayout();
                activity.getWindow().getDecorView().invalidate();
            }
        });

        StableLogHashMap logHashMap = new StableLogHashMap("showPraiseList");
        logHashMap.put("listtype", PraiseListPager.PRAISE_LIST_TYPE_EXECELLENT + "");
        logHashMap.put("sno", "4");
        logHashMap.put("stable", "1");
        logHashMap.put("nonce", nonce);
        logHashMap.put("ex", "Y");
        umsAgentDebugPv(LiveVideoConfig.LIVE_PRAISE_LIST, logHashMap.getData());
    }

    /**
     * 显示计算小超市榜
     *
     * @param minimarketListEntity
     */
    @Override
    public void onMiniMarketList(final MinimarketListEntity minimarketListEntity) {
        mLogtf.d("onProgressList");
        if (mListType == PraiseListPager.PRAISE_LIST_TYPE_MINI_MARKET) {
            //如果当前榜单类型和新开启榜单类型相同，则退出。
            return;
        }
        mListType = PraiseListPager.PRAISE_LIST_TYPE_MINI_MARKET;
        mWeakHandler.post(new Runnable() {
            @Override
            public void run() {
                //rBottomContent.setClickable(true);
                mPraiseListPager = new PraiseListPager(activity, minimarketListEntity, mPresenter, PraiseListBll.this);
                rlPraiseListContent.removeAllViews();
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                rlPraiseListContent.addView(mPraiseListPager.getRootView(), params);
                activity.getWindow().getDecorView().requestLayout();
                activity.getWindow().getDecorView().invalidate();
            }
        });

        StableLogHashMap logHashMap = new StableLogHashMap("showPraiseList");
        logHashMap.put("listtype", PraiseListPager.PRAISE_LIST_TYPE_MINI_MARKET + "");
        logHashMap.put("sno", "4");
        logHashMap.put("stable", "1");
        logHashMap.put("nonce", nonce);
        logHashMap.put("ex", "Y");
        umsAgentDebugPv(LiveVideoConfig.LIVE_PRAISE_LIST, logHashMap.getData());
    }

    /**
     * 显示点赞榜
     *
     * @param likeListEntity
     */
    @Override
    public void onLikeList(final LikeListEntity likeListEntity) {
        mLogtf.d("onLikeList");
        if (mListType == PraiseListPager.PRAISE_LIST_TYPE_LIKE) {
            //如果当前榜单类型和新开启榜单类型相同，则退出。
            return;
        }
        mListType = PraiseListPager.PRAISE_LIST_TYPE_LIKE;
        mWeakHandler.post(new Runnable() {
            @Override
            public void run() {
                //rBottomContent.setClickable(true);
                mPraiseListPager = new PraiseListPager(activity, likeListEntity, mPresenter, PraiseListBll.this);
                rlPraiseListContent.removeAllViews();
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                rlPraiseListContent.addView(mPraiseListPager.getRootView(), params);
                activity.getWindow().getDecorView().requestLayout();
                activity.getWindow().getDecorView().invalidate();
            }
        });

        StableLogHashMap logHashMap = new StableLogHashMap("showPraiseList");
        logHashMap.put("listtype", PraiseListPager.PRAISE_LIST_TYPE_LIKE + "");
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
        if (mPraiseListPager != null)
            mWeakHandler.post(new Runnable() {
                @Override
                public void run() {
                    mPraiseListPager.startScrollAnimation(stuName, tecName);
                }
            });
    }

    /**
     * 收到老师广播赞数的消息
     *
     * @param danmakuList
     */
    @Override
    public void receiveLikeNotice(final ArrayList<PraiseListDanmakuEntity> danmakuList) {
        mLogtf.d("receiveLikeNotice");
        if (mPraiseListPager != null)
            mWeakHandler.post(new Runnable() {
                @Override
                public void run() {
                    mPraiseListPager.receiveLikeNotice(danmakuList);
                }
            });
    }

    /**
     * 显示感谢点赞的Toast
     */
    @Override
    public void showLikeToast() {
        mLogtf.d("showLikeToast");
        if (mPraiseListPager != null)
            mWeakHandler.post(new Runnable() {
                @Override
                public void run() {
                    mPraiseListPager.showLikeToast();
                }
            });
    }

    /**
     * 关闭榜单
     */
    @Override
    public void closePraiseList() {
        mLogtf.d("closePraiseList");
        mListType = 0;
        //停止点赞弹幕线程
        if (mPraiseListPager != null)
            mPraiseListPager.onDestroy();
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
                if (mPraiseListPager != null) {
                    mPraiseListPager.alignLayout();
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
