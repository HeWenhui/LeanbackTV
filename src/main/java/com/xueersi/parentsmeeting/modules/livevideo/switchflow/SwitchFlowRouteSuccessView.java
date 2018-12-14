package com.xueersi.parentsmeeting.modules.livevideo.switchflow;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.widget.FangZhengCuYuanTextView;

public class SwitchFlowRouteSuccessView extends BasePager {
    /**  */
    private FangZhengCuYuanTextView tvSuccess;
    private ImageView ivBackGround;

    public SwitchFlowRouteSuccessView(Context mContext, boolean isLazy) {
        super(mContext, isLazy);
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_triple_screen_switch_route_success, null);
        ivBackGround = view.findViewById(R.id.iv_livevideo_triple_screen_switch_route_success);
        tvSuccess = view.findViewById(R.id.fzcytv_livevideo_triple_screen_switch_route_success_text);
        return view;
    }

    public void updateView(int pos) {
        boolean isSmallEnglish = ((Activity) mContext).getIntent().getBooleanExtra("isSmallEnglish", false);
        Drawable drawable;
        int color;
        if (LiveVideoConfig.isSmallChinese) {
            drawable = mContext.getResources().getDrawable(R.drawable.shellwindow_toast_board);
            color = mContext.getResources().getColor(R.color.COLOR_5A3A13);
        } else if (LiveVideoConfig.isPrimary) {
            drawable = mContext.getResources().getDrawable(R.drawable.livevideo_mic_psbg);
            color = mContext.getResources().getColor(R.color.COLOR_7D553F);
        } else if (isSmallEnglish) {
            drawable = mContext.getResources().getDrawable(R.drawable.bg_livevideo_small_english_raise_hand);
            color = mContext.getResources().getColor(R.color.COLOR_0096EF);
        } else {
            drawable = mContext.getResources().getDrawable(R.drawable.bg_livevideo_small_english_raise_hand);
            color = mContext.getResources().getColor(R.color.COLOR_0096EF);
        }
        ivBackGround.setImageDrawable(drawable);
        tvSuccess.setText("已切换到线路" + pos);
        tvSuccess.setTextColor(color);
    }

    @Override
    public void initData() {

    }
}
