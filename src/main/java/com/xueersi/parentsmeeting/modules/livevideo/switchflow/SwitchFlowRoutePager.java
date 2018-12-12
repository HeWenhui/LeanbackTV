package com.xueersi.parentsmeeting.modules.livevideo.switchflow;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.xueersi.common.base.BasePager;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.widget.FangZhengCuYuanTextView;
import com.xueersi.ui.adapter.AdapterItemInterface;
import com.xueersi.ui.adapter.CommonAdapter;

import java.util.ArrayList;
import java.util.List;

public class SwitchFlowRoutePager extends BasePager {
    /** 出现时从右往左侧滑的动画 */
//    private ObjectAnimator animationIn;

    /** 消失时从左往右侧滑的动画 */
//    private ObjectAnimator animationOut;
    /** 背景图片 */
    private ImageView ivBackGround;
    /** 顶部图片 */
    private ImageView ivTopIcon;
    /** 背景图片的顶部图片 */
    private ImageView ivBackGroundTopIcon;

    private int routeSum;

    private ListView lvRoute;

    private List<String> listRoute;

    private int nowPos = 0;

    private int nowTextColor;
    private int textColor;

    private boolean isSmallEnglish;

    public SwitchFlowRoutePager(Context context, boolean isLazy) {
        super(context, isLazy);
    }

    /** 每次 */
    public void setRouteSum(int routeSum) {
        this.routeSum = routeSum;
        initData();
        logger.i("线路数量为" + routeSum);
//        init();
    }

    public void init() {
        if (mView == null) {
            initView();
            initData();
        } else {
            initData();
        }

    }

    @Override
    public View initView() {
        isSmallEnglish = ((Activity) mContext).getIntent().getBooleanExtra("isSmallEnglish", false);
        if (LiveVideoConfig.isSmallChinese) {
            mView = View.inflate(mContext, R.layout.page_livevideo_triple_screen_switch_route, null);
            ivBackGround = mView.findViewById(R.id.iv_livevideo_small_chinese_play_achievement_board);
            ivTopIcon = mView.findViewById(R.id.iv_livevideo_triple_screen_switch_flow_route_top_icon);
            ivBackGroundTopIcon = mView.findViewById(R.id.iv_livevideo_small_chinese_live_message_background_top);
            lvRoute = mView.findViewById(R.id.lv_livevideo_triple_screen_switch_route);
            dynamicChangeTopIcon();

        } else if (isSmallEnglish) {
            mView = View.inflate(mContext, R.layout.page_livevideo_triple_screen_switch_small_enlgish_route, null);
            lvRoute = mView.findViewById(R.id.lv_livevideo_triple_screen_switch_route);
        } else if (LiveVideoConfig.isPrimary) {
            mView = View.inflate(mContext, R.layout.page_livevideo_triple_screen_switch_small_science_route, null);
            lvRoute = mView.findViewById(R.id.lv_livevideo_triple_screen_switch_route);
        } else {
            mView = View.inflate(mContext, R.layout.page_livevideo_triple_screen_switch_normal_route, null);
            lvRoute = mView.findViewById(R.id.lv_livevideo_triple_screen_switch_route);

        }

        return mView;
    }

    /** 动态调整背景高度 */
    private void dynamicChangeTopIcon() {
        LiveVideoPoint liveVideoPoint = LiveVideoPoint.getInstance();
        int ivRealWid = liveVideoPoint.x4 - liveVideoPoint.x3;

        Drawable topIconDrawable = mContext.getResources().getDrawable(R.drawable.bg_livevideo_small_chinese_rank_top_icon);
        int topIconHeight = topIconDrawable.getIntrinsicHeight();
        int topIconWid = topIconDrawable.getIntrinsicWidth();

        double mag = ivRealWid * 1.0 / topIconWid;
        int ivRealHeight = (int) (mag * topIconHeight);

        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) ivTopIcon.getLayoutParams();
        layoutParams.width = ivRealWid;
        layoutParams.height = ivRealHeight;
        ivTopIcon.setLayoutParams(layoutParams);
        logger.i("wid = " + topIconWid + ", height = " + ", ivRealHeight = " + ivRealHeight + ", ivWid = " + ivRealWid + ",mag = " + mag);
        /** btn按钮的高度 */
        ConstraintLayout.LayoutParams rankLayout = (ConstraintLayout.LayoutParams) ivBackGroundTopIcon.getLayoutParams();
        int btnTopMargin = (int) (SizeUtils.Dp2Px(mContext, 49) * mag);
        rankLayout.topMargin = btnTopMargin;
        ivBackGroundTopIcon.setLayoutParams(rankLayout);
        /** 修改排行榜背景的图片 */
        Drawable btnDrable = mContext.getResources().getDrawable(R.drawable.bg_livevideo_small_chinese_rank_btn_nor);
        int btnDrableHeight = btnDrable.getIntrinsicHeight();
        int rankBackGroundTopMargin = (int) (btnTopMargin + btnDrableHeight - SizeUtils.Dp2Px(mContext, 6));
        ConstraintLayout.LayoutParams backLayout = (ConstraintLayout.LayoutParams) ivBackGround.getLayoutParams();
        backLayout.topMargin = rankBackGroundTopMargin;
        ivBackGround.setLayoutParams(backLayout);
    }


    @Override
    public void initData() {
        if (listRoute == null) {
            listRoute = new ArrayList<>();
        } else {
            listRoute.clear();
        }
        for (int i = 0; i < routeSum; i++) {
            listRoute.add("线路" + String.valueOf(i + 1));
        }
        if (lvRoute.getAdapter() == null) {
            lvRoute.setAdapter(new CommonAdapter<String>(listRoute) {
                FangZhengCuYuanTextView tvRoute;

                @Override
                public AdapterItemInterface<String> getItemView(Object type) {
                    return new AdapterItemInterface<String>() {
                        @Override
                        public int getLayoutResId() {
                            return R.layout.item_livevideo_triple_screen_switch_flow_route;
                        }

                        @Override
                        public void initViews(View root) {
                            tvRoute = root.findViewById(R.id.fzcy_livevideo_switch_flow_route_item);

//                        tvRoute.setTextColor();
                        }

                        @Override
                        public void bindListener() {
                            tvRoute.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (itemClickListener != null && listRoute != null) {
                                        nowPos = listRoute.indexOf(tvRoute.getText());
                                        itemClickListener.itemClick(nowPos);
                                        notifyDataSetChanged();
                                    }
                                }
                            });
                        }

                        @Override
                        public void updateViews(String entity, int position, Object objTag) {
                            if (listRoute != null && listRoute.size() > position) {
                                tvRoute.setText(listRoute.get(position));
                                int textColor = mContext.getResources().getColor(R.color.COLOR_008B97);
                                if (LiveVideoConfig.isSmallChinese) {
                                    textColor = mContext.getResources().getColor(R.color.COLOR_008B97);
                                    nowTextColor = mContext.getResources().getColor(R.color.COLOR_005952);
                                } else if (LiveVideoConfig.isPrimary) {
                                    textColor = mContext.getResources().getColor(R.color.COLOR_FFFFFF);
                                    nowTextColor = mContext.getResources().getColor(R.color.COLOR_FF6326);
                                } else if (isSmallEnglish) {
                                    textColor = mContext.getResources().getColor(R.color.COLOR_C3DAFF);
                                    nowTextColor = mContext.getResources().getColor(R.color.COLOR_FFB400);
                                } else {
                                    textColor = mContext.getResources().getColor(R.color.COLOR_FFFFFF);
                                    nowTextColor = mContext.getResources().getColor(R.color.COLOR_F13232);
                                }
                                if (nowPos != position) {
                                    tvRoute.setTextColor(textColor);
                                } else {
                                    tvRoute.setTextColor(nowTextColor);
                                }
                            }
                        }
                    };
                }
            });
        } else {
            ((BaseAdapter) lvRoute.getAdapter()).notifyDataSetChanged();
        }

    }

    public interface ItemClickListener {
        void itemClick(int pos);
    }

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
