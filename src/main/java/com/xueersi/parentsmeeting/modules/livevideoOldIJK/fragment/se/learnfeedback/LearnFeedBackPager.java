//package com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment.se.learnfeedback;
//
//import android.annotation.TargetApi;
//import android.content.Context;
//import android.os.Build;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.RadioGroup;
//
//import com.xueersi.common.base.BasePager;
//import com.xueersi.common.business.UserBll;
//import com.xueersi.common.http.HttpCallBack;
//import com.xueersi.common.http.ResponseEntity;
//import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
//import com.xueersi.parentsmeeting.modules.livevideo.R;
//import com.xueersi.parentsmeeting.modules.livevideo.fragment.se.IExperiencePresenter;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.IOException;
//
//import okhttp3.Call;
//
//public class LearnFeedBackPager extends BasePager {
//
//    private IExperiencePresenter mPresenter;
//
//    private IExperienceSendHttp mSendHttp;
//
//    private RadioGroup rgDifficulty;
//    private RadioGroup rgSatisficing;
//    private EditText etSuggest;
//    private ImageButton imgbtnClose;
//    private Button btnSubmit;
//    /** 选择1 课程难度评价 */
//    private String mDifficulty = "-1";
//    /** 选择2 课程满意度评价 */
//    private String mSatisficing = "-1";
//
//    private VideoLivePlayBackEntity mVideoEntity;
//
//    public LearnFeedBackPager(Context context, StandExperienceLearnFeedbackBll presenter, VideoLivePlayBackEntity
//            videoLivePlayBackEntity) {
//        super(context);
//        this.mPresenter = presenter;
//        this.mSendHttp = presenter;
//        this.mVideoEntity = videoLivePlayBackEntity;
//        initListener();
//    }
//
//    @Override
//    public View initView() {
//        mView = View.inflate(mContext, R.layout.pop_experience_livevideo_feedback, null);
//        rgDifficulty = mView.findViewById(R.id.rg_experience_feedback_difficulty);
//        rgSatisficing = mView.findViewById(R.id.rg_experience_feedback_satisficing);
//        etSuggest = mView.findViewById(R.id.et_experience_feedback_suggest);
//        imgbtnClose = mView.findViewById(R.id.imgbtn_experience_feedback_close);
//        btnSubmit = mView.findViewById(R.id.btn_experience_feedback_submit);
//        btnSubmit.setEnabled(false);
//        return mView;
//    }
//
//    @Override
//    public void initListener() {
//        super.initListener();
//        rgDifficulty.clearCheck();
//        rgSatisficing.clearCheck();
//        rgDifficulty.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                if (checkedId == R.id.rbtn_difficulty_1) {
//                    mDifficulty = "1";
//                } else if (checkedId == R.id.rbtn_difficulty_2) {
//                    group.check(R.id.rbtn_difficulty_2);
//                    mDifficulty = "2";
//                } else if (checkedId == R.id.rbtn_difficulty_3) {
//                    group.check(R.id.rbtn_difficulty_3);
//                    mDifficulty = "3";
//                } else {
//                    mDifficulty = "-1";
//                }
//                if (!"-1".equals(mDifficulty) && !"-1".equals(mSatisficing)) {
//                    btnSubmit.setBackground(mContext.getResources().getDrawable(R.color.COLOR_F13232));
//                    btnSubmit.setEnabled(true);
//                }
//            }
//        });
//        rgSatisficing.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                if (checkedId == R.id.rbtn_satisficing_1) {
//                    mSatisficing = "1";
//                } else if (checkedId == R.id.rbtn_satisficing_2) {
//                    mSatisficing = "2";
//                } else if (checkedId == R.id.rbtn_satisficing_3) {
//                    mSatisficing = "3";
//                } else {
//                    mSatisficing = "-1";
//                }
//                if (!"-1".equals(mDifficulty) && !"-1".equals(mSatisficing)) {
//                    btnSubmit.setBackground(mContext.getResources().getDrawable(R.color.COLOR_F13232));
//                    btnSubmit.setEnabled(true);
//                }
//            }
//        });
//        imgbtnClose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mPresenter.removeWindow();
//            }
//        });
//        btnSubmit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                JSONArray jsonArray = new JSONArray();
//                try {
//
//                    JSONObject jsonOption = new JSONObject();
//                    jsonOption.put("1", mDifficulty);
//                    jsonArray.put(jsonOption);
//                    JSONObject jsonObject2 = new JSONObject();
//                    jsonObject2.put("2", mSatisficing);
//                    jsonArray.put(jsonObject2);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                HttpCallBack httpCallBack = new HttpCallBack() {
//
//                    @Override
//                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
//                        mPresenter.removeWindow();
//                    }
//
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//                        super.onFailure(call, e);
//                    }
//
//
//                    @Override
//                    public void onPmFailure(Throwable error, String msg) {
//                        logger.d("sendFeedbackFailure:" + msg);
//                        super.onPmFailure(error, msg);
//                    }
//
//                    @Override
//                    public void onPmError(ResponseEntity responseEntity) {
//                        logger.d("sendFeedbackError:" + responseEntity.toString());
//                        super.onPmError(responseEntity);
//                    }
//                };
//                if (mSendHttp != null) {
//                    mSendHttp.sendHttp(
//                            UserBll.getInstance().getMyUserInfoEntity().getStuId(),
//                            mVideoEntity.getLiveId(),
//                            mVideoEntity.getSubjectId(),
//                            mVideoEntity.getGradId(),
//                            mVideoEntity.getChapterId(),
//                            etSuggest.getText().toString(),
//                            jsonArray,
//                            httpCallBack);
//                }
//                mPresenter.removeWindow();
////                setBackgroundAlpha(1f);
//            }
//        });
//    }
//
////    private void setBackgroundAlpha(float bgAlpha) {
////        WindowManager.LayoutParams lp = mWindow
////                .getAttributes();
////        lp.alpha = bgAlpha;
////        mWindow.setAttributes(lp);
////    }
//
//    @Override
//    public void initData() {
//
//    }
//
//}
