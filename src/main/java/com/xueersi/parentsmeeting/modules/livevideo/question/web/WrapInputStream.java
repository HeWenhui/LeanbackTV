package com.xueersi.parentsmeeting.modules.livevideo.question.web;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

public class WrapInputStream extends InputStream {
    String TAG = "WrapInputStream";
    InputStream inputStream;

    public WrapInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public int read() throws IOException {
        Log.d(TAG, "read1");
        return inputStream.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        Log.d(TAG, "read2");
        return inputStream.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        Log.d(TAG, "read3:off=" + off + ",len=" + len);
        return inputStream.read(b, off, len);
    }

    @Override
    public void close() throws IOException {
        Log.d(TAG, "close");
        inputStream.close();
    }

    @Override
    public long skip(long n) throws IOException {
        Log.d(TAG, "skip:n=" + n);
        return inputStream.skip(n);
    }

    @Override
    public int available() throws IOException {
        int available = inputStream.available();
        Log.d(TAG, "available:available=" + available);
        return available;
    }

    @Override
    public synchronized void mark(int readlimit) {
        Log.d(TAG, "mark");
        inputStream.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException {
        Log.d(TAG, "reset");
        inputStream.reset();
    }

    @Override
    public boolean markSupported() {
        Log.d(TAG, "markSupported");
        return inputStream.markSupported();
    }

}
