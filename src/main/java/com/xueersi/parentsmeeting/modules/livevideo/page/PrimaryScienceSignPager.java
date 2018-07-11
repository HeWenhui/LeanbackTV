package com.xueersi.parentsmeeting.modules.livevideo.page;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sobot.chat.utils.ToastUtil;
import com.xueersi.parentsmeeting.base.BasePager;
import com.xueersi.parentsmeeting.http.HttpCallBack;
import com.xueersi.parentsmeeting.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.business.RollCallAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassSignEntity;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveVideoFloatTitle;

import java.io.File;

/**
 * Created by David on 2018/7/9.
 */

public class PrimaryScienceSignPager extends BasePager {
    String TAG = "PrimaryScienceSignPager";
    RollCallAction rollCallAction;
    LiveBll liveBll;
    RelativeLayout rlSignStatus1, rlSignStatus2;
    LinearLayout mLinearLayout;
    TextView tvSignName;
    /** 查看评价，查看按钮 */
    Button btLearnreportCheck;
    ImageView ivSignSuccess,ivSignFail,ivClose;
    TextView tvSignStatus;
    ClassSignEntity classSignEntity;
    /** 点名按钮提示，0-准时签到，1-签到成功，2-签到失败(在签到的时候，老师点结束签到) */
    String[] bttips = {"签到成功", "签到失败"};
    private LogToFile logToFile;

    public PrimaryScienceSignPager(Context context, RollCallAction rollCallAction, ClassSignEntity classSignEntity, LiveBll liveBll) {
        super(context);
        this.rollCallAction = rollCallAction;
        this.classSignEntity = classSignEntity;
        this.liveBll = liveBll;
        logToFile = new LogToFile(TAG, new File(Environment.getExternalStorageDirectory(), "parentsmeeting/log/" + TAG
                + ".txt"));
        initData();
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.page_livevideo_primary_sign, null);
//        rlSignStatus1 = (RelativeLayout) mView.findViewById(R.id.rl_livevideo_sign_status1);
//        rlSignStatus2 = (RelativeLayout) mView.findViewById(R.id.rl_livevideo_sign_status2);
//        tvSignName = (TextView) mView.findViewById(R.id.tv_livevideo_sign_name);
//        btLearnreportCheck = (Button) mView.findViewById(R.id.bt_livevideo_learnreport_check);
//        ivSignStatus = (ImageView) mView.findViewById(R.id.iv_livevideo_sign_status);
//        tvSignStatus = (TextView) mView.findViewById(R.id.tv_livevideo_sign_status);
        mLinearLayout = (LinearLayout) mView.findViewById(R.id.ll_start_sign);
        tvSignName = (TextView) mView.findViewById(R.id.tv_sign_name);
        btLearnreportCheck = (Button) mView.findViewById(R.id.bt_primary_sign);
        ivClose = (ImageView) mView.findViewById(R.id.iv_livevideo_primarysign_close);
        ivSignSuccess = (ImageView) mView.findViewById(R.id.iv_sign_success);
        ivSignFail = (ImageView) mView.findViewById(R.id.iv_sign_fail);
        return mView;
    }

    @Override
    public void initData() {
        tvSignName.setText(String.format("%s 你好", classSignEntity.getStuName()));
        updateStatus(classSignEntity.getStatus());
        btLearnreportCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //liveBll.onRollCallSuccess();
                if (classSignEntity.getStatus() != 1) {
                    logToFile.d("stopRollCall");
                    rollCallAction.stopRollCall();
                    return;
                }
                liveBll.userSign(new HttpCallBack() {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) {
                        logToFile.d("onPmSuccess:responseEntity=" + responseEntity.getJsonObject().toString());
                        updateStatus(2);
                        liveBll.onRollCallSuccess();
                    }

                    @Override
                    public void onPmFailure(Throwable error, String msg) {
                        logToFile.e("onPmFailure:msg=" + msg, error);
                        ToastUtil.showToast(mContext, TextUtils.isEmpty(msg)?"网络异常":msg);
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        // rollCallAction.stopRollCall();
                        String errorMsg = TextUtils.isEmpty(responseEntity.getErrorMsg())?"网络异常":responseEntity.getErrorMsg();
                        ToastUtil.showToast(mContext,errorMsg);
                    }
                });
            }
        });
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mView.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 更新签到状态
     *
     * @param status
     */
    public void updateStatus(int status) {
//        classSignEntity.setStatus(status);
//        if (status == 1) {
//            rlSignStatus1.setVisibility(View.VISIBLE);
//            rlSignStatus2.setVisibility(View.GONE);
//            return;
//        } else if (status == 2) {
//            tvSignStatus.setText(bttips[0]);
//            ivSignStatus.setImageResource(R.drawable.bg_livevideo_sign_suc);
//        } else {
//            tvSignStatus.setText(bttips[1]);
//            ivSignStatus.setImageResource(R.drawable.bg_web_request_error);
//        }
//        rlSignStatus1.setVisibility(View.GONE);
//        rlSignStatus2.setVisibility(View.VISIBLE);
        classSignEntity.setStatus(status);
        if(status == 1){
            mLinearLayout.setVisibility(View.VISIBLE);
            btLearnreportCheck.setVisibility(View.VISIBLE);
            ivSignSuccess.setVisibility(View.GONE);
            ivSignFail.setVisibility(View.GONE);
            return;
        }else if(status == 2){
            ivSignSuccess.setVisibility(View.VISIBLE);
            ivSignFail.setVisibility(View.GONE);
        } else {
            ivSignSuccess.setVisibility(View.GONE);
            ivSignFail.setVisibility(View.VISIBLE);
        }
        mLinearLayout.setVisibility(View.GONE);
        btLearnreportCheck.setVisibility(View.GONE);
    }

}
