package com.xueersi.parentsmeeting.modules.livevideo.business.evendrive.itempager;

import android.view.View;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RankEntity;
import com.xueersi.ui.adapter.AdapterItemInterface;

/**
 * 中学激励系统小组的ListView子ItemPager
 */
public class ItemMiddleScienceGroupsPager implements AdapterItemInterface<RankEntity> {

    TextView tv_livevideo_rank_item_left;
    TextView tv_livevideo_rank_item_mid;
    TextView tv_livevideo_rank_item_right;
    int colorYellow, colorWhite;

    public ItemMiddleScienceGroupsPager(int colorYellow, int colorWhite) {
        this.colorYellow = colorYellow;
        this.colorWhite = colorWhite;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_livevideo_middle_science_evendrive_groups;
    }

    @Override
    public void initViews(View root) {
        tv_livevideo_rank_item_left = (TextView) root.findViewById(R.id.tv_livevideo_rank_item_left);
        tv_livevideo_rank_item_mid = (TextView) root.findViewById(R.id.tv_livevideo_rank_item_mid);
        tv_livevideo_rank_item_right = (TextView) root.findViewById(R.id.tv_livevideo_rank_item_right);
    }

    @Override
    public void bindListener() {

    }

    @Override
    public void updateViews(RankEntity entity, int position, Object objTag) {
        String index = entity.getRank();
        if ("1".equals(index)) {
            tv_livevideo_rank_item_left.setText("");
            tv_livevideo_rank_item_left.setBackgroundResource(R.drawable.bg_livevideo_rank_no1);
//            if (isSmallEnglish) {
//                tv_livevideo_rank_item_left.setBackgroundResource(R.drawable.bg_livevideo_rank_no1);
//            } else {
//                tv_livevideo_rank_item_left.setBackgroundResource(R.drawable.bg_livevideo_small_english_rank_no1);
//            }
        } else if ("2".equals(index)) {
            tv_livevideo_rank_item_left.setText("");
            tv_livevideo_rank_item_left.setBackgroundResource(R.drawable.bg_livevideo_rank_no2);
//            if (isSmallEnglish) {
//                tv_livevideo_rank_item_left.setBackgroundResource(R.drawable.bg_livevideo_rank_no2);
//            } else {
//                tv_livevideo_rank_item_left.setBackgroundResource(R.drawable.bg_livevideo_small_english_rank_no2);
//            }
        } else if ("3".equals(index)) {
            tv_livevideo_rank_item_left.setBackgroundResource(R.drawable.bg_livevideo_rank_no3);
            tv_livevideo_rank_item_left.setText("");
//            if (!isSmallEnglish) {
//                tv_livevideo_rank_item_left.setBackgroundResource(R.drawable.bg_livevideo_rank_no3);
//            } else {
//                tv_livevideo_rank_item_left.setBackgroundResource(R.drawable.bg_livevideo_small_english_rank_no3);
//            }
        } else {
            tv_livevideo_rank_item_left.setBackgroundDrawable(null);
            tv_livevideo_rank_item_left.setText(String.valueOf(entity.getRank()));
        }
        tv_livevideo_rank_item_mid.setText(entity.getName());
        tv_livevideo_rank_item_right.setText(entity.getRate());
        if (entity.isMe()) {
            tv_livevideo_rank_item_left.setTextColor(colorYellow);
            tv_livevideo_rank_item_mid.setTextColor(colorYellow);
            tv_livevideo_rank_item_right.setTextColor(colorYellow);
        } else {
            tv_livevideo_rank_item_left.setTextColor(colorWhite);
            tv_livevideo_rank_item_mid.setTextColor(colorWhite);
            tv_livevideo_rank_item_right.setTextColor(colorWhite);
        }
    }
}
