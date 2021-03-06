 package com.xueersi.parentsmeeting.modules.livevideo.question.web;

import android.content.Context;
import android.os.SystemClock;

import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.question.InputStreamClose;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by linyuqiang on 2019/3/5.
 * 包裹一个输入流，可以进行关闭流
 */
public class WrapInputStream extends InputStream {
    private String TAG = "WrapInputStream";
    private InputStream inputStream;
    private Logger logger;
    private LogToFile logToFile;
    private InputStreamClose inputStreamClose;
    private long startTime = -1;
    private String readMethod = "";

    public WrapInputStream(Context context, InputStream inputStream) {
        this.inputStream = inputStream;
        logger = LiveLoggerFactory.getLogger(TAG);
        logToFile = new LogToFile(context, TAG);
        logToFile.d("WrapInputStream:inputStream=" + inputStream);
    }

    public void setInputStreamClose(InputStreamClose inputStreamClose) {
        this.inputStreamClose = inputStreamClose;
    }

    @Override
    public int read() throws IOException {
        if (startTime == -1) {
            startTime = SystemClock.elapsedRealtime();
            readMethod = "read1";
        }
        logger.d("read1");
        try {
            return inputStream.read();
        } catch (IOException e) {
            logToFile.e("read1", e);
            throw e;
        }
    }

    @Override
    public int read(byte[] b) throws IOException {
        if (startTime == -1) {
            startTime = SystemClock.elapsedRealtime();
            readMethod = "read2";
        }
        logger.d("read2");
        return inputStream.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (startTime == -1) {
            startTime = SystemClock.elapsedRealtime();
            readMethod = "read3:off=" + off + ",len=" + len;
        }
        logger.d("read3:off=" + off + ",len=" + len);
        try {
            return inputStream.read(b, off, len);
        } catch (IOException e) {
            logToFile.e("read3", e);
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void close() throws IOException {
//        logToFile.d("close:class" + inputStream.getClass());
        try {
            inputStream.close();
            if (inputStreamClose != null) {
                long readTime = SystemClock.elapsedRealtime() - startTime;
                inputStreamClose.onClose(readMethod, readTime);
            }
        } catch (IOException e) {
            logToFile.e("close", e);
            e.printStackTrace();
            if (inputStreamClose != null) {
                long readTime = SystemClock.elapsedRealtime() - startTime;
                inputStreamClose.onClose(readMethod, readTime);
            }
            throw e;
        }
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

