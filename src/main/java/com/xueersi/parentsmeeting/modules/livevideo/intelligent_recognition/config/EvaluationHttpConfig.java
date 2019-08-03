package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.config;

public class EvaluationHttpConfig {

    /** 获取智能语音反馈信息 */
    public static final String GET_MATERIAL_VOICE_INFOS_URL = "https://app.arts.xueersi.com/v2/intellectVoiceFeedback/getMaterialVoiceInfos";
    /** 英语智能测评第一次提交测评数据 */
    public static final String INTELLIGENT_SPEECH_SUBMIT_URL = "https://app.arts.xueersi.com/v2/intellectVoiceFeedback/submitIntellectVoice";

    public static final String INTELLIGENT_RECOGNITION_TOP3_URL = "https://app.arts.xuersi.com/v2/standLiveStatus/getSpeechEvalAnswerTeamRank";

    /** 智能语音反馈纠音及重读提交接口 wiki:https://app.arts.xueersi.com/v2/intellectVoiceFeedback/submitIntellectVoiceCorrect */
    public static final String SUBMIT_INTELLECT_VOICE_CORRECT_URL = "https://app.arts.xueersi.com/v2/intellectVoiceFeedback/submitIntellectVoiceCorrect";
}
