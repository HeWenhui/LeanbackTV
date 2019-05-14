package com.xueersi.parentsmeeting.modules.livevideo.util;

import com.tencent.bugly.crashreport.CrashReport;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSessionContext;

public class WebTrustVerifier {
    private static String TAG = "WebTrustVerifier";
    private static boolean trustVerifier = false;

    /**
     * 网页证书认证
     */
    public static void trustVerifier() {
        if (trustVerifier) {
            return;
        }
        trustVerifier = true;
        try {
            //  直接通过主机认证
            HostnameVerifier hv = new HostnameVerifier() {
                public boolean verify(String urlHostName, SSLSession session) {
                    return true;
                }
            };
//  配置认证管理器
            javax.net.ssl.TrustManager[] trustAllCerts = {new TrustAllTrustManager()};
            SSLContext sc = SSLContext.getInstance("SSL");
            SSLSessionContext sslsc = sc.getServerSessionContext();
            sslsc.setSessionTimeout(0);
            sc.init(null, trustAllCerts, null);
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
//  激活主机认证
            HttpsURLConnection.setDefaultHostnameVerifier(hv);
        } catch (Throwable e) {
            e.printStackTrace();
            CrashReport.postCatchedException(new LiveException(TAG, e));
        }
    }

    static class TrustAllTrustManager implements javax.net.ssl.TrustManager, javax.net.ssl.X509TrustManager {

        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public boolean isServerTrusted(java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public boolean isClientTrusted(java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }

        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }

    }
}
