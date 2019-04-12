package com.xueersi.parentsmeeting.modules.livevideoOldIJK.activity.item;

import android.view.View;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RankEntity;
import com.xueersi.parentsmeeting.widget.FangZhengCuYuanTextView;
import com.xueersi.ui.adapter.AdapterItemInterface;

/**
 * 小学语文排行榜
 */
public class SmallChineseRankItem implements AdapterItemInterface<RankEntity> {
    FangZhengCuYuanTextView tv_livevideo_rank_item_left;
    FangZhengCuYuanTextView tv_livevideo_rank_item_mid;
    FangZhengCuYuanTextView tv_livevideo_rank_item_right;
    int colorYellow, colorWhite;


    public SmallChineseRankItem(int colorYellow, int colorWhite) {
        this.colorYellow = colorYellow;
        this.colorWhite = colorWhite;

    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_livevideo_small_chinese_rank_item;
    }

    @Override
    public void initViews(View root) {
        tv_livevideo_rank_item_left = root.findViewById(R.id.tv_livevideo_rank_item_left);
        tv_livevideo_rank_item_mid = root.findViewById(R.id.tv_livevideo_rank_item_mid);
        tv_livevideo_rank_item_right = root.findViewById(R.id.tv_livevideo_rank_item_right);
    }

    @Override
    public void bindListener() {

    }

    @Override
    public void updateViews(RankEntity entity, int position, Object objTag) {
        String index = entity.getRank();
        if ("1".equals(index)) {
            tv_livevideo_rank_item_left.setText("");
            tv_livevideo_rank_item_left.setBackgroundResource(R.drawable.bg_livevideo_small_chinese_rank_no1);
        } else if ("2".equals(index)) {
            tv_livevideo_rank_item_left.setText("");
            tv_livevideo_rank_item_left.setBackgroundResource(R.drawable.bg_livevideo_small_chinese_rank_no2);
        } else if ("3".equals(index)) {
            tv_livevideo_rank_item_left.setBackgroundResource(R.drawable.bg_livevideo_small_chinese_rank_no3);
            tv_livevideo_rank_item_left.setText("");
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
