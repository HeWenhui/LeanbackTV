package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.utils;

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
        if (!disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    @Override
    public void onComplete() {
        if (!disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
