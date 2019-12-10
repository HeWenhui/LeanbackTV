package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveUIStateListener;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveUIStateReg;
import com.xueersi.parentsmeeting.modules.livevideo.config.HalfBodyLiveConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;

import java.util.ArrayList;

/**
 * @ProjectName: xueersiwangxiao
 * @Package: com.xueersi.parentsmeeting.modules.livevideo.widget
 * @ClassName: LightLiveMediaControllerBottom
 * @Description: 轻直播底部栏
 * @Author: WangDe
 * @CreateDate: 2019/11/21 11:36
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/11/21 11:36
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class LightLiveMediaControllerBottom extends BaseLiveMediaControllerBottom {

     /**
     * 显示隐藏回调监听器
     */
   ControllerStateListener controllerStateListener;

    /**
     * 是否拦截 顶部控制栏自动隐藏
     */
    boolean interceptBtmMediaCtrHide;

    public LightLiveMediaControllerBottom(Context context, LiveMediaController controller, LiveMediaController
            .MediaPlayerControl player) {
        super(context, controller, player);
    }


    @Override
    public View inflateLayout() {
        //在得到 详细的直播间初始化参数之前 返回默认布局信息
        return LayoutInflater.from(mContext).inflate(R.layout.layout_livehalfbody_mediacontroller_bottom_ch,
                this);

    }

    @Override
    public void onShow() {
        if (!interceptBtmMediaCtrHide) {
            super.onShow();
        }

        if (controllerStateListener != null) {
            controllerStateListener.onSHow();
        }
    }

    @Override
    public void onHide() {
        if (!interceptBtmMediaCtrHide) {
            super.onHide();
        }

        if (controllerStateListener != null) {
            controllerStateListener.onHide();
        }
    }


    /**
     * 拦截 显示隐藏 动画
     * @param intercept
     */
    public void interceptHideBtmMediaCtr(boolean intercept) {
        interceptBtmMediaCtrHide = intercept;
    }


    public void setControllerStateListener(ControllerStateListener controllerStateListener) {
        this.controllerStateListener = controllerStateListener;
    }

    public interface ControllerStateListener {
        /**
         * 状态栏显示
         */
        void onSHow();

        /**
         * 状态栏隐藏
         */
        void onHide();
    }

}
