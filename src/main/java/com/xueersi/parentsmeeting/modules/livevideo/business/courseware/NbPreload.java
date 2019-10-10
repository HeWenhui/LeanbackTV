package com.xueersi.parentsmeeting.modules.livevideo.business.courseware;

import com.xueersi.common.network.download.DownloadListener;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;

public class NbPreload {
    private static String TAG = "NbPreload";
    private static Logger logger = LoggerFactory.getLogger(TAG);

    public static class NbDownLoadListener implements DownloadListener {
        private static String TAG = "NbPreload";
        private static Logger logger = LoggerFactory.getLogger(TAG);

        @Override
        public void onStart(String url) {
            logger.i("onStart:" + url);
        }

        @Override
        public void onProgressChange(long currentLength, long fileLength) {
            logger.i("currentLength:" + currentLength + " fileLength:" + fileLength);
        }

        @Override
        public void onSuccess(String folderPath, String fileName) {
            logger.i("folderPath:" + folderPath + " fileName:" + fileName);
        }

        @Override
        public void onFail(int errorCode) {
            logger.i("errorCode:" + errorCode);
        }

        @Override
        public void onFinish() {
            logger.i("onFinish");
        }
    }
}
