package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.widget;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.entity.IntelligentRecognitionRecord;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.IntelligentRecognitionContract.BasePresenter;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.IntelligentRecognitionContract.BaseView;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.IntelligentRecognitionPager;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.IntelligentRecognitionPresenter;

public class IntelligentRecognitionFragment extends Fragment {
    private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
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
        logger.i("onAttach");
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
        pager.initView(inflater, container,false);
        mPresenter = new IntelligentRecognitionPresenter(mActivity);
        associatePV(pager, mPresenter);
//        IntelligentRecognitionViewModel viewModel = ViewModelProviders.of(this).get(IntelligentRecognitionViewModel.class);

//        viewModel.getIeResultData().observe(this, new Observer<IEResult>() {
//            @Override
//            public void onChanged(@Nullable IEResult ieResult) {
//                handleResult(ieResult);
//            }
//        });
        logger.i("pagerView");
        return pager.getRootView();
    }

//    private void handleResult(IEResult ieResult) {
//
//    }

//    private <T extends BaseView> void craeteClass(Class<T> v, Class p) {
//        try {
//            v.newInstance();
//            p.newInstance();
//        } catch (java.lang.InstantiationException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
//    }

    protected final <V extends BaseView, P extends BasePresenter> void associatePV(V v, P p) {
        v.setPresenter(p);
        p.setView(v);
        if (p instanceof MyObserver) {
            getLifecycle().addObserver((MyObserver) p);
        }
    }
}
