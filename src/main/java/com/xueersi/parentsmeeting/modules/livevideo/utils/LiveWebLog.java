package com.xueersi.parentsmeeting.modules.livevideo.utils;

import com.xueersi.common.broswer.XrsX5Broswer;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.framework.utils.file.FileUtils;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveThreadPoolExecutor;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class LiveWebLog {

    public static void init(final String liveid) {
        LiveThreadPoolExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                XrsX5Broswer.XrsTbsLogClient tbsLogClient = XrsX5Broswer.getTbsLogClient();
                if (tbsLogClient != null) {
                    File dir = LiveCacheFile.geCacheFile(ContextManager.getContext(), "tbslog");
                    File uploadfile = new File(dir, "uploald" + System.currentTimeMillis() + ".txt");
                    File[] files = dir.listFiles();
                    if (files != null) {
                        for (int i = 0; i < files.length; i++) {
                            try {
                                File file = files[i];
                                if (!file.getName().startsWith("uploald")) {
                                    FileOutputStream fileOutputStream = new FileOutputStream(uploadfile, true);
                                    List<String> stringList = FileUtils.readFile2List(file, "utf-8");
                                    if (stringList != null) {
                                        fileOutputStream.write((file.getName() + "---------------\n").getBytes());
                                        for (int j = 0; j < stringList.size(); j++) {
                                            fileOutputStream.write((stringList.get(j) + "\n").getBytes());
                                        }
                                    }
                                    file.delete();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    int index = 0;
                    File file = new File(dir, "tbslog_" + liveid + "_" + index + ".txt");
                    while (file.exists()) {
                        index++;
                        file = new File(dir, "tbslog_" + liveid + "_" + index + ".txt");
                    }
                    tbsLogClient.setLogFile(file);
                }
            }
        });
    }
}
