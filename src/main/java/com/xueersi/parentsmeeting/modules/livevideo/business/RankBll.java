package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.activity.item.RankItem;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;

import com.xueersi.parentsmeeting.modules.livevideo.entity.AllRankEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RankEntity;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveStandMediaControllerBottom;
import com.xueersi.ui.adapter.AdapterItemInterface;
import com.xueersi.ui.adapter.CommonAdapter;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by lyqai on 2017/9/20.
 */

public class RankBll extends LiveBaseBll implements BaseLiveMediaControllerBottom.MediaChildViewClick {
    Logger logger = LoggerFactory.getLogger("RankBll");
    LiveMediaController mMediaController;
    BaseLiveMediaControllerBottom liveMediaControllerBottom;
    Button rl_livevideo_common_rank;//排名
    View relativeLayout;
    View tempView;
    /** 动画出现 */
    private Animation mAnimSlideIn;
    /** 动画隐藏 */
    private Animation mAnimSlideOut;
    AllRankEntity allRankEntity;
    int index = 1;
    ListView lv_livevideo_rank_list;
    int colorYellow;
    int colorWhite;

    private Boolean isSmallEnglish = false;

    public RankBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
        colorYellow = context.getResources().getColor(R.color.COLOR_FFFF00);
        colorWhite = context.getResources().getColor(R.color.white);
    }

    private void initAnimation() {
        if (mAnimSlideIn == null) {
            mAnimSlideIn = AnimationUtils.loadAnimation(activity, R.anim.anim_livevideo_rank_in);
            mAnimSlideOut = AnimationUtils.loadAnimation(activity, R.anim.anim_livevideo_rank_out);
            mAnimSlideOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    relativeLayout.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }

    @Override
    public void onCreate(HashMap<String, Object> data) {
        super.onCreate(data);
        mMediaController = (LiveMediaController) data.get("mMediaController");
        BaseLiveMediaControllerBottom controllerBottom = (BaseLiveMediaControllerBottom) data.get
                ("liveMediaControllerBottom");
        setLiveMediaController(mMediaController, controllerBottom);
    }

    public void setLiveMediaController(final LiveMediaController mMediaController, BaseLiveMediaControllerBottom
            liveMediaControllerBottom) {
        this.mMediaController = mMediaController;
        this.liveMediaControllerBottom = liveMediaControllerBottom;
        if (liveMediaControllerBottom instanceof LiveStandMediaControllerBottom) {
            LiveStandMediaControllerBottom liveStandMediaControllerBottom = (LiveStandMediaControllerBottom)
                    liveMediaControllerBottom;
            liveStandMediaControllerBottom.addOnViewChange(onViewChange);
        }
        rl_livevideo_common_rank = (Button) liveMediaControllerBottom.findViewById(R.id.rl_livevideo_common_rank);
        if (rl_livevideo_common_rank == null) {
            return;
        }
        rl_livevideo_common_rank.setVisibility(View.VISIBLE);
        rl_livevideo_common_rank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initAnimation();
                mMediaController.show();
                if (relativeLayout.getVisibility() == View.VISIBLE) {
                    relativeLayout.startAnimation(mAnimSlideOut);
                } else {
                    if (mGetInfo == null) {
                        XESToastUtils.showToast(activity, "请稍等");
                        return;
                    }
                    String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
                    String classId = "";
                    if (mGetInfo.getStudentLiveInfo() != null) {
                        classId = mGetInfo.getStudentLiveInfo().getClassId();
                    }
                    getAllRanking(new AbstractBusinessDataCallBack() {

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
                                    return new RankItem(colorYellow, colorWhite, isSmallEnglish);
                                }
                            });
                        }
                    });
                    relativeLayout.setVisibility(View.VISIBLE);
                    relativeLayout.startAnimation(mAnimSlideIn);
                }
            }
        });
    }

    private LiveStandMediaControllerBottom.OnViewChange onViewChange = new LiveStandMediaControllerBottom
            .OnViewChange() {
        @Override
        public void onViewChange(BaseLiveMediaControllerBottom baseLiveMediaControllerBottom) {
            setLiveMediaController(mMediaController, baseLiveMediaControllerBottom);
        }
    };

    public void getAllRanking(final AbstractBusinessDataCallBack callBack) {
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        String classId = "";
        if (mGetInfo.getStudentLiveInfo() != null) {
            classId = mGetInfo.getStudentLiveInfo().getClassId();
        }
        getHttpManager().getAllRanking(enstuId, mGetInfo.getId(), classId, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                AllRankEntity allRankEntity = getHttpResponseParser().parseAllRank(responseEntity);
                callBack.onDataSucess(allRankEntity);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                super.onPmError(responseEntity);
                logger.e("getAllRanking:onPmError" + responseEntity.getErrorMsg());
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
                logger.e("getAllRanking:onPmFailure" + msg);
            }
        });
    }

    public void setGetInfo(LiveGetInfo getInfo) {
        this.mGetInfo = getInfo;
        if (mGetInfo != null) {
            isSmallEnglish = mGetInfo.getSmallEnglish();
        }
    }

    @Override
    public void initView(RelativeLayout bottomContent, AtomicBoolean mIsLand) {
        super.initView(bottomContent, mIsLand);
        tempView = new View(activity);
        bottomContent.addView(tempView);
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        initView(mRootView);
        BaseLiveMediaControllerBottom.RegMediaChildViewClick regMediaChildViewClick = ProxUtil.getProxUtil().get
                (activity, BaseLiveMediaControllerBottom.RegMediaChildViewClick.class);
        if (regMediaChildViewClick != null) {
            regMediaChildViewClick.regMediaViewClick(this);
        }
        RegMediaPlayerControl regMediaPlayerControl = getInstance(RegMediaPlayerControl.class);
        regMediaPlayerControl.addMediaPlayerControl(new LiveMediaController.SampleMediaPlayerControl() {
            @Override
            public void onTitleShow(boolean show) {
                RankBll.this.onTitleShow(show);
            }
        });
    }

    private List<RankEntity> mArtsRankEntities = null;
    private CommonAdapter<RankEntity> mArtsGroupCommonAdapter;

    public void initView(final RelativeLayout bottomContent) {
        //小英
        Log.i("testRankBll", mGetInfo.getGrade() + " " + mGetInfo.getIsArts());
        mArtsGroupCommonAdapter = new CommonAdapter<RankEntity>(mArtsRankEntities) {
            @Override
            public AdapterItemInterface<RankEntity> getItemView(Object type) {
                return new RankItem(colorYellow, colorWhite, isSmallEnglish);
            }
        };
        if (mGetInfo != null) {
            isSmallEnglish = mGetInfo.getSmallEnglish();
        }
        if (isSmallEnglish) {
//            Log.i("testRankBll", mGetInfo.getGrade() + " " + mGetInfo.getIsArts());
            relativeLayout = LayoutInflater.from(activity).inflate(R.layout.layout_livevideo_small_english_rank,
                    bottomContent, false);
            //小组
            final ImageView ivMyGroup = relativeLayout.findViewById(R.id.iv_livevideo_small_english_rank_mygroup);
            //组内
            final ImageView ivGroups = relativeLayout.findViewById(R.id.iv_livevideo_small_english_rank_groups);
            //班级
            final ImageView ivClass = relativeLayout.findViewById(R.id.iv_livevideo_small_english_rank_class);
            //标题下面的字
            final TextView ivRankId = relativeLayout.findViewById(R.id.tv_livevideo_rank_subtitle_mid);
            Button btnMyGroup = relativeLayout.findViewById(R.id.btn_livevideo_small_english_rank_mygroup);
            Button btnGroups = relativeLayout.findViewById(R.id.btn_livevideo_small_english_rank_groups);
            Button btnClass = relativeLayout.findViewById(R.id.btn_livevideo_small_english_rank_class);
            //展现排行榜的listview
            lv_livevideo_rank_list = relativeLayout.findViewById(R.id.lv_livevideo_rank_list);
            //组内
            btnMyGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ivMyGroup.setVisibility(View.VISIBLE);
                    ivGroups.setVisibility(View.GONE);
                    ivClass.setVisibility(View.GONE);
                    ivRankId.setText("学员");
                    if (allRankEntity == null) {
                        return;
                    }

                    mArtsRankEntities = allRankEntity.getMyRankEntityMyTeam().getRankEntities();
                    mArtsGroupCommonAdapter.updateData(mArtsRankEntities);//一定要先更新在
                    if (lv_livevideo_rank_list.getAdapter() == null || lv_livevideo_rank_list.getAdapter() !=
                            mArtsGroupCommonAdapter) {
                        lv_livevideo_rank_list.setAdapter(mArtsGroupCommonAdapter);
                    }
                }
            });
//小组
            btnGroups.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ivMyGroup.setVisibility(View.GONE);
                    ivGroups.setVisibility(View.VISIBLE);
                    ivClass.setVisibility(View.GONE);
                    ivRankId.setText("组别");
                    if (allRankEntity == null) {
                        return;
                    }
                    mArtsRankEntities = allRankEntity.getMyRankEntityTeams().getRankEntities();
                    mArtsGroupCommonAdapter.updateData(mArtsRankEntities);
                    if (lv_livevideo_rank_list.getAdapter() == null || lv_livevideo_rank_list.getAdapter() !=
                            mArtsGroupCommonAdapter) {
                        lv_livevideo_rank_list.setAdapter(mArtsGroupCommonAdapter);
                    }
                }
            });
//班级
            btnClass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ivMyGroup.setVisibility(View.GONE);
                    ivGroups.setVisibility(View.GONE);
                    ivClass.setVisibility(View.VISIBLE);
                    ivRankId.setText("班级");
                    if (allRankEntity == null) {
                        return;
                    }
                    mArtsRankEntities = allRankEntity.getMyRankEntityClass().getRankEntities();
                    mArtsGroupCommonAdapter.updateData(mArtsRankEntities);
                    if (lv_livevideo_rank_list.getAdapter() == null || lv_livevideo_rank_list.getAdapter() !=
                            mArtsGroupCommonAdapter) {
                        lv_livevideo_rank_list.setAdapter(mArtsGroupCommonAdapter);
                    }
                }
            });
        } else {
            relativeLayout = LayoutInflater.from(activity).inflate(R.layout.layout_livevodeo_rank, bottomContent,
                    false);

            //小组
            View rl_livevideo_rank_mygroup = relativeLayout.findViewById(R.id.rl_livevideo_rank_mygroup);
            final TextView tv_livevideo_rank_mygroup = (TextView) relativeLayout.findViewById(R.id
                    .tv_livevideo_rank_mygroup);
            final View v_livevideo_rank_mygroup = relativeLayout.findViewById(R.id.v_livevideo_rank_mygroup);
            //组内
            View rl_livevideo_rank_groups = relativeLayout.findViewById(R.id.rl_livevideo_rank_groups);
            final TextView tv_livevideo_rank_groups = (TextView) relativeLayout.findViewById(R.id
                    .tv_livevideo_rank_groups);

            final View v_livevideo_rank_groups = relativeLayout.findViewById(R.id.v_livevideo_rank_groups);
            //班级
            View rl_livevideo_rank_class = relativeLayout.findViewById(R.id.rl_livevideo_rank_class);
            final TextView tv_livevideo_rank_class = (TextView) relativeLayout.findViewById(R.id
                    .tv_livevideo_rank_class);
            final View v_livevideo_rank_class = relativeLayout.findViewById(R.id.v_livevideo_rank_class);
            //下面标题中间的字
            final TextView tv_livevideo_rank_subtitle_mid = (TextView) relativeLayout.findViewById(R.id
                    .tv_livevideo_rank_subtitle_mid);
            lv_livevideo_rank_list = relativeLayout.findViewById(R.id.lv_livevideo_rank_list);
//        ArrayList<RankEntity> rankEntities = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            RankEntity rankEntity = new RankEntity();
//            rankEntity.setRank(i + 1);
//            rankEntity.setName("王星" + i);
//            rankEntity.setRate((100 - i) + "%");
//            rankEntities.add(rankEntity);
//        }
            final int COLOR_F13232 = activity.getResources().getColor(R.color.COLOR_F13232);
            final int white = activity.getResources().getColor(R.color.white);
            rl_livevideo_rank_mygroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    index = 1;
                    tv_livevideo_rank_subtitle_mid.setText("学员");
                    v_livevideo_rank_mygroup.setVisibility(View.VISIBLE);
                    tv_livevideo_rank_mygroup.setTextColor(COLOR_F13232);
                    v_livevideo_rank_groups.setVisibility(View.GONE);
                    tv_livevideo_rank_groups.setTextColor(white);
                    v_livevideo_rank_class.setVisibility(View.GONE);
                    tv_livevideo_rank_class.setTextColor(white);
                    if (allRankEntity == null) {
                        return;
                    }
                    ArrayList<RankEntity> rankEntities = allRankEntity.getMyRankEntityMyTeam().getRankEntities();
                    lv_livevideo_rank_list.setAdapter(new CommonAdapter<RankEntity>(rankEntities) {
                        @Override
                        public AdapterItemInterface<RankEntity> getItemView(Object type) {
                            return new RankItem(colorYellow, colorWhite, isSmallEnglish);
                        }
                    });
                }
            });
            rl_livevideo_rank_groups.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    index = 2;
                    tv_livevideo_rank_subtitle_mid.setText("组别");
                    v_livevideo_rank_mygroup.setVisibility(View.GONE);
                    tv_livevideo_rank_mygroup.setTextColor(white);
                    v_livevideo_rank_groups.setVisibility(View.VISIBLE);
                    tv_livevideo_rank_groups.setTextColor(COLOR_F13232);
                    v_livevideo_rank_class.setVisibility(View.GONE);
                    tv_livevideo_rank_class.setTextColor(white);
                    if (allRankEntity == null) {
                        return;
                    }
                    ArrayList<RankEntity> rankEntities = allRankEntity.getMyRankEntityTeams().getRankEntities();
                    lv_livevideo_rank_list.setAdapter(new CommonAdapter<RankEntity>(rankEntities) {
                        @Override
                        public AdapterItemInterface<RankEntity> getItemView(Object type) {
                            return new RankItem(colorYellow, colorWhite, isSmallEnglish);
                        }
                    });
                }
            });
            rl_livevideo_rank_class.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    index = 3;
                    tv_livevideo_rank_subtitle_mid.setText("班级");
                    v_livevideo_rank_mygroup.setVisibility(View.GONE);
                    tv_livevideo_rank_mygroup.setTextColor(white);
                    v_livevideo_rank_groups.setVisibility(View.GONE);
                    tv_livevideo_rank_groups.setTextColor(white);
                    v_livevideo_rank_class.setVisibility(View.VISIBLE);
                    tv_livevideo_rank_class.setTextColor(COLOR_F13232);
                    if (allRankEntity == null) {
                        return;
                    }
                    ArrayList<RankEntity> rankEntities = allRankEntity.getMyRankEntityClass().getRankEntities();
                    lv_livevideo_rank_list.setAdapter(new CommonAdapter<RankEntity>(rankEntities) {
                        @Override
                        public AdapterItemInterface<RankEntity> getItemView(Object type) {
                            return new RankItem(colorYellow, colorWhite, isSmallEnglish);
                        }
                    });
                }
            });
        }
        //把该布局加到排行榜的右边
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
//        relativeLayout.setBackgroundColor(liveVideoActivity.getResources().getColor(R.color.translucent_black));
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        int index = bottomContent.indexOfChild(tempView);
        bottomContent.removeViewInLayout(tempView);
        bottomContent.addView(relativeLayout, index, lp);
        setVideoLayout();
    }

    public boolean onBack() {
        if (relativeLayout != null && relativeLayout.getVisibility() == View.VISIBLE) {
            relativeLayout.startAnimation(mAnimSlideOut);
            return true;
        }
        return false;
    }

    public void onTitleShow(boolean show) {
        if (relativeLayout != null && relativeLayout.getVisibility() == View.VISIBLE && mAnimSlideOut != null) {
            relativeLayout.startAnimation(mAnimSlideOut);
        }
    }

    @Override
    public void setVideoLayout(LiveVideoPoint liveVideoPoint) {
        super.setVideoLayout(liveVideoPoint);
        setVideoLayout();
    }

    public void setVideoLayout() {
        if (relativeLayout == null) {
            return;
        }
        LiveVideoPoint liveVideoPoint = LiveVideoPoint.getInstance();
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) relativeLayout.getLayoutParams();
        int wradio = liveVideoPoint.getRightMargin();
        if (wradio != params.width) {
            //Loger.e(TAG, "setVideoWidthAndHeight:screenWidth=" + screenWidth + ",width=" + width + "," + height
            // + ",wradio=" + wradio + "," + params.width);
            params.width = wradio;
//                relativeLayout.setLayoutParams(params);
            LayoutParamsUtil.setViewLayoutParams(relativeLayout, params);
        }
    }

    @Override
    public void onMediaViewClick(View child) {
        onTitleShow(true);
    }
}
