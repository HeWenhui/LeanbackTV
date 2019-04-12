package com.xueersi.parentsmeeting.modules.livevideoOldIJK.message.pager;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.BaseLiveMessagePager;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.ui.widget.ZoomerLayout;

/**
 * 小英献花pager，从LiveMessagePager中抽离出来，单独实现
 */
public class SmallEnglishSendFlowerPager extends BasePager {
    /**
     * 取消按钮
     */
    private ImageView ivArtsCancel;
    //献一朵花的布局(添加监听器用)
    private ZoomerLayout zlOneFlower;
    //献三朵花的布局(添加监听器用)
    private ZoomerLayout zlThreeFlower;
    //献五朵花的布局(添加监听器用)
    private ZoomerLayout zlFiveFlower;
    //选中一朵花时的√
    private ImageView ivOneFlowerTick;
    //选中3朵花时的√
    private ImageView ivThreeFlowerTick;
    //选中5朵花时的√
    private ImageView ivFiveFlowerTick;
    //第一朵花盆
    private ImageView ivOneFlowerSquare;//shellwindow_giveflower_oneflower_square_nor;
    //3朵花盆
    private ImageView ivThreeFlowerSquare;
    //5朵花盆
    private ImageView ivFiveFlowerSquare;
    //送花按钮
    private ImageView ivSendFlower;
    //金币余额
    private TextView tvBalance;
    //金币余额
    private String goldNum;
    //设置发送花的监听器
    private SendFlowerListener sendFlowerListener;
    //关闭献花的监听器
    private CloseFlowerListener closeFlowerListener;
    //是否选中了花,默认选中
    private boolean isSelectFlower = true;
    //选中了第几朵花，默认选中小花
    private int whichFlower = BaseLiveMessagePager.FLOWERS_SMALL;

    public SmallEnglishSendFlowerPager(Context context) {
        super(context);
        initListener();
        initData();
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.pop_livevideo_small_english_sendflower, null);
        ivArtsCancel = view.findViewById(R.id.iv_livevideo_small_english_sendflower_cancel);
        zlOneFlower = view.findViewById(R.id.zl_livevideo_small_english_oneflower);
        zlThreeFlower = view.findViewById(R.id.zl_livevideo_small_english_threeflower);
        zlFiveFlower = view.findViewById(R.id.zl_livevideo_small_english_fiveflower);

        ivOneFlowerTick = view.findViewById(R.id.iv_livevideo_small_english_sendflower_oneflower_tick);
        ivThreeFlowerTick = view.findViewById(R.id.iv_livevideo_small_english_sendflower_threeflower_tick);
        ivFiveFlowerTick = view.findViewById(R.id.iv_livevideo_small_english_sendflower_fiveflower_tick);

        ivOneFlowerSquare = view.findViewById(R.id.iv_livevideo_small_english_sendflower_oneflower_square);
        ivThreeFlowerSquare = view.findViewById(R.id.iv_livevideo_small_english_sendflower_threeflower_square);
        ivFiveFlowerSquare = view.findViewById(R.id.iv_livevideo_small_english_sendflower_fiveflower_square);

        tvBalance = view.findViewById(R.id.tv_livevideo_small_english_sendflower_balance);
        ivSendFlower = view.findViewById(R.id.iv_livevideo_small_english_sendflower_send);
        return view;
    }

    //给这些图标设置监听器，注意因为点击需要变色，所以采用setOnTouchListener()
    @Override
    public void initListener() {
//        ivArtsCancel.setOnTouchListener(cancelTouchListener);

//        注意，这里oneFlower,ThreeFlower,fiveFlower只能同时出现一个被点击
        zlOneFlower.setOnTouchListener(oneFlowerTouchListener);
        zlThreeFlower.setOnTouchListener(threeFlowerTouchListener);
        zlFiveFlower.setOnTouchListener(fiveFlowerTouchListener);

//        ivSendFlower.setOnTouchListener(sendTouchListener);
        ivArtsCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (closeFlowerListener != null) {
                    closeFlowerListener.onTouch();
                }
            }
        });
        ivSendFlower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sendFlowerListener != null) {
                    sendFlowerListener.onTouch();
                }
            }
        });
    }

    //取消按钮的监听器
    private View.OnTouchListener cancelTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            ivArtsCancel.setImageResource(R.drawable.bg_livevideo_small_english_close_btn_click);
            if (event.getAction() == MotionEvent.ACTION_UP) {
                ivArtsCancel.setImageResource(R.drawable.bg_livevideo_small_english_close_btn_nor);
                if (closeFlowerListener != null) {
                    closeFlowerListener.onTouch();
                }
            }
            return false;
        }
    };

    private View.OnTouchListener oneFlowerTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            updateFlowerStatus(true, false, false);
            whichFlower = BaseLiveMessagePager.FLOWERS_SMALL;
            return false;
        }
    };

    private View.OnTouchListener threeFlowerTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            updateFlowerStatus(false, true, false);
            whichFlower = BaseLiveMessagePager.FLOWERS_MIDDLE;
            return false;
        }
    };

    private View.OnTouchListener fiveFlowerTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            whichFlower = BaseLiveMessagePager.FLOWERS_BIG;
            updateFlowerStatus(false, false, true);
            return false;
        }
    };

    private void updateFlowerStatus(boolean one, boolean three, boolean five) {
        updateOneStatus(one);
        updateThreeStatus(three);
        updateFiveStatus(five);
        if (one || three || five) {
            isSelectFlower = true;
        } else {
            isSelectFlower = false;
        }
    }


    //送花按钮的监听器
    private View.OnTouchListener sendTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            ivSendFlower.setImageResource(R.drawable.bg_livevideo_small_english_sendflower_send_btn_click);
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (sendFlowerListener != null) {
                    sendFlowerListener.onTouch();
                }
                ivSendFlower.setImageResource(R.drawable.bg_livevideo_small_english_sendflower_send_btn_nor);
            }
            return false;
        }
    };

    public void setSendFlowerListener(SendFlowerListener sendFlowerListener) {
        this.sendFlowerListener = sendFlowerListener;
    }

    public void setCloseFlowerListener(CloseFlowerListener closeFlowerListener) {
        this.closeFlowerListener = closeFlowerListener;
    }

    private void updateOneStatus(boolean one) {
        if (one) {
            ivOneFlowerSquare.setImageResource(R.drawable.bg_livevideo_small_english_sendflower_square_btn_click);
            ivOneFlowerTick.setVisibility(View.VISIBLE);
        } else {
            ivOneFlowerSquare.setImageResource(R.drawable.bg_livevideo_small_english_sendflower_square_btn_nor);
            ivOneFlowerTick.setVisibility(View.GONE);
        }
    }

    private void updateThreeStatus(boolean three) {
        if (three) {
            ivThreeFlowerSquare.setImageResource(R.drawable.bg_livevideo_small_english_sendflower_square_btn_click);
            ivThreeFlowerTick.setVisibility(View.VISIBLE);
        } else {
            ivThreeFlowerSquare.setImageResource(R.drawable.bg_livevideo_small_english_sendflower_square_btn_nor);
            ivThreeFlowerTick.setVisibility(View.GONE);
        }
    }

    private void updateFiveStatus(boolean five) {
        if (five) {
            ivFiveFlowerSquare.setImageResource(R.drawable.bg_livevideo_small_english_sendflower_square_btn_click);
            ivFiveFlowerTick.setVisibility(View.VISIBLE);
        } else {
            ivFiveFlowerSquare.setImageResource(R.drawable.bg_livevideo_small_english_sendflower_square_btn_nor);
            ivFiveFlowerTick.setVisibility(View.GONE);
        }
    }

    /**
     * 更新剩余金币数量
     */
    public void onGetMyGoldDataEvent(String goldNum) {
        this.goldNum = goldNum;
        tvBalance.setText(goldNum);
    }

    @Override
    public void initData() {

    }

    public RelativeLayout.LayoutParams getCenterInVideoLayoutParams() {
        RelativeLayout.LayoutParams flowerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
                .MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        Drawable drawable = mContext.getResources().getDrawable(R.drawable.shellwindow_sendflower_board);
        LiveVideoPoint liveVideoPoint = LiveVideoPoint.getInstance();

        int width = (liveVideoPoint.x3 - liveVideoPoint.x2 - drawable.getIntrinsicWidth()) / 2;
        flowerParams.leftMargin = width;
        return flowerParams;
    }

    public boolean getIsSelectFlower() {
        return isSelectFlower;
    }

    public int getWhichFlower() {
        return whichFlower;
    }

    //赠送按钮的监听器
    public interface SendFlowerListener {
        void onTouch();
    }

    //关闭按钮的监听器
    public interface CloseFlowerListener {
        void onTouch();
    }
}
