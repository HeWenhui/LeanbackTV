package com.xueersi.parentsmeeting.modules.livevideo.redpackage.business;

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

import com.xueersi.common.business.UserBll;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.framework.utils.EventBusUtil;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.achievement.entity.UpdateAchievementEvent;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.business.RedPackageAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.WeakHandler;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.redpackage.entity.RedPackageEvent;

import org.json.JSONObject;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author linyuqiang
 * Created by linyuqiang on 2016/9/23.
 */
public class RedPackageBll extends LiveBaseBll implements NoticeAction, RedPackageAction, Handler.Callback {
    private static final String TAG = "RedPackageBll";
    private WeakHandler mVPlayVideoControlHandler = new WeakHandler(this);
    private LogToFile mLogtf;
    private Activity activity;

    LiveHttpResponseParser mHttpResponseParser = null;


    /**
     * 直播id
     */
    private String mVSectionID;
    /**
     * 红包的布局
     */
    private RelativeLayout rlRedpacketContent;

    public RedPackageBll(Activity context, LiveBll2 liveBll, ViewGroup rootView) {
        super(context, liveBll, rootView);

        mLogtf = new LogToFile(TAG, new File(Environment.getExternalStorageDirectory(), "parentsmeeting/log/" + TAG
                + ".txt"));
        mLogtf.clear();
        this.activity = context;
        rlRedpacketContent = (RelativeLayout) rootView;
    }

    public RedPackageBll(Activity activity) {
        this(activity, null, null);
        mLogtf = new LogToFile(TAG, new File(Environment.getExternalStorageDirectory(), "parentsmeeting/log/" + TAG
                + ".txt"));
        mLogtf.clear();
        this.activity = activity;


    }

    public void setLiveBll(LiveBll mLiveBll) {
        // this.mLiveBll = mLiveBll;
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
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_red_packet_view, rlRedpacketContent, false);
        view.setBackgroundColor(activity.getResources().getColor(R.color.mediacontroller_bg));
        view.setTag(operateId);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
                .LayoutParams.MATCH_PARENT);
        rlRedpacketContent.addView(view, params);
        Button btnRedPacket = (Button) view.findViewById(R.id.bt_livevideo_redpackage_cofirm);

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
        activity.getWindow().getDecorView().requestLayout();
        activity.getWindow().getDecorView().invalidate();
    }


    private void sendReceiveGold(final int operateId, String mVSectionID) {
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        getHttpManager().sendReceiveGold(mLiveBll.getLiveType(), enstuId, operateId, mLiveBll.getLiveId(), new
                HttpCallBack() {

                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        mLogtf.d("sendReceiveGold:onPmSuccess=" + responseEntity.getJsonObject().toString() + "," +
                                "operateId=" +
                                operateId);

                        if (mHttpResponseParser == null) {
                            mHttpResponseParser = new LiveHttpResponseParser(activity);
                        }

                        VideoResultEntity entity = mHttpResponseParser.redPacketParseParser(responseEntity);
                        entity.setHttpUrl(url);
                        entity.setHttpRes("" + responseEntity.getJsonObject());
                        onGetPackage(entity);

                        // 广播 领取红包成功事件
                        EventBusUtil.post(new RedPackageEvent(mLiveBll.getLiveId(), entity.getGoldNum(),
                                operateId + "", RedPackageEvent.STATE_CODE_SUCCESS));
                    }

                    @Override
                    public void onPmFailure(Throwable error, String msg) {
                        super.onPmFailure(error, msg);
                        mLogtf.d("sendReceiveGold:onPmFailure=" + msg + ",operateId=" + operateId);
                        onGetPackageFailure(operateId);
                    }


                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        super.onPmError(responseEntity);
                        mLogtf.d("sendReceiveGold:onPmError=" + responseEntity.getErrorMsg() + ",operateId=" +
                                operateId);
                        onGetPackageError(operateId);
                    }


                });


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
                // 更新 本场成就
                EventBusUtil.post(new UpdateAchievementEvent(mLiveBll.getLiveId()));
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

    @Override
    public void onDestory() {
        super.onDestory();

        if (mVPlayVideoControlHandler != null) {
            mVPlayVideoControlHandler.removeCallbacksAndMessages(null);
        }
    }


    ///消息通信///////

    private int[] noticeCodes = {
            XESCODE.READPACAGE
    };



    private static final String DEFULT_VOICE_CHAT_STATE = "off";

    private boolean isRedPageAvaliable() {
        boolean result = true;
        try {
            //与 语音接麦 功能互斥
            Object targetParam = mLiveBll.getBusinessShareParam("voiceChatStatus");

            if (targetParam != null) {
                String voiceChatStatus = (String) targetParam;
                result = DEFULT_VOICE_CHAT_STATE.equals(voiceChatStatus);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    @Override
    public void onNotice(JSONObject data, int type) {

        try {
            switch (type) {
                case XESCODE.READPACAGE:
                    if (isRedPageAvaliable()) {
                        onReadPackage(data.getInt("id"), null);
                    }
                    break;

                default:
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public int[] getNoticeFilter() {
        return noticeCodes;
    }
}
