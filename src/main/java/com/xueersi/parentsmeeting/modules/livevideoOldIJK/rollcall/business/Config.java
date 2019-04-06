package com.xueersi.parentsmeeting.modules.livevideoOldIJK.rollcall.business;

/**
 * ${tags}
 *
 * @author chenkun
 * @version 1.0, 2018/6/22 下午4:39
 */

public interface Config {

    /**
     * 已签到
     */
    int SIGN_STATE_CODE_SIGNED = 2;

    /**
     * 签到未开始
     */
    int SIGN_STATE_CODE_SIGN_UNSTART = 0;

    /**
     * 签到开始且未签到
     */
    int SIGN_STATE_CODE_UNSIGN = 1;
}
