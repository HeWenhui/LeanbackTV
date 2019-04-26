package com.xueersi.parentsmeeting.modules.livevideoOldIJK.chpk.adapter;

/**
 * Created by lenovo on 2019/1/26.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassChestEntity;

import java.util.List;

public class WinnerAdapter extends RecyclerView.Adapter<WinnerHolder> {

    private List<ClassChestEntity.SubChestEntity> mData;

    public WinnerAdapter(List<ClassChestEntity.SubChestEntity> data) {
        this.mData = data;
    }

    public List<ClassChestEntity.SubChestEntity> getData() {
        return mData;
    }

    public void setData(List<ClassChestEntity.SubChestEntity> data) {
        this.mData = data;
    }

    @Override
    public WinnerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_livevideo_chpk_winner,parent,false);
        return new WinnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(WinnerHolder holder, int position) {
        holder.bindData(mData.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }
}
