package com.xueersi.parentsmeeting.modules.livevideo.primaryclass;

import android.content.Context;

import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;

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
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

public class PrimaryClassViewSec implements PrimaryClassView {
    private Context context;
    private Logger logger = LiveLoggerFactory.getLogger(this);
    private Bitmap lastDrawBitmap;

    public PrimaryClassViewSec(Context context) {
        this.context = context;
        ProxUtil.getProxUtil().put(context, PrimaryClassView.class, this);
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
        int left = (oldBitmap.getWidth() - width - (screenWidth - width) / 2) / 2;
        int top = (oldBitmap.getHeight() - height - (screenHeight - height) / 2) / 2;
        if (left < 0) {
            left = 0;
        }
        if (top < 0) {
            top = 0;
        }
        int right = left;
        int bottom = top;
        int width2 = oldBitmap.getWidth() - left - right;
        int height2 = oldBitmap.getHeight() - top - bottom;
        logger.d("decorateBack:left=" + left + ",top=" + top + ",width2=" + width2 + ",height2=" + height2);
        if (width2 < 1 || height2 < 1) {
            rl_course_video_contentview.setBackground(new BitmapDrawable(context.getResources(), oldBitmap));
            return;
        }
        Bitmap drawBitmap = Bitmap.createBitmap(oldBitmap, left, top, width2, height2);
        oldBitmap.recycle();
        if (lastDrawBitmap != null) {
            lastDrawBitmap.recycle();
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

        LiveVideoPoint videoPoint = LiveVideoPoint.getInstance();
        int leftGap = (videoPoint.screenWidth - width) / 2;
        int topGap = (videoPoint.screenHeight - height) / 2;
        int leftMargin = (int) (13 * scale) + leftGap;
        int bottomMargin = (int) (13 * scale) + topGap;
        int rightMargin = (int) (219 * scale) + leftGap;
        int topMargin = (int) (96 * scale) + topGap;
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
    public void decorateItemPager(View view) {
        TextView tv_livevideo_primary_team_name_mid = view.findViewById(R.id.tv_livevideo_primary_team_name_mid);
        tv_livevideo_primary_team_name_mid.setBackgroundResource(R.drawable.bg_live_tips_bg_normal);
    }

    @Override
    public void decorateItemPagerView(RelativeLayout rl_livevideo_primary_team_content, ImageView iv_livevideo_primary_team_icon, LinearLayout ll_livevideo_primary_team_content, TextView tv_livevideo_primary_team_name_mid, int width, int height) {
        float scale = (float) width / 1334f;
        {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) rl_livevideo_primary_team_content.getLayoutParams();
            int lpwidth = (int) (193 * scale);
            int lpheight = (int) (54 * scale);
            int leftMargin = (ScreenUtils.getScreenWidth() - width) / 2 + (int) (1124 * scale);
            int topMargin = (ScreenUtils.getScreenHeight() - height) / 2 + (int) (24 * scale);
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
            int leftMargin = (ScreenUtils.getScreenWidth() - width) / 2 + (int) (1126 * scale);
            int topMargin = (ScreenUtils.getScreenHeight() - height) / 2 + (int) (101 * scale);
            if (lp.width != lpwidth || lp.height != lpheight || lp.leftMargin != leftMargin || lp.topMargin != topMargin) {
                lp.width = lpwidth;
                lp.height = lpheight;
                lp.leftMargin = leftMargin;
                lp.topMargin = topMargin;
                LayoutParamsUtil.setViewLayoutParams(ll_livevideo_primary_team_content, lp);
                for (int i = 0; i < ll_livevideo_primary_team_content.getChildCount(); i++) {
                    View child = ll_livevideo_primary_team_content.getChildAt(i);
                    ViewGroup.MarginLayoutParams childLp = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
                    int childHeight = (int) (149 * scale);
                    int margin = (int) (10 * scale);
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

    @Override
    public void decorateItemMy(View view) {

    }

    @Override
    public void decorateItemOther(View view) {

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
