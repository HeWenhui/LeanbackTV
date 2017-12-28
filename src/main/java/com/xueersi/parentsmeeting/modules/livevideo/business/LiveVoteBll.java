package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.xesalib.utils.uikit.ScreenUtils;

/**
 * Created by lyqai on 2017/12/27.
 */

public class LiveVoteBll implements LiveVoteAction {
    Context context;
    RelativeLayout bottomContent;
    int choiceNum = 6;

    public LiveVoteBll(Context context) {
        this.context = context;
    }

    public void initView(final RelativeLayout bottomContent) {
        this.bottomContent = bottomContent;
//        final View view = LayoutInflater.from(context).inflate(R.layout.page_livevodeo_vote_select, bottomContent, false);
//        bottomContent.addView(view);
//        LinearLayout il_livevideo_vote_choice = (LinearLayout) view.findViewById(R.id.il_livevideo_vote_choice);
//        for (int i = 0; i < choiceNum; i++) {
//            View convertView = LayoutInflater.from(context).inflate(R.layout.item_livevideo_vote_select, il_livevideo_vote_choice, false);
//            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            if (i != choiceNum - 1) {
//                lp.rightMargin = (int) (25 * ScreenUtils.getScreenDensity());
//            }
//            il_livevideo_vote_choice.addView(convertView, lp);
//            Button btn_livevideo_vote_item = (Button) convertView.findViewById(R.id.btn_livevideo_vote_item);
//            char c = (char) ('A' + i);
//            btn_livevideo_vote_item.setText("" + c);
//            btn_livevideo_vote_item.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    showResult();
//                }
//            });
//        }
    }

    private void showResult() {
        final View view1 = LayoutInflater.from(context).inflate(R.layout.layout_livevideo_vote_result, bottomContent, false);
        bottomContent.addView(view1);
        LinearLayout linearLayout = (LinearLayout) view1.findViewById(R.id.ll_livevideo_vote_result_content);
        for (int i = 0; i < choiceNum; i++) {
            View convertView = LayoutInflater.from(context).inflate(R.layout.item_livevideo_vote_result_select, linearLayout, false);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) convertView.getLayoutParams();
            if (choiceNum > 4) {
                lp.weight = 1;
            } else {
                if (i != choiceNum - 1) {
                    lp.rightMargin = (int) (63 * ScreenUtils.getScreenDensity());
                }
            }
            linearLayout.addView(convertView, lp);
            TextView tv_livevideo_vote_result_item = (TextView) convertView.findViewById(R.id.tv_livevideo_vote_result_item);
            char c = (char) ('A' + i);
            tv_livevideo_vote_result_item.setText("" + c);
            ProgressBar pb_livevideo_vote_result_item = (ProgressBar) convertView.findViewById(R.id.pb_livevideo_vote_result_item);
            pb_livevideo_vote_result_item.setProgress(i * 10);
//            Drawable drawable = context.getResources().getDrawable(R.drawable.shape_live_vote_prog_max);
//            pb_livevideo_vote_result_item.setProgressDrawable(drawable);
        }
        view1.findViewById(R.id.iv_livevideo_vote_result_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomContent.removeView(view1);
            }
        });
    }

    @Override
    public void voteStart(int choiceType, int choiceNum) {
        this.choiceNum = choiceNum;
    }

    @Override
    public void voteStop() {

    }
}
