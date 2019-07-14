package com.xueersi.parentsmeeting.modules.livevideo.business.danmaku;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.View;

import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.BaseLiveMessagePager;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveViewAction;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoLevel;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveThreadPoolExecutor;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

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
    private DanmakuView dvMessageDanmaku;
    private DanmakuContext mDanmakuContext;
    private BaseDanmakuParser mParser;
    protected LiveThreadPoolExecutor liveThreadPoolExecutor = LiveThreadPoolExecutor.getInstance();
    private Context mContext;
    /** 同学献花,小中大 */
    public static final int FLOWERS_SMALL = 2, FLOWERS_MIDDLE = 3, FLOWERS_BIG = 4;
    /** 同学献花提示 */
    public String[] flowsTips = {"老师真赞", "老师太棒了", "老师辛苦了"};
    /** 同学献花花的资源,小，弹幕和聊天中用 */
    public int[] flowsDrawLittleTips = {R.drawable.bg_livevideo_flower_small2, R.drawable
            .bg_livevideo_flower_middle2, R.drawable.bg_livevideo_flower_big2,};

    public LiveDanmaku(Context context) {
        this.mContext = context;
        ProxUtil.getProxUtil().put(context, LiveDanmakuPro.class, this);
    }

    public void initView(LiveViewAction liveViewAction) {
        View view = liveViewAction.inflateView(R.layout.layout_livevideo_danmuku);
        liveViewAction.addView(LiveVideoLevel.LEVEL_DANMU, view);
        dvMessageDanmaku = view.findViewById(R.id.dv_livevideo_message_danmaku);
    }

    public void onLiveInited(LiveGetInfo getInfo) {
        initDanmaku();
    }

    protected void initDanmaku() {
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
                .setCacheStuffer(new SpannedCacheStuffer(), mCacheStufferAdapter) // 图文混排使用SpannedCacheStuffer
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

    protected BaseCacheStuffer.Proxy mCacheStufferAdapter = new BaseCacheStuffer.Proxy() {

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
    };

    protected SpannableStringBuilder createSpannable(int ftype, String name, Drawable drawable) {
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

    public void addDanmaku(BaseDanmaku danmaku) {
        danmaku.time = dvMessageDanmaku.getCurrentTime() + 1200;
        dvMessageDanmaku.addDanmaku(danmaku);
    }
}
