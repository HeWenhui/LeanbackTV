package com.xueersi.parentsmeeting.modules.livevideo.worddictation.business;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.xueersi.common.business.AppBll;
import com.xueersi.common.route.XueErSiRouter;
import com.xueersi.common.route.module.ModuleHandler;
import com.xueersi.common.route.module.entity.Module;
import com.xueersi.common.route.module.entity.ModuleData;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.worddictation.entity.WordStatisticInfo;
import com.xueersi.parentsmeeting.share.business.dctx.DictationConfig;
import com.xueersi.parentsmeeting.share.business.dctx.DictationQuery;
import com.xueersi.parentsmeeting.share.business.dctx.RecognizeFlow;

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
            bundle.put("what","Result");
//            XueErSiRouter.startModule(activity, "/dictation/Result", bundle);
            Module m = AppBll.getInstance().getModuleByModuleName("endictation");
            if (m==null) {
                m = new Module();
                m.moduleName = "endictation";
                m.version = "1.0.0";
                m.fileUrl = "https://test.com/endiction.apk";
                m.mac = "4156465424185";
                m.moduleId = "1054564";
                m.title ="单词听写";
                m.moduleType = 2;
            }
            ModuleHandler.start(activity,new ModuleData(m,bundle));
        }else {
            // 没有作答记录，直接进入引导页
            RecognizeFlow recognizeFlow = new RecognizeFlow(wordStatisticInfo.testid, liveGetInfo.getId(), wordStatisticInfo.pagetype, liveGetInfo.getTeacherId(), wordStatisticInfo.answers);
            bundle.putParcelable("data", recognizeFlow);
            bundle.put("what","Launch");
//            XueErSiRouter.startModule(activity, "/dictation/Launch", bundle);
            Module m = AppBll.getInstance().getModuleByModuleName("endictation");
            if (m==null) {
                m = new Module();
                m.moduleName = "endictation";
                m.version = "1.0.0";
                m.fileUrl = "https://test.com/endiction.apk";
                m.mac = "4156465424185";
                m.moduleId = "1054564";
                m.title ="单词听写";
                m.moduleType = 2;
            }
            ModuleHandler.start(activity,new ModuleData(m,bundle));
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
                    bundle.put("what","Result");
//                    XueErSiRouter.startModule(activity, "/dictation/Result", bundle);
                    Module m = AppBll.getInstance().getModuleByModuleName("endictation");
                    if (m==null) {
                        m = new Module();
                        m.moduleName = "endictation";
                        m.version = "1.0.0";
                        m.fileUrl = "https://test.com/endiction.apk";
                        m.mac = "4156465424185";
                        m.moduleId = "1054564";
                        m.title ="单词听写";
                        m.moduleType = 2;
                    }
                    ModuleHandler.start(activity,new ModuleData(m,bundle));
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
