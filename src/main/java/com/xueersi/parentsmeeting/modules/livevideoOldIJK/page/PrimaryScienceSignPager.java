package com.xueersi.parentsmeeting.modules.livevideoOldIJK.page;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xueersi.common.base.BasePager;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassSignEntity;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.rollcall.business.RollCallBll;

/**
 * Created by David on 2018/7/9.
 */

public class PrimaryScienceSignPager extends BasePager {
    String TAG = "PrimaryScienceSignPager";
    RollCallBll rollCallBll;
    RelativeLayout rlSignStatus1, rlSignStatus2;
    LinearLayout mLinearLayout;
    TextView tvSignName;
    /** 查看评价，查看按钮 */
    Button btLearnreportCheck;
    ImageView ivSignSuccess, ivSignFail, ivClose;
    TextView tvSignStatus;
    ClassSignEntity classSignEntity;
    /** 点名按钮提示，0-准时签到，1-签到成功，2-签到失败(在签到的时候，老师点结束签到) */
    String[] bttips = {"签到成功", "签到失败"};
    private LogToFile logToFile;

    public PrimaryScienceSignPager(Context context, RollCallBll rollCallBll, ClassSignEntity classSignEntity) {
        super(context);
        this.classSignEntity = classSignEntity;
        this.rollCallBll = rollCallBll;
        logToFile = new LogToFile(context, TAG);
        initData();
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.page_livevideo_primary_sign, null);
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
                if (classSignEntity.getStatus() != 1) {
                    logToFile.d("stopRollCall");
                    rollCallBll.stopRollCall();
                    return;
                }
                rollCallBll.userSign(classSignEntity, new HttpCallBack() {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) {
                        logToFile.d("onPmSuccess:responseEntity=" + responseEntity.getJsonObject().toString());
                        updateStatus(2);
                    }

                    @Override
                    public void onPmFailure(Throwable error, String msg) {
                        logToFile.e("onPmFailure:msg=" + msg, error);
                        Toast.makeText(mContext, TextUtils.isEmpty(msg) ? "网络异常" : msg, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        // rollCallAction.stopRollCall();
                        String errorMsg = TextUtils.isEmpty(responseEntity.getErrorMsg()) ? "网络异常" : responseEntity.getErrorMsg();
                        Toast.makeText(mContext, errorMsg, Toast.LENGTH_SHORT).show();
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
        classSignEntity.setStatus(status);
        if (status == 1) {
            mLinearLayout.setVisibility(View.VISIBLE);
            btLearnreportCheck.setVisibility(View.VISIBLE);
            ivSignSuccess.setVisibility(View.GONE);
            ivSignFail.setVisibility(View.GONE);
            return;
        } else if (status == 2) {
            ivSignSuccess.setVisibility(View.VISIBLE);
            ivSignFail.setVisibility(View.GONE);
        } else {
            ivSignSuccess.setVisibility(View.GONE);
            ivSignFail.setVisibility(View.VISIBLE);
        }
        mLinearLayout.setVisibility(View.GONE);
        btLearnreportCheck.setVisibility(View.GONE);
        ivClose.setVisibility(View.GONE);
        mView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mView.setVisibility(View.GONE);
            }
        },3000);
    }

}
