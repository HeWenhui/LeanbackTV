package com.xueersi.parentsmeeting.modules.livevideo.business.newpreload;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DownLoadExecutor<T> {
    private List<LiveVideoDownLoadUtils.LiveVideoDownLoadFile> downLoadFiles;
    private DownLoadEntityUtils.Factory<T, LiveVideoDownLoadUtils.LiveVideoDownLoadFile> factory;

    public DownLoadExecutor(DownLoadEntityUtils.Factory factory) {
        this.factory = factory;
        downLoadFiles = new CopyOnWriteArrayList<>();
    }

    public void execute() {
        for (int i = 0; i < downLoadFiles.size(); i++) {
            FileDownLoadManager.addToAutoDownloadPool(downLoadFiles.get(i));
        }
    }

    public void addEntity(T entity) {
        downLoadFiles.add(factory.create(entity));
    }

    public void addEntityLists(List<T> entity) {
        for (int i = 0; i < entity.size(); i++) {
            downLoadFiles.add(factory.create(entity.get(i)));
        }
    }
}
