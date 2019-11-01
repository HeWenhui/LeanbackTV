package com.xueersi.parentsmeeting.modules.livevideo.englishname.entity;


/**
 * 英文名实体
 */
public class EngLishNameEntity {
    /** 英文名*/
    private String name;
    private int index;
    /** 名字索引*/
    private String wordIndex;
    /** 是否 索引*/
    private boolean isSelect;
    /** 一行显示多少 */
    private int spanNum = 1;
    /** 是否是导航 */
    private boolean isIndex;
    /** 导航位置 */
    private int indexPostion;
    /** 音频位置*/
    private String audioPath;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getWordIndex() {
        return wordIndex;
    }

    public void setWordIndex(String wordIndex) {
        this.wordIndex = wordIndex;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public int getSpanNum() {
        return spanNum;
    }

    public void setSpanNum(int spanNum) {
        this.spanNum = spanNum;
    }

    public boolean isIndex() {
        return isIndex;
    }

    public void setIndex(boolean index) {
        isIndex = index;
    }

    public int getIndexPostion() {
        return indexPostion;
    }

    public void setIndexPostion(int indexPostion) {
        this.indexPostion = indexPostion;
    }

    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }
}
