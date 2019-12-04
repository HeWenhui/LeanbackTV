package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.hwl.bury.xrsbury.XrsBury;
import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController;
import com.xueersi.parentsmeeting.modules.livevideo.R;

/**
 * @ProjectName: xueersiwangxiao
 * @Package: com.xueersi.parentsmeeting.modules.livevideo.widget
 * @ClassName: LightLiveMediaControllerTop
 * @Description: 轻直播顶部标题栏
 * @Author: WangDe
 * @CreateDate: 2019/11/22 11:03
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/11/22 11:03
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class LightLiveMediaControllerTop extends BaseLiveMediaControllerTop{

    public LightLiveMediaControllerTop(Context context, LiveMediaController controller, LiveMediaController.MediaPlayerControl mPlayer) {
        super(context, controller, mPlayer);

    }

    @Override
    protected View inflateLayout() {
        mAllViewClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayer.changeLOrP(); // 切换横竖屏
                XrsBury.clickBury(mContext.getResources().getString(R.string.livevideo_click_03_54_012));
            }
        };
        return LayoutInflater.from(mContext).inflate(R.layout.layout_lightlivemediacontroller_top, this);

    }



}
