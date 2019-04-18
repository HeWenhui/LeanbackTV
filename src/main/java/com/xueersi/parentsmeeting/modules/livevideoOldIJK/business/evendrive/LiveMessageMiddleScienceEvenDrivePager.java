package com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.evendrive;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;

public class LiveMessageMiddleScienceEvenDrivePager extends BasePager {
    private TextView tvRightNow;

    private TextView tvRightMaxNum;

    public LiveMessageMiddleScienceEvenDrivePager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.item_livemessage_middle_science_even_drive_layout, null);
        tvRightNow = view.findViewById(R.id.tv_livevideo_livemessage_middle_even_right_now);
        tvRightMaxNum = view.findViewById(R.id.tv_livevideo_livemessage_middle_even_right_max);
        return view;
    }

    @Override
    public void initData() {

    }

    public void updateData(int nowNum, int maxNum) {
        tvRightMaxNum.setText("最高连对x" + maxNum);
        tvRightNow.setText("最高连对x" + nowNum);
    }
}
