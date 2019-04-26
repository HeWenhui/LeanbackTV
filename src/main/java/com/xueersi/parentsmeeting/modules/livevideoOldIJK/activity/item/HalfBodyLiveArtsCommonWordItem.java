package com.xueersi.parentsmeeting.modules.livevideoOldIJK.activity.item;

import android.content.Context;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.ui.adapter.CommonAdapter;

/**
* 半身直播 语文热词 item
*@author chekun
*created  at 2018/11/29 14:51
*/
public class HalfBodyLiveArtsCommonWordItem extends HalfBodyLiveCommonWordItem {
    public HalfBodyLiveArtsCommonWordItem(Context context, CommonAdapter commonAdapter) {
        super(context, commonAdapter);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_live_halfbody_hotwrod_arts;
    }
}
