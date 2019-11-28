package com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.pager;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.common.base.BasePager;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.entity.CouponEntity;

import java.util.List;

/**
 * @ProjectName: xueersiwangxiao
 * @Package: com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.pager
 * @ClassName: DiscountCouponPager
 * @Description: 优惠券
 * @Author: WangDe
 * @CreateDate: 2019/11/25 20:07
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/11/25 20:07
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class DiscountCouponPager extends BasePager {


    private LinearLayout llCoupon;
    private ImageView ivMore;
    private List<CouponEntity> mData;
    private MoreCouponClickListener listener;
    private RelativeLayout rlCoupon;

    public DiscountCouponPager(Activity activity) {
        super(activity);

    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.page_lightlive_discount_coupon, null);
        rlCoupon = mView.findViewById(R.id.rl_iv_livevideo_lightlive_coupon);
        llCoupon = mView.findViewById(R.id.ll_livevideo_lightlive_coupon_data);
        ivMore = mView.findViewById(R.id.iv_livevideo_lightlive_coupon_more);
        mView.setVisibility(View.GONE);
        initListener();
        return mView;
    }

    @Override
    public void initData() {
        llCoupon.removeAllViews();
        for (int i = 0; i < mData.size(); i++) {
            if (i > 2) {
                break;
            }
            CouponEntity entity = mData.get(i);
            String tag = entity.getTitle();
            if (TextUtils.isEmpty(tag)) continue;
            TextView textView = new TextView(mContext);
            textView.setBackgroundResource(R.drawable.livevideo_lightlive_coupon_small_bg);
            textView.setTextColor(mContext.getResources().getColor(R.color.COLOR_FF5E50));
            textView.setTextSize(10);
//            textView.setPadding(SizeUtils.Dp2Px(mContext, 6), 0, SizeUtils.Dp2Px(mContext, 6), 0);
            textView.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams
                    .WRAP_CONTENT);
            layoutParams.setMargins(SizeUtils.Dp2Px(mContext, 4), 0, SizeUtils.Dp2Px(mContext, 4), 0);
            textView.setLayoutParams(layoutParams);
            textView.setText(tag);
            llCoupon.addView(textView);
        }
    }


    public void setData(List<CouponEntity> data) {
        this.mData = data;
        if (!data.isEmpty()){
            initData();
            mView.setVisibility(View.VISIBLE);
        }else {
            mView.setVisibility(View.GONE);
        }

    }

    @Override
    public void initListener() {
        rlCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick();
            }
        });
    }

    public void setMoreClickListener(MoreCouponClickListener listener) {
        this.listener = listener;
    }

    public interface MoreCouponClickListener {
        void onClick();
    }
}
