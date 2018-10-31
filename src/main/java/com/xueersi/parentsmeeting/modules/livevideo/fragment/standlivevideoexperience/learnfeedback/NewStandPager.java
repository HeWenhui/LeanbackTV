package com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience.learnfeedback;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.xueersi.common.base.BasePager;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.LiveExperienceEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;

public class NewStandPager<T extends LearnFeedBackContract.ISendHttp> extends BasePager {

    T presenter;

    ScrollView scrollView;

    ItemLearnFeedbackPageSelectLayout itemLearnFeedbackPageSelectLayout1, itemLearnFeedbackPageSelectLayout2;

    private GestureScrollEditText gestureScrollEditText;
    /**
     * 反馈弹窗中需要显示的信息
     */
    private List<LiveExperienceEntity.LearnFeedBack> arrayOptions;

    private VideoLivePlayBackEntity mVideoEntity;

    private GestureScrollEditText etSuggest;

    private ImageView ivSubmit;

    private ImageView ivClose;

    public NewStandPager(Context context, T presenter, VideoLivePlayBackEntity
            videoLivePlayBackEntity) {
        super(context);
        this.presenter = presenter;
        this.arrayOptions = videoLivePlayBackEntity.getLearnFeedback();
        priHandle();
        initData();
        initListener();
    }

    @Override
    public View initView() {
        mView = LayoutInflater.from(mContext).inflate(R.layout.page_new_stand_experience_learn_feedback, null);
        scrollView = mView.findViewById(R.id.sv_stand_experience_learn_feedback_select);
        itemLearnFeedbackPageSelectLayout1 = mView.findViewById(R.id.item_select_layout1);
        itemLearnFeedbackPageSelectLayout2 = mView.findViewById(R.id.item_select_layout2);
        gestureScrollEditText = mView.findViewById(R.id.et_stand_experience_learn_feedback_suggest);
        etSuggest = mView.findViewById(R.id.et_stand_experience_learn_feedback_suggest);
        ivSubmit = mView.findViewById(R.id.btn_stand_experience_learn_feedback_submit);
        ivClose = mView.findViewById(R.id.iv_stand_experience_learn_feedback_close);
        return mView;
    }

    /**
     * 预处理
     */
    private void priHandle() {
        //对ScrollView预处理，防止因为EditText获取焦点自动滑动
        scrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        scrollView.setFocusable(true);
        scrollView.setFocusableInTouchMode(true);
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.requestFocusFromTouch();
                return false;
            }
        });
    }

    @Override
    public void initData() {
        if (arrayOptions.size() == 1) {
            itemLearnFeedbackPageSelectLayout1.updateView(arrayOptions.get(0));
        }
        if (arrayOptions.size() > 1) {
            itemLearnFeedbackPageSelectLayout2.updateView(arrayOptions.get(1));
        } else {
            itemLearnFeedbackPageSelectLayout2.setVisibility(View.GONE);
        }
    }

    @Override
    public void initListener() {
        super.initListener();
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (presenter != null) {
                    presenter.removeWindow();
                }
            }
        });
        ivSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONArray jsonArray = new JSONArray();
                JSONObject jsonObject1 = new JSONObject();
                JSONObject jsonObject2 = new JSONObject();
                try {
                    jsonObject1.put("1", itemLearnFeedbackPageSelectLayout1.getAns());
                    jsonObject2.put("2", itemLearnFeedbackPageSelectLayout2.getAns());
                    jsonArray.put(jsonObject1);
                    jsonArray.put(jsonObject2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                HttpCallBack httpCallBack = new HttpCallBack() {

                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        presenter.removeWindow();
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

                presenter.sendHttp(
                        UserBll.getInstance().getMyUserInfoEntity().getStuId(),
                        mVideoEntity.getLiveId(),
                        mVideoEntity.getSubjectId(),
                        mVideoEntity.getGradId(),
                        mVideoEntity.getChapterId(),
                        etSuggest.getText().toString(),
                        jsonArray,
                        httpCallBack
                );
            }
        });
    }
}
