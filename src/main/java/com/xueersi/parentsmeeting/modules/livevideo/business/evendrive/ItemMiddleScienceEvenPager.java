package com.xueersi.parentsmeeting.modules.livevideo.business.evendrive;

import android.content.Context;
import android.view.View;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;

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
    private long lastTime = -1;

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
                    //可以点赞
                    ivRedHeard.setImageDrawable(mContext.getResources().getDrawable(R.drawable.livevideo_list_redheart_icon_normal));
                    rankRight.setText(String.valueOf(entity.getThumbsUpNum() + 1));
                    long nowTime = System.currentTimeMillis();
                    //15s内点赞需要给用户发送相关消息
                    if (nowTime - lastTime <= 1000 * 15 && getiNotice() != null) {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("type", XESCODE.EvenDrive.PRAISE_PRIVATE_STUDENT);
                            jsonObject.put("from", entity.getStuId());
                            jsonObject.put("stuName", entity.getName());
                            jsonObject.put("approvalType", 2);
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

    @Override
    public void updateViews(EvenDriveEntity.OtherEntity entity, int position, Object objTag) {

    }

}
