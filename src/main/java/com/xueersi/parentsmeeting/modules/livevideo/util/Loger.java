package com.xueersi.parentsmeeting.modules.livevideo.util;

import android.content.Context;
import android.util.Log;

import com.xueersi.lib.analytics.umsagent.UmsAgentManager;

import java.util.HashMap;
import java.util.Map;

/**
 * 调试日志打印的封装类
 */
public final class Loger {
    /** 是否打印日志 */
    private static boolean DEBUG = false;

    /** 默认的TAG */
    private static String sTag = "xes";

    /** 默认KEY */
    private static final String DEFAULT_KEY = "default_key";

    static String className;//类名
    static String methodName;//方法名
    static int lineNumber;//行数

    private static String createLog(String log ) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(methodName);
        buffer.append("(").append(className).append(":").append(lineNumber).append(")");
        buffer.append(log);
        return buffer.toString();
    }

    private static void getMethodNames(StackTraceElement[] sElements){
        className = sElements[1].getFileName();
        methodName = sElements[1].getMethodName();
        lineNumber = sElements[1].getLineNumber();
    }

    /**
     * 是否打印日志
     *
     * @return
     */
    public static boolean isDebug() {
        return DEBUG;
    }

    public static void setDebug(boolean isShowLog){
        DEBUG=isShowLog;
    }

    /**
     * verbose 任何信息，调试信息文字黑色
     *
     * @param msg 调试信息
     */
    public static void v(String msg) {
        if (DEBUG) {
            Log.v(sTag, msg);
        }
    }

    /**
     * verbose 任何信息，调试信息文字黑色
     *
     * @param tag 标签
     * @param msg 调试信息
     */
    public static void v(String tag, String msg) {
        if (DEBUG) {
            Log.v(tag, msg);
        }
    }

    /**
     * verbose 任何信息，调试信息文字黑色
     *
     * @param context
     * @param tag      标签
     * @param msg      调试信息
     * @param isRequst 是否上传大数据
     */
    public static void v(Context context, String tag, String msg, boolean isRequst) {
        v(tag, msg);
        if (isRequst) {
            Map<String, String> map = new HashMap<String, String>();
            map.put(DEFAULT_KEY, msg);
            UmsAgentManager.umsAgentDebug(context, tag, map);
        }
    }

    /**
     * verbose 任何信息，调试信息文字黑色
     *
     * @param tag       标签
     * @param mParams   调试信息map
     * @param isRequest 是否上传大数据
     */
    public static void v(Context context, String tag, Map<String, String> mParams, boolean isRequest) {
        v(tag, mParams.toString());
        if (isRequest) {
            UmsAgentManager.umsAgentDebug(context, tag, mParams);
        }
    }

    /**
     * verbose 任何信息，调试信息文字黑色
     *
     * @param msg 调试信息
     * @param tr  异常
     */
    public static void v(String msg, Throwable tr) {
        if (DEBUG) {
            Log.v(sTag, msg, tr);
        }
    }

    /**
     * verbose 任何信息，调试信息文字黑色
     *
     * @param tag 标签
     * @param msg 调试信息
     * @param tr  异常
     */
    public static void v(String tag, String msg, Throwable tr) {
        if (DEBUG) {
            Log.v(tag, msg, tr);
        }
    }

    /**
     * debug 调试信息, 调试信息文字蓝色
     *
     * @param message 调试信息
     */
    public static void d(String message) {
        if (DEBUG) {
            getMethodNames(new Throwable().getStackTrace());
            Log.d(className, createLog(message));
        }
    }

    /**
     * debug 调试信息, 调试信息文字蓝色
     *
     * @param message 调试信息
     * @param args    可变参数
     */
    public static void d(String message, Object... args) {
        if (DEBUG) {
            d(String.format(message, args));
        }
    }

    /**
     * debug 调试信息, 调试信息文字蓝色
     *
     * @param tag 标签
     * @param msg 调试信息
     */
    public static void d(String tag, String msg) {
        if (DEBUG) {
            getMethodNames(new Throwable().getStackTrace());
            Log.d(tag, createLog(msg));
        }
    }


    /**
     * debug 任何信息，调试信息文字黑色
     *
     * @param context
     * @param tag      标签
     * @param msg      调试信息
     * @param isRequst 是否上传大数据
     */
    public static void d(Context context, String tag, String msg, boolean isRequst) {
        d(tag, msg);
        if (isRequst) {
            Map<String, String> map = new HashMap<String, String>();
            map.put(DEFAULT_KEY, msg);
            UmsAgentManager.umsAgentDebug(context, tag, map);
        }
    }

    /**
     * debug 任何信息，调试信息文字黑色
     *
     * @param tag       标签
     * @param mParams   调试信息map
     * @param isRequest 是否上传大数据
     */
    public static void d(Context context, String tag, Map<String, String> mParams, boolean isRequest) {
        d(tag, mParams.toString());
        if (isRequest) {
            UmsAgentManager.umsAgentDebug(context, tag, mParams);
        }
    }

    /**
     * debug 调试信息, 调试信息文字蓝色
     *
     * @param msg 调试信息
     * @param tr  错误
     */
    public static void d(String msg, Throwable tr) {
        if (DEBUG) {
            Log.d(sTag, msg, tr);
        }
    }

    /**
     * debug 调试信息,调试信息文字蓝色
     *
     * @param tag 标签
     * @param msg 调试信息
     * @param tr  错误
     */
    public static void d(String tag, String msg, Throwable tr) {
        if (DEBUG) {
            Log.d(tag, msg, tr);
        }
    }

    /**
     * infomation 调试信息, 调试信息文字绿色
     *
     * @param msg 调试信息
     */
    public static void i(String msg) {
        if (DEBUG) {
            getMethodNames(new Throwable().getStackTrace());
            Log.i(className, createLog(msg));
        }
    }

    /**
     * infomation 调试信息, 调试信息文字绿色
     *
     * @param tag 标签
     * @param msg 调试信息
     */
    public static void i(String tag, String msg) {
        if (DEBUG) {
            getMethodNames(new Throwable().getStackTrace());
            Log.i(tag, createLog(msg));
        }
    }


    /**
     * infomation 任何信息，调试信息文字黑色
     *
     * @param context
     * @param tag      标签
     * @param msg      调试信息
     * @param isRequst 是否上传大数据
     */
    public static void i(Context context, String tag, String msg, boolean isRequst) {
        i(tag, msg);
        if (isRequst) {
            Map<String, String> map = new HashMap<String, String>();
            map.put(DEFAULT_KEY, msg);
            UmsAgentManager.umsAgentDebug(context, tag, map);
        }
    }

    /**
     * infomation 任何信息，调试信息文字黑色
     *
     * @param tag       标签
     * @param mParams   调试信息map
     * @param isRequest 是否上传大数据
     */
    public static void i(Context context, String tag, Map<String, String> mParams, boolean isRequest) {
        i(tag, mParams.toString());
        if (isRequest) {
            UmsAgentManager.umsAgentDebug(context, tag, mParams);
        }
    }

    /**
     * infomation 调试信息, 调试信息文字绿色
     *
     * @param msg 调试信息
     * @param tr  错误
     */
    public static void i(String msg, Throwable tr) {
        if (DEBUG) {
            Log.i(sTag, msg, tr);
        }
    }

    /**
     * infomation 调试信息, 调试信息文字绿色
     *
     * @param tag 标签
     * @param msg 调试信息
     * @param tr  错误
     */
    public static void i(String tag, String msg, Throwable tr) {
        if (DEBUG) {
            Log.i(tag, msg, tr);
        }
    }

    /**
     * warning 调试信息，调试信息文字橙色
     *
     * @param msg 调试信息
     */
    public static void w(String msg) {
        if (DEBUG) {
            Log.w(sTag, msg);
        }
    }

    /**
     * warning 调试信息，调试信息文字橙色
     *
     * @param tag 标签
     * @param msg 调试信息
     */
    public static void w(String tag, String msg) {
        if (DEBUG) {
            Log.w(tag, msg);
        }
    }

    /**
     * warning 任何信息，调试信息文字黑色
     *
     * @param context
     * @param tag      标签
     * @param msg      调试信息
     * @param isRequst 是否上传大数据
     */
    public static void w(Context context, String tag, String msg, boolean isRequst) {
        w(tag, msg);
        if (isRequst) {
            Map<String, String> map = new HashMap<String, String>();
            map.put(DEFAULT_KEY, msg);
            UmsAgentManager.umsAgentDebug(context, tag, map);
        }
    }

    /**
     * warning 任何信息，调试信息文字黑色
     *
     * @param tag       标签
     * @param mParams   调试信息map
     * @param isRequest 是否上传大数据
     */
    public static void w(Context context, String tag, Map<String, String> mParams, boolean isRequest) {
        w(tag, mParams.toString());
        if (isRequest) {
            UmsAgentManager.umsAgentDebug(context, tag, mParams);
        }
    }

    /**
     * warning 调试信息，调试信息文字橙色
     *
     * @param msg 调试信息
     * @param tr  错误
     */
    public static void w(String msg, Throwable tr) {
        if (DEBUG) {
            Log.w(sTag, msg, tr);
        }
    }

    /**
     * warning 调试信息，调试信息文字橙色
     *
     * @param tag 标签
     * @param msg 调试信息
     * @param tr  错误
     */
    public static void w(String tag, String msg, Throwable tr) {
        if (DEBUG) {
            Log.w(tag, msg, tr);
        }
    }

    /**
     * error 调试信息,调试信息文字红色
     *
     * @param msg 调试信息
     */
    public static void e(String msg) {
        if (DEBUG) {
            Log.e(sTag, msg);
        }
    }

    /**
     * error 调试信息,调试信息文字红色
     *
     * @param tag 标签
     * @param msg 调试信息
     */
    public static void e(String tag, String msg) {
        if (DEBUG) {
            Log.e(tag, msg);
        }
    }

    /**
     * error 调试信息,调试信息文字红色
     *
     * @param tag       标签
     * @param msg       调试信息
     * @param tr        异常
     * @param isRequest 是否上传
     */
    public static void e(Context context, String tag, String msg, Throwable tr, boolean isRequest) {
        if (DEBUG) {
            Log.e(tag, msg, tr);
        }
        if (isRequest) {
            UmsAgentManager.umsAgentException(context, tag + msg, tr);
        }
    }

    /**
     * error 任何信息，调试信息文字黑色
     *
     * @param context
     * @param tag      标签
     * @param msg      调试信息
     * @param isRequst 是否上传大数据
     */
    public static void e(Context context, String tag, String msg, boolean isRequst) {
        e(tag, msg);
        if (isRequst) {
            Map<String, String> map = new HashMap<String, String>();
            map.put(DEFAULT_KEY, msg);
            UmsAgentManager.umsAgentDebug(context, tag, map);
        }
    }

    /**
     * error 任何信息，调试信息文字黑色
     *
     * @param tag       标签
     * @param mParams   调试信息map
     * @param isRequest 是否上传大数据
     */
    public static void e(Context context, String tag, Map<String, String> mParams, boolean isRequest) {
        e(tag, mParams.toString());
        if (isRequest) {
            UmsAgentManager.umsAgentDebug(context, tag, mParams);
        }
    }

    /**
     * error 调试信息,调试信息文字红色
     *
     * @param msg 调试信息
     * @param tr  错误
     */
    public static void e(String msg, Throwable tr) {
        if (DEBUG) {
            Log.e(sTag, msg, tr);
        }
    }

    /**
     * error 调试信息,调试信息文字红色
     *
     * @param tag 标签
     * @param msg 调试信息
     * @param tr  错误
     */
    public static void e(String tag, String msg, Throwable tr) {
        if (DEBUG) {
            Log.e(tag, msg, tr);
        }
    }

    /**
     * error 调试信息,调试信息文字红色
     *
     * @param msg  调试信息
     * @param args 可变参数拼接字符串
     */
    public static void t(String msg, Object... args) {
        if (DEBUG) {
            Log.v(sTag, String.format(msg, args));
        }
    }
}