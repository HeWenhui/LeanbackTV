package com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience.examination;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.tencent.smtt.sdk.WebView;
import com.xueersi.common.route.XueErSiRouter;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience.IPresenter;
import com.xueersi.parentsmeeting.modules.livevideo.page.BaseWebviewX5Pager;

import java.util.ArrayList;
import java.util.List;

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

        return super.initView();
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
        // FIXME: 2018/10/16 跳转到 商城的学习中心页
        //进入推荐的课程   https://www.sina.com?courseId=**&classId=**
        if (url.contains(" www.sina.com")) {
            String courseId = findNumber(url, "courseId");
            String classId = findNumber(url, "classId");
            String orderId = findNumber(url, "orderId");
//            ARouter.getInstance().build("/xesmall/orderDetail").withString("orderNum", orderId).navigation();
            //跳转到商城的订单详情页面
            Bundle bundle = new Bundle();
            bundle.putString("orderNum", orderId);
            //跳转到
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

    @Override
    public void showWebView(String url) {
//        if (!TextUtils.isEmpty(url)) {
//            wvSubjectWeb.loadUrl(url);
//        } else {
        wvSubjectWeb.loadUrl("http://exp-class.xesv5.com/wholeBodyLive/expClassApp/index" +
                ".html#/index?nowLevel=12&liveId=210285&gradeId=1&subjectId=2&teacherId=2671&orderId=2523&userId" +
                "=11");
//        }
    }


    @Override
    public void onResume() {
        wvSubjectWeb.onResume();
    }

}
