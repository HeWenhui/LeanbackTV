package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class CommonRxObserver<T> implements Observer<T> {
    protected Disposable disposable;

    @Override
    public void onSubscribe(Disposable d) {
        disposable = d;
    }

    @Override
    public void onNext(T o) {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onComplete() {

    }
}
