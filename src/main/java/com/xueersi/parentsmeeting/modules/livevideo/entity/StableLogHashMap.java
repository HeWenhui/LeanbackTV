package com.xueersi.parentsmeeting.modules.livevideo.entity;

import java.util.HashMap;
import java.util.Map;

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

    public StableLogHashMap addEx(String ex) {
        mData.put("ex", ex);
        return this;
    }

    public StableLogHashMap addExpect(String expect) {
        mData.put("expect", expect);
        return this;
    }

    public StableLogHashMap addSno(String sno) {
        mData.put("sno", sno);
        return this;
    }

    public StableLogHashMap addNonce(String nonce) {
        mData.put("nonce", nonce);
        return this;
    }

    public StableLogHashMap addStable(String stable) {
        mData.put("stable", stable);
        return this;
    }

    public Map<String, String> getData() {
        return mData;
    }
}
