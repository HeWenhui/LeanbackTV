package com.xueersi.parentsmeeting.modules.livevideo.praiselist.business;

import android.app.Activity;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.log.Loger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.TopicAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ArtsRraiseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.http.ArtsPraiseHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.page.ArtsPraisePager;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.ui.dialog.VerifyCancelAlertDialog;

import org.json.JSONObject;


/**
 * 文科表扬榜
 *
 * @author chenkun
 * @version 1.0, 2018/7/12 下午1:50
 */

public class ArtsPraiseListBll extends LiveBaseBll implements NoticeAction, TopicAction {

    private LiveBll2 mLiveBll;
    //    private RelativeLayout rlPraiseContentView;
    private ArtsPraisePager artsPraisePager;
    private LiveHttpManager mHttpManager;
    private LiveGetInfo mRoomInitData;
    private ArtsPraiseHttpResponseParser mParser;
    private boolean isAvailable;

    /**
     * 是否已经处理过topic 消息了
     */
    private boolean topicHandled;
    private VerifyCancelAlertDialog verifyCancelAlertDialog;
    private String mRankId;

    public ArtsPraiseListBll(Activity activity, LiveBll2 liveBll) {
        super(activity, liveBll);
        mLiveBll = liveBll;
    }

    @Override
    public void setVideoLayout(LiveVideoPoint liveVideoPoint) {
        if (artsPraisePager != null) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) artsPraisePager.getRootView()
                    .getLayoutParams();
            int rightMargin = liveVideoPoint.getRightMargin();
            if (rightMargin != params.rightMargin) {
                params.rightMargin = rightMargin;
                LayoutParamsUtil.setViewLayoutParams(artsPraisePager.getRootView(), params);
            }
        }
    }

    private void addPager(ArtsRraiseEntity data) {
        upLoadLog("ArtsPraise", "_ArtsPraiseListBll_addPager");
        if (artsPraisePager != null) {
            removeView(artsPraisePager.getRootView());
        }
        artsPraisePager = new ArtsPraisePager(mContext, this, data);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        params.rightMargin = getRightMargin();;
        addView(artsPraisePager.getRootView(), params);
    }


    /**
     * @param rankId 榜单id
     */
    public void showPraiseList(String rankId) {
        if (TextUtils.isEmpty(rankId)) {
            return;
        }
        if (artsPraisePager != null && !rankId.equals(mRankId)) {
            mRankId = rankId;
            stopPraise();
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    getRankData(mRankId);
                }
            }, 700);
        } else {
            mRankId = rankId;
            getRankData(rankId);
        }
    }

    private void getRankData(String rankId) {

        if (mHttpManager != null && artsPraisePager == null) {
            String liveId = mRoomInitData.getId();
            String courseId = mRoomInitData.getStudentLiveInfo().getCourseId();
            String counselorId = mRoomInitData.getTeacherId();
           /* String liveId = "210905";
            String courseId = "43091";
            String counselorId = "2670";*/
            mHttpManager.getArtsRankData(rankId, liveId
                    , courseId, counselorId, new
                            HttpCallBack() {
                                @Override
                                public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                                    if (mParser == null) {
                                        mParser = new ArtsPraiseHttpResponseParser();
                                    }
                                    ArtsRraiseEntity entity = mParser.parsePraise(responseEntity);
                                    showPraiseList(entity);
                                }

                                @Override
                                public void onPmError(ResponseEntity responseEntity) {
                                    super.onPmError(responseEntity);
                                    showGetDataFailDlg();
                                }

                                @Override
                                public void onPmFailure(Throwable error, String msg) {
                                    super.onPmFailure(error, msg);
                                    showGetDataFailDlg();
                                }
                            }
            );
        }
    }


    private void showGetDataFailDlg() {

        if (verifyCancelAlertDialog == null) {
            verifyCancelAlertDialog = new VerifyCancelAlertDialog(mContext, ((Activity) mContext).getApplication(),
                    false, VerifyCancelAlertDialog.MESSAGE_VERIFY_CANCEL_TYPE);
            verifyCancelAlertDialog.setVerifyBtnListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPraiseList(mRankId);
                }
            });
        }
        verifyCancelAlertDialog.initInfo("", "获取榜单数据失败,点击重试", VerifyCancelAlertDialog.CANCEL_SELECTED)
                .showDialog();
    }

    /**
     * 老师结束点赞
     */
    private void stopPraise() {
        post(new Runnable() {
            @Override
            public void run() {
                onDestroy();
            }
        });
    }


    /**
     * 显示表扬榜
     */
    private void showPraiseList(ArtsRraiseEntity data) {
        addPager(data);
    }


    int prasieBtnClickTime = 0;

    /**
     * 点赞消息发送时间 间隔
     */
    protected final static long SEND_MSG_INTERVAL = 2000;

    long lastSendTime;

    private Runnable clickTimeSendTask = new Runnable() {
        @Override
        public void run() {
            doSend();
        }
    };

    public void sendPraiseNotice() {
        prasieBtnClickTime++;
        long timePasted = System.currentTimeMillis() - lastSendTime;
        removeCallbacks(clickTimeSendTask);
        if (timePasted >= SEND_MSG_INTERVAL) {
            doSend();
        } else {
            if (prasieBtnClickTime > 0) {
                long sendDelay = SEND_MSG_INTERVAL - timePasted;
                sendDelay = sendDelay < 0 ? 0 : sendDelay;
                postDelayed(clickTimeSendTask, sendDelay);
            }
        }
    }

    private void doSend() {
        sendArtsPraiseNum(prasieBtnClickTime);
        Loger.e("ArtsPraiseListBll", "======>sendPraiseNotice:" + prasieBtnClickTime);
        prasieBtnClickTime = 0;
        lastSendTime = System.currentTimeMillis();
    }

    /**
     * 向教师端发送 点赞数
     *
     * @param praiseNum
     */
    private void sendArtsPraiseNum(int praiseNum) {
        mLogtf.i("sendArtsPraiseNum");
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "" + XESCODE.ARTS_SEND_PRAISE_NUM);
            jsonObject.put("id", "" + mGetInfo.getStuId());
            jsonObject.put("num", "" + praiseNum);
            sendNoticeToCoun(jsonObject);
        } catch (Exception e) {
            mLogtf.e("sendArtsPraiseNum", e);
        }
    }


    /**
     * 更新点赞数
     *
     * @param praiseNum
     */
    private void updatePraiseNum(final int praiseNum) {
        if (artsPraisePager != null) {
            //主线程更新UI
            post(new Runnable() {
                @Override
                public void run() {
                    if (artsPraisePager != null) {
                        artsPraisePager.upDatePraiseNum(praiseNum);
                    }
                }
            });
        }
    }

    /**
     * 从topic中恢复 老师点赞相关信息
     *
     * @param topicInfo
     */
    private void handleTopic(LiveTopic.ArtsPraiseTopicEntity topicInfo) {
        if (!topicHandled) {
            topicHandled = true;
            String rankId = topicInfo.getId();
            if (!TextUtils.isEmpty(rankId)) {
                showPraiseList(rankId);
            }
        }
    }

    /**
     * @return
     */
    private int getRightMargin() {
        LiveVideoPoint liveVideoPoint = LiveVideoPoint.getInstance();
        return liveVideoPoint.getRightMargin();
    }

    @Override
    public void onStop() {
        if (artsPraisePager != null) {
            artsPraisePager.onStop();
        }
    }

    @Override
    public void onResume() {
        if (artsPraisePager != null) {
            artsPraisePager.onResume();
        }
    }

    @Override
    public void onDestroy() {
        if (artsPraisePager != null) {
            artsPraisePager.onDestroy();
        }
        if (verifyCancelAlertDialog != null) {
            verifyCancelAlertDialog.cancelDialog();
        }
        removeCallbacks(clickTimeSendTask);
    }


    public void closePager() {
        if (artsPraisePager != null) {
            final ArtsPraisePager finalartsPraisePager=artsPraisePager;
            post(new Runnable() {
                @Override
                public void run() {
                    removeView(finalartsPraisePager.getRootView());
                }
            });
            artsPraisePager = null;
        }
    }

    /**
     * 上传流程日志到  kibanna
     *
     * @param Tag
     * @param msg
     */
    public void upLoadLog(String Tag, String msg) {
        Loger.d(mContext, Tag, mRoomInitData.getStuId() + "_" + msg, true);
    }


    /**
     * notice 指令集
     */
    private int[] noticeCodes = {
            XESCODE.ARTS_PRAISE_START,
            XESCODE.ARTS_SEND_PRAISE_NUM,
            XESCODE.ARTS_RECEIVE_PRAISE_NUM,
    };

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        if (getInfo != null && getInfo.getShowArtsPraise() == 1) {
            isAvailable = true;
            mHttpManager = getHttpManager();
            mRoomInitData = getInfo;
        }
    }

    @Override
    public int[] getNoticeFilter() {
        return noticeCodes;
    }

    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {
        if (isAvailable) {
            switch (type) {
                case XESCODE.ARTS_PRAISE_START:
                    int id = data.optInt("id", -1);
                    int open = data.optInt("open", -1);
                    Loger.d(mContext, "ArtsPraise", mGetInfo.getStuId() + "_recive_notice_1000_rankId_"
                            + id + "_stateCode_" + open, true);
                    if (open == 1) {
                        showPraiseList(id + "");
                    } else if (open == 0) {
                        stopPraise();
                    }
                    break;
                case XESCODE.ARTS_RECEIVE_PRAISE_NUM:
                    int praiseNum = data.optInt("num");
                    Loger.e("LiveBll", "ARTS_RECEIVE_PRAISE_NUM:" + praiseNum + ":" + data);
                    updatePraiseNum(praiseNum);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onTopic(LiveTopic liveTopic, JSONObject jsonObject, boolean modeChange) {
        if (isAvailable) {
            LiveTopic.ArtsPraiseTopicEntity artsPraiseTopicEntity = liveTopic.getArtsPraiseTopicEntity();
            if (artsPraiseTopicEntity != null) {
                handleTopic(artsPraiseTopicEntity);
            }
        }
    }
}
