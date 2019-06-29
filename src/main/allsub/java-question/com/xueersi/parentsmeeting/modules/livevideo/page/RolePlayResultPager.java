package com.xueersi.parentsmeeting.modules.livevideo.page;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.common.business.UserBll;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.common.util.FontCache;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.RolePlayConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RolePlayerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.SpeechResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.SpeechResultMember;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.SpeechResultPager;
import com.xueersi.ui.widget.CircleImageView;

import java.util.ArrayList;
import java.util.List;

public class RolePlayResultPager extends LiveBasePager {
    ViewGroup group;
    RolePlayerEntity mEntity;
    /**
     * 结果总分
     */
    private TextView tvTotalScore;
    /**
     * 获得的点赞数
     */
    private TextView tvDzCount;
    /**
     * 流畅性
     */
    private TextView tvFluency;
    /**
     * 金币数
     */
    private TextView tvGoldCount;
    /**
     * 准确性
     */
    private TextView tvAccuracy;
    /**
     * 结果页自己的头像
     */
    private CircleImageView civResultHeadImg;
    /**
     * 总评
     */
    private TextView tvResultMsgTip;

    /**
     * 排名1
     */
    private RelativeLayout rlResultRole1;
    /**
     * 排名1头像
     */
    private CircleImageView civResultRoleHeadImg1;
    /**
     * 排名1分数
     */
    private TextView tvResultRoleScore1;
    /**
     * 排名1名字
     */
    private TextView tvResultRoleName1;

    /**
     * 排名2
     */
    private RelativeLayout rlResultRole2;
    /**
     * 排名2头像
     */
    private CircleImageView civResultRoleHeadImg2;
    /**
     * 排名2分数
     */
    private TextView tvResultRoleScore2;
    /**
     * 排名2名字
     */
    private TextView tvResultRoleName2;

    /**
     * 排名3
     */
    private RelativeLayout rlResultRole3;
    /**
     * 排名3头像
     */
    private CircleImageView civResultRoleHeadImg3;
    /**
     * 排名3分数
     */
    private TextView tvResultRoleScore3;
    /**
     * 排名3名字
     */
    private TextView tvResultRoleName3;
    private ImageView ivRoleplayerResultStar;//显示成绩结果星星旗帜

    public RolePlayResultPager(Context context, RolePlayerEntity rolePlayerEntity, ViewGroup group) {
        super(context, false);
        this.mEntity = rolePlayerEntity;
        this.group = group;
        mView = initView();
        initData();
    }

    @Override
    public View initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.pager_roleplayer_result, group, false);
        tvTotalScore = view.findViewById(R.id.tv_livevideo_roleplayer_result_totalscore);
        tvDzCount = view.findViewById(R.id.tv_livevideo_roleplayer_result_dz_count);
        tvFluency = view.findViewById(R.id.tv_livevideo_roleplayer_result_fluency);
        tvGoldCount = view.findViewById(R.id.tv_livevideo_roleplayer_result_gold_count);
        tvAccuracy = view.findViewById(R.id.tv_livevideo_roleplayer_result_accuracy);
        civResultHeadImg = view.findViewById(R.id.civ_livevideo_roleplayer_result_headimg);
        tvResultMsgTip = view.findViewById(R.id.tv_livevideo_roleplayer_result_msgtip);

        ivRoleplayerResultStar = view.findViewById(R.id.iv_live_roleplayer_result_star);

        rlResultRole1 = view.findViewById(R.id.rl_livevideo_roleplayer_result_role_1);
        civResultRoleHeadImg1 = view.findViewById(R.id.civ_livevideo_roleplayer_result_role_head_1);
        tvResultRoleScore1 = view.findViewById(R.id.tv_livevideo_roleplayer_result_role_score_1);
        tvResultRoleName1 = view.findViewById(R.id.tv_livevideo_roleplayer_result_role_nickname_1);

        rlResultRole2 = view.findViewById(R.id.rl_livevideo_roleplayer_result_role_2);
        civResultRoleHeadImg2 = view.findViewById(R.id.civ_livevideo_roleplayer_result_role_head_2);
        tvResultRoleScore2 = view.findViewById(R.id.tv_livevideo_roleplayer_result_role_score_2);
        tvResultRoleName2 = view.findViewById(R.id.tv_livevideo_roleplayer_result_role_nickname_2);

        rlResultRole3 = view.findViewById(R.id.rl_livevideo_roleplayer_result_role_3);
        civResultRoleHeadImg3 = view.findViewById(R.id.civ_livevideo_roleplayer_result_role_head_3);
        tvResultRoleScore3 = view.findViewById(R.id.tv_livevideo_roleplayer_result_role_score_3);
        tvResultRoleName3 = view.findViewById(R.id.tv_livevideo_roleplayer_result_role_nickname_3);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        ///获取当前应该走的离线模型
        final int curSubModEva = ShareDataManager.getInstance().getInt(RolePlayConfig
                .KEY_FOR_WHICH_SUBJECT_MODEL_EVA, RolePlayConfig.VALUE_FOR_ENGLISH_MODEL_EVA, ShareDataManager
                .SHAREDATA_NOT_CLEAR);
        List<RolePlayerEntity.RolePlayerHead> lstHead = mEntity.getResultRoleList();
        RolePlayerEntity.RolePlayerHead head = mEntity.getSelfRoleHead();
        if (head != null) {
            Typeface tFace = getTypeface(mContext);
            if (tFace != null) {
                tvResultMsgTip.setTypeface(getTypeface(mContext));
                tvTotalScore.setTypeface(getTypeface(mContext));
            }
            if (head.getSpeechScore() >= 90) {
                tvResultMsgTip.setText((curSubModEva == RolePlayConfig.VALUE_FOR_CHINESE_MODEL_EVA) ? "天才" :
                        "Fantastic");

            } else if (head.getSpeechScore() >= 60) {
                tvResultMsgTip.setText((curSubModEva == RolePlayConfig.VALUE_FOR_CHINESE_MODEL_EVA) ? "不错哦" :
                        "Welldone");
            } else {
                tvResultMsgTip.setText((curSubModEva == RolePlayConfig.VALUE_FOR_CHINESE_MODEL_EVA) ? "加油哦" :
                        "Fighting");
            }

            if (head.getSpeechScore() >= 0 && head.getSpeechScore() < 40) {
                ivRoleplayerResultStar.setImageResource(R.drawable.livevideo_roleplay_alertview_pic_xingxing1);
                head.setResultStar(1);
            }
            if (head.getSpeechScore() >= 40 && head.getSpeechScore() < 60) {
                ivRoleplayerResultStar.setImageResource(R.drawable.livevideo_roleplay_alertview_pic_xingxing2);
                head.setResultStar(2);
            }
            if (head.getSpeechScore() >= 60 && head.getSpeechScore() < 75) {
                ivRoleplayerResultStar.setImageResource(R.drawable.livevideo_roleplay_alertview_pic_xingxing3);
                head.setResultStar(3);
            }
            if (head.getSpeechScore() >= 75 && head.getSpeechScore() < 90) {
                ivRoleplayerResultStar.setImageResource(R.drawable.livevideo_roleplay_alertview_pic_xingxing4);
                head.setResultStar(4);
            }
            if (head.getSpeechScore() >= 90) {
                ivRoleplayerResultStar.setImageResource(R.drawable.livevideo_roleplay_alertview_pic_xingxing5);
                head.setResultStar(5);
            }


            tvTotalScore.setText(head.getSpeechScore() + "分");
            tvDzCount.setText(mEntity.getPullDZCount() + "");
            tvFluency.setText("流畅性:" + head.getFluency());
            tvGoldCount.setText(mEntity.getGoldCount() + "");
            tvAccuracy.setText("准确性:" + head.getAccuracy());

            /** catch exception:java.lang.IllegalArgumentException: You cannot start a load for a destroyed activity;
             *
             *  at com.bumptech.glide.manager.RequestManagerRetriever.assertNotDestroyed(RequestManagerRetriever
             *  .java:284) at com.bumptech.glide.manager.RequestManagerRetriever.get(RequestManagerRetriever.java:145)
             *  at com.bumptech.glide.manager.RequestManagerRetriever.get(RequestManagerRetriever.java:111)
             *  at com.bumptech.glide.Glide.with(Glide.java:554)
             *
             * 通过查看源码发现，异常的原因在于，当前activity已经销毁，所以无法加载角色头像
             *
             * */
            if (mContext instanceof Activity) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    if (!((Activity) mContext).isDestroyed()) {
                        ImageLoader.with(mContext).load(UserBll.getInstance().getMyUserInfoEntity().getHeadImg())
                                .into(civResultHeadImg);
                    }
                }
            }

            if (lstHead.size() >= 1) {
                RolePlayerEntity.RolePlayerHead head1 = lstHead.get(0);
                tvResultRoleScore1.setText(head1.getSpeechScore() + "分");
                tvResultRoleName1.setText(head1.getNickName());
                ImageLoader.with(ContextManager.getApplication()).load(head1.getHeadImg()).into(civResultRoleHeadImg1);
                civResultRoleHeadImg1.setBorderWidth(SizeUtils.Dp2Px(mContext, 2));
                if (head1.isSelfRole()) {
                    civResultRoleHeadImg1.setBorderColor(Color.parseColor("#FAD2D1"));
                    tvResultRoleScore1.setTextColor(Color.parseColor("#333333"));
                    tvResultRoleName1.setTextColor(Color.parseColor("#333333"));
                } else {
                    civResultRoleHeadImg1.setBorderColor(Color.parseColor("#E0E0E0"));
                    tvResultRoleScore1.setTextColor(Color.parseColor("#666666"));
                    tvResultRoleName1.setTextColor(Color.parseColor("#666666"));
                }
            } else {
                rlResultRole1.setVisibility(View.INVISIBLE);
            }

            if (lstHead.size() >= 2) {
                RolePlayerEntity.RolePlayerHead head2 = lstHead.get(1);
                tvResultRoleScore2.setText(head2.getSpeechScore() + "分");
                tvResultRoleName2.setText(head2.getNickName());
                ImageLoader.with(ContextManager.getApplication()).load(head2.getHeadImg()).into(civResultRoleHeadImg2);
                civResultRoleHeadImg2.setBorderWidth(SizeUtils.Dp2Px(mContext, 2));
                if (head2.isSelfRole()) {
                    civResultRoleHeadImg2.setBorderColor(Color.parseColor("#FAD2D1"));
                    tvResultRoleScore2.setTextColor(Color.parseColor("#333333"));
                    tvResultRoleName2.setTextColor(Color.parseColor("#333333"));
                } else {
                    civResultRoleHeadImg2.setBorderColor(Color.parseColor("#E0E0E0"));
                    tvResultRoleScore2.setTextColor(Color.parseColor("#666666"));
                    tvResultRoleName2.setTextColor(Color.parseColor("#666666"));
                }
            } else {
                rlResultRole2.setVisibility(View.INVISIBLE);
            }

            if (lstHead.size() >= 3) {
                RolePlayerEntity.RolePlayerHead head3 = lstHead.get(2);
                tvResultRoleScore3.setText(head3.getSpeechScore() + "分");
                tvResultRoleName3.setText(head3.getNickName());
                ImageLoader.with(ContextManager.getApplication()).load(head3.getHeadImg()).into(civResultRoleHeadImg3);
                civResultRoleHeadImg3.setBorderWidth(SizeUtils.Dp2Px(mContext, 2));
                if (head3.isSelfRole()) {
                    civResultRoleHeadImg3.setBorderColor(Color.parseColor("#FAD2D1"));
                    tvResultRoleScore3.setTextColor(Color.parseColor("#333333"));
                    tvResultRoleName3.setTextColor(Color.parseColor("#333333"));
                } else {
                    civResultRoleHeadImg3.setBorderColor(Color.parseColor("#E0E0E0"));
                    tvResultRoleScore3.setTextColor(Color.parseColor("#666666"));
                    tvResultRoleName3.setTextColor(Color.parseColor("#666666"));
                }
            } else {
                rlResultRole3.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * 获取字体
     *
     * @param context
     * @return
     */
    public Typeface getTypeface(Context context) {
        Typeface tf = null;
        try {
            tf = FontCache.getTypeface(context, "fangzhengcuyuan.ttf");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return tf;
    }
}
