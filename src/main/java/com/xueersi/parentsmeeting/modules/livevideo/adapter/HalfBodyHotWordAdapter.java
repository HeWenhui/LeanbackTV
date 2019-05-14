package com.xueersi.parentsmeeting.modules.livevideo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.xueersi.parentsmeeting.modules.livevideo.R;

/**
 * 半身直播 热词
 * @author chenkun
 * @version 1.0, 2019/4/18 下午5:49
 */

public class HalfBodyHotWordAdapter extends RecyclerView.Adapter<HalfBodyHotWordHolder>  {

    private  int[] mData;

    HalfBodyHotWordHolder.ItemClickListener mItemClickListener;

    /**
     *
     * @param data      图片资源数组
     * @param listener  item 点击监听
     */
    public HalfBodyHotWordAdapter(int [] data, HalfBodyHotWordHolder.ItemClickListener listener){
        mData = data;
        mItemClickListener = listener;
    }

    @Override
    public HalfBodyHotWordHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HalfBodyHotWordHolder(View.inflate(parent.getContext(), R.layout.item_livevideo_halfbody_hotword, null),
                mItemClickListener);
    }

    @Override
    public void onBindViewHolder(HalfBodyHotWordHolder holder, int position) {
        ((HalfBodyHotWordHolder)holder).bindData(mData[position]);
    }

    @Override
    public int getItemCount() {
        return mData != null?mData.length:0;
    }
}
