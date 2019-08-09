package com.xueersi.parentsmeeting.modules.livevideo.util;

/**
 * @author linyuqiang
 *         解压进度条回调
 * @date 2018/5/8
 */
public interface ZipProg {
    /**
     * 获得解压的进度。
     *
     * @param values length == 1只有进度，>1 index=1是最大值
     */
    void onProgressUpdate(Integer... values);

    /**
     * 解压结果
     *
     * @param exception 如果==null.是解压成功，如果不为空，且没有取消是失败
     */
    void onPostExecute(Exception exception);

    /**
     * 设置最大值
     *
     * @param max
     */
    void setMax(int max);
}
