package com.xueersi.parentsmeeting.modules.livevideo.util;

import android.os.Looper;

import com.alibaba.fastjson.JSON;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.xueersi.common.network.IpAddressUtil;
import com.xueersi.common.network.TxHttpDns;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;

/**
 * Created by linyuqiang on 2018/6/11.
 * 网页错误统计
 */
public class ErrorWebViewClient extends WebViewClient {
    private String TAG;
    protected Logger logger;
    LiveThreadPoolExecutor liveThreadPoolExecutor = LiveThreadPoolExecutor.getInstance();
    public HashMap<String, String> urlAndIp = new HashMap<>();
    protected String loadUrl = null;

    public ErrorWebViewClient(String TAG) {
        this.TAG = TAG;
        logger = LoggerFactory.getLogger(TAG);
    }

    @Override
    public void onLoadResource(WebView webView, String s) {
        //第一次加载的就是课件地址
        if (loadUrl == null) {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                try {
                    loadUrl = webView.getUrl();
                } catch (Exception e) {
                    LiveCrashReport.postCatchedException(TAG, e);
                }
            }
            if (loadUrl == null) {
                loadUrl = s;
            }
        }
        super.onLoadResource(webView, s);
    }


    @Override
    public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {

        if (sslErrorHandler != null) {
            sslErrorHandler.proceed();
        }
        onReceivedHttpError(webView, "xes_err_web_onReceivedSslError", 1, sslError.getCertificate().toString());
    }

    public void onError(WebView webView, String url, int statusCode, String reasonPhrase) {
        HashMap<String, String> logHashMap = new HashMap<>();
        logHashMap.put("tag", TAG);
        logHashMap.put("status", "false");
        logHashMap.put("loadurl", "" + url);
        logHashMap.put("weburl", "" + loadUrl);
        logHashMap.put("errcode", "" + statusCode);
        logHashMap.put("errmsg", "" + reasonPhrase);
        logHashMap.put("userip", "" + IpAddressUtil.USER_IP);
        logHashMap.put("operator", "" + IpAddressUtil.USER_OPERATE);
        UmsAgentManager.umsAgentDebug(webView.getContext(), "webError:", JSON.toJSONString(logHashMap));
    }


    @Override
    public void onReceivedError(final WebView webView, final WebResourceRequest webResourceRequest, final WebResourceError webResourceError) {
        liveThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = webResourceRequest.getUrl().toString();
                    int index = url.indexOf("?");
                    if (index != -1) {
                        url = url.substring(0, index);
                    }
                    String remoteip = "";
                    try {
                        synchronized (urlAndIp) {
                            remoteip = urlAndIp.get(url);
                        }
                        if (StringUtils.isEmpty(remoteip)) {
                            URL url2 = new URL(url);
                            InetAddress inetAddress = InetAddress.getByName(url2.getHost());
                            remoteip = inetAddress.getHostAddress();
                            synchronized (urlAndIp) {
                                urlAndIp.put(url, remoteip);
                            }
                            logger.d("onReceivedError:host=" + url2 + ",ip=" + remoteip);
                        }
                    } catch (UnknownHostException e) {
                        remoteip = "unknown";
                        e.printStackTrace();
                    } catch (Exception e) {
                        remoteip = "" + e;
                        e.printStackTrace();
                    }
                    StableLogHashMap logHashMap = new StableLogHashMap("platLoadError");
                    logHashMap.put("tag", TAG);
                    logHashMap.put("status", "false");
                    logHashMap.put("isformain", "" + webResourceRequest.isForMainFrame());
                    logHashMap.put("loadurl", url);
                    logHashMap.put("weburl", "" + loadUrl);
                    logHashMap.put("errcode", "" + webResourceError.getErrorCode());
                    logHashMap.put("errmsg", "" + webResourceError.getDescription());
                    logHashMap.put("remoteip", "" + remoteip);
                    logHashMap.put("userip", "" + IpAddressUtil.USER_IP);
                    logHashMap.put("operator", "" + IpAddressUtil.USER_OPERATE);
                    logHashMap.put("txdns", TxHttpDns.getInstance().getTxEnterpriseDns(url));
                    otherMsg(logHashMap, loadUrl);
                    String enentId = logHashMap.getData().get("eventid");
                    if (url.endsWith("index_files/data2.js") || url.endsWith("assets/images/startBg.jpg")) {
                        enentId = LiveVideoConfig.LIVE_WEBVIEW_ERROR;
                    }
                    if (enentId != null) {
                        LiveAndBackDebug liveAndBackDebug = ProxUtil.getProxUtil().get(webView.getContext(), LiveAndBackDebug.class);
                        if (liveAndBackDebug != null) {
                            liveAndBackDebug.umsAgentDebugInter(enentId, logHashMap.getData());
                        } else {
                            UmsAgentManager.umsAgentDebug(webView.getContext(), enentId, logHashMap.getData());
                        }
                    } else {
                        UmsAgentManager.umsAgentDebug(webView.getContext(), LiveVideoConfig.LIVE_WEBVIEW_ERROR, logHashMap.getData());
                    }
                } catch (Exception e) {
                    LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                }
            }
        });
        super.onReceivedError(webView, webResourceRequest, webResourceError);
    }

    @Override
    public void onReceivedHttpError(WebView webView, WebResourceRequest webResourceRequest, WebResourceResponse webResourceResponse) {
        logger.d("onReceivedHttpError:url=" + webResourceRequest.getUrl() + ",code=" + webResourceResponse.getStatusCode());
        onReceivedHttpError(webView, "" + webResourceRequest.getUrl(), webResourceResponse.getStatusCode(), webResourceResponse.getReasonPhrase());
        super.onReceivedHttpError(webView, webResourceRequest, webResourceResponse);
    }

    public void onReceivedHttpError(WebView webView, String url, int statusCode, String reasonPhrase) {
        StableLogHashMap logHashMap = new StableLogHashMap("platLoadError");
        logHashMap.put("tag", TAG);
        logHashMap.put("status", "false");
        logHashMap.put("loadurl", "" + url);
        logHashMap.put("weburl", "" + loadUrl);
        logHashMap.put("errcode", "" + statusCode);
        logHashMap.put("errmsg", "" + reasonPhrase);
        logHashMap.put("userip", "" + IpAddressUtil.USER_IP);
        logHashMap.put("operator", "" + IpAddressUtil.USER_OPERATE);
        otherMsg(logHashMap, loadUrl);
        String enentId = logHashMap.getData().get("eventid");
        if (enentId != null) {
            LiveAndBackDebug liveAndBackDebug = ProxUtil.getProxUtil().get(webView.getContext(), LiveAndBackDebug.class);
            if (liveAndBackDebug != null) {
                liveAndBackDebug.umsAgentDebugInter(enentId, logHashMap.getData());
            } else {
                UmsAgentManager.umsAgentDebug(webView.getContext(), enentId, logHashMap.getData());
            }
        } else {
            UmsAgentManager.umsAgentDebug(webView.getContext(), LiveVideoConfig.LIVE_WEBVIEW_ERROR, logHashMap.getData());
        }
    }

    protected void otherMsg(StableLogHashMap logHashMap, String loadUrl) {

    }
}
