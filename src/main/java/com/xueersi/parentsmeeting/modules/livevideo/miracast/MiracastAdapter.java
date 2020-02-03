package com.xueersi.parentsmeeting.modules.livevideo.miracast;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hpplay.sdk.source.browse.api.LelinkServiceInfo;
import com.xueersi.parentsmeeting.modules.livevideo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by: WangDe on 2019/2/25
 */
public class MiracastAdapter extends RecyclerView.Adapter<MiracastAdapter.DevViewHolder> {
    private LayoutInflater mInflater;
    List<LelinkServiceInfo> devList;
    private IOnItemClickListener mItemClickListener;

    public MiracastAdapter(Context context){
        mInflater = LayoutInflater.from(context);
        devList = new ArrayList<>();
    }

    public void updateDatas(List<LelinkServiceInfo> infos) {
        if (null != infos) {
            devList.clear();
            devList.addAll(infos);
            notifyDataSetChanged();
        }
    }
    public void setOnItemClickListener(IOnItemClickListener l) {
        this.mItemClickListener = l;
    }
    @Override
    public DevViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.item_miracast_name, null);
        return new DevViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DevViewHolder holder, int position) {
        LelinkServiceInfo info = devList.get(position);
        if (null == info) {
            return;
        }
        String item = info.getName() + " uid:" + info.getUid() + " types:" + info.getTypes();
        holder.tvName.setText(item);
        holder.tvName.setTag(R.id.id_position, position);
        holder.tvName.setTag(R.id.id_info, info);
        holder.tvName.setOnClickListener(mOnItemClickListener);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return null == devList ? 0 : devList.size();
    }

    class DevViewHolder extends RecyclerView.ViewHolder{
        TextView tvName;

        public DevViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_dev_name);
        }
    }
    private View.OnClickListener mOnItemClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            int position = (int) v.getTag(R.id.id_position);
            LelinkServiceInfo info = (LelinkServiceInfo) v.getTag(R.id.id_info);
            if (null != mItemClickListener) {
                mItemClickListener.onClick(position, info);
            }
        }

    };
}
