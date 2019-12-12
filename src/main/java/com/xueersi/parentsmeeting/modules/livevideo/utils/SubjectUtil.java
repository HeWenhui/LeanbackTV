package com.xueersi.parentsmeeting.modules.livevideo.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.entity.CourseTeacherEntity;

import java.util.regex.Pattern;

/**
 * Created by dqq on 2019/5/8.
 */
public class SubjectUtil {


    public static String subModSubjectName(String name, int courseType) {
        return name; // 课程标准化，课程学科标签不做处理展示；2019.9.4 修改
//        String sub = name;
//        if (!TextUtils.isEmpty(sub) && !(sub.equals("编程") || sub.equals("生化") || courseType == XesMallConfig.LIVE_COURSE_LITERACY_NEW)) {
//            sub = sub.substring(0, 1);
//        }
//        return sub;
    }


    public static String subName(String name, boolean hasThree) {
        if (TextUtils.isEmpty(name)) {
            return "";
        }

        if (!hasThree && name.length() < 9) {
            return name;
        }

        boolean allLetter = Pattern.matches("^[a-zA-Z0-9]+$", name);
        int subLng = allLetter ? 7 : 3;
        String subStr = (name.length() > (subLng + 1)) ? (name.substring(0, subLng) + "…") : name;

        return subStr;
    }


    @NonNull
    public static String getTeaType(Context context,int intTeaType) {
        String mainTeaTypeText = context.getString(R.string.exclusive_tea_type_teaching);
        switch (intTeaType){
            case CourseTeacherEntity.IDENTITY_MAIN:
                mainTeaTypeText = context.getString(R.string.exclusive_tea_type_teaching);
                break;
            case CourseTeacherEntity.IDENTITY_SECONDARY:
                mainTeaTypeText = context.getString(R.string.coache_tea_type);
                break;
            case CourseTeacherEntity.IDENTITY_FOREIGN:
                mainTeaTypeText = context.getString(R.string.exclusive_tea_type_teaching);
                break;
            case CourseTeacherEntity.IDENTITY_EXE:
                mainTeaTypeText = context.getString(R.string.exclusive_tea_type);
                break;
        }
        return mainTeaTypeText;
    }

}
