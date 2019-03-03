package com.xueersi.parentsmeeting.modules.livevideo.question.web;

import android.util.Log;

import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class WrapInputStream extends InputStream {
    String TAG = "WrapInputStream";
    InputStream inputStream;
    Logger logger;

    public WrapInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
        logger = LiveLoggerFactory.getLogger(TAG);
        logger.d("read1");
    }

    @Override
    public int read() throws IOException {
        logger.d("read1");
        return inputStream.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        logger.d("read2");
        return inputStream.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        logger.d("read3:off=" + off + ",len=" + len);
        return inputStream.read(b, off, len);
    }

    @Override
    public void close() throws IOException {
        logger.d("close:class" + inputStream.getClass());
        inputStream.close();
    }

    @Override
    public long skip(long n) throws IOException {
        logger.d("skip:n=" + n);
        return inputStream.skip(n);
    }

    @Override
    public int available() throws IOException {
        int available = inputStream.available();
        logger.d("available:available=" + available);
        return available;
    }

    @Override
    public synchronized void mark(int readlimit) {
        logger.d("mark");
        inputStream.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException {
        logger.d("reset");
        inputStream.reset();
    }

    @Override
    public boolean markSupported() {
        logger.d("markSupported");
        return inputStream.markSupported();
    }

}
