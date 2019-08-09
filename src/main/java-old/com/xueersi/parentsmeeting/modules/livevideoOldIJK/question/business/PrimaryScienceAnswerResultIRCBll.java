package com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business;

import android.app.Activity;

import com.xueersi.parentsmeeting.modules.livevideo.question.http.CourseWareHttpManager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;

import java.util.HashMap;

public class PrimaryScienceAnswerResultIRCBll extends LiveBaseBll {
    private CourseWareHttpManager courseWareHttpManager;

    public PrimaryScienceAnswerResultIRCBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
    }

    @Override
    public void onCreate(HashMap<String, Object> data) {
        super.onCreate(data);
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
//        LinearLayout llTest = new LinearLayout(activity);
//        mRootView.addView(llTest);
//        Button btnTest1 = new Button(activity);
//        btnTest1.setText("小学理科互动题结果页");
//        Button btnTest2 = new Button(activity);
//        btnTest2.setText("");
//        Button btnTest3 = new Button(activity);
//        btnTest3.setText("");
//        Button btnTest4 = new Button(activity);
//        btnTest4.setText("");
//        llTest.addView(btnTest1);
//        llTest.addView(btnTest2);
//        llTest.addView(btnTest3);
//        llTest.addView(btnTest4);
//
//
//        btnTest1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getCourseWareHttpManager().getStuTestResult("", "", "", "", "", "", "", 0, new AbstractBusinessDataCallBack() {
//                    @Override
//                    public void onDataSucess(Object... objData) {
//                        PrimaryScienceAnswerResultEntity entity = (PrimaryScienceAnswerResultEntity) objData[0];
//                        PrimaryScienceAnserResultPager primaryScienceAnserResultPager = new PrimaryScienceAnserResultPager(mContext, entity);
//                        mRootView.addView(primaryScienceAnserResultPager.getRootView(), new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//                    }
//                });
//            }
//        });
//        btnTest2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            }
//        });
//        btnTest3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            }
//        });
//        btnTest4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
    }

    public CourseWareHttpManager getCourseWareHttpManager() {
        if (courseWareHttpManager == null) {
            courseWareHttpManager = new CourseWareHttpManager(getHttpManager());
        }
        return courseWareHttpManager;
    }
}
