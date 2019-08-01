package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.content_view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Group;
import android.support.v4.app.FragmentActivity;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.tal.speech.speechrecognizer.PhoneScore;
import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.entity.IEResult;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.entity.SpeechScoreEntity;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.entity.SpeechStopEntity;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.rxutils.RxFilter;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils.IntelligentConstants;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils.OkhttpUtils;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.IntelligentRecognitionContract.IIntelligentRecognitionPresenter;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.IntelligentRecognitionContract.IIntelligentRecognitionView;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.viewmodel.IntelligentRecognitionViewModel;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;
import com.xueersi.ui.widget.WaveView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Request;

import static com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils.IntelligentConstants.INTELLIGENT_LOTTIE_PATH;
import static com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils.IntelligentConstants.PRELOAD_DIR;

/**
 * 英语智能测评Pager
 */
abstract class BaseIntelligentRecognitionPager extends BasePager implements IIntelligentRecognitionView<IIntelligentRecognitionPresenter> {

    protected FragmentActivity mActivity;

    IntelligentRecognitionViewModel viewModel;

    private IIntelligentRecognitionPresenter mPresenter;

    private LottieAnimationView waveLottie;

    private WaveView waveView;

    private LottieAnimationView scoreLottieView;

    private TextView tvContent;

    private LottieAnimationView readyGoLottieView;

    private ImageView ivQuestion;

    private ConstraintLayout layoutScore;
//    private Handler handler = new Handler(Looper.getMainLooper());
    /** 使用回答时间 */
    private int answerTime;
    //结束前的提示结束提示
    private Group groupEndTip;
    /** speech是否初始化成功 */
    private boolean isSpeechReady = false;
    /** 是否拿到后台接口返回的数据 */
    private boolean isResultGet = false;
    /** waveViews是否初始化成功 */
    private boolean waveViewinit = false;

    protected ViewGroup settingViewGroup;
    /** 需要纠音的单词 */
    TextView tvLayoutScoreWord;
    /** 语音测评完需要纠音的分数 */
    TextView tvLayoutScore;

    /** 测评使用的单词列表(句子中的单词拆出来) */
    List<WordInfo> wordList = new LinkedList<>();

    static class WordInfo {
        private String word;
        private int pos;

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }

        public int getPos() {
            return pos;
        }

        public void setPos(int pos) {
            this.pos = pos;
        }
    }

    public BaseIntelligentRecognitionPager(FragmentActivity context) {
        super(context, false);
        this.mActivity = context;
        initView();
    }


    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.page_livevideo_english_intelligent_recognition, null);
        afterInitView(mView);
        return mView;
    }

    public View initView(LayoutInflater inflater, ViewGroup containter, boolean attachToRoot) {
        mView = inflater.inflate(R.layout.page_livevideo_english_intelligent_recognition, containter, attachToRoot);
        afterInitView(mView);
        return mView;
    }

    private void afterInitView(View mView) {
        tvLayoutScore = mView.findViewById(R.id.tv_livevideo_intelligent_recognition_word_score_value);
        tvLayoutScoreWord = mView.findViewById(R.id.tv_livevideo_intelligent_recognition_score_word);
        waveView = mView.findViewById(R.id.wv_livevideo_intelligent_recognition_energy_bar);
        waveLottie = mView.findViewById(R.id.lottie_view_livevideo_intelligent_recognition_waveview_open_start);
        tvContent = mView.findViewById(R.id.tv_livevideo_intelligent_recognition_textview);
        readyGoLottieView = mView.findViewById(R.id.lottie_view_livevideo_intelligent_recognition_ready_go);
        scoreLottieView = mView.findViewById(R.id.lottie_view_livevideo_intelligent_recognition_get_score);
        groupEndTip = mView.findViewById(R.id.group_livevideo_intelligent_recognition_end_tip);
        settingViewGroup = mView.findViewById(R.id.layout_livevideo_intelligent_recognition_permission);
        ivQuestion = mView.findViewById(R.id.iv_livevideo_intelligent_recognition_question);
        layoutScore = mView.findViewById(R.id.layout_livevideo_intelligent_recognition_word_score_background);

        performOpenViewStart();
        initData();
        initListener();
    }

    @Override
    public void initListener() {
        super.initListener();
//        if (scoreLottieView != null) {
//            scoreLottieView.addAnimatorListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    super.onAnimationEnd(animation);
//                    logger.i("ScoreLottieView setValue true");
//                    viewModel.getIsScorePopWindowFinish().setValue(true);
//                }
//
//                @Override
//                public void onAnimationStart(Animator animation, boolean isReverse) {
//                    logger.i("animator start " + isReverse);
//                }
//            });
//        } else {
//            logger.i("scoreLottieView is null");
//        }
    }

    protected void performOpenViewStart() {
        showReadyGo();
    }

    /** 显示开场的ReadyGo动画 */
    private void showReadyGo() {
        if (readyGoLottieView.getVisibility() != View.VISIBLE) {
            readyGoLottieView.setVisibility(View.VISIBLE);
        }
        readyGoLottieView.playAnimation();
        readyGoLottieView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                readyGoLottieView.setVisibility(View.GONE);
                logger.i("ready go end");
            }

            @Override
            public void onAnimationStart(Animator animation, boolean isReverse) {
                logger.i("ready go start");
            }
        });
    }

    private Consumer getConsumer() {
        return new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                logger.i("waveView init");
                waveView.initialize();
                logger.i("waveView start");
                waveView.start();
                waveViewinit = true;
//                if (mPresenter != null) {
//
//                }
            }
        };
    }

    /** 显示得分的Lottie动画 */
    private void showGetGold(String gold) {
        if (scoreLottieView != null) {
            scoreLottieView.playAnimation();
        }
    }

    private boolean isWord() {
        return true;
    }

    @Override
    public void initData() {
//        tvContent.setText(viewModel.getRecordData().getContent());
        viewModel = ViewModelProviders.
                of(mActivity).
                get(IntelligentRecognitionViewModel.class);

        String content;
        if (!TextUtils.isEmpty(content = viewModel.getRecordData().getContent())) {
            tvContent.setText(content);
            handleContentWordList(content);
        }
        viewModel.getIeResultData().observe(mActivity, new Observer<IEResult>() {
            @Override
            public void onChanged(@Nullable IEResult ieResult) {
                logger.i("isResultGet:" + isResultGet);
                isResultGet = true;
                handleResult(ieResult);
            }
        });
        viewModel.getIsSpeechReady().observe(mActivity, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                isSpeechReady = aBoolean;
                if (aBoolean) {
                    performStartWaveLottie();
                }

            }
        });
        viewModel.getVolume().observe(mActivity, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (waveViewinit) {
                    logger.i("wave Volume:" + integer);
                    waveView.setWaveAmplitude(integer / 20.0f);
                }
            }
        });
        viewModel.getIsIntelligentSpeechFinish().observe(mActivity, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean b) {
                revertWaveLottie();
            }
        });
        viewModel.getIsSpeechJudgeFinish().observe(mActivity, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (integer != IntelligentConstants.PERFECT) {
                    logger.i("judge finish wave");
                    performStartWaveLottie();
                    if (isWord()) {
                        if (layoutScore != null && layoutScore.getVisibility() != View.VISIBLE) {
                            layoutScore.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });
        viewModel.getIsFinish().observe(mActivity, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                Observable.just(true).subscribeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (groupEndTip != null &&
                                groupEndTip.getVisibility() != View.VISIBLE) {
                            groupEndTip.setVisibility(View.VISIBLE);
                        }
                    }
                }).delay(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread()).doOnNext(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (groupEndTip.getVisibility() == View.VISIBLE) {
                            groupEndTip.setVisibility(View.GONE);
                        }
                    }
                }).delay(2, TimeUnit.SECONDS, AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
//                        mActivity.finish();
                        viewModel.getIsTop3Show().postValue(true);
                    }
                });

            }
        });
        viewModel.getSpeechScoreData().observe(mActivity, new Observer<SpeechScoreEntity>() {
            @Override
            public void onChanged(@Nullable final SpeechScoreEntity speechScoreEntity) {
                logger.i("show scoreLottieView");
                if (scoreLottieView != null) {
                    if (scoreLottieView.getVisibility() != View.VISIBLE) {
                        logger.i("scoreLottieView setVisible VISIBLE");
                        scoreLottieView.setVisibility(View.VISIBLE);
                    }
                    //替换json资源文件
                    ImageAssetDelegate delegate = new ImageAssetDelegate() {
                        @Override
                        public Bitmap fetchBitmap(LottieImageAsset asset) {
                            if (asset.getFileName().equals("img_5.png")) {
                                return creatFireBitmap(speechScoreEntity.getScore(),
                                        asset.getFileName(), 0xFFFFFFFF);
                            } else if (asset.getFileName().equals("img_0.png")) {
                                return creatFireBitmap("+" + speechScoreEntity.getStar(),
                                        asset.getFileName(), 0xFFFFE376);
                            } else if (asset.getFileName().equals("img_1.png")) {
                                return creatFireBitmap("+" + speechScoreEntity.getGold(),
                                        asset.getFileName(), 0xFFFFE376);
                            }
                            String resPath = INTELLIGENT_LOTTIE_PATH + "images";
                            String jsonPath = INTELLIGENT_LOTTIE_PATH + "data.json";
                            LottieEffectInfo bubbleEffectInfo = new LottieEffectInfo(resPath, jsonPath);
                            return bubbleEffectInfo.fetchBitmapFromAssets(
                                    scoreLottieView,
                                    asset.getFileName(),
                                    asset.getId(),
                                    asset.getWidth(),
                                    asset.getHeight(),
                                    mContext);
                        }
                    };
                    scoreLottieView.setImageAssetDelegate(delegate);
                    scoreLottieView.playAnimation();
                }
            }
        });
        viewModel.getResultPhoneScores().observe(mActivity, new Observer<List<PhoneScore>>() {
            @Override
            public void onChanged(@Nullable List<PhoneScore> list) {
                handleContentWord(list);
            }
        });
    }

    /**
     * 根据测评结果显示测评单词的颜色
     *
     * @param list
     */
    private void handleContentWord(final List<PhoneScore> list) {

//        }
        tvContent.setText(getSpannableString(list, tvContent.getText().toString()));
    }

    protected abstract SpannableString getSpannableString(final List<PhoneScore> list, String content);


    private boolean isNotNullEquals(String word, String contentWord) {
        return !TextUtils.isEmpty(word) &&
                !TextUtils.isEmpty(contentWord) &&
                word.equals(contentWord);
    }

    /** 反转WaveLottie动画 */
    private void revertWaveLottie() {
        logger.i("revertWaveLottie");
        if (waveLottie != null) {
            waveLottie.setSpeed(-1.5f);
            waveLottie.playAnimation();
            waveLottie.addAnimatorListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    waveLottie.removeAnimatorListener(this);
//                    waveLottie.setVisibility(View.GONE);
                }
            });
        }
        if (waveView != null && waveView.getVisibility() != View.GONE) {
            logger.i("waveView setVisible GONE");
//            waveView.setVisibility(View.GONE);
            waveView.stop();
        }
    }

    /**
     * 语音测评和测评接口请求成功后才显示WaveView和lottieView
     */
    protected void performStartWaveLottie() {
        if (isSpeechReady && isResultGet) {
            if (waveLottie.getVisibility() != View.VISIBLE) {
                waveLottie.setVisibility(View.VISIBLE);
            }
            waveLottie.setSpeed(1.5f);
            waveLottie.playAnimation();
            waveLottie.addAnimatorListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    logger.i("waveLottie end,waveView show");
                    super.onAnimationEnd(animation);
                    if (waveView.getVisibility() != View.VISIBLE) {
                        waveView.setVisibility(View.VISIBLE);
                    }
                    waveLottie.removeAnimatorListener(this);
                    mPresenter.startSpeech();
                    logger.i("waveView init");
                    waveView.initialize();
                    logger.i("waveView start");
                    waveView.start();
                    waveViewinit = true;
//                    Observable.
//                            just(true).
//                            delay(300, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()).
//                            subscribe(getConsumer());
                }
            });

        } else {
            logger.i("wave open fail.isSpeechReady:" + isSpeechReady + " isResultGet:" + isResultGet);
        }
    }

    /**
     * 处理从后台拿到的评测数据
     *
     * @param ieResult
     */
    private void handleResult(IEResult ieResult) {
        loadContentImageView(ieResult.getImgSrc());

        performStartWaveLottie();


    }

    abstract void handleContentWordList(String content);

    /**
     * 加载图片，本地预加载是否成功，成功则从本地加载，否则直接加载url
     *
     * @param remoteUrl
     */
    private void loadContentImageView(final String remoteUrl) {
        String testId = viewModel.getRecordData().getMaterialId();
        if (TextUtils.isEmpty(testId)) {
            loadImageViewFormRemote(remoteUrl);
            return;
        }
        //获取预加载的
        File cacheFile = LiveCacheFile.geCacheFile(mActivity, PRELOAD_DIR);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        Date date = new Date();
        final String today = dateFormat.format(date);
        File todayCacheDir = new File(cacheFile, today);
        File todayLiveCacheDir = new File(todayCacheDir, viewModel.getRecordData().getLiveId());
//        final File mMorecachein = new File(todayLiveCacheDir, viewModel.getRecordData().getLiveId());
//        if (!mMorecachein.exists()) {
//            mMorecachein.mkdirs();
//        }
        File mMorecacheout = new File(todayLiveCacheDir, viewModel.getRecordData().getLiveId() + "child");


        mMorecacheout = new File(mMorecacheout, testId);

        File resourseDir = new File(mMorecacheout, testId);
        if (resourseDir.exists()) {
            ivQuestion.setImageURI(Uri.fromFile(resourseDir));
        } else {
            loadImageViewFormRemote(remoteUrl);

        }
    }

    private void loadImageViewFormRemote(String remoteUrl) {
        getRxRemoteBitmap(remoteUrl).observeOn(AndroidSchedulers.mainThread()).
                subscribe(new Consumer<Bitmap>() {
                    @Override
                    public void accept(Bitmap bitmap) throws Exception {
                        ivQuestion.setImageBitmap(bitmap);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        logger.e(throwable.getMessage());
                        throwable.printStackTrace();
                    }
                });
        ;

    }

    private Observable<Bitmap> getRxRemoteBitmap(String remoteUrl) {
        return Observable.just(remoteUrl).filter(RxFilter.filterString()).subscribeOn(Schedulers.io()).
                map(new Function<String, Bitmap>() {
                    @Override
                    public Bitmap apply(String s) throws Exception {
//                            OkHttpClient client = OkhttpUtils.getOkHttpClient();
//                            Request request = new Request.Builder().url(s).build();
//                            Response response = OkhttpUtils.getOkHttpClient().newCall(new Request.Builder().url(s).build()).execute();
//                            Response response = client.newCall(request).execute();
                        return BitmapFactory.decodeStream(
                                OkhttpUtils.getOkHttpClient().newCall(
                                        new Request.Builder().url(s).build()).
                                        execute().body().byteStream());
                    }
                });
    }

    /**
     * 注册停止广播
     * {
     * "answer": "friend",
     * "choiceType": 0,
     * "gold": "2",
     * "id": [
     * "3282277"
     * ],
     * "isAllow42": 1,
     * "isTestUseH5": "0",
     * "nonce": "a451339510194553b2198a0f99cbe300",
     * "num": 0,
     * "package_source": "2",
     * "ptype": 30,
     * "refresh": false,
     * "time": "3",
     * "type": "1104"
     * }
     *
     * @param stopEntity
     */
    @Override
    public void receiveStopEvent(final SpeechStopEntity stopEntity) {

        Observable.
                <Boolean>just(XESCODE.ARTS_STOP_QUESTION == stopEntity.getType()).
                filter(RxFilter.filterTrue()).
                subscribeOn(AndroidSchedulers.mainThread()).
                subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        showGetGold(stopEntity.getGoldNum() + "");
                    }
                });

    }

    @Override
    public void setPresenter(IIntelligentRecognitionPresenter mPresenter) {
        this.mPresenter = mPresenter;
    }

    protected abstract Bitmap creatFireBitmap(String fireNum, String lottieId, int color);
}
