package com.xueersi.parentsmeeting.modules.livevideo.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by lyqai on 2018/1/6.
 * 稳定性日志
 */
public class StableLogHashMap {
    Map<String, String> mData = new HashMap<>();

    public StableLogHashMap() {

    }

    public StableLogHashMap(String logType) {
        mData.put("logtype", logType);
    }

    public StableLogHashMap put(String key, String value) {
        mData.put(key, value);
        return this;
    }

    /**
     * 收到上一步，期望是不是成功
     *
     * @param ex
     * @return
     */
    public StableLogHashMap addEx(String ex) {
        mData.put("ex", ex);
        return this;
    }

    /**
     * 期望下一步收到的数量
     *
     * @param expect
     * @return
     */
    public StableLogHashMap addExpect(String expect) {
        mData.put("expect", expect);
        return this;
    }

    /**
     * 第几步
     *
     * @param sno
     * @return
     */
    public StableLogHashMap addSno(String sno) {
        mData.put("sno", sno);
        return this;
    }

    /**
     * 随机值
     *
     * @param nonce
     * @return
     */
    public StableLogHashMap addNonce(String nonce) {
        mData.put("nonce", nonce);
        return this;
    }

    /**
     * 随机值
     *
     * @return
     */
    public static String creatNonce() {
        return "" + UUID.randomUUID();
    }

    /**
     * 稳定行
     *
     * @param stable
     * @return
     */
    public StableLogHashMap addStable(String stable) {
        mData.put("stable", stable);
        return this;
    }

    public Map<String, String> getData() {
        return mData;
    }
}
