package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils;

public interface IntelligentConstants {
    int PERFECT = 1;
    //
    int GOOD = 2;
    //情况1,50%及以上的单词分数低于60分
    int FEED_BACK_SENTENCE_1_0 = 3;
    //情况2，50%及以下的单词分数低于60分
    int FEED_BACK_WORD_1 = 4;
    //流畅性得分＜60分
    int FEED_BACK_SENTENCE_1_1 = 5;
    //1）	整句重读/二次挑战  大于原分数 老师语音反馈有点退步，多加练习哦！（多套）配合鼓励加油等表情动作。
    int FEED_BACK_SENTENCE_2_0 = 6;
    //1）	整句重读/二次挑战  小于原分数 老师语音反馈有点退步，多加练习哦！（多套）配合鼓励加油等表情动作。
    int FEED_BACK_SENTENCE_2_1 = 7;
    //2）	单词纠音  分数≥60分 反馈真棒/有进步，配合点赞，鼓掌等动作
    int FEED_BACK_WORD_2_0 = 8;
    // 单词纠音 分数＜60分 老师语音反馈认真些再来一次
    int FEED_BACK_WORD_2_1 = 9;
    //3）	单词纠音第二次反馈规则 分数≥60分 老师语音反馈真棒/有进步，配合点赞，鼓掌等动作
    int FEED_BACK_WORD_3_0 = 10;
    // 二次反馈  分数<60
    int FEED_BACK_WORD_3_1 = 11;
}
