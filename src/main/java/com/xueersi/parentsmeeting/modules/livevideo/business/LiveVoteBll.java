package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.parentsmeeting.base.BaseApplication;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.dialog.VoteDialog;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.xesalib.utils.log.Loger;
import com.xueersi.xesalib.utils.uikit.ScreenUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by linyuqiang on 2017/12/27.
 * 投票
 */
public class LiveVoteBll implements LiveVoteAction {
    String TAG = "LiveVoteBll";
    Context context;
    RelativeLayout bottomContent;
    RelativeLayout contentView;
    LiveTopic.VoteEntity voteEntity;
    LiveBll liveBll;
    VoteDialog voteDialog;
    HashMap<LiveTopic.VoteEntity, Integer> idAndAnswer = new HashMap<>();
    int answer;

    public LiveVoteBll(Context context) {
        this.context = context;
    }

    public void initView(final RelativeLayout bottomContent) {
        this.bottomContent = bottomContent;
    }

    public void setLiveBll(LiveBll liveBll) {
        this.liveBll = liveBll;
    }

    private void showResult(LiveTopic.VoteEntity voteEntity) {
        if (contentView != null) {
            bottomContent.removeView(contentView);
        }
        if (voteDialog != null) {
            voteDialog.cancelDialog();
            voteDialog = null;
        }
        final View view1 = LayoutInflater.from(context).inflate(R.layout.layout_livevideo_vote_result, bottomContent, false);
        contentView = new RelativeLayout(context);
        contentView.addView(view1);
        bottomContent.addView(contentView);
        LinearLayout linearLayout = (LinearLayout) view1.findViewById(R.id.ll_livevideo_vote_result_content);
        int choiceNum = voteEntity.getChoiceNum();
        ArrayList<LiveTopic.VoteResult> voteResults = voteEntity.getVoteResults();
        TextView tv_livevideo_vote_result_mine = (TextView) view1.findViewById(R.id.tv_livevideo_vote_result_mine);
        for (int i = 0; i < choiceNum; i++) {
            View convertView = LayoutInflater.from(context).inflate(R.layout.item_livevideo_vote_result_select, linearLayout, false);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) convertView.getLayoutParams();
            if (choiceNum > 4) {
//                lp.weight = 1;
                if (i != choiceNum - 1) {
                    lp.rightMargin = (int) (23 * ScreenUtils.getScreenDensity());
                }
            } else {
                if (i != choiceNum - 1) {
                    lp.rightMargin = (int) (63 * ScreenUtils.getScreenDensity());
                }
            }
            linearLayout.addView(convertView, lp);
            TextView tv_livevideo_vote_result_item = (TextView) convertView.findViewById(R.id.tv_livevideo_vote_result_item);
            if (voteEntity.getChoiceType() == 1) {
                char c = (char) ('A' + i);
                tv_livevideo_vote_result_item.setText("" + c);
            } else {
                if (i == 0) {
                    tv_livevideo_vote_result_item.setText("是");
                } else {
                    tv_livevideo_vote_result_item.setText("否");
                }
            }
            LiveTopic.VoteResult result = voteResults.get(i);

            float rado;
            if (voteEntity.getTotal() == 0) {
                rado = 0;
            } else {
                rado = result.getPople() * 100 / voteEntity.getTotal();
            }
            TextView tv_livevideo_vote_result_count = (TextView) convertView.findViewById(R.id.tv_livevideo_vote_result_count);
            tv_livevideo_vote_result_count.setText(result.getPople() + "人" + (int) rado + "%");
//            Drawable drawable = context.getResources().getDrawable(R.drawable.shape_live_vote_prog_max);
//            pb_livevideo_vote_result_item.setProgressDrawable(drawable);
            final ProgressBar pb_livevideo_vote_result_item = (ProgressBar) convertView.findViewById(R.id.pb_livevideo_vote_result_item);
            pb_livevideo_vote_result_item.setMax(voteEntity.getTotal());
//            pb_livevideo_vote_result_item.setProgress(result.getPople());
            int newProgress = result.getPople();
            final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, newProgress);
            final float finalNewProgress = newProgress;
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float fraction = animation.getAnimatedFraction();
                    float oldProgress = (finalNewProgress) * fraction;
                    pb_livevideo_vote_result_item.setProgress((int) oldProgress);
                }
            });
            valueAnimator.setDuration(1000);
            valueAnimator.start();
        }
        if (answer > 0) {
            View convertView = linearLayout.getChildAt(answer - 1);
            TextView tv_livevideo_vote_result_item = (TextView) convertView.findViewById(R.id.tv_livevideo_vote_result_item);
            String myAnwer = tv_livevideo_vote_result_item.getText() + "";
            tv_livevideo_vote_result_mine.setText("我的选择: " + myAnwer);
        } else {
            tv_livevideo_vote_result_mine.setText("我没有选择");
        }
        view1.findViewById(R.id.iv_livevideo_vote_result_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomContent.removeView(contentView);
                contentView = null;
            }
        });
    }

    @Override
    public void voteJoin(final LiveTopic.VoteEntity voteEntity, int answer) {
        Loger.d(TAG, "voteJoin:choiceId=" + voteEntity + ",answer=" + answer);
        this.answer = answer;
        idAndAnswer.put(voteEntity, answer);
        if (0 == answer) {
            showChoice(voteEntity);
        }
    }

    @Override
    public void voteStart(final LiveTopic.VoteEntity voteEntity) {
        Loger.d(TAG, "voteStart:voteEntity=" + voteEntity);
        this.voteEntity = voteEntity;
        this.answer = 0;
        showChoice(voteEntity);
    }

    private void showChoice(final LiveTopic.VoteEntity voteEntity) {
        bottomContent.post(new Runnable() {
            @Override
            public void run() {
                if (contentView != null) {
                    bottomContent.removeView(contentView);
                }
                if (voteDialog != null) {
                    voteDialog.cancelDialog();
                    voteDialog = null;
                }
                final View view = LayoutInflater.from(context).inflate(R.layout.page_livevodeo_vote_select, bottomContent, false);
                contentView = new RelativeLayout(context);
                contentView.addView(view);
                bottomContent.addView(contentView);
                LinearLayout il_livevideo_vote_choice = (LinearLayout) view.findViewById(R.id.il_livevideo_vote_choice);
                int choiceNum = voteEntity.getChoiceNum();
                for (int i = 0; i < choiceNum; i++) {
                    final int answer = i + 1;
                    View convertView = LayoutInflater.from(context).inflate(R.layout.item_livevideo_vote_select, il_livevideo_vote_choice, false);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    if (i != choiceNum - 1) {
                        lp.rightMargin = (int) (25 * ScreenUtils.getScreenDensity());
                    }
                    il_livevideo_vote_choice.addView(convertView, lp);
                    Button btn_livevideo_vote_item = (Button) convertView.findViewById(R.id.btn_livevideo_vote_item);
                    if (voteEntity.getChoiceType() == 1) {
                        char c = (char) ('A' + i);
                        btn_livevideo_vote_item.setText("" + c);
                    } else {
                        if (i == 0) {
                            btn_livevideo_vote_item.setText("是");
                        } else {
                            btn_livevideo_vote_item.setText("否");
                        }
                    }
                    btn_livevideo_vote_item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            BaseApplication baseApplication = (BaseApplication) BaseApplication.getContext();
                            voteDialog = new VoteDialog(context, baseApplication, false);
                            voteDialog.showDialog();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (voteDialog != null) {
                                        voteDialog.cancelDialog();
                                        voteDialog = null;
                                    }
                                }
                            }, 20000);
                            bottomContent.removeView(contentView);
                            contentView = null;
                            LiveVoteBll.this.answer = answer;
                            idAndAnswer.put(voteEntity, answer);
                            liveBll.sendVote(answer);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void voteStop(final LiveTopic.VoteEntity voteEntity) {
        Loger.d(TAG, "voteStop:voteEntity=" + voteEntity);
        this.voteEntity = null;
        bottomContent.post(new Runnable() {
            @Override
            public void run() {
                showResult(voteEntity);
            }
        });
    }

    @Override
    public void onCancle() {
        this.voteEntity = null;
    }
}