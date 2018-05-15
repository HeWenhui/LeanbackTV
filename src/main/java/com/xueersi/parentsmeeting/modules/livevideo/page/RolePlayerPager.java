package com.xueersi.parentsmeeting.modules.livevideo.page;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tal.speech.speechrecognizer.EvaluatorListener;
import com.tal.speech.speechrecognizer.EvaluatorListenerWithPCM;
import com.tal.speech.speechrecognizer.ResultEntity;
import com.tal.speech.speechrecognizer.SpeechEvaluatorInter;
import com.xueersi.parentsmeeting.base.BasePager;
import com.xueersi.parentsmeeting.entity.BaseVideoQuestionEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.activity.item.RolePlayerOtherItem;
import com.xueersi.parentsmeeting.modules.livevideo.activity.item.RolePlayerSelfItem;
import com.xueersi.parentsmeeting.modules.livevideo.business.RolePlayAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.RolePlayerBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.agora.WorkerThread;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RolePlayerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.loginregisters.business.UserBll;
import com.xueersi.parentsmeeting.speech.SpeechEvaluatorUtils;
import com.xueersi.parentsmeeting.widget.VolumeWaveView;
import com.xueersi.xesalib.adapter.AdapterItemInterface;
import com.xueersi.xesalib.adapter.CommonAdapter;
import com.xueersi.xesalib.utils.app.ContextManager;
import com.xueersi.xesalib.utils.app.XESToastUtils;
import com.xueersi.xesalib.utils.log.Loger;
import com.xueersi.xesalib.utils.uikit.SizeUtils;
import com.xueersi.xesalib.utils.uikit.imageloader.ImageLoader;
import com.xueersi.xesalib.view.image.CircleImageView;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import io.agora.rtc.Constants;


/**
 * RolePlayer多人语音连麦
 * Created by zouhao on 2018/3/29.
 */

public class RolePlayerPager extends BasePager<RolePlayerEntity> {

    /**
     * 匹配页默认停留时间
     */
    private final int MATCH_WAIT_SECOND = 3000;
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
    private RelativeLayout rlResult;

    /**
     * 角色展示区适配器
     */
    private RolePlayerHeadShowAdapter mHeadShowAdapter;

    /**
     * 对话区
     */
    private ListView lvReadList;

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

    /**
     * 语音评测
     */
    protected SpeechEvaluatorUtils mIse;
    private SpeechEvaluatorInter speechEvaluatorInter;
    private File saveVideoFile, dir;
    /**
     * 声网
     */
    private WorkerThread mWorkerThread;

    /**
     * 结果总分
     */
    private TextView tvTotalScore;
    /**
     * 获得的点赞数
     */
    private TextView tvDzCount;
    /**
     * 流畅性
     */
    private TextView tvFluency;
    /**
     * 金币数
     */
    private TextView tvGoldCount;
    /**
     * 准确性
     */
    private TextView tvAccuracy;
    /**
     * 结果页自己的头像
     */
    private CircleImageView civResultHeadImg;
    /**
     * 总评
     */
    private TextView tvResultMsgTip;

    /**
     * 排名1
     */
    private RelativeLayout rlResultRole1;
    /**
     * 排名1头像
     */
    private CircleImageView civResultRoleHeadImg1;
    /**
     * 排名1分数
     */
    private TextView tvResultRoleScore1;
    /**
     * 排名1名字
     */
    private TextView tvResultRoleName1;

    /**
     * 排名2
     */
    private RelativeLayout rlResultRole2;
    /**
     * 排名2头像
     */
    private CircleImageView civResultRoleHeadImg2;
    /**
     * 排名2分数
     */
    private TextView tvResultRoleScore2;
    /**
     * 排名2名字
     */
    private TextView tvResultRoleName2;

    /**
     * 排名3
     */
    private RelativeLayout rlResultRole3;
    /**
     * 排名3头像
     */
    private CircleImageView civResultRoleHeadImg3;
    /**
     * 排名3分数
     */
    private TextView tvResultRoleScore3;
    /**
     * 排名3名字
     */
    private TextView tvResultRoleName3;

    public RolePlayerPager(Context context, RolePlayerEntity obj, boolean isNewView, RolePlayerBll rolePlayerBll) {
        super(context, obj, isNewView);
        this.mRolePlayBll = rolePlayerBll;
        dir = new File(Environment.getExternalStorageDirectory(), "parentsmeeting/liveSpeech/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        mWorkerThread = new WorkerThread(ContextManager.getApplication(), Integer.parseInt(UserBll.getInstance()
                .getMyUserInfoEntity().getStuId()), true, true);


    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.pager_roleplayer, null);
        rlMatchPager = view.findViewById(R.id.rl_live_roleplayer_matchpager);
        rlMatchLottie = view.findViewById(R.id.rl_live_roleplayer_match_lottie);
        rlMatchRoleList = view.findViewById(R.id.rl_live_roleplayer_rolelist);
        rlMatchPager.setVisibility(View.VISIBLE);
        tvCountTime = view.findViewById(R.id.tv_live_roleplayer_countdown);
        gvRoleHeadShow = view.findViewById(R.id.gv_live_roleplayer_headshow);
        rlRoleReadMain = view.findViewById(R.id.rl_live_roleplayer_read_main);
        tvBeginTipMsg = view.findViewById(R.id.tv_live_roleplayer_countdown_tip);
        lvReadList = view.findViewById(R.id.lv_live_roleplayer_read_list);
        civMatchHead = view.findViewById(R.id.civ_live_roleplayer_match_head);
        rlSpeechVolumnMain = view.findViewById(R.id.rl_live_roleplayer_speech_volumewave_main);
        vwvSpeechVolume = view.findViewById(R.id.vwv_livevideo_roleplayer_speech_volumewave);
        tvTotalScore = view.findViewById(R.id.tv_livevideo_roleplayer_result_totalscore);
        tvDzCount = view.findViewById(R.id.tv_livevideo_roleplayer_result_dz_count);
        tvFluency = view.findViewById(R.id.tv_livevideo_roleplayer_result_fluency);
        tvGoldCount = view.findViewById(R.id.tv_livevideo_roleplayer_result_gold_count);
        tvAccuracy = view.findViewById(R.id.tv_livevideo_roleplayer_result_accuracy);
        civResultHeadImg = view.findViewById(R.id.civ_livevideo_roleplayer_result_headimg);
        tvResultMsgTip = view.findViewById(R.id.tv_livevideo_roleplayer_result_msgtip);

        rlResultRole1 = view.findViewById(R.id.rl_livevideo_roleplayer_result_role_1);
        civResultRoleHeadImg1 = view.findViewById(R.id.civ_livevideo_roleplayer_result_role_head_1);
        tvResultRoleScore1 = view.findViewById(R.id.tv_livevideo_roleplayer_result_role_score_1);
        tvResultRoleName1 = view.findViewById(R.id.tv_livevideo_roleplayer_result_role_nickname_1);

        rlResultRole2 = view.findViewById(R.id.rl_livevideo_roleplayer_result_role_2);
        civResultRoleHeadImg2 = view.findViewById(R.id.civ_livevideo_roleplayer_result_role_head_2);
        tvResultRoleScore2 = view.findViewById(R.id.tv_livevideo_roleplayer_result_role_score_2);
        tvResultRoleName2 = view.findViewById(R.id.tv_livevideo_roleplayer_result_role_nickname_2);

        rlResultRole3 = view.findViewById(R.id.rl_livevideo_roleplayer_result_role_3);
        civResultRoleHeadImg3 = view.findViewById(R.id.civ_livevideo_roleplayer_result_role_head_3);
        tvResultRoleScore3 = view.findViewById(R.id.tv_livevideo_roleplayer_result_role_score_3);
        tvResultRoleName3 = view.findViewById(R.id.tv_livevideo_roleplayer_result_role_nickname_3);

        rlDZBubbleMessage = view.findViewById(R.id.rl_live_roleplayer_dz_message_bubble_main);
        int colors[] = {0x1936BC9B, 0x3236BC9B, 0x6436BC9B, 0x9636BC9B, 0xFF36BC9B};
        vwvSpeechVolume.setColors(colors);
        vwvSpeechVolume.setBackColor(Color.TRANSPARENT);
        rlResult = view.findViewById(R.id.rl_live_roleplayer_result_main);
        view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View view) {

            }

            @Override
            public void onViewDetachedFromWindow(View view) {
                Loger.i("RolePlayerDemoTest", "离开连麦界面");
                mReadHandler.removeMessages(READ_MESSAGE);
                mRolePlayBll.realease();

            }
        });
        return view;
    }

    @Override
    public void initData() {
        //默认MATCH_WAIT_SECOND 后，匹配页消失
        rlRoleReadMain.setVisibility(View.GONE);
        final HashMap<String, String> assetFolders = new HashMap<String, String>();
        civMatchHead.setBorderWidth(SizeUtils.Dp2Px(mContext, 3));
        civMatchHead.setBorderColor(Color.WHITE);
        ImageLoader.with(mContext).load(UserBll.getInstance().getMyUserInfoEntity().getHeadImg()).into(civMatchHead);

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
                        Loger.i("RolePlayerDemoTest","无朗读数据,进人机");
                        XESToastUtils.showToast(mContext, "无朗读数据,进人机");
                        //进入人机
                        mRolePlayBll.goToRobot();
                    }
                } else {
                    rlMatchPager.setVisibility(View.GONE);
                    Loger.i("RolePlayerDemoTest", "匹配失败,进人机");
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
        mHeadShowAdapter = new RolePlayerHeadShowAdapter(mContext, mEntity.getLstRoleInfo());
        int roleHeadsSize = mEntity.getLstRoleInfo().size();
        //当角色小于3个的时候，为保证角色头像都居中显示，动态改变列数
        if (roleHeadsSize < 3) {
            gvRoleHeadShow.setNumColumns(roleHeadsSize);
        } else {
            gvRoleHeadShow.setNumColumns(3);
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
    }

    /**
     * 准备好朗读数据显示，3秒倒计时准备RolePlayer
     */
    private void waitRolePlayer() {
        rlMatchPager.setVisibility(View.GONE);
        rlRoleReadMain.setVisibility(View.VISIBLE);

        tvCountTime.setText(getCountDownTime());
        rlRoleReadMain.setVisibility(View.VISIBLE);
        if (mEntity.getLstRolePlayerMessage().get(0).getRolePlayer().isSelfRole()) {
            tvBeginTipMsg.setText("You go first. Are you ready?");
            tvBeginTipMsg.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tvBeginTipMsg.setBackgroundResource(R.drawable.shape_livevideo_roleplayer_ready_go_bg);
                    tvBeginTipMsg.setText("GO");
                    //tvBeginTipMsg.setPadding(60, 10, 60, 15);
                    tvBeginTipMsg.setGravity(Gravity.CENTER);

                }
            }, 2000);
        } else {
            tvBeginTipMsg.setText("Don't hurry. Not your turn yet.");
        }
        //开始倒计时，1秒更新一次
        tvCountTime.postDelayed(new Runnable() {
            @Override
            public void run() {
//                if (mEntity.getCountDownSecond() == 0) {
//                    //倒计时结束，时钟正走
//                    return;
//                }
                mEntity.setCountDownSecond(mEntity.getCountDownSecond() - 1);
                tvCountTime.setText(getCountDownTime());
                tvCountTime.postDelayed(this, 1000);
            }
        }, 1000);
//
        //填充对话内容
        mRolePlayerAdapter = new CommonAdapter<RolePlayerEntity.RolePlayerMessage>(mEntity.getLstRolePlayerMessage(),
                2) {
            @Override
            public AdapterItemInterface<RolePlayerEntity.RolePlayerMessage> getItemView(Object type) {
                if ((boolean) type) {
                    //自己朗读的
                    return new RolePlayerSelfItem(mContext, mRolePlayBll);
                } else {
                    //他人朗读的
                    return new RolePlayerOtherItem(mContext, mRolePlayBll);
                }
            }

            @Override
            public Object getItemViewType(RolePlayerEntity.RolePlayerMessage t) {
                return t.getRolePlayer().isSelfRole();
            }
        };
        lvReadList.setAdapter(mRolePlayerAdapter);
        lvReadList.setVisibility(View.VISIBLE);
        lvReadList.setDividerHeight(SizeUtils.Dp2Px
                (mContext, 15));
        vHead = new View(mContext);
        //修改类型转换异常
        ListView.LayoutParams lp = new ListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, SizeUtils.Dp2Px
                (mContext, 50));
        vHead.setLayoutParams(lp);
        lvReadList.addFooterView(vHead);

//        int rop = ScreenUtils.getScreenHeight() / 2;
//        lvReadList.smoothScrollToPositionFromTop(0, -rop);
//        lvReadList.setSelection(0);


        //整个前奏3秒后开始
        tvBeginTipMsg.postDelayed(new Runnable() {
            @Override
            public void run() {
                tvBeginTipMsg.setVisibility(View.GONE);
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
     * 用来自动朗读
     */
    Handler mReadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
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
                            mRolePlayBll.requestResult();
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
                        mIse.stop();
                    }
                }
                if (mCurrentReadIndex == (mEntity.getLstRolePlayerMessage().size())) {
                    //已经对话完毕
                    endRolePlayer();
                    return;
                } else {
                    //lvReadList.smoothScrollToPosition(mCurrentReadIndex + 1);
                    lvReadList.setSelection(mCurrentReadIndex);
                    Loger.i("RolePlayerDemoTest", "滚动到下一条"+mCurrentReadIndex+1);
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
                mReadHandler.sendMessageDelayed(temp, (currentMessage.getMaxReadTime()) * 1000);
                mReadHandler.sendEmptyMessageDelayed(GO_SPEECH, (currentMessage.getMaxReadTime() - 1) * 1000);
            } else if (msg.what == GO_SPEECH) {
                //结束评测
                if (mIse != null) {
                    mIse.stop();
                }
            }
        }
    };

    /**
     * 开始进入RolePlayer对话
     */
    private void beginRolePlayer() {
        mReadHandler.sendEmptyMessage(READ_MESSAGE);

        //开启声网连接
        mWorkerThread.start();
        mWorkerThread.waitForReady();
        int vProfile = Constants.VIDEO_PROFILE_120P;
        mWorkerThread.configEngine(Constants.CLIENT_ROLE_BROADCASTER, vProfile);
        mWorkerThread.joinChannel(null, mEntity.getLiveId() + "_" + mEntity.getTestId() + "_" + mEntity.getTeamId(),
                Integer.parseInt(UserBll.getInstance().getMyUserInfoEntity().getStuId()), new WorkerThread
                        .OnJoinChannel() {
                    @Override
                    public void onJoinChannel(int joinChannel) {
                        Loger.i("RolePlayerDemoTest", "声网:" + joinChannel);
                    }
                });
    }

    /**
     * 显示结果
     */
    public void showResult() {
        Loger.i("RolePlayerDemoTest", "显示结果");
        mRolePlayBll.cancelDZ();
        vwvSpeechVolume.stop();
        rlSpeechVolumnMain.setVisibility(View.INVISIBLE);
        vwvSpeechVolume.setVisibility(View.GONE);
        //XESToastUtils.showToast(mContext, "结束");
        rlResult.setVisibility(View.VISIBLE);

        List<RolePlayerEntity.RolePlayerHead> lstHead = mEntity.getResultRoleList();
        RolePlayerEntity.RolePlayerHead head = mEntity.getSelfRoleHead();
        if (head != null) {
            if (head.getSpeechScore() >= 90) {
                tvResultMsgTip.setText("Fantastic");
            } else if (head.getSpeechScore() >= 60) {
                tvResultMsgTip.setText("Welldone");
            } else if (head.getSpeechScore() >= 40) {
                tvResultMsgTip.setText("Very Good");
            } else if (head.getSpeechScore() >= 20) {
                tvResultMsgTip.setText("Good!");
            } else {
                tvResultMsgTip.setText("Fighting");
            }

            tvTotalScore.setText(head.getSpeechScore() + "分");
            tvDzCount.setText(mEntity.getPullDZCount() + "");
            tvFluency.setText("流畅性:" + head.getFluency());
            tvGoldCount.setText(mEntity.getGoldCount() + "");
            tvAccuracy.setText("准确性:" + head.getAccuracy());

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
            if (mContext instanceof Activity) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    if (!((Activity) mContext).isDestroyed()) {
                        ImageLoader.with(mContext).load(UserBll.getInstance().getMyUserInfoEntity().getHeadImg())
                                .into(civResultHeadImg);
                    }
                }
            }

            if (lstHead.size() >= 1) {
                RolePlayerEntity.RolePlayerHead head1 = lstHead.get(0);
                tvResultRoleScore1.setText(head1.getSpeechScore() + "分");
                tvResultRoleName1.setText(head1.getNickName());
                ImageLoader.with(ContextManager.getApplication()).load(head1.getHeadImg()).into(civResultRoleHeadImg1);
                civResultRoleHeadImg1.setBorderWidth(SizeUtils.Dp2Px(mContext, 3));
                if (head1.isSelfRole()) {
                    civResultRoleHeadImg1.setBorderColor(Color.parseColor("#FAD2D1"));
                } else {
                    civResultRoleHeadImg1.setBorderColor(Color.parseColor("#E0E0E0"));
                }
            } else {
                rlResultRole1.setVisibility(View.INVISIBLE);
            }

            if (lstHead.size() >= 2) {
                RolePlayerEntity.RolePlayerHead head2 = lstHead.get(1);
                tvResultRoleScore2.setText(head2.getSpeechScore() + "分");
                tvResultRoleName2.setText(head2.getNickName());
                ImageLoader.with(ContextManager.getApplication()).load(head2.getHeadImg()).into(civResultRoleHeadImg2);
                civResultRoleHeadImg2.setBorderWidth(SizeUtils.Dp2Px(mContext, 3));
                if (head2.isSelfRole()) {
                    civResultRoleHeadImg2.setBorderColor(Color.parseColor("#FAD2D1"));
                } else {
                    civResultRoleHeadImg2.setBorderColor(Color.parseColor("#E0E0E0"));
                }
            } else {
                rlResultRole2.setVisibility(View.INVISIBLE);
            }

            if (lstHead.size() >= 3) {
                RolePlayerEntity.RolePlayerHead head3 = lstHead.get(2);
                tvResultRoleScore3.setText(head3.getSpeechScore() + "分");
                tvResultRoleName3.setText(head3.getNickName());
                ImageLoader.with(ContextManager.getApplication()).load(head3.getHeadImg()).into(civResultRoleHeadImg3);
                civResultRoleHeadImg3.setBorderWidth(SizeUtils.Dp2Px(mContext, 3));
                if (head3.isSelfRole()) {
                    civResultRoleHeadImg3.setBorderColor(Color.parseColor("#FAD2D1"));
                } else {
                    civResultRoleHeadImg3.setBorderColor(Color.parseColor("#E0E0E0"));
                }
            } else {
                rlResultRole3.setVisibility(View.INVISIBLE);
            }

        }

        //结果弹窗5秒后消失
        rlResult.postDelayed(new Runnable() {
            @Override
            public void run() {
                rlResult.setVisibility(View.GONE);
            }
        }, 5000);
        if (mWorkerThread != null) {
            mWorkerThread.leaveChannel(mWorkerThread.getEngineConfig().mChannel, new WorkerThread.OnLevelChannel() {
                @Override
                public void onLevelChannel(int leaveChannel) {
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
    }

    /**
     * 结束RolePlayer
     */
    private void endRolePlayer() {
        if (!mEntity.isResult()) {
            mRolePlayBll.requestResult();
        } else {
            showResult();
        }
    }


    /**
     * 进入自己朗读评测
     *
     * @param message
     * @param entity
     */

    private void speechReadMessage(final RolePlayerEntity.RolePlayerMessage message, final RolePlayerEntity entity) {
        if (!message.getRolePlayer().isSelfRole()) {
            //对方朗读则隐藏
            rlSpeechVolumnMain.setVisibility(View.INVISIBLE);
            vwvSpeechVolume.setVisibility(View.GONE);
            return;
        }


        rlSpeechVolumnMain.setVisibility(View.VISIBLE);
        vwvSpeechVolume.setVisibility(View.VISIBLE);
        String spechMsg = message.getReadMsg().replace("\n", "");
        if (mIse == null) {
            mIse = new SpeechEvaluatorUtils(true);
            saveVideoFile = new File(dir, "roleplayer" + System.currentTimeMillis() + ".mp3");
        }
        mIse.cancel();
        speechEvaluatorInter = mIse.startEnglishEvaluatorOffline(spechMsg, saveVideoFile.getAbsolutePath(), false,
                new RoleEvaluatorListener() {
                    @Override
                    public void onBeginOfSpeech() {
                        Loger.i("RolePlayerDemoTest", "开始测评");
                        vwvSpeechVolume.start();
                    }

                    @Override
                    public void onResult(ResultEntity resultEntity) {
                        if (resultEntity.getStatus() == ResultEntity.SUCCESS) {
                            Loger.i("RolePlayerDemoTest", "测评成功，开始上传自己的mp3");
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
                            XESToastUtils.showToast(mContext, resultEntity.getScore() + "");
                            //提前开始下一条
                            nextReadMessage();
                        } else if (resultEntity.getStatus() == ResultEntity.ERROR) {
                            Loger.i("RolePlayerDemoTest", "测评失败，"+ ResultEntity.ERROR+" 不上传自己的mp3");
                            XESToastUtils.showToast(mContext, "测评失败");
                            message.setMsgStatus(RolePlayerEntity.RolePlayerMessageStatus.END_SPEECH);
                            //提前开始下一条
                            nextReadMessage();
                        } else if (resultEntity.getStatus() == ResultEntity.EVALUATOR_ING) {
                            Loger.i("RolePlayerDemoTest", "测评中");
                        }

                    }

                    @Override
                    public void onVolumeUpdate(int volume) {
                        vwvSpeechVolume.setVolume(volume * 3);
                    }

                    @Override
                    public void onRecordPCMData(short[] shorts, int readSize) {
                        Loger.i("RolePlayerDemoTest", "通过声网走");
                        //通过声网走
                        byte[] dest = new byte[readSize * 2];
                        int count = readSize;
                        for (int i = 0; i < count; i++) {
                            dest[i * 2] = (byte) (shorts[i]);
                            dest[i * 2 + 1] = (byte) (shorts[i] >> 8);
                        }
                        mWorkerThread.getRtcEngine().pushExternalAudioFrame(dest, System.currentTimeMillis());
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
    private class RolePlayerHeadShowAdapter extends BaseAdapter {

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
        long countTime;
        boolean isFu = false;
        if (mEntity.getCountDownSecond() < 0) {
            isFu = true;
            countTime = Math.abs(mEntity.getCountDownSecond());
        } else {
            countTime = mEntity.getCountDownSecond();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mIse != null) {
            mIse.cancel();
        }
        mReadHandler.removeMessages(READ_MESSAGE);
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
}
