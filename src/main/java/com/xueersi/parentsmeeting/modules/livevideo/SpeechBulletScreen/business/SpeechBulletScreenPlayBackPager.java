package com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.page;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.lib.imageloader.SingleConfig;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.business.SpeechBulletScreenBll;
import com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.business.SpeechBulletScreenHttp;
import com.xueersi.parentsmeeting.modules.livevideo.business.WeakHandler;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;

import java.util.HashMap;
import java.util.Random;

import master.flame.danmaku.danmaku.controller.DrawHandler;
import master.flame.danmaku.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.danmaku.model.android.BaseCacheStuffer;
import master.flame.danmaku.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.danmaku.model.android.SpannedCacheStuffer;
import master.flame.danmaku.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.ui.widget.DanmakuView;

/**
 * 语音弹幕页面
 * Created by Zhang Yuansun on 2018/7/11.
 */

public class SpeechBulletScreenPlayBackPager extends LiveBasePager {

    /** 底部语音识别模块的布局 */
    RelativeLayout rlSpeechbulBottomContent;
    private DanmakuView dvSpeechbulDanmaku;
    protected DanmakuContext mDanmakuContext;
    private BaseDanmakuParser mParser;
    SpeechBulletScreenBll speechBulletScreenBll;

    private SpeechBulletScreenHttp speechBulletScreenHttp;
    public void setSpeechBulletScreenHttp(SpeechBulletScreenHttp speechBulletScreenHttp) {
        this.speechBulletScreenHttp = speechBulletScreenHttp;
    }

    private WeakHandler mWeakHandler = new WeakHandler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            return false;
        }
    });


    public SpeechBulletScreenPlayBackPager(Context context, SpeechBulletScreenBll speechBulletScreenBll) {
        super(context);
        this.speechBulletScreenBll = speechBulletScreenBll;
        initData();
    }

    @Override
    public View initView() {
        Log.d(TAG,"initView()");

        View view = View.inflate(mContext, R.layout.page_livevideo_speech_bullet_screen,null);
        dvSpeechbulDanmaku = view.findViewById(R.id.dv_livevideo_speechbul_danmaku);
        rlSpeechbulBottomContent = view.findViewById(R.id.rl_livevideo_speechbul_bottom_content);
        rlSpeechbulBottomContent.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void initData() {
        Log.d(TAG,"initData()");
        initDanmaku();
    }

    /**
     * ************************************************** 弹 幕 **************************************************
     */

    private static final long ADD_DANMU_TIME = 2000;
    private int   BITMAP_WIDTH_GUEST    = 34;//头像的宽度
    private int   BITMAP_HEIGHT_GUEST   = 34;//头像的高度
    private int   BITMAP_WIDTH_ME       = 42;//头像的宽度
    private int   BITMAP_HEIGHT_ME      = 42;//头像的高度
    private float DANMU_TEXT_SIZE       = 14;//弹幕字体的大小
    private int DANMU_PADDING           = 11;//控制两行弹幕之间的间距
    private int DANMU_RADIUS            = 16;//圆角半径
    private int DANMU_BACKGROUND_HEIGHT = 33;

    /**
     * 对数值进行转换，适配手机，必须在初始化之前，否则有些数据不会起作用
     */
    private void setSize(Context context) {
        BITMAP_WIDTH_GUEST = SizeUtils.Dp2Px(context, BITMAP_WIDTH_GUEST);
        BITMAP_HEIGHT_GUEST = SizeUtils.Dp2Px(context, BITMAP_HEIGHT_GUEST);
        BITMAP_WIDTH_ME = SizeUtils.Dp2Px(context, BITMAP_WIDTH_ME);
        BITMAP_HEIGHT_ME = SizeUtils.Dp2Px(context, BITMAP_HEIGHT_ME);
        DANMU_PADDING = SizeUtils.Dp2Px(context, DANMU_PADDING);
        DANMU_RADIUS = SizeUtils.Dp2Px(context, DANMU_RADIUS);
        DANMU_TEXT_SIZE = SizeUtils.Dp2Px(context, DANMU_TEXT_SIZE);
        DANMU_BACKGROUND_HEIGHT = SizeUtils.Dp2Px(context, DANMU_BACKGROUND_HEIGHT);
    }

    protected void initDanmaku() {
        Log.d(TAG,"initDanmaku()");
        setSize(mContext);
        // 设置最大显示行数
        HashMap<Integer, Integer> maxLinesPair = new HashMap<>();
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 3); // 滚动弹幕最大显示3行
        // 设置是否禁止重叠
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);
        mDanmakuContext = DanmakuContext.create();
        mDanmakuContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 3)
                .setDuplicateMergingEnabled(false)
                .setScrollSpeedFactor(1.2f) // 越大速度越慢
                .setScaleTextSize(1.2f)
                .setCacheStuffer(new BackgroundCacheStuffer(), mCacheStufferAdapter) // 图文混排使用BaseCacheStuffer,绘制背景使用BackgroundCacheStuffer
                .setMaximumLines(maxLinesPair)
                .preventOverlapping(overlappingEnablePair);
        dvSpeechbulDanmaku.setCallback(new DrawHandler.Callback() {
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
                dvSpeechbulDanmaku.start();
 //               generateSomeDanmaku();
            }
        });

        dvSpeechbulDanmaku.prepare(new BaseDanmakuParser() {
            @Override
            protected Danmakus parse() {
                return new Danmakus();
            }
        }, mDanmakuContext);
        dvSpeechbulDanmaku.showFPS(false);
        dvSpeechbulDanmaku.enableDanmakuDrawingCache(false);
    }

    /**
     * 随机生成一些弹幕内容以供测试
     */
    private void generateSomeDanmaku() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    final int time = new Random().nextInt(300);
                    String content = "" + time + time;
                    mWeakHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            addDanmaKuFlowers(time + "",time + "","http://xesfile.xesimg.com/user/h/def10002.png" ,true);
                        }
                    });
                    try {
                        Thread.sleep(800);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
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
            paint.setAlpha((int)(255*0.6)); //  透明度0.6
            if (danmaku.isGuest) {
                canvas.drawRoundRect(new RectF(left + danmaku.padding, top + danmaku.padding + (BITMAP_HEIGHT_GUEST - DANMU_BACKGROUND_HEIGHT)/2 + 1
                                , left + danmaku.paintWidth - danmaku.padding ,
                                top + DANMU_BACKGROUND_HEIGHT + (BITMAP_HEIGHT_GUEST - DANMU_BACKGROUND_HEIGHT)/2 + 1 + danmaku.padding),
                        DANMU_RADIUS, DANMU_RADIUS, paint);
            }
            else {
                canvas.drawRoundRect(new RectF(left + danmaku.padding, top + danmaku.padding + (BITMAP_HEIGHT_ME - DANMU_BACKGROUND_HEIGHT)/2 + 1
                                , left + danmaku.paintWidth - danmaku.padding ,
                                top + DANMU_BACKGROUND_HEIGHT + (BITMAP_HEIGHT_ME - DANMU_BACKGROUND_HEIGHT)/2 + 1 + danmaku.padding),
                        DANMU_RADIUS, DANMU_RADIUS, paint);
            }
        }

        @Override
        public void drawStroke(BaseDanmaku danmaku, String lineText, Canvas canvas, float left, float top, Paint paint) {
            // 禁用描边绘制
        }
    }

    private BaseCacheStuffer.Proxy mCacheStufferAdapter = new BaseCacheStuffer.Proxy() {

        @Override
        public void prepareDrawing(final BaseDanmaku danmaku, boolean fromWorkerThread) {
//            if (danmaku.text instanceof Spanned) { // 根据你的条件检查是否需要需要更新弹幕
//            }
        }

        @Override
        public void releaseResource(BaseDanmaku danmaku) {
            // TODO 重要:清理含有ImageSpan的text中的一些占用内存的资源 例如drawable
            if (danmaku.text instanceof Spanned) {
                danmaku.text = "";
            }
        }
    };

    public void addDanmaKuFlowers(final String name, final String msg, final String headImgUrl , final boolean isGuest) {
        if (mDanmakuContext == null || !dvSpeechbulDanmaku.isPrepared()) {
            mWeakHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    addDanmaKuFlowers(name, msg, headImgUrl, isGuest);
                }
            }, 100);
            return;
        }
        final BaseDanmaku danmaku = mDanmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        if (danmaku == null || dvSpeechbulDanmaku == null) {
            return;
        }
        danmaku.isGuest = isGuest;
        ImageLoader.with(mContext).load(headImgUrl).asCircle().asBitmap(new SingleConfig.BitmapListener() {
            @Override
            public void onSuccess(Drawable drawable) {
                Drawable circleDrawable = drawable;
                if (isGuest) {
                    circleDrawable.setBounds(0, 0, BITMAP_WIDTH_GUEST, BITMAP_WIDTH_GUEST);
                    danmaku.textColor = Color.WHITE;
                    danmaku.priority = 0;
                    danmaku.padding = DANMU_PADDING;
                } else {
                    circleDrawable.setBounds(0, 0, BITMAP_WIDTH_ME, BITMAP_WIDTH_ME);
                    danmaku.textColor = Color.YELLOW;
                    danmaku.priority = 0;  // 1:一定会显示, 一般用于本机发送的弹幕,但会导致限制行数和禁止堆叠失效
                    danmaku.padding = DANMU_PADDING - (BITMAP_HEIGHT_ME - DANMU_BACKGROUND_HEIGHT) / 2;
                }
                SpannableStringBuilder spannable = createSpannable(name, msg, circleDrawable, isGuest);
                danmaku.text = spannable;
                danmaku.isLive = false;
                danmaku.time = dvSpeechbulDanmaku.getCurrentTime() + 1200;
                danmaku.textSize = SizeUtils.Dp2Px(mContext, 14f);
                danmaku.textShadowColor = 0; // 重要：如果有图文混排，最好不要设置描边(设textShadowColor=0)，否则会进行两次复杂的绘制导致运行效率降低
                dvSpeechbulDanmaku.addDanmaku(danmaku);
            }

            @Override
            public void onFail() {

            }
        });
    }

    protected SpannableStringBuilder createSpannable(String name, String msg, Drawable drawable, boolean isGuest) {
//        logger.i( "createSpannable:name=" + name + ",ftype=" + ftype);

        String text = " " + name + " : " + msg + "  ";
        SpannableStringBuilder spannable = new SpannableStringBuilder(text);
        spannable.append(text);
        ImageSpan span = new VerticalImageSpan(drawable, isGuest);
        spannable.setSpan(span, 0, text.length() , Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//        spannableStringBuilder.setSpan(new BackgroundColorSpan(Color.parseColor("#8A2233B1")), 0, spannableStringBuilder.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return spannable;
    }

    public static class TypeSpannableStringBuilder extends SpannableStringBuilder {
        public int ftype;
        public String name;

        public TypeSpannableStringBuilder(CharSequence text, String name, int ftype) {
            super(text);
            this.name = name;
            this.ftype = ftype;
        }
    }

    /**
     * VerticalImageSpan：Image和文字拼接时竖直方向居中
     */
    public class VerticalImageSpan extends ImageSpan {
        boolean isGuset;
        public VerticalImageSpan(Drawable drawable) {
            super(drawable);
        }

        public VerticalImageSpan(Drawable drawable, boolean isGuset) {
            super(drawable);
            this.isGuset = isGuset;
        }

        /**
         * update the text line height
         */
        @Override
        public int getSize(Paint paint, CharSequence text, int start, int end,
                           Paint.FontMetricsInt fontMetricsInt) {
            Drawable drawable = getDrawable();
            Rect rect = drawable.getBounds();
            if (fontMetricsInt != null) {
                Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
                int fontHeight = fmPaint.descent - fmPaint.ascent;
                int drHeight = rect.bottom - rect.top;
                int centerY = fmPaint.ascent + fontHeight / 2;

                fontMetricsInt.ascent = centerY - drHeight / 2;
                fontMetricsInt.top = fontMetricsInt.ascent;
                fontMetricsInt.bottom = centerY + drHeight / 2;
                fontMetricsInt.descent = fontMetricsInt.bottom;
            }
            return rect.right;
        }

        /**
         * see detail message in android.text.TextLine
         *
         * @param canvas the canvas, can be null if not rendering
         * @param text the text to be draw
         * @param start the text start position
         * @param end the text end position
         * @param x the edge of the replacement closest to the leading margin
         * @param top the top of the line
         * @param y the baseline
         * @param bottom the bottom of the line
         * @param paint the work paint
         */
        @Override
        public void draw(Canvas canvas, CharSequence text, int start, int end,
                         float x, int top, int y, int bottom, Paint paint) {

            Drawable drawable = getDrawable();
            canvas.save();
            Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
            int fontHeight = fmPaint.descent - fmPaint.ascent;
            int centerY = y + fmPaint.descent - fontHeight / 2;
            int transY = centerY - (drawable.getBounds().bottom - drawable.getBounds().top) / 2;
            canvas.translate(x, transY);
            drawable.draw(canvas);
            canvas.restore();

            if (!isGuset) {
                Paint circlePaint = new Paint();
                circlePaint.setAntiAlias(true);
                circlePaint.setStyle(Paint.Style.STROKE);
                circlePaint.setColor(Color.YELLOW);
                circlePaint.setStrokeWidth(SizeUtils.Dp2Px(mContext,1));
                canvas.drawCircle(x+BITMAP_WIDTH_ME/2,transY+BITMAP_WIDTH_ME/2,BITMAP_WIDTH_ME/2,circlePaint);
            }
        }

    }

    public void  pauseDanmaku(){
        if (dvSpeechbulDanmaku!=null) {
            dvSpeechbulDanmaku.pause();
        }
    }

    public void  resumeDanmaku(){
        if (dvSpeechbulDanmaku!=null) {
            dvSpeechbulDanmaku.resume();
        }
    }

    public void  setDanmakuSpeed(float speed){
        if (mDanmakuContext!=null) {
            mDanmakuContext.setScrollSpeedFactor(1.2f/speed);
        }
    }

    public void onDestroy(){
        Log.i(TAG,"onDestroy()");
    }
}
