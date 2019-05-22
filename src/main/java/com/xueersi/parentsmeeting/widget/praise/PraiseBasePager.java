package com.xueersi.parentsmeeting.widget.praise;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.xueersi.common.util.FontCache;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.widget.FastScrollableRecyclerView;
import com.xueersi.parentsmeeting.modules.livevideo.widget.PraiseBtnAnimLayout;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.dialog.CloseConfirmDialog;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.page.LiveBasePager;
import com.xueersi.parentsmeeting.widget.praise.business.OnPraisePageListener;
import com.xueersi.parentsmeeting.widget.praise.config.PraiseConfig;
import com.xueersi.parentsmeeting.widget.praise.entity.PraiseContentEntity;
import com.xueersi.parentsmeeting.widget.praise.entity.PraiseEntity;
import com.xueersi.parentsmeeting.widget.praise.item.LivePraiseItem;
import com.xueersi.parentsmeeting.widget.praise.item.LivePraiseTitleItem;
import com.xueersi.parentsmeeting.widget.praise.item.RecyclerViewSpacesItemDecoration;
import com.xueersi.ui.adapter.RCommonAdapter;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/**
 * 表扬榜-辅导老师
 *
 * @param hua
 */
public class PraiseBasePager extends LiveBasePager {

    /** 内容view */
    FastScrollableRecyclerView recyclerView;
    RCommonAdapter contentAdapter;
    /** 榜单数据 */
    List<PraiseContentEntity> listContent;
    /** 点赞 */
    PraiseBtnAnimLayout imgBtnPractice;
    /** 点赞动画 */
    LottieAnimationView practiceView;
    /** 点赞数 */
    TextView tvPracticeCount;
    /** 教师信息 */
    LinearLayout llTeacherContent;
    /** 教师信息 */
    ImageButton imgBtnClose;
    /** 标题 */
    ImageView ivTitle;
    /** 自定义标题 */
    TextView tvTitle;
    /** 副标题 */
    TextView tvSubTitle;
    /** 老师提示 */
    TextView tvTeacherTip;
    /** 老师批语 */
    TextView tvTeacherTalk;
    /** 老师头像 */
    ImageView ivTeacherHeadImage;
    int mCurrentNum = 0;
    int TEN_THOUSAND = 10000;
    int HUNDRED = 100;

    PraiseEntity mPraiseEntity;
    PraiseBasePagerHandler mHandler;
    OnPraisePageListener onPraisePageListener;
    RelativeLayout bottomContent;
    int totalCurrentNum = 0;
    RelativeLayout rlMain;
    /**
     * 是否第一次点赞
     */
    boolean isSendParise = true;
    Typeface fontFace;
    private static class PraiseBasePagerHandler extends Handler {
        private WeakReference<PraiseBasePager> mc;

        public PraiseBasePagerHandler(PraiseBasePager mc) {
            this.mc = new WeakReference<PraiseBasePager>(mc);
        }

        @Override
        public void handleMessage(Message msg) {
            PraiseBasePager praiseBasePager = mc.get();
            if (praiseBasePager == null) {
                return;
            }
            int waht = msg.what;
            // 隐藏鼓励语
            if (waht == PraiseConfig.ENCOURAGING_HIDE) {
                praiseBasePager.hideEncouraging();
            } else if (waht == PraiseConfig.ENCOURAGING_SHOW) {
                praiseBasePager.showEncouragingView();
            } else if (waht == PraiseConfig.PRAISE_TOTAL_SEND) {
                praiseBasePager.updatePraiseNum();
            } else if (waht == PraiseConfig.PRAISE_CLICK_SEND) {
                praiseBasePager.sentPraiseLikes();

            } else if (waht == PraiseConfig.PRAISE_CLICK_CLOSE) {
                praiseBasePager.closePraisePagerMain();
            }else if (waht == PraiseConfig.PRAISE_CLOSE_VISIBLE) {
                praiseBasePager.closeBtnVisible();
            }
        }
    }

    public PraiseBasePager(Context context, PraiseEntity praiseEntity, OnPraisePageListener listener, RelativeLayout
            bottomContent) {
        super(context, praiseEntity, true);
        fontFace = FontCache.getTypeface(context, "fangzhengcuyuan.ttf");

        mHandler = new PraiseBasePagerHandler(this);
        listContent = praiseEntity.getContentEntityList();
        this.bottomContent = bottomContent;
        this.onPraisePageListener = listener;
        setContentData();
        setLayout(mView);
    }

    public PraiseBasePager(Context context) {
        super(context);

    }

    private void setLayout(View view) {
        int rightMargin = LiveVideoPoint.getInstance().getRightMargin();
        // 设置主视图参数
        RelativeLayout.LayoutParams mainParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
                .MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mainParam.rightMargin = rightMargin;
        view.setLayoutParams(mainParam);
    }

    /** */
    public class GridSpanSizeLookup extends GridLayoutManager.SpanSizeLookup {
        @Override
        public int getSpanSize(int position) {
            return listContent.get(position).getItemSpan();
        }
    }

    @Override
    public View initView() {
        mPraiseEntity = (PraiseEntity) mEntity;
        int layoutId = R.layout.page_livevideo_praise_list_wood;
        if (mPraiseEntity.getPraiseStyle() == PraiseConfig.PRAISE_DARK) {
            layoutId = R.layout.page_livevideo_praise_list_dark;
        } else if (mPraiseEntity.getPraiseStyle() == PraiseConfig.PRAISE_LOVELY) {
            layoutId = R.layout.page_livevideo_praise_list_lovely;
        } else if (mPraiseEntity.getPraiseStyle() == PraiseConfig.PRAISE_CHINA) {
            layoutId = R.layout.page_livevideo_praise_list_china;
        }
        mView = View.inflate(mContext, layoutId, bottomContent);
        recyclerView = mView.findViewById(R.id.rv_livevideo_praise_list_content);
        GridLayoutManager manager = new GridLayoutManager(mContext, 4);
        manager.setSpanSizeLookup(new GridSpanSizeLookup());
        recyclerView.setLayoutManager(manager);
        rlMain = mView.findViewById(R.id.rl_page_livevideo_praise_list_main_content);
        imgBtnPractice = mView.findViewById(R.id.fl_page_livevideo_praise_list_practice);
        practiceView = mView.findViewById(R.id.lav_livevideo_praise_list_practice);
        tvPracticeCount = mView.findViewById(R.id.tv_page_livevideo_praise_list_practice_count);
        llTeacherContent = mView.findViewById(R.id.ll_page_livevideo_praise_list_teacher_content);
        imgBtnClose = mView.findViewById(R.id.btn_page_livevideo_praise_list_close);
        ivTitle = mView.findViewById(R.id.iv_page_livevideo_praise_list_title);
        tvTitle = mView.findViewById(R.id.tv_page_livevideo_praise_list_title);
        tvSubTitle = mView.findViewById(R.id.tv_page_livevideo_praise_list_sub_title);
        tvTeacherTip = mView.findViewById(R.id.tv_page_livevideo_praise_list_teacher_tip);
        tvTeacherTalk = mView.findViewById(R.id.tv_page_livevideo_praise_list_teacher_talk);
        ivTeacherHeadImage = mView.findViewById(R.id.iv_page_livevideo_praise_list_teacher_head_image);
        setRecyclerViewDecoration();
        return mView;
    }

    public void setRecyclerViewDecoration(){
        HashMap<String, Integer> stringIntegerHashMap = new HashMap<>();
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.TOP_DECORATION,0);//top间距
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.BOTTOM_DECORATION,0);//底部间距
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.LEFT_DECORATION,0);//左间距
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.RIGHT_DECORATION, SizeUtils.Dp2Px(mContext,15));//右间距
        recyclerView.addItemDecoration(new RecyclerViewSpacesItemDecoration(stringIntegerHashMap));
    }

    private void setContentData() {
        if (contentAdapter == null) {
            contentAdapter = new RCommonAdapter(mContext, listContent);
            contentAdapter.addItemViewDelegate(1, new LivePraiseItem(mContext,fontFace));
            contentAdapter.addItemViewDelegate(4, new LivePraiseTitleItem(mContext,fontFace));
            recyclerView.setAdapter(contentAdapter);
        } else {
            contentAdapter.updateData(listContent);
        }
        setListener();
        setPriseType();
        setReslutType();

        ImageLoader.with(mContext).load(mPraiseEntity.getTeacherHeadImage()).
                error(R.drawable.icon_livevideo_praiselist_team_head_default).into(ivTeacherHeadImage);
        tvTeacherTip.setText(mPraiseEntity.getTeacherName() + "老师对你说:");
        tvTeacherTalk.setText(mPraiseEntity.getEncouraging());
        practiceView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mHandler.sendEmptyMessageDelayed(PraiseConfig.PRAISE_CLOSE_VISIBLE, 10000);
                practiceView.playAnimation();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    practiceView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    practiceView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
    }

    /**
     * 设置榜单类型
     */
    protected void setPriseType() {
    }

    /**
     * 设置结果类型
     */
    protected void setReslutType() {
        if (mPraiseEntity.getPraiseType() == PraiseConfig.PRAISE_TYPE_TALK) {
            tvSubTitle.setVisibility(View.GONE);
        }
    }

    private void setListener() {
        // 点赞
        imgBtnPractice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickPraiseLikes();

            }
        });
        // 关闭
        imgBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              ///  closePraisePager();
                showEncouraging();

            }
        });
    }

    /**
     * 关闭表扬榜
     *
     * @param onPagerClose
     */
    public void closeBtnVisible(){
        if (imgBtnClose != null) {
            imgBtnClose.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 关闭表扬榜 子线程
     *
     * @param onPagerClose
     */
    public void closePraisePager() {
        mHandler.sendEmptyMessageDelayed(PraiseConfig.PRAISE_CLICK_CLOSE, 0);

    }
    /**
     * 关闭表扬榜
     *
     * @param onPagerClose
     */
    public void closePraisePagerMain(){
        if (onPagerClose != null) {
            onPagerClose.onClose(this);
            if (onPraisePageListener != null){
                onPraisePageListener.onPracticeClose();
            }
            if (mHandler != null) {
                mHandler.removeMessages(PraiseConfig.ENCOURAGING_HIDE);
                mHandler.removeMessages(PraiseConfig.ENCOURAGING_SHOW);
                mHandler.removeMessages(PraiseConfig.PRAISE_TOTAL_SEND);
                mHandler.removeMessages(PraiseConfig.PRAISE_CLICK_SEND);
                mHandler.removeMessages(PraiseConfig.PRAISE_CLICK_CLOSE);
                mHandler.removeMessages(PraiseConfig.PRAISE_CLOSE_VISIBLE);
            }
        }
    }

    /**
     * 发送本地点赞数到服务器
     */
    private void sentPraiseLikes() {
        if (onPraisePageListener != null && mCurrentNum > 0) {
            onPraisePageListener.onPraiseClick(mCurrentNum);
            mCurrentNum = 0;
        }
        if (mHandler != null) {
            mHandler.sendEmptyMessageDelayed(PraiseConfig.PRAISE_CLICK_SEND, 3000);
        }
    }

    /**
     * 本地点赞数增加
     */
    private void clickPraiseLikes() {
        mCurrentNum++;
        totalCurrentNum = totalCurrentNum + 1;
        if (isSendParise) {
            sentPraiseLikes();
            isSendParise = false;
        }
        updatePraiseNum();
    }

    /**
     * 更新点赞
     */
    private void updatePraiseNum() {
        if (imgBtnPractice != null && imgBtnPractice.getVisibility() == View.VISIBLE) {
            StringBuilder sb = new StringBuilder();
            if (totalCurrentNum > TEN_THOUSAND) {
                if (totalCurrentNum % TEN_THOUSAND >= HUNDRED) {
                    BigDecimal bigDecimal = new BigDecimal(totalCurrentNum / 10000.0f);
                    sb.append(bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()).append("万");
                } else {
                    sb.append(totalCurrentNum / TEN_THOUSAND).append("万");
                }
            } else {
                sb.append(totalCurrentNum);
            }
            tvPracticeCount.setText(sb.toString());
        }
    }

    /**
     * 关闭表扬监听增加
     *
     * @param onPagerClose
     */
    @Override
    public void setOnPagerClose(OnPagerClose onPagerClose) {
        super.setOnPagerClose(onPagerClose);
    }

    public int getColor(int id) {
        return mContext.getResources().getColor(id);
    }

    /**
     * 隐藏鼓励语
     */
    public void hideEncouraging() {
        if (llTeacherContent != null) {
            llTeacherContent.setVisibility(View.GONE);
        }
    }

    /**
     * 显示鼓励语消息发送
     */
    public void showEncouraging() {
        if (mHandler != null) {
            mHandler.sendEmptyMessageDelayed(PraiseConfig.ENCOURAGING_SHOW, 0);
        }
    }

    /**
     * 设置点赞
     *
     * @param num
     */
    public void setPraiseTotal(int num) {
        if (totalCurrentNum < num) {
            totalCurrentNum = num;
        }
        if (mHandler != null) {
            mHandler.sendEmptyMessageDelayed(PraiseConfig.PRAISE_TOTAL_SEND, 0);
        }
    }

    /**
     * 显示鼓励语
     */
    private void showEncouragingView() {
        if (llTeacherContent != null) {
            llTeacherContent.setVisibility(View.VISIBLE);
        }
        if (mHandler != null) {
            mHandler.sendEmptyMessageDelayed(PraiseConfig.ENCOURAGING_HIDE, 5000);
        }

    }

}
