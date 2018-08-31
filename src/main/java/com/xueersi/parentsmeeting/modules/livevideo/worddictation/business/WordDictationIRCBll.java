package com.xueersi.parentsmeeting.modules.livevideo.worddictation.business;

import android.app.Activity;

import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.TopicAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;

import org.json.JSONObject;

/**
 * Created by linyuqiang on 2018/8/31.
 * 单词听写
 */
public class WordDictationIRCBll extends LiveBaseBll implements NoticeAction, TopicAction {

    public WordDictationIRCBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
    }

    @Override
    public void onTopic(LiveTopic liveTopic, JSONObject jsonObject, boolean modeChange) {

    }

    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {
        switch (type) {
            case XESCODE.ARTS_WORD_DICTATION:
                try {
                    int state = data.optInt("state", 0);
                    if (state == 1) {
                        int pagetype = data.getInt("pagetype");
                        int testid = data.getInt("testid");
                        String answers = data.getString("answers");
                    } else {

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
}
