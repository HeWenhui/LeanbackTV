package com.xueersi.parentsmeeting.modules.livevideo.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoFragment;
import com.xueersi.parentsmeeting.modules.livevideo.business.HalfBodyLiveVideoAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.PrimaryClassLiveVideoAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.StandLiveVideoAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.widget.PrimaryClassLiveMediaCtrlTop;

/**
 * Created by linyuqiang on 2018/7/13.
 * 小版体验
 */
public class PrimaryClassVideoFragment extends LiveVideoFragment {
    private String TAG = "PrimaryClassVideoFragment";
    Logger logger = LoggerFactory.getLogger(TAG);

    public PrimaryClassVideoFragment() {
        mLayoutVideo = R.layout.activity_video_live_primary_class;
    }

    @Override
    protected void createLiveVideoAction() {
        liveVideoAction = new PrimaryClassLiveVideoAction(activity, mLiveBll, mContentView, mode);
    }

    @Override
    protected void createMediaControlerTop() {
        baseLiveMediaControllerTop = new PrimaryClassLiveMediaCtrlTop(activity, mMediaController, videoFragment);
    }

    @Override
    protected void initView() {
        super.initView();
        ImageView iv_live_primary_class_kuangjia_img_normal = mContentView.findViewById(R.id.iv_live_primary_class_kuangjia_img_normal);
        Bitmap bitmap = ((BitmapDrawable) iv_live_primary_class_kuangjia_img_normal.getDrawable()).getBitmap();
        {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) iv_live_primary_class_kuangjia_img_normal.getLayoutParams();
//            lp.height = bitmap.getHeight();
            lp.width = bitmap.getHeight() / ScreenUtils.getScreenHeight() * bitmap.getWidth();
            iv_live_primary_class_kuangjia_img_normal.setLayoutParams(lp);
        }
        {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) rlContent.getLayoutParams();
            float scale = (float) bitmap.getWidth() / 1328f;
            lp.leftMargin = (int) (18 * scale);
            lp.bottomMargin = (int) (13 * scale);
            lp.rightMargin = (int) (219 * scale);
            lp.topMargin = (int) (96 * scale);
            rlContent.setLayoutParams(lp);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
