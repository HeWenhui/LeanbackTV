package com.xueersi.parentsmeeting.modules.livevideo.fragment.se.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Group;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.xueersi.parentsmeeting.module.videoplayer.entity.LiveExperienceEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.widget.FangZhengCuYuanTextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 将反馈谈弹窗的每个题目（包含一个选项和三个选择按钮）封装成一个Layout
 */
public class ItemLearnFeedbackPageSelectLayout extends ConstraintLayout {

    private Context mContext;
    /**
     * 问题的题目
     */
    private FangZhengCuYuanTextView fzcytvTitle;
    /**
     * 问题的三个单选按钮
     */
    private ImageView ivSelectBtn1, ivSelectBtn2, ivSelectBtn3;
    /**
     * 问题的三个单选按钮的选项文字
     */
    private FangZhengCuYuanTextView fzcytvSelect1, fzcytvSelect2, fzcytvSelect3;
    /**
     * 第三个单选框是否显示
     */
    private Group groupSelect3;

    public ItemLearnFeedbackPageSelectLayout(Context context) {
        super(context);
        init(context);
    }

    public ItemLearnFeedbackPageSelectLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ItemLearnFeedbackPageSelectLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        initView(context);
        initListener();
    }


    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.item_stand_experience_learn_feedback_select, this, true);
        fzcytvTitle = findViewById(R.id.fzcytv_stand_experience_learn_feedback_title);
        fzcytvSelect1 = findViewById(R.id.fzcytv_stand_experience_learn_feedback_select1_button1);
        fzcytvSelect2 = findViewById(R.id.fzcytv_stand_experience_learn_feedback_select1_button2);
        fzcytvSelect3 = findViewById(R.id.fzcytv_stand_experience_learn_feedback_select1_button3);
        ivSelectBtn1 = findViewById(R.id.iv_stand_experience_learn_feedback_select_button1);
        ivSelectBtn2 = findViewById(R.id.iv_stand_experience_learn_feedback_select_button2);
        ivSelectBtn3 = findViewById(R.id.iv_stand_experience_learn_feedback_select_button3);
        groupSelect3 = findViewById(R.id.group_stand_experience_learn_feedback_select3);
    }

    /**
     * 最后选择的答案
     */
    private String ans;

    private void initListener() {
        ivSelectBtn1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSelect(true, false, false);
            }
        });
        ivSelectBtn2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSelect(false, true, false);
            }
        });
        ivSelectBtn3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSelect(false, false, true);
            }
        });
    }

    private void handleSelect(boolean isSelect1, boolean isSelect2, boolean isSelect3) {
        Drawable drawable = mContext.getResources().getDrawable(R.drawable.bg_stand_experience_learn_feedback_radiobutton_select);
        Drawable noSelectDrawable = mContext.getResources().getDrawable(R.drawable.bg_stand_experience_learn_feedback_radiobutton_nor);
        ivSelectBtn1.setImageDrawable(noSelectDrawable);
        ivSelectBtn2.setImageDrawable(noSelectDrawable);
        ivSelectBtn3.setImageDrawable(noSelectDrawable);
        if (isSelect1) {
            ans = questionOption.listKey.get(0);
            ivSelectBtn1.setImageDrawable(drawable);
        }
        if (isSelect2) {
            ans = questionOption.listKey.get(1);
            ivSelectBtn2.setImageDrawable(drawable);
        }
        if (isSelect3) {
            ans = questionOption.listKey.get(2);
            ivSelectBtn3.setImageDrawable(drawable);
        }
    }

    QuestionOption questionOption;

    public void updateView(LiveExperienceEntity.LearnFeedBack learnFeedBack) {
        if (learnFeedBack == null || learnFeedBack.getOptions() == null || learnFeedBack.getOptions().size() == 0) {
            return;
        }
        if (learnFeedBack.getOptions().size() < 3) {
            groupSelect3.setVisibility(GONE);
        }
        fzcytvTitle.setText(learnFeedBack.getTitle());
        questionOption = parseData(learnFeedBack);

        if (questionOption.listKey.size() > 0) {
            fzcytvSelect1.setText(questionOption.listValue.get(0));
        }
        if (questionOption.listValue.size() > 1) {
            fzcytvSelect2.setText(questionOption.listValue.get(1));
        }
        if (questionOption.listKey.size() > 2) {
            fzcytvSelect3.setText(questionOption.listValue.get(2));
        }

    }

    private QuestionOption parseData(LiveExperienceEntity.LearnFeedBack learnFeedBack) {
        Map<String, String> map = learnFeedBack.getOptions();
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
        return new QuestionOption(keyList, valueList);
    }

    public class QuestionOption {
        private List<String> listKey;
        private List<String> listValue;

        public QuestionOption(List<String> listKey, List<String> listValue) {
            this.listKey = listKey;
            this.listValue = listValue;
        }
    }

    public String getAns() {
        return ans;
    }
}
