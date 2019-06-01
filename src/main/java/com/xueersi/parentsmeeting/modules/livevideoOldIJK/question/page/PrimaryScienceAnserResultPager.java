package com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.page;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.PrimaryScienceAnswerResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.widget.FastScrollableRecyclerView;

import java.util.List;

import static android.view.View.GONE;

/**
 * Created by ZhangYuansun on 2019/3/6
 * <p>
 * 小学理科 互动题结果页
 */
public class PrimaryScienceAnserResultPager extends LiveBasePager {
    PrimaryScienceAnswerResultEntity mEnytity;
    int isGame;
    AnswerResultAdapter mAdapter;
    static final int[] rightImageResource = new int[]{
            R.drawable.live_interact_primary_wrong,
            R.drawable.live_interact_primary_right,
            R.drawable.live_interact_primary_middle
    };
    static final String LOTTIE_RES_ASSETS_ROOTDIR = "primary_science_answer_result/";

    static final String[] jsonAssetsFolder = new String[]{
            LOTTIE_RES_ASSETS_ROOTDIR + "interact-active-wrong.json",
            LOTTIE_RES_ASSETS_ROOTDIR + "interact-active-right.json",
            LOTTIE_RES_ASSETS_ROOTDIR + "interact-active-middle.json"
    };

    static final String[] imageAssetsFolder = new String[]{
            LOTTIE_RES_ASSETS_ROOTDIR + "animation/interact-active/wrong",
            LOTTIE_RES_ASSETS_ROOTDIR + "animation/interact-active/right",
            LOTTIE_RES_ASSETS_ROOTDIR + "animation/interact-active/middle"
    };

    static final String[] jsonAssetsFolderGame = new String[]{
            LOTTIE_RES_ASSETS_ROOTDIR + "game-wrong.json",
            LOTTIE_RES_ASSETS_ROOTDIR + "game-right.json",
    };

    static final String[] imageAssetsFolderGame = new String[]{
            LOTTIE_RES_ASSETS_ROOTDIR + "animation/game/wrong",
            LOTTIE_RES_ASSETS_ROOTDIR + "animation/game/right",
    };

    OnNativeResultPagerClose onNativeResultPagerClose;

    public PrimaryScienceAnserResultPager(Context context, PrimaryScienceAnswerResultEntity enytity, int isGame,
                                          OnNativeResultPagerClose onNativeResultPagerClose) {
        super(context);
        this.mEnytity = enytity;
        this.isGame = isGame;
        this.onNativeResultPagerClose = onNativeResultPagerClose;
        initData();
        initListener();
    }

    RelativeLayout rlContent;
    LottieAnimationView lavActiveRight;
    FastScrollableRecyclerView mRecycleView;
    TextView tvGold;
    TextView tvGoldGame;
    ImageView ivClose;

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.page_livevideo_primaryscience_anwserresult, null);
        rlContent = mView.findViewById(R.id.rl_livevideo_primaryscience_anwserrsult_content);
        lavActiveRight = mView.findViewById(R.id.lav_livevideo_primaryscience_anwserrsult_active_right);
        mRecycleView = mView.findViewById(R.id.frv_livevideo_primaryscience_anwserrsult);
        tvGold = mView.findViewById(R.id.tv_livevideo_primaryscience_anwerresult_gold);
        tvGoldGame = mView.findViewById(R.id.tv_livevideo_primaryscience_anwerresult_gold_game);
        ivClose = mView.findViewById(R.id.iv_livevideo_primaryscience_anwerresult_close);
        return mView;
    }

    @Override
    public void initData() {
        if (isGame == 1) {
            rlContent.setVisibility(GONE);
            ivClose.setVisibility(GONE);
            tvGoldGame.setText("+" + mEnytity.getGold());
            startGameLottieAnimation();
            mView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    onNativeResultPagerClose.onClose();
                }
            }, 3000);

        } else {
            mAdapter = new AnswerResultAdapter(mEnytity.getAnswerList());
            mRecycleView.setLayoutManager(new GridLayoutManager(mContext, 1, LinearLayoutManager.VERTICAL, false));
            mRecycleView.setAdapter(mAdapter);
            tvGold.setText("+" + mEnytity.getGold());
            startLottieAnimation();
        }
    }

    @Override
    public void initListener() {
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNativeResultPagerClose.onClose();
            }
        });
    }

    class AnswerResultAdapter extends RecyclerView.Adapter {
        public AnswerResultAdapter(List<PrimaryScienceAnswerResultEntity.Answer> answerList) {
            this.answerList = answerList;
        }

        List<PrimaryScienceAnswerResultEntity.Answer> answerList;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new AnswerViewHolder(View.inflate(parent.getContext(), R.layout
                    .item_livevideo_primaryscience_answerresult_answerlist, null));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            PrimaryScienceAnswerResultEntity.Answer data = answerList.get(position);
            ((AnswerViewHolder) holder).bindData(data, position);
        }

        @Override
        public int getItemCount() {
            return answerList.size();
        }
    }

    class AnswerViewHolder extends RecyclerView.ViewHolder {
        View vDashLine;
        TextView tvAnswerNumber;
        TextView tvRightAnswer;
        TextView tvMyAnswer;
        ImageView ivRight;

        AnswerViewHolder(View itemView) {
            super(itemView);
            vDashLine = itemView.findViewById(R.id.tv_livevideo_primaryscience_answerresult_dash_line);
            tvAnswerNumber = itemView.findViewById(R.id.tv_livevideo_primaryscience_answerresult_answer_number);
            tvRightAnswer = itemView.findViewById(R.id.tv_livevideo_primaryscience_answerresult_right_answer);
            tvMyAnswer = itemView.findViewById(R.id.tv_livevideo_primaryscience_answerresult_my_answer);
            ivRight = itemView.findViewById(R.id.iv_livevideo_primaryscience_answerresult_answer_result);
        }

        public void bindData(PrimaryScienceAnswerResultEntity.Answer data, int position) {
            if (position == 0) {
                vDashLine.setVisibility(GONE);
            } else {
                RelativeLayout.LayoutParams dashLineLayoutParams = (RelativeLayout.LayoutParams) vDashLine
                        .getLayoutParams();
                if (data.getAmswerNumber() == 0) {

                    dashLineLayoutParams.width = SizeUtils.Dp2Px(mContext, 264);
                } else {
                    dashLineLayoutParams.width = SizeUtils.Dp2Px(mContext, 361);
                }
                vDashLine.setLayoutParams(dashLineLayoutParams);
            }

            if (data.getAmswerNumber() == 0) {
                tvAnswerNumber.setText("");
            } else {
                tvAnswerNumber.setText(data.getAmswerNumber() + "");
            }
            tvRightAnswer.setText(data.getRightAnswer());
            tvMyAnswer.setText(data.getMyAnswer());
            if (data.getRight() < 0 || data.getRight() >= rightImageResource.length) {
                return;
            }
            ivRight.setImageResource(rightImageResource[data.getRight()]);
        }
    }

    private void startLottieAnimation() {
        if (mEnytity.getType() < 0 || mEnytity.getType() >= jsonAssetsFolder.length) {
            return;
        }
        lavActiveRight.setAnimation(jsonAssetsFolder[mEnytity.getType()]);
        lavActiveRight.setImageAssetsFolder(imageAssetsFolder[mEnytity.getType()]);
        lavActiveRight.setVisibility(View.VISIBLE);
        lavActiveRight.useHardwareAcceleration();
        lavActiveRight.loop(true);
        lavActiveRight.playAnimation();
    }

    private void startGameLottieAnimation() {
        String animScript = mEnytity.getType() == 1 ? jsonAssetsFolderGame[1] : jsonAssetsFolderGame[0];
        String assetFolder = mEnytity.getType() == 1 ? imageAssetsFolderGame[1] : imageAssetsFolderGame[0];
        lavActiveRight.setAnimation(animScript);
        lavActiveRight.setImageAssetsFolder(assetFolder);
        lavActiveRight.setVisibility(View.VISIBLE);
        lavActiveRight.useHardwareAcceleration();
        lavActiveRight.loop(true);
        lavActiveRight.playAnimation();
    }

    interface OnNativeResultPagerClose {
        void onClose();
    }
}
