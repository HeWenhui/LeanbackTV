package com.xueersi.parentsmeeting.modules.livevideo.core;

import android.content.Context;

import com.xueersi.parentsmeeting.modules.livevideo.business.AllLiveBasePagerInter;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

import java.util.ArrayList;

/**
 * Created by lyqai on 2018/7/30.
 */

public class AllLiveBasePagerIml implements AllLiveBasePagerInter {
    private String TAG = "AllLiveBasePagerIml";
    private ArrayList<LiveBasePager> liveBasePagers = new ArrayList<>();
    Context context;

    public AllLiveBasePagerIml(Context context) {
        this.context = context;
        ProxUtil.getProxUtil().put(context, AllLiveBasePagerInter.class, this);
    }

    public boolean onUserBackPressed() {
        Loger.d(TAG, "onUserBackPressed:liveBasePagers=" + liveBasePagers.size());
        ArrayList<LiveBasePager> liveBasePagersTemp = new ArrayList<>(liveBasePagers);
        for (int i = liveBasePagersTemp.size() - 1; i >= 0; i--) {
            LiveBasePager liveBasePager = liveBasePagersTemp.get(i);
            boolean onUserBackPressed = liveBasePager.onUserBackPressed();
            Loger.d(TAG, "onUserBackPressed:liveBasePager=" + liveBasePager + ",Back=" + onUserBackPressed);
            if (onUserBackPressed) {
                return true;
            }
        }
        return false;
    }

    public void onDestory() {
        ArrayList<LiveBasePager> tempLiveBasePagers = new ArrayList<>(liveBasePagers);
        for (LiveBasePager basePager : tempLiveBasePagers) {
            basePager.onDestroy();
        }
        liveBasePagers.clear();
    }

    @Override
    public void addLiveBasePager(LiveBasePager liveBasePager) {
        liveBasePagers.add(liveBasePager);
    }

    @Override
    public void removeLiveBasePager(LiveBasePager liveBasePager) {
        liveBasePagers.remove(liveBasePager);
        if (liveBasePager.getLivePagerBack() != null) {
            LiveBackBll.ShowQuestion showQuestion = ProxUtil.getProxUtil().get(context, LiveBackBll.ShowQuestion.class);
            if (showQuestion != null) {
                showQuestion.onShow(false);
            }
        }
    }
}
