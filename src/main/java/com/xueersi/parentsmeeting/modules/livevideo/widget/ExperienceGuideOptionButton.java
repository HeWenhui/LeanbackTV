package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;

public class ExperienceGuideOptionButton extends LinearLayout {

    ImageView ivMonkey;
    TextView tvTitle;
    TextView tvContent;
    private View view;

    public ExperienceGuideOptionButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initParams(context,attrs);
    }

    public ExperienceGuideOptionButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
       initParams(context,attrs);
    }

    private void initParams(Context context, AttributeSet attrs) {
        view = LayoutInflater.from(context).inflate(R.layout.item_experience_guide_option, this, true);
        ivMonkey = findViewById(R.id.iv_experience_guide_home_monkey);
        tvTitle = findViewById(R.id.tv_experience_guide_home_title);
        tvContent = findViewById(R.id.tv_experience_guide_home_content);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ExpGuideOptionView);
        ivMonkey.setImageDrawable(typedArray.getDrawable(R.styleable.ExpGuideOptionView_imageSrc));
        tvTitle.setText(typedArray.getText(R.styleable.ExpGuideOptionView_titleText));
        tvContent.setText(typedArray.getText(R.styleable.ExpGuideOptionView_contentText));
        typedArray.recycle();
        initListener();
    }

    private void initListener (){
        view.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_BUTTON_PRESS||event.getAction() == MotionEvent.ACTION_MOVE){
                    tvTitle.setTextColor(0xffff6e1a);
                } else {
                    tvTitle.setTextColor(0xff333333);
                }
                return false;
            }
        });
    }



}
