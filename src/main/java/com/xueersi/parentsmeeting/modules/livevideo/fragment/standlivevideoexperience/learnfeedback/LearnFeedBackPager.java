package com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience.learnfeedback;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;

import com.xueersi.common.base.BasePager;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.LiveExperienceEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class LearnFeedBackPager<T extends LearnFeedBackContract.ISendHttp> extends BasePager {

    private T mSendHttp;

    private RadioGroup rgDifficulty;
    private RadioGroup rgSatisficing;
    private EditText etSuggest;
    private ImageButton imgbtnClose;
    private Button btnSubmit;
    /** 选择1 课程难度评价 */
    private String mDifficulty = "-1";
    /** 选择2 课程满意度评价 */
    private String mSatisficing = "-1";

    private VideoLivePlayBackEntity mVideoEntity;

    private List<LiveExperienceEntity.LearnFeedBack> arrayOptions;

    public LearnFeedBackPager(Context context, T presenter, VideoLivePlayBackEntity
            videoLivePlayBackEntity) {
        super(context);
        this.mSendHttp = presenter;
        this.mVideoEntity = videoLivePlayBackEntity;
        arrayOptions = videoLivePlayBackEntity.getLearnFeedback();
        initListener();
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
        return mView;
    }

    @Override
    public void initListener() {
        super.initListener();
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
                mSendHttp.removeWindow();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonOption = new JSONObject();
                try {
                    jsonOption.put("1", mDifficulty);
                    jsonOption.put("2", mSatisficing);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                HttpCallBack httpCallBack = new HttpCallBack() {

                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        mSendHttp.removeWindow();
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        super.onFailure(call, e);
                    }


                    @Override
                    public void onPmFailure(Throwable error, String msg) {
                        logger.d("sendFeedbackFailure:" + msg);
                        super.onPmFailure(error, msg);
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        logger.d("sendFeedbackError:" + responseEntity.toString());
                        super.onPmError(responseEntity);
                    }
                };
                if (mSendHttp != null) {
                    mSendHttp.sendHttp(
                            UserBll.getInstance().getMyUserInfoEntity().getStuId(),
                            mVideoEntity.getLiveId(),
                            mVideoEntity.getSubjectId(),
                            mVideoEntity.getGradId(),
                            mVideoEntity.getChapterId(),
                            etSuggest.getText().toString(),
                            jsonOption,
                            httpCallBack);
                }
                mSendHttp.removeWindow();
//                setBackgroundAlpha(1f);
            }
        });
    }

    //    private void setBackgroundAlpha(float bgAlpha) {
//        WindowManager.LayoutParams lp = mWindow
//                .getAttributes();
//        lp.alpha = bgAlpha;
//        mWindow.setAttributes(lp);
//    }
// FIXME: 2018/10/16 ui给了之后，再来选择显示什么样子
    @Override
    public void initData() {
        for (LiveExperienceEntity.LearnFeedBack item : arrayOptions) {
            item.getTitle();
            Map<String, String> map = item.getOptions();
            int length = map.size();

            Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entryKey = iterator.next();
                String key = entryKey.getKey();
                String value = entryKey.getValue();
            }

        }
    }

}
