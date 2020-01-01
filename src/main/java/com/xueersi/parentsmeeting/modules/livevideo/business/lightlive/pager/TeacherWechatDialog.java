package com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.pager;

import android.app.Application;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.tal100.chatsdk.utils.ToastUtils;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.xrs.bury.xrsbury.XrsBury;
import com.xueersi.common.config.AppConfig;
import com.xueersi.common.config.FileConfig;
import com.xueersi.common.util.ScreenShot;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.file.FileUtils;
import com.xueersi.lib.framework.utils.image.ImageUtils;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.lib.imageloader.SingleConfig;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.widget.FrameAnimation;
import com.xueersi.ui.dialog.BaseAlertDialog;
import com.xueersi.ui.dialog.BaseDialog;

import java.io.File;

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
    private String imgQrcodeURL;
    private boolean isLoadSuccess;
    private Bitmap bitmap;
    IWXAPI mApi;
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
                        type == TYPE_WITH_HEAD ? SizeUtils.Dp2Px(mContext, 246) : SizeUtils.Dp2Px(mContext, 416)));
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
                if (type == TYPE_WITH_QRCODE) {
                    if (isLoadSuccess && bitmap != null) {
                        saveBitmap(bitmap);
                        openScanner();
                    } else {
                        ImageLoader.with(ContextManager.getContext()).load(imgQrcodeURL).asBitmap(new SingleConfig.BitmapListener() {
                            @Override
                            public void onSuccess(Drawable drawable) {
                                bitmap = ImageUtils.drawable2Bitmap(drawable);
                                if(bitmap != null){
                                    saveBitmap(bitmap);
                                    openScanner();
                                }else {
                                    XESToastUtils.showToast("二维码下载失败");
                                }
                            }

                            @Override
                            public void onFail() {
                                XESToastUtils.showToast("二维码下载失败");
                            }
                        });
                    }
                    XrsBury.clickBury(mContext.getResources().getString(R.string.click_03_63_002));
                } else if (type == TYPE_WITH_HEAD) {
                    ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData mClipData = ClipData.newPlainText("Label", mWechat);
                    cm.setPrimaryClip(mClipData);
                    XESToastUtils.showToast("您已复制老师微信号，快去添加吧");
                    XrsBury.clickBury(mContext.getResources().getString(R.string.click_03_63_014));

                }
            }
        });
    }

    /**
     * 跳转到微信扫一扫
     */
    private void openScanner() {
        if (mApi == null){
            mApi = WXAPIFactory.createWXAPI(mContext,AppConfig.SHARE_WX_APP_ID);
            mApi.registerApp(AppConfig.SHARE_WX_APP_ID);
        }
        if (mApi.isWXAppInstalled()){
            String WECHAT_APP_PACKAGE = "com.tencent.mm";
            String WECHAT_LAUNCHER_UI_CLASS = "com.tencent.mm.ui.LauncherUI";
            String WECHAT_OPEN_SCANER_NAME = "LauncherUI.From.Scaner.Shortcut";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setComponent(new ComponentName(WECHAT_APP_PACKAGE, WECHAT_LAUNCHER_UI_CLASS));
            intent.putExtra(WECHAT_OPEN_SCANER_NAME, true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mContext.startActivity(intent);
        }else {
            XESToastUtils.showToastAtCenter("你的设备还未安装微信客户端");
        }
    }

    /**
     * 保存二维码
     * @param bitmap
     */
    private void saveBitmap(Bitmap bitmap) {
        File dir = FileUtils.createOrExistsSDCardDirForFile(FileConfig.savePathImageDir);
        String filePath = "";
        if (dir != null && dir.exists()) {
            String filename = "Xes" + System.currentTimeMillis() + ".jpg";
            File path = FileUtils.getFileByPath(dir.getPath() + File.separator + filename);
            if (path != null) {
                if (path.exists()) {
                    path.delete();
                }
                filePath = path.getPath();
                boolean isSuccess = ScreenShot.saveToGallery(mContext, bitmap, path.getAbsolutePath(), Bitmap.CompressFormat.JPEG, filename, filename);
                if (isSuccess) {
                    XESToastUtils.showToastAtCenter("二维码截图已保存到相册");
                } else {
                    XESToastUtils.showToastAtCenter("二维码保存失败");
                }
            }
        }
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
            btWechatCopy.setText("保存二维码，去微信扫码");
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) btWechatCopy.getLayoutParams();
            params.width = SizeUtils.Dp2Px(mContext, 194);
            btWechatCopy.setLayoutParams(params);
        }
    }

    public TeacherWechatDialog setTeacherName(String name) {
        if (name != null) {
            tvName.setText(name);
        }
        return this;
    }

    public TeacherWechatDialog setTeacherWechat(String num) {
        if (num != null) {
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
        imgQrcodeURL = imgURL;
        final int defaultHeadImg = R.color.COLOR_F5F5F6;
        if (TextUtils.isEmpty(imgURL)) {
            // 如果图片URL为空则直接加载默认图片，因为图片加载框架对空字符串的路径加载会加载到其它图片上，故这样解决
            ivQrcode.setImageResource(defaultHeadImg);

        } else {
            ImageLoader.with(ContextManager.getContext()).load(imgURL).error(defaultHeadImg)
                    .placeHolder(defaultHeadImg).into(ivQrcode, new SingleConfig.BitmapListener() {
                @Override
                public void onSuccess(Drawable drawable) {
                    bitmap = ImageUtils.drawable2Bitmap(drawable);
                    isLoadSuccess = true;
                }

                @Override
                public void onFail() {
                    isLoadSuccess = false;
                }
            });
        }
        return this;
    }

    public TeacherWechatDialog setSubTitle(String subTitle) {
        tvSubTitle.setText(subTitle);
        return this;
    }

    @Override
    public void showDialog() {
        super.showDialog();
        if (type == TYPE_WITH_QRCODE) {
            ScaleAnimation animation =  new ScaleAnimation(0.95f, 1.05f, 0.95f, 1.05f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            animation.setDuration(1100);
            animation.setRepeatCount(Animation.INFINITE);
            animation.setRepeatMode(Animation.REVERSE);
            btWechatCopy.startAnimation(animation);
        }
    }
}
