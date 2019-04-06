package com.xueersi.parentsmeeting.modules.livevideoOldIJK.message.pager;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.BaseLiveMessagePager;
import com.xueersi.parentsmeeting.widget.FangZhengCuYuanTextView;

public class SmallChineseSendGiftPager extends BasePager {
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
    private FangZhengCuYuanTextView tvMoneyValue;
    /** 金币数额 */
    private String goldNum;
    /** 是否选择了 */
    private boolean isSelect;
    /** 选中了哪种礼物 */
    private int which;

    public SmallChineseSendGiftPager(Context context) {
        super(context);
        initListener();
    }

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
                if (giftListaner != null) {
                    giftListaner.close();
                }
            }
        });
        ivSmallGift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                which = BaseLiveMessagePager.FLOWERS_SMALL;
                select(true, false, false);
            }
        });
        ivMiddleGift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                which = BaseLiveMessagePager.FLOWERS_MIDDLE;
                select(false, true, false);
            }
        });
        ivBigGift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                which = BaseLiveMessagePager.FLOWERS_BIG;
                select(false, false, true);
            }
        });
        ivSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (giftListaner != null) {
                    giftListaner.submit();
                }
            }
        });
    }

    /**
     * @param gift1 小礼物是否选中
     * @param gift2 中礼物2是否选中
     * @param gift3 大礼物3是否选中
     */
    private void select(boolean gift1, boolean gift2, boolean gift3) {
        isSelect = true;
        ivSmallGiftSelect.setVisibility(gift1 ? View.VISIBLE : View.GONE);
        ivMiddleGiftSelect.setVisibility(gift2 ? View.VISIBLE : View.GONE);
        ivBigGiftSelect.setVisibility(gift3 ? View.VISIBLE : View.GONE);
    }

    /**
     * 更新剩余金币数量
     *
     * @param gold
     */
    public void onGetMyGoldDataEvent(String gold) {
        this.goldNum = gold;
        tvMoneyValue.setText(gold);
    }

    public interface GiftListaner {
        void close();

        void submit();
    }

    private GiftListaner giftListaner;

    public void setListener(GiftListaner giftListaner) {
        this.giftListaner = giftListaner;
    }

    public int getWhich() {
        return which;
    }

    public boolean isSelect() {
        return isSelect;
    }
}
