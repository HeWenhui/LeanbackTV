package com.xueersi.parentsmeeting.modules.livevideoOldIJK.praiselist.page;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.WeakHandler;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.dialog.CloseConfirmDialog;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.praiselist.contract.PraiseListPresenter;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.praiselist.contract.PraiseListView;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity.ExcellentListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity.LikeListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity.MinimarketListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity.PraiseListDanmakuEntity;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity.PraiseListStudentEntity;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity.PraiseListTeamEntity;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.studyreport.business.StudyReportAction;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.ProxUtil;
import com.xueersi.ui.adapter.RCommonAdapter;
import com.xueersi.ui.adapter.RItemViewInterface;
import com.xueersi.ui.adapter.ViewHolder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Zhang Yuansun on 2018/1/2.
 * <p>
 * 小理表扬榜
 */

public class PraiseListPager extends LiveBasePager {
    private PraiseListPresenter mPresenter;
    private PraiseListView mPraiseListView;
    private ExcellentListEntity excellentListEntity;
    private LikeListEntity likeListEntity;
    private MinimarketListEntity minimarketListEntity;
    private static final String LOTTIE_RES_ASSETS_ROOTDIR = "praise_list/";
    /**
     * 当前表扬榜类型
     */
    private int listType;
    public final static int PRAISE_LIST_TYPE_EXECELLENT = 1;//优秀榜
    public final static int PRAISE_LIST_TYPE_MINI_MARKET = 2;//小超市计算榜
    public final static int PRAISE_LIST_TYPE_LIKE = 3;//点赞榜
    //循环光芒动画
    private LottieAnimationView lottieAnimationLoopLightView;
    //主背景动画
    private LottieAnimationView lottieAnimationBGView;
    //老师表扬学生
    private LottieAnimationView lottieAnimationTeacherView;
    private View lottieAnimationTeacherGroup;
    private TextView teacherTipsView;
    private View contentGroup;

    //循环星星动画
    private LottieAnimationView lottieAnimationLoopStarView;
    //发现二倍卡动画
    private LottieAnimationView lottieAnimationDoubleCardView;
    //点击 点赞按钮 动画显示的图层
    private RelativeLayout lottieClickLikeGroup;

    /**
     * 金榜题名
     */
    private TextView tvCongratulations;
    /**
     * 表扬榜单
     */
    private RecyclerView rvStudentlist;
    /**
     * 战队列表
     */
    private RecyclerView rvTeamList;
    /**
     * 关闭按钮
     */
    private Button btnClose;
    /**
     * 点赞按钮显示的图层
     */
    RelativeLayout likeContentGroup;
    /**
     * 点赞按钮
     */
    private Button btnLike;
    /**
     * 点赞计数
     */
    private TextView tvLikeCount;
    /**
     * 备注
     */
    private TextView tvNotes;
    /**
     * 我是否上榜
     */
    private boolean isOnList = false;
    /**
     * 声音池
     */
    private SoundPool mSoundPool;
    /**
     * 榜单弹出声音
     */
    private int soundPraiselistIn = 0;
    /**
     * 点赞声音
     */
    private int soundLike = 0;

    /**
     * 学生列表
     */
    private RCommonAdapter studentAdapter;
    private static final int MAX_STUDENT_COLUMN_NUMBER = 5; //每行最多有几个

    /**
     * 战队列表
     */
    private RCommonAdapter teamAdapter;
    private int selectedTeamTabs = 0;
    private int myTeamTabs = -1;
    private static final int MAX_TEAM_NUMBER = 6;
    private static final int[] tabsBackgroundRes = new int[]{
            R.drawable.bg_livevideo_praiselist_tabs0,
            R.drawable.bg_livevideo_praiselist_tabs1,
            R.drawable.bg_livevideo_praiselist_tabs2,
            R.drawable.bg_livevideo_praiselist_tabs3,
            R.drawable.bg_livevideo_praiselist_tabs4,
            R.drawable.bg_livevideo_praiselist_tabs5,
    };
    private int[] totalLikeCount = new int[MAX_TEAM_NUMBER];
    private int[] latestLikeCount = new int[MAX_TEAM_NUMBER];
    private int totalLikeSum = 0;
    private int latestLikeSum = 0;
    private List<PraiseListTeamEntity> mTeamList;
//    private Drawable[] pressImg = new Drawable[MAX_TEAM_NUMBER];
//    private Drawable[] normalImg = new Drawable[MAX_TEAM_NUMBER];

    /**
     * 弹幕消息
     */
    private RecyclerView rvDanmaku;
    private DanmakuAdapter danmakuAdapter;
    private List<PraiseListDanmakuEntity> teamDanmakuCache = new ArrayList<>();
    private List<PraiseListDanmakuEntity> stuDanmakuCache = new ArrayList<>();
    private List<PraiseListDanmakuEntity> myDanmakuCache = new ArrayList<>();
    private List<PraiseListDanmakuEntity> danmakuList = new ArrayList<>();
    private static final int DURATION_DANMAKU_SCROOL = 1000; // 弹幕每1s滚动一条
    private Timer danmakuTimer; //弹幕定时器
    private int teamDanmakuCount = 0; //弹幕计数
    private int stuDanmakuCount = 0; //弹幕计数
    private int myDanmakuCount = 0; //弹幕计数
    private Timer likeTimer; //点赞定时器

    private WeakHandler mWeakHandler = new WeakHandler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            return false;
        }
    });

    public PraiseListPager(Context context, ExcellentListEntity excellentListEntity, PraiseListPresenter presenter, PraiseListView praiseListView) {
        super(context);
        listType = PRAISE_LIST_TYPE_EXECELLENT;
        if (excellentListEntity.getIsMy() == 1) {
            isOnList = true;
        }
        this.excellentListEntity = excellentListEntity;
        mTeamList = excellentListEntity.getTeamList();
        this.mPresenter = presenter;
        this.mPraiseListView = praiseListView;
        initData();
        initListener();
    }

    public PraiseListPager(Context context, MinimarketListEntity minimarketListEntity, PraiseListPresenter presenter, PraiseListView praiseListView) {
        super(context);
        listType = PRAISE_LIST_TYPE_MINI_MARKET;
        if (minimarketListEntity.getIsMy() == 1) {
            isOnList = true;
        }
        this.minimarketListEntity = minimarketListEntity;
        mTeamList = minimarketListEntity.getTeamList();
        this.mPresenter = presenter;
        this.mPraiseListView = praiseListView;
        initData();
        initListener();
    }

    public PraiseListPager(Context context, LikeListEntity likeListEntity, PraiseListPresenter presenter, PraiseListView praiseListView) {
        super(context);
        listType = PRAISE_LIST_TYPE_LIKE;
        if (likeListEntity.getIsMy() == 1) {
            isOnList = true;
        }
        this.likeListEntity = likeListEntity;
        mTeamList = likeListEntity.getTeamList();
        this.mPresenter = presenter;
        this.mPraiseListView = praiseListView;
        initData();
        initListener();
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.page_livevideo_praiselist, null);
        lottieAnimationLoopLightView = mView.findViewById(R.id.lav_livevideo_praiselist_looplight);
        lottieAnimationBGView = mView.findViewById(R.id.lav_livevideo_praiselist_pager_bg);
        lottieAnimationTeacherView = mView.findViewById(R.id.lav_livevideo_praiselist_teacher);
        lottieAnimationTeacherGroup = mView.findViewById(R.id.rl_livevideo_praiselist_teacher_group);
        teacherTipsView = mView.findViewById(R.id.tv_livevideo_praiselist_teacher_tips);
        contentGroup = mView.findViewById(R.id.rl_livevideo_praiselist_content);
        lottieAnimationLoopStarView = mView.findViewById(R.id.lav_livevideo_praiselist_loopstar);
        lottieClickLikeGroup = mView.findViewById(R.id.rl_livevideo_praiselist_lottie_click_like);
        lottieAnimationDoubleCardView = mView.findViewById(R.id.lav_livevideo_praiselist_double_card);
        tvCongratulations = mView.findViewById(R.id.tv_livevideo_praiselist_congratulations);
        btnClose = mView.findViewById(R.id.btn_livevideo_praiselist_close);
        likeContentGroup = mView.findViewById(R.id.rl_livevideo_praiselist_like_content);
        btnLike = mView.findViewById(R.id.btn_livevideo_praiselist_like);
        tvLikeCount = mView.findViewById(R.id.tv_livevideo_praiselist_like_count);
        tvNotes = mView.findViewById(R.id.tv_livevideo_notes);
        rvStudentlist = mView.findViewById(R.id.rv_livevideo_praiselist_student);
        rvTeamList = mView.findViewById(R.id.rv_livevideo_praiselist_team);
        rvDanmaku = mView.findViewById(R.id.rv_livevideo_praiselist_danmaku);
        return mView;
    }

//    public void test() {
//        Button button1 = mView.findViewById(R.id.btn_livevideo_praiselist_test1);
//        Button button2 = mView.findViewById(R.id.btn_livevideo_praiselist_test2);
//        Button button3 = mView.findViewById(R.id.btn_livevideo_praiselist_test3);
//        Button button4 = mView.findViewById(R.id.btn_livevideo_praiselist_test4);
//        button1.setText("学生点赞+1");
//        button2.setText("战队点赞+1");
//        button3.setText("老师表扬");
//
//        final List<PraiseListDanmakuEntity> tempList = new ArrayList<>();
//        button1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                PraiseListDanmakuEntity danmakuEntity = new PraiseListDanmakuEntity();
//                danmakuEntity.setName("詹姆斯哈登");
//                danmakuEntity.setNumber(100);
//                danmakuEntity.setBarrageType(1);
//                tempList.add(danmakuEntity);
//                receiveLikeNotice(tempList);
//                tempList.clear();
//            }
//        });
//        button2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                PraiseListDanmakuEntity danmakuEntity = new PraiseListDanmakuEntity();
//                danmakuEntity.setName("休斯敦火箭");
//                danmakuEntity.setNumber(200);
//                danmakuEntity.setBarrageType(2);
//                tempList.add(danmakuEntity);
//                receiveLikeNotice(tempList);
//                tempList.clear();
//            }
//        });
//
//        button3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startScrollAnimation("詹姆斯", "安东尼");
//            }
//        });
//    }

    /**
     * 校准布局
     */
    public void alignLayout() {
        lottieAnimationBGView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver
                .OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                lottieAnimationBGView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int measuredHeight = lottieAnimationBGView.getMeasuredHeight();
                int measudredWidth = lottieAnimationBGView.getMeasuredWidth();
                logger.d("lottieAnimationBGView: getMeasuredHeight() = " + measuredHeight + ", getMeasuredWidth() = " + measudredWidth);

                int originHeight = measudredWidth * 3 / 4;
                int differenceHeight = (originHeight - measuredHeight) / 2;

                int originWidth = measuredHeight * 4 / 3;
                int differenceWidth = (originWidth - measudredWidth) / 2;

                //内容区域的位置
                RelativeLayout.LayoutParams contentParams = (RelativeLayout.LayoutParams) contentGroup.getLayoutParams();
                if (measudredWidth * 3 <= measuredHeight * 4) {
                    //水平方向上截断
                    contentParams.topMargin = caculateVerticalMargin(SizeUtils.Dp2Px(mContext, 65));
                    contentParams.bottomMargin = caculateVerticalMargin(SizeUtils.Dp2Px(mContext, 45));
                } else {
                    //垂直方向上截断

                    contentParams.topMargin = caculateVerticalMargin(SizeUtils.Dp2Px(mContext, 65) - differenceHeight);
                    contentParams.bottomMargin = caculateVerticalMargin(SizeUtils.Dp2Px(mContext, 45) - differenceHeight);
                }
                contentParams.width = caculateVerticalMargin(SizeUtils.Dp2Px(mContext, 415));
                contentGroup.setLayoutParams(contentParams);

                //点赞区域的位置
                RelativeLayout.LayoutParams likeContentParams = (RelativeLayout.LayoutParams) likeContentGroup.getLayoutParams();
                if (measudredWidth * 3 <= measuredHeight * 4) {
                    //水平方向上截断
                    likeContentParams.topMargin = caculateVerticalMargin(SizeUtils.Dp2Px(mContext, 65));
                    likeContentParams.bottomMargin = caculateVerticalMargin(SizeUtils.Dp2Px(mContext, 45));
                } else {
                    //垂直方向上截断

                    likeContentParams.topMargin = caculateVerticalMargin(SizeUtils.Dp2Px(mContext, 65) - differenceHeight);
                    likeContentParams.bottomMargin = caculateVerticalMargin(SizeUtils.Dp2Px(mContext, 45) - differenceHeight);
                }
                likeContentParams.width = caculateVerticalMargin(SizeUtils.Dp2Px(mContext, 435));
                likeContentGroup.setLayoutParams(likeContentParams);

                //金榜题名的位置
                RelativeLayout.LayoutParams titleparams = (RelativeLayout.LayoutParams) tvCongratulations.getLayoutParams();
                titleparams.topMargin = caculateVerticalMargin(SizeUtils.Dp2Px(mContext, 39));
                tvCongratulations.setLayoutParams(titleparams);

                //战队列表的位置
                RelativeLayout.LayoutParams teamListParams = (RelativeLayout.LayoutParams) rvTeamList.getLayoutParams();
                teamListParams.topMargin = caculateVerticalMargin(SizeUtils.Dp2Px(mContext, 57));
                teamListParams.leftMargin = caculateVerticalMargin(SizeUtils.Dp2Px(mContext, 42));
                teamListParams.rightMargin = caculateVerticalMargin(SizeUtils.Dp2Px(mContext, 32));
                teamListParams.height = caculateVerticalMargin(SizeUtils.Dp2Px(mContext, 58));
                rvTeamList.setLayoutParams(teamListParams);

                //学生列表的位置
                RelativeLayout.LayoutParams studentListParams = (RelativeLayout.LayoutParams) rvStudentlist.getLayoutParams();
                studentListParams.topMargin = caculateVerticalMargin(SizeUtils.Dp2Px(mContext, 116));
                studentListParams.bottomMargin = caculateVerticalMargin(SizeUtils.Dp2Px(mContext, 45));
                studentListParams.leftMargin = caculateVerticalMargin(SizeUtils.Dp2Px(mContext, 47));
                studentListParams.rightMargin = caculateVerticalMargin(SizeUtils.Dp2Px(mContext, 40));
                rvStudentlist.setLayoutParams(studentListParams);

                //点赞按钮的位置
                RelativeLayout.LayoutParams likeParams = (RelativeLayout.LayoutParams) btnLike.getLayoutParams();
                likeParams.rightMargin = caculateVerticalMargin(SizeUtils.Dp2Px(mContext, 25));
                likeParams.width = caculateVerticalMargin(SizeUtils.Dp2Px(mContext, 40));
                likeParams.height = caculateVerticalMargin(SizeUtils.Dp2Px(mContext, 40));
                btnLike.setLayoutParams(likeParams);

                //点赞计数的位置
                RelativeLayout.LayoutParams likeCountParams = (RelativeLayout.LayoutParams) tvLikeCount.getLayoutParams();
                likeCountParams.width = caculateVerticalMargin(SizeUtils.Dp2Px(mContext, 90));
                likeCountParams.bottomMargin = caculateVerticalMargin(SizeUtils.Dp2Px(mContext, 60));
                tvLikeCount.setLayoutParams(likeCountParams);

                //备注的位置
                RelativeLayout.LayoutParams noteParams = (RelativeLayout.LayoutParams) tvNotes.getLayoutParams();
                noteParams.bottomMargin = caculateVerticalMargin(SizeUtils.Dp2Px(mContext, 30));
                noteParams.leftMargin = caculateVerticalMargin(SizeUtils.Dp2Px(mContext, 50));
                tvNotes.setLayoutParams(noteParams);

                //循环星星动画的位置
                RelativeLayout.LayoutParams lottieStarParams = (RelativeLayout.LayoutParams) lottieAnimationLoopStarView.getLayoutParams();
                lottieStarParams.rightMargin = caculateVerticalMargin(SizeUtils.Dp2Px(mContext, 20));
                lottieStarParams.bottomMargin = caculateVerticalMargin(SizeUtils.Dp2Px(mContext, 30));
                lottieStarParams.leftMargin = caculateVerticalMargin(SizeUtils.Dp2Px(mContext, -20));
                lottieStarParams.topMargin = caculateVerticalMargin(SizeUtils.Dp2Px(mContext, -30));
                lottieAnimationLoopStarView.setLayoutParams(lottieStarParams);

                //二倍卡动画的位置
                RelativeLayout.LayoutParams lottieDoubleCardParams = (RelativeLayout.LayoutParams) lottieAnimationDoubleCardView.getLayoutParams();
                lottieDoubleCardParams.rightMargin = caculateVerticalMargin(SizeUtils.Dp2Px(mContext, 20));
                lottieDoubleCardParams.bottomMargin = caculateVerticalMargin(SizeUtils.Dp2Px(mContext, 20));
                lottieDoubleCardParams.leftMargin = caculateVerticalMargin(SizeUtils.Dp2Px(mContext, -20));
                lottieDoubleCardParams.topMargin = caculateVerticalMargin(SizeUtils.Dp2Px(mContext, -20));
                lottieAnimationDoubleCardView.setLayoutParams(lottieDoubleCardParams);

                //老师表扬学生文本框的位置
                RelativeLayout.LayoutParams teacherTipsParams = (RelativeLayout.LayoutParams) teacherTipsView.getLayoutParams();
                teacherTipsParams.topMargin = caculateVerticalMargin(SizeUtils.Dp2Px(mContext, 225));
                teacherTipsParams.width = caculateVerticalMargin(SizeUtils.Dp2Px(mContext, 287));
                teacherTipsView.setLayoutParams(teacherTipsParams);

                if (teamAdapter != null) {
                    teamAdapter.updateData(mTeamList);
                }
            }
        });
    }

    @Override
    public void initData() {
//        test();
        alignLayout();
        startBackgtoundAnimation();

        while (mTeamList.size() > MAX_TEAM_NUMBER) {
            mTeamList.remove(mTeamList.size() - 1);
        }

        //计算战队排名
        if ((listType == PRAISE_LIST_TYPE_EXECELLENT || listType == PRAISE_LIST_TYPE_MINI_MARKET) && mTeamList.size() != 0) {
            mTeamList.get(0).setTeamRanking(1);
            for (int i = 1; i < mTeamList.size(); i++) {
                if (mTeamList.get(i).getOnListNums() * mTeamList.get(i - 1).getTeamMemberNums() == mTeamList.get(i - 1).getOnListNums() * mTeamList.get(i).getTeamMemberNums()) {
                    mTeamList.get(i).setTeamRanking(mTeamList.get(i - 1).getTeamRanking());
                } else {
                    mTeamList.get(i).setTeamRanking(mTeamList.get(i - 1).getTeamRanking() + 1);
                }
                if (mTeamList.get(i).getIsMy() == 1 || mTeamList.get(i).getIsMy() == 0) {
                    if (mTeamList.get(i).getIsMy() == 1) {
                        selectedTeamTabs = i;
                    }
                    myTeamTabs = i;
                }
            }
        } else if (listType == PRAISE_LIST_TYPE_LIKE && mTeamList.size() != 0) {
            mTeamList.get(0).setTeamRanking(1);
            for (int i = 1; i < mTeamList.size(); i++) {
                if (mTeamList.get(i).getPraiseTotalNum() == mTeamList.get(i - 1).getPraiseTotalNum()) {
                    mTeamList.get(i).setTeamRanking(mTeamList.get(i - 1).getTeamRanking());
                } else {
                    mTeamList.get(i).setTeamRanking(mTeamList.get(i - 1).getTeamRanking() + 1);
                }
                if (mTeamList.get(i).getIsMy() == 1 || mTeamList.get(i).getIsMy() == 0) {
                    if (mTeamList.get(i).getIsMy() == 1) {
                        selectedTeamTabs = i;
                    }
                    myTeamTabs = i;
                }
            }
        }

        //播放声音
        if (mSoundPool == null)
            mSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        if (soundPraiselistIn == 0) {
            soundPraiselistIn = mSoundPool.load(mContext, R.raw.praise_list, 1);
            mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    soundPool.play(soundPraiselistIn, 1, 1, 0, 0, 1);
                }
            });
        } else {
            mSoundPool.play(soundPraiselistIn, 1, 1, 0, 0, 1);
        }

        //恭喜 金榜题名
        if (isOnList) {
            SpannableString successString = new SpannableString("恭喜 " + mPresenter.getStuName() + "同学 金榜题名，努力总有收获");
            AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(SizeUtils.Dp2Px(mContext, 11));
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(mContext.getResources().getColor(R.color.white));
            successString.setSpan(sizeSpan, "恭喜 ".length(), "恭喜 ".length() + mPresenter.getStuName().length() + "同学".length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            successString.setSpan(colorSpan, "恭喜 ".length(), "恭喜 ".length() + mPresenter.getStuName().length() + "同学".length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            tvCongratulations.setText(successString);
        } else {
            tvCongratulations.setText("不要灰心，努力一点，下次一定上榜");
            tvCongratulations.setBackgroundResource(R.drawable.bg_livevideo_praiselist_congratulations_fail);
        }

        //备注
        switch (listType) {
            case PRAISE_LIST_TYPE_EXECELLENT:
                tvNotes.setText("注:全对或者订正到全对的同学可以上榜哦~");

                break;
            case PRAISE_LIST_TYPE_MINI_MARKET:

                if (minimarketListEntity.getTitleId() == 1) {
                    tvNotes.setText("注:统计周期为上讲结束后到本讲当天");
                } else {
                    tvNotes.setText("注:统计周期为本周一到本讲当天");
                }
                break;
            case PRAISE_LIST_TYPE_LIKE:
                tvNotes.setText("注:点赞数高的同学可以上榜哦~");
                rvDanmaku.setVisibility(View.GONE);
                btnLike.setVisibility(View.GONE);
                break;
            default:
                break;
        }

        //学生列表
        studentAdapter = new RCommonAdapter(mContext, mTeamList.get(selectedTeamTabs).getStudentList());
        studentAdapter.addItemViewDelegate(new StudentItem());
        GridLayoutManager studentLayoutManager = new GridLayoutManager(mContext, MAX_STUDENT_COLUMN_NUMBER);
        rvStudentlist.setLayoutManager(studentLayoutManager);
        rvStudentlist.setAdapter(studentAdapter);
        rvStudentlist.addItemDecoration(new SpaceItemDecoration(SizeUtils.Dp2Px(mContext, 20)));
        rvStudentlist.setHasFixedSize(true);

        //战队列表
        teamAdapter = new RCommonAdapter(mContext, mTeamList);
        teamAdapter.addItemViewDelegate(new TeamItem());
        LinearLayoutManager teamLayoutManager = new LinearLayoutManager(mContext);
        teamLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        teamLayoutManager.setInitialPrefetchItemCount(MAX_TEAM_NUMBER);
        rvTeamList.setLayoutManager(teamLayoutManager);
        rvTeamList.setAdapter(teamAdapter);

        //默认直接展示自己战队的页卡
        rvStudentlist.setBackgroundResource(tabsBackgroundRes[selectedTeamTabs]);
        studentAdapter.updateData(mTeamList.get(selectedTeamTabs).getStudentList());

        //弹幕消息
        danmakuAdapter = new DanmakuAdapter(danmakuList);
        rvDanmaku.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, true));
        rvDanmaku.setAdapter(danmakuAdapter);
        rvDanmaku.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int itemPosition = parent.getChildAdapterPosition(view);
                int left = 0;
                int right = 0;
                int top = 0;
                int bottom = 0;
                if (itemPosition < danmakuList.size()) {
                    top = SizeUtils.Dp2Px(mContext, 10);
                }
                outRect.set(left, top, right, bottom);
            }
        });
    }

    private class DanmakuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<PraiseListDanmakuEntity> mData;

        public DanmakuAdapter(List<PraiseListDanmakuEntity> data) {
            mData = data;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new DanmakuItem(View.inflate(parent.getContext(), R.layout.item_livevideo_praiselist_danmaku, null));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int dataIndex = (mData.size() - 1) - position;
            ((DanmakuItem) holder).bindData(mData.get(dataIndex));
        }

        @Override
        public int getItemCount() {
            return mData == null ? 0 : mData.size();
        }
    }

    private class DanmakuItem extends RecyclerView.ViewHolder {
        private TextView tvMsg;
        private TextView tvTeam;

        private DanmakuItem(View itemView) {
            super(itemView);
            tvMsg = itemView.findViewById(R.id.tv_livevideo_praiselist_danmaku_msg);
            tvTeam = itemView.findViewById(R.id.tv_livevideo_praiselist_danmaku_team);
        }

        public void bindData(PraiseListDanmakuEntity data) {
            tvMsg.setVisibility(View.VISIBLE);
            if (data.getBarrageType() == 1) {
                tvTeam.setVisibility(View.GONE);
                if (mPresenter.getStuName() != null && mPresenter.getStuName().equals(data.getName())) {
                    tvMsg.setText(data.getName() + "同学点了" + data.getNumber() + "个赞!!");
                    tvMsg.setTextColor(Color.parseColor("#FFFFDB5C"));
                } else {
                    tvMsg.setText(data.getName() + "同学点了" + data.getNumber() + "个赞!!");
                    tvMsg.setTextColor(mContext.getResources().getColor(R.color.white));
                }

            } else if (data.getBarrageType() == 2) {
                tvTeam.setVisibility(View.VISIBLE);
                tvMsg.setText(data.getName() + "共获得" + data.getNumber() + "个赞!!");
                tvMsg.setTextColor(mContext.getResources().getColor(R.color.white));
            }
        }
    }

    private void addDanmaku(PraiseListDanmakuEntity danmakuEntity) {
        danmakuList.add(danmakuEntity);
        danmakuAdapter.notifyItemInserted(0);
    }

    private Runnable hideLikeCountRunnable = new Runnable() {
        @Override
        public void run() {
            fadeOut();
        }
    };

    private Runnable hideDoubleCardRunnable = new Runnable() {
        @Override
        public void run() {
            duringDoubleCard = false;
            tvLikeCount.setVisibility(View.VISIBLE);
            tvLikeCount.setText("+" + totalLikeCount[selectedTeamTabs]);
            lottieAnimationDoubleCardView.setVisibility(View.GONE);
            mWeakHandler.postDelayed(hideLikeCountRunnable, 2000);
        }
    };

    private int btnLikeClickTime = 0;
    boolean duringDoubleCard = false;

    @Override
    public void initListener() {
        //监听点赞按钮点击事件
        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (soundLike == 0) {
                    soundLike = mSoundPool.load(mContext, R.raw.thumbs_up, 1);
                    mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                        public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                            soundPool.play(soundLike, 1, 1, 0, 0, 1);
                        }
                    });
                } else {
                    if (mSoundPool != null) {
                        mSoundPool.play(soundLike, 1, 1, 0, 0, 1);
                    }
                }
                startClickLikeAnimation();
                lottieAnimationLoopStarView.setVisibility(View.GONE);

                if (btnLikeClickTime >= 10) {
                    if (duringDoubleCard) {
                        return;
                    }
                    //发现二倍卡
                    if (findDoubleCard()) {
                        duringDoubleCard = true;
                        tvLikeCount.setVisibility(View.GONE);
                        totalLikeCount[selectedTeamTabs] *= 2;
                        startDoubleCardAnimation();
                        mWeakHandler.removeCallbacks(hideLikeCountRunnable);
                        mWeakHandler.removeCallbacks(hideDoubleCardRunnable);
                        mWeakHandler.postDelayed(hideDoubleCardRunnable, 2000);
                    } else {
                        if (tvLikeCount.getVisibility() == View.GONE) {
                            tvLikeCount.setVisibility(View.VISIBLE);
                            fadeIn();
                        }
                        totalLikeCount[selectedTeamTabs]++;
                        tvLikeCount.setText("+" + totalLikeCount[selectedTeamTabs]);
                        mWeakHandler.removeCallbacks(hideLikeCountRunnable);
                        mWeakHandler.postDelayed(hideLikeCountRunnable, 2000);
                    }
                } else {
                    if (tvLikeCount.getVisibility() == View.GONE) {
                        tvLikeCount.setVisibility(View.VISIBLE);
                        fadeIn();
                    }
                    totalLikeCount[selectedTeamTabs]++;
                    tvLikeCount.setText("+" + totalLikeCount[selectedTeamTabs]);
                    mWeakHandler.removeCallbacks(hideLikeCountRunnable);
                    mWeakHandler.postDelayed(hideLikeCountRunnable, 2000);
                }
                if (likeTimer == null) {
                    likeTimer = new Timer();
                    likeTimer.schedule(new LikeTimerTask(), 0, 4000);
                }
                btnLikeClickTime++;
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
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
                        mPraiseListView.closePraiseList();
                    }
                });
                closeConfirmDialog.showDialog();
            }
        });

        teamAdapter.setOnItemClickListener(new RCommonAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                if (position < 0 || position == selectedTeamTabs) {
                    //同时点击两个item，可能会返回小于0的positon
                    return;
                }
                int oldSelectedTeamTabs = selectedTeamTabs;
                selectedTeamTabs = position;
                teamAdapter.notifyItemChanged(oldSelectedTeamTabs);
                teamAdapter.notifyItemChanged(selectedTeamTabs);
                btnLikeClickTime = 0;
                studentAdapter.updateData(mTeamList.get(selectedTeamTabs).getStudentList());
                rvStudentlist.setBackgroundResource(tabsBackgroundRes[selectedTeamTabs]);
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
    }

    private void fadeIn() {
        float curTranslationX = tvLikeCount.getTranslationX();
        ObjectAnimator translationY = ObjectAnimator.ofFloat(tvLikeCount, "translationY", caculateVerticalMargin(SizeUtils.Dp2Px(mContext, 20)), curTranslationX);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(tvLikeCount, "alpha", 0f, 1f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(translationY).with(alpha);
        animSet.setDuration(500);
        animSet.start();
    }

    private void fadeOut() {
        float curTranslationX = tvLikeCount.getTranslationX();
        ObjectAnimator translationY = ObjectAnimator.ofFloat(tvLikeCount, "translationY", curTranslationX, caculateVerticalMargin(SizeUtils.Dp2Px(mContext, -20)));
        ObjectAnimator alpha = ObjectAnimator.ofFloat(tvLikeCount, "alpha", 1f, 0f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(translationY).with(alpha);
        animSet.setDuration(500);
        animSet.start();
        animSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                tvLikeCount.setVisibility(View.GONE);
                lottieAnimationLoopStarView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    /**
     * 根据效果图比例动态计算控件垂直方向边距
     *
     * @param rate
     * @return
     */
    private int caculateVerticalMargin(int rate) {

        int measuredHeight = lottieAnimationBGView.getMeasuredHeight();
        int measudredWidth = lottieAnimationBGView.getMeasuredWidth();
        if (measudredWidth * 3 <= measuredHeight * 4) {
            //水平方向上截断
            return (rate * measuredHeight) / SizeUtils.Dp2Px(mContext, 375);
        } else {
            //数值方向上截断
            return (rate * measudredWidth) / SizeUtils.Dp2Px(mContext, 500);
        }
    }


    public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if (parent.getChildLayoutPosition(view) < MAX_STUDENT_COLUMN_NUMBER) {
                outRect.top = space / 2;
            }
        }
    }

    /**
     * 二倍卡触发次数
     */
    int doubleCardCount = 0;
    /**
     * 普通学生最多触发2次
     */
    private static int NORMOL_MAX_DOUBLE_CARD_COUNT = 2;
    /**
     * 新生和未续保学生最多触发4次
     */
    private static int VIP_MAX_DOUBLE_CARD_COUNT = 4;

    /**
     * 是否触发二倍卡  1：概率不加倍  2：概率加倍
     */
    public boolean findDoubleCard() {
        int probability = mPresenter.getProbability();
        int maxCount;
        if (probability == 2) {
            maxCount = VIP_MAX_DOUBLE_CARD_COUNT;
        } else {
            maxCount = NORMOL_MAX_DOUBLE_CARD_COUNT;
        }
        if (doubleCardCount >= maxCount) {
            return false;
        }
        if (myTeamTabs != selectedTeamTabs) {
            //给其他战队点赞 概率加倍
            probability = 2;
        } else if (mTeamList.get(selectedTeamTabs).getTeamMemberNums() <= 10) {
            //战队人数小于等于10人 概率加倍
            probability = 2;
        }
        Random random = new Random();
        int i;
        if (probability == 2) {
            i = random.nextInt(1000);
            if (i < 10) {
                doubleCardCount++;
                return true;
            }
        } else {
            i = random.nextInt(1000);
            if (i < 4) {
                doubleCardCount++;
                return true;
            }
        }
        return false;
    }

    /**
     * 表扬榜背景 动画
     */
    public void startBackgtoundAnimation() {
        logger.d("startBackgtoundAnimation");

        //优秀榜背景动画
        String bacnkgroundResPath = LOTTIE_RES_ASSETS_ROOTDIR + "background/images";
        String bacnkgroundJsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "background/data.json";
        final LottieEffectInfo backgroundEffectInfo = new LottieEffectInfo(bacnkgroundResPath, bacnkgroundJsonPath);

        //计算小超市背景动画
        String miniMarketResPath = LOTTIE_RES_ASSETS_ROOTDIR + "background/images_mini_market";
        final LottieEffectInfo miniMarketEffectInfo = new LottieEffectInfo(miniMarketResPath, bacnkgroundJsonPath);

        //点赞榜背景动画
        String likeResPath = LOTTIE_RES_ASSETS_ROOTDIR + "background/images_like";
        final LottieEffectInfo likeEffectInfo = new LottieEffectInfo(likeResPath, bacnkgroundJsonPath);

        //删除资源
        final ArrayList<String> bacnkgroundDeleteRes = new ArrayList<>(3);
        bacnkgroundDeleteRes.add("img_0.png");
        bacnkgroundDeleteRes.add("img_1.png");
        bacnkgroundDeleteRes.add("img_2.png");
        bacnkgroundDeleteRes.add("img_3.png");
        bacnkgroundDeleteRes.add("img_4.png");
        bacnkgroundDeleteRes.add("img_5.png");
        bacnkgroundDeleteRes.add("img_6.png");
        bacnkgroundDeleteRes.add("img_7.png");
        bacnkgroundDeleteRes.add("img_8.png");
        bacnkgroundDeleteRes.add("img_8.png");
        bacnkgroundDeleteRes.add("img_10.png");
        bacnkgroundDeleteRes.add("img_15.png");
        bacnkgroundDeleteRes.add("img_44.png");

        //计算小超市榜资源替换
        final Map<String, String> miniMarketToBackgroundRes = new HashMap<>();
        if (listType == PRAISE_LIST_TYPE_MINI_MARKET && minimarketListEntity != null) {
            if (minimarketListEntity.getTitleId() == 1) {
                miniMarketToBackgroundRes.put("img_9.png", "img_9_1.png");
            } else {
                miniMarketToBackgroundRes.put("img_9.png", "img_9_2.png");
            }
        }

        //点赞榜资源替换
        final Map<String, String> likeToBackgroundRes = new HashMap<>();
        likeToBackgroundRes.put("img_9.png", "img_9.png");

        ImageAssetDelegate imageAssetDelegate = new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                String fileName = lottieImageAsset.getFileName();
                if (bacnkgroundDeleteRes.contains(fileName)) {
                    return null;
                }
                if (listType == PRAISE_LIST_TYPE_MINI_MARKET) {
                    if (miniMarketToBackgroundRes.containsKey(fileName)) {
                        return miniMarketEffectInfo.fetchBitmapFromAssets(
                                lottieAnimationBGView,
                                miniMarketToBackgroundRes.get(fileName),
                                lottieImageAsset.getId(),
                                lottieImageAsset.getWidth(),
                                lottieImageAsset.getHeight(),
                                mContext);
                    }
                } else if (listType == PRAISE_LIST_TYPE_LIKE) {
                    if (likeToBackgroundRes.containsKey(fileName)) {
                        return likeEffectInfo.fetchBitmapFromAssets(
                                lottieAnimationBGView,
                                likeToBackgroundRes.get(fileName),
                                lottieImageAsset.getId(),
                                lottieImageAsset.getWidth(),
                                lottieImageAsset.getHeight(),
                                mContext);
                    }
                }
                return backgroundEffectInfo.fetchBitmapFromAssets(
                        lottieAnimationBGView,
                        lottieImageAsset.getFileName(),
                        lottieImageAsset.getId(),
                        lottieImageAsset.getWidth(),
                        lottieImageAsset.getHeight(),
                        mContext);
            }
        };

        lottieAnimationBGView.setAnimationFromJson(backgroundEffectInfo.getJsonStrFromAssets(mContext), "background");
        lottieAnimationBGView.useHardwareAcceleration(true);
        lottieAnimationBGView.setImageAssetDelegate(imageAssetDelegate);
        lottieAnimationBGView.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedFraction = animation.getAnimatedFraction();
                if (animatedFraction > 0.1) {
                    lottieAnimationBGView.removeUpdateListener(this);
                    contentGroup.setVisibility(View.VISIBLE);
                    likeContentGroup.setVisibility(View.VISIBLE);
                    if (listType != PRAISE_LIST_TYPE_LIKE) {
                        startLoopStarAnimation();
                    }
                    startLoopLightAnimation();
                }
            }
        });
        lottieAnimationBGView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                //截屏
                StudyReportAction studyReportAction = ProxUtil.getProxUtil().get(mContext, StudyReportAction.class);
                if (studyReportAction != null && isOnList) {
                    if (listType == PRAISE_LIST_TYPE_EXECELLENT) {
                        studyReportAction.cutImage(LiveVideoConfig.STUDY_REPORT.TYPE_5, mView, false, false);
                    } else if (listType == PRAISE_LIST_TYPE_MINI_MARKET) {
                        studyReportAction.cutImage(LiveVideoConfig.STUDY_REPORT.TYPE_4, mView, false, false);
                    } else if (listType == PRAISE_LIST_TYPE_LIKE) {
                        studyReportAction.cutImage(LiveVideoConfig.STUDY_REPORT.TYPE_6, mView, false, false);
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        lottieAnimationBGView.playAnimation();
    }

    /**
     * 循环光芒 动画
     */
    private void startLoopLightAnimation() {
        lottieAnimationLoopLightView.useHardwareAcceleration(true);
        lottieAnimationLoopLightView.playAnimation();
    }

    /**
     * 点赞 循环星星 动画
     */
    private void startLoopStarAnimation() {
        lottieAnimationLoopStarView.setVisibility(View.VISIBLE);
        lottieAnimationLoopStarView.useHardwareAcceleration(true);
        lottieAnimationLoopStarView.playAnimation();
    }

    /**
     * 点赞 发现二倍卡 动画
     */
    private void startDoubleCardAnimation() {
        lottieAnimationDoubleCardView.setVisibility(View.VISIBLE);
        lottieAnimationDoubleCardView.useHardwareAcceleration(true);
        lottieAnimationDoubleCardView.playAnimation();
    }

    /**
     * 点赞 点击 动画
     */
    private void startClickLikeAnimation() {
        final LottieAnimationView lottieAnimationClickLikeView = new LottieAnimationView(mContext);
        lottieClickLikeGroup.addView(lottieAnimationClickLikeView);
        lottieAnimationClickLikeView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        RelativeLayout.LayoutParams clickLikeParams = (RelativeLayout.LayoutParams) lottieAnimationClickLikeView.getLayoutParams();
        clickLikeParams.rightMargin = caculateVerticalMargin(SizeUtils.Dp2Px(mContext, 20));
        clickLikeParams.bottomMargin = caculateVerticalMargin(SizeUtils.Dp2Px(mContext, 20));
        clickLikeParams.leftMargin = caculateVerticalMargin(SizeUtils.Dp2Px(mContext, -20));
        clickLikeParams.topMargin = caculateVerticalMargin(SizeUtils.Dp2Px(mContext, -20));
        lottieAnimationClickLikeView.setLayoutParams(clickLikeParams);
        lottieAnimationClickLikeView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                lottieClickLikeGroup.removeView(lottieAnimationClickLikeView);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        String bubbleResPath = LOTTIE_RES_ASSETS_ROOTDIR + "click_like/images";
        String bubbleJsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "click_like/data.json";
        final LottieEffectInfo bubbleEffectInfo = new LottieEffectInfo(bubbleResPath, bubbleJsonPath);
        lottieAnimationClickLikeView.setAnimationFromJson(bubbleEffectInfo.getJsonStrFromAssets(mContext), "click_like");
        lottieAnimationClickLikeView.useHardwareAcceleration(true);
        ImageAssetDelegate imageAssetDelegate = new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                return bubbleEffectInfo.fetchBitmapFromAssets(
                        lottieAnimationClickLikeView,
                        lottieImageAsset.getFileName(),
                        lottieImageAsset.getId(),
                        lottieImageAsset.getWidth(),
                        lottieImageAsset.getHeight(),
                        mContext);
            }
        };
        lottieAnimationClickLikeView.setImageAssetDelegate(imageAssetDelegate);
        lottieAnimationClickLikeView.playAnimation();
    }

    /**
     * 老师表扬横幅 动画
     */
    public void startScrollAnimation(String stuName, String tecName) {
        if (!isOnList) {
            return;
        }
        String student = stuName + "同学";
        String str1 = " 获得 ";
        String teacher = tecName + "老师";
        String str2 = " 的重点表扬，要努力继续上榜哦～";
        SpannableString spanText = new SpannableString(student + str1 + teacher + str2);
        spanText.setSpan(new AbsoluteSizeSpan(SizeUtils.Dp2Px(mContext, 18)), 0, student.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        int start = student.length() + str1.length();
        spanText.setSpan(new AbsoluteSizeSpan(SizeUtils.Dp2Px(mContext, 18)), start, start + teacher.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        teacherTipsView.setText(spanText);

        teacherTipsView.setVisibility(View.GONE);
        lottieAnimationTeacherGroup.setVisibility(View.VISIBLE);
        lottieAnimationTeacherView.useHardwareAcceleration(true);
        lottieAnimationTeacherView.playAnimation();

        lottieAnimationTeacherView.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedFraction = valueAnimator.getAnimatedFraction();
                if (animatedFraction > 0.1) {
                    teacherTipsView.setVisibility(View.VISIBLE);
                }
                if (animatedFraction > 0.4) {
                    lottieAnimationTeacherGroup.setVisibility(View.GONE);
                    lottieAnimationTeacherView.cancelAnimation();
                    lottieAnimationTeacherView.removeUpdateListener(this);
                }
            }
        });
    }

    /**
     * 收到老师广播赞数的消息
     */
    public void receiveLikeNotice(final List<PraiseListDanmakuEntity> danmakuList) {
        for (int i = 0; i < danmakuList.size(); i++) {
            if (danmakuList.get(i).getBarrageType() == 1) {
                if (!danmakuList.get(i).getName().equals(mPresenter.getStuName())) {
                    //过滤掉自己点赞数的消息
                    this.stuDanmakuCache.add(danmakuList.get(i));
                }
            } else if (danmakuList.get(i).getBarrageType() == 2) {
                this.teamDanmakuCache.add(danmakuList.get(i));
            }
        }

        if (this.teamDanmakuCache.size() != 0 || this.stuDanmakuCache.size() != 0) {
            //如果点赞消息列表不为空，开始滚动弹幕
            if (danmakuTimer == null) {
                danmakuTimer = new Timer();
                danmakuTimer.schedule(new DanmakuTimerTask(), 0, DURATION_DANMAKU_SCROOL);
            }
        }
    }

    private class DanmakuTimerTask extends TimerTask {
        @Override
        public void run() {
            mWeakHandler.post(new Runnable() {
                @Override
                public void run() {
                    //优先级：战队>自己>学生
                    if (teamDanmakuCount < teamDanmakuCache.size()) {
                        addDanmaku(teamDanmakuCache.get(teamDanmakuCount));
                        teamDanmakuCount++;
                    } else if (myDanmakuCount < myDanmakuCache.size()) {
                        addDanmaku(myDanmakuCache.get(myDanmakuCount));
                        myDanmakuCount++;
                    } else if (stuDanmakuCount < stuDanmakuCache.size()) {
                        addDanmaku(stuDanmakuCache.get(stuDanmakuCount));
                        stuDanmakuCount++;
                    }
                }
            });
        }
    }

    private class LikeTimerTask extends TimerTask {
        @Override
        public void run() {
            mWeakHandler.post(new Runnable() {
                @Override
                public void run() {
                    //自己的点赞数本地显示
                    totalLikeSum = 0;
                    latestLikeSum = 0;
                    for (int i = 0; i < mTeamList.size(); i++) {
                        totalLikeSum += totalLikeCount[i];
                        latestLikeSum += latestLikeCount[i];
                    }
                    logger.i("totalLikeSum=" + totalLikeSum + " latestLikeSum=" + latestLikeSum);
                    if (totalLikeSum != latestLikeSum) {
                        PraiseListDanmakuEntity praiseListDanmakuEntity = new PraiseListDanmakuEntity();
                        praiseListDanmakuEntity.setBarrageType(1);
                        praiseListDanmakuEntity.setNumber(totalLikeSum);
                        praiseListDanmakuEntity.setName(mPresenter.getStuName());

                        myDanmakuCache.add(praiseListDanmakuEntity);
                        if (myDanmakuCache.size() != 0) {
                            //如果点赞消息列表不为空，开始滚动弹幕
                            if (danmakuTimer == null) {
                                danmakuTimer = new Timer();
                                danmakuTimer.schedule(new DanmakuTimerTask(), 0, DURATION_DANMAKU_SCROOL);
                            }
                        }
                    }

                    //上传点赞数
                    for (int i = 0; i < mTeamList.size(); i++) {
                        if (totalLikeCount[i] != latestLikeCount[i]) {
                            int increment = totalLikeCount[i] - latestLikeCount[i];
                            mPresenter.sendLikeNum(increment, mTeamList.get(i).getPkTeamId(), 1);
                            latestLikeCount[i] = totalLikeCount[i];
                        }
                    }
                }
            });
        }
    }

    public void showLikeToast() {
        StableLogHashMap logHashMap = new StableLogHashMap("praisePraiseList");
        logHashMap.put("listtype", "" + listType);
        logHashMap.put("stable", "2");
        logHashMap.put("expect", "1");
        logHashMap.put("sno", "5");
        logHashMap.put("ex", "Y");
        umsAgentDebugInter(LiveVideoConfig.LIVE_PRAISE_LIST, logHashMap.getData());
    }


    /**
     * 战队列表item
     */
    class TeamItem implements RItemViewInterface<PraiseListTeamEntity> {
        ImageView ivHead;
        ImageView ivMedal;
        ImageView ivLike;
        TextView tvCount;

        @Override
        public int getItemLayoutId() {
            return R.layout.item_livevideo_praiselist_team;
        }

        @Override
        public boolean isShowView(PraiseListTeamEntity item, int position) {
            return true;
        }

        @Override
        public void initView(ViewHolder holder, int position) {
            ivHead = holder.getView(R.id.iv_livevideo_praiselist_team_head);
            ivMedal = holder.getView(R.id.iv_livevideo_praiselist_team_medal);
            ivLike = holder.getView(R.id.iv_livevideo_praiselist_team_like);
            tvCount = holder.getView(R.id.tv_livevideo_praiselist_team_count);

            RecyclerView.LayoutParams holderParams = (RecyclerView.LayoutParams) holder.getConvertView().getLayoutParams();
            holderParams.width = rvTeamList.getMeasuredWidth() / MAX_TEAM_NUMBER;

            RelativeLayout.LayoutParams headParams = (RelativeLayout.LayoutParams) ivHead.getLayoutParams();
            if (selectedTeamTabs == position) {
                headParams.height = caculateVerticalMargin(SizeUtils.Dp2Px(mContext, 48));
            } else {
                headParams.height = caculateVerticalMargin(SizeUtils.Dp2Px(mContext, 40));
            }

            RelativeLayout.LayoutParams medalParams = (RelativeLayout.LayoutParams) ivMedal.getLayoutParams();
            medalParams.width = caculateVerticalMargin(SizeUtils.Dp2Px(mContext, 13));
            medalParams.height = caculateVerticalMargin(SizeUtils.Dp2Px(mContext, 16));
        }

        @Override
        public void convert(ViewHolder holder, PraiseListTeamEntity teamEntity, final int position) {
            if (selectedTeamTabs == position) {
                //这里自己做缓存。因为频繁切换战队页情况下，Glide缓存有闪动
//                if (pressImg[position] != null) {
//                    ivHead.setImageDrawable(pressImg[position]);
//                } else {
//                    ivHead.setImageResource(R.drawable.icon_livevideo_praiselist_team_head_default);
//                    ImageLoader.with(mContext).load(teamEntity.getPressImg()).asBitmap(new SingleConfig.BitmapListener() {
//                        @Override
//                        public void onSuccess(Drawable drawable) {
//                            pressImg[position] = drawable;
//                            ivHead.setImageDrawable(drawable);
//                        }
//
//                        @Override
//                        public void onFail() {
//
//                        }
//                    });
//                }

                ImageLoader.with(mContext).load(teamEntity.getPressImg()).error(R.drawable.icon_livevideo_praiselist_team_head_default).into(ivHead);
            } else {
//                if (normalImg[position] != null) {
//                    ivHead.setImageDrawable(normalImg[position]);
//                } else {
//                    ivHead.setImageResource(R.drawable.icon_livevideo_praiselist_team_head_default);
//                    ImageLoader.with(mContext).load(teamEntity.getNormalImg()).asBitmap(new SingleConfig.BitmapListener() {
//                        @Override
//                        public void onSuccess(Drawable drawable) {
//                            normalImg[position] = drawable;
//                            ivHead.setImageDrawable(drawable);
//                        }
//
//                        @Override
//                        public void onFail() {
//                        }
//                    });
//                }

                ImageLoader.with(mContext).load(teamEntity.getNormalImg()).error(R.drawable.icon_livevideo_praiselist_team_head_default).into(ivHead);
            }

            switch (listType) {
                case PRAISE_LIST_TYPE_EXECELLENT:
                case PRAISE_LIST_TYPE_MINI_MARKET: {
                    ivLike.setVisibility(View.GONE);
                    tvCount.setText(teamEntity.getOnListNums() + "/" + teamEntity.getTeamMemberNums());
                    break;
                }
                case PRAISE_LIST_TYPE_LIKE: {
                    ivLike.setVisibility(View.VISIBLE);

                    if (teamEntity.getPraiseTotalNum() >= 10000) {
                        double d = (double) teamEntity.getPraiseTotalNum() / (double) 10000;
                        BigDecimal b = new BigDecimal(d);
                        d = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        tvCount.setText(d + "万");
                    } else {
                        tvCount.setText(teamEntity.getPraiseTotalNum() + "");
                    }
                    break;
                }
                default:
                    break;
            }

            switch (teamEntity.getTeamRanking()) {
                case 1: {
                    ivMedal.setVisibility(View.VISIBLE);
                    ivMedal.setImageResource(R.drawable.livevideo_list_jin_icon);
                    break;
                }
                case 2: {
                    ivMedal.setVisibility(View.VISIBLE);
                    ivMedal.setImageResource(R.drawable.livevideo_list_yin_icon);
                    break;
                }
                case 3: {
                    ivMedal.setVisibility(View.VISIBLE);
                    ivMedal.setImageResource(R.drawable.livevido_list_tong_icon);
                    break;
                }
                default:
                    ivMedal.setVisibility(View.GONE);
                    break;
            }
        }
    }

    /**
     * 学生列表item
     */
    private class StudentItem implements RItemViewInterface<PraiseListStudentEntity> {
        TextView tvName;
        RelativeLayout rlCount;
        ImageView ivLike;
        TextView tvCount;

        int tvNameWidth;

        @Override
        public int getItemLayoutId() {
            return R.layout.item_livevideo_praiselist_excellent;
        }

        @Override
        public void initView(ViewHolder viewHolder, int i) {
            tvName = viewHolder.getView(R.id.tv_livevideo_praiselist_excellent_name);
            rlCount = viewHolder.getView(R.id.rl_livevideo_praiselist_excellent_count);
            ivLike = viewHolder.getView(R.id.iv_livevideo_praiselist_excellent_like);
            tvCount = viewHolder.getView(R.id.tv_livevideo_praiselist_excellent_count);

            tvNameWidth = (int) Math.ceil(tvName.getPaint().measureText("一二三... "));
        }

        @Override
        public boolean isShowView(PraiseListStudentEntity studentEntity, int i) {
            return true;
        }

        @Override
        public void convert(ViewHolder viewHolder, PraiseListStudentEntity studentEntity, int i) {
            if (studentEntity != null) {
                String stuName = studentEntity.getStuName();
                if (!TextUtils.isEmpty(stuName)) {
                    if (stuName.length() > 4) {
                        stuName = stuName.substring(0, 3) + "...";
                    }
                }
                tvName.setText(stuName);
                tvName.setWidth(tvNameWidth);
                if (studentEntity.getIsMy() == 1) {
                    //自己名字高亮
                    tvName.setTextColor(Color.parseColor("#FFAE00"));
                } else {
                    tvName.setTextColor(Color.parseColor("#7D553F"));
                }

                switch (listType) {
                    case PRAISE_LIST_TYPE_EXECELLENT:
                        rlCount.setVisibility(View.INVISIBLE);
                        break;
                    case PRAISE_LIST_TYPE_MINI_MARKET: {
                        rlCount.setVisibility(View.VISIBLE);
                        ivLike.setVisibility(View.GONE);
                        tvCount.setText(studentEntity.getExcellentNum() + "次");
                        break;
                    }
                    case PRAISE_LIST_TYPE_LIKE: {
                        rlCount.setVisibility(View.VISIBLE);
                        ivLike.setVisibility(View.VISIBLE);
                        if (studentEntity.getExcellentNum() >= 10000) {
                            double d = (double) studentEntity.getExcellentNum() / (double) 10000;
                            BigDecimal b = new BigDecimal(d);
                            d = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                            tvCount.setText(d + "万");
                        } else {
                            tvCount.setText(studentEntity.getExcellentNum() + "");
                        }
                        break;
                    }
                    default:
                        break;
                }
            }
        }
    }

    private void stopDanmaku() {
        if (danmakuTimer != null) {
            danmakuTimer.cancel();
        }
        danmakuTimer = null;
        if (likeTimer != null) {
            likeTimer.cancel();
        }
        likeTimer = null;
    }

    private void releaseSoundPool() {
        if (mSoundPool != null)
            mSoundPool.release();
        mSoundPool = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopDanmaku();
        releaseSoundPool();
    }
}
