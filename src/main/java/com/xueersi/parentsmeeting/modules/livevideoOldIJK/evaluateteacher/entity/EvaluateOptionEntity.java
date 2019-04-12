package com.xueersi.parentsmeeting.modules.livevideoOldIJK.evaluateteacher.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by：WangDe on 2018/11/30 16:43
 */
public class EvaluateOptionEntity {

    Map<String,String> evaluateScore;
    Map<String,List<String>> teacherEvaluOption;
    Map<String,List<String>> tutorEvaluOption;
    public EvaluateOptionEntity(){
        evaluateScore = new HashMap<>();
        teacherEvaluOption = new HashMap<>();
        tutorEvaluOption = new HashMap<>();
    }

    public Map<String, String> getEvaluateScore() {
        return evaluateScore;
    }

    public void setEvaluateScore(Map<String, String> evaluateScore) {
        this.evaluateScore = evaluateScore;
    }

    public Map<String, List<String>> getTeacherEvaluOption() {
        return teacherEvaluOption;
    }

    public void setTeacherEvaluOption(Map<String, List<String>> teacherEvaluOption) {
        this.teacherEvaluOption = teacherEvaluOption;
    }

    public Map<String, List<String>> getTutorEvaluOption() {
        return tutorEvaluOption;
    }

    public void setTutorEvaluOption(Map<String, List<String>> tutorEvaluOption) {
        this.tutorEvaluOption = tutorEvaluOption;
    }
}
