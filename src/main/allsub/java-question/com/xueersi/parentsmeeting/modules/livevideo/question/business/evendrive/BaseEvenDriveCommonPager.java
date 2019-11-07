package com.xueersi.parentsmeeting.modules.livevideo.question.business.evendrive;

import android.content.Context;
import android.text.SpannableString;

import com.xueersi.common.config.AppConfig;
import com.xueersi.parentsmeeting.modules.livevideo.business.BaseLiveMessagePager;
import com.xueersi.parentsmeeting.modules.livevideo.message.pager.StringDrawableUtils;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseEvenDriveCommonPager extends BaseLiveMessagePager {

    public static final String EVEN_DRIVE_ICON = "icon ";
    public static final String EVEN_DRIVE_ICON_SPAN = "icon";
    protected boolean myTest = false && AppConfig.DEBUG;

    public BaseEvenDriveCommonPager(Context context) {
        super(context);
    }

    /**
     * 发送带有连对激励的msg
     *
     * @param msg
     * @param name
     * @return
     */
    protected boolean sendEvenDriveMessage(String msg, String name) {
        Map<String, String> map = new HashMap<>();
        map.put("evenexc", String.valueOf(getEvenNum()));
        boolean send = ircState.sendMessage(msg, name, map);
        return send;
    }

    protected int getEvenNum() {
        if (getInfo != null && getInfo.getEvenDriveInfo() != null) {
            return getInfo.getEvenDriveInfo().getEvenNum();
        } else {
            return 0;
        }
    }

    protected boolean isOpenStimulation() {
        return EvenDriveUtils.isOpenStimulation(getInfo);
//        return getInfo != null &&
//                getInfo.getEvenDriveInfo() != null &&
//                OPEN_STIMULATION == getInfo.getEvenDriveInfo().getIsOpenStimulation();
    }

    protected SpannableString addEvenDriveMessageNum(SpannableString spannableString, String evenNum, int type) {
        if (isOpenStimulation()) {
            return StringDrawableUtils.addEvenDriveMessageNum(mContext, spannableString, evenNum, type);
        }
        return spannableString;
    }
}
