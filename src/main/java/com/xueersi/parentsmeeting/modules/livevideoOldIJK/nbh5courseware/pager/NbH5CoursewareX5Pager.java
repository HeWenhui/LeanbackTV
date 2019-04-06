package com.xueersi.parentsmeeting.modules.livevideoOldIJK.nbh5courseware.pager;

import android.content.Context;
import android.view.View;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.page.BaseWebviewX5Pager;

/**
 * Created by linyuqiang on 2017/3/25.
 * h5 课件nb实验
 */
public class NbH5CoursewareX5Pager extends BaseWebviewX5Pager implements BaseNbH5CoursewarePager {
    String url;

    public NbH5CoursewareX5Pager(Context context, String url) {
        super(context);
        this.url = url;
        initWebView();
        setErrorTip("H5课件加载失败，请重试");
        setLoadTip("H5课件正在加载，请稍候");
        initData();
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public View initView() {
        final View view = View.inflate(mContext, R.layout.page_livevideo_h5_courseware_x5, null);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        loadUrl(url);
    }

}
