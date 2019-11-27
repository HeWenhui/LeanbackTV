package com.xueersi.parentsmeeting.modules.livevideo.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.XesActivity;
import com.xueersi.common.logerhelper.MobEnumUtil;
import com.xueersi.common.logerhelper.XesMobAgent;
import com.xueersi.lib.framework.utils.string.RegexUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.adapter.QuestionRateAdapter;
import com.xueersi.parentsmeeting.modules.livevideo.adapter.QuestionRateDetailAdapter;
import com.xueersi.parentsmeeting.modules.livevideo.adapter.VoiceQuestionDetailAdapter;
import com.xueersi.parentsmeeting.modules.livevideo.business.AuditClassRoomBll;
import com.xueersi.parentsmeeting.modules.livevideo.entity.AuditClassRoomEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.UserScoreEntity;
import com.xueersi.parentsmeeting.modules.livevideo.view.AuditClassRoomProgressView;
import com.xueersi.ui.dataload.DataErrorManager;
import com.xueersi.ui.dataload.DataLoadEntity;
import com.xueersi.ui.widget.AppTitleBar;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * 旁听课堂
 */
public class AuditClassRoomActivity extends XesActivity {
    /**
     * 课前测正确率
     */
    AuditClassRoomProgressView preTestProgressView;
    /**
     * 互动题正确率
     */
    AuditClassRoomProgressView questionRateProgressView;
    /**
     * 直播Id
     */
    String liveId;
    String stuCouId;
    /**
     * 旁听课堂业务类
     */
    AuditClassRoomBll mAuditClassRoomBll;
    private boolean isBigLive;
    /**
     * 数据加载
     */
    DataLoadEntity mDataLoadEntity;
    /**
     * 互动题排名
     */
    QuestionRateAdapter mQuestionRateAdapter;
    /**
     * 互动题对错情况
     */
    QuestionRateDetailAdapter mQuestionRateDetailAdapter;
    /**
     * 语音题目得分情况
     */
    VoiceQuestionDetailAdapter mVoiceQuestionDetailAdapter;
    /**
     * 互动题排名
     */
    RecyclerView questionRecyclerView;
    /**
     * 课前测正确率
     */
    TextView tvPreTestRate;
    /**
     * 互动题正确率
     */
    TextView tvQuestionRate;
    /**
     * 互动题详细
     */
    GridView gvQuestionDetail;
    /**
     * 语音题题详细
     */
    GridView gvVoiceQuestionDetail;
    /**
     * 左边线
     */
    View vLeftLine;
    /**
     * 签到小时
     */
    TextView tvCheckInHour;
    /**
     * 签到分
     */
    TextView tvCheckInMinute;
    /**
     * 签到秒
     */
    TextView tvCheckInSecond;
    /**
     * 签到秒
     */
    TextView tvCheckInSecondEnd;

    /**
     * 我的排名
     */
    TextView tvMyRate;
    /**
     * 小组排名
     */
    TextView tvTeamRate;
    /**
     * 班级排名
     */
    TextView tvClassRate;
    /**
     * 选中draw
     */
    Drawable selectDraw;
    /**
     * 未选中draw
     */
    Drawable normalDraw;
    /**
     * 旁听课堂数据
     */
    AuditClassRoomEntity mAuditClassRoomEntity;
    /**
     * 排名数据
     */
    List<UserScoreEntity> lstUserScore;
    /**
     * 我的排名标题
     */
    TextView tvNameTitle;
    /**
     * 语音题标题
     */
    LinearLayout llVoiceTitle;
    /**
     * 语音题内容
     */
    LinearLayout llVoiceContent;
    /**
     * 互动题对错title
     */
    LinearLayout llQuestionDetailTitle;
    /**
     * 互动题对错情况
     */
    LinearLayout llQuestionDetailList;
    /**
     * 互动题正确率
     */
    RelativeLayout rlQuestionDetailRate;
    /**
     * 排名list
     */
    LinearLayout llRateList;
    /**
     * 排名list标题
     */
    RelativeLayout rlRateListTitle;
    /**
     * 排名内容
     */
    RelativeLayout rlRateContent;
    /**
     * 排名list标题
     */
    LinearLayout llRateTitle;
    /**
     * 课前测正确率标题
     */
    LinearLayout llPreTestTitle;
    /**
     * 课前测正确率内容
     */
    RelativeLayout rlPreTestContent;
    /**
     * 签到时间标题
     */
    LinearLayout llCheckInTitle;
    /**
     * 签到时间
     */
    LinearLayout llCheckInContent;
    /**
     * 滚动布局
     */
    ScrollView svContent;
    /**
     * 是否有数据展示
     */
    boolean hasData = false;
    /**
     * 无数据提示
     */
    TextView tvEmptyHint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audit_class_room);
        EventBus.getDefault().register(this);
        initView();
        initListener();
        initData();
    }

    /**
     * 初始化布局
     */
    private void initView() {
        preTestProgressView = (AuditClassRoomProgressView) findViewById(R.id.arcpview_audit_class_room_pre_test_content);
        questionRecyclerView = (RecyclerView) findViewById(R.id.rv_audit_class_room_interactive_ranking_result_content);
        tvPreTestRate = (TextView) findViewById(R.id.tv_audit_class_room_pre_test_rank);
        questionRateProgressView = (AuditClassRoomProgressView) findViewById(R.id.arcpview_rl_audit_class_room_right_wrong_press_content);
        tvQuestionRate = (TextView) findViewById(R.id.tv_audit_class_room_right_wrong_press_content);
        gvQuestionDetail = (GridView) findViewById(R.id.gv_audit_class_room_right_wrong_question_content);
        gvVoiceQuestionDetail = (GridView) findViewById(R.id.gv_audit_class_room_voice_question_content);
        vLeftLine = findViewById(R.id.v_audit_class_room_left_line);
        tvCheckInHour = (TextView) findViewById(R.id.tv_audit_class_room_check_in_hour);
        tvCheckInMinute = (TextView) findViewById(R.id.tv_audit_class_room_check_in_minute);
        tvCheckInSecond = (TextView) findViewById(R.id.tv_audit_class_room_check_in_second);
        tvCheckInSecondEnd = (TextView) findViewById(R.id.tv_audit_class_room_check_in_second_end);
        tvMyRate = (TextView) findViewById(R.id.tv_audit_class_room_my_rate);
        tvTeamRate = (TextView) findViewById(R.id.tv_audit_class_room_team_rate);
        tvClassRate = (TextView) findViewById(R.id.tv_audit_class_room_class_rate);
        tvNameTitle = (TextView) findViewById(R.id.tv_audit_class_room_interactive_ranking_name_hint);
        llVoiceTitle = (LinearLayout) findViewById(R.id.ll_audit_class_room_voice_question_title);
        llVoiceContent = (LinearLayout) findViewById(R.id.ll_audit_class_room_voice_question_content);
        llQuestionDetailList = (LinearLayout) findViewById(R.id.ll_audit_class_room_right_wrong_question_content);
        rlQuestionDetailRate = (RelativeLayout) findViewById(R.id.rl_audit_class_room_right_wrong_press_content);
        llQuestionDetailTitle = (LinearLayout) findViewById(R.id.ll_audit_class_room_right_wrong_title);
        llRateList = (LinearLayout) findViewById(R.id.ll_audit_class_room_interactive_ranking_result_content);
        rlRateListTitle = (RelativeLayout) findViewById(R.id.rl_audit_class_room_interactive_ranking_result_title);
        rlRateContent = (RelativeLayout) findViewById(R.id.ll_audit_class_room_interactive_ranking_content);
        llRateTitle = (LinearLayout) findViewById(R.id.ll_audit_class_room_interactive_ranking_title);
        llPreTestTitle = (LinearLayout) findViewById(R.id.ll_audit_class_room_pre_test_title);
        rlPreTestContent = (RelativeLayout) findViewById(R.id.rl_audit_class_room_pre_test_content);
        llCheckInTitle = (LinearLayout) findViewById(R.id.ll_audit_class_room_check_in_time_title);
        llCheckInContent = (LinearLayout) findViewById(R.id.ll_audit_class_room_check_in_time_content);
        svContent = (ScrollView) findViewById(R.id.sv_audit_class_room_content);
        tvEmptyHint = (TextView) findViewById(R.id.tv_audit_class_room_no_content_hint);
    }

    /**
     * 初始化监听
     */
    private void initListener() {
        selectDraw = getResources().getDrawable(R.drawable.bg_ranking_selected);
        normalDraw = getResources().getDrawable(R.drawable.bg_ranking_un_selected);
        // 我的排名
        tvMyRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectRateStyle(tvMyRate, mAuditClassRoomEntity.getMineRate(), "我的排名");
                setNormalRateStyle(tvTeamRate, mAuditClassRoomEntity.getTeamRate(), "我的小组");
                setNormalRateStyle(tvClassRate, mAuditClassRoomEntity.getClassRate(), "我的班级");
                lstUserScore = mAuditClassRoomEntity.getMineRateList();
                tvNameTitle.setText("学员");
                fillQuestionRateData();
                XesMobAgent.XesAuditClassRoomRateClick(MobEnumUtil.XES_AUDIT_CLASS_ROOM_MY_RATE);
            }
        });
        // 小组排名
        tvTeamRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNormalRateStyle(tvMyRate, mAuditClassRoomEntity.getMineRate(), "我的排名");
                setSelectRateStyle(tvTeamRate, mAuditClassRoomEntity.getTeamRate(), "我的小组");
                setNormalRateStyle(tvClassRate, mAuditClassRoomEntity.getClassRate(), "我的班级");
                lstUserScore = mAuditClassRoomEntity.getTeamRateList();
                tvNameTitle.setText("小组");
                fillQuestionRateData();
                XesMobAgent.XesAuditClassRoomRateClick(MobEnumUtil.XES_AUDIT_CLASS_ROOM_XES_TEAM_RATE);
            }
        });
        // 班级排名
        tvClassRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvNameTitle.setText("班级");
                setNormalRateStyle(tvMyRate, mAuditClassRoomEntity.getMineRate(), "我的排名");
                setNormalRateStyle(tvTeamRate, mAuditClassRoomEntity.getTeamRate(), "我的小组");
                setSelectRateStyle(tvClassRate, mAuditClassRoomEntity.getClassRate(), "我的班级");
                lstUserScore = mAuditClassRoomEntity.getClassRateList();
                fillQuestionRateData();
                XesMobAgent.XesAuditClassRoomRateClick(MobEnumUtil.XES_AUDIT_CLASS_ROOM_XES_CLASS_RATE);
            }
        });
    }

    private void setSelectRateStyle(TextView tv, String text, String fixText) {
        tv.setBackgroundDrawable(selectDraw);
        String regEx = "[^0-9]";
        String numberText = RegexUtils.getReplaceAll(text, regEx, "");
        SpannableString sp = null;
        if (TextUtils.isEmpty(text)) {
            sp = new SpannableString("暂无" + "\n" + fixText);
        } else {
            sp = new SpannableString(text + "\n" + fixText);
            sp.setSpan(new AbsoluteSizeSpan(25, true), 1, numberText.length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        int whiteColor = getResources().getColor(R.color.white);
        sp.setSpan(new ForegroundColorSpan(whiteColor), 0, sp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(sp);
    }

    private void setNormalRateStyle(TextView tv, String text, String fixText) {
        tv.setBackgroundDrawable(normalDraw);
        String regEx = "[^0-9]";
        String numberText = RegexUtils.getReplaceAll(text, regEx, "");
        int textColor = getResources().getColor(R.color.COLOR_333333);
        if (TextUtils.isEmpty(text)) {
            SpannableString sp = new SpannableString("暂无" + "\n" + fixText);
            sp.setSpan(new ForegroundColorSpan(textColor), 0, sp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv.setText(sp);
        } else {
            SpannableStringBuilder sb = new SpannableStringBuilder();
            SpannableString firstSpan = new SpannableString("第");
            firstSpan.setSpan(new ForegroundColorSpan(textColor), 0, firstSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            SpannableString numberSpan = new SpannableString(numberText);
            numberSpan.setSpan(new AbsoluteSizeSpan(25, true), 0, numberText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            int numberColor = getResources().getColor(R.color.COLOR_FF903D);
            numberSpan.setSpan(new ForegroundColorSpan(numberColor), 0, numberText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            SpannableString endSpan = new SpannableString("名");
            endSpan.setSpan(new ForegroundColorSpan(textColor), 0, firstSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            sb.append(firstSpan).append(numberSpan).append(endSpan).append("\n" + fixText);
            tv.setText(sb);
        }

    }

    /**
     * 初始化数据
     */
    private void initData() {
        mTitleBar = new AppTitleBar(this, "旁听课堂");
        int isArts = getIntent().getIntExtra("isArts", 0);
        mAuditClassRoomBll = new AuditClassRoomBll(AuditClassRoomActivity.this, isArts);
        liveId = getIntent().getStringExtra("liveId");
        stuCouId = getIntent().getStringExtra("stuCouId");
        if (mDataLoadEntity == null) {
            mDataLoadEntity = new DataLoadEntity(R.id.rl_audit_class_room_main_content, DataErrorManager.IMG_TIP_BUTTON)
                    .setWebErrorTip(R.string.web_error_tip_study_center).setDataIsEmptyTip(R.string
                            .data_is_empty_tip_study_center);
        }
        mAuditClassRoomBll.postDataLoadEvent(mDataLoadEntity.beginLoading());
        Intent intent = getIntent();
        isBigLive = intent.getBooleanExtra("isBigLive", false);
        if (isBigLive) {
            int classId = intent.getIntExtra("classId", -1);
            int teamId = intent.getIntExtra("teamId", -1);
            mAuditClassRoomBll.getBigLiveCourseUserScoreDetail(liveId, stuCouId, classId, teamId, auditClassRoomRequestCallBack, mDataLoadEntity);
        } else {
            mAuditClassRoomBll.getLiveCourseUserScoreDetail(liveId, stuCouId, auditClassRoomRequestCallBack, mDataLoadEntity);
        }
    }


    /**
     * 进入旁听课堂
     *
     * @param context
     * @param liveId
     * @param bundle
     */
    public static void intentTo(Context context, String liveId, String stuCouId, Bundle bundle) {
        Intent intent = new Intent(context, AuditClassRoomActivity.class);
        intent.putExtra("liveId", liveId);
        intent.putExtra("stuCouId", stuCouId);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
    }

    /**
     * 数据回调
     */
    AbstractBusinessDataCallBack auditClassRoomRequestCallBack = new AbstractBusinessDataCallBack() {
        @Override
        public void onDataSucess(Object... objData) {
            mAuditClassRoomEntity = (AuditClassRoomEntity) objData[0];
            fillData();
        }

    };

    /**
     * 填充数据
     */
    private void fillData() {
        // title设定
        if (!TextUtils.isEmpty(mAuditClassRoomEntity.getTitle())) {
            mTitleBar.setTitle(mAuditClassRoomEntity.getTitle());
        }
        // 签到时间
        setCheckInTime();
        // 课前测正确率
        setPreTestRate();
        // 排名
        setRateData();
        // 我的排名
        fillQuestionRateData();
        // 互动题正确率
        setQuestionRate();
        // 互动提对错情况
        fillQuestionRateDetailData(mAuditClassRoomEntity.getQuestionDetailList());
        // 语音题得分情况
        fillVoiceQuestionDetailData(mAuditClassRoomEntity.getVoiceQuestionDetailList());
        // 检测数据
        checkData();
    }

    /**
     * 签到时间
     */
    private void setCheckInTime() {
        String time = mAuditClassRoomEntity.getCheckInTime();
        if (!TextUtils.isEmpty(time)) {
            hasData = true;
            llCheckInContent.setVisibility(View.VISIBLE);
            llCheckInTitle.setVisibility(View.VISIBLE);
            String[] times = time.split(":");
            char[] hourTime = times[0].toCharArray();
            if (hourTime.length < 2) {
                tvCheckInHour.setText("0");
                tvCheckInMinute.setText(String.valueOf(hourTime[0]));
            } else {
                tvCheckInHour.setText(String.valueOf(hourTime[0]));
                tvCheckInMinute.setText(String.valueOf(hourTime[1]));
            }
            if (times.length > 1) {
                char[] secondTime = times[1].toCharArray();
                tvCheckInSecondEnd.setText(String.valueOf(secondTime[1]));
                tvCheckInSecond.setText(String.valueOf(secondTime[0]));
            }else {
                tvCheckInSecondEnd.setText("0");
                tvCheckInSecond.setText("0");
            }
        }
    }

    /**
     * 排名情况
     */
    private void setRateData() {
        setSelectRateStyle(tvMyRate, mAuditClassRoomEntity.getMineRate(), "我的排名");
        setNormalRateStyle(tvTeamRate, mAuditClassRoomEntity.getTeamRate(), "我的小组");
        setNormalRateStyle(tvClassRate, mAuditClassRoomEntity.getClassRate(), "我的班级");
        tvNameTitle.setText("学员");
        if (mAuditClassRoomEntity.getMineRateList() != null && mAuditClassRoomEntity.getMineRateList().size() != 0) {
            setSelectRateStyle(tvMyRate, mAuditClassRoomEntity.getMineRate(), "我的排名");
            setNormalRateStyle(tvTeamRate, mAuditClassRoomEntity.getTeamRate(), "我的小组");
            setNormalRateStyle(tvClassRate, mAuditClassRoomEntity.getClassRate(), "我的班级");
            lstUserScore = mAuditClassRoomEntity.getMineRateList();
            tvNameTitle.setText("学员");
        } else if (mAuditClassRoomEntity.getTeamRateList() != null && mAuditClassRoomEntity.getTeamRateList().size() != 0) {
            setNormalRateStyle(tvMyRate, mAuditClassRoomEntity.getMineRate(), "我的排名");
            setSelectRateStyle(tvTeamRate, mAuditClassRoomEntity.getTeamRate(), "我的小组");
            setNormalRateStyle(tvClassRate, mAuditClassRoomEntity.getClassRate(), "我的班级");
            lstUserScore = mAuditClassRoomEntity.getTeamRateList();
            tvNameTitle.setText("小组");
        } else if (mAuditClassRoomEntity.getClassRateList() != null && mAuditClassRoomEntity.getClassRateList().size() != 0) {
            setNormalRateStyle(tvMyRate, mAuditClassRoomEntity.getMineRate(), "我的排名");
            setNormalRateStyle(tvTeamRate, mAuditClassRoomEntity.getTeamRate(), "我的小组");
            setSelectRateStyle(tvClassRate, mAuditClassRoomEntity.getClassRate(), "我的班级");
            lstUserScore = mAuditClassRoomEntity.getClassRateList();
            tvNameTitle.setText("班级");
        }
    }


    /**
     * 课前测正确率
     */
    private void setPreTestRate() {
        // 课前测正确率
        String preTestRate = mAuditClassRoomEntity.getPreTestCorrectRate();
        if (!TextUtils.isEmpty(preTestRate)) {
            rlPreTestContent.setVisibility(View.VISIBLE);
            llPreTestTitle.setVisibility(View.VISIBLE);
            hasData = true;
            int color = getResources().getColor(R.color.COLOR_F2725E);
            preTestProgressView.setProgress(Float.parseFloat(preTestRate.replace("%", "")));
            SpannableString sp = new SpannableString(preTestRate + "\n正确率");
            sp.setSpan(new ForegroundColorSpan(color), 0, preTestRate.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            sp.setSpan(new AbsoluteSizeSpan(25, true), 0, preTestRate.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvPreTestRate.setText(sp);
        }
    }

    /**
     * 互动题正确率
     */
    private void setQuestionRate() {
        // 课前测正确率
        String preTestRate = mAuditClassRoomEntity.getQuestionRateCorrectRate();
        if (!TextUtils.isEmpty(preTestRate)) {
            int color = getResources().getColor(R.color.COLOR_6AC00B);
            rlQuestionDetailRate.setVisibility(View.VISIBLE);
            questionRateProgressView.setProgress(Float.parseFloat(preTestRate.replace("%", "")));
            SpannableString sp = new SpannableString(preTestRate + "\n正确率");
            sp.setSpan(new ForegroundColorSpan(color), 0, preTestRate.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            sp.setSpan(new AbsoluteSizeSpan(25, true), 0, preTestRate.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvQuestionRate.setText(sp);
        }
    }


    /**
     * 互动题排名
     */
    private void fillQuestionRateData() {

        if ((mAuditClassRoomEntity.getMineRateList() != null && mAuditClassRoomEntity.getMineRateList().size() != 0)
                || (mAuditClassRoomEntity.getClassRateList() != null && mAuditClassRoomEntity.getClassRateList().size() != 0)
                || (mAuditClassRoomEntity.getTeamRateList() != null && mAuditClassRoomEntity.getTeamRateList().size() != 0)) {
            rlRateContent.setVisibility(View.VISIBLE);
            llRateTitle.setVisibility(View.VISIBLE);
            llRateList.setVisibility(View.VISIBLE);
            rlRateListTitle.setVisibility(View.VISIBLE);
            hasData = true;
        }
        if (mQuestionRateAdapter == null) {
            questionRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            mQuestionRateAdapter = new QuestionRateAdapter(mContext, lstUserScore);
            questionRecyclerView.setAdapter(mQuestionRateAdapter);
        } else {
            mQuestionRateAdapter.upDataList(lstUserScore);
            mQuestionRateAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 互动题对错情况
     *
     * @param lstUserScore
     */
    private void fillQuestionRateDetailData(List<UserScoreEntity> lstUserScore) {
        // 互动提对错情况
        if (lstUserScore != null && lstUserScore.size() != 0) {
            llQuestionDetailList.setVisibility(View.VISIBLE);
            hasData = true;
            if (mQuestionRateDetailAdapter == null) {
                mQuestionRateDetailAdapter = new QuestionRateDetailAdapter(mContext, lstUserScore,isBigLive);
                gvQuestionDetail.setAdapter(mQuestionRateDetailAdapter);
            } else {
                mQuestionRateDetailAdapter.notifyDataSetChanged();
            }
        }

        if (rlQuestionDetailRate.getVisibility() == View.VISIBLE || llQuestionDetailList.getVisibility() == View.VISIBLE) {
            llQuestionDetailTitle.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 语音题目得分情况
     *
     * @param lstUserScore
     */
    private void fillVoiceQuestionDetailData(List<UserScoreEntity> lstUserScore) {
        // 语音题得分情况
        if (lstUserScore != null) {
            if (mVoiceQuestionDetailAdapter == null) {
                mVoiceQuestionDetailAdapter = new VoiceQuestionDetailAdapter(mContext, lstUserScore);
                gvVoiceQuestionDetail.setAdapter(mVoiceQuestionDetailAdapter);
            } else {
                mVoiceQuestionDetailAdapter.notifyDataSetChanged();
            }
            if (lstUserScore.size() != 0) {
                llVoiceContent.setVisibility(View.VISIBLE);
                llVoiceTitle.setVisibility(View.VISIBLE);
                hasData = true;
            }
        }
    }

    private void checkData() {
        if (hasData) {
            svContent.setVisibility(View.VISIBLE);
            tvEmptyHint.setVisibility(View.GONE);
        } else {
            svContent.setVisibility(View.GONE);
            tvEmptyHint.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
