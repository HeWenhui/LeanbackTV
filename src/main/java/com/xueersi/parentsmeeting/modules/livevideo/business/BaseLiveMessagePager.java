package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.common.base.BasePager;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.config.AppConfig;
import com.xueersi.lib.framework.utils.string.ConstUtils;
import com.xueersi.lib.framework.utils.string.RegexUtils;
import com.xueersi.parentsmeeting.module.browser.activity.BrowserActivity;
import com.xueersi.parentsmeeting.modules.livevideo.OtherModulesEnter;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveExPressionEditData;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.message.IRCState;
import com.xueersi.parentsmeeting.modules.livevideo.message.business.LiveMessageBll;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionShowAction;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveThreadPoolExecutor;
import com.xueersi.parentsmeeting.modules.livevideo.widget.VerticalImageSpan;
import com.xueersi.parentsmeeting.widget.expressionView.ExpressionView;
import com.xueersi.parentsmeeting.widget.expressionView.adapter.ExpressionListAdapter;
import com.xueersi.parentsmeeting.widget.expressionView.entity.ExpressionAllInfoEntity;
import com.xueersi.ui.adapter.CommonAdapter;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

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

/**
 * Created by linyuqiang on 2016/12/19.
 * 聊天信息一些基本方法
 */
public abstract class BaseLiveMessagePager extends LiveBasePager implements RoomAction, QuestionShowAction {
    protected ArrayList<LiveMessageEntity> liveMessageEntities = new ArrayList<>();
    /** 发送消息间隔 */
    protected final static long SEND_MSG_INTERVAL = 5000;
    /** 同学献花提示 */
    public String[] flowsTips = {"老师真赞", "老师太棒了", "老师辛苦了"};
//    private String[] smallEnglishTips = {"送老师一朵太阳花", "送老师一束太阳花", "送老师一捧太阳花"};
    /** 同学献花花的资源 */
    public int[] flowsDrawTips = {R.drawable.bg_livevideo_flower_small, R.drawable.bg_livevideo_flower_middle, R
            .drawable.bg_livevideo_flower_big};
    /** 同学献花花的资源,小，弹幕和聊天中用 */
    public int[] flowsDrawLittleTips = {R.drawable.bg_livevideo_flower_small2, R.drawable
            .bg_livevideo_flower_middle2, R.drawable.bg_livevideo_flower_big2,};
    /** 同学献花,小中大 */
    public static final int FLOWERS_SMALL = 2, FLOWERS_MIDDLE = 3, FLOWERS_BIG = 4;
    /** 献花字体颜色 */
    public int[] flowsDrawColors = {0xFF1a8615, Color.RED, 0xFFff00ea};
    public String SYSTEM_TIP = "系统提示";
    public static String SYSTEM_TIP_STATIC = "系统提示";
    /** 聊天连接 */
    public static final String CONNECT = "聊天服务器连接成功";
    /** 聊天断开连接 */
    public static final String DISCONNECT = "聊天服务器断开连接";
    /** 聊天信息为空 */
    public static final String MESSAGE_EMPTY = "请输入有效信息！";
    /** 聊天信息为中文 */
    public static final String MESSAGE_CHINESE = "你的班级已禁止中文发言！";
    /** 聊天名字颜色 */
    protected int[] nameColors;
    protected DanmakuView dvMessageDanmaku;
    protected DanmakuContext mDanmakuContext;
    private BaseDanmakuParser mParser;
    protected ExpressionView mExpressionView;
    protected EditText etMessageContent;
    /** 讨论人数 */
    protected XesAtomicInteger peopleCount;
    /** 房间注册成功 */
    protected boolean isRegister = false;
    protected boolean isHaveFlowers = false;
    protected boolean keyboardShowing = false;
    protected IRCState ircState;
    protected LiveMessageBll messageBll;
    protected static int MESSAGE_SEND_DEF = 0;
    protected static int MESSAGE_SEND_DIS = 1;
    protected static int MESSAGE_SEND_CLO = 2;
    public int urlclick;
    public LiveGetInfo getInfo;
    /** 从getinfo获得金币 */
    protected int getInfoGoldNum = 0;
    /** 聊天线程池 */
    protected ThreadPoolExecutor pool;
    protected LiveThreadPoolExecutor liveThreadPoolExecutor = LiveThreadPoolExecutor.getInstance();
    //小英的献花
    public final static int SMALL_ENGLISH = 1;
    //其他部分的献花
    public final static int OTHER_FLOWER = 2;

    public BaseLiveMessagePager(Context context, boolean isNewView) {
        super(context, isNewView);
        logger.setLogMethod(false);
        pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);
        pool.setRejectedExecutionHandler(new RejectedExecutionHandler() {

            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {

                logger.i(pool.isShutdown() + "加入线程池被拒绝了");
            }
        });
        Resources resources = context.getResources();
        nameColors = new int[]{resources.getColor(R.color.COLOR_E74C3C), resources.getColor(R.color.COLOR_20ABFF),
                resources.getColor(R.color.COLOR_666666), resources.getColor(R.color.COLOR_E74C3C)};
    }

    public BaseLiveMessagePager(Context context) {
        this(context, true);
    }

    public void setMessageBll(LiveMessageBll messageBll) {
        this.messageBll = messageBll;
    }

    /**
     * 开始倒计时
     *
     * @param time 倒计时时间
     * @return
     */
    public Runnable startCountDown(final String tag, final int time) {
        final AtomicInteger atomicInteger = new AtomicInteger(time);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                countDown(tag, atomicInteger.get());
                if (atomicInteger.get() > 0) {
                    atomicInteger.set(atomicInteger.get() - 1);
                    mainHandler.postDelayed(this, 1000);
                }
            }
        };
        mainHandler.post(runnable);
        return runnable;
    }


    public void countDown(String tag, int time) {

    }

    public void setGetInfo(LiveGetInfo getInfo) {
        this.getInfo = getInfo;
        if (getInfo != null) {
            LiveGetInfo.StudentLiveInfoEntity studentLiveInfo = getInfo.getStudentLiveInfo();
            if (studentLiveInfo != null) {
                getInfoGoldNum = studentLiveInfo.getGoldNum();
            }
        }
    }

    /**
     * 表情键盘item被点击
     *
     * @param position
     * @param catogaryId
     * @param expressionId
     * @param exPressionUrl
     * @param exPressionName
     * @param exPressionGifUrl
     * @param bottomId
     */
    protected void onExpressionClick(int position, int catogaryId, String expressionId,
                                     int exPressionUrl, String exPressionName, int exPressionGifUrl, int bottomId) {

    }

    @Override
    public void initData() {
        mExpressionView = new ExpressionView(mContext, mView.findViewById(R.id.layout_chat_expression),
                etMessageContent, new LiveExPressionEditData()) {
            @Override
            public void clickGridItem(int position, int catogaryId, String expressionId,
                                      int exPressionUrl, String exPressionName, int exPressionGifUrl, int bottomId) {
                onExpressionClick(position, catogaryId, expressionId, exPressionUrl, exPressionName,
                        exPressionGifUrl, bottomId);
                int length = etMessageContent.getText().length();
                if (length + exPressionName.length() <= 40) {
                    Bitmap bitmap11 = BitmapFactory.decodeResource(mContext.getResources(), exPressionUrl);
                    ImageSpan imageSpan11 = new VerticalImageSpan(mContext, Bitmap.createScaledBitmap(bitmap11,
                            (int) etMessageContent.getTextSize(), (int) etMessageContent.getTextSize(), true));
                    SpannableString spannableString11 = new SpannableString(exPressionName);
                    spannableString11.setSpan(imageSpan11, 0, exPressionName.length(), Spannable
                            .SPAN_EXCLUSIVE_EXCLUSIVE);
                    etMessageContent.getEditableText().insert(etMessageContent.getSelectionStart(), spannableString11);
                }
            }
        };
        // 加载表情布局适配器
        ExpressionListAdapter mExpressionListAdapter = new ExpressionListAdapter(mContext, mExpressionView
                .getExpressionAllInfoList(), mExpressionView) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final ExpressionAllInfoEntity mCustomData = datas.get(position);
                ViewHolder holder;
                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.item_expression_list_view, parent, false);
                    convertView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
                    holder = new ViewHolder();
                    holder.mBottomImage = (ImageView) convertView.findViewById(R.id.iv_expression_gif_image);
                    if (position == 0) {
                        convertView.setBackgroundColor(Color.parseColor("#dddddd"));
                    }
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                int url = mCustomData.getBackgroundResource();
                holder.mBottomImage.setImageResource(url);
                int bottomId = mCustomData.getBottomImageId();
                OnClickListener(holder.mBottomImage, bottomId);
                return convertView;
            }
        };
        mExpressionView.loadPagerData(mExpressionListAdapter);
        mExpressionView.getExpressionViewPagerAdapter().notifyDataSetChanged();
    }

    /**
     * 显示和隐藏表情键盘区
     *
     * @param isVisible
     * @author zouhao
     * @Create at: 2015-10-27 下午2:01:29
     */
    protected void showExpressionView(boolean isVisible) {
        if (isVisible) {
            // 显示
            mExpressionView.show();
        } else {
            // 隐藏
            mExpressionView.hide();
        }
    }

    public void setPeopleCount(XesAtomicInteger peopleCount) {
        this.peopleCount = peopleCount;
    }

    /** 得到聊天输入信息 */
    public String getMessageContentText() {
        return etMessageContent.getText().toString();
    }

    /** 设置聊天输入信息 */
    public void setEtMessageContentText(String text) {
        this.etMessageContent.setText(text);
    }

    /** 聊天是不是注册 */
    public boolean isRegister() {
        return isRegister;
    }

    /** 设置聊天注册 */
    public void setIsRegister(boolean isRegister) {
        this.isRegister = isRegister;
    }

    /** 是不是打开献花 */
    public boolean isHaveFlowers() {
        return isHaveFlowers;
    }

    /** 设置打开献花 */
    public void setHaveFlowers(boolean haveFlowers) {
        isHaveFlowers = haveFlowers;
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


    public void setIrcState(IRCState ircState) {
        this.ircState = ircState;
    }

    /** 控制栏显示 */
    public void onTitleShow(boolean show) {
    }

    public abstract void closeChat(boolean close);

    /** 聊天是不是锁屏 */
    public abstract boolean isCloseChat();

    /** 按返回键 */
    public boolean onBack() {
        return false;
    }

    /** 聊天模式变化 */
    public void onModeChange(String mode) {
    }

    public void setVideoLayout(LiveVideoPoint liveVideoPoint) {

    }

    public void onGetMyGoldDataEvent(String goldNum) {
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
                        if (danmaku.text instanceof TypeSpannableStringBuilder) {
                            TypeSpannableStringBuilder spannableStringBuilder = (TypeSpannableStringBuilder) danmaku
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
                            if (danmaku.text instanceof TypeSpannableStringBuilder) {
                                TypeSpannableStringBuilder typeSpannableStringBuilder = (TypeSpannableStringBuilder)
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
        Drawable drawable;
        switch (ftype) {
            case FLOWERS_SMALL:
            case FLOWERS_MIDDLE:
            case FLOWERS_BIG:
                drawable = mContext.getResources().getDrawable(flowsDrawLittleTips[ftype - 2]);
                danmaku.textColor = flowsDrawColors[ftype - 2];
                break;
            default:
                drawable = mContext.getResources().getDrawable(R.drawable.ic_launcher);
                danmaku.textColor = Color.BLUE;
                break;
        }
        drawable.setBounds(0, 0, 100, 100);
        SpannableStringBuilder spannable = createSpannable(ftype, name, drawable);
        danmaku.text = spannable;
        danmaku.padding = 5;
        danmaku.priority = 1;  // 一定会显示, 一般用于本机发送的弹幕
        danmaku.isLive = false;
        danmaku.time = dvMessageDanmaku.getCurrentTime() + 1200;
        if (LiveVideoConfig.isPrimary) {
            danmaku.textSize = 20f * (mParser.getDisplayer().getDensity() - 0.6f);
        } else {
            danmaku.textSize = 25f * (mParser.getDisplayer().getDensity() - 0.6f);
        }
//        danmaku.underlineColor = Color.GREEN;

        dvMessageDanmaku.addDanmaku(danmaku);
    }

//    public void addSmallEnglishDanmaKuFlowers(final int ftype, final String name) {
//        if (mDanmakuContext == null) {
//            mView.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    addSmallEnglishDanmaKuFlowers(ftype, name);
//                }
//            }, 20);
//            return;
//        }
//
//        BaseDanmaku danmaku = mDanmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
//        if (danmaku == null || dvMessageDanmaku == null) {
//            return;
//        }
//        int[] smallEnglishFlowers = new int[]{R.drawable.bg_livevideo_small_english_sendflower_oneflower_img, R
//                .drawable.bg_livevideo_small_english_sendflower_threeflowers_img, R.drawable
//                .bg_livevideo_small_english_sendflower_fiveflowers_img};
//        Drawable drawable;
//        switch (ftype) {
//            case FLOWERS_SMALL:
//            case FLOWERS_MIDDLE:
//            case FLOWERS_BIG:
//                drawable = mContext.getResources().getDrawable(smallEnglishFlowers[ftype - 2]);
//                danmaku.textColor = R.color.COLOR_FFFFFF;
//                break;
//            default:
//                drawable = mContext.getResources().getDrawable(R.drawable.ic_launcher);
//                danmaku.textColor = Color.BLUE;
//                break;
//        }
//        drawable.setBounds(0, 0, SizeUtils.Dp2Px(mContext, 60), SizeUtils.Dp2Px(mContext, 60));
//        SpannableStringBuilder spannable = smallEnglishCreateSpannable(ftype, name, drawable);
//        danmaku.text = spannable;
//        danmaku.padding = 13;
//        danmaku.priority = 1;  // 一定会显示, 一般用于本机发送的弹幕
//        danmaku.isLive = false;
//        danmaku.time = dvMessageDanmaku.getCurrentTime() + 1200;
//        danmaku.textSize = 25f * (mParser.getDisplayer().getDensity() - 0.6f);
//        danmaku.textShadowColor = 0; // 重要：如果有图文混排，最好不要设置描边(设textShadowColor=0)，否则会进行两次复杂的绘制导致运行效率降低
////        danmaku.underlineColor = Color.GREEN;
//
//        dvMessageDanmaku.addDanmaku(danmaku);
//    }

    //


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
        TypeSpannableStringBuilder spannableStringBuilder = new TypeSpannableStringBuilder(msg, name, ftype);
        spannableStringBuilder.append(msg);
        ImageSpan span = new ImageSpan(drawable);//ImageSpan.ALIGN_BOTTOM);
        spannableStringBuilder.setSpan(span, msg.length(), spannableStringBuilder.length(), Spannable
                .SPAN_INCLUSIVE_EXCLUSIVE);
//        spannableStringBuilder.setSpan(new BackgroundColorSpan(Color.parseColor("#8A2233B1")), 0,
// spannableStringBuilder.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        return spannableStringBuilder;
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

    /** 互动题隐藏后，显示输入,竖屏显示输入，横屏显示聊天内容 */
    public void onQuestionHide() {

    }

    /** 互动题显示后，隐藏输入,,竖屏隐藏输入，横屏隐藏聊天内容 */
    public void onQuestionShow() {

    }

    public abstract void addMessage(final String sender, final int type, final String text, String headUrl);

    public abstract CommonAdapter<LiveMessageEntity> getMessageAdapter();

    public abstract void setOtherMessageAdapter(CommonAdapter<LiveMessageEntity> otherMessageAdapter);

    /**
     * 带连接的文本点击事件
     */
    public interface TextUrlClick {
        /**
         * 文本点击
         *
         * @param tvContent
         */
        void onUrlClick(TextView tvContent);
    }

    protected void urlClick(TextView tvContent) {
        CharSequence text = tvContent.getText();

        if (text instanceof Spannable) {
            int end = text.length();
            Spannable sp = (Spannable) tvContent.getText();
            URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
            SpannableStringBuilder style = new SpannableStringBuilder(text);
            // style.clearSpans(); // should clear old spans
            for (URLSpan url : urls) {

                if (url.getURL().startsWith("tel:")) {
                    style.setSpan(url, sp.getSpanStart(url), sp.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                } else {
                    MessageTextURLSpan myURLSpan = new MessageTextURLSpan(url.getURL());
                    style.setSpan(myURLSpan, sp.getSpanStart(url), sp.getSpanEnd(url),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            tvContent.setText(style);
            // 绑定一个链接的事件，只有在500毫秒以内才会触发链接点击事件，解决和longClick的冲突
//            tvContent.setMovementMethod(LinkMovementClickMethod.getInstance());
        }
    }

    public interface OnMsgUrlClick {
        void onMsgUrlClick(String url);
    }

    /**
     * 监听链接点击事件
     *
     * @author ZouHao
     */
    protected class MessageTextURLSpan extends ClickableSpan {

        private String mUrl;
        OnMsgUrlClick onUrlClick;

        public MessageTextURLSpan(String url) {
            if (mContext instanceof OnMsgUrlClick) {
                onUrlClick = (OnMsgUrlClick) mContext;
            }
            String params = "fromtype=livelecturechat&fromplatformtype=android&fromliveid=" + getInfo.getId()
                    + "&fromuserid=" + UserBll.getInstance().getMyUserInfoEntity().getStuId();
            if (url.contains("?")) {
                mUrl = url + "&" + params;
            } else {
                mUrl = url + "?" + params;
            }
        }

        @Override
        public void onClick(View widget) {
            if (mUrl.contains("xueersi.com/kc/")) {
                if (BrowserActivity.startCourseDetail((Activity) mContext, mUrl, mUrl)) {
                    if (onUrlClick != null) {
                        onUrlClick.onMsgUrlClick(mUrl);
                    }
                    return;
                }
            }
            try {
                Intent intent = new Intent();
                intent.setAction(AppConfig.ANDROID_INTENT_ACTION_VIEWXUEERSI);
                Uri content_url = null;
                if (mUrl.contains("AxhSignup/detail")) {
                    intent.putExtra("isAxhSignup", true);
                    content_url = Uri.parse(mUrl);
                } else {
                    content_url = Uri.parse(mUrl);
                }
                intent.setData(content_url);
                mContext.startActivity(intent);
                if (onUrlClick != null) {
                    onUrlClick.onMsgUrlClick(mUrl);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 跳转课程详情页
     *
     * @param url
     */
    private boolean startCourseDetail(String url) {
        try {
            int index = url.lastIndexOf('/');
            String kc = url.substring(index + 1);
            index = kc.indexOf('?');
            if (index != -1) {
                kc = kc.substring(0, index);
            }
            index = kc.indexOf('.');
            if (index != -1) {
                kc = kc.substring(0, index);
            }
            logger.i("startCourseDetail:kc=" + kc);
            String courseId = "";
            String groupId = "";
            String[] courseIds = kc.split("-");
            // 如果长度是1说明只有课程ID
            if (courseIds.length == 1) {
                courseId = RegexUtils.getMatchesByFirst(ConstUtils.REGEX_POSITIVE_INTEGER, courseIds[0]);
                // 长度是大于1有课程ID和直播组ID
            } else if (courseIds.length > 1) {
                courseId = RegexUtils.getMatchesByFirst(ConstUtils.REGEX_POSITIVE_INTEGER, courseIds[0]);
                groupId = RegexUtils.getMatchesByFirst(ConstUtils.REGEX_POSITIVE_INTEGER, courseIds[1]);
            }
            if (TextUtils.isEmpty(courseId)) {
                return false;
            }
            OtherModulesEnter.intentTo((Activity) mContext, courseId, groupId, "", url);
            return true;
        } catch (Exception e) {
            logger.e("startCourseDetail", e);
            return false;
        }
    }

    @Override
    public void onQuestionShow(VideoQuestionLiveEntity videoQuestionLiveEntity, boolean isShow) {

    }

    @Override
    public void onOtherDisable(String id, String name, boolean disable) {

    }

    @Override
    public void onDestroy() {
        if (pool != null) {
            logger.i("线程池被shutdown");
            pool.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void videoStatus(final String status) {
        if (dvMessageDanmaku != null) {
            mView.post(new Runnable() {
                @Override
                public void run() {
                    dvMessageDanmaku.setVisibility("on".endsWith(status) ? View.INVISIBLE : View.VISIBLE);
                }
            });
        }
    }

    protected boolean isChinese(String str) {
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            byte[] bytes = ("" + chars[i]).getBytes();
            if (bytes.length > 2) {
                return true;
            }
        }
        return false;
    }

}
