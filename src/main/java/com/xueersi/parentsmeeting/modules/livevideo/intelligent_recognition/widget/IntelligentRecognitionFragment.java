package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.widget;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.IntelligentRecognitionRecord;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.IntelligentRecognitionPager;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.IntelligentRecognitionPresenter;

public class IntelligentRecognitionFragment extends Fragment {

    private Activity mActivity;

    private IntelligentRecognitionPresenter mPresenter;

    public static Fragment newInstance(Bundle bundle) {
        Fragment fragment = new IntelligentRecognitionFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    IntelligentRecognitionRecord mRecord;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        mRecord = getArguments().getParcelable("");

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        IntelligentRecognitionPager pager = new IntelligentRecognitionPager(mActivity);
        mPresenter = new IntelligentRecognitionPresenter(mActivity, pager);
        getLifecycle().addObserver(mPresenter);
        pager.setPresenter(mPresenter);
        return pager.getRootView();
    }


}
