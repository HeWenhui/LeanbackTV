package com.xueersi.parentsmeeting.widget.praise;

import android.content.Context;
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
import com.xueersi.ui.adapter.RCommonAdapter;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
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

    int totalCurrentNum = 0;
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
            }

        }
    }

    public PraiseBasePager(Context context, PraiseEntity praiseEntity, OnPraisePageListener listener) {
        super(context, praiseEntity, true);
        mHandler = new PraiseBasePagerHandler(this);
        listContent = praiseEntity.getContentEntityList();
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
        mView = View.inflate(mContext, layoutId, null);
        recyclerView = mView.findViewById(R.id.rv_livevideo_praise_list_content);
        GridLayoutManager manager = new GridLayoutManager(mContext, 4);
        manager.setSpanSizeLookup(new GridSpanSizeLookup());
        recyclerView.setLayoutManager(manager);
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
        return mView;
    }

    private void setContentData() {
        if (contentAdapter == null) {
            contentAdapter = new RCommonAdapter(mContext, listContent);
            contentAdapter.addItemViewDelegate(1, new LivePraiseItem(mContext));
            contentAdapter.addItemViewDelegate(4, new LivePraiseTitleItem(mContext));
            recyclerView.setAdapter(contentAdapter);
        } else {
            contentAdapter.updateData(listContent);
        }
        setListener();
        setPriseType();
        setReslutType();
        ImageLoader.with(mContext).load(mPraiseEntity.getTeacherHeadImage()).
                error(R.drawable.icon_livevideo_praiselist_team_head_default).into(ivTeacherHeadImage);
        practiceView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
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
                upDatePraiseNum();

            }
        });
        // 关闭
        imgBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CloseConfirmDialog closeConfirmDialog = new CloseConfirmDialog(mContext);
                closeConfirmDialog.setTitle("关闭后将无法再开启表扬榜哦，确定关闭吗？");
                closeConfirmDialog.setTitleGravaty(Gravity.LEFT);
                closeConfirmDialog.hideContent();
                closeConfirmDialog.setOnClickCancelListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        closeConfirmDialog.cancelDialog();
                    }
                });
                closeConfirmDialog.setOnClickConfirmlListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        closeConfirmDialog.cancelDialog();
                        closePraisePager();
                    }
                });
                closeConfirmDialog.showDialog();

            }
        });
    }

    public void closePraisePager() {
        if (onPagerClose != null) {
            onPagerClose.onClose(this);
        }
    }

    /**
     * 更新点赞数
     *
     * @param praiseNum
     * @param withAnim  是否显示动画
     */
    private void upDatePraiseNum() {
        mCurrentNum++;
        if (onPraisePageListener != null) {
            onPraisePageListener.onPraiseClick(mCurrentNum);
        }
    }

    private void updatePraiseNum() {
       // TotalCurrentNum
        if (imgBtnPractice != null && imgBtnPractice.getVisibility() == View.VISIBLE) {
            StringBuilder sb = new StringBuilder();
            int total = mCurrentNum + totalCurrentNum;
            if (mCurrentNum > TEN_THOUSAND) {
                if (mCurrentNum % TEN_THOUSAND >= HUNDRED) {
                    BigDecimal bigDecimal = new BigDecimal(mCurrentNum / 10000.0f);
                    sb.append(bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()).append("万");
                } else {
                    sb.append(mCurrentNum / TEN_THOUSAND).append("万");
                }
            } else {
                sb.append(mCurrentNum);
            }
            tvPracticeCount.setText(sb.toString());
        }
    }

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
        llTeacherContent.setVisibility(View.GONE);
    }

    /**
     * 显示鼓励语
     */
    public void showEncouraging() {
        if (mHandler != null) {
            mHandler.sendEmptyMessageDelayed(PraiseConfig.ENCOURAGING_SHOW, 0);
        }
    }

    public void setPraiseTotal(int num) {
        totalCurrentNum = num;
        mHandler.sendEmptyMessageDelayed(PraiseConfig.PRAISE_TOTAL_SEND, 0);
    }

    private void showEncouragingView() {
        if (llTeacherContent != null) {
            llTeacherContent.setVisibility(View.VISIBLE);
        }
        if (mHandler != null) {
            mHandler.sendEmptyMessageDelayed(PraiseConfig.ENCOURAGING_HIDE, 2000);
        }

    }

}
