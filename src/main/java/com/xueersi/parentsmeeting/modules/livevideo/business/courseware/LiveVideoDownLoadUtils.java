package com.xueersi.parentsmeeting.modules.livevideo.business.courseware;

import android.text.TextUtils;

import com.alibaba.android.arouter.thread.DefaultPoolExecutor;
import com.xueersi.common.network.download.DownloadListener;
import com.xueersi.parentsmeeting.modules.livevideo.util.ZipExtractorTask;

import java.io.File;
import java.util.concurrent.Executor;

public class LiveVideoDownLoadUtils {
    public final static int URGENT_LEVEL_1 = 1;
    public final static int URGENT_LEVEL_2 = 2;
    public final static int URGENT_LEVEL_3 = 3;
    public final static int URGENT_LEVEL_4 = 4;
    public final static int URGENT_LEVEL_DEFAULT = 0;

    public final static class LiveVideoDownLoadFile {
        //下载的url
        final String url;
        //下载文件的文件夹
        final File inDir;
        //文件名称
        final File inFile;
        //文件夹路径，文件路径
        final String inDirPath, inFilePath;
        final int deleteOldFileCategary;

        final Executor executor;
        //解压的路径
        final File outFileDir;
//    final IDeleteFile iDeleteFile;
        /** md5校验，校验失败会删除重试 */
        final String md5;
        /**
         * 在下载池的唯一标识
         * tip:因为所有的文件都在一个下载池里面，可能同一场直播，
         * 每一个课件都需要这个文件，所以带上liveId和resourceId
         */
        final String onlyKey;
        /**
         * 是否需要插队，排在最前面下载
         */
        final int urgent;

        final DownloadListener downloadListener;

        final ZipExtractorTask zipExtractorTask;

        LiveVideoDownLoadFile(Builder builder) {
            this.url = builder.url;
            inDir = builder.inDir;
            this.inFile = inDir;
            this.inDirPath = builder.inDirPath;
            this.inFilePath = builder.inFilePath;
            if (builder.executor != null) {
                this.executor = builder.executor;
            } else {
                executor = DefaultPoolExecutor.getInstance();
            }
            this.deleteOldFileCategary = builder.deleteOldFileCategary;
            this.urgent = builder.urgent;
            if (builder.onlyKey != null) {
                this.onlyKey = builder.onlyKey;
            } else {
                this.onlyKey = this.url;
            }
            if (builder.outFileDir != null) {
                this.outFileDir = builder.outFileDir;
            } else {
                this.outFileDir = inFile;
            }
            if (TextUtils.isEmpty(builder.md5)) {
                this.md5 = builder.md5;
            } else {
                this.md5 = null;
            }
            if (builder.downloadListener != null) {
                this.downloadListener = builder.downloadListener;
            } else {
                this.downloadListener = null;
            }
            if (builder.zipExtractorTask != null) {
                this.zipExtractorTask = builder.zipExtractorTask;
            } else {
                this.zipExtractorTask = null;
            }
        }

        //不删除文件
        public static final int NO_DELETE = 0;

        //使用默认策略删除文件
        public static final int DELETE_NEW_DEFAULT = 1;

        //使用自定义方式删除文件
        public static final int DELETE_NEW_CUSTOM = 2;

        static class Builder {
            //下载的url
            String url;
            //下载文件的文件夹
            File inDir;
            //文件名称
            File inFile;
            //文件夹路径，文件路径
            String inDirPath, inFilePath;
            int deleteOldFileCategary;

            Executor executor;

            File outFileDir;
//        IDeleteFile iDeleteFile;
            /** md5校验，校验失败会删除重试 */
            String md5;
            /**
             * 在下载池的唯一标识
             * tip:因为所有的文件都在一个下载池里面，可能同一场直播，
             * 每一个课件都需要这个文件，所以带上liveId和resourceId
             */
            String onlyKey;
            /**
             * 是否需要插队，排在最前面下载
             */
            int urgent;

            DownloadListener downloadListener;

            ZipExtractorTask zipExtractorTask;

            public Builder setUrl(String url) {
                this.url = url;
                return this;
            }

            public Builder setInDir(File inDir) {
                this.inDir = inDir;
                return this;
            }

            public Builder setInFile(File inFile) {
                this.inFile = inFile;
                return this;
            }

            public Builder setInDirPath(String inDirPath) {
                this.inDirPath = inDirPath;
                return this;
            }

            public Builder setInFilePath(String inFilePath) {
                this.inFilePath = inFilePath;
                return this;
            }

            public Builder setDeleteOldFileCategary(int deleteOldFileCategary) {
                this.deleteOldFileCategary = deleteOldFileCategary;
                return this;
            }

            public Builder setExecutor(Executor executor) {
                this.executor = executor;
                return this;
            }

            public Builder setOutFileDir(File outFileDir) {
                this.outFileDir = outFileDir;
                return this;
            }

            public Builder setMd5(String md5) {
                this.md5 = md5;
                return this;
            }

            public Builder setOnlyKey(String onlyKey) {
                this.onlyKey = onlyKey;
                return this;
            }

            public Builder setUrgent(int urgent) {
                this.urgent = urgent;
                return this;
            }

            public Builder setDownloadListener(DownloadListener downloadListener) {
                this.downloadListener = downloadListener;
                return this;
            }

            public LiveVideoDownLoadFile build() {
                return new LiveVideoDownLoadFile(this);
            }
        }

        static class UnZipFile {
            File outFile;
            File outDir;
            String outDirPath, outFileName;
        }

        public interface IDeleteFile {
            void deleteFile();
        }

        public static class DefaultDeleteFile implements IDeleteFile {

            @Override
            public void deleteFile() {

            }
        }

        /**
         * 同步删除文件
         */
        private void deleteSync() {

        }

        //异步删除文件
        private void deleteAsync() {

        }
    }
}
