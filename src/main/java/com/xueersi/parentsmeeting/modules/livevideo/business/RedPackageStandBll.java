package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.base.AbstractBusinessDataCallBack;
import com.xueersi.parentsmeeting.config.AppConfig;
import com.xueersi.parentsmeeting.entity.MyUserInfoEntity;
import com.xueersi.parentsmeeting.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.GoldTeamStatus;
import com.xueersi.parentsmeeting.modules.livevideo.page.RedPackagePage;
import com.xueersi.parentsmeeting.modules.loginregisters.business.UserBll;

import java.io.File;
import java.util.HashMap;

/**
 * Created by linyuqiang on 2018/3/20.
 * 站立直播红包
 */
public class RedPackageStandBll implements RedPackageAction, Handler.Callback {
    String TAG = "RedPackageStandBll";
    private WeakHandler mVPlayVideoControlHandler = new WeakHandler(this);
    private LogToFile mLogtf;
    private Activity activity;
    /** 直播id */
    private String mVSectionID;
    /** 红包的布局 */
    private RelativeLayout rlRedpacketContent;
    RedPackagePage redPackagePage;
    HashMap<String, RedPackagePage> packagePageHashMap = new HashMap<>();
    ReceiveGold receiveGold;
    String headUrl;
    String userName;

    public RedPackageStandBll(Activity activity) {
        mLogtf = new LogToFile(TAG, new File(Environment.getExternalStorageDirectory(), "parentsmeeting/log/" + TAG
                + ".txt"));
        mLogtf.clear();
        this.activity = activity;
    }

    public void setReceiveGold(ReceiveGold receiveGold) {
        this.receiveGold = receiveGold;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
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
        mVPlayVideoControlHandler.post(new Runnable() {
            @Override
            public void run() {
                showRedPacket(operateId);
            }
        });
    }

    private void onGetPackage(VideoResultEntity entity) {
//        rlRedpacketContent.removeAllViews();
//        initRedPacketResult(entity.getGoldNum());
    }

    private void onGetPackageFailure(int operateId) {
    }

    private void onGetPackageError(int operateId) {
//        RedPackagePage redPackagePage = packagePageHashMap.remove("" + operateId);
//        if (redPackagePage != null) {
//            rlRedpacketContent.removeView(redPackagePage.getRootView());
//        }
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
//        rlRedpacketContent.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                showRedPacket(1);
//            }
//        }, 1000);
    }

    /**
     * 显示红包
     */
    private void showRedPacket(final int operateId) {
        mLogtf.d("showRedPacket:operateId=" + operateId);
//        rlRedpacketContent.removeAllViews();
        final RedPackagePage oldRedPackagePage = redPackagePage;
        redPackagePage = new RedPackagePage(activity, operateId, new RedPackagePage.RedPackagePageAction() {

            @Override
            public void onPackageClick(final int operateId, final int clickPackage) {
                receiveGold.sendReceiveGold(operateId, mVSectionID, new AbstractBusinessDataCallBack() {
                    @Override
                    public void onDataSucess(Object... objData) {
                        VideoResultEntity entity = (VideoResultEntity) objData[0];
                        RedPackagePage redPackagePage = packagePageHashMap.get("" + operateId);
                        redPackagePage.onGetPackage(entity);
                        if (clickPackage == 1) {
                            //结果页增加自己数据
                            MyUserInfoEntity mMyInfo = UserBll.getInstance().getMyUserInfoEntity();
                            GoldTeamStatus goldTeamStatus = new GoldTeamStatus();
                            GoldTeamStatus.Student student = new GoldTeamStatus.Student();
                            student.setNickname(userName);
                            student.setAvatar_path(headUrl);
                            student.setStuId(mMyInfo.getStuId());
                            student.setGold("" + entity.getGoldNum());
                            student.setMe(true);
                            goldTeamStatus.getStudents().add(student);
                            redPackagePage.onGetTeamPackage(goldTeamStatus);
                            //结果页获得小组数据
                            getReceiveGoldTeamStatus(operateId);
                        }
                    }

                    @Override
                    public void onDataFail(int errStatus, String failMsg) {
                        if (errStatus == 0) {
                            onGetPackageFailure(operateId);
                        } else {
                            onGetPackageError(operateId);
                        }
                        if (AppConfig.DEBUG) {
                            VideoResultEntity entity = new VideoResultEntity();
                            entity.setGoldNum(12);
                            RedPackagePage redPackagePage = packagePageHashMap.get("" + operateId);
                            redPackagePage.onGetPackage(entity);
                            if (clickPackage == 1) {
                                MyUserInfoEntity mMyInfo = UserBll.getInstance().getMyUserInfoEntity();
                                GoldTeamStatus goldTeamStatus = new GoldTeamStatus();
                                GoldTeamStatus.Student student = new GoldTeamStatus.Student();
                                student.setNickname(userName);
                                student.setAvatar_path(headUrl);
                                student.setStuId(mMyInfo.getStuId());
                                student.setGold("99");
                                student.setMe(true);
                                goldTeamStatus.getStudents().add(student);
                                redPackagePage.onGetTeamPackage(goldTeamStatus);
                                //
                                getReceiveGoldTeamStatus(operateId);
                            }
                        }
                    }
                });
            }

            @Override
            public void onPackageClose(int operateId) {
                RedPackagePage redPackagePage = packagePageHashMap.remove("" + operateId);
                if (redPackagePage != null) {
                    rlRedpacketContent.removeView(redPackagePage.getRootView());
                }
            }

            @Override
            public void onPackageRight(int operateId) {
                if (oldRedPackagePage != null) {
                    oldRedPackagePage.onOtherPackage();
                }
            }
        }, userName, headUrl);
        View view = redPackagePage.getRootView();
        packagePageHashMap.put("" + operateId, redPackagePage);
//        view.setBackgroundColor(activity.getResources().getColor(R.color.mediacontroller_bg));
        view.setTag(operateId);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rlRedpacketContent.addView(view, params);
        activity.getWindow().getDecorView().requestLayout();
        activity.getWindow().getDecorView().invalidate();
        redPackagePage.initEnter();
    }

    /**
     * 结果页获得小组数据
     *
     * @param operateId
     */
    private void getReceiveGoldTeamStatus(final int operateId) {
        receiveGold.getReceiveGoldTeamStatus(operateId, new AbstractBusinessDataCallBack() {
            @Override
            public void onDataSucess(Object... objData) {
                GoldTeamStatus entity = (GoldTeamStatus) objData[0];
                RedPackagePage redPackagePage = packagePageHashMap.get("" + operateId);
                if (redPackagePage != null) {
                    redPackagePage.onGetTeamPackage(entity);
                }
                onFinish();
            }

            @Override
            public void onDataFail(int errStatus, String failMsg) {
                super.onDataFail(errStatus, failMsg);
                onFinish();
            }

            void onFinish() {
                RedPackagePage redPackagePage = packagePageHashMap.get("" + operateId);
                if (redPackagePage != null) {
                    redPackagePage.getRootView().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveGoldTeamStatus(operateId);
                        }
                    }, 1000);
                }
            }
        });
    }

    /**
     * 获取红包成功
     *
     * @param goldNum 金币数量
     */
    private void initRedPacketResult(int goldNum) {
//        String msg = "+" + goldNum + "金币";
//        View view = activity.getLayoutInflater().inflate(R.layout.dialog_red_packet_success, rlRedpacketContent, false);
//        view.setBackgroundColor(activity.getResources().getColor(R.color.mediacontroller_bg));
//        SpannableString msp = new SpannableString(msg);
//        float screenDensity = ScreenUtils.getScreenDensity();
//        // 字体
//        msp.setSpan(new AbsoluteSizeSpan((int) (50 * screenDensity)), 0, msg.length() - 2,
//                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        TextView tvGoldHint = (TextView) view.findViewById(R.id.tv_livevideo_redpackage_gold);
//        tvGoldHint.setText(msp);
//        rlRedpacketContent.addView(view);
//        view.findViewById(R.id.iv_livevideo_redpackage_close).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                rlRedpacketContent.removeAllViews();
//            }
//        });
//        final TextView tvAutoclose = (TextView) view.findViewById(R.id.tv_livevideo_redpackage_autoclose);
//        final AtomicInteger count = new AtomicInteger(3);
//        postDelayedIfNotFinish(new Runnable() {
//            @Override
//            public void run() {
//                count.set(count.get() - 1);
//                if (count.get() == 0) {
//                    rlRedpacketContent.removeAllViews();
//                } else {
//                    if (rlRedpacketContent.getChildCount() > 0) {
//                        tvAutoclose.setText(count.get() + "秒自动关闭");
//                        postDelayedIfNotFinish(this, 1000);
//                    }
//                }
//            }
//        }, 1000);
//        postDelayedIfNotFinish(new Runnable() {
//            @Override
//            public void run() {
//                receiveGold.onReceiveGold();
//            }
//        }, 2900);
//        ImageView ivRedpackageLight = (ImageView) view.findViewById(R.id.iv_livevideo_redpackage_light);
//        Animation animation = AnimationUtils.loadAnimation(activity, R.anim.anim_livevideo_light_rotate);
//        ivRedpackageLight.startAnimation(animation);
    }

    public void postDelayedIfNotFinish(Runnable r, long delayMillis) {
        if (activity.isFinishing()) {
            return;
        }
        mVPlayVideoControlHandler.postDelayed(r, delayMillis);
    }

    public interface ReceiveGold {
        void sendReceiveGold(final int operateId, String liveId, final AbstractBusinessDataCallBack callBack);

        void getReceiveGoldTeamStatus(int operateId, final AbstractBusinessDataCallBack callBack);

        void onReceiveGold();
    }
}
