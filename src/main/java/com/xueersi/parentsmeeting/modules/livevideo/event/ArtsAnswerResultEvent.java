package com.xueersi.parentsmeeting.modules.livevideo.event;


/**
*文科答题结果 事件
*@author  chekun
*created  at 2018/9/6 14:07
*/
public class ArtsAnswerResultEvent {
   /**h5 js回调待会的原始数据*/
   private String dataStr;


   private String testId;
   private int isRight;

   private int mType;
   /**js回调 传回答案*/
   public static final int TYPE_H5_ANSWERRESULT = 1;

   /**本地答题*/
   public static final int TYPE_NATIVE_ANSWERRESULT = 2;

   /**本地语音答题 填空 选择*/
   public static final int TYPE_VOICE_SELECT_BLANK = 4;

   /**js回传rolePlay答题结果*/
   public static final int TYPE_ROLEPLAY_ANSWERRESULT = 3;

   /**本地上传语音答题 填空选择*/
   public static final int TYPE_NATIVE_UPLOAD_VOICE_SELECT_BLANK = 5;
    /**
     * @param dataStr   结果数据   type 为1时  dataStr 为答案原始数据  type为2时  为试题id
     * @param type   答题结果类型
     */
   public ArtsAnswerResultEvent(String dataStr,int type){
       this.dataStr = dataStr;
       this.mType = type;
   }
    public String getDataStr() {
        return dataStr;
    }

    public int getType(){
       return  this.mType;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public String getTestId() {
        return testId;
    }


    public void setIsRight(int isRight) {
        this.isRight = isRight;
    }

    public int getIsRight() {
        return isRight;
    }

    @Override
    public boolean equals(Object obj) {
       if(obj != null && obj == this){
           return  true;
       }
       if(obj != null && obj instanceof ArtsAnswerResultEvent){
           ArtsAnswerResultEvent target = (ArtsAnswerResultEvent)obj;
           if(dataStr.equals(target.getDataStr()) && mType == target.getType()){
               return  true;
           }
        }
        return false;
    }


    @Override
    public String toString() {
        return "ArtsAnswerResultEvent{" +
                "dataStr='" + dataStr + '\'' +
                ", mType=" + mType +
                '}';
    }
}