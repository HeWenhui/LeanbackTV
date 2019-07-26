package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import com.xueersi.common.sharedata.ShareDataManager;

public class UserAnswerSaveSahre implements UserAnswerSave {
    ShareDataManager mShareDataManager;

    public UserAnswerSaveSahre(ShareDataManager mShareDataManager) {
        this.mShareDataManager = mShareDataManager;
    }

    @Override
    public String getString(String key, String defValue, int iSpName) {
        return mShareDataManager.getString(key, defValue, iSpName);
    }

    @Override
    public void put(String key, String value, int iSpName) {
        mShareDataManager.put(key, value, iSpName);
    }

    @Override
    public void clear() {

    }
}
