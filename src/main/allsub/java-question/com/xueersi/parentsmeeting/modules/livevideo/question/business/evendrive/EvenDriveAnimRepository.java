package com.xueersi.parentsmeeting.modules.livevideo.question.business.evendrive;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveViewAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.evendrive.view.QuestionResultMiddleEvenDrivePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.evendrive.view.QuestionResultPrimaryEvenDrivePager;
import com.xueersi.parentsmeeting.modules.livevideo.utils.RxjavaUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class EvenDriveAnimRepository implements TasksDataSource {
    Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
    private LiveGetInfo getInfo;

    LiveHttpManager liveHttpManager;

    private Context context;
    private LiveViewAction liveViewAction;

    private TasksDataSource evenDriveAnimDataSource;

    private QuestionResultEvenDrivePager evenDrivePager;

    public EvenDriveAnimRepository(Context context, LiveGetInfo getInfo,
                                   LiveHttpManager liveHttpManager, LiveViewAction liveViewAction) {
        this.getInfo = getInfo;
        this.liveHttpManager = liveHttpManager;
        if (liveHttpManager == null) {
            this.liveHttpManager = new LiveHttpManager(context);
        }
        this.context = context;
        this.liveViewAction = liveViewAction;
    }

    @Override
    public void getDataSource(EvenDriveQuestionType question_type, String testId, LoadAnimCallBack loadAnimCallBack) {
        getResultSuccess(question_type, testId, loadAnimCallBack);
    }

    public enum EvenDriveQuestionType {
        INIT_EVEN_NUM,
        QUES_TYPE_ENGLISH_NEW_PLATFORM,
        QUES_TYPE_CHS_SELF_UPLOAD,
        QUES_TYPE_CHS_NEW_PLAYFROM
    }

    private void getResultSuccess(final EvenDriveQuestionType question_type, String testId,
                                  final LoadAnimCallBack loadAnimCallBack) {

        if (EvenDriveUtils.isOpenStimulation(getInfo)) {
            if (evenDriveAnimDataSource == null) {
                evenDriveAnimDataSource = new EvenDriveAnimDataSource(liveHttpManager, getInfo);
            }
            evenDriveAnimDataSource.getDataSource(
                    question_type,
                    testId,
                    new LoadAnimCallBack() {

                        @Override
                        public void onDataNotAvailable() {
                            if (loadAnimCallBack != null) {
                                loadAnimCallBack.onDataNotAvailable();
                            }
                        }

                        @Override
                        public void onDatasLoaded(String num) {
                            if (question_type != EvenDriveQuestionType.INIT_EVEN_NUM) {
                                showAnima(num);
                            }
                            if (loadAnimCallBack != null) {
                                loadAnimCallBack.onDatasLoaded(num);
                            }
                        }
                    });
        }
    }

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private boolean isPrimarySchool() {
        return getInfo != null && (getInfo.getSmallEnglish() ||
                getInfo.isPrimaryChinese() || getInfo.getIsPrimarySchool() == 1 || getInfo.getUseSkin() == 2);
    }

    private void showAnima(String num) {
        final int mNum = Integer.valueOf(num);
        if (mNum >= 2) {
            if (liveViewAction != null) {
                if (evenDrivePager == null) {

                    if (isPrimarySchool()) {
                        evenDrivePager = new QuestionResultPrimaryEvenDrivePager(context, getInfo);
                    } else {
                        evenDrivePager = new QuestionResultMiddleEvenDrivePager(context, getInfo);
                    }
                }

                doShowAnim(mNum);

                Observable.just(true).
//                    delay(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread()).
//                    doOnNext(new Consumer<Boolean>() {
//                        @Override
//                        public void accept(Boolean aBoolean) throws Exception {
//                            if (mNum >= 2) {
//                                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//                                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
//                                liveViewAction.addView(evenDrivePager.getRootView(), layoutParams);
//                                evenDrivePager.showNum(mNum);
//                                logger.i("add lottie view");
//                            }
//                        }
//                    }).
        delay(3, TimeUnit.SECONDS, AndroidSchedulers.mainThread()).
                        subscribe(new RxjavaUtils.CommonRxObserver() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                super.onSubscribe(d);
                                compositeDisposable.dispose();
                            }

                            @Override
                            public void onComplete() {
                                super.onComplete();
                                disposable.dispose();
                                evenDrivePager.rmLottieView();
                                removeViewAndAnima();
                            }
                        });
            }
        }
    }

    public void removeViewAndAnima() {
        if (evenDrivePager != null && evenDrivePager.getRootView().getParent() != null) {
            logger.i("remove lottie view");
            evenDrivePager.onDestroy();
            ((ViewGroup) evenDrivePager.getRootView().getParent()).removeView(evenDrivePager.getRootView());
        }
    }

    private void doShowAnim(int mNum) {
        if (mNum >= 2) {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            liveViewAction.addView(evenDrivePager.getRootView(), layoutParams);
            evenDrivePager.showNum(mNum);
            logger.i("add lottie view");
        }
    }
}
