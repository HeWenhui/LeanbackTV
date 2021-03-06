package com.xueersi.parentsmeeting.modules.livevideo.primaryclass;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.common.resources.DrawableHelper;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.weight.PrimaryKuangjiaImageView;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;


public class PrimaryClassViewCn implements PrimaryClassView {
    private Context context;
    private Logger logger = LiveLoggerFactory.getLogger(this);
    private Bitmap lastDrawBitmap;
    private int live_primary_right_head_width;
    private int live_primary_right_head_gap;
    private int live_primary_right_item_height;

    public PrimaryClassViewCn(Context context) {
        this.context = context;
        ProxUtil.getProxUtil().put(context, PrimaryClassView.class, this);
        Resources resources = context.getResources();
        live_primary_right_head_width = resources.getInteger(R.integer.live_primary_right_head_width_cn);
        live_primary_right_head_gap = resources.getInteger(R.integer.live_primary_right_head_gap_cn);
        live_primary_right_item_height = resources.getInteger(R.integer.live_primary_right_item_height_cn);
    }

    @Override
    public int getLive_primary_right_head_gap() {
        return live_primary_right_head_gap;
    }

    @Override
    public int getLive_primary_right_item_height() {
        return live_primary_right_item_height;
    }

    @Override
    public void decorateFrame(ImageView imageView) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
        lp.topMargin = lp.bottomMargin = 5;
        imageView.setLayoutParams(lp);
        imageView.setImageResource(R.drawable.bg_live_primary_class_kuangjia_img_normal_cn);
    }

    @Override
    public void decorateBack(int width, int height, RelativeLayout rl_course_video_contentview) {
        Bitmap oldBitmap = DrawableHelper.bitmapFromResource(context.getResources(), R.drawable.bg_livevideo_priclass_normal_cn);
        LiveVideoPoint liveVideoPoint = LiveVideoPoint.getInstance();
        int screenWidth = liveVideoPoint.screenWidth;
        int screenHeight = liveVideoPoint.screenHeight;
        int bitHeight = oldBitmap.getHeight();
        int bitWidth = oldBitmap.getWidth();
        int newWidth = bitHeight * screenWidth / screenHeight;
        int left = (oldBitmap.getWidth() - newWidth) / 2;
        int top = 0;
        if (left < 0) {
            left = 0;
        }
        int right = left;
        int width2 = oldBitmap.getWidth() - left - right;
        logger.d("decorateBack:left=" + left + ",width=" + bitWidth + "," + width2 + ",height2=" + bitHeight);
        if (width2 < 1 || bitHeight < 1) {
            rl_course_video_contentview.setBackground(new BitmapDrawable(context.getResources(), oldBitmap));
            return;
        }
        Bitmap drawBitmap = Bitmap.createBitmap(oldBitmap, left, top, width2, bitHeight);
        oldBitmap.recycle();
        if (lastDrawBitmap != null) {
            lastDrawBitmap.recycle();
            lastDrawBitmap = null;
        }
        lastDrawBitmap = drawBitmap;
        rl_course_video_contentview.setBackground(new BitmapDrawable(context.getResources(), drawBitmap));
    }

    @Override
    public void decorateNovideo(View view) {
        view.setBackgroundColor(0xff2D2D2D);
        ImageView imageView = view.findViewById(R.id.iv_course_video_novideo);
        imageView.setImageResource(R.drawable.bg_livevideo_paly_novidwo_bg_cn);
    }

    @Override
    public void decorateRlContent(View rlContent, int width, int height) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) rlContent.getLayoutParams();
        float scale = (float) width / 1334f;
        LiveVideoPoint videoPoint = LiveVideoPoint.getInstance();
        int leftGap = (videoPoint.screenWidth - width) / 2;
        int topGap = (videoPoint.screenHeight - height) / 2;
        int leftMargin = (int) (12 * scale) + leftGap;
        int bottomMargin = (int) (16 * scale) + topGap;
        int rightMargin = (int) (226 * scale) + leftGap;
        int topMargin = (int) (89 * scale) + topGap;
        if (lp.leftMargin != leftMargin || lp.bottomMargin != bottomMargin || lp.rightMargin != rightMargin || lp.topMargin != topMargin) {
            lp.leftMargin = leftMargin;
            lp.bottomMargin = bottomMargin;
            lp.rightMargin = rightMargin;
            lp.topMargin = topMargin;
            LayoutParamsUtil.setViewLayoutParams(rlContent, lp);
        }
    }

    @Override
    public void decorateItemPager(View view, PrimaryKuangjiaImageView ivLivePrimaryClassKuangjiaImgNormal) {
        TextView tv_livevideo_primary_team_name_mid = view.findViewById(R.id.tv_livevideo_primary_team_name_mid);
        tv_livevideo_primary_team_name_mid.setTextColor(0xff7B583E);
        tv_livevideo_primary_team_name_mid.setBackgroundResource(R.drawable.bg_live_tips_bg_normal_cn);
        TextView tv_livevideo_primary_team_name = view.findViewById(R.id.tv_livevideo_primary_team_name);
        tv_livevideo_primary_team_name.setTextColor(0xff408474);
//        setPkMid(tv_livevideo_primary_team_name_mid, ivLivePrimaryClassKuangjiaImgNormal.getWidth(), ivLivePrimaryClassKuangjiaImgNormal.getHeight());
        ImageView iv_live_halfbody_pk_state = view.findViewById(R.id.iv_live_halfbody_pk_state);
        iv_live_halfbody_pk_state.setImageResource(R.drawable.bg_live_ke_energy_zhunbei_icon_normal_cn);
    }

    @Override
    public void decorateItemPagerView(RelativeLayout rl_livevideo_primary_team_content, ImageView iv_livevideo_primary_team_icon, LinearLayout ll_livevideo_primary_team_content, TextView tv_livevideo_primary_team_name_mid, int width, int height) {
        float scale = (float) width / 1334f;
        float scaleX = (float) width / 2001f;
        float scaleY = (float) height / 1089f;
        logger.d("decorateItemPagerView:scale=" + scale + ",scale2=" + scaleX + ",scaleY=" + scaleY);
        int backLeft = (ScreenUtils.getScreenWidth() - width) / 2;
        int backTop = (ScreenUtils.getScreenHeight() - height) / 2;
        {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) rl_livevideo_primary_team_content.getLayoutParams();
            int lpwidth = (int) (188 * scale);
            int lpheight = (int) (72 * scale);
            int leftMargin = backLeft + (int) (1124 * scale);
            int topMargin = backTop + (int) (0 * scale);
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
            int lpwidth = (int) (live_primary_right_head_width * scaleX);
            int lpheight = (int) (936 * scaleX);
            int margin = (int) (live_primary_right_head_gap * scaleX);
            int leftMargin = backLeft + (int) (1120 * scale);
            int topMargin = backTop + (int) (131 * scaleX);
            if (lp.width != lpwidth || lp.height != lpheight || lp.leftMargin != leftMargin || lp.topMargin != topMargin) {
                lp.width = lpwidth;
                lp.height = lpheight;
                lp.leftMargin = leftMargin;
                lp.topMargin = topMargin;
                LayoutParamsUtil.setViewLayoutParams(ll_livevideo_primary_team_content, lp);
                for (int i = 0; i < ll_livevideo_primary_team_content.getChildCount(); i++) {
                    View child = ll_livevideo_primary_team_content.getChildAt(i);
                    ViewGroup.MarginLayoutParams childLp = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
                    int childHeight = (int) (live_primary_right_item_height * scaleX);
                    if (childLp.height != childHeight || childLp.bottomMargin != margin) {
                        childLp.height = childHeight;
                        childLp.bottomMargin = margin;
                        LayoutParamsUtil.setViewLayoutParams(child, childLp);
                    }
                }
            }
        }
        setPkMid(tv_livevideo_primary_team_name_mid, width, height);
    }

    private void setPkMid(TextView tv_livevideo_primary_team_name_mid, int width, int height) {
        float scale = (float) width / 1334f;
        int leftGap = (ScreenUtils.getScreenWidth() - width) / 2;
        int backTop = (ScreenUtils.getScreenHeight() - height) / 2;
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tv_livevideo_primary_team_name_mid.getLayoutParams();
        int topMargin = backTop / 2 + (int) (114 * scale);
        Bitmap bitmapDrawable = DrawableHelper.bitmapFromResource(tv_livevideo_primary_team_name_mid.getResources(), R.drawable.bg_live_tips_bg_normal);
        int left = (int) (14 * scale) + leftGap;
        int right = (int) (219 * scale) + leftGap;
        int leftMargin = left + (ScreenUtils.getScreenWidth() - left - right - bitmapDrawable.getWidth()) / 2;
        if (lp.topMargin != topMargin || lp.leftMargin != leftMargin) {
            lp.topMargin = topMargin;
            lp.leftMargin = leftMargin;
            logger.d("setPkMid:leftMargin=" + leftMargin);
            LayoutParamsUtil.setViewLayoutParams(tv_livevideo_primary_team_name_mid, lp);
        }
    }

    @Override
    public void decorateItemPeople(View view) {
        ImageView iv_livevideo_primary_team_voice_open = view.findViewById(R.id.iv_livevideo_primary_team_voice_open);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) iv_livevideo_primary_team_voice_open.getLayoutParams();
        lp.bottomMargin = SizeUtils.Dp2Px(view.getContext(), -2);
        LayoutParamsUtil.setViewLayoutParams(iv_livevideo_primary_team_voice_open, lp);
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
        RelativeLayout rl_livevideo_course_item_video_nocamera = view.findViewById(R.id.rl_livevideo_course_item_video_nocamera);
        rl_livevideo_course_item_video_nocamera.setBackgroundResource(R.drawable.shape_primary_item_empty_back_cn);
        ImageView iv_livevideo_course_item_video_nocamera = view.findViewById(R.id.iv_livevideo_course_item_video_nocamera);
        iv_livevideo_course_item_video_nocamera.setImageResource(R.drawable.bg_live_xuesheng_shipin_mid_carmer_normal_cn);
    }

    /** {@link R.layout#item_primary_class_team_other_video } */
    @Override
    public void decorateItemOther(View view, int index) {
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
