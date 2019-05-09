package com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.evendrive;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.evendrive.EvenDriveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.AllRankEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.MyRankEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RankEntity;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.evendrive.itempager.ItemMiddleScienceEvenPager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.evendrive.itempager.ItemMiddleScienceGroupsPager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.evendrive.itempager.ItemMiddleSciencePager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.evendrive.itempager.ItemMiddleScienceRankPager;
import com.xueersi.ui.adapter.AdapterItemInterface;
import com.xueersi.ui.adapter.CommonAdapter;
import com.xueersi.ui.adapter.RCommonAdapter;

import java.util.List;

/**
 * 中学激励系统里面的连对页面
 */
public class MiddleScienceEvenDrivePager extends BasePager {
    /** 顶部标题 */
    private TextView tvMygroup, tvEven, tvGroups;
    /** 选中的下划线 */
    private View vMyGroup, vEvenLine, vGroupsLine;

    private ListView lvEven;
    /** tips:include使用的layout， */
    private ConstraintLayout evenDriveLayout, normalLayout;

    private TextView tvMiddleRight;
    //记录上一次点击的位置
    private int index = 1;


    public MiddleScienceEvenDrivePager(Context context) {
        super(context);
    }


    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.layout_livevideo_middle_science_even_drive, null);

        tvMygroup = view.findViewById(R.id.tv_livevideo_middle_science_mygroup);
        tvEven = view.findViewById(R.id.tv_livevideo_middle_science_even);
        tvGroups = view.findViewById(R.id.tv_livevideo_middle_science_groups);
        vMyGroup = view.findViewById(R.id.v_livevideo_rank_mygroup);
        vEvenLine = view.findViewById(R.id.v_livevideo_rank_groups);
        vGroupsLine = view.findViewById(R.id.v_livevideo_rank_class);

        evenDriveLayout = view.findViewById(R.id.include_livevideo_even_drive_tips_layout);
        normalLayout = view.findViewById(R.id.include_livevideo_rank_normal_tips_layout);

        tvMiddleRight = view.findViewById(R.id.tv_livevideo_middle_science_even_title_right);

        lvEven = view.findViewById(R.id.lv_livevideo_middle_science_list);
        return view;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {
//        mView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                logger.i("点击事件触发");
//            }
//        });
        //组内
        tvMygroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showView(1);
                if (allRankEntity == null || allRankEntity.getMyRankEntityMyTeam() == null || rankEntityAdapter == null) {
                    return;
                }
                lvEven.setAdapter(rankEntityAdapter);
            }
        });
        //连对
        tvEven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showView(2);
                if (evenDriveEntity == null || evenDriveEntityCommonAdapter == null) {
                    return;
                }
                lvEven.setAdapter(evenDriveEntityCommonAdapter);
            }
        });
        //小组
        tvGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showView(3);
                if (allRankEntity == null || allRankEntity.getMyRankEntityTeams() == null) {
                    return;
                }
                lvEven.setAdapter(teamsCommonAdapter);
            }
        });
    }

    /** 点击之后相应text显示 */
    private void showView(int type) {
        final int COLOR_F13232 = mContext.getResources().getColor(R.color.COLOR_F13232);
        final int white = mContext.getResources().getColor(R.color.white);
        this.index = type;
        if (type == 1) {
            vMyGroup.setVisibility(View.VISIBLE);
            vEvenLine.setVisibility(View.GONE);
            vGroupsLine.setVisibility(View.GONE);
            evenDriveLayout.setVisibility(View.VISIBLE);


            tvMygroup.setTextColor(COLOR_F13232);
            tvEven.setTextColor(white);
            tvGroups.setTextColor(white);

            normalLayout.setVisibility(View.GONE);


            tvMiddleRight.setText("正确率");
        } else if (type == 2) {
            vMyGroup.setVisibility(View.GONE);
            vEvenLine.setVisibility(View.VISIBLE);
            vGroupsLine.setVisibility(View.GONE);

            tvMygroup.setTextColor(white);
            tvEven.setTextColor(COLOR_F13232);
            tvGroups.setTextColor(white);

            evenDriveLayout.setVisibility(View.VISIBLE);
            normalLayout.setVisibility(View.GONE);
            tvMiddleRight.setText("连对");
        } else if (type == 3) {
            vMyGroup.setVisibility(View.GONE);
            vEvenLine.setVisibility(View.GONE);
            vGroupsLine.setVisibility(View.VISIBLE);

            tvMygroup.setTextColor(white);
            tvEven.setTextColor(white);
            tvGroups.setTextColor(COLOR_F13232);

            normalLayout.setVisibility(View.VISIBLE);

            ConstraintSet constraintSet = new ConstraintSet();

            constraintSet.clone((ConstraintLayout) mView);

//            constraintSet.connect(R.id.lv_livevideo_middle_science_list, ConstraintSet.TOP, R.id.include_livevideo_rank_normal_tips_layout, ConstraintSet.BOTTOM, SizeUtils.Dp2Px(mContext, 20));
            //这里必须设置成INVISIBLE，因为lvEven需要此布局来限制位置
            evenDriveLayout.setVisibility(View.INVISIBLE);
        }
    }

    /** 中学连对激励系统 */
    private CommonAdapter<EvenDriveEntity.OtherEntity> evenDriveEntityCommonAdapter;
    /** 中学激励系统组内排名 */
    private CommonAdapter<RankEntity> rankEntityAdapter;
    /** 小组内使用普通的Adapter */
    private CommonAdapter<RankEntity> teamsCommonAdapter;
    /** RecyclerView使用的View */
    private RCommonAdapter<EvenDriveEntity.OtherEntity> evenDriveEntityCommon;//= new RCommonAdapter<EvenDriveEntity.OtherEntity>()
    /** 组内和小组使用的entity */
    private AllRankEntity allRankEntity;
    /** 连对使用的Entity */
    private EvenDriveEntity evenDriveEntity;

    public void updateEvenData(final EvenDriveEntity evenDriveEntity) {
        this.evenDriveEntity = evenDriveEntity;
//        if (evenDriveEntityCommonAdapter == null) {
        evenDriveEntityCommonAdapter = new CommonAdapter<EvenDriveEntity.OtherEntity>(evenDriveEntity.getOtherEntities()) {
            @Override
            public AdapterItemInterface<EvenDriveEntity.OtherEntity> getItemView(Object type) {
                ItemMiddleScienceEvenPager itemMiddleScienceEvenPager = new ItemMiddleScienceEvenPager(mContext);
                itemMiddleScienceEvenPager.setiNotice(iNotice);
                itemMiddleScienceEvenPager.setMyStuId(evenDriveEntity.getMyEntity().getStuId());
                itemMiddleScienceEvenPager.setEndTime(endTime);
                itemMiddleScienceEvenPager.setH5Open(isH5Open);
                itemMiddleScienceEvenPager.setiClickSelf(new ItemMiddleSciencePager.IClickSelf() {
                    @Override
                    public void clickSelf() {
                        for (EvenDriveEntity.OtherEntity otherEntity : evenDriveEntity.getOtherEntities()) {
                            if (otherEntity.getStuId().equals(evenDriveEntity.getMyEntity().getStuId())) {
                                if (otherEntity.getIsThumbsUp() == 1) {
                                    otherEntity.setThumbsUpNum(otherEntity.getThumbsUpNum() + 1);
                                    otherEntity.setIsThumbsUp(0);
                                }
                            }
                        }
                        notifyDataSetChanged();
                    }
                });
                return itemMiddleScienceEvenPager;
            }
        };
//        } else {
//            evenDriveEntityCommonAdapter.updateData(evenDriveEntity.getOtherEntities());
//            evenDriveEntityCommonAdapter.notifyDataSetChanged();
//        }
        if (index == 2) {
            lvEven.setAdapter(evenDriveEntityCommonAdapter);
        }
    }

    public void updataRankData(final AllRankEntity allRankEntity) {
        if (allRankEntity == null) {
            return;
        }
        this.allRankEntity = allRankEntity;
//        lvEven.setAdapter(new);
        //排行榜组内的数据
//        if (rankEntityAdapter == null) {
        MyRankEntity myRankEntity = allRankEntity.getMyRankEntityMyTeam();

        if (myRankEntity != null) {
            List<RankEntity> rankEntities = myRankEntity.getRankEntities();
            for (RankEntity rankEntity : myRankEntity.getRankEntities()) {
                if (myRankEntity.getMyId().equals(rankEntity.getId())) {
                    rankEntities.add(0, rankEntity);
                    break;
                }
            }
            rankEntityAdapter = new CommonAdapter<RankEntity>(rankEntities) {
                @Override
                public AdapterItemInterface<RankEntity> getItemView(Object type) {
                    ItemMiddleScienceRankPager itemMiddleScienceRankPager = new ItemMiddleScienceRankPager(mContext);
                    itemMiddleScienceRankPager.setiNotice(iNotice);
                    itemMiddleScienceRankPager.setEndTime(endTime);
                    itemMiddleScienceRankPager.setH5Open(isH5Open);
                    itemMiddleScienceRankPager.setMyStuId(allRankEntity.getMyRankEntityMyTeam().getMyId());
                    itemMiddleScienceRankPager.setiClickSelf(new ItemMiddleSciencePager.IClickSelf() {
                        @Override
                        public void clickSelf() {
                            //所有自己的信息点赞数+1
                            for (RankEntity rankEntity : allRankEntity.getMyRankEntityMyTeam().getRankEntities()) {
                                String stuId = rankEntity.getId();
                                if (stuId.equals(allRankEntity.getMyRankEntityMyTeam().getMyId())) {
                                    if (rankEntity.getIsThumbsUp() == 1) {
                                        rankEntity.setIsThumbsUp(0);
                                        rankEntity.setThumbsUpNum(rankEntity.getThumbsUpNum() + 1);
                                    }
                                }
                            }
                            notifyDataSetChanged();
                        }
                    });
                    return itemMiddleScienceRankPager;
                }
            };
//        } else {
//            rankEntityAdapter.updateData(allRankEntity.getMyRankEntityMyTeam().getRankEntities());
//            rankEntityAdapter.notifyDataSetChanged();
//        }
        }
        final int colorYellow = mContext.getResources().getColor(R.color.COLOR_FFFF00);
        final int colorWhite = mContext.getResources().getColor(R.color.white);
        //排行榜小组的数据
        if (teamsCommonAdapter == null) {
            teamsCommonAdapter = new CommonAdapter<RankEntity>(allRankEntity.getMyRankEntityTeams().getRankEntities()) {
                @Override
                public AdapterItemInterface<RankEntity> getItemView(Object type) {
                    return new ItemMiddleScienceGroupsPager(colorYellow, colorWhite);
                }
            };
        } else {
            teamsCommonAdapter.updateData(allRankEntity.getMyRankEntityTeams().getRankEntities());
            teamsCommonAdapter.notifyDataSetChanged();
        }
        if (index == 1) {
            lvEven.setAdapter(rankEntityAdapter);
        }
        if (index == 3) {
            lvEven.setAdapter(teamsCommonAdapter);
        }
//                        itemMiddleScienceEvenPager(evenDriveEntity);
    }

    /** 收题时间 */
    private long endTime;

    public void setEndTime(long time) {
        this.endTime = time;
//        if (rankEntityAdapter != null) {
//            rankEntityAdapter.notifyDataSetChanged();
//        }
//        if (evenDriveEntityCommonAdapter != null) {
//            evenDriveEntityCommonAdapter.notifyDataSetChanged();
//        }
    }

    /**
     * H5课件是否处于打开状态
     */
    private boolean isH5Open = false;

    public boolean isH5Open() {
        return isH5Open;
    }

    public void setH5Open(boolean h5Open) {
        isH5Open = h5Open;
    }

    private ItemMiddleSciencePager.INotice iNotice;

    public void setiNotice(ItemMiddleSciencePager.INotice iNotice) {
        this.iNotice = iNotice;
    }
}
