package com.xueersi.parentsmeeting.modules.livevideo.event;

import android.text.SpannableStringBuilder;

import com.xueersi.common.base.BaseEvent;
import com.xueersi.common.entity.BaseVideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoChapterEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoCourseEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoCourseEntity.ShowVideoCourseList;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoSectionEntity;

import java.util.List;

/**
 * 课程视频播放器的事件
 *
 * @author ZouHao
 */
public class PlaybackVideoEvent extends BaseEvent {

    public static class OnGetVideoCourseEntity extends PlaybackVideoEvent {
        /**
         * 上个页面传过来的VideoCourseEntity对象
         */
        private VideoCourseEntity data;

        /**
         * 上个页面传过来的VideoCourseEntity对象
         */
        public VideoCourseEntity getData() {
            return data;
        }

        public OnGetVideoCourseEntity(VideoCourseEntity entity) {
            this.data = entity;
        }
    }

    /**
     * 请求互动题成功并播放视频
     */
    public static class OnLoadQuestionList extends PlaybackVideoEvent {

        private VideoSectionEntity data;
        /**
         * 播放地址
         */
        private String playWebPath;

        /**
         * 视频播放的地址
         */
        public String getPlayWebPath() {
            return playWebPath;
        }

        public void setPlayWebPath(String playWebPath) {
            this.playWebPath = playWebPath;
        }

        public VideoSectionEntity getData() {
            return data;
        }

        public OnLoadQuestionList(VideoSectionEntity data, String webPath) {
            this.data = data;
            this.playWebPath = webPath;
        }
    }

    /**
     * 看完当前视频触发的事件
     */
    public static class OnFinishLookCourseVideoEvent extends PlaybackVideoEvent {

        /**
         * 成功消息的填充字符串
         */
        private SpannableStringBuilder builder;

        /**
         * 返回消息字符串
         */
        public SpannableStringBuilder getMessageBuilder() {
            return builder;
        }

        public OnFinishLookCourseVideoEvent(SpannableStringBuilder builder) {
            this.builder = builder;
        }

        public OnFinishLookCourseVideoEvent() {

        }

    }

    /**
     * 网络异常错误
     */
    public static class OnWebErrorEvent extends PlaybackVideoEvent {
        /**
         * 是否是加载视频失败
         */
        public boolean isLoadingVideoError = false;

        public OnWebErrorEvent(boolean isLoadingError) {
            this.isLoadingVideoError = isLoadingError;
        }
    }

    /**
     * 视频课程的信息数据异常，需要通知课程列表页和课程详情页刷新数据
     */
    public static class OnVideoCourseDataErrorEvent extends PlaybackVideoEvent {
        /** 参数错误 */
        public static final int PARAMETER_ERROR = 6;
        /** 已经退掉此课程 */
        public static final int ALREADY_BACK_COURSE = 2;
        /** 课程已过期 */
        public static final int ALREADY_FINISH_COURSE = 3;
        /** 实验课程只能在电脑上观看 */
        public static final int TEST_COURSE = 4;
        /** 正在请假中 */
        public static final int COURSE_LEAVEING = 5;

        private int mError;

        public int getError() {
            return mError;
        }

        public OnVideoCourseDataErrorEvent(int iError) {
            this.mError = iError;
        }
    }

    /**
     * 互动题回答正确的金币提示
     */
    public static class OnQuestionAnswerScuess extends PlaybackVideoEvent {
        /**
         * 金币奖励提示语
         */
        private SpannableStringBuilder builder;

        public SpannableStringBuilder getAnswerSucessMessage() {
            return builder;
        }

        public OnQuestionAnswerScuess(SpannableStringBuilder builder) {
            this.builder = builder;
        }
    }

    /**
     * 请求互动题成功并播放视频
     */
    public static class OnLoadLivePlayBackVideo extends PlaybackVideoEvent {

        private VideoSectionEntity data;
        /**
         * 播放地址
         */
        private String playWebPath;

        /**
         * 视频播放的地址
         */
        public String getPlayWebPath() {
            return playWebPath;
        }

        public void setPlayWebPath(String playWebPath) {
            this.playWebPath = playWebPath;
        }

        public VideoSectionEntity getData() {
            return data;
        }

        public OnLoadLivePlayBackVideo(VideoSectionEntity data, String webPath) {
            this.data = data;
            this.playWebPath = webPath;
        }
    }

    /**
     * 请求互动题成功并播放视频
     */
    public static class OnQuesionDown extends PlaybackVideoEvent {
        private BaseVideoQuestionEntity mQuestionEntity;

        public BaseVideoQuestionEntity getVideoQuestionEntity() {
            return mQuestionEntity;
        }

        public OnQuesionDown(BaseVideoQuestionEntity questionEntity) {
            this.mQuestionEntity = questionEntity;
        }
    }

    /**
     * 请求播放视频
     */
    public static class OnPlayLivePlayBack extends PlaybackVideoEvent {
        private VideoSectionEntity sectionEntity;

        public VideoSectionEntity getData() {
            return sectionEntity;
        }

        public OnPlayLivePlayBack(VideoSectionEntity sectionEntit) {
            this.sectionEntity = sectionEntit;

        }
    }

    /**
     * 金币不足
     */
    public static class OnGoleNotEnough extends PlaybackVideoEvent {

        public OnGoleNotEnough() {
        }
    }

    /**
     * 金币扣除成功
     */
    public static class OnGoleEnough extends PlaybackVideoEvent {

        private VideoSectionEntity sectionEntity;

        private VideoResultEntity result;

        public VideoSectionEntity getData() {
            return sectionEntity;
        }

        public VideoResultEntity getResult() {
            return result;
        }

        public OnGoleEnough(VideoSectionEntity sectionEntit, VideoResultEntity result) {
            this.sectionEntity = sectionEntit;
            this.result = result;

        }
    }

    /**
     * 返回数据（数据库或者网络）
     */
    public static class OnLiveBackListEvent extends PlaybackVideoEvent {

        private List<ShowVideoCourseList> data;

        private boolean isWebData = false;

        /**
         * 返回数据
         */
        public List<ShowVideoCourseList> getData() {
            return data;
        }

        /**
         * 数据是否来自网络
         */
        public boolean isWebData() {
            return isWebData;
        }

        public OnLiveBackListEvent(List<ShowVideoCourseList> data, boolean isWebData) {
            this.data = data;
            this.isWebData = isWebData;
        }

    }

    /**
     * 返回数据（数据库或者网络）
     */
    public static class OnLiveListEvent extends PlaybackVideoEvent {

        private List<VideoChapterEntity> data;

        private boolean isWebData = false;

        /**
         * 返回数据
         */
        public List<VideoChapterEntity> getData() {
            return data;
        }

        /**
         * 数据是否来自网络
         */
        public boolean isWebData() {
            return isWebData;
        }

        public OnLiveListEvent(List<VideoChapterEntity> data, boolean isWebData) {
            this.data = data;
            this.isWebData = isWebData;
        }

    }

    /**
     * 获取金币成功
     */
    public static class OnGetRedPacket extends PlaybackVideoEvent {
        // 金币数
        private VideoResultEntity resultEntity;

        public VideoResultEntity getVideoResultEntity() {
            return resultEntity;
        }

        public OnGetRedPacket(VideoResultEntity resultEntity) {
            this.resultEntity = resultEntity;
        }
    }

    /**
     * 获取金币成功
     */
    public static class OnSaveQuestionResult extends PlaybackVideoEvent {
        private int goldNum = 0;

        public int getGoldNum() {
            return goldNum;
        }

        public OnSaveQuestionResult(int goldNum) {
            this.goldNum = goldNum;
        }
    }

    /**
     * 网络异常错误
     */
    public static class OnLivePlayBackWebErrorEvent extends PlaybackVideoEvent {
        /**
         * 是否是加载视频失败
         */
        public boolean isLoadingVideoError = false;

        public OnLivePlayBackWebErrorEvent(boolean isLoadingError) {
            this.isLoadingVideoError = isLoadingError;
        }
    }

    /**
     * 互动题结果
     */
    public static class OnAnswerReslut extends PlaybackVideoEvent {
        // 返回结果
        private VideoResultEntity resultEntity;
        private VideoQuestionEntity questionEntity;

        public VideoResultEntity getVideoResultEntity() {
            return resultEntity;
        }

        public OnAnswerReslut(VideoResultEntity resultEntity) {
            this.resultEntity = resultEntity;
        }

        public VideoQuestionEntity getQuestionEntity() {
            return questionEntity;
        }

        public void setQuestionEntity(VideoQuestionEntity questionEntity) {
            this.questionEntity = questionEntity;
        }
    }

    /**
     * 提交互动题没有网络
     */
    public static class OnPlayVideoWebError extends PlaybackVideoEvent {
        /**
         * 互动题答案
         */
        private String result;

        public String getResult() {
            return result;
        }

        public OnPlayVideoWebError(String result) {
            this.result = result;
        }
    }

}
