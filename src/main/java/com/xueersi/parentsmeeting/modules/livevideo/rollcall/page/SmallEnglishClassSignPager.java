package com.xueersi.parentsmeeting.modules.livevideo.rollcall.page;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.common.base.BasePager;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassSignEntity;
import com.xueersi.parentsmeeting.modules.livevideo.rollcall.business.Config;

import java.io.File;

public class SmallEnglishClassSignPager extends BasePager {
    private String TAG = "ArtsClassSignPager";
    //    private Context mContext;
    //iv_live_video_small_english_sign_background  //签到显示的背景图
    private ImageView ivArtsSignBoard;
    //tv_livevideo_small_english_sign_user_name  签到时显示的用户名
    private TextView tvArtsSignName;
    //iv_livevideo_arts_sign_click 签到的点击button
    private ImageView ivArtsSignBtn;
    //开始签到的TextView
    private TextView tvArtsStartSign;
    //关闭按钮
    private ImageView ivSignClose;

    private SmallEnglishClassSign smallEnglishClassSign;
//    private RollCallBll rollCallBll;


    public void setSmallEnglishClassSign(SmallEnglishClassSign smallEnglishClassSign) {
        this.smallEnglishClassSign = smallEnglishClassSign;
    }

    private ClassSignEntity classSignEntity;
    private LogToFile logToFile;

    public SmallEnglishClassSignPager(Context context, ClassSignEntity classSignEntity) {
        super(context);
//        this.rollCallBll = rollCallBll;
        this.classSignEntity = classSignEntity;
        logToFile = new LogToFile(TAG, new File(Environment.getExternalStorageDirectory(), "parentsmeeting/log/" + TAG
                + ".txt"));

        initData();
        initListener();
    }

    //    public void setSmallEnglishClassSign()
    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_small_english_sign, null);
        ivArtsSignBoard = view.findViewById(R.id.iv_live_video_small_english_sign_background);
        tvArtsSignName = view.findViewById(R.id.tv_livevideo_small_english_sign_user_name);
        ivArtsSignBtn = view.findViewById(R.id.iv_livevideo_arts_sign_click);
        tvArtsStartSign = view.findViewById(R.id.tv_livevideo_small_english_sign_start_sign);
        ivSignClose = view.findViewById(R.id.iv_small_english_sign_close);
        return view;
    }

    @Override
    public void initData() {
        tvArtsSignName.setText(classSignEntity.getStuName() + " 你好");
        updateStatus(classSignEntity.getStatus());

    }

    @Override
    public void initListener() {
        super.initListener();
        ivSignClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 这里本来是回调rollCallBll的stopRollCall函数，但是这里为了将View层和Bll层解耦，去掉stopRollCall,在这里面进行单独操作
                 */
//                rollCallBll.stopRollCall();
                if (smallEnglishClassSign != null) {
                    smallEnglishClassSign.close();
                }

            }
        });
        ivArtsSignBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (smallEnglishClassSign != null) {
                    smallEnglishClassSign.sign(mHttpCallBack);
                }
//                rollCallBll.userSign(classSignEntity, new HttpCallBack() {

//                });
            }
        });
    }

    private HttpCallBack mHttpCallBack = new HttpCallBack() {
        @Override
        public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
            logToFile.d("onPmSuccess:responseEntity=" + responseEntity.getJsonObject().toString());
            updateStatus(Config.SIGN_STATE_CODE_SIGNED);
        }

        @Override
        public void onPmFailure(Throwable error, String msg) {
            logToFile.e("onPmFailure:msg=" + msg, error);
            XESToastUtils.showToast(mContext, TextUtils.isEmpty(msg) ? "网络异常" : msg);
        }

        @Override
        public void onPmError(ResponseEntity responseEntity) {
            String errorMsg = TextUtils.isEmpty(responseEntity.getErrorMsg()) ? "网络异常" : responseEntity
                    .getErrorMsg();
            XESToastUtils.showToast(mContext, errorMsg);
        }
    };

    public void updateStatus(int status) {
        if (status == Config.SIGN_STATE_CODE_UNSIGN) {//准备签到
            tvArtsStartSign.setVisibility(View.VISIBLE);
            tvArtsSignName.setVisibility(View.VISIBLE);
            ivArtsSignBoard.setVisibility(View.VISIBLE);
            ivArtsSignBoard.setImageResource(R.drawable.bg_livevideo_small_english_sign_registration_board);
            ivArtsSignBtn.setVisibility(View.VISIBLE);

        } else if (status == Config.SIGN_STATE_CODE_SIGNED) {//签到成功
            tvArtsStartSign.setVisibility(View.GONE);
            ivArtsSignBtn.setVisibility(View.GONE);
            tvArtsSignName.setVisibility(View.GONE);
            ivArtsSignBoard.setVisibility(View.VISIBLE);
            ivArtsSignBoard.setImageResource(R.drawable.shellwindow_registration_success_bg);

        } else {//签到失败
            // FIXME: 2018/7/22 签到失败啥情况？ 什么情况下签到失败？小英这里还有设么特殊措施吗？
            tvArtsSignName.setVisibility(View.GONE);
            tvArtsStartSign.setVisibility(View.GONE);
            ivArtsSignBtn.setVisibility(View.GONE);
            ivArtsSignBoard.setVisibility(View.VISIBLE);
            ivArtsSignBoard.setImageResource(R.drawable.bg_livevideo_small_english_registrationfail);
        }
    }

    public interface SmallEnglishClassSign {
        void close();

        void sign(HttpCallBack httpCallBack);
    }

}
