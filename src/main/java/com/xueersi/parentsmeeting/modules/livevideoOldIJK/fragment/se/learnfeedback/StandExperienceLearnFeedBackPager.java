package com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment.se.learnfeedback;

import android.content.Context;
import android.support.constraint.Group;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * 学习反馈弹窗，采用RadioGroup来进行单选，如果选项按钮左边的Button样式不好，使用
 *
 * @param <T>
 * @see NewStandExperienceLearnFeedBackPager
 * 来显示
 */
public class StandExperienceLearnFeedBackPager<T extends IStandExperienceLearnFeedbackContract.IExperienceSendHttp> extends BasePager {

    T presenter;

    RadioGroup radioGroup1, radioGroup2;

    Group groupSelect2Visible;

    RadioButton[][] radioButtons;
    /**
     * 反馈弹窗中需要显示的信息
     */
    private List<LiveExperienceEntity.LearnFeedBack> arrayOptions;
    /**
     * 提交按钮
     */
    private ImageView submitBtn;
    /**
     * 标题
     */
    private TextView tvTittle1, tvTittle2, tvSuggest;
    private VideoLivePlayBackEntity mVideoEntity;

    private ImageView ivClose;
    /**
     * 反馈建议
     */
    EditText etSuggest;
    /**
     * 问题2是否隐藏
     */
    private Group select2;

    public StandExperienceLearnFeedBackPager(Context context, T presenter, VideoLivePlayBackEntity
            videoLivePlayBackEntity) {
        super(context);
        this.presenter = presenter;
        this.mVideoEntity = videoLivePlayBackEntity;
        arrayOptions = videoLivePlayBackEntity.getLearnFeedback();
        questionSize = arrayOptions.size();
        initListener();
        initData();
    }

    int[][] radioButtonIds;

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.page_stand_experience_learn_feedback, null);
        radioGroup1 = mView.findViewById(R.id.rg_stand_experience_learn_feedback_select1);
        radioGroup2 = mView.findViewById(R.id.rg_stand_experience_learn_feedback_select2);
        tvTittle1 = mView.findViewById(R.id.fzcytv_stand_experience_learn_feedback_title1);
        tvTittle2 = mView.findViewById(R.id.fzcytv_stand_experience_learn_feedback_title2);
        tvSuggest = mView.findViewById(R.id.fzcytv_stand_experience_learn_feedback_suggest);
        radioButtonIds = new int[][]{
                {
                        R.id.rb_stand_experience_learn_feedback_select1_button1,
                        R.id.rb_stand_experience_learn_feedback_select1_button2,
                        R.id.rb_stand_experience_learn_feedback_select1_button3
                },
                {
                        R.id.rb_stand_experience_learn_feedback_select2_button1,
                        R.id.rb_stand_experience_learn_feedback_select2_button2,
                        R.id.rb_stand_experience_learn_feedback_select2_button3
                }
        };
        logger.i(radioButtonIds.length);
        logger.i(" " + radioButtonIds[0].length);
        radioButtons = new RadioButton[radioButtonIds.length][radioButtonIds[0].length];
        for (int i = 0; i < line; i++) {
            for (int j = 0; j < column; j++) {
                radioButtons[i][j] = mView.findViewById(radioButtonIds[i][j]);
            }
        }
        etSuggest = mView.findViewById(R.id.et_stand_experience_learn_feedback_suggest);
        submitBtn = mView.findViewById(R.id.btn_stand_experience_learn_feedback_submit);
        ivClose = mView.findViewById(R.id.iv_stand_experience_learn_feedback_close);

        return mView;
    }

    String radioGroupAns1, radioGroupAns2;

    @Override
    public void initListener() {
        super.initListener();
        radioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.rb_stand_experience_learn_feedback_select1_button1) {
                    radioGroupAns1 = questionList.get(0).listKey.get(0);
                } else if (checkedId == R.id.rb_stand_experience_learn_feedback_select1_button2) {
                    radioGroupAns1 = questionList.get(0).listKey.get(1);
                } else if (checkedId == R.id.rb_stand_experience_learn_feedback_select1_button3) {
                    radioGroupAns1 = questionList.get(0).listKey.get(2);
                }
            }
        });
        if (questionSize > 1) {
            radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {

                    if (checkedId == R.id.rb_stand_experience_learn_feedback_select2_button1) {
                        radioGroupAns2 = questionList.get(1).listKey.get(0);
                    } else if (checkedId == R.id.rb_stand_experience_learn_feedback_select2_button2) {
                        radioGroupAns2 = questionList.get(1).listKey.get(1);
                    } else if (checkedId == R.id.rb_stand_experience_learn_feedback_select2_button3) {
                        radioGroupAns2 = questionList.get(1).listKey.get(2);
                    }
                }
            });


        } else {
            radioGroup2.setVisibility(View.GONE);
        }
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (presenter != null) {
                    presenter.removeWindow();
                }
            }
        });
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(radioGroupAns1)) {
                    Toast.makeText(mContext, "你至少需要选择一项哦", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (questionSize == 2 && TextUtils.isEmpty(radioGroupAns2)) {
//                    if (TextUtils.isEmpty(radioGroupAns1) || TextUtils.isEmpty(radioGroupAns2)) {
                    Toast.makeText(mContext, "你至少需要选择一项哦", Toast.LENGTH_SHORT).show();
                    return;
                }
                JSONArray jsonArray = new JSONArray();
                JSONObject jsonObject1 = new JSONObject();
                JSONObject jsonObject2 = new JSONObject();
                try {
                    jsonObject1.put("1", radioGroupAns1);
                    jsonObject2.put("2", radioGroupAns2);
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
        etSuggest.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                //触摸的是EditText并且当前EditText可以滚动则将事件交给EditText处理；否则将事件交由其父类处理
                if ((view.getId() == R.id.et_stand_experience_learn_feedback_suggest && canVerticalScroll(etSuggest))) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        view.getParent().requestDisallowInterceptTouchEvent(false);
                    }
                }
                return false;
            }

            /**
             * EditText竖直方向是否可以滚动
             * @param editText  需要判断的EditText
             * @return true：可以滚动   false：不可以滚动
             */
            private boolean canVerticalScroll(EditText editText) {
                //滚动的距离
                int scrollY = editText.getScrollY();
                //控件内容的总高度
                int scrollRange = editText.getLayout().getHeight();
                //控件实际显示的高度
                int scrollExtent = editText.getHeight() - editText.getCompoundPaddingTop() - editText.getCompoundPaddingBottom();
                //控件内容总高度与实际显示高度的差值
                int scrollDifference = scrollRange - scrollExtent;
                if (scrollDifference == 0) {
                    return false;
                }
                return (scrollY > 0) || (scrollY < scrollDifference - 1);
            }
        });

    }

    /**
     * 存储map,采用value-key的方式便于查找
     */
//    List<Map<String, String>> optionArray;
    ArrayList<QuestionOption> questionList;
    /**
     * 题目数量
     */
    int questionSize;
    String title1, title2;
    /**
     * 默认选项
     */
    String defaultOp1, defaultOp2;

    @Override
    public void initData() {
        questionSize = arrayOptions.size();
        title1 = arrayOptions.get(0).getTitle();
        title2 = arrayOptions.get(1).getTitle();
        defaultOp1 = arrayOptions.get(0).getDefaultOption();
        defaultOp2 = arrayOptions.get(1).getDefaultOption();
        questionList = new ArrayList<>();
        for (LiveExperienceEntity.LearnFeedBack item : arrayOptions) {
            item.getTitle();
            Map<String, String> map = item.getOptions();
            int length = map.size();
//            Map<String, String> itemMap = new HashMap<>();//采用value-key的方式来查找key;

            List<String> keyList = new ArrayList<>();
            List<String> valueList = new ArrayList<>();

            Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entryKey = iterator.next();
                String key = entryKey.getKey();
                String value = entryKey.getValue();
//                itemMap.put(value, key);
                keyList.add(key);
                valueList.add(value);
            }
            QuestionOption tempQuestion = new QuestionOption(keyList, valueList);
            questionList.add(tempQuestion);
        }
        setView();
        judgeNum();

//        if(radioGroupAns1.)

//        if (!TextUtils.isEmpty(arrayOptions.get(0).getDefaultOption())) {
//            radioGroupAns1 = arrayOptions.get(0).getDefaultOption();
//        }
//        if (!TextUtils.isEmpty(arrayOptions.get(1).getDefaultOption())) {
//            radioGroupAns2 = arrayOptions.get(1).getDefaultOption();
//        }
    }

    public class QuestionOption {
        private List<String> listKey;
        private List<String> listValue;

        public QuestionOption(List<String> listKey, List<String> listValue) {
            this.listKey = listKey;
            this.listValue = listValue;
        }
    }

    /**
     * 拿到数据后，对View进行数据填充
     */
    private final int column = 3;
    private final int line = 2;

    /**
     * 根据
     * {@Link #initData()}
     */
    private void setView() {
        logger.i(radioButtons.length + " " + radioButtons[0].length);

        for (int i = 0; i < questionList.size(); i++) {
            List<String> itemValueList = questionList.get(i).listValue;
            List<String> itemKeyList = questionList.get(i).listKey;
            for (int j = 0; j < itemValueList.size(); j++) {

//                radioButtons[i][j].setTypeface(FontCache.getTypeface(mContext, "fangzhengcuyuan.ttf"));
                ;
                radioButtons[i][j].setText(itemValueList.get(j));
                if (i == 0 && defaultOp1 != null && defaultOp1.equals(itemKeyList.get(j))) {
                    radioButtons[0][j].setChecked(true);
                    radioGroupAns1 = defaultOp1;
                }

                if (i == 1 && defaultOp2 != null && defaultOp2.equals(itemKeyList.get(j))) {
                    radioButtons[1][j].setChecked(true);
                    radioGroupAns2 = defaultOp2;
                }
//                radioButtons[i][j].setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fangzhengcuyuan.ttf"));
            }
        }
        //设置默认选项至
//        for (int i = 0; i < questionList.size(); i++) {

//        }
        tvTittle1.setText(title1);
        tvTittle2.setText(title2);

    }

    /**
     * 判断有几个题目，以及几个选项
     */
    private void judgeNum() {
        if (arrayOptions.size() == 1) {
//            select2.setVisibility(View.GONE);
            radioGroup2.setVisibility(View.GONE);
            if (arrayOptions.get(0).getOptions().size() < 3) {//如果只有两个选项，隐藏第三个.
                radioButtons[0][2].setVisibility(View.INVISIBLE);
            }
        } else {
            if (arrayOptions.get(0).getOptions().size() < 3) {//如果只有两个选项，隐藏第三个.
                radioButtons[0][2].setVisibility(View.INVISIBLE);
            }
            if (arrayOptions.get(1).getOptions().size() < 3) {
                radioButtons[1][2].setVisibility(View.INVISIBLE);
            }
        }

    }
}
