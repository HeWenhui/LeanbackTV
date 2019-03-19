package com.xueersi.parentsmeeting.modules.livevideo.groupgame.pager;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.tal.speech.config.SpeechConfig;
import com.tal.speech.speechrecognizer.EvaluatorListener;
import com.tal.speech.speechrecognizer.ResultEntity;
import com.tal.speech.speechrecognizer.SpeechParamEntity;
import com.tal.speech.utils.SpeechUtils;
import com.xueersi.common.base.BasePager;
import com.xueersi.common.entity.EnglishH5Entity;
import com.xueersi.lib.framework.utils.file.FileUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5CoursewareBll;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5CoursewareSecHttp;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseCoursewareNativePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseEnglishH5CoursewarePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.web.NewCourseCache;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @Date on 2019/3/15 18:31
 * @Author zhangyuansun
 * @Description
 */
public class GroupGameNativePager extends BaseCoursewareNativePager implements BaseEnglishH5CoursewarePager {

    /**
     * 加载中
     */
    private RelativeLayout rlSubjectLoading;
    /**
     * 右侧控制区
     */
    private RelativeLayout rlGroupGameControl;
    /**
     * 课件接口失败刷新
     */
    private ImageView ivCourseRefresh;
    /**
     * 课件网页刷新
     */
    private ImageView ivWebViewRefresh;

    /**
     * 新课件缓存
     */
    private NewCourseCache newCourseCache;
    /**
     * 新课件是否是预加载
     */
    private boolean isPreload;
    private String learningStage;

    /**
     * 语音评测
     */
    protected SpeechUtils mIse;
    private SpeechParamEntity mParam;
    /**
     * 语音保存位置
     */
    private File saveVideoFile;

    public GroupGameNativePager(Context context, String learningStage) {
        super(context);
        this.learningStage = learningStage;
        initData();
        initListener();
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_h5_courseware_groupgame, null);
        wvSubjectWeb = view.findViewById(R.id.wv_livevideo_subject_web);
        ivCourseRefresh = view.findViewById(R.id.iv_livevideo_course_refresh);
        ivWebViewRefresh = view.findViewById(R.id.iv_livevideo_subject_refresh);
        rlSubjectLoading = view.findViewById(R.id.rl_livevideo_subject_loading);
        rlGroupGameControl = view.findViewById(R.id.rl_livevideo_groupgame_control);
        return view;
    }

    @Override
    public void initData() {


        startSpeechRecognize();
    }

    private void startSpeechRecognize() {
        File dir = LiveCacheFile.geCacheFile(mContext, "liveSpeech");
        FileUtils.deleteDir(dir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        saveVideoFile = new File(dir, "ise" + System.currentTimeMillis() + ".mp3");
        mParam = new SpeechParamEntity();
        //语音评测开始
        if (mIse == null) {
            mIse = SpeechUtils.getInstance(mContext.getApplicationContext());
            mIse.prepar();
        }
        mParam.setRecogType(SpeechConfig.SPEECH_ENGLISH_EVALUATOR_OFFLINE);
        mParam.setStrEvaluator("Are you ok?|Hello world|Thank you");
        mParam.setLocalSavePath(saveVideoFile.getPath());
        mParam.setMultRef(false);
        mParam.setLearning_stage(learningStage);
        mIse.startRecog(mParam, new EvaluatorListener() {
            int lastVolume = 0;

            @Override
            public void onBeginOfSpeech() {
                logger.d("onBeginOfSpeech");
            }

            @Override
            public void onResult(ResultEntity resultEntity) {

                if (resultEntity.getStatus() == ResultEntity.SUCCESS) {
                    logger.d("onEvaluatorSuccess:score=" + resultEntity.getScore());
                } else if (resultEntity.getStatus() == ResultEntity.ERROR) {
                    logger.d("onEvaluatorError:ErrorNo=" + resultEntity.getErrorNo() + ",isOfflineFail=" + mIse
                            .isOfflineFail());
                } else if (resultEntity.getStatus() == ResultEntity.EVALUATOR_ING) {
                    logger.d("onEvaluatorIng:result="+resultEntity.toString());
                }
            }

            @Override
            public void onVolumeUpdate(int volume) {
                logger.d("onVolumeUpdate:volume=" + volume);
                lastVolume = volume;
            }

        });
    }

    @Override
    public void initListener() {

    }

    /**
     * 实现 h5课件基础接口
     *
     */
    @Override
    public boolean isFinish() {
        return false;
    }

    @Override
    public void close() {

    }

    @Override
    public String getUrl() {
        return null;
    }

    @Override
    public void onBack() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void submitData() {

    }

    @Override
    public void setEnglishH5CoursewareBll(EnglishH5CoursewareBll englishH5CoursewareBll) {

    }

    @Override
    public void setEnglishH5CoursewareSecHttp(EnglishH5CoursewareSecHttp englishH5CoursewareSecHttp) {

    }

    @Override
    public BasePager getBasePager() {
        return null;
    }

    @Override
    public void setWebBackgroundColor(int color) {

    }

    @Override
    public EnglishH5Entity getEnglishH5Entity() {
        return null;
    }

}
