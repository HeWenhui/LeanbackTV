package com.xueersi.parentsmeeting.modules.livevideo.fragment.se;

import android.app.Activity;
import android.text.TextUtils;

import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.network.IpAddressUtil;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.analytics.umsagent.UmsConstants;
import com.xueersi.lib.framework.utils.TimeUtils;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveAppUserInfo;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.se.examination.StandExperienceEvaluationBll;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.se.learnfeedback.StandExperienceLearnFeedbackBll;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.se.recommodcourse.StandExperienceRecommondBll;

import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class StandExperienceLiveBackBll extends LiveBackBll {

    public StandExperienceLiveBackBll(Activity activity, VideoLivePlayBackEntity mVideoEntity) {
        super(activity, mVideoEntity);
    }

    @Override
    public void scanQuestion(long position) {
        super.scanQuestion(position);
        if (getExperience() && getPattern() == LiveVideoConfig.LIVE_PATTERN_2) {//全身直播体验课扫描开关聊天区，三分屏不走这里，有自己的逻辑
            scanMessage(position);
        }
    }

    /**
     * zyy:扫描聊天区开启或者关闭
     *
     * @param position ms
     *                 playPosition s
     */
    private void scanMessage(long position) {
        VideoQuestionEntity oldQuestionEntity = mQuestionEntity;
        int playPosition = TimeUtils.gennerSecond(position);
        logger.d("scanQuestion:playPosition=" + playPosition);
        List<VideoQuestionEntity> lstVideoQuestion = mVideoEntity.getLstVideoQuestion();
        if (lstVideoQuestion == null || lstVideoQuestion.size() == 0) {
            return;
        }
        //处理聊天区逻辑
        for (VideoQuestionEntity videoQuestionEntity : lstVideoQuestion) {
            //这是使用for循环，仅仅只是为了拿到一个CATEGORY_OPEN_CHAT 或者CATEGORY_CLOSE_CHAT时间
            if (videoQuestionEntity.getvCategory() == LocalCourseConfig.CATEGORY_OPEN_CHAT ||
                    videoQuestionEntity.getvCategory() == LocalCourseConfig.CATEGORY_CLOSE_CHAT) {
                //体验课聊天区关闭或者打开，独立于任何题型
                if (openQue != null && closeQue != null && !openQue.isEmpty() && !closeQue.isEmpty()) {
                    if (videoQuestionEntity.getvCategory() == LocalCourseConfig.CATEGORY_OPEN_CHAT) {
                        if (openQue.peek() < closeQue.peek()) {
                            if (playPosition >= openQue.peek() && playPosition <= closeQue.peek()) {
                                LiveBackBaseBll liveBackBaseBll = array.get(LocalCourseConfig.CATEGORY_OPEN_CHAT);
                                if (liveBackBaseBll != null) {
                                    liveBackBaseBll.showQuestion(oldQuestionEntity, videoQuestionEntity, showQuestion);
                                    logger.i(playPosition + " 2:进去了打开站立直播聊天区");
                                }
                                break;
                            }
                        }

                    } else {
                        if (playPosition < openQue.peek()) {
                            LiveBackBaseBll liveBackBaseBll = array.get(LocalCourseConfig.CATEGORY_CLOSE_CHAT);
                            if (liveBackBaseBll != null) {
                                liveBackBaseBll.showQuestion(oldQuestionEntity, videoQuestionEntity, showQuestion);
                                logger.i(playPosition + " 1:进去了关闭站立直播聊天区");
                            }
                            break;
                        } else if (playPosition >= openQue.peek() && playPosition <= closeQue.peek()) {
                        } else {

                            LiveBackBaseBll liveBackBaseBll = array.get(LocalCourseConfig.CATEGORY_CLOSE_CHAT);
                            if (liveBackBaseBll != null) {
                                liveBackBaseBll.showQuestion(oldQuestionEntity,
                                        videoQuestionEntity, showQuestion);
                                logger.i(playPosition + " 3:进去了关闭站立直播聊天区");
                                openQue.poll();
                                closeQue.poll();
                            }
                            break;
                        }

                    }
                }

            }
        }
    }

    /**
     * 存储打开聊天区的Event的所有Open-startTime，优先队列
     */
    private Queue<Integer> openQue;
    /**
     * 存储关闭聊天区的Event的所有Close-startTime，优先队列
     */
    private Queue<Integer> closeQue;

    /**
     * zyy:筛选出开关聊天区是哪些时间段，采用优先队列排序
     */
    private void initLiveMessageQueue() {
        openQue = new PriorityQueue<>();
        openQue.add(0);//聊天区默认是开启的，所以默认开启时间是0
        closeQue = new PriorityQueue<>();

        for (VideoQuestionEntity videoQuestionEntity : mVideoEntity.getLstVideoQuestion()) {
            int openStartTime, closeStartTime;
            if (videoQuestionEntity.getvCategory() == LocalCourseConfig.CATEGORY_OPEN_CHAT) {//打开聊天
                openStartTime = videoQuestionEntity.getvQuestionInsretTime();
                openQue.add(openStartTime);
            }
            if (videoQuestionEntity.getvCategory() == LocalCourseConfig.CATEGORY_CLOSE_CHAT) {//关闭聊天
                closeStartTime = videoQuestionEntity.getvQuestionInsretTime();
                closeQue.add(closeStartTime);
            }
        }
        //1.如果打开聊天区的Event (包括刚刚加入的在 0 时刻开始的聊天事件) 比关闭聊天区的Event多一个(多两个以上是数据有问题)，
        //  那么视屏结束的时间就是关闭聊天区的时间，在关闭队列末尾加上一个无穷大的数来模拟视频结束时间
        //2.如果两个Event数量一样多，说明视频结束时间不是关闭聊天区的时间，即在视频结束之前老师就已经关闭了聊天区
        if (openQue.size() > closeQue.size()) {
            closeQue.add(Integer.MAX_VALUE);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (getExperience() && getPattern() == LiveVideoConfig.LIVE_PATTERN_2) {//全身直播准备开关聊天区的queue
            initLiveMessageQueue();
        }
    }

    /**
     * 视频结束的时候，扫描一遍所有的{@link #liveBackBaseBlls}做视频结束操作
     */
    public void resultAllComplete() {
        for (LiveBackBaseBll liveBackBaseBll : liveBackBaseBlls) {
            if (liveBackBaseBll instanceof StandExperienceEventBaseBll) {
                ((StandExperienceEventBaseBll) liveBackBaseBll).resultComplete();
            }
            //如果prek为false，并且examUrl不为null,则加载定级卷
            if (!mVideoEntity.isPrek() && !TextUtils.isEmpty(mVideoEntity.getExamUrl())) {
                //定级卷展示窗口
                if (liveBackBaseBll instanceof StandExperienceEvaluationBll) {
                    showNextWindow((StandExperienceEvaluationBll) liveBackBaseBll);
                }
            } else {
                //反过来，prek为true，或者examUrl为null，不加载定级卷
                if (liveBackBaseBll instanceof StandExperienceLearnFeedbackBll) {
                    showNextWindow((StandExperienceLearnFeedbackBll) liveBackBaseBll);
                }
            }
        }
    }


    /**
     * 展示下一个View的页面,这里可以做一些下一个View展示前的逻辑处理
     *
     * @param mPresenter 下一个需要展示的Presenter类
     */
    public void showNextWindow(IExperiencePresenter mPresenter) {
        if (mPresenter != null) {
            mPresenter.showWindow();
        }
    }

    @Override
    public String getPrefix() {
        return "ELB";
    }

    public void onResume() {
        for (LiveBackBaseBll liveBackBaseBll : liveBackBaseBlls) {
            if (liveBackBaseBll instanceof StandExperienceRecommondBll) {
                ((StandExperienceRecommondBll) liveBackBaseBll).onResume();
            }
        }
    }

    /**
     * 上传日志，视频播放错误。
     *
     * @param eventId
     * @param mData
     */
    @Override
    public void umsAgentDebugSys(String eventId, Map<String, String> mData) {
        mData.put("uid", LiveAppUserInfo.getInstance().getStuId());
        mData.put("uname", LiveAppUserInfo.getInstance().getChildName());
        mData.put("courseid", mVideoEntity.getCourseId());
        mData.put("liveid", mVideoEntity.getLiveId());
        mData.put("orderid", mVideoEntity.getChapterId());
        mData.put("livetype", String.valueOf(4));
        mData.put("logtype", "play error");
        mData.put("os", "Android");
        mData.put("ip", IpAddressUtil.USER_IP);

//        if ("PublicLiveDetailActivity".equals(where)) {
//            mData.put("livetype", "" + 2);
//        } else {
//            mData.put("livetype", "" + 3);
//        }
        mData.put("clits", "" + System.currentTimeMillis());
//        Loger.d(mContext, eventId, mData, true);
        UmsAgentManager.umsAgentDebug(activity, eventId, mData);
    }

    @Override
    public void umsAgentDebugInter(String eventId, final Map<String, String> mData) {
        mData.put("uid", LiveAppUserInfo.getInstance().getStuId());
        mData.put("uname", LiveAppUserInfo.getInstance().getChildName());
        mData.put("courseid", mVideoEntity.getCourseId());
        mData.put("liveid", mVideoEntity.getLiveId());
        mData.put("livetype", "" + 4);
//        if ("PublicLiveDetailActivity".equals(where)) {
//            mData.put("livetype", "" + 2);
//        } else {
//            mData.put("livetype", "" + 3);
//        }
        mData.put("eventid", "" + eventId);
        mData.put("clits", "" + System.currentTimeMillis());
        UmsAgentManager.umsAgentOtherBusiness(activity, appID, UmsConstants.uploadBehavior, mData);
    }

    @Override
    public void umsAgentDebugPv(String eventId, final Map<String, String> mData) {
        mData.put("uid", LiveAppUserInfo.getInstance().getStuId());
        mData.put("uname", LiveAppUserInfo.getInstance().getChildName());
        mData.put("courseid", mVideoEntity.getCourseId());
        mData.put("liveid", mVideoEntity.getLiveId());
        mData.put("livetype", "" + 4);
//        if ("PublicLiveDetailActivity".equals(where)) {
//            mData.put("livetype", "" + 2);
//        } else {
//            mData.put("livetype", "" + 3);
//        }
        mData.put("eventid", "" + eventId);
        mData.put("clits", "" + System.currentTimeMillis());
        UmsAgentManager.umsAgentOtherBusiness(activity, appID, UmsConstants.uploadShow, mData);
    }

}
