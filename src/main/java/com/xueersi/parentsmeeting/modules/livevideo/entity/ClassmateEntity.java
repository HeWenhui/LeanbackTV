package com.xueersi.parentsmeeting.modules.livevideo.entity;

public class ClassmateEntity {
    private String id;
    private String name;
    private String img;
    private int place;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getPlace() {
        return place;
    }

    public void setPlace(int place) {
        this.place = place;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ClassmateEntity)) {
            return false;
        }
        ClassmateEntity classmateEntity = (ClassmateEntity) obj;
        return ("" + id).equals(((ClassmateEntity) obj).id);
    }
}
