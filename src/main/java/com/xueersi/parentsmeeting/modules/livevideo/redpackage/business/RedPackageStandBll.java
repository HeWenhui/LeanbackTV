package com.xueersi.parentsmeeting.modules.livevideo.redpackage.business;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.entity.MyUserInfoEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.business.WeakHandler;
import com.xueersi.parentsmeeting.modules.livevideo.entity.GoldTeamStatus;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveAppUserInfo;
import com.xueersi.parentsmeeting.modules.livevideo.redpackage.pager.RedPackagePage;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.RedPackageStandLog;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by linyuqiang on 2018/3/20.
 * 站立直播红包
 */
public class RedPackageStandBll implements RedPackageAction, Handler.Callback {
    private String TAG = "RedPackageStandBll";
    private WeakHandler mVPlayVideoControlHandler = new WeakHandler(this);
    private LogToFile mLogtf;
    private Activity activity;
    /** 直播id */
    private String mVSectionID;
    /** 红包的布局 */
    private RelativeLayout rlRedpacketContent;
    private RedPackagePage redPackagePage;
    private HashMap<String, RedPackagePage> packagePageHashMap = new HashMap<>();
    private ReceiveGoldStand receiveGold;
    private String headUrl;
    private String userName;
    private boolean isLive;
    LiveAndBackDebug liveAndBackDebug;

    public RedPackageStandBll(Activity activity, boolean isLive, LiveAndBackDebug liveAndBackDebug) {
        mLogtf = new LogToFile(activity, TAG);
        mLogtf.clear();
        this.activity = activity;
        this.isLive = isLive;
        this.liveAndBackDebug = liveAndBackDebug;
    }

    public void setReceiveGold(ReceiveGoldStand receiveGold) {
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
    public void onReadPackage(final int operateId, final OnReceivePackage onReceivePackage) {
        mVPlayVideoControlHandler.post(new Runnable() {
            @Override
            public void run() {
                showRedPacket(operateId, onReceivePackage);
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
        RedPackagePage redPackagePage = packagePageHashMap.remove("" + operateId);
        if (redPackagePage != null) {
            rlRedpacketContent.removeView(redPackagePage.getRootView());
        }
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
    private void showRedPacket(final int operateId, final OnReceivePackage onReceivePackage) {
        mLogtf.d("showRedPacket:operateId=" + operateId);
        RedPackageStandLog.sno1(liveAndBackDebug, "" + operateId);
        final RedPackagePage oldRedPackagePage = redPackagePage;
        if (oldRedPackagePage != null) {
            //新的红包来了，移除旧的
            packagePageHashMap.remove("" + oldRedPackagePage.getOperateId());
            rlRedpacketContent.removeView(oldRedPackagePage.getRootView());
        }
        redPackagePage = new RedPackagePage(activity, operateId, new RedPackagePage.RedPackagePageAction() {

            @Override
            public void onPackageClick(final int operateId, final int clickPackage, final AbstractBusinessDataCallBack callBack) {
                receiveGold.sendReceiveGold(operateId, mVSectionID, new AbstractBusinessDataCallBack() {
                    @Override
                    public void onDataSucess(Object... objData) {
                        VideoResultEntity entity = (VideoResultEntity) objData[0];
                        callBack.onDataSucess(entity);
                        if (onReceivePackage != null) {
                            onReceivePackage.onReceivePackage(operateId);
                        }
                        RedPackagePage redPackagePage = packagePageHashMap.get("" + operateId);
                        redPackagePage.onGetPackage(entity);
                        receiveGold.onReceiveGold();
                        if (clickPackage == RedPackagePage.CLICK_PACKAGE_1) {
                            //结果页增加自己数据
                            GoldTeamStatus goldTeamStatus = new GoldTeamStatus();
                            GoldTeamStatus.Student student = new GoldTeamStatus.Student();
                            student.setNickname(userName);
                            student.setAvatar_path(headUrl);
                            student.setStuId(LiveAppUserInfo.getInstance().getStuId());
                            student.setGold("" + entity.getGoldNum());
                            student.setMe(true);
                            goldTeamStatus.getStudents().add(student);
                            redPackagePage.onGetTeamPackage(goldTeamStatus);
                            //直播获得小组数据，回放隔几秒就消失
                            if (isLive) {
                                final AtomicBoolean stop = new AtomicBoolean(false);
                                getReceiveGoldTeamStatus(operateId, stop);
                                rlRedpacketContent.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        stop.set(true);
                                        final AtomicInteger tryTime = new AtomicInteger();
                                        receiveGold.getReceiveGoldTeamRank(operateId, new AbstractBusinessDataCallBack() {
                                            @Override
                                            public void onDataSucess(Object... objData) {
                                                GoldTeamStatus entity = (GoldTeamStatus) objData[0];
                                                RedPackagePage redPackagePage = packagePageHashMap.get("" + operateId);
                                                redPackagePage.onGetTeamRank(entity);
                                            }

                                            @Override
                                            public void onDataFail(int errStatus, String failMsg) {
                                                super.onDataFail(errStatus, failMsg);
                                                if (errStatus == 0) {
                                                    if (tryTime.get() == 0) {
                                                        receiveGold.getReceiveGoldTeamRank(operateId, this);
                                                        tryTime.getAndIncrement();
                                                    } else {
                                                        onPackageClose(operateId);
                                                    }
                                                } else if (errStatus == 1) {
                                                    onPackageClose(operateId);
                                                }
                                            }
                                        });
                                    }
                                }, 14000);
                            }
                        }
                    }

                    @Override
                    public void onDataFail(int errStatus, String failMsg) {
                        if (errStatus == 0) {
                            onGetPackageFailure(operateId);
                        } else {
                            onGetPackageError(operateId);
                        }
//                        if (AppConfig.DEBUG) {
//                            VideoResultEntity entity = new VideoResultEntity();
//                            entity.setGoldNum(12);
//                            onDataSucess(entity);
//                        }
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
//                if (oldRedPackagePage != null) {
//                    oldRedPackagePage.onOtherPackage();
//                }
            }
        }, userName, headUrl, isLive, liveAndBackDebug);
        View view = redPackagePage.getRootView();
        packagePageHashMap.put("" + operateId, redPackagePage);
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
     * @param stop
     */
    private void getReceiveGoldTeamStatus(final int operateId, final AtomicBoolean stop) {
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
                if (errStatus == 0) {
                    if (stop.get()) {
                        return;
                    }
                    RedPackagePage redPackagePage = packagePageHashMap.get("" + operateId);
                    if (redPackagePage != null) {
                        redPackagePage.getRootView().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getReceiveGoldTeamStatus(operateId, stop);
                            }
                        }, 1000);
                    }
                }
            }

            void onFinish() {
//                if (stop.get()) {
//                    return;
//                }
//                RedPackagePage redPackagePage = packagePageHashMap.get("" + operateId);
//                if (redPackagePage != null) {
//                    redPackagePage.getRootView().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            getReceiveGoldTeamStatus(operateId, stop);
//                        }
//                    }, 1000);
//                }
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

}
