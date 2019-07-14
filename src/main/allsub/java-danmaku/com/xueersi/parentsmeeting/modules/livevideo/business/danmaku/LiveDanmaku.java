package com.xueersi.parentsmeeting.modules.livevideo.business.danmaku;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewGroup;

import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.BaseLiveMessagePager;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveViewAction;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoLevel;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveThreadPoolExecutor;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.videochat.business.VideoChatStatusChange;

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
import master.flame.danmaku.danmaku.ui.widget.DanmakuView;

public class LiveDanmaku implements LiveDanmakuPro {
    private Logger logger = LoggerFactory.getLogger("LiveDanmaku");
    protected Handler mainHandler = new Handler(Looper.getMainLooper());
    private DanmakuView dvMessageDanmaku;
    private DanmakuContext mDanmakuContext;
    protected LiveThreadPoolExecutor liveThreadPoolExecutor = LiveThreadPoolExecutor.getInstance();
    private Activity mContext;
    /** 同学献花,小中大 */
    public static final int FLOWERS_SMALL = 2, FLOWERS_MIDDLE = 3, FLOWERS_BIG = 4;
    /** 同学献花提示 */
    public String[] flowsTips = {"老师真赞", "老师太棒了", "老师辛苦了"};
    /** 同学献花花的资源,小，弹幕和聊天中用 */
    public int[] flowsDrawLittleTips = {R.drawable.bg_livevideo_flower_small2, R.drawable
            .bg_livevideo_flower_middle2, R.drawable.bg_livevideo_flower_big2,};

    public LiveDanmaku(Activity context) {
        this.mContext = context;
        ProxUtil.getProxUtil().put(context, LiveDanmakuPro.class, this);
    }

    public void initView(LiveViewAction liveViewAction) {
        View view = liveViewAction.inflateView(R.layout.layout_livevideo_danmuku);
        liveViewAction.addView(LiveVideoLevel.LEVEL_DANMU, view);
        dvMessageDanmaku = view.findViewById(R.id.dv_livevideo_danmaku);
        LiveVideoPoint.getInstance().addVideoSizeChangeAndCall(mContext, new LiveVideoPoint.VideoSizeChange() {
            @Override
            public void videoSizeChange(LiveVideoPoint liveVideoPoint) {
                logger.d("videoSizeChange:rightMargin=" + liveVideoPoint.getRightMargin());
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) dvMessageDanmaku.getLayoutParams();
                if (lp.rightMargin != liveVideoPoint.getRightMargin()) {
                    lp.rightMargin = liveVideoPoint.getRightMargin();
                    LayoutParamsUtil.setViewLayoutParams(dvMessageDanmaku, lp);
                }
            }
        });
    }

    public void onLiveInited(LiveGetInfo getInfo) {
        initDanmaku();
        VideoChatStatusChange videoChatStatusChange = ProxUtil.getProvide(mContext, VideoChatStatusChange.class);
        if (videoChatStatusChange != null) {
            videoChatStatusChange.addVideoChatStatusChange(new VideoChatStatusChange.ChatStatusChange() {
                @Override
                public void onVideoChatStatusChange(String voiceChatStatus) {
                    videoStatus(voiceChatStatus);
                }
            });
        }
    }

    private void videoStatus(final String status) {
        if (dvMessageDanmaku != null) {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    dvMessageDanmaku.setVisibility("on".endsWith(status) ? View.INVISIBLE : View.VISIBLE);
                }
            });
        }
    }

    protected void initDanmaku() {
        Intent intent = mContext.getIntent();
        boolean isPrimary = intent.getBooleanExtra("isPrimary", false);
        boolean isSmallChinese = intent.getBooleanExtra("isSmallChinese", false);
        boolean isSmallEnglish = intent.getBooleanExtra("isSmallEnglish", false);
        BaseCacheStuffer cacheStuffer;
        BaseCacheStuffer.Proxy mCacheStufferAdapter;
        BaseDanmakuParser mParser;
        if (isPrimary) {
            if (flowsTips != null) {
                flowsTips = new String[]{"送老师一颗小心心，老师也喜欢你哟~", "送老师一杯暖心茉莉茶，老师嗓子好舒服~", "送老师一个冰淇淋，夏天好凉爽~"};
            }
            if (flowsDrawLittleTips != null) {
                flowsDrawLittleTips = new int[]{R.drawable.primarypresentheart, R.drawable
                        .primarypresentcup, R.drawable
                        .primarypresentirc};
            }
            cacheStuffer = new PrimaryBackgroundCacheStuffer(mContext);
            mParser = new BaseDanmakuParser() {
                @Override
                protected Danmakus parse() {
                    return new Danmakus();
                }
            };
            mCacheStufferAdapter = new PrimaryScienceCacheProxy();
        } else if (isSmallChinese) {
            if (flowsTips != null) {
                flowsTips = new String[]{"送老师一座自由女神", "送老师一座埃菲尔铁塔", "送老师一座长城"};
            }

            if (flowsDrawLittleTips != null) {
                flowsDrawLittleTips = new int[]{R.drawable.bg_livevideo_small_chinese_danmu_small_gift,
                        R.drawable.bg_livevideo_small_chinese_danmu_middle_gift,
                        R.drawable.bg_livevideo_small_chinese_danmu_big_gift};
            }
            cacheStuffer = new SmallChineseBackgroundCacheStuffer(mContext);
            mParser = new BaseDanmakuParser() {
                @Override
                protected Danmakus parse() {
                    return new Danmakus();
                }
            };
            mCacheStufferAdapter = new SmallChineseCacheProxy();
        } else if (isSmallEnglish) {
            if (flowsTips != null) {
                flowsTips = new String[]{"送老师一朵太阳花", "送老师一束太阳花", "送老师一捧太阳花"};
            }
            if (flowsDrawLittleTips != null) {
                flowsDrawLittleTips = new int[]{R.drawable.bg_livevideo_small_english_sendflower_oneflower_img,
                        R.drawable.bg_livevideo_small_english_sendflower_threeflowers_img,
                        R.drawable.bg_livevideo_small_english_sendflower_fiveflowers_img};
            }
            cacheStuffer = new SmallEngBackgroundCacheStuffer(mContext);
            mParser = new BaseDanmakuParser() {
                @Override
                protected Danmakus parse() {
                    return new Danmakus();
                }
            };
            mCacheStufferAdapter = new SmallEnglishCacheProxy();
        } else {
            cacheStuffer = new SpannedCacheStuffer();
            mParser = createParser(mContext.getResources().openRawResource(R.raw.comments));
            mCacheStufferAdapter = new BaseCacheProxy();
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
                .setCacheStuffer(cacheStuffer, mCacheStufferAdapter) // 图文混排使用SpannedCacheStuffer
//                .setCacheStuffer(new BackgroundCacheStuffer())  // 绘制背景使用BackgroundCacheStuffer
                .setMaximumLines(maxLinesPair)
                .preventOverlapping(overlappingEnablePair);

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
                logger.i("onDanmakuClick text:" + latest.text);
            }

            @Override
            public void onDanmakuClick(IDanmakus danmakus) {
                logger.i("onDanmakuClick danmakus size:" + danmakus.size());
            }
        });
        dvMessageDanmaku.prepare(mParser, mDanmakuContext);
        dvMessageDanmaku.showFPS(false);
        dvMessageDanmaku.enableDanmakuDrawingCache(false);
    }

    private class PrimaryScienceCacheProxy extends BaseCacheStuffer.Proxy {

        @Override
        public void prepareDrawing(final BaseDanmaku danmaku, boolean fromWorkerThread) {
        }

        @Override
        public void releaseResource(BaseDanmaku danmaku) {
            // TODO 重要:清理含有ImageSpan的text中的一些占用内存的资源 例如drawable
        }
    }

    private class SmallChineseCacheProxy extends BaseCacheStuffer.Proxy {
        @Override
        public void prepareDrawing(final BaseDanmaku danmaku, boolean fromWorkerThread) {
        }

        @Override
        public void releaseResource(BaseDanmaku danmaku) {
            // TODO 重要:清理含有ImageSpan的text中的一些占用内存的资源 例如drawable
            if (danmaku.text instanceof SpannableStringBuilder) {
                danmaku.text = "";
            }
        }
    }

    private class SmallEnglishCacheProxy extends BaseCacheStuffer.Proxy {
        @Override
        public void prepareDrawing(final BaseDanmaku danmaku, boolean fromWorkerThread) {
        }

        @Override
        public void releaseResource(BaseDanmaku danmaku) {
            // TODO 重要:清理含有ImageSpan的text中的一些占用内存的资源 例如drawable
            if (danmaku.text instanceof SpannableStringBuilder) {
                danmaku.text = "";
            }
        }
    }

    private class BaseCacheProxy extends BaseCacheStuffer.Proxy {

        @Override
        public void prepareDrawing(final BaseDanmaku danmaku, boolean fromWorkerThread) {
            if (danmaku.text instanceof Spanned) { // 根据你的条件检查是否需要需要更新弹幕
                // FIXME 这里只是简单启个线程来加载远程url图片，请使用你自己的异步线程池，最好加上你的缓存池
                liveThreadPoolExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        Drawable drawable;
                        if (danmaku.text instanceof BaseLiveMessagePager.TypeSpannableStringBuilder) {
                            BaseLiveMessagePager.TypeSpannableStringBuilder spannableStringBuilder = (BaseLiveMessagePager.TypeSpannableStringBuilder) danmaku
                                    .text;
//                            logger.i( "prepareDrawing:ftype=" + spannableStringBuilder.ftype);
                            switch (spannableStringBuilder.ftype) {
                                case FLOWERS_SMALL:
                                case FLOWERS_MIDDLE:
                                case FLOWERS_BIG:
                                    drawable = mContext.getResources().getDrawable
                                            (flowsDrawLittleTips[spannableStringBuilder.ftype - 2]);
                                    break;
                                default:
                                    drawable = mContext.getResources().getDrawable(R.drawable.ic_app_xueersi_desktop);
                                    break;
                            }
                        } else {
                            drawable = mContext.getResources().getDrawable(R.drawable.ic_app_xueersi_desktop);
                        }
                        if (drawable != null) {
                            drawable.setBounds(0, 0, 100, 100);
                            SpannableStringBuilder spannable;
                            if (danmaku.text instanceof BaseLiveMessagePager.TypeSpannableStringBuilder) {
                                BaseLiveMessagePager.TypeSpannableStringBuilder typeSpannableStringBuilder = (BaseLiveMessagePager.TypeSpannableStringBuilder)
                                        danmaku.text;
                                spannable = createSpannable(typeSpannableStringBuilder.ftype,
                                        typeSpannableStringBuilder.name, drawable);
                            } else {
                                String msg = danmaku.text.toString();
                                if (msg.length() > 0) {
                                    msg = msg.substring(0, msg.length() / 2);
                                }
                                spannable = createSpannable(1, msg, drawable);
                            }
                            danmaku.text = spannable;
                            if (dvMessageDanmaku != null) {
                                dvMessageDanmaku.invalidateDanmaku(danmaku, false);
                            }
                        }
                    }
                });
            }
        }

        @Override
        public void releaseResource(BaseDanmaku danmaku) {
            // TODO 重要:清理含有ImageSpan的text中的一些占用内存的资源 例如drawable
        }

        public SpannableStringBuilder createSpannable(int ftype, String name, Drawable drawable) {
//        logger.i( "createSpannable:name=" + name + ",ftype=" + ftype);
            String tip = "";
            switch (ftype) {
                case FLOWERS_SMALL:
                case FLOWERS_MIDDLE:
                case FLOWERS_BIG:
                    tip = flowsTips[ftype - 2];
                    break;
            }

            String msg = name + ":" + tip + ",献上";
            BaseLiveMessagePager.TypeSpannableStringBuilder spannableStringBuilder = new BaseLiveMessagePager.TypeSpannableStringBuilder(msg, name, ftype);
            spannableStringBuilder.append(msg);
            ImageSpan span = new ImageSpan(drawable);//ImageSpan.ALIGN_BOTTOM);
            spannableStringBuilder.setSpan(span, msg.length(), spannableStringBuilder.length(), Spannable
                    .SPAN_INCLUSIVE_EXCLUSIVE);
//        spannableStringBuilder.setSpan(new BackgroundColorSpan(Color.parseColor("#8A2233B1")), 0,
// spannableStringBuilder.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

            return spannableStringBuilder;
        }
    }

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

    private class PrimaryBackgroundCacheStuffer extends SpannedCacheStuffer {
        //    private int DANMU_RADIUS = 20;//圆角半径
        public int DANMU_PADDING = 0;

        private int DANMU_BACKGROUND_HEIGHT = 45;
        private int BITMAP_WIDTH_ME = 61;//头像的宽度
        private int BITMAP_HEIGHT_ME = 60;//头像的高度

        private int CIRCEL_WIDTH = 40;
        private int CIRCEL_HEIGHT = 40;

        // 通过扩展SimpleTextCacheStuffer或SpannedCacheStuffer个性化你的弹幕样式
//        final Paint paint = new Paint();
        public PrimaryBackgroundCacheStuffer(Context context) {
            DANMU_PADDING = SizeUtils.Dp2Px(context, DANMU_PADDING);
//        DANMU_RADIUS = SizeUtils.Dp2Px(context, 20);

            DANMU_BACKGROUND_HEIGHT = SizeUtils.Dp2Px(context, DANMU_BACKGROUND_HEIGHT);
            BITMAP_WIDTH_ME = SizeUtils.Dp2Px(context, BITMAP_WIDTH_ME);
            BITMAP_HEIGHT_ME = SizeUtils.Dp2Px(context, BITMAP_HEIGHT_ME);
            CIRCEL_HEIGHT = SizeUtils.Dp2Px(context, CIRCEL_HEIGHT);
            CIRCEL_WIDTH = SizeUtils.Dp2Px(context, CIRCEL_WIDTH);
        }

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

    /**
     * 绘制背景(自定义弹幕样式)
     */
    private class SmallChineseBackgroundCacheStuffer extends SpannedCacheStuffer {
        //    private int DANMU_RADIUS = 20;//圆角半径
        public int DANMU_PADDING = 0;//弹幕之间的间距

        //    private int DANMU_BACKGROUND_HEIGHT = 45;
        private int BITMAP_WIDTH_ME = 28;// 弹幕上面实际显示的头像的宽度
        private int BITMAP_HEIGHT_ME = 55;//弹幕上面实际显示的头像的高度

        private int CIRCEL_WIDTH = 40;
        private int CIRCEL_HEIGHT = 40;
        /** 背景Drawalble,弹幕的礼物Drawalble */
        private Drawable backgroundDrawable, flowerDrawable;

        public SmallChineseBackgroundCacheStuffer(Context context) {
            DANMU_PADDING = SizeUtils.Dp2Px(context, DANMU_PADDING);
//        DANMU_BACKGROUND_HEIGHT = SizeUtils.
//                Dp2Px(context, DANMU_BACKGROUND_HEIGHT);
            BITMAP_WIDTH_ME = SizeUtils.Dp2Px(context, BITMAP_WIDTH_ME);
            BITMAP_HEIGHT_ME = SizeUtils.Dp2Px(context, BITMAP_HEIGHT_ME);
            CIRCEL_HEIGHT = SizeUtils.Dp2Px(context, CIRCEL_HEIGHT);
            CIRCEL_WIDTH = SizeUtils.Dp2Px(context, CIRCEL_WIDTH);
        }

        // 通过扩展SimpleTextCacheStuffer或SpannedCacheStuffer个性化你的弹幕样式
//        final Paint paint = new Paint();

        @Override
        public void measure(BaseDanmaku danmaku, TextPaint paint, boolean fromWorkerThread) {
//=            danmaku.padding = 20;  // 在背景绘制模式下增加padding
            super.measure(danmaku, paint, fromWorkerThread);
        }

        @Override
        public void drawBackground(BaseDanmaku danmaku, Canvas canvas, float left, float top) {
//            backgroundDrawable = mContext.getResources().getDrawable(danmaku.isGuest ?
//                    R.drawable //采用.9的方式来显示
////                .bg_livevideo_small_chinese_gift_danmu_my_background
//                            .bg_livevideo_small_chinese_gift_danmu_my_background_mid
//                    : R.drawable //采用.9的方式来显示
//                    .bg_livevideo_small_chinese_gift_danmu_other_backgroud_mid);

            backgroundDrawable = mContext.getResources().getDrawable(R.drawable.bg_livevideo_small_chinese_live_messagen_back_ground);

            float height = backgroundDrawable.getIntrinsicHeight();
//            float offsetRight = (BITMAP_HEIGHT_ME - CIRCEL_HEIGHT) / 2;
            logger.i("height = " + height + ",padding = " + danmaku.padding + ", left = " + left + ", bitmap_height = " + BITMAP_HEIGHT_ME);
            //左边的偏移量

//            int offsetLeft = (danmaku.isGuest ? 0 : SizeUtils.Dp2Px(mContext, 2));
            int offsetLeft = 0;
            //上面的偏移量，这里必须加上offsetTop，否则文字无法居中
            float offsetTop = (BITMAP_HEIGHT_ME > height ? BITMAP_HEIGHT_ME - height : height - BITMAP_HEIGHT_ME) / 2;
            backgroundDrawable.setBounds(
                    (int) (left + danmaku.padding + offsetLeft),
                    (int) (top + danmaku.padding + offsetTop),
                    (int) (left + danmaku.paintWidth + offsetLeft),
                    (int) (top + height + danmaku.padding + offsetTop))
            ;
            backgroundDrawable.draw(canvas);
        }

        @Override
        public void drawStroke(BaseDanmaku danmaku, String lineText, Canvas canvas, float left, float top, Paint
                paint) {
            // 禁用描边绘制
        }
    }

    private class SmallEngBackgroundCacheStuffer extends SpannedCacheStuffer {
        public int DANMU_PADDING = 0;

        private int DANMU_BACKGROUND_HEIGHT = 45;
        private int BITMAP_WIDTH_ME = 61;//头像的宽度
        private int BITMAP_HEIGHT_ME = 60;//头像的高度

        private int CIRCEL_WIDTH = 40;
        private int CIRCEL_HEIGHT = 40;

        /** 背景Drawalble,弹幕的礼物Drawalble */
        private Drawable backgroundDrawable;

        public SmallEngBackgroundCacheStuffer(Context context) {
            DANMU_PADDING = SizeUtils.Dp2Px(context, DANMU_PADDING);
//        DANMU_RADIUS = SizeUtils.Dp2Px(context, 20);

            DANMU_BACKGROUND_HEIGHT = SizeUtils.
                    Dp2Px(context, DANMU_BACKGROUND_HEIGHT);
            BITMAP_WIDTH_ME = SizeUtils.Dp2Px(context, BITMAP_WIDTH_ME);
            BITMAP_HEIGHT_ME = SizeUtils.Dp2Px(context, BITMAP_HEIGHT_ME);
            CIRCEL_HEIGHT = SizeUtils.Dp2Px(context, CIRCEL_HEIGHT);
            CIRCEL_WIDTH = SizeUtils.Dp2Px(context, CIRCEL_WIDTH);
            backgroundDrawable = mContext.getResources().getDrawable(R.drawable
                    .bg_livevideo_send_flower_screen_bullet_background);
        }
        // 通过扩展SimpleTextCacheStuffer或SpannedCacheStuffer个性化你的弹幕样式
//        final Paint paint = new Paint();

        @Override
        public void measure(BaseDanmaku danmaku, TextPaint paint, boolean fromWorkerThread) {
//=            danmaku.padding = 20;  // 在背景绘制模式下增加padding
            super.measure(danmaku, paint, fromWorkerThread);
        }

        @Override
        public void drawBackground(BaseDanmaku danmaku, Canvas canvas, float left, float top) {
//            paint.setAntiAlias(true);
//            paint.setColor(Color.BLACK);
//            paint.setAlpha((int) (255 * 0.6)); //  透明度0.6
//
//            int height = SizeUtils.Dp2Px(mContext, 40);
            //由于该库并没有提供margin的设置，所以我这边试出这种方法：将danmaku.padding也就是内间距设置大一点，并在这里的RectF中设置绘制弹幕背景的位置，就可以形成类似margin的效果
//            canvas.drawRoundRect(new RectF(left + DANMU_PADDING, top + DANMU_PADDING
//                            , left + danmaku.paintWidth - DANMU_PADDING,
//                            top + height + DANMU_PADDING),
//                    DANMU_RADIUS, DANMU_RADIUS, paint);


//            if (danmaku.isGuest) {
//            canvas.drawRoundRect(new RectF(left + danmaku.padding, top + danmaku.padding + (BITMAP_HEIGHT_GUEST -
//                            DANMU_BACKGROUND_HEIGHT) / 2 + 1
//                            , left + danmaku.paintWidth - danmaku.padding,
//                            top + DANMU_BACKGROUND_HEIGHT + (BITMAP_HEIGHT_GUEST - DANMU_BACKGROUND_HEIGHT) / 2 +
//                                    1 + danmaku.padding),
//                    DANMU_RADIUS, DANMU_RADIUS, paint);
//            } else {
            //绘制圆，宽高一样
//            int imgWidth = BITMAP_WIDTH_ME / 2;
//            int circleHeigh = imgWidth;
//            canvas.drawCircle(left + danmaku.padding + imgWidth, left + danmaku.padding + imgWidth,
//                    CIRCEL_WIDTH / 2, paint);
//            canvas.drawRoundRect(new RectF(left + danmaku.padding, top + danmaku.padding + (BITMAP_HEIGHT_ME -
//                            DANMU_BACKGROUND_HEIGHT) / 2 + 1
//                            , left + danmaku.paintWidth - danmaku.padding,
//                            top + DANMU_BACKGROUND_HEIGHT + (BITMAP_HEIGHT_ME - DANMU_BACKGROUND_HEIGHT) / 2 + 1
//                                    + danmaku.padding),
//                    DANMU_RADIUS, DANMU_RADIUS, paint);
//            }
            float height = backgroundDrawable.getIntrinsicHeight();
            float offsetRight = (BITMAP_HEIGHT_ME - CIRCEL_HEIGHT) / 2;
            backgroundDrawable.setBounds(
                    (int) (left + danmaku.padding + offsetRight),
                    (int) (top + danmaku.padding + (BITMAP_HEIGHT_ME - height) / 2),
                    (int) (left + danmaku.paintWidth),
                    (int) (top + height + (BITMAP_HEIGHT_ME - height) / 2
                            + danmaku.padding));
            backgroundDrawable.draw(canvas);
        }

        @Override
        public void drawStroke(BaseDanmaku danmaku, String lineText, Canvas canvas, float left, float top, Paint
                paint) {
            // 禁用描边绘制
        }
    }

    @Override
    public BaseDanmaku createDanmaku(int type) {
        BaseDanmaku danmaku = mDanmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        return danmaku;
    }

    public void addDanmaku(BaseDanmaku danmaku) {
        danmaku.time = dvMessageDanmaku.getCurrentTime() + 1200;
        dvMessageDanmaku.addDanmaku(danmaku);
    }

    public void onDestroy() {
        dvMessageDanmaku.release();
    }
}
