package com.xueersi.parentsmeeting.modules.livevideo.betterme.contract;

/**
 * @Date on 2019/7/9 17:46
 * @Author zhangyuansun
 * @Description
 */
public interface BetterMeTeamPKContract {
    /**
     * 显示分队仪式，从notice显示，topic不显示
     *
     * @param showPk
     */
    void onPKStart(boolean showPk);

    void onPKEnd();
}
