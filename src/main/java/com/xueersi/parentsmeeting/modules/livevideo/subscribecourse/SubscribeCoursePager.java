package com.xueersi.parentsmeeting.modules.livevideo.subscribecourse;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.lib.framework.utils.listener.OnUnDoubleClickListener;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;

/**
 * @ProjectName: xueersiwangxiao
 * @Package: com.xueersi.parentsmeeting.modules.livevideo.subscribecourse
 * @ClassName: SubscribeCoursePager
 * @Description: 讲座预约所有系列讲座页面
 * @Author: WangDe
 * @CreateDate: 2020/02/07
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/02/07
 * @UpdateRemark:
 * @Version: 1.0
 */
public class SubscribeCoursePager extends LiveBasePager {

    private TextView tvTip;
    private ImageView ivSubscribe;
    private ISubscribeClickListener subClickListener;

    public SubscribeCoursePager(Context context) {
        super(context);
        initListener();
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.page_livevideo_lecture_subscribe, null);
        ivSubscribe = mView.findViewById(R.id.iv_livevideo_lecture_subscribe);
        tvTip = mView.findViewById(R.id.tv_livevideo_lecture_tip);
        return mView;
    }


    @Override
    public void initListener() {
        super.initListener();
        ivSubscribe.setOnClickListener(new OnUnDoubleClickListener() {
            @Override
            public void onClick(View v) {
                super.onClick(v);
                subClickListener.onClick();
            }
        });
    }

    public void setSubClickListener(ISubscribeClickListener subClickListener) {
        this.subClickListener = subClickListener;
    }

    /**
     * 设置提示文字
     * @param tip
     */
    public void setTvTip(final String tip) {
        post(new Runnable() {
            @Override
            public void run() {
                if (tip != null) {
                    tvTip.setText(tip);
                }
            }
        });
    }

    /**
     * 设置提示文字是否隐藏
     * @param visible
     */
    public void setTvTipVisible(final boolean visible) {

        if (visible) {
            tvTip.setVisibility(View.VISIBLE);
        } else {
            tvTip.setVisibility(View.GONE);
        }

    }

    /**
     * 设置是否已预约
     * @param subscribe
     */
    public void hasSubscribe(final boolean subscribe) {
        post(new Runnable() {
            @Override
            public void run() {
                if (subscribe) {
                    ivSubscribe.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_livevideo_lecture_hassubscribe));
                }else {
                    ivSubscribe.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_livevideo_lecture_subscribe));
                }
            }
        });
    }

    public void setTvSubscribeEnable(boolean enable) {
        ivSubscribe.setEnabled(enable);
    }


}
