package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.parentsmeeting.base.BaseApplication;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.dialog.VoteWaitDialog;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.event.NativeVoteRusltulCloseEvent;
import com.xueersi.parentsmeeting.modules.livevideo.widget.VerticalImageSpan;
import com.xueersi.xesalib.utils.log.Loger;
import com.xueersi.xesalib.utils.uikit.ScreenUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by linyuqiang on 2017/12/27.
 * 投票
 */
public class LiveVoteBll implements LiveVoteAction {
    String TAG = "LiveVoteBll";
    String eventId = LiveVideoConfig.LIVE_VOTE;
    Context context;
    RelativeLayout bottomContent;
    RelativeLayout contentView;
    LiveTopic.VoteEntity voteEntity;
    LiveBll liveBll;
    VoteWaitDialog voteWaitDialog;
    HashMap<LiveTopic.VoteEntity, Integer> idAndAnswer = new HashMap<>();
    int answer;
    // 选项资源的图片
    public int[] pschoices = {R.drawable.livevideo_votechoice_psa, R.drawable.livevideo_votechoice_psb, R.drawable.livevideo_votechoice_psc,R.drawable.livevideo_votechoice_psd,R.drawable.livevideo_votechoice_pse,R.drawable.livevideo_votechoice_psf};
    private Button mBtn_livevideo_vote_item;
    private LinearLayout mIl_livevideo_vote_ps_choice;

    public LiveVoteBll(Context context) {
        this.context = context;
    }

    public void initView(final RelativeLayout bottomContent) {
        this.bottomContent = bottomContent;
    }

    public void setLiveBll(LiveBll liveBll) {
        this.liveBll = liveBll;
    }

    private void showResult(final LiveTopic.VoteEntity voteEntity) {
        if (contentView != null) {
            bottomContent.removeView(contentView);
        }
        if (voteWaitDialog != null) {
            voteWaitDialog.cancelDialog();
            voteWaitDialog = null;
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
//            tv_livevideo_vote_result_count.setText(Math.round(rado) + "% " + result.getPople() + "人");
            tv_livevideo_vote_result_count.setText(Math.round(rado) + "% ");
//            Drawable drawable = context.getResources().getDrawable(R.drawable.shape_live_vote_prog_max);
//            pb_livevideo_vote_result_item.setProgressDrawable(drawable);
            final ProgressBar pb_livevideo_vote_result_item = (ProgressBar) convertView.findViewById(R.id.pb_livevideo_vote_result_item);
            pb_livevideo_vote_result_item.setMax(voteEntity.getTotal());
            int newProgress = result.getPople();
            if (newProgress > 1) {
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
            } else {
                pb_livevideo_vote_result_item.setProgress(newProgress);
            }
        }
        if (answer > 0) {
            View convertView = linearLayout.getChildAt(answer - 1);
            TextView tv_livevideo_vote_result_item = (TextView) convertView.findViewById(R.id.tv_livevideo_vote_result_item);
            String myAnwer = tv_livevideo_vote_result_item.getText() + "";
            tv_livevideo_vote_result_mine.setText("我的选择: " + myAnwer);
        } else {
            tv_livevideo_vote_result_mine.setText("你错过了本次选择");
        }
        view1.findViewById(R.id.iv_livevideo_vote_result_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomContent.removeView(contentView);
                contentView = null;
                EventBus.getDefault().post(new NativeVoteRusltulCloseEvent(answer>0,voteEntity.getChoiceId()));
            }
        });
        StableLogHashMap logHashMap = new StableLogHashMap("showVoteResult");
        logHashMap.put("voteid", "" + voteEntity.getChoiceId());
        logHashMap.addSno("8").addNonce("" + voteEntity.getNonce());
        logHashMap.addExY().addStable("1");
        umsAgentDebug3(eventId, logHashMap.getData());
    }

    @Override
    public void voteJoin(final LiveTopic.VoteEntity voteEntity, int answer) {
        Loger.d(TAG, "voteJoin:choiceId=" + voteEntity + ",answer=" + answer);
        this.answer = answer;
        idAndAnswer.put(voteEntity, answer);
        if (0 == answer && !LiveVideoConfig.isPrimary) {
            showChoice(voteEntity);
        }else if(0 == answer && LiveVideoConfig.isPrimary){
            showPSChoice(voteEntity);
        }
    }

    @Override
    public void voteStart(final LiveTopic.VoteEntity voteEntity) {
        Loger.d(TAG, "voteStart:voteEntity=" + voteEntity);
        this.voteEntity = voteEntity;
        this.answer = 0;
        StableLogHashMap logHashMap = new StableLogHashMap("receiveVote");
        logHashMap.put("voteid", "" + voteEntity.getChoiceId());
        logHashMap.addSno("3").addNonce("" + voteEntity.getNonce()).addStable("2");
        umsAgentDebug(eventId, logHashMap.getData());
        if(LiveVideoConfig.isPrimary){
            showPSChoice(voteEntity);
        } else {
            showChoice(voteEntity);
        }

    }

    private void showPSChoice(final LiveTopic.VoteEntity voteEntity) {
        bottomContent.post(new Runnable() {
            @Override
            public void run() {
                if (contentView != null) {
                    bottomContent.removeView(contentView);
                }
                final View view = LayoutInflater.from(context).inflate(R.layout.page_livevideo_ps_vote_select, bottomContent, false);
                view.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                });
                contentView = new RelativeLayout(context);
                contentView.addView(view);
                bottomContent.addView(contentView);
                mIl_livevideo_vote_ps_choice = (LinearLayout) view.findViewById(R.id.il_livevideo_vote_ps_choice);
                final LinearLayout ll_statistics = (LinearLayout) view.findViewById(R.id.ll_statistics);
                final ImageView progress = (ImageView) view.findViewById(R.id.iv_psprogress);
                ll_statistics.setVisibility(View.GONE);
               final int choiceNum = voteEntity.getChoiceNum();
                for (int i = 0; i < choiceNum; i++) {
                    final int answer = i + 1;
                    final int j = i + 1;
                    View convertView = LayoutInflater.from(context).inflate(R.layout.item_livevideo_vote_ps_select, mIl_livevideo_vote_ps_choice, false);
                    if(choiceNum > 5){
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        if (i != choiceNum - 1) {
                            lp.rightMargin = (int) (8 * ScreenUtils.getScreenDensity());
                        }
                        mIl_livevideo_vote_ps_choice.setOrientation(LinearLayout.HORIZONTAL);
                        mIl_livevideo_vote_ps_choice.addView(convertView, lp);
                    } else {
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        if (i != choiceNum - 1) {
                            lp.rightMargin = (int) (25 * ScreenUtils.getScreenDensity());
                        }
                        mIl_livevideo_vote_ps_choice.setOrientation(LinearLayout.HORIZONTAL);
                        mIl_livevideo_vote_ps_choice.addView(convertView, lp);
                    }
                    mBtn_livevideo_vote_item = (Button) convertView.findViewById(R.id.btn_livevideo_vote_ps_item);
                    if (voteEntity.getChoiceType() == 1) {
                        mBtn_livevideo_vote_item.setBackgroundResource(pschoices[i]);
                    } else {
                        if (i == 0) {
                            mBtn_livevideo_vote_item.setBackgroundResource(R.drawable.livevideo_votechoice_psyes);
                        } else {
                            mBtn_livevideo_vote_item.setBackgroundResource(R.drawable.livevideo_votechoice_psno);
                        }
                    }
                    mBtn_livevideo_vote_item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ll_statistics.setVisibility(View.VISIBLE);
                            Animation circle_anim = AnimationUtils.loadAnimation(context, R.anim.anim_livevideo_psvote_progress);
                            LinearInterpolator interpolator = new LinearInterpolator();  //设置匀速旋转，在xml文件中设置会出现卡顿
                            circle_anim.setInterpolator(interpolator);
                            if (circle_anim != null) {
                                progress.startAnimation(circle_anim);  //开始动画
                            }
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ll_statistics.setVisibility(View.GONE);
                                    bottomContent.removeView(contentView);
                                    contentView = null;
                                }
                            }, 20000);
                            LiveVoteBll.this.answer = answer;
                            // 未被选中的选项背景色改变
                            mIl_livevideo_vote_ps_choice.removeAllViews();
                            for(int i = 0 ; i < choiceNum ; i++){
                                View convertView = LayoutInflater.from(context).inflate(R.layout.item_livevideo_vote_ps_select, mIl_livevideo_vote_ps_choice, false);
                                if(choiceNum > 5){
                                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    if (i != choiceNum - 1) {
                                        lp.rightMargin = (int) (8 * ScreenUtils.getScreenDensity());
                                    }
                                    mIl_livevideo_vote_ps_choice.setOrientation(LinearLayout.HORIZONTAL);
                                    mIl_livevideo_vote_ps_choice.addView(convertView, lp);
                                } else {
                                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    if (i != choiceNum - 1) {
                                        lp.rightMargin = (int) (25 * ScreenUtils.getScreenDensity());
                                    }
                                    mIl_livevideo_vote_ps_choice.setOrientation(LinearLayout.HORIZONTAL);
                                    mIl_livevideo_vote_ps_choice.addView(convertView, lp);
                                }

                                mBtn_livevideo_vote_item = (Button) convertView.findViewById(R.id.btn_livevideo_vote_ps_item);
                                if (voteEntity.getChoiceType() == 1) {
                                    if(i+1 == j){
                                        mBtn_livevideo_vote_item.setBackgroundResource(pschoices[i]);
                                    }else{
                                        mBtn_livevideo_vote_item.setBackgroundResource(pschoices[i]);
                                        mBtn_livevideo_vote_item.setAlpha(0.2f);
                                    }

                                } else {
                                    if (i == 0) {
                                        if(i+1 == j){
                                            mBtn_livevideo_vote_item.setBackgroundResource(R.drawable.livevideo_votechoice_psyes);
                                        }else{
                                            mBtn_livevideo_vote_item.setBackgroundResource(R.drawable.livevideo_votechoice_psyes);
                                            mBtn_livevideo_vote_item.setAlpha(0.2f);
                                        }

                                    } else {
                                        if(i+1 == j){
                                            mBtn_livevideo_vote_item.setBackgroundResource(R.drawable.livevideo_votechoice_psno);
                                        }else{
                                            mBtn_livevideo_vote_item.setBackgroundResource(R.drawable.livevideo_votechoice_psno);
                                            mBtn_livevideo_vote_item.setAlpha(0.2f);
                                        }

                                    }
                                }
                                //
                            }
                            idAndAnswer.put(voteEntity, answer);
                            String nonce = "" + StableLogHashMap.creatNonce();
                            liveBll.sendVote(answer, nonce);
                            StableLogHashMap logHashMap = new StableLogHashMap("submitVote");
                            logHashMap.put("voteid", "" + voteEntity.getChoiceId());
                            logHashMap.put("stuvote", "" + answer);
                            logHashMap.addSno("5").addNonce(nonce).addStable("2");
                            umsAgentDebug2(eventId, logHashMap.getData());
                        }
                    });


                    view.findViewById(R.id.iv_livevideo_votepschoice_close).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bottomContent.removeView(contentView);
                            contentView = null;
                        }
                    });
                    StableLogHashMap logHashMap = new StableLogHashMap("showVote");
                    logHashMap.put("voteid", "" + voteEntity.getChoiceId());
                    logHashMap.addSno("4").addExY().addNonce("" + voteEntity.getNonce()).addStable("1");
                    umsAgentDebug3(eventId, logHashMap.getData());

                }
            }

        });
    }

    private void conVertColor() {
        mBtn_livevideo_vote_item.setAlpha(0.6f);
    }

    private void conVertColors() {
        mBtn_livevideo_vote_item.setAlpha(1f);
    }

    private void showChoice(final LiveTopic.VoteEntity voteEntity) {
        bottomContent.post(new Runnable() {
            @Override
            public void run() {
                if (contentView != null) {
                    bottomContent.removeView(contentView);
                }
                if (voteWaitDialog != null) {
                    voteWaitDialog.cancelDialog();
                    voteWaitDialog = null;
                }
                final View view = LayoutInflater.from(context).inflate(R.layout.page_livevodeo_vote_select, bottomContent, false);
                view.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                });
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
                            voteWaitDialog = new VoteWaitDialog(context, baseApplication, false);
                            voteWaitDialog.showDialog();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (voteWaitDialog != null) {
                                        voteWaitDialog.cancelDialog();
                                        voteWaitDialog = null;
                                    }
                                }
                            }, 20000);
                            bottomContent.removeView(contentView);
                            contentView = null;
                            LiveVoteBll.this.answer = answer;
                            idAndAnswer.put(voteEntity, answer);
                            String nonce = "" + StableLogHashMap.creatNonce();
                            liveBll.sendVote(answer, nonce);
                            StableLogHashMap logHashMap = new StableLogHashMap("submitVote");
                            logHashMap.put("voteid", "" + voteEntity.getChoiceId());
                            logHashMap.put("stuvote", "" + answer);
                            logHashMap.addSno("5").addNonce(nonce).addStable("2");
                            umsAgentDebug2(eventId, logHashMap.getData());
                        }
                    });
                    view.findViewById(R.id.iv_livevideo_vote_result_close).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bottomContent.removeView(contentView);
                            contentView = null;
                        }
                    });
                    StableLogHashMap logHashMap = new StableLogHashMap("showVote");
                    logHashMap.put("voteid", "" + voteEntity.getChoiceId());
                    logHashMap.addSno("4").addExY().addNonce("" + voteEntity.getNonce()).addStable("1");
                    umsAgentDebug3(eventId, logHashMap.getData());
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
                ArrayList<LiveTopic.VoteResult> voteResults = voteEntity.getVoteResults();
                if (voteResults.isEmpty()) {
                    if (contentView != null) {
                        bottomContent.removeView(contentView);
                        contentView = null;
                    }
                    if (voteWaitDialog != null) {
                        voteWaitDialog.cancelDialog();
                        voteWaitDialog = null;
                    }
                } else {
                    StableLogHashMap logHashMap = new StableLogHashMap("receiveVoteResult");
                    logHashMap.put("voteid", "" + voteEntity.getChoiceId());
                    logHashMap.addSno("7");
                    logHashMap.addNonce("" + voteEntity.getNonce());
                    logHashMap.addStable("2");
                    umsAgentDebug(eventId, logHashMap.getData());
                    if(LiveVideoConfig.isPrimary){
                        showPsResult(voteEntity);
                    } else{
                        showResult(voteEntity);
                    }

                }
            }
        });
    }

    private void showPsResult(final LiveTopic.VoteEntity voteEntity) {
        if (contentView != null) {
            bottomContent.removeView(contentView);
        }
        if (voteWaitDialog != null) {
            voteWaitDialog.cancelDialog();
            voteWaitDialog = null;
        }
        final View view1 = LayoutInflater.from(context).inflate(R.layout.layout_livevideo_ps_vote_result, bottomContent, false);
        contentView = new RelativeLayout(context);
        contentView.addView(view1);
        bottomContent.addView(contentView);
        LinearLayout linearLayout = (LinearLayout) view1.findViewById(R.id.ll_livevideo_vote_ps_result_content);
        int choiceNum = voteEntity.getChoiceNum();
        ArrayList<LiveTopic.VoteResult> voteResults = voteEntity.getVoteResults();
//        TextView tv_livevideo_vote_result_mine = (TextView) view1.findViewById(R.id.tv_livevideo_vote_result_mine);
        for (int i = 0; i < choiceNum; i++) {
            View convertView = LayoutInflater.from(context).inflate(R.layout.item_livevideo_vote_psresult_select, linearLayout, false);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) convertView.getLayoutParams();
            if (choiceNum > 4) {
//                lp.weight = 1;
                if (i != choiceNum - 1) {
                    lp.rightMargin = (int) (23 * ScreenUtils.getScreenDensity());
                }
            } else {
                if (i != choiceNum - 1) {
                    lp.rightMargin = (int) (60 * ScreenUtils.getScreenDensity());
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

            final float rado;
            if (voteEntity.getTotal() == 0) {
                rado = 0;
            } else {
                rado = result.getPople() * 100 / voteEntity.getTotal();
            }
            TextView tv_livevideo_vote_result_count = (TextView) convertView.findViewById(R.id.tv_livevideo_vote_result_count);
//            tv_livevideo_vote_result_count.setText(Math.round(rado) + "% " + result.getPople() + "人");
            tv_livevideo_vote_result_count.setText(Math.round(rado) + "% ");
//            Drawable drawable = context.getResources().getDrawable(R.drawable.shape_live_vote_prog_max);
//            pb_livevideo_vote_result_item.setProgressDrawable(drawable);
            final ImageView livevideo_psvote_result_item = (ImageView) convertView.findViewById(R.id.iv_livevideo_ps_progress);
//            pb_livevideo_vote_result_item.setMax(voteEntity.getTotal());
            int newProgress = result.getPople();
            if (newProgress > 1) {
                final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, newProgress);
                final float finalNewProgress = newProgress;
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float fraction = animation.getAnimatedFraction();
                        float oldProgress = (finalNewProgress) * fraction;
//                        pb_livevideo_vote_result_item.setProgress((int) oldProgress);
                        ViewGroup.LayoutParams params = livevideo_psvote_result_item.getLayoutParams();
                        params.height = dp2px(context,(int)(150 * (rado/100))-16);
                        params.width = dp2px(context,14);
                        livevideo_psvote_result_item.setLayoutParams(params);
//                        livevideo_psvote_result_item.setMaxHeight(248*(int)(finalNewProgress/voteEntity.getTotal()));

                    }
                });
                valueAnimator.setDuration(1000);
                valueAnimator.start();
            } else {
//                pb_livevideo_vote_result_item.setProgress(newProgress);
                ViewGroup.LayoutParams params = livevideo_psvote_result_item.getLayoutParams();
                if(rado > 0){
                    params.height = dp2px(context,(int)(150 * (rado/100))-16);
                }else{
                    params.height = dp2px(context,(int)(150 * (rado/100)));
                }
                params.width = dp2px(context,14);
                livevideo_psvote_result_item.setLayoutParams(params);
//                livevideo_psvote_result_item.setMaxHeight(248*(int)(newProgress/voteEntity.getTotal()));
            }
        }
        if (answer > 0) {
            View convertView = linearLayout.getChildAt(answer - 1);
            TextView tv_livevideo_vote_result_item = (TextView) convertView.findViewById(R.id.tv_livevideo_vote_result_item);
            String myAnwer = tv_livevideo_vote_result_item.getText() + "";
//            tv_livevideo_vote_result_mine.setText("我的选择: " + myAnwer);
        } else {
//            tv_livevideo_vote_result_mine.setText("你错过了本次选择");
        }
        view1.findViewById(R.id.iv_livevideo_psvoteresult_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomContent.removeView(contentView);
                contentView = null;
                EventBus.getDefault().post(new NativeVoteRusltulCloseEvent(answer>0,voteEntity.getChoiceId()));
            }
        });
        StableLogHashMap logHashMap = new StableLogHashMap("showVoteResult");
        logHashMap.put("voteid", "" + voteEntity.getChoiceId());
        logHashMap.addSno("8").addNonce("" + voteEntity.getNonce());
        logHashMap.addExY().addStable("1");
        umsAgentDebug3(eventId, logHashMap.getData());
    }

    @Override
    public void onCancle() {
        this.voteEntity = null;
    }

    public void umsAgentDebug(String eventId, final Map<String, String> mData) {
        liveBll.umsAgentDebugSys(eventId, mData);
    }

    public void umsAgentDebug2(String eventId, final Map<String, String> mData) {
        liveBll.umsAgentDebugInter(eventId, mData);
    }

    public void umsAgentDebug3(String eventId, final Map<String, String> mData) {
        liveBll.umsAgentDebugPv(eventId, mData);
    }

    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}