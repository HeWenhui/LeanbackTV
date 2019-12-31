package com.xueersi.parentsmeeting.modules.livevideo.worddictation.business;

import android.app.Activity;
import android.widget.RelativeLayout;

import com.xueersi.common.business.AppBll;
import com.xueersi.common.route.module.ModuleHandler;
import com.xueersi.common.route.module.entity.Module;
import com.xueersi.common.route.module.entity.ModuleData;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.TopicAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.worddictation.entity.WordStatisticInfo;

import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by linyuqiang on 2018/8/31.
 * 单词听写
 */
public class WordDictationIRCBll extends LiveBaseBll implements NoticeAction, TopicAction {

    boolean isOpen = false;
    WordDictationAction wordDictationAction;
    LogToFile logToFile;

    public WordDictationIRCBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
        logToFile = new LogToFile(context, TAG);
//        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Bundle bundle = new Bundle();
//                String answer = "[\"twenty\",\"thirty\",\"forty\",\"fifty\",\"sisty\",\"twenty\",\"thirty\",\"forty\",\"fifty\",\"sisty\"]";
//                RecognizeFlow recognizeFlow = new RecognizeFlow("1111", "1313", "", "3021", answer);
//                bundle.putSerializable("data", recognizeFlow);
//                bundle.putString("what","MiddleLaunch");
//
//                String moduleName = "endictation";
//                Module m = AppBll.getInstance().getModuleByModuleName(moduleName);
//                if (m == null) {
//                    m = new Module();
//                    m.moduleName = moduleName;
//                    m.version = "1.0.1";
//                    m.title = "英语单词听写";
//                    m.moduleType = 0;
//                }
//                ModuleHandler.start(activity, new ModuleData(m, bundle));
//            }
//        },5000);
    }

    @Override
    public void onTopic(LiveTopic liveTopic, JSONObject jsonObject, boolean modeChange) {
        if (mLiveType == LiveVideoConfig.LIVE_TYPE_LIVE && jsonObject.has("room_2")) {
            try {
                JSONObject status = jsonObject.getJSONObject("room_2");
                if (status.has("wordStatisticInfo")) {
                    JSONObject jsonWordStatisticInfo = status.getJSONObject("wordStatisticInfo");
                    final WordStatisticInfo wordStatisticInfo = new WordStatisticInfo();
                    int state = jsonWordStatisticInfo.getInt("state");
                    wordStatisticInfo.state = state;
                    if (state == 1) {
                        wordStatisticInfo.testid = jsonWordStatisticInfo.getString("testid");
                        wordStatisticInfo.pagetype = jsonWordStatisticInfo.getString("pagetype");
                        wordStatisticInfo.answers = jsonWordStatisticInfo.getString("answers");
                    }
                    if (state == 1) {
                        if (!isOpen) {
                            isOpen = true;
                            if (wordDictationAction == null) {
                                WordDictationBll wordDictationBll = new WordDictationBll(activity);
                                wordDictationBll.initView(mRootView);
                                wordDictationBll.setGetInfo(mGetInfo);
                                wordDictationAction = wordDictationBll;
                            }
                            post(new Runnable() {
                                @Override
                                public void run() {
                                    if (wordDictationAction != null) {
                                        wordDictationAction.onStart(wordStatisticInfo);
                                    }
                                }
                            });
                        }
                    } else {
                        if (isOpen) {
                            isOpen = false;
                            if (wordDictationAction != null) {
                                wordDictationAction.onStop();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                logToFile.e("onTopic", e);
            }
        }
    }

    @Override
    public void onModeChange(String oldMode, String mode, boolean isPresent) {
        super.onModeChange(oldMode, mode, isPresent);
        if (LiveTopic.MODE_CLASS.equals(mode) && wordDictationAction != null) {
            WordDictationBll bll = (WordDictationBll) wordDictationAction;
            bll.sendSwtichStream();
        }
    }

    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {
        switch (type) {
            case XESCODE.ARTS_WORD_DICTATION:
                try {
                    final WordStatisticInfo wordStatisticInfo = new WordStatisticInfo();
                    int state = data.optInt("state", 0);
                    wordStatisticInfo.state = state;
                    if (state == 1) {
                        if (!isOpen) {
                            String pagetype = data.getString("pagetype");
                            String testid = data.getString("testid");
                            String answers = data.getString("answers");
                            wordStatisticInfo.pagetype = pagetype;
                            wordStatisticInfo.testid = testid;
                            wordStatisticInfo.answers = answers;
                            isOpen = true;
                            if (wordDictationAction == null) {
                                WordDictationBll wordDictationBll = new WordDictationBll(activity);
                                wordDictationBll.initView(mRootView);
                                wordDictationBll.setGetInfo(mGetInfo);
                                wordDictationAction = wordDictationBll;
                            }
                            post(new Runnable() {
                                @Override
                                public void run() {
                                    if (wordDictationAction != null) {
                                        wordDictationAction.onStart(wordStatisticInfo);
                                    }
                                }
                            });
                        }
                    } else {
                        if (isOpen) {
                            isOpen = false;
                            if (wordDictationAction != null) {
                                wordDictationAction.onStop();
                            }
                        }
                    }
                } catch (Exception e) {

                }
                break;
        }
    }

    @Override
    public int[] getNoticeFilter() {
        return new int[]{XESCODE.ARTS_WORD_DICTATION};
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (wordDictationAction != null) {
            wordDictationAction.onDestroy();
        }
    }
}
