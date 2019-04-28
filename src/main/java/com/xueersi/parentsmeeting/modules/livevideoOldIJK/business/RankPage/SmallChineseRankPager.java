package com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.RankPage;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.xueersi.common.base.BasePager;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.activity.item.SmallChineseRankItem;
import com.xueersi.parentsmeeting.modules.livevideo.entity.AllRankEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RankEntity;
import com.xueersi.parentsmeeting.widget.FangZhengCuYuanTextView;
import com.xueersi.ui.adapter.AdapterItemInterface;
import com.xueersi.ui.adapter.CommonAdapter;

import java.util.List;

public class SmallChineseRankPager extends BasePager<RankEntity> {

    private ListView lvRank;
    /** 选项,左边的组内，中间的小组，右边的班级 */
    private ImageView ivLeftSelect, ivMiddleSelect, ivRightSelect;
    /** 选项，左边的排名，成员，正确率 */
    private FangZhengCuYuanTextView tvLeft, tvMiddle, tvRight;
    /** 数据实体 */
    AllRankEntity allRankEntity;
    private List<RankEntity> mArtsRankEntities = null;
    private CommonAdapter<RankEntity> mArtsGroupCommonAdapter;
    /** 顶部的icon */
    private ImageView ivTopIcon;
    /** 选中了哪一个 0,1,2从左到右 */
    private int which = LEFT_SELECT;
    /** 背景图片 */
    private ImageView ivRankBackGround;

    private static final int LEFT_SELECT = 0;
    private static final int RIGHT_SELECT = 2;
    private static final int MID_SELECT = 1;

    public SmallChineseRankPager(Context context) {
        super(context);
//        this.allRankEntity = allRankEntity;
        //因为allRankEntity是异步获取的，所以
        initListener();
//        initData();
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_small_chinese_rank, null);
        lvRank = view.findViewById(R.id.lv_livevideo_small_chinese_rank);
        ivLeftSelect = view.findViewById(R.id.iv_livevideo_small_chinese_rank_left_btn);
        ivMiddleSelect = view.findViewById(R.id.iv_livevideo_small_chinese_rank_middle_btn);
        ivRightSelect = view.findViewById(R.id.iv_livevideo_small_chinese_rank_right_btn);
        tvLeft = view.findViewById(R.id.fzcytv_livevideo_small_chinese_left_text);
        tvMiddle = view.findViewById(R.id.fzcytv_livevideo_small_chinese_middle_text);
        tvRight = view.findViewById(R.id.fzcytv_livevideo_small_chinese_right_text);
        ivTopIcon = view.findViewById(R.id.iv_livevideo_small_chinese_rank_top_icon);
        ivRankBackGround = view.findViewById(R.id.iv_livevideo_small_chinese_rank_rank_background);
//        view.findViewById(R.id.fzcytv_livevideo_small_chinese_left_text).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                logger.i("点击了");
//            }
//        });
        dynamicChangeTopIcon(view);
//        dynamicChangeRankView();
        return view;
    }

    /** 转换的倍率 */
    private double mag;

    /** 动态调整排行榜背景高度 */
    private void dynamicChangeTopIcon(View view) {
        Drawable topIconDrawable = mContext.getResources().getDrawable(R.drawable.bg_livevideo_small_chinese_rank_top_icon);
        int topIconHeight = topIconDrawable.getIntrinsicHeight();
        int topIconWid = topIconDrawable.getIntrinsicWidth();
        LiveVideoPoint liveVideoPoint = LiveVideoPoint.getInstance();

        int ivWid = liveVideoPoint.x4 - liveVideoPoint.x3;
        double mag = ivWid * 1.0 / topIconWid;
        int ivRealHeight = (int) (mag * topIconHeight);
        //整个布局宽度都是ivWid;
        ViewGroup.LayoutParams rootParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rootParams.width = ivWid;
        view.setLayoutParams(rootParams);

        ViewGroup.LayoutParams layoutParams = ivTopIcon.getLayoutParams();
        layoutParams.width = ivWid;
        layoutParams.height = ivRealHeight;
        ivTopIcon.setLayoutParams(layoutParams);
        logger.i("wid = " + topIconWid + ", height = " + ", ivRealHeight = " + ivRealHeight + ", ivWid = " + ivWid + ",mag = " + mag);
        /** 三个btn按钮的高度 */
        ConstraintLayout.LayoutParams rankLayout = (ConstraintLayout.LayoutParams) ivLeftSelect.getLayoutParams();
        int btnTopMargin = (int) (SizeUtils.Dp2Px(mContext, 49) * mag);
        rankLayout.topMargin = btnTopMargin;
        ivLeftSelect.setLayoutParams(rankLayout);
        /** 修改排行榜背景的图片 */
        Drawable btnDrable = mContext.getResources().getDrawable(R.drawable.bg_livevideo_small_chinese_rank_btn_nor);
        int btnDrableHeight = btnDrable.getIntrinsicHeight();
        int rankBackGroundTopMargin = (int) (btnTopMargin + btnDrableHeight - SizeUtils.Dp2Px(mContext, 6));
        ConstraintLayout.LayoutParams backLayout = (ConstraintLayout.LayoutParams) ivRankBackGround.getLayoutParams();
        backLayout.topMargin = rankBackGroundTopMargin;
        ivRankBackGround.setLayoutParams(backLayout);
    }

    //
    public void setRankEntity(AllRankEntity allRankEntity) {
        this.allRankEntity = allRankEntity;
    }

    @Override
    public void initListener() {
        super.initListener();

        ivLeftSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select(true, false, false);
                which = LEFT_SELECT;
                tvMiddle.setText("学员");
                if (allRankEntity != null) {
                    mArtsRankEntities = allRankEntity.getMyRankEntityMyTeam().getRankEntities();
                    mArtsGroupCommonAdapter.updateData(mArtsRankEntities);
                }

            }
        });

        ivMiddleSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                which = MID_SELECT;
                select(false, true, false);
                tvMiddle.setText("组名");
//                ivMiddleSelect.setImageDrawable(mContext.getResources().getDrawable(R.drawable.bg_livevideo_small_chinese_rank_btn_click));
                if (allRankEntity != null) {
                    mArtsRankEntities = allRankEntity.getMyRankEntityTeams().getRankEntities();
                    mArtsGroupCommonAdapter.updateData(mArtsRankEntities);
                }
            }
        });
        ivRightSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                which = RIGHT_SELECT;
//                ivRightSelect.setImageDrawable(mContext.getResources().getDrawable(R.drawable.bg_livevideo_small_chinese_rank_btn_click));
                select(false, false, true);
                tvMiddle.setText("班级");
                if (allRankEntity != null) {
                    mArtsRankEntities = allRankEntity.getMyRankEntityClass().getRankEntities();
                    mArtsGroupCommonAdapter.updateData(mArtsRankEntities);
                }
            }
        });

    }

    @Override
    public void initData() {
        logger.i("获取到数据，成功复制");
        if (which == LEFT_SELECT) {
            mArtsRankEntities = allRankEntity.getMyRankEntityMyTeam().getRankEntities();
        } else if (which == MID_SELECT) {
            mArtsRankEntities = allRankEntity.getMyRankEntityTeams().getRankEntities();
        } else if (which == RIGHT_SELECT) {
            mArtsRankEntities = allRankEntity.getMyRankEntityClass().getRankEntities();
        } else {
            mArtsRankEntities = allRankEntity.getMyRankEntityMyTeam().getRankEntities();
        }
        final int colorMe = mContext.getResources().getColor(R.color.COLOR_005952);
        final int colorOther = mContext.getResources().getColor(R.color.COLOR_47827E);
//        RankEntity rankEntity = new RankEntity();
//        rankEntity.setRate("43%");
//        rankEntity.setMe(true);
//        rankEntity.setRank("1");
//        rankEntity.setName("张悦祎呃呃小");
//        mArtsRankEntities.add(rankEntity);
        mArtsGroupCommonAdapter = new CommonAdapter<RankEntity>(mArtsRankEntities) {
            @Override
            public AdapterItemInterface<RankEntity> getItemView(Object type) {
                return new SmallChineseRankItem(colorMe, colorOther);
            }
        };
        lvRank.setAdapter(mArtsGroupCommonAdapter);


    }

    /** 根据三个按钮(组内，小组，班级) */
    private void select(boolean left, boolean mid, boolean right) {
        logger.i("left = " + left + ", right = " + right + ", mid = " + mid);
        ivLeftSelect.setBackground(mContext.getResources().getDrawable(left ? R.drawable.bg_livevideo_small_chinese_rank_btn_nor : R.drawable.bg_livevideo_small_chinese_rank_btn_click));
        ivMiddleSelect.setBackground(mContext.getResources().getDrawable(mid ? R.drawable.bg_livevideo_small_chinese_rank_btn_nor : R.drawable.bg_livevideo_small_chinese_rank_btn_click));
        ivRightSelect.setBackground(mContext.getResources().getDrawable(right ? R.drawable.bg_livevideo_small_chinese_rank_btn_nor : R.drawable.bg_livevideo_small_chinese_rank_btn_click));
    }
}
