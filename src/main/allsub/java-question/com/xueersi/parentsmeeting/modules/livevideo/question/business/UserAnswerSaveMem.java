package com.xueersi.parentsmeeting.modules.livevideo.question.business;

import java.util.HashMap;

public class UserAnswerSaveMem implements UserAnswerSave {
    private HashMap<String, String> keyAndValue = new HashMap<>();

    @Override
    public String getString(String key, String defValue, int iSpName) {
        String getValue = keyAndValue.get(key);
        if (getValue == null) {
            getValue = defValue;
        }
        return getValue;
    }

    @Override
    public void put(String key, String value, int iSpName) {
        keyAndValue.put(key, value);
    }

    @Override
    public void clear() {
        keyAndValue.clear();
    }
}
