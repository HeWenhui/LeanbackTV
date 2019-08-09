package com.xueersi.parentsmeeting.modules.livevideoOldIJK.nbh5courseware.pager;

import android.content.Context;
import android.view.View;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.NbCourseWareEntity;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.nbh5courseware.pager.BaseNbH5CoursewarePager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.page.BaseWebviewX5Pager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.LivePagerBack;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.nbh5courseware.business.NbH5PagerAction;

/**
*线上普通 NB 实验展示页面
*@author chekun
*created  at 2019/4/15 17:13
*/
public class NbH5CoursewareX5Pager extends BaseWebviewX5Pager implements NbH5PagerAction,BaseNbH5CoursewarePager{
    String url;

    public NbH5CoursewareX5Pager(Context context, NbCourseWareEntity entity, LivePagerBack pagerBack) {
        super(context);
        this.url = entity.getUrl();
        initWebView();
        setErrorTip("H5课件加载失败，请重试");
        setLoadTip("H5课件正在加载，请稍候");
        setLivePagerBack(pagerBack);
        initData();
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getTestId() {
        return null;
    }

    @Override
    public void submitData() {

    }

    @Override
    public boolean onBack() {
        return false;
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

    @Override
    public void destroy() {
        this.onDestroy();
    }
}