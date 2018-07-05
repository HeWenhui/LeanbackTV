package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.common.base.BaseApplication;
import com.xueersi.lib.framework.utils.Log;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.TopicAction;
import com.xueersi.parentsmeeting.modules.livevideo.dialog.VoteWaitDialog;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.event.NativeVoteRusltulCloseEvent;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;
import com.xueersi.lib.framework.utils.ScreenUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
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
    RelativeLayout bottomContent;
    RelativeLayout contentView;
    LiveTopic.VoteEntity voteEntity;

    VoteWaitDialog voteWaitDialog;
    HashMap<LiveTopic.VoteEntity, Integer> idAndAnswer = new HashMap<>();
    int answer;
    private LiveGetInfo mGetInfo;

    public LiveVoteBll(Activity context, LiveBll2 liveBll, RelativeLayout rootView) {
        super(context, liveBll, rootView);
        bottomContent = (RelativeLayout) rootView;
        this.context = context;
    }

    public LiveVoteBll(Context context) {

        this((Activity) context, null, null);
        this.context = context;

    }

    public void initView(final RelativeLayout bottomContent) {
        this.bottomContent = bottomContent;
    }

    public void setLiveBll(LiveBll liveBll) {
        // this.liveBll = liveBll;
    }

    private void showResult(final LiveTopic.VoteEntity voteEntity) {
        if (contentView != null) {
            bottomContent.removeView(contentView);
        }
        if (voteWaitDialog != null) {
            voteWaitDialog.cancelDialog();
            voteWaitDialog = null;
        }
        final View view1 = LayoutInflater.from(context).inflate(R.layout.layout_livevideo_vote_result, bottomContent,
                false);
        contentView = new RelativeLayout(context);
        contentView.addView(view1);
        bottomContent.addView(contentView);
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
                bottomContent.removeView(contentView);
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
        StableLogHashMap logHashMap = new StableLogHashMap("receiveVote");
        logHashMap.put("voteid", "" + voteEntity.getChoiceId());
        logHashMap.addSno("3").addNonce("" + voteEntity.getNonce()).addStable("2");
        umsAgentDebug(eventId, logHashMap.getData());
        showChoice(voteEntity);
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
                final View view = LayoutInflater.from(context).inflate(R.layout.page_livevodeo_vote_select,
                        bottomContent, false);
                view.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                });
                contentView = new RelativeLayout(context);
                contentView.addView(view);
                bottomContent.addView(contentView);
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
                            bottomContent.removeView(contentView);
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
                    showResult(voteEntity);
                }
            }
        });
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
    public void onNotice(JSONObject data, int type) {
        Loger.e("LiveVoteBll", "=====>onNotice =:" + type);
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
     * @param noce
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

}