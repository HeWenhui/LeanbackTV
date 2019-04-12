package com.xueersi.parentsmeeting.modules.livevideoOldIJK.understand.page;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;

public class SmallEnglishUnderstandPager extends BasePager {

    ImageView underStand;

    ImageView noUnderStand;

    ImageView ivClose;

    public SmallEnglishUnderstandPager(Context context) {
        super(context);
        initData();
        initListener();
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.layout_livevideo_small_english_understand, null);
        underStand = view.findViewById(R.id.iv_livevideo_small_english_understand);
        noUnderStand = view.findViewById(R.id.iv_livevideo_small_english_no_understand);
        //.setOnClickListener(smallEnglishListener);
        ivClose = view.findViewById(R.id.iv_livevideo_small_english_close);
        //.setOnClickListener(smallEnglishCloseListener);

        return view;
    }

    @Override
    public void initData() {
        mView.setClickable(true);
    }

    @Override
    public void initListener() {
        super.initListener();
        underStand.setOnClickListener(underStandListener);
        noUnderStand.setOnClickListener(noUnderStandListener);
        ivClose.setOnClickListener(closeListener);
        //(smallEnglishListener);

    }

    private View.OnClickListener closeListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mUnderStandListener != null) {
                mUnderStandListener.closeListener();
            }
        }
    };
    private View.OnClickListener underStandListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mUnderStandListener != null) {
                mUnderStandListener.underStandListener(v.getId() == R.id.iv_livevideo_small_english_understand);
            }
        }
    };
    private View.OnClickListener noUnderStandListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mUnderStandListener != null) {
                mUnderStandListener.noUnderStandListener(v.getId() == R.id.iv_livevideo_small_english_understand);
            }
        }
    };
    //private View.OnClickListener

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
