package com.xueersi.parentsmeeting.modules.livevideo.nbh5courseware.business;

import com.xueersi.parentsmeeting.modules.livevideo.entity.NbCourseWareEntity;

/**
 * Created by linyuqiang on 2017/3/25.
 * 物理h5课件
 */
public interface H5CoursewareAction {

    /**
     * h5 课件
     *
     * @param url
     * @param status
     */
    void onH5Courseware(NbCourseWareEntity entity, String status);
}
