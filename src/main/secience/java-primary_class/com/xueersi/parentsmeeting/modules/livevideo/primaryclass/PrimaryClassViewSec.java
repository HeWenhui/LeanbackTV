package com.xueersi.parentsmeeting.modules.livevideo.primaryclass;

import android.content.Context;

import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.weight.PrimaryKuangjiaImageView;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

public class PrimaryClassViewSec implements PrimaryClassView {
    private Context context;
    private Logger logger = LiveLoggerFactory.getLogger(this);
    private Bitmap lastDrawBitmap;
    private int live_primary_right_head_width;
    private int live_primary_right_head_gap;
    private int live_primary_right_item_height;

    public PrimaryClassViewSec(Context context) {
        this.context = context;
        ProxUtil.getProxUtil().put(context, PrimaryClassView.class, this);
        Resources resources = context.getResources();
        live_primary_right_head_width = resources.getInteger(R.integer.live_primary_right_head_width);
        live_primary_right_head_gap = resources.getInteger(R.integer.live_primary_right_head_gap);
        live_primary_right_item_height = resources.getInteger(R.integer.live_primary_right_item_height);
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
        imageView.setImageResource(R.drawable.bg_live_primary_class_kuangjia_img_normal);
    }

    @Override
    public void decorateBack(int width, int height, RelativeLayout rl_course_video_contentview) {
        Bitmap oldBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg_livevideo_priclass_normal);
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
        view.setBackgroundColor(0xff222222);
        ImageView imageView = view.findViewById(R.id.iv_course_video_novideo);
        imageView.setImageResource(R.drawable.bg_livevideo_paly_novidwo_bg);
    }

    @Override
    public void decorateRlContent(View rlContent, int width, int height) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) rlContent.getLayoutParams();
        float scale = (float) width / 1334f;
        float scaleX = (float) width / 2001f;
        LiveVideoPoint videoPoint = LiveVideoPoint.getInstance();
        int leftGap = (videoPoint.screenWidth - width) / 2;
        int topGap = (videoPoint.screenHeight - height) / 2;
        int leftMargin = (int) (14 * scale) + leftGap;
        int bottomMargin = (int) (13 * scale) + topGap;
        int rightMargin = (int) (219 * scale) + leftGap;
        int topMargin = (int) (159 * scaleX) + topGap;
        if (lp.leftMargin != leftMargin || lp.bottomMargin != bottomMargin || lp.rightMargin != rightMargin || lp.topMargin != topMargin) {
            lp.leftMargin = leftMargin;
            lp.bottomMargin = bottomMargin;
            lp.rightMargin = rightMargin;
            lp.topMargin = topMargin;
            LayoutParamsUtil.setViewLayoutParams(rlContent, lp);
        }
//        lp.leftMargin = (int) (13 * scale);
//        lp.bottomMargin = (int) (13 * scale);
//        lp.rightMargin = (int) (219 * scale);
//        lp.topMargin = (int) (96 * scale);
//        rlContent.setLayoutParams(lp);
    }

    @Override
    public void decorateItemPager(View view, PrimaryKuangjiaImageView ivLivePrimaryClassKuangjiaImgNormal) {
        TextView tv_livevideo_primary_team_name_mid = view.findViewById(R.id.tv_livevideo_primary_team_name_mid);
        tv_livevideo_primary_team_name_mid.setBackgroundResource(R.drawable.bg_live_tips_bg_normal);
//        setPkMid(tv_livevideo_primary_team_name_mid, ivLivePrimaryClassKuangjiaImgNormal.getWidth(), ivLivePrimaryClassKuangjiaImgNormal.getHeight());
    }

    @Override
    public void decorateItemPagerView(RelativeLayout rl_livevideo_primary_team_content, ImageView iv_livevideo_primary_team_icon, LinearLayout ll_livevideo_primary_team_content, TextView tv_livevideo_primary_team_name_mid, int width, int height) {
        float scale = (float) width / 1334f;
        float scaleX = (float) width / 2001f;
        float scaleY = (float) height / 1107f;
        logger.d("decorateItemPagerView:scale=" + scale + ",scale2=" + scaleX + ",scaleY=" + scaleY);
        int backLeft = (ScreenUtils.getScreenWidth() - width) / 2;
        int backTop = (ScreenUtils.getScreenHeight() - height) / 2;
        {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) rl_livevideo_primary_team_content.getLayoutParams();
            int lpwidth = (int) (198 * scale);
            int lpheight = (int) (54 * scale);
            int leftMargin = backLeft + (int) (1124 * scale);
            int topMargin = backTop + (int) (23 * scale);
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
            int leftMargin = backLeft + (int) (1126 * scale);
            int topMargin = backTop + (int) (101 * scale);
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
        Bitmap bitmapDrawable = BitmapFactory.decodeResource(tv_livevideo_primary_team_name_mid.getResources(), R.drawable.bg_live_tips_bg_normal);
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

    }

    @Override
    public void decorateItemMy(View view) {

    }

    @Override
    public void decorateItemOther(View view, int index) {
        if (index == 3) {
            View iv_livevideo_primary_team_voice_open = view.findViewById(R.id.iv_livevideo_primary_team_voice_open);
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) iv_livevideo_primary_team_voice_open.getLayoutParams();
            logger.d("initViews:bottomMargin=" + lp.bottomMargin);
            lp.bottomMargin += 9;
            LayoutParamsUtil.setViewLayoutParams(iv_livevideo_primary_team_voice_open, lp);
        }
    }

    @Override
    public void decorateItemEmpty(View view) {

    }

    @Override
    public void decorateItemBack(View view) {

    }

    @Override
    public void decorateItemMyAddEnergy(View view) {

    }

}
