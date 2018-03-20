package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.parentsmeeting.base.AbstractBusinessDataCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.entity.VideoResultEntity;
import com.xueersi.xesalib.utils.uikit.ScreenUtils;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by linyuqiang on 2016/9/23.
 */
public class RedPackageBll implements RedPackageAction, Handler.Callback {
    String TAG = "RedPackageBll";
    private WeakHandler mVPlayVideoControlHandler = new WeakHandler(this);
    private LogToFile mLogtf;
    private Activity activity;
    private LiveBll mLiveBll;
    /** 直播id */
    private String mVSectionID;
    /** 红包的布局 */
    private RelativeLayout rlRedpacketContent;

    public RedPackageBll(Activity activity) {
        mLogtf = new LogToFile(TAG, new File(Environment.getExternalStorageDirectory(), "parentsmeeting/log/" + TAG
                + ".txt"));
        mLogtf.clear();
        this.activity = activity;
    }

    public void setLiveBll(LiveBll mLiveBll) {
        this.mLiveBll = mLiveBll;
    }

    public void setVSectionID(String mVSectionID) {
        this.mVSectionID = mVSectionID;
    }

    @Override
    public boolean handleMessage(Message msg) {
        return false;
    }

    @Override
    public void onReadPackage(final int operateId) {
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                showRedPacket(operateId);
            }
        };
        mVPlayVideoControlHandler.post(runnable);
    }

    private void onGetPackage(VideoResultEntity entity) {
        rlRedpacketContent.removeAllViews();
        initRedPacketResult(entity.getGoldNum());
    }

    private void onGetPackageFailure(int operateId) {
    }

    private void onGetPackageError(int operateId) {
        rlRedpacketContent.removeAllViews();
    }

    public void initView(RelativeLayout bottomContent) {
        //红包
        if (rlRedpacketContent != null) {
            bottomContent.addView(rlRedpacketContent, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        } else {
            rlRedpacketContent = new RelativeLayout(activity);
            rlRedpacketContent.setId(R.id.rl_livevideo_content_readpackage);
            bottomContent.addView(rlRedpacketContent, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }

        //测试红包自动关闭
//        else {
//            initRedPacketResult(5);
//        }
    }

    /**
     * 显示红包
     */
    private void showRedPacket(final int operateId) {
        mLogtf.d("showRedPacket:operateId=" + operateId);
        rlRedpacketContent.removeAllViews();
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_red_packet_view, rlRedpacketContent, false);
        view.setBackgroundColor(activity.getResources().getColor(R.color.mediacontroller_bg));
        view.setTag(operateId);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rlRedpacketContent.addView(view, params);
        Button btnRedPacket = (Button) view.findViewById(R.id.bt_livevideo_redpackage_cofirm);
        btnRedPacket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLiveBll.sendReceiveGold(operateId, mVSectionID, new AbstractBusinessDataCallBack() {
                    @Override
                    public void onDataSucess(Object... objData) {
                        VideoResultEntity entity = (VideoResultEntity) objData[0];
                        onGetPackage(entity);
                    }

                    @Override
                    public void onDataFail(int errStatus, String failMsg) {
                        if (errStatus == 0) {
                            onGetPackageFailure(operateId);
                        } else {
                            onGetPackageError(operateId);
                        }
                    }
                });
            }
        });
        view.findViewById(R.id.iv_livevideo_redpackage_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlRedpacketContent.removeAllViews();
            }
        });
        activity.getWindow().getDecorView().requestLayout();
        activity.getWindow().getDecorView().invalidate();
    }

    /**
     * 获取红包成功
     *
     * @param goldNum 金币数量
     */
    private void initRedPacketResult(int goldNum) {
        String msg = "+" + goldNum + "金币";
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_red_packet_success, rlRedpacketContent, false);
        view.setBackgroundColor(activity.getResources().getColor(R.color.mediacontroller_bg));
        SpannableString msp = new SpannableString(msg);
        float screenDensity = ScreenUtils.getScreenDensity();
        // 字体
        msp.setSpan(new AbsoluteSizeSpan((int) (50 * screenDensity)), 0, msg.length() - 2,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        TextView tvGoldHint = (TextView) view.findViewById(R.id.tv_livevideo_redpackage_gold);
        tvGoldHint.setText(msp);
        rlRedpacketContent.addView(view);
        view.findViewById(R.id.iv_livevideo_redpackage_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlRedpacketContent.removeAllViews();
            }
        });
        final TextView tvAutoclose = (TextView) view.findViewById(R.id.tv_livevideo_redpackage_autoclose);
        final AtomicInteger count = new AtomicInteger(3);
        postDelayedIfNotFinish(new Runnable() {
            @Override
            public void run() {
                count.set(count.get() - 1);
                if (count.get() == 0) {
                    rlRedpacketContent.removeAllViews();
                } else {
                    if (rlRedpacketContent.getChildCount() > 0) {
                        tvAutoclose.setText(count.get() + "秒自动关闭");
                        postDelayedIfNotFinish(this, 1000);
                    }
                }
            }
        }, 1000);
        postDelayedIfNotFinish(new Runnable() {
            @Override
            public void run() {
                mLiveBll.getStuGoldCount();
            }
        }, 2900);
        ImageView ivRedpackageLight = (ImageView) view.findViewById(R.id.iv_livevideo_redpackage_light);
        Animation animation = AnimationUtils.loadAnimation(activity, R.anim.anim_livevideo_light_rotate);
        ivRedpackageLight.startAnimation(animation);
    }

    public void postDelayedIfNotFinish(Runnable r, long delayMillis) {
        if (activity.isFinishing()) {
            return;
        }
        mVPlayVideoControlHandler.postDelayed(r, delayMillis);
    }
}
