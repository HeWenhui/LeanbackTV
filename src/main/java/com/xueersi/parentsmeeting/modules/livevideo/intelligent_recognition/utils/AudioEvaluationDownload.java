package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Request;
import okhttp3.Response;

public class AudioEvaluationDownload {

    /**
     * 如果本地文件{new File(filePath)}不存在，则从url下载到本地
     *
     * @param filePath
     * @param url
     * @return
     */
    public static Observable<String> startDownLoad(String filePath, final String url) {
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
//                Request request = new Request.Builder().url(s).build();
//                OkHttpClient okHttpClient = OkhttpUtils.getOkHttpClient();
                try {
//                    Response response = OkhttpUtils.getOkHttpClient().newCall(new Request.Builder().url(s).build()).execute();
                    storageFile(
                            OkhttpUtils.getOkHttpClient().newCall(
                                    new Request.Builder().url(s).build()).
                                    execute(),
                            file);
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
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len = 0;
        FileOutputStream fos = null;
        try {
            long total = response.body().contentLength();
            long current = 0;
            is = response.body().byteStream();
            fos = new FileOutputStream(file);
            while ((len = is.read(buf)) != -1) {
                current += len;
                fos.write(buf, 0, len);
//                                progressCallBack(total, current, callBack);
            }
            fos.flush();
        } catch (IOException e) {
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
            }
        }
    }
}
