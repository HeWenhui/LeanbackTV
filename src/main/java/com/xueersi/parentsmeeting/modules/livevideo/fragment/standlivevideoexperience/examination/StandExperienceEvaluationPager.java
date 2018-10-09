package com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience.examination;

import android.content.Context;
import android.view.View;
import android.webkit.JavascriptInterface;

import com.xueersi.parentsmeeting.modules.livevideo.event.StandExperienceRecommondCourseEvent;
import com.xueersi.parentsmeeting.modules.livevideo.page.BaseWebviewX5Pager;

import org.greenrobot.eventbus.EventBus;

public class StandExperienceEvaluationPager extends BaseWebviewX5Pager {

    public StandExperienceEvaluationPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        addJavascriptInterface();
//        wvSubjectWeb.addJavascriptInterface(new StandExperienceEvaluation(), "standExperienceEvaluation");

        return super.initView();
    }
//
//    private class StandExperienceEvaluation {
//        @JavascriptInterface
//        public void evaluate() {
//            EventBus.getDefault().post(new StandExperienceRecommondCourseEvent());
//        }
//    }

}
