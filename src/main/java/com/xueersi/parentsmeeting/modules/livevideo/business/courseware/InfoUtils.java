package com.xueersi.parentsmeeting.modules.livevideo.business.courseware;

import com.xueersi.parentsmeeting.modules.livevideo.entity.CoursewareInfoEntity;

import java.util.LinkedList;
import java.util.List;

public class InfoUtils {

    public static List mergeNbList(List<CoursewareInfoEntity> coursewareInfoEntities) {
        List<CoursewareInfoEntity.NbCoursewareInfo> ansList = new LinkedList<>();
        for (CoursewareInfoEntity coursewareInfoEntity : coursewareInfoEntities) {
            ansList = appendList(ansList, coursewareInfoEntity.getAddExperiments());
            ansList = appendList(ansList, coursewareInfoEntity.getFreeExperiments());
        }
        return ansList;
    }

    public static <T> List<T> appendList(List<T> totalList, List<T> list) {
        if (totalList != null && list != null) {
            for (T item : list) {
                if (!totalList.contains(item)) {
                    totalList.add(item);
                }
            }
        }
        return totalList;
    }
}
