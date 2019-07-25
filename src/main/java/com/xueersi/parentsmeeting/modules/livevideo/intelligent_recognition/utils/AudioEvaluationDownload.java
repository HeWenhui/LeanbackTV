package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AudioEvaluationDownload {
//    private volatile static AudioEvaluationDownload instance;
//
//    public static AudioEvaluationDownload getInstance() {
//        if (instance == null) {
//            synchronized (AudioEvaluationDownload.class) {
//                if (instance == null) {
//                    instance = new AudioEvaluationDownload();
//                }
//            }
//        }
//        return instance;
//    }
//
//    private LocalFileRespository respository;
//
//    public void startDownLoad(Context context, String path) {
//        if (respository == null) {
//            respository = new LocalFileRespository(context, path);
//        }

//    }

//    public Observable<String> startRxDownLoad(Context context, String filePath, String url) {
//        Observable.zip(startDownLoad(context, filePath, url),
//                Observable.just(url),
//                new BiFunction<String, String, Object>() {
//                })
//    }

//    private class RxDownLoadFileEntity {
//        File file;
//        String url;
//
//        public RxDownLoadFileEntity(File file, String url) {
//            this.file = file;
//            this.url = url;
//        }
//
//        public File getFile() {
//            return file;
//        }
//
//        public void setFile(File file) {
//            this.file = file;
//        }
//
//        public String getUrl() {
//            return url;
//        }
//
//        public void setUrl(String url) {
//            this.url = url;
//        }
//    }

    public static Observable<String> startDownLoad(Context context, String filePath, final String url) {
        return Observable.
                just(filePath).
                map(new Function<String, File>() {
                    @Override
                    public File apply(String s) throws Exception {
                        return new File((s));
                    }
                }).
                doOnNext(new Consumer<File>() {
                    @Override
                    public void accept(File file) throws Exception {
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                    }
                }).
                flatMap(new Function<File, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(File file) throws Exception {
                        return getRxStorageFile(url, file);
                    }
                }).
                subscribeOn(Schedulers.io());
    }

    private static Observable<String> getRxStorageFile(String url, final File file) {
        return Observable.just(url).subscribeOn(Schedulers.io()).doOnNext(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Request request = new Request.Builder().url(s).build();
                OkHttpClient okHttpClient = new OkHttpClient();
                try {
                    Response response = okHttpClient.newCall(request).execute();
                    storageFile(response, file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static void storageFile(Response response, File file) throws IOException {
//        final File file = new File(getExternalCacheDir(), "unit3d");
        if (!file.exists()) {
            file.createNewFile();
        }
//        Log.e(TAG, "accept: atomic" + atomicInteger.getAndIncrement());
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len = 0;
        FileOutputStream fos = null;
        try {
            long total = response.body().contentLength();
//            Log.e(TAG, "total------>" + total);
            long current = 0;
            is = response.body().byteStream();
            fos = new FileOutputStream(file);
            while ((len = is.read(buf)) != -1) {
                current += len;
                fos.write(buf, 0, len);
//                Log.e(TAG, "current------>" + current);
//                                progressCallBack(total, current, callBack);
            }
            fos.flush();
//                            successCallBack((T) file, callBack);
        } catch (IOException e) {
//            Log.e(TAG, e.toString());
//                            failedCallBack("下载失败", callBack);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
//                Log.e(TAG, e.toString());
            }
        }
    }
}
