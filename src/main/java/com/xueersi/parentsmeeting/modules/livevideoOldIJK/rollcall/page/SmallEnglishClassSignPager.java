package com.xueersi.parentsmeeting.modules.livevideoOldIJK.rollcall.page;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.common.base.BasePager;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassSignEntity;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.rollcall.business.Config;

public class SmallEnglishClassSignPager extends BasePager {
    private String TAG = "SmallEnglishClassSignPager";
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
    //签到按钮的监听器
    private SmallEnglishClassSign smallEnglishClassSign;
    //    private RollCallBll rollCallBll;
    //方便显示签到页面出来时，背景80%黑色透明，不可点击
//    RelativeLayout backGroundLayout;
//    //签到背景的布局
    private RelativeLayout.LayoutParams layoutParams;

    public void setSmallEnglishClassSign(SmallEnglishClassSign smallEnglishClassSign) {
        this.smallEnglishClassSign = smallEnglishClassSign;
    }

    private ClassSignEntity classSignEntity;
    private LogToFile logToFile;

    public SmallEnglishClassSignPager(Context context, ClassSignEntity classSignEntity) {
        super(context);
//        this.rollCallBll = rollCallBll;
        this.classSignEntity = classSignEntity;
        logToFile = new LogToFile(context, TAG);

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

//        backGroundLayout = new RelativeLayout(mContext);
//        //80%半透明
//        backGroundLayout.setBackgroundColor(Color.parseColor("#CC000000"));
//        backGroundLayout.setClickable(true);//背景不可点击
////        backGroundLayout.addView(smallEnglishSendFlowerPager.getRootView());
//        backGroundLayout.addView(view, getLayoutParams());
        return view;
    }

    //返回已经在backGroundLayout中布好局的backGroundLayout
//    public RelativeLayout getBackground() {
//        return backGroundLayout;
//    }

    //全屏正中间显示
    public RelativeLayout.LayoutParams getLayoutParams() {
        if (layoutParams == null) {
            layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout
                    .LayoutParams.MATCH_PARENT);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        }
        return layoutParams;
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
                    logToFile.d("点击了关闭签到按钮");
                    smallEnglishClassSign.close();
                }

            }
        });
        ivArtsSignBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (smallEnglishClassSign != null) {
                    logToFile.d("点击了签到按钮，发送了http请求");
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
            if (responseEntity.getErrorMsg() != null) {
                logToFile.d("primary english onPmError:msg=" + responseEntity.getErrorMsg());
                XESToastUtils.showToast(mContext, responseEntity.getErrorMsg());
            }
//            String errorMsg = TextUtils.isEmpty(responseEntity.getErrorMsg()) ? "网络异常" : responseEntity
//                    .getErrorMsg();
//            XESToastUtils.showToast(mContext, errorMsg);
        }
    };

    //更新状态
    public void updateStatus(int status) {
        classSignEntity.setStatus(status);
        if (status == Config.SIGN_STATE_CODE_UNSIGN) {//准备签到
            logToFile.d("准备签到");
            mView.removeCallbacks(closeRun);//移除之前关闭签到的Runnable.
            tvArtsStartSign.setVisibility(View.VISIBLE);
            tvArtsSignName.setVisibility(View.VISIBLE);
            ivArtsSignBoard.setVisibility(View.VISIBLE);
            ivArtsSignBoard.setImageResource(R.drawable.bg_livevideo_small_english_sign_registration_board);
            ivArtsSignBtn.setVisibility(View.VISIBLE);

        } else if (status == Config.SIGN_STATE_CODE_SIGNED) {//签到成功
            logToFile.d("签到成功");
            tvArtsStartSign.setVisibility(View.GONE);
            ivArtsSignBtn.setVisibility(View.GONE);
            tvArtsSignName.setVisibility(View.GONE);
            ivArtsSignBoard.setVisibility(View.VISIBLE);
            ivArtsSignBoard.setImageResource(R.drawable.shellwindow_registration_success_bg);
            //3秒自动消失
            if (mView != null) {
//                mView.getHandler().removeCallbacks(closeRun);
                mView.removeCallbacks(closeRun);
                mView.postDelayed(closeRun, 3000);
            }
        } else {//签到失败
            logToFile.d("签到失败");
            tvArtsSignName.setVisibility(View.GONE);
            tvArtsStartSign.setVisibility(View.GONE);
            ivArtsSignBtn.setVisibility(View.GONE);
            ivArtsSignBoard.setVisibility(View.VISIBLE);
            ivArtsSignBoard.setImageResource(R.drawable.bg_livevideo_small_english_registrationfail);
            //3秒自动消失
            if (mView != null) {
//                mView.getHandler().removeCallbacks(closeRun);
                mView.removeCallbacks(closeRun);
                mView.postDelayed(closeRun, 3000);
            }
        }
    }

    Runnable closeRun = new Runnable() {
        @Override
        public void run() {
            if (smallEnglishClassSign != null && smallEnglishClassSign.containsView()) {
                smallEnglishClassSign.close();
            }
        }
    };

    public interface SmallEnglishClassSign {
        //关闭签到
        void close();

        //签到回调服务器
        void sign(HttpCallBack httpCallBack);

        //当前View是否任然存活
        boolean containsView();
    }

}
