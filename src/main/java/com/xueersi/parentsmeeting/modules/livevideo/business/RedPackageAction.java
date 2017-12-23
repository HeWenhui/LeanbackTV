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

    /**
     * 得到红包
     *
     * @param entity
     */
    void onGetPackage(VideoResultEntity entity);

    /**
     * 领取红包网络失败
     *
     * @param operateId
     */
    void onGetPackageFailure(int operateId);

    /**
     * 领取红包业务失败
     *
     * @param operateId
     */
    void onGetPackageError(int operateId);
}
