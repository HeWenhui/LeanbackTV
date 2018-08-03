package com.xueersi.parentsmeeting.modules.livevideo.message.pager;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.ImageSpan;

import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.BaseLiveMessagePager;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;
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

public abstract class BaseSmallEnglishLiveMessagePager extends BaseLiveMessagePager {
    private BaseDanmakuParser mParser;
    private int DANMU_RADIUS;//圆角半径
    public int DANMU_PADDING;

    public BaseSmallEnglishLiveMessagePager(Context context) {
        super(context);
        DANMU_PADDING = SizeUtils.Dp2Px(context, 13);
        DANMU_RADIUS = SizeUtils.Dp2Px(context, 20);
    }

    @Override
    protected void initDanmaku() {
        if (flowsTips != null) {
            flowsTips = new String[]{"送老师一朵太阳花", "送老师一束太阳花", "送老师一捧太阳花"};
        }

        if (flowsDrawLittleTips != null) {
            flowsDrawLittleTips = new int[]{R.drawable.bg_livevideo_small_english_sendflower_oneflower_img, R.drawable
                    .bg_livevideo_small_english_sendflower_threeflowers_img, R.drawable
                    .bg_livevideo_small_english_sendflower_fiveflowers_img};
        }


        // 设置最大显示行数
        HashMap<Integer, Integer> maxLinesPair = new HashMap<>();
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 5); // 滚动弹幕最大显示5行
        // 设置是否禁止重叠
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);
        mDanmakuContext = DanmakuContext.create();
        mDanmakuContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 3).setDuplicateMergingEnabled(false)
                .setScrollSpeedFactor(1.2f).setScaleTextSize(1.2f)
                .setCacheStuffer(new BackgroundCacheStuffer(), mCacheStufferAdapter) // 图文混排使用SpannedCacheStuffer
//                .setCacheStuffer(new BackgroundCacheStuffer())  // 绘制背景使用BackgroundCacheStuffer
                .setMaximumLines(maxLinesPair)
                .preventOverlapping(overlappingEnablePair);
        mParser = createParser(mContext.getResources().openRawResource(R.raw.comments));
        dvMessageDanmaku.setCallback(new DrawHandler.Callback() {
            @Override
            public void updateTimer(DanmakuTimer timer) {
            }

            @Override
            public void drawingFinished() {

            }

            @Override
            public void danmakuShown(BaseDanmaku danmaku) {
//                    Log.d("DFM", "danmakuShown(): text=" + danmaku.text);
            }

            @Override
            public void prepared() {
                dvMessageDanmaku.start();
            }
        });
        dvMessageDanmaku.setOnDanmakuClickListener(new IDanmakuView.OnDanmakuClickListener() {
            @Override
            public void onDanmakuClick(BaseDanmaku latest) {
                Loger.i("DFM", "onDanmakuClick text:" + latest.text);
            }

            @Override
            public void onDanmakuClick(IDanmakus danmakus) {
                Loger.i("DFM", "onDanmakuClick danmakus size:" + danmakus.size());
            }
        });
        dvMessageDanmaku.prepare(new BaseDanmakuParser() {
            @Override
            protected Danmakus parse() {
                return new Danmakus();
            }
        }, mDanmakuContext);
        dvMessageDanmaku.showFPS(false);
        dvMessageDanmaku.enableDanmakuDrawingCache(true);
    }

    protected BaseCacheStuffer.Proxy mCacheStufferAdapter = new BaseCacheStuffer.Proxy() {

        @Override
        public void prepareDrawing(final BaseDanmaku danmaku, boolean fromWorkerThread) {
//            if (danmaku.text instanceof Spanned) { // 根据你的条件检查是否需要需要更新弹幕
//                // FIXME 这里只是简单启个线程来加载远程url图片，请使用你自己的异步线程池，最好加上你的缓存池
//                liveThreadPoolExecutor.execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        Drawable drawable;
//                        if (danmaku.text instanceof TypeSpannableStringBuilder) {
//                            TypeSpannableStringBuilder spannableStringBuilder = (TypeSpannableStringBuilder) danmaku
//                                    .text;
////                            Loger.i(TAG, "prepareDrawing:ftype=" + spannableStringBuilder.ftype);
//                            switch (spannableStringBuilder.ftype) {
//                                case FLOWERS_SMALL:
//                                case FLOWERS_MIDDLE:
//                                case FLOWERS_BIG:
//                                    drawable = mContext.getResources().getDrawable
//                                            (flowsDrawLittleTips[spannableStringBuilder.ftype - 2]);
//                                    break;
//                                default:
//                                    drawable = mContext.getResources().getDrawable(R.drawable.ic_app_xueersi_desktop);
//                                    break;
//                            }
//                        } else {
//                            drawable = mContext.getResources().getDrawable(R.drawable.ic_app_xueersi_desktop);
//                        }
//                        if (drawable != null) {
//                            drawable.setBounds(0, 0, SizeUtils.Dp2Px(mContext, 60), SizeUtils.Dp2Px(mContext, 60));
//                            SpannableStringBuilder spannable;
//                            if (danmaku.text instanceof TypeSpannableStringBuilder) {
//                                TypeSpannableStringBuilder typeSpannableStringBuilder = (TypeSpannableStringBuilder)
//                                        danmaku.text;
//                                spannable = createSpannable(typeSpannableStringBuilder.ftype,
//                                        typeSpannableStringBuilder.name, drawable);
//                            } else {
//                                String msg = danmaku.text.toString();
//                                if (msg.length() > 0) {
//                                    msg = msg.substring(0, msg.length() / 2);
//                                }
//                                spannable = createSpannable(1, msg, drawable);
//                            }
//                            danmaku.text = spannable;
//                            if (dvMessageDanmaku != null) {
//                                dvMessageDanmaku.invalidateDanmaku(danmaku, false);
//                            }
//                        }
//                    }
//                });
//            }
        }

        @Override
        public void releaseResource(BaseDanmaku danmaku) {
            // TODO 重要:清理含有ImageSpan的text中的一些占用内存的资源 例如drawable
        }
    };

    private BaseDanmakuParser createParser(InputStream stream) {

        if (stream == null) {
            return new BaseDanmakuParser() {

                @Override
                protected Danmakus parse() {
                    return new Danmakus();
                }
            };
        }

        ILoader loader = DanmakuLoaderFactory.create(DanmakuLoaderFactory.TAG_BILI);

        try {
            loader.load(stream);
        } catch (IllegalDataException e) {
            e.printStackTrace();
        }
        BaseDanmakuParser parser = new BiliDanmukuParser();
        IDataSource<?> dataSource = loader.getDataSource();
        parser.load(dataSource);
        return parser;
    }

    @Override
    public void addDanmaKuFlowers(final int ftype, final String name) {
        if (mDanmakuContext == null) {
            mView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    addDanmaKuFlowers(ftype, name);
                }
            }, 20);
            return;
        }

        BaseDanmaku danmaku = mDanmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        if (danmaku == null || dvMessageDanmaku == null) {
            return;
        }
//        int[] smallEnglishFlowers = new int[]{R.drawable.bg_livevideo_small_english_sendflower_oneflower_img, R
//                .drawable.bg_livevideo_small_english_sendflower_threeflowers_img, R.drawable
//                .bg_livevideo_small_english_sendflower_fiveflowers_img};
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
        drawable.setBounds(0, 0, SizeUtils.Dp2Px(mContext, 60), SizeUtils.Dp2Px(mContext, 60));
        SpannableStringBuilder spannable = createSpannable(ftype, name, drawable);
        danmaku.text = spannable;
        danmaku.padding = SizeUtils.Dp2Px(mContext, 13);
        danmaku.priority = 1;  // 一定会显示, 一般用于本机发送的弹幕
        danmaku.isLive = false;
        danmaku.time = dvMessageDanmaku.getCurrentTime() + 1200;
        danmaku.textSize = SizeUtils.Sp2Px(mContext, 14);//25f * (mParser.getDisplayer().getDensity() - 0.6f);
        danmaku.textShadowColor = 0; // 重要：如果有图文混排，最好不要设置描边(设textShadowColor=0)，否则会进行两次复杂的绘制导致运行效率降低
//        danmaku.underlineColor = Color.GREEN;

        dvMessageDanmaku.addDanmaku(danmaku);
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
//        int color = mContext.getResources().getColor(R.color.COLOR_FFFFFF);

        spannable.append(msg);
//        spannable.setSpan(new BackgroundColorSpan(color), msg.length(), msg.length() + 1, Spannable
//                .SPAN_EXCLUSIVE_EXCLUSIVE);
        ImageSpan span = new VerticalImageSpan(drawable);
        spannable.setSpan(span, 0, msg.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        return spannable;
    }

    /**
     * 绘制背景(自定义弹幕样式)
     */


    private class BackgroundCacheStuffer extends SpannedCacheStuffer {

        // 通过扩展SimpleTextCacheStuffer或SpannedCacheStuffer个性化你的弹幕样式
        final Paint paint = new Paint();

        @Override
        public void measure(BaseDanmaku danmaku, TextPaint paint, boolean fromWorkerThread) {
//=            danmaku.padding = 20;  // 在背景绘制模式下增加padding
            super.measure(danmaku, paint, fromWorkerThread);
        }

        @Override
        public void drawBackground(BaseDanmaku danmaku, Canvas canvas, float left, float top) {
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setAlpha((int) (255 * 0.6)); //  透明度0.6

            int height = SizeUtils.Dp2Px(mContext, 33);
            //由于该库并没有提供margin的设置，所以我这边试出这种方法：将danmaku.padding也就是内间距设置大一点，并在这里的RectF中设置绘制弹幕背景的位置，就可以形成类似margin的效果
            canvas.drawRoundRect(new RectF(left + DANMU_PADDING, top + DANMU_PADDING
                            , left + danmaku.paintWidth - DANMU_PADDING,
                            top + height + DANMU_PADDING),
                    DANMU_RADIUS, DANMU_RADIUS, paint);
        }

        @Override
        public void drawStroke(BaseDanmaku danmaku, String lineText, Canvas canvas, float left, float top, Paint
                paint) {
            // 禁用描边绘制
        }
    }

}