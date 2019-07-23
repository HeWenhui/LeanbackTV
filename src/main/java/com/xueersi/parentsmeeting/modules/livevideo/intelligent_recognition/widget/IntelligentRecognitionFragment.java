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

import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.GoldTeamStatus;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.IntelligentLifecycleObserver;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.IntelligentRecognitionContract;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.content_view.IntelligentRecognitionPermissionPager;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.content_view.IntelligentRecognitionPresenter;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.top3.IntelligentRecognitionTop3View;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.viewmodel.IntelligentRecognitionViewModel;

public class IntelligentRecognitionFragment extends BaseMVPAssociateFragment {
    private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private FragmentActivity mActivity;

//    private IntelligentRecognitionRecord mRecord;

    private IntelligentRecognitionViewModel mViewModel;

    public static Fragment newInstance(Bundle bundle) {
        Fragment fragment = new IntelligentRecognitionFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public static Fragment newInstance() {
        Fragment fragment = new IntelligentRecognitionFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        logger.i("onAttach");
        mActivity = (FragmentActivity) context;
//        mRecord = getArguments().getParcelable("intelligentRecognitionRecord");
        mViewModel = ViewModelProviders.of(mActivity).get(IntelligentRecognitionViewModel.class);
//        mViewModel.setRecordData(mRecord);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_livevideo_english_inteliigent_recognition, container, false);

        viewGroup.addView(addContentView());
//        IntelligentRecognitionViewModel viewModel = ViewModelProviders.of(this).get(IntelligentRecognitionViewModel.class);

//        viewModel.getIeResultData().observe(this, new Observer<IEResult>() {
//            @Override
//            public void onChanged(@Nullable IEResult ieResult) {
//                handleResult(ieResult);
//            }
//        });
        logger.i("pagerView");
        return viewGroup;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        addObserver();
    }

    private View addContentView() {

        IntelligentRecognitionPermissionPager pager = new IntelligentRecognitionPermissionPager(mActivity);
//        pager.initView(inflater, container, false);
        IntelligentRecognitionPresenter mPresenter = new IntelligentRecognitionPresenter(mActivity);

        associatePV(pager, mPresenter);

        return pager.getRootView();
    }

    private boolean isTop3DataSuccess = false;

    private boolean isTop3TimeUp = false;

    private GoldTeamStatus goldTeamStatus;

    private void addObserver() {

        mViewModel.getIsTop3Show().observe(mActivity, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                isTop3TimeUp = aBoolean;
                performShowTop3();
            }
        });

        mViewModel.getIsTop3DataSuccess().observe(mActivity, new Observer<GoldTeamStatus>() {
            @Override
            public void onChanged(@Nullable GoldTeamStatus goldTeamStatus) {

                IntelligentRecognitionFragment.this.goldTeamStatus = goldTeamStatus;
                isTop3DataSuccess = true;
                performShowTop3();
            }
        });
    }
    /** 添加Top3的页面 */
    private void performShowTop3() {
        if (isTop3DataSuccess && isTop3TimeUp) {
            if (getView() == null) {
                return;
            }

            ViewGroup viewGroup = (ViewGroup) getView();
            IntelligentRecognitionTop3View top3View = new IntelligentRecognitionTop3View(mActivity, goldTeamStatus);

            ViewGroup.LayoutParams layoutParams = new ViewGroup.
                    LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            viewGroup.addView(top3View.getRootView(), layoutParams);
        }
    }

    @Override
    protected <P extends IntelligentRecognitionContract.BasePresenter> void addObserver(P p) {
        if (p instanceof IntelligentLifecycleObserver) {
            getLifecycle().addObserver((IntelligentLifecycleObserver) p);
        }
    }
}
