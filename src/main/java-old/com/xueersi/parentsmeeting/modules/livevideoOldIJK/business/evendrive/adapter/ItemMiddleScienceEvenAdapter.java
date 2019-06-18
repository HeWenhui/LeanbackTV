package com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.evendrive.adapter;

import android.annotation.SuppressLint;
import android.content.Context;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.evendrive.EvenDriveEntity;

import java.util.List;

public class ItemMiddleScienceEvenAdapter extends MiddleScienceAdapter<EvenDriveEntity.OtherEntity> {

    public ItemMiddleScienceEvenAdapter(Context mContext, List list) {
        super(mContext, list);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void itemSetting(ViewHolder viewHolder, int position) {
        EvenDriveEntity.OtherEntity entity = mList.get(position);
        String index = String.valueOf(entity.getRanking());
        if ("1".equals(index)) {
            viewHolder.rankLeft.setText("");
            viewHolder.rankLeft.setBackgroundResource(R.drawable.bg_livevideo_rank_no1);
        } else if ("2".equals(index)) {
            viewHolder.rankLeft.setText("");
            viewHolder.rankLeft.setBackgroundResource(R.drawable.bg_livevideo_rank_no2);
        } else if ("3".equals(index)) {
            viewHolder.rankLeft.setBackgroundResource(R.drawable.bg_livevideo_rank_no3);
            viewHolder.rankLeft.setText("");
        } else {
            viewHolder.rankLeft.setBackgroundDrawable(null);
            viewHolder.rankLeft.setText("" + entity.getRanking());
        }
        viewHolder.rankMiddleLeft.setText(entity.getName());
        viewHolder.rankMiddleRight.setText(entity.getEvenPairNum() + "");
        viewHolder.rankRight.setText(String.valueOf(entity.getThumbsUpNum()) + "");
//        if (entity.isMe()) {
//            viewHolder.rankLeft.setTextColor(colorYellow);
//            viewHolder.rankMiddleLeft.setTextColor(colorYellow);
//            viewHolder.rankMiddleRight.setTextColor(colorYellow);
//        } else {
        viewHolder.rankLeft.setTextColor(colorWhite);
        viewHolder.rankMiddleLeft.setTextColor(colorWhite);
        viewHolder.rankMiddleRight.setTextColor(colorWhite);
        viewHolder.rankRight.setTextColor(colorWhite);
    }
}
