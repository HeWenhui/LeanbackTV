package com.xueersi.parentsmeeting.modules.livevideoOldIJK.activity.item;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.common.event.MiniEvent;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.MoreChoice;
import com.xueersi.ui.adapter.AdapterItemInterface;

import org.greenrobot.eventbus.EventBus;

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
    private ImageView mUnapplyed;
    private Activity mActivity;

    public MoreChoiceItem(Context context, MoreChoice entity) {
        mActivity = (Activity)context;
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
        mUnapplyed = (ImageView)view.findViewById(R.id.iv_unapply);
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
            mToApply.setTextColor(Color.parseColor("#999999"));
            mToApply.setBackgroundResource(R.drawable.bg_applyed);
        }else{
            mToApply.setText("立即报名");
            mToApply.setTextColor(Color.parseColor("#F13232"));
            mToApply.setBackgroundResource(R.drawable.bg_apply);
        }
        if(mDetail.getLimit() == 0){
            mToApply.setVisibility(View.GONE);
            mUnapplyed.setVisibility(View.VISIBLE);
        }else{
            mToApply.setVisibility(View.VISIBLE);
            mUnapplyed.setVisibility(View.GONE);
        }

        mToApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mDetail.getLimit() > 0 && mDetail.getIsLearn() == 0){
                    EventBus.getDefault().post(new MiniEvent("Order",mDetail.getCourseId(),mDetail.getClassId(),mDetail.getAdId()));
                }
            }
        });
    }
}
