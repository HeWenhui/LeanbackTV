package com.xueersi.parentsmeeting.modules.livevideoOldIJK.message.pager;

import android.content.Context;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.activity.item.HalfBodyLiveArtsCommonWordItem;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;
import com.xueersi.ui.adapter.AdapterItemInterface;
import com.xueersi.ui.adapter.CommonAdapter;

import java.util.ArrayList;

import cn.dreamtobe.kpswitch.util.KeyboardUtil;

/**
 * 半身直播  语文聊天UI
 *
 * @author chekun
 * created  at 2018/11/16 10:13
 */

public class HalfBodyArtsLiveMsgPager extends HalfBodyLiveMessagePager {

    public HalfBodyArtsLiveMsgPager(Context context, KeyboardUtil.OnKeyboardShowingListener keyboardShowingListener,
                                    LiveAndBackDebug ums, BaseLiveMediaControllerBottom liveMediaControllerBottom,
                                    ArrayList<LiveMessageEntity> liveMessageEntities, ArrayList<LiveMessageEntity>
                                            otherLiveMessageEntities) {
        super(context, keyboardShowingListener, ums, liveMediaControllerBottom, liveMessageEntities,
                otherLiveMessageEntities);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.page_livevideo_message_halfbody_arts;
    }

    @Override
    protected int getHotWordPopwindLayout(){
        return R.layout.layout_live_commonwrod_popwindow_arts;
    }

    @Override
    protected AdapterItemInterface<String>  generateHotWordItem(CommonAdapter adapter){
        return new HalfBodyLiveArtsCommonWordItem(mContext,adapter);
    }

}
