package com.xueersi.parentsmeeting.modules.livevideo.question.business.evendrive;

import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;

import static com.xueersi.parentsmeeting.modules.livevideo.question.business.evendrive.EvenDriveConstants.OPEN_STIMULATION;

public class EvenDriveUtils {

    public static boolean isOpenStimulation(LiveGetInfo getInfo) {
        return getInfo != null &&
                getInfo.getEvenDriveInfo() != null &&
                OPEN_STIMULATION == getInfo.getEvenDriveInfo().getIsOpenStimulation();
    }
}
