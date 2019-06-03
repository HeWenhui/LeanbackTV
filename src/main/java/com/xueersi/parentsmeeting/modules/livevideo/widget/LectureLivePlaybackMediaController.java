package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.parentsmeeting.module.videoplayer.media.BackMediaPlayerControl;
import com.xueersi.parentsmeeting.module.videoplayer.widget.LivePlaybackMediaController;
import com.xueersi.parentsmeeting.modules.livevideo.R;

/**
 * 直播讲座回放
 *
 * @author ZouHao
 */
public class LectureLivePlaybackMediaController extends LivePlaybackMediaController {
    private String TAG = "LectureLivePlaybackMediaController";
    /** 在线人数 */
    private TextView tvPeopleCount;
    /** 关闭滚屏 */
    private CheckBox cbLock;
    /** 关闭滚屏点击事件 */
    private OnLockCheckedChange onLockCheckedChange;

    public LectureLivePlaybackMediaController(Context context, BackMediaPlayerControl player) {
        super(context, player);
    }

    @Override
    protected void findViewItems() {
        super.findViewItems();
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.rl_video_mediacontroller_info_panel);
        tvPeopleCount = new TextView(getContext());
        tvPeopleCount.setTextColor(Color.WHITE);
        tvPeopleCount.setVisibility(GONE);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp.addRule(RelativeLayout.CENTER_VERTICAL);
        lp.rightMargin = (int) (15 * ScreenUtils.getScreenDensity());
        relativeLayout.addView(tvPeopleCount, lp);
    }

    @Override
    public void setHaveBottom(boolean have) {
        super.setHaveBottom(have);
        RelativeLayout relativeLayout = (RelativeLayout) mediaControllerBottom.findViewById(R.id.rl_video_mediacontroller_bottom2);
        if (relativeLayout != null) {
            cbLock = new CheckBox(getContext());
            cbLock.setVisibility(GONE);
            cbLock.setBackgroundResource(R.drawable.selector_livevideo_message_lockclean);
            cbLock.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            lp.addRule(RelativeLayout.CENTER_VERTICAL);
            lp.rightMargin = (int) (15 * ScreenUtils.getScreenDensity());
            relativeLayout.addView(cbLock, lp);
            cbLock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (onLockCheckedChange != null) {
                        onLockCheckedChange.onLockCheckedChange(isChecked);
                    }
                }
            });
        }
    }

    /** 关闭滚屏显示 */
    public void setLockCheckVisibility(int visibility) {
        if (cbLock != null) {
            cbLock.setVisibility(visibility);
        }
    }

    /** 关闭滚屏选中 */
    public void setLockCheck() {
        if (cbLock != null) {
            cbLock.setChecked(true);
        }
    }

    public void setPeopleCount(int peopleCount) {
        if (tvPeopleCount.getVisibility() == View.GONE) {
            tvPeopleCount.setVisibility(View.VISIBLE);
        }
        this.tvPeopleCount.setText("在线听众: " + peopleCount + "人");
    }

    public void setOnLockClick(OnLockCheckedChange onLockCheckedChange) {
        this.onLockCheckedChange = onLockCheckedChange;
    }

    /** 关闭滚屏点击事件 */
    public interface OnLockCheckedChange {
        void onLockCheckedChange(boolean check);
    }
}
