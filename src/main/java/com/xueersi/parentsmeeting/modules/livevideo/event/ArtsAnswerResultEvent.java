package com.xueersi.parentsmeeting.modules.livevideo.event;


/**
*文科答题结果 事件
*@author  chekun
*created  at 2018/9/6 14:07
*/
public class ArtsAnswerResultEvent {
   /**h5 js回调待会的原始数据*/
   private String dataStr;

   public ArtsAnswerResultEvent(String dataStr){
       this.dataStr = dataStr;
   }
    public String getDataStr() {
        return dataStr;
    }
}
