package com.xueersi.parentsmeeting.modules.livevideo.activity;

import android.app.FragmentManager;

import com.xueersi.parentsmeeting.modules.livevideo.fragment.ExperienceRecordFragmentBase;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.LiveBackVideoActivityBase;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.LiveBackVideoFragmentBase;

public class ExperienceActivity extends LiveBackVideoActivityBase {
    @Override
    protected LiveBackVideoFragmentBase getFragment() {
        return new ExperienceRecordFragmentBase();
    }
}
