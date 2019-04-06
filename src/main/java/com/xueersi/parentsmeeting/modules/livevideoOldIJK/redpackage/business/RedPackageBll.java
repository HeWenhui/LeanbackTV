package com.xueersi.parentsmeeting.modules.livevideoOldIJK.redpackage.business;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
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

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.business.UserBll;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.achievement.business.UpdateAchievement;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.WeakHandler;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business.RedPackageAction;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.redpackage.entity.RedPackageEvent;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.redpackage.pager.SmallChineseRedPackagePager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.redpackage.pager.SmallEnglishRedPackagePager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.ProxUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author linyuqiang
 * Created by linyuqiang on 2016/9/23.
 */
public class RedPackageBll implements RedPackageAction, Handler.Callback {
    private Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
    private static final String TAG = "RedPackageBll";
    private WeakHandler mVPlayVideoControlHandler = new WeakHandler(this);
    private LogToFile mLogtf;
    private Activity activity;
    private ReceiveGold receiveGold;
    LiveHttpResponseParser mHttpResponseParser = null;
    /** 小学英语 */
    SmallEnglishRedPackagePager artsRedPackagePager;
    /** 小学语文 */
    SmallChineseRedPackagePager chineseRedPackagePager;
    private LiveGetInfo mGetInfo;
    /**
     * 直播id
     */
    private String mVSectionID;
    /**
     * 红包的布局
     */
    private RelativeLayout rlRedpacketContent;
    boolean isLive;
    private boolean isSmallEnglish = false;

    public RedPackageBll(Activity activity, LiveGetInfo liveGetInfo, boolean isLive) {
        mLogtf = new LogToFile(activity, TAG);
        mLogtf.clear();
        this.activity = activity;
        this.isLive = isLive;
        this.mGetInfo = liveGetInfo;
        if (mGetInfo != null) {
            isSmallEnglish = mGetInfo.getSmallEnglish();
        }
    }

    public void setReceiveGold(ReceiveGold receiveGold) {
        this.receiveGold = receiveGold;
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
        logger.i(String.valueOf(operateId));
        mVPlayVideoControlHandler.post(new Runnable() {
            @Override
            public void run() {
                showRedPacket(operateId, onReceivePackage);
            }
        });
    }

    private void onGetPackage(VideoResultEntity entity) {
        if (!isSmallEnglish && !LiveVideoConfig.isSmallChinese) {
            rlRedpacketContent.removeAllViews();
        }
        if (!isLive && entity.getResultType() == 0) {
            initRedPacketOtherResult();
        } else {
            initRedPacketResult(entity.getGoldNum());
        }
    }

    private void onGetPackageFailure(int operateId) {
    }

    private void onGetPackageError(int operateId) {
        rlRedpacketContent.removeAllViews();
    }

    public void initView(RelativeLayout bottomContent) {
        //红包
        if (rlRedpacketContent != null) {
            bottomContent.addView(rlRedpacketContent, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
        } else {
            rlRedpacketContent = new RelativeLayout(activity);
            rlRedpacketContent.setId(R.id.rl_livevideo_content_readpackage);
            bottomContent.addView(rlRedpacketContent, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }

    /**
     * 显示红包
     */
    private void showRedPacket(final int operateId, final OnReceivePackage onReceivePackage) {
        mLogtf.d("showRedPacket:operateId=" + operateId);
        rlRedpacketContent.removeAllViews();
        View view = null;
        RelativeLayout.LayoutParams params = null;
        //小英
        if (isSmallEnglish) {

            artsRedPackagePager = new SmallEnglishRedPackagePager(activity);
            view = artsRedPackagePager.getRootView();
            //小英红包打开红包按钮的监听器
            artsRedPackagePager.setRedPackageOpenListenr(new SmallEnglishRedPackagePager.RedPackageOpenListenr() {
                @Override
                public void openRedPackage() {
                    sendReceiveGold(operateId, mVSectionID);
                }
            });
            artsRedPackagePager.setCancelRedPackageTouchListener(cancelRedPackageTouchListener);

            params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
                    .MATCH_PARENT);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
//            }

        } else if (LiveVideoConfig.isSmallChinese) {
            //
            logger.i("在家小英的红包");
            if (chineseRedPackagePager == null) {
                chineseRedPackagePager = new SmallChineseRedPackagePager(activity);
            } else {//再次发红包
                chineseRedPackagePager.updateView(false, 0);
            }
            chineseRedPackagePager.setListener(new SmallChineseRedPackagePager.SmallChineseRedPackageListener() {
                @Override
                public void close() {
                    if (chineseRedPackagePager != null && chineseRedPackagePager.getRootView().getParent() == rlRedpacketContent) {
                        rlRedpacketContent.removeView(chineseRedPackagePager.getRootView());
                    }
                }

                @Override
                public void submit() {
                    sendReceiveGold(operateId, mVSectionID);
                }
            });
            view = chineseRedPackagePager.getRootView();
            params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
        } else {
            view = activity.getLayoutInflater().inflate(R.layout.dialog_red_packet_view, rlRedpacketContent, false);
            ImageView imageView = view.findViewById(R.id.iv_livevideo_redpackage_monkey);
            try {
                Drawable drawable = activity.getResources().getDrawable(R.drawable.bg_livevideo_redpackage_monkey);
                imageView.setBackground(drawable);
            } catch (Exception e) {
                mLogtf.e("showRedPacket:operateId=" + operateId, e);
            }
            view.setBackgroundColor(ContextCompat.getColor(activity, R.color.mediacontroller_bg));
            view.setTag(operateId);
            Button btnRedPacket = view.findViewById(R.id.bt_livevideo_redpackage_cofirm);
            btnRedPacket.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendReceiveGold(operateId, mVSectionID);
                }
            });
            view.findViewById(R.id.iv_livevideo_redpackage_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rlRedpacketContent.removeAllViews();
                }
            });
            params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout
                    .LayoutParams.MATCH_PARENT);
        }

        rlRedpacketContent.addView(view, params);

        activity.getWindow().getDecorView().requestLayout();
        activity.getWindow().getDecorView().invalidate();
    }

    //小英红包页面取消红包的监听器
    private SmallEnglishRedPackagePager.CancelRedPackageTouchListener cancelRedPackageTouchListener
            = new SmallEnglishRedPackagePager.CancelRedPackageTouchListener() {
        @Override
        public void cancelRedPackage() {
            rlRedpacketContent.removeAllViews();
        }

        @Override
        public boolean containsView() {
            return artsRedPackagePager != null && artsRedPackagePager.getRootView() != null && rlRedpacketContent ==
                    artsRedPackagePager.getRootView().getParent();
        }
    };

    private void sendReceiveGold(final int operateId, String sectionID) {
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        receiveGold.sendReceiveGold(operateId, sectionID, new AbstractBusinessDataCallBack() {
            @Override
            public void onDataSucess(Object... objData) {
                VideoResultEntity entity = (VideoResultEntity) objData[0];
                onGetPackage(entity);
                // 广播 领取红包成功事件
                EventBus.getDefault().post(new RedPackageEvent(mVSectionID, entity.getGoldNum(),
                        operateId + "", RedPackageEvent.STATE_CODE_SUCCESS));
            }

            @Override
            public void onDataFail(int errStatus, String failMsg) {
                super.onDataFail(errStatus, failMsg);
                if (errStatus == 0) {
                    onGetPackageFailure(operateId);
                } else {
                    onGetPackageError(operateId);
                }
            }
        });
    }

    /**
     * 获取红包成功,3秒后自动消失
     *
     * @param goldNum 金币数量
     */
    private void initRedPacketResult(int goldNum) {
        //小英
        if (isSmallEnglish) {
            //如果监听器没有设置（监听器理论上不会出现没有设置的情况，但是测试发现，加上保证无错误）
            if (artsRedPackagePager.getCancelRedPackageTouchListener() == null) {
                artsRedPackagePager.setCancelRedPackageTouchListener(cancelRedPackageTouchListener);
            }
            //如果当前artsRedPackagePager仍然在rlRedPackketContent中(即已经remove掉)
            if (artsRedPackagePager != null && artsRedPackagePager.getRootView().getParent() != rlRedpacketContent) {
                rlRedpacketContent.addView(artsRedPackagePager.getRootView());
            }
            artsRedPackagePager.updateStatus(String.valueOf(goldNum));
            postDelayedIfNotFinish(new Runnable() {
                @Override
                public void run() {
                    // 更新 本场成就
                    UpdateAchievement updateAchievement = ProxUtil.getProxUtil().get(activity, UpdateAchievement.class);
                    if (updateAchievement != null) {
                        updateAchievement.getStuGoldCount("initRedPacketResult:isSmallEnglish", UpdateAchievement.GET_TYPE_RED);
                    }
                }
            }, 2900);

//            postDelayedIfNotFinish(new Runnable() {
//                @Override
//                public void run() {
//                    //3秒后自动消失
//                    if (artsRedPackagePager.getRootView().getParent() == rlRedpacketContent) {
//                        rlRedpacketContent.removeAllViews();
//                    }
//                }
//            }, 3000);
        } else if (LiveVideoConfig.isSmallChinese) {
            if (chineseRedPackagePager != null && chineseRedPackagePager.getRootView().getParent() != rlRedpacketContent) {
                rlRedpacketContent.addView(chineseRedPackagePager.getRootView());
            }
            chineseRedPackagePager.updateView(true, goldNum);
            postDelayedIfNotFinish(new Runnable() {
                @Override
                public void run() {
                    // 更新 本场成就
                    UpdateAchievement updateAchievement = ProxUtil.getProxUtil().get(activity, UpdateAchievement.class);
                    if (updateAchievement != null) {
                        updateAchievement.getStuGoldCount("initRedPacketResult:isSmallChinese", UpdateAchievement.GET_TYPE_RED);
                    }
                }
            }, 0);

            postDelayedIfNotFinish(new Runnable() {
                @Override
                public void run() {
                    //3秒后自动消失
                    if (chineseRedPackagePager.getRootView().getParent() == rlRedpacketContent) {
                        rlRedpacketContent.removeAllViews();
                    }
                }
            }, 3000);
        } else {
            String msg = "+" + goldNum + "金币";
            View view = activity.getLayoutInflater().inflate(R.layout.dialog_red_packet_success, rlRedpacketContent,
                    false);
            view.setBackgroundColor(ContextCompat.getColor(activity, R.color.mediacontroller_bg));
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
                    // 更新 本场成就
                    UpdateAchievement updateAchievement = ProxUtil.getProxUtil().get(activity, UpdateAchievement.class);
                    if (updateAchievement != null) {
                        updateAchievement.getStuGoldCount("initRedPacketResult", UpdateAchievement.GET_TYPE_RED);
                    }
                }
            }, 2900);
            ImageView ivRedpackageLight = (ImageView) view.findViewById(R.id.iv_livevideo_redpackage_light);
            Animation animation = AnimationUtils.loadAnimation(activity, R.anim.anim_livevideo_light_rotate);
            ivRedpackageLight.startAnimation(animation);
        }
    }


    /**
     * 已获取红包
     */
    private void initRedPacketOtherResult() {
        View popupWindow_view = activity.getLayoutInflater().inflate(R.layout.pop_question_redpacket_other, null,
                false);
        initQuestionAnswerReslut(popupWindow_view);
    }

    /**
     * 创建互动题作答，抢红包结果提示PopupWindow
     */
    protected void initQuestionAnswerReslut(final View popupWindow_view) {
        rlRedpacketContent.addView(popupWindow_view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
                .MATCH_PARENT);
        popupWindow_view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                rlRedpacketContent.removeView(popupWindow_view);
            }
        });
        mVPlayVideoControlHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                rlRedpacketContent.removeView(popupWindow_view);
            }
        }, 3000);
    }

    public void postDelayedIfNotFinish(Runnable r, long delayMillis) {
        if (activity.isFinishing()) {
            return;
        }
        mVPlayVideoControlHandler.postDelayed(r, delayMillis);
    }

    public void onDestory() {
        if (mVPlayVideoControlHandler != null) {
            mVPlayVideoControlHandler.removeCallbacksAndMessages(null);
        }
    }

}
