package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils;

import android.os.Environment;

import java.io.File;

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
    //byebye动作1
    // 1）	点评前/时收到结束指令，播放完评价话术直接接下次见（多套）配合拜拜动作
    int END_GOOD_BYE_1 = 12;
    //byebye动作2
    // 2）	播放重读提示/纠音提示时收到结束指令，播放完当前话术，紧接课下多练习哦！下次见。（多套）配合拜拜动作。
    int END_GOOD_BYE_2 = 13;
    //byebye动作3
    // 3）	学生正在重读时收到指令，直接语音反馈课下多练习哦！下次见。（多套）配合拜拜动作。
    int END_GOOD_BYE_3 = 14;
    //byebye动作4
    // 4）	一旦收到收题指令，题干板区域toast提示3s倒计时收题。提示文案“3s后收题”。
//    int END_GOOD_BYE_4 = 15;

    /** 并未测评 */
    int NOT_SPEECH = 0;
    /** 正在测评 */
    int SPEECH_ING = 1;
    /** 测评完成，正在评价 */
    int SPEECH_OVER_JUDGE = 2;
    /** 正在重读 */
    int SPEECH_AGIN = 3;
    /** 评价完成 */
    int JUDGE_OVER = 4;
    /** 本地语音文件存放位置 */
    String AUDIO_EVALUATE_PARENT_URL = Environment.getExternalStorageDirectory() +
            File.separator + "parentsmeeting" + File.separator + "livevideo" +
            File.separator + "audio";
    /** lottie动画存放位置 */
    String INTELLIGENT_LOTTIE_PATH = "intelligent_recognition_study_v2/english/get_score/";
    /** unity3D模型存放位置 */
    String[] intelligent_recognition_unity3D = new String[]{
            Environment.getExternalStorageDirectory() +
                    File.separator + "parentsmeeting" + File.separator + "livevideo" +
                    File.separator + "unity3d"};

    String AUDIO_EVALUATE_FILE_DIR = "audio_evaluate";
    /** unity3D的文件名 */
    String UNITY3D_DIR = "unity_3d";
    /** 英语智能测评的缓存目录 */
    String CACHE_FILE = "intelligent_recognition";

    String SENTENCE_NAME = "1.mp3";

    String UNITY3D_FILE_NAME_1_V_1 = "monscene6";

    String UNITY3D_FILE_NAME_2_V_1 = "monavater7";

    String UNITY_3D_FILE1_URL = "https://xeswxapp.oss-cn-beijing.aliyuncs.com/Android/temp/monscene6";
    String UNITY_3D_FILE2_URL = "https://xeswxapp.oss-cn-beijing.aliyuncs.com/Android/temp/monavater7";

    String AUDIO_EVALUATE_URL = "https://xeswxapp.oss-cn-beijing.aliyuncs.com/test/ieAudio.zip";

    String AUDIO_EVALUATE_FILE_NAME = "ieAudio.zip";

    String PRELOAD_DIR = "webviewCache";
//    String unity3d_file_NAME_2_V_2 =
}
