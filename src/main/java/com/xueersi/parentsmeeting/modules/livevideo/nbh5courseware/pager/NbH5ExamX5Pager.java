package com.xueersi.parentsmeeting.modules.livevideo.nbh5courseware.pager;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.file.FileUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.ContextLiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.config.NbCourseWareConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LivePagerBack;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveAppUserInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.NbCourseWareEntity;
import com.xueersi.parentsmeeting.modules.livevideo.event.NbCourseEvent;
import com.xueersi.parentsmeeting.modules.livevideo.http.NbHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.nbh5courseware.business.NbH5PagerAction;
import com.xueersi.parentsmeeting.modules.livevideo.nbh5courseware.business.NbPresenter;
import com.xueersi.parentsmeeting.modules.livevideo.nbh5courseware.web.NbCourseCache.NbCourseCache;
import com.xueersi.parentsmeeting.modules.livevideo.nbh5courseware.web.NbCourseCache.NbWebJsProvider;
import com.xueersi.parentsmeeting.modules.livevideo.page.BaseWebviewX5Pager;
import com.xueersi.parentsmeeting.modules.livevideo.question.web.OnHttpCode;
import com.xueersi.parentsmeeting.modules.livevideo.question.web.WebInstertJs;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.NbCourseLog;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;
import com.xueersi.parentsmeeting.modules.livevideo.widget.TimeCountTextView;
import com.xueersi.ui.dialog.VerifyCancelAlertDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import ren.yale.android.cachewebviewlib.CacheWebView;
import ren.yale.android.cachewebviewlib.WebViewCache;

/**
 * @ahthor chenkun
 * created 2019/5/5 下午3:54
 * version 1.0
 */
public class NbH5ExamX5Pager extends BaseWebviewX5Pager implements NbH5PagerAction, BaseNbH5CoursewarePager {
    // String url;
    private NbWebJsProvider mJsProvider;

    /**
     * 新课件缓存
     */
    //private NewCourseCache newCourseCache;
    private NbCourseCache newCourseCache;

    /**
     * 直播场次id
     */
    private String mLiveId = "";
    /**
     * js 是否已注入
     */
    private boolean jsInserted = false;

    /**
     * 加载本地结果页面
     **/
    private boolean resultLoaded = false;

    /**
     * 乐步 物理实验 请求拦截关键字
     */
    private static final String NB_COURSE_WARE_URL_KEY_WORD = "exam-phy-player.nobook.com";
    private TimeCountTextView tvTime;
    private TextView tvTitle;
    private Button btnSubmit;
    private RelativeLayout rlCtrContainer;
    private ImageView ivRefresh;
    private NbPresenter mPresenter;
    /**
     * 答题结果反馈
     **/
    private RelativeLayout stepResultContanier;

    /**
     * 连对此次
     **/
    private int correctNum;
    private final NbCourseWareEntity mCourseWareEntity;
    private Object nbTestInfo;
    private boolean isForceSubmit = false;

    /** 加试 url **/
    private String nbExamUrl;

    /** 练习模式Url **/
    private String nbTestModeUrl;

    /**
     * 考试模式
     */
    private static final int MODE_EXAM = 1;
    /**
     * 练习模式
     */
    private static final int MODE_TEST = 0;

    /**
     * 是否点击过刷新按钮
     */
    private boolean isFresh;
    /**
     * 当前模式
     **/
    private int currentMode = MODE_EXAM;
    /** 查看实验报告 **/
    private Button btnReport;
    /** webView 父布局 **/
    private View webViewContainer;

    /** 是否进入过 练习模式 **/
    private boolean testModEntered;

    private LiveAndBackDebug liveAndBackDebug;
    private long loadingStartTime;

    /** 课件是否成功加载过 **/
    private boolean nbLoaded;

    /**
     * 是否已开始提交过程
     */
    private boolean onSubmit;

    /** 当前用户加载 nb 实验时 刷新按钮点击次数 **/
    private int refreshTimes;

    /** 加载本地资源尝试次数 **/

    private static final int LOCAL_RES_LOAD_TRY_TIMES = 3;

    public NbH5ExamX5Pager(Context context, NbCourseWareEntity entity, LivePagerBack livePagerBack, NbPresenter
            presenter) {
        super(context);
        mCourseWareEntity = entity;
        this.nbExamUrl = entity.getUrl();
        mLiveId = entity.getLiveId();
        mPresenter = presenter;
        initWebView();
        setErrorTip("实验加载失败，请重试");
        setLoadTip("实验正在加载，请稍候");
        setLivePagerBack(livePagerBack);
        initData();
        liveAndBackDebug = new ContextLiveAndBackDebug(context);
    }

    @Override
    public String getUrl() {
        return nbExamUrl;
    }

    @Override
    public String getTestId() {
        return mCourseWareEntity != null ? mCourseWareEntity.getExperimentId() : "";
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_nb_courseware_x5, null);
        rlCtrContainer = view.findViewById(R.id.rl_livevideo_nb_course_control);
        //添加底部 答题 交互控制栏
        LayoutInflater.from(mContext).inflate(R.layout.page_livevideo_nb_courseware_control, rlCtrContainer);
        tvTime = view.findViewById(R.id.tv_livevideo_nb_course_time);
        tvTitle = view.findViewById(R.id.tv_livevideo_nb_course_title);
        btnSubmit = view.findViewById(R.id.bt_livevideo_new_course_submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentMode == MODE_EXAM && !onSubmit) {
                    VerifyCancelAlertDialog cancelDialog = new VerifyCancelAlertDialog(mContext, ContextManager.getApplication(), false,
                            VerifyCancelAlertDialog.MESSAGE_VERIFY_CANCEL_TYPE);
                    cancelDialog.setVerifyBtnListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onSubmit = true;
                            tvTime.stop();
                            callNbSubmitMethod();
                        }
                    });
                    cancelDialog.setCancelShowText("取消").setVerifyShowText("确定").initInfo("提交后不能修改，确定提交吗？",
                            VerifyCancelAlertDialog.VERIFY_SELECTED).showDialog();
                } else {
                    endTestMode();
                }
            }
        });
        ivRefresh = view.findViewById(R.id.iv_livevideo_subject_refresh);
        ivRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (resultLoaded) {
                    showResult();
                } else {
                    isFresh = true;
                    VerifyCancelAlertDialog cancelDialog = new VerifyCancelAlertDialog(mContext, ContextManager.getApplication(), false,
                            VerifyCancelAlertDialog.MESSAGE_VERIFY_CANCEL_TYPE);
                    cancelDialog.setVerifyBtnListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!TextUtils.isEmpty(nbExamUrl)) {
                                refreshTimes++;
                                if (refreshTimes == LOCAL_RES_LOAD_TRY_TIMES) {
                                    if (wvSubjectWeb instanceof CacheWebView) {
                                        CacheWebView cacheWebView = (CacheWebView) wvSubjectWeb;
                                        cacheWebView.setCacheStrategy(WebViewCache.CacheStrategy.NORMAL);
                                        cacheWebView.clearCache();
                                    }
                                }
                            }
                            reloadUrl();
                        }
                    });
                    cancelDialog.setCancelShowText("取消").setVerifyShowText("确定").initInfo("确定重新加载实验吗？",
                            VerifyCancelAlertDialog.TITLE_MESSAGE_VERIRY_CANCEL_TYPE).showDialog();
                }
            }
        });
        stepResultContanier = view.findViewById(R.id.rl_livevideo_nb_step_result);
        btnReport = view.findViewById(R.id.btn_livevideo_nb_reportbtn);
        webViewContainer = view.findViewById(R.id.rl_livevideo_subject_web);

        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnReport.setVisibility(View.GONE);
                webViewContainer.setVisibility(View.VISIBLE);
            }
        });


        return view;
    }

    /**
     * 结束练习模式
     */
    private void endTestMode() {
        VerifyCancelAlertDialog cancelDialog = new VerifyCancelAlertDialog(mContext, ContextManager.getApplication(), false,
                VerifyCancelAlertDialog.MESSAGE_VERIFY_CANCEL_TYPE);
        cancelDialog.setVerifyBtnListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testModEntered = true;
                currentMode = MODE_EXAM;
                showResult();
            }
        });
        cancelDialog.setCancelShowText("取消").setVerifyShowText("确定").initInfo("只有一次练习机会,确定结束吗？",
                VerifyCancelAlertDialog.VERIFY_SELECTED).showDialog();
    }

    /**
     * 调用NB 提交答案
     */
    private void callNbSubmitMethod() {
        if (mJsProvider != null) {
            try {
                JSONObject msg = new JSONObject();
                JSONObject paramsObj = new JSONObject();
                int seconds = tvTime != null ? tvTime.getCurrentTime() : 0;
                paramsObj.put("time_length", seconds);
                paramsObj.put("exam_sn", NbCourseWareConfig.EXAM_SN);
                msg.put("type", NbCourseWareConfig.NOBOOK_SUBMIT);
                msg.put("params", paramsObj);
                mJsProvider.sendMsg(wvSubjectWeb, msg, "*");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showResult() {
        rlCtrContainer.setVisibility(View.GONE);
        tvTime.stop();
        resultLoaded = true;
        jsInserted = true;
        StringBuilder sb = new StringBuilder();
        sb.append(NbCourseWareConfig.LIVE_NB_COURSE_RESULT).append("?liveId=")
                .append(mCourseWareEntity.getLiveId())
                .append("&stuId=").append(LiveAppUserInfo.getInstance().getStuId())
                .append("&experimentId=").append(mCourseWareEntity.getExperimentId())
                .append("&isPlayBack=").append(mCourseWareEntity.isPlayBack() ? "1" : "0")
                .append("&force=").append(isForceSubmit ? "1" : "0");

        if (isForceSubmit || testModEntered) {
            sb.append("&testMode=0");
        } else {
            sb.append("&testMode=1");
        }
        //Log.e("NbH5ExamX5Pager", "=====>showResultPager:" + sb.toString());
        wvSubjectWeb.loadUrl(sb.toString());
    }


    @Override
    public void reloadUrl() {
        showLoadingView();
        rlCtrContainer.setVisibility(View.GONE);
        jsInserted = false;
        resultLoaded = false;
        //没有加载地址
        if (currentMode == MODE_EXAM) {
            if (TextUtils.isEmpty(nbExamUrl)) {
                getNbTestInfo();
                //Log.e("NbH5ExamX5Pager", "=====>reloadUrl 11111");
            } else {
                //Log.e("NbH5ExamX5Pager", "=====>reloadUrl 2222:" + nbExamUrl);
                loadUrl(nbExamUrl);
            }
        } else {
            //Log.e("NbH5ExamX5Pager", "=====>reloadUrl 3333:" + nbTestModeUrl);
            loadUrl(nbTestModeUrl);
        }

    }

    @Override
    public void initData() {
        super.initData();
        // nb 加实验  才需要同课件进行交互
        registJsProvider();
        NbWebViewClient client = new NbWebViewClient();
        wvSubjectWeb.setWebViewClient(client);
        wvSubjectWeb.setWebChromeClient(new NbWebChromClient());
        newCourseCache = new NbCourseCache(mContext, mCourseWareEntity);
        EventBus.getDefault().register(this);

        mView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                mView.removeCallbacks(hideStepResultTask);
                EventBus.getDefault().unregister(NbH5ExamX5Pager.this);
            }
        });
        getNbTestInfo();
        showLoadingView();
    }

    /**
     * 注册js 通信对象
     */
    private void registJsProvider() {
        WebSettings webSetting = wvSubjectWeb.getSettings();
        webSetting.setBuiltInZoomControls(true);
        webSetting.setJavaScriptEnabled(true);
        mJsProvider = new NbWebJsProvider();
        wvSubjectWeb.addJavascriptInterface(mJsProvider, "xesApp");
    }

    private void showLoadError() {
        hideLoadingView();
        if (errorView != null) {
            errorView.setVisibility(View.VISIBLE);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNbCourseEvent(NbCourseEvent event) {

        switch (event.getEventType()) {
            case NbCourseEvent.EVENT_TYPE_ONLOAD:
                hideLoadingView();
                String url1 = currentMode == MODE_EXAM ? nbExamUrl : nbTestModeUrl;
                NbCourseLog.loadNbCourseWare(liveAndBackDebug, mCourseWareEntity.getExperimentId(), "1", "load_success_url=" + url1);
                onSubmit = false;
                if (!nbLoaded) {
                    tvTime.start();
                    nbLoaded = true;
                }
                rlCtrContainer.setVisibility(View.VISIBLE);
                long timeSpend = System.currentTimeMillis() - loadingStartTime;
                NbCourseLog.sno4(liveAndBackDebug, mCourseWareEntity.getExperimentId(), wvSubjectWeb.getUrl(), isPreLaod(), timeSpend + "", mCourseWareEntity.isPlayBack(),
                        "1", isFresh);
                break;
            case NbCourseEvent.EVENT_TYPE_LOAD_ERROR:
                showLoadError();

                String url = currentMode == MODE_EXAM ? nbExamUrl : nbTestModeUrl;
                NbCourseLog.loadNbCourseWare(liveAndBackDebug, mCourseWareEntity.getExperimentId(), "0", "load_failed_url=" + url + "_errorMsg=" + event.getResponseStr());

                wvSubjectWeb.setVisibility(View.INVISIBLE);
                long timeSpend2 = System.currentTimeMillis() - loadingStartTime;
                NbCourseLog.sno4(liveAndBackDebug, mCourseWareEntity.getExperimentId(), wvSubjectWeb.getUrl(), isPreLaod(), timeSpend2 + "", mCourseWareEntity.isPlayBack(),
                        "0", isFresh);

                break;
            case NbCourseEvent.EVENT_TYPE_SUBMIT_FAIL:
                onSubmit = false;
                XESToastUtils.showToast(mContext, !TextUtils.isEmpty(event.getResponseStr()) ? event.getResponseStr()
                        : "实验提交失败");

                NbCourseLog.submitNbCourseWare(liveAndBackDebug, mCourseWareEntity.getExperimentId(), "0", !TextUtils.isEmpty(event.getResponseStr()) ? event.getResponseStr()
                        : "NB方提交失败");
                break;
            case NbCourseEvent.EVENT_TYPE_SUBMIT_SUCCESS:
                NbCourseLog.submitNbCourseWare(liveAndBackDebug, mCourseWareEntity.getExperimentId(), "1", "Nb方提交成功");
                upLoadSubmitResult(event.getResponseStr());
                break;
            case NbCourseEvent.EVENT_TYPE_STEP_WRONG:
                correctNum = 0;
                break;
            case NbCourseEvent.EVENT_TYPE_STEP_CORRECT:
                correctNum++;
                showStepResult(correctNum, event.getResponseStr());
                break;
            case NbCourseEvent.EVENT_TYPE_TOGGLEPACKUP:
                hideResult();
                break;
            case NbCourseEvent.EVENT_TYPE_INTOTESTMODE:
                currentMode = MODE_TEST;
                intoTestMode();
                break;
            case NbCourseEvent.EVENT_TYPE_RESULTPAGE_ONLOAD:
                try {
                    String data = event.getResponseStr();
                    JSONObject jsonObject = new JSONObject(data);
                    String highrightcount = jsonObject.optString("rightCount", "0");
                    String goldcount = jsonObject.optString("highGold", "0");
                    NbCourseLog.sno7(liveAndBackDebug, mCourseWareEntity.getExperimentId(), mCourseWareEntity.isPlayBack(), "1", highrightcount, goldcount);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 隐藏实验结果
     */
    private void hideResult() {
        //回放直接关闭页面返回视频流
        if (mCourseWareEntity.isPlayBack()) {
            mPresenter.closePager();
        } else {
            if (resultLoaded) {
                //强制提交 直接关闭页面
                if (isForceSubmit && mPresenter != null) {
                    mPresenter.closePager();
                } else {
                    webViewContainer.setVisibility(View.INVISIBLE);
                    btnReport.setVisibility(View.VISIBLE);
                }
            }
        }

    }

    /**
     * 进入练习模式
     */
    private void intoTestMode() {
        showLoadingView();
        resultLoaded = false;
        jsInserted = false;
        nbLoaded = false;
        wvSubjectWeb.loadUrl(nbTestModeUrl);
        //Log.e("NbH5ExamX5Pager","====>intoTestMode:"+nbTestModeUrl);
        btnSubmit.setText("结束练习");
        rlCtrContainer.setVisibility(View.VISIBLE);
    }

    @Override
    protected void showLoadingView() {
        super.showLoadingView();
        loadingStartTime = System.currentTimeMillis();
    }


    private Runnable hideStepResultTask = new Runnable() {
        @Override
        public void run() {
            hideStepResult();
        }
    };

    /**
     * 小时连对UI
     *
     * @param rightNum 当前连对次数
     * @param stepDesc 小步 描述
     */
    private void showStepResult(int rightNum, String stepDesc) {
        if (rightNum >= 2 && stepResultContanier.getVisibility() != View.VISIBLE) {
            stepResultContanier.setVisibility(View.VISIBLE);
            final LottieAnimationView animationView = stepResultContanier.findViewById(R.id
                    .lav_livevideo_nb_step_result);
            TextView tvStepDesc = stepResultContanier.findViewById(R.id.tv_livevideo_nb_step_desc);
            tvStepDesc.setText(stepDesc);
            TextView tvRightNum = stepResultContanier.findViewById(R.id.tv_livevideo_nb_step_result);
            StringBuilder sb = new StringBuilder();
            sb.append("连对 ").append("X").append(rightNum);
            tvRightNum.setText(sb.toString());
            String lottieResPath = "nb_courseware/images";
            String lottieJsonPath = "nb_courseware/data.json";
            final LottieEffectInfo effectInfo = new LottieEffectInfo(lottieResPath, lottieJsonPath);
            animationView.setAnimationFromJson(effectInfo.getJsonStrFromAssets(mContext), null);
            animationView.setImageAssetDelegate(new ImageAssetDelegate() {
                @Override
                public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                    return effectInfo.fetchBitmapFromAssets(animationView, lottieImageAsset.getFileName(),
                            lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(),
                            mContext);
                }
            });
            animationView.playAnimation();
            mView.postDelayed(hideStepResultTask, 3000);
        } else {
            //当前正在显示 每小步结果,更新当前 展示内容
            mView.removeCallbacks(hideStepResultTask);
            TextView tvStepDesc = stepResultContanier.findViewById(R.id.tv_livevideo_nb_step_desc);
            tvStepDesc.setText(stepDesc);
            TextView tvRightNum = stepResultContanier.findViewById(R.id.tv_livevideo_nb_step_result);
            StringBuilder sb = new StringBuilder();
            sb.append("连对 ").append("X").append(rightNum);
            tvRightNum.setText(sb.toString());
            mView.postDelayed(hideStepResultTask, 3000);
        }
    }

    /**
     * 隐藏每小步结果
     */
    private void hideStepResult() {
        stepResultContanier.setVisibility(View.GONE);
    }


    /**
     * 提交答案开始时间
     */
    long upLoadStartTime;

    /**
     * 上传NB 返回的提交结果到 服务器
     *
     * @param responseStr
     */
    private void upLoadSubmitResult(final String responseStr) {
        if (mPresenter != null) {
            int seconds = tvTime != null ? tvTime.getCurrentTime() : 0;
            upLoadStartTime = System.currentTimeMillis();
            NbCourseLog.sno5(liveAndBackDebug, mCourseWareEntity.getExperimentId(), isForceSubmit, seconds + "", mCourseWareEntity.isPlayBack());
            mPresenter.uploadNbResult(responseStr, isForceSubmit ? "1" : "0", new
                    HttpCallBack(false) {
                        @Override
                        public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                            long spendTiem = System.currentTimeMillis() - upLoadStartTime;
                            NbCourseLog.sno6(liveAndBackDebug, mCourseWareEntity.getExperimentId(), mCourseWareEntity.isPlayBack(), "1", spendTiem + "");
                            // 向主讲发送消息 学生提交成功了
                            if (mCourseWareEntity != null && !mCourseWareEntity.isPlayBack()) {
                                mPresenter.sendSubmitSuccessMsg(LiveAppUserInfo.getInstance().getStuId(), mCourseWareEntity.getExperimentId());
                            }
                            onSubmit = false;

                            NbCourseLog.submitNbCourseWare(liveAndBackDebug, mCourseWareEntity.getExperimentId(), "1", "网校提交成功");
                            showResult();
                        }

                        @Override
                        public void onPmFailure(Throwable error, String msg) {
                            onSubmit = false;
                            XESToastUtils.showToast(mContext, TextUtils.isEmpty(msg) ? "实验提交失败" : msg);
                            long spendTiem = System.currentTimeMillis() - upLoadStartTime;
                            NbCourseLog.sno6(liveAndBackDebug, mCourseWareEntity.getExperimentId(), mCourseWareEntity.isPlayBack(), "0", spendTiem + "");
                            NbCourseLog.submitNbCourseWare(liveAndBackDebug, mCourseWareEntity.getExperimentId(), "0", "网校提交失败");
                        }

                        @Override
                        public void onPmError(ResponseEntity responseEntity) {
                            onSubmit = false;
                            XESToastUtils.showToast(mContext, TextUtils.isEmpty(responseEntity.getErrorMsg()) ? "实验提交失败"
                                    : responseEntity.getErrorMsg());
                            long spendTiem = System.currentTimeMillis() - upLoadStartTime;
                            NbCourseLog.sno6(liveAndBackDebug, mCourseWareEntity.getExperimentId(), mCourseWareEntity.isPlayBack(), "0", spendTiem + "");
                            NbCourseLog.submitNbCourseWare(liveAndBackDebug, mCourseWareEntity.getExperimentId(), "0", "网校提交失败");

                        }

                        @Override
                        public void onFailure(Call call, IOException e) {
                            onSubmit = false;
                            super.onFailure(call, e);
                            XESToastUtils.showToast(mContext, "实验提交失败");
                            long spendTiem = System.currentTimeMillis() - upLoadStartTime;
                            NbCourseLog.sno6(liveAndBackDebug, mCourseWareEntity.getExperimentId(), mCourseWareEntity.isPlayBack(), "0", spendTiem + "");
                            NbCourseLog.submitNbCourseWare(liveAndBackDebug, mCourseWareEntity.getExperimentId(), "0", "网校提交失败");
                        }

                        @Override
                        public void onFailure(String postUrl, Exception e, String msg) {
                            onSubmit = false;
                            super.onFailure(postUrl, e, msg);
                            XESToastUtils.showToast(mContext, "实验提交失败");
                            long spendTiem = System.currentTimeMillis() - upLoadStartTime;
                            NbCourseLog.sno6(liveAndBackDebug, mCourseWareEntity.getExperimentId(), mCourseWareEntity.isPlayBack(), "0", spendTiem + "");
                            NbCourseLog.submitNbCourseWare(liveAndBackDebug, mCourseWareEntity.getExperimentId(), "0", "网校提交失败");
                        }
                    });
        }
    }


    @Override
    public void submitData() {
        //避免多少调用
        if (!isForceSubmit) {
            isForceSubmit = true;
            if (currentMode == MODE_EXAM) {
                //已加载过结果页
                if (resultLoaded) {
                    // 结果页面被隐藏（学生点击收起） 直接关闭页面
                    if (isResultPagerHide() && mPresenter != null) {
                        mPresenter.closePager();
                    } else {
                        // 结果页面可见:通知前端 老师收题了
                        mJsProvider.onTeachTakeUp(wvSubjectWeb);
                    }
                } else {
                    //未加载过结果页面
                    callNbSubmitMethod();
                }
            } else {
                // 练习模式 返回结果页面
                /* if(mPresenter != null){
                     mPresenter.closePager();
                 }*/
                testModEntered = true;
                currentMode = MODE_EXAM;
                showResult();
            }
        }
    }

    @Override
    public boolean onBack() {
        //Log.e("nbTrace","pager onBack:"+currentMode +":"+this);
        if (currentMode == MODE_TEST) {
            hideLoadingView();
            endTestMode();
            return true;
        } else {
            return false;
        }

    }

    @Override
    public void loadUrl() {

    }

    /**
     * 当前结果页是否已加载成功，只是不可见
     *
     * @return
     */
    private boolean isResultPagerHide() {
        return btnReport.getVisibility() == View.VISIBLE;
    }

    /** 获取需要加载的试题信息 */
    private void getNbTestInfo() {

        mPresenter.getNBTestInfo(mCourseWareEntity, new HttpCallBack() {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                NbCourseWareEntity testInfo = NbHttpResponseParser.parseNbTestInfo(responseEntity);
                LoadNbCourseWare(testInfo);
                NbCourseLog.getNbTestInfo(liveAndBackDebug, mCourseWareEntity.getExperimentId(), "1", "");

            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                super.onPmError(responseEntity);
                showLoadError();
                NbCourseLog.getNbTestInfo(liveAndBackDebug, mCourseWareEntity.getExperimentId(), "0", "获取Nb试题信息失败");
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
                showLoadError();
                NbCourseLog.getNbTestInfo(liveAndBackDebug, mCourseWareEntity.getExperimentId(), "0", "获取Nb试题信息失败");
            }
        });
    }

    /**
     * 加载Nb 课件
     *
     * @param testInfo
     */
    private void LoadNbCourseWare(NbCourseWareEntity testInfo) {
        mCourseWareEntity.setAnswer(testInfo.isAnswer());
        mCourseWareEntity.setExperimentName(testInfo.getExperimentName());

        StringBuilder sb = new StringBuilder();
        sb.append(testInfo.getUrl()).append("&token=").append(mCourseWareEntity.getNbToken()).append("&isexam=1");
        nbExamUrl = sb.toString();
        //Log.e("NbH5ExamX5Pager", "=====>LoadNbCourseWare url nbExamUrl:" + nbExamUrl);
        sb.setLength(0);
        sb.append(testInfo.getUrl()).append("&token=").append(mCourseWareEntity.getNbToken()).append("&isexam=0");
        nbTestModeUrl = sb.toString();
        //Log.e("NbH5ExamX5Pager", "=====>LoadNbCourseWare nbTestModeUrl:" + nbTestModeUrl+":"+mCourseWareEntity.getExperimentType());
        btnSubmit.setText(currentMode == MODE_EXAM ? "提交" : "结束练习");
        tvTitle.setText(mCourseWareEntity.getExperimentName());
        //Log.e("NbH5ExamX5Pager","======>LoadNbCourseWare isAnswered:"+testInfo.isAnswer());
        if (testInfo.isAnswer()) {
            hideLoadingView();
            showResult();
        } else {
            wvSubjectWeb.loadUrl(nbExamUrl);
            NbCourseLog.sno3(liveAndBackDebug, mCourseWareEntity.getExperimentId(), nbExamUrl, mCourseWareEntity.isPlayBack(), isPreLaod());
            NbCourseLog.loadNbCourseWare(liveAndBackDebug, mCourseWareEntity.getExperimentId(), "1", "load_start_url=" + nbExamUrl);
        }
    }

    /**
     * 是否有预加载
     *
     * @return
     */
    private boolean isPreLaod() {
        boolean result = false;
        try {
            String resDir = ShareDataManager.getInstance().getString(NbCourseWareConfig.LOCAL_RES_DIR, "",
                    ShareDataManager.SHAREDATA_NOT_CLEAR);
            File mResDir = LiveCacheFile.geCacheFile(mContext, NbCourseWareConfig.NB_RESOURSE_CACHE_DIR);
            File mNbCacheFileDir = null;
            if (mResDir.exists() && !TextUtils.isEmpty(resDir)) {
                mNbCacheFileDir = new File(mResDir, resDir);
                if (mNbCacheFileDir.exists()) {
                    List<File> subFiles = FileUtils.listFilesInDir(mNbCacheFileDir);
                    if (subFiles != null && subFiles.size() > 0) {
                        result = true;
                    }
                }
            }
            //Log.e("NbH5ExamX5Pager","=====>isPreLaod:"+resDir+":"+result);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 用于拦截 网络请求，动态注入 js
     */
    private class NbWebViewClient extends MyWebViewClient implements OnHttpCode {

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView webView, WebResourceRequest request) {
            String url = request.getUrl() + "";
            //第一个必须加载网页的url,并且在下载下来的js文件中注入一段js代码。
            if (url.contains(NB_COURSE_WARE_URL_KEY_WORD) || url.contains(".html")) {
                logger.e("======>X5Pager_InterceptRequest_new:" + url);
                if (!jsInserted) {
                    jsInserted = true;
                    WebResourceResponse webResourceResponse = newCourseCache.interceptIndexRequest(webView, url);
                    logger.d("shouldInterceptRequest:index:url=" + url + ",response=null?" + (webResourceResponse ==
                            null));
                    if (webResourceResponse != null) {
                        return webResourceResponse;
                    } else {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                wvSubjectWeb.stopLoading();
                            }
                        });
                        XESToastUtils.showToast(mContext, "主文件加载失败，请刷新");
                    }
                }
            }
            if (url.contains(WebInstertJs.indexStr())) {
                logger.e("======>X5Pager_InterceptRequest_new_insertJs called:");
                WebResourceResponse webResourceResponse = newCourseCache.interceptJsRequest(webView, url);
                logger.d("shouldInterceptRequest:js:url=" + url + ",response=null?" + (webResourceResponse == null));
                if (webResourceResponse != null) {
                    return webResourceResponse;
                } else {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            wvSubjectWeb.stopLoading();
                        }
                    });
                    XESToastUtils.showToast(mContext, "通信文件加载失败，请刷新");
                }
            }

            //拦截资源请求，提供本地资源
            //Log.e("NbH5ExamPager","======>shouldInterceptRequest：refreshTimes="+refreshTimes);
            if (refreshTimes < LOCAL_RES_LOAD_TRY_TIMES) {
                WebResourceResponse webResourceResponse = newCourseCache.shouldInterceptRequest(webView, url);
                if (webResourceResponse != null) {
                    //Log.e("nbTrac","======>X5Pager_InterceptRequest_new called:"+"======>返回本地资源："+url);
                    logger.d("shouldInterceptRequest:url=" + url);
                    return webResourceResponse;
                }
            }

            return super.shouldInterceptRequest(webView, request);
        }

        @Override
        public void onHttpCode(String url, int code) {
            onReceivedHttpError(wvSubjectWeb, url, code, "");
        }
    }


    private class NbWebChromClient extends MyWebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {

        }
    }

    @Override
    public void destroy() {
        if(wvSubjectWeb != null){
            wvSubjectWeb.destroy();
        }
        this.onDestroy();
    }
}
