package com.xueersi.parentsmeeting.modules.livevideo.enteampk.pager;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.xueersi.common.base.BasePager;
import com.xueersi.common.config.AppConfig;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.config.EnTeamPkConfig;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.EnTeamPkRankEntity;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.TeamMemberEntity;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.item.TeamMemberStarItem;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 贡献之星
 */
public class TeamPkLeadPager extends LiveBasePager {
    /** 贡献之星，课中 */
    public static int TEAM_TYPE_1 = 1;
    /** 贡献之星，课后 */
    public static int TEAM_TYPE_2 = 2;
    private static int WIN_VIEW_SHOW = 3000;
    private int type;
    private EnTeamPkRankEntity enTeamPkRankEntity;
    private RelativeLayout rlTeampkLeadBottom;
    private ProgressBar pgTeampkLead;
    private View rlTeampkLeadLeft;
    private View rlTeampkLeadRight;
    private RelativeLayout rlTeampkLead;
    private ImageView ivTeampkLeadProg;
    private ImageView ivTeampkMine;
    private ImageView ivTeampkOther;
    private TextView tvTeampkLeadFireAddLeft;
    private TextView tvTeampkLeadScoreLeft;
    private TextView ivTeampkLeadFireAddRight;
    private TextView tvTeampkLeadScoreRight;
    private int pattern;
    private float finalFprog;
    private Handler handler = new Handler(Looper.getMainLooper());
    private OnClose onClose;
    private OnStudyClick onStudyClick;
    private ArrayList<TeamMemberStarItem> teamMemberStarItems = new ArrayList<>();
    private String testId;

    public TeamPkLeadPager(Context context, EnTeamPkRankEntity enTeamPkRankEntity, String testId, int type, int pattern, OnClose onClose) {
        super(context, false);
        this.testId = testId;
        this.type = type;
        this.pattern = pattern;
        this.onClose = onClose;
        mView = initView();
        this.enTeamPkRankEntity = enTeamPkRankEntity;
        initData();
        initListener();
    }

    public String getTestId() {
        return testId;
    }

    public void setOnStudyClick(OnStudyClick onStudyClick) {
        this.onStudyClick = onStudyClick;
    }

    @Override
    public View initView() {
        View view;
        if (type == TEAM_TYPE_2) {
            view = LayoutInflater.from(mContext).inflate(R.layout.page_livevideo_en_team_result, null);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.page_livevideo_en_team_lead, null);
        }
        rlTeampkLeadBottom = view.findViewById(R.id.rl_livevideo_en_teampk_lead_bottom);
        RelativeLayout.LayoutParams bottomLayoutParams = (RelativeLayout.LayoutParams) rlTeampkLeadBottom.getLayoutParams();
        if (pattern == 2) {
            view.setBackgroundResource(R.drawable.bg_livevideo_en_team_bg_16_9);
            bottomLayoutParams.height = SizeUtils.Dp2Px(mContext, 154);
        } else {
            view.setBackgroundResource(R.drawable.bg_livevideo_en_team_bg_4_3);
            bottomLayoutParams.height = SizeUtils.Dp2Px(mContext, 153);
        }
        LayoutParamsUtil.setViewLayoutParams(rlTeampkLeadBottom, bottomLayoutParams);
        pgTeampkLead = view.findViewById(R.id.pg_livevideo_en_teampk_lead);
        rlTeampkLeadLeft = view.findViewById(R.id.rl_livevideo_en_teampk_lead_left);
        rlTeampkLeadRight = view.findViewById(R.id.rl_livevideo_en_teampk_lead_right);
        rlTeampkLead = view.findViewById(R.id.rl_livevideo_en_teampk_lead);
        ivTeampkLeadProg = view.findViewById(R.id.iv_livevideo_en_teampk_lead_prog);
        ivTeampkMine = view.findViewById(R.id.iv_livevideo_en_teampk_mine);
        ivTeampkOther = view.findViewById(R.id.iv_livevideo_en_teampk_other);
        tvTeampkLeadFireAddLeft = view.findViewById(R.id.tv_livevideo_en_teampk_lead_fire_add_left);
        tvTeampkLeadScoreLeft = view.findViewById(R.id.tv_livevideo_en_teampk_lead_score_left);
        ivTeampkLeadFireAddRight = view.findViewById(R.id.iv_livevideo_en_teampk_lead_fire_add_right);
        tvTeampkLeadScoreRight = view.findViewById(R.id.tv_livevideo_en_teampk_lead_score_right);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        int win = enTeamPkRankEntity.getMyTeamTotal() - enTeamPkRankEntity.getOpTeamTotal();
        if (type == TEAM_TYPE_2) {
            mLogtf.d("initData:win=" + win);
            ImageView iv_livevideo_en_teampk_lead_mid = mView.findViewById(R.id.iv_livevideo_en_teampk_lead_mid);
            {
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) iv_livevideo_en_teampk_lead_mid.getLayoutParams();
                if (win >= 0) {
                    iv_livevideo_en_teampk_lead_mid.setImageResource(R.drawable.bg_livevideo_en_teampk_result_win);
                } else {
                    iv_livevideo_en_teampk_lead_mid.setImageResource(R.drawable.bg_livevideo_en_teampk_result_lost);
                }
                lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
                lp.width = SizeUtils.Dp2Px(mContext, 137);
                lp.height = SizeUtils.Dp2Px(mContext, 47);
                iv_livevideo_en_teampk_lead_mid.setLayoutParams(lp);
            }
            tvTeampkLeadFireAddLeft.setVisibility(View.GONE);
            ivTeampkLeadFireAddRight.setVisibility(View.GONE);
            mView.findViewById(R.id.iv_livevideo_en_teampk_lead_fire_left).setVisibility(View.GONE);
            mView.findViewById(R.id.iv_livevideo_en_teampk_lead_fire_right).setVisibility(View.GONE);
            ViewGroup group = (ViewGroup) mView;
            ImageView imageView = new ImageView(mContext);
            imageView.setImageResource(R.drawable.livevideo_enpk_gongxihuoshegn_guang_pic);
            int width = SizeUtils.Dp2Px(mContext, 214);
            if (win == 0) {
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, width);
//                lp.leftMargin = SizeUtils.Dp2Px(mContext, 13);
//                lp.topMargin = SizeUtils.Dp2Px(mContext, 17);
                group.addView(imageView, 0, lp);
                setBg(ivTeampkMine, imageView);
                //打平了，两个背景
                ImageView imageView2 = new ImageView(mContext);
                imageView2.setImageResource(R.drawable.livevideo_enpk_gongxihuoshegn_guang_pic);
                RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(width, width);
//                lp2.leftMargin = SizeUtils.Dp2Px(mContext, 275);
//                lp2.topMargin = SizeUtils.Dp2Px(mContext, 17);
                group.addView(imageView2, 0, lp2);
                setBg(ivTeampkOther, imageView2);
            } else if (win > 0) {
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, width);
//                lp.leftMargin = SizeUtils.Dp2Px(mContext, 13);
//                lp.topMargin = SizeUtils.Dp2Px(mContext, 17);
                group.addView(imageView, 0, lp);
                setBg(ivTeampkMine, imageView);
            } else {
//                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, width);
//                lp.leftMargin = SizeUtils.Dp2Px(mContext, 275);
//                lp.topMargin = SizeUtils.Dp2Px(mContext, 17);
//                group.addView(imageView, 0, lp);
//                setBg(ivTeampkOther, imageView);
            }
        } else {
            int lastM = enTeamPkRankEntity.getMyTeamTotal() - enTeamPkRankEntity.getMyTeamCurrent();
            int lastO = enTeamPkRankEntity.getOpTeamTotal() - enTeamPkRankEntity.getOpTeamCurrent();
            int lastWin = lastM - lastO;
            ImageView iv_livevideo_en_teampk_lead_left = mView.findViewById(R.id.iv_livevideo_en_teampk_lead_left);
            ImageView iv_livevideo_en_teampk_lead_right = mView.findViewById(R.id.iv_livevideo_en_teampk_lead_right);
            ImageView iv_livevideo_en_teampk_lead_mid = mView.findViewById(R.id.iv_livevideo_en_teampk_lead_mid);
            String s;
            if (win == 0) {
                s = "0";
                iv_livevideo_en_teampk_lead_mid.setImageResource(R.drawable.bg_livevideo_en_teampk_lead_mid);
            } else {
                if (win > 0) {
                    if (lastWin > 0) {
                        s = "1";
                        iv_livevideo_en_teampk_lead_left.setImageResource(R.drawable.bg_livevideo_en_teampk_lead_win);
                        iv_livevideo_en_teampk_lead_right.setImageResource(R.drawable.bg_livevideo_en_teampk_lead_lost);
                    } else {
                        s = "2";
                        iv_livevideo_en_teampk_lead_left.setImageResource(R.drawable.bg_livevideo_en_teampk_lead_exceed);
                        iv_livevideo_en_teampk_lead_right.setImageResource(R.drawable.bg_livevideo_en_teampk_lead_wait);
//                        XESToastUtils.showToast(mContext, "恭喜反超对手");
                    }
                } else {
                    if (lastWin < 0) {
                        s = "3";
                        iv_livevideo_en_teampk_lead_left.setImageResource(R.drawable.bg_livevideo_en_teampk_lead_lost);
                        iv_livevideo_en_teampk_lead_right.setImageResource(R.drawable.bg_livevideo_en_teampk_lead_win);
                    } else {
                        s = "4";
                        iv_livevideo_en_teampk_lead_left.setImageResource(R.drawable.bg_livevideo_en_teampk_lead_wait);
                        iv_livevideo_en_teampk_lead_right.setImageResource(R.drawable.bg_livevideo_en_teampk_lead_exceed);
                    }
                }
            }
            mLogtf.d("initData:win=" + win + ",last=" + lastWin + ",s=" + s);
        }
        int[] res = EnTeamPkConfig.TEAM_RES;
        ivTeampkMine.setImageResource(res[enTeamPkRankEntity.getMyTeam()]);
        int progress = 50;
        float fprog = 0.5f;
        int total = enTeamPkRankEntity.getMyTeamTotal() + enTeamPkRankEntity.getOpTeamTotal();
        ivTeampkOther.setImageResource(res[enTeamPkRankEntity.getBpkTeamId()]);
        tvTeampkLeadFireAddLeft.setText("+" + enTeamPkRankEntity.getMyTeamCurrent());
        tvTeampkLeadScoreLeft.setText("" + enTeamPkRankEntity.getMyTeamTotal());
        ivTeampkLeadFireAddRight.setText("+" + enTeamPkRankEntity.getOpTeamCurrent());
        tvTeampkLeadScoreRight.setText("" + enTeamPkRankEntity.getOpTeamTotal());
        if (total != 0) {
            fprog = (float) (enTeamPkRankEntity.getMyTeamTotal()) / (float) (total);
            progress = (int) ((float) (enTeamPkRankEntity.getMyTeamTotal() * 100) / (float) (total));
        }
        int closeDelay = type == TeamPkLeadPager.TEAM_TYPE_2 ? 10000 : 5000;
//        if (AppConfig.DEBUG) {
//            closeDelay = type == TeamPkLeadPager.TEAM_TYPE_2 ? 60000 : 60000;
//            ivTeampkMine.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (onClose != null) {
//                        onClose.close(TeamPkLeadPager.this);
//                    }
//                }
//            });
//        }
        showRank();
        final TextView tv_livevideo_en_teampk_rank_start_close = rlTeampkLeadBottom.findViewById(R.id.tv_livevideo_en_teampk_rank_start_close);
        final AtomicInteger integer = new AtomicInteger(closeDelay / 1000);
        int countDelay = 1000;
        if (type == TEAM_TYPE_2 && win >= 0) {
            countDelay += WIN_VIEW_SHOW;
            final ViewGroup group = (ViewGroup) mView;
            final View view = LayoutInflater.from(mContext).inflate(R.layout.layout_livevideo_en_team_lead_win, group, false);
            ImageView ivResult = view.findViewById(R.id.iv_livevideo_enpk_jieguo_pic_nor);
            if (win == 0) {
                ivResult.setImageResource(R.drawable.livevideo_enpk_jieguo_pingshou_pic_nor);
            } else {
                ivResult.setImageResource(R.drawable.livevideo_enpk_jieguo_shengli_pic_nor);
            }
            group.addView(view);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    group.removeView(view);
                    tv_livevideo_en_teampk_rank_start_close.setText(integer.get() + "s后关闭");
                }
            }, WIN_VIEW_SHOW);
        } else {
            tv_livevideo_en_teampk_rank_start_close.setText(integer.get() + "s后关闭");
        }
        pgTeampkLead.setProgress(progress);
        finalFprog = fprog;
        mLogtf.d("initData:type=" + type + ",fprog=" + fprog);
        final ViewTreeObserver viewTreeObserver = pgTeampkLead.getViewTreeObserver();
        viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (viewTreeObserver.isAlive()) {
                    viewTreeObserver.removeOnPreDrawListener(this);
                }
                pgTeampkLead.getViewTreeObserver().removeOnPreDrawListener(this);
                setProgFire();
                return false;
            }
        });
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int count = integer.decrementAndGet();
                if (count == 0) {
                    if (onClose != null) {
                        onClose.close(TeamPkLeadPager.this);
                    } else {
                        ViewGroup group = (ViewGroup) mView.getParent();
                        if (group != null) {
                            group.removeView(mView);
                        }
                    }
                } else {
                    setCloseText(tv_livevideo_en_teampk_rank_start_close, integer);
                    tv_livevideo_en_teampk_rank_start_close.postDelayed(this, 1000);
                }
            }
        }, countDelay);
    }

    public void setVideoLayout(LiveVideoPoint liveVideoPoint) {
//        pgTeampkLead.post(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        });
        lastLeftMargin = 0;
        final ViewTreeObserver viewTreeObserver = pgTeampkLead.getViewTreeObserver();
        viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                boolean same = setProgFire();
                if (same) {
                    if (viewTreeObserver.isAlive()) {
                        viewTreeObserver.removeOnPreDrawListener(this);
                    }
                    logger.d("setVideoLayout:equal=" + (viewTreeObserver == pgTeampkLead.getViewTreeObserver()));
                    pgTeampkLead.getViewTreeObserver().removeOnPreDrawListener(this);
                }
                return false;
            }
        });
        setTeamWidth();
    }

    /** 火焰上次的位置 */
    private int lastLeftMargin;
    private int lastwidthPg = 0;

    //设置火焰在进度条的位置
    private boolean setProgFire() {
        //左边的宽度
        int widthLeft = rlTeampkLeadLeft.getWidth();
        //右边的宽度
        int widthRight = rlTeampkLeadRight.getWidth();
        //总体的宽度
        int myWidth = mView.getWidth();
        int widthPg = myWidth - Math.max(widthLeft, widthRight) * 2 - SizeUtils.Dp2Px(mContext, 8);
        //进度条的最大宽度
        int maxPgWidth = SizeUtils.Dp2Px(mContext, 364);
        if (widthPg > maxPgWidth) {
            widthPg = maxPgWidth;
        }
        int pgLeft = (myWidth - widthPg) / 2;
        RelativeLayout.LayoutParams lpIvProg = (RelativeLayout.LayoutParams) ivTeampkLeadProg.getLayoutParams();
        RelativeLayout.LayoutParams lpPg = (RelativeLayout.LayoutParams) pgTeampkLead.getLayoutParams();
        if (lpPg.width != widthPg) {
            //设置进度条的宽度，可以显示下左右的布局
            lpPg.width = widthPg;
            pgTeampkLead.setLayoutParams(lpPg);
        }
        {
            int ivWidth = ivTeampkLeadProg.getWidth();
            int pgLeftMargin = (int) (pgTeampkLead.getLeft() + widthPg * finalFprog) - ivWidth / 2;
            //为了和进度条对齐，计算火的宽度
            float fireRatio = 88.0f / 395.0f;
            int fireWidth = (int) (ivWidth * fireRatio);
            //火最大和进度条右边距对齐
            int maxLeftMargin = (pgTeampkLead.getLeft() + widthPg - ivWidth / 2 - fireWidth / 2);
            logger.d("setProgFire:width=" + mView.getWidth() + ",left=" + pgTeampkLead.getLeft() + ",widthPg=" + widthPg + "," + pgTeampkLead.getWidth() + ",pgLeftMargin=" + pgLeftMargin + ",maxLeftMargin=" + maxLeftMargin);
            int leftMargin2 = Math.min(pgLeftMargin, maxLeftMargin);
            if (lpIvProg.leftMargin != leftMargin2) {
                lpIvProg.leftMargin = leftMargin2;
                ivTeampkLeadProg.setLayoutParams(lpIvProg);
            }
            if (ivTeampkLeadProg.getVisibility() != View.VISIBLE) {
                ivTeampkLeadProg.setVisibility(View.VISIBLE);
            }
        }
        {
            //设置左边的左边距
            RelativeLayout.LayoutParams lpLet = (RelativeLayout.LayoutParams) rlTeampkLeadLeft.getLayoutParams();
            int leftMargin = pgLeft - widthLeft - SizeUtils.Dp2Px(mContext, 4);
            if (leftMargin != lpLet.leftMargin) {
                lpLet.leftMargin = leftMargin;
                rlTeampkLeadLeft.setLayoutParams(lpLet);
            }
        }
        {
            //设置右边的左边距
            RelativeLayout.LayoutParams lpRight = (RelativeLayout.LayoutParams) rlTeampkLeadRight.getLayoutParams();
            int leftMargin = (pgLeft + widthPg) + SizeUtils.Dp2Px(mContext, 4);
            if (leftMargin != lpRight.leftMargin) {
                lpRight.leftMargin = leftMargin;
                rlTeampkLeadRight.setLayoutParams(lpRight);
            }
        }
        //两次距离一样，说明绘制完成
        if (lastLeftMargin == lpIvProg.leftMargin && lastwidthPg == widthPg) {
            return true;
        }
        lastLeftMargin = lpIvProg.leftMargin;
        lastwidthPg = widthPg;
        return false;
    }

    private void setCloseText(TextView textView, AtomicInteger integer) {
//        SpannableStringBuilder spannable = new SpannableStringBuilder(integer + "s后关闭");
//        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#FFFF7A1D")), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(integer + "s后关闭");
    }

    public void onStuLike(ArrayList<TeamMemberEntity> teamMemberEntities) {
        for (int i = 0; i < teamMemberEntities.size(); i++) {
            TeamMemberEntity teamMemberEntity = teamMemberEntities.get(i);
            for (int j = 0; j < teamMemberStarItems.size(); j++) {
                TeamMemberStarItem teamMemberStarItem = teamMemberStarItems.get(j);
                TeamMemberEntity entity = teamMemberStarItem.getEntity();
                if (teamMemberEntity.id == entity.id) {
                    int oldPraiseCount = entity.praiseCount;
                    if (oldPraiseCount != teamMemberEntity.praiseCount) {
                        entity.praiseCount = teamMemberEntity.praiseCount;
                        teamMemberStarItem.updatePraise();
                    }
                    break;
                }
            }
        }
    }

    private void showRank() {
        View layout_livevideo_en_team_lead_star = LayoutInflater.from(mContext).inflate(R.layout.layout_livevideo_en_team_lead_star, rlTeampkLeadBottom, false);
        rlTeampkLeadBottom.addView(layout_livevideo_en_team_lead_star);
        final ArrayList<TeamMemberEntity> myTeamEntitys = enTeamPkRankEntity.getMemberEntities();
        int oldSize = myTeamEntitys.size();
        for (int i = 6; i < myTeamEntitys.size(); i++) {
            myTeamEntitys.remove(i);
            i--;
        }
        int newSize = myTeamEntitys.size();
        logger.d("showRank:oldSize=" + oldSize + ",newSize=" + newSize);
        addTeam();
    }

    private void addTeam() {
        LinearLayout llTeampkLeadStar = rlTeampkLeadBottom.findViewById(R.id.ll_livevideo_en_teampk_lead_star);
        HashMap<TeamMemberEntity, LottieAnimationView> map = new HashMap<>();
        final ArrayList<TeamMemberEntity> myTeamEntitys = enTeamPkRankEntity.getMemberEntities();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        for (int i = 0; i < myTeamEntitys.size(); i++) {
            TeamMemberStarItem teamMemberStarItem = new TeamMemberStarItem(mContext, type, pattern, map);
            teamMemberStarItems.add(teamMemberStarItem);
            teamMemberStarItem.setOnItemClick(new TeamMemberStarItem.OnItemClick() {
                @Override
                public void onItemClick(TeamMemberEntity entity) {
                    if (onStudyClick != null) {
                        onStudyClick.onStudyClick(myTeamEntitys);
                    }
                }
            });
            View convertView = inflater.inflate(teamMemberStarItem.getLayoutResId(), llTeampkLeadStar, false);
            teamMemberStarItem.initViews(convertView);
            teamMemberStarItem.updateViews(myTeamEntitys.get(i), i, null);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) convertView.getLayoutParams();
            if (i != 0) {
                if (pattern == 2) {
                    layoutParams.leftMargin = SizeUtils.Dp2Px(mContext, 20);
                } else {
                    layoutParams.leftMargin = SizeUtils.Dp2Px(mContext, 11);
                }
            }
            llTeampkLeadStar.addView(convertView, layoutParams);
        }
        setTeamWidth();
    }

    private void setTeamWidth() {
        if (pattern == 2) {
            return;
        }
        final LinearLayout llTeampkLeadStar = rlTeampkLeadBottom.findViewById(R.id.ll_livevideo_en_teampk_lead_star);
        final ViewTreeObserver viewTreeObserver = llTeampkLeadStar.getViewTreeObserver();
        viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (viewTreeObserver.isAlive()) {
                    viewTreeObserver.removeOnPreDrawListener(this);
                }
                llTeampkLeadStar.getViewTreeObserver().removeOnPreDrawListener(this);
                if (llTeampkLeadStar.getChildCount() > 4) {
                    int llWidth = llTeampkLeadStar.getWidth();
                    int myWidth = rlTeampkLeadBottom.getWidth();
                    logger.d("addTeam:llWidth=" + llWidth + "," + myWidth);
                    int leftMargin;
                    if (llWidth > myWidth) {
                        leftMargin = (llWidth - myWidth) / (llTeampkLeadStar.getChildCount() - 1);
                        if (leftMargin < 0) {
                            leftMargin = 0;
                        }
                    } else if (llWidth < myWidth) {
                        leftMargin = (myWidth - llWidth) / (llTeampkLeadStar.getChildCount() - 1);
                        if (leftMargin < 0) {
                            leftMargin = 0;
                        }
                        int maxleftMargin = SizeUtils.Dp2Px(mContext, 11);
                        if (leftMargin > maxleftMargin) {
                            leftMargin = maxleftMargin;
                        }
                    } else {
                        return false;
                    }
                    logger.d("addTeam:leftMargin=" + leftMargin);
                    for (int i = 1; i < llTeampkLeadStar.getChildCount(); i++) {
                        View childView = llTeampkLeadStar.getChildAt(i);
                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) childView.getLayoutParams();
                        if (leftMargin != layoutParams.leftMargin) {
                            layoutParams.leftMargin = leftMargin;
                            childView.setLayoutParams(layoutParams);
                        } else {
                            break;
                        }
                    }
                }
                return false;
            }
        });
    }

    private void setBg(final ImageView ivTeampkMine, final ImageView back) {
        final ViewTreeObserver viewTreeObserver = ivTeampkMine.getViewTreeObserver();
        viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (viewTreeObserver.isAlive()) {
                    viewTreeObserver.removeOnPreDrawListener(this);
                }
                ivTeampkMine.getViewTreeObserver().removeOnPreDrawListener(this);
//                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) ivTeampkMine.getLayoutParams();
                RelativeLayout.LayoutParams lp2 = (RelativeLayout.LayoutParams) back.getLayoutParams();
                lp2.leftMargin = ivTeampkMine.getLeft() - SizeUtils.Dp2Px(mContext, 42);
                lp2.topMargin = ivTeampkMine.getTop() - SizeUtils.Dp2Px(mContext, 32);
                back.setLayoutParams(lp2);
                return false;
            }
        });
    }

    @Override
    public void initListener() {
        super.initListener();
    }

    public interface OnClose {
        void close(BasePager basePager);
    }

    public interface OnStudyClick {
        void onStudyClick(ArrayList<TeamMemberEntity> entities);
    }
}
