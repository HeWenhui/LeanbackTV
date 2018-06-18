package com.xueersi.parentsmeeting.modules.livevideo.page;

import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tal.speech.speechrecognizer.EvaluatorListener;
import com.tal.speech.speechrecognizer.PhoneScore;
import com.tal.speech.speechrecognizer.ResultCode;
import com.tal.speech.speechrecognizer.ResultEntity;
import com.xueersi.common.base.BasePager;
import com.xueersi.common.entity.BaseVideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.QuestionSwitch;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.VoiceAnswerLog;
import com.xueersi.parentsmeeting.widget.VolumeWaveView;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.speech.SpeechEvaluatorUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.file.FileUtils;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;
import com.xueersi.lib.framework.utils.NetWorkHelper;

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
    String eventId = LiveVideoConfig.LIVE_TEST_VOICE;
    private SpeechEvaluatorUtils mIse;
    BaseVideoQuestionEntity baseVideoQuestionEntity;
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
    LiveAndBackDebug liveAndBackDebug;
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
    String type;
    ScoreAndIndex lastMaxScoreAndIndex = null;
    int netWorkType = NetWorkHelper.WIFI_STATE;
    private long entranceTime;

    public VoiceAnswerPager(Context context, BaseVideoQuestionEntity baseVideoQuestionEntity, JSONObject assess_ref, String type, QuestionSwitch questionSwitch, LiveAndBackDebug liveAndBackDebug) {
        super(context);
        this.baseVideoQuestionEntity = baseVideoQuestionEntity;
        this.questionSwitch = questionSwitch;
        this.liveAndBackDebug = liveAndBackDebug;
        this.type = type;
        this.assess_ref = assess_ref;
        if (LocalCourseConfig.QUESTION_TYPE_SELECT.equals(type)) {
            try {
                answer = assess_ref.getJSONArray("answer").getString(0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                JSONArray array = assess_ref.getJSONArray("options");
                answer = array.getJSONObject(0).getJSONArray("content").getString(0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        initListener();
        initData();
    }

    @Override
    public BaseVideoQuestionEntity getBaseVideoQuestionEntity() {
        return baseVideoQuestionEntity;
    }

    @Override
    public void setAudioRequest() {
        Loger.d(TAG, "setAudioRequest:mIse=" + (mIse == null));
        mView.post(new Runnable() {
            @Override
            public void run() {
                if (mIse == null) {
                    mIse = new SpeechEvaluatorUtils(true);
                }
                startEvaluator();
            }
        });
    }

    public void setIse(SpeechEvaluatorUtils ise) {
        this.mIse = ise;
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
                logHashMap.put("sourcetype", sourcetype).put("clicktime", "" + (System.currentTimeMillis() - entranceTime) / 1000);
                logHashMap.addExY().addExpect("1").addSno("6").addStable("2");
                liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap.getData());
                switchQuestion();
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

    private void switchQuestion() {
        Loger.d(TAG, "switchQuestion:isEnd=" + isEnd);
        if (isEnd) {
            return;
        }
        BasePager basePager = questionSwitch.questionSwitch(baseVideoQuestionEntity);
        if (basePager == null) {
            XESToastUtils.showToast(mContext, "切换失败");
        } else {
            userSwitch = true;
            if (mIse != null) {
                mIse.stop();
            }
        }
    }

    @Override
    public void initData() {
        entranceTime = System.currentTimeMillis();
        String questionID = baseVideoQuestionEntity.getvQuestionID();
        Loger.d(TAG, "initData:questionID=" + questionID);
        mView.postDelayed(new Runnable() {
            @Override
            public void run() {
                vwvSpeectevalWave.start();
            }
        }, 1000);
        dir = new File(Environment.getExternalStorageDirectory(), "parentsmeeting/voice/");
        FileUtils.deleteDir(dir);
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
//                        Loger.d(TAG, "initData:audioRequest=" + audioRequest);
//                        quesRequest = true;
//                        if (audioRequest) {
//                            mIse = new SpeechEvaluatorUtils(mContext);
//                            mIse.startEnglishEvaluatorOffline(assess_ref.toString(), saveVideoFile.getPath(), multRef, listener);
//                        }
//                    }
//                });
//            }
//        });
    }

    @Override
    public void stopPlayer() {
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
        isEnd = true;
    }

    @Override
    public void examSubmitAll(String method, String nonce) {
        isEnd = true;
        endnonce = nonce;
        ViewGroup group = (ViewGroup) mView.getParent();
        Loger.d(TAG, "examSubmitAll:method=" + method + ",error=" + isSpeechError + ",success=" + isSpeechSuccess);
        if (group == null) {
            return;
        }
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
        if (mIse != null) {
            mIse.stop();
        }
    }

    class VoiceEvaluatorListener implements EvaluatorListener {
        File saveVideoFile;

        @Override
        public void onBeginOfSpeech() {
            isSpeechError = false;
            Loger.d(TAG, "onBeginOfSpeech");
        }

        @Override
        public void onResult(ResultEntity resultEntity) {
            Loger.d(TAG, "onResult:status=" + resultEntity.getStatus() + ",errorNo=" + resultEntity.getErrorNo() + ",isEnd=" + isEnd);
            if (resultEntity.getStatus() == ResultEntity.SUCCESS) {
                onEvaluatorSuccess(resultEntity);
            } else if (resultEntity.getStatus() == ResultEntity.ERROR) {
                onEvaluatorError(resultEntity);
            }
        }

        @Override
        public void onVolumeUpdate(int volume) {
//            Loger.d(TAG, "onVolumeUpdate:volume=" + volume);
            vwvSpeectevalWave.setVolume(volume * 3);
        }
    }

    VoiceEvaluatorListener listener = new VoiceEvaluatorListener();

    private void onEvaluatorError(final ResultEntity resultEntity) {
        isSpeechError = true;
        if (userSwitch || userBack) {
            return;
        }
        if (isEnd) {
            questionSwitch.stopSpeech(this, baseVideoQuestionEntity);
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
        } else if (resultEntity.getErrorNo() == ResultCode.WEBSOCKET_TIME_OUT || resultEntity.getErrorNo() == ResultCode.NETWORK_FAIL
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
                        switchQuestion();
                    }
                }, 1500);
            }
        } else {
            rlSpeectevalTip.setVisibility(View.VISIBLE);
            ivSpeectevalTip.setImageResource(R.drawable.bg_livevideo_speecteval_tip1);
            tvSpeectevalTip.setText("语音输入有点小问题，\n先手动答题哦（" + resultEntity.getErrorNo() + ")");
            tvSpeectevalTip.setTag("5");
            mView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    switchQuestion();
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
            Loger.d(TAG, "onResult(SUCCESS):phoneScores.isEmpty");
        } else {
            if (LocalCourseConfig.QUESTION_TYPE_SELECT.equals(type)) {
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
                Loger.d(TAG, "onResult(SUCCESS):scores=" + sss + ",rightIndex=" + rightIndex + ",rightCount=" + rightCount + ",isEnd=" + isEnd);
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
                    Loger.d(TAG, "onResult(SUCCESS):more");
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
                        liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap.getData());
                        baseVideoQuestionEntity.nonce = nonce;
                        questionSwitch.onPutQuestionResult(baseVideoQuestionEntity, answer, option, 1, isRight, resultEntity.getSpeechDuration(), isEnd ? "1" : "0", new QuestionSwitch.OnAnswerReslut() {
                            @Override
                            public void onAnswerReslut(BaseVideoQuestionEntity baseVideoQuestionEntity, VideoResultEntity entity) {
                                if (entity != null) {
                                    entity.setYourAnswer(option);
                                    entity.setStandardAnswer(answer);
                                    String sourcetype = questionSwitch.getsourcetype(baseVideoQuestionEntity);
                                    VoiceAnswerLog.sno5(liveAndBackDebug, sourcetype, baseVideoQuestionEntity.getvQuestionID(), baseVideoQuestionEntity.nonce);
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
                                    Loger.d(TAG, "onResult(SUCCESS):onAnswerFailure");
                                    mView.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            startEvaluator();
                                        }
                                    }, 200);
                                }
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (isEnd) {
                        questionSwitch.stopSpeech(VoiceAnswerPager.this, baseVideoQuestionEntity);
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
                        Loger.d(TAG, "onResult(SUCCESS):reread");
                        mView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startEvaluator();
                            }
                        }, 200);
                    }
                }
            } else {
                int score = phoneScores.get(0).getScore();
                boolean isRight = score > 0;
                Loger.d(TAG, "onResult(SUCCESS):score=" + score);
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
                    Loger.d(TAG, "onResult(SUCCESS):reread");
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
                liveAndBackDebug.umsAgentDebugInter(eventId, logHashMap.getData());
                try {
                    JSONArray options = assess_ref.getJSONArray("options");
                    JSONObject jsonObject = options.getJSONObject(0);
                    JSONArray content1 = jsonObject.getJSONArray("content");
//                    XESToastUtils.showToast(mContext, "你的答案" + answer);
                    tvVoiceansSwitch.setVisibility(View.GONE);
                    questionSwitch.uploadVoiceFile(saveVideoFile);
                    isSpeechSuccess = true;
                    questionSwitch.onPutQuestionResult(baseVideoQuestionEntity, content1.getString(0), content1.getString(0), 1, isRight, resultEntity.getSpeechDuration(), isEnd ? "1" : "0", new QuestionSwitch.OnAnswerReslut() {
                        @Override
                        public void onAnswerReslut(BaseVideoQuestionEntity baseVideoQuestionEntity, VideoResultEntity entity) {
                            if (entity != null) {
                                entity.setStandardAnswer(answer);
                                String sourcetype = questionSwitch.getsourcetype(baseVideoQuestionEntity);
                                VoiceAnswerLog.sno5(liveAndBackDebug, sourcetype, baseVideoQuestionEntity.getvQuestionID(), baseVideoQuestionEntity.nonce);
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
                                Loger.d(TAG, "onResult(SUCCESS):onAnswerFailure");
                                mView.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startEvaluator();
                                    }
                                }, 200);
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
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
        saveVideoFile = new File(dir, "ise" + System.currentTimeMillis() + ".mp3");
        listener.saveVideoFile = saveVideoFile;
        mIse.startEnglishEvaluatorOffline(assess_ref.toString(), saveVideoFile.getPath(), multRef, listener);
    }

    static class ScoreAndIndex {
        int score;
        int index;

        @Override
        public String toString() {
            return "" + score;
        }
    }

//    private void onEvaluatorSuccess2(final ResultEntity resultEntity) {
//        if (userSwitch || userBack) {
//            return;
//        }
//        int[] scores = resultEntity.getScores();
//        if (scores == null) {
//            Loger.d(TAG, "onResult(SUCCESS):scores.null");
//        } else {
//            if (LocalCourseConfig.QUESTION_TYPE_SELECT.equals(type)) {
//                String sss = "";
//                ScoreAndIndex maxScoreAndIndex = null;
//                ScoreAndIndex secScoreAndIndex = null;
//                for (int i = 0; i < scores.length; i++) {
//                    int score = scores[i];
//                    sss += score + ",";
//                    if (score >= 70) {
//                        ScoreAndIndex scoreAndIndex = new ScoreAndIndex();
//                        scoreAndIndex.score = score;
//                        scoreAndIndex.index = i;
//                        if (maxScoreAndIndex == null) {
//                            maxScoreAndIndex = scoreAndIndex;
//                        } else {
//                            if (maxScoreAndIndex.score < score) {
//                                secScoreAndIndex = maxScoreAndIndex;
//                                maxScoreAndIndex = scoreAndIndex;
//                            } else {
//                                if (secScoreAndIndex == null) {
//                                    secScoreAndIndex = scoreAndIndex;
//                                } else if (secScoreAndIndex.score < score) {
//                                    secScoreAndIndex = scoreAndIndex;
//                                }
//                            }
//                        }
//                    }
//                }
//                Loger.d(TAG, "onResult(SUCCESS):scores=" + sss + ",max=" + maxScoreAndIndex + ",sec=" + secScoreAndIndex + ",isEnd=" + isEnd);
//                if (maxScoreAndIndex != null) {
//                    boolean isAnswer = false;
//                    if (secScoreAndIndex == null) {
//                        isAnswer = true;
//                    } else {
//                        if (lastMaxScoreAndIndex != null) {
//                            isAnswer = true;
//                        } else {
//                            if (maxScoreAndIndex.score - secScoreAndIndex.score >= 10) {
//                                isAnswer = true;
//                            } else {
//                                lastMaxScoreAndIndex = maxScoreAndIndex;
//                                if (isEnd) {
//                                    isAnswer = true;
//                                } else {
////                                XESToastUtils.showToast(mContext, "你的答案多读");
//                                    rlSpeectevalTip.setVisibility(View.VISIBLE);
//                                    tvSpeectevalTip.setText("要认真作答哦，\n赶紧再来一次！");
//                                    tvSpeectevalTip.setTag("6");
//                                    mView.postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            rlSpeectevalTip.setVisibility(View.GONE);
//                                        }
//                                    }, 1500);
//                                    Loger.d(TAG, "onResult(SUCCESS):more");
//                                    mView.postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            startEvaluator();
//                                        }
//                                    }, 200);
//                                }
//                            }
//                        }
//                    }
//                    if (isAnswer) {
//                        try {
//                            JSONArray options = assess_ref.getJSONArray("options");
//                            JSONObject jsonObject = options.getJSONObject(maxScoreAndIndex.index);
//                            String option = jsonObject.optString("option");
////                            XESToastUtils.showToast(mContext, "你的答案" + option);
//                            tvVoiceansSwitch.setVisibility(View.GONE);
//                            questionSwitch.uploadVoiceFile(saveVideoFile);
//                            isSpeechSuccess = true;
//                            questionSwitch.onPutQuestionResult(baseVideoQuestionEntity, answer, option, maxScoreAndIndex.score, false, resultEntity.getSpeechDuration(), isEnd ? "1" : "0", new QuestionSwitch.OnAnswerReslut() {
//                                @Override
//                                public void onAnswerReslut(BaseVideoQuestionEntity baseVideoQuestionEntity, VideoResultEntity entity) {
//
//                                }
//
//                                @Override
//                                public void onAnswerFailure() {
//                                    isSpeechSuccess = false;
//                                    if (isEnd) {
//                                        questionSwitch.stopSpeech(VoiceAnswerPager.this, baseVideoQuestionEntity);
//                                    } else {
//                                        tvVoiceansSwitch.setVisibility(View.VISIBLE);
////                                        XESToastUtils.showToast(mContext, "提交失败，请重读");
//                                        rlSpeectevalTip.setVisibility(View.VISIBLE);
//                                        tvSpeectevalTip.setText("提交失败，请重读");
//                                        tvSpeectevalTip.setTag("7");
//                                        mView.postDelayed(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                rlSpeectevalTip.setVisibility(View.GONE);
//                                            }
//                                        }, 1500);
//                                        Loger.d(TAG, "onResult(SUCCESS):onAnswerFailure");
//                                        mView.postDelayed(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                startEvaluator();
//                                            }
//                                        }, 200);
//                                    }
//                                }
//                            });
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                } else {
//                    if (isEnd) {
//                        questionSwitch.stopSpeech(VoiceAnswerPager.this, baseVideoQuestionEntity);
//                    } else {
////                        XESToastUtils.showToast(mContext, "重读");
//                        rlSpeectevalTip.setVisibility(View.VISIBLE);
//                        tvSpeectevalTip.setText("要认真作答哦，\n赶紧再来一次");
//                        tvSpeectevalTip.setTag("8");
//                        mView.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                rlSpeectevalTip.setVisibility(View.GONE);
//                            }
//                        }, 1500);
//                        Loger.d(TAG, "onResult(SUCCESS):reread");
//                        mView.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                startEvaluator();
//                            }
//                        }, 200);
//                    }
//                }
//            } else {
//                int score = scores[0];
//                Loger.d(TAG, "onResult(SUCCESS):score=" + score);
//                try {
//                    JSONArray options = assess_ref.getJSONArray("options");
//                    JSONObject jsonObject = options.getJSONObject(0);
//                    JSONArray content1 = jsonObject.getJSONArray("content");
////                    XESToastUtils.showToast(mContext, "你的答案" + answer);
//                    tvVoiceansSwitch.setVisibility(View.GONE);
//                    questionSwitch.uploadVoiceFile(saveVideoFile);
//                    isSpeechSuccess = true;
//                    questionSwitch.onPutQuestionResult(baseVideoQuestionEntity, content1.getString(0), content1.getString(0), score, false, resultEntity.getSpeechDuration(), isEnd ? "1" : "0", new QuestionSwitch.OnAnswerReslut() {
//                        @Override
//                        public void onAnswerReslut(BaseVideoQuestionEntity baseVideoQuestionEntity, VideoResultEntity entity) {
//
//                        }
//
//                        @Override
//                        public void onAnswerFailure() {
//                            isSpeechSuccess = false;
//                            if (isEnd) {
//                                questionSwitch.stopSpeech(VoiceAnswerPager.this, baseVideoQuestionEntity);
//                            } else {
//                                tvVoiceansSwitch.setVisibility(View.VISIBLE);
//                                XESToastUtils.showToast(mContext, "提交失败，请重读");
//                                Loger.d(TAG, "onResult(SUCCESS):onAnswerFailure");
//                                mView.postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        startEvaluator();
//                                    }
//                                }, 200);
//                            }
//                        }
//                    });
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
}
