package com.xueersi.parentsmeeting.modules.livevideo.entity;

/**
 * @ProjectName: xueersiwangxiao
 * @Package: com.xueersi.parentsmeeting.modules.livevideo.entity
 * @ClassName: LiveBackMsgEntity
 * @Description: 回放消息实体
 * @Author: WangDe
 * @CreateDate: 2019/12/26 10:43
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/12/26 10:43
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class LiveBackMsgEntity {
    //自己
    public static final int MESSAGE_MINE = 0;
    //教师
    public static final int MESSAGE_TEACHER = 1;
    //同班同学
    public static final int MESSAGE_CLASS = 2;
    //系统提示
    public static final int MESSAGE_TIP = 3;
    //从接口获取的消息类型----聊天消息
    public static final String MESSAGE_TYPE = "130";

    /** 发送人 */
    private String sender;
    /** 消息内容 */
    private CharSequence text;
    /** 消息来源  */
    private int from;
    /** 时间戳序号毫秒 */
    private long id;
    /** 接收者*/
    private String receiver;
    /** 是否是群聊消息（1群聊，0单聊）*/
    private int channel;
    /** 是否为notice消息（1是notice，0不是notice*/
    private int notice;
    /** 消息类型 130为聊天消息*/
    private String type;
    /** 用户姓名*/
    private String name;
    /** 用户头像*/
    private String headImg;

    /**
     * 激励体系连对信息
     */
    private String evenNum;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public int getNotice() {
        return notice;
    }

    public void setNotice(int notice) {
        this.notice = notice;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public CharSequence getText() {
        return text;
    }

    public void setText(CharSequence text) {
        this.text = text;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public void setEvenNum(String evenNum) {
        this.evenNum = evenNum;
    }

    public String getEvenNum() {
        return evenNum;
    }
}
