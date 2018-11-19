package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.xueersi.parentsmeeting.modules.livevideo.R;

/**
 * 半身直播 战队Pk 状态栏
 *
 * @author chenkun
 * @version 1.0, 2018/10/25 下午4:15
 */

public class LiveArtsHalBodyPkStateLayout extends LiveHalBodyPkStateLayout {


    public LiveArtsHalBodyPkStateLayout(@NonNull Context context) {
        super(context);
    }

    public LiveArtsHalBodyPkStateLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LiveArtsHalBodyPkStateLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.halfbody_arts_teampk_state_layout;
    }

    @Override
    public void showPkReady() {
        ivPkState.setImageResource(R.drawable.live_halfbody_pk_ready_arts);
        showViewWithFadeInOutEffect(ivPkState,PK_STATE_DISPLAY_DURATION);
    }


    @Override
    public void updatePkState(float ratio){
        if (this.showPopWindow) {
            this.showPopWindow = false;
            if (ratio > HALF_PROGRESS) {
                ivPkState.setImageResource(R.drawable.live_halfbody_pk_lead_arts);
                showViewWithFadeInOutEffect(ivPkState,PK_STATE_DISPLAY_DURATION);
            } else if (ratio < HALF_PROGRESS) {
                ivPkState.setImageResource(R.drawable.live_halfbody_pk_follow_arts);
                showViewWithFadeInOutEffect(ivPkState,PK_STATE_DISPLAY_DURATION);
            }
        }
    }

}
