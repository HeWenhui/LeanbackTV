package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.common.business.UserBll;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.activity.item.RankItem;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.entity.AllRankEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RankEntity;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveStandMediaControllerBottom;
import com.xueersi.ui.adapter.AdapterItemInterface;
import com.xueersi.ui.adapter.CommonAdapter;
import com.xueersi.lib.framework.utils.ScreenUtils;

import java.util.ArrayList;

/**
 * Created by lyqai on 2017/9/20.
 */

public class RankBll extends LiveBaseBll {
    Logger logger = LoggerFactory.getLogger("RankBll");
    LiveMediaController mMediaController;
    BaseLiveMediaControllerBottom liveMediaControllerBottom;
    Button rl_livevideo_common_rank;
    View relativeLayout;
    /** 动画出现 */
    private Animation mAnimSlideIn;
    /** 动画隐藏 */
    private Animation mAnimSlideOut;
    AllRankEntity allRankEntity;
    int index = 1;
    ListView lv_livevideo_rank_list;
    int colorYellow;
    int colorWhite;

    public RankBll(Activity context, LiveBll2 liveBll, RelativeLayout rootView) {
        super(context, liveBll, rootView);
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

    public void setLiveMediaController(final LiveMediaController mMediaController, BaseLiveMediaControllerBottom liveMediaControllerBottom) {
        this.mMediaController = mMediaController;
        this.liveMediaControllerBottom = liveMediaControllerBottom;
        if (liveMediaControllerBottom instanceof LiveStandMediaControllerBottom) {
            LiveStandMediaControllerBottom liveStandMediaControllerBottom = (LiveStandMediaControllerBottom) liveMediaControllerBottom;
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
                                    return new RankItem(colorYellow, colorWhite);
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

    private LiveStandMediaControllerBottom.OnViewChange onViewChange = new LiveStandMediaControllerBottom.OnViewChange() {
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
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        initView(mRootView);
    }

    public void initView(final RelativeLayout bottomContent) {
        relativeLayout = LayoutInflater.from(activity).inflate(R.layout.layout_livevodeo_rank, bottomContent, false);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
//        relativeLayout.setBackgroundColor(liveVideoActivity.getResources().getColor(R.color.translucent_black));
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        bottomContent.addView(relativeLayout, lp);
        setVideoLayout();
        //小组
        View rl_livevideo_rank_mygroup = relativeLayout.findViewById(R.id.rl_livevideo_rank_mygroup);
        final TextView tv_livevideo_rank_mygroup = (TextView) relativeLayout.findViewById(R.id.tv_livevideo_rank_mygroup);
        final View v_livevideo_rank_mygroup = relativeLayout.findViewById(R.id.v_livevideo_rank_mygroup);
        //组内
        View rl_livevideo_rank_groups = relativeLayout.findViewById(R.id.rl_livevideo_rank_groups);
        final TextView tv_livevideo_rank_groups = (TextView) relativeLayout.findViewById(R.id.tv_livevideo_rank_groups);
        final View v_livevideo_rank_groups = relativeLayout.findViewById(R.id.v_livevideo_rank_groups);
        //班级
        View rl_livevideo_rank_class = relativeLayout.findViewById(R.id.rl_livevideo_rank_class);
        final TextView tv_livevideo_rank_class = (TextView) relativeLayout.findViewById(R.id.tv_livevideo_rank_class);
        final View v_livevideo_rank_class = relativeLayout.findViewById(R.id.v_livevideo_rank_class);
        //下面标题中间的字
        final TextView tv_livevideo_rank_subtitle_mid = (TextView) relativeLayout.findViewById(R.id.tv_livevideo_rank_subtitle_mid);
        lv_livevideo_rank_list = (ListView) relativeLayout.findViewById(R.id.lv_livevideo_rank_list);
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
                        return new RankItem(colorYellow, colorWhite);
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
                        return new RankItem(colorYellow, colorWhite);
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
                        return new RankItem(colorYellow, colorWhite);
                    }
                });
            }
        });
    }

    public boolean onBack() {
        if (relativeLayout != null && relativeLayout.getVisibility() == View.VISIBLE) {
            relativeLayout.startAnimation(mAnimSlideOut);
            return true;
        }
        return false;
    }

    public void onTitleShow(boolean show) {
        if (relativeLayout != null && relativeLayout.getVisibility() == View.VISIBLE) {
            relativeLayout.startAnimation(mAnimSlideOut);
        }
    }

    public void setVideoLayout() {
        if (relativeLayout == null) {
            return;
        }
        final View contentView = activity.findViewById(android.R.id.content);
        final View actionBarOverlayLayout = (View) contentView.getParent();
        Rect r = new Rect();
        actionBarOverlayLayout.getWindowVisibleDisplayFrame(r);
        int screenWidth = (r.right - r.left);
        int screenHeight = ScreenUtils.getScreenHeight();
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
}
