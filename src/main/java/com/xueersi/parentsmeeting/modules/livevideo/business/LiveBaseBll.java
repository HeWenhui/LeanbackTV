package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.content.Context;
import android.media.midi.MidiManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.xueersi.common.base.BaseBll;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 直播间 bll 基类
 *
 * @author chekun
 * created  at 2018/6/20 9:34
 */
public  class LiveBaseBll extends BaseBll {

    protected View mRootView;
    private final LiveAction mLiveAction;

    public LiveBaseBll(Context context, LiveAction liveAction, View rootView) {
        super(context);
        mLiveAction = liveAction;
        mRootView = rootView;
    }

    /**
     * 发送直播间聊天消息
     */
    public void sendMsg(JSONObject jsonObject) {
       if(mLiveAction != null){
           mLiveAction.sendMessage(jsonObject);
       }
    }

    /**
     * 发送 notice 消息
     */
    public void sendNotice(JSONObject jsonObject,String target) {
        if(mLiveAction != null){
            mLiveAction.sendNotice(target,jsonObject);
        }
    }

    /**
     * 上传 系统日志
     * @param eventId
     * @param data
     */
    public void  umsAgentDebugSys(String eventId,Map<String,String> data){
        if(mLiveAction != null){
            mLiveAction.umsAgentDebugSys(eventId,data);
        }
    }

    /**
     *  上传交互日志
     * @param eventId
     * @param data
     */
    public void umsAgentDebugInter(String eventId,Map<String,String> data){
        if(mLiveAction != null){
            mLiveAction.umsAgentDebugInter(eventId,data);
        }
    }


    /**
     * 上传 展现日志
     * @param eventId
     * @param data
     */
    public void umsAgentDebugPv(String eventId,Map<String,String> data){
        if(mLiveAction != null){
            mLiveAction.umsAgentDebugPv(eventId,data);
        }
    }


    /**
     * 直播间初始化完成
     * @param data 直播间初始化参数
     */
    public void onLiveInited(LiveGetInfo data) {

    }


    /**
     * 直播间创建
     */
    public void onCreate(HashMap<String,Object> data) {

    }

    /**
     * activity onPause
     */
    public void onPause() {

    }

    /**
     * activity onStop
     */
    public void onStop() {

    }

    /**
     * activity onResume
     */
    public void onResume() {

    }

    /**
     * activity onDestory
     */
    public void onDestory() {

    }


    ///公共管理View 添加、移除、虚拟键引起布局 变化相关

    /**
     * 弹出toast，判断activity是不是在活动
     * @param text
     */
    public void showToast(String text) {
        ActivityStatic activityStatic = (ActivityStatic) mContext;
        if (activityStatic.isResume()) {
            XESToastUtils.showToast(mContext, text);
        }
    }



}
