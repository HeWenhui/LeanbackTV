package com.xueersi.parentsmeeting.modules.livevideo.page;

import android.content.Context;
import android.os.Environment;
import android.view.View;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;

import java.io.File;

/**
 * Created by linyuqiang on 2018/7/5.
 * 直播基础pager
 */
public class LiveBasePager extends BasePager {
    protected LogToFile mLogtf;

    public LiveBasePager(Context context) {
        super(context);
        mLogtf = new LogToFile(context, TAG, new File(Environment.getExternalStorageDirectory(), "parentsmeeting/log/" + TAG
                + ".txt"));
    }

    @Override
    public View initView() {
        return null;
    }

    @Override
    public void initData() {

    }
}
