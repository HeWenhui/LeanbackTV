package com.xueersi.parentsmeeting.modules.livevideo.business.courseware;

import com.alibaba.android.arouter.thread.DefaultPoolExecutor;

import java.io.File;
import java.util.concurrent.Executor;

public class LiveVideoDownLoadUtils {
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

    File outFile;
    IDeleteFile iDeleteFile;
    /** md5校验，校验失败会删除重试 */
    int md5;
    /**
     * 在下载池的唯一标识
     * tip:因为所有的文件都在一个下载池里面，可能同一场直播，
     * 每一个课件都需要这个文件，所以带上liveId和resourceId
     */
    String onlyKey;
    /**
     * 是否需要插队，排在最前面下载
     */
    boolean isUrgent;
    private static volatile LiveVideoDownLoadUtils mUtils;

    private LiveVideoDownLoadUtils(Builder builder) {
        if (executor == null) {
            executor = DefaultPoolExecutor.getInstance();
        }
        if (deleteOldFileCategary == DELETE_NEW_CUSTOM) {

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

        File outFile;
        IDeleteFile iDeleteFile;
        /** md5校验，校验失败会删除重试 */
        int md5;
        /**
         * 在下载池的唯一标识
         * tip:因为所有的文件都在一个下载池里面，可能同一场直播，
         * 每一个课件都需要这个文件，所以带上liveId和resourceId
         */
        String onlyKey;
        /**
         * 是否需要插队，排在最前面下载
         */
        boolean isUrgent;
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
