package com.xueersi.parentsmeeting.modules.livevideo.event;
/**
*nobook 实验 事件
*@author chekun
*created  at 2019/4/5 14:46
*/
public class NbCourseEvent {
   /**课件加载成功**/
   public static final int EVENT_TYPE_ONLOAD = 1;
  /**每小步实验正确**/
   public static final int EVENT_TYPE_STEP_CORRECT =2;
   /**每小步实验错误**/
   public static final int EVENT_TYPE_STEP_WRONG =6;

   /**加载失败**/
    public static final int EVENT_TYPE_LOAD_ERROR = 3;

    /**试题 提交失败**/
    public static final int EVENT_TYPE_SUBMIT_FAIL= 4;

    /**NB 方提交试题 成功**/
    public static final int EVENT_TYPE_SUBMIT_SUCCESS = 5;

    /**进入练习模式**/
    public static final int EVENT_TYPE_INTOTESTMODE= 7;

    /**收起结果页面**/
    public static final int EVENT_TYPE_TOGGLEPACKUP= 8;

    /**
     * 试题加载页面关闭
     */
    public static final int EVENT_TYPE_NBH5_CLOSE = 9;

    /**
     *本地结果页面展示成功
     */
    public static final int EVENT_TYPE_RESULTPAGE_ONLOAD = 10;

    private int eventType;

    /**
     * NB 提交返回数据
     */
    private String responseStr;


   public NbCourseEvent(int eventType){
       this.eventType = eventType;
   }

    public int getEventType() {
        return eventType;
    }

    public void setResponseStr(String responseStr) {
        this.responseStr = responseStr;
    }

    public String getResponseStr() {
        return responseStr;
    }
}
