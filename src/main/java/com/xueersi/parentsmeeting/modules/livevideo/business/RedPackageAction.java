package com.xueersi.parentsmeeting.modules.livevideo.business;

import com.xueersi.parentsmeeting.entity.VideoResultEntity;

/**
 * Created by linyuqiang on 2016/9/23.
 * 红包事件
 */
public interface RedPackageAction {
    /**
     * 红包消息
     *
     * @param operateId
     */
    void onReadPackage(int operateId);

}
