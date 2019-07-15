package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils;

import com.xueersi.lib.unity3d.UnityCommandPlay;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Unity3DPlayManager {
    /**
     *
     */
//    private static final String start_Mouth = "1";
    private static final Map<Integer, List<String>> mapList;

    static {
        mapList = new HashMap();
        mapList.put(IntelligentConstants.PERFECT,
                Arrays.asList(IntelligentUnity3DBodyParam.A_MON_T_U, IntelligentUnity3DFaceParam.A_MON_S2));

        mapList.put(IntelligentConstants.GOOD,
                Arrays.asList(IntelligentUnity3DBodyParam.A_MON_RH_E, IntelligentUnity3DFaceParam.A_MON_SP));

        mapList.put(IntelligentConstants.FEED_BACK_SENTENCE_1_0,
                Arrays.asList(IntelligentUnity3DBodyParam.A_MON_T_U, IntelligentUnity3DFaceParam.A_MON_SK));

        mapList.put(IntelligentConstants.FEED_BACK_WORD_1,
                Arrays.asList(IntelligentUnity3DBodyParam.A_MON_T_U, IntelligentUnity3DFaceParam.A_MON_SK,
                        IntelligentUnity3DBodyParam.A_MON_LH_U));

        mapList.put(IntelligentConstants.FEED_BACK_SENTENCE_1_1,
                Arrays.asList(IntelligentUnity3DBodyParam.A_MON_BH_C, IntelligentUnity3DFaceParam.A_MON_SK));

        mapList.put(IntelligentConstants.FEED_BACK_SENTENCE_2_0,
                Arrays.asList(IntelligentUnity3DBodyParam.A_MON_T_U, IntelligentUnity3DFaceParam.A_MON_SK));

        mapList.put(IntelligentConstants.FEED_BACK_SENTENCE_2_1,
                Arrays.asList(IntelligentUnity3DBodyParam.A_MON_BH_C, IntelligentUnity3DFaceParam.A_MON_S1));

        mapList.put(IntelligentConstants.FEED_BACK_WORD_2_0,
                Arrays.asList(IntelligentUnity3DBodyParam.A_MON_T_U, IntelligentUnity3DFaceParam.A_MON_S2));

        mapList.put(IntelligentConstants.FEED_BACK_WORD_2_1,
                Arrays.asList(IntelligentUnity3DBodyParam.A_MON_BH_C, IntelligentUnity3DFaceParam.A_MON_S1));

        mapList.put(IntelligentConstants.FEED_BACK_WORD_3_0,
                Arrays.asList(IntelligentUnity3DBodyParam.A_MON_T_U, IntelligentUnity3DFaceParam.A_MON_S2));

        mapList.put(IntelligentConstants.FEED_BACK_WORD_3_1,
                Arrays.asList(IntelligentUnity3DBodyParam.A_MON_T_U, IntelligentUnity3DFaceParam.A_MON_S1));

        mapList.put(IntelligentConstants.END_GOOD_BYE_1,
                Arrays.asList(IntelligentUnity3DBodyParam.A_MON_RH_W));

        mapList.put(IntelligentConstants.END_GOOD_BYE_2,
                Arrays.asList(IntelligentUnity3DBodyParam.A_MON_RH_W));

        mapList.put(IntelligentConstants.END_GOOD_BYE_3,
                Arrays.asList(IntelligentUnity3DBodyParam.A_MON_RH_W));

        mapList.put(IntelligentConstants.END_GOOD_BYE_4,
                Arrays.asList(IntelligentUnity3DBodyParam.A_MON_RH_W));
    }


    public static void playUnity3D(int status) {
        UnityCommandPlay.playBodyActionSingle(mapList.get(status).get(0));
        UnityCommandPlay.playFaceActionSingle(mapList.get(status).get(1));
//        switch (status) {
//            case IntelligentConstants.PERFECT: {
////                play_A_MON_LH_U();
//                play_A_MON_S2();
//
//                break;
//            }
//            case IntelligentConstants.GOOD: {
//
//                break;
//            }
////            case IntelligentConstants.REPEAT_SENTENCE: {
////                break;
////            }
////            case IntelligentConstants.REPEAT_WORD: {
////                break;
////            }
//        }

    }

    /** 开始说话 */
    public static void playSayStart() {
        UnityCommandPlay.playSayStart();
    }

    /** 停止说话 */
    public static void playSayStop() {
        UnityCommandPlay.playSayStop();
    }

    //右手抬起，竖起大拇指表示赞
    public static void play_A_MON_T_U() {
        UnityCommandPlay.playBodyActionSingle(IntelligentUnity3DBodyParam.A_MON_T_U);
    }

    //大笑
    public static void play_A_MON_S2() {
        UnityCommandPlay.playFaceActionSingle(IntelligentUnity3DFaceParam.A_MON_S2);
    }

    //左手指向左图片方向指
    //（抬手，保持动作，放手）
    public static void play_A_MON_LH_U() {

    }

    //老师头向左边方向偏转
    //（转头，保持动作，恢复）
    public static void play_A_MON_H_L() {
        UnityCommandPlay.playBodyActionSingle(IntelligentUnity3DBodyParam.A_MON_H_L);
    }

    private interface IntelligentUnity3DBodyParam {
        //wave挥手
        String A_MON_RH_W = "A_MON_RH_W";

        //左手指向左图片方向指
        //（抬手，保持动作，放手）
        String A_MON_LH_U = "A_MON_LH_U";

        //老师头向左边方向偏转
        //（转头，保持动作，恢复）
        String A_MON_H_L = "A_MON_H_L";

        //右手抬起放在耳侧（抬手，保持动作，放手）
        String A_MON_RH_E = "A_MON_RH_E";

        //举起右手，和学员击掌，击掌后作出右手举大拇指的姿势
        String A_MON_RH_CL = "A_MON_RH_CL";

        //右手举“1”（抬手，保持动作，放手）
        String A_MON_RH_1 = "A_MON_RH_1";

        //右手抬起，竖起大拇指表示赞
        String A_MON_T_U = "A_MON_T_U";

        //手托下颚（抬手，保持动作，放手）
        String A_MON_LH_J = "A_MON_LH_J";

        //hands claps在胸前拍手（抬手，保持动作，放手）
        String A_MON_BH_C = "A_MON_BH_C";

        //胜利剪刀手
        String A_MON_SH = "A_MON_SH";

        //右手轻拍胸口一次
        String A_MON_RH_B = "A_MON_RH_B";

        //默认动作，站立，手放两侧
        String A_MON_B_0 = "A_MON_B_0";

        //身体左右晃动扭一扭
        String A_MON_B_S = "A_MON_B_S";

        //老师点头
        String A_MON_H_N = "A_MON_H_N";
    }

    private interface IntelligentUnity3DFaceParam {
        //微笑
        String A_MON_S1 = "A_MON_S1";
        //大笑
        String A_MON_S2 = "A_MON_S2";
        //疑惑
        String A_MON_D = "A_MON_D";
        //恍然大悟
        String A_MON_TT = "A_MON_TT";
        //说话
        String A_MON_SP = "A_MON_SP";
        //说话R
        String A_MON_RSP = "A_MON_RSP";
        //难过
        String A_MON_S = "A_MON_S";
        //调皮
        String A_MON_N = "A_MON_N";
        //惊讶
        String A_MON_SK = "A_MON_SK";
        //喜悦开心
        String A_MON_H = "A_MON_H";
    }
}
