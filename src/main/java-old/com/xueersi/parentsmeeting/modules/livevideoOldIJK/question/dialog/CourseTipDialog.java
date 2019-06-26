package com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.dialog;

import android.app.Application;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.ui.dialog.BaseAlertDialog;

public class CourseTipDialog extends BaseAlertDialog {
    private TextView tvCommitOk;
    private TextView tvCommitContinue;
    private OnClick onClick;

    /**
     * 初始化
     *
     * @param context
     * @param application
     */
    public CourseTipDialog(Context context, Application application) {
        super(context, application, false, 0);
    }

    @Override
    protected View initDialogLayout(int type) {
        View view = mInflater.inflate(R.layout.dialog_livevideo_courseware_answer, null);
        TextView tvCommitTip=view.findViewById(R.id.tv_livevideo_new_course_commit_tip);
        tvCommitTip.setText("您还有题目未作答完\n答完所有题目才能提交呦~");
        tvCommitOk = view.findViewById(R.id.tv_livevideo_new_course_commit_ok);
        tvCommitContinue = view.findViewById(R.id.tv_livevideo_new_course_commit_continue);
        tvCommitOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClick != null) {
                    onClick.onCommit(CourseTipDialog.this, view);
                }
            }
        });
        tvCommitContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClick != null) {
                    onClick.onCancle(CourseTipDialog.this, view);
                }
            }
        });
        view.findViewById(R.id.iv_livevideo_new_course_commit_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClick != null) {
                    onClick.onCancle(CourseTipDialog.this, view);
                }
            }
        });
        return view;
    }

    public void setOnClick(OnClick onClick) {
        this.onClick = onClick;
    }

    public interface OnClick {
        void onCancle(CourseTipDialog dialog, View view);

        void onCommit(CourseTipDialog dialog, View view);
    }
}
