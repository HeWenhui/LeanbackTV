package com.xueersi.parentsmeeting.modules.livevideo.nbh5courseware.business;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.entity.NbCourseWareEntity;

/**
*
*@author chekun
*created  at 2019/4/9 13:45
*/
public interface NbPresenter {
    /**
     * 上传实验 答题结果
     * @param resultStr
     * @param isForce
     * @param requestCallBack
     */
    void uploadNbResult(String resultStr, String isForce, HttpCallBack requestCallBack);


    /**获取NB 加试信息**/
    void getNBTestInfo(NbCourseWareEntity testInfo, HttpCallBack requestCallBack);


    /**
     * 关闭页面
     * **/
    void closePager();


    /**
     * 发送学生提交加试实验成功消息
     * @param stuId           学生id
     * @param experimentId    实验id
     */
    void sendSubmitSuccessMsg(String stuId, String experimentId);

}
