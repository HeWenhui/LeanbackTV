package com.xueersi.parentsmeeting.modules.livevideo.betterme.config;

import com.xueersi.parentsmeeting.modules.livevideo.R;

public class BetterMeConfig {
    /**
     * 小目标类型 - 正确率
     */
    public static final String TYPE_CORRECTRATE = "CORRECTRATE";
    /**
     * 小目标类型 - 参与率
     */
    public static final String TYPE_PARTICIPATERATE = "PARTICIPATERATE";
    /**
     * 小目标类型 - 开口时长
     */
    public static final String TYPE_TALKTIME = "TALKTIME";

    /**
     * 正确率
     */
    public static final String CORRECTRATE = "正确率";
    /**
     * 参与率
     */
    public static final String PARTICIPATERATE = "参与率";
    /**
     * 开口时长
     */
    public static final String TALKTIME = "开口时长";

    /**
     * 段位名称
     */
    public static String[] LEVEL_NAMES = new String[]{
            "倔强青铜",
            "勤奋白银",
            "刻苦黄金",
            "恒心铂金",
            "笃学钻石",
            "最强学霸"
    };

    /**
     * 升星提示
     */
    public static String[] LEVEL_UPSTAR_DESCRIPTIONS = new String[]{
            "每完成3次目标升1星",
            "每完成4次目标升1星",
            "每完成4次目标升1星",
            "每完成5次目标升1星",
            "每完成6次目标升1星",
            "每完成6次目标升1星"
    };
    /**
     * 升级提示
     */
    public static String[] LEVEL_UPLEVEL_DESCRIPTIONS = new String[]{
            "满3星升级",
            "满3星升级",
            "满4星升级",
            "满5星升级",
            "满6星升级",
            "满6星升级"
    };

    /**
     * 升级所需星星数量
     */
    public static int[] LEVEL_UPLEVEL_STARS = new int[]{
            3, 3, 4, 5, 6, 6
    };

    public static int[] LEVEL_IMAGE_RES_HEAD = new int[]{
            R.drawable.app_livevideo_enteampk_juejiangqingtong_img_head,
            R.drawable.app_livevideo_enteampk_qinfenbaiyin_img_head,
            R.drawable.app_livevideo_enteampk_kekuhuangjin_img_head,
            R.drawable.app_livevideo_enteampk_hengxinbojin_img_head,
            R.drawable.app_livevideo_enteampk_zuanshi_img_head,
            R.drawable.app_livevideo_enteampk_zuiqiangxueba_img_head
    };

    /**
     * 满星段位图片资源
     */
    public static int[] LEVEL_IMAGE_RES_ALLSTAR = new int[]{
            R.drawable.app_xiaomubiao_qingtong,
            R.drawable.app_xiaomubiao_baiyin,
            R.drawable.app_xiaomubiao_huangjin,
            R.drawable.app_xiaomubiao_bojin,
            R.drawable.app_xiaomubiao_zuanshi,
            R.drawable.app_xiaomubiao_wangzhe
    };

    /**
     * 无星段位图片资源
     */
    public static int[] LEVEL_IMAGE_RES_NOSTAR = new int[]{
            R.drawable.app_livevideo_enteampk_juejiangqingtong_img_nor,
            R.drawable.app_livevideo_enteampk_qinfenbaiyin_img_nor,
            R.drawable.app_livevideo_enteampk_kekuhuangjin_img_nor,
            R.drawable.app_livevideo_enteampk_hengxinbojin_img_nor,
            R.drawable.app_livevideo_enteampk_zhuanshi_img_nol,
            R.drawable.app_livevideo_enteampk_zuiqiangxueba_img_nor
    };

    /**
     * 灰色段位图片资源
     */
    public static int[] LEVEL_IMAGE_RES_DISS = new int[]{
            R.drawable.app_livevideo_enteampk_juejiangqingtong_img_diss,
            R.drawable.app_livevideo_enteampk_qinfenbaiyin_img_diss,
            R.drawable.app_livevideo_enteampk_kekuhuangjin_img_diss,
            R.drawable.app_livevideo_enteampk_hengxinbojin_img_diss,
            R.drawable.app_livevideo_enteampk_zhuanshi_img_diss,
            R.drawable.app_livevideo_enteampk_zuiqiangxueba_img_diss
    };

    /**
     * 最强学霸1-6星星
     */
    public static int[] ZUIQIANGXUEBA_STAR_IMAGE_RES = new int[]{
            R.drawable.app_livevideo_enteampk__zuiqiangxueba_xingxing1_img_nor,
            R.drawable.app_livevideo_enteampk__zuiqiangxueba_xingxing2_img_nor,
            R.drawable.app_livevideo_enteampk__zuiqiangxueba_xingxing3_img_nor,
            R.drawable.app_livevideo_enteampk__zuiqiangxueba_xingxing4_img_nor,
            R.drawable.app_livevideo_enteampk__zuiqiangxueba_xingxing5_img_nor,
            R.drawable.app_livevideo_enteampk__zuiqiangxueba_xingxing6_img_nor,
    };

    /**
     * 恒心铂金1-5星星
     */
    public static int[] HENGXINBOJIN_STAR_IMAGE_RES = new int[]{
            R.drawable.app_livevideo_enteampk_hengxinbojin_xingxign1_img_nor,
            R.drawable.app_livevideo_enteampk_hengxinbojin_xingxign2_img_nor,
            R.drawable.app_livevideo_enteampk_hengxinbojin_xingxign3_img_nor,
            R.drawable.app_livevideo_enteampk_hengxinbojin_xingxign4_img_nor,
            R.drawable.app_livevideo_enteampk_hengxinbojin_xingxign5_img_nor,
    };

    /**
     * 刻苦黄金1-4星星
     */
    public static int[] KEKUHUANGJIN_STAR_IMAGE_RES = new int[]{
            R.drawable.app_livevideo_enteampk_kekuhuangjin_xingxing1_img_nor,
            R.drawable.app_livevideo_enteampk_kekuhuangjin_xingxing2_img_nor,
            R.drawable.app_livevideo_enteampk_kekuhuangjin_xingxing3_img_nor,
            R.drawable.app_livevideo_enteampk_kekuhuangjin_xingxing4_img_nor,
    };

    /**
     * 勤奋白银1-3星星
     */
    public static int[] QINFENBAIYIN_STAR_IMAGE_RES = new int[]{
            R.drawable.app_livevideo_enteampk_qinfenbaiyin_xingxing1_img_nor,
            R.drawable.app_livevideo_enteampk_qinfenbaiyin_xingxing2_img_nor,
            R.drawable.app_livevideo_enteampk_qinfenbaiyin_xingxing3_img_nor,
    };

    public static int LEVEL_NUMBER = 6;

    /**
     * 星星
     */
    public static int[][] STAR_IMAGE_RES = new int[][]{
            {
                    R.drawable.app_livevideo_enteampk_qinfenbaiyin_xingxing1_img_nor,
                    R.drawable.app_livevideo_enteampk_qinfenbaiyin_xingxing2_img_nor,
                    R.drawable.app_livevideo_enteampk_qinfenbaiyin_xingxing3_img_nor,},
            {
                    R.drawable.app_livevideo_enteampk_qinfenbaiyin_xingxing1_img_nor,
                    R.drawable.app_livevideo_enteampk_qinfenbaiyin_xingxing2_img_nor,
                    R.drawable.app_livevideo_enteampk_qinfenbaiyin_xingxing3_img_nor,},
            {
                    R.drawable.app_livevideo_enteampk_kekuhuangjin_xingxing1_img_nor,
                    R.drawable.app_livevideo_enteampk_kekuhuangjin_xingxing2_img_nor,
                    R.drawable.app_livevideo_enteampk_kekuhuangjin_xingxing3_img_nor,
                    R.drawable.app_livevideo_enteampk_kekuhuangjin_xingxing4_img_nor,},
            {
                    R.drawable.app_livevideo_enteampk_hengxinbojin_xingxign1_img_nor,
                    R.drawable.app_livevideo_enteampk_hengxinbojin_xingxign2_img_nor,
                    R.drawable.app_livevideo_enteampk_hengxinbojin_xingxign3_img_nor,
                    R.drawable.app_livevideo_enteampk_hengxinbojin_xingxign4_img_nor,
                    R.drawable.app_livevideo_enteampk_hengxinbojin_xingxign5_img_nor,},
            {
                    R.drawable.app_livevideo_enteampk_juejiangqingtong_img_head,
                    R.drawable.app_livevideo_enteampk_qinfenbaiyin_img_head,
                    R.drawable.app_livevideo_enteampk_kekuhuangjin_img_head,
                    R.drawable.app_livevideo_enteampk_hengxinbojin_img_head,
                    R.drawable.app_livevideo_enteampk_zuanshi_img_head,
                    R.drawable.app_livevideo_enteampk_zuiqiangxueba_img_head},
            {
                    R.drawable.app_livevideo_enteampk_juejiangqingtong_img_head,
                    R.drawable.app_livevideo_enteampk_qinfenbaiyin_img_head,
                    R.drawable.app_livevideo_enteampk_kekuhuangjin_img_head,
                    R.drawable.app_livevideo_enteampk_hengxinbojin_img_head,
                    R.drawable.app_livevideo_enteampk_zuanshi_img_head,
                    R.drawable.app_livevideo_enteampk_zuiqiangxueba_img_head}
    };
}
