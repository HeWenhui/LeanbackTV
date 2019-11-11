package com.xueersi.parentsmeeting.modules.livevideo.core;

import android.content.Context;

import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.business.AllLiveBasePagerInter;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.page.FirstPager;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by linyuqiang on 2018/7/30.
 */

public class AllLiveBasePagerIml implements AllLiveBasePagerInter {
    protected Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private ArrayList<LiveBasePager> liveBasePagers = new ArrayList<>();
    Context context;

    private volatile List<ViewRemoveObserver> unAttachViewObservers;

    public AllLiveBasePagerIml(Context context) {
        this.context = context;
        ProxUtil.getProxUtil().put(context, AllLiveBasePagerInter.class, this);
    }

    public boolean onUserBackPressed() {
        logger.d("onUserBackPressed:liveBasePagers=" + liveBasePagers.size());
        ArrayList<LiveBasePager> liveBasePagersTemp = new ArrayList<>(liveBasePagers);
        sortPager(liveBasePagersTemp);
        for (int i = liveBasePagersTemp.size() - 1; i >= 0; i--) {
            LiveBasePager liveBasePager = liveBasePagersTemp.get(i);
            boolean onUserBackPressed = liveBasePager.onUserBackPressed();
            logger.d("onUserBackPressed:liveBasePager=" + liveBasePager + ",Back=" + onUserBackPressed);
            if (onUserBackPressed) {
                return true;
            }
        }
        return false;
    }

    public void onDestroy() {
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
        notifyUnAttachPager(liveBasePager);
        if (liveBasePager.getLivePagerBack() != null) {
            LiveBackBll.ShowQuestion showQuestion = ProxUtil.getProxUtil().get(context, LiveBackBll.ShowQuestion.class);
            if (showQuestion != null) {
                showQuestion.onHide(liveBasePager.getBaseVideoQuestionEntity());
            }
        }
    }

    private synchronized void notifyUnAttachPager(LiveBasePager liveBasePager) {
        if (unAttachViewObservers != null) {

            Iterator<ViewRemoveObserver> iterator = unAttachViewObservers.iterator();
            while (iterator.hasNext()) {
                ViewRemoveObserver viewRemoveObserver = iterator.next();
                if (viewRemoveObserver.removeViewCallBack(liveBasePager)) {
                    iterator.remove();
                }
            }
        }
    }

    @Override
    public synchronized void addViewRemoveObserver(ViewRemoveObserver viewRemoveObserver) {
        if (unAttachViewObservers == null) {
            unAttachViewObservers = new ArrayList<>();
        }
        if (!unAttachViewObservers.contains(viewRemoveObserver)) {
            unAttachViewObservers.add(viewRemoveObserver);
        }
    }

    @Override
    public synchronized void removeViewRemoveObserver(ViewRemoveObserver viewRemoveObserver) {
        if (unAttachViewObservers != null) {
            unAttachViewObservers.remove(viewRemoveObserver);
        }
    }

    /**
     * 将evaluteTeacherPager排到最前，最后获得onUserBackPressed事件
     */
    private void sortPager(ArrayList<LiveBasePager> liveBasePagersTemp) {
        LiveBasePager liveBasePager;
        for (int i = 0; i < liveBasePagersTemp.size(); i++) {
            liveBasePager = liveBasePagersTemp.get(i);
            if (liveBasePager instanceof FirstPager) {
//                for (int j = i - 1; j >= 0; j--) {
//                    liveBasePagersTemp.set(j + 1, liveBasePagersTemp.get(j));
//                }
                liveBasePagersTemp.remove(i);
                liveBasePagersTemp.add(0, liveBasePager);
                break;
            }
        }
    }
}
