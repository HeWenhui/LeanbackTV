package com.xueersi.parentsmeeting.modules.livevideo.primaryclass.Item;

import android.content.Context;
import android.os.Build;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.business.agora.CloudWorkerThreadPool;
import com.xueersi.parentsmeeting.modules.livevideo.groupgame.item.TextureVideoViewOutlineProvider;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.entity.TeamMember;
import com.xueersi.ui.adapter.AdapterItemInterface;

public class BasePrimaryTeamItem implements AdapterItemInterface<TeamMember> {
    protected String TAG = getClass().getSimpleName();
    protected Context mContext;
    protected TeamMember entity;
    protected CloudWorkerThreadPool cloudWorkerThreadPool;
    protected LogToFile mLogtf;
    protected int uid;
    RelativeLayout rlCourseItemVideo;
    protected RelativeLayout rlCourseItemVideoHead;
    TextView tv_livevideo_primary_team_people_name;
    protected ImageView iv_livevideo_primary_team_voice_open;

    public BasePrimaryTeamItem(Context context, TeamMember entity, CloudWorkerThreadPool workerThread, int uid) {
        this.mContext = context;
        this.entity = entity;
        this.cloudWorkerThreadPool = workerThread;
        this.uid = uid;
        mLogtf = new LogToFile(context, TAG);
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
    }

    @Override
    public void bindListener() {

    }

    @Override
    public void updateViews(TeamMember entity, int position, Object objTag) {

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

    public void reportAudioVolumeOfSpeaker(int volume) {

    }
}
