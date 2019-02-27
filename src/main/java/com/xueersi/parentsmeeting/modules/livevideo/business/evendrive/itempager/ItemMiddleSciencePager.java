package com.xueersi.parentsmeeting.modules.livevideo.business.evendrive.itempager;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.ui.adapter.AdapterItemInterface;

import org.json.JSONObject;

public abstract class ItemMiddleSciencePager<T> implements AdapterItemInterface<T> {
    protected Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
    /**  */
    protected TextView rankLeft;//tv_livevideo_rank_item_left;

    protected TextView rankMiddleLeft;//tv_livevideo_rank_item_mid_left

    protected TextView rankMiddleRight;//tv_livevideo_rank_item_mid_right

    protected TextView rankRight;//tv_livevideo_rank_item_right

    protected ImageView ivRedHeard;

    protected T entity;

    protected Context mContext;
    /** 试题结束的时间 */
    protected long endTime = -1;
    /** 自己的学生id */
    protected String myStuId;

    protected View viewRoot;

    @Override
    public int getLayoutResId() {
        return R.layout.item_livevideo_middle_science_even_drive_listview_item;
    }

    @Override
    public void initViews(View root) {
        viewRoot = root;
        rankLeft = root.findViewById(R.id.tv_livevideo_rank_item_left);
        rankMiddleLeft = root.findViewById(R.id.tv_livevideo_rank_item_mid_left);
        rankMiddleRight = root.findViewById(R.id.tv_livevideo_rank_item_mid_right);
        rankRight = root.findViewById(R.id.tv_livevideo_rank_item_right);
        ivRedHeard = root.findViewById(R.id.iv_livevideo_rank_item_right_leftimg);
    }

//    @Override
//    public void bindListener() {
//
//    }

    public void setMyStuId(String stuId) {
        this.myStuId = stuId;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    /**
     * H5课件是否处于打开状态
     */
    private boolean isH5Open = false;

    public boolean isH5Open() {
        return isH5Open;
    }

    public void setH5Open(boolean h5Open) {
        isH5Open = h5Open;
    }

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
        void sendLike(int listFlag, String bePraised, HttpCallBack httpCallBack);

    }

    /** 给自己点赞 */
    public interface IClickSelf {
        /** 点赞自己 */
        void clickSelf();
    }

    protected IClickSelf iClickSelf;

    public void setiClickSelf(IClickSelf iClickSelf) {
        this.iClickSelf = iClickSelf;
    }

    private INotice iNotice;

    public INotice getiNotice() {
        return iNotice;
    }

    public void setiNotice(INotice iNotice) {
        this.iNotice = iNotice;
    }
}
