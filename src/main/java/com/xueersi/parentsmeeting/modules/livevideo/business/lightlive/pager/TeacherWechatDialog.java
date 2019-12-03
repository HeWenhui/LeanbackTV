package com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.pager;

import android.app.Application;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Typeface;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xueersi.common.toast.XesToast;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.ui.dialog.BaseAlertDialog;
import com.xueersi.ui.dialog.BaseDialog;

/**
 * @ProjectName: xueersiwangxiao
 * @Package: com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.pager
 * @ClassName: TeacherWechatDialog
 * @Description: 老师微信弹窗
 * @Author: WangDe
 * @CreateDate: 2019/11/25 14:31
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/11/25 14:31
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class TeacherWechatDialog extends BaseAlertDialog {

    public final static int TYPE_WITH_HEAD = 3;
    public final static int TYPE_WITH_QRCODE = 2;
    private ImageView ivClose;
    private TextView tvTitle;
    private TextView tvSubTitle;
    private Button btWechatCopy;
    private RelativeLayout rlWechatWithHead;
    private ImageView ivTeacherHead;
    private TextView tvName;
    private TextView tvWechatNum;
    private LinearLayout llWechatWithQrcode;
    private ImageView ivQrcode;
    private RelativeLayout rlWechat;
    private String mWechat = "";
    private int type;

    public TeacherWechatDialog(Context context, Application application, int type) {
        super(context, application, false, type);
    }

    @Override
    protected View initDialogLayout(int type) {
        this.type = type;
        View view = mInflater.inflate(R.layout.dialog_livevideo_lightlive_teacher_wechat, null);

        rlWechat = view.findViewById(R.id.rl_livevideo_lightlive_wechat);

        ivClose = view.findViewById(R.id.iv_livevideo_lightlive_wechat_close);
        tvTitle = view.findViewById(R.id.tv_livevideo_lightlive_wechat_title);
        tvSubTitle = view.findViewById(R.id.tv_livevideo_lightlive_wechat_subtitle);
        btWechatCopy = view.findViewById(R.id.bt_livevideo_lightlive_wechat_copy);

        rlWechatWithHead = view.findViewById(R.id.rl_livevideo_lightlive_wechat_head);
        ivTeacherHead = view.findViewById(R.id.iv_lightlive_teacher_headimg);
        tvName = view.findViewById(R.id.tv_lightlive_wechat_teacher_name);
        tvWechatNum = view.findViewById(R.id.tv_lightlive_wechat_num);

        llWechatWithQrcode = view.findViewById(R.id.ll_livevideo_lightlive_wechat_qrcode);
        ivQrcode = view.findViewById(R.id.iv_livevideo_lightlive_wechat_qrcode);
        initListener();
        initView();
        return view;
    }

    @Override
    protected void createDialog(View alertView, boolean isSystem) {
        //系统对话框
        vDialog = alertView;
        int sdkInt = android.os.Build.VERSION.SDK_INT;
        String company = android.os.Build.MANUFACTURER;
        /*if (isSystem && sdkInt >= 19 && sdkInt <= 24 && company != null && "xiaomi".equals(company.toLowerCase())) {
            createSystemAlertDialog(alertView);
            return;
        }*/
        setAlertDialog(new BaseDialog(mContext, com.xueersi.ui.component.R.style.Translucent_NoTitle));
        Window window = mAlertDialog.getWindow();
        getAlertDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = window.getAttributes();
//        lp.width = (int) (ScreenUtils.getScreenWidth() * mDialogWidth);
        lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG;
        lp.alpha = 1f;
        lp.dimAmount = 0f;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//        lp.horizontalMargin = 0;
        window.getDecorView().setMinimumWidth(mContext.getResources().getDisplayMetrics().widthPixels);
        window.setAttributes(lp);
        window.setWindowAnimations(android.R.style.Animation_Dialog);
        mAlertDialog.setContentView(
                alertView,
                new ViewGroup.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                        type == TYPE_WITH_HEAD ? SizeUtils.Dp2Px(mContext, 246) : SizeUtils.Dp2Px(mContext, 390)));

    }

    private void setAlertDialog(Dialog alertDialog) {
        if (this.mAlertDialog != null && this.mAlertDialog.isShowing()) {
            //前一个alertDialog不为空且显示，则先隐藏
            this.mAlertDialog.dismiss();
        }
        this.mAlertDialog = alertDialog;
    }

    private void initListener() {
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelDialog();
            }
        });
        btWechatCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);

                ClipData mClipData = ClipData.newPlainText("Label", mWechat);

                cm.setPrimaryClip(mClipData);
                XESToastUtils.showToast("您已复制老师微信号，快去添加吧");
            }
        });
    }

    private void initView() {

        if (TYPE_WITH_HEAD == type) {
            rlWechatWithHead.setVisibility(View.VISIBLE);
            llWechatWithQrcode.setVisibility(View.GONE);
//            tvSubTitle.setText("添加班主任微信号，领取课程资料");
        } else {
            rlWechatWithHead.setVisibility(View.GONE);
            llWechatWithQrcode.setVisibility(View.VISIBLE);
//            tvSubTitle.setText("老师为你准备了神秘礼物哦！");
            tvSubTitle.setTextSize(14);
            tvSubTitle.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            tvTitle.setText("扫码加入班级群");
        }
    }

    public TeacherWechatDialog setTeacherName(String name) {
        if (name != null){
            tvName.setText(name);
        }
        return this;
    }

    public TeacherWechatDialog setTeacherWechat(String num){
        if (num != null){
            mWechat = num;
            tvWechatNum.setText(num);
        }
        return this;
    }

    public TeacherWechatDialog setTeacherHead(String imgURL) {
        int defaultHeadImg = R.drawable.bg_tutor_default_head_imge;
        if (TextUtils.isEmpty(imgURL)) {
            // 如果图片URL为空则直接加载默认图片，因为图片加载框架对空字符串的路径加载会加载到其它图片上，故这样解决
            ivTeacherHead.setImageResource(defaultHeadImg);

        } else {
            ImageLoader.with(ContextManager.getContext()).asCircle().load(imgURL).error(defaultHeadImg)
                    .placeHolder(defaultHeadImg).into(ivTeacherHead);
        }
        return this;
    }

    public TeacherWechatDialog setQrcode(String imgURL) {
        int defaultHeadImg = R.drawable.bg_tutor_default_head_imge;
        if (TextUtils.isEmpty(imgURL)) {
            // 如果图片URL为空则直接加载默认图片，因为图片加载框架对空字符串的路径加载会加载到其它图片上，故这样解决
            ivQrcode.setImageResource(defaultHeadImg);

        } else {
            ImageLoader.with(ContextManager.getContext()).load(imgURL).error(defaultHeadImg)
                    .placeHolder(defaultHeadImg).into(ivQrcode);
        }
        return this;
    }

    public TeacherWechatDialog setSubTitle(String subTitle){
        tvSubTitle.setText(subTitle);
        return this;
    }


}
