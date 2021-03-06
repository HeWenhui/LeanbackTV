package com.xueersi.parentsmeeting.modules.livevideo.page;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.evaluateteacher.bussiness.FeedBackTeacherInterface;

import com.xueersi.common.base.BaseApplication;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.EvaluateContent;
import com.xueersi.parentsmeeting.modules.livevideo.entity.FeedBackEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.page.item.LiveTeacherFeedbackItem;
import com.xueersi.parentsmeeting.modules.livevideo.page.item.RecyclerViewSpacesItemDecoration;
import com.xueersi.ui.adapter.RCommonAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LiveFeedBackPager extends LiveBasePager {
    LiveHttpManager mHttpManager;
    RelativeLayout  rlFeedbackContent;
    /** 主讲布局 */
    RecyclerView rvFeedbackContent;
    RCommonAdapter contentAdapter;
    ImageView ivMainHeader;
    ImageView ivStatus1;
    ImageView ivStatus2;
    ImageView ivStatus3;
    TextView tvMainName;
    EditText etMainFeedback;


    /** 辅导布局 */
    RelativeLayout rlTutorContent;
    RecyclerView rvTutorContent;
    RCommonAdapter contentTutorAdapter;
    ImageView ivTutorHeader;
    ImageView ivTutorStatus1;
    ImageView ivTutorStatus2;
    ImageView ivTutorStatus3;
    TextView tvTutorName;
    EditText etTutorFeedback;
    TextView tvMainInputNum;
    TextView tvTutorInputNum;
    List<EvaluateContent> mainFeedbackList;

    List<EvaluateContent> tutorFeedbackList;

    /** 反馈内容 */
    FeedBackEntity mFeedbackEntity;
    /** 提交提示 */
    TextView tvSubmitHint;
    TextView ivSubmit;
    ImageView ivClose;

    /** 不满意 */
    int FEED_TYPE_1 = 1;
    /** 有待提高 */
    int FEED_TYPE_2 = 2;
    /** 满意 */
    int FEED_TYPE_3 = 3;

    int mainType = 0;
    int tutorType = 0;

    LiveGetInfo mGetInfo;

    String mainIntput, tutorInput;
    List<String> mainFeedback = new ArrayList<>();
    List<String> tutorFeedback = new ArrayList<>();
    String liveId;
    int redColor;
    int greyColor;
    NestedScrollView nestedScrollView;
    TextView tvSubmitError;
    TextView tvRetrySubmit;
    LinearLayout llRetrySubmit;
    ImageView ivRetryLoading;
    public boolean isShow = false;
    public boolean showEvaluate = false;
    ImageView ivTitle;
    LinearLayout llBottom;
    LinearLayout llSubmitSucess;

    String mainScore;
    String tutorScore;
    FeedBackTeacherInterface feedBackTeacherInterface;
    private String  courseId;
    public LiveFeedBackPager(Context context) {
        super(context);
    }

    public LiveFeedBackPager(Context context, boolean isNewView) {
        super(context, isNewView);

    }

    public LiveFeedBackPager(Context context, Object obj, boolean isNewView) {
        super(context, obj, isNewView);
    }

    public LiveFeedBackPager(Context context, String liveId, FeedBackEntity feedBackEntity, LiveGetInfo getInfo,
                             LiveHttpManager mHttpManager) {
        super(context, feedBackEntity, true);
        this.mGetInfo = getInfo;
        this.liveId = liveId;
        this.mHttpManager = mHttpManager;
        redColor = mContext.getResources().getColor(R.color.COLOR_FB5E50);
        greyColor = mContext.getResources().getColor(R.color.COLOR_5E5E7F);
        setLayout(mView);
        mFeedbackEntity = feedBackEntity;
        showEvaluate = true;
        initData();
    }

    private void setLayout(View view) {
        // 设置主视图参数
        RelativeLayout.LayoutParams mainParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
                .MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(mainParam);
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.layout_live_video_feed_back, null);
        rlFeedbackContent=  mView.findViewById(R.id.rl_pager_live_teacher_feedback_content);
        rvFeedbackContent = mView.findViewById(R.id.rv_pager_live_teacher_feedback_content);
        ivTitle = mView.findViewById(R.id.iv_pager_live_teacher_feedback_title);
        llBottom = mView.findViewById(R.id.ll_pager_live_teacher_feedback_bottom);
        llSubmitSucess = mView.findViewById(R.id.ll_pager_live_teacher_feedback_submit_sucess);
        rvTutorContent = mView.findViewById(R.id.rv_pager_live_teacher_feedback_tutor_content);
        ivMainHeader = mView.findViewById(R.id.iv_pager_live_teacher_feedback_main_head_image);
        tvMainName = mView.findViewById(R.id.tv_pager_live_teacher_feedback_main_name);
        ivStatus1 = mView.findViewById(R.id.iv_pager_live_teacher_feedback_main_status_1);
        ivStatus2 = mView.findViewById(R.id.iv_pager_live_teacher_feedback_main_status_2);
        ivStatus3 = mView.findViewById(R.id.iv_pager_live_teacher_feedback_main_status_3);
        etMainFeedback = mView.findViewById(R.id.et_pager_live_teacher_feedback_main_input_text);

        rlTutorContent = mView.findViewById(R.id.rl_pager_live_teacher_feedback_tutor_content);
        ivTutorHeader = mView.findViewById(R.id.iv_pager_live_teacher_feedback_tutor_head_image);
        tvTutorName = mView.findViewById(R.id.tv_pager_live_teacher_feedback_tutor_name);
        ivTutorStatus1 = mView.findViewById(R.id.iv_pager_live_teacher_feedback_tutor_status_1);
        ivTutorStatus2 = mView.findViewById(R.id.iv_pager_live_teacher_feedback_tutor_status_2);
        ivTutorStatus3 = mView.findViewById(R.id.iv_pager_live_teacher_feedback_tutor_status_3);
        etTutorFeedback = mView.findViewById(R.id.et_pager_live_teacher_feedback_tutor_input_text);
        tvSubmitHint = mView.findViewById(R.id.tv_pager_live_teacher_feedback_bottom_submit_hint);
        ivSubmit = mView.findViewById(R.id.iv_pager_live_teacher_feedback_submit);
        ivClose = mView.findViewById(R.id.iv_pager_live_teacher_feedback_close);
        nestedScrollView = mView.findViewById(R.id.nsv_pager_live_teacher_feedback_content);
        tvSubmitError = mView.findViewById(R.id.tv_pager_live_teacher_feedback_submit_error);

        tvMainInputNum = mView.findViewById(R.id.tv_pager_live_teacher_feedback_main_input_num);
        tvTutorInputNum = mView.findViewById(R.id.tv_pager_live_teacher_feedback_tutor_input_num);
        tvRetrySubmit = mView.findViewById(R.id.tv_pager_live_teacher_feedback_retry_submit);
        llRetrySubmit= mView.findViewById(R.id.ll_pager_live_teacher_feedback_retry_content);
        ivRetryLoading = mView.findViewById(R.id.iv_pager_live_teacher_feedback_retry_loading);


        GridLayoutManager manager = new GridLayoutManager(mContext, 3);
        rvFeedbackContent.setLayoutManager(manager);
        GridLayoutManager manager2 = new GridLayoutManager(mContext, 3);
        rvTutorContent.setLayoutManager(manager2);

        setRecyclerViewDecoration();
//        mainFeedbackList = new ArrayList<String>();
//        mainFeedbackList.add("讲得太快了");
//        mainFeedbackList.add("讲得太慢了");
//        mainFeedbackList.add("内容讲错了");
//        mainFeedbackList.add("闲话有点多");
//        mainFeedbackList.add("只会读课件");
//        mainFeedbackList.add("课堂闷无聊");
        setClickListener();
        showEvaluate = true;
        return mView;

    }

    @Override
    public boolean onUserBackPressed() {
        if (showEvaluate) {
            if (!isShow) {
                isShow = feedBackTeacherInterface.showPager();
            } else {
                isShow = feedBackTeacherInterface.removeView();
            }
            return isShow;
        } else {
            return false;
        }
    }

    public void setRecyclerViewDecoration() {
        HashMap<String, Integer> stringIntegerHashMap = new HashMap<>();
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.TOP_DECORATION, 0);//top间距
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.BOTTOM_DECORATION, 0);//底部间距
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.LEFT_DECORATION, SizeUtils.Dp2Px(mContext, 20));//左间距
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.RIGHT_DECORATION, 0);//右间距
        rvFeedbackContent.addItemDecoration(new RecyclerViewSpacesItemDecoration(stringIntegerHashMap));
        rvTutorContent.addItemDecoration(new RecyclerViewSpacesItemDecoration(stringIntegerHashMap));
    }


    @Override
    public void initData() {
        super.initData();
        //  setFeedStyleList(FEED_TYPE_1);

        tvMainName.setText(mGetInfo.getMainTeacherInfo().getTeacherName());
        if (TextUtils.isEmpty(mGetInfo.getMainTeacherInfo().getTeacherImg())) {
            ImageLoader.with(ContextManager.getContext()).asCircle().load(R.drawable.bg_main_default_head_image)
                    .placeHolder(R.drawable.bg_main_default_head_image).into(ivMainHeader);
        } else {
            ImageLoader.with(ContextManager.getContext()).asCircle().load(mGetInfo.getMainTeacherInfo()
                    .getTeacherImg())
                    .placeHolder(R.drawable.bg_main_default_head_image).into(ivMainHeader);
        }

        if (mFeedbackEntity.isHaveTutor()) {
            rlTutorContent.setVisibility(View.VISIBLE);
            tvTutorName.setText(mGetInfo.getTeacherName());
            if (TextUtils.isEmpty(mGetInfo.getTeacherIMG())) {
                ImageLoader.with(ContextManager.getContext()).asCircle().load(R.drawable.bg_main_default_head_image)
                        .placeHolder(R.drawable.bg_main_default_head_image).into(ivTutorHeader);
            } else {
                ImageLoader.with(ContextManager.getContext()).asCircle().load(mGetInfo.getTeacherIMG())
                        .placeHolder(R.drawable.bg_main_default_head_image).into(ivTutorHeader);
            }

        } else {
            rlTutorContent.setVisibility(View.GONE);

        }
        // 设置反馈内容
        //  setContentData();
    }


    /**
     * 设置反馈内容
     */
    private void setContentData() {
        if (mainFeedbackList != null && mainFeedbackList.size() > 0) {
            if (mFeedbackEntity.isHaveInput()) {
                etMainFeedback.setVisibility(View.VISIBLE);
                tvMainInputNum.setVisibility(View.VISIBLE);
            } else {
                etMainFeedback.setVisibility(View.GONE);
                tvMainInputNum.setVisibility(View.GONE);

            }
            rvFeedbackContent.setVisibility(View.VISIBLE);
            if (contentAdapter == null) {
                contentAdapter = new RCommonAdapter(mContext, mainFeedbackList);
                contentAdapter.addItemViewDelegate(1, new LiveTeacherFeedbackItem(mContext, feedbackSelect,
                        true, redColor, greyColor));
                rvFeedbackContent.setAdapter(contentAdapter);
            } else {
                contentAdapter.updateData(mainFeedbackList);
            }
        } else {
            rvFeedbackContent.setVisibility(View.GONE);

        }
        if (tutorFeedbackList != null && tutorFeedbackList.size() > 0) {
            if (mFeedbackEntity.isHaveInput() && mFeedbackEntity.isHaveTutor()) {
                etTutorFeedback.setVisibility(View.VISIBLE);
                tvTutorInputNum.setVisibility(View.VISIBLE);
            } else {
                etTutorFeedback.setVisibility(View.GONE);
                tvTutorInputNum.setVisibility(View.GONE);

            }
            if (contentTutorAdapter == null) {
                contentTutorAdapter = new RCommonAdapter(mContext, tutorFeedbackList);
                contentTutorAdapter.addItemViewDelegate(1, new LiveTeacherFeedbackItem(mContext, feedbackSelect,
                        false, redColor, greyColor));
                rvTutorContent.setAdapter(contentTutorAdapter);
            } else {
                contentTutorAdapter.updateData(tutorFeedbackList);
            }
            rvTutorContent.setVisibility(View.VISIBLE);

        } else {
            rvTutorContent.setVisibility(View.GONE);
        }
    }

    private void setClickListener() {
        // 主讲输入监听
        etMainFeedback.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                tvMainInputNum.setText(etMainFeedback.getText().toString().trim().length() + "/200");
                if (etMainFeedback.getText().toString().trim().length() >= 196) {
                    tvMainInputNum.setTextColor(redColor);
                } else {
                    tvMainInputNum.setTextColor(greyColor);

                }
                mainIntput = etMainFeedback.getText().toString();
                if(tvSubmitHint.getVisibility() == View.VISIBLE) {
                    checkContentSubmit();
                }
            }
        });
        // 主讲输入监听
        etTutorFeedback.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                tvTutorInputNum.setText(etTutorFeedback.getText().toString().trim().length() + "/200");
                if (etTutorFeedback.getText().toString().trim().length() >= 196) {
                    tvTutorInputNum.setTextColor(redColor);
                } else {
                    tvTutorInputNum.setTextColor(greyColor);

                }
                tutorInput = etTutorFeedback.getText().toString();
                if(tvSubmitHint.getVisibility() == View.VISIBLE) {

                    checkContentSubmit();
                }
            }
        });

        // 提交
        ivSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainIntput = etMainFeedback.getText().toString().trim();
                tutorInput = etTutorFeedback.getText().toString().trim();
                if (checkContentSubmit()) {
                    submitFeedback();
                }
            }
        });
        llRetrySubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivRetryLoading.setVisibility(View.VISIBLE);
                submitFeedback();
            }
        });
        // 关闭
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onClose();
            }
        });
        // 主讲-不满意
        ivStatus1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainScore = "不满意";
                mainFeedback.clear();
                setSelect(true);
                setStyleData(FEED_TYPE_1, true);
            }
        });
        // 主讲-有待提高
        ivStatus2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainScore = "有待提高";

                mainFeedback.clear();
                setSelect(true);
                setStyleData(FEED_TYPE_2, true);

            }
        });
        // 主讲-满意
        ivStatus3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainScore = "满意";
                mainFeedback.clear();
                setSelect(true);
                setStyleData(FEED_TYPE_3, true);

            }
        });
        // 辅导-不满意
        ivTutorStatus1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tutorScore = "不满意";
                tutorFeedback.clear();
                setSelect(false);
                setStyleData(FEED_TYPE_1, false);

            }
        });
        // 辅导-有待提交
        ivTutorStatus2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tutorScore = "有待提高";
                tutorFeedback.clear();
                setSelect(false);
                setStyleData(FEED_TYPE_2, false);

            }
        });
        // 辅导-满意
        ivTutorStatus3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tutorScore = "满意";
                tutorFeedback.clear();
                setSelect(false);
                setStyleData(FEED_TYPE_3, false);

            }
        });
    }

    private void onClose() {
        if (onPagerClose != null) {
            onPagerClose.onClose(this);
        }
        if (feedBackTeacherInterface != null) {
            feedBackTeacherInterface.onClose();
        }
    }

    CountDownTimer timer = new CountDownTimer(2000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
//            String time = String.valueOf(millisUntilFinished / 1000);
            //  onSubmitError(time + "s后退出直播间", true);
        }

        @Override
        public void onFinish() {
            if (feedBackTeacherInterface != null) {
                feedBackTeacherInterface.onClose();
            }
        }
    };

    /**
     * 提交反馈
     */
    private void submitFeedback() {
        Drawable leftDraw = mContext.getResources().getDrawable(R.drawable.lspj_tanchuang_loading_icon_normal);
        ivSubmit.setCompoundDrawablesWithIntrinsicBounds(leftDraw, null, null, null);
        if(TextUtils.isEmpty(courseId)) {
            courseId = mGetInfo.getStudentLiveInfo().getCourseId();
        }
        mHttpManager.saveEvaluationTeacher(liveId, courseId, mGetInfo.getMainTeacherInfo()
                        .getTeacherId(),mainScore,
                getFeedTextList(mainFeedback), mGetInfo.getTeacherId(),tutorScore, getFeedTextList(tutorFeedback),
                 mGetInfo.getStudentLiveInfo().getClassId(), mainIntput,tutorInput,new HttpCallBack() {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        ivSubmit.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        ivTitle.setVisibility(View.GONE);
                        nestedScrollView.setVisibility(View.GONE);
                        tvSubmitError.setVisibility(View.GONE);
                        llBottom.setVisibility(View.GONE);
                        ivClose.setVisibility(View.GONE);
                        rlFeedbackContent.setBackgroundResource(R.color.transparent_60);
                        llSubmitSucess.setVisibility(View.VISIBLE);
                        llRetrySubmit.setVisibility(View.GONE);
                        timer.start();
                    }

                    @Override
                    public void onPmFailure(Throwable error, String msg) {
                        super.onPmFailure(error, msg);
                        onSubmitError(msg, false);
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        super.onPmError(responseEntity);
                        onSubmitError(responseEntity.getErrorMsg(), false);
                    }
                });
    }

    private String getFeedTextList(List<String> list) {
        String mainText = "";
        for (int i = 0; i < list.size(); i++) {
            if (i != 0) {
                mainText = mainText + "," + list.get(i);
            } else {
                mainText = list.get(i);
            }
        }
        return mainText;
    }

    private void onSubmitError(String text, boolean isSuccess) {
        tvSubmitError.setText(text);
        if (!isSuccess) {
            llRetrySubmit.setVisibility(View.VISIBLE);
            ivSubmit.setVisibility(View.GONE);
        }
        tvSubmitError.setVisibility(View.VISIBLE);
        nestedScrollView.setVisibility(View.GONE);


    }

    /**
     * 是否输入一个内容
     *
     * @return
     */
    private boolean checkContentSubmit() {
        if (mainType == 0 && (mFeedbackEntity.isHaveTutor() && tutorType == 0)) {
            tvSubmitHint.setText("请选择一个满意度");
            tvSubmitHint.setVisibility(View.VISIBLE);
            return false;
        }
        if (mFeedbackEntity.isHaveTutor()) {
            if (mainFeedback.size() == 0 && (mFeedbackEntity.isHaveInput() && TextUtils.isEmpty(mainIntput))
                    && tutorFeedback.size() == 0 && (mFeedbackEntity.isHaveInput() && TextUtils.isEmpty(tutorInput))) {
                tvSubmitHint.setText("请至少为一位老师做出文字评价或一个以上标签");
                tvSubmitHint.setVisibility(View.VISIBLE);
                return false;
            }
        } else {
            if (mainFeedback.size() == 0 && (mFeedbackEntity.isHaveInput() && TextUtils.isEmpty(mainIntput))) {
                tvSubmitHint.setText("请输入文字建议或至少选择一个标签");
                tvSubmitHint.setVisibility(View.VISIBLE);
                return false;
            }
        }
        tvSubmitHint.setVisibility(View.INVISIBLE);
        return true;
    }

    /**
     * 选中态设置
     *
     * @param style
     * @param isMain
     */
    private void setStyleData(int style, boolean isMain) {
        if (isMain) {
            mainType = style;
            if (FEED_TYPE_1 == style) {
                ivStatus1.setBackgroundResource(R.drawable.bg_live_video_feedback_type1_selected);
                ivStatus2.setBackgroundResource(R.drawable.bg_live_video_feedback_type2_normal);
                ivStatus3.setBackgroundResource(R.drawable.bg_live_video_feedback_type3_normal);
                etMainFeedback.setHint("说说老师有哪里需要改进的呢?");
            } else if (FEED_TYPE_2 == style) {
                ivStatus1.setBackgroundResource(R.drawable.bg_live_video_feedback_type1_normal);
                ivStatus2.setBackgroundResource(R.drawable.bg_live_video_feedback_type2_selected);
                ivStatus3.setBackgroundResource(R.drawable.bg_live_video_feedback_type3_normal);
                etMainFeedback.setHint("说说老师有哪里需要改进的呢?");

            } else {
                ivStatus1.setBackgroundResource(R.drawable.bg_live_video_feedback_type1_normal);
                ivStatus2.setBackgroundResource(R.drawable.bg_live_video_feedback_type2_normal);
                ivStatus3.setBackgroundResource(R.drawable.bg_live_video_feedback_type3_selected);
                etMainFeedback.setHint("谢谢你的肯定，说点什么夸夸你的老师吧!");

            }
        } else {
            tutorType = style;
            if (FEED_TYPE_1 == style) {
                ivTutorStatus1.setBackgroundResource(R.drawable.bg_live_video_feedback_type1_selected);
                ivTutorStatus2.setBackgroundResource(R.drawable.bg_live_video_feedback_type2_normal);
                ivTutorStatus3.setBackgroundResource(R.drawable.bg_live_video_feedback_type3_normal);
                etTutorFeedback.setHint("说说老师有哪里需要改进的呢?");

            } else if (FEED_TYPE_2 == style) {
                ivTutorStatus1.setBackgroundResource(R.drawable.bg_live_video_feedback_type1_normal);
                ivTutorStatus2.setBackgroundResource(R.drawable.bg_live_video_feedback_type2_selected);
                ivTutorStatus3.setBackgroundResource(R.drawable.bg_live_video_feedback_type3_normal);
                etTutorFeedback.setHint("说说老师有哪里需要改进的呢?");

            } else {
                ivTutorStatus1.setBackgroundResource(R.drawable.bg_live_video_feedback_type1_normal);
                ivTutorStatus2.setBackgroundResource(R.drawable.bg_live_video_feedback_type2_normal);
                ivTutorStatus3.setBackgroundResource(R.drawable.bg_live_video_feedback_type3_selected);
                etTutorFeedback.setHint("谢谢你的肯定，说点什么夸夸你的老师吧!");

            }
        }
        setFeedStyleList(style, isMain);
        if(tvSubmitHint.getVisibility() == View.VISIBLE) {
            checkContentSubmit();
        }
    }

    /**
     * 选中内容设置
     *
     * @param style
     */
    private void setFeedStyleList(int style, boolean isMain) {
        if (mFeedbackEntity.getMainContentList() != null && mFeedbackEntity.getMainContentList().size() >= style &&
                isMain) {
            mainFeedbackList = mFeedbackEntity.getMainContentList().get(style - 1);
        } else if (mFeedbackEntity.getTutorContentList() != null && mFeedbackEntity.getTutorContentList().size() >=
                style) {
            tutorFeedbackList = mFeedbackEntity.getTutorContentList().get(style - 1);
        }
        setContentData();
    }

    public interface FeedbackSelectInterface {
        void onSelect(String text, boolean isMain);
    }

    public FeedbackSelectInterface feedbackSelect = new FeedbackSelectInterface() {
        @Override
        public void onSelect(String text, boolean isMain) {
            setSelectData(text, isMain);

        }
    };

    /**
     * 评分选择
     *
     * @param text
     * @param isMain
     */
    private void setSelectData(String text, boolean isMain) {
        if (isMain) {
            if (mainFeedback.contains(text)) {
                mainFeedback.remove(text);
            } else {
                mainFeedback.add(text);
            }
        } else {
            if (tutorFeedback.contains(text)) {
                tutorFeedback.remove(text);
            } else {
                tutorFeedback.add(text);
            }
        }
        setSelect(isMain);
    }

    private void setSelect(boolean isMain) {
        if(tvSubmitHint.getVisibility() == View.VISIBLE) {
            checkContentSubmit();
        }
        if (isMain && mainFeedbackList != null && mainFeedbackList.size() > 0) {
            for (int i = 0; i < mainFeedbackList.size(); i++) {
                if (mainFeedback.contains(mainFeedbackList.get(i).getText())) {
                    mainFeedbackList.get(i).setSelectFlag(true);
                } else {
                    mainFeedbackList.get(i).setSelectFlag(false);

                }
            }
            if (contentAdapter != null) {
                contentAdapter.updateData(mainFeedbackList);
            }
        } else if (!isMain && tutorFeedbackList != null && tutorFeedbackList.size() > 0) {
            for (int i = 0; i < tutorFeedbackList.size(); i++) {
                if (tutorFeedback.contains(tutorFeedbackList.get(i).getText())) {
                    tutorFeedbackList.get(i).setSelectFlag(true);
                } else {
                    tutorFeedbackList.get(i).setSelectFlag(false);

                }
            }
            if (contentTutorAdapter != null) {
                contentTutorAdapter.updateData(tutorFeedbackList);
            }
        }
    }


    public void setFeedbackSelectInterface(FeedBackTeacherInterface feedBackTeacherInterface) {
        this.feedBackTeacherInterface = feedBackTeacherInterface;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
}
