package com.xueersi.parentsmeeting.modules.livevideo.question.page;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tal.speech.config.SpeechConfig;
import com.tal.speech.speechrecognizer.EvaluatorListener;
import com.tal.speech.speechrecognizer.PhoneScore;
import com.tal.speech.speechrecognizer.ResultCode;
import com.tal.speech.speechrecognizer.ResultEntity;
import com.tal.speech.speechrecognizer.SpeechParamEntity;
import com.tal.speech.utils.SpeechUtils;
import com.xueersi.common.base.BasePager;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.entity.BaseVideoQuestionEntity;
import com.xueersi.lib.framework.utils.NetWorkHelper;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.file.FileUtils;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.achievement.business.EnglishSpeekAction;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.SysLogLable;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.BaseVoiceAnswerPager;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionSwitch;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.EngForceSubmit;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.VoiceAnswerLog;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.widget.VolumeWaveView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

/**
 * 语音答题
 * Created by linyuqiang on 2017/12/5.
 */

public class VoiceAnswerPager extends BaseVoiceAnswerPager {
    static int staticInt = 0;
    String TAG = "VoiceAnswerPager" + staticInt++;
    String eventId = LiveVideoConfig.LIVE_TEST_VOICE;
    //    private SpeechEvaluatorUtils mIse;
    private SpeechUtils mIse;
    /** 评测标题 */
    TextView tvVoiceansTitle;
    /** 错误提示 */
    RelativeLayout rlSpeectevalTip;
    /** 错误提示-图片 */
    ImageView ivSpeectevalTip;
    /** 错误提示-文字 */
    TextView tvSpeectevalTip;
    /** 波形 */
    VolumeWaveView vwvSpeectevalWave;
    /** 答题切换 */
    TextView tvVoiceansSwitch;
    QuestionSwitch questionSwitch;
    /** 语音保存位置-目录 */
    File dir;
    /** 语音保存位置 */
    private File saveVideoFile;
    /** 是不是评测失败 */
    private boolean isSpeechError = false;
    private boolean isSpeechSuccess = false;
    /** 评测文本 */
    JSONObject assess_ref;
    String answer;
    boolean multRef = true;
    /** 是不是结束答题 */
    boolean isEnd = false;
    String endnonce;
    /** 是不是用户返回 */
    boolean userBack = false;
    /** 是不是用户切换答题 */
    boolean userSwitch = false;
    private String type;
    private boolean isNewArts;
    int netWorkType = NetWorkHelper.WIFI_STATE;
    private long entranceTime;
    private VideoQuestionLiveEntity mDetail;

    public VoiceAnswerPager(Context context, VideoQuestionLiveEntity baseVideoQuestionEntity, JSONObject assess_ref,
                            String type, QuestionSwitch questionSwitch) {
        super(context);
        setBaseVideoQuestionEntity(baseVideoQuestionEntity);
        this.mDetail = (VideoQuestionLiveEntity) baseVideoQuestionEntity;
        this.questionSwitch = questionSwitch;
        this.type = type;
        this.assess_ref = assess_ref;
        isNewArts = mDetail.isNewArtsH5Courseware();
        String select = null;
        if (isNewArts) {
            if (LocalCourseConfig.QUESTION_TYPE_SELECT_VOICE.equals(mDetail.getVoiceType()) || LocalCourseConfig.QUESTION_TYPE_SELECT_H5VOICE.equals(mDetail.getVoiceType())) {
                try {
                    answer = assess_ref.getJSONArray("answer").getString(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (LocalCourseConfig.QUESTION_TYPE_SELECT_VOICE.equals(type) || LocalCourseConfig.QUESTION_TYPE_SELECT_H5VOICE.equals(type)) {
                try {
                    answer = assess_ref.getJSONArray("answer").getString(0);
                    JSONArray array = assess_ref.getJSONArray("options");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jsonObject = array.getJSONObject(i);
                        if (answer.equals(jsonObject.getString("option"))) {
                            select = jsonObject.getJSONArray("content").getString(0);
                        }
                    }
                } catch (Exception e) {
                    LiveCrashReport.postCatchedException(TAG, e);
                }
            } else {
                try {
                    JSONArray array = assess_ref.getJSONArray("options");
                    answer = array.getJSONObject(0).getJSONArray("content").getString(0);
                } catch (JSONException e) {
                    LiveCrashReport.postCatchedException(TAG, e);
                }
            }
        } else {
            if (LocalCourseConfig.QUESTION_TYPE_SELECT.equals(type)) {
                try {
                    answer = assess_ref.getJSONArray("answer").getString(0);
                } catch (JSONException e) {
                    LiveCrashReport.postCatchedException(TAG, e);
                }
            } else {
                try {
                    JSONArray array = assess_ref.getJSONArray("options");
                    answer = array.getJSONObject(0).getJSONArray("content").getString(0);
                } catch (JSONException e) {
                    LiveCrashReport.postCatchedException(TAG, e);
                }
            }
        }
        mLogtf.addCommon("testid", mDetail.getvQuestionID());
        if (select == null) {
            mLogtf.d("VoiceAnswerPager:answer=" + answer);
        } else {
            mLogtf.d("VoiceAnswerPager:answer=" + answer + "，select=" + select);
        }
        initListener();
        initData();
    }

    @Override
    public void setIse(SpeechUtils mIse) {
        this.mIse = mIse;
    }

    @Override
    public BaseVideoQuestionEntity getBaseVideoQuestionEntity() {
        return baseVideoQuestionEntity;
    }

    @Override
    public void setAudioRequest() {
        mLogtf.d("setAudioRequest:mIse=" + (mIse == null));
        mView.post(new Runnable() {
            @Override
            public void run() {
                if (mIse == null) {
                    mIse = SpeechUtils.getInstance(mContext.getApplicationContext());
                    mIse.prepar();
                }
//                if (mIse == null) {
//                    mIse = new SpeechEvaluatorUtils(true);
//                }

            }
        });
        mView.postDelayed(new Runnable() {
            @Override
            public void run() {
                startEvaluator();
            }
        }, 300);
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_voice_answer, null);
        rlSpeectevalTip = (RelativeLayout) view.findViewById(R.id.rl_livevideo_speecteval_tip);
        tvVoiceansTitle = (TextView) view.findViewById(R.id.tv_livevideo_voiceans_title);
        ivSpeectevalTip = (ImageView) view.findViewById(R.id.iv_livevideo_speecteval_tip);
        tvSpeectevalTip = (TextView) view.findViewById(R.id.tv_livevideo_speecteval_tip);
        vwvSpeectevalWave = (VolumeWaveView) view.findViewById(R.id.vwv_livevideo_speecteval_wave);
        int colors[] = {0x19F13232, 0x32F13232, 0x64F13232, 0x96F13232, 0xFFF13232};
        vwvSpeectevalWave.setColors(colors);
//        vwvSpeectevalWave.setBackColor(0xffeaebf9);
        vwvSpeectevalWave.setBackColor(Color.TRANSPARENT);
        tvVoiceansSwitch = (TextView) view.findViewById(R.id.tv_livevideo_voiceans_switch);
//        tvSpeectevalTip.setText("语音输入有点小问题，\n先手动答题哦（1131)");
        view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            long before;

            @Override
            public void onViewAttachedToWindow(View view) {
                before = System.currentTimeMillis();
            }

            @Override
            public void onViewDetachedFromWindow(View view) {
                mLogtf.d("onViewDetachedFromWindow:time=" + (System.currentTimeMillis() - before));
            }
        });
        return view;
    }

    @Override
    public void initListener() {
        tvVoiceansSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sourcetype = questionSwitch.getsourcetype(baseVideoQuestionEntity);
                StableLogHashMap logHashMap = new StableLogHashMap("changAnswerType");
                logHashMap.put("testtype", "" + type);
                logHashMap.put("testid", "" + baseVideoQuestionEntity.getvQuestionID());
                logHashMap.put("sourcetype", sourcetype).put("clicktime", "" + (System.currentTimeMillis() -
                        entranceTime) / 1000);
                logHashMap.addExY().addExpect("1").addSno("6").addStable("2");
                umsAgentDebugInter(eventId, logHashMap.getData());
                switchQuestion("Click");
            }
        });
//        tvVoiceansTitle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mIse != null) {
//                    startEvaluator();
//                }
//            }
//        });
    }

    private void switchQuestion(String method) {
        mLogtf.d(SysLogLable.switchQuestion, "switchQuestion:method=" + method + ",isEnd=" + isEnd);
        if (isEnd) {
            return;
        }
        BasePager basePager = questionSwitch.questionSwitch(this, baseVideoQuestionEntity);
        if (basePager == null) {
            XESToastUtils.showToast(mContext, "切换失败");
        } else {
            userSwitch = true;
//            if (mIse != null) {
//                mIse.stop();
//            }
            if (mIse != null) {
                mIse.stop();
            }
        }
    }

    @Override
    public void initData() {
        entranceTime = System.currentTimeMillis();
        String questionID = baseVideoQuestionEntity.getvQuestionID();
        logger.d("initData:questionID=" + questionID);
        mView.postDelayed(new Runnable() {
            @Override
            public void run() {
                vwvSpeectevalWave.start();
            }
        }, 1000);
        dir = LiveCacheFile.geCacheFile(mContext, "livevoice");
        FileUtils.deleteFilesInDir(dir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
//        questionSwitch.getQuestion(baseVideoQuestionEntity, new QuestionSwitch.OnQuestionGet() {
//            @Override
//            public void onQuestionGet(BaseVideoQuestionEntity baseQuestionEntity) {
//                mView.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        XESToastUtils.showToast(mContext, "得到试题");
//                        logger.d( "initData:audioRequest=" + audioRequest);
//                        quesRequest = true;
//                        if (audioRequest) {
//                            mIse = new SpeechEvaluatorUtils(mContext);
//                            mIse.startEnglishEvaluatorOffline(assess_ref.toString(), saveVideoFile.getPath(),
// multRef, listener);
//                        }
//                    }
//                });
//            }
//        });
    }

    @Override
    public void stopPlayer() {
//        if (mIse != null) {
//            mIse.stop();
//        }
        if (mIse != null) {
            mIse.stop();
        }
    }

    int count = 3;
    private Runnable autoUploadRunnable = new Runnable() {
        @Override
        public void run() {
            tvSpeectevalTip.setText(count + "秒后自动提交");
            if (count > 0) {
                tvSpeectevalTip.setTag("200");
                tvSpeectevalTip.postDelayed(this, 1000);
            } else {
                tvSpeectevalTip.setTag("0");
                rlSpeectevalTip.setVisibility(View.GONE);
//                rlSpeectevalTipGone();
//                if (mIse != null) {
//                    mIse.stop();
//                }
                if (mIse != null) {
                    mIse.stop();
                }
                if (isSpeechError || isSpeechSuccess) {
                    questionSwitch.stopSpeech(VoiceAnswerPager.this, baseVideoQuestionEntity);
                }
            }
            count--;
        }
    };

    @Override
    public boolean isEnd() {
        return isEnd;
    }

    @Override
    public void setEnd() {
        mLogtf.d("setEnd");
        isEnd = true;
    }

    @Override
    public void examSubmitAll(String method, String nonce) {
        isEnd = true;
        endnonce = nonce;
        ViewGroup group = (ViewGroup) mView.getParent();
        mLogtf.d(SysLogLable.voiceAnswerExamSubmitAll, "examSubmitAll:method=" + method + ",group=" + (group == null) + ",error=" + isSpeechError + "," +
                "success=" + isSpeechSuccess);
        if (isSpeechError || isSpeechSuccess) {
            questionSwitch.stopSpeech(VoiceAnswerPager.this, baseVideoQuestionEntity);
        } else {
            rlSpeectevalTip.setVisibility(View.VISIBLE);
            ivSpeectevalTip.setImageResource(R.drawable.bg_livevideo_speecteval_tip3);
            tvSpeectevalTip.setText(count + "秒后自动提交");
            tvSpeectevalTip.setTag("200");
            tvSpeectevalTip.postDelayed(autoUploadRunnable, 1000);
            count--;
        }
    }

    @Override
    public void onUserBack() {
        userBack = true;
//        if (mIse != null) {
//            mIse.stop();
//        }
        if (mIse != null) {
            mIse.stop();
        }
    }

    class VoiceEvaluatorListener implements EvaluatorListener {
        File saveVideoFile;

        @Override
        public void onBeginOfSpeech() {
            isSpeechError = false;
            logger.d("onBeginOfSpeech");
        }

        @Override
        public void onResult(ResultEntity resultEntity) {
            logger.d("onResult:status=" + resultEntity.getStatus() + ",errorNo=" + resultEntity.getErrorNo() + "," +
                    "isEnd=" + isEnd);
            if (resultEntity.getStatus() == ResultEntity.SUCCESS) {
                onEvaluatorSuccess(resultEntity);
            } else if (resultEntity.getStatus() == ResultEntity.ERROR) {
                onEvaluatorError(resultEntity);
            }
        }

        @Override
        public void onVolumeUpdate(int volume) {
            vwvSpeectevalWave.setVolume(volume * 3);
        }

    }
//    class VoiceEvaluatorListener implements EvaluatorListener {
//        File saveVideoFile;
//
//        @Override
//        public void onBeginOfSpeech() {
//            isSpeechError = false;
//            logger.d( "onBeginOfSpeech");
//        }
//
//        @Override
//        public void onResult(ResultEntity resultEntity) {
//            logger.d( "onResult:status=" + resultEntity.getStatus() + ",errorNo=" + resultEntity.getErrorNo() + ",
// isEnd=" + isEnd);
//            if (resultEntity.getStatus() == ResultEntity.SUCCESS) {
//                onEvaluatorSuccess(resultEntity);
//            } else if (resultEntity.getStatus() == ResultEntity.ERROR) {
//                onEvaluatorError(resultEntity);
//            }
//        }
//
//        @Override
//        public void onVolumeUpdate(int volume) {
////            logger.d( "onVolumeUpdate:volume=" + volume);
//            vwvSpeectevalWave.setVolume(volume * 3);
//        }
//    }

    VoiceEvaluatorListener listener = new VoiceEvaluatorListener();

    private void onEvaluatorError(final ResultEntity resultEntity) {
        mLogtf.d(SysLogLable.voiceError, "onEvaluatorError:userSwitch=" + userSwitch + ",userBack=" + userBack + ",isEnd=" + isEnd + ",errorNo=" + resultEntity.getErrorNo());
        isSpeechError = true;
        if (userSwitch || userBack) {
            return;
        }
        if (isEnd) {
            //语音答题也强制提交
//            questionSwitch.stopSpeech(this, baseVideoQuestionEntity);
            if (LocalCourseConfig.QUESTION_TYPE_SELECT.equals(type) || LocalCourseConfig.QUESTION_TYPE_SELECT_VOICE.equals(type) || LocalCourseConfig.QUESTION_TYPE_SELECT_H5VOICE.equals(type)) {
                submitQuestionSelect("", false, resultEntity.getSpeechDuration());
            } else {
                try {
                    JSONArray options = assess_ref.getJSONArray("options");
                    JSONObject jsonObject = options.getJSONObject(0);
                    JSONArray content1 = jsonObject.getJSONArray("content");
                    submitQuestionBlack(content1.getString(0), "", false, resultEntity.getSpeechDuration());
                } catch (Exception e) {
                    mLogtf.e("onEvaluatorError:submitQuestionBlack", e);
                }
            }
            return;
        }
        if (resultEntity.getErrorNo() == ResultCode.MUTE_AUDIO || resultEntity.getErrorNo() == ResultCode
                .MUTE) {
            rlSpeectevalTip.setVisibility(View.VISIBLE);
            tvSpeectevalTip.setText("声音有点小，\n再来一次哦！");
//            tvSpeectevalTip.setTag("2");
            mView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startEvaluator();
                }
            }, 300);
            mView.postDelayed(new Runnable() {
                @Override
                public void run() {
//                    rlSpeectevalTip.setVisibility(View.GONE);
                    rlSpeectevalTipGone();
                }
            }, 1500);
            return;
        } else if (resultEntity.getErrorNo() == ResultCode.NO_AUTHORITY) {
            rlSpeectevalTip.setVisibility(View.VISIBLE);
            ivSpeectevalTip.setImageResource(R.drawable.bg_livevideo_speecteval_tip1);
            tvSpeectevalTip.setText("麦克风不可用，\n快去检查一下");
            tvSpeectevalTip.setTag("3");
//            tvSpeectevalError.setText("好像没网了，快检查一下");
//            mView.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    rlSpeectevalTip.setVisibility(View.GONE);
//                    switchQuestion();
//                }
//            }, 1500);
        } else if (resultEntity.getErrorNo() == ResultCode.WEBSOCKET_TIME_OUT || resultEntity.getErrorNo() ==
                ResultCode.NETWORK_FAIL
                || resultEntity.getErrorNo() == ResultCode.WEBSOCKET_CONN_REFUSE) {
            int netWorkType = NetWorkHelper.getNetWorkState(mContext);
            if (netWorkType == NetWorkHelper.NO_NETWORK) {
                rlSpeectevalTip.setVisibility(View.VISIBLE);
                ivSpeectevalTip.setImageResource(R.drawable.bg_livevideo_speecteval_tip1);
                tvSpeectevalTip.setText("好像没网了，\n快检查一下");
                tvSpeectevalTip.setTag("100");
            } else {
                rlSpeectevalTip.setVisibility(View.VISIBLE);
                ivSpeectevalTip.setImageResource(R.drawable.bg_livevideo_speecteval_tip1);
                tvSpeectevalTip.setText("服务器连接不上，\n切换手动答题");
                tvSpeectevalTip.setTag("4");
//            tvSpeectevalError.setText("好像没网了，快检查一下");
                mView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        rlSpeectevalTip.setVisibility(View.GONE);
                        rlSpeectevalTipGone();
                        switchQuestion("NO_NETWORK");
                    }
                }, 1500);
            }
        } else if (resultEntity.getErrorNo() == ResultCode.SPEECH_CANCLE) {
//            mView.post(new Runnable() {
//                @Override
//                public void run() {
//                    mIse.cancel();
//                }
//            });
            mView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startEvaluator();
                }
            }, 1000);
        } else {
            rlSpeectevalTip.setVisibility(View.VISIBLE);
            ivSpeectevalTip.setImageResource(R.drawable.bg_livevideo_speecteval_tip1);
            tvSpeectevalTip.setText("语音输入有点小问题，\n先手动答题哦（" + resultEntity.getErrorNo() + ")");
            tvSpeectevalTip.setTag("5");
            mView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    switchQuestion("Other");
                }
            }, 1500);
        }
    }

    private void onEvaluatorSuccess(final ResultEntity resultEntity) {
        if (userSwitch || userBack) {
            return;
        }
        List<PhoneScore> phoneScores = resultEntity.getLstPhonemeScore();
        int[] scores = resultEntity.getScores();
        if (phoneScores.isEmpty()) {
            logger.d("onResult(SUCCESS):phoneScores.isEmpty");
        } else {
            if (LocalCourseConfig.QUESTION_TYPE_SELECT.equals(type) || LocalCourseConfig.QUESTION_TYPE_SELECT_VOICE.equals(type) || LocalCourseConfig.QUESTION_TYPE_SELECT_H5VOICE.equals(type)) {
                logger.e("选择题！！！" + "type:" + type);
                int rightIndex = -1;
                int rightCount = 0;
                String sss = "";
                for (int i = 0; i < phoneScores.size(); i++) {
                    PhoneScore phoneScore = phoneScores.get(i);
                    if (phoneScore.getScore() == 1) {
                        if (rightIndex == -1) {
                            rightIndex = i;
                        }
                        sss += phoneScore.getScore() + ",";
                        rightCount++;
                    }
                }
                mLogtf.d(SysLogLable.voiceSelectResult, "onResult(SUCCESS):scores=" + sss + ",rightIndex=" + rightIndex + ",rightCount=" +
                        rightCount + ",isEnd=" + isEnd);
                if (rightCount > 1) {
                    rlSpeectevalTip.setVisibility(View.VISIBLE);
                    tvSpeectevalTip.setText("认真些，\n再来一次吧（" + resultEntity.getCurStatus() + ")");
                    tvSpeectevalTip.setTag("6");
                    mView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
//                            rlSpeectevalTip.setVisibility(View.GONE);
                            rlSpeectevalTipGone();
                        }
                    }, 1500);
                    logger.d("onResult(SUCCESS):more");
                    mView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startEvaluator();
                        }
                    }, 200);
                } else if (rightCount == 1) {
                    try {
                        JSONArray options = assess_ref.getJSONArray("options");
                        JSONObject jsonObject = options.getJSONObject(rightIndex);
                        final String option = jsonObject.optString("option");
//                            XESToastUtils.showToast(mContext, "你的答案" + option);
                        tvVoiceansSwitch.setVisibility(View.GONE);
                        questionSwitch.uploadVoiceFile(saveVideoFile);
                        isSpeechSuccess = true;
                        boolean isRight = option.equalsIgnoreCase(answer);

                        String sourcetype = questionSwitch.getsourcetype(baseVideoQuestionEntity);
                        String nonce = StableLogHashMap.creatNonce();
                        StableLogHashMap logHashMap = new StableLogHashMap("submitAnswerResult");
                        logHashMap.put("testtype", "" + type);
                        logHashMap.put("testid", "" + baseVideoQuestionEntity.getvQuestionID());
                        logHashMap.put("submittype", isEnd ? "force" : "active");
                        logHashMap.put("sourcetype", sourcetype).put("stuanswer", isRight ? "Y" : "N");
                        logHashMap.addExY().addExpect("1").addSno("4").addNonce("" + nonce).addStable("1");
                        umsAgentDebugInter(eventId, logHashMap.getData());
                        baseVideoQuestionEntity.nonce = nonce;
                        submitQuestionSelect(option, isRight, resultEntity.getSpeechDuration());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (isEnd) {
                        submitQuestionSelect("", false, resultEntity.getSpeechDuration());
//                        questionSwitch.stopSpeech(VoiceAnswerPager.this, baseVideoQuestionEntity);
                    } else {
//                        XESToastUtils.showToast(mContext, "重读");
                        rlSpeectevalTip.setVisibility(View.VISIBLE);
                        tvSpeectevalTip.setText("认真些，\n再来一次吧(" + resultEntity.getCurStatus() + ")");
                        tvSpeectevalTip.setTag("8");
                        mView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
//                                rlSpeectevalTip.setVisibility(View.GONE);
                                rlSpeectevalTipGone();
                            }
                        }, 1500);
                        logger.d("onResult(SUCCESS):reread");
                        mView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startEvaluator();
                            }
                        }, 200);
                    }
                }
            } else {
                logger.e("  填空题！！！" + "type:" + type);
                int score = phoneScores.get(0).getScore();
                boolean isRight = score > 0;
                mLogtf.d(SysLogLable.voiceFillinResult, "onResult(SUCCESS):score=" + score + ",curstatus=" + resultEntity.getCurStatus());
                if (!isEnd && !isRight && resultEntity.getCurStatus() == 5) {
                    rlSpeectevalTip.setVisibility(View.VISIBLE);
                    tvSpeectevalTip.setText("认真些，\n再来一次吧");
                    tvSpeectevalTip.setTag("9");
                    mView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
//                                rlSpeectevalTip.setVisibility(View.GONE);
                            rlSpeectevalTipGone();
                        }
                    }, 1500);
                    logger.d("onResult(SUCCESS):reread");
                    mView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startEvaluator();
                        }
                    }, 200);
                    return;
                }
                String sourcetype = questionSwitch.getsourcetype(baseVideoQuestionEntity);
                String nonce = StableLogHashMap.creatNonce();
                StableLogHashMap logHashMap = new StableLogHashMap("submitAnswerResult");
                logHashMap.put("testtype", "" + type);
                logHashMap.put("testid", "" + baseVideoQuestionEntity.getvQuestionID());
                logHashMap.put("submittype", isEnd ? "force" : "active").put("sourcetype", sourcetype);
                logHashMap.put("stuanswer", isRight ? "Y" : "N");
                logHashMap.addExY().addExpect("1").addSno("4").addNonce("" + nonce).addStable("1");
                baseVideoQuestionEntity.nonce = nonce;
                umsAgentDebugInter(eventId, logHashMap.getData());
                try {
                    JSONArray options = assess_ref.getJSONArray("options");
                    JSONObject jsonObject = options.getJSONObject(0);
                    JSONArray content1 = jsonObject.getJSONArray("content");
//                    XESToastUtils.showToast(mContext, "你的答案" + answer);
                    tvVoiceansSwitch.setVisibility(View.GONE);
                    questionSwitch.uploadVoiceFile(saveVideoFile);
                    isSpeechSuccess = true;
                    String result = isRight ? "" + content1.getString(0) : "";
                    submitQuestionBlack(content1.getString(0), result, isRight, resultEntity.getSpeechDuration());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void submitQuestionSelect(final String option, boolean isRight, double speechDuration) {
        String isSubmit = EngForceSubmit.getSubmit(isNewArts, isEnd);
        questionSwitch.onPutQuestionResult(this, baseVideoQuestionEntity, answer, option, 1, isRight,
                speechDuration, isSubmit, new QuestionSwitch
                        .OnAnswerReslut() {
                    @Override
                    public void onAnswerReslut(BaseVideoQuestionEntity baseVideoQuestionEntity,
                                               VideoResultEntity entity) {
                        if (entity != null) {
                            entity.setYourAnswer(option);
                            entity.setStandardAnswer(answer);
                            String sourcetype = questionSwitch.getsourcetype(baseVideoQuestionEntity);
                            VoiceAnswerLog.sno5(VoiceAnswerPager.this, sourcetype, baseVideoQuestionEntity
                                    .getvQuestionID(), baseVideoQuestionEntity.nonce);
                            // 发送已答过这题的标识
//                                    EventBus.getDefault().post(new ArtsAnswerResultEvent(baseVideoQuestionEntity
// .getvQuestionID(),2));
                        }
                    }

                    @Override
                    public void onAnswerFailure() {
                        isSpeechSuccess = false;
                        if (isEnd) {
                            questionSwitch.stopSpeech(VoiceAnswerPager.this, baseVideoQuestionEntity);
                        } else {
                            tvVoiceansSwitch.setVisibility(View.VISIBLE);
//                                        XESToastUtils.showToast(mContext, "提交失败，请重读");
                            rlSpeectevalTip.setVisibility(View.VISIBLE);
                            tvSpeectevalTip.setText("提交失败，请重读");
                            tvSpeectevalTip.setTag("7");
                            mView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
//                                            rlSpeectevalTip.setVisibility(View.GONE);
                                    rlSpeectevalTipGone();
                                }
                            }, 1500);
                            logger.d("onResult(SUCCESS):onAnswerFailure");
                            mView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startEvaluator();
                                }
                            }, 200);
                        }
                    }
                });
    }

    private void submitQuestionBlack(final String answer, final String result, boolean isRight, double speechDuration) {
        String isSubmit = EngForceSubmit.getSubmit(isNewArts, isEnd);
        questionSwitch.onPutQuestionResult(this, baseVideoQuestionEntity, answer,
                result, 1, isRight, speechDuration, isSubmit,
                new QuestionSwitch.OnAnswerReslut() {
                    @Override
                    public void onAnswerReslut(BaseVideoQuestionEntity baseVideoQuestionEntity, VideoResultEntity
                            entity) {
                        if (entity != null) {
                            entity.setYourAnswer(result);
                            entity.setStandardAnswer(answer);
                            String sourcetype = questionSwitch.getsourcetype(baseVideoQuestionEntity);
                            VoiceAnswerLog.sno5(VoiceAnswerPager.this, sourcetype, baseVideoQuestionEntity
                                    .getvQuestionID(), baseVideoQuestionEntity.nonce);
                            // 发送已答过这题的标识
//                                EventBus.getDefault().post(new ArtsAnswerResultEvent(baseVideoQuestionEntity
// .getvQuestionID(),ArtsAnswerResultEvent.TYPE_NATIVE_ANSWERRESULT));
                        }
                    }

                    @Override
                    public void onAnswerFailure() {
                        isSpeechSuccess = false;
                        if (isEnd) {
                            questionSwitch.stopSpeech(VoiceAnswerPager.this, baseVideoQuestionEntity);
                        } else {
                            tvVoiceansSwitch.setVisibility(View.VISIBLE);
                            XESToastUtils.showToast(mContext, "提交失败，请重读");
                            logger.d("onResult(SUCCESS):onAnswerFailure");
                            mView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startEvaluator();
                                }
                            }, 200);
                        }
                    }
                });
    }

    @Override
    public void onNetWorkChange(int netWorkType) {
        this.netWorkType = netWorkType;
        if (netWorkType != NetWorkHelper.NO_NETWORK) {
            if ("100".equals(tvSpeectevalTip.getTag())) {
//                rlSpeectevalTip.setVisibility(View.GONE);
                rlSpeectevalTipGone();
                startEvaluator();
            }
        }
    }

    private void rlSpeectevalTipGone() {
        if (!"200".equals(tvSpeectevalTip.getTag())) {
            rlSpeectevalTip.setVisibility(View.GONE);
        }
    }

    private void startEvaluator() {
        if (isEnd) {
            ViewGroup group = (ViewGroup) mView.getParent();
            mLogtf.d("examSubmitAll:group=" + group);
            if (group == null) {
                return;
            }
            questionSwitch.stopSpeech(VoiceAnswerPager.this, baseVideoQuestionEntity);
            return;
        }
        saveVideoFile = new File(dir, "ise" + System.currentTimeMillis() + ".mp3");
        listener.saveVideoFile = saveVideoFile;
        SpeechParamEntity param = new SpeechParamEntity();
        param.setRecogType(SpeechConfig.SPEECH_ENGLISH_EVALUATOR_OFFLINE);
        param.setLocalSavePath(saveVideoFile.getPath());
        param.setStrEvaluator(assess_ref.toString());
        param.setMultRef(multRef);
        //添加试题信息
        Bundle extra = new Bundle();
        extra.putString("testid", "" + baseVideoQuestionEntity.id);
        extra.putString("creattime", "" + creattime);
        param.setExtraBundle(extra);
        mIse.startRecog(param, listener);
//        mIse.startEnglishEvaluatorOffline(assess_ref.toString(), saveVideoFile.getPath(), multRef, listener);
    }

}
