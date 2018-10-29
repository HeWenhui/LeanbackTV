package com.xueersi.parentsmeeting.modules.livevideo.entity;

import java.util.List;

/**
 * Created by David on 2018/9/4.
 */

public class NewArtsAnswer {

    private List<String> blank;
    private List<String> choice;
    private String useVoice;
    private String voiceTime;
    private String voiceUrl;
    private String testId;

    public List<String> getBlank() {
        return blank;
    }

    public void setBlank(List<String> blank) {
        this.blank = blank;
    }

    public List<String> getChoice() {
        return choice;
    }

    public void setChoice(List<String> choice) {
        this.choice = choice;
    }

    public String getUseVoice() {
        return useVoice;
    }

    public void setUseVoice(String useVoice) {
        this.useVoice = useVoice;
    }

    public String getVoiceTime() {
        return voiceTime;
    }

    public void setVoiceTime(String voiceTime) {
        this.voiceTime = voiceTime;
    }

    public String getVoiceUrl() {
        return voiceUrl;
    }

    public void setVoiceUrl(String voiceUrl) {
        this.voiceUrl = voiceUrl;
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    @Override
    public String toString() {
        return "NewArtsAnswer{" +
                "blank=" + blank +
                ", choice=" + choice +
                ", useVoice='" + useVoice + '\'' +
                ", voiceTime='" + voiceTime + '\'' +
                ", voiceUrl='" + voiceUrl + '\'' +
                ", testId='" + testId + '\'' +
                '}';
    }
}
