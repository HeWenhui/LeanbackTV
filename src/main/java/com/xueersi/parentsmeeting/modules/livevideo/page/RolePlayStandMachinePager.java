package com.xueersi.parentsmeeting.modules.livevideo.page;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tal.speech.config.SpeechConfig;
import com.tal.speech.speechrecognizer.Constants;
import com.tal.speech.speechrecognizer.EvaluatorListener;
import com.tal.speech.speechrecognizer.EvaluatorListenerWithPCM;
import com.tal.speech.speechrecognizer.ResultEntity;
import com.tal.speech.speechrecognizer.SpeechEvaluatorInter;
import com.tal.speech.speechrecognizer.SpeechParamEntity;
import com.tal.speech.utils.SpeechUtils;
import com.xueersi.common.base.BaseApplication;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.lib.imageloader.SingleConfig;
import com.xueersi.parentsmeeting.module.audio.AudioPlayer;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.activity.item.RolePlayerStandMachineOtherItem;
import com.xueersi.parentsmeeting.modules.livevideo.activity.item.RolePlayerStandMachineSelfItem;
import com.xueersi.parentsmeeting.modules.livevideo.business.RolePlayMachineBll;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.RolePlayConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LivePagerBack;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RolePlayerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.event.ArtsAnswerResultEvent;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.SpeechEvalAction;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseSpeechAssessmentPager;
import com.xueersi.parentsmeeting.modules.livevideo.question.view.StandSpeechResult;
import com.xueersi.parentsmeeting.modules.livevideo.util.GlideDrawableUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveSoundPool;
import com.xueersi.parentsmeeting.modules.livevideo.util.StandLiveMethod;
import com.xueersi.parentsmeeting.modules.livevideo.view.CustomUnScorllListView;
import com.xueersi.parentsmeeting.modules.livevideo.widget.FrameAnimation;
import com.xueersi.parentsmeeting.modules.livevideo.widget.ReadyGoImageView;
import com.xueersi.ui.adapter.AdapterItemInterface;
import com.xueersi.ui.adapter.CommonAdapter;
import com.xueersi.ui.widget.CircleImageView;
import com.xueersi.ui.widget.WaveView;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class RolePlayStandMachinePager extends BaseSpeechAssessmentPager {

    String file3 = "live_stand/frame_anim/speech/mine_score";
    String file4 = "live_stand/frame_anim/speech/mine_score_loop";
    /**
     * 匹配页默认停留时间
     */
    private final int MATCH_WAIT_SECOND = 0;
    /**
     * 角色确认页停留时间
     */
    private final int WAIT_ROLE_HEAD_SHOW = 3000;
    private final VideoQuestionLiveEntity videoQuestionLiveEntity;

    SpeechEvalAction speechEvalAction;


    /**
     * 准备开始朗读前的提示文案
     */
    //private TextView tvBeginTipMsg;
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
     * 朗读区
     */
    private RelativeLayout rlRoleReadMain;

    /**
     * 测评音量条展示区
     */
    private View rlSpeechVolumnMain;
    /**
     * 测评音量波形
     */
    private WaveView vwvSpeechVolume;

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
    private RolePlayMachineBll mRolePlayBll;

    /**
     * 语音评测
     */
    protected SpeechUtils mIse;
    private SpeechEvaluatorInter speechEvaluatorInter;
    private File saveVideoFile, dir;


    private ImageView ivRoleplayerResultStar;//显示成绩结果星星旗帜
    private boolean isShowResult;//标记是否正在显示结果页
    private LiveGetInfo mLiveGetInfo;//用于获取学科字段，以此判断roleplay进入哪个学科的连麦
    private RolePlayerStandMachineSelfItem mRolePlayerSelfItem;
    private RolePlayerStandMachineOtherItem mRolePlayerOtherItem;
    private boolean mIsListViewUnSroll;//listview是否可滑动

    /**
     * ture 直播，false 回放
     */
    private boolean mIsLive;
    private boolean mIsEnd;
    /**
     * 开始朗读下一条
     */
    public final static int READ_MESSAGE = 100;
    /**
     * 去评测
     */
    private final static int GO_SPEECH = 200;
    /**
     * 停止roleplay,及其音频播放
     */
    private static final int STOP_ROLEPLAY = 404;
    private RolePlayerEntity mEntity;
    private ReadyGoImageView rgivLivevideoStandReadygo;
    private LiveSoundPool liveSoundPool;

    /**
     * 是不是已经开始
     */
    private boolean isSpeechStart = false;
    /**
     * 是不是评测失败
     */
    private boolean isSpeechError = false;
    private Bitmap headBitmap;
    private String headUrl;
    private Bitmap canvasBitmap;
    private View resultUiParent;
    private String myNickName;
    private int myGold;
    private LinearLayout llLivevideoSpeectevalResultMine;
    private SpeechParamEntity param;
    /**
     * 用来自动朗读
     */


    /**
     * initView()先于此构造执行
     *
     * @param context
     * @param videoQuestionLiveEntity
     * @param id
     * @param testId
     * @param stuId
     * @param islive
     * @param nonce
     * @param speechEvalAction
     * @param stuCouId
     * @param isSilence
     * @param livePagerBack
     * @param rolePlayMachineBll
     * @param liveGetInfo
     */
    public RolePlayStandMachinePager(Context context, VideoQuestionLiveEntity videoQuestionLiveEntity, String id, String
            testId, String stuId, boolean islive, String nonce, SpeechEvalAction speechEvalAction, String stuCouId,
                                     boolean isSilence, LivePagerBack livePagerBack, RolePlayMachineBll
                                             rolePlayMachineBll, LiveGetInfo liveGetInfo) {
        super(context);
        this.mIsLive = islive;
        mRolePlayBll = rolePlayMachineBll;
        this.livePagerBack = livePagerBack;
        this.mLiveGetInfo = liveGetInfo;
        this.videoQuestionLiveEntity = videoQuestionLiveEntity;
        this.speechEvalAction = speechEvalAction;
        dir = LiveCacheFile.geCacheFile(context, "liveSpeech");

        //由于先执行initView，所以mIsLive的值在构造完成以后才赋值，需要在此判断是否是直播，是否需要显示关闭按钮
        ifShowCloseBt();

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
        //记录当前正在走的模型，留给界面更新使用
        ShareDataManager.getInstance().put(RolePlayConfig.KEY_FOR_WHICH_SUBJECT_MODEL_EVA, RolePlayConfig
                .VALUE_FOR_ENGLISH_MODEL_EVA, ShareDataManager.SHAREDATA_NOT_CLEAR);
    }

    /**
     * 设置语文标题布局
     */
    private void setChRoleplayUI() {
        //记录当前正在走的模型，留给界面更新使用
        ShareDataManager.getInstance().put(RolePlayConfig.KEY_FOR_WHICH_SUBJECT_MODEL_EVA,
                RolePlayConfig.VALUE_FOR_CHINESE_MODEL_EVA, ShareDataManager.SHAREDATA_NOT_CLEAR);
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.pager_stand_live_roleplayer, null);
        tv_close_role_play = view.findViewById(R.id.tv_close_role_play);
        tv_close_role_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onUserBackPressed();
            }
        });
        //倒计时整体布局,在回放的时候隐藏显示
        ll_live_roleplayer_countdown_main = view.findViewById(R.id.ll_live_roleplayer_countdown_main);
        //倒计时textview
        tvCountTime = view.findViewById(R.id.tv_live_roleplayer_countdown);

        rlRoleReadMain = view.findViewById(R.id.rl_live_roleplayer_read_main);
        lvReadList = view.findViewById(R.id.lv_live_roleplayer_read_list);
        mIsListViewUnSroll = true;
        lvReadList.setUnScroll(mIsListViewUnSroll);//设置listview不可滑动
        rlSpeechVolumnMain = view.findViewById(R.id.rl_live_roleplayer_speech_volumewave_main);
        vwvSpeechVolume = view.findViewById(R.id.vwv_livevideo_roleplayer_speech_volumewave);

        ivRoleplayerResultStar = view.findViewById(R.id.iv_live_roleplayer_result_star);


        rlDZBubbleMessage = view.findViewById(R.id.rl_live_roleplayer_dz_message_bubble_main);

        //ready go动画
        rgivLivevideoStandReadygo = view.findViewById(R.id.rgiv_livevideo_stand_readygo);

        //我的分数
        llLivevideoSpeectevalResultMine = view.findViewById(R.id
                .ll_livevideo_speecteval_result_mine);

        int colors[] = {0x1936BC9B, 0x3236BC9B, 0x6436BC9B, 0x9636BC9B, 0xFF36BC9B};
        //vwvSpeechVolume.setColors(colors);
        //vwvSpeechVolume.setBackColor(Color.TRANSPARENT);

        view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View view) {

            }

            @Override
            public void onViewDetachedFromWindow(View view) {
                logger.i("离开连麦界面，清除数据");
                mReadHandler.removeMessages(READ_MESSAGE);

                //释放所有正在播放的音频
                relaseAllAudioPlay();
//                if (mEntity != null) {
//                    mEntity = null;//防止结果页数据错乱，尤其点赞个数
//                }

                onDestroy();

            }
        });
        return view;
    }


    private void ifShowCloseBt() {
        if (mIsLive) {
            //只在直播的时候显示倒计时布局
            ll_live_roleplayer_countdown_main.setVisibility(View.VISIBLE);
        } else {
            //只在回放的时候显示关闭按钮的布局
            ll_live_roleplayer_countdown_main.setVisibility(View.GONE);
            tv_close_role_play.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onPause() {
        logger.i("界面失去焦点");
        super.onPause();
    }

    @Override
    public void onStop() {
        logger.i("界面不可见");
        super.onStop();
    }

    @Override
    public boolean onUserBackPressed() {
        //走返回的大逻辑，回放时候，答题结束，才不会卡顿
        if (speechEvalAction != null) {
            speechEvalAction.stopSpeech(RolePlayStandMachinePager.this, getBaseVideoQuestionEntity(), getId());
        }

        return super.onUserBackPressed();
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
        if(mReadHandler != null){
            mReadHandler.sendEmptyMessage(STOP_ROLEPLAY);
        }

    }

    /**
     * 用来自动朗读
     */
    Handler mReadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

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
            if(STOP_ROLEPLAY == msg.what){
                logger.i("print_stop_role_play:"+mCurrentReadIndex);
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
                    //vwvSpeechVolume.stop();
                    if (position == 0) {
                        RolePlayerEntity.RolePlayerMessage upMessage = mEntity.getLstRolePlayerMessage().get
                                (mCurrentReadIndex - 1);
                        if ((mCurrentReadIndex - 1) == mEntity.getSelfLastIndex()) {
                            logger.i("提交结果 mCurrentReadIndex = " + mCurrentReadIndex);

                            if (mEntity.isNewArts()) {
                                mRolePlayBll.requestNewArtsResult();
                            } else {
                                mRolePlayBll.requestResult();
                            }

                        }
                        if (upMessage.getMsgStatus() != RolePlayerEntity.RolePlayerMessageStatus.END_SPEECH) {
                            upMessage.setMsgStatus(RolePlayerEntity.RolePlayerMessageStatus.END_ROLEPLAY);
                        }
                        if (upMessage.getRolePlayer().isSelfRole()) {
                            //自己朗读完毕，只通知除自己以外的其他组内成员
//                            mRolePlayBll.selfReadEnd(upMessage.getStars(), upMessage.getSpeechScore(), upMessage
//                                            .getFluency(), upMessage.getAccuracy(), upMessage.getPosition(), mEntity,
//                                    upMessage.getRolePlayer().getRoleId());
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
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) rgivLivevideoStandReadygo
                                .getLayoutParams();
                        int animDistance = rgivLivevideoStandReadygo.getHeight() + layoutParams.topMargin;
                        ObjectAnimator oaAnimTransY = ObjectAnimator.ofFloat(rgivLivevideoStandReadygo, ImageView.TRANSLATION_Y,
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
                        rgivLivevideoStandReadygo.setVisibility(View.GONE);
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
                mLogtf.i("handleMessage:maxReadTime=" + currentMessage.getMaxReadTime() + ",mIsEnd=" + mIsEnd);
                if (currentMessage.getRolePlayer().isSelfRole() && !mIsEnd) {
                    mReadHandler.sendEmptyMessageDelayed(GO_SPEECH, (currentMessage.getMaxReadTime() - 1) * 1000);
                }


            } else if (msg.what == GO_SPEECH) {
                //结束评测
                if (mIse != null) {
                    mIse.stop();
                }
            }
        }
    };

    /**
     * 进入自己朗读评测
     *
     * @param message
     * @param entity
     */

    private void speechReadMessage(final RolePlayerEntity.RolePlayerMessage message, final RolePlayerEntity entity) {

        Random rm = new Random();
        //机器85到100随机给分
        int score = rm.nextInt(15) + 85;
        if (!message.getRolePlayer().isSelfRole()) {
            //对方朗读则隐藏
            rlSpeechVolumnMain.setVisibility(View.INVISIBLE);
            vwvSpeechVolume.setVisibility(View.GONE);
            message.setSpeechScore(score);
            logger.i("score = " + score);
            return;
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
                logger.i(TAG + "走英语离线测评");
                mIse = SpeechUtils.getInstance(mContext.getApplicationContext());
                mIse.setLanguage(Constants.ASSESS_PARAM_LANGUAGE_EN);
//                mIse = SpeechEvaluatorUtils.getInstance(mContext.getApplicationContext());

                break;
            case RolePlayConfig.VALUE_FOR_CHINESE_MODEL_EVA:
                //走语文离线测评
                logger.i(TAG + "走语文离线测评");
                mIse = SpeechUtils.getInstance(mContext.getApplicationContext());
                mIse.setLanguage(com.tal.speech.speechrecognizer.Constants
                        .ASSESS_PARAM_LANGUAGE_CH);
                break;
            default:
                //走英语离线测评
                logger.i(TAG + "走英语离线测评");
                mIse = SpeechUtils.getInstance(mContext.getApplicationContext());
                mIse.setLanguage(Constants.ASSESS_PARAM_LANGUAGE_EN);
//                mIse = SpeechUtils.getInstance(mContext.getApplicationContext());
                break;
        }
        mIse.prepar();
        mIse.cancel();
        if (param == null) {
            param = new SpeechParamEntity();
        }
        param.setStrEvaluator(spechMsg);
        param.setLocalSavePath(saveVideoFile.getAbsolutePath());
        param.setMultRef(false);
        param.setRecogType(SpeechConfig.SPEECH_ENGLISH_EVALUATOR_OFFLINE);
        mIse.startRecog(param,
                new RoleEvaluatorListener() {
                    @Override
                    public void onBeginOfSpeech() {
                        logger.i("开始测评 mCurrentReadIndex = " + mCurrentReadIndex);
                        isSpeechError = false;
                        vwvSpeechVolume.initialize();
                    }

                    @Override
                    public void onResult(ResultEntity resultEntity) {

                        if (resultEntity.getStatus() == ResultEntity.SUCCESS) {
                            logger.i("show_eva_score_suc:" + resultEntity.getSpeechDuration()
                                    + ":" + resultEntity.getScore());
                            entity.setSelfValidSpeechTime(resultEntity.getSpeechDuration());
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
                            if (mRolePlayerAdapter != null) {
                                mRolePlayerAdapter.updataSingleRow(lvReadList, message);
                            }

                            if (!mIsEnd) {
                                nextReadMessage();
                            }
                        } else if (resultEntity.getStatus() == ResultEntity.ERROR) {
                            logger.i("show_eva_score_error:" + resultEntity.getSpeechDuration()
                                    + ":" + resultEntity.getScore());
                            isSpeechError = true;
                            //XESToastUtils.showToast(mContext, "测评失败");
                            //mIsEvaluatoring = false;
                            message.setMsgStatus(RolePlayerEntity.RolePlayerMessageStatus.END_SPEECH);
                            message.setSpeechScore(resultEntity.getScore());
                            if (mRolePlayerAdapter != null) {
                                mRolePlayerAdapter.updataSingleRow(lvReadList, message);
                            }
                            //提前开始下一条
                            if (!mIsEnd) {
                                nextReadMessage();
                            }
                        } else if (resultEntity.getStatus() == ResultEntity.EVALUATOR_ING) {
                            // logger.i("RolePlayerDemoTest", "测评中");

                        }

                    }

                    @Override
                    public void onVolumeUpdate(int volume) {
                        //vwvSpeechVolume.setVolume(volume * 3);

                        float fVolume = (float) volume * 3 / 90;
                        logger.i("volume = " + volume + ":" + fVolume);
                        fVolume = fVolume < 0.5f ? 0.5f : fVolume;
                        vwvSpeechVolume.setWaveAmplitude(fVolume);
                    }

                    @Override
                    public void onRecordPCMData(short[] shorts, int readSize) {
                        // logger.i("RolePlayerDemoTest", "通过声网走");
                        //通过声网走
                    }
                });
    }


    /**
     * 提前开始下一条
     */
    private void nextReadMessage() {
        mReadHandler.removeMessages(GO_SPEECH);
        mReadHandler.removeMessages(READ_MESSAGE);
        mReadHandler.sendEmptyMessageDelayed(READ_MESSAGE, 1000);
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
            if (mEntity.isNewArts()) {
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
     * 显示结果
     */
    public void showResult() {

        if (isShowResult) {
            logger.i("结果页已经在显示");
            mRolePlayBll.cancelStandLiveDZ();//取消点赞
            return;
        }

        isShowResult = true;
        mEntity = mRolePlayBll.getRoleEntry();
        final RelativeLayout group = (RelativeLayout) mView;
        if (mEntity != null) {
            myGold = mEntity.getGoldCount();
            int energy = mEntity.getEnergy();
            logger.i("显示结果" + myNickName + ":" + headUrl + ":" + myGold + ",energy=" + energy);
            RolePlayerEntity.RolePlayerHead head = mEntity.getSelfRoleHead();
            if (head != null) {
                int score = head.getSpeechScore();
//                resultUiParent = StandSpeechResult.resultViewScore(mContext, group, myGold, energy, score);
                resultUiParent = StandSpeechResult.resultViewScoreEnergy(mContext, group, myGold, energy, score);
                myNickName = head.getNickName();
            }


            headUrl = UserBll.getInstance().getMyUserInfoEntity()
                    .getHeadImg();

            ImageLoader.with(BaseApplication.getContext()).load(headUrl).asCircle().asBitmap(new SingleConfig
                    .BitmapListener() {
                @Override
                public void onSuccess(Drawable drawable) {
                    headBitmap = GlideDrawableUtil.getBitmap(drawable, mLogtf, "initData", headUrl);
                }

                @Override
                public void onFail() {

                }
            });

        }
        //没有作答，或者没分队成功
        if (resultUiParent == null) {
//            resultUiParent = StandSpeechResult.resultViewScore(mContext, group, 0, 0, 0);
            resultUiParent = StandSpeechResult.resultViewScoreEnergy(mContext, group, myGold, 0, 0);
        }
        group.addView(resultUiParent, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        View tv_close_role_play_result = resultUiParent.findViewById(R.id.iv_livevideo_speecteval_result_close);
        //关闭结果页
        tv_close_role_play_result.setOnClickListener(new View
                .OnClickListener() {
            @Override
            public void onClick(View v) {
                recoverListScrollAndCancelDZ();
                StandLiveMethod.onClickVoice(liveSoundPool);
               /* if (!mIsLive) {
                    logger.i("close:" + getId());
                    if (speechEvalAction != null) {
                        speechEvalAction.stopSpeech(RolePlayStandMachinePager.this, getBaseVideoQuestionEntity(), getId());
                    }
                }*/
            }
        });
        vwvSpeechVolume.stop();
        rlSpeechVolumnMain.setVisibility(View.INVISIBLE);
        vwvSpeechVolume.setVisibility(View.INVISIBLE);
        final ImageView lottieAnimationView = resultUiParent.findViewById(R.id.iv_livevideo_speecteval_result_mine);
        try {
            final FrameAnimation frameAnimation = FrameAnimation.createFromAees(mContext, lottieAnimationView, file3, 50,
                    false);
            //frameAnimations.add(frameAnimation);
            frameAnimation.setBitmapCreate(new FrameAnimation.BitmapCreate() {
                @Override
                public Bitmap onAnimationCreate(String file) {
                    if (file.contains("WDDFruchang_00169") || file.contains("WDDFruchang_00170") || file.contains
                            ("WDDFruchang_00171")) {
                        return null;
                    }
                    boolean havename = true;
                    if (file.contains("_00172") || file.contains("_00173") || file.contains("_00174") || file.contains
                            ("_00175")
                            || file.contains("_00176") || file.contains("_00177")) {
                        havename = false;
                    }
                    return updateHead(frameAnimation, file, havename, myGold);
                }
            });
            frameAnimation.setAnimationListener(new FrameAnimation.AnimationListener() {
                @Override
                public void onAnimationStart() {

                }

                @Override
                public void onAnimationEnd() {
                    final FrameAnimation frameAnimation2 = FrameAnimation.createFromAees(mContext, lottieAnimationView,
                            file4, 50, true);
                    //frameAnimations.add(frameAnimation2);
                    frameAnimation2.setBitmapCreate(new FrameAnimation.BitmapCreate() {
                        @Override
                        public Bitmap onAnimationCreate(String file) {
                            return updateHead(frameAnimation2, file, true, myGold);
                        }
                    });
                    //结果弹窗5秒后消失,全身直播不要自动消失
                   /* if (resultUiParent != null) {
                        resultUiParent.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                recoverListScrollAndCancelDZ();

                                //isShowResult = false;
                            }
                        }, 5000);
                    }*/

                }

                @Override
                public void onAnimationRepeat() {

                }
            });
            frameAnimation.startAnimation();
        } catch (Exception e) {
            logger.i("exception:" + e.getMessage());
        }


        //显示结果的时候记录日志
        // RolePlayLog.sno7(liveAndBackDebug, mEntity, mContext);
        //结果弹窗5秒后消失
//        resultUiParent.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                //TODO:暂时注释掉
//                //recoverListScrollAndCancelDZ();
//
//                //isShowResult = false;
//            }
//        }, 5000);


        if (mEntity == null) {
            logger.i("需要显示结果弹窗，可是数据为空,不再往下执行，恢复滑动，取消点赞，离开频道");
            recoverListScrollAndCancelDZ();
            //leaveChannel();
            return;

        }

    }

    private Bitmap updateHead(final FrameAnimation frameAnimation, final String file,
                              final boolean havename, final int gold) {
        InputStream inputStream = null;
        try {
            inputStream = FrameAnimation.getInputStream(mContext, file);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            bitmap.setDensity(FrameAnimation.DEFAULT_DENSITY);
            Bitmap canvasBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            canvasBitmap.setDensity(FrameAnimation.DEFAULT_DENSITY);
            Canvas canvas = new Canvas(canvasBitmap);
            canvas.drawBitmap(bitmap, 0, 0, null);
            final Bitmap head = headBitmap;
            if (head != null && !head.isRecycled()) {
                float scaleWidth = 148f / head.getHeight();
                Matrix matrix = new Matrix();
                matrix.postScale(scaleWidth, scaleWidth);
                Bitmap scalHeadBitmap = Bitmap.createBitmap(head, 0, 0, head.getWidth(), head.getHeight(), matrix,
                        true);
                scalHeadBitmap.setDensity(FrameAnimation.DEFAULT_DENSITY);
                float left = (bitmap.getWidth() - scalHeadBitmap.getWidth()) / 2;
                float top;
                left += 3f;
                top = (bitmap.getHeight() - scalHeadBitmap.getHeight()) / 2 - 30;
                canvas.drawBitmap(scalHeadBitmap, left, top - 2, null);
                scalHeadBitmap.recycle();
            } else {
                Activity activity = (Activity) mContext;
                if (!activity.isFinishing()) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ImageLoader.with(mContext).load(headUrl).asCircle().asBitmap(new SingleConfig
                                    .BitmapListener() {
                                @Override
                                public void onSuccess(Drawable drawable) {
                                    Bitmap headBitmap = GlideDrawableUtil.getBitmap(drawable, mLogtf, "updateHead",
                                            headUrl);
                                    RolePlayStandMachinePager.this.headBitmap = headBitmap;
                                    frameAnimation.removeBitmapCache(file);
                                }

                                @Override
                                public void onFail() {

                                }
                            });
                        }
                    });
                }
            }
            bitmap.recycle();
            //画名字和金币数量
            if (havename) {
//                View layout_live_stand_red_mine1 = StandSpeechResult.resultViewName(mContext, "" + myGold, getTypeface(mContext), mLiveGetInfo.getStandLiveName());
                View layout_live_stand_red_mine1 = StandSpeechResult.resultViewNameEnergy(mContext, mLiveGetInfo.getStandLiveName());
                canvas.save();
                canvas.translate((canvasBitmap.getWidth() - layout_live_stand_red_mine1.getMeasuredWidth()) / 2, 348);
                layout_live_stand_red_mine1.draw(canvas);
                canvas.restore();
            }
            return canvasBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            logger.e("roleplay IOException:" + e.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.e("roleplay IOException:" + e.getMessage());
                }
            }
        }
        return null;
    }

    /**
     * 恢复页面滑动，取消点赞
     */
    public void recoverListScrollAndCancelDZ() {
        //恢复listview可滑动
        mIsListViewUnSroll = false;

        if (lvReadList != null) {
            lvReadList.setUnScroll(mIsListViewUnSroll);//恢复列表滑动
            logger.i("恢复列表滑动");
        }
        if (mRolePlayBll != null) {
            logger.i("取消点赞");
            mRolePlayBll.cancelStandLiveDZ();//取消点赞
        }
        //TODO:通知弹出top3
        //XESToastUtils.showToast(mContext,"test");
        if (resultUiParent != null) {
            ViewGroup group = (ViewGroup) resultUiParent.getParent();
            if (group != null) {
                group.removeView(resultUiParent);
            }
            resultUiParent = null;
        }

        JSONObject jsonObject = mEntity.getJson();
        if (jsonObject != null) {
            String data = jsonObject.toString();
            EventBus.getDefault().post(new ArtsAnswerResultEvent(data, ArtsAnswerResultEvent.TYPE_ROLEPLAY_ANSWERRESULT));
        }


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

    @Override
    public void initData() {
        //super.initData();

        //mRolePlayBll.teacherPushTest(videoQuestionLiveEntity);
        mEntity = mRolePlayBll.getRoleEntry();
        //默认MATCH_WAIT_SECOND 后，匹配页消失
        //rlRoleReadMain.setVisibility(View.GONE);
        final HashMap<String, String> assetFolders = new HashMap<String, String>();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mEntity = mRolePlayBll.getRoleEntry();
                if (mEntity != null) {

                    List<RolePlayerEntity.RolePlayerHead> rolePlayerHeads = mEntity.getLstRoleInfo();
                    List<RolePlayerEntity.RolePlayerMessage> rolePlayerMessages = mEntity.getLstRolePlayerMessage();
                    if (rolePlayerHeads.size() == 0) {

                    }
                    if (rolePlayerHeads != null && rolePlayerHeads.size() > 0 && rolePlayerMessages != null &&
                            rolePlayerMessages.size() > 0) {
                        logger.i("开始匹配");
                        roleConfirmPage(); //确定角色开始RolePlayer
                    } else {
                        logger.i("无朗读数据,回到直播界面" + rolePlayerHeads.size());
                        XESToastUtils.showToast(mContext, "无朗读数据");
                        //mRolePlayBll.goToRobot();
//                        if (mRolePlayBll != null) {
//                            mRolePlayBll.onStopQuestion(null, null);
//                        }
                    }
                } else {
                    logger.i("匹配失败");
                    XESToastUtils.showToast(mContext, "匹配失败");
//                    if (mRolePlayBll != null) {
//                        mRolePlayBll.onStopQuestion(null, null);
//                    }
                    //mRolePlayBll.goToRobot();
                }

            }
        }, MATCH_WAIT_SECOND);
    }

    /**
     * 确定角色准备开始RolePlayer
     */
    private void roleConfirmPage() {

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
        logger.i("准备显示对话了");
        ///获取当前应该走的离线模型

        final int curSubModEva = ShareDataManager.getInstance().getInt(RolePlayConfig
                .KEY_FOR_WHICH_SUBJECT_MODEL_EVA, RolePlayConfig.VALUE_FOR_ENGLISH_MODEL_EVA, ShareDataManager
                .SHAREDATA_NOT_CLEAR);


        rlRoleReadMain.setVisibility(View.VISIBLE);

        //只在直播的时候显示倒计时
        if (mIsLive) {
            tvCountTime.setText(getCountDownTime());
        }

        rlRoleReadMain.setVisibility(View.VISIBLE);
        Typeface tFace = getTypeface(mContext);

//        if (mEntity.getLstRolePlayerMessage().get(0).getRolePlayer().isSelfRole()) {
//            tvBeginTipMsg.setText((curSubModEva == RolePlayConfig.VALUE_FOR_CHINESE_MODEL_EVA) ? "你先开始.准备好了吗？" : "You" +
//                    " go first. Are you ready?");
//            tvBeginTipMsg.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    switch (curSubModEva) {
//                        case RolePlayConfig.VALUE_FOR_ENGLISH_MODEL_EVA:
//                            //当前是英语离线测评
//                            tvBeginTipMsg.setBackgroundResource(R.drawable.shape_livevideo_roleplayer_ready_go_bg);
//                            tvBeginTipMsg.setText("GO");
//                            break;
//                        case RolePlayConfig.VALUE_FOR_CHINESE_MODEL_EVA:
//                            //当前是语文离线测评
//                            tvBeginTipMsg.setBackgroundResource(R.drawable.shape_livevideo_roleplayer_ready_go_bg);
//                            tvBeginTipMsg.setText("开始");
//                            break;
//                        default:
//                            //默认走英语离线测评
//                            tvBeginTipMsg.setBackgroundResource(R.drawable.shape_livevideo_roleplayer_ready_go_bg);
//                            tvBeginTipMsg.setText("GO");
//                            break;
//                    }
//
//                    //tvBeginTipMsg.setPadding(60, 10, 60, 15);
//                    tvBeginTipMsg.setGravity(Gravity.CENTER);
//
//                }
//            }, 2000);
//        } else {
//            tvBeginTipMsg.setText((curSubModEva == RolePlayConfig.VALUE_FOR_CHINESE_MODEL_EVA) ? "别着急.还没到你." : "Don't" +
//                    " hurry. Not your turn yet.");
//        }

        showReadyGo();
        //只在直播的时候，有倒计时的逻辑
        if (mIsLive) {
            //开始倒计时，1秒更新一次
            tvCountTime.postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (mEntity == null) {
                        return;
                    }
                    mEntity.setCountDownSecond(mEntity.getCountDownSecond() - 1);
                    tvCountTime.setText(getCountDownTime());
                    tvCountTime.postDelayed(this, 1000);
                }
            }, 1000);
        }

        //填充对话内容
        mRolePlayerAdapter = new CommonAdapter<RolePlayerEntity.RolePlayerMessage>(mEntity.getLstRolePlayerMessage(),
                2) {
            @Override
            public AdapterItemInterface<RolePlayerEntity.RolePlayerMessage> getItemView(Object type) {
                logger.i("type = " + type);
                if ((boolean) type) {
                    //自己朗读的
                    mRolePlayerSelfItem = new RolePlayerStandMachineSelfItem(mContext, mRolePlayBll,mReadHandler);
                    return mRolePlayerSelfItem;
                } else {
                    //他人朗读的
                    mRolePlayerOtherItem = new RolePlayerStandMachineOtherItem(mContext, mRolePlayBll, mReadHandler);
                    return mRolePlayerOtherItem;
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
                (mContext, 5));
        vHead = new View(mContext);
        //修改类型转换异常
        ListView.LayoutParams lp = new ListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, SizeUtils.Dp2Px
                (mContext, 50));
        vHead.setLayoutParams(lp);
        lvReadList.addFooterView(vHead);

        //整个前奏3秒后开始
        rgivLivevideoStandReadygo.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (rgivLivevideoStandReadygo != null) {
                    //此刻要占位
                    rgivLivevideoStandReadygo.setVisibility(View.INVISIBLE);
                    //rgivLivevideoStandReadygo.destory();
                }

                beginRolePlayer();
            }
        }, 3000);


    }

    /**
     * 显示ready go
     */
    private void showReadyGo() {
        rgivLivevideoStandReadygo.setAnimationListener(new FrameAnimation.AnimationListener() {
            @Override
            public void onAnimationStart() {

            }

            @Override
            public void onAnimationEnd() {
//                ViewGroup group = (ViewGroup) rgivLivevideoStandReadygo.getParent();
//                if (group != null) {
//                    group.removeView(rgivLivevideoStandReadygo);
//                }
                //rgivLivevideoStandReadygo.destory();
                if (mContext instanceof Activity) {
                    Activity activity = (Activity) mContext;
                    if (activity.isFinishing()) {
                        return;
                    }
                }
            }

            @Override
            public void onAnimationRepeat() {

            }
        });
        liveSoundPool = LiveSoundPool.createSoundPool();
        rgivLivevideoStandReadygo.start(liveSoundPool);
    }


    /**
     * 开始进入RolePlayer对话
     */
    private void beginRolePlayer() {
        mReadHandler.sendEmptyMessage(READ_MESSAGE);

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
                span.setSpan(new ForegroundColorSpan(Color.parseColor("#e85050")), 0, span.length(),
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

    public void stopSpeech() {
        if (mIse != null) {
            mIse.stop();
        }
        mIsEnd = true;
        mReadHandler.removeMessages(GO_SPEECH);
        mReadHandler.removeMessages(READ_MESSAGE);
        if (mEntity != null && !mEntity.isResult() && mRolePlayBll.getRoleEntry() != null) {
            if (mEntity.isNewArts()) {
                mRolePlayBll.requestNewArtsResult();
            } else {
                mRolePlayBll.requestResult();
            }
        }
    }

    /**
     * 关闭当前页面
     */
    public void relaseCurrentPage() {
        //释放所有正在播放的音频
        relaseAllAudioPlay();
        if (mIse != null) {
            mIse.stop();
        }
        new Thread() {
            @Override
            public void run() {
                AudioPlayer.releaseAudioPlayer(mContext);
            }
        }.start();
        ViewGroup group = (ViewGroup) mView.getParent();
        if (group != null) {
            logger.i("关闭roleplay界面");
            group.removeView(mView);
            speechEvalAction.stopSpeech(RolePlayStandMachinePager.this, getBaseVideoQuestionEntity(),
                    videoQuestionLiveEntity.id);
            //speechEvalAction = null;
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
            RolePlayerHeadShowAdapter.Holder holder;
            if (convertView == null) {
                holder = new RolePlayerHeadShowAdapter.Holder();
                convertView = View.inflate(mContext, R.layout.item_live_roleplayer_rolehead, null);
                holder.tvNickName = convertView.findViewById(R.id.tv_live_roleplayer_item_rolehead_nickname);
                holder.civHeadImg = convertView.findViewById(R.id.civ_roleplayer_item_rolehead_img);
                holder.ivHeadShadow = convertView.findViewById(R.id.iv_roleplayer_item_shadow);
                holder.tvRoleName = convertView.findViewById(R.id.tv_live_roleplayer_item_rolename);
                convertView.setTag(holder);
            } else {
                holder = (RolePlayerHeadShowAdapter.Holder) convertView.getTag();
            }
            setData(lstRolePlayerHead.get(position), holder);
            return convertView;
        }

        private void setData(RolePlayerEntity.RolePlayerHead entity, RolePlayerHeadShowAdapter.Holder holder) {
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

    @Override
    public void examSubmitAll() {

    }

    @Override
    public String getId() {
        return videoQuestionLiveEntity.id;
    }

    /**
     * 用户手动返回，提交评测
     */
    @Override
    public void jsExamSubmit() {
        logger.i("用户手动返回，提交评测");
    }

    @Override
    public void stopPlayer() {

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
