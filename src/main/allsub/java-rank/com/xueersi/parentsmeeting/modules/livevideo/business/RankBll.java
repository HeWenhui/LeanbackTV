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
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.activity.item.RankItem;
import com.xueersi.parentsmeeting.modules.livevideo.business.RankPage.SmallChineseRankPager;
import com.xueersi.parentsmeeting.modules.livevideo.business.evendrive.EvenDriveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.evendrive.MiddleScienceEvenDrivePager;
import com.xueersi.parentsmeeting.modules.livevideo.business.evendrive.itempager.ItemMiddleSciencePager;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoLevel;
import com.xueersi.parentsmeeting.modules.livevideo.entity.User;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.MessageAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.AllRankEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RankEntity;
import com.xueersi.parentsmeeting.modules.livevideo.http.RankHttp;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;
import com.xueersi.ui.adapter.AdapterItemInterface;
import com.xueersi.ui.adapter.CommonAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by linyuqiang on 2017/9/20.
 */

public class RankBll extends LiveBaseBll implements BaseLiveMediaControllerBottom.MediaChildViewClick, NoticeAction, MessageAction {
    Logger logger = LoggerFactory.getLogger("RankBll");
    LiveMediaController mMediaController;
    BaseLiveMediaControllerBottom liveMediaControllerBottom;
    Button rl_livevideo_common_rank;//排名
    View relativeLayout;
    /** 动画出现 */
    private Animation mAnimSlideIn;
    /** 动画隐藏 */
    private Animation mAnimSlideOut;
    AllRankEntity allRankEntity;
    /** 上一次离开时的位置 */
    int index = 1;
    ListView lv_livevideo_rank_list;
    int colorYellow;
    int colorWhite;
    private RankHttp rankHttp;
    private Boolean isSmallEnglish = false;
    /** 小学语文排名 */
    private SmallChineseRankPager chineseRankPager;

    public RankBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
        colorYellow = context.getResources().getColor(R.color.COLOR_FFFF00);
        colorWhite = context.getResources().getColor(R.color.white);
    }

    private void initAnimation() {
        if (mAnimSlideIn == null) {
            //补间动画，结束后消失
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

    public RankHttp getRankHttp() {
        if (rankHttp == null) {
            rankHttp = new RankHttp(getLiveHttpAction());
        }
        return rankHttp;
    }

    @Override
    public void onModeChange(String oldMode, String mode, boolean isPresent) {
        super.onModeChange(oldMode, mode, isPresent);
        if (mGetInfo.getPattern() == LiveVideoConfig.LIVE_PATTERN_2) {
            onTitleShow(true);
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

    /**
     * TODO
     * 以后放到onLiveInited方法里
     */
    public void setLiveMediaController(final LiveMediaController mMediaController, BaseLiveMediaControllerBottom
            liveMediaControllerBottom) {
        this.mMediaController = mMediaController;
        this.liveMediaControllerBottom = liveMediaControllerBottom;

        if (liveMediaControllerBottom instanceof LiveUIStateReg) {
            LiveUIStateReg liveUIStateReg = (LiveUIStateReg) liveMediaControllerBottom;
            liveUIStateReg.addLiveUIStateListener(onViewChange);
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
                //bugly 7801.在getinfo之前点击会崩
                if (relativeLayout == null) {
                    logger.d("rl_livevideo_common_rank.onClick:relativeLayout=null");
                    XESToastUtils.showToast(activity, "请稍等");
                    return;
                }
                if (relativeLayout.getVisibility() == View.VISIBLE) {
                    relativeLayout.startAnimation(mAnimSlideOut);
                } else {
                    if (mGetInfo == null) {
                        XESToastUtils.showToast(activity, "请稍等");
                        return;
                    }
                    getAllRanking(new AbstractBusinessDataCallBack() {

                        @Override
                        public void onDataSucess(Object... objData) {
                            /** 异步获取的实体数据，这个时候 */
                            allRankEntity = (AllRankEntity) objData[0];

                            if (mGetInfo.getIsOpenNewCourseWare() == 1) {
                                //中学连对激励

                                scienceEvenDrivePager.updataRankData(allRankEntity);
                            } else {
                                if (!LiveVideoConfig.isSmallChinese) {
                                    ArrayList<RankEntity> rankEntities;
                                    if (index == 1) {
                                        rankEntities = allRankEntity.getMyRankEntityMyTeam().getRankEntities();
                                    } else if (index == 2) {
                                        //是否支持连对激励
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
                                } else {
                                    chineseRankPager.setRankEntity(allRankEntity);
                                    chineseRankPager.initData();

                                }
                            }
                        }
                    });
                    /** 是否支持连对激励 0：关闭 1：打开 */
                    if (mGetInfo.getIsOpenNewCourseWare() == 1) {
                        getEvenLikeData();
                    }
                    relativeLayout.setVisibility(View.VISIBLE);
                    relativeLayout.startAnimation(mAnimSlideIn);
                }
            }
        });
    }

    private MiddleScienceEvenDrivePager scienceEvenDrivePager;

    /** 获取连对排名 */
    private void getEvenLikeData() {
        getHttpManager().getEvenLikeData(
//                "https://www.easy-mock.com/mock/5b56d172008bc8159f336281/example/science/Stimulation/evenPairList",
                mGetInfo.getGetEvenPairListUrl(),
                mGetInfo.getStudentLiveInfo().getClassId(),
                mGetInfo.getId(),
                mGetInfo.getStudentLiveInfo().getTeamId(),
                new HttpCallBack() {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        EvenDriveEntity evenDriveEntity = getHttpResponseParser().parseEvenEntity(responseEntity);
                        scienceEvenDrivePager.updateEvenData(evenDriveEntity);
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        super.onFailure(call, e);
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        super.onPmError(responseEntity);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        super.onResponse(call, response);
                    }
                });
    }

    private LiveUIStateListener onViewChange = new LiveUIStateListener() {
        @Override
        public void onViewChange(BaseLiveMediaControllerBottom baseLiveMediaControllerBottom) {
            setLiveMediaController(mMediaController, baseLiveMediaControllerBottom);
        }
    };


    public void getAllRanking(final AbstractBusinessDataCallBack callBack) {
        logger.e("======> rankBll getAllRanking called:" + ":" + mGetInfo.getArtsExtLiveInfo());
        if (mGetInfo.getArtsExtLiveInfo() != null
                && mGetInfo.getArtsExtLiveInfo().getNewCourseWarePlatform().equals("1")) {
            getArtsNewAllRanking(callBack);
        } else {
            getOldRankingData(callBack);
        }
    }

    /**
     * 获取文科新课件平台 排名
     *
     * @param callBack
     */
    private void getArtsNewAllRanking(final AbstractBusinessDataCallBack callBack) {
        logger.e("======> rankBll getArtsNewAllRanking called:" + ":" + mGetInfo.getArtsExtLiveInfo().getNewCourseWarePlatform());
        getRankHttp().getNewArtsAllRank(mGetInfo.getId(), mGetInfo.getStuCouId(), new HttpCallBack() {
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

    private void getOldRankingData(final AbstractBusinessDataCallBack callBack) {
        String classId = "";
        if (mGetInfo.getStudentLiveInfo() != null) {
            classId = mGetInfo.getStudentLiveInfo().getClassId();
        }
        getRankHttp().getAllRanking(mGetInfo.getId(), classId, new HttpCallBack(false) {
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
            if (mGetInfo.getIsOpenNewCourseWare() == 1) {
                setEvenDriveLayout();
            }
        }
    }

    /**
     * 设置连对激励系统布局
     */
    private void setEvenDriveLayout() {
        if (relativeLayout == null) {
            return;
        }
        LiveVideoPoint liveVideoPoint = LiveVideoPoint.getInstance();
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) relativeLayout.getLayoutParams();
        int wradio = liveVideoPoint.x4 - liveVideoPoint.x3;
        int rightMargin = liveVideoPoint.screenWidth - liveVideoPoint.x4;
        if (wradio != params.width || rightMargin != params.rightMargin) {
            //logger.e( "setVideoWidthAndHeight:screenWidth=" + screenWidth + ",width=" + width + "," + height
            // + ",wradio=" + wradio + "," + params.width);
            params.rightMargin = rightMargin;
            params.width = wradio;
//                relativeLayout.setLayoutParams(params);
            LayoutParamsUtil.setViewLayoutParams(relativeLayout, params);
        }
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        initView(getLiveViewAction());
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
    /** 中学激励系统listView对应的提示使用的layout */
//    private ConstraintLayout rankEvenDriveTipsLayout;
    /**  */
//    private RelativeLayout rankTipsLayout;

    /**
     * 中学连对激励系统正确率和连对
     * 组内时为正确率
     * 连对时为连对
     */
//    private TextView tvEvenDriveTitleRight;
    public void initView(final LiveViewAction liveViewAction) {
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
            relativeLayout = liveViewAction.inflateView(R.layout.layout_livevideo_small_english_rank);
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
        } else if (LiveVideoConfig.isSmallChinese) {//如果是小学语文
            chineseRankPager = new SmallChineseRankPager(mContext);
            relativeLayout = chineseRankPager.getRootView();

        } else {
            //中学连对激励系统
            if (mGetInfo.getIsOpenNewCourseWare() == 1) {
                scienceEvenDrivePager = new MiddleScienceEvenDrivePager(mContext);

                scienceEvenDrivePager.setiNotice(new ItemMiddleSciencePager.INotice() {
                    @Override
                    public void sendNotice(JSONObject jsonObject, String targetName) {
                        try {
                            jsonObject.put("from", mGetInfo.getStuId());
                            jsonObject.put("stuName", mGetInfo.getStuName());
                            logger.i(jsonObject.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String senderId = "";
                        for (int i = 0; i < users.size(); i++) {
                            if (targetName.equals(findUserId(users.get(i)))) {
                                senderId = users.get(i);
                                break;
                            }
                        }
                        RankBll.this.sendNotice(jsonObject, senderId);
                    }

                    @Override
                    public void sendLike(int listFlag, String bePraised, HttpCallBack httpCallBack) {
                        getRankHttp().sendEvenDriveLike(
                                mGetInfo.getGetThumbsUpUrl(),
                                mGetInfo.getStudentLiveInfo().getClassId(),
                                mGetInfo.getId(),
                                mGetInfo.getStudentLiveInfo().getTeamId(),
                                String.valueOf(listFlag),
                                bePraised,
                                httpCallBack);
                    }
                });
                scienceEvenDrivePager.initListener();
                relativeLayout = scienceEvenDrivePager.getRootView();
            } else {
                if (LiveVideoConfig.isPrimary) {
                    relativeLayout = liveViewAction.inflateView(R.layout.layout_livevideo_psrank);
                } else {
                    relativeLayout = liveViewAction.inflateView(R.layout.layout_livevodeo_rank);
                }

//            rankEvenDriveTipsLayout = relativeLayout.findViewById(R.id.ctlayout_livevideo_rank_middle_science_even_drive_tips);
//            rankTipsLayout = relativeLayout.findViewById(R.id.rl_livevideo_rank_tips);
//            tvEvenDriveTitleRight = relativeLayout.findViewById(R.id.tv_livevideo_middle_science_even_title_right);
                //是否是中学激励系统
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


                final int COLOR_F13232 = activity.getResources().getColor(R.color.COLOR_F13232);
                final int white = activity.getResources().getColor(R.color.white);
                final int slider = activity.getResources().getColor(R.color.COLOR_SLIDER);


                rl_livevideo_rank_mygroup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        index = 1;
                        tv_livevideo_rank_subtitle_mid.setText("学员");
                        v_livevideo_rank_mygroup.setVisibility(View.VISIBLE);
                        if (LiveVideoConfig.isPrimary) {
                            tv_livevideo_rank_mygroup.setTextColor(slider);
                        } else {
                            tv_livevideo_rank_mygroup.setTextColor(COLOR_F13232);
                        }
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
                        if (LiveVideoConfig.isPrimary) {
                            tv_livevideo_rank_groups.setTextColor(slider);
                        } else {
                            tv_livevideo_rank_groups.setTextColor(COLOR_F13232);
                        }
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
//                }
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
                        if (LiveVideoConfig.isPrimary) {
                            tv_livevideo_rank_class.setTextColor(slider);
                        } else {
                            tv_livevideo_rank_class.setTextColor(COLOR_F13232);
                        }
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
        }
        //把该布局加到排行榜的右边
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
//        relativeLayout.setBackgroundColor(liveVideoActivity.getResources().getColor(R.color.translucent_black));
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        liveViewAction.addView(new LiveVideoLevel(11), relativeLayout, lp);
        setVideoLayout();
    }

    private static String findUserId(String user) {
        if (user == null) {
            return "";
        }
        int len = user.length();
        int numSum = 0;
        char last = 'a';
        String ans = "";
        String nowStr = "";
        for (int i = 0; i < len; i++) {
            char ch = user.charAt(i);
            if (ch == '_') {
                if (last >= '0' && last <= '9') {
                    numSum++;
                }
                if (numSum == 3) {
                    ans = nowStr;
                    break;
                }
                nowStr = "";
            } else if (ch >= '0' && ch <= '9') {
                nowStr += ch;
            }
            last = ch;
        }
        return ans;
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
            //logger.e( "setVideoWidthAndHeight:screenWidth=" + screenWidth + ",width=" + width + "," + height
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

    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {
        logger.i("sourceNick" + sourceNick + ",target" + target + ",data" + data + ",type" + type);
        switch (type) {
            case XESCODE.EvenDrive.PRAISE_PRIVATE_STUDENT:

                break;
            case XESCODE.EvenDrive.BROADCAST_STUDY_REPORT:
//                if (scienceEvenDrivePager != null) {
//                    scienceEvenDrivePager.setEndTime(System.currentTimeMillis());
//                }
                break;
            case XESCODE.STOPQUESTION: {
//                if (scienceEvenDrivePager != null) {
//                    //是否收题
////                    boolean isOff = data.optBoolean("open");
////                    if (!isOff) {
//                    logger.i("设置结束时间");
//                    isMiddleScienceH5Open = false;
//                    scienceEvenDrivePager.setH5Open(isMiddleScienceH5Open);
//                    scienceEvenDrivePager.setEndTime(System.currentTimeMillis());
////                    }
//                }
                break;
            }
            case XESCODE.EXAM_STOP: {

//                if (scienceEvenDrivePager != null) {
//                    isMiddleScienceH5Open = false;
//                    scienceEvenDrivePager.setH5Open(isMiddleScienceH5Open);
//                    scienceEvenDrivePager.setEndTime(System.currentTimeMillis());
//                }
                break;
            }
            case XESCODE.MULTIPLE_H5_COURSEWARE: {
                boolean isOff = data.optBoolean("open");
                if (!isOff) {

//                    if (scienceEvenDrivePager != null) {
//                        logger.i("设置结束时间");
//                        scienceEvenDrivePager.setEndTime(System.currentTimeMillis());
//                        isMiddleScienceH5Open = false;
//                        scienceEvenDrivePager.setH5Open(isMiddleScienceH5Open);
//                    }
                } else {
                    //收到题目把排行榜收起来
                    post(new Runnable() {
                        @Override
                        public void run() {
                            if (relativeLayout.getVisibility() == View.VISIBLE) {
                                relativeLayout.startAnimation(mAnimSlideOut);
                            }
                        }
                    });
//                    isMiddleScienceH5Open = true;
////                    if (mHandler != null) {
////                        mHandler.post(new Runnable() {
////                            @Override
////                            public void run() {
////                                relativeLayout.requestLayout();
////                            }
////                        });
////                    }

                }
                break;
            }
            case XESCODE.ENGLISH_H5_COURSEWARE: {
                isMiddleScienceH5Open = true;
//                if (mHandler != null) {
//                    mHandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            relativeLayout.requestLayout();
//                        }
//                    });
//                }
                break;
            }
//            case XESCODE.ARTS_STOP_QUESTION:
//            case XESCODE.ARTS_H5_COURSEWARE: {
//
//                break;
//            }
            default:
                break;
        }
    }

    //现在点赞消息是在  发题至收题后15s.
    private boolean isMiddleScienceH5Open = false;

    //    private boolean isNot;
    @Override
    public int[] getNoticeFilter() {
        //学生点赞
        return new int[]{
                XESCODE.EvenDrive.PRAISE_PRIVATE_STUDENT,
                XESCODE.EvenDrive.BROADCAST_STUDY_REPORT,
                XESCODE.STOPQUESTION,
                XESCODE.EXAM_STOP,
                XESCODE.MULTIPLE_H5_COURSEWARE};
    }

    @Override
    public void onStartConnect() {

    }

    @Override
    public void onConnect(IRCConnection connection) {

    }

    @Override
    public void onRegister() {

    }

    @Override
    public void onDisconnect(IRCConnection connection, boolean isQuitting) {

    }

    @Override
    public void onMessage(String target, String sender, String login, String hostname, String text) {

    }

    @Override
    public void onPrivateMessage(boolean isSelf, String sender, String login, String hostname, String target, String message) {

    }

    @Override
    public void onChannelInfo(String channel, int userCount, String topic) {

    }

    private List<String> users = new ArrayList<>();

    @Override
    public void onUserList(String channel, User[] users) {
//        this.users = users;

        if (users == null) {
            this.users = new ArrayList<>();
        }
        for (int i = 0; i < users.length; i++) {
            logger.i("channel =" + channel + "nick = " + users[i].getNick() + " prefix = " + users[i].getPrefix());
            this.users.add(users[i].getNick());
        }
    }

    @Override
    public void onJoin(String target, String sender, String login, String hostname) {
        logger.i("target " + target + " sender" + sender + " login" + login + " hostname" + hostname);
        if (!users.contains(sender)) {
            users.add(new String(sender));
        }
    }

    @Override
    public void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason) {

    }

    @Override
    public void onKick(String target, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason) {

    }

    @Override
    public void onUnknown(String line) {

    }
}
