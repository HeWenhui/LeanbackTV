package com.xueersi.parentsmeeting.modules.livevideo.primaryclass.pager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.weight.PrimaryKuangjiaImageView;

public class PrimaryItemPager extends LiveBasePager implements PrimaryItemView {
    private LinearLayout ll_livevideo_primary_team_content;
    private TextView tv_livevideo_primary_team_name;
    private RelativeLayout mContentView;
    private PrimaryKuangjiaImageView ivLivePrimaryClassKuangjiaImgNormal;

    public PrimaryItemPager(Context context, RelativeLayout mContentView) {
        super(context);
        this.mContentView = mContentView;
        initData();
    }

    @Override
    public View initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.pager_primary_class_team, null);
        ll_livevideo_primary_team_content = view.findViewById(R.id.ll_livevideo_primary_team_content);
        tv_livevideo_primary_team_name = view.findViewById(R.id.tv_livevideo_primary_team_name);
        return view;
    }

    private void setLayout() {
        setImageViewWidth();
    }

    public void setImageViewWidth() {
        ivLivePrimaryClassKuangjiaImgNormal.addSizeChange(new PrimaryKuangjiaImageView.OnSizeChange() {
            @Override
            public void onSizeChange(int width, int height) {
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tv_livevideo_primary_team_name.getLayoutParams();
                float scale = (float) width / 1334f;
                int lpwidth = (int) (191 * scale);
                int lpheight = (int) (54 * scale);
                int leftMargin = (ScreenUtils.getScreenWidth() - width) / 2 + (int) (1124 * scale);
                int topMargin = (ScreenUtils.getScreenHeight() - height) / 2 + (int) (26 * scale);
                if (lp.width != lpwidth || lp.height != lpheight || lp.leftMargin != leftMargin || lp.topMargin != topMargin) {
                    lp.width = lpwidth;
                    lp.height = lpheight;
                    lp.leftMargin = leftMargin;
                    lp.topMargin = topMargin;
                    tv_livevideo_primary_team_name.setLayoutParams(lp);
                }
            }
        });
    }

    @Override
    public void initData() {
        super.initData();
        ivLivePrimaryClassKuangjiaImgNormal = mContentView.findViewById(R.id.iv_live_primary_class_kuangjia_img_normal);
        setLayout();
    }
}
