package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.xueersi.common.entity.EnglishH5Entity;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.groupgame.pager.GroupGameMVPPager;
import com.xueersi.parentsmeeting.modules.livevideo.groupgame.pager.GroupGameNativePager;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;

import java.util.HashMap;

public class GroupGameTestBll extends LiveBaseBll {
    GroupGameNativePager groupGameNativePager;
    GroupGameMVPPager groupGameMVPPager;

    public GroupGameTestBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
    }

    @Override
    public void onCreate(HashMap<String, Object> data) {
        super.onCreate(data);
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        LinearLayout llTest = new LinearLayout(activity);
        addView(llTest);
        Button btnTest1 = new Button(activity);
        btnTest1.setText("小组互动");
        Button btnTest2 = new Button(activity);
        btnTest2.setText("MVP单人模式");
        Button btnTest3 = new Button(activity);
        btnTest3.setText("");
        Button btnTest4 = new Button(activity);
        btnTest4.setText("");
        llTest.addView(btnTest1);
        llTest.addView(btnTest2);
        llTest.addView(btnTest3);
        llTest.addView(btnTest4);


        btnTest1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                groupGameNativePager = new GroupGameNativePager(mContext, mGetInfo,new VideoQuestionLiveEntity(),new EnglishH5Entity());
//                mRootView.addView(groupGameNativePager.getRootView(), new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        });
        btnTest2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                groupGameMVPPager = new GroupGameMVPPager(mContext);
//                mRootView.addView(groupGameMVPPager.getRootView(), new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//                groupGameMVPPager.setOnPagerClose(new LiveBasePager.OnPagerClose() {
//                    @Override
//                    public void onClose(LiveBasePager basePager) {
//                        mRootView.removeView(groupGameMVPPager.getRootView());
//                    }
//                });
            }
        });
        btnTest3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        btnTest4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void onDestory() {
        super.onDestory();
        if (groupGameNativePager != null) {
            groupGameNativePager.onDestroy();
        }
    }
}
