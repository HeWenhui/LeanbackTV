package com.xueersi.parentsmeeting.modules.livevideo.lib;

/**
 * tcp 一些常量，协议规则
 * http://wiki.xesv5.com/pages/viewpage.action?pageId=12966243
 */
public class TcpConstants {
    private static short PackSize = 4;
    private static short HeaderSize = 2;
    private static short VerSize = 2;
    static short ver = 2;
    private static short TypeSize = 2;
    private static short OperationSize = 4;
    private static short SeqIDSize = 4;
    /** 头里面时间戳长度 */
    private static short timestamp = 8;
    static short header = (short) (PackSize + HeaderSize + VerSize + TypeSize + OperationSize + SeqIDSize + timestamp);
    /** 消息类型，回执 */
    public final static short REPLAY_TYPE = 0;
    /** 消息类型，回执 */
    public final static short REPLAY_REC = 2501;
    /** 消息类型，登陆 */
    public final static short LOGIN_TYPE = 3;
    /** 消息登陆-发送 */
    public static int LOGIN_OPERATION_SEND = 6;
    /** 消息登陆-回执 */
    public static int LOGIN_OPERATION_REC = 7;
    /** 消息类型，心跳 */
    public final static short HEAD_TYPE = 6;
    /** 消息心跳-发送 */
    public static int HEAD_OPERATION_SEND = 2;
    /** 消息心跳-回执 */
    public static int HEAD_OPERATION_REC = 3;
    /** 消息类型，服务器回复学生互动分组 */
    public final static short TEAM_TYPE = 7;
    /** 消息心跳-服务器回复学生互动分组 */
    public final static int TEAM_OPERATION_SEND = 15;
    /** 消息类型，客户端发语音炮弹数据 */
    public final static short Voice_Projectile_TYPE = 8;
    /** 消息类型，服务器端发送恢复场景的数据 */
    public final static int Voice_Projectile_Scene = 17;
    /** 消息类型，服务器端发送语音炮弹统计数据 */
    public final static int Voice_Projectile_Statis = 18;
    /** 消息类型，客户端发语音炮弹数据 */
    public final static int Voice_Projectile_SEND = 16;
    /** 消息类型，服务器回复 */
    public final static int Voice_Projectile_REC = 3;
}
