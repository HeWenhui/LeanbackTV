package com.xueersi.parentsmeeting.modules.livevideo.subscribecourse;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.common.business.AppBll;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.toast.XesCenterToast;
import com.xueersi.common.toast.XesToast;
import com.xueersi.common.util.LoginEnter;
import com.xueersi.lib.framework.utils.EventBusUtil;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoLevel;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.util.GlideDrawableUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.share.business.login.LoginActionEvent;
import com.xueersi.ui.dialog.ConfirmAlertDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

/**
 * @ProjectName: xueersiwangxiao
 * @Package: com.xueersi.parentsmeeting.modules.livevideo.subscribecourse
 * @ClassName: SubscribeCourseBll
 * @Description: 讲座预约所以系列讲座业务
 * @Author: WangDe
 * @CreateDate: 2020/02/07
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/02/07
 * @UpdateRemark:
 * @Version: 1.0
 */
public class SubscribeCourseBll extends LiveBaseBll implements ISubscribeClickListener{

    private SubscribeCoursePager mSubCoursePager;
    private RelativeLayout.LayoutParams params;
    private String seriesLectureName;
    /** 系列讲座ID*/
    private int seriesLectureId;
    /** 是否已预约*/
    private boolean isSeriesLectureSub;
    /** 预约弹窗主标题*/
    private String popupMainTitle;
    /** 预约弹窗副标题*/
    private String popupSubTitle;
    /** 预约弹窗*/
    private ConfirmAlertDialog subscribeDialog;
    /** 已预约弹窗*/
    private ConfirmAlertDialog hasSubscribeDialog;
    /** 弹窗中图片地址*/
    private final String URL_PICTURE = "http://xeswxapp.oss-cn-beijing.aliyuncs.com/Android/imges/image_home_guide.png";
    /** 已预约弹窗标题*/
    private String subSuccessTitle;

    private final int IMG_DEFAULT_WIDTH = 320;

    private final int IMG_DEFAULT_HEIGHT = 74;

    public SubscribeCourseBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
        mSubCoursePager = new SubscribeCoursePager(mContext);
        mSubCoursePager.setSubClickListener(this);

        EventBusUtil.register(this);
    }

    @Override
    public void initView() {
        super.initView();
        if (mIsLand.get()){
            getLiveViewAction().removeView(mSubCoursePager.getRootView());
            getLiveViewAction().addView(LiveVideoLevel.LEVEL_CTRl, mSubCoursePager.getRootView(),params);
            setVideoLayout(LiveVideoPoint.getInstance());
        }

    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
//        if (getInfo != null){
//            getInfo.setSeriesLectureId(55);
//            getInfo.setIsSeriesLectureSub(0);
//            getInfo.setPopupMainTitle("是否添加《系列课程名称》至你的课程？");
//            getInfo.setPopupSubTitle("-上课提醒不错过，随时随地看回放-");
//            getInfo.setSubSuccessTitle("《系列课程名称》已添加至你的课程");
//            getInfo.setBubbleSwitch(1);
//        }
        if (getInfo != null && 0 != getInfo.getSeriesLectureId()){
            seriesLectureName = getInfo.getSeriesLectureName();
            seriesLectureId = getInfo.getSeriesLectureId();
            isSeriesLectureSub = getInfo.getIsSeriesLectureSub() == 1;
            popupMainTitle = getInfo.getPopupMainTitle();
            popupSubTitle = getInfo.getPopupSubTitle();
            mSubCoursePager.getRootView().setVisibility(View.VISIBLE);
            subSuccessTitle = getInfo.getSubSuccessTitle();
            if (1 == getInfo.getBubbleSwitch()){
                mSubCoursePager.setTvTipVisible(true);
                postDelayed(tipRunable,5000);
            }else {
                mSubCoursePager.setTvTipVisible(false);
            }
            if (!StringUtils.isEmpty(getInfo.getBubbleText())){
                mSubCoursePager.setTvTip(getInfo.getBubbleText());
            }
            setSubscribeText();

        }else {
            mSubCoursePager.getRootView().setVisibility(View.GONE);
            mLiveBll.removeBusinessBll(this);
        }
    }

    @Override
    public void setVideoLayout(LiveVideoPoint liveVideoPoint) {
        super.setVideoLayout(liveVideoPoint);
        params = (RelativeLayout.LayoutParams) mSubCoursePager.getRootView().getLayoutParams();
        if (params == null){
            params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
                    .WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        }
        int topMargin = liveVideoPoint.y3;
        if (params != null && topMargin != params.topMargin + SizeUtils.Dp2Px(mContext,3)) {
            params.topMargin = topMargin + SizeUtils.Dp2Px(mContext,3);
            params.rightMargin = liveVideoPoint.screenWidth - liveVideoPoint.x4 + SizeUtils.Dp2Px(mContext,8);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//                rlInfo.setLayoutParams(params);
            LayoutParamsUtil.setViewLayoutParams(mSubCoursePager.getRootView(), params);
            logger.d("initView:width=" + liveVideoPoint.getRightMargin() + "," + liveVideoPoint.y3);

        }
    }

    private Runnable tipRunable = new Runnable() {
        @Override
        public void run() {
            if (mSubCoursePager != null){
                mSubCoursePager.setTvTipVisible(false);
            }
        }
    };

    @Override
    public void onClick() {
        initDialog();
        mSubCoursePager.setTvTipVisible(false);
        if (AppBll.getInstance().isAlreadyLogin()){
            if (isSeriesLectureSub){
                hasSubscribeDialog.showDialog();
            }else {
                subscribeDialog.showDialog();
            }
        }else {
            LoginEnter.openLogin(mContext, false, new Bundle());
        }

    }

    private void initDialog(){
        if (subscribeDialog == null){
            subscribeDialog = new ConfirmAlertDialog(mContext,mBaseApplication,false,ConfirmAlertDialog.TITLE_SUBTITLE_IMGMESSAGE_VERIFY_CANCEL_TYPE);
            subscribeDialog.initInfo(popupMainTitle,popupSubTitle,URL_PICTURE, R.drawable.bg_corners_f6f7f8_radius_4_width_380_height_88,IMG_DEFAULT_WIDTH,IMG_DEFAULT_HEIGHT);
            subscribeDialog.setVerifyShowText("立即添加");
            subscribeDialog.setVerifyBtnListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSubCoursePager.setTvSubscribeEnable(false);
                    getHttpManager().subscribSeriesLecture(String.valueOf(seriesLectureId), new HttpCallBack(false) {
                        @Override
                        public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                            mSubCoursePager.setTvSubscribeEnable(true);
                            JSONObject data = (JSONObject)responseEntity.getJsonObject();
                            int status = data.optInt("status");
                            if (status == 1){
                                isSeriesLectureSub = true;
                                setSubscribeText();
                                XesCenterToast.showToast("已成功添加至你的课程");
                            }else {
                                XesCenterToast.showToast("出错了，请重试");
                            }

                        }

                        @Override
                        public void onPmFailure(Throwable error, String msg) {
                            super.onPmFailure(error, msg);
                            mSubCoursePager.setTvSubscribeEnable(true);
                            XesCenterToast.showToast("出错了，请重试");
                        }

                        @Override
                        public void onPmError(ResponseEntity responseEntity) {
                            super.onPmError(responseEntity);
                            mSubCoursePager.setTvSubscribeEnable(true);
                            XesCenterToast.showToast("出错了，请重试");
                        }
                    });
                }
            });
        }
        if (hasSubscribeDialog == null){
            hasSubscribeDialog = new ConfirmAlertDialog(mContext,mBaseApplication,false,ConfirmAlertDialog.TITLE_IMGMESSAGE_VERIFY_TYPE);
            hasSubscribeDialog.initInfo(subSuccessTitle,"",URL_PICTURE, R.drawable.bg_corners_f6f7f8_radius_4_width_380_height_88,
                    IMG_DEFAULT_WIDTH,IMG_DEFAULT_HEIGHT);
            hasSubscribeDialog.setVerifyShowText("好的");
        }

    }

    private void setSubscribeText(){
        if (mSubCoursePager != null ){
            mSubCoursePager.hasSubscribe(isSeriesLectureSub);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAnswerResult(LoginActionEvent event) {
        if (event.isAlreadyLogin()){
            getHttpManager().getIsSubscribe(String.valueOf(seriesLectureId), new HttpCallBack(false) {
                @Override
                public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                    JSONObject data = (JSONObject)responseEntity.getJsonObject();
                    isSeriesLectureSub = data.optInt("isSub") == 1;
                    setSubscribeText();
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBusUtil.unregister(this);
    }
}
