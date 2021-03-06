package com.xueersi.parentsmeeting.modules.livevideo.worddictation.business;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.RelativeLayout;

import com.xueersi.common.business.AppBll;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.permission.XesPermission;
import com.xueersi.common.permission.config.PermissionConfig;
import com.xueersi.common.route.XueErSiRouter;
import com.xueersi.common.route.module.ModuleHandler;
import com.xueersi.common.route.module.entity.Module;
import com.xueersi.common.route.module.entity.ModuleData;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveAppUserInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.User;
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
        if (liveGetInfo.getSmallEnglish()){
            if (DictationQuery.hasSavedRecord(activity,wordStatisticInfo.testid, liveGetInfo.getId())){
                // 已经有作答记录,直接查看结果
                RecognizeFlow savedData = DictationQuery.getLastRecord(activity);
                bundle.putSerializable("data", savedData);
                bundle.putString("what","Result");

                String moduleName = "endictation";
                Module m = AppBll.getInstance().getModuleByModuleName(moduleName);
                if (m == null) {
                    m = new Module();
                    m.moduleName = moduleName;
                    m.version = "1.0.1";
                    m.title = "英语单词听写";
                    m.moduleType = 0;
                }
                ModuleHandler.start(activity, new ModuleData(m, bundle));
            }else {
                // 没有作答记录，直接进入引导页
                RecognizeFlow recognizeFlow = new RecognizeFlow(wordStatisticInfo.testid, liveGetInfo.getId(), wordStatisticInfo.pagetype, liveGetInfo.getTeacherId(), wordStatisticInfo.answers, LiveAppUserInfo.getInstance().getStuId());
                bundle.putSerializable("data", recognizeFlow);
                bundle.putString("what","Launch");

                String moduleName = "endictation";
                Module m = AppBll.getInstance().getModuleByModuleName(moduleName);
                if (m == null) {
                    m = new Module();
                    m.moduleName = moduleName;
                    m.version = "1.0.1";
                    m.title = "英语单词听写";
                    m.moduleType = 0;
                }
                ModuleHandler.start(activity, new ModuleData(m, bundle));
            }
        } else{
            if (DictationQuery.hasSavedRecord(activity,wordStatisticInfo.testid, liveGetInfo.getId())){
                // 已经有作答记录,直接查看结果
                RecognizeFlow savedData = DictationQuery.getLastRecord(activity);
                bundle.putSerializable("data", savedData);
                bundle.putString("what","MiddleResult");

                String moduleName = "endictation";
                Module m = AppBll.getInstance().getModuleByModuleName(moduleName);
                if (m == null) {
                    m = new Module();
                    m.moduleName = moduleName;
                    m.version = "1.0.1";
                    m.title = "英语单词听写";
                    m.moduleType = 0;
                }
                ModuleHandler.start(activity, new ModuleData(m, bundle));
            }else {
                // 没有作答记录，直接进入引导页
                RecognizeFlow recognizeFlow = new RecognizeFlow(wordStatisticInfo.testid, liveGetInfo.getId(), wordStatisticInfo.pagetype, liveGetInfo.getTeacherId(), wordStatisticInfo.answers, LiveAppUserInfo.getInstance().getStuId());
                bundle.putSerializable("data", recognizeFlow);
                bundle.putString("what","MiddleLaunch");

                String moduleName = "endictation";
                Module m = AppBll.getInstance().getModuleByModuleName(moduleName);
                if (m == null) {
                    m = new Module();
                    m.moduleName = moduleName;
                    m.version = "1.0.1";
                    m.title = "英语单词听写";
                    m.moduleType = 0;
                }
                ModuleHandler.start(activity, new ModuleData(m, bundle));
            }
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
    public void onDestroy() {
        if (wordReceiver != null) {
            activity.unregisterReceiver(wordReceiver);
        }
    }

    class WordReceiver extends BroadcastReceiver {
        View view;
        @Override
        public void onReceive(Context context, Intent intent) {
            final RecognizeFlow recognizeFlow = (RecognizeFlow) intent.getSerializableExtra("data");
            logger.d("onReceive:recognizeFlow=" + recognizeFlow);
            LiveVideoPoint liveVideoPoint = LiveVideoPoint.getInstance();
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
            if (liveGetInfo.getSmallEnglish()){
                view = LayoutInflater.from(activity).inflate(R.layout.layout_word_dictation_complete, null);
                view.setPadding(view.getLeft(), (int) (50 * ScreenUtils.getScreenDensity()), liveVideoPoint.getRightMargin(), view.getBottom());
            }else {
                view = LayoutInflater.from(activity).inflate(R.layout.layout_word_middle_school_dictation_complete, null);
                lp.topMargin = SizeUtils.Dp2Px(activity,20);
            }
            logger.d("onReceive:top=" + view.getTop() + ",rightMargin=" + liveVideoPoint.getRightMargin());
            bottomContent.addView(view, lp);
            view.findViewById(R.id.bt_livevideo_worddictation_result).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (liveGetInfo.getSmallEnglish()){
                        logger.d("result stuanswer"+ recognizeFlow);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("data", recognizeFlow);
                        bundle.putString("what","Result");

                        String moduleName = "endictation";
                        Module m = AppBll.getInstance().getModuleByModuleName(moduleName);
                        if (m == null) {
                            m = new Module();
                            m.moduleName = moduleName;
                            m.version = "1.0.1";
                            m.title = "英语单词听写";
                            m.moduleType = 0;
                        }
                        ModuleHandler.start(activity, new ModuleData(m, bundle));
                    } else {
                        logger.d("middle result stuanswer"+ recognizeFlow);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("data", recognizeFlow);
                        bundle.putString("what","MiddleResult");

                        String moduleName = "endictation";
                        Module m = AppBll.getInstance().getModuleByModuleName(moduleName);
                        if (m == null) {
                            m = new Module();
                            m.moduleName = moduleName;
                            m.version = "1.0.1";
                            m.title = "英语单词听写";
                            m.moduleType = 0;
                        }
                        ModuleHandler.start(activity, new ModuleData(m, bundle));
                    }
                    bottomContent.removeView(view);
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
