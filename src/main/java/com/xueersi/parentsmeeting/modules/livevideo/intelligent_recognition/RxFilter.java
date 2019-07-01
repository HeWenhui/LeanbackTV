package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition;

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
}
