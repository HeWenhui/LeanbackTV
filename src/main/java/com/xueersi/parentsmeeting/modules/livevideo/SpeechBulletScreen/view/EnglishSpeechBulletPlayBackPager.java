package com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ImageSpan;
import android.view.View;

import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.lib.imageloader.SingleConfig;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.Contract.SpeechbulletPlayBackView;
import com.xueersi.parentsmeeting.modules.livevideo.business.WeakHandler;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.widget.VerticalImageSpan;

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
 * 英语回放弹幕页面
 * Created by Zhang Yuansun on 2019/5/22.
 */

public class EnglishSpeechBulletPlayBackPager extends LiveBasePager implements SpeechbulletPlayBackView {
    private DanmakuView mDanmakuView;
    private DanmakuContext mDanmakuContext;
    private boolean isSmallEnglish;
    private WeakHandler mWeakHandler = new WeakHandler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            return false;
        }
    });

    public EnglishSpeechBulletPlayBackPager(Context context, boolean isSmallEnglish) {
        super(context);
        this.isSmallEnglish = isSmallEnglish;
        initData();
    }

    @Override
    public View initView() {
        return initDanmaku();
    }

    @Override
    public void initData() {
        transformSize(mContext);
        if (!isSmallEnglish) {
            DANMU_TEXT_COLOR = "#D9953D";
        }
    }

    /**
     * ************************************************** 弹 幕 **************************************************
     */

    private static final long ADD_DANMU_TIME = 1200;
    private int BITMAP_WIDTH_GUEST = 34;//别人头像的宽度
    private int BITMAP_HEIGHT_GUEST = 34;//别人头像的高度
    private int BITMAP_WIDTH_ME = 34;//自己头像的宽度
    private int BITMAP_HEIGHT_ME = 34;//自己头像的高度
    private float DANMU_TEXT_SIZE = 14;//弹幕字体的大小
    private int DANMU_PADDING = 5;//控制两行弹幕之间的间距
    private int DANMU_RADIUS = 16;//圆角半径
    private int DANMU_BACKGROUND_HEIGHT = 33;
    private String DANMU_TEXT_COLOR = "#72DAFB";
    private Drawable DANMU_BACKGROUND = mContext.getResources().getDrawable(R.drawable
            .bg_livevideo_send_flower_screen_bullet_background);

    /**
     * 初始化弹幕
     */
    private View initDanmaku() {
        // 设置最大显示行数
        HashMap<Integer, Integer> maxLinesPair = new HashMap<>();
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 3); // 滚动弹幕最大显示3行
        // 设置是否禁止重叠
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);
        mDanmakuView = new DanmakuView(mContext);
        mDanmakuView.setId(R.id.dv_livevideo_bullet_playback);
        mDanmakuContext = DanmakuContext.create();
        mDanmakuContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 3)
                .setDuplicateMergingEnabled(false)
                .setScrollSpeedFactor(1.2f) // 越大速度越慢
                .setScaleTextSize(1.2f)
                .setCacheStuffer(new BackgroundCacheStuffer(), mCacheStufferAdapter) // 图文混排使用BaseCacheStuffer,
                // 绘制背景使用BackgroundCacheStuffer
                .setMaximumLines(maxLinesPair)
                .preventOverlapping(overlappingEnablePair);
        mDanmakuView.setCallback(new DrawHandler.Callback() {
            @Override
            public void updateTimer(DanmakuTimer timer) {
            }

            @Override
            public void drawingFinished() {

            }

            @Override
            public void danmakuShown(BaseDanmaku danmaku) {

            }

            @Override
            public void prepared() {
                mDanmakuView.start();
                // generateSomeDanmaku();
            }
        });

        mDanmakuView.prepare(new BaseDanmakuParser() {
            @Override
            protected Danmakus parse() {
                return new Danmakus();
            }
        }, mDanmakuContext);
        mDanmakuView.showFPS(false);
        mDanmakuView.enableDanmakuDrawingCache(false);
        return mDanmakuView;
    }

    /**
     * 随机生成一些弹幕内容以供测试
     */
    private void generateSomeDanmaku() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    final int time = new Random().nextInt(300);
                    String content = "" + time + time;
                    mWeakHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            addDanmaku(time + "", time + "", "", true);
                        }
                    });
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * dp转成sp
     */
    private void transformSize(Context context) {
        BITMAP_WIDTH_GUEST = SizeUtils.Dp2Px(context, BITMAP_WIDTH_GUEST);
        BITMAP_HEIGHT_GUEST = SizeUtils.Dp2Px(context, BITMAP_HEIGHT_GUEST);
        BITMAP_WIDTH_ME = SizeUtils.Dp2Px(context, BITMAP_WIDTH_ME);
        BITMAP_HEIGHT_ME = SizeUtils.Dp2Px(context, BITMAP_HEIGHT_ME);
        DANMU_PADDING = SizeUtils.Dp2Px(context, DANMU_PADDING);
        DANMU_RADIUS = SizeUtils.Dp2Px(context, DANMU_RADIUS);
        DANMU_TEXT_SIZE = SizeUtils.Dp2Px(context, DANMU_TEXT_SIZE);
        DANMU_BACKGROUND_HEIGHT = SizeUtils.Dp2Px(context, DANMU_BACKGROUND_HEIGHT);
    }

    /**
     * 绘制背景(自定义弹幕样式)
     */
    private class BackgroundCacheStuffer extends SpannedCacheStuffer {
        // 通过扩展SimpleTextCacheStuffer或SpannedCacheStuffer实现自定义弹幕样式
        final Paint paint = new Paint();

        @Override
        public void measure(BaseDanmaku danmaku, TextPaint paint, boolean fromWorkerThread) {
            super.measure(danmaku, paint, fromWorkerThread);
        }

        @Override
        public void drawBackground(BaseDanmaku danmaku, Canvas canvas, float left, float top) {
            paint.setAntiAlias(true);
            float height = DANMU_BACKGROUND.getIntrinsicHeight();
            float offsetRight = (BITMAP_HEIGHT_ME - height) / 2;
            DANMU_BACKGROUND.setBounds(
                    (int) (left + danmaku.padding + offsetRight),
                    (int) (top + danmaku.padding + offsetRight),
                    (int) (left + danmaku.paintWidth),
                    (int) (top + height + offsetRight + danmaku.padding));
            DANMU_BACKGROUND.draw(canvas);
        }

        @Override
        public void drawStroke(BaseDanmaku danmaku, String lineText, Canvas canvas, float left, float top, Paint
                paint) {
            // 禁用描边绘制
        }
    }

    private BaseCacheStuffer.Proxy mCacheStufferAdapter = new BaseCacheStuffer.Proxy() {

        @Override
        public void prepareDrawing(final BaseDanmaku danmaku, boolean fromWorkerThread) {
            // 根据你的条件检查是否需要需要更新弹幕
//            if (danmaku.text instanceof Spanned) {
//            }
        }

        @Override
        public void releaseResource(BaseDanmaku danmaku) {
            // 清理含有ImageSpan的text中的一些占用内存的资源 例如drawable
            if (danmaku.text instanceof Spanned) {
                danmaku.text = "";
            }
        }
    };

    public void addDanmaku(final String name, final String msg, final String headImgUrl, final boolean isGuest) {
        if (mDanmakuContext == null || mDanmakuView == null || !mDanmakuView.isPrepared()) {
            mWeakHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    addDanmaku(name, msg, headImgUrl, isGuest);
                }
            }, 100);
            return;
        }
        //如果长时间没有弹幕，可能会休眠
        if (mDanmakuView.isPaused()) {
            mDanmakuView.resume();
        }
        final BaseDanmaku danmaku = mDanmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        if (danmaku == null) {
            return;
        }
        if (isGuest) {
            danmaku.priority = 0;
            danmaku.textColor = Color.WHITE;
        } else {
            mDanmakuView.pause();
            mDanmakuView.resume();
            danmaku.priority = 0;  // 1:一定会显示, 一般用于本机发送的弹幕。但是会导致限制行数和禁止堆叠失效
            danmaku.textColor = Color.parseColor(DANMU_TEXT_COLOR);
        }
        danmaku.isGuest = isGuest;
        danmaku.isLive = true;
        danmaku.padding = DANMU_PADDING;
        danmaku.time = mDanmakuView.getCurrentTime() + ADD_DANMU_TIME;
        danmaku.textSize = DANMU_TEXT_SIZE;
        danmaku.textShadowColor = 0; // 如果有图文混排，最好不要设置描边(设textShadowColor=0)，否则会进行两次复杂的绘制导致运行效率降低
        ImageLoader.with(mContext).load(headImgUrl).asCircle().asBitmap(new SingleConfig.BitmapListener() {
            @Override
            public void onSuccess(Drawable drawable) {
                if (isGuest) {
                    drawable.setBounds(0, 0, BITMAP_WIDTH_GUEST, BITMAP_WIDTH_GUEST);
                } else {
                    drawable.setBounds(0, 0, BITMAP_WIDTH_ME, BITMAP_WIDTH_ME);
                }
                danmaku.text = createSpannable(name, msg, drawable);
                mDanmakuView.addDanmaku(danmaku);
            }

            @Override
            public void onFail() {
                Drawable circleDrawable;
                circleDrawable = mContext.getResources().getDrawable(R.drawable.ic_livevideo_default_head_boy);
                if (isGuest) {
                    circleDrawable.setBounds(0, 0, BITMAP_WIDTH_GUEST, BITMAP_WIDTH_GUEST);
                } else {
                    circleDrawable.setBounds(0, 0, BITMAP_WIDTH_ME, BITMAP_WIDTH_ME);
                }
                danmaku.text = createSpannable(name, msg, circleDrawable);
                mDanmakuView.addDanmaku(danmaku);
            }
        });
    }

    protected SpannableStringBuilder createSpannable(String name, String msg, Drawable drawable) {
        String text = "  " + name + ": " + msg + "  ";
        SpannableStringBuilder spannable = new SpannableStringBuilder(text);
        spannable.append(text);
        ImageSpan span = new VerticalImageSpan(drawable);
        spannable.setSpan(span, 0, text.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    public void pauseDanmaku() {
        if (mDanmakuView != null) {
            mDanmakuView.pause();
        }
    }

    public void resumeDanmaku() {
        if (mDanmakuView != null) {
            mDanmakuView.resume();
        }
    }

    public void setDanmakuSpeed(float speed) {
        if (mDanmakuContext != null) {
            mDanmakuContext.setScrollSpeedFactor(1.2f / speed);
        }
    }

    @Override
    public View getPager() {
        return mView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDanmakuView != null) {
            mDanmakuView.release();
            mDanmakuView = null;
        }
    }
}
