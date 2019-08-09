package com.xueersi.parentsmeeting.modules.livevideoOldIJK.business;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.HalfBodyLiveStudyInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StudyInfo;

/**
 * 半身直播  家长旁听 业务bll
 *
 * @author chenkun
 * @version 1.0, 2018/10/29 下午4:04
 */

public class HalfBodyAuditClassBll implements AuditClassAction {

    private TextView tvCheckTime;
    private TextView tvOnlineTime;
    private RecyclerView rclAnwerState;
    private HalfBodyLiveStudyInfo mLiveStudyInfo;
    private StuLiveInfoAdapter mAdapter;
    private ImageView ivNodata;

    private TextView tvMyRank;
    private TextView tvOurEnergy;
    private TextView tvAgainstEnergy;


    public HalfBodyAuditClassBll(Activity activity) {
        initView(activity);
    }

    private void initView(Activity activity) {
        tvCheckTime = activity.findViewById(R.id.tv_livevideo_student_check_time);
        tvOnlineTime = activity.findViewById(R.id.tv_livevideo_student_online);
        rclAnwerState = activity.findViewById(R.id.rcl_livevideo_auditclass_anwser_state);
        rclAnwerState.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        ivNodata = activity.findViewById(R.id.iv_live_auditclass_nodata);

        tvAgainstEnergy = activity.findViewById(R.id.tv_livevideo_auditclass_against_energy);
        tvOurEnergy = activity.findViewById(R.id.tv_livevideo_auditclass_energy);
        tvMyRank = activity.findViewById(R.id.tv_livevideo_auditclass_myrank);
    }

    @Override
    public void onGetStudyInfo(StudyInfo studyInfo) {
        if (studyInfo != null && studyInfo instanceof HalfBodyLiveStudyInfo) {
            mLiveStudyInfo = (HalfBodyLiveStudyInfo) studyInfo;
            if (mLiveStudyInfo.getTestList() == null || mLiveStudyInfo.getTestList().size() == 0) {
                ivNodata.setVisibility(View.VISIBLE);
            } else {
                ivNodata.setVisibility(View.GONE);
            }
            tvCheckTime.setText(studyInfo.getSignTime());
            tvOnlineTime.setText(studyInfo.getOnlineTime());
            tvMyRank.setText(mLiveStudyInfo.getMyRank());
            tvOurEnergy.setText(mLiveStudyInfo.getOurTeamEnergy() + "");
            tvAgainstEnergy.setText(mLiveStudyInfo.getHostileTeamEnergy() + "");

            if (mAdapter == null) {
                mAdapter = new StuLiveInfoAdapter(mLiveStudyInfo);
                rclAnwerState.setAdapter(mAdapter);
            } else {
                mAdapter.upDateData(mLiveStudyInfo);
            }
        }
    }


    private static class StuLiveInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private HalfBodyLiveStudyInfo mData;

        public StuLiveInfoAdapter(HalfBodyLiveStudyInfo data) {
            this.mData = data;
        }

        /**
         * 战队PK信息 item
         */
        private static final int ITEM_TYPE_PK = 1;
        /**
         * 试题信息 item
         */
        private static final int ITEM_TYPE_TEST = 2;

        public void upDateData(HalfBodyLiveStudyInfo data) {
            mData = data;
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            int itemType;
            if (position == 0) {
                itemType = ITEM_TYPE_PK;
            } else {
                itemType = ITEM_TYPE_TEST;
            }
            return itemType;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder holder = null;
            if (ITEM_TYPE_PK == viewType) {
                holder = new PkItemHolder(LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.item_livevideo_auditclass_pk, parent, false));
            } else {
                holder = new TestItemHolder(LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.item_livevideo_auditclass_test_info, parent, false));
            }
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            if (getItemViewType(position) == ITEM_TYPE_PK) {
                ((PkItemHolder) holder).bindData(mData);
            } else {
                int realIndex = position - 1;
                ((TestItemHolder) holder).bindData(mData.getTestList().get(realIndex));
            }
        }

        @Override
        public int getItemCount() {
            int itemCount = 0;
            if (mData != null && mData.getTestList() != null) {
                itemCount = mData.getTestList().size() > 0? mData.getTestList().size() + 1:0;
            }
            return itemCount;
        }
    }

    private static class PkItemHolder extends RecyclerView.ViewHolder {

        private TextView tvRate;
        private View vTitleContainer;

        public PkItemHolder(View itemView) {
            super(itemView);
            vTitleContainer = itemView.findViewById(R.id.consl_livevideo_auditclass_test_tilte_container);
            tvRate = itemView.findViewById(R.id.tv_livevideo_auditclass_rate);
        }

        public void bindData(HalfBodyLiveStudyInfo data) {
            tvRate.setText(data.getStuAvgRate());
           /* if (data.getTestList() == null || data.getTestList().size() == 0) {
                vTitleContainer.setVisibility(View.GONE);
            } else {
                vTitleContainer.setVisibility(View.VISIBLE);
            }*/
        }
    }

    private static class TestItemHolder extends RecyclerView.ViewHolder {
        private TextView tvIndex;
        private ImageView ivState;
        private TextView tvRate;

        public TestItemHolder(View itemView) {
            super(itemView);
            tvIndex = itemView.findViewById(R.id.tv_livevideo_auditclass_test_index);
            ivState = itemView.findViewById(R.id.iv_livevideo_auditclass_answer_state);
            tvRate = itemView.findViewById(R.id.tv_livevideo_auditclass_answer_rate);
        }

        public void bindData(HalfBodyLiveStudyInfo.TestInfo data) {
            tvIndex.setText(data.getOrderNum() + "");
            int stateRes = getStateRes(data.getAnsweredStatus());
            if (stateRes != 0) {
                ivState.setImageResource(stateRes);
            }
            tvRate.setText(data.getPlanAvgRightRate());
        }

        private int getStateRes(int orderNum) {
            int resId = 0;
            if (orderNum == HalfBodyLiveStudyInfo.ANSWER_STATE_RIGHT) {
                resId = R.drawable.icon_live_auditclass_correct;
            } else if (orderNum == HalfBodyLiveStudyInfo.ANSWER_STATE_PART_RIGHT) {
                resId = R.drawable.icon_live_auditclass_part_correct;
            } else {
                resId = R.drawable.icon_live_auditclass_wrong;
            }
            return resId;
        }
    }

}
