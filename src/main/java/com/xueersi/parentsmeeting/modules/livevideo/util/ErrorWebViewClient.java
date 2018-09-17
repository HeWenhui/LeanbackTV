package com.xueersi.parentsmeeting.modules.livevideo.util;

import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.xueersi.common.network.IpAddressUtil;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;

/**
 * Created by linyuqiang on 2018/6/11.
 * 网页错误统计
 */
public class ErrorWebViewClient extends WebViewClient {
    private String TAG;
    LiveThreadPoolExecutor liveThreadPoolExecutor = LiveThreadPoolExecutor.getInstance();
    public HashMap<String, String> urlAndIp = new HashMap<>();

    public ErrorWebViewClient(String TAG) {
        this.TAG = TAG;
    }

    @Override
    public void onReceivedError(final WebView webView, final WebResourceRequest webResourceRequest, final WebResourceError webResourceError) {
        if (!webResourceRequest.isForMainFrame()) {
            liveThreadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
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
                            Loger.d(TAG, "onReceivedError:host=" + url2 + ",ip=" + remoteip);
                        }
                    } catch (UnknownHostException e) {
                        remoteip = "unknown";
                        e.printStackTrace();
                    } catch (Exception e) {
                        remoteip = "" + e;
                        e.printStackTrace();
                    }
                    StableLogHashMap logHashMap = new StableLogHashMap();
                    logHashMap.put("tag", TAG);
                    logHashMap.put("url", url);
                    logHashMap.put("errorcode", "" + webResourceError.getErrorCode());
                    logHashMap.put("description", "" + webResourceError.getDescription());
                    logHashMap.put("remoteip", "" + remoteip);
                    logHashMap.put("userip", "" + IpAddressUtil.USER_IP);
                    logHashMap.put("operator", "" + IpAddressUtil.USER_OPERATE);
//                    Loger.d(webView.getContext(), LiveVideoConfig.LIVE_WEBVIEW_ERROR, logHashMap.getData(), true);
                    UmsAgentManager.umsAgentDebug(webView.getContext(), LiveVideoConfig.LIVE_WEBVIEW_ERROR, logHashMap.getData());
                }
            });
        }
        super.onReceivedError(webView, webResourceRequest, webResourceError);
    }
}
