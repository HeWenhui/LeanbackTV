package com.xueersi.parentsmeeting.modules.livevideoOldIJK.page;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
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
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LectureLivePlayBackBll;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
                    closeAction.onClose("2");
                }
                setBackgroundAlpha(1f);
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONArray jsonArray = new JSONArray();
                try {
                    JSONObject jsonOption = new JSONObject();
                    jsonOption.put("1", mDifficulty);
                    jsonArray.put(jsonOption);
                    JSONObject jsonObject2 = new JSONObject();
                    jsonObject2.put("2", mSatisficing);
                    jsonArray.put(jsonObject2);
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
                                            logger.d("sendFeedbackSuccess");
                                        }
                                    });
                }
                if (closeAction != null) {
                    closeAction.onClose("1");
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
        void onClose(String type);
    }


}
