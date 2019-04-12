package com.xueersi.parentsmeeting.modules.livevideoOldIJK.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tencent.cos.xml.utils.StringUtils;
import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.XesActivity;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.irc.DeviceDetectionBll;
import com.xueersi.parentsmeeting.modules.livevideo.entity.DeviceDetectionEntity;
import com.xueersi.ui.dataload.DataErrorManager;
import com.xueersi.ui.dataload.DataLoadEntity;
import com.xueersi.ui.widget.AppTitleBar;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by ZhangYuansun on 2018/9/4
 * 低端机设备检测页
 */

public class DeviceDetectionActivity extends XesActivity {
    /**
     * 内容布局
     */
    private RelativeLayout rlContent;
    /**
     * 汇总信息布局
     */
    private RelativeLayout rlSummary;
    /**
     * 不符合要求的数量
     */
    private TextView tvSummaryNumber;
    /**
     * 不符合要求的描述信息
     */
    private TextView tvSummaryDescription;
    /**
     * 不符合要求的后果
     */
    private TextView tvSummaryConsequence;
    /**
     * 系统信息布局
     */
    private RelativeLayout rlSystem;
    /**
     * 系统版本
     */
    private TextView tvSystemVersion;
    /**
     * 系统要求
     */
    private TextView tvSystemRequirement;
    /**
     * 内存信息布局
     */
    private RelativeLayout rlMemory;
    /**
     * 内存大小
     */
    private TextView tvMemorySize;
    /**
     * 内存要求
     */
    private TextView tvMemoryRequirement;
    /**
     * 我知道了
     */
    private TextView tvGotit;
    /**
     * 分割线
     */
    private View vLine;
    /**
     * loading猴儿
     */
    DataLoadEntity mDataLoadEntity;
    DeviceDetectionEntity mDeviceDetectionEntity;
    DeviceDetectionBll mDeviceDetectionBll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_livevideo_device_detection);
        UmsAgentManager.umsAgentCustomerBusiness(this, this.getString(com
                        .xueersi
                        .parentsmeeting.base.R
                        .string.app_09902006),
                1);
        EventBus.getDefault().register(this);
        initView();
        initData();
        initListener();
    }

    /**
     * 初始化布局
     */
    private void initView() {
        mTitleBar = new AppTitleBar(this, "设备检测");
        rlContent = findViewById(R.id.rl_study_center_device_detection_content);
        rlSummary = findViewById(R.id.rl_study_center_device_detection_summary);
        tvSummaryNumber = findViewById(R.id.tv_study_center_device_detection_summary_number);
        tvSummaryDescription = findViewById(R.id.tv_study_center_device_detection_summary_description);
        tvSummaryConsequence = findViewById(R.id.tv_study_center_device_detection_summary_consequence);
        rlSystem = findViewById(R.id.rl_study_center_device_detection_system);
        tvSystemVersion = findViewById(R.id.tv_study_center_device_detection_system_version);
        tvSystemRequirement = findViewById(R.id.tv_study_center_device_detection_system_requirement);
        rlMemory = findViewById(R.id.rl_study_center_device_detection_memory);
        tvMemorySize = findViewById(R.id.tv_study_center_device_detection_memory_size);
        tvMemoryRequirement = findViewById(R.id.tv_study_center_device_detection_memory_requirement);
        tvGotit = findViewById(R.id.btn_study_center_device_detection_gotit);
        vLine = findViewById(R.id.v_study_center_device_detection_line);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mDataLoadEntity = new DataLoadEntity(R.id.rl_study_center_device_detection_content, DataErrorManager
                .IMG_TIP_BUTTON).setWebErrorTip(R.string.web_error_tip_study_center).setDataIsEmptyTip(R.string
                .data_is_empty_tip_study_center);
        mDeviceDetectionBll = new DeviceDetectionBll(DeviceDetectionActivity.this);
        mDeviceDetectionBll.getDeviceDetectionInfo(mDataLoadEntity, new AbstractBusinessDataCallBack() {
            @Override
            public void onDataSucess(Object... objData) {
                mDeviceDetectionEntity = (DeviceDetectionEntity) objData[0];
                fillData();
            }
        });
    }

    /**
     * 布局监听
     */
    private void initListener() {
        //我知道了
        tvGotit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * 渲染数据
     */
    private void fillData() {
        rlSummary.setVisibility(View.VISIBLE);
        tvSummaryNumber.setText(mDeviceDetectionEntity.getUnMatchCount() + "");
        tvSummaryConsequence.setText(mDeviceDetectionEntity.getUnMatchDesc());
        if (!StringUtils.isEmpty(mDeviceDetectionEntity.getVersionCurrent())) {
            rlSystem.setVisibility(View.VISIBLE);
            tvSystemVersion.setText(mDeviceDetectionEntity.getVersionCurrent());
            tvSystemRequirement.setText(mDeviceDetectionEntity.getVersionNotice());
        }
        if (!StringUtils.isEmpty(mDeviceDetectionEntity.getMemoryCurrent())) {
            rlMemory.setVisibility(View.VISIBLE);
            tvMemorySize.setText(mDeviceDetectionEntity.getMemoryCurrent());
            tvMemoryRequirement.setText(mDeviceDetectionEntity.getMemoryNotice());
        }
        tvGotit.setVisibility(View.VISIBLE);
        vLine.setVisibility(View.VISIBLE);
    }

    public static void openDeviceDetectionActivity(Activity context) {
        Intent intent = new Intent(context, DeviceDetectionActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
