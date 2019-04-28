package com.xueersi.parentsmeeting.modules.livevideo.event;

/**
*理科答题结果 事件
*@author chekun
*created  at 2019/2/20 10:19
*/
public class AnswerResultEvent {

   public String getData() {
      return data;
   }

   public void setData(String data) {
      this.data = data;
   }

   /**理科H5页面回传答题结果原始数据**/
   private String data;

   public AnswerResultEvent(String data) {
      this.data = data;
   }

   @Override
   public String toString() {
      return "AnswerResultEvent{" +
              "data='" + data + '\'' +
              '}';
   }
}
