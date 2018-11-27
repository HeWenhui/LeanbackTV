package com.xueersi.parentsmeeting.modules.livevideo.question.entity;

import java.util.ArrayList;

public class SpeechResultEntity {
    public int score;
    public int gold;
    public int enery;
    public String headUrl;
    public String name;
    public int fluency;
    public int accuracy;
    public ArrayList<SpeechResultMember> speechResultMembers = new ArrayList<>();
}
