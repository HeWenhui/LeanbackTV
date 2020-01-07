package com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.bll;

import com.xrs.bury.ConstantCode;
import com.xrs.bury.xrsbury.XrsBury;

/**
 * @ProjectName: xueersiwangxiao
 * @Package: com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.bll
 * @ClassName: LightLiveBury
 * @Description: 轻直播埋点 都带liveid
 * @Author: WangDe
 * @CreateDate: 2020/1/6 17:11
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/1/6 17:11
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class LightLiveBury extends XrsBury {
    public static String liveId;

    /**
     * @brief Bury 点击事件
     */
    public static void clickBury(String jsonStr) {
        baseBury(ConstantCode.BuryType_click, false, 0, jsonStr,liveId);
    }
    /**
     * @brief Bury 点击事件
     */
    public static void clickBury(String jsonStr, Object... strParam) {
        baseBury(ConstantCode.BuryType_click, false, 0, jsonStr, addLiveId(strParam));
    }

    /**
     * @brief Bury 显示事件
     */
    public static void showBury(String jsonStr) {
        baseBury(ConstantCode.BuryType_show, false, 0, jsonStr, liveId);
    }
    /**
     * @brief Bury 显示事件
     */
    public static void showBury(String jsonStr, Object... strParam) {
        baseBury(ConstantCode.BuryType_show, false, 0, jsonStr, addLiveId(strParam));
    }

    /**
     * @brief Bury 页面事件(开始)
     */
    public static void pageStartBury(String jsonStr) {

        baseBury(ConstantCode.BuryType_pv, true, ConstantCode.PV_TYPE_START, jsonStr, liveId);
    }

    /**
     * @brief Bury 页面事件(开始)
     */
    public static void pageStartBury(String jsonStr, Object... strParam) {
        baseBury(ConstantCode.BuryType_pv, true, ConstantCode.PV_TYPE_START, jsonStr,addLiveId(strParam));
    }


    /**
     * @brief Bury 页面事件（结束）
     */
    public static void pageEndBury(String jsonStr) {

        baseBury(ConstantCode.BuryType_pv, true, ConstantCode.PV_TYPE_END, jsonStr,liveId);

    }


    /**
     * @brief Bury 页面事件（结束）
     */
    public static void pageEndBury(String jsonStr, Object... strParam) {
        baseBury(ConstantCode.BuryType_pv, true, ConstantCode.PV_TYPE_END, jsonStr,addLiveId(strParam));

    }

    private static Object[] addLiveId(Object... strParam){
        Object[] param = new Object[strParam.length+1];
        param[0] = liveId;
        for (int i = 1; i < param.length; i++) {
            param[i] = strParam[i-1];
        }
        return param;
    }

}
