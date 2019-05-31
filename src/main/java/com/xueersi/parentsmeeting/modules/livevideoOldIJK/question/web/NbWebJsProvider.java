package com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.web;

import android.util.Log;
import android.webkit.JavascriptInterface;

import com.tencent.smtt.sdk.WebView;
import com.xueersi.parentsmeeting.modules.livevideo.config.NbCourseWareConfig;
import com.xueersi.parentsmeeting.modules.livevideo.event.NbCourseEvent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

/**
 * NB 实验js 通信对象
 *
 * @author chekun
 * created  at 2019/4/3 18:38
 */
public class NbWebJsProvider {

    public NbWebJsProvider(){

    }

    @JavascriptInterface
    public final void postMessage(String data) {
        Log.e("nbTrac","======>postMessage_old:"+data);
        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONObject msgObj = jsonObject.optJSONObject("message");
            if(msgObj != null){
                String type = msgObj.optString("type");
                if(NbCourseWareConfig.NOBOOK_ONLOAD.equals(type)){
                     //加载成功
                    EventBus.getDefault().post(new NbCourseEvent(NbCourseEvent.EVENT_TYPE_ONLOAD));
                }else if(NbCourseWareConfig.NOBOOK_SUBMIT_RESPONSE.equals(type)){
                     //提交结果
                    JSONObject paramObj = msgObj.optJSONObject("params");
                    if(paramObj != null){
                        boolean submitSuccess = paramObj.optBoolean("success",false);
                        if(submitSuccess){
                            NbCourseEvent event = new NbCourseEvent(NbCourseEvent.EVENT_TYPE_SUBMIT_SUCCESS);
                            JSONObject  dataObj = paramObj.optJSONObject("data");
                            String responsStr = dataObj != null?dataObj.toString():"";
                            event.setResponseStr(responsStr);
                            EventBus.getDefault().post(event);
                        }else{
                            NbCourseEvent event = new NbCourseEvent(NbCourseEvent.EVENT_TYPE_SUBMIT_FAIL);
                            String responsStr = paramObj.optString("msg","实验提交失败");
                            event.setResponseStr(responsStr);
                            EventBus.getDefault().post(event);
                        }
                    }else{
                        NbCourseEvent event = new NbCourseEvent(NbCourseEvent.EVENT_TYPE_SUBMIT_FAIL);
                        String responsStr = paramObj.optString("msg","实验提交失败");
                        event.setResponseStr(responsStr);
                        EventBus.getDefault().post(event);
                    }
                }else if(NbCourseWareConfig.NOBOOK_ONE_STEP_CORRECT.equals(type)){
                    //暂不处理 每小步 回调结果
                    /*
                    // 每小步正确
                    NbCourseEvent event = new NbCourseEvent(NbCourseEvent.EVENT_TYPE_STEP_CORRECT);
                    JSONObject paramObj = msgObj.optJSONObject("params");
                    if(paramObj != null){
                        String stepDesc = paramObj.optString("msg");
                        event.setResponseStr(stepDesc);
                    }
                    EventBus.getDefault().post(event);
                    */
                }else if(NbCourseWareConfig.NOBOOK_ONE_STEP_WRONG.equals(type)) {
                    //暂不处理 每小步 回调结果
                    /*
                    NbCourseEvent event = new NbCourseEvent(NbCourseEvent.EVENT_TYPE_STEP_WRONG);
                    // 每小步错误
                    JSONObject paramObj = msgObj.optJSONObject("params");
                    if(paramObj != null){
                        String stepDesc = paramObj.optString("msg");
                        event.setResponseStr(stepDesc);
                    }
                    EventBus.getDefault().post(event);
                    */

                }else if(NbCourseWareConfig.NOBOOK_LOAD_ERROR.equals(type)){
                    //加载失败
                    EventBus.getDefault().post(new NbCourseEvent(NbCourseEvent.EVENT_TYPE_LOAD_ERROR));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @JavascriptInterface
    public final void intoTestMode(String data) {
        // 进入练习模式
        EventBus.getDefault().post(new NbCourseEvent(NbCourseEvent.EVENT_TYPE_INTOTESTMODE));
    }

    @JavascriptInterface
    public final void togglePackUp(String data) {
        // 收起结果页面
        EventBus.getDefault().post(new NbCourseEvent(NbCourseEvent.EVENT_TYPE_TOGGLEPACKUP));
    }

    @JavascriptInterface
    public final void experimentResult(String data){
        //本地结果页加载成功，回传 最高连对，金币数
        NbCourseEvent event = new NbCourseEvent(NbCourseEvent.EVENT_TYPE_RESULTPAGE_ONLOAD);
        event.setResponseStr(data);
        EventBus.getDefault().post(event);
    }


    /**
     * @param view
     * @param msg    消息内容
     * @param target 窗口地址  * 标识 向所有窗口发送
     */
    public void sendMsg(WebView webView, JSONObject msg, String target) {
        if(webView != null){
            webView.loadUrl("javascript:sendToCourseware(" + msg + ",'" + target + "')");
        }
    }


    /**
     * 通知结果页面 老师收题了
     * @param view
     */
    public void onTeachTakeUp(WebView webView){
        if(webView != null){
            webView.loadUrl("javascript: onTeachTakeUp()");
        }
    }


    /**
     * 调用sendToCourseware后的回执
     *
     * @param jsonStr
     */
    @JavascriptInterface
    public void onReceive(String jsonStr) {
    }
}
