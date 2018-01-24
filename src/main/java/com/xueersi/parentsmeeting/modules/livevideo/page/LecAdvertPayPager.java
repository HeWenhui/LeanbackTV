package com.xueersi.parentsmeeting.modules.livevideo.page;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.xesalib.utils.log.Loger;

/**
 * Created by linyuqiang on 2018/1/15.
 * 讲座广告支付
 */
public class LecAdvertPayPager extends BaseWebviewPager {
    String reloadurl;
    RelativeLayout rl_livevideo_subject_web;
    TextView tv_livelec_advert_step2_title;
    String url;
    OnPaySuccess onPaySuccess;
    boolean paySuccess = false;

    public LecAdvertPayPager(Context context, String url, TextView tv_livelec_advert_step2_title, OnPaySuccess onPaySuccess) {
        super(context);
        this.url = url;
        this.tv_livelec_advert_step2_title = tv_livelec_advert_step2_title;
        this.onPaySuccess = onPaySuccess;
        initWebView();
        setErrorTip("支付加载失败，请重试");
        setLoadTip("支付正在加载，请稍候");
        initData();
    }

    @Override
    public void onPause() {
        wvSubjectWeb.onPause();
    }

    public void destroy() {
        wvSubjectWeb.destroy();
    }

    @Override
    public void onResume() {
        wvSubjectWeb.onResume();
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livelec_adver_payment, null);
        rl_livevideo_subject_web = (RelativeLayout) view.findViewById(R.id.rl_livevideo_subject_web);
        return view;
    }

    @Override
    protected void onPageFinished(WebView view, String url) {

    }

    @Override
    protected void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
    }

    public void onBack() {

    }

    public void close() {
        onBack();
    }

    @Override
    protected boolean shouldOverrideUrlLoading(WebView view, String url) {
        //      if ("http://baidu.com/".equals(url)) {
        Loger.d(TAG, "shouldOverrideUrlLoading:url=" + url);
        reloadurl = url;
//        if (url.contains("baidu.com")) {
//            return true;
//        }
//        if (url.contains("https://submit.com")) {
//
//        }
        if (reloadurl.contains("xueersi.com/ShoppingCart/complete")) {
            if (onPaySuccess != null) {
                paySuccess = true;
                onPaySuccess.onPaySuccess();
                return true;
            }
        }

        return super.shouldOverrideUrlLoading(view, url);
    }

    @Override
    public void initData() {
        super.initData();

        WebSettings webSetting = wvSubjectWeb.getSettings();
        webSetting.setBuiltInZoomControls(true);

        Loger.i(TAG, "initData:url=" + url);
        loadUrl(url);
        reloadurl = url;
        mView.findViewById(R.id.btn_error_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorView.setVisibility(View.GONE);
//                wvSubjectWeb.setVisibility(View.VISIBLE);

                newWebView();

                View loadView = mView.findViewById(R.id.rl_livevideo_subject_loading);
                loadView.setVisibility(View.VISIBLE);
                ImageView ivLoading = (ImageView) mView.findViewById(R.id.iv_data_loading_show);
                ((AnimationDrawable) ivLoading.getBackground()).start();
                loadUrl(reloadurl);
            }
        });
    }

    private void newWebView() {
        rl_livevideo_subject_web.removeView(wvSubjectWeb);
        wvSubjectWeb = (WebView) View.inflate(mContext, R.layout.page_livevideo_h5_courseware_cacheweb, null);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        rl_livevideo_subject_web.addView(wvSubjectWeb, 0, lp);

        addJavascriptInterface();
        WebSettings webSetting = wvSubjectWeb.getSettings();
        webSetting.setBuiltInZoomControls(true);
        wvSubjectWeb.setWebChromeClient(new MyWebChromeClient());
        wvSubjectWeb.setWebViewClient(new MyWebViewClient());
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
        Loger.d(TAG, "onReceivedTitle:paySuccess=" + paySuccess + ",title=" + title);
        if (paySuccess) {
            return;
        }
//        tv_livelec_advert_step2_title.setText(title);
    }

    public interface OnPaySuccess {
        void onPaySuccess();
    }

}
