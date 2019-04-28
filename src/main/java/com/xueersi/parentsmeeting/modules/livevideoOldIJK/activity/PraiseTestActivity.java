package com.xueersi.parentsmeeting.modules.livevideoOldIJK.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.speechfeedback.page.SpeechCollectivePager;

@Route(path = "/livevideo/praise")
public class PraiseTestActivity extends Activity {
    SpeechCollectivePager praiseInteractionPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(lp);

        setContentView(R.layout.activity_livevideo_praise_test);
        RelativeLayout conntLayout = findViewById(R.id.iv_livevideo_praise_test_content);
        praiseInteractionPager = new SpeechCollectivePager(this,null);

        View rootView = praiseInteractionPager.getRootView();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        conntLayout.addView(praiseInteractionPager.getRootView(), params);
    }

}
