package com.xueersi.parentsmeeting.modules.livevideo.page;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.ImageSpan;

import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.log.Loger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.BaseLiveMessagePager;
import com.xueersi.parentsmeeting.modules.livevideo.business.danmaku.LiveDanmakuPro;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.VerticalImageSpan;

import java.io.InputStream;
import java.util.HashMap;

import master.flame.danmaku.danmaku.controller.DrawHandler;
import master.flame.danmaku.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.danmaku.loader.ILoader;
import master.flame.danmaku.danmaku.danmaku.loader.IllegalDataException;
import master.flame.danmaku.danmaku.danmaku.loader.android.DanmakuLoaderFactory;
import master.flame.danmaku.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.danmaku.model.android.BaseCacheStuffer;
import master.flame.danmaku.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.danmaku.model.android.SpannedCacheStuffer;
import master.flame.danmaku.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.danmaku.parser.IDataSource;
import master.flame.danmaku.danmaku.danmaku.parser.android.BiliDanmukuParser;

/**
 * Created by David on 2018/8/12.
 */

public abstract class BasePrimaryScienceMessagePager extends BaseLiveMessagePager {
    //    private int DANMU_RADIUS = 20;//圆角半径
    public int DANMU_PADDING = 0;

    private int DANMU_BACKGROUND_HEIGHT = 45;
    private int BITMAP_WIDTH_ME = 61;//头像的宽度
    private int BITMAP_HEIGHT_ME = 60;//头像的高度

    private int CIRCEL_WIDTH = 40;
    private int CIRCEL_HEIGHT = 40;

    public BasePrimaryScienceMessagePager(Context context) {
        super(context);
        DANMU_PADDING = SizeUtils.Dp2Px(context, DANMU_PADDING);
//        DANMU_RADIUS = SizeUtils.Dp2Px(context, 20);

        DANMU_BACKGROUND_HEIGHT = SizeUtils.Dp2Px(context, DANMU_BACKGROUND_HEIGHT);
        BITMAP_WIDTH_ME = SizeUtils.Dp2Px(context, BITMAP_WIDTH_ME);
        BITMAP_HEIGHT_ME = SizeUtils.Dp2Px(context, BITMAP_HEIGHT_ME);
        CIRCEL_HEIGHT = SizeUtils.Dp2Px(context, CIRCEL_HEIGHT);
        CIRCEL_WIDTH = SizeUtils.Dp2Px(context, CIRCEL_WIDTH);
    }

//    @Override
//    protected void initDanmaku() {
//        if (flowsTips != null) {
//            flowsTips = new String[]{"送老师一颗小心心，老师也喜欢你哟~", "送老师一杯暖心茉莉茶，老师嗓子好舒服~", "送老师一个冰淇淋，夏天好凉爽~"};
//        }
//
//        if (flowsDrawLittleTips != null) {
//            flowsDrawLittleTips = new int[]{R.drawable.primarypresentheart, R.drawable
//                    .primarypresentcup, R.drawable
//                    .primarypresentirc};
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
//                .setCacheStuffer(new BackgroundCacheStuffer(), mCacheStufferAdapter)// 图文混排使用SpannedCacheStuffer
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
//                Loger.i("DFM", "onDanmakuClick text:" + latest.text);
//            }
//
//            @Override
//            public void onDanmakuClick(IDanmakus danmakus) {
//                Loger.i("DFM", "onDanmakuClick danmakus size:" + danmakus.size());
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
//        }
//    };

    @Override
    public void addDanmaKuFlowers(final int ftype, final String name) {
        LiveDanmakuPro liveDanmakuPro = ProxUtil.getProvide(mContext, LiveDanmakuPro.class);
        if (liveDanmakuPro == null) {
            return;
        }
        BaseDanmaku danmaku = liveDanmakuPro.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        if (danmaku == null) {
            return;
        }
        Drawable drawable;
        switch (ftype) {
            case FLOWERS_SMALL:
            case FLOWERS_MIDDLE:
            case FLOWERS_BIG:
                drawable = mContext.getResources().getDrawable(flowsDrawLittleTips[ftype - 2]);
                danmaku.textColor = Color.WHITE;
                break;
            default:
                drawable = mContext.getResources().getDrawable(R.drawable.ic_launcher);
                danmaku.textColor = Color.BLUE;
                break;
        }
        drawable.setBounds(0, 0, BITMAP_HEIGHT_ME, BITMAP_HEIGHT_ME);
        SpannableStringBuilder spannable = createSpannable(ftype, name, drawable);
        danmaku.text = spannable;
        danmaku.padding = DANMU_PADDING;
        danmaku.priority = 1;  // 一定会显示, 一般用于本机发送的弹幕
        danmaku.isLive = false;
        danmaku.textSize = SizeUtils.Sp2Px(mContext, 14);//25f * (mParser.getDisplayer().getDensity() - 0.6f);
        danmaku.textShadowColor = 0; // 重要：如果有图文混排，最好不要设置描边(设textShadowColor=0)，否则会进行两次复杂的绘制导致运行效率降低
//        danmaku.underlineColor = Color.GREEN;

//        dvMessageDanmaku.addDanmaku(danmaku);

        liveDanmakuPro.addDanmaku(danmaku);
    }

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
        String msg = name + ":" + tip;
        SpannableStringBuilder spannable = new TypeSpannableStringBuilder(msg, name, ftype);
        spannable.append(msg).append(msg);
        ImageSpan imgSpan = new VerticalImageSpan(drawable);
        spannable.setSpan(imgSpan, 0, msg.length(), imgSpan.ALIGN_BASELINE);

//

        return spannable;
    }

    /**
     * 绘制背景(自定义弹幕样式)
     */


    private class BackgroundCacheStuffer extends SpannedCacheStuffer {

        // 通过扩展SimpleTextCacheStuffer或SpannedCacheStuffer个性化你的弹幕样式
//        final Paint paint = new Paint();

        @Override
        public void measure(BaseDanmaku danmaku, TextPaint paint, boolean fromWorkerThread) {
//=            danmaku.padding = 20;  // 在背景绘制模式下增加padding
            super.measure(danmaku, paint, fromWorkerThread);
        }

        @Override
        public void drawBackground(BaseDanmaku danmaku, Canvas canvas, float left, float top) {
            Drawable drawable = mContext.getResources().getDrawable(R.drawable
                    .livevideo_psgiftsuccess);
            float height = 0.0f;
            height = drawable.getIntrinsicHeight();
            float offsetRight = 0.0f;
            offsetRight = (BITMAP_HEIGHT_ME - CIRCEL_HEIGHT) / 2;
            drawable.setBounds(
                    (int) (left + danmaku.padding + offsetRight),
                    (int) (top + danmaku.padding + (BITMAP_HEIGHT_ME - height) / 2),
                    (int) (left + danmaku.paintWidth),
                    (int) (top + height + (BITMAP_HEIGHT_ME - height) / 2
                            + danmaku.padding));
            drawable.draw(canvas);
        }

        @Override
        public void drawStroke(BaseDanmaku danmaku, String lineText, Canvas canvas, float left, float top, Paint
                paint) {
            // 禁用描边绘制
        }
    }
}
