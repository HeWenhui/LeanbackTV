package com.xueersi.parentsmeeting.modules.livevideo.achievement.business;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveViewAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StarAndGoldEntity;

import java.util.ArrayList;

/**
 * ${tags}
 *
 * @author chenkun
 * @version 1.0, 2018/10/25 下午2:48
 */

public class LiveHalfBodyAchievementBll implements StarInteractAction {

    private int starCount;
    private int goldCount;
    private boolean mIsLand;
    private int liveType;
    private ViewGroup myView;
    private Activity activity;
    LiveAchievementHttp liveBll;

    LiveAndBackDebug liveAndBackDebug;

    /**
     * 金币数量
     */
    private TextView tvGoldCount;


    public LiveHalfBodyAchievementBll(Activity activity, int liveType, int starCount, int goldCount, boolean mIsLand) {
        this.activity = activity;
        this.liveType = liveType;
        this.starCount = starCount;
        this.goldCount = goldCount;
        this.mIsLand = mIsLand;
    }


    @Override
    public void onStarStart(ArrayList<String> data, String starid, String answer, String nonce) {

    }

    @Override
    public void onStarStop(String id, ArrayList<String> answer, String nonce) {

    }

    @Override
    public void onSendMsg(String msg) {

    }

    @Override
    public void onGetStar(StarAndGoldEntity starAndGoldEntity) {

    }

    @Override
    public void onStarAdd(int star, float x, float y) {

    }

    public void setLiveBll(LiveAchievementIRCBll liveAchievementIRCBll) {

        if (liveBll instanceof LiveAndBackDebug) {
            liveAndBackDebug = (LiveAndBackDebug) liveBll;
        }
        this.liveBll = liveBll;


    }

    public void setLiveAndBackDebug(LiveBll2 mLiveBll) {

        this.liveAndBackDebug = liveAndBackDebug;
    }

    public void initView(LiveViewAction liveViewAction) {
        tvGoldCount = activity.findViewById(R.id.tv_teampk_pkstate_coin_num);
        tvGoldCount.setText(String.valueOf(goldCount));
    }


}
