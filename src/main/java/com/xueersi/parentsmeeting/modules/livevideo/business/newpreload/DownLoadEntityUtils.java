package com.xueersi.parentsmeeting.modules.livevideo.business.newpreload;

import com.xueersi.parentsmeeting.modules.livevideo.entity.CoursewareInfoEntity;

public class DownLoadEntityUtils {
    public static class DownLoadEntityFactory implements Factory<CoursewareInfoEntity, LiveVideoDownLoadUtils.LiveVideoDownLoadFile> {

        @Override
        public LiveVideoDownLoadUtils.LiveVideoDownLoadFile create(CoursewareInfoEntity entity) {


            return new LiveVideoDownLoadUtils.
                    LiveVideoDownLoadFile.Builder()
                    .setUrl(entity.getIps().get(0))
                    .build();
        }
    }

    public static class CourseWareArrayFactory implements ArrayFactory<CoursewareInfoEntity> {

        @Override
        public LiveVideoDownLoadUtils.LiveVideoDownLoadFile[] create(CoursewareInfoEntity entity) {
            LiveVideoDownLoadUtils.LiveVideoDownLoadFile[] liveVideoDownLoadFiles = new
                    LiveVideoDownLoadUtils.LiveVideoDownLoadFile[10];

            return liveVideoDownLoadFiles;
        }
    }

    public interface Factory<T, V> {
        V create(T entity);
    }

    public interface ArrayFactory<T> {
        LiveVideoDownLoadUtils.LiveVideoDownLoadFile[] create(T entity);
    }

    public interface Adapter {
        LiveVideoDownLoadUtils.LiveVideoDownLoadFile create();
    }

    public interface ArrayAdapter {
        LiveVideoDownLoadUtils.LiveVideoDownLoadFile[] create();
    }
}
