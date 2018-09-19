package com.xueersi.parentsmeeting.modules.livevideo.worddictation.business;

import android.app.Activity;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
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

    public WordDictationIRCBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
    }

    @Override
    public void onTopic(LiveTopic liveTopic, JSONObject jsonObject, boolean modeChange) {
        WordStatisticInfo wordStatisticInfo = liveTopic.getCoachRoomstatus().getWordStatisticInfo();
        if (wordStatisticInfo != null) {
            int state = wordStatisticInfo.state;
            if (state == 1) {
                if (!isOpen) {
                    isOpen = true;
                    if (wordDictationAction == null) {
                        WordDictationBll wordDictationBll = new WordDictationBll(activity);
                        wordDictationBll.initView(mRootView);
                        wordDictationBll.setGetInfo(mGetInfo);
                        wordDictationAction = wordDictationBll;
                    }
                    wordDictationAction.onStart(wordStatisticInfo);
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
    }

    @Override
    public void initView(RelativeLayout bottomContent, AtomicBoolean mIsLand) {
        super.initView(bottomContent, mIsLand);

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
                    WordStatisticInfo wordStatisticInfo = new WordStatisticInfo();
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
                            wordDictationAction.onStart(wordStatisticInfo);
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
    public void onDestory() {
        super.onDestory();
        if (wordDictationAction != null) {
            wordDictationAction.onDestory();
        }
    }
}
