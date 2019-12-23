package com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.pager;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.common.base.BasePager;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.entity.CouponEntity;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveMainHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: xueersiwangxiao
 * @Package: com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.pager
 * @ClassName: DiscountCouponDetailPager
 * @Description: 优惠券页面
 * @Author: WangDe
 * @CreateDate: 2019/11/26 14:32
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/11/26 14:32
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class DiscountCouponDetailPager extends BasePager {

    private RecyclerView rvCouponDetail;
    private ImageView ivClose;
    List<CouponEntity> mData = new ArrayList<>();
    private CouponDetatilAdapter adapter;
    private CloseClickListener listener;
    private Handler mHandler = LiveMainHandler.getMainHandler();

    public DiscountCouponDetailPager(Context context){
        super(context);
        initData();
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.page_lightlive_discount_coupon_detail, null);
        rvCouponDetail = mView.findViewById(R.id.rv_livevideo_lightlive_coupon_data_detail);
        ivClose = mView.findViewById(R.id.iv_livevideo_lightlive_coupon_close);
        rvCouponDetail.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false));
        initListener();
        return mView;
    }

    @Override
    public void initData() {
        adapter = new CouponDetatilAdapter(mData);
        rvCouponDetail.setAdapter(adapter);
    }

    @Override
    public void initListener() {
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick();
            }
        });
    }

    public void updataView (List<CouponEntity> data){
        this.mData = data;
        adapter.setData(mData);

    }

    private class CouponDetatilAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<CouponEntity> mData;
        private GetCouponClickListener listener;

        public CouponDetatilAdapter(List<CouponEntity> mData) {
            this.mData = mData;
        }

        public void setData(List<CouponEntity> mData){
            this.mData = mData;
            notifyDataSetChanged();
        }

        public void setCouponClickListener(GetCouponClickListener couponClickListener){
            listener = couponClickListener;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CouponDetatilHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_livevideo_lightlive_discount_coupon_detail, parent,false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            ((CouponDetatilHolder) holder).bindData(mData.get(position));
            ((CouponDetatilHolder) holder).tvGetCoupon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(String.valueOf(mData.get(position).getId()));
                }
            });
        }

        @Override
        public int getItemCount() {
            return mData == null ? 0 : mData.size();
        }
    }

    private class CouponDetatilHolder extends RecyclerView.ViewHolder {

        TextView tvAmount;
        TextView tvLimit;
        TextView tvName;
        TextView tvDate;
        TextView tvGetCoupon;
        TextView tvHasGot;
        ImageView ivFinish;

        public CouponDetatilHolder(View itemView) {
            super(itemView);
            tvAmount = itemView.findViewById(R.id.tv_livevideo_lightlive_coupon_detail_amount);
            tvLimit = itemView.findViewById(R.id.tv_livevideo_lightlive_coupon_detail_limit);
            tvName = itemView.findViewById(R.id.tv_livevideo_lightlive_coupon_detail_name);
            tvDate = itemView.findViewById(R.id.tv_livevideo_lightlive_coupon_detail_date);
            tvGetCoupon = itemView.findViewById(R.id.tv_livevideo_lightlive_coupon_detail_get);
            tvHasGot = itemView.findViewById(R.id.tv_livevideo_lightlive_coupon_detail_got);
            ivFinish = itemView.findViewById(R.id.tv_livevideo_lightlive_coupon_detail_finish);
        }

        public void bindData(CouponEntity data) {
            String moneyIcon = data.getMoneyIcon();
            String faceText = data.getFaceText();
            if (!TextUtils.isEmpty(faceText)) {
                if (TextUtils.isEmpty(moneyIcon)) {
                    if (faceText.endsWith("折")) {
                        SpannableString cpSpan = new SpannableString(faceText);
                        cpSpan.setSpan(new RelativeSizeSpan(0.43f), faceText.length() - 1, faceText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        tvAmount.setText(cpSpan);
                    } else {
                        tvAmount.setText(faceText);
                    }
                } else {
                    SpannableString cpSpan = new SpannableString(moneyIcon + faceText);
                    cpSpan.setSpan(new RelativeSizeSpan(0.6f), 0, moneyIcon.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tvAmount.setText(cpSpan);
                }
            } else {
                tvAmount.setText("");
            }

            String reduceText = data.getReduceText();
            tvLimit.setText(TextUtils.isEmpty(reduceText) ? "" : reduceText);
            String name = data.getName();
            tvName.setText(TextUtils.isEmpty(name) ? "" : name);
            String validDate = data.getValidDate();
            tvDate.setText(TextUtils.isEmpty(validDate) ? "" : validDate);

            int status = data.getStatus();
            if (status == 2) { // 领取完成
                tvGetCoupon.setVisibility(View.GONE);
                tvHasGot.setVisibility(View.GONE);
                ivFinish.setVisibility(View.VISIBLE);
            } else {
                tvGetCoupon.setVisibility(View.VISIBLE);
                tvHasGot.setVisibility(View.VISIBLE);
                ivFinish.setVisibility(View.GONE);
                String buttonText = data.getButtonText();
                tvGetCoupon.setText(TextUtils.isEmpty(buttonText) ? "" : buttonText);
                String getedText = data.getGetedText();
                tvHasGot.setText(TextUtils.isEmpty(getedText) ? "" : getedText);
            }

            tvGetCoupon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        }
    }

    public void setCloseListener(CloseClickListener listener) {
        this.listener = listener;
    }

    public void setCouponClickListener(GetCouponClickListener couponListener){
        adapter.setCouponClickListener(couponListener);
    }

    public interface CloseClickListener{
        void onClick();
    }

    public interface GetCouponClickListener{
        void onClick(String couponId);
    }
}
