package com.xueersi.parentsmeeting.modules.livevideo.englishname.config;

import android.os.Environment;

import com.xueersi.common.util.LoadFileUtils;
import com.xueersi.lib.framework.are.ContextManager;

public class EnglishNameConfig {
    /** 推荐 */
    public static final int GROUP_CLASS_ENGLISH_NAME_RECOMMEND = 1;
    /** 导航 */
    public static final int GROUP_CLASS_ENGLISH_NAME_BAR = 2;
    /** 名字选择*/
    public static final int GROUP_CLASS_ENGLISH_NAME_SELECT = 3;
    /** 搜索选中*/
    public static final int GROUP_CLASS_ENGLISH_NAME_SEARCH = 4;
    public static final String GROUP_CLASS_ENGLSIH_NAME_URL = "https://studentlive.xueersi.com/v1/student/namesuggest/getUrl";

    public static final String GROUP_CLASS_SUB_NAME_LIST_TXT = "group_class_sub_name_list_txt";

    public static final String LIVE_UNITI_PATH_ENGLISH_VERSION = "70601";

    public static final String LIVE_UNITI_NET_PATH_L  = LoadFileUtils.geCacheFile(ContextManager.getContext(), "subgroup").getAbsolutePath();


    public static final String LIVE_GROUP_ENGLISH_NAME_NAME_AUDIO = "live_group_english_name_name_audio";

    public static final String  LIVE_UNITI_NET_PATH_L_FILE_URL= "live_uniti_net_path_l_file_url";

}
