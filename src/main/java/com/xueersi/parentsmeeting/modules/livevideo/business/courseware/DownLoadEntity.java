package com.xueersi.parentsmeeting.modules.livevideo.business.courseware;

import com.xueersi.common.network.download.DownloadListener;

import java.io.File;

public class DownLoadEntity<T extends DownloadListener> {

    private String url;
    /** 存在本地的位置 */
    private File inFile;
    /** 解压的位置 */
    private File outFile;
    /** 是否是需要紧急下载(插队下载) */
    private boolean isUrgent;
    /** 是否需要解压 */
    private boolean isUpZip;
    /** 下载池子的唯一标识 */
    private String downloadPoolKey;

    private T downListener;

    private DownLoadEntity(DownLoadBuilder builder) {
        this.url = builder.url;
        this.inFile = builder.inFile;
        this.outFile = builder.outFile;
        this.isUpZip = builder.isUpZip;
        this.isUrgent = builder.isUrgent;
        this.downloadPoolKey = builder.downloadPoolKey;
        if (downloadPoolKey == null) {
            this.downloadPoolKey = builder.url;
        }
        try {
            this.downListener = (T) builder.downListener;
        } catch (Exception e) {

        }
    }

    public void execute() {

    }

    public static class DownLoadBuilder<T extends DownloadListener> {

        private String url;
        /** 存在本地的位置 */
        private File inFile;
        /** 解压的位置 */
        private File outFile;
        /** 是否是需要紧急下载(插队下载) */
        private boolean isUrgent;
        /** 是否需要解压 */
        private boolean isUpZip;
        /** 下载池子的唯一标识 */
        private String downloadPoolKey;
        /** 下载监听器 */
        private T downListener;

        public DownLoadBuilder setUrl(String url) {
            this.url = url;
            return this;
        }

        public String getUrl() {
            return url;
        }

        public File getInFile() {
            return inFile;
        }

        public DownLoadBuilder setInFile(File inFile) {
            this.inFile = inFile;
            return this;
        }

        public File getOutFile() {
            return outFile;
        }

        public DownLoadBuilder setOutFile(File outFile) {
            this.outFile = outFile;
            return this;
        }

        public String isDownloadPoolKey() {
            return downloadPoolKey;
        }

        public DownLoadBuilder setDownloadPoolKey(String downloadPoolKey) {
            this.downloadPoolKey = downloadPoolKey;
            return this;
        }

        public DownLoadEntity build() {
            if (url == null) throw new IllegalStateException("url == null");
            return new DownLoadEntity(this);
        }

        public T getDownLIstener() {
            return downListener;
        }

        public DownLoadBuilder setDownLIstener(T downLIstener) {
            this.downListener = downLIstener;
            return this;
        }
    }
}
