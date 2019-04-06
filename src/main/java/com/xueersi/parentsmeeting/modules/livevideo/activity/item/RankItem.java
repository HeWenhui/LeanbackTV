package com.xueersi.parentsmeeting.modules.livevideo.activity.item;

import android.view.View;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RankEntity;
import com.xueersi.ui.adapter.AdapterItemInterface;

/**
 * Created by lyqai on 2017/9/21.
 */
public class RankItem implements AdapterItemInterface<RankEntity> {
    TextView tv_livevideo_rank_item_left;
    TextView tv_livevideo_rank_item_mid;
    TextView tv_livevideo_rank_item_right;
    int colorYellow, colorWhite;
    //小英，第一第二第三图标不一样
    boolean isSmallEnglish;

    public RankItem(int colorYellow, int colorWhite, boolean isSmallEnglish) {
        this.colorYellow = colorYellow;
        this.colorWhite = colorWhite;
        this.isSmallEnglish = isSmallEnglish;
    }

    @Override
    public int getLayoutResId() {
//        if (isSmallEnglish) {
//            return R.layout.item_livevodeo_rank_smalleng;
//        }
        return isSmallEnglish ? R.layout.item_livevodeo_rank_smalleng : R.layout.item_livevodeo_rank;
//        return R.layout.item_livevodeo_rank;

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
            tv_livevideo_rank_item_left.setBackgroundResource(isSmallEnglish ? R.drawable
                    .bg_livevideo_small_english_rank_no1 : R.drawable.bg_livevideo_rank_no1);
//            if (isSmallEnglish) {
//                tv_livevideo_rank_item_left.setBackgroundResource(R.drawable.bg_livevideo_rank_no1);
//            } else {
//                tv_livevideo_rank_item_left.setBackgroundResource(R.drawable.bg_livevideo_small_english_rank_no1);
//            }
        } else if ("2".equals(index)) {
            tv_livevideo_rank_item_left.setText("");
            tv_livevideo_rank_item_left.setBackgroundResource(isSmallEnglish ? R.drawable
                    .bg_livevideo_small_english_rank_no2 : R.drawable.bg_livevideo_rank_no2);
//            if (isSmallEnglish) {
//                tv_livevideo_rank_item_left.setBackgroundResource(R.drawable.bg_livevideo_rank_no2);
//            } else {
//                tv_livevideo_rank_item_left.setBackgroundResource(R.drawable.bg_livevideo_small_english_rank_no2);
//            }
        } else if ("3".equals(index)) {
            tv_livevideo_rank_item_left.setBackgroundResource(isSmallEnglish ? R.drawable
                    .bg_livevideo_small_english_rank_no3 :
                    R.drawable.bg_livevideo_rank_no3);
            tv_livevideo_rank_item_left.setText("");
//            if (!isSmallEnglish) {
//                tv_livevideo_rank_item_left.setBackgroundResource(R.drawable.bg_livevideo_rank_no3);
//            } else {
//                tv_livevideo_rank_item_left.setBackgroundResource(R.drawable.bg_livevideo_small_english_rank_no3);
//            }
        } else {
            tv_livevideo_rank_item_left.setBackgroundDrawable(null);
            tv_livevideo_rank_item_left.setText("" + entity.getRank());
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
