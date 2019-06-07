package com.xueersi.parentsmeeting.modules.livevideo.primaryclass;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;


public class PrimaryClassViewCn implements PrimaryClassView {
    public PrimaryClassViewCn(Context context) {
        ProxUtil.getProxUtil().put(context, PrimaryClassView.class, this);
    }

    @Override
    public void decorateFrame(ImageView imageView) {
        imageView.setImageResource(R.drawable.bg_live_primary_class_kuangjia_img_normal_cn);
    }

    @Override
    public void decorateBack(RelativeLayout rl_course_video_contentview) {
        rl_course_video_contentview.setBackgroundResource(R.drawable.bg_livevideo_priclass_normal_cn);
    }

    @Override
    public void decorateNovideo(View view) {
        view.setBackgroundColor(0xff2D2D2D);
        ImageView imageView = view.findViewById(R.id.iv_course_video_novideo);
        imageView.setImageResource(R.drawable.bg_livevideo_paly_novidwo_bg);
    }

    @Override
    public void decorateRlContent(View rlContent, int width, int height) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) rlContent.getLayoutParams();
        float scale = (float) width / 1334f;
        LiveVideoPoint videoPoint = LiveVideoPoint.getInstance();
        int leftGap = (videoPoint.screenWidth - width) / 2;
        int topGap = (videoPoint.screenHeight - height) / 2;
        int leftMargin = (int) (13 * scale) + leftGap;
        int bottomMargin = (int) (13 * scale) + topGap;
        int rightMargin = (int) (226 * scale) + leftGap;
        int topMargin = (int) (90 * scale) + topGap;
        if (lp.leftMargin != leftMargin || lp.bottomMargin != bottomMargin || lp.rightMargin != rightMargin || lp.topMargin != topMargin) {
            lp.leftMargin = leftMargin;
            lp.bottomMargin = bottomMargin;
            lp.rightMargin = rightMargin;
            lp.topMargin = topMargin;
            LayoutParamsUtil.setViewLayoutParams(rlContent, lp);
        }
    }

    @Override
    public void decorateItemPager(View view) {
        TextView tv_livevideo_primary_team_name_mid = view.findViewById(R.id.tv_livevideo_primary_team_name_mid);
        tv_livevideo_primary_team_name_mid.setTextColor(0xff7B583E);
        tv_livevideo_primary_team_name_mid.setBackgroundResource(R.drawable.bg_live_tips_bg_normal_cn);
        TextView tv_livevideo_primary_team_name = view.findViewById(R.id.tv_livevideo_primary_team_name);
        tv_livevideo_primary_team_name.setTextColor(0xff408474);
    }

    @Override
    public void decorateItemPagerView(RelativeLayout rl_livevideo_primary_team_content, ImageView iv_livevideo_primary_team_icon, LinearLayout ll_livevideo_primary_team_content, TextView tv_livevideo_primary_team_name_mid, int width, int height) {
        float scale = (float) width / 1334f;
        {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) rl_livevideo_primary_team_content.getLayoutParams();
            int lpwidth = (int) (188 * scale);
            int lpheight = (int) (72 * scale);
            int leftMargin = (ScreenUtils.getScreenWidth() - width) / 2 + (int) (1124 * scale);
            int topMargin = (ScreenUtils.getScreenHeight() - height) / 2 + (int) (0 * scale);
            if (lp.width != lpwidth || lp.height != lpheight || lp.leftMargin != leftMargin || lp.topMargin != topMargin) {
                lp.width = lpwidth;
                lp.height = lpheight;
                lp.leftMargin = leftMargin;
                lp.topMargin = topMargin;
                LayoutParamsUtil.setViewLayoutParams(rl_livevideo_primary_team_content, lp);
            }
            RelativeLayout.LayoutParams lpImg = (RelativeLayout.LayoutParams) iv_livevideo_primary_team_icon.getLayoutParams();
            int lpImgWidth = (int) (49 * scale);
            int lpImgHeight = (int) (46 * scale);
            if (lpImg.width != lpImgWidth || lpImg.height != lpImgHeight) {
                lpImg.width = lpImgWidth;
                lpImg.height = lpImgHeight;
                LayoutParamsUtil.setViewLayoutParams(iv_livevideo_primary_team_icon, lpImg);
            }
        }
        {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) ll_livevideo_primary_team_content.getLayoutParams();
            int lpwidth = (int) (195 * scale);
            int lpheight = (int) (630 * scale);
            int leftMargin = (ScreenUtils.getScreenWidth() - width) / 2 + (int) (1121 * scale);
            int topMargin = (ScreenUtils.getScreenHeight() - height) / 2 + (int) (88 * scale);
            if (lp.width != lpwidth || lp.height != lpheight || lp.leftMargin != leftMargin || lp.topMargin != topMargin) {
                lp.width = lpwidth;
                lp.height = lpheight;
                lp.leftMargin = leftMargin;
                lp.topMargin = topMargin;
                LayoutParamsUtil.setViewLayoutParams(ll_livevideo_primary_team_content, lp);
                for (int i = 0; i < ll_livevideo_primary_team_content.getChildCount(); i++) {
                    View child = ll_livevideo_primary_team_content.getChildAt(i);
                    ViewGroup.MarginLayoutParams childLp = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
                    int childHeight = (int) (148 * scale);
                    int margin = (int) (11 * scale);
                    if (childLp.height != childHeight || childLp.bottomMargin != margin) {
                        childLp.height = childHeight;
                        childLp.bottomMargin = margin;
                        LayoutParamsUtil.setViewLayoutParams(child, childLp);
                    }
                }
            }
        }
        {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tv_livevideo_primary_team_name_mid.getLayoutParams();
            int topMargin = (ScreenUtils.getScreenHeight() - height) / 2 + (int) (114 * scale);
            if (lp.topMargin != topMargin) {
                lp.topMargin = topMargin;
                LayoutParamsUtil.setViewLayoutParams(tv_livevideo_primary_team_name_mid, lp);
            }
        }
    }

    /** {@link R.layout#item_primary_class_team_video } */
    @Override
    public void decorateItemMy(View view) {
        ImageView iv_livevideo_primary_team_energy = view.findViewById(R.id.iv_livevideo_primary_team_energy);
        iv_livevideo_primary_team_energy.setImageResource(R.drawable.bg_livevideo_toast_energe2_icon_normal_cn);
        RelativeLayout rl_livevideo_course_item_video_off = view.findViewById(R.id.rl_livevideo_course_item_video_off);
        rl_livevideo_course_item_video_off.setBackgroundResource(R.drawable.shape_primary_item_empty_back_cn);
        RelativeLayout rl_livevideo_course_item_video_ufo = view.findViewById(R.id.rl_livevideo_course_item_video_ufo);
        rl_livevideo_course_item_video_ufo.setBackgroundResource(R.drawable.shape_primary_item_empty_back_cn);
        ImageView iv_livevideo_course_item_video_ufo = view.findViewById(R.id.iv_livevideo_course_item_video_ufo);
        iv_livevideo_course_item_video_ufo.setImageResource(R.drawable.bg_live_xuesheng_shipin_mid_ufo_normal_cn);
        ImageView iv_live_xuesheng_shipin_mid_daijiaru_normal = view.findViewById(R.id.iv_live_xuesheng_shipin_mid_daijiaru_normal);
        iv_live_xuesheng_shipin_mid_daijiaru_normal.setImageResource(R.drawable.bg_lvie_xuesheng_shipin_mid_diaoxian_normal_cn);
    }

    /** {@link R.layout#item_primary_class_team_other_video } */
    @Override
    public void decorateItemOther(View view) {
        RelativeLayout rl_livevideo_course_item_video_off = view.findViewById(R.id.rl_livevideo_course_item_video_off);
        rl_livevideo_course_item_video_off.setBackgroundResource(R.drawable.shape_primary_item_empty_back_cn);
        RelativeLayout rl_livevideo_course_item_video_ufo = view.findViewById(R.id.rl_livevideo_course_item_video_ufo);
        rl_livevideo_course_item_video_ufo.setBackgroundResource(R.drawable.shape_primary_item_empty_back_cn);
        ImageView iv_livevideo_course_item_video_ufo = view.findViewById(R.id.iv_livevideo_course_item_video_ufo);
        iv_livevideo_course_item_video_ufo.setImageResource(R.drawable.bg_live_xuesheng_shipin_mid_ufo_normal_cn);
        ImageView iv_live_xuesheng_shipin_mid_daijiaru_normal = view.findViewById(R.id.iv_live_xuesheng_shipin_mid_daijiaru_normal);
        iv_live_xuesheng_shipin_mid_daijiaru_normal.setImageResource(R.drawable.bg_lvie_xuesheng_shipin_mid_diaoxian_normal_cn);
    }

    @Override
    public void decorateItemBack(View view) {

    }

    /** {@link R.layout#item_primary_class_team_empty_video } */
    @Override
    public void decorateItemEmpty(View view) {
        view.setBackgroundResource(R.drawable.shape_primary_item_empty_back_cn);
        ImageView iv_live_xuesheng_shipin_mid_daijiaru_normal = view.findViewById(R.id.iv_live_xuesheng_shipin_mid_daijiaru_normal);
        iv_live_xuesheng_shipin_mid_daijiaru_normal.setImageResource(R.drawable.bg_live_xuesheng_shipin_mid_daijiaru_normal_cn);
    }

    @Override
    public void decorateItemMyAddEnergy(View view) {
        ImageView iv_livevideo_primary_team_energy = view.findViewById(R.id.iv_livevideo_primary_team_energy);
        iv_livevideo_primary_team_energy.setImageResource(R.drawable.bg_livevideo_toast_energe1_icon_normal_cn);
    }
}
