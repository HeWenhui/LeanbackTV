package com.xueersi.parentsmeeting.modules.livevideo.util;

import android.content.Context;

import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/** 存类传对象，暂时方案，以后改类 */
public class ObjectUtils {
    private static String TAG = "ObjectUtils";
    private static Logger logger = LoggerFactory.getLogger(TAG);

    static {
        logger.setLogMethod(false);
    }

    public static void test() {
        TestObj testObj = new TestObj();
        testObj.a = "aaa";
        testObj.strings.add("aaa");
        testObj.map.put("aaa", "aaa");
        String path = saveObj(ContextManager.getContext(), testObj);
        FileObj fileObj = getSaveObj(path);
        logger.d("test");
    }

    public static String saveObj(Context context, Object object) {
        if (object == null) {
            return null;
        }
        try {
            File dir = new File(context.getCacheDir(), "livesave");
            if (!dir.exists()) {
                dir.mkdirs();
            } else {
//                File[] fs = dir.listFiles();
//                if (fs != null) {
//                    for (int i = 0; i < fs.length; i++) {
//
//                    }
//                }
            }
            File file = new File(dir, "" + object.hashCode());
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
            logger.d("saveObj:file=" + file + ",length=" + file.length());
            return file.getPath();
        } catch (Exception e) {
            logger.d("saveObj:e=", e);
            LiveCrashReport.postCatchedException(new LiveException(TAG, e));
        }
        return null;
    }

    public static FileObj getSaveObj(String filePath) {
        if (filePath == null) {
            return null;
        }
        try {
            FileObj fileObj = new FileObj();
            File file = new File(filePath);
            FileInputStream fileOutputStream = new FileInputStream(file);
            ObjectInputStream objectOutputStream = new ObjectInputStream(fileOutputStream);
            Object object = objectOutputStream.readObject();
            fileObj.object = object;
            fileObj.length = file.length();
            logger.d("getSaveObj:file=" + file.getPath() + ",length=" + file.length() + ",object=" + object);
            return fileObj;
        } catch (Exception e) {
            logger.d("getSaveObj:e=", e);
            LiveCrashReport.postCatchedException(new LiveException(TAG, e));
        }
        return null;
    }

    public static class FileObj implements Serializable {
        public Object object;
        public long length = 1;
    }

    static class TestObj implements Serializable {
        String a;
        ArrayList<String> strings = new ArrayList<>();
        HashMap<String, String> map = new HashMap<>();
    }
}
