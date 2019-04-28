package com.xueersi.parentsmeeting.modules.livevideoOldIJK.business;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.common.base.BaseApplication;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.activity.item.VoteAdapter;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.dialog.VoteWaitDialog;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PsState;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.event.NativeVoteRusltulCloseEvent;
import com.xueersi.parentsmeeting.modules.livevideo.widget.MyGradView;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author linyuqiang
 * Created by linyuqiang on 2017/12/27.
 * 投票
 */
public class LiveVoteBll extends LiveBaseBll implements NoticeAction, LiveVoteAction {
    private static String TAG = "LiveVoteBll";
    String eventId = LiveVideoConfig.LIVE_VOTE;
    Context context;
    RelativeLayout contentView;
    LiveTopic.VoteEntity voteEntity;

    VoteWaitDialog voteWaitDialog;
    HashMap<LiveTopic.VoteEntity, Integer> idAndAnswer = new HashMap<>();
    int answer;
    private LiveGetInfo mGetInfo;

    public LiveVoteBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
        this.context = context;
    }
    public List<PsState> resource;
    // 选项资源的图片
    public int[] pschoices = {R.drawable.livevideo_votechoice_psa, R.drawable.livevideo_votechoice_psb, R.drawable.livevideo_votechoice_psc,R.drawable.livevideo_votechoice_psd,R.drawable.livevideo_votechoice_pse,R.drawable.livevideo_votechoice_psf};
    public int[] pschoiceone = {R.drawable.livevideo_votechoice_psa, R.drawable.livevideo_votechoice_psb, R.drawable.livevideo_votechoice_psc,R.drawable.livevideo_votechoice_psd,R.drawable.livevideo_votechoice_pse};
    public int[] pschoicetwo = {R.drawable.livevideo_votechoice_psa, R.drawable.livevideo_votechoice_psb, R.drawable.livevideo_votechoice_psc,R.drawable.livevideo_votechoice_psd,R.drawable.livevideo_votechoice_pse,R.drawable.livevideo_votechoice_psf};
    public int[] pschoicess = {R.drawable.livevideo_votechoice_psyes, R.drawable.livevideo_votechoice_psno};
    private Button mBtn_livevideo_vote_item;
    private LinearLayout mIl_livevideo_vote_ps_choice;

    public LiveVoteBll(Context context) {

        this((Activity) context, null);
        this.context = context;

    }

    public void initView(final RelativeLayout bottomContent) {
        this.mRootView = bottomContent;
    }

    private void showResult(final LiveTopic.VoteEntity voteEntity) {
        if (contentView != null) {
            mRootView.removeView(contentView);
        }
        if (voteWaitDialog != null) {
            voteWaitDialog.cancelDialog();
            voteWaitDialog = null;
        }
        final View view1 = LayoutInflater.from(context).inflate(R.layout.layout_livevideo_vote_result, mRootView,
                false);
        contentView = new RelativeLayout(context);
        contentView.addView(view1);
        mRootView.addView(contentView);
        LinearLayout linearLayout = (LinearLayout) view1.findViewById(R.id.ll_livevideo_vote_result_content);
        int choiceNum = voteEntity.getChoiceNum();
        ArrayList<LiveTopic.VoteResult> voteResults = voteEntity.getVoteResults();
        TextView tvVoteResultMine = (TextView) view1.findViewById(R.id.tv_livevideo_vote_result_mine);
        for (int i = 0; i < choiceNum; i++) {
            View convertView = LayoutInflater.from(context).inflate(R.layout.item_livevideo_vote_result_select,
                    linearLayout, false);
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
            TextView tvVoteResultItem = (TextView) convertView.findViewById(R.id
                    .tv_livevideo_vote_result_item);
            if (voteEntity.getChoiceType() == 1) {
                char c = (char) ('A' + i);
                tvVoteResultItem.setText("" + c);
            } else {
                if (i == 0) {
                    tvVoteResultItem.setText("是");
                } else {
                    tvVoteResultItem.setText("否");
                }
            }
            LiveTopic.VoteResult result = voteResults.get(i);

            float rado;
            if (voteEntity.getTotal() == 0) {
                rado = 0;
            } else {
                rado = result.getPople() * 100 / voteEntity.getTotal();
            }
            TextView tvVoteResultCount = (TextView) convertView.findViewById(R.id
                    .tv_livevideo_vote_result_count);
            tvVoteResultCount.setText(Math.round(rado) + "% ");
            final ProgressBar pbResultItem = (ProgressBar) convertView.findViewById(R.id
                    .pb_livevideo_vote_result_item);
            pbResultItem.setMax(voteEntity.getTotal());
            int newProgress = result.getPople();
            if (newProgress > 1) {
                final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, newProgress);
                final float finalNewProgress = newProgress;
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float fraction = animation.getAnimatedFraction();
                        float oldProgress = (finalNewProgress) * fraction;
                        pbResultItem.setProgress((int) oldProgress);
                    }
                });
                valueAnimator.setDuration(1000);
                valueAnimator.start();
            } else {
                pbResultItem.setProgress(newProgress);
            }
        }
        if (answer > 0) {
            View convertView = linearLayout.getChildAt(answer - 1);
            TextView tvVoteResultItem = (TextView) convertView.findViewById(R.id
                    .tv_livevideo_vote_result_item);
            String myAnwer = tvVoteResultItem.getText() + "";
            tvVoteResultMine.setText("我的选择: " + myAnwer);
        } else {
            tvVoteResultMine.setText("你错过了本次选择");
        }
        view1.findViewById(R.id.iv_livevideo_vote_result_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRootView.removeView(contentView);
                contentView = null;
                EventBus.getDefault().post(new NativeVoteRusltulCloseEvent(answer > 0, voteEntity.getChoiceId()));
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
        logger.d( "voteJoin:choiceId=" + voteEntity + ",answer=" + answer);
        this.answer = answer;
        idAndAnswer.put(voteEntity, answer);
        if (0 == answer && !LiveVideoConfig.isPrimary) {
            showChoice(voteEntity);
        }else if(0 == answer && LiveVideoConfig.isPrimary){
//            showPSChoice(voteEntity);
            showSPChoice(voteEntity);
        }
    }

    @Override
    public void voteStart(final LiveTopic.VoteEntity voteEntity) {
        logger.d( "voteStart:voteEntity=" + voteEntity);
        this.voteEntity = voteEntity;
        this.answer = 0;
        StableLogHashMap logHashMap = new StableLogHashMap("receiveVote");
        logHashMap.put("voteid", "" + voteEntity.getChoiceId());
        logHashMap.addSno("3").addNonce("" + voteEntity.getNonce()).addStable("2");
        umsAgentDebug(eventId, logHashMap.getData());
        if(LiveVideoConfig.isPrimary){
//            showPSChoice(voteEntity);
            showSPChoice(voteEntity);
        } else {
            showChoice(voteEntity);
        }

    }

    private void showSPChoice(final LiveTopic.VoteEntity voteEntity) {
        mRootView.post(new Runnable() {
            @Override
            public void run() {
                if (contentView != null) {
                    mRootView.removeView(contentView);
                }
                final View view = LayoutInflater.from(context).inflate(R.layout.page_livevideo_ps_vote_select, mRootView, false);
                view.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                });
                contentView = new RelativeLayout(context);
                contentView.addView(view);
                mRootView.addView(contentView);
                mIl_livevideo_vote_ps_choice = (LinearLayout) view.findViewById(R.id.il_livevideo_vote_ps_choice);
                final LinearLayout ll_statistics = (LinearLayout) view.findViewById(R.id.ll_statistics);
                final ImageView progress = (ImageView) view.findViewById(R.id.iv_psprogress);
                ImageView bg = (ImageView) view.findViewById(R.id.iv_livevideo_psvote_simplebg);
                ll_statistics.setVisibility(View.GONE);
                final int choiceNum = voteEntity.getChoiceNum();
                if(choiceNum > 4){
                    bg.setImageResource(R.drawable.livevideo_ps_vote_complex);
                    resource = new ArrayList<>();
                    if(resource.size() > 0){
                        resource.clear();
                    }
                    for(int i = 0 ; i < choiceNum ; i++){
                        resource.add(new PsState(pschoices[i],true));
                    }
                    MyGradView gv1 = new MyGradView(context);
                    gv1.setNumColumns(3);
                    final VoteAdapter adapter = new VoteAdapter(context,resource);
                    gv1.setAdapter(adapter);
                    LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(800, 150);
                    lp1.setMargins(134, 113, 103, 125);
                    gv1.setLayoutParams(lp1);
                    gv1.setHorizontalSpacing(50);
                    gv1.setVerticalSpacing(-40);
                    gv1.setSelector(new ColorDrawable(Color.TRANSPARENT));
                    gv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> view, View view1, int position, long l) {
                            ll_statistics.setVisibility(View.VISIBLE);
                            Animation circle_anim = AnimationUtils.loadAnimation(context, R.anim.anim_livevideo_psvote_progress);
                            LinearInterpolator interpolator = new LinearInterpolator();  //设置匀速旋转，在xml文件中设置会出现卡顿
                            circle_anim.setInterpolator(interpolator);
                            if (circle_anim != null) {
                                progress.startAnimation(circle_anim);  //开始动画
                            }
//                            Handler handler = new Handler();
//                            handler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    ll_statistics.setVisibility(View.GONE);
//                                    mRootView.removeView(contentView);
//                                    contentView = null;
//                                }
//                            }, 200000);
                            // 未被选中的item变颜色
                            for (int i = 0 ; i < choiceNum ; i++){
                                if(i == position){
                                    resource.get(position).setState(true);
                                }else{
                                    resource.get(i).setState(false);
                                }
                            }
                            adapter.notifyDataSetChanged();
                            LiveVoteBll.this.answer = position + 1;
                            idAndAnswer.put(voteEntity, answer);
                            String nonce = "" + StableLogHashMap.creatNonce();
                            sendVote(answer, nonce);
                            StableLogHashMap logHashMap = new StableLogHashMap("submitVote");
                            logHashMap.put("voteid", "" + voteEntity.getChoiceId());
                            logHashMap.put("stuvote", "" + answer);
                            logHashMap.addSno("5").addNonce(nonce).addStable("2");
                            umsAgentDebug2(eventId, logHashMap.getData());
                        }
                    });
                    mIl_livevideo_vote_ps_choice.addView(gv1);
                } else {
                    bg.setImageResource(R.drawable.livevideo_ps_vote_simple);
                    for (int i = 0; i < choiceNum; i++) {
                        final int answer = i + 1;
                        final int j = i + 1;
                        View convertView = LayoutInflater.from(context).inflate(R.layout.item_livevideo_vote_ps_select, mIl_livevideo_vote_ps_choice, false);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        if (i != choiceNum - 1) {
                            lp.rightMargin = (int) (25 * ScreenUtils.getScreenDensity());
                        }
                        mIl_livevideo_vote_ps_choice.setOrientation(LinearLayout.HORIZONTAL);
                        mIl_livevideo_vote_ps_choice.addView(convertView, lp);
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
//                                Handler handler = new Handler();
//                                handler.postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        ll_statistics.setVisibility(View.GONE);
//                                        mRootView.removeView(contentView);
//                                        contentView = null;
//                                    }
//                                }, 20000);
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
                                sendVote(answer, nonce);
                                StableLogHashMap logHashMap = new StableLogHashMap("submitVote");
                                logHashMap.put("voteid", "" + voteEntity.getChoiceId());
                                logHashMap.put("stuvote", "" + answer);
                                logHashMap.addSno("5").addNonce(nonce).addStable("2");
                                umsAgentDebug2(eventId, logHashMap.getData());
                            }
                        });

                    }
                }
                view.findViewById(R.id.iv_livevideo_votepschoice_close).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mRootView.removeView(contentView);
                        contentView = null;
                    }
                });
                StableLogHashMap logHashMap = new StableLogHashMap("showVote");
                logHashMap.put("voteid", "" + voteEntity.getChoiceId());
                logHashMap.addSno("4").addExY().addNonce("" + voteEntity.getNonce()).addStable("1");
                umsAgentDebug3(eventId, logHashMap.getData());

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
        mRootView.post(new Runnable() {
            @Override
            public void run() {
                if (contentView != null) {
                    mRootView.removeView(contentView);
                }
                if (voteWaitDialog != null) {
                    voteWaitDialog.cancelDialog();
                    voteWaitDialog = null;
                }
                final View view = LayoutInflater.from(context).inflate(R.layout.page_livevodeo_vote_select,
                        mRootView, false);
                view.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                });
                contentView = new RelativeLayout(context);
                contentView.addView(view);
                mRootView.addView(contentView);
                LinearLayout llVoteChoice = (LinearLayout) view.findViewById(R.id.il_livevideo_vote_choice);
                int choiceNum = voteEntity.getChoiceNum();
                for (int i = 0; i < choiceNum; i++) {
                    final int answer = i + 1;
                    View convertView = LayoutInflater.from(context).inflate(R.layout.item_livevideo_vote_select,
                            llVoteChoice, false);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams
                            .WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    if (i != choiceNum - 1) {
                        lp.rightMargin = (int) (25 * ScreenUtils.getScreenDensity());
                    }
                    llVoteChoice.addView(convertView, lp);
                    Button btnVoteItem = (Button) convertView.findViewById(R.id.btn_livevideo_vote_item);
                    if (voteEntity.getChoiceType() == 1) {
                        char c = (char) ('A' + i);
                        btnVoteItem.setText("" + c);
                    } else {
                        if (i == 0) {
                            btnVoteItem.setText("是");
                        } else {
                            btnVoteItem.setText("否");
                        }
                    }
                    btnVoteItem.setOnClickListener(new View.OnClickListener() {
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
                            mRootView.removeView(contentView);
                            contentView = null;
                            LiveVoteBll.this.answer = answer;
                            idAndAnswer.put(voteEntity, answer);
                            String nonce = "" + StableLogHashMap.creatNonce();
                            sendVote(answer, nonce);
                            StableLogHashMap logHashMap = new StableLogHashMap("submitVote");
                            logHashMap.put("voteid", "" + voteEntity.getChoiceId());
                            logHashMap.put("stuvote", "" + answer);
                            logHashMap.addSno("5").addNonce(nonce).addStable("2");
                            umsAgentDebug2(eventId, logHashMap.getData());
                        }
                    });
                    view.findViewById(R.id.iv_livevideo_vote_result_close).setOnClickListener(new View
                            .OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mRootView.removeView(contentView);
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
        logger.d( "voteStop:voteEntity=" + voteEntity);
        this.voteEntity = null;
        mRootView.post(new Runnable() {
            @Override
            public void run() {
                ArrayList<LiveTopic.VoteResult> voteResults = voteEntity.getVoteResults();
                if (voteResults.isEmpty()) {
                    if (contentView != null) {
                        mRootView.removeView(contentView);
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
            mRootView.removeView(contentView);
        }
        if (voteWaitDialog != null) {
            voteWaitDialog.cancelDialog();
            voteWaitDialog = null;
        }
        final View view1 = LayoutInflater.from(context).inflate(R.layout.layout_livevideo_ps_vote_result, mRootView, false);
        contentView = new RelativeLayout(context);
        contentView.addView(view1);
        mRootView.addView(contentView);
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
                    lp.rightMargin = (int) (50 * ScreenUtils.getScreenDensity());
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
            livevideo_psvote_result_item.setAdjustViewBounds(true);
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
                        params.height = dp2px(context,(int)(148 * (rado/100)));
                        params.width = dp2px(context,13);
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
                    params.height = dp2px(context,(int)(148 * (rado/100)));
                }else{
                    params.height = dp2px(context,(int)(148 * (rado/100)));
                }
                params.width = dp2px(context,13);
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
                mRootView.removeView(contentView);
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
        mLiveBll.umsAgentDebugSys(eventId, mData);
    }

    public void umsAgentDebug2(String eventId, final Map<String, String> mData) {
        mLiveBll.umsAgentDebugInter(eventId, mData);
    }

    public void umsAgentDebug3(String eventId, final Map<String, String> mData) {
        mLiveBll.umsAgentDebugPv(eventId, mData);
    }


    ///////通信相关//////

    private int[] noticeCodes = {
            XESCODE.VOTE_START,
            XESCODE.VOTE_START_JOIN
    };


    @Override
    public void onLiveInited(LiveGetInfo data) {
        super.onLiveInited(data);
        mGetInfo = data;
    }

    @Override
    public int[] getNoticeFilter() {
        return noticeCodes;
    }


    private static final String VOTE_STATE_ON = "on";

    private static final String VOTE_STATE_OFF = "off";

    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {
        logger.e( "=====>onNotice =:" + type);
        try {
            switch (type) {
                case XESCODE.VOTE_START: {
                    String open = data.optString("open");
                    String choiceId = data.getString("choiceId");
                    int choiceType = data.optInt("choiceType");
                    int choiceNum = data.optInt("choiceNum");
                    LiveTopic.VoteEntity voteEntity = new LiveTopic.VoteEntity();
                    voteEntity.setChoiceNum(choiceNum);
                    voteEntity.setChoiceType(choiceType);
                    voteEntity.setChoiceId(choiceId);
                    voteEntity.setNonce(data.optString("nonce"));
                    if (VOTE_STATE_ON.equals(open)) {
                        voteStart(voteEntity);
                    } else if (VOTE_STATE_OFF.equals(open)) {
                        ArrayList<LiveTopic.VoteResult> voteResults = voteEntity.getVoteResults();
                        JSONArray result = data.getJSONArray("result");
                        int total = 0;
                        for (int i = 0; i < result.length(); i++) {
                            LiveTopic.VoteResult voteResult = new LiveTopic.VoteResult();
                            int people = result.getInt(i);
                            voteResult.setPople(people);
                            total += people;
                            voteResults.add(voteResult);
                        }
                        voteEntity.setTotal(total);
                        voteStop(voteEntity);
                    }
                }
                break;
                case XESCODE.VOTE_START_JOIN: {
                    String open = data.optString("open");
                    String choiceId = data.getString("choiceId");
                    int choiceType = data.optInt("choiceType");
                    int choiceNum = data.optInt("choiceNum");
                    LiveTopic.VoteEntity voteEntity = new LiveTopic.VoteEntity();
                    voteEntity.setChoiceNum(choiceNum);
                    voteEntity.setChoiceType(choiceType);
                    voteEntity.setChoiceId(choiceId);
                    int answer = data.getInt("answer");
                    voteJoin(voteEntity, answer);
                }
                break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 发送 投票选择
     *
     * @param answer
     * @param nonce
     */
    private void sendVote(int answer, String nonce) {

        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "" + XESCODE.VOTE_SEND);
            jsonObject.put("id", "" + mGetInfo.getStuId());
            jsonObject.put("answer", "" + answer);
            jsonObject.put("nonce", "" + nonce);
            sendNotice(jsonObject, mLiveBll.getMainTeacherStr());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}