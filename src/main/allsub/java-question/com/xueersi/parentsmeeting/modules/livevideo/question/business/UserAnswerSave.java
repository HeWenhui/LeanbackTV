package com.xueersi.parentsmeeting.modules.livevideo.question.business;

/**
 * 用户答案保持。直播存硬盘，回放存内存
 */
public interface UserAnswerSave {
    String getString(String key, String defValue, int iSpName);

    void put(String key, String value, int iSpName);

    void clear();
}
