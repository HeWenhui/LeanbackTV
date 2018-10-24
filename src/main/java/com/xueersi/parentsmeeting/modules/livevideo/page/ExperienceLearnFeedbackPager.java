package com.xueersi.parentsmeeting.modules.livevideo.page;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;

import com.xueersi.common.base.BasePager;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.log.Loger;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LectureLivePlayBackBll;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.zip.Inflater;

import okhttp3.Call;

/**
 * Created by：WangDe on 2018/8/23 19:40
 */
public class ExperienceLearnFeedbackPager extends BasePager {

    VideoLivePlayBackEntity mVideoEntity;
    Window mWindow;
    LectureLivePlayBackBll lectureLivePlayBackBll;
    private RadioGroup rgDifficulty;
    private RadioGroup rgSatisficing;
    private EditText etSuggest;
    private ImageButton imgbtnClose;
    private Button btnSubmit;
    /** 选择1 课程难度评价 */
    private String mDifficulty = "-1";
    /** 选择2 课程满意度评价 */
    private String mSatisficing = "-1";
    private CloseAction closeAction;

    public ExperienceLearnFeedbackPager(Context context, VideoLivePlayBackEntity videoEntity, Window window) {
        super(context);
        mVideoEntity = videoEntity;
        mWindow = window;
    }

    public ExperienceLearnFeedbackPager(Context context, VideoLivePlayBackEntity videoEntity, Window window,
                                        LectureLivePlayBackBll lectureLivePlayBackBll) {
        super(context);
        mVideoEntity = videoEntity;
        mWindow = window;
        this.lectureLivePlayBackBll = lectureLivePlayBackBll;
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.pop_experience_livevideo_feedback, null);
        rgDifficulty = mView.findViewById(R.id.rg_experience_feedback_difficulty);
        rgSatisficing = mView.findViewById(R.id.rg_experience_feedback_satisficing);
        etSuggest = mView.findViewById(R.id.et_experience_feedback_suggest);
        imgbtnClose = mView.findViewById(R.id.imgbtn_experience_feedback_close);
        btnSubmit = mView.findViewById(R.id.btn_experience_feedback_submit);
        btnSubmit.setEnabled(false);
        registerListener();
        return mView;
    }

    @Override
    public void initData() {
    }

    public void setCloseAction(CloseAction closeAction) {
        this.closeAction = closeAction;
    }

    private void registerListener() {
        rgDifficulty.clearCheck();
        rgSatisficing.clearCheck();
        rgDifficulty.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbtn_difficulty_1) {
                    mDifficulty = "1";
                } else if (checkedId == R.id.rbtn_difficulty_2) {
                    group.check(R.id.rbtn_difficulty_2);
                    mDifficulty = "2";
                } else if (checkedId == R.id.rbtn_difficulty_3) {
                    group.check(R.id.rbtn_difficulty_3);
                    mDifficulty = "3";
                } else {
                    mDifficulty = "-1";
                }
                if (!"-1".equals(mDifficulty) && !"-1".equals(mSatisficing)) {
                    btnSubmit.setBackground(mContext.getResources().getDrawable(R.color.COLOR_F13232));
                    btnSubmit.setEnabled(true);
                }
            }
        });
        rgSatisficing.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbtn_satisficing_1) {
                    mSatisficing = "1";
                } else if (checkedId == R.id.rbtn_satisficing_2) {
                    mSatisficing = "2";
                } else if (checkedId == R.id.rbtn_satisficing_3) {
                    mSatisficing = "3";
                } else {
                    mSatisficing = "-1";
                }
                if (!"-1".equals(mDifficulty) && !"-1".equals(mSatisficing)) {
                    btnSubmit.setBackground(mContext.getResources().getDrawable(R.color.COLOR_F13232));
                    btnSubmit.setEnabled(true);
                }
            }
        });
        imgbtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (closeAction != null) {
                    closeAction.onClose();
                }
                setBackgroundAlpha(1f);
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonOption = new JSONObject();
                JSONArray jsonArray = new JSONArray();
                try {

                    jsonOption.put("1", mDifficulty);
                    jsonOption.put("2", mSatisficing);
                    jsonArray.put(jsonOption);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (lectureLivePlayBackBll != null) {
                    lectureLivePlayBackBll.sendExperienceFeedback(UserBll.getInstance().getMyUserInfoEntity()
                                    .getStuId(), mVideoEntity.getLiveId()
                            , mVideoEntity.getSubjectId(), mVideoEntity.getGradId(), mVideoEntity.getChapterId(),
                            etSuggest.getText().toString(), jsonArray, new
                                    HttpCallBack() {

                                        @Override
                                        public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                                            logger.d( "sendFeedbackSuccess");
                                        }
                                    });
                } else {
                    if (learnFeedBackPagerListener != null) {
                        learnFeedBackPagerListener.submitClick(
                                UserBll.getInstance().getMyUserInfoEntity().getStuId(),
                                mVideoEntity.getLiveId(),
                                mVideoEntity.getSubjectId(),
                                mVideoEntity.getGradId(),
                                mVideoEntity.getChapterId(),
                                etSuggest.getText().toString(),
                                jsonOption,
                                new HttpCallBack() {

                                    @Override
                                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                                        if (closeAction != null) {
                                            closeAction.onClose();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        super.onFailure(call, e);
                                    }


                                    @Override
                                    public void onPmFailure(Throwable error, String msg) {
                                        logger.d( "sendFeedbackFailure:" + msg);
                                        super.onPmFailure(error, msg);
                                    }

                                    @Override
                                    public void onPmError(ResponseEntity responseEntity) {
                                        logger.d( "sendFeedbackError:" + responseEntity.toString());
                                        super.onPmError(responseEntity);
                                    }
                                });
                    }
                }
                if (closeAction != null) {
                    closeAction.onClose();
                }
                setBackgroundAlpha(1f);

            }
        });
    }

    private void setBackgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = mWindow
                .getAttributes();
        lp.alpha = bgAlpha;
        mWindow.setAttributes(lp);
    }

    //关闭该pager接口
    public interface CloseAction {
        void onClose();

    }

    /**
     * 目前全身直播体验课专用,提交一系列
     */
    public interface LearnFeedBackPagerListener extends CloseAction {
        void submitClick(String useId, String liveId, String subjectId, String gradId, String chapterId, String
                suggest, JSONObject jsonObject, HttpCallBack httpCallBack);
    }

    private LearnFeedBackPagerListener learnFeedBackPagerListener;

    public void setLearnFeedBackPagerListener(LearnFeedBackPagerListener learnFeedBackPagerListener) {
        this.learnFeedBackPagerListener = learnFeedBackPagerListener;
        this.closeAction = learnFeedBackPagerListener;
    }

}
