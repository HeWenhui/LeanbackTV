package com.xueersi.parentsmeeting.modules.livevideoOldIJK.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.xueersi.parentsmeeting.modules.livevideo.R;

/**
 * 半身直播 热词item holder
 *
 * @author chenkun
 * @version 1.0, 2019/4/18 下午5:51
 */

public class HalfBodyHotWordHolder extends RecyclerView.ViewHolder {

    private  ImageView ivHotWord;
    ItemClickListener mItemclickListener;
    public HalfBodyHotWordHolder(View itemView,ItemClickListener clickListener) {
        super(itemView);
        ivHotWord = (ImageView) itemView.findViewById(R.id.iv_livevideo_halbody_hotword);
        mItemclickListener = clickListener;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mItemclickListener != null){
                    mItemclickListener.onItemClick(v,HalfBodyHotWordHolder.this.getAdapterPosition());
                }
            }
        });
    }

    public void bindData(int resId){
        ivHotWord.setImageResource(resId);
    }


    public static interface ItemClickListener{
        /**
         * item 点击回调
         * @param view
         * @param postion
         */
        void onItemClick(View view,int postion);
    }

}
