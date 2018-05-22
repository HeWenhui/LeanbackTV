package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.xueersi.parentsmeeting.base.AbstractBusinessDataCallBack;
import com.xueersi.parentsmeeting.base.BaseHttpBusiness;
import com.xueersi.parentsmeeting.http.HttpCallBack;
import com.xueersi.parentsmeeting.http.HttpRequestParams;
import com.xueersi.parentsmeeting.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TalkConfHost;
import com.xueersi.xesalib.utils.log.Loger;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 直播聊天调度
 *
 * @author lyqai
 * @date 2018/5/22
 */
public class IRCTalkConf {
    private static String TAG = "IRCTalkConf";
    private BaseHttpBusiness baseHttpBusiness;
    private ArrayList<TalkConfHost> hosts;
    /** 从上面的列表选择一个服务器 */
    private int mSelectTalk = 0;
    private String liveId;
    private LiveGetInfo liveGetInfo;
    private String classid;
    private int mLiveType;
    private HttpCallBack callBack;
    private AbstractBusinessDataCallBack businessDataCallBack;
    private String baseHost = null;
    private static final int GET_SERVER = 1;
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Loger.d(TAG, "handleMessage:what=" + msg.what);
            getserver();
        }
    };

    public IRCTalkConf(LiveGetInfo liveGetInfo, int mLiveType, BaseHttpBusiness baseHttpBusiness, ArrayList<TalkConfHost> hosts) {
        this.liveId = liveGetInfo.getId();
        this.mLiveType = mLiveType;
        this.baseHttpBusiness = baseHttpBusiness;
        this.hosts = hosts;
        LiveGetInfo.StudentLiveInfoEntity studentLiveInfo = liveGetInfo.getStudentLiveInfo();
        if (studentLiveInfo != null) {
            classid = studentLiveInfo.getClassId();
        }
        for (int i = 0; i < hosts.size(); i++) {
            TalkConfHost talkConfHost = hosts.get(i);
            if (!talkConfHost.isIp()) {
                baseHost = talkConfHost.getHost();
                break;
            }
        }
        if (baseHost == null) {
            baseHost = "chatgslb.xescdn.com";
        }
    }

    public void getserver(final AbstractBusinessDataCallBack businessDataCallBack) {
        this.businessDataCallBack = businessDataCallBack;
        Loger.d(TAG, "getserver:hosts=" + hosts.size());
        if (hosts.isEmpty()) {
            return;
        }
        getserver();
    }

    private void getserver() {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveid", liveId);
        if (mLiveType == LiveBll.LIVE_TYPE_LIVE) {
            params.addBodyParam("appid", "1");
        } else {
            params.addBodyParam("appid", "2");
        }
//        params.addBodyParam("location", liveId);
        params.addBodyParam("classid", classid);
        params.setWriteAndreadTimeOut(2000);
        TalkConfHost talkConfHost = hosts.get(mSelectTalk++ % hosts.size());
        String host = talkConfHost.getHost();
        String url = "http://" + host + "/getserver";
        if (talkConfHost.isIp()) {
            params.addHeaderParam("Host", baseHost);
        }
        callBack = new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                if (callBack != this) {
                    return;
                }
                handler.removeMessages(GET_SERVER);
                JSONArray jsonArray = (JSONArray) responseEntity.getJsonObject();
                Loger.d(TAG, "onPmSuccess:jsonObject=" + jsonArray);
                List<LiveGetInfo.NewTalkConfEntity> mNewTalkConf = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        LiveGetInfo.NewTalkConfEntity entity = new LiveGetInfo.NewTalkConfEntity();
                        entity.setHost(jsonObject.getString("host"));
                        entity.setPort(jsonObject.getString("port"));
                        entity.setPwd(jsonObject.getString("pwd"));
                        mNewTalkConf.add(entity);
                    } catch (Exception e) {

                    }
                }
                businessDataCallBack.onDataSucess(mNewTalkConf);
//                if (callBack != this) {
//                    return;
//                }
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
                Loger.e(TAG, "onPmFailure:msg=" + msg);
                if (callBack != this) {
                    return;
                }
                reTry();
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                super.onPmError(responseEntity);
                Loger.d(TAG, "onPmError:responseEntity=" + responseEntity.getErrorMsg());
                if (callBack != this) {
                    return;
                }
                reTry();
            }

            void reTry() {
                handler.removeMessages(GET_SERVER);
                handler.sendEmptyMessageDelayed(GET_SERVER, 2000);
            }
        };
        baseHttpBusiness.sendGet(url, params, callBack);
        handler.sendEmptyMessageDelayed(GET_SERVER, 2000);
    }
}
