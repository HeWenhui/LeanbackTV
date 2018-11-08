package com.xueersi.parentsmeeting.modules.livevideo.understand.page;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;

public class SmallChineseUnderstandPager extends BasePager {
    /**
     *
     */
    private ImageView ivUnderstandYes;
    /**
     *
     */
    private ImageView ivUnderstandNo;


    public SmallChineseUnderstandPager(Context context) {
        super(context);
        initListener();
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_small_chinese_understand, null);
        ivUnderstandYes = view.findViewById(R.id.iv_livevideo_small_chinese_understand_yes);
        ivUnderstandNo = view.findViewById(R.id.iv_livevideo_small_chinese_understand_no);

        return view;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {
        super.initListener();
        ivUnderstandYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }

        });

        ivUnderstandNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    /**
     * 同小英
     */
    public interface UnderStandListener {
        void closeListener();

        void underStandListener(boolean underStand);

        void noUnderStandListener(boolean noUnderStand);
    }

    private UnderStandListener mUnderStandListener;

    public void setListener(UnderStandListener underStandListener) {
        this.mUnderStandListener = underStandListener;
    }
}

