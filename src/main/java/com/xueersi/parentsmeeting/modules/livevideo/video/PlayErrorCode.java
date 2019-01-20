package com.xueersi.parentsmeeting.modules.livevideo.video;

/**
 * Created by linyuqiang on 2018/8/6.
 * 黑屏错误码
 * <p>
 * 新版本PSIJK1.1版本已经不再使用这些状态码
 *
 */
public enum PlayErrorCode {
    PLAY_SERVER_CODE_101(101, "获取视频资源失败[101]"),
    PLAY_SERVER_CODE_102(102, "获取视频资源失败[102]，正在尝试重连…"),
    PLAY_SERVER_CODE_103(103, "获取视频资源失败[103]"),
    PLAY_TIMEOUT_300(300, "视频连接超时[300]，正在尝试重连…"),
    TEACHER_LEAVE_200(200, "教师不在直播间[200]"),
    PLAY_NO_WIFI(400, "连接超时，请检查网络[400]"),
    E2BIG(-7, -1, "Argument list too long", ""),
    EACCES(-13, -1, "Permission denied", ""),
    EAGAIN(-11, -1, "Resource temporarily unavailable", ""),
    EBADF(-9, -1, "Bad file descriptor", ""),
    EBUSY(-16, -1, "Device or resource busy", ""),
    ECHILD(-10, -1, "No child processes", ""),
    EDEADLK(-35, -1, "Resource deadlock avoided", ""),
    EDOM(-33, -1, "Numerical argument out of domain", ""),
    EEXIST(-17, -1, "File exists", ""),
    EFAULT(-14, -1, "Bad address", ""),
    EFBIG(-27, -1, "File too large", ""),
    EILSEQ(-84, -1, "Illegal byte sequence", ""),
    EINTR(-4, -1, "Interrupted system call", ""),
    EINVAL(-22, -1, "Invalid argument", ""),
    EIO(-5, 207, "I/O error", ""),
    EISDIR(-21, -1, "Is a directory", ""),
    EMFILE(-24, -1, "Too many open files", ""),
    EMLINK(-31, -1, "Too many links", ""),
    ENAMETOOLONG(-36, -1, "File name too long", ""),
    ENFILE(-23, -1, "Too many open files in system", ""),
    ENODEV(-19, -1, "No such device", ""),
    ENOENT(-2, -1, "No such file or directory", ""),
    ENOEXEC(-8, -1, "Exec format error", ""),
    ENOLCK(-37, -1, "No locks available", ""),
    ENOMEM(-12, -1, "Cannot allocate memory", ""),
    ENOSPC(-28, -1, "No space left on device", ""),
    ENOSYS(-38, -1, "Function not implemented", ""),
    ENOTDIR(-20, -1, "Not a directory", ""),
    ENOTEMPTY(-39, -1, "Directory not empty", ""),
    ENOTTY(-25, -1, "Inappropriate I/O control operation", ""),
    ENXIO(-6, -1, "No such device or address", ""),
    EPERM(-1, -1, "Operation not permitted", ""),
    EPIPE(-32, -1, "Broken pipe", ""),
    ERANGE(-34, -1, "Result too large", ""),
    EROFS(-30, -1, "Read-only file system", ""),
    ESPIPE(-29, -1, "Illegal seek", ""),
    ESRCH(-3, -1, "No such process", ""),
    EXDEV(-18, -1, "Cross-device link", ""),
    ENETDOWN(-100, -1, "Network is down", ""),
    ENETUNREACH(-101, -1, "Network is unreachable", ""),
    ENETRESET(-102, -1, "Network dropped connection on reset", ""),
    ECONNABORTED(-103, -1, "Software caused connection abort", ""),
    ETIMEDOUT(-110, -1, "Connection timed out", ""),
    ECONNREFUSED(-111, -1, "Connection refused", ""),
    EHOSTDOWN(-112, -1, "Host is down", ""),
    EHOSTUNREACH(-113, -1, "No route to host", ""),
    BSF_NOT_FOUND(-1179861752, 201, "BSF_NOT_FOUND", "Bitstream filter not found"),
    Internal_bug1(-558323010, 202, "BUG", "Internal bug , should not have happened"),
    Internal_bug2(-541545794, 217, "BUG2", "Internal bug , should not have happened"),
    BUFFER_TOO_SMALL(-1397118274, 203, "BUFFER_TOO_SMALL", "Buffer too small"),
    DECODER_NOT_FOUND(-1128613112, 204, "DECODER_NOT_FOUND", "Decoder not found"),
    DEMUXER_NOT_FOUND(-1296385272, 205, "DEMUXER_NOT_FOUND", "Demuxer not found"),
    ENCODER_NOT_FOUND(-1129203192, 206, "ENCODER_NOT_FOUND", "Encoder not found"),
    EOF(-541478725, 207, "EOF", "End of file"),
    EXIT(-1414092869, 208, "EXIT", "Immediate exit requested"),
    EXTERNAL(-542398533, 209, "EXTERNAL", "Generic error in an external library"),
    FILTER_NOT_FOUND(-1279870712, 210, "FILTER_NOT_FOUND", "Filter not found"),
    INPUT_CHANGED(-1668179713, -1, "INPUT_CHANGED", "Input changed"),
    INVALIDDATA(-1094995529, 211, "INVALIDDATA", "Invalid data found when processing input"),
    MUXER_NOT_FOUND(-1481985528, 212, "MUXER_NOT_FOUND", "Muxer not found"),
    OPTION_NOT_FOUND(-1414549496, 213, "OPTION_NOT_FOUND", "Option not found"),
    OUTPUT_CHANGED(-1668179714, 221, "OUTPUT_CHANGED", "Output changed"),
    PATCHWELCOME(-1163346256, -1, "PATCHWELCOME", "Not yet implemented in FFmpeg , patches welcome"),
    PROTOCOL_NOT_FOUND(-1330794744, 215, "PROTOCOL_NOT_FOUND", "Protocol not found"),
    STREAM_NOT_FOUND(-1381258232, 216, "STREAM_NOT_FOUND", "Stream not found"),
    UNKNOWN(-1313558101, 218, "UNKNOWN", "Unknown error occurred"),
    EXPERIMENTAL(-733130664, 219, "EXPERIMENTAL", "Experimental feature"),
    INPUT_AND_OUTPUT_CHANGED(-1668179713, 220, "INPUT_AND_OUTPUT_CHANGED", "Input and output changed"),
    HTTP_BAD_REQUEST(-808465656, 221, "HTTP_BAD_REQUEST", "Server returned 400 Bad Request"),
    HTTP_UNAUTHORIZED(-825242872, 223, "HTTP_UNAUTHORIZED", "Server returned 401 Unauthorized (authorization failed)"),
    HTTP_FORBIDDEN(-858797304, 224, "HTTP_FORBIDDEN", "Server returned 403 Forbidden (access denied)"),
    HTTP_NOT_FOUND(-875574520, 225, "HTTP_NOT_FOUND", "Server returned 404 Not Found"),
    HTTP_OTHER_4XX(-1482175736, 226, "HTTP_OTHER_4XX", "Server returned 4XX Client Error , but not one of 40{0','1','3','4}'"),
    HTTP_SERVER_ERROR(-1482175992, 227, "HTTP_SERVER_ERROR", "Server returned 5XX Server Error reply"),
    PLAY_UNKNOWN(-22222, "获取视频资源失败");
    int ffmpegCode = 0;
    int code;
    String tip;

    PlayErrorCode(int code, String tip) {
        this.code = code;
        this.tip = tip;
    }

    PlayErrorCode(int ffmpegCode, int code, String tag, String tip) {
        this.ffmpegCode = ffmpegCode;
        if (code == -1) {
            code = ffmpegCode;
        }
        this.code = code;
        this.tip = tip;
    }

    PlayErrorCode(int ffmpegCode, int code, String tip) {
        this.ffmpegCode = ffmpegCode;
        this.code = code;
        this.tip = tip;
    }

    public int getCode() {
        return code;
    }

    public String getTip() {
        return tip;
    }

    public static PlayErrorCode getError(int num) {
        PlayErrorCode[] es = values();
        for (int i = 0; i < es.length; i++) {
            if (es[i].ffmpegCode == num) {
                return es[i];
            }
        }
        PLAY_UNKNOWN.ffmpegCode = num;
        PLAY_UNKNOWN.code = num;
        return PLAY_UNKNOWN;
    }

}
