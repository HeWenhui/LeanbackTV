package com.xueersi.parentsmeeting.modules.livevideo.nbh5courseware.pager;

import android.content.Context;
import android.view.View;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.NbCourseWareEntity;
import com.xueersi.parentsmeeting.modules.livevideo.nbh5courseware.business.NbH5PagerAction;
import com.xueersi.parentsmeeting.modules.livevideo.page.BaseWebviewX5Pager;
import com.xueersi.parentsmeeting.modules.livevideo.core.LivePagerBack;

/**
 * Created by linyuqiang on 2017/3/25.
 * h5 课件nb实验
 */
public class NbH5CoursewareX5Pager extends BaseWebviewX5Pager implements NbH5PagerAction, BaseNbH5CoursewarePager {
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
    public void destroy() {
        this.onDestroy();
    }

    @Override
    public boolean onBack() {
        return false;
    }

}
