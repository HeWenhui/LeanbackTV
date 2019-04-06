package com.xueersi.parentsmeeting.modules.livevideo.fragment.se.buycourse;

import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.module.browser.activity.BrowserActivity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ExperienceResult;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.se.IExperiencePresenter;
import com.xueersi.parentsmeeting.modules.livevideo.widget.RoundProgressBar;

/**
 * @author zyy
 */
public class ExperienceBuyCourseView<T extends IExperiencePresenter> extends BasePager implements
        IBuyCourseContract.View {
    protected T mPresenter;

    private TextView recommand;

    private TextView beat;

    private TextView totalscore;

    private RoundProgressBar mProgressbar;

    private Button chat;

    private Button apply;

    public ExperienceBuyCourseView(Context context, T mPresenter) {
        super(context);
        this.mPresenter = mPresenter;
    }

    @Override
    public View initView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View result = inflater.inflate(R.layout.pop_experience_livevideo_result, null);
//        ((Activity) mContext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//        mWindow = new PopupWindow(result, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams
//                .MATCH_PARENT, false);
//        mWindow.setOutsideTouchable(false);
//        mWindow.showAtLocation(result, Gravity.CENTER, 0, 0);
        mProgressbar = (RoundProgressBar) result.findViewById(R.id.roundProgressBar);
        recommand = (TextView) result.findViewById(R.id.tv_detail_result);
        beat = (TextView) result.findViewById(R.id.tv_result);
        totalscore = (TextView) result.findViewById(R.id.tv_total_score);
        chat = (Button) result.findViewById(R.id.bt_chat);
        apply = (Button) result.findViewById(R.id.bt_apply);
        return result;
    }

    @Override
    public void updateView(final ExperienceResult mData) {
        beat.setText("恭喜，你打败了" + mData.getBeat() + "%的学生");
        if (TextUtils.isEmpty(mData.getRecommend())) {
            recommand.setVisibility(View.VISIBLE);
            recommand.setText("赶快去报班继续提高成绩吧");
        } else {
            recommand.setVisibility(View.VISIBLE);
            recommand.setText("推荐您报名" + mData.getRecommend());
        }
        totalscore.setText(mData.getCorrect() + "%");
        mProgressbar.setMax(100);
        if (mData.getCorrect() > 0) {
            mProgressbar.setProgress(mData.getCorrect());
        } else {
            mProgressbar.setProgress(0);
        }
        ImageButton shut = (ImageButton) mView.findViewById(R.id.ib_shut);
        shut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mWindow.dismiss();
                mPresenter.removeWindow();
//                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//                showPopupwinFeedback();
                mPresenter.showNextWindow();//展示学习反馈弹窗
//                mWindow = null;
//                setBackgroundAlpha(1f);
            }
        });

        if (TextUtils.isEmpty(mData.getWechatNum())) {
            chat.setVisibility(View.GONE);
        } else {
            chat.setVisibility(View.VISIBLE);
        }
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(mData.getWechatNum());
                Toast.makeText(mContext, "您已复制老师微信号，快去添加吧!", Toast.LENGTH_LONG).show();
            }
        });

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mData.getUrl() != null) {
                    BrowserActivity.openBrowser(mContext, mData.getUrl());
                } else {
                    Toast.makeText(mContext, "数据异常", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public void initData() {

    }
}
