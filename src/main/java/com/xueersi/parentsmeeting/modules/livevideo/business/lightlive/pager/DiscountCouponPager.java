package com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.pager;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;

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


    private RecyclerView rvCoupon;
    private ImageView ivMore;
    private List<String> mData;
    private MoreCouponClickListener listener;

    public DiscountCouponPager(Activity activity) {
        super(activity);

    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.page_lightlive_discount_coupon, null);
        rvCoupon = mView.findViewById(R.id.rv_livevideo_lightlive_coupon_data);
        ivMore = mView.findViewById(R.id.iv_livevideo_lightlive_coupon_more);
        rvCoupon.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false));
        mView.setVisibility(View.GONE);
        return mView;
    }

    @Override
    public void initData() {
        rvCoupon.setAdapter(new CouponAdapter(mData));
    }


    public void setData(List<String> data) {
        this.mData = data;
        initData();
        mView.setVisibility(View.VISIBLE);
    }

    private class CouponAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<String> mData;

        public CouponAdapter(List<String> mData) {
            this.mData = mData;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CouponHolder(View.inflate(parent.getContext(), R.layout.item_livevideo_lightlive_coupon_small, null));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((CouponHolder) holder).bindData(mData.get(position));
        }

        @Override
        public int getItemCount() {
            return mData == null ? 0 : mData.size();
        }
    }

    private class CouponHolder extends RecyclerView.ViewHolder {

        TextView tvData;

        public CouponHolder(View itemView) {
            super(itemView);
            tvData = itemView.findViewById(R.id.tv_livevideo_lightlive_coupon_small);
        }

        public void bindData(String data) {
            tvData.setText(data);
        }
    }

    @Override
    public void initListener() {
        ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick();
            }
        });
    }

    public void setMoreClickListener(MoreCouponClickListener listener){
        this.listener = listener;
    }

    public interface MoreCouponClickListener{
        void onClick();
    }
}
