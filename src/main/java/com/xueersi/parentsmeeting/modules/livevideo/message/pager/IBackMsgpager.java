package com.xueersi.parentsmeeting.modules.livevideo.message.pager;

import android.view.View;

import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveBackMsgEntity;

/**
 * @ProjectName: xueersiwangxiao
 * @Package: com.xueersi.parentsmeeting.modules.livevideo.message.pager
 * @ClassName: IBackMsgpager
 * @Description: 回放消息接口
 * @Author: WangDe
 * @CreateDate: 2019/12/26 10:36
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/12/26 10:36
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public interface IBackMsgpager {
    /**
     * 展示消息
     *
     * @param sender
     * @param type
     * @param msgStr
     * @param headUrl
     */
    void addMsg(LiveBackMsgEntity entity);

    /**
     * 移除所有消息展示
     */
    void removeAllMsg();

    /**
     * 移除这个时间点以后的聊天记录（回退进度使用）
     * @param pos
     */
    void removeOverMsg(long pos);

    /**
     * 获取根布局
     * @return
     */
    View getRootView();
}
