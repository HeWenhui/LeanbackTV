package com.xueersi.parentsmeeting.modules.livevideo.message.pager;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;

public class SmallChineseSendGift extends BasePager {
    /** 关闭按钮 */
    private ImageView ivClose;
    /** 提交按钮 */
    private ImageView ivSubmit;
    /** 小礼物 */
    private ImageView ivSmallGift;
    /** 小礼物选中 */
    private ImageView ivSmallGiftSelect;
    /** 中等礼物 */
    private ImageView ivMiddleGift;
    /** 中等礼物选中按钮 */
    private ImageView ivMiddleGiftSelect;
    /** 大礼物 */
    private ImageView ivBigGift;
    /** 大礼物选中按钮 */
    private ImageView ivBigGiftSelect;
    /** 金钱剩余数量 */
    private TextView tvMoneyValue;

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_small_chinese_send_gift, null);
        ivClose = view.findViewById(R.id.iv_livevideo_small_chinese_send_gift_close);
        ivSubmit = view.findViewById(R.id.iv_livevideo_small_chinese_send_gift_send);
        ivSmallGiftSelect = view.findViewById(R.id.iv_livevideo_small_chinese_send_gift_small_select);
        ivMiddleGiftSelect = view.findViewById(R.id.iv_livevideo_small_chinese_send_gift_middle_select);
        ivBigGiftSelect = view.findViewById(R.id.iv_livevideo_small_chinese_send_gift_big_select);
        ivSmallGift = view.findViewById(R.id.iv_livevideo_small_chinese_send_gift_small_gift);
        ivMiddleGift = view.findViewById(R.id.iv_livevideo_small_chinese_send_gift_middle_gift);
        ivBigGift = view.findViewById(R.id.iv_livevideo_small_chinese_send_gift_big_gift);
        tvMoneyValue = view.findViewById(R.id.tv_livevideo_small_chinese_send_gift_money_value);
        return view;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {
        super.initListener();
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        ivSmallGift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select(true, false, false);
            }
        });
        ivMiddleGift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select(false, true, false);
            }
        });
        ivBigGift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select(false, false, true);
            }
        });
        ivSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    /**
     * 更新剩余金币数量
     *
     * @param sum
     */
    public void updateSum(int sum) {
        tvMoneyValue.setText(String.valueOf(sum) + "金币");
    }

    /**
     * @param gift1 小礼物是否选中
     * @param gift2 中礼物2是否选中
     * @param gift3 大礼物3是否选中
     */
    private void select(boolean gift1, boolean gift2, boolean gift3) {
        ivSmallGiftSelect.setVisibility(gift1 ? View.VISIBLE : View.GONE);
        ivMiddleGiftSelect.setVisibility(gift2 ? View.VISIBLE : View.GONE);
        ivBigGiftSelect.setVisibility(gift3 ? View.VISIBLE : View.GONE);
    }


}
