package com.xueersi.parentsmeeting.modules.livevideo.stablelog;

import android.util.Log;

import com.xueersi.common.base.BaseApplication;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;

import java.io.File;

/**
 * @author linyuqiang
 * 站立直播预加载日志
 */
public class StandLoadLog {
    static String eventId = LiveVideoConfig.LIVE_STAND_RES_UPDATE;

    public static void downFile(File file, File zipfile) {
        StableLogHashMap logHashMap = new StableLogHashMap("savefile");
        logHashMap.put("file", "" + file);
        logHashMap.put("zipfile", "" + zipfile);
        int type;
        if (zipfile.exists()) {
            if (file.exists()) {
                type = 1;
            } else {
                type = 2;
            }
        } else {
            type = 3;
        }
        logHashMap.put("type", "" + type);
        UmsAgentManager.umsAgentDebug(ContextManager.getContext(), eventId, logHashMap.getData());
    }

    public static void zipFileFailSuc(long startTime, File saveFile) {
        StableLogHashMap logHashMap = new StableLogHashMap("zipfilesuc");
        logHashMap.put("time", "" + (System.currentTimeMillis() - startTime));
        logHashMap.put("savefilename", "" + saveFile);
        logHashMap.put("savefilelength", "" + saveFile.length());
        UmsAgentManager.umsAgentDebug(ContextManager.getContext(), eventId, logHashMap.getData());
    }

    public static void zipFileFailErr(long startTime, Exception e) {
        StableLogHashMap logHashMap = new StableLogHashMap("zipfileerror");
        logHashMap.put("time", "" + (System.currentTimeMillis() - startTime));
        logHashMap.put("exception", "" + Log.getStackTraceString(e));
        UmsAgentManager.umsAgentDebug(ContextManager.getContext(), eventId, logHashMap.getData());
    }
}
