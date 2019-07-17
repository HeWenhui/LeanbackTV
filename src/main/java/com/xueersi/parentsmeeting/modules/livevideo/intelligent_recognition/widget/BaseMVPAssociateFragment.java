package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.widget;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.IntelligentRecognitionContract;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.IntelligentRecognitionContract.BasePresenter;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.IntelligentRecognitionContract.BaseView;

public abstract class BaseMVPAssociateFragment extends Fragment {

//    private FragmentActivity mActivity;

    protected final <V extends BaseView, P extends BasePresenter> void associatePV(V v, P p) {
        v.setPresenter(p);
        p.setView(v);
        if (p instanceof IntelligentLifecycleObserver) {
            getLifecycle().addObserver((IntelligentLifecycleObserver) p);
        }
//        addObserver(p);
    }

//    protected abstract <P extends BasePresenter>void addObserver(P p);

    protected <P extends IntelligentRecognitionContract.BasePresenter> void addObserver(P p) {
        if (p instanceof IntelligentLifecycleObserver) {
            getLifecycle().addObserver((IntelligentLifecycleObserver) p);
        }
    }

//    protected FragmentActivity getFMActivity() {
//        return mActivity;
//    }
}
