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
 * 半身直播-小班体验 战队Pk 状态栏
 *
 * @author linyuqiang
 * @version 1.0, 2019/5/16 下午4:15
 */

public class LiveHalBodyPrimaryPkStateLayout extends LiveHalBodyPkStateLayout {

    public LiveHalBodyPrimaryPkStateLayout(@NonNull Context context) {
        super(context);
    }

    public LiveHalBodyPrimaryPkStateLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LiveHalBodyPrimaryPkStateLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 获取布局layout
     * @return
     */
    protected int getLayoutId() {
        return R.layout.halfbody__primary_team_pk_state_layout;
    }

}
