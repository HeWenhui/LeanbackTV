package com.xueersi.parentsmeeting.modules.livevideo.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;

import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
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
        int pattern = getIntent().getIntExtra("pattern", LiveVideoConfig.LIVE_PATTERN_COMMON);
        logger.d("getFragment:pattern=" + pattern);
        if (pattern == LiveVideoConfig.LIVE_PATTERN_2) {
            return new ExperStandRecordFragmentBase();
        } else if (pattern == LiveVideoConfig.LIVE_TYPE_HALFBODY) {
            return new ExperStandRecordFragmentBase();
        }
        return new ExperienceRecordFragmentBase();
    }
}
