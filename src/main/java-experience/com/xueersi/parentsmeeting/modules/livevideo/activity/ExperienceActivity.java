package com.xueersi.parentsmeeting.modules.livevideo.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;

import com.xueersi.parentsmeeting.modules.livevideo.fragment.ExperStandRecordFragmentBase;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.ExperienceRecordFragmentBase;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.LiveBackVideoActivityBase;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.LiveBackVideoFragmentBase;

public class ExperienceActivity extends LiveBackVideoActivityBase {

    public static void intentTo(Activity context, Bundle bundle, String where, int requestCode) {
        Intent intent = new Intent(context, ExperienceActivity.class);
        intent.putExtras(bundle);
        intent.putExtra("where", where);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected LiveBackVideoFragmentBase getFragment() {
        if (true) {
            return new ExperStandRecordFragmentBase();
        }
        return new ExperienceRecordFragmentBase();
    }
}
