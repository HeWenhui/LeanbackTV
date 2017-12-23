package com.xueersi.parentsmeeting.modules.livevideo.page;

import android.content.Context;
import android.view.View;

import com.xueersi.parentsmeeting.modules.livevideo.R;

/**
 * Created by linyuqiang on 2017/3/25.
 * h5 课件
 */
public class H5CoursewarePager extends BaseWebviewPager {
    String url;

    public H5CoursewarePager(Context context, String url) {
        super(context);
        this.url = url;
        initWebView();
        setErrorTip("H5课件加载失败，请重试");
        setLoadTip("H5课件正在加载，请稍候");
        initData();
    }

    public String getUrl() {
        return url;
    }

    @Override
    public View initView() {
        final View view = View.inflate(mContext, R.layout.page_livevideo_h5_courseware, null);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        loadUrl(url);
    }

}
