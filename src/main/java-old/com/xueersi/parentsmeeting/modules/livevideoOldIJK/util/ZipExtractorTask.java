package com.xueersi.parentsmeeting.modules.livevideoOldIJK.util;

import android.os.AsyncTask;
import android.util.Log;

import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.config.LogConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;

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
    private boolean mReplaceAll;
    protected int max;
    private boolean cancle = false;
    private ZipProg zipProg;

    public ZipExtractorTask(File in, File out, boolean replaceAll, ZipProg zipProg) {
        mInput = in;
        mOutput = out;
        if (!mOutput.exists()) {
            if (!mOutput.mkdirs()) {
                logger.e("Failed to make directories:" + mOutput.getAbsolutePath());
            }
        }
        mReplaceAll = replaceAll;
        this.zipProg = zipProg;
    }

    public ZipExtractorTask(String in, String out, boolean replaceAll) {
        this(new File(in), new File(out), replaceAll, null);
    }

    @Override
    protected Exception doInBackground(Void... params) {
        return unzip();
    }

    @Override
    protected void onPostExecute(Exception exception) {
        if (zipProg != null) {
            zipProg.onPostExecute(exception);
        }
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
            if (zipProg != null) {
                zipProg.setMax(max);
            }
        } else {
            if (zipProg != null) {
                zipProg.onProgressUpdate(values);
            }
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
                File destinationTemp = new File(mOutput, entry.getName() + ".tmp");
                long oldLength = destinationTemp.length();
                File destination = new File(mOutput, entry.getName());
                long oldLength2 = destination.length();
                File parentFile = destination.getParentFile();
                if (!parentFile.exists()) {
                    boolean mkdir = parentFile.mkdirs();
                }
                ProgressReportingOutputStream outStream = new ProgressReportingOutputStream(destinationTemp);
                try {
                    extractedSize += copy(zip.getInputStream(entry), outStream);
                    boolean renameTo = destinationTemp.renameTo(destination);
                    if (!renameTo) {
                        try {
                            StableLogHashMap logHashMap = new StableLogHashMap();
                            logHashMap.put("logtype", "renameto");
                            logHashMap.put("path", "" + destination.getPath());
                            logHashMap.put("parentfile", "" + parentFile.exists());
                            logHashMap.put("extractedsize", "" + extractedSize);
                            logHashMap.put("oldlength1", "" + oldLength);
                            logHashMap.put("oldlength2", "" + oldLength2);
                            logHashMap.put("length1", "" + destinationTemp.length());
                            logHashMap.put("length2", "" + destination.length());
                            UmsAgentManager.umsAgentDebug(ContextManager.getContext(), LogConfig.LIVE_ZIP_FILE_ERROR, logHashMap.getData());
                        } catch (Exception e) {
                            LiveCrashReport.postCatchedException(e);
                        }
                    }
                } catch (Exception e) {
                    try {
                        StableLogHashMap logHashMap = new StableLogHashMap();
                        logHashMap.put("logtype", "exception");
                        logHashMap.put("path", "" + destination.getPath());
                        logHashMap.put("parentfile", "" + parentFile.exists());
                        logHashMap.put("extractedsize", "" + extractedSize);
                        logHashMap.put("exception", "" + Log.getStackTraceString(e));
                        logHashMap.put("oldlength1", "" + oldLength);
                        logHashMap.put("oldlength2", "" + oldLength2);
                        logHashMap.put("length1", "" + destinationTemp.length());
                        logHashMap.put("length2", "" + destination.length());
                        UmsAgentManager.umsAgentDebug(ContextManager.getContext(), LogConfig.LIVE_ZIP_FILE_ERROR, logHashMap.getData());
                    } catch (Exception e2) {
                        LiveCrashReport.postCatchedException(e2);
                    }
                    destinationTemp.delete();
                    destination.delete();
                    throw e;
                } finally {
                    outStream.close();
                }
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
        } finally {
            if (zip != null) {
                try {
                    zip.close();
                } catch (IOException e) {
                    exception = e;
                    e.printStackTrace();
                }
            }
        }
        try {
            StableLogHashMap logHashMap = new StableLogHashMap();
            logHashMap.put("inputname", "" + mInput);
            logHashMap.put("inputlength", "" + mInput.length());
            logHashMap.put("output", "" + mOutput);
            logHashMap.put("exception", "" + Log.getStackTraceString(exception));
            UmsAgentManager.umsAgentDebug(ContextManager.getContext(), LogConfig.LIVE_ZIP_ERROR, logHashMap.getData());
        } catch (Exception e) {
            LiveCrashReport.postCatchedException(e);
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

    private int copy(InputStream input, OutputStream output) throws IOException {
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
            throw e;
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
