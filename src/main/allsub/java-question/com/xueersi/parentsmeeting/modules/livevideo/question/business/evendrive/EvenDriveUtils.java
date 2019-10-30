package com.xueersi.parentsmeeting.modules.livevideo.question.business.evendrive;

import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;

import static com.xueersi.parentsmeeting.modules.livevideo.question.business.evendrive.EvenDriveConstants.OPEN_STIMULATION;

public class EvenDriveUtils {
    /**
     * 新连读激励是否打开
     *
     * @param getInfo
     * @return
     */
    public static boolean isOpenStimulation(LiveGetInfo getInfo) {
        return getInfo != null &&
                getInfo.getEvenDriveInfo() != null &&
                OPEN_STIMULATION == getInfo.getEvenDriveInfo().getIsOpenStimulation();
    }

    /**
     * 老的连对激励的限制条件
     *
     * @param getInfo
     * @return
     */
    public static boolean getOldEvenDrive(LiveGetInfo getInfo) {
        return getInfo != null && getInfo.getIsOpenNewCourseWare() == 1
                && getInfo.getIsPrimarySchool() != 1 && getInfo.getIsArts() == 0;
    }

    /**
     * 所有连对激励是否打开
     *
     * @return
     */
    public static boolean getAllEvenDriveOpen(LiveGetInfo getInfo) {
        return isOpenStimulation(getInfo) || getOldEvenDrive(getInfo);
    }
}
