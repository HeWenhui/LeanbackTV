package com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience.examination;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.tencent.smtt.sdk.WebView;
import com.xueersi.common.route.XueErSiRouter;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience.IPresenter;
import com.xueersi.parentsmeeting.modules.livevideo.page.BaseWebviewX5Pager;

import java.util.ArrayList;
import java.util.List;

import ren.yale.android.cachewebviewlib.CacheWebView;

public class StandExperienceEvaluationPager<T extends IPresenter> extends BaseWebviewX5Pager implements
        IEvaluationContract.IEvaluationView {
    private T iPresenter;

    public StandExperienceEvaluationPager(Context context, T iPresenter) {
        super(context);
        this.iPresenter = iPresenter;

    }

    @Override
    public View initView() {
//        addJavascriptInterface();
//        wvSubjectWeb.addJavascriptInterface(new StandExperienceEvaluation(), "standExperienceEvaluation");
        View view = View.inflate(mContext, R.layout.page_livevideo_h5_courseware_x5, null);
        mView = wvSubjectWeb = new CacheWebView(mContext);
        wvSubjectWeb.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return mView;
    }

    @Override
    public void onPause() {
        super.onPause();
        wvSubjectWeb.onPause();
    }

    @Override
    protected boolean shouldOverrideUrlLoading(WebView view, String url) {
        logger.i("shouldOverrideUrlLoading:url=" + url);
        //关闭页面  https://www.baidu.com
        if (url.contains("baidu.com")) {
//            onClose.onH5ResultClose(this, getBaseVideoQuestionEntity());
//            iPresenter.removeWindow();
            iPresenter.removeWindow();
            wvSubjectWeb.destroy();
            iPresenter.showNextWindow();

        }
        //进入推荐的课程   https://www.sina.com?courseId=**&classId=**
        if (url.contains(" www.sina.com")) {
            String courseId = findNumber(url, "courseId");
            String classId = findNumber(url, "classId");
            String orderId = findNumber(url, "orderId");
//            ARouter.getInstance().build("/xesmall/orderDetail").withString("orderNum", orderId).navigation();
            //跳转到商城的订单详情页面
            Bundle bundle = new Bundle();
            bundle.putString("orderNum", orderId);
            //采用ARouter来跳转
            XueErSiRouter.startModule(mContext, "/module/Browser", bundle);
//            OtherModulesEnter.intentToOrderConfirmActivity((Activity) mContext, courseId + "-" + classId, 100,
//                    "LivePlaybackVideoActivity");
        }
        return false;
    }


    /**
     * 找出指定String后面的id
     *
     * @param url
     * @param index
     * @return
     */

    private String findNumber(String url, String index) {
        StringBuilder ans = new StringBuilder();
        int pos = url.indexOf(index);
        System.out.println(pos);
        boolean preIsStr = true;
        for (int i = pos; i < url.length(); i++) {
            char ch = url.charAt(i);
            if (ch >= '0' && ch <= '9') {
                ans.append(ch);
                preIsStr = false;
            } else {
                if (!preIsStr) {
                    preIsStr = true;
                    break;
                }
            }
        }
        return ans.toString();
    }

    /**
     * 找出这一串字符串中第几个数字
     *
     * @param url
     * @return
     */
    private List<String> getNumber(String url) {
        boolean preIsStr = false;
        List<String> sts = new ArrayList<>();
        StringBuilder ans = new StringBuilder();
        for (int i = 0; i < url.length(); i++) {
            char ch = url.charAt(i);
            if (ch >= '0' && ch <= '9') {
                ans.append(ch);
                preIsStr = false;
            } else {
                if (!preIsStr) {
                    preIsStr = true;
                    sts.add(ans.toString());
                    ans = new StringBuilder();
                }
            }
        }
        if (!preIsStr) {
            sts.add(ans.toString());
        }
        return sts;
    }

    /**
     * "http://exp-class.xesv5.com/wholeBodyLive/expClassApp/index" +
     * ".html#/index?nowLevel=12&liveId=210285&gradeId=1&subjectId=2&teacherId=2671&orderId=2523&userId" +
     * "=11"
     *
     * @param url
     */
    @Override
    public void showWebView(String url) {
        if (!TextUtils.isEmpty(url)) {
            logger.i("加载url:" + url);
            wvSubjectWeb.loadUrl(url);
        } else {
//            logger.i("加载url:" + "http://exp-class.xesv5.com/wholeBodyLive/expClassApp/index" +
//                    ".html#/index?nowLevel=12&liveId=210285&gradeId=1&subjectId=2&teacherId=2671&orderId=2523&userId" +
//                    "=11");
//            wvSubjectWeb.loadUrl("http://exp-class.xesv5.com/wholeBodyLive/expClassApp/index" +
//                    ".html#/index?nowLevel=12&liveId=210285&gradeId=1&subjectId=2&teacherId=2671&orderId=2523&userId" +
//                    "=11");
        }
    }


    @Override
    public void onResume() {
        wvSubjectWeb.onResume();
    }

}
