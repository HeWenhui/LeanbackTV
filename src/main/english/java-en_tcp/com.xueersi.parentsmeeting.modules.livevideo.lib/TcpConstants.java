package com.xueersi.parentsmeeting.modules.livevideo.lib;

public class TcpConstants {
    static short PackSize = 4;
    static short HeaderSize = 2;
    static short ver = 1;
    static short VerSize = 2;
    static short TypeSize = 2;
    static short OperationSize = 4;
    static short SeqIDSize = 4;
    static short header = (short) (PackSize + HeaderSize + VerSize + TypeSize + OperationSize + SeqIDSize);
    static short LOGIN_TYPE = 3;
    static int LOGIN_OPERATION_SEND = 6;
    static int LOGIN_OPERATION_REC = 7;
    static short HEAD_TYPE = 6;
    static int HEAD_OPERATION_SEND = 2;
    static int HEAD_OPERATION_REC = 3;
}
