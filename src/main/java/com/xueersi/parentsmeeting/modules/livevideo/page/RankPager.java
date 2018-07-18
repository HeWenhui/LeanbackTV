package com.xueersi.parentsmeeting.modules.livevideo.page;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.xueersi.parentsmeeting.base.AbstractBusinessDataCallBack;
import com.xueersi.parentsmeeting.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.activity.item.RankItem;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBll;
import com.xueersi.parentsmeeting.modules.livevideo.entity.AllRankEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RankEntity;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.videoplayer.media.LiveMediaController;
import com.xueersi.xesalib.adapter.AdapterItemInterface;
import com.xueersi.xesalib.adapter.CommonAdapter;
import com.xueersi.xesalib.utils.app.XESToastUtils;

import java.util.ArrayList;

/**
 * Created by David on 2018/7/18.
 */

public class RankPager extends BasePager {
    Activity liveVideoActivity;
    AllRankEntity allRankEntity;
    int index = 1;
    ListView lv_livevideo_rank_list;
    LiveBll mliveBll;
    BaseLiveMediaControllerBottom liveMediaControllerBottom;
    int colorYellow;
    int colorWhite;
    Button rl_livevideo_common_rank;
    private View mRl_livevideo_rank_mygroup;
    private TextView mTv_livevideo_rank_mygroup;
    private View mV_livevideo_rank_mygroup;
    private View mRl_livevideo_rank_groups;
    private TextView mTv_livevideo_rank_groups;
    private View mV_livevideo_rank_groups;
    private View mRl_livevideo_rank_class;
    private TextView mTv_livevideo_rank_class;
    private View mV_livevideo_rank_class;
    private TextView mTv_livevideo_rank_subtitle_mid;
    /** 动画出现 */
    private Animation mAnimSlideIn;
    /** 动画隐藏 */
    private Animation mAnimSlideOut;
    private LiveMediaController mMediaController;
    private final int mF13232;
    private final int mWhite;
    private LinearLayout pagerContent;

    public RankPager(Context context, LiveBll liveBll,LiveMediaController mMediaController, Button rl_livevideo_common_rank){
        super(context);
        this.liveVideoActivity = (Activity) context;
        this.mliveBll = liveBll;
        this.mMediaController = mMediaController;
        this.rl_livevideo_common_rank = rl_livevideo_common_rank;
        colorYellow = liveVideoActivity.getResources().getColor(R.color.COLOR_FFFF00);
        colorWhite = liveVideoActivity.getResources().getColor(R.color.white);
        mF13232 = liveVideoActivity.getResources().getColor(R.color.COLOR_F13232);
        mWhite = liveVideoActivity.getResources().getColor(R.color.white);
        registerListener();
    }
    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.page_livevideo_rank, null);
        pagerContent = (LinearLayout)mView.findViewById(R.id.ll_rank_pager);
        //小组
        mRl_livevideo_rank_mygroup = mView.findViewById(R.id.rl_livevideo_rank_mygroup);
        mTv_livevideo_rank_mygroup = (TextView) mView.findViewById(R.id.tv_livevideo_rank_mygroup);
        mV_livevideo_rank_mygroup = mView.findViewById(R.id.v_livevideo_rank_mygroup);
        //组内
        mRl_livevideo_rank_groups = mView.findViewById(R.id.rl_livevideo_rank_groups);
        mTv_livevideo_rank_groups = (TextView) mView.findViewById(R.id.tv_livevideo_rank_groups);
        mV_livevideo_rank_groups = mView.findViewById(R.id.v_livevideo_rank_groups);
        //班级
        mRl_livevideo_rank_class = mView.findViewById(R.id.rl_livevideo_rank_class);
        mTv_livevideo_rank_class = (TextView) mView.findViewById(R.id.tv_livevideo_rank_class);
        mV_livevideo_rank_class = mView.findViewById(R.id.v_livevideo_rank_class);
        //下面标题中间的字
        mTv_livevideo_rank_subtitle_mid = (TextView) mView.findViewById(R.id.tv_livevideo_rank_subtitle_mid);
        lv_livevideo_rank_list = (ListView) mView.findViewById(R.id.lv_livevideo_rank_list);
        return mView;
    }

    private void registerListener() {

        mRl_livevideo_rank_mygroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index = 1;
                mTv_livevideo_rank_subtitle_mid.setText("学员");
                mV_livevideo_rank_mygroup.setVisibility(View.VISIBLE);
                mTv_livevideo_rank_mygroup.setTextColor(mF13232);
                mV_livevideo_rank_groups.setVisibility(View.GONE);
                mTv_livevideo_rank_groups.setTextColor(mWhite);
                mV_livevideo_rank_class.setVisibility(View.GONE);
                mTv_livevideo_rank_class.setTextColor(mWhite);
                if (allRankEntity == null) {
                    return;
                }
                ArrayList<RankEntity> rankEntities = allRankEntity.getMyRankEntityMyTeam().getRankEntities();
                lv_livevideo_rank_list.setAdapter(new CommonAdapter<RankEntity>(rankEntities) {
                    @Override
                    public AdapterItemInterface<RankEntity> getItemView(Object type) {
                        return new RankItem(colorYellow, colorWhite);
                    }
                });
            }
        });
        mRl_livevideo_rank_groups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index = 2;
                mTv_livevideo_rank_subtitle_mid.setText("组别");
                mV_livevideo_rank_mygroup.setVisibility(View.GONE);
                mTv_livevideo_rank_mygroup.setTextColor(mWhite);
                mV_livevideo_rank_groups.setVisibility(View.VISIBLE);
                mTv_livevideo_rank_groups.setTextColor(mF13232);
                mV_livevideo_rank_class.setVisibility(View.GONE);
                mTv_livevideo_rank_class.setTextColor(mWhite);
                if (allRankEntity == null) {
                    return;
                }
                ArrayList<RankEntity> rankEntities = allRankEntity.getMyRankEntityTeams().getRankEntities();
                lv_livevideo_rank_list.setAdapter(new CommonAdapter<RankEntity>(rankEntities) {
                    @Override
                    public AdapterItemInterface<RankEntity> getItemView(Object type) {
                        return new RankItem(colorYellow, colorWhite);
                    }
                });
            }
        });
        mRl_livevideo_rank_class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index = 3;
                mTv_livevideo_rank_subtitle_mid.setText("班级");
                mV_livevideo_rank_mygroup.setVisibility(View.GONE);
                mTv_livevideo_rank_mygroup.setTextColor(mWhite);
                mV_livevideo_rank_groups.setVisibility(View.GONE);
                mTv_livevideo_rank_groups.setTextColor(mWhite);
                mV_livevideo_rank_class.setVisibility(View.VISIBLE);
                mTv_livevideo_rank_class.setTextColor(mF13232);
                if (allRankEntity == null) {
                    return;
                }
                ArrayList<RankEntity> rankEntities = allRankEntity.getMyRankEntityClass().getRankEntities();
                lv_livevideo_rank_list.setAdapter(new CommonAdapter<RankEntity>(rankEntities) {
                    @Override
                    public AdapterItemInterface<RankEntity> getItemView(Object type) {
                        return new RankItem(colorYellow, colorWhite);
                    }
                });
            }
        });
        rl_livevideo_common_rank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initAnimation();
                mMediaController.show();
                if (pagerContent.getVisibility() == View.VISIBLE) {
                    pagerContent.startAnimation(mAnimSlideOut);
                } else {
                    if (mliveBll.getGetInfo() == null) {
                        XESToastUtils.showToast(liveVideoActivity, "请稍等");
                        return;
                    }
                    mliveBll.getAllRanking(new AbstractBusinessDataCallBack() {
                        @Override
                        public void onDataSucess(Object... objData) {
                            allRankEntity = (AllRankEntity) objData[0];
                            ArrayList<RankEntity> rankEntities;
                            if (index == 1) {
                                rankEntities = allRankEntity.getMyRankEntityMyTeam().getRankEntities();
                            } else if (index == 2) {
                                rankEntities = allRankEntity.getMyRankEntityTeams().getRankEntities();
                            } else {
                                rankEntities = allRankEntity.getMyRankEntityClass().getRankEntities();
                            }
                            lv_livevideo_rank_list.setAdapter(new CommonAdapter<RankEntity>(rankEntities) {
                                @Override
                                public AdapterItemInterface<RankEntity> getItemView(Object type) {
                                    return new RankItem(colorYellow, colorWhite);
                                }
                            });
                        }
                    });
                    pagerContent.setVisibility(View.VISIBLE);
                    pagerContent.startAnimation(mAnimSlideIn);
                }
            }
        });
    }

    @Override
    public void initData() {

    }

    public void initAnimation() {
        if (mAnimSlideIn == null) {
            mAnimSlideIn = AnimationUtils.loadAnimation(liveVideoActivity, R.anim.anim_livevideo_rank_in);
            mAnimSlideOut = AnimationUtils.loadAnimation(liveVideoActivity, R.anim.anim_livevideo_rank_out);
            mAnimSlideOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    pagerContent.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }

    public boolean onBack() {
        if (pagerContent != null && pagerContent.getVisibility() == View.VISIBLE) {
            pagerContent.startAnimation(mAnimSlideOut);
            return true;
        }
        return false;
    }

    public void onTitleShow(boolean show) {
        if (pagerContent != null && pagerContent.getVisibility() == View.VISIBLE) {
            pagerContent.startAnimation(mAnimSlideOut);
        }
    }
}
