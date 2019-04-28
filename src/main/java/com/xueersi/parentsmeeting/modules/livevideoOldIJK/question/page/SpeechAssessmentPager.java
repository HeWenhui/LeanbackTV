//package com.xueersi.parentsmeeting.modules.livevideoOldIJK.page;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.drawable.AnimationDrawable;
//import android.os.Build;
//import android.os.Environment;
//import android.view.View;
//import android.view.ViewGroup;
//import android.webkit.ConsoleMessage;
//import android.webkit.WebResourceError;
//import android.webkit.WebResourceRequest;
//import android.webkit.WebSettings;
//import android.webkit.WebView;
//import android.webkit.WebViewClient;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.tal.speech.speechrecognizer.ResultCode;
//import com.tal.speech.speechrecognizer.ResultEntity;
//import com.xueersi.parentsmeeting.modules.livevideo.R;
//import com.xueersi.common.base.BasePager;
//import com.xueersi.common.http.ResponseEntity;
//import com.xueersi.common.logerhelper.LogerTag;
//import com.xueersi.common.logerhelper.XesMobAgent;
//import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
//import com.xueersi.parentsmeeting.modules.livevideo.question.business.OnSpeechEval;
//import com.xueersi.parentsmeeting.modules.livevideo.question.business.SpeechEvalAction;
//import com.xueersi.parentsmeeting.modules.livevideo.entity.SpeechEvalEntity;
//import com.xueersi.common.speech.SpeechEvaluatorUtils;
//import com.xueersi.lib.framework.utils.XESToastUtils;
//import com.xueersi.common.util.audio.AudioPlayer;
//import com.xueersi.common.util.audio.AudioPlayerListening;
//import com.xueersi.lib.framework.utils.file.FileUtils;
//import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;
//import com.xueersi.xesalib.view.RoundProgressBar;
//import com.xueersi.ui.dialog.VerifyCancelAlertDialog;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.concurrent.atomic.AtomicInteger;
//
//import static com.xueersi.xesalib.view.alertdialog.VerifyCancelAlertDialog.MESSAGE_VERIFY_CANCEL_TYPE;
//
///**
// * Created by linyuqiang on 2017/2/24.
// * 语音评测
// */
//public class SpeechAssessmentPager extends BasePager {
//    public static boolean DEBUG = false;
//    private String id;
//    private ArrayList<SocketAndTime> socketAndTimes = new ArrayList<>();
//    /** 录音按键 */
//    private ImageView ivSpeectevalStart;
//    private TextView tvSpeectevalStart;
//    /** 上传按键 */
//    private RelativeLayout rlSpeectevalUpload;
//    private ImageView ivSpeectevalUpload;
//    private TextView tvSpeectevalUpload;
//    private RelativeLayout rlSpeectevalCancle;
//    private ImageView ivSpeectevalCancle;
//    /** 播放按键 */
//    private ImageView ivSpeectevalPlay;
//    private TextView tvSpeectevalPlay;
//    private TextView tvSpeectevalEvaltype;
//    /** 评测语音内容 */
//    private WebView wvSpeectevalEval;
//    /** 评测语音试题计时 */
//    private TextView tvSpeectevalEvaltime;
//    /** 评测语音录音时间 */
//    private TextView tvSpeectevalVideotime;
//    private RoundProgressBar rrpSpeectevalVideo;
//    private View rlSpeectevalGroup;
//    /** 加载失败 */
//    private View errorView;
//    /** 结果页显示 */
//    private WebView wvSpeechResult;
//    private SpeechEvaluatorUtils mIse;
//    SpeechEvalEntity evalEntity;
//    /** 语音保存位置 */
//    private File saveVideoFile;
//    private SpeechEvalAction speechEvalAction;
//    private LogToFile logToFile;
//    private long entranceTime;
//    /** 是不是考试结束 */
//    private boolean isEnd = false;
//    /** 是不是页面加载完成。完成后更新结果 */
//    private boolean onPageFinished = false;
//    /** 是不是直播 */
//    private boolean isLive;
//    private String speechEvalResultUrl;
//
//    public SpeechAssessmentPager(Context context, boolean isLive, String liveid, String stuid, SpeechEvalAction
//            speechEvalAction, String id, String speechEvalResultUrl) {
//        super(context);
//        logToFile = new LogToFile(TAG, new File(Environment.getExternalStorageDirectory(), "parentsmeeting/log/" + TAG
//                + ".txt"));
//        this.isLive = isLive;
//        this.id = id;
//        this.speechEvalResultUrl = speechEvalResultUrl + liveid + "/" + id + "/" + stuid;
//        this.speechEvalAction = speechEvalAction;
//        logToFile.i("SpeechAssessmentPager:id=" + id);
//        entranceTime = System.currentTimeMillis();
//        initData();
//    }
//
//    public String getId() {
//        return id;
//    }
//
//    @Override
//    public View initView() {
//        View view = View.inflate(mContext, R.layout.page_livebackvideo_speecheval_question, null);
//        errorView = view.findViewById(R.id.rl_livevideo_subject_error);
//        tvSpeectevalEvaltype = (TextView) view.findViewById(R.id.tv_livevideo_speecteval_evaltype);
//        rlSpeectevalGroup = view.findViewById(R.id.rl_livevideo_speecteval_group);
//        ivSpeectevalStart = (ImageView) view.findViewById(R.id.iv_livevideo_speecteval_start);
//        tvSpeectevalStart = (TextView) view.findViewById(R.id.tv_livevideo_speecteval_start);
//        ivSpeectevalPlay = (ImageView) view.findViewById(R.id.iv_livevideo_speecteval_play);
//        tvSpeectevalPlay = (TextView) view.findViewById(R.id.tv_livevideo_speecteval_play);
//        rlSpeectevalUpload = (RelativeLayout) view.findViewById(R.id.rl_livevideo_speecteval_upload);
//        ivSpeectevalUpload = (ImageView) view.findViewById(R.id.iv_livevideo_speecteval_upload);
//        tvSpeectevalUpload = (TextView) view.findViewById(R.id.tv_livevideo_speecteval_upload);
//        rlSpeectevalCancle = (RelativeLayout) view.findViewById(R.id.rl_livevideo_speecteval_cancle);
//        ivSpeectevalCancle = (ImageView) view.findViewById(R.id.iv_livevideo_speecteval_cancle);
//        wvSpeectevalEval = (WebView) view.findViewById(R.id.wv_livevideo_speecteval_eval);
//        tvSpeectevalEvaltime = (TextView) view.findViewById(R.id.tv_livevideo_speecteval_evaltime);
//        tvSpeectevalVideotime = (TextView) view.findViewById(R.id.tv_livevideo_speecteval_videotime);
//        wvSpeechResult = (WebView) view.findViewById(R.id.wv_livevideo_speecteval_web);
//        rrpSpeectevalVideo = (RoundProgressBar) view.findViewById(R.id.rrp_livevideo_speecteval_video);
////        rrpSpeectevalVideo.setTextSize(50);
////        rrpSpeectevalVideo.setTextColor(mContext.getResources().getColor(R.color.COLOR_F13232));
////        rrpSpeectevalVideo.setTipText("你的分数");
////        rrpSpeectevalVideo.setTipTextSize(22);
////        rrpSpeectevalVideo.setTipTextColor(mContext.getResources().getColor(R.color.grey));
////        rrpSpeectevalVideo.setTipGap(16);
//        return view;
//    }
//
//    @Override
//    public void initData() {
//        mIse = new SpeechEvaluatorUtils(false);
//        File dir = new File(Environment.getExternalStorageDirectory(), "parentsmeeting/livevideo/");
//        FileUtils.deleteDir(dir);
//        if (!dir.exists()) {
//            dir.mkdirs();
//        }
//        saveVideoFile = new File(dir, "ise" + System.currentTimeMillis() + ".mp3");
//        ivSpeectevalStart.setTag("1");
//        setPlayAndUploadEnabled(false);
//        ivSpeectevalStart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (ivSpeectevalStart.getTag().equals("1")) {
//                    if (socketAndTimes.size() > 0) {
//                        VerifyCancelAlertDialog verifyCancelAlertDialog = new VerifyCancelAlertDialog(mContext,
//                                mBaseApplication, false, MESSAGE_VERIFY_CANCEL_TYPE);
//                        verifyCancelAlertDialog.initInfo("确认重录？");
//                        verifyCancelAlertDialog.setVerifyBtnListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                XesMobAgent.liveIseClick(isLive, "start", "1-1");
//                                tvSpeectevalStart.setText("录制中");
//                                setPlayAndUploadEnabled(false);
//                                startSpeechEval();
//                            }
//                        });
//                        verifyCancelAlertDialog.setCancelBtnListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                XesMobAgent.liveIseClick(isLive, "start", "1-2");
//                            }
//                        });
//                        verifyCancelAlertDialog.showDialog();
//                    } else {
//                        XesMobAgent.liveIseClick(isLive, "start", "1");
//                        tvSpeectevalStart.setText("录制中");
//                        startSpeechEval();
//                        setPlayAndUploadEnabled(false);
//                    }
//                } else {
//                    XesMobAgent.liveIseClick(isLive, "start", "2");
//                    tvSpeectevalVideotime.setVisibility(View.GONE);
//                    stopSpeectevalVideotime();
//                    mIse.stop();
//                    logger.d( "mIse.stop");
//                    rrpSpeectevalVideo.setVisibility(View.GONE);
//                    setSpeechEvalStartPlay();
//                    setPlayAndUploadEnabled(false);
//                }
//            }
//        });
//        ivSpeectevalUpload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                VerifyCancelAlertDialog verifyCancelAlertDialog = new VerifyCancelAlertDialog(mContext,
//                        mBaseApplication, false, MESSAGE_VERIFY_CANCEL_TYPE);
//                verifyCancelAlertDialog.initInfo("确认提交？");
//                verifyCancelAlertDialog.setVerifyBtnListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        sendSpeechEvalResult(true);
//                    }
//                });
//                verifyCancelAlertDialog.showDialog();
//            }
//        });
//        ivSpeectevalCancle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mIse.cancel();
//                logger.d( "mIse.cancel");
//                rlSpeectevalUpload.setVisibility(View.VISIBLE);
//                rlSpeectevalCancle.setVisibility(View.GONE);
//                tvSpeectevalVideotime.setVisibility(View.GONE);
//                stopSpeectevalVideotime();
//                rrpSpeectevalVideo.setVisibility(View.GONE);
//                setSpeechEvalStartPlay();
//                tvSpeectevalStart.setText("录音");
//            }
//        });
//        ivSpeectevalPlay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new Thread() {
//                    @Override
//                    public void run() {
//                        XesMobAgent.liveIseClick(isLive, "play", "1");
//                        AudioPlayer.releaseAudioPlayer(mContext);
//                        AudioPlayer.audioPlayerControl(saveVideoFile.getPath(), mContext, 1000, new
//                                AudioPlayerListening() {
//                                    @Override
//                                    public void playComplete(int where) {
//                                        logger.i( "playComplete");
//                                    }
//
//                                    @Override
//                                    public void prepared(int duration) {
//                                        logger.i( "prepared:duration=" + duration);
//                                    }
//
//                                    @Override
//                                    public void currentDuration(int current, int duration) {
//                                        logger.i( "currentDuration:current=" + current + ",duration=" + duration);
//                                    }
//
//                                }, false, 0, true);
//                    }
//                }.start();
//            }
//        });
//        startLoad("获取互动题");
//        rlSpeectevalGroup.setVisibility(View.INVISIBLE);
//        speechEvalAction.getSpeechEval(id, onSpeechEval);
//        getRootView().findViewById(R.id.btn_error_refresh).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                errorView.setVisibility(View.GONE);
//                if (rlSpeectevalGroup.getParent() == null) {
//                    startLoad("加载结果页");
//                    wvSpeechResult.reload();
//                } else {
//                    startLoad("获取互动题");
//                    speechEvalAction.getSpeechEval(id, onSpeechEval);
//                }
//            }
//        });
//        addJavascriptInterface();
//        wvSpeectevalEval.setWebViewClient(new WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                wvSpeectevalEval.loadUrl(url);
//                return true;
//            }
//        });
//        wvSpeechResult.setWebChromeClient(new MyWebChromeClient());
//        wvSpeechResult.setWebViewClient(new MyWebViewClient());
//    }
//
//    private void startSpeechEval() {
//        rlSpeectevalCancle.setVisibility(View.VISIBLE);
//        rlSpeectevalUpload.setVisibility(View.GONE);
//        String evaText = evalEntity.getAnswer();
//        evaText = evaText.replace("<br>", " ");
//        evaText = evaText.replace("\n", " ");
//        saveVideoFile = new File(saveVideoFile.getParent(), "ise" + System.currentTimeMillis() + ".mp3");
//        mIse.startEnglishEvaluator(evaText, saveVideoFile.getPath(), false, mEvaluatorListener, true, "");
//        setSpeechEvalStartStop();
//        tvSpeectevalVideotime.setVisibility(View.VISIBLE);
//        final AtomicInteger timeInteger = new AtomicInteger(0);
//        tvSpeectevalVideotime.post(new Runnable() {
//            @Override
//            public void run() {
//                tvSpeectevalVideotime.setTag(R.id.tv_livevideo_speecteval_videotime, this);
//                if (tvSpeectevalVideotime.getVisibility() == View.VISIBLE) {
//                    timeInteger.set(timeInteger.get() + 1);
//                    tvSpeectevalVideotime.setText(timeInteger.get() + "S");
//                }
//                tvSpeectevalVideotime.postDelayed(this, 1000);
//            }
//        });
//    }
//
//    /**
//     * 设置播放上传是不是可以点击
//     *
//     * @param enabled
//     */
//    private void setPlayAndUploadEnabled(boolean enabled) {
//        ivSpeectevalPlay.setEnabled(enabled);
//        tvSpeectevalPlay.setEnabled(enabled);
//        ivSpeectevalUpload.setEnabled(enabled);
//        tvSpeectevalUpload.setEnabled(enabled);
//    }
//
//    /** 录音机准备状态 */
//    private void setSpeechEvalStartPlay() {
//        ivSpeectevalStart.setImageResource(R.drawable.bg_livevideo_speecheval_video);
//        ivSpeectevalStart.setTag("1");
//    }
//
//    /** 录音机暂停状态 */
//    private void setSpeechEvalStartStop() {
//        ivSpeectevalStart.setImageResource(R.drawable.bg_livevideo_speecheval_videostop);
//        ivSpeectevalStart.setTag("2");
//    }
//
//    /** 录音时间停止 */
//    private void stopSpeectevalVideotime() {
//        Runnable runnable = (Runnable) tvSpeectevalVideotime.getTag(R.id.tv_livevideo_speecteval_videotime);
//        if (runnable != null) {
//            tvSpeectevalVideotime.removeCallbacks(runnable);
//            tvSpeectevalVideotime.setTag(R.id.tv_livevideo_speecteval_videotime, null);
//        }
//    }
//
//    private OnSpeechEval onSpeechEval = new OnSpeechEval() {
//        @Override
//        public void onSpeechEval(Object object) {
//            evalEntity = (SpeechEvalEntity) object;
//            if (isLive && evalEntity.answered() == 1) {
//                wvSpeechResult.setVisibility(View.VISIBLE);
//                ViewGroup group = (ViewGroup) rlSpeectevalGroup.getParent();
//                group.removeView(rlSpeectevalGroup);
//                wvSpeechResult.loadUrl(speechEvalResultUrl);
//                return;
//            }
//            if (!isLive) {
//                long cha = evalEntity.getEndTime() - evalEntity.getSpeechEvalReleaseTime();
//                evalEntity.setEndTime(evalEntity.getNowTime() + cha);
//            }
//            if ("read_sentence".equals(evalEntity.getTesttype())) {
//                tvSpeectevalEvaltype.setText("口语-句子朗读");
//            } else {
//                tvSpeectevalEvaltype.setText("口语-单词朗读");
//            }
//            stopLoad();
//            errorView.setVisibility(View.GONE);
//            rlSpeectevalGroup.setVisibility(View.VISIBLE);
//            wvSpeectevalEval.loadDataWithBaseURL(null, evalEntity.getContent(), "text/html", "utf-8",
//                    null);
//            //颜色变化
//            final AtomicInteger color = new AtomicInteger(0);
//            if (evalEntity.getNowTime() > evalEntity.getEndTime()) {
//                tvSpeectevalEvaltime.setTextColor(mContext.getResources().getColor(R.color.COLOR_E74C3C));
//                color.set(1);
//            } else {
//                tvSpeectevalEvaltime.setTextColor(mContext.getResources().getColor(R.color.COLOR_6462A2));
//                color.set(2);
//            }
//            tvSpeectevalEvaltime.setText(getTime(evalEntity));
//            tvSpeectevalEvaltime.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    if (getRootView().getParent() != null) {
//                        evalEntity.setNowTime(evalEntity.getNowTime() + 1);
//                        if (evalEntity.getNowTime() > evalEntity.getEndTime()) {
//                            if (color.get() != 1) {
//                                tvSpeectevalEvaltime.setTextColor(mContext.getResources().getColor(R.color.COLOR_E74C3C));
//                                color.set(1);
//                            }
//                        } else {
//                            if (color.get() != 2) {
//                                tvSpeectevalEvaltime.setTextColor(mContext.getResources().getColor(R.color
//                                        .COLOR_6462A2));
//                                color.set(2);
//                            }
//                        }
//                        tvSpeectevalEvaltime.setText(getTime(evalEntity));
//                        tvSpeectevalEvaltime.postDelayed(this, 1000);
//                    }
//                }
//            }, 1000);
//        }
//
//        private String getTime(SpeechEvalEntity evalEntity) {
//            long time;
//            if (evalEntity.getNowTime() > evalEntity.getEndTime()) {
//                time = evalEntity.getNowTime() - evalEntity.getEndTime();
//            } else {
//                time = evalEntity.getEndTime() - evalEntity.getNowTime();
//            }
//            long min = time / 60;
//            long sec = time % 60;
//            long hour = min / 60;
//            min %= 60;
//            if (hour == 0) {
//                return min + "分" + sec + "秒";
//            } else {
//                return hour + "时" + min + "分" + sec + "秒";
//            }
//        }
//
//        @Override
//        public void onPmFailure(Throwable error, String msg) {
//            errorView.setVisibility(View.VISIBLE);
//            stopLoad();
//        }
//
//        @Override
//        public void onPmError(ResponseEntity responseEntity) {
//            errorView.setVisibility(View.VISIBLE);
//            stopLoad();
//        }
//    };
//
//    @android.webkit.JavascriptInterface
//    private void addJavascriptInterface() {
//        WebSettings webSetting = wvSpeechResult.getSettings();
//        webSetting.setJavaScriptEnabled(true);
//        webSetting.setDomStorageEnabled(true);
//        webSetting.setLoadWithOverviewMode(true);
//        webSetting.setBuiltInZoomControls(false);
//        webSetting = wvSpeectevalEval.getSettings();
//        webSetting.setJavaScriptEnabled(true);
//        webSetting.setDomStorageEnabled(true);
//        webSetting.setLoadWithOverviewMode(true);
//        webSetting.setBuiltInZoomControls(false);
////        int scale = DeviceUtils.getScreenWidth(mContext) * 100 / 878;
////        wvSubjectWeb.setInitialScale(scale);
////        // 设置可以支持缩放
////        webSetting.setSupportZoom(true);
////        // 设置出现缩放工具
////        webSetting.setBuiltInZoomControls(true);
////        webSetting.setDisplayZoomControls(false);
//    }
//
//    /** 开始加载 */
//    private void startLoad(String tip) {
//        View loadView = mView.findViewById(R.id.rl_livevideo_subject_loading);
//        if (loadView != null) {
//            loadView.setVisibility(View.VISIBLE);
//            ImageView ivLoading = (ImageView) mView.findViewById(R.id.iv_data_loading_show);
//            ((AnimationDrawable) ivLoading.getBackground()).start();
//            TextView tvLoadingTip = (TextView) loadView.findViewById(R.id.tv_data_loading_tip);
//            tvLoadingTip.setText(tip);
//            logger.i( "startLoad:start");
//        } else {
//            logger.i( "startLoad:else");
//        }
//    }
//
//    /** 停止加载 */
//    private void stopLoad() {
//        View loadView = mView.findViewById(R.id.rl_livevideo_subject_loading);
//        if (loadView != null) {
//            loadView.setVisibility(View.GONE);
//            ImageView ivLoading = (ImageView) mView.findViewById(R.id.iv_data_loading_show);
//            ((AnimationDrawable) ivLoading.getBackground()).stop();
//            logger.i( "stopLoad:stop");
//        } else {
//            logger.i( "stopLoad:else");
//        }
//    }
//
//    // 评测监听接口
//    private com.tal.speech.speechrecognizer.EvaluatorListener mEvaluatorListener = new com.tal.speech
//            .speechrecognizer.EvaluatorListener() {
//        long begin;
//
//        @Override
//        public void onBeginOfSpeech() {
//            begin = System.currentTimeMillis();
//            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
//            logger.i( "onBeginOfSpeech");
//            rrpSpeectevalVideo.setVisibility(View.VISIBLE);
//        }
//
//        @Override
//        public void onResult(ResultEntity resultEntity) {
//            onEndOfSpeech();
//            String evaText = evalEntity.getAnswer();
//            logToFile.i("onResult:evaText=" + evaText.length() + ",status=" + resultEntity.getStatus() + ",errorNo="
//                    + resultEntity.getErrorNo());
//            if (resultEntity.getStatus() != ResultEntity.SUCCESS) {
//                String msg = "提交失败，再读一次哦！";
//                if (resultEntity.getErrorNo() == ResultCode.MUTE_AUDIO || resultEntity.getErrorNo() == ResultCode
//                        .MUTE) {
//                    msg = "再大点声，你是最棒的!";
//                }
//                XESToastUtils.showToast(mContext, msg + "(" + resultEntity.getErrorNo() + ")");
//                setSpeechEvalStartPlay();
//                rrpSpeectevalVideo.setVisibility(View.GONE);
//                rlSpeectevalCancle.setVisibility(View.GONE);
//                rlSpeectevalUpload.setVisibility(View.VISIBLE);
//                stopSpeectevalVideotime();
//                tvSpeectevalVideotime.setVisibility(View.GONE);
//                tvSpeectevalStart.setText("录音");
//
//                return;
//            }
//            setSpeechEvalStartPlay();
//            rlSpeectevalCancle.setVisibility(View.GONE);
//            rlSpeectevalUpload.setVisibility(View.VISIBLE);
//            setPlayAndUploadEnabled(true);
//            SocketAndTime socketAndTime = new SocketAndTime();
//            socketAndTime.total_score = resultEntity.getScore();
//            socketAndTime.time = (System.currentTimeMillis() - begin) / 1000;
//            socketAndTimes.add(socketAndTime);
//            logToFile.i("EvaluatorListener.onResult:total_score=" + resultEntity.getScore());
//            tvSpeectevalStart.setText("重新录音");
//            stopSpeectevalVideotime();
//        }
//
//        @Override
//        public void onVolumeUpdate(int volume) {
//            logger.i( "onVolumeChanged:volume=" + volume);
//            rrpSpeectevalVideo.setProgress(volume);
//        }
//
//        public void onEndOfSpeech() {
//            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
//            logger.i( "onEndOfSpeech");
//            rlSpeectevalCancle.setVisibility(View.GONE);
//            rlSpeectevalUpload.setVisibility(View.VISIBLE);
//            rrpSpeectevalVideo.setVisibility(View.GONE);
//        }
//
//    };
//
//    /**
//     * 提交答案
//     *
//     * @param first 是不是提示toast,重试不弹
//     */
//    private void sendSpeechEvalResult(final boolean first) {
//        startLoad("提交答案");
//        String stuAnswer = "";
//        String times = "";
//        for (int i = 0; i < socketAndTimes.size(); i++) {
//            stuAnswer += socketAndTimes.get(i).total_score + ",";
//            times += socketAndTimes.get(i).time + ",";
//        }
//        if (stuAnswer.length() > 0) {
//            stuAnswer = stuAnswer.substring(0, stuAnswer.length() - 1);
//        }
//        if (times.length() > 0) {
//            times = times.substring(0, times.length() - 1);
//        }
//        entranceTime = System.currentTimeMillis() - entranceTime;
//        logToFile.i("sendSpeechEvalResult:stuAnswer=" + stuAnswer + ",entranceTime=" + entranceTime);
//        speechEvalAction.sendSpeechEvalResult(id, stuAnswer, times, (int) entranceTime, new OnSpeechEval() {
//            @Override
//            public void onSpeechEval(Object object) {
//                logger.i( "sendSpeechEvalResult:onSpeechEval");
//                wvSpeechResult.setVisibility(View.VISIBLE);
//                stopSpeechEval();
//                ViewGroup group = (ViewGroup) rlSpeectevalGroup.getParent();
//                group.removeView(rlSpeectevalGroup);
//                wvSpeechResult.loadUrl(speechEvalResultUrl);
//            }
//
//            @Override
//            public void onPmFailure(Throwable error, String msg) {
//                if (first) {
//                    XESToastUtils.showToast(mContext, mContext
//                            .getResources()
//                            .getString(R.string.net_request_error));
//                }
//                logger.i( "sendSpeechEvalResult:onPmFailure:msg=" + msg);
//                if (isEnd) {
//                    tvSpeectevalPlay.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (mView.getParent() != null) {
//                                sendSpeechEvalResult(false);
//                            }
//                        }
//                    }, 500);
//                } else {
//                    stopLoad();
//                }
//            }
//
//            @Override
//            public void onPmError(ResponseEntity responseEntity) {
//                logger.i( "sendSpeechEvalResult:onPmError=" + responseEntity.getErrorMsg());
//                XESToastUtils.showToast(mContext, responseEntity
//                        .getErrorMsg());
//                stopLoad();
//            }
//        });
//    }
//
//    public void stopSpeechEval() {
//        mIse.cancel();
//        logger.d( "mIse.cancel");
//        new Thread() {
//            @Override
//            public void run() {
//                AudioPlayer.releaseAudioPlayer(mContext);
//            }
//        }.start();
//    }
//
//    public class MyWebChromeClient extends android.webkit.WebChromeClient {
//
//        @Override
//        public void onProgressChanged(WebView view, int newProgress) {
//            super.onProgressChanged(view, newProgress);
//            if (newProgress == 100) {
//                View loadView = mView.findViewById(R.id.rl_livevideo_subject_loading);
//                if (loadView != null) {
//                    stopLoad();
//                }
//            }
//        }
//
//        @Override
//        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
//            ConsoleMessage.MessageLevel mLevel = consoleMessage.messageLevel();
//            boolean isRequst = false;
//            if (mLevel == ConsoleMessage.MessageLevel.ERROR || mLevel == ConsoleMessage.MessageLevel.WARNING) {
//                isRequst = true;
//            }
//            Loger.d(mContext, LogerTag.DEBUG_WEBVIEW_CONSOLE, TAG + ",Level=" + mLevel + "&&," + consoleMessage
//                    .sourceId() +
//                    "&&," + consoleMessage.lineNumber() + "&&," + consoleMessage.message(), isRequst);
//            if (isRequst) {
//                logToFile.i("onConsoleMessage:messageLevel=" + consoleMessage.messageLevel() + ",sourceID=" +
//                        consoleMessage.sourceId()
//                        + ",lineNumber=" + consoleMessage.lineNumber() + ",message=" + consoleMessage.message());
//            }
//            return super.onConsoleMessage(consoleMessage);
//        }
//    }
//
//    public class MyWebViewClient extends WebViewClient {
//        String failingUrl;
//
//        @Override
//        public void onPageStarted(WebView view, String url, Bitmap favicon) {
//            this.failingUrl = null;
//            super.onPageStarted(view, url, favicon);
//        }
//
//        @Override
//        public void onPageFinished(WebView view, String url) {
//            onPageFinished = true;
//            logToFile.i("onPageFinished:url=" + url + ",failingUrl=" + failingUrl);
//            if (failingUrl == null) {
//                wvSpeechResult.setVisibility(View.VISIBLE);
//                errorView.setVisibility(View.GONE);
//            }
//            if (isEnd) {
//                speechExamSubmitAll();
//            }
////            super.onPageFinished(view, url);
//        }
//
//        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
//            super.onReceivedError(view, request, error);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                if (!request.isForMainFrame()) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        logToFile.i("onReceivedError:failingUrl=" + request.getUrl() + ",errorCode=" + error
//                                .getErrorCode());
//                    } else {
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                            logToFile.i("onReceivedError:failingUrl=" + request.getUrl());
//                        }
//                    }
//                }
//            }
//        }
//
//        @Override
//        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//            Loger.d(mContext, LogerTag.DEBUG_WEBVIEW_ERROR, TAG + ",failingUrl=" + failingUrl + "&&," + errorCode +
//                    "&&," + description, true);
//            this.failingUrl = failingUrl;
//            logToFile.i("onReceivedError:failingUrl=" + failingUrl + ",errorCode=" + errorCode);
////            super.onReceivedError(view, errorCode, description, failingUrl);
//            wvSpeechResult.setVisibility(View.INVISIBLE);
//            errorView.setVisibility(View.VISIBLE);
//        }
//
//        @Override
//        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            logToFile.i("shouldOverrideUrlLoading:url=" + url);
//            if ("http://baidu.com/".equals(url)) {
//                ViewGroup group = (ViewGroup) mView.getParent();
//                if (group != null) {
//                    group.removeView(mView);
//                }
//                speechEvalAction.stopSpeech(null, id);
//            } else {
//                if (url.contains("xueersi.com")) {
//                    view.loadUrl(url);
//                }
//            }
//            return true;
//        }
//    }
//
//    public void examSubmitAll() {
//        isEnd = true;
//        if (rlSpeectevalGroup.getParent() == null) {
//            speechExamSubmitAll();
//        } else {
//            mIse.stop();
//            logger.d( "mIse.stop");
//            ivSpeectevalStart.setEnabled(false);
//            ivSpeectevalPlay.setEnabled(false);
//            ivSpeectevalUpload.setEnabled(false);
//            stopSpeechEval();
//            if (socketAndTimes.size() > 0) {
//                sendSpeechEvalResult(true);
//            } else {
//                startLoad("加载结果页");
//                wvSpeechResult.setVisibility(View.VISIBLE);
//                ViewGroup group = (ViewGroup) rlSpeectevalGroup.getParent();
//                group.removeView(rlSpeectevalGroup);
//                wvSpeechResult.loadUrl(speechEvalResultUrl);
//            }
//        }
//    }
//
//    private void speechExamSubmitAll() {
//        if (onPageFinished) {
//            logger.i( "speechExamSubmitAll");
//            wvSpeechResult.loadUrl("javascript:speechExamSubmitAll()");
//        }
//    }
//
//    public boolean onSpeechResult(final String json) {
//        if (onPageFinished) {
//            logger.i( "onSpeechResult");
//            mView.post(new Runnable() {
//                @Override
//                public void run() {
//                    wvSpeechResult.loadUrl("javascript:showSpeechData(" + json + ")");
//                }
//            });
//        }
//        return onPageFinished;
//    }
//
//    class SocketAndTime {
//        float total_score = -1;
//        long time;
//    }
//}
