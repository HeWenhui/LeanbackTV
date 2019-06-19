package com.xueersi.parentsmeeting.modules.livevideo.question.web;

import android.content.Context;

import java.io.File;

/**
 * Created by linyuqiang on 2019/4/1.
 * 新课件预加载-小组互动
 */
public class GroupCourseCache extends NewCourseCache {
    private File mMorecacheout;
    private boolean newCourse;

    public GroupCourseCache(Context mContext, String liveId, String testid, boolean newCourse) {
        super(mContext, liveId, testid);
        this.newCourse = newCourse;
        mMorecacheout = new File(todayLiveCacheDir, liveId + "artschild");
    }

//    @Override
//    public WebResourceResponse shouldInterceptRequest(WebView view, String s) {
//        WebResourceResponse webResourceResponse1 = super.shouldInterceptRequest(view, s);
//        if (webResourceResponse1 != null) {
//            return webResourceResponse1;
//        }
//        if (s.endsWith("ArialRoundedMTBold.ttf")) {
//            InputStream inputStream = null;
//            try {
//                String extension = MimeTypeMap.getFileExtensionFromUrl(s.toLowerCase());
//                String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
//                inputStream = AssertUtil.open("fangzhengcuyuan.ttf");
//                WebResourceResponse webResourceResponse = new WebResourceResponse(mimeType, "", new WrapInputStream(mContext, inputStream));
//                webResourceResponse.setResponseHeaders(header);
//                logger.d("shouldInterceptRequest:Bold.ttf");
//                return webResourceResponse;
////            return inputStream;
//            } catch (Exception e) {
//                e.printStackTrace();
//                logger.d("shouldInterceptRequest", e);
//            }
//        }
//        return null;
//    }

    protected File getCourseWarePagesFileName(String s, String contens, int index) {
        if (!newCourse) {
            String url2 = s.substring(index + contens.length());
            int index2 = url2.indexOf("?");
            if (index2 != -1) {
                url2 = url2.substring(0, index2);
            }
            File file = new File(mMorecacheout, url2);
            if (file.exists()) {
                return file;
            }
        }
        return super.getCourseWarePagesFileName(s, contens, index);
    }
}
