package com.xueersi.parentsmeeting.modules.livevideo.util;

import android.content.Context;
import android.os.AsyncTask;

import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipException;

public class ZipExtractorTask extends AsyncTask<Void, Integer, Exception> {
    private final static String TAG = "ZipExtractorTask";
    protected static Logger logger = LoggerFactory.getLogger(TAG);
    private final File mInput;
    private final File mOutput;
    private int mProgress = 0;
    private final Context mContext;
    private boolean mReplaceAll;
    protected int max;
    private boolean cancle = false;

    public ZipExtractorTask(File in, File out, Context context, boolean replaceAll) {
        mInput = in;
        mOutput = out;
        if (!mOutput.exists()) {
            if (!mOutput.mkdirs()) {
                logger.e( "Failed to make directories:" + mOutput.getAbsolutePath());
            }
        }
        mContext = context;
        mReplaceAll = replaceAll;
    }

    public ZipExtractorTask(String in, String out, Context context, boolean replaceAll) {
        this(new File(in), new File(out), context, replaceAll);
    }

    protected Exception doInBackground(Void... params) {
        return unzip();
    }

    protected void onPostExecute(Exception exception) {

    }

    @Override
    protected void onPreExecute() {

    }

    public void setCancle(boolean cancle) {
        this.cancle = cancle;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if (values.length > 1) {
            max = values[1];

        } else {

        }
    }

    @SuppressWarnings("unchecked")
    private Exception unzip() {
        long extractedSize = 0L;
        Enumeration<ZipEntry> entries;
        ZipFile zip = null;
        Exception exception;
        try {
            zip = new ZipFile(mInput, "GBK");
            long uncompressedSize = getOriginalSize(zip);
            publishProgress(0, (int) uncompressedSize);
            entries = (Enumeration<ZipEntry>) zip.getEntries();
            boolean isBreak = false;
            while (entries.hasMoreElements()) {
                if (cancle) {
                    isBreak = true;
                    break;
                }
                ZipEntry entry = entries.nextElement();
                if (entry.isDirectory() || entry.getSize() == 0) {
                    continue;
                }
                File destination = new File(mOutput, entry.getName());
                if (!destination.getParentFile().exists()) {
                    boolean mkdir = destination.getParentFile().mkdirs();
                }
                ProgressReportingOutputStream outStream = new ProgressReportingOutputStream(destination);
                extractedSize += copy(zip.getInputStream(entry), outStream);
                outStream.close();
            }
            if (isBreak) {
                return new Exception("cancel");
            } else {
                return null;
            }
        } catch (ZipException e) {
            exception = e;
            e.printStackTrace();
        } catch (IOException e) {
            exception = e;
            e.printStackTrace();
        } catch (Exception e) {
            exception = e;
            e.printStackTrace();
        }  finally {
            if (zip != null) {
                try {
                    zip.close();
                } catch (IOException e) {
                    exception = e;
                    e.printStackTrace();
                }
            }
        }
        return exception;
    }

    private long getOriginalSize(ZipFile file) {
        @SuppressWarnings("unchecked")
        Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) file.getEntries();
        long originalSize = 0l;
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (entry.getSize() >= 0) {
                originalSize += entry.getSize();
            }
        }
        return originalSize;
    }

    private int copy(InputStream input, OutputStream output) {
        byte[] buffer = new byte[1024 * 8];
        BufferedInputStream in = new BufferedInputStream(input, 1024 * 8);
        BufferedOutputStream out = new BufferedOutputStream(output, 1024 * 8);
        int count = 0, n = 0;
        try {
            while ((n = in.read(buffer, 0, 1024 * 8)) != -1) {
                out.write(buffer, 0, n);
                count += n;
            }
            out.flush();
        } catch (IOException e) {

            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {

                e.printStackTrace();
            }
            try {
                in.close();
            } catch (IOException e) {

                e.printStackTrace();
            }
        }
        return count;
    }

    private final class ProgressReportingOutputStream extends FileOutputStream {

        public ProgressReportingOutputStream(File file) throws FileNotFoundException {
            super(file);

        }

        @Override
        public void write(byte[] buffer, int byteOffset, int byteCount) throws IOException {

            super.write(buffer, byteOffset, byteCount);
            mProgress += byteCount;
            publishProgress(mProgress);
        }

    }

}
