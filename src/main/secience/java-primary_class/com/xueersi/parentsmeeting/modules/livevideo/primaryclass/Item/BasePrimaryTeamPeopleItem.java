package com.xueersi.parentsmeeting.modules.livevideo.primaryclass.Item;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.agora.CloudWorkerThreadPool;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamMate;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.weight.VoiceImageView;

public class BasePrimaryTeamPeopleItem extends BasePrimaryTeamItem {
    protected RelativeLayout rlCourseItemVideo;
    protected RelativeLayout rlCourseItemVideoHead;
    protected TextView tv_livevideo_primary_team_people_name;
    protected ImageView iv_livevideo_primary_team_voice_open;
    protected RelativeLayout rl_livevideo_primary_team_tip;
    protected VoiceImageView voiceImageView;
    protected Handler handler = new Handler(Looper.getMainLooper());
    protected OnNameClick onNameClick;

    public BasePrimaryTeamPeopleItem(Context context, TeamMate entity, CloudWorkerThreadPool workerThread, int uid) {
        super(context, entity, workerThread, uid);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_primary_class_team_video;
    }

    @Override
    public void initViews(View root) {
        rlCourseItemVideo = root.findViewById(R.id.rl_livevideo_course_item_video);
        tv_livevideo_primary_team_people_name = root.findViewById(R.id.tv_livevideo_primary_team_people_name);
        rlCourseItemVideoHead = root.findViewById(R.id.rl_livevideo_course_item_video_head);
        iv_livevideo_primary_team_voice_open = root.findViewById(R.id.iv_livevideo_primary_team_voice_open);
        rl_livevideo_primary_team_tip = root.findViewById(R.id.rl_livevideo_primary_team_tip);
        voiceImageView = root.findViewById(R.id.iv_livevideo_primary_team_voice_voice);
    }

    @Override
    public void bindListener() {

    }

    @Override
    public void updateViews(TeamMate entity, int position, Object objTag) {

    }

    @Override
    public void reportAudioVolumeOfSpeaker(int volume) {
        voiceImageView.setVoice(volume);
    }

    public void doRenderRemoteUi(SurfaceView surfaceV) {
        rlCourseItemVideoHead.setVisibility(View.GONE);
        boolean remove = false;
        if (rlCourseItemVideo.getChildCount() > 0) {
            View view = rlCourseItemVideo.getChildAt(0);
            if (view instanceof SurfaceView) {
                rlCourseItemVideo.removeView(view);
                remove = true;
            }
        }
        mLogtf.d("doRenderRemoteUi:remove=" + remove + ",uid=" + uid);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        rlCourseItemVideo.addView(surfaceV, 0, lp);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            surfaceV.setOutlineProvider(new TextureVideoViewOutlineProvider(headCornerSize));
//            surfaceV.setClipToOutline(true);
//        }
    }

    public void setOnNameClick(OnNameClick onNameClick) {
        this.onNameClick = onNameClick;
    }

    public interface OnNameClick {
        void onNameClick(TeamMate entity, TextView tvName);
    }
}
