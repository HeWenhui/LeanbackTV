package com.xueersi.parentsmeeting.modules.livevideoOldIJK.activity.item;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PsState;

import java.util.List;

/**
 * Created by David on 2018/8/10.
 */

public class VoteAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater listContainer;
    public  List<PsState> mSystem;

    public VoteAdapter(Context context, List<PsState> mSystem) {
        this.mContext = context;
        this.mSystem = mSystem;
        listContainer = LayoutInflater.from(mContext);
    }
    @Override
    public int getCount() {
        return mSystem.size();
    }

    @Override
    public Object getItem(int i) {
        return mSystem.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = listContainer.inflate(R.layout.item_livevideo_vote_ps_selects, null);
            holder.item = (ImageView)convertView.findViewById(R.id.btn_livevideo_vote_ps_item);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }
//        holder.item.setText((mSystem.get(position).getRealposition())+"");
        if(mSystem.get(position).isState()){
            holder.item.setBackgroundResource(mSystem.get(position).getResId());
        }else {
            holder.item.setBackgroundResource(mSystem.get(position).getResId());
            holder.item.setAlpha(0.2f);
        }
        return convertView;
    }

    private class ViewHolder{
        public ImageView item;
    }
}
