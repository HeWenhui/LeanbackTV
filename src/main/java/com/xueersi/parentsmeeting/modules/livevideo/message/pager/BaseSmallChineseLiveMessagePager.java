package com.xueersi.parentsmeeting.modules.livevideo.message.pager;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;

import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.danmaku.LiveDanmakuPro;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.VerticalImageSpan;

import master.flame.danmaku.danmaku.danmaku.model.BaseDanmaku;

/**
 * 小学语文礼物弹幕，同小英三分屏的鲜花弹幕
 */
public abstract class BaseSmallChineseLiveMessagePager extends LiveMessageCommonMessagePager {

    //    private int DANMU_RADIUS = 20;//圆角半径
    public int DANMU_PADDING = 0;//弹幕之间的间距

    //    private int DANMU_BACKGROUND_HEIGHT = 45;
    private int BITMAP_WIDTH_ME = 28;// 弹幕上面实际显示的头像的宽度
    private int BITMAP_HEIGHT_ME = 55;//弹幕上面实际显示的头像的高度

    private int CIRCEL_WIDTH = 40;
    private int CIRCEL_HEIGHT = 40;
    /** 背景Drawalble,弹幕的礼物Drawalble */
    private Drawable backgroundDrawable, flowerDrawable;

    private Drawable[] sendFlowerArray;
    //** 是否是自己的图 */
//    protected boolean self = false;

    public BaseSmallChineseLiveMessagePager(Context context) {
        super(context);
        DANMU_PADDING = SizeUtils.Dp2Px(context, DANMU_PADDING);
//        DANMU_BACKGROUND_HEIGHT = SizeUtils.
//                Dp2Px(context, DANMU_BACKGROUND_HEIGHT);
        BITMAP_WIDTH_ME = SizeUtils.Dp2Px(context, BITMAP_WIDTH_ME);
        BITMAP_HEIGHT_ME = SizeUtils.Dp2Px(context, BITMAP_HEIGHT_ME);
        CIRCEL_HEIGHT = SizeUtils.Dp2Px(context, CIRCEL_HEIGHT);
        CIRCEL_WIDTH = SizeUtils.Dp2Px(context, CIRCEL_WIDTH);

//        backgroundDrawable = mContext.getResources().getDrawable(self ?
//                R.drawable //采用.9的方式来显示
//                .bg_livevideo_small_chinese_gift_danmu_my_background
//                        .bg_livevideo_small_chinese_gift_danmu_my_background_mid
//                : R.drawable //采用.9的方式来显示
//                .bg_livevideo_small_chinese_gift_danmu_other_background);
//                .bg_livevideo_small_chinese_gift_danmu_other_backgroud_mid);
//                .bg_livevideo_small_chinese_gift_danmu_my_background_mid);
//                .bg_livevideo_small_chinese_live_message_danmu_background_small);
    }

    @Override
    public void initData() {
        super.initData();
        if (flowsTips != null) {
            flowsTips = new String[]{"送老师一座自由女神", "送老师一座埃菲尔铁塔", "送老师一座长城"};
        }
        if (flowsDrawLittleTips != null) {
            flowsDrawLittleTips = new int[]{R.drawable.bg_livevideo_small_chinese_danmu_small_gift,
                    R.drawable.bg_livevideo_small_chinese_danmu_middle_gift,
                    R.drawable.bg_livevideo_small_chinese_danmu_big_gift};
            sendFlowerArray = new Drawable[]{
                    mContext.getResources().getDrawable(flowsDrawLittleTips[0]),
                    mContext.getResources().getDrawable(flowsDrawLittleTips[1]),
                    mContext.getResources().getDrawable(flowsDrawLittleTips[2])};
        }
    }

    //    @Override
//    protected void initDanmaku() {
//        if (flowsTips != null) {
//            flowsTips = new String[]{"送老师一座自由女神", "送老师一座埃菲尔铁塔", "送老师一座长城"};
//        }
//
//        if (flowsDrawLittleTips != null) {
//            flowsDrawLittleTips = new int[]{R.drawable.bg_livevideo_small_chinese_danmu_small_gift,
//                    R.drawable.bg_livevideo_small_chinese_danmu_middle_gift,
//                    R.drawable.bg_livevideo_small_chinese_danmu_big_gift};
//            sendFlowerArray = new Drawable[]{
//                    mContext.getResources().getDrawable(flowsDrawLittleTips[0]),
//                    mContext.getResources().getDrawable(flowsDrawLittleTips[1]),
//                    mContext.getResources().getDrawable(flowsDrawLittleTips[2])};
//        }
//
//
//        // 设置最大显示行数
//        HashMap<Integer, Integer> maxLinesPair = new HashMap<>();
//        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 5); // 滚动弹幕最大显示5行
//        // 设置是否禁止重叠
//        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<>();
//        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
//        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);
//        mDanmakuContext = DanmakuContext.create();
//        mDanmakuContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 3).setDuplicateMergingEnabled(false)
//                .setScrollSpeedFactor(1.2f).setScaleTextSize(1.2f)
//                .setCacheStuffer(new BackgroundCacheStuffer(), mCacheStufferAdapter) // 图文混排使用SpannedCacheStuffer
////                .setCacheStuffer(new BackgroundCacheStuffer())  // 绘制背景使用BackgroundCacheStuffer
//                .setMaximumLines(maxLinesPair)
//                .preventOverlapping(overlappingEnablePair);
//        dvMessageDanmaku.setCallback(new DrawHandler.Callback() {
//            @Override
//            public void updateTimer(DanmakuTimer timer) {
//            }
//
//            @Override
//            public void drawingFinished() {
//
//            }
//
//            @Override
//            public void danmakuShown(BaseDanmaku danmaku) {
////                    Log.d("DFM", "danmakuShown(): text=" + danmaku.text);
//            }
//
//            @Override
//            public void prepared() {
//                dvMessageDanmaku.start();
//            }
//        });
//        dvMessageDanmaku.setOnDanmakuClickListener(new IDanmakuView.OnDanmakuClickListener() {
//            @Override
//            public void onDanmakuClick(BaseDanmaku latest) {
//                logger.i("onDanmakuClick text:" + latest.text);
//            }
//
//            @Override
//            public void onDanmakuClick(IDanmakus danmakus) {
//                logger.i("onDanmakuClick danmakus size:" + danmakus.size());
//            }
//        });
//        dvMessageDanmaku.prepare(new BaseDanmakuParser() {
//            @Override
//            protected Danmakus parse() {
//                return new Danmakus();
//            }
//        }, mDanmakuContext);
//        dvMessageDanmaku.showFPS(false);
//        dvMessageDanmaku.enableDanmakuDrawingCache(false);
//    }
//
//    protected BaseCacheStuffer.Proxy mCacheStufferAdapter = new BaseCacheStuffer.Proxy() {
//
//        @Override
//        public void prepareDrawing(final BaseDanmaku danmaku, boolean fromWorkerThread) {
//        }
//
//        @Override
//        public void releaseResource(BaseDanmaku danmaku) {
//            // TODO 重要:清理含有ImageSpan的text中的一些占用内存的资源 例如drawable
//            if (danmaku.text instanceof SpannableStringBuilder) {
//                danmaku.text = "";
//            }
//        }
//    };

//    boolean aBoolean = false;

    //    @Override
    public void addDanmaKuFlowers(final int ftype, final String name, final boolean isSelf) {
        LiveDanmakuPro liveDanmakuPro = ProxUtil.getProvide(mContext, LiveDanmakuPro.class);
        if (liveDanmakuPro == null) {
            return;
        }

        BaseDanmaku danmaku = liveDanmakuPro.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        if (danmaku == null) {
            return;
        }

        switch (ftype) {
            case FLOWERS_SMALL:
            case FLOWERS_MIDDLE:
            case FLOWERS_BIG:
                flowerDrawable = sendFlowerArray[ftype - 2];
                danmaku.textColor = isSelf ? mContext.getResources().getColor(R.color.COLOR_FFD93D) : Color.WHITE;
                break;
            default:
                flowerDrawable = mContext.getResources().getDrawable(R.drawable.ic_launcher);
                danmaku.textColor = Color.BLUE;
                break;
        }

//        aBoolean = !aBoolean;
        flowerDrawable.setBounds(0, 0, BITMAP_WIDTH_ME, BITMAP_HEIGHT_ME);
        SpannableStringBuilder spannable = createSpannable(ftype, name, flowerDrawable);
        danmaku.text = spannable;

        danmaku.isGuest = isSelf;//是不是自己，false代表不是自己，true代表是自己。

        danmaku.padding = DANMU_PADDING;
        danmaku.priority = 1;  // 一定会显示, 一般用于本机发送的弹幕
        danmaku.isLive = false;
        danmaku.textSize = SizeUtils.Sp2Px(mContext, 14);//25f * (mParser.getDisplayer().getDensity() - 0.6f);
        danmaku.textShadowColor = 0; // 重要：如果有图文混排，最好不要设置描边(设textShadowColor=0)，否则会进行两次复杂的绘制导致运行效率降低
//        danmaku.underlineColor = Color.GREEN;

        liveDanmakuPro.addDanmaku(danmaku);
    }

    /** 创建图文混排,即屏幕显示的最终结果 */
    @Override
    protected SpannableStringBuilder createSpannable(int ftype, String name, Drawable drawable) {
        String tip = "";
        switch (ftype) {
            case FLOWERS_SMALL:
            case FLOWERS_MIDDLE:
            case FLOWERS_BIG:
                tip = flowsTips[ftype - 2];
                break;
        }
        String replace = name + ":" + tip;
        //下面两个字符串为了适配图片在弹幕左边圆圈的位置
        String pre = "  ";
        String suffix = "  ";
        String msg = pre + replace + suffix;
        SpannableStringBuilder spannable = new TypeSpannableStringBuilder(msg, name, ftype);
//        int color = mContext.getResources().getColor(R.color.COLOR_FFFFFF);

        spannable.append(replace).append("  ");
//        spannable.setSpan(new BackgroundColorSpan(color), msg.length(), msg.length() + 1, Spannable
//                .SPAN_EXCLUSIVE_EXCLUSIVE);
//        BackgroundColorSpan backgroundColorSpan = new BackgroundColorSpan(R.drawable
//                .bg_livevideo_send_flower_screen_bullet_background);
//        spannable.setSpan(backgroundColorSpan, 0, spannable.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        ImageSpan imgSpan = new VerticalImageSpan(drawable);
        spannable.setSpan(imgSpan, pre.length(), pre.length() + replace.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

//


        return spannable;
    }

}
