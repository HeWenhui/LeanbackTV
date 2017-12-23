package com.xueersi.parentsmeeting.modules.livevideo.page;

import android.content.Context;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.base.BasePager;
import com.xueersi.parentsmeeting.http.ResponseEntity;
import com.xueersi.parentsmeeting.http.HttpCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.business.RollCallAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassSignEntity;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveVideoFloatTitle;

import java.io.File;

/**
 * @author linyuqiang 签到
 */
public class ClassSignPager extends BasePager {
    String TAG = "ClassSignPager";
    RollCallAction rollCallAction;
    LiveBll liveBll;
    RelativeLayout rlSignStatus1, rlSignStatus2;
    TextView tvSignName;
    /** 查看评价，查看按钮 */
    Button btLearnreportCheck;
    ImageView ivSignStatus;
    TextView tvSignStatus;
    ClassSignEntity classSignEntity;
    /** 点名按钮提示，0-准时签到，1-签到成功，2-签到失败(在签到的时候，老师点结束签到) */
    String[] bttips = {"签到成功", "签到失败"};
    private LogToFile logToFile;

    public ClassSignPager(Context context, RollCallAction rollCallAction, ClassSignEntity classSignEntity, LiveBll liveBll) {
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
        mView = View.inflate(mContext, R.layout.page_livevodeo_sign, null);
        rlSignStatus1 = (RelativeLayout) mView.findViewById(R.id.rl_livevideo_sign_status1);
        rlSignStatus2 = (RelativeLayout) mView.findViewById(R.id.rl_livevideo_sign_status2);
        tvSignName = (TextView) mView.findViewById(R.id.tv_livevideo_sign_name);
        btLearnreportCheck = (Button) mView.findViewById(R.id.bt_livevideo_learnreport_check);
        ivSignStatus = (ImageView) mView.findViewById(R.id.iv_livevideo_sign_status);
        tvSignStatus = (TextView) mView.findViewById(R.id.tv_livevideo_sign_status);
        return mView;
    }

    @Override
    public void initData() {
        tvSignName.setText(String.format("%s 你好,", classSignEntity.getStuName()));
        updateStatus(classSignEntity.getStatus());
        ((LiveVideoFloatTitle) mView.findViewById(R.id.lrf_livevideo_sign_title)).setOnCancleClick(new LiveVideoFloatTitle.OnCancleClick() {
            @Override
            public void onCancleClick() {
                rollCallAction.stopRollCall();
            }
        });
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
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        rollCallAction.stopRollCall();
                        logToFile.d("onPmError:responseEntity=" + responseEntity);
                    }
                });
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
            rlSignStatus1.setVisibility(View.VISIBLE);
            rlSignStatus2.setVisibility(View.GONE);
            return;
        } else if (status == 2) {
            tvSignStatus.setText(bttips[0]);
            ivSignStatus.setImageResource(R.drawable.bg_livevideo_sign_suc);
        } else {
            tvSignStatus.setText(bttips[1]);
            ivSignStatus.setImageResource(R.drawable.bg_web_request_error);
        }
        rlSignStatus1.setVisibility(View.GONE);
        rlSignStatus2.setVisibility(View.VISIBLE);
    }
}
