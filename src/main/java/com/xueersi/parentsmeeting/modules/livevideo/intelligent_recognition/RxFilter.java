package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition;

import android.text.TextUtils;

import java.io.File;

import io.reactivex.functions.Predicate;

public class RxFilter {
    //检查是否为空
    public static <T> Predicate<T> filterNull() {
        return new Predicate<T>() {
            @Override
            public boolean test(T s) throws Exception {
                return s != null;
            }
        };
    }

    public static Predicate<Boolean> filterTrue() {
        return new Predicate<Boolean>() {
            @Override
            public boolean test(Boolean aBoolean) throws Exception {
                return aBoolean;
            }
        };
    }

    public static Predicate<File> filterFile() {
        return new Predicate<File>() {
            @Override
            public boolean test(File file) throws Exception {
                return file != null && file.exists();
            }
        };
    }

    public static Predicate<String> filterString() {
        return new Predicate<String>() {
            @Override
            public boolean test(String s) throws Exception {
                return !TextUtils.isEmpty(s);
            }
        };
    }
}
