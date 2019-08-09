package com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.page;

import android.os.Handler;
import android.os.Looper;
import android.webkit.JavascriptInterface;

import com.tencent.smtt.sdk.WebView;
import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BaseApplication;
import com.xueersi.common.base.BasePager;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business.EnglishH5CoursewareSecHttp;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.LiveLoggerFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 小学理科互动题提交
 */
public class ScienceStaticWeb {
    private Logger logger = LiveLoggerFactory.getLogger("ScienceStaticWeb");
    private BasePager basePager;
    private EnglishH5CoursewareSecHttp englishH5CoursewareSecHttp;
    private WebView webView;
    private Handler handler = new Handler(Looper.getMainLooper());

    public ScienceStaticWeb(BasePager basePager, WebView webView, EnglishH5CoursewareSecHttp englishH5CoursewareSecHttp) {
        this.basePager = basePager;
        this.webView = webView;
        this.englishH5CoursewareSecHttp = englishH5CoursewareSecHttp;
    }

    @JavascriptInterface
    public void getCourseWareTests(final String str) {
        logger.d("getCourseWareTests:str=" + str);
        final AtomicInteger atomicBoolean = new AtomicInteger(0);
        sendPost(str, new AbstractBusinessDataCallBack() {
            AbstractBusinessDataCallBack callBack = this;

            @Override
            public void onDataSucess(Object... objData) {
                logger.d("getCourseWareTests:onDataSucess");
            }

            @Override
            public void onDataFail(int errStatus, String failMsg) {
                logger.d("getCourseWareTests:onDataFail:atomicBoolean=" + atomicBoolean + ",failMsg=" + failMsg);
                if (atomicBoolean.getAndIncrement() < 3) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sendPost(str, callBack);
                        }
                    }, 1000);
                } else {
                    if (basePager.getRootView() != null && basePager.getRootView().getParent() == null) {
                        return;
                    }
                    XESToastUtils.showToast(BaseApplication.getContext(), "获取试题失败，请刷新");
                }
            }
        });
    }

    @JavascriptInterface
    public void submitCourseWareTests(final String str) {
        logger.d("submitCourseWareTests:str=" + str);
        final AtomicInteger atomicBoolean = new AtomicInteger(0);
        sendPost(str, new AbstractBusinessDataCallBack() {
            AbstractBusinessDataCallBack callBack = this;

            @Override
            public void onDataSucess(Object... objData) {
                logger.d("submitCourseWareTests:onDataSucess");
            }

            @Override
            public void onDataFail(int errStatus, String failMsg) {
                logger.d("submitCourseWareTests:onDataFail:atomicBoolean=" + atomicBoolean + ",failMsg=" + failMsg);
                if (atomicBoolean.getAndIncrement() < 3) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sendPost(str, callBack);
                        }
                    }, 1000);
                } else {
                    if (basePager.getRootView() != null && basePager.getRootView().getParent() == null) {
                        return;
                    }
                    XESToastUtils.showToast(BaseApplication.getContext(), "提交失败，请重试");
                }
            }
        });
    }

    @JavascriptInterface
    public void getStuTestResult(final String str) {
        logger.d("getStuTestResult:str=" + str);
        final AtomicInteger atomicBoolean = new AtomicInteger(0);
        sendPost(str, new AbstractBusinessDataCallBack() {
            AbstractBusinessDataCallBack callBack = this;

            @Override
            public void onDataSucess(Object... objData) {
                logger.d("getStuTestResult:onDataSucess");
            }

            @Override
            public void onDataFail(int errStatus, String failMsg) {
                logger.d("getStuTestResult:onDataFail:atomicBoolean=" + atomicBoolean + ",failMsg=" + failMsg);
                if (atomicBoolean.getAndIncrement() < 3) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sendPost(str, callBack);
                        }
                    }, 1000);
                } else {
                    if (basePager.getRootView() != null && basePager.getRootView().getParent() == null) {
                        return;
                    }
                    XESToastUtils.showToast(BaseApplication.getContext(), "获取试题结果失败，请刷新");
                }
            }
        });
    }

    private void sendPost(String str, final AbstractBusinessDataCallBack callBack) {
        try {
            JSONObject jsonObject = new JSONObject(str);
            String url = jsonObject.getString("url");
            String params = jsonObject.getString("params");
            final String methodName = jsonObject.getString("methodName");
            final String invokeID = jsonObject.getString("invokeID");
            englishH5CoursewareSecHttp.getCourseWareTests(url, params, new AbstractBusinessDataCallBack() {
                @Override
                public void onDataSucess(Object... objData) {
                    String res = (String) objData[0];
                    String jsurl = "javascript:__endInvodeMe('" + methodName + "','" + res + "','" + invokeID + "')";
                    webView.loadUrl(jsurl);
                    callBack.onDataSucess(objData);
                }

                @Override
                public void onDataFail(int errStatus, String failMsg) {
                    callBack.onDataFail(errStatus, failMsg);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
