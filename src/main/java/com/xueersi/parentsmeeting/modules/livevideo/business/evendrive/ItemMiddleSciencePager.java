package com.xueersi.parentsmeeting.modules.livevideo.business.evendrive;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.ui.adapter.AdapterItemInterface;

import org.json.JSONObject;

public abstract class ItemMiddleSciencePager<T> implements AdapterItemInterface<T> {
    /**  */
    protected TextView rankLeft;//tv_livevideo_rank_item_left;

    protected TextView rankMiddleLeft;//tv_livevideo_rank_item_mid_left

    protected TextView rankMiddleRight;//tv_livevideo_rank_item_mid_right

    protected TextView rankRight;//tv_livevideo_rank_item_right

    protected ImageView ivRedHeard;

    protected T entity;

    protected Context mContext;

    @Override
    public int getLayoutResId() {
        return R.layout.item_livevideo_middle_science_even_drive_listview_item;
    }

    @Override
    public void initViews(View root) {
        rankLeft = root.findViewById(R.id.tv_livevideo_rank_item_left);
        rankMiddleLeft = root.findViewById(R.id.tv_livevideo_rank_item_mid_left);
        rankMiddleRight = root.findViewById(R.id.tv_livevideo_rank_item_mid_right);
        rankRight = root.findViewById(R.id.tv_livevideo_rank_item_right);
        ivRedHeard = root.findViewById(R.id.iv_livevideo_rank_item_right_leftimg);
    }

    @Override
    public void bindListener() {

    }

    /**
     * 点赞发送消息
     */
    public interface INotice {
        void sendNotice(JSONObject jsonObject);
    }

    private INotice iNotice;

    public INotice getiNotice() {
        return iNotice;
    }

    public void setiNotice(INotice iNotice) {
        this.iNotice = iNotice;
    }
}
