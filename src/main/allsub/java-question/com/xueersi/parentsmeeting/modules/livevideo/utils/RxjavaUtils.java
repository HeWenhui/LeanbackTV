package com.xueersi.parentsmeeting.modules.livevideo.utils;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class RxjavaUtils {
    public static class CommonRxObserver<T> implements Observer<T> {
        protected Disposable disposable;

        @Override
        public void onSubscribe(Disposable d) {
            this.disposable = d;
        }

        @Override
        public void onNext(T t) {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onComplete() {

        }
    }
}
