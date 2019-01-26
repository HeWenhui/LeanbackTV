package com.xueersi.parentsmeeting.modules.livevideo.chpk.adapter;

/**
 * Created by lenovo on 2019/1/26.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassChestEntity;

import java.util.List;

public class WinnerAdapter extends RecyclerView.Adapter<WinnerHolder> {

    private List<ClassChestEntity.SubChestEntity> mData;
    private boolean isAiPatner;

    public WinnerAdapter(List<ClassChestEntity.SubChestEntity> data, boolean isAiPatner) {
        this.mData = data;
        this.isAiPatner = isAiPatner;
    }

    public List<ClassChestEntity.SubChestEntity> getData() {
        return mData;
    }

    public void setData(List<ClassChestEntity.SubChestEntity> data) {
        this.mData = data;
    }

    @Override
    public WinnerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = isAiPatner ? R.layout.item_teampk_open_box_aipatnerwinner : R.layout.item_teampk_open_box_winner;
        return new WinnerHolder(LayoutInflater.from(parent.getContext()).inflate(layout, parent, false));
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
