package com.xueersi.parentsmeeting.modules.livevideo.goldmicrophone;

import android.app.Activity;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;

import org.json.JSONObject;

public class PhoneBll extends LiveBaseBll implements NoticeAction {

    GoldPhoneContract.GoldPhoneView mView;

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
                    getIsOnlineRecognize(sign);
                } else {

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
                    showOpenView();
                    showVolumeView();
                    startAudioRecord();
                }
            }
        });
    }

    private void showVolumeView() {
        if (mView == null) {
            mView = new PhoneView(mContext);
        }
        mRootView.addView(mView.getRootView());

    }

    private void showOpenView() {

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

}
