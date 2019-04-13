package com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.evendrive.itempager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.business.evendrive.EvenDriveEntity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 中学激励系统连对的ListView的子项
 */
public class ItemMiddleScienceEvenPager extends ItemMiddleSciencePager<EvenDriveEntity.OtherEntity> {

    private int colorWhite = R.color.white;

    public ItemMiddleScienceEvenPager(Context context) {
        this.mContext = context;
    }

    /** 试题结束的时间 */
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
                    getiNotice().sendLike(2, entity.getStuId(), new HttpCallBack() {
                        @Override
                        public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                            //可以点赞
                            ivRedHeard.setImageDrawable(mContext.getResources().getDrawable(R.drawable.livevideo_list_redheart_icon_normal));
                            entity.setThumbsUpNum(entity.getThumbsUpNum() + 1);
                            rankRight.setText(String.valueOf(entity.getThumbsUpNum()));
                            entity.setIsThumbsUp(0);
                            //如果是给自己点赞，自己的两处显示信息都需要加一
                            //15s内点赞需要给用户发送相关消息
                            long nowTime = System.currentTimeMillis();
                            if (entity.getStuId() != null && entity.getStuId().equals(myStuId)) {
                                if (iClickSelf != null) {
                                    iClickSelf.clickSelf();
                                }
                            } else if (getiNotice() != null) {
                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("type", String.valueOf(XESCODE.EvenDrive.PRAISE_PRIVATE_STUDENT));
//                            jsonObject.put("from", entity.getStuId());
//                            jsonObject.put("stuName", entity.getName());
                                    jsonObject.put("approvalType", 2);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                logger.i("发送点赞IRC消息:" + entity.getName() + " " + jsonObject.toString());
                                getiNotice().sendNotice(jsonObject, entity.getStuId());
                            } else {
                                logger.i("发送IRC点赞消息失败");
                            }
                        }
                    });
                }
//                JSONObject jsonObject = new JSONObject();
//                try {
//                    jsonObject.put("type", String.valueOf(XESCODE.EvenDrive.PRAISE_PRIVATE_STUDENT));
////                            jsonObject.put("from", entity.getStuId());
////                            jsonObject.put("stuName", entity.getName());
//                    jsonObject.put("approvalType", 2);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                logger.i("发送点赞IRC消息:" + entity.getName() + " " + jsonObject.toString());
//
//                getiNotice().sendNotice(jsonObject, entity.getStuId());
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void updateViews(EvenDriveEntity.OtherEntity entity, int position, Object objTag) {
        this.entity = entity;
        if (position == 0 && viewRoot != null) {
            viewRoot.setBackgroundDrawable(mContext.getResources().getDrawable(R.color.COLOR_1AFFFFFF));
        }
        colorWhite = mContext.getResources().getColor(R.color.white);
        String index = String.valueOf(entity.getRanking());
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
            rankLeft.setText("" + entity.getRanking());
        }
        rankMiddleLeft.setText(entity.getName());
        rankMiddleRight.setText(entity.getEvenPairNum() + "");
        rankRight.setText(String.valueOf(entity.getThumbsUpNum()) + "");
        if (entity.getIsThumbsUp() == 1) {
            ivRedHeard.setImageDrawable(mContext.getResources().getDrawable(R.drawable.livevideo_list_heart_icon_normal));
        } else {
            ivRedHeard.setImageDrawable(mContext.getResources().getDrawable(R.drawable.livevideo_list_redheart_icon_normal));
        }
//        if (entity.isMe()) {
//            rankLeft.setTextColor(colorYellow);
//            rankMiddleLeft.setTextColor(colorYellow);
//            rankMiddleRight.setTextColor(colorYellow);
//        } else {
        rankLeft.setTextColor(colorWhite);
        rankMiddleLeft.setTextColor(colorWhite);
        rankMiddleRight.setTextColor(colorWhite);
        rankRight.setTextColor(colorWhite);
    }
}
