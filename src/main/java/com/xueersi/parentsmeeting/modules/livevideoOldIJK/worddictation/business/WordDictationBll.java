package com.xueersi.parentsmeeting.modules.livevideoOldIJK.worddictation.business;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.xueersi.common.route.XueErSiRouter;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.endictation.entity.DictationConfig;
import com.xueersi.parentsmeeting.modules.endictation.entity.DictationQuery;
import com.xueersi.parentsmeeting.modules.endictation.entity.RecognizeFlow;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.worddictation.entity.WordStatisticInfo;

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
    }

    public void sendSwtichStream() {
        Intent intent = new Intent(DictationConfig.ACTION_SWITCH_ANCHORLIVE);
        activity.sendBroadcast(intent);
    }

    public void initView(RelativeLayout bottomContent) {
        this.bottomContent = bottomContent;
    }

    public void setGetInfo(LiveGetInfo liveGetInfo) {
        this.liveGetInfo = liveGetInfo;
    }

    @Override
    public void onStart(WordStatisticInfo wordStatisticInfo) {
        logger.d("onStart");
        Bundle bundle = new Bundle();


        if (DictationQuery.hasSavedRecord(activity,wordStatisticInfo.testid, liveGetInfo.getId())){
            // 已经有作答记录,直接查看结果
            RecognizeFlow savedData = DictationQuery.getLastRecord(activity);
            bundle.putParcelable("data", savedData);
            XueErSiRouter.startModule(activity, "/dictation/Result", bundle);
        }else {
            // 没有作答记录，直接进入引导页
            RecognizeFlow recognizeFlow = new RecognizeFlow(wordStatisticInfo.testid, liveGetInfo.getId(), wordStatisticInfo.pagetype, liveGetInfo.getTeacherId(), wordStatisticInfo.answers);
            bundle.putParcelable("data", recognizeFlow);
            XueErSiRouter.startModule(activity, "/dictation/Launch", bundle);
        }

        if (wordReceiver == null) {
            wordReceiver = new WordReceiver();
            IntentFilter filter = new IntentFilter(DictationConfig.ACTION_DICTATION_COMPLETE);
            activity.registerReceiver(wordReceiver, filter);
        }
    }

    @Override
    public void onStop() {
        logger.d("onStop");
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
            final View view = LayoutInflater.from(activity).inflate(R.layout.layout_word_dictation_complete, null);
            LiveVideoPoint liveVideoPoint = LiveVideoPoint.getInstance();
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
            logger.d("onReceive:top=" + view.getTop() + ",rightMargin=" + liveVideoPoint.getRightMargin());
            view.setPadding(view.getLeft(), (int) (50 * ScreenUtils.getScreenDensity()), liveVideoPoint.getRightMargin(), view.getBottom());
            bottomContent.addView(view, lp);
            view.findViewById(R.id.bt_livevideo_worddictation_result).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("data", recognizeFlow);
                    XueErSiRouter.startModule(activity, "/dictation/Result", bundle);
                }
            });
            view.findViewById(R.id.bt_livevideo_worddictation_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomContent.removeView(view);
                }
            });
            if (wordReceiver != null) {
                activity.unregisterReceiver(wordReceiver);
                wordReceiver = null;
            }
        }
    }
}
