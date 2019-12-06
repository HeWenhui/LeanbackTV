package com.xueersi.parentsmeeting.modules.livevideo.englishname.business;

import android.content.Context;

import com.xueersi.common.base.BaseHttpBusiness;
import com.xueersi.common.http.BaseHttp;
import com.xueersi.common.http.HttpCall;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.englishname.config.EnglishNameConfig;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpAction;

public class SettingEnglishNameHttpManager extends BaseHttpBusiness {

    public SettingEnglishNameHttpManager(Context context) {
        super(context);
    }


    public void getDownLoadPath( HttpCallBack httpCallBack){
        sendJsonPost(EnglishNameConfig.GROUP_CLASS_ENGLSIH_NAME_URL,"",httpCallBack);
    }
}
