package com.xueersi.parentsmeeting.modules.livevideo.business;

/**
 * Created by linyuqiang on 2017/11/12.
 * 语音请求和释放
 */
public interface AudioRequest {
    void request(OnAudioRequest onAudioRequest);

    void release();

    interface OnAudioRequest {
        void requestSuccess();
    }
}
