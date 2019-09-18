package com.xueersi.parentsmeeting.modules.livevideo.liveLog.busiLog;

import android.util.Log;

import com.google.gson.Gson;
import com.hwl.log.xrsLog.XrsLogEntity;
import com.hwl.logan.SendLogRunnable;
import com.xueersi.common.logerhelper.XesLogEntity;
import com.xueersi.lib.framework.utils.string.MD5Utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;


/***
 *  直播业务日志上传
 */
public class LiveBusiLogSendLogRunnable extends SendLogRunnable {


    private static String LOGFILEPATH_1 = "/sdcard/xes/";
    private static String LOGFILEPATH = LOGFILEPATH_1 + "log/";
    private static String LOGFILEPATH_LOGIN = "/storage/emulated/0/Android/data/com.xueersi.parentsmeeting/files/log_v1";
    private static final String TAG = "LiveBusiLog";

    private static Gson gson = new Gson();

    public static int LOGTYPE_SYS = -1;
    private String mUploadLogUrl_sys = "https://appdj.xesimg.com/1001829/sys.gif";
    public static int LOGTYPE_PV = 0;
    private String mUploadLogUrl_pv = "https://appdj.xesimg.com/1001829/pv.gif";
    public static int LOGTYPE_CLICK = 1;
    private String mUploadLogUrl_click = "https://appdj.xesimg.com/1001829/click.gif";
    public static int LOGTYPE_SHOW = 2;
    private String mUploadLogUrl_show = "https://appdj.xesimg.com/1001829/show.gif";
    public static int LOGTYPE_LAUNCH = 3;
    private String mUploadLogUrl_launch = "https://appdj.xesimg.com/1001829/launch.gif";


    public static void setPath(String path) {

        //不同的进程，不同的管理文件
        LOGFILEPATH_LOGIN = path;
        LOGFILEPATH_1 = LOGFILEPATH_LOGIN + "xes/";
        LOGFILEPATH = LOGFILEPATH_1 + "log/";
        Log.d(TAG, "LOGFILEPATH_LOGIN:" + LOGFILEPATH_LOGIN);
    }


    @Override
    public void sendLog(File logFile) {

        File file = null;
        if (logFile.getName().contains("bury.copy")) {
            file = logFile;
        } else {
            file = logFile;
        }

        if (file == null) {
            return;
        }
        boolean success = doSendLogFilesByAction(file);
        Log.d(TAG, "日志上传测试结果：" + success);
        finish();
        if (logFile.getName().contains(".copy")) {
            logFile.delete();
            file.delete();
            //Log.d(TAG, "本地日志删除：" + "logFile：" + logFile.getAbsolutePath());
            if (logFile.exists()) {
                Log.d(TAG, "本地日志删除失败！");
            } else {
                Log.d(TAG, "本地日志删除成功！");
            }
            deleteDir(LOGFILEPATH_LOGIN);
        }
    }


    private HashMap<String, String> getActionHeader() {

        HashMap<String, String> map = new HashMap<>();
        map.put("Content-Type", "binary/octet-stream"); //二进制上传
        map.put("client", "android");


        String appID = "1001829";
        long currentStamp = System.currentTimeMillis();
        String appKey = "05cabf2fa971160fbedf6bc5954d1cc3";
        String sign = MD5Utils.disgest(appID + "&" + currentStamp + (appKey == null ? "" : appKey));

        map.put("X-Log-TimeStamp", currentStamp + "");
        map.put("X-Log-Sign", sign);
        map.put("X-Log-Appid", appID);

        return map;
    }

    /**
     * 主动上报
     */
    private boolean doSendLogFilesByAction(File logFile) {


        if (logFile.exists()) {
            Log.e(TAG, "loginFile is exit");
        }

        deleteDir(LOGFILEPATH);//清空临时文件夹
        String log = getFileContent(logFile);
        Log.e(TAG, "all_Log------------------:" + log);
        boolean isSuccess = false;
        try {

            //业务日志
            if (new File(LOGFILEPATH + LiveBusiLogSendLogRunnable.LOGTYPE_SYS + ".txt").exists()) {
                Log.e(TAG, "日志-------requset--(-1)------------------");
                FileInputStream fileStream1 = new FileInputStream(new File(LOGFILEPATH + -1 + ".txt"));
                boolean backData1 = doXrsPostRequest(mUploadLogUrl_sys, fileStream1, getActionHeader());
                new File(LOGFILEPATH + LiveBusiLogSendLogRunnable.LOGTYPE_SYS + ".txt").delete();
                isSuccess = backData1;
            }

            //pv
            if (new File(LOGFILEPATH + LiveBusiLogSendLogRunnable.LOGTYPE_PV + ".txt").exists()) {
                //Log.e(TAG, "页面日志-------requset--1------------------");
                FileInputStream fileStream1 = new FileInputStream(new File(LOGFILEPATH + 0 + ".txt"));
                boolean backData1 = doXrsPostRequest(mUploadLogUrl_pv, fileStream1, getActionHeader());
                new File(LOGFILEPATH + LiveBusiLogSendLogRunnable.LOGTYPE_PV + ".txt").delete();
                isSuccess = backData1;
            }

            //click
            if (new File(LOGFILEPATH + LiveBusiLogSendLogRunnable.LOGTYPE_CLICK + ".txt").exists()) {
                //Log.e(TAG, "click日志-------requset--2------------------");
                FileInputStream fileStream2 = new FileInputStream(new File(LOGFILEPATH + 1 + ".txt"));
                boolean backData2 = doXrsPostRequest(mUploadLogUrl_click, fileStream2, getActionHeader());
                new File(LOGFILEPATH + LiveBusiLogSendLogRunnable.LOGTYPE_CLICK + ".txt").delete();
                isSuccess = backData2;
            }
            //show
            if (new File(LOGFILEPATH + LiveBusiLogSendLogRunnable.LOGTYPE_SHOW + ".txt").exists()) {
                //Log.e(TAG, "show日志-------requset--3------------------");
                FileInputStream fileStream3 = new FileInputStream(new File(LOGFILEPATH + 2 + ".txt"));
                boolean backData3 = doXrsPostRequest(mUploadLogUrl_show, fileStream3, getActionHeader());
                new File(LOGFILEPATH + LiveBusiLogSendLogRunnable.LOGTYPE_SHOW + ".txt").delete();
                isSuccess = backData3;
            }
            //launch
            if (new File(LOGFILEPATH + LiveBusiLogSendLogRunnable.LOGTYPE_LAUNCH + ".txt").exists()) {
                //Log.e(TAG, "launch日志-------requset--4-----------------");
                FileInputStream fileStream4 = new FileInputStream(new File(LOGFILEPATH + 3 + ".txt"));
                boolean backData4 = doXrsPostRequest(mUploadLogUrl_launch, fileStream4, getActionHeader());
                new File(LOGFILEPATH + LiveBusiLogSendLogRunnable.LOGTYPE_LAUNCH + ".txt").delete();
                isSuccess = backData4;
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return isSuccess;
    }


    private boolean doXrsPostRequest(String url, InputStream inputData, Map<String, String> headerMap) {


        boolean data = false;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        HttpURLConnection c = null;
        ByteArrayOutputStream back;
        byte[] Buffer = new byte[2048];
        try {
            URL u = new URL(url);
            c = (HttpURLConnection) u.openConnection();
            if (c instanceof HttpsURLConnection) {
                ((HttpsURLConnection) c).setHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
            }
            Set<Map.Entry<String, String>> entrySet = headerMap.entrySet();
            for (Map.Entry<String, String> tempEntry : entrySet) {
                c.addRequestProperty(tempEntry.getKey(), tempEntry.getValue());
            }
            c.setReadTimeout(15000);
            c.setConnectTimeout(60);
            c.setDoInput(true);
            c.setDoOutput(true);
            c.setRequestMethod("POST");
            outputStream = c.getOutputStream();
            int i;
            Log.e(TAG, "read start-------");
            while ((i = inputData.read(Buffer)) != -1) {
                //Log.e("stone", "buffer:-------pre");
                outputStream.write(Buffer, 0, i);
                //Log.e("stone", "buffer:-------end");
            }
            outputStream.flush();
            int res = c.getResponseCode();
            if (res == 200) {
                Log.e(TAG, "res code:------200,url:" + url);
                data = true;
            } else {
                Log.e(TAG, "res code:-----error,code=" + res);
            }

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputData != null) {
                try {
                    inputData.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (c != null) {
                c.disconnect();
            }
        }
        return data;
    }


    //生成文件

    private File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    //生成文件夹
    private static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.i("error:", e + "");
        }
    }


    // 将字符串写入到文本文件中
    private void writeTxtToFile(String strcontent, String filePath, String fileName) {
        //生成文件夹之后，再生成文件，不然会出错
        makeFilePath(filePath, fileName);
        String strFilePath = filePath + fileName;
        // 每次写入时，都换行写
        String strContent = strcontent + "\r\n";
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                Log.d(TAG, "Create the file:" + strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
            Log.e(TAG, "Error on write File:" + e);
        }
    }


    //读取指定目录下的所有TXT文件的文件内容
    private String getFileContent(File file) {

        String content = "";
        if (!file.isDirectory()) {  //检查此路径名的文件是否是一个目录(文件夹)
            //if (file.getName().endsWith("txt") || file.getName().endsWith(".copy")) {//文件格式为""文件
            try {
                InputStream instream = new FileInputStream(file);
                if (instream != null) {
                    InputStreamReader inputreader
                            = new InputStreamReader(instream, "UTF-8");
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line = "";
                    //分行读取
                    while ((line = buffreader.readLine()) != null) {

                        swichLineLogToLogFile(line);
                        content += line + "\n";
                        if (!line.contains("clogan header")) {
                            Log.e(TAG, "lineLog:" + line);
                        }
                    }
                    instream.close();//关闭输入流
                }
            } catch (FileNotFoundException e) {
                Log.d(TAG, "The File doesn't not exist.");
            } catch (IOException e) {
                Log.d(TAG, e.getMessage());
            }
            // }
        }
        return content;
    }


    /**
     * 分别将日志传入不同的日志文件
     *
     * @param lineLog
     * @return
     */
    private XesLogEntity swichLineLogToLogFile(String lineLog) {

        try {
            XesLogEntity entity = new Gson().fromJson(lineLog, XesLogEntity.class);
            int type = entity.f;
            Object data = entity.c;
            try {
                if (type == 0) {
                    String buryString = gson.toJson(data);

                    //LiveBusiLogEntity bury = gson.fromJson(buryString, LiveBusiLogEntity.class);
                    //writeToFile(bury, buryString);

                    XrsLogEntity log = gson.fromJson(buryString, XrsLogEntity.class);
                    writeToFile(log, buryString);

                }
            } catch (Exception e) {
                Log.e(TAG, "Exception lineLogqq:" + lineLog);
                //CrashReport.postCatchedException(new BuryException("Exception:" + lineLog, e));
                Log.e(TAG, "Exception lineLogqq exception:" + e.getMessage());
            }
            return entity;


        } catch (Exception e) {
            e.printStackTrace();
            //CrashReport.postCatchedException(new BuryException("" + lineLog, e));
            Log.e(TAG, "Exception lineLog:" + lineLog);
            Log.e(TAG, "Exception lineLog exception:" + e.getMessage());

        }
        return null;
    }


    public String fileName() {
        return "";
    }

    /**
     * 按照日志类型，分别写入各自类型文件
     */
    private void writeToFile(LiveBusiLogEntity bury, String log) {
        String fileName = bury.logType + ".txt";

        Log.e(TAG, "livebusiLogInfo:" + log);
        writeTxtToFile(log, LOGFILEPATH, fileName);

    }

    /**
     * 按照日志类型，分别写入各自类型文件
     */
    private void writeToFile(XrsLogEntity bury, String log) {
        String fileName = bury.type + ".txt";

        Log.e(TAG, "livebusiLogInfo:" + log);
        writeTxtToFile(log, LOGFILEPATH, fileName);

    }

    public static boolean deleteDir(String path) {
        File file = new File(path);
        if (!file.exists()) {//判断是否待删除目录是否存在
            System.err.println("The dir are not exists!");
            return false;
        }

        String[] content = file.list();//取得当前目录下所有文件和文件夹
        for (String name : content) {
            File temp = new File(path, name);
            if (temp.isDirectory()) {//判断是否是目录
                deleteDir(temp.getAbsolutePath());//递归调用，删除目录里的内容
                temp.delete();//删除空目录
            } else {
                if (!temp.delete()) {//直接删除文件
                    System.err.println("Failed to delete " + name);
                }
            }
        }
        return true;
    }

}
