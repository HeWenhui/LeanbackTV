package com.xueersi.parentsmeeting.modules.livevideo.goldmicrophone;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.view.View;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;

import org.json.JSONObject;

public class PhoneBll extends LiveBaseBll implements NoticeAction, GoldPhoneContract.GoldPhonePresenter {

    GoldPhoneContract.GoldPhoneView mGoldView;

    public PhoneBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
    }

    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {
        switch (type) {
            case XESCODE.ARTS_GOLD_MICROPHONE: {
                int open = data.optInt("open");
                if (open == 1) {
                    String sign = data.optString("sign");
                    showVolumeView();
                    startAudioRecord();
                    getIsOnlineRecognize(sign);
                    showGoldSettingView(isHasAudioPermission());
                } else {
                    //提示关闭语音弹幕
                    if (mGoldView != null) {
                        mGoldView.showCloseView();
                        postDelayedIfNotFinish(new Runnable() {
                            @Override
                            public void run() {
                                if (mGoldView.getRootView().getParent() == mRootView) {
                                    mRootView.removeView(mGoldView.getRootView());
                                }
                            }
                        }, 700);
                    }
                }
                break;
            }
            default:
                break;
        }
    }

    @Override
    public int[] getNoticeFilter() {
        return new int[]{XESCODE.ARTS_GOLD_MICROPHONE};
    }

    public void getIsOnlineRecognize(String sign) {
        getHttpManager().getIsOnlineRecognize(mGetInfo.getId(), sign, new HttpCallBack() {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                int isOnline = 0;
                if (responseEntity.getJsonObject() instanceof JSONObject) {
                    JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                    isOnline = jsonObject.optInt("isGoldMicrophoneToAi");
                }
                if (isOnline == 1) {

                }
            }
        });
    }

    /**
     * 检查是否有权限
     */
    private void showGoldSettingView(boolean isShow) {
        if (!isShow) {
            mGoldView.showSettingView(true);
        } else {
            mGoldView.showSettingView(false);
        }
    }

    /**
     * 是否有语音权限
     *
     * @return
     */
    private boolean isHasAudioPermission() {
        PackageManager pkm = mContext.getPackageManager();
        return (PackageManager.PERMISSION_GRANTED == pkm.checkPermission("android.permission.MODIFY_AUDIO_SETTINGS", mContext.getPackageName())
                && PackageManager.PERMISSION_GRANTED == pkm.checkPermission("android.permission.RECORD_AUDIO", mContext.getPackageName()));
    }

    private void showVolumeView() {
        if (mGoldView == null) {
            mGoldView = new PhoneView(mContext, this);
        }
        mRootView.addView(mGoldView.getRootView());
    }

    public void sendIsGoldMicroPhone(String isOpenMicrophone, String isGoldMicrophone, String sign) {
        getHttpManager().sendIsGoldPhone(mGetInfo.getId(),
                isOpenMicrophone,
                isGoldMicrophone, sign, new HttpCallBack() {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        logger.i("gold microphone send success");
                    }
                });
    }

    private void startAudioRecord() {

    }

    @Override
    public void remove(View view) {
        if (view.getParent() == mRootView) {
            mRootView.removeView(view);
        }
    }
}
