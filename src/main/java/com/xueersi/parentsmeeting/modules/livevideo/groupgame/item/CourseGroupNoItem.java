package com.xueersi.parentsmeeting.modules.livevideo.groupgame.item;

import android.content.Context;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.agora.WorkerThread;
import com.xueersi.parentsmeeting.modules.livevideo.business.agora.WorkerThreadPool;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.TeamMemberEntity;


public class CourseGroupNoItem extends BaseCourseGroupItem {
    /** 用户下方的控制 */
    private RelativeLayout rlCourseItemCtrl;
    /** 用户下方的加载中 */
    private TextView tvCourseItemLoad;
    private boolean enableVideo = true;
    private boolean enableAudio = true;
    private int progress = 0;

    public CourseGroupNoItem(Context context, TeamMemberEntity entity, WorkerThreadPool workerThread, int uid) {
        super(context, entity, workerThread, uid);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_livevideo_h5_courseware_group_people;
    }

    @Override
    public void initViews(View root) {
        super.initViews(root);
        ImageView imageView = new ImageView(root.getContext());
        imageView.setImageResource(R.drawable.pc_zbhd_shipingkuang_ufo);
        ((ViewGroup) root).addView(imageView);
    }

    public void doRenderRemoteUi(SurfaceView surfaceV) {
        rlCourseItemVideoHead.setVisibility(View.GONE);
        rlCourseItemVideo.addView(surfaceV, 0);
        rlCourseItemCtrl.setVisibility(View.VISIBLE);
        tvCourseItemLoad.setVisibility(View.GONE);
    }

    public void onUserOffline() {
        rlCourseItemVideoHead.setVisibility(View.VISIBLE);
        rlCourseItemCtrl.setVisibility(View.GONE);
        tvCourseItemLoad.setVisibility(View.VISIBLE);
    }

    @Override
    public void bindListener() {

    }

    public TeamMemberEntity getEntity() {
        return entity;
    }

    @Override
    public void updateViews(TeamMemberEntity entity, int position, Object objTag) {
        super.updateViews(entity, position, objTag);
    }

    public void onVolumeUpdate(int volume) {

    }

    public void onOtherDis(int type, boolean enable) {

    }

    public void onScene(String method) {

    }
}
