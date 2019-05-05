//package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.support.annotation.NonNull;
//
//import java.util.List;
//
//import androidx.work.Configuration;
//import androidx.work.impl.Processor;
//import androidx.work.impl.Scheduler;
//import androidx.work.impl.WorkDatabase;
//import androidx.work.impl.WorkManagerImpl;
//import androidx.work.impl.utils.taskexecutor.TaskExecutor;
//
//public class UploadVideoService extends WorkManagerImpl {
//    @SuppressLint("RestrictedApi")
//    public UploadVideoService(@NonNull Context context, @NonNull Configuration configuration, @NonNull TaskExecutor workTaskExecutor) {
//        super(context, configuration, workTaskExecutor);
//    }
//
//    @SuppressLint("RestrictedApi")
//    public UploadVideoService(@NonNull Context context, @NonNull Configuration configuration, @NonNull TaskExecutor workTaskExecutor, boolean useTestDatabase) {
//        super(context, configuration, workTaskExecutor, useTestDatabase);
//    }
//
//    @SuppressLint("RestrictedApi")
//    public UploadVideoService(@NonNull Context context, @NonNull Configuration configuration, @NonNull TaskExecutor workTaskExecutor, @NonNull WorkDatabase workDatabase, @NonNull List<Scheduler> schedulers, @NonNull Processor processor) {
//        super(context, configuration, workTaskExecutor, workDatabase, schedulers, processor);
//    }
//
//}
