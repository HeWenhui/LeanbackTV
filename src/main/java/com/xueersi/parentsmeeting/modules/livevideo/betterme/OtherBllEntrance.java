package com.xueersi.parentsmeeting.modules.livevideo.betterme;

import android.content.Context;

import com.xueersi.parentsmeeting.modules.livevideo.achievement.business.UpdateAchievement;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.contract.BetterMeTeamPKContract;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.AimRealTimeValEntity;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.BetterMeEntity;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

/**
 * @Date on 2019/7/16 19:12
 * @Author zhangyuansun
 * @Description 小目标引用其他业务入口
 */
public class OtherBllEntrance {
    public static class EnglishTeamPK {
        /*
         * 开始战队PK分队仪式
         */
        public static void startPK(Context context, boolean isNotice) {
            BetterMeTeamPKContract betterMeTeamPKContract = ProxUtil.getProxUtil().get(context,
                    BetterMeTeamPKContract.class);
            if (betterMeTeamPKContract != null) {
                betterMeTeamPKContract.onPKStart(isNotice);
            }
        }
        /*
         * 开始战队PK结果页面
         */
        public static void endPK(Context context) {
            BetterMeTeamPKContract betterMeTeamPKContract = ProxUtil.getProxUtil().get(context,
                    BetterMeTeamPKContract.class);
            if (betterMeTeamPKContract != null) {
                betterMeTeamPKContract.onPKEnd();
            }
        }
    }

    public static class EnglishAchievent {
        public static void updateBetterMe(Context context, AimRealTimeValEntity aimRealTimeValEntity,boolean isShowBubble) {
            UpdateAchievement updateAchievement = ProxUtil.getProxUtil().get(context, UpdateAchievement.class);
            if (updateAchievement != null) {
                updateAchievement.onUpdateBetterMe(aimRealTimeValEntity,isShowBubble);
            }
        }

        public static void receiveBetterMe(Context context, BetterMeEntity betterMeEntity, boolean isNotice) {
            UpdateAchievement updateAchievement = ProxUtil.getProxUtil().get(context, UpdateAchievement.class);
            if (updateAchievement != null) {
                updateAchievement.onReceiveBetterMe(betterMeEntity, isNotice);
            }
        }
    }
}
