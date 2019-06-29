package com.xueersi.parentsmeeting.modules.livevideo.question.business;

/**
 * Created by linyuqiang on 2019/6/26.
 * 互动题是不是老师关闭
 */
public interface TeacherClose {
    boolean isWebViewCloseByTeacher();

    void setWebViewCloseByTeacher(boolean webViewCloseByTeacher);
}
