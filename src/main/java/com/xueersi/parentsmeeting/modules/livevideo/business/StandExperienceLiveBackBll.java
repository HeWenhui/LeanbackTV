package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;

import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.lib.framework.utils.TimeUtils;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience.IPresenter;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience.StandExperienceEventBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience.recommodcourse.RecommondCourseBll;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience.standexperiencebuycourse
        .ExperienceBuyCoursePresenter;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class StandExperienceLiveBackBll extends LiveBackBll {
    private String TAG = getClass().getSimpleName();

    public StandExperienceLiveBackBll(Activity activity, VideoLivePlayBackEntity mVideoEntity) {
        super(activity, mVideoEntity);
    }

    @Override
    public void scanQuestion(long position) {
        super.scanQuestion(position);
        if (getExperience() && getPattern() == 2) {//全身直播体验课扫描开关聊天区，三分屏不走这里，有自己的逻辑
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
        if (getExperience() && getPattern() == 2) {//全身直播准备开关聊天区的queue
            initLiveMessageQueue();
        }
    }

    /**
     * 视频结束的时候，扫描一遍所有的livebackbasebll是否需要做什么事情
     */
    public void resultAllComplete() {
//        for (LiveBackBaseBll liveBackBaseBll : liveBackBaseBlls) {
//            if (liveBackBaseBll instanceof StandExperienceEventBaseBll) {
//                ((StandExperienceEventBaseBll) liveBackBaseBll).resultComplete();
//            }
//        }
        for (LiveBackBaseBll liveBackBaseBll : liveBackBaseBlls) {
            if (liveBackBaseBll instanceof StandExperienceEventBaseBll) {
                ((StandExperienceEventBaseBll) liveBackBaseBll).resultComplete();
            }
            if (liveBackBaseBll instanceof ExperienceBuyCoursePresenter) {
//                ((ExperienceBuyCoursePresenter) liveBackBaseBll).showNextWindow();
                showNextWindow((ExperienceBuyCoursePresenter) liveBackBaseBll);
            }
        }
//        showNextWindow(new ExperienceBuyCoursePresenter(activity, this));
//        showNextWindow(ProxUtil.getProxUtil().get(activity, ExperienceBuyCoursePresenter
//                .class));

//        showNextWindow(iPresenter);
    }

    //    购课完成后专用
//    public void buyCourseComplete(boolean isSuccess) {
//        for (LiveBackBaseBll liveBackBaseBll : liveBackBaseBlls) {
//            if (liveBackBaseBll instanceof RecommondCourseBll) {//只有推荐课程才使用这个方法
//                ((RecommondCourseBll) liveBackBaseBll).buyRecommondCourseComplete(isSuccess);
//            }
//        }
//    }
    //学习反馈弹窗bll
//    private ExperienceLearnFeedbackBll experienceLearnFeedbackBll;

    //展示学习反馈弹窗
//    public void showFeedBackWindow() {
//        if (experienceLearnFeedbackBll == null) {
//            experienceLearnFeedbackBll = new ExperienceLearnFeedbackBll(activity, this);
//        }
//        experienceLearnFeedbackBll.showFeedBackPager();
//    }

    //定级卷Bll
//    private StandExperienceEvaluationBll standExperienceEvaluationBll;

    //展现定级卷
//    public void showEvaluation() {
//        if (standExperienceEvaluationBll == null) {
//            standExperienceEvaluationBll = new StandExperienceEvaluationBll(activity, this);
//        }
//
//    }

    /**
     * 展示下一个View的页面,这里可以做一些下一个View展示前的逻辑处理
     *
     * @param mPresenter
     */
    public void showNextWindow(IPresenter mPresenter) {
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
            if (liveBackBaseBll instanceof RecommondCourseBll) {
                ((RecommondCourseBll) liveBackBaseBll).onResume();
            }
        }
    }

}
