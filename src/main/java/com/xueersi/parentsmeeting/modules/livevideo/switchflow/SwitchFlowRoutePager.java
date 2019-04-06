package com.xueersi.parentsmeeting.modules.livevideo.switchflow;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.MainThread;
import android.support.annotation.UiThread;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.xueersi.common.base.BasePager;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;

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

    /**
     * 可能在子线程
     */
    public void setRouteSum(int routeSum) {
        this.routeSum = routeSum;
        if (mView != null) {
            mView.post(new Runnable() {
                @Override
                public void run() {
                    initData();
                }
            });
        }
        logger.i("线路数量为" + routeSum);
//        init();
    }

    @MainThread
    public void init() {
        if (mView == null) {
            initView();
            initData();
        } else {
            initData();
        }

    }

    private int pattern;

    @Override
    public View initView() {
        isSmallEnglish = ((Activity) mContext).getIntent().getBooleanExtra("isSmallEnglish", false);
        pattern = ((Activity) mContext).getIntent().getIntExtra("pattern", 2);
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

    private RouteAdapter routeAdapter;

    /**
     * ui操作，必须在主线程中
     */
    @UiThread
    @Override
    public void initData() {
        if (listRoute == null) {
            listRoute = new ArrayList<>();
        } else {
            listRoute.clear();
        }
        for (int i = 0; i < routeSum; i++) {
            String strRoute = "";
            if (i == 0) {
                strRoute = "一";
            } else if (i == 1) {
                strRoute = "二";
            } else if (i == 2) {
                strRoute = "三";
            } else if (i == 3) {
                strRoute = "四";
            }
            listRoute.add("线路" + strRoute);
        }
        if (routeAdapter == null) {
            lvRoute.setSelector(R.color.transparent);
            routeAdapter = new RouteAdapter();
            lvRoute.setAdapter(routeAdapter);
//                    new CommonAdapter<String>(listRoute) {
//                FangZhengCuYuanTextView tvRoute;
//
//                @Override
//                public AdapterItemInterface<String> getItemView(Object type) {
//                    return new AdapterItemInterface<String>() {
//                        @Override
//                        public int getLayoutResId() {
//                            return R.layout.item_livevideo_triple_screen_switch_flow_route;
//                        }
//
//                        @Override
//                        public void initViews(View root) {
//                            tvRoute = root.findViewById(R.id.fzcy_livevideo_switch_flow_route_item);
//
////                        tvRoute.setTextColor();
//                        }
//
//                        @Override
//                        public void bindListener() {
//                            tvRoute.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    if (itemClickListener != null && listRoute != null) {
//                                        if (v instanceof FangZhengCuYuanTextView) {
//                                            FangZhengCuYuanTextView nowTvRoute = (FangZhengCuYuanTextView) v;
//                                            logger.i("tvRoute = " + nowTvRoute.getText());
//                                            nowPos = listRoute.indexOf(nowTvRoute.getText().toString());
//                                            logger.i("nowPos" + nowPos);
//                                            itemClickListener.itemClick(nowPos);
//
//                                            notifyDataSetChanged();
//                                        }
//
//                                    }
//                                }
//                            });
//                        }
//
//                        @Override
//                        public void updateViews(String entity, int position, Object objTag) {
//                        }
//                    };
//                }
//            }
//            );
            lvRoute.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position == nowPos) {
                        return;
                    }
                    if (itemClickListener != null && listRoute != null) {
                        logger.i("position = " + position);
//                        nowTvRoute =  view;
//                        logger.i("tvRoute = " + nowTvRoute.getText());
//                        nowPos = listRoute.indexOf(nowTvRoute.getText().toString());
//                        logger.i("nowPos" + nowPos);
                        nowPos = position;
                        UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string
                                .livevideo_switch_flow_1707013));
                        itemClickListener.itemClick(position);
                        routeAdapter.notifyDataSetChanged();
                    }
                }
            });
        } else {
            routeAdapter.notifyDataSetChanged();
        }
    }

    public interface ItemClickListener {
        void itemClick(int pos);
    }

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    private class RouteAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return listRoute.size();
        }

        @Override
        public Object getItem(int position) {
            return listRoute.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder mHolder = null;
            if (convertView == null) {
                mHolder = new ViewHolder();
                LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                if (isSmallEnglish || LiveVideoConfig.isPrimary || LiveVideoConfig.isSmallChinese) {
                    convertView = layoutInflater.inflate(R.layout.item_livevideo_triple_screen_switch_flow_route, null);
                    mHolder.tvRoute = convertView.findViewById(R.id.fzcy_livevideo_switch_flow_route_item);
                } else if (pattern == 1) {
                    convertView = layoutInflater.inflate(R.layout.item_livevideo_triple_screen_switch_flow_normal_route, null);
                    mHolder.tvRoute = convertView.findViewById(R.id.fzcy_livevideo_switch_flow_route_item);
                } else {
                    convertView = layoutInflater.inflate(R.layout.item_livevideo_triple_screen_switch_flow_route, null);
                    mHolder.tvRoute = convertView.findViewById(R.id.fzcy_livevideo_switch_flow_route_item);
                }

                convertView.setTag(mHolder);
            } else {
                mHolder = (ViewHolder) convertView.getTag();
            }
            if (listRoute != null && listRoute.size() > position) {
                mHolder.tvRoute.setText(listRoute.get(position));
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
                    mHolder.tvRoute.setTextColor(textColor);
                } else {
                    mHolder.tvRoute.setTextColor(nowTextColor);
                }
            }
//            mHolder.tvRoute.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (itemClickListener != null && listRoute != null) {
//                        FangZhengCuYuanTextView nowTvRoute = (FangZhengCuYuanTextView) v;
//                        logger.i("tvRoute = " + nowTvRoute.getText());
//                        nowPos = listRoute.indexOf(nowTvRoute.getText().toString());
//                        logger.i("nowPos" + nowPos);
//                        itemClickListener.itemClick(nowPos);
//                        notifyDataSetChanged();
//                    }
//                }
//            });


            return convertView;
        }

        private class ViewHolder {
            TextView tvRoute;
        }
    }
}
