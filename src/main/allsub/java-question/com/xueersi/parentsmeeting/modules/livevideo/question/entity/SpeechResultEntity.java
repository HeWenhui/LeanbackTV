package com.xueersi.parentsmeeting.modules.livevideo.question.entity;

import java.util.ArrayList;

public class SpeechResultEntity {
    /** 评测内容 */
    public String content;
    public int score;
    public int gold;
    public int progress;
    public int energy;
    public int praise = -1;
    public String headUrl;
    public String name;
    public int fluency;
    public int accuracy;
    /** 是不是已作答 */
    public boolean isAnswered;
    public ArrayList<SpeechResultMember> speechResultMembers = new ArrayList<>();
}
