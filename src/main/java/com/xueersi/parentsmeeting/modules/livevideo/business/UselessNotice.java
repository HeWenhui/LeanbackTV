package com.xueersi.parentsmeeting.modules.livevideo.business;

/**
 * 直播没用的notice
 *
 * @author linyuqiang
 * created  at 2018/5/13
 */
public class UselessNotice {
    /**
     * 是不是在用
     *
     * @param mtype
     * @return
     */
    public static boolean isUsed(int mtype) {
        if (mtype != XESCODE.MODECHANGE && mtype != XESCODE.RANK_FRESH && mtype != XESCODE.TEAMPK_237) {
            return true;
        }
        return false;
    }
}
