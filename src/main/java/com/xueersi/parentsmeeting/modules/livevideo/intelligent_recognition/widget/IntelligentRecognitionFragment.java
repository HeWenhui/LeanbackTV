package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.widget;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.IEResult;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.IntelligentRecognitionRecord;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.entity.IntelligentRecognitionViewModel;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.IntelligentRecognitionPager;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.IntelligentRecognitionPresenter;

public class IntelligentRecognitionFragment extends Fragment {

    private FragmentActivity mActivity;

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
        mActivity = (FragmentActivity) context;
        mRecord = getArguments().getParcelable("intelligentRecognitionRecord");
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
        IntelligentRecognitionViewModel viewModel = ViewModelProviders.of(this).get(IntelligentRecognitionViewModel.class);

        viewModel.getIeResultData().observe(this, new Observer<IEResult>() {
            @Override
            public void onChanged(@Nullable IEResult ieResult) {
                handleResult(ieResult);
            }
        });
        return pager.getRootView();
    }

    private void handleResult(IEResult ieResult) {

    }

}
