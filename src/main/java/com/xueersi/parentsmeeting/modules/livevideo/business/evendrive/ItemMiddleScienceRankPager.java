package com.xueersi.parentsmeeting.modules.livevideo.business.evendrive;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RankEntity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 中学激励系统子项排行榜页面
 */
public class ItemMiddleScienceRankPager extends ItemMiddleSciencePager<RankEntity> {

    private RankEntity entity;
    private int colorWhite = R.color.white;

    public ItemMiddleScienceRankPager(Context context) {
        this.mContext = context;
    }

    /** 试题结束的时间 */
    private long lastTime = -1;

    @Override
    public void initViews(View root) {
        super.initViews(root);
        ivRedHeard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (entity.getIsThumbsUp() == 1) {
                    //可以点赞
                    ivRedHeard.setImageDrawable(mContext.getResources().getDrawable(R.drawable.livevideo_list_redheart_icon_normal));
                    rankRight.setText(String.valueOf(entity.getThumbsUpNum() + 1));
                    long nowTime = System.currentTimeMillis();
                    //15s内点赞需要给用户发送相关消息
                    if (nowTime - lastTime <= 1000 * 15 && getiNotice() != null) {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("type", XESCODE.EvenDrive.PRAISE_PRIVATE_STUDENT);
                            jsonObject.put("from", entity.getId());
                            jsonObject.put("stuName", entity.getName());
                            jsonObject.put("approvalType", 1);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        getiNotice().sendNotice(jsonObject);
                    }
                } else {
                    //已经点过赞
//                    ivRedHeard.setImageDrawable(mContext.getResources().getDrawable(R.drawable.livevideo_list_redheart_icon_normal));

                }
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void updateViews(RankEntity entity, int position, Object objTag) {
        this.entity = entity;
        String index = entity.getRank();
        if ("1".equals(index)) {
            rankLeft.setText("");
            rankLeft.setBackgroundResource(R.drawable.bg_livevideo_rank_no1);
        } else if ("2".equals(index)) {
            rankLeft.setText("");
            rankLeft.setBackgroundResource(R.drawable.bg_livevideo_rank_no2);
        } else if ("3".equals(index)) {
            rankLeft.setBackgroundResource(R.drawable.bg_livevideo_rank_no3);
            rankLeft.setText("");
        } else {
            rankLeft.setBackgroundDrawable(null);
            rankLeft.setText("" + entity.getRank());
        }
        rankMiddleLeft.setText(entity.getName());
        rankMiddleRight.setText(entity.getRate());
        rankRight.setText(String.valueOf(entity.getThumbsUpNum()));
//        if (entity.isMe()) {
//            rankLeft.setTextColor(colorYellow);
//            rankMiddleLeft.setTextColor(colorYellow);
//            rankMiddleRight.setTextColor(colorYellow);
//        } else {
        rankLeft.setTextColor(colorWhite);
        rankMiddleLeft.setTextColor(colorWhite);
        rankMiddleRight.setTextColor(colorWhite);
        rankRight.setTextColor(colorWhite);
//        }
    }

}
