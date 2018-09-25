package com.xueersi.parentsmeeting.modules.livevideo.fragment.standlivevideoexperience.standexperienceunderstand;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;

public class StandExperienceUnderstandPager extends BasePager {
    /**
     * 听懂了
     */
    public final static int STAND_EXPERIENCE_UNDERSTAND = 0;
    /**
     * 似懂非懂
     */
    public static final int STAND_EXPERIENCE_LITTLE_UNDERSTAND = 1;
    /**
     * 没懂
     */
    public static final int STAND_EXPERIENCE_NO_UNDERSTAND = 2;
    /**
     * 听懂了
     */
    private ImageView ivUnderStandBtn;
    /**
     * 没有听懂
     */
    private ImageView ivNoUnderStandBtn;
    /**
     * 似懂非懂
     */
    private ImageView ivLittleUnderStandBtn;
    /**
     * 懂了么点击事件监听器
     */
    private IUnderStandListener iUnderStandListener;

    public StandExperienceUnderstandPager(Context context) {
        super(context);
        initListener();
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_standexperience_understand, null);
        ivUnderStandBtn = view.findViewById(R.id.iv_livevideo_stand_experience_understand_understand);
        ivLittleUnderStandBtn = view.findViewById(R.id.iv_livevideo_stand_experience_understand_little_understand);
        ivNoUnderStandBtn = view.findViewById(R.id.iv_livevideo_stand_experience_understand_no_understand);
        return view;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {
        super.initListener();
        ivUnderStandBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickEvent(STAND_EXPERIENCE_UNDERSTAND);
            }
        });
        ivLittleUnderStandBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickEvent(STAND_EXPERIENCE_LITTLE_UNDERSTAND);
            }
        });
        ivNoUnderStandBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickEvent(STAND_EXPERIENCE_NO_UNDERSTAND);
            }
        });
    }

    private void clickEvent(int sign) {
        if (iUnderStandListener != null) {
            iUnderStandListener.onClick(sign);
        }
    }

    public interface IUnderStandListener {
        void onClick(int sign);
    }

    public void setUnderStandListener(IUnderStandListener iUnderStandListener) {
        this.iUnderStandListener = iUnderStandListener;
    }
}
