package com.xueersi.parentsmeeting.modules.livevideo.rollcall.page;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.xueersi.common.base.BasePager;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassSignEntity;
import com.xueersi.parentsmeeting.modules.livevideo.rollcall.business.Config;

public class SmallChineseClassSignPager extends BasePager {
    /**
     * 签到状态的按钮，采用字体来显示
     */
    private ImageView ivSignStatus;
    /**
     * 关闭按钮
     */
    private ImageView ivClose;

    private ClassSignEntity classSignEntity;
    /**
     * 签到按钮
     */
    private ImageView ivSign;

    public SmallChineseClassSignPager(Context context, ClassSignEntity classSignEntity) {
        super(context);
        this.classSignEntity = classSignEntity;
        initData();
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_small_chinese_sign, null);
        ivSignStatus = view.findViewById(R.id.bg_livevideo_small_chinses_sign_status);
        ivClose = view.findViewById(R.id.iv_livevideo_small_chinese_sign_close);
        ivSign = view.findViewById(R.id.iv_livevideo_small_chinese_sign_sign);

        return view;
    }

    @Override
    public void initData() {
        updateStatus(classSignEntity.getStatus());
    }

    private void updateStatus(int status) {
        //准备签到
        if (status == Config.SIGN_STATE_CODE_UNSIGN) {

        } else if (status == Config.SIGN_STATE_CODE_SIGNED) {//签到成功

        } else {//签到失败

        }
    }

    @Override
    public void initListener() {
        super.initListener();
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        ivSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sign.sign(mHttpCallBack);
            }
        });
    }

    private HttpCallBack mHttpCallBack = new HttpCallBack() {
        @Override
        public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
//            logToFile.d("onPmSuccess:responseEntity=" + responseEntity.getJsonObject().toString());
            updateStatus(Config.SIGN_STATE_CODE_SIGNED);
        }

        @Override
        public void onPmFailure(Throwable error, String msg) {
//            logToFile.e("onPmFailure:msg=" + msg, error);
            XESToastUtils.showToast(mContext, TextUtils.isEmpty(msg) ? "网络异常" : msg);
        }

        @Override
        public void onPmError(ResponseEntity responseEntity) {
//            logToFile.d("onPmError:msg=" + responseEntity.getErrorMsg());
//            String errorMsg = TextUtils.isEmpty(responseEntity.getErrorMsg()) ? "网络异常" : responseEntity
//                    .getErrorMsg();
//            XESToastUtils.showToast(mContext, errorMsg);
        }
    };

    /**
     * 签到，同小英一样
     */
    public static interface Sign {
        //关闭签到
        void close();

        //签到回调服务器
        void sign(HttpCallBack httpCallBack);

        //当前View是否任然存活
        boolean containsView();
    }

    private Sign sign;


    public void setSign(Sign sign) {
        this.sign = sign;
    }
}
