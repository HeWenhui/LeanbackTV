package com.xueersi.parentsmeeting.modules.livevideoOldIJK.understand.page;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;

public class SmallChineseUnderstandPager extends BasePager {
    /** 懂了 */
    private ImageView ivUnderstandYes;
    /** 没懂 */
    private ImageView ivUnderstandNo;
    /** 关闭 */
    private ImageView ivClose;

    public SmallChineseUnderstandPager(Context context) {
        super(context);
        initListener();
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_small_chinese_understand, null);
        ivUnderstandYes = view.findViewById(R.id.iv_livevideo_small_chinese_understand_yes);
        ivUnderstandNo = view.findViewById(R.id.iv_livevideo_small_chinese_understand_no);
        ivClose = view.findViewById(R.id.iv_livevideo_small_chinese_understand_close);
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
                if (mUnderStandListener != null) {
                    mUnderStandListener.underStand(v.getId() == R.id.iv_livevideo_small_chinese_understand_yes);
                }
            }

        });

        ivUnderstandNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUnderStandListener != null) {
                    mUnderStandListener.underStand(v.getId() == R.id.iv_livevideo_small_chinese_understand_yes);
                }
            }
        });
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUnderStandListener != null) {
                    mUnderStandListener.close();
                }
            }
        });
    }

    /**
     * 同小英
     */
    public interface UnderStandListener {
        /**
         * 关闭当前监听器
         */
        void close();

        /**
         * 是否懂了
         *
         * @param underStand
         */
        void underStand(boolean underStand);
    }

    private UnderStandListener mUnderStandListener;

    public void setListener(UnderStandListener underStandListener) {
        this.mUnderStandListener = underStandListener;
    }
}

