package com.xueersi.parentsmeeting.modules.livevideo.util;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;

import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by linyuqiang on 2018/6/8.
 */

public class HardWareUtil {
    private static String TAG = "HardWareUtil";
    protected static Logger logger = LiveLoggerFactory.getLogger(TAG);

    /**
     * 获得cpu型号名字
     *
     * @return
     */
    public static String getCpuName() {
        try {
            FileReader fr = new FileReader("/proc/cpuinfo");
            BufferedReader br = new BufferedReader(fr);
            String text;
            while ((text = br.readLine()) != null) {
                if (text.contains("Hardware")) {
                    int index = text.indexOf(":");
                    String cpu;
                    if (index == -1) {
                        cpu = text.substring(8);
                    } else {
                        cpu = text.substring(index + 1);
                    }
                    cpu = cpu.trim();
                    logger.d("getCpuName:text=" + text + ",cpu=" + cpu);
                    return cpu;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //华为手机获取不到,取系统变量
        //三星
        //Build.HARDWARE qcom
        //Build.BOARD msm8998
        //华为
        //Build.HARDWARE kirin970
        //Build.BOARD BLA
        return Build.HARDWARE;
    }

    /**
     * 获得cpu使用率-app的
     *
     * @return
     */
    public static float getProcessCpuRate() {
        float totalCpuTime1 = allCpuTime();
        float processCpuTime1 = processCpuTime();
        try {
            Thread.sleep(360);
        } catch (Exception e) {
        }
        float totalCpuTime2 = allCpuTime();
        float processCpuTime2 = processCpuTime();
        float cpuRate = 100 * (processCpuTime2 - processCpuTime1)
                / (totalCpuTime2 - totalCpuTime1);
        return cpuRate;
    }

    /**
     * 获得总的cpu时间
     *
     * @return
     */
    private static long allCpuTime() {
        String[] cpuInfos = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/proc/stat")), 1000);
            String load = reader.readLine();
            reader.close();
            cpuInfos = load.split(" ");
        } catch (IOException ex) {
            logger.e("IOException" + ex.toString());
            return 0;
        }
        long totalCpu = 0;
        try {
            totalCpu = Long.parseLong(cpuInfos[2])
                    + Long.parseLong(cpuInfos[3]) + Long.parseLong(cpuInfos[4])
                    + Long.parseLong(cpuInfos[6]) + Long.parseLong(cpuInfos[5])
                    + Long.parseLong(cpuInfos[7]) + Long.parseLong(cpuInfos[8]);
        } catch (ArrayIndexOutOfBoundsException e) {
            logger.i("ArrayIndexOutOfBoundsException" + e.toString());
            return 0;
        }
        return totalCpu;
    }

    /**
     * 获得app的 cpu时间
     *
     * @return
     */
    private static long processCpuTime() {
        String[] cpuInfos = null;
        try {
            int pid = android.os.Process.myPid();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/proc/" + pid + "/stat")), 1000);
            String load = reader.readLine();
            reader.close();
            cpuInfos = load.split(" ");
        } catch (IOException e) {
            logger.e("IOException" + e.toString());
            return 0;
        }
        long appCpuTime = 0;
        try {
            appCpuTime = Long.parseLong(cpuInfos[13])
                    + Long.parseLong(cpuInfos[14]) + Long.parseLong(cpuInfos[15])
                    + Long.parseLong(cpuInfos[16]);
        } catch (ArrayIndexOutOfBoundsException e) {
            logger.i("ArrayIndexOutOfBoundsException" + e.toString());
            return 0;
        }
        return appCpuTime;
    }

    private static boolean CPU_FAIL = false;

    /**
     * 获得cpu使用率-总的
     *
     * @return
     */
    public static double getCPURateDesc() {
        if (CPU_FAIL) {
            return 0;
        }
        String path = "/proc/stat";// 系统CPU信息文件
        long totalJiffies[] = new long[2];
        long totalIdle[] = new long[2];
        int firstCPUNum = 0;//设置这个参数，这要是防止两次读取文件获知的CPU数量不同，导致不能计算。这里统一以第一次的CPU数量为基准
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        String regex = " [0-9]+";
        Pattern pattern = Pattern.compile(regex);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 2; i++) {
            totalJiffies[i] = 0;
            totalIdle[i] = 0;
            try {
                fileReader = new FileReader(path);
                bufferedReader = new BufferedReader(fileReader, 8192);
                int currentCPUNum = 0;
                String str;
                while ((str = bufferedReader.readLine()) != null && (i == 0 || currentCPUNum < firstCPUNum)) {
                    stringBuilder.append("i=" + i + "---" + str + "\n");
                    if (str.toLowerCase().startsWith("cpu")) {
                        currentCPUNum++;
                        int index = 0;
                        Matcher matcher = pattern.matcher(str);
                        while (matcher.find()) {
                            try {
                                long tempJiffies = Long.parseLong(matcher.group(0).trim());
                                totalJiffies[i] += tempJiffies;
                                if (index == 3) {//空闲时间为该行第4条栏目
                                    totalIdle[i] += tempJiffies;
                                }
                                index++;
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (i == 0) {
                        firstCPUNum = currentCPUNum;
                        try {//暂停50毫秒，等待系统更新信息。
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                CPU_FAIL = false;
            } catch (IOException e) {
                CPU_FAIL = true;
                // TODO Auto-generated catch block
                logger.d("getCPURateDesc", e);
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        double rate = 0;
        if (totalJiffies[0] > 0 && totalJiffies[1] > 0 && totalJiffies[0] != totalJiffies[1]) {
            rate = 100.0 * ((totalJiffies[1] - totalIdle[1]) - (totalJiffies[0] - totalIdle[0])) / (totalJiffies[1] - totalJiffies[0]);
        }
//        return String.format("cpu:%.2f", rate);
        boolean error = false;
        if (rate > 100) {
            rate = 100;
            error = true;
        } else if (rate < 0) {
            rate = 0;
            error = false;
        }
        if (error) {
            logger.d("getCPURateDesc:stringBuilder=" + stringBuilder);
        }
        return rate;
    }

    /**
     * 手机可用内存,单位bytes
     *
     * @param context
     * @return
     */
    public static long getAvailMemory(Context context) {// 获取android当前可用内存大小
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (am == null) {
            return 0;
        }
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        //mi.availMem; 当前系统的可用内存

        return mi.availMem;// 将获取的内存大小规格化
    }

    /** 手机总内存,单位kb */
    private static int TOTAL_RAM = 0;

    /**
     * 手机总内存,单位kb
     * 可能返回0
     *
     * @return
     */
    public static int getTotalRam() {//GB
        if (TOTAL_RAM != 0) {
            return TOTAL_RAM;
        }
        String path = "/proc/meminfo";
        String firstLine = null;
        int totalRam = 0;
        try {
            FileReader fileReader = new FileReader(path);
            BufferedReader br = new BufferedReader(fileReader, 8192);
            firstLine = br.readLine().split("\\s+")[1];
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (firstLine != null) {
            try {
                totalRam = (int) Math.ceil((Float.valueOf(firstLine).doubleValue()));
            } catch (Exception e) {
                LiveCrashReport.postCatchedException(new LiveException(TAG, e));
            }
        }
        TOTAL_RAM = totalRam;
        return totalRam;
    }
}
