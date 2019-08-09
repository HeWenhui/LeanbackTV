package com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.evendrive.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.evendrive.itempager.ItemMiddleSciencePager;

import org.json.JSONObject;

import java.util.List;

public abstract class MiddleScienceAdapter<T> extends BaseAdapter {

    protected int colorWhite = R.color.white;

    protected List<T> mList;

    protected Context mContext;

    public MiddleScienceAdapter(Context mContext, List<T> list) {
        this.mContext = mContext;
        this.mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        //获取数据集中与指定索引对应的数据项
        return position;
    }

    @Override
    public long getItemId(int position) {
        //获取在列表中与指定索引对应的行id
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_livevideo_middle_science_even_drive_listview_item, null);
            viewHolder.rankLeft = convertView.findViewById(R.id.tv_livevideo_rank_item_left);
            viewHolder.rankMiddleLeft = convertView.findViewById(R.id.tv_livevideo_rank_item_mid_left);
            viewHolder.rankMiddleRight = convertView.findViewById(R.id.tv_livevideo_rank_item_mid_right);
            viewHolder.rankRight = convertView.findViewById(R.id.tv_livevideo_rank_item_right);
            viewHolder.ivRedHeard = convertView.findViewById(R.id.iv_livevideo_rank_item_right_leftimg);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        itemSetting(viewHolder, position);

        return convertView;
    }

    class ViewHolder {
        public TextView rankLeft;//tv_livevideo_rank_item_left;

        TextView rankMiddleLeft;//tv_livevideo_rank_item_mid_left

        TextView rankMiddleRight;//tv_livevideo_rank_item_mid_right

        TextView rankRight;//tv_livevideo_rank_item_right

        ImageView ivRedHeard;

    }

    protected abstract void itemSetting(ViewHolder viewHolder, int position);


    /**
     * 点赞发送消息
     * wiki文档 ：http://wiki.xesv5.com/pages/viewpage.action?pageId=16827379
     */
    public interface INotice {
        /** 发送Notice消息 */
        void sendNotice(JSONObject jsonObject, String targetName);

        /**
         * 发送点赞消息
         *
         * @param listFlag  榜单标识（1：排行榜 2：连对榜）
         * @param bePraised 被点赞的ID
         */
        void sendLike(int listFlag, String bePraised);

    }

    private ItemMiddleSciencePager.INotice iNotice;

    public ItemMiddleSciencePager.INotice getiNotice() {
        return iNotice;
    }

    public void setiNotice(ItemMiddleSciencePager.INotice iNotice) {
        this.iNotice = iNotice;
    }
}
