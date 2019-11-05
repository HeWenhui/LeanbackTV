package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.widget;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.app.job.JobWorkItem;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class UploadVideoJob extends JobScheduler {
    @Override
    public int schedule(@NonNull JobInfo job) {
        return 0;
    }

    @Override
    public int enqueue(@NonNull JobInfo job, @NonNull JobWorkItem work) {
        return 0;
    }

    @Override
    public void cancel(int jobId) {

    }

    @Override
    public void cancelAll() {

    }

    @NonNull
    @Override
    public List<JobInfo> getAllPendingJobs() {
        return null;
    }

    @Nullable
    @Override
    public JobInfo getPendingJob(int jobId) {
        return null;
    }
}
