package ren.yale.android.cachewebviewlib;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.text.TextUtils;
import android.widget.RelativeLayout;

import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.xueersi.lib.framework.utils.NetWorkHelper;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by yale on 2017/10/27.
 */

public class CachePreLoadService extends Service {
    protected Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
    public static final String KEY_URL = "preload_url_key";
    public static final String KEY_URL_HEADER = "preload_url_key_header";
    public static final String URL_CACHE_ACTION = "url_cache_action";
    private boolean mLastFinish = true;
    ArrayList<CacheWebView> cacheWebViews = new ArrayList<>();
    private String TAG = "CachePreLoadService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent == null) {
            return super.onStartCommand(intent, flags, startId);
        }
        final String url = intent.getStringExtra(KEY_URL);
        int netWorkType = NetWorkHelper.getNetWorkState(this);
        if (netWorkType == NetWorkHelper.NO_NETWORK) {
            int status = -1;
            Intent intent1 = new Intent(URL_CACHE_ACTION);
            intent1.putExtra("url", url);
            intent1.putExtra("status", status);
            sendBroadcast(intent1);
            return super.onStartCommand(intent, flags, startId);
        }
        if (netWorkType == NetWorkHelper.MOBILE_STATE) {
            int status = -2;
            Intent intent1 = new Intent(URL_CACHE_ACTION);
            intent1.putExtra("url", url);
            intent1.putExtra("status", status);
            sendBroadcast(intent1);
            return super.onStartCommand(intent, flags, startId);
        }

//        if (!NetworkUtils.isConnected(this.getApplicationContext())) {
//            return super.onStartCommand(intent, flags, startId);
//        }
//        if (!TextUtils.isEmpty(url) && mLastFinish) {
        if (!TextUtils.isEmpty(url)) {
            mLastFinish = false;
            RelativeLayout relativeLayout = new RelativeLayout(this.getApplicationContext());
            final CacheWebView cacheWebView = new CacheWebView(this.getApplicationContext());
            relativeLayout.addView(cacheWebView, new RelativeLayout.LayoutParams(ScreenUtils.getScreenWidth(), ScreenUtils.getScreenHeight()));
            cacheWebView.setCacheStrategy(WebViewCache.CacheStrategy.FORCE);
            Map header = null;
            try {
                header = (Map) intent.getSerializableExtra(KEY_URL_HEADER);
            } catch (Exception e) {
            }
            cacheWebView.loadUrl(url, header);
            cacheWebViews.add(cacheWebView);
            cacheWebView.setWebViewClient(new WebViewClient() {
                long before = System.currentTimeMillis();
                String failingUrl;

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    mLastFinish = true;
                    int status = failingUrl == null ? 0 : -3;
                    Intent intent1 = new Intent(URL_CACHE_ACTION);
                    intent1.putExtra("url", url);
                    intent1.putExtra("status", status);
                    intent1.putExtra("time", (System.currentTimeMillis() - before));
                    sendBroadcast(intent1);
                    cacheWebViews.remove(cacheWebView);
                    view.destroy();
                }

                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    mLastFinish = true;
                    this.failingUrl = failingUrl;
                }
            });
        }


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        logger.d( "onDestroy:cacheWebViews=" + cacheWebViews.size());
        for (int i = 0; i < cacheWebViews.size(); i++) {
            WebView view = cacheWebViews.get(i);
            view.destroy();
        }
    }
}
