package com.xueersi.parentsmeeting.modules.livevideo.switchflow;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;

public class SwitchFlowPager extends BasePager {
    /** 出现时从右往左侧滑的动画 */
    private Animation animationIn;

    /** 消失时从左往右侧滑的动画 */
    private Animation animationOut;

    public SwitchFlowPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_switch_flow, null);
        view.findViewById(R.id.tv_livevideo_switch_flow_route1);
        view.findViewById(R.id.tv_livevideo_switch_flow_route2);
        view.findViewById(R.id.tv_livevideo_switch_flow_route3);
        view.findViewById(R.id.tv_livevideo_switch_flow_route4);

        return view;
    }

    @Override
    public void initData() {
        animationIn = AnimationUtils.loadAnimation(mContext, R.anim.anim_livevideo_rank_in);
        animationOut = AnimationUtils.loadAnimation(mContext, R.anim.anim_livevideo_rank_out);
    }
}
