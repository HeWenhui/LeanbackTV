package com.xueersi.parentsmeeting.modules.livevideo.page;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.parentsmeeting.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LecAdvertBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LecAdvertPagerClose;

/**
 * Created by lyqai on 2018/1/15.
 */
public class LecAdvertPager extends BasePager {
    View step1;
    View step2;
    View step3;
    LayoutInflater inflater;
    LecAdvertPagerClose lecAdvertBll;
    ViewGroup group;
    LecAdvertPayPager lecAdvertPayPager;

    public LecAdvertPager(Context context, LecAdvertPagerClose lecAdvertBll) {
        super(context);
        this.lecAdvertBll = lecAdvertBll;
        initData();
    }

    @Override
    public View initView() {
        inflater = LayoutInflater.from(mContext);
        group = (ViewGroup) View.inflate(mContext, R.layout.page_leclive_advert, null);
        step1 = inflater.inflate(R.layout.page_leclive_advert_step1, group, false);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        group.addView(step1, lp);
        return group;
    }

    @Override
    public void initData() {
        step1.findViewById(R.id.iv_livelec_advert_step1_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lecAdvertBll.close();
            }
        });
        step1.findViewById(R.id.tv_livelec_advert_step1_enroll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                step2 = inflater.inflate(R.layout.page_leclive_advert_step2, group, false);
                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                group.addView(step2, lp);
                initViewStep2();
            }
        });
    }

    private void initViewStep2() {
        TextView tv_livelec_advert_step2_title = (TextView) step2.findViewById(R.id.tv_livelec_advert_step2_title);
        lecAdvertPayPager = new LecAdvertPayPager(mContext, "http://www.xueersi.com/", tv_livelec_advert_step2_title);
        ViewGroup group = (ViewGroup) step2.findViewById(R.id.rl_livelec_advert_step2_web);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        group.addView(lecAdvertPayPager.getRootView(), lp);
        step2.findViewById(R.id.iv_livelec_advert_step2_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lecAdvertBll.close();
            }
        });
    }
}
