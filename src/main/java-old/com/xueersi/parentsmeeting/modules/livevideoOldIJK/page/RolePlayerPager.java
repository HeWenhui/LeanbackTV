package com.xueersi.parentsmeeting.modules.livevideoOldIJK.page;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tal.speech.config.SpeechConfig;
import com.tal.speech.speechrecognizer.EvaluatorListener;
import com.tal.speech.speechrecognizer.EvaluatorListenerWithPCM;
import com.tal.speech.speechrecognizer.ResultEntity;
import com.tal.speech.speechrecognizer.SpeechEvaluatorInter;
import com.tal.speech.speechrecognizer.SpeechParamEntity;
import com.tal.speech.utils.SpeechUtils;
import com.tal.speech.utils.SpeechEvaluatorUtils;
import com.xueersi.common.base.BaseApplication;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.SpeechResultMember;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.activity.item.RolePlayerOtherItem;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.activity.item.RolePlayerSelfItem;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.RolePlayerBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.agora.WorkerThread;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.RolePlayConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RolePlayerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.SpeechResultEntity;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.page.SpeechResultPager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.stablelog.RolePlayLog;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.LiveCacheFile;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.view.CustomUnScorllListView;
import com.xueersi.ui.adapter.AdapterItemInterface;
import com.xueersi.ui.adapter.CommonAdapter;
import com.xueersi.ui.widget.CircleImageView;
import com.xueersi.parentsmeeting.widget.VolumeWaveView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.agora.rtc.Constants;
import io.agora.rtc.RtcEngine;


/**
 * RolePlayer多人语音连麦
 * Created by zouhao on 2018/3/29.
 */

public class RolePlayerPager extends LiveBasePager<RolePlayerEntity> {

    /**
     * 匹配页默认停留时间
     */
    private final int MATCH_WAIT_SECOND = 4000;
    /**
     * 角色确认页停留时间
     */
    private final int WAIT_ROLE_HEAD_SHOW = 3000;

    /**
     * 匹配页我的头像
     */
    private CircleImageView civMatchHead;

    /**
     * 匹配页
     */
    private RelativeLayout rlMatchPager;
    /**
     * 角色列表展示区
     */
    private RelativeLayout rlMatchRoleList;
    /**
     * 角色显示页
     */
    private RelativeLayout rlMatchLottie;
    /**
     * 准备开始朗读前的提示文案
     */
    private TextView tvBeginTipMsg;

    /**
     * roleplay回放的时候，增加关闭按钮
     */
    TextView tv_close_role_play;

    /**
     * 倒计时整体布局
     */
    View ll_live_roleplayer_countdown_main;


    /**
     * 倒计时器
     */
    private TextView tvCountTime;
    /**
     * 角色展示列表
     */
    private GridView gvRoleHeadShow;
    /**
     * 朗读区
     */
    private RelativeLayout rlRoleReadMain;

    /**
     * 测评音量条展示区
     */
    private RelativeLayout rlSpeechVolumnMain;
    /**
     * 测评音量波形
     */
    private VolumeWaveView vwvSpeechVolume;

    /**
     * 结果页
     */
    private LiveBasePager resultPager;
//    private RelativeLayout rlResult;

    /**
     * 角色展示区适配器
     */
    private RolePlayerHeadShowAdapter mHeadShowAdapter;

    /**
     * 对话区
     */
    private CustomUnScorllListView lvReadList;

    /**
     * 点赞区
     */
    private RelativeLayout rlDZBubbleMessage;

    /**
     * 对话数据适配器
     */
    private CommonAdapter<RolePlayerEntity.RolePlayerMessage> mRolePlayerAdapter;

    /**
     * 当前正在朗读的索引
     */
    private int mCurrentReadIndex;

    private View vHead;

    /**
     * 角色业务
     */
    private RolePlayerBll mRolePlayBll;
    private boolean mIsEnd;
    /**
     * 语音评测
     */
    protected SpeechUtils mIse;
    private SpeechEvaluatorInter speechEvaluatorInter;
    private File saveVideoFile, dir;
    /**
     * 声网
     */
    private WorkerThread mWorkerThread;

    /**
     * 结果总分
     */
//    private TextView tvTotalScore;
    /**
     * 获得的点赞数
     */
//    private TextView tvDzCount;
    /**
     * 流畅性
     */
//    private TextView tvFluency;
    /**
     * 金币数
     */
//    private TextView tvGoldCount;
    /**
     * 准确性
     */
//    private TextView tvAccuracy;
    /**
     * 结果页自己的头像
     */
//    private CircleImageView civResultHeadImg;
    /**
     * 总评
     */
//    private TextView tvResultMsgTip;

    /**
     * 排名1
     */
//    private RelativeLayout rlResultRole1;
    /**
     * 排名1头像
     */
//    private CircleImageView civResultRoleHeadImg1;
    /**
     * 排名1分数
     */
//    private TextView tvResultRoleScore1;
    /**
     * 排名1名字
     */
//    private TextView tvResultRoleName1;

    /**
     * 排名2
     */
//    private RelativeLayout rlResultRole2;
    /**
     * 排名2头像
     */
//    private CircleImageView civResultRoleHeadImg2;
    /**
     * 排名2分数
     */
//    private TextView tvResultRoleScore2;
    /**
     * 排名2名字
     */
//    private TextView tvResultRoleName2;

    /**
     * 排名3
     */
//    private RelativeLayout rlResultRole3;
    /**
     * 排名3头像
     */
//    private CircleImageView civResultRoleHeadImg3;
    /**
     * 排名3分数
     */
//    private TextView tvResultRoleScore3;
    /**
     * 排名3名字
     */
//    private TextView tvResultRoleName3;
    private ImageView ivRoleplayerResultStar;//显示成绩结果星星旗帜
    private boolean isShowResult;//标记是否正在显示结果页
    private LiveGetInfo mLiveGetInfo;//用于获取学科字段，以此判断roleplay进入哪个学科的连麦
    private RolePlayerSelfItem mRolePlayerSelfItem;
    private RolePlayerOtherItem mRolePlayerOtherItem;
    private ImageView iv_live_roleplayer_title;//roleplay标题icon
    private final LiveAndBackDebug liveAndBackDebug;//只为记录日志调用方便
    private boolean mIsListViewUnSroll;//listview是否可滑动
    //private boolean mIsEvaluatoring;//标记正在测评中
    private RolePlayerEntity mtype;
    private SpeechParamEntity param;
    private RtcEngine rtcEngine;

    public RolePlayerPager(Context context, RolePlayerEntity obj, boolean isNewView, RolePlayerBll rolePlayerBll,
                           LiveGetInfo liveGetInfo) {
        super(context, obj, isNewView);
        this.mRolePlayBll = rolePlayerBll;
        mLiveGetInfo = liveGetInfo;
        mtype = obj;
        dir = LiveCacheFile.geCacheFile(context, "liveSpeech");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String stuId = LiveAppUserInfo.getInstance().getStuId();
        if (StringUtils.isEmpty(stuId)) {
            stuId = liveGetInfo.getStuId();
        }
        mWorkerThread = new WorkerThread(ContextManager.getApplication(), Integer.parseInt(stuId), false, true);

        liveAndBackDebug = ProxUtil.getProxUtil().get(context, LiveAndBackDebug.class);

        //区分英语，语文测评，显示中英文的roleplay标题和布局
        initRoleplayTitleUi();

    }

    /**
     * 区分英语，语文测评，显示中英文的roleplay标题和布局
     */
    private void initRoleplayTitleUi() {
        if (mLiveGetInfo != null) {
            if (1 == mLiveGetInfo.getIsEnglish()) {
                logger.i("走英语离线测评");
                //走英语离线测评
                setEnRoleplayUI();
            } else {
                String[] arrSubIds = mLiveGetInfo.getSubjectIds();
                if (arrSubIds != null) {
                    for (String subId : arrSubIds) {
                        if (LiveVideoConfig.SubjectIds.SUBJECT_ID_CH.equals(subId)) {
                            //走语文离线测评
                            logger.i("走语文离线测评:" + subId);
                            setChRoleplayUI();

                            break;
                        } else {
                            //走英语离线测评
                            logger.i("走英语离线测评:" + subId);
                            setEnRoleplayUI();
                        }
                    }
                } else {
                    //走英语离线测评
                    logger.i("走英语离线测评:");
                    setEnRoleplayUI();
                }

            }

        } else {
            //默认走英语离线测评
            logger.i("走英语离线测评");
            setEnRoleplayUI();
        }
    }

    /**
     * 设置英语标题布局
     */
    private void setEnRoleplayUI() {
        iv_live_roleplayer_title.setImageResource(R.drawable.livevideo_roleplay_title);
        //记录当前正在走的模型，留给界面更新使用
        ShareDataManager.getInstance().put(RolePlayConfig.KEY_FOR_WHICH_SUBJECT_MODEL_EVA, RolePlayConfig
                .VALUE_FOR_ENGLISH_MODEL_EVA, ShareDataManager.SHAREDATA_NOT_CLEAR);
    }

    /**
     * 设置语文标题布局
     */
    private void setChRoleplayUI() {
        iv_live_roleplayer_title.setImageResource(R.drawable.live_role_play_title);
        //记录当前正在走的模型，留给界面更新使用
        ShareDataManager.getInstance().put(RolePlayConfig.KEY_FOR_WHICH_SUBJECT_MODEL_EVA,
                RolePlayConfig.VALUE_FOR_CHINESE_MODEL_EVA, ShareDataManager.SHAREDATA_NOT_CLEAR);
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.pager_roleplayer, null);
        rlMatchPager = view.findViewById(R.id.rl_live_roleplayer_matchpager);
        rlMatchLottie = view.findViewById(R.id.rl_live_roleplayer_match_lottie);
        rlMatchRoleList = view.findViewById(R.id.rl_live_roleplayer_rolelist);
        rlMatchPager.setVisibility(View.VISIBLE);
        tv_close_role_play = view.findViewById(R.id.tv_close_role_play);
        //倒计时整体布局,在回放的时候隐藏显示
        ll_live_roleplayer_countdown_main = view.findViewById(R.id.ll_live_roleplayer_countdown_main);
        //倒计时textview
        tvCountTime = view.findViewById(R.id.tv_live_roleplayer_countdown);

        //只在直播的时候显示倒计时布局
        ll_live_roleplayer_countdown_main.setVisibility(View.VISIBLE);
        tv_close_role_play.setVisibility(View.GONE);

        gvRoleHeadShow = view.findViewById(R.id.gv_live_roleplayer_headshow);
        rlRoleReadMain = view.findViewById(R.id.rl_live_roleplayer_read_main);
        tvBeginTipMsg = view.findViewById(R.id.tv_live_roleplayer_countdown_tip);
        lvReadList = view.findViewById(R.id.lv_live_roleplayer_read_list);
        mIsListViewUnSroll = true;
        lvReadList.setUnScroll(mIsListViewUnSroll);//设置listview不可滑动
        civMatchHead = view.findViewById(R.id.civ_live_roleplayer_match_head);
        rlSpeechVolumnMain = view.findViewById(R.id.rl_live_roleplayer_speech_volumewave_main);
        vwvSpeechVolume = view.findViewById(R.id.vwv_livevideo_roleplayer_speech_volumewave);
//        tvTotalScore = view.findViewById(R.id.tv_livevideo_roleplayer_result_totalscore);
//        tvDzCount = view.findViewById(R.id.tv_livevideo_roleplayer_result_dz_count);
//        tvFluency = view.findViewById(R.id.tv_livevideo_roleplayer_result_fluency);
//        tvGoldCount = view.findViewById(R.id.tv_livevideo_roleplayer_result_gold_count);
//        tvAccuracy = view.findViewById(R.id.tv_livevideo_roleplayer_result_accuracy);
//        civResultHeadImg = view.findViewById(R.id.civ_livevideo_roleplayer_result_headimg);
//        tvResultMsgTip = view.findViewById(R.id.tv_livevideo_roleplayer_result_msgtip);

        ivRoleplayerResultStar = view.findViewById(R.id.iv_live_roleplayer_result_star);

//        rlResultRole1 = view.findViewById(R.id.rl_livevideo_roleplayer_result_role_1);
//        civResultRoleHeadImg1 = view.findViewById(R.id.civ_livevideo_roleplayer_result_role_head_1);
//        tvResultRoleScore1 = view.findViewById(R.id.tv_livevideo_roleplayer_result_role_score_1);
//        tvResultRoleName1 = view.findViewById(R.id.tv_livevideo_roleplayer_result_role_nickname_1);

//        rlResultRole2 = view.findViewById(R.id.rl_livevideo_roleplayer_result_role_2);
//        civResultRoleHeadImg2 = view.findViewById(R.id.civ_livevideo_roleplayer_result_role_head_2);
//        tvResultRoleScore2 = view.findViewById(R.id.tv_livevideo_roleplayer_result_role_score_2);
//        tvResultRoleName2 = view.findViewById(R.id.tv_livevideo_roleplayer_result_role_nickname_2);

//        rlResultRole3 = view.findViewById(R.id.rl_livevideo_roleplayer_result_role_3);
//        civResultRoleHeadImg3 = view.findViewById(R.id.civ_livevideo_roleplayer_result_role_head_3);
//        tvResultRoleScore3 = view.findViewById(R.id.tv_livevideo_roleplayer_result_role_score_3);
//        tvResultRoleName3 = view.findViewById(R.id.tv_livevideo_roleplayer_result_role_nickname_3);

        rlDZBubbleMessage = view.findViewById(R.id.rl_live_roleplayer_dz_message_bubble_main);
        int colors[] = {0x1936BC9B, 0x3236BC9B, 0x6436BC9B, 0x9636BC9B, 0xFF36BC9B};
        vwvSpeechVolume.setColors(colors);
        vwvSpeechVolume.setBackColor(Color.TRANSPARENT);
//        rlResult = view.findViewById(R.id.rl_live_roleplayer_result_main);

        iv_live_roleplayer_title = view.findViewById(R.id.iv_live_roleplayer_title);

        view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View view) {

            }

            @Override
            public void onViewDetachedFromWindow(View view) {
                logger.i("离开连麦界面，清除数据");
                mReadHandler.removeMessages(READ_MESSAGE);
                mRolePlayBll.realease();
                //释放所有正在播放的音频
                relaseAllAudioPlay();
                if (mEntity != null) {
                    mEntity = null;//防止结果页数据错乱，尤其点赞个数
                }

                onDestroy();

            }
        });
        return view;
    }

    /**
     * 释放所有正在播放的音频
     */
    private void relaseAllAudioPlay() {
        if (mRolePlayerOtherItem != null) {
            mRolePlayerOtherItem.relaseAudioPlay();
        }
        if (mRolePlayerSelfItem != null) {
            mRolePlayerSelfItem.relaseAudioPlay();
        }
        if (mReadHandler != null) {
            mReadHandler.sendEmptyMessage(STOP_ROLEPLAY);
        }
    }

    @Override
    public void initData() {
        //默认MATCH_WAIT_SECOND 后，匹配页消失
        rlRoleReadMain.setVisibility(View.GONE);
        final HashMap<String, String> assetFolders = new HashMap<String, String>();
        civMatchHead.setBorderWidth(SizeUtils.Dp2Px(mContext, 3));
        civMatchHead.setBorderColor(Color.WHITE);
        ImageLoader.with(BaseApplication.getContext()).load(UserBll.getInstance().getMyUserInfoEntity().getHeadImg())
                .into(civMatchHead);

        rlMatchPager.setVisibility(View.VISIBLE);
        rlMatchLottie.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mEntity != null) {
                    List<RolePlayerEntity.RolePlayerHead> rolePlayerHeads = mEntity.getLstRoleInfo();
                    List<RolePlayerEntity.RolePlayerMessage> rolePlayerMessages = mEntity.getLstRolePlayerMessage();
                    if (rolePlayerHeads != null && rolePlayerHeads.size() > 0 && rolePlayerMessages != null &&
                            rolePlayerMessages.size() > 0) {
                        rlMatchLottie.setVisibility(View.GONE);
                        rlMatchRoleList.setVisibility(View.VISIBLE);
                        roleConfirmPage(); //确定角色开始RolePlayer
                    } else {
                        rlMatchPager.setVisibility(View.GONE);
                        logger.i("无朗读数据,进人机");
                        XESToastUtils.showToast(mContext, "无朗读数据,进人机");
                        //进入人机
                        mRolePlayBll.goToRobot();
                    }
                } else {
                    rlMatchPager.setVisibility(View.GONE);
                    mLogtf.i("匹配失败,进人机");
                    XESToastUtils.showToast(mContext, "匹配失败,进人机");
                    mRolePlayBll.goToRobot();
                }

            }
        }, MATCH_WAIT_SECOND);

    }


    /**
     * 确定角色准备开始RolePlayer
     */
    private void roleConfirmPage() {
        try {
            mHeadShowAdapter = new RolePlayerHeadShowAdapter(mContext, mEntity.getLstRoleInfo());
            int roleHeadsSize = mEntity.getLstRoleInfo().size();
            //当角色小于3个的时候，为保证角色头像都居中显示，动态改变列数
            if (roleHeadsSize < 3) {
                gvRoleHeadShow.setNumColumns(roleHeadsSize);
                gvRoleHeadShow.setHorizontalSpacing(-20);
            } else {
                gvRoleHeadShow.setNumColumns(3);
                gvRoleHeadShow.setHorizontalSpacing(SizeUtils.Dp2Px(mContext, 42));
            }
            gvRoleHeadShow.setAdapter(mHeadShowAdapter);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //rlMatchPager.setVisibility(View.GONE);
                    //进入朗读页
                    waitRolePlayer();
                }
            }, WAIT_ROLE_HEAD_SHOW);
        } catch (Exception e) {
            logger.e("group success but exception :" + e.toString() + "；" + e.getMessage());
        }

    }

    /**
     * 准备好朗读数据显示，3秒倒计时准备RolePlayer
     */
    private void waitRolePlayer() {

        ///获取当前应该走的离线模型

        final int curSubModEva = ShareDataManager.getInstance().getInt(RolePlayConfig
                .KEY_FOR_WHICH_SUBJECT_MODEL_EVA, RolePlayConfig.VALUE_FOR_ENGLISH_MODEL_EVA, ShareDataManager
                .SHAREDATA_NOT_CLEAR);


        rlMatchPager.setVisibility(View.GONE);
        rlRoleReadMain.setVisibility(View.VISIBLE);

        tvCountTime.setText(getCountDownTime());
        rlRoleReadMain.setVisibility(View.VISIBLE);
        Typeface tFace = getTypeface(mContext);
        if (tFace != null) {
            tvBeginTipMsg.setTypeface(getTypeface(mContext));
        }
        if (mEntity != null && mEntity.getLstRolePlayerMessage().get(0).getRolePlayer().isSelfRole()) {
            tvBeginTipMsg.setText((curSubModEva == RolePlayConfig.VALUE_FOR_CHINESE_MODEL_EVA) ? "你先开始.准备好了吗？" : "You" +
                    " go first. Are you ready?");
            tvBeginTipMsg.postDelayed(new Runnable() {
                @Override
                public void run() {
                    switch (curSubModEva) {
                        case RolePlayConfig.VALUE_FOR_ENGLISH_MODEL_EVA:
                            //当前是英语离线测评
                            tvBeginTipMsg.setBackgroundResource(R.drawable.shape_livevideo_roleplayer_ready_go_bg);
                            tvBeginTipMsg.setText("GO");
                            break;
                        case RolePlayConfig.VALUE_FOR_CHINESE_MODEL_EVA:
                            //当前是语文离线测评
                            tvBeginTipMsg.setBackgroundResource(R.drawable.shape_livevideo_roleplayer_ready_go_bg);
                            tvBeginTipMsg.setText("开始");
                            break;
                        default:
                            //默认走英语离线测评
                            tvBeginTipMsg.setBackgroundResource(R.drawable.shape_livevideo_roleplayer_ready_go_bg);
                            tvBeginTipMsg.setText("GO");
                            break;
                    }

                    //tvBeginTipMsg.setPadding(60, 10, 60, 15);
                    tvBeginTipMsg.setGravity(Gravity.CENTER);

                }
            }, 2000);
        } else {
            tvBeginTipMsg.setText((curSubModEva == RolePlayConfig.VALUE_FOR_CHINESE_MODEL_EVA) ? "别着急.还没到你." : "Don't" +
                    " hurry. Not your turn yet.");
        }
        //开始倒计时，1秒更新一次
        tvCountTime.postDelayed(new Runnable() {
            @Override
            public void run() {
//                if (mEntity.getCountDownSecond() == 0) {
//                    //倒计时结束，时钟正走
//                    return;
//                }
                if (mEntity == null) {
                    logger.e("waitRolePlayer but test is empty");
                    return;
                }
                mEntity.setCountDownSecond(mEntity.getCountDownSecond() - 1);
                tvCountTime.setText(getCountDownTime());
                tvCountTime.postDelayed(this, 1000);
            }
        }, 1000);
//

        if (mEntity == null) {
            //防止此时，老师结束答题，程序闪退的问题
            return;
        }
        //填充对话内容
        mRolePlayerAdapter = new CommonAdapter<RolePlayerEntity.RolePlayerMessage>(mEntity.getLstRolePlayerMessage(),
                2) {
            @Override
            public AdapterItemInterface<RolePlayerEntity.RolePlayerMessage> getItemView(Object type) {
                if ((boolean) type) {
                    //自己朗读的
                    mRolePlayerSelfItem = new RolePlayerSelfItem(mContext, mRolePlayBll,mReadHandler);
                    return mRolePlayerSelfItem;
                } else {
                    //他人朗读的
                    mRolePlayerOtherItem = new RolePlayerOtherItem(mContext, mRolePlayBll,mReadHandler);
                    return mRolePlayerOtherItem;
                }
            }

            @Override
            public Object getItemViewType(RolePlayerEntity.RolePlayerMessage t) {
                return t.getRolePlayer().isSelfRole();
            }
        };

        try {
            lvReadList.setAdapter(mRolePlayerAdapter);
            lvReadList.setVisibility(View.VISIBLE);
            lvReadList.setDividerHeight(SizeUtils.Dp2Px
                    (mContext, 5));
            vHead = new View(mContext);
            //修改类型转换异常
            ListView.LayoutParams lp = new ListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, SizeUtils.Dp2Px
                    (mContext, 50));
            vHead.setLayoutParams(lp);
            lvReadList.addFooterView(vHead);
        } catch (Exception e) {
            logger.e("waitRolePlayer but test lvReadList may be null ：" + lvReadList + " : " + e.toString() + ":" + e.getMessage());
        }


        //整个前奏3秒后开始
        tvBeginTipMsg.postDelayed(new Runnable() {
            @Override
            public void run() {
                tvBeginTipMsg.setVisibility(View.INVISIBLE);
                beginRolePlayer();
            }
        }, 3000);


    }

    /**
     * 开始朗读下一条
     */
    private final static int READ_MESSAGE = 100;
    /**
     * 去评测
     */
    private final static int GO_SPEECH = 200;
    /**
     * 停止roleplay,及其音频播放
     */
    private static final int STOP_ROLEPLAY = 404;

    /**
     * 用来自动朗读
     */
    Handler mReadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                if (mEntity == null) {
                    logger.i("数据实体已经销毁，handler不再处理剩余消息");
                    return;
                }
                if (RolePlayerEntity.RolePlayerMessageStatus.CUR_PLAYING_ITEM_INDEX == msg.what){
                    int curPlayingIndex = (int) msg.obj;
                    logger.i("print_curPlayingIndex:" + curPlayingIndex);
                    mCurrentReadIndex = curPlayingIndex + 1;
                    return;
                }
                //主要为了停止音频
                if (STOP_ROLEPLAY == msg.what) {
                    logger.i("print_stop_role_play:" + mCurrentReadIndex);
                    int tempIndex = mCurrentReadIndex - 1;
                    if (tempIndex >= mEntity.getLstRolePlayerMessage().size()) {
                        return;
                    }
                    if (tempIndex < 0) {
                        tempIndex = 0;
                    }
                    RolePlayerEntity.RolePlayerMessage upMessage = mEntity.getLstRolePlayerMessage().get
                            (tempIndex);
                    upMessage.setMsgStatus(RolePlayerEntity.RolePlayerMessageStatus.END_ROLEPLAY);
                    if(mRolePlayerAdapter != null){
                        mRolePlayerAdapter.updataSingleRow(lvReadList, upMessage);
                    }
                    mEntity = null;
                    return;

                }
                if (msg.what == READ_MESSAGE) {
                    //恢复上一条的状态
                    if (mCurrentReadIndex > 0) {
                        int position = 0;
                        if (msg.obj != null) {
                            position = (int) msg.obj;
                        }
                        vwvSpeechVolume.stop();
                        if (position == 0) {
                            RolePlayerEntity.RolePlayerMessage upMessage = mEntity.getLstRolePlayerMessage().get
                                    (mCurrentReadIndex - 1);
                            if ((mCurrentReadIndex - 1) == mEntity.getSelfLastIndex()) {
                                mLogtf.i("handleMessage:isResult="+mEntity.isResult());
                                if (!mEntity.isResult()){
                                    if (mtype.isNewArts()) {
                                        mRolePlayBll.requestNewArtsResult();
                                    } else {
                                        mRolePlayBll.requestResult();
                                    }
                                }
                            }
                            if (upMessage.getMsgStatus() != RolePlayerEntity.RolePlayerMessageStatus.END_SPEECH) {
                                upMessage.setMsgStatus(RolePlayerEntity.RolePlayerMessageStatus.END_ROLEPLAY);
                            }
                            if (upMessage.getRolePlayer().isSelfRole()) {
                                //自己朗读完毕，只通知除自己以外的其他组内成员
                                mRolePlayBll.selfReadEnd(upMessage.getStars(), upMessage.getSpeechScore(), upMessage
                                                .getFluency(), upMessage.getAccuracy(), upMessage.getPosition(), mEntity,
                                        upMessage.getRolePlayer().getRoleId());
                            }
                            mRolePlayerAdapter.updataSingleRow(lvReadList, upMessage);

                        } else {
                            for (int i = position; i >= mCurrentReadIndex - 1; i--) {
                                RolePlayerEntity.RolePlayerMessage tempMessage = mEntity.getLstRolePlayerMessage().get(i);
                                if (tempMessage.getMsgStatus() != RolePlayerEntity.RolePlayerMessageStatus.END_SPEECH) {
                                    tempMessage.setMsgStatus(RolePlayerEntity.RolePlayerMessageStatus.END_ROLEPLAY);
                                }
                                mRolePlayerAdapter.updataSingleRow(lvReadList, tempMessage);
                            }
                            mCurrentReadIndex = position + 1;

                        }
                        if (mIse != null) {

                            mIse.cancel();
                        }
                    }
                    if (mCurrentReadIndex == (mEntity.getLstRolePlayerMessage().size())) {
                        //已经对话完毕
                        endRolePlayer();
                        return;
                    } else {
                        //lvReadList.smoothScrollToPosition(mCurrentReadIndex + 1);

                        if (mCurrentReadIndex == 1) {
                            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) tvBeginTipMsg
                                    .getLayoutParams();
                            int animDistance = tvBeginTipMsg.getHeight() + layoutParams.topMargin;
                            ObjectAnimator oaAnimTransY = ObjectAnimator.ofFloat(tvBeginTipMsg, ImageView.TRANSLATION_Y,
                                    0, -animDistance * 3 / 2);
                            oaAnimTransY.setInterpolator(new AccelerateInterpolator());
                            oaAnimTransY.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {

                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            });
                            oaAnimTransY.setDuration(500);
                            oaAnimTransY.start();
                            tvBeginTipMsg.setVisibility(View.GONE);
                            lvReadList.setSelection(mCurrentReadIndex);
                            logger.i("滚动到下一条" + mCurrentReadIndex);
                            logger.i("第一条读完了，将提示带着平滑动画消失");


                        } else {
                            lvReadList.setSelection(mCurrentReadIndex);
                            logger.i("滚动到下一条" + mCurrentReadIndex);
                        }


                    }

                    //取出当前这条的延时时间
                    RolePlayerEntity.RolePlayerMessage currentMessage = mEntity.getLstRolePlayerMessage().get
                            (mCurrentReadIndex);
                    currentMessage.setMsgStatus(RolePlayerEntity.RolePlayerMessageStatus.BEGIN_ROLEPLAY);
                    mRolePlayerAdapter.updataSingleRow(lvReadList, currentMessage);
                    speechReadMessage(currentMessage, mEntity);

                    mCurrentReadIndex++;
                    Message temp = mReadHandler.obtainMessage();
                    temp.what = READ_MESSAGE;
                    mLogtf.i("currentMessage.getMaxReadTime() = " + currentMessage.getMaxReadTime()+",mIsEnd="+mIsEnd);
                    if(!mIsEnd){
                        mReadHandler.sendMessageDelayed(temp, (currentMessage.getMaxReadTime()) * 1000);
                        mReadHandler.sendEmptyMessageDelayed(GO_SPEECH, (currentMessage.getMaxReadTime() - 1) * 1000);
                    }
                } else if (msg.what == GO_SPEECH) {
                    //结束评测
                    if (mIse != null) {
                        mIse.stop();
                    }
                }
            } catch (Exception e) {
                logger.i("exception when begin roleplay");
            }

        }
    };

    /**
     * 开始进入RolePlayer对话
     */
    private void beginRolePlayer() {
        mReadHandler.sendEmptyMessage(READ_MESSAGE);
        mWorkerThread.setOnEngineCreate(new WorkerThread.OnEngineCreate() {
            @Override
            public void onEngineCreate(RtcEngine mRtcEngine) {
                logger.i("agora rtcEngine init");
                rtcEngine = mRtcEngine;
            }
        });
        //开启声网连接
        mWorkerThread.start();
        mWorkerThread.waitForReady();

        int vProfile = Constants.VIDEO_PROFILE_120P;
        mWorkerThread.configEngine(Constants.CLIENT_ROLE_BROADCASTER, vProfile);
        if (mEntity == null) {
            if (mRolePlayBll != null) {
                mRolePlayBll.closeCurPage();
            }
            return;
        }
        mWorkerThread.joinChannel(null, mEntity.getLiveId() + "_" + mEntity.getTestId() + "_" + mEntity.getTeamId(),
                Integer.parseInt(LiveAppUserInfo.getInstance().getStuId()), new WorkerThread
                        .OnJoinChannel() {
                    @Override
                    public void onJoinChannel(int joinChannel) {
                        logger.i("agora onJoinChannel" + joinChannel);
                    }
                });
    }

    /**
     * 获取字体
     *
     * @param context
     * @return
     */
    public Typeface getTypeface(Context context) {
        Typeface tf = null;
        try {
            tf = Typeface.createFromAsset(context.getAssets(), "fangzhengcuyuan.ttf");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return tf;
    }

    /**
     * 显示结果
     */
    public void showResult() {

        if (isShowResult) {
            logger.i("结果页已经在显示");
            mRolePlayBll.cancelDZ();//取消点赞
            return;
        }


        isShowResult = true;
        logger.i("显示结果,记录日志");
        //显示结果的时候记录日志
        RolePlayLog.sno7(liveAndBackDebug, mEntity, mContext);
        tvBeginTipMsg.setVisibility(View.GONE);//readgo不再占位

        vwvSpeechVolume.stop();
        rlSpeechVolumnMain.setVisibility(View.INVISIBLE);
        vwvSpeechVolume.setVisibility(View.GONE);
        //XESToastUtils.showToast(mContext, "结束");
//        rlResult.setVisibility(View.VISIBLE);

        ///获取当前应该走的离线模型
        final int curSubModEva = ShareDataManager.getInstance().getInt(RolePlayConfig
                .KEY_FOR_WHICH_SUBJECT_MODEL_EVA, RolePlayConfig.VALUE_FOR_ENGLISH_MODEL_EVA, ShareDataManager
                .SHAREDATA_NOT_CLEAR);

        if (mEntity == null) {
            logger.i("需要显示结果弹窗，可是数据为空,不再往下执行，恢复滑动，取消点赞，离开频道");
            recoverListScrollAndCancelDZ();
            leaveChannel();
            return;

        }
        List<RolePlayerEntity.RolePlayerHead> lstHead = mEntity.getResultRoleList();
        RolePlayerEntity.RolePlayerHead head = mEntity.getSelfRoleHead();


        if (head != null) {
            Typeface tFace = getTypeface(mContext);
//            if (tFace != null) {
//                tvResultMsgTip.setTypeface(getTypeface(mContext));
//                tvTotalScore.setTypeface(getTypeface(mContext));
//            }
//            if (head.getSpeechScore() >= 90) {
//                tvResultMsgTip.setText((curSubModEva == RolePlayConfig.VALUE_FOR_CHINESE_MODEL_EVA) ? "天才" :
//                        "Fantastic");
//
//            } else if (head.getSpeechScore() >= 60) {
//                tvResultMsgTip.setText((curSubModEva == RolePlayConfig.VALUE_FOR_CHINESE_MODEL_EVA) ? "不错哦" :
//                        "Welldone");
//            } else {
//                tvResultMsgTip.setText((curSubModEva == RolePlayConfig.VALUE_FOR_CHINESE_MODEL_EVA) ? "加油哦" :
//                        "Fighting");
//            }

//            if (head.getSpeechScore() >= 0 && head.getSpeechScore() < 40) {
//                ivRoleplayerResultStar.setImageResource(R.drawable.livevideo_roleplay_alertview_pic_xingxing1);
//                head.setResultStar(1);
//            }
//            if (head.getSpeechScore() >= 40 && head.getSpeechScore() < 60) {
//                ivRoleplayerResultStar.setImageResource(R.drawable.livevideo_roleplay_alertview_pic_xingxing2);
//                head.setResultStar(2);
//            }
//            if (head.getSpeechScore() >= 60 && head.getSpeechScore() < 75) {
//                ivRoleplayerResultStar.setImageResource(R.drawable.livevideo_roleplay_alertview_pic_xingxing3);
//                head.setResultStar(3);
//            }
//            if (head.getSpeechScore() >= 75 && head.getSpeechScore() < 90) {
//                ivRoleplayerResultStar.setImageResource(R.drawable.livevideo_roleplay_alertview_pic_xingxing4);
//                head.setResultStar(4);
//            }
//            if (head.getSpeechScore() >= 90) {
//                ivRoleplayerResultStar.setImageResource(R.drawable.livevideo_roleplay_alertview_pic_xingxing5);
//                head.setResultStar(5);
//            }


//            tvTotalScore.setText(head.getSpeechScore() + "分");
//            tvDzCount.setText(mEntity.getPullDZCount() + "");
//            tvFluency.setText("流畅性:" + head.getFluency());
//            tvGoldCount.setText(mEntity.getGoldCount() + "");
//            tvAccuracy.setText("准确性:" + head.getAccuracy());

            /** catch exception:java.lang.IllegalArgumentException: You cannot start a load for a destroyed activity;
             *
             *  at com.bumptech.glide.manager.RequestManagerRetriever.assertNotDestroyed(RequestManagerRetriever
             *  .java:284) at com.bumptech.glide.manager.RequestManagerRetriever.get(RequestManagerRetriever.java:145)
             *  at com.bumptech.glide.manager.RequestManagerRetriever.get(RequestManagerRetriever.java:111)
             *  at com.bumptech.glide.Glide.with(Glide.java:554)
             *
             * 通过查看源码发现，异常的原因在于，当前activity已经销毁，所以无法加载角色头像
             *
             * */
//            if (mContext instanceof Activity) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//                    if (!((Activity) mContext).isDestroyed()) {
//                        ImageLoader.with(mContext).load(UserBll.getInstance().getMyUserInfoEntity().getHeadImg())
//                                .into(civResultHeadImg);
//                    }
//                }
//            }

//            if (lstHead.size() >= 1) {
//                RolePlayerEntity.RolePlayerHead head1 = lstHead.get(0);
//                tvResultRoleScore1.setText(head1.getSpeechScore() + "分");
//                tvResultRoleName1.setText(head1.getNickName());
//                ImageLoader.with(ContextManager.getApplication()).load(head1.getHeadImg()).into(civResultRoleHeadImg1);
//                civResultRoleHeadImg1.setBorderWidth(SizeUtils.Dp2Px(mContext, 2));
//                if (head1.isSelfRole()) {
//                    civResultRoleHeadImg1.setBorderColor(Color.parseColor("#FAD2D1"));
//                    tvResultRoleScore1.setTextColor(Color.parseColor("#333333"));
//                    tvResultRoleName1.setTextColor(Color.parseColor("#333333"));
//                } else {
//                    civResultRoleHeadImg1.setBorderColor(Color.parseColor("#E0E0E0"));
//                    tvResultRoleScore1.setTextColor(Color.parseColor("#666666"));
//                    tvResultRoleName1.setTextColor(Color.parseColor("#666666"));
//                }
//            } else {
//                rlResultRole1.setVisibility(View.INVISIBLE);
//            }
//
//            if (lstHead.size() >= 2) {
//                RolePlayerEntity.RolePlayerHead head2 = lstHead.get(1);
//                tvResultRoleScore2.setText(head2.getSpeechScore() + "分");
//                tvResultRoleName2.setText(head2.getNickName());
//                ImageLoader.with(ContextManager.getApplication()).load(head2.getHeadImg()).into(civResultRoleHeadImg2);
//                civResultRoleHeadImg2.setBorderWidth(SizeUtils.Dp2Px(mContext, 2));
//                if (head2.isSelfRole()) {
//                    civResultRoleHeadImg2.setBorderColor(Color.parseColor("#FAD2D1"));
//                    tvResultRoleScore2.setTextColor(Color.parseColor("#333333"));
//                    tvResultRoleName2.setTextColor(Color.parseColor("#333333"));
//                } else {
//                    civResultRoleHeadImg2.setBorderColor(Color.parseColor("#E0E0E0"));
//                    tvResultRoleScore2.setTextColor(Color.parseColor("#666666"));
//                    tvResultRoleName2.setTextColor(Color.parseColor("#666666"));
//                }
//            } else {
//                rlResultRole2.setVisibility(View.INVISIBLE);
//            }
//
//            if (lstHead.size() >= 3) {
//                RolePlayerEntity.RolePlayerHead head3 = lstHead.get(2);
//                tvResultRoleScore3.setText(head3.getSpeechScore() + "分");
//                tvResultRoleName3.setText(head3.getNickName());
//                ImageLoader.with(ContextManager.getApplication()).load(head3.getHeadImg()).into(civResultRoleHeadImg3);
//                civResultRoleHeadImg3.setBorderWidth(SizeUtils.Dp2Px(mContext, 2));
//                if (head3.isSelfRole()) {
//                    civResultRoleHeadImg3.setBorderColor(Color.parseColor("#FAD2D1"));
//                    tvResultRoleScore3.setTextColor(Color.parseColor("#333333"));
//                    tvResultRoleName3.setTextColor(Color.parseColor("#333333"));
//                } else {
//                    civResultRoleHeadImg3.setBorderColor(Color.parseColor("#E0E0E0"));
//                    tvResultRoleScore3.setTextColor(Color.parseColor("#666666"));
//                    tvResultRoleName3.setTextColor(Color.parseColor("#666666"));
//                }
//            } else {
//                rlResultRole3.setVisibility(View.INVISIBLE);
//            }
            ViewGroup group = (ViewGroup) mView;
            if (!mLiveGetInfo.getSmallEnglish()) {
                //初中结果页
                RolePlayResultPager rolePlayResultPager = new RolePlayResultPager(mContext, mEntity, group);
                group.addView(rolePlayResultPager.getRootView());
                resultPager=rolePlayResultPager;
            } else {
                //小学结果页
                SpeechResultEntity speechResultEntity = new SpeechResultEntity();
                speechResultEntity.score = head.getSpeechScore();
                speechResultEntity.gold = mEntity.getGoldCount();
                speechResultEntity.energy = mEntity.getEnergy();
                speechResultEntity.fluency = head.getFluency();
                speechResultEntity.accuracy = head.getAccuracy();
                speechResultEntity.headUrl = mLiveGetInfo.getHeadImgPath();
                speechResultEntity.praise = mEntity.getPullDZCount();
                ArrayList<SpeechResultMember> speechResultMembers = speechResultEntity.speechResultMembers;
                for (int i = 0; i < lstHead.size(); i++) {
                    RolePlayerEntity.RolePlayerHead head1 = lstHead.get(i);
                    SpeechResultMember speechResultMember = new SpeechResultMember();
                    speechResultMember.isSelfRole = head1.isSelfRole();
                    speechResultMember.name = head1.getNickName();
                    speechResultMember.score = head1.getSpeechScore();
                    speechResultMember.headUrl = head1.getHeadImg();
                    speechResultMembers.add(speechResultMember);
                }
                SpeechResultPager resultPager = new SpeechResultPager(mContext, group, speechResultEntity, mLiveGetInfo);
                group.addView(resultPager.getRootView());
                RolePlayerPager.this.resultPager = resultPager;
            }
        }

        //结果弹窗5秒后消失
        mView.postDelayed(new Runnable() {
            @Override
            public void run() {
                recoverListScrollAndCancelDZ();

                //isShowResult = false;
            }
        }, 5000);
        leaveChannel();
    }

    /**
     * 恢复页面滑动，取消点赞
     */
    public void recoverListScrollAndCancelDZ() {
        //恢复listview可滑动
        mIsListViewUnSroll = false;
//        if (rlResult != null) {
//            rlResult.setVisibility(View.GONE);
//        }
        if(resultPager!=null&&resultPager.getRootView()!=null){
            ViewGroup group= (ViewGroup) resultPager.getRootView().getParent();
            if(group!=null){
                group.removeView(resultPager.getRootView());
            }
            resultPager=null;
        }
        if (lvReadList != null) {
            lvReadList.setUnScroll(mIsListViewUnSroll);//恢复列表滑动
        }
        if (mRolePlayBll != null) {
            mRolePlayBll.cancelDZ();//取消点赞
        }

    }

    /**
     * 对话结束后，离开频道
     */
    public void leaveChannel() {
        try {
            if (mWorkerThread != null) {
                mWorkerThread.leaveChannel(mWorkerThread.getEngineConfig().mChannel, new WorkerThread.OnLeaveChannel() {
                    @Override
                    public void onLeaveChannel(int leaveChannel) {
                        StableLogHashMap logHashMap = new StableLogHashMap("getLeaveChannel");
                        logHashMap.put("status", (leaveChannel == 0 ? "1" : "0"));
                        if (leaveChannel != 0) {
                            logHashMap.put("errcode", "" + leaveChannel);
                        }
                    }
                });
                mWorkerThread.exit();
                try {
                    mWorkerThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mWorkerThread = null;
            }
        } catch (Exception e) {
            logger.i("rolePlay leave channel exception");
        }

    }

    /**
     * 结束RolePlayer
     */
    private void endRolePlayer() {
        if (mEntity == null) {
            logger.i("roleplay界面的数据已经销毁，不再向下执行");
            return;
        }
        if (!mEntity.isResult()) {
            logger.i("结束RolePlayer,结果还未提交，再次提交结果");
            if (mtype.isNewArts()) {
                mRolePlayBll.requestNewArtsResult();
            } else {
                mRolePlayBll.requestResult();
            }
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showResult();//延迟2秒显示结果页
                }
            }, 2000);

        }
    }


    /**
     * 进入自己朗读评测
     *
     * @param message
     * @param entity
     */

    private void speechReadMessage(final RolePlayerEntity.RolePlayerMessage message, final RolePlayerEntity entity) {

        //解决别人在读的时候，安卓端还在录音导致出现噪音的情况
        if (mWorkerThread == null) {
            return;
        }
        rtcEngine = mWorkerThread.getRtcEngine();

        if (!message.getRolePlayer().isSelfRole()) {
            if (rtcEngine != null) {
                rtcEngine.muteLocalAudioStream(true);
                logger.i("agora rtcEngine stop other");
            }
            //对方朗读则隐藏
            rlSpeechVolumnMain.setVisibility(View.INVISIBLE);
            vwvSpeechVolume.setVisibility(View.GONE);
            return;
        }

        if (rtcEngine != null) {
            rtcEngine.muteLocalAudioStream(false);
            rtcEngine.adjustRecordingSignalVolume(400);
            logger.i("agora rtcEngine start self");
        }


        rlSpeechVolumnMain.setVisibility(View.VISIBLE);
        vwvSpeechVolume.setVisibility(View.VISIBLE);
        String spechMsg = message.getReadMsg().replace("\n", "");
        logger.i("待测评的文本" + spechMsg);

        final int curSubModEva = ShareDataManager.getInstance().getInt(RolePlayConfig
                .KEY_FOR_WHICH_SUBJECT_MODEL_EVA, RolePlayConfig.VALUE_FOR_ENGLISH_MODEL_EVA, ShareDataManager
                .SHAREDATA_NOT_CLEAR);
        saveVideoFile = new File(dir, "roleplayer_machine_" + System.currentTimeMillis() + ".mp3");
        switch (curSubModEva) {
            case RolePlayConfig.VALUE_FOR_ENGLISH_MODEL_EVA:
                //走英语离线测评
                mLogtf.i(TAG+"走英语离线测评");
                mIse = SpeechUtils.getInstance(mContext.getApplicationContext());
                mIse.setLanguage(com.tal.speech.speechrecognizer.Constants.ASSESS_PARAM_LANGUAGE_EN);

                break;
            case RolePlayConfig.VALUE_FOR_CHINESE_MODEL_EVA:
                //走语文离线测评
                mLogtf.i(TAG+"走语文离线测评");
                mIse = SpeechUtils.getInstance(mContext.getApplicationContext());
                mIse.setLanguage(com.tal.speech.speechrecognizer.Constants
                        .ASSESS_PARAM_LANGUAGE_CH);
                break;
            default:
                //默认走英语离线测评
                mLogtf.i(TAG+"走英语离线测评");
                mIse = SpeechUtils.getInstance(mContext.getApplicationContext());
                mIse.setLanguage(com.tal.speech.speechrecognizer.Constants.ASSESS_PARAM_LANGUAGE_EN);
                break;
        }
        mIse.prepar();
        mIse.cancel();
        if (param == null){
            param = new SpeechParamEntity();
        }
        param.setStrEvaluator(spechMsg);
        param.setLocalSavePath(saveVideoFile.getAbsolutePath());
        param.setMultRef(false);
        param.setPcm(true);
        param.setRecogType(SpeechConfig.SPEECH_ENGLISH_EVALUATOR_OFFLINE);
        mLogtf.i("multi_roleplay_old_sdk_onBeginOfSpeech:"+spechMsg);
        mIse.startRecog(param,new RoleEvaluatorListener() {
            @Override
            public void onBeginOfSpeech() {
                logger.i( "开始测评");
                mLogtf.i("multi_roleplay_old_sdk_onBeginOfSpeech");
                vwvSpeechVolume.start();
            }

            @Override
            public void onResult(ResultEntity resultEntity) {
                if (resultEntity.getStatus() == ResultEntity.SUCCESS) {
                    logger.i("测评成功，开始上传自己的mp3,开口时长：" + resultEntity.getSpeechDuration()
                            + "得分：" + resultEntity.getScore());
                    entity.setSelfValidSpeechTime(resultEntity.getSpeechDuration());
                    message.setSelfValidSpeechTime(resultEntity.getSpeechDuration());
                    //mIsEvaluatoring = false;
                    message.setMsgStatus(RolePlayerEntity.RolePlayerMessageStatus.END_SPEECH);
                    message.setSpeechScore(resultEntity.getScore());
                    message.setLstPhoneScore(resultEntity.getLstPhonemeScore());
                    message.setFluency(resultEntity.getContScore());
                    message.setAccuracy(resultEntity.getPronScore());
                    message.setWebVoiceUrl(saveVideoFile.getAbsolutePath());
                    message.setLevel(resultEntity.getLevel());
                    //上传自己读完的语句，只通知除了自己以外的其他组内成员
                    mRolePlayBll.uploadFileToAliCloud(saveVideoFile.getAbsolutePath(), message, entity,
                            message.getRolePlayer().getRoleId());
                    //XESToastUtils.showToast(mContext, resultEntity.getScore() + "");
                    //提前开始下一条
                    if(!mIsEnd){
                        nextReadMessage();
                    }
                } else if (resultEntity.getStatus() == ResultEntity.ERROR) {
                    mLogtf.i("onResult:errorNo=" + resultEntity.getErrorNo() + " 不上传自己的mp3");
                    //XESToastUtils.showToast(mContext, "测评失败");
                    //mIsEvaluatoring = false;
                    message.setMsgStatus(RolePlayerEntity.RolePlayerMessageStatus.END_SPEECH);
                    //提前开始下一条
                    if(!mIsEnd){
                        nextReadMessage();
                    }
                } else if (resultEntity.getStatus() == ResultEntity.EVALUATOR_ING) {
                    // Loger.i("RolePlayerDemoTest", "测评中");

                }
            }

            @Override
            public void onVolumeUpdate(int volume) {
                logger.i("agora rtcEngine onVolumeUpdate:"+volume);
                vwvSpeechVolume.setVolume(volume * 3);
            }

            @Override
            public void onRecordPCMData(short[] shorts, int readSize) {
                // Loger.i("RolePlayerDemoTest", "通过声网走");
                logger.i("agora rtcEngine onRecordPCMData ");
                //通过声网走
                byte[] dest = new byte[readSize * 2];
                int count = readSize;
                for (int i = 0; i < count; i++) {
                    dest[i * 2] = (byte) (shorts[i]);
                    dest[i * 2 + 1] = (byte) (shorts[i] >> 8);
                }
                rtcEngine = mWorkerThread.getRtcEngine();
                if (rtcEngine != null) {
                    logger.i("agora rtcEngine through");
                    rtcEngine.pushExternalAudioFrame(dest, System.currentTimeMillis());
                    rtcEngine.adjustRecordingSignalVolume(400);
                }else {
                    logger.i("agora rtcEngine is null");
                }

            }

        });
    }

    /**
     * 收到点赞消息
     */
    public void showDZ(String roleName) {
        mEntity.setPullDZCount(mEntity.getPullDZCount() + 1);
        final View view = View.inflate(mContext, R.layout.layout_livevideo_roleplayer_bubble_message_dz, null);
        TextView tvMessage = view.findViewById(R.id.tv_livevideo_roleplayer_bubble_message);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, R.id.rl_live_roleplayer_dz_message_bubble_main);
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL, R.id.rl_live_roleplayer_dz_message_bubble_main);
        tvMessage.setText(roleName + "给你点赞啦~");
        Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.slide_bubble_out_to_top);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        rlDZBubbleMessage.removeView(view);
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        rlDZBubbleMessage.addView(view, lp);
        view.startAnimation(anim);
    }


    /**
     * 提前开始下一条
     */
    private void nextReadMessage() {
        mReadHandler.removeMessages(GO_SPEECH);
        mReadHandler.removeMessages(READ_MESSAGE);
        mReadHandler.sendEmptyMessage(READ_MESSAGE);
    }

    /**
     * 对方提前读完
     *
     * @param position
     */
    public void nextRextMessage(int position) {
        if (position == (mCurrentReadIndex - 1)) {
            nextReadMessage();
        } else if (position < (mCurrentReadIndex - 1)) {
            return;
        } else {
            //提前超过一行结束
            mReadHandler.removeMessages(GO_SPEECH);
            mReadHandler.removeMessages(READ_MESSAGE);
            Message msg = Message.obtain();
            msg.what = READ_MESSAGE;
            msg.obj = position;
            mReadHandler.sendMessage(msg);
        }
    }

    /**
     * 角色扮演列表适配器
     */
    public class RolePlayerHeadShowAdapter extends BaseAdapter {

        List<RolePlayerEntity.RolePlayerHead> lstRolePlayerHead;

        public RolePlayerHeadShowAdapter(Context context, List<RolePlayerEntity.RolePlayerHead>
                lstHead) {
            this.lstRolePlayerHead = lstHead;
        }

        @Override
        public int getCount() {
            return lstRolePlayerHead.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;
            if (convertView == null) {
                holder = new Holder();
                convertView = View.inflate(mContext, R.layout.item_live_roleplayer_rolehead, null);
                holder.tvNickName = convertView.findViewById(R.id.tv_live_roleplayer_item_rolehead_nickname);
                holder.civHeadImg = convertView.findViewById(R.id.civ_roleplayer_item_rolehead_img);
                holder.ivHeadShadow = convertView.findViewById(R.id.iv_roleplayer_item_shadow);
                holder.tvRoleName = convertView.findViewById(R.id.tv_live_roleplayer_item_rolename);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            setData(lstRolePlayerHead.get(position), holder);
            return convertView;
        }

        private void setData(RolePlayerEntity.RolePlayerHead entity, Holder holder) {
            holder.tvNickName.setText(entity.getNickName());
            holder.tvRoleName.setText(entity.getRoleName());
            ImageLoader.with(ContextManager.getApplication()).load(entity.getHeadImg()).into(holder.civHeadImg);
            if (entity.isSelfRole()) {
                holder.tvNickName.setTextColor(Color.parseColor("#36BC9B"));
                holder.civHeadImg.setBorderColor(Color.parseColor("#36BC9B"));
                holder.civHeadImg.setBorderWidth(SizeUtils.Dp2Px(mContext, 3));
                holder.tvRoleName.setTextColor(Color.parseColor("#36BC9B"));
                holder.ivHeadShadow.setVisibility(View.VISIBLE);

            } else {
                holder.civHeadImg.setBorderColor(Color.WHITE);
                holder.ivHeadShadow.setVisibility(View.INVISIBLE);
                holder.civHeadImg.setBorderWidth(SizeUtils.Dp2Px(mContext, 3));
            }
        }


        class Holder {
            /**
             * 昵称
             */
            private TextView tvNickName;
            /**
             * 阴影
             */
            private ImageView ivHeadShadow;
            /**
             * 头像
             */
            private CircleImageView civHeadImg;
            /**
             * 角色名
             */
            private TextView tvRoleName;
        }
    }

    /**
     * 返回当前的倒计时
     */
    private SpannableString getCountDownTime() {
        long countTime = 3000;//默认三秒
        boolean isFu = false;
        if (mEntity != null) {
            if (mEntity.getCountDownSecond() < 0) {
                isFu = true;
                countTime = Math.abs(mEntity.getCountDownSecond());
            } else {
                countTime = mEntity.getCountDownSecond();
            }
        }

        long min = countTime / 60;
        long sec = countTime % 60;
        long hour = min / 60;
        min %= 60;
        if (hour == 0) {
            SpannableString span = new SpannableString(min + "分" + sec + "秒");
            if (isFu) {
                span.setSpan(new ForegroundColorSpan(Color.RED), 0, span.length(),
                        Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            }
            return span;
        } else {
            SpannableString span = new SpannableString(hour + "时" + min + "分" + sec + "秒");
            if (isFu) {
                span.setSpan(new ForegroundColorSpan(Color.RED), 0, span.length(),
                        Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            }
            return span;
        }
    }

    public void stopSpeech(){
        if (mIse != null) {
            mIse.stop();
        }
        mIsEnd=true;
        mReadHandler.removeMessages(GO_SPEECH);
        mReadHandler.removeMessages(READ_MESSAGE);
        if (mEntity!=null&&!mEntity.isResult()&&mRolePlayBll.getRoleEntry()!=null) {
            if (mEntity.isNewArts()) {
                mRolePlayBll.requestNewArtsResult();
            } else {
                mRolePlayBll.requestResult();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        logger.i("Roleplayer destory，清除数据");
        if (mIse != null) {
            mIse.cancel();
        }

        if (mRolePlayBll != null) {
            mRolePlayBll.realease();
        }

        mReadHandler.removeMessages(READ_MESSAGE);
        if (mEntity != null) {
            mEntity = null;//防止结果页数据错乱，尤其点赞个数
        }
        if (mLiveGetInfo != null) {
            mLiveGetInfo = null;
        }
        isShowResult = false;

        if (mRolePlayerSelfItem != null) {
            mRolePlayerSelfItem.stopVoicePlay();
        }
        if (mRolePlayerOtherItem != null) {
            mRolePlayerOtherItem.stopVoicePlay();
        }
        relaseAllAudioPlay();

    }

    /**
     * 更新指定对话的数据样式
     *
     * @param entity
     */
    public void updateRolePlayList(RolePlayerEntity.RolePlayerMessage entity) {
        if (mRolePlayerAdapter != null && lvReadList != null) {
            mRolePlayerAdapter.updataSingleRow(lvReadList, entity);
        }
    }

    /**
     * 带PCM音频数据的回调
     */
    interface RoleEvaluatorListener extends EvaluatorListenerWithPCM, EvaluatorListener {

    }

    @Override
    public boolean onUserBackPressed() {
        logger.i("点击返回");
        if (mRolePlayBll != null && mRolePlayBll.getRolePlayPager() != null && mRolePlayBll.getBottomView() != null) {
            mRolePlayBll.closeCurPage();
            return true;
        }
        return super.onUserBackPressed();
    }

}
