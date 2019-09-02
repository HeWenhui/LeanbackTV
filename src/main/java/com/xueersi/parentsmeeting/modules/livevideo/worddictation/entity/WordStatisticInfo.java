package com.xueersi.parentsmeeting.modules.livevideo.worddictation.entity;

/**
 * Created by linyuqiang on 2018/8/31.
 */
public class WordStatisticInfo {
    //小学答题卡
    public static final  String PAGETYPE_PRIMARY = "1";
    //初中答题卡
    public static final  String PAGETYPE_MIDDLE = "3";
    public int state;
    public String testid;
    public String pagetype;
    public String answers;
}
