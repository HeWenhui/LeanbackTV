package com.xueersi.parentsmeeting.modules.livevideo.switchflow;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.xueersi.common.base.BasePager;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
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

    public SwitchFlowRoutePager(Context context, boolean isLazy) {
        super(context, isLazy);
    }

    /** 每次 */
    public void setRouteSum(int routeSum) {
        this.routeSum = routeSum;
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
        mView = View.inflate(mContext, R.layout.page_livevideo_triple_screen_switch_route, null);
        ivBackGround = mView.findViewById(R.id.iv_livevideo_small_chinese_play_achievement_board);
        ivTopIcon = mView.findViewById(R.id.iv_livevideo_triple_screen_switch_flow_route_top_icon);
        ivBackGroundTopIcon = mView.findViewById(R.id.iv_livevideo_small_chinese_live_message_background_top);
        lvRoute = mView.findViewById(R.id.lv_livevideo_triple_screen_switch_route);
        dynamicChangeTopIcon();
        return mView;
    }

    /** 动态调整背景高度 */
    private void dynamicChangeTopIcon() {
        LiveVideoPoint liveVideoPoint = LiveVideoPoint.getInstance();

        Drawable topIconDrawable = mContext.getResources().getDrawable(R.drawable.bg_livevideo_small_chinese_rank_top_icon);
        int topIconHeight = topIconDrawable.getIntrinsicHeight();
        int topIconWid = topIconDrawable.getIntrinsicWidth();


        int ivWid = liveVideoPoint.x4 - liveVideoPoint.x3;
        double mag = ivWid * 1.0 / topIconWid;
        int ivRealHeight = (int) (mag * topIconHeight);

        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) ivTopIcon.getLayoutParams();
        layoutParams.width = ivWid;
        layoutParams.height = ivRealHeight;
        ivTopIcon.setLayoutParams(layoutParams);
        logger.i("wid = " + topIconWid + ", height = " + ", ivRealHeight = " + ivRealHeight + ", ivWid = " + ivWid + ",mag = " + mag);
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

        listRoute = new ArrayList<>();
        for (int i = 0; i < routeSum; i++) {
            listRoute.add("线路" + String.valueOf(i));
        }
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
                    }

                    @Override
                    public void bindListener() {
                        tvRoute.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (itemClickListener != null) {
                                    itemClickListener.itemClick(listRoute.indexOf(tvRoute.getText()));
                                }
                            }
                        });
                    }

                    @Override
                    public void updateViews(String entity, int position, Object objTag) {
                        tvRoute.setText(listRoute.get(position));
                    }
                };
            }
        });
    }

    public interface ItemClickListener {
        void itemClick(int pos);
    }

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
