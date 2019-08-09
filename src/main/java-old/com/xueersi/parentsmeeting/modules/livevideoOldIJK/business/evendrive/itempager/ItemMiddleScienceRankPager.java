package com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.evendrive.itempager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RankEntity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 中学激励系统子项排行榜页面
 */
public class ItemMiddleScienceRankPager extends ItemMiddleSciencePager<RankEntity> {

    private int colorWhite = R.color.white;

    public ItemMiddleScienceRankPager(Context context) {
        this.mContext = context;
    }

    @Override
    public void initViews(View root) {
        super.initViews(root);
    }

    @Override
    public void bindListener() {
        ivRedHeard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (entity.getIsThumbsUp() == 1) {
                    getiNotice().sendLike(1, entity.getId(), new HttpCallBack() {
                        @Override
                        public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                            //可以点赞
                            ivRedHeard.setImageDrawable(mContext.getResources().getDrawable(R.drawable.livevideo_list_redheart_icon_normal));
                            entity.setThumbsUpNum(entity.getThumbsUpNum() + 1);
                            rankRight.setText(String.valueOf(entity.getThumbsUpNum()));
                            entity.setIsThumbsUp(0);
                            long nowTime = System.currentTimeMillis();
                            //15s内点赞需要给用户发送相关消息

                            //如果是给自己点赞
                            if (entity.getId() != null && entity.getId().equals(myStuId)) {
                                if (iClickSelf != null) {
                                    iClickSelf.clickSelf();
                                }
                            } else if (getiNotice() != null) {
                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("type", String.valueOf(XESCODE.EvenDrive.PRAISE_PRIVATE_STUDENT));
                                    jsonObject.put("approvalType", 1);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                getiNotice().sendNotice(jsonObject, entity.getId());
                            }
                        }
                    });
                }
//
//                JSONObject jsonObject = new JSONObject();
//                try {
//                    jsonObject.put("type", XESCODE.EvenDrive.PRAISE_PRIVATE_STUDENT);
//                    jsonObject.put("approvalType", 1);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                getiNotice().sendNotice(jsonObject, entity.getName());

            }
        });
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void updateViews(RankEntity entity, int position, Object objTag) {
        this.entity = entity;

        if (position == 0 && viewRoot != null) {
//            viewRoot.setBackgroundColor(R.color.COLOR_E6FFFFFF);
            viewRoot.setBackgroundDrawable(mContext.getResources().getDrawable(R.color.COLOR_1AFFFFFF));

        }
        colorWhite = mContext.getResources().getColor(R.color.white);
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
        if (entity.getIsThumbsUp() == 1) {
            ivRedHeard.setImageDrawable(mContext.getResources().getDrawable(R.drawable.livevideo_list_heart_icon_normal));
        } else {
            ivRedHeard.setImageDrawable(mContext.getResources().getDrawable(R.drawable.livevideo_list_redheart_icon_normal));
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
