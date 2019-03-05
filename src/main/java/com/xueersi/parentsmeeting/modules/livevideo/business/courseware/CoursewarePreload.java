package com.xueersi.parentsmeeting.modules.livevideo.business.courseware;

import android.content.Context;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.network.download.DownLoadInfo;
import com.xueersi.common.network.download.DownLoader;
import com.xueersi.common.network.download.DownloadListener;
import com.xueersi.lib.framework.utils.file.FileUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.entity.CoursewareInfoEntity;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;
import com.xueersi.parentsmeeting.modules.livevideo.util.ZipExtractorTask;
import com.xueersi.parentsmeeting.modules.livevideo.util.ZipProg;

import org.xutils.xutils.common.util.MD5;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import ren.yale.android.cachewebviewlib.utils.MD5Utils;


/**
 * Created by: WangDe on 2019/2/27
 */
public class CoursewarePreload {

    String TAG = "CoursewarePreload";
    Logger logger = LoggerFactory.getLogger(TAG);
    private Context mContext;
    private String liveId;
    private int mSubject = -1;
    private LiveHttpManager mHttpManager;
    private LiveHttpResponseParser liveHttpResponseParser;
    File cacheFile;
    private File todayCacheDir;
    public static String mPublicCacheoutName = "publicRes";
    public static int mDownloadThreadCount = 1;

    public CoursewarePreload(Context context, String liveId, int subject) {
        mContext = context;
        this.liveId = liveId;
        mSubject = subject;
        liveHttpResponseParser = new LiveHttpResponseParser(context);
        cacheFile = LiveCacheFile.geCacheFile(mContext, "webviewCache");
        logger.d("cache path :" + cacheFile.getAbsolutePath());
        if (!cacheFile.exists()) {
            cacheFile.mkdirs();
        }
    }

    public void setmHttpManager(LiveHttpManager mHttpManager) {
        this.mHttpManager = mHttpManager;
    }

    List<CoursewareInfoEntity> courseWareInfos = new CopyOnWriteArrayList<>();

    /**
     * 获取课件信息
     */
    public void getCoursewareInfo() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        Date date = new Date();
        final String today = dateFormat.format(date);
        todayCacheDir = new File(cacheFile, today);
        //根据传liveid来判断 不为空或者不是""则为直播进入下载资源，否则为学习中心进入下载资源
        if (liveId != null && !"".equals(liveId)) {
            if (0 == mSubject) {//理科
                mHttpManager.getScienceCourewareInfo(liveId, new CoursewareHttpCallBack());
            } else if (1 == mSubject) {//英语
                mHttpManager.getEnglishCourewareInfo(liveId, new CoursewareHttpCallBack());
            } else if (2 == mSubject) {//语文
                mHttpManager.getArtsCourewareInfo(liveId, new CoursewareHttpCallBack());
            }
        } else {//下载当天所有课件资源
            mHttpManager.getScienceCourewareInfo("", new CoursewareHttpCallBack());
            mHttpManager.getEnglishCourewareInfo("", new CoursewareHttpCallBack());
            mHttpManager.getArtsCourewareInfo("", new CoursewareHttpCallBack());
        }
    }


    class CoursewareHttpCallBack extends HttpCallBack {
        @Override
        public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
            CoursewareInfoEntity coursewareInfoEntity = liveHttpResponseParser.parseCoursewareInfo(responseEntity);
            courseWareInfos.add(coursewareInfoEntity);
            if (courseWareInfos.size() == 3) {
                execDownLoad(
                        sortArrays(),
                        mergeList(courseWareInfos, 1),
                        mergeList(courseWareInfos, 2),
                        mergeList(courseWareInfos, 3));
            }
        }

        @Override
        public void onPmFailure(Throwable error, String msg) {
            super.onPmFailure(error, msg);
        }

        @Override
        public void onPmError(ResponseEntity responseEntity) {
            super.onPmError(responseEntity);
        }
    }

    private List<String> mergeList(List<CoursewareInfoEntity> coursewareInfoEntities, int type) {
        List<String> ansList = new LinkedList<>();
//        HashSet<String> ansSet = new HashSet<>();
        for (CoursewareInfoEntity coursewareInfoEntity : coursewareInfoEntities) {
            if (type == 1) {
                ansList = appendList(ansList, coursewareInfoEntity.getCdns());
            } else if (type == 2) {
                ansList = appendList(ansList, coursewareInfoEntity.getIps());
            } else if (type == 3) {
                ansList = appendList(ansList, coursewareInfoEntity.getResources());
            }
        }
        return ansList;
    }

    /**
     * 对数组进行排序
     *
     * @return
     */
    private List<CoursewareInfoEntity.LiveCourseware> sortArrays() {
        List<CoursewareInfoEntity.LiveCourseware> liveCoursewares = new LinkedList<>();

        for (CoursewareInfoEntity coursewareInfoEntity : courseWareInfos) {
            liveCoursewares.addAll(coursewareInfoEntity.getCoursewaresList());
        }
        Collections.sort(liveCoursewares, new Comparator<CoursewareInfoEntity.LiveCourseware>() {
            @Override
            public int compare(CoursewareInfoEntity.LiveCourseware liveCourseware, CoursewareInfoEntity.LiveCourseware t1) {
                if (liveCourseware.getStime() < t1.getStime()) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        return liveCoursewares;
    }

    private List<CoursewareInfoEntity.ItemCoursewareInfo> mergeList(List<CoursewareInfoEntity.LiveCourseware> coursewares) {
        List<CoursewareInfoEntity.ItemCoursewareInfo> itemCourseware = new LinkedList<>();
        for (CoursewareInfoEntity.LiveCourseware liveCourseware : coursewares) {
            itemCourseware.addAll(liveCourseware.getCoursewareInfos());
        }
        return itemCourseware;
    }

    private List<String> appendList(List<String> totalList, List<String> list) {
        for (String item : list) {
            if (!totalList.contains(item)) {
                totalList.add(item);
            }
        }
        return totalList;
    }

    private void execDownLoad(List<CoursewareInfoEntity.LiveCourseware> liveCoursewares, List<String> cdns, List<String> ips, List<String> resources) {
        //直播资源列表
        //cdns列表
//        final List<String> cdns = coursewareInfoEntity.getCdns();
//        List<String> ips = coursewareInfoEntity.getIps();
//        List<String> resources = coursewareInfoEntity.getResources();
//            final List<String> loadpages = coursewareInfoEntity.getLoadpages();
//            List<String> staticSources = coursewareInfoEntity.getStaticSources();
        //下载公共资源
        downloadResources(resources, cdns, ips);
        exeDownLoadCourseware(liveCoursewares, cdns, ips);

    }

    /**
     * //下载异一场直播的课件资源
     *
     * @param liveCoursewares
     * @param cdns
     * @param ips
     */
    private void exeDownLoadCourseware(List<CoursewareInfoEntity.LiveCourseware> liveCoursewares, List<String> cdns, List<String> ips) {

//        List<CoursewareInfoEntity.LiveCourseware> liveCoursewareList = liveCoursewares;
//        for (int i = 0; i < liveCoursewares.size(); i++) {
        for (CoursewareInfoEntity.LiveCourseware liveCourseware : liveCoursewares) {
            //课件列表
//            CoursewareInfoEntity.LiveCourseware liveCourseware = liveCoursewares.get(i);
            List<CoursewareInfoEntity.ItemCoursewareInfo> coursewareInfos = liveCourseware.getCoursewareInfos();
            File todayLiveCacheDir = new File(todayCacheDir, liveCourseware.getLiveId());
            boolean exists = todayLiveCacheDir.exists();
            boolean mkdirs = false;
            if (!todayLiveCacheDir.exists()) {
                mkdirs = todayLiveCacheDir.mkdirs();
            }
            logger.d("getCourseWareUrl:exists=" + exists + ",mkdirs=" + mkdirs);
            downloadCourseware(todayLiveCacheDir, coursewareInfos, ips, cdns);
        }
    }

    private int resourceTextPos = 0, courseWarePos = 0;

    /**
     * 下载每一个单独的课件资源
     *
     * @param path
     * @param coursewareInfos
     * @param ips
     * @param cdns
     */
    private void downloadCourseware(File path, List<CoursewareInfoEntity.ItemCoursewareInfo> coursewareInfos, List<String> ips, List<String> cdns) {

        final File mMorecachein = new File(path, liveId);
        if (!mMorecachein.exists()) {
            mMorecachein.mkdirs();
        }
        final File mMorecacheout = new File(path, liveId + "child");
        if (!mMorecacheout.exists()) {
            mMorecacheout.mkdirs();
        }
        //拼接ip
        String ip = "http://" + ips.get(getPos(ips));
        //截取host
        int index = cdns.get((getPos(cdns))).indexOf("/") + 2;
        String cdn = cdns.get(getPos(cdns)).substring(index);
//        for (int j = 0; j < coursewareInfos.size(); j++) {
        for (CoursewareInfoEntity.ItemCoursewareInfo coursewareInfo : coursewareInfos) {
//            CoursewareInfoEntity.ItemCoursewareInfo coursewareInfo = coursewareInfos.get((courseWarePos++) % courseWareInfos.size());
            //下载课件资源
            final String resourceName = MD5.md5(coursewareInfo.getResourceUrl()) + ".zip";
            File resourceSave = new File(mMorecachein, resourceName);
            boolean equals = false;
            if (fileIsExists(resourceSave.getAbsolutePath())) {
                String filemd5 = FileUtils.getFileMD5ToString(resourceSave);
                equals = coursewareInfo.getMd5().equalsIgnoreCase(filemd5);
            }
            if (!fileIsExists(resourceSave.getAbsolutePath()) || (fileIsExists(resourceSave.getAbsolutePath()) && !equals)) {
                DownLoadInfo resourceDownLoadInfo = DownLoadInfo.createFileInfo(ip + coursewareInfo.getResourceUrl(), mMorecachein.getAbsolutePath(), resourceName + ".temp", coursewareInfo.getMd5());
                resourceDownLoadInfo.setHost(cdn);
//                DownLoader resourceDownLoader = new DownLoader(mContext, resourceDownLoadInfo);
//                resourceDownLoader.setDownloadThreadCount(mDownloadThreadCount);
                logger.d("courseware url path:  " + ip + coursewareInfo.getResourceUrl() + "   file name:" + resourceName + ".zip");
//                resourceDownLoader.start(new ZipDownloadListener(mMorecachein, mMorecacheout, resourceName, ips, cdns, coursewareInfo.getResourceUrl(), coursewareInfo.getMd5(), new AtomicInteger()));
                PreLoadDownLoaderManager.addToAutoDownloadPool(resourceDownLoadInfo, new ZipDownloadListener(mMorecachein, mMorecacheout, resourceName, ips, cdns, coursewareInfo.getResourceUrl(), coursewareInfo.getMd5(), new AtomicInteger(0)));
            }
            //下载模板资源
            final String templateName = MD5.md5(coursewareInfo.getTemplateUrl()) + ".zip";
            File templateSave = new File(mMorecachein, resourceName);
            if (!fileIsExists(templateSave.getAbsolutePath())) {
                DownLoadInfo templateDownLoadInfo = DownLoadInfo.createFileInfo(ip + coursewareInfo.getTemplateUrl(), mMorecachein.getAbsolutePath(), templateName + ".temp", "");
                logger.d("template url path:  " + ip + coursewareInfo.getTemplateUrl() + "   file name:" + templateName + ".zip");
                templateDownLoadInfo.setHost(cdn);
//                DownLoader templateDownLoader = new DownLoader(mContext, templateDownLoadInfo);
//                templateDownLoader.setDownloadThreadCount(mDownloadThreadCount);
//                templateDownLoader.start(new ZipDownloadListener(mMorecachein, mMorecacheout, templateName, ips, cdns, coursewareInfo.getTemplateUrl(), coursewareInfo.getMd5(), new AtomicInteger()));

                PreLoadDownLoaderManager.addToAutoDownloadPool(templateDownLoadInfo, new ZipDownloadListener(mMorecachein, mMorecacheout, resourceName, ips, cdns, coursewareInfo.getTemplateUrl(), coursewareInfo.getMd5(), new AtomicInteger(0)));
            }
        }
    }


    private List<DownLoadInfo> courseWareDownLoadInfos;

    private List<ZipDownloadListener> courseWareDownListeners;

    private List<DownLoadInfo> resourcesDownLoadInfos;


    /**
     * 下载公共资源
     *
     * @param resourseInfos
     * @param ips
     * @param cdns
     */
    private void downloadResources(List<String> resourseInfos, List<String> cdns, List<String> ips) {
        final File mPublicCacheout = new File(cacheFile, mPublicCacheoutName);
        if (!mPublicCacheout.exists()) {
            mPublicCacheout.mkdirs();
        }
        String ip = "http://" + ips.get((int) (Math.random() * ips.size()));
        int cdnIndex = (int) (Math.random() * (cdns.size()));
        int subIndex = cdns.get(cdnIndex).indexOf("/") + 2;
        String cdn = cdns.get(cdnIndex).substring(subIndex);
        if (resourseInfos.size() > 0) {
//            for (int i = 0; i < resourseInfos.size(); i++) {
            for (String url : resourseInfos) {
//                String url = resourseInfos.get(i);
                final AtomicInteger downTryCount = new AtomicInteger();
                if (url.endsWith(".zip")) {
                    final String fileName;
                    int index = url.lastIndexOf("/");
                    if (index != -1) {
                        fileName = url.substring(index + 1);
                    } else {
                        fileName = MD5Utils.getMD5(url);
                    }
                    final File save = new File(mPublicCacheout, fileName);
                    if (!fileIsExists(save.getAbsolutePath())) {
                        logger.d("resource zip url path:  " + ip + url + "   file name:" + fileName + ".zip");
                        DownLoadInfo downLoadInfo = DownLoadInfo.createFileInfo(ip + url, mPublicCacheout.getAbsolutePath(), fileName + ".temp", "");
                        downLoadInfo.setHost(cdn);
//                        final DownLoader downLoader = new DownLoader(mContext, downLoadInfo);
//                        downLoader.start(new ZipDownloadListener(mPublicCacheout, mPublicCacheout, fileName, ips, cdns, url, "", downTryCount));
                        PreLoadDownLoaderManager.addToAutoDownloadPool(downLoadInfo, new ZipDownloadListener(save, save, fileName, ips, cdns, url, fileName, new AtomicInteger(0)));
                    }
                } else {
                    String fileName = MD5Utils.getMD5(url);
                    final File save = new File(mPublicCacheout, fileName);
                    if (!fileIsExists(save.getPath())) {
                        logger.d("resource ttf url path:  " + ip + url + "   file name:" + fileName + ".zip");
                        DownLoadInfo downLoadInfo = DownLoadInfo.createFileInfo(ip + url, mPublicCacheout.getAbsolutePath(), fileName + ".temp", "");
//                        final DownLoader downLoader = new DownLoader(mContext, downLoadInfo);
                        PreLoadDownLoaderManager.addToAutoDownloadPool(downLoadInfo);
//                        downLoader.start(new NoZipDownloadListener(mPublicCacheout, mPublicCacheout, fileName, ips, cdns, url, "", downTryCount));
                    }
                }
            }
        }
    }


    private int getPos(List list) {
        return (int) ((Math.random() * list.size()) % list.size());
    }

    private class Progresses implements ZipProg {
        @Override
        public void onProgressUpdate(Integer... values) {

        }

        @Override
        public void onPostExecute(Exception exception) {

        }

        @Override
        public void setMax(int max) {

        }
    }

    class ZipDownloadListener implements DownloadListener {

        private File mMorecacheout;
        private File mMorecachein;
        private String mFileName;
        private String url;
        private String md5;
        AtomicInteger downTryCount;
        List<String> cdns;
        List<String> ips;

        public ZipDownloadListener(File mMorecachein, File mMorecacheout, String fileName, List<String> ips, List<String> cdns, String url, String md5, AtomicInteger downTryCount) {
            this.mMorecachein = mMorecachein;
            this.mMorecacheout = mMorecacheout;
            this.mFileName = fileName;
            this.url = url;
            this.md5 = md5;
            this.ips = ips;
            this.cdns = cdns;
            this.downTryCount = downTryCount;
        }

        @Override
        public void onStart(String url) {

        }

        @Override
        public void onProgressChange(long currentLength, long fileLength) {

        }

        @Override
        public void onSuccess(String folderPath, String fileName) {
            logger.d("download success");
            File tempFile = new File(folderPath, fileName);
            File file = new File(folderPath, mFileName);
            boolean rename = tempFile.renameTo(file);
            new ZipExtractorTask(file, mMorecacheout, true, new Progresses()).execute();
        }

        @Override
        public void onFail(int errorCode) {
            String ip = "http://" + ips.get((int) Math.random() * (ips.size()));
            int tryCount = downTryCount.get();
            downTryCount.getAndIncrement();
            logger.d("download fail trycount" + tryCount);
            if (tryCount < cdns.size()) {
                int index = cdns.get(tryCount).indexOf("/") + 2;
                DownLoadInfo downLoadInfo = DownLoadInfo.createFileInfo(ip + url, mMorecachein.getAbsolutePath(), mFileName + ".temp", md5);
                logger.d("fail url path:  " + ip + url + "   file name:" + mFileName + ".zip");
                downLoadInfo.setHost(cdns.get(tryCount).substring(index));
                DownLoader downLoader = new DownLoader(mContext, downLoadInfo);
                downLoader.start(new ZipDownloadListener(mMorecachein, mMorecacheout, mFileName, ips, cdns, url, md5, downTryCount));
            }

        }

        @Override
        public void onFinish() {
        }
    }

    class NoZipDownloadListener implements DownloadListener {
        private File mMorecacheout;
        private File mMorecachein;
        private String mFileName;
        private String url;
        private String md5;
        AtomicInteger downTryCount;
        List<String> cdns;
        List<String> ips;

        public NoZipDownloadListener(File mMorecachein, File mMorecacheout, String fileName, List<String> ips, List<String> cdns, String url, String md5, AtomicInteger downTryCount) {
            this.mMorecachein = mMorecachein;
            this.mMorecacheout = mMorecacheout;
            this.mFileName = fileName;
            this.url = url;
            this.md5 = md5;
            this.ips = ips;
            this.cdns = cdns;
            this.downTryCount = downTryCount;
        }

        @Override
        public void onStart(String url) {

        }

        @Override
        public void onProgressChange(long currentLength, long fileLength) {

        }

        @Override
        public void onSuccess(String folderPath, String fileName) {
            logger.d("download ttf success");
            File file = new File(folderPath, fileName);
            file.renameTo(new File(folderPath, mFileName));
        }

        @Override
        public void onFail(int errorCode) {
            String ip = "http://" + ips.get((int) Math.random() * (ips.size()));
            int tryCount = downTryCount.get();
            downTryCount.getAndIncrement();
            logger.d("download fail trycount" + tryCount);
            if (tryCount < cdns.size()) {
                int index = cdns.get(tryCount).indexOf("/") + 2;
                DownLoadInfo downLoadInfo = DownLoadInfo.createFileInfo(ip + url, mMorecachein.getAbsolutePath(), mFileName + ".temp", md5);
                logger.d("fail url path:  " + cdns.get(tryCount) + url + "   file name:" + mFileName + ".zip");
                downLoadInfo.setHost(cdns.get(tryCount).substring(index));
                DownLoader downLoader = new DownLoader(mContext, downLoadInfo);
                downLoader.start(new NoZipDownloadListener(mMorecachein, mMorecacheout, mFileName, ips, cdns, url, md5, downTryCount));
            }

        }

        @Override
        public void onFinish() {

        }
    }

    //判断文件是否存在
    public boolean fileIsExists(String strFile) {
        try {
            File f = new File(strFile);
            if (!f.exists()) {
                return false;
            }

        } catch (Exception e) {
            return false;
        }

        return true;
    }


}
