package com.xueersi.parentsmeeting.modules.livevideo.enteampk.pager;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.config.EnTeamPkConfig;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.EnTeamPkRankEntity;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.TeamMemberEntity;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.item.TeamMemberStarItem;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.ui.adapter.AdapterItemInterface;
import com.xueersi.ui.adapter.CommonAdapter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 贡献之星
 */
public class TeamPkLeadPager extends LiveBasePager {
    /**
     * 贡献之星，课中
     */
    public static int TEAM_TYPE_1 = 1;
    /**
     * 贡献之星，课后
     */
    public static int TEAM_TYPE_2 = 2;
    private int type;
    private EnTeamPkRankEntity enTeamPkRankEntity;
    private RelativeLayout rlTeampkLeadBottom;
    private ProgressBar pgTeampkLead;
    private ImageView iv_livevideo_en_teampk_lead_prog;
    private ImageView ivTeampkMine;
    private ImageView ivTeampkOther;
    private TextView tvTeampkLeadFireAddLeft;
    private TextView tvTeampkLeadScoreLeft;
    private TextView ivTeampkLeadFireAddRight;
    private TextView tvTeampkLeadScoreRight;
    private int pattern;
    private Handler handler = new Handler(Looper.getMainLooper());

    public TeamPkLeadPager(Context context, EnTeamPkRankEntity enTeamPkRankEntity, int type, int pattern) {
        super(context, false);
        this.type = type;
        this.pattern = pattern;
        mView = initView();
        this.enTeamPkRankEntity = enTeamPkRankEntity;
        initData();
        initListener();
    }

    @Override
    public View initView() {
        View view;
        if (type == TEAM_TYPE_2) {
            view = LayoutInflater.from(mContext).inflate(R.layout.page_livevideo_en_team_result, null);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.page_livevideo_en_team_lead, null);
        }
        if (pattern == 2) {
            view.setBackgroundResource(R.drawable.bg_livevideo_en_team_bg_16_9);
        } else {
            view.setBackgroundResource(R.drawable.bg_livevideo_en_team_bg_4_3);
        }
        rlTeampkLeadBottom = view.findViewById(R.id.rl_livevideo_en_teampk_lead_bottom);
        pgTeampkLead = view.findViewById(R.id.pg_livevideo_en_teampk_lead);
        iv_livevideo_en_teampk_lead_prog = view.findViewById(R.id.iv_livevideo_en_teampk_lead_prog);
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
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, width);
//                lp.leftMargin = SizeUtils.Dp2Px(mContext, 275);
//                lp.topMargin = SizeUtils.Dp2Px(mContext, 17);
                group.addView(imageView, 0, lp);
                setBg(ivTeampkOther, imageView);
            }
        } else {
            int lastM = enTeamPkRankEntity.getMyTeamTotal() - enTeamPkRankEntity.getMyTeamCurrent();
            int lastO = enTeamPkRankEntity.getOpTeamTotal() - enTeamPkRankEntity.getOpTeamCurrent();
            int lastWin = lastM - lastO;
            ImageView iv_livevideo_en_teampk_lead_left = mView.findViewById(R.id.iv_livevideo_en_teampk_lead_left);
            ImageView iv_livevideo_en_teampk_lead_right = mView.findViewById(R.id.iv_livevideo_en_teampk_lead_right);
            String s;
            if (win == 0) {
                s = "0";
                iv_livevideo_en_teampk_lead_left.setImageResource(R.drawable.bg_livevideo_en_teampk_lead_mid);
                iv_livevideo_en_teampk_lead_right.setImageResource(R.drawable.bg_livevideo_en_teampk_lead_mid);
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
                        XESToastUtils.showToast(mContext, "恭喜反超对手");
                    }
                } else {
                    if (lastWin < 0) {
                        s = "3";
                        iv_livevideo_en_teampk_lead_left.setImageResource(R.drawable.bg_livevideo_en_teampk_lead_win);
                        iv_livevideo_en_teampk_lead_right.setImageResource(R.drawable.bg_livevideo_en_teampk_lead_lost);
                    } else {
                        s = "4";
                        iv_livevideo_en_teampk_lead_left.setImageResource(R.drawable.bg_livevideo_en_teampk_lead_exceed);
                        iv_livevideo_en_teampk_lead_right.setImageResource(R.drawable.bg_livevideo_en_teampk_lead_wait);
                    }
                }
            }
            mLogtf.d("initData:win=" + win + ",last=" + lastWin + ",s=" + s);
        }
        int[] res = EnTeamPkConfig.TEAM_RES;
        ivTeampkMine.setImageResource(res[enTeamPkRankEntity.getMyTeam()]);
        int progress = 50;
        float fprog = 0.0f;
        int total = enTeamPkRankEntity.getMyTeamTotal() + enTeamPkRankEntity.getOpTeamTotal();
        ivTeampkOther.setImageResource(res[enTeamPkRankEntity.getBpkTeamId()]);
        tvTeampkLeadFireAddLeft.setText("" + enTeamPkRankEntity.getMyTeamCurrent());
        tvTeampkLeadScoreLeft.setText("" + enTeamPkRankEntity.getMyTeamTotal());
        ivTeampkLeadFireAddRight.setText("" + enTeamPkRankEntity.getOpTeamCurrent());
        tvTeampkLeadScoreRight.setText("" + enTeamPkRankEntity.getOpTeamTotal());
        if (total != 0) {
            fprog = (float) (enTeamPkRankEntity.getMyTeamTotal()) / (float) (total);
            progress = (int) ((float) (enTeamPkRankEntity.getMyTeamTotal() * 100) / (float) (total));
        }
        if (type == TEAM_TYPE_1 && win >= 0) {
            final ViewGroup group = (ViewGroup) mView;
            final View view = LayoutInflater.from(mContext).inflate(R.layout.layout_livevideo_en_team_lead_win, group, false);
            group.addView(view);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    group.removeView(view);
                }
            }, 100);
        }
        showRank();
        pgTeampkLead.setProgress(progress);
        final float finalFprog = fprog;
        mLogtf.d("initData:type=" + type + ",fprog=" + fprog);
        pgTeampkLead.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                pgTeampkLead.getViewTreeObserver().removeOnPreDrawListener(this);
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) iv_livevideo_en_teampk_lead_prog.getLayoutParams();
                lp.leftMargin = (int) (pgTeampkLead.getLeft() + pgTeampkLead.getWidth() * finalFprog) - iv_livevideo_en_teampk_lead_prog.getWidth() / 2;
                iv_livevideo_en_teampk_lead_prog.setLayoutParams(lp);
                iv_livevideo_en_teampk_lead_prog.setVisibility(View.VISIBLE);
                return false;
            }
        });
    }

    private void showRank() {
        View layout_livevideo_en_team_lead_star = LayoutInflater.from(mContext).inflate(R.layout.layout_livevideo_en_team_lead_star, rlTeampkLeadBottom, false);
        rlTeampkLeadBottom.addView(layout_livevideo_en_team_lead_star);
        GridView gv_livevideo_en_teampk_lead_star = layout_livevideo_en_team_lead_star.findViewById(R.id.gv_livevideo_en_teampk_lead_star);
        ArrayList<TeamMemberEntity> myTeamEntitys = enTeamPkRankEntity.getMemberEntities();
        int oldSize = myTeamEntitys.size();
        for (int i = 4; i < myTeamEntitys.size(); i++) {
            myTeamEntitys.remove(i);
            i--;
        }
        int newSize = myTeamEntitys.size();
        logger.d("showRank:oldSize=" + oldSize + ",newSize=" + newSize);
        CommonAdapter<TeamMemberEntity> myTeamAdapter = new CommonAdapter<TeamMemberEntity>(myTeamEntitys) {
            HashMap<TeamMemberEntity, LottieAnimationView> map = new HashMap<>();

            @Override
            public AdapterItemInterface<TeamMemberEntity> getItemView(Object type) {
                return new TeamMemberStarItem(mContext, map);
            }
        };
        gv_livevideo_en_teampk_lead_star.setAdapter(myTeamAdapter);
    }

    private void setBg(final ImageView ivTeampkMine, final ImageView back) {
        ivTeampkMine.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
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
}
