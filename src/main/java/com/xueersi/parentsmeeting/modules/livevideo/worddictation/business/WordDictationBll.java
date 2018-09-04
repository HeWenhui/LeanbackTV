package com.xueersi.parentsmeeting.modules.livevideo.worddictation.business;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.common.route.XueErSiRouter;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.endictation.entity.DictationConfig;
import com.xueersi.parentsmeeting.modules.endictation.entity.RecognizeFlow;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.worddictation.entity.WordStatisticInfo;

/**
 * Created by linyuqiang on 2018/9/4.
 */
public class WordDictationBll implements WordDictationAction {
    Logger logger = LoggerFactory.getLogger("WordDictationBll");
    Activity activity;
    LiveGetInfo liveGetInfo;
    RelativeLayout bottomContent;
    WordReceiver wordReceiver;

    public WordDictationBll(Activity activity) {
        this.activity = activity;
        wordReceiver = new WordReceiver();
        IntentFilter filter = new IntentFilter(DictationConfig.ACTION_DICTATION_COMPLETE);
        activity.registerReceiver(wordReceiver, filter);
    }

    public void initView(RelativeLayout bottomContent) {
        this.bottomContent = bottomContent;
    }

    public void setGetInfo(LiveGetInfo liveGetInfo) {
        this.liveGetInfo = liveGetInfo;
    }

    @Override
    public void onStart(WordStatisticInfo wordStatisticInfo) {
        Bundle bundle = new Bundle();
        RecognizeFlow recognizeFlow = new RecognizeFlow(wordStatisticInfo.testid, liveGetInfo.getId(), wordStatisticInfo.pagetype, liveGetInfo.getTeacherId(), wordStatisticInfo.answers);
        bundle.putParcelable("data", recognizeFlow);
        XueErSiRouter.startModule(activity, "/dictation/Launch", bundle);
    }

    @Override
    public void onStop() {
        activity.sendBroadcast(new Intent(DictationConfig.ACTION_DICTATION_GOTOLIVE));
    }

    @Override
    public void onDestory() {
        if (wordReceiver != null) {
            activity.unregisterReceiver(wordReceiver);
        }
    }

    class WordReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            final RecognizeFlow recognizeFlow = intent.getParcelableExtra("data");
            logger.d("onReceive:recognizeFlow=" + recognizeFlow);
            TextView textView = new TextView(activity);
            textView.setText("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
            textView.setTextSize(16);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
            lp.topMargin = 100;
            bottomContent.addView(textView, lp);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("data", recognizeFlow);
                    XueErSiRouter.startModule(activity, "/dictation/Result", bundle);
                }
            });
        }
    }
}
