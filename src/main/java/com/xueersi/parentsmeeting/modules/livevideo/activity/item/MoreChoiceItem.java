package com.xueersi.parentsmeeting.modules.livevideo.activity.item;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.MoreChoice;
import com.xueersi.xesalib.adapter.AdapterItemInterface;

/**
 * Created by Administrator on 2018/4/4.
 */

public class MoreChoiceItem implements AdapterItemInterface<MoreChoice.Choice> {
    private final Context mContext;
    private MoreChoice.Choice mDetail;
    private MoreChoice mEntity;
    private TextView mCourseName;
    private TextView mLimitNum;
    private Button mToApply;

    public MoreChoiceItem(Context context, MoreChoice entity) {
       this.mContext = context;
       this.mEntity = entity;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_livevideo_morechoice;
    }

    @Override
    public void initViews(View view) {
        mCourseName = (TextView)view.findViewById(R.id.tv_course_name);
        mLimitNum = (TextView)view.findViewById(R.id.tv_limit_num);
        mToApply = (Button)view.findViewById(R.id.bt_to_apply);
    }

    @Override
    public void bindListener() {

    }

    @Override
    public void updateViews(MoreChoice.Choice choice, int i, Object o) {
        if(choice == null)
            return;
        mDetail = choice;
        mCourseName.setText(mDetail.getSaleName());
        mLimitNum.setText(Html.fromHtml("<font color='#999999'>剩余名额</font>"+ "<font color='#F13232'>" +"  " +mDetail.getLimit()+ "</font>"));
        if(mDetail.getIsLearn() > 0){
            mToApply.setText("已报名");
            mToApply.setBackgroundResource(R.drawable.bg_applyed);
        }else{
            mToApply.setText("立即报名");
            mToApply.setBackgroundResource(R.drawable.bg_apply);
        }
        if(mDetail.getLimit() == 0){
            mToApply.setBackgroundResource(R.drawable.cannotapply);
        }
    }
}
