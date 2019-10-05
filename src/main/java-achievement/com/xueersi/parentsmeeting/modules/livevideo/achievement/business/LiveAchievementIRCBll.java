package com.xueersi.parentsmeeting.modules.livevideo.achievement.business;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import com.tal.speech.language.TalLanguage;
import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.route.XueErSiRouter;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.config.BetterMeConfig;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.contract.BetterMeContract;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.AimRealTimeValEntity;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.BetterMeEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.AudioRequest;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveEvent;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.TopicAction;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.business.EnPkTeam;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.EnTeamPkRankEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StarAndGoldEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.message.business.SendMessageReg;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishShowReg;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionShowAction;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionShowReg;
import com.xueersi.parentsmeeting.modules.livevideo.question.config.LiveQueConfig;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.ui.dialog.VerifyCancelAlertDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.Call;

/**
 * Created by linyuqiang on 2018/7/5.
 * 本场成就和语音能量条
 */
public class LiveAchievementIRCBll extends LiveBaseBll implements NoticeAction, TopicAction, LiveAchievementHttp,
        EnglishSpeekHttp, AudioRequest {
    private StarInteractAction starAction;
    private BetterMeInteractAction betterMeInteractAction;
    private EnglishSpeekAction englishSpeekAction;
    private AtomicBoolean audioRequest = new AtomicBoolean(false);
    private EnglishSpeekMode englishSpeekMode;
    private SpeakerRecognitioner speakerRecognitioner;
    private VerifyCancelAlertDialog recognizeDialog;
    private boolean isDestory = false;
    private int smallEnglish;
    LiveAchievementEngStandBll liveAchievementEngStandBll;

    public LiveAchievementIRCBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
        putInstance(LiveAchievementIRCBll.class, this);
        smallEnglish = activity.getIntent().getIntExtra("smallEnglish", 0);
    }

    @Override
    public void onCreate(HashMap<String, Object> data) {
        super.onCreate(data);
        SendMessageReg sendMessageReg = getInstance(SendMessageReg.class);
        if (sendMessageReg != null) {
            sendMessageReg.addOnSendMsg(new SendMessageReg.OnSendMsg() {
                @Override
                public void onSendMsg(String msg) {
                    LiveAchievementIRCBll.this.onSendMsg(msg);
                }
            });
        }
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        final long sTime = mGetInfo.getsTime();
        if (1 == getInfo.getIsAllowStar()) {
            initRecognizeDialog();
            putInstance(AudioRequest.class, this);
            final LiveGetInfo.EnglishPk englishPk = getInfo.getEnglishPk();
            if (1 == englishPk.canUsePK) {
                mLiveBll.registEvent(EnPkTeam.class, new LiveEvent() {
                    @Override
                    public void onEvent(Object object) {
                        post(new Runnable() {
                            @Override
                            public void run() {
                                if (starAction instanceof EnPkInteractAction) {
                                    EnPkInteractAction enPkInteractAction = (EnPkInteractAction) starAction;
                                    enPkInteractAction.onEnglishPk();
                                }
                            }
                        });
                    }
                });
                //全身直播得仪式结束以后，请求。三分屏在互动题结束请求
                if (mGetInfo.getPattern() != LiveVideoConfig.LIVE_PATTERN_2) {
                    AchieveQuestionShowAction enTeamPkQuestionShowAction = new AchieveQuestionShowAction();
                    QuestionShowReg questionShowReg = getInstance(QuestionShowReg.class);
                    if (questionShowReg != null) {
                        questionShowReg.registQuestionShow(enTeamPkQuestionShowAction);
                    }
                    EnglishShowReg englishShowReg = getInstance(EnglishShowReg.class);
                    if (englishShowReg != null) {
                        englishShowReg.registQuestionShow(enTeamPkQuestionShowAction);
                    }
                }
            }
            putInstance(UpdateAchievement.class, new UpdateAchievement() {
                @Override
                public void getStuGoldCount(Object method, int type) {
                    mLogtf.d("getStuGoldCount:method=" + method + ",type=" + type);
                    if (1 == englishPk.canUsePK) {
                        if (type != UpdateAchievement.GET_TYPE_RED && type != UpdateAchievement.GET_TYPE_TEAM
                                && type != UpdateAchievement.GET_TYPE_INTELLIGENT_RECOGNITION && type != UpdateAchievement.GET_TYPE_VOTE) {
                            return;
                        }
                    }
                    postDelayedIfNotFinish(new Runnable() {
                        @Override
                        public void run() {
                            String liveid = mGetInfo.getId();
                            getHttpManager().getStuGoldCount(liveid, new HttpCallBack() {
                                @Override
                                public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                                    StarAndGoldEntity starAndGoldEntity = getHttpResponseParser().parseStuGoldCount
                                            (responseEntity);
                                    mGetInfo.setGoldCount(starAndGoldEntity.getGoldCount());
                                    mGetInfo.setStarCount(starAndGoldEntity.getStarCount());
                                    StarAndGoldEntity.PkEnergy pkEnergy = starAndGoldEntity.getPkEnergy();
                                    LiveGetInfo.EnPkEnergy enpkEnergy = mGetInfo.getEnpkEnergy();
                                    enpkEnergy.me = pkEnergy.me;
                                    enpkEnergy.myTeam = pkEnergy.myTeam;
                                    enpkEnergy.opTeam = pkEnergy.opTeam;
                                    if (starAction != null) {
                                        starAction.onGetStar(starAndGoldEntity);
                                    }
                                }
                            });
                        }
                    }, 500);

                }

                @Override
                public void updateEnpk(EnTeamPkRankEntity enTeamPkRankEntity) {
                    logger.d("updateEnpk");
                    if (starAction instanceof EnPkInteractAction) {
                        EnPkInteractAction enPkInteractAction = (EnPkInteractAction) starAction;
                        enPkInteractAction.updateEnpk(enTeamPkRankEntity);
                    }
                }

                @Override
                public void onUpdateBetterMe(AimRealTimeValEntity aimRealTimeValEntity, boolean isShowBubble) {
                    logger.d("onUpdateBetterMe");
                    if (betterMeInteractAction != null) {
                        betterMeInteractAction.onBetterMeUpdate(aimRealTimeValEntity, isShowBubble);
                    }
                }

                @Override
                public void onReceiveBetterMe(BetterMeEntity betterMeEntity, boolean isShowBubble) {
                    logger.d(" onReceiveBetterMe");
                    if (betterMeInteractAction != null) {
                        betterMeInteractAction.onReceiveBetterMe(betterMeEntity, isShowBubble);
                    }
                }
            });
            boolean voiceRecognSwitchOn = mShareDataManager.getBoolean(ShareBusinessConfig.SP_VOICE_RECOGNI_SWITCH,
                    true,
                    ShareDataManager.SHAREDATA_USER);
            if (voiceRecognSwitchOn) {
                if (!isDestory) {
                    speakerRecognitioner = new SpeakerRecognitioner(activity, audioRequest);
                    if (englishSpeekAction != null) {
                        englishSpeekAction.setSpeakerRecognitioner(speakerRecognitioner);
                    }
                    speakerRecognitioner.setSpeakerEnrollIvector(new SpeakerEnrollIvector() {
                        @Override
                        public void enrollIvector(int enrollIvector) {
                            logger.d("enrollIvector:enrollIvector=" + enrollIvector);
                            if (enrollIvector != 0 && !hasGotoRecogniz) {
                                long interval = sTime * 1000 - System.currentTimeMillis();
                                boolean allow = true;
                                if (!LiveTopic.MODE_TRANING.equals(mGetInfo.getMode()) ||
                                        interval <= 60 * 1000) {
                                    allow = false;
                                }
                                if (allow) {
                                    hasGotoRecogniz = true;
                                    post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (recognizeDialog != null && !recognizeDialog.isDialogShow()) {
                                                UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext
                                                        .getResources().getString(R.string.personal_1701001));
                                                recognizeDialog.showDialog();
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });
                    long interval = sTime * 1000 - System.currentTimeMillis();
                    boolean allow = true;
                    if (!LiveTopic.MODE_TRANING.equals(mGetInfo.getMode()) ||
                            interval <= 60 * 1000) {
                        allow = false;
                    }
                    if (allow) {
                        speakerRecognitioner.check();
                    }
                    startAchievement();
                }
            } else {
                startAchievement();
            }
        } else {
            mLiveBll.removeBusinessBll(this);
        }
    }

    @Override
    public void onArtsExtLiveInited(LiveGetInfo getInfo) {
        super.onArtsExtLiveInited(getInfo);
        if (liveAchievementEngStandBll != null) {
            liveAchievementEngStandBll.setAchievementLayout(getInfo.getArtsExtLiveInfo());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (englishSpeekAction != null) {
            handler.removeMessages(1);
            englishSpeekAction.stop(null);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isGotoRecogniz) {
            isGotoRecogniz = false;
            if (speakerRecognitioner != null) {
                speakerRecognitioner.check();
            }
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    SpeakerRecognitionerInterface speakerRecognitionerInterface = SpeakerRecognitionerInterface
//                            .getInstance();
//                    boolean result = speakerRecognitionerInterface.init();
//                    if (result) {
//                        String stuId = LiveAppUserInfo.getInstance().getStuId();
//                        if (StringUtils.isEmpty(stuId)) {
//                            mLogtf.d("onResume:stuId=" + stuId);
//                            startAchievement();
//                        } else {
//                            byte[] pcmdata = new byte[10];
//                            int enrollIvector = speakerRecognitionerInterface.
//                                    enrollIvector(pcmdata, pcmdata.length, 0, stuId, false);
//                            if (enrollIvector != 0) {
//                                startAchievement();
//                            } else {
//                                mLogtf.d("onResume:isDestory=" + isDestory);
//                                if (!isDestory) {
//                                    speakerRecognitioner = new SpeakerRecognitioner(activity, audioRequest);
//                                    if (englishSpeekAction != null) {
//                                        englishSpeekAction.setSpeakerRecognitioner(speakerRecognitioner);
//                                    }
//                                    startAchievement();
//                                }
//                            }
//                        }
//                    } else {
//                        startAchievement();
//                    }
//                }
//            }).start();
        } else {
            if (audioRequest.get()) {
                release();
            }
        }
    }

    boolean isGotoRecogniz = false;
    boolean hasGotoRecogniz = false;

    private void initRecognizeDialog() {
        recognizeDialog = new VerifyCancelAlertDialog(mContext, ContextManager.getApplication(), false,
                VerifyCancelAlertDialog.MESSAGE_VERIFY_CANCEL_TYPE);
        recognizeDialog.initInfo("为了让开口数据更为准确，请进行声纹认证");
        recognizeDialog.setVerifyBtnListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string
                        .personal_1701002));
                isGotoRecogniz = true;
//                SpeakerRecognitionerInterface speakerRecognitionerInterface = SpeakerRecognitionerInterface
//                        .getInstance();
//                speakerRecognitionerInterface.speakerRecognitionerFree();
                Bundle bundle = new Bundle();
                bundle.putString("from", "livevideo");
                XueErSiRouter.startModule(mContext, "/pager_personals/voicerecognize_land", bundle);
            }
        });
        recognizeDialog.setCancelBtnListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string
                        .personal_1701003));
//                startAchievement();
            }
        });
        recognizeDialog.setCancelShowText("取消").setVerifyShowText("去认证");

    }


    private class EnglishSpeekModeNomal implements EnglishSpeekMode {

        @Override
        public void initAchievement(String mode) {
            EnglishSpeekAction oldEnglishSpeekAction = LiveAchievementIRCBll.this.englishSpeekAction;
            if (oldEnglishSpeekAction != null) {
                oldEnglishSpeekAction.stop(null);
            }
            if (LiveAchievementIRCBll.this.starAction == null) {
                if (1 == smallEnglish) {
                    LiveAchievementEngBll liveAchievementEngBll = new LiveAchievementEngBll(activity, mLiveType,
                            mGetInfo, true);
//                    liveAchievementEngBll.setLiveBll(LiveAchievementIRCBll.this);
//                    liveAchievementEngBll.setLiveAndBackDebug(mLiveBll);
                    liveAchievementEngBll.initView(getLiveViewAction());
                    liveAchievementEngBll.setLiveAchievementHttp(LiveAchievementIRCBll.this);
                    LiveAchievementIRCBll.this.starAction = liveAchievementEngBll;
                    LiveAchievementIRCBll.this.betterMeInteractAction = liveAchievementEngBll;
                    EnglishSpeekEnBll englishSpeekBll = new EnglishSpeekEnBll(activity, mGetInfo);
                    if (speakerRecognitioner != null) {
                        englishSpeekBll.setSpeakerRecognitioner(speakerRecognitioner);
                    }
                    englishSpeekBll.setLiveBll(LiveAchievementIRCBll.this);
                    englishSpeekBll.initView(getLiveViewAction(), mGetInfo.getMode(), null, audioRequest);
                    LiveAchievementIRCBll.this.englishSpeekAction = englishSpeekBll;
                } else {
                    LiveAchievementBll starBll = new LiveAchievementBll(activity, mLiveType, mGetInfo,//mGetInfo
                            // .getStarCount(),
                            //mGetInfo.getGoldCount(),
                            true);
                    starBll.setLiveBll(LiveAchievementIRCBll.this);
                    starBll.setLiveAndBackDebug(contextLiveAndBackDebug);
                    starBll.initView(getLiveViewAction());
                    LiveAchievementIRCBll.this.starAction = starBll;
                    //能量条
                    EnglishSpeekBll englishSpeekBll = new EnglishSpeekBll(activity, mGetInfo);
                    if (speakerRecognitioner != null) {
                        englishSpeekBll.setSpeakerRecognitioner(speakerRecognitioner);
                    }
                    boolean initView = englishSpeekBll.initView(getLiveViewAction(), mGetInfo.getMode(), null,
                            audioRequest);
                    if (initView) {
                        englishSpeekBll.setTotalOpeningLength(mGetInfo.getTotalOpeningLength());
                        englishSpeekBll.setLiveBll(LiveAchievementIRCBll.this);
                        englishSpeekBll.setLiveAndBackDebug(contextLiveAndBackDebug);
                        englishSpeekBll.setmShareDataManager(mShareDataManager);
                        LiveAchievementIRCBll.this.englishSpeekAction = englishSpeekBll;
                    }
                }
            }
            if (LiveAchievementIRCBll.this.englishSpeekAction != null) {
                LiveAchievementIRCBll.this.englishSpeekAction.onModeChange(mode, audioRequest.get());
            }
        }
    }

    private class EnglishSpeekModeStand implements EnglishSpeekMode {

        @Override
        public void initAchievement(String mode) {
            if (smallEnglish == 1) {
                EnglishSpeekAction oldEnglishSpeekAction = LiveAchievementIRCBll.this.englishSpeekAction;
                if (oldEnglishSpeekAction != null) {
                    oldEnglishSpeekAction.stop(null);
                }
                if (LiveTopic.MODE_CLASS.equals(mode)) {
                    liveAchievementEngStandBll = new LiveAchievementEngStandBll(activity, mLiveType, mGetInfo, true);
//                    liveAchievementEngBll.setLiveBll(LiveAchievementIRCBll.this);
//                    liveAchievementEngBll.setLiveAndBackDebug(mLiveBll);
                    liveAchievementEngStandBll.initView(getLiveViewAction());
                    LiveAchievementIRCBll.this.starAction = liveAchievementEngStandBll;
                    LiveAchievementIRCBll.this.betterMeInteractAction = liveAchievementEngStandBll;
                    EnglishSpeekEnBll englishSpeekBll = new EnglishSpeekEnBll(activity, mGetInfo);
                    if (speakerRecognitioner != null) {
                        englishSpeekBll.setSpeakerRecognitioner(speakerRecognitioner);
                    }
                    englishSpeekBll.setLiveBll(LiveAchievementIRCBll.this);
                    englishSpeekBll.initView(getLiveViewAction(), mGetInfo.getMode(), null, audioRequest);
                    LiveAchievementIRCBll.this.englishSpeekAction = englishSpeekBll;
                } else {
                    LiveAchievementEngBll liveAchievementEngBll = new LiveAchievementEngBll(activity, mLiveType,
                            mGetInfo, true);
//                    liveAchievementEngBll.setLiveBll(LiveAchievementIRCBll.this);
//                    liveAchievementEngBll.setLiveAndBackDebug(mLiveBll);
                    liveAchievementEngBll.initView(getLiveViewAction());
                    liveAchievementEngBll.setLiveAchievementHttp(LiveAchievementIRCBll.this);
                    LiveAchievementIRCBll.this.starAction = liveAchievementEngBll;
                    LiveAchievementIRCBll.this.betterMeInteractAction = liveAchievementEngBll;
                    LiveAchievementIRCBll.this.englishSpeekAction = null;
                }
            } else {
                EnglishSpeekAction oldEnglishSpeekAction = LiveAchievementIRCBll.this.englishSpeekAction;
                TalLanguage talLanguage = null;
                if (oldEnglishSpeekAction != null) {
                    oldEnglishSpeekAction.stop(null);
                    talLanguage = oldEnglishSpeekAction.getTalLanguage();
                }
                StarInteractAction starAction;
                EnglishSpeekAction englishSpeekAction = null;
                if (LiveTopic.MODE_CLASS.equals(mode)) {
                    LiveStandAchievementBll starBll = new LiveStandAchievementBll(activity, mLiveType, mGetInfo
                            .getStarCount(), mGetInfo.getGoldCount(), true);
                    starBll.setLiveBll(LiveAchievementIRCBll.this);
                    starBll.setLiveAndBackDebug(contextLiveAndBackDebug);
                    starBll.initView(getLiveViewAction());
                    starAction = starBll;

                    //能量条
                    EnglishStandSpeekBll englishSpeekBll = new EnglishStandSpeekBll(activity);
                    if (speakerRecognitioner != null) {
                        englishSpeekBll.setSpeakerRecognitioner(speakerRecognitioner);
                    }
                    boolean initView = englishSpeekBll.initView(getLiveViewAction(), mGetInfo.getMode(), talLanguage,
                            audioRequest);
                    if (initView) {
                        englishSpeekBll.setTotalOpeningLength(mGetInfo.getTotalOpeningLength());
                        englishSpeekBll.setLiveBll(LiveAchievementIRCBll.this);
                        starBll.setLiveAndBackDebug(contextLiveAndBackDebug);
                        englishSpeekBll.setmShareDataManager(mShareDataManager);
                        englishSpeekAction = englishSpeekBll;
                    }
                } else {
                    LiveAchievementBll starBll = new LiveAchievementBll(activity, mLiveType, mGetInfo// mGetInfo
                            // .getStarCount(),
                            //mGetInfo.getGoldCount()
                            , true);
                    starBll.setLiveBll(LiveAchievementIRCBll.this);
                    starBll.setLiveAndBackDebug(contextLiveAndBackDebug);
                    starBll.initView(getLiveViewAction());
                    starAction = starBll;

                    //能量条
                    EnglishSpeekBll englishSpeekBll = new EnglishSpeekBll(activity, mGetInfo);
                    if (speakerRecognitioner != null) {
                        englishSpeekBll.setSpeakerRecognitioner(speakerRecognitioner);
                    }
                    boolean initView = englishSpeekBll.initView(getLiveViewAction(), mGetInfo.getMode(), talLanguage,
                            audioRequest);
                    if (initView) {
                        englishSpeekBll.setTotalOpeningLength(mGetInfo.getTotalOpeningLength());
                        englishSpeekBll.setLiveBll(LiveAchievementIRCBll.this);
                        englishSpeekBll.setmShareDataManager(mShareDataManager);
                        englishSpeekAction = englishSpeekBll;
                    }
                }
                LiveAchievementIRCBll.this.starAction = starAction;
                LiveAchievementIRCBll.this.englishSpeekAction = englishSpeekAction;
            }
        }
    }


    /**
     * 半身直播
     */
    private class EnglishSpeekModHalfBody implements EnglishSpeekMode {

        @Override
        public void initAchievement(String mode) {
            EnglishSpeekAction oldEnglishSpeekAction = LiveAchievementIRCBll.this.englishSpeekAction;
            TalLanguage talLanguage = null;
            if (oldEnglishSpeekAction != null) {
                oldEnglishSpeekAction.stop(null);
                talLanguage = oldEnglishSpeekAction.getTalLanguage();
            }
            StarInteractAction starAction;
            EnglishSpeekAction englishSpeekAction = null;
            if (LiveTopic.MODE_CLASS.equals(mode)) {
                // 本场成就 ：金币 + 星星
                LiveHalfBodyAchievementBll starBll = new LiveHalfBodyAchievementBll(activity, mLiveType, mGetInfo
                        .getStarCount(), mGetInfo.getGoldCount(), true);
                starBll.setLiveBll(LiveAchievementIRCBll.this);
                starBll.setLiveAndBackDebug(mLiveBll);
                starBll.initView(getLiveViewAction());
                starAction = starBll;

                englishSpeekAction = null;

            } else {
                LiveAchievementBll starBll = new LiveAchievementBll(activity, mLiveType, mGetInfo, true);
                starBll.setLiveBll(LiveAchievementIRCBll.this);
                starBll.setLiveAndBackDebug(contextLiveAndBackDebug);
                starBll.initView(getLiveViewAction());
                starAction = starBll;
                //能量条
                EnglishSpeekBll englishSpeekBll = new EnglishSpeekBll(activity, mGetInfo);
                if (speakerRecognitioner != null) {
                    englishSpeekBll.setSpeakerRecognitioner(speakerRecognitioner);
                }
                boolean initView = englishSpeekBll.initView(getLiveViewAction(), mGetInfo.getMode(), talLanguage,
                        audioRequest);
                if (initView) {
                    englishSpeekBll.setTotalOpeningLength(mGetInfo.getTotalOpeningLength());
                    englishSpeekBll.setLiveBll(LiveAchievementIRCBll.this);
                    englishSpeekBll.setmShareDataManager(mShareDataManager);
                    englishSpeekAction = englishSpeekBll;
                }
            }
            LiveAchievementIRCBll.this.starAction = starAction;
            LiveAchievementIRCBll.this.englishSpeekAction = englishSpeekAction;
        }
    }


    private void startAchievement() {
        if (isDestory) {
            mLogtf.d("startAchievement:isDestory=true");
            return;
        }
        if (mGetInfo.getPattern() == LiveVideoConfig.LIVE_PATTERN_2) {
            smallEnglish = 1;
            englishSpeekMode = new EnglishSpeekModeStand();
        } else if (mGetInfo.getPattern() == LiveVideoConfig.LIVE_TYPE_HALFBODY) {
            englishSpeekMode = new EnglishSpeekModHalfBody();
        } else {
            englishSpeekMode = new EnglishSpeekModeNomal();
        }
        initAchievement(mGetInfo.getMode());
    }

    private void initAchievement(final String mode) {
        if (englishSpeekMode != null) {
            post(new Runnable() {
                @Override
                public void run() {
                    englishSpeekMode.initAchievement(mode);
                }
            });
        }
    }

    @Override
    public void setVideoLayout(LiveVideoPoint liveVideoPoint) {
        super.setVideoLayout(liveVideoPoint);
        if (starAction instanceof LiveAchievementEngBll) {
            LiveAchievementEngBll engBll = (LiveAchievementEngBll) starAction;
            engBll.setVideoLayout(liveVideoPoint);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isDestory = true;
        logger.d("onDestroy:speakerRecognitioner=" + speakerRecognitioner);
        if (englishSpeekAction != null) {
            englishSpeekAction.destory();
        }
        if (speakerRecognitioner != null) {
            speakerRecognitioner.destory();
        }
    }

    @Override
    public void onModeChange(String oldMode, String mode, boolean isPresent) {
        initAchievement(mode);
        if (recognizeDialog != null && recognizeDialog.isDialogShow()) {
            recognizeDialog.cancelDialog();
        }
    }

    @Override
    public void setStuStarCount(final long reTryTime, final String starId, final AbstractBusinessDataCallBack
            callBack) {
        getHttpManager().setStuStarCount(mLiveType, mLiveId, starId, new HttpCallBack() {

            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                callBack.onDataSucess();
                mLogtf.d("setStuStarCount:onPmSuccess:responseEntity=" + responseEntity.getJsonObject());
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                callBack.onDataFail(1, msg);
                mLogtf.d("setStuStarCount:onPmFailure:msg=" + msg);
                postDelayedIfNotFinish(new Runnable() {
                    @Override
                    public void run() {
                        setStuStarCount(reTryTime + 1000, starId, callBack);
                    }
                }, reTryTime);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                callBack.onDataFail(2, responseEntity.getErrorMsg());
                mLogtf.d("setStuStarCount:onPmFailure:responseEntity=" + responseEntity.getErrorMsg());
            }
        });
    }

    @Override
    public void setTotalOpeningLength(final long reTryTime, final String duration, final String speakingNum, final
    String speakingLen, final float x, final float y) {
        String classId = mGetInfo.getStudentLiveInfo().getClassId();
        getHttpManager().setTotalOpeningLength(mLiveBll.getCourseId(), mLiveId, classId, duration,
                speakingNum, speakingLen,
                new HttpCallBack(false) {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        //更新小目标开口时长
                        String aimType = null;
                        if (mGetInfo.getBetterMe().getTarget() != null) {
                            aimType = mGetInfo.getBetterMe().getTarget().getAimType();
                        } else if (mGetInfo.getBetterMe().getCurrent() != null) {
                            aimType = mGetInfo.getBetterMe().getCurrent().getType();
                        }
                        if (BetterMeConfig.TYPE_TALKTIME.equals(aimType)) {
                            BetterMeContract.BetterMePresenter betterMePresenter = ProxUtil.getProxUtil().get
                                    (mContext, BetterMeContract.BetterMePresenter.class);
                            if (betterMePresenter != null) {
                                betterMePresenter.updateBetterMe(true);
                            }
                        }
                        logger.d("setTotalOpeningLength:onPmSuccess" + responseEntity.getJsonObject());
                        if (starAction != null) {
                            JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                            if (jsonObject.has("star")) {
                                int star = jsonObject.getInt("star");
                                if (star > 0) {
                                    starAction.onStarAdd(star, x, y);
                                }
                            } else if (jsonObject.has("data")) {
                                //服务端可能返回data两层嵌套
                                int star = jsonObject.getJSONObject("data").getInt("star");
                                if (star > 0) {
                                    starAction.onStarAdd(star, x, y);
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        logger.d("setTotalOpeningLength:onFailure");
                        super.onFailure(call, e);
                        postDelayedIfNotFinish(new Runnable() {
                            @Override
                            public void run() {
                                setTotalOpeningLength(reTryTime + 1000, duration, speakingNum, speakingLen, x, y);
                            }
                        }, reTryTime);
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        logger.d("setTotalOpeningLength:onPmError" + responseEntity.getErrorMsg());
                        super.onPmError(responseEntity);
                    }
                });
    }

    @Override
    public void setNotOpeningNum() {
        getHttpManager().setNotOpeningNum(mGetInfo.getId(), new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                logger.d("setNotOpeningNum:onPmSuccess" + responseEntity.getJsonObject());
            }

            @Override
            public void onFailure(Call call, IOException e) {
                logger.e("setNotOpeningNum:onFailure", e);
                super.onFailure(call, e);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                logger.d("setNotOpeningNum:onPmError" + responseEntity.getErrorMsg());
                super.onPmError(responseEntity);
            }
        });
    }

    @Override
    public void sendStat(int index) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "" + XESCODE.ROOM_STAR_SEND_S);
            jsonObject.put("id", "" + mGetInfo.getStuId());
            jsonObject.put("answer", index);
            sendNoticeToMain(jsonObject);
        } catch (Exception e) {
            // logger.e( "understand", e);
            mLogtf.e("sendStat", e);
        }
    }

    @Override
    public void sendDBStudent(int dbDuration) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "" + XESCODE.XCR_ROOM_DB_STUDENT);
            jsonObject.put("id", "" + mGetInfo.getStuId());
            jsonObject.put("duration", "" + dbDuration);
            sendNoticeToMain(jsonObject);
        } catch (Exception e) {
            // logger.e( "understand", e);
            mLogtf.e("sendDBStudent", e);
        }
    }

    @Override
    public void onTopic(LiveTopic liveTopic, JSONObject jsonObject, boolean modeChange) {
        if (englishSpeekAction != null) {
            LiveTopic.RoomStatusEntity mainRoomstatus = liveTopic.getMainRoomstatus();
            boolean openDbEnergy = mainRoomstatus.isOpenDbEnergy();
            if (openDbEnergy) {
                englishSpeekAction.onDBStart();
            } else {
                englishSpeekAction.onDBStop();
            }
        }
    }

    @Override
    public void onNotice(String sourceNick, String target, JSONObject object, final int type) {
        try {
            switch (type) {
                case XESCODE.ROOM_STAR_OPEN:
                    if (starAction != null) {
                        JSONArray array = object.optJSONArray("data");
                        ArrayList<String> data = new ArrayList<>();
                        if (array != null) {
                            for (int i = 0; i < array.length(); i++) {
                                data.add(array.optString(i));
                            }
                        }
                        String nonce = object.optString("nonce");
                        String starid = object.optString("starid");
                        starAction.onStarStart(data, starid, "", nonce);
                    }
                    break;
                case XESCODE.ROOM_STAR_SEND_T:
                    if (starAction != null) {
                        JSONArray array = object.optJSONArray("data");
                        ArrayList<String> data = new ArrayList<>();
                        if (array != null) {
                            for (int i = 0; i < array.length(); i++) {
                                data.add(array.optString(i));
                            }
                        }
                        int index = object.optInt("answer", -1);
                        String answer = "";
                        if (index >= 0 && index < data.size()) {
                            answer = data.get(index);
                        }
                        String starid = object.optString("starid");
                        starAction.onStarStart(data, starid, answer, "");
                    }
                    break;
                case XESCODE.ROOM_STAR_CLOSE:
                    if (starAction != null) {
                        String id = object.getString("id");
                        Object answerObj = object.get("answer");
                        ArrayList<String> answer = new ArrayList<>();
                        if (answerObj instanceof JSONArray) {
                            JSONArray array = (JSONArray) answerObj;
                            for (int i = 0; i < array.length(); i++) {
                                answer.add(array.optString(i));
                            }
                        } else {
                            answer.add("" + answerObj);
                        }
                        String nonce = object.optString("nonce");
                        starAction.onStarStop(id, answer, nonce);
                    }
                    break;
                case XESCODE.XCR_ROOM_DB_START:
                    if (englishSpeekAction != null) {
                        englishSpeekAction.onDBStart();
                    }
                    break;
                case XESCODE.XCR_ROOM_DB_CLOSE: {
                    if (englishSpeekAction != null) {
                        englishSpeekAction.onDBStop();
                    }
                    break;
                }
                case XESCODE.XCR_ROOM_DB_PRAISE: {
                    if (englishSpeekAction != null) {
                        int answer = object.getInt("answer");
                        englishSpeekAction.praise(answer);
                    }
                    break;
                }
                case XESCODE.XCR_ROOM_DB_REMIND: {
                    if (englishSpeekAction != null) {
                        int answer = object.getInt("answer");
                        englishSpeekAction.remind(answer);
                    }
                    break;
                }
//                case XESCODE.STOPQUESTION: {
//                    updateAchievement("STOPQUESTION");
//                }
//                break;
//                case XESCODE.ARTS_H5_COURSEWARE:
//                    String status = object.optString("status", "off");
//                    if ("off".equals(status)) {
//                        updateAchievement("ARTS_H5_COURSEWARE");
//                    }
//                    break;
//                case XESCODE.ARTS_STOP_QUESTION: {
//                    updateAchievement("ARTS_STOP_QUESTION");
//                    break;
//                }
                default:
                    break;
            }
        } catch (Exception e) {

        }
    }

    //上一个互动题的实体
    private VideoQuestionLiveEntity lastVideoQuestionLiveEntity;

    private class AchieveQuestionShowAction implements QuestionShowAction {

        @Override
        public void onQuestionShow(VideoQuestionLiveEntity videoQuestionLiveEntity, boolean isShow) {
            //英语智能测评需要特殊判断
            if (!isShow && lastVideoQuestionLiveEntity != null
                    && LiveQueConfig.EN_INTELLIGENT_EVALUTION.equals(lastVideoQuestionLiveEntity.type)) {
                return;
            }
//            if (isShow && videoQuestionLiveEntity != null &&
//                    (LiveQueConfig.EN_INTELLIGENT_EVALUTION.equals(videoQuestionLiveEntity.type))) {
//                isEnglishIntelligentEvaluation = true;
//            } else if (!isShow && isEnglishIntelligentEvaluation) {
//                isEnglishIntelligentEvaluation = false;
//                return;
//            } else {
            //如果没有收到英语智能测评的收题指令
//                isEnglishIntelligentEvaluation = false;
//            }
            if (!isShow) {
                updateAchievement("onQuestionShow");
            } else {
                lastVideoQuestionLiveEntity = videoQuestionLiveEntity;
            }
        }

        private void updateAchievement(String method) {
            logger.d("updateAchievement:method=" + method);
            final long before = System.currentTimeMillis();
            postDelayedIfNotFinish(new Runnable() {
                @Override
                public void run() {
                    String liveid = mGetInfo.getId();
                    getHttpManager().getStuGoldCount(liveid, new HttpCallBack() {
                        @Override
                        public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                            final StarAndGoldEntity starAndGoldEntity = getHttpResponseParser().parseStuGoldCount
                                    (responseEntity);
                            mGetInfo.setGoldCount(starAndGoldEntity.getGoldCount());
                            mGetInfo.setStarCount(starAndGoldEntity.getStarCount());
                            StarAndGoldEntity.PkEnergy pkEnergy = starAndGoldEntity.getPkEnergy();
                            LiveGetInfo.EnPkEnergy enpkEnergy = mGetInfo.getEnpkEnergy();
                            enpkEnergy.me = pkEnergy.me;
                            enpkEnergy.myTeam = pkEnergy.myTeam;
                            enpkEnergy.opTeam = pkEnergy.opTeam;
                            long time = System.currentTimeMillis() - before;
                            mLogtf.d("updateAchievement:onPmSuccess:time=" + time);
                            if (starAction != null) {
                                starAction.onGetStar(starAndGoldEntity);
                            }
                        }

                        @Override
                        public void onPmError(ResponseEntity responseEntity) {
                            super.onPmError(responseEntity);
                            logger.d("updateAchievement:onPmError=" + responseEntity.getErrorMsg());
                        }

                        @Override
                        public void onPmFailure(Throwable error, String msg) {
                            super.onPmFailure(error, msg);
                            logger.d("updateAchievement:onPmFailure=" + msg, error);
                        }
                    });
                }
            }, 2000);
        }
    }

    @Override
    public int[] getNoticeFilter() {
        return new int[]{XESCODE.ROOM_STAR_OPEN, XESCODE.ROOM_STAR_SEND_T,
                XESCODE.ROOM_STAR_CLOSE, XESCODE.XCR_ROOM_DB_START, XESCODE.XCR_ROOM_DB_CLOSE,
                XESCODE.XCR_ROOM_DB_PRAISE, XESCODE.XCR_ROOM_DB_REMIND, XESCODE.ARTS_STOP_QUESTION, XESCODE
                .ARTS_H5_COURSEWARE};
    }

    public void onSendMsg(String msg) {
        if (starAction != null) {
            starAction.onSendMsg(msg);
        }
    }

    //语音请求和释放
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                mLogtf.d("start:englishSpeekBll=null?" + (englishSpeekAction == null) + ",isDestory=" + isDestory);
                if (!isDestory && englishSpeekAction != null) {
                    englishSpeekAction.start();
                }
            }
        }
    };

    @Override
    public void request(OnAudioRequest onAudioRequest) {
        audioRequest.set(true);
        logger.d("request:englishSpeekBll=" + (englishSpeekAction == null));
        if (englishSpeekAction != null) {
            handler.removeMessages(1);
            englishSpeekAction.stop(onAudioRequest);
        } else {
            if (onAudioRequest != null) {
                onAudioRequest.requestSuccess();
            }
        }
    }

    @Override
    public void release() {
        audioRequest.set(false);
        logger.d("release:englishSpeekBll=" + (englishSpeekAction == null));
        if (englishSpeekAction != null) {
            handler.sendEmptyMessageDelayed(1, 2000);
        }
    }
}
