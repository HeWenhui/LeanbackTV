package com.xueersi.parentsmeeting.modules.livevideo.lib;

import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.SparseArray;

import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveMainHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.HashMap;

/**
 * 小组互动的tcp
 */
public class GroupGameTcp {
    private static int CREATE_TIMES = 0;
    private String TAG = "GroupGameTcp" + CREATE_TIMES++;
    private Logger log = LiveTcpLoggerFactory.getLogger(TAG);
    private ReceiveMegCallBack receiveMegCallBack;
    /** 测试用，从本地文件读 */
    private boolean readSave = false;
    /** 测试用，存在本地文件 */
    private boolean saveRead = false;
    private File saveDir;
    private InetSocketAddress inetSocketAddress;
    private Socket socket;
    /** 消息序号 */
    private static int seq = 0;
    /** ping 超时 */
    private long pingTime = 10000;
    /** 心跳间隔 */
    private long heartTime = 10000;
    private PingTimeOut pingTimeOut = new PingTimeOut();
    private PingRunnable pingRunnable = new PingRunnable();
    private WriteThread writeThread;
    private Handler sendMessageHandler;
    private Handler mainHandler = LiveMainHandler.getMainHandler();
    private boolean isStop = false;
    private SparseArray<SendCallBack> callBackSparseArray = new SparseArray<>();

    public GroupGameTcp(InetSocketAddress inetSocketAddress, File saveDir) {
        this.inetSocketAddress = inetSocketAddress;
        log.d("GroupGameTcp:host=" + inetSocketAddress);
        this.saveDir = saveDir;
        saveRead = true;
    }

    public void setReceiveMegCallBack(ReceiveMegCallBack receiveMegCallBack) {
        this.receiveMegCallBack = receiveMegCallBack;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public int getSeq() {
        return seq;
    }

    public void start() {
        long before = System.currentTimeMillis();
        try {
            isStop = false;
            log.d("start");
            socket = new Socket();
            socket.setKeepAlive(true);
            socket.setSoTimeout(130000);
            socket.connect(inetSocketAddress, 5000);
            if (receiveMegCallBack != null) {
                HashMap<String, String> logs = new HashMap<>();
                logs.put("logtype", "connect");
                logs.put("time", "" + (System.currentTimeMillis() - before));
                logs.put("times", "" + (CREATE_TIMES - 1));
                receiveMegCallBack.onLog(inetSocketAddress, logs);
            } else {
                log.d("start:KeepAlive=" + socket.getKeepAlive() + ",time=" + (System.currentTimeMillis() - before));
            }
            writeThread = new WriteThread(socket.getOutputStream());
            writeThread.start();
            new Thread(new ReadThread(writeThread, socket.getInputStream())).start();
            sendMessageHandler = new Handler(writeThread.getLooper());
            if (receiveMegCallBack != null) {
                receiveMegCallBack.onConnect(this);
            }
        } catch (Exception e) {
            log.d("start:e=" + e.getMessage() + ",time=" + (System.currentTimeMillis() - before));
            if (!isStop && receiveMegCallBack != null) {
                receiveMegCallBack.onDisconnect(inetSocketAddress, e, this);
            }
        }
    }

    public void stop(String method) {
        mainHandler.removeCallbacks(pingTimeOut);
        mainHandler.removeCallbacks(pingRunnable);
        isStop = true;
        log.d("stop:method=" + method);
        if (socket != null) {
            if (writeThread != null) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    writeThread.quitSafely();
                } else {
                    writeThread.quit();
                }
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void send(final short type, final int operation, final String bodyStr, final SendCallBack sendCallBack) {
        if (sendMessageHandler != null && writeThread != null) {
            sendMessageHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (!isStop) {
                        final int finalSeq = seq;
                        if (sendCallBack != null) {
                            sendCallBack.onStart(finalSeq);
                            callBackSparseArray.put(finalSeq, sendCallBack);
                            mainHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    SendCallBack callBack = callBackSparseArray.get(finalSeq);
                                    if (callBack != null) {
                                        callBackSparseArray.remove(finalSeq);
                                        callBack.onTimeOut();
                                    }
                                }
                            }, 5000);
                        }
                        writeThread.send(type, operation, bodyStr);
                    }
                }
            });
        }
    }

    public void send(final short type, final int operation, final String bodyStr) {
        if (sendMessageHandler != null && writeThread != null) {
            sendMessageHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (!isStop) {
                        writeThread.send(type, operation, bodyStr);
                    }
                }
            });
        }
    }

    class WriteThread extends HandlerThread {
        Logger log = LiveTcpLoggerFactory.getLogger(TAG + ":WriteThread");
        OutputStream outputStream;

        WriteThread(OutputStream outputStream) {
            super(TAG + ":WriteThread");
            this.outputStream = outputStream;
        }

//        private void login() {
//            try {
//                // 包长度计算
//                // package length = PackSize + HeaderSize + VerSize + TypeSize + OperationSize +
//                // SeqIdSize + len（body）
//                // 包头长度计算
//                // header Length = PackSize + HeaderSize + VerSize + TypeSize + OperationSize +
//                // SeqIdSize
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("uid", stuId);
//                jsonObject.put("role", "1");
//                jsonObject.put("xes_rfh", xes_rfh);
//                jsonObject.put("live_id", live_id);
//                jsonObject.put("class_id", class_id);
//                String bodyStr = jsonObject.toString();
//                short type = TcpConstants.LOGIN_TYPE;
//                int operation = TcpConstants.LOGIN_OPERATION_SEND;
//                send(type, operation, bodyStr);
//            } catch (Exception e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }

        private void send(short type, int operation, String bodyStr) {
            try {
                // 包长度计算
                // package length = PackSize + HeaderSize + VerSize + TypeSize + OperationSize +
                // SeqIdSize + len（body）
                // 包头长度计算
                // header Length = PackSize + HeaderSize + VerSize + TypeSize + OperationSize +
                // SeqIdSize
                int packageLength = TcpConstants.header + bodyStr.getBytes().length;
                log.d("WriteThread:send:type=" + type + ",operation=" + operation + ",packageLength=" + packageLength);
                if (type != TcpConstants.HEAD_TYPE) {
                    if (receiveMegCallBack != null) {
                        try {
                            HashMap<String, String> logs = new HashMap<>();
                            logs.put("logtype", "sendtcp");
                            logs.put("type", "" + type);
                            logs.put("operation", "" + operation);
                            logs.put("bodyStr", "" + bodyStr);
                            logs.put("seq", "" + seq);
                            logs.put("packageLength", "" + packageLength);
                            logs.put("times", "" + (CREATE_TIMES - 1));
                            receiveMegCallBack.onLog(inetSocketAddress, logs);
                        } catch (Exception e) {
                            LiveCrashReport.postCatchedException(new TcpException(TAG, e));
                        }
                    }
                }
                ByteBuffer b = ByteBuffer.allocate(packageLength);
                b.putInt(packageLength);
                b.putShort(TcpConstants.header);
                b.putShort(TcpConstants.ver);
                b.putShort(type);
                b.putInt(operation);
                b.putInt(seq);
                seq++;
                log.d("WriteThread:send:seq=" + seq);
                b.putLong(System.currentTimeMillis());
                if (bodyStr.length() > 0) {
                    b.put(bodyStr.getBytes());
                }
                byte[] array = b.array();
                outputStream.write(array);
                log.d("WriteThread:send:write:array=" + array.length);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                log.e("WriteThread:send", e);
            }
        }

        private void heart() {
            try {
                // 包长度计算
                // package length = PackSize + HeaderSize + VerSize + TypeSize + OperationSize +
                // SeqIdSize + len（body）
                // 包头长度计算
                // header Length = PackSize + HeaderSize + VerSize + TypeSize + OperationSize +
                // SeqIdSize
                log.d("WriteThread:header:seq=" + seq);
                short type = TcpConstants.HEAD_TYPE;
                int operation = TcpConstants.HEAD_OPERATION_SEND;
                send(type, operation, "");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private class PingTimeOut implements Runnable {
        int seq;
        int operation;

        PingTimeOut() {
            log.d("PingTimeOut");
        }

        @Override
        public void run() {
            log.d("PingTimeOut:run:isStop=" + isStop + ",seq=" + seq);
            if (!isStop && receiveMegCallBack != null) {
                receiveMegCallBack.onDisconnect(inetSocketAddress, "seq=" + seq + ",operation=" + operation, GroupGameTcp.this);
            }
        }
    }

    private class PingRunnable implements Runnable {

        @Override
        public void run() {
            if (!isStop) {
                mainHandler.postDelayed(pingTimeOut, pingTime);
                if (sendMessageHandler != null) {
                    sendMessageHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!isStop) {
                                writeThread.heart();
                            }
                        }
                    });
                }
            }
        }
    }

    class ReadThread implements Runnable {
        Logger log = LiveTcpLoggerFactory.getLogger(TAG + ":ReadThread");
        WriteThread writeThread;
        InputStream inputStream;
        // 每包最小长度
        int miniLength = TcpConstants.header;
        int readCount = 0;

        ReadThread(WriteThread writeThread, InputStream inputStream) {
            this.writeThread = writeThread;
            this.inputStream = inputStream;
        }

        private void onReceiveMeg(short type, int operation, int seq, String msg) {
            log.d("onReceiveMeg:type=" + type + ",operation=" + operation + ",seq=" + seq + ",msg=" + msg);
//			if (readCount > 10) {
//				return;
//			}
//			readCount++;
            if (!readSave) {
                if (type == TcpConstants.LOGIN_TYPE) {
                    if (operation == TcpConstants.LOGIN_OPERATION_REC) {
                        pingTimeOut.seq = seq;
                        pingTimeOut.operation = operation;
                        sendMessageHandler.postDelayed(pingRunnable, heartTime);
                    }
                } else if (type == TcpConstants.REPLAY_TYPE) {
                    if (operation == TcpConstants.REPLAY_REC) {
                        mainHandler.removeCallbacks(pingTimeOut);
                        mainHandler.removeCallbacks(pingRunnable);
                        pingTimeOut.seq = seq;
                        pingTimeOut.operation = operation;
                        mainHandler.postDelayed(pingRunnable, heartTime);
                    }
                }else if (type == TcpConstants.HEAD_TYPE){
                    if (operation == TcpConstants.HEAD_OPERATION_REC ) {
                        mainHandler.removeCallbacks(pingTimeOut);
                        mainHandler.removeCallbacks(pingRunnable);
                        pingTimeOut.seq = seq;
                        pingTimeOut.operation = operation;
                        mainHandler.postDelayed(pingRunnable, heartTime);
                    }
                }
            }
            if (type == TcpConstants.REPLAY_TYPE) {
                if (operation == TcpConstants.REPLAY_REC) {
                    SendCallBack callBack = callBackSparseArray.get(seq);
                    if (callBack != null) {
                        callBackSparseArray.remove(seq);
                        callBack.onReceiveMeg(type, operation, seq, msg);
                    }
                    return;
                }
            }
            if (receiveMegCallBack != null) {
                receiveMegCallBack.onReceiveMeg(type, operation, msg);
            }
        }

        @Override
        public void run() {
            testBuffer();
        }

        private void testBuffer() {
            int length = -1;
            // byte[] readBuffer = new byte[miniLength / 2 - 4];
            byte[] readBuffer = new byte[4790];
            // byte[] readBuffer = new byte[miniLength / 2];
            log.d("testBuffer:header=" + TcpConstants.header + ",miniLength=" + miniLength + ",readBuffer=" + readBuffer.length);

            int lastBody = 0;
            short lastType = 0;
            int lastOper = 0;
            // 上一次的序列号
            int lastSeq = 0;
            boolean readHead = false;
            // 上一次的缓存
            ByteBuffer lastBuffer = null;
            Exception endEx = null;
            FileOutputStream fileOutputStream = null;
            FileInputStream fileInputStream = null;
            File saveFile = null;
            try {
                if (readSave || saveRead) {
                    //暂时没有出现过异常
                    try {
                        String name = ("" + inetSocketAddress).replaceAll("/", "_").replaceAll("\\.", "_").replaceAll(":", "-");
                        saveDir = new File(saveDir, name);
                        if (!saveDir.exists()) {
                            saveDir.mkdirs();
                        }
                        saveFile = new File(saveDir, name + "_" + CREATE_TIMES + "_read_" + System.currentTimeMillis());
                        log.d("testBuffer:saveFile=" + saveFile.length());
                        if (readSave) {
                            try {
                                fileInputStream = new FileInputStream(saveFile);
                                inputStream = fileInputStream;
                            } catch (Exception e) {
                                log.d("testBuffer:Input.e=" + e);
                            }
                            saveRead = false;
                        }
                        if (saveRead) {
                            try {
                                fileOutputStream = new FileOutputStream(saveFile);
                            } catch (Exception e) {
                                log.d("testBuffer:Output.e=" + e);
                            }
                        }
                    } catch (Exception e) {
                        LiveCrashReport.postCatchedException(new TcpException("testBuffer", e));
                    }
                }
                while (!isStop && (length = inputStream.read(readBuffer)) != -1) {
                    log.d("testBuffer:readHead=" + readHead + ",lastBuffer=null?" + (lastBuffer == null)
                            + ",lastBody=" + lastBody + ",length=" + length);
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.write(readBuffer, 0, length);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    ByteBuffer thisBuffer;
                    int capacity;
                    if (lastBuffer != null) {
                        byte[] array = lastBuffer.array();
                        log.d("testBuffer:allocate=" + array.length + ",length=" + length);
                        capacity = length + array.length;
                        thisBuffer = ByteBuffer.allocate(length + array.length);
                        thisBuffer.put(array);
                        thisBuffer.put(readBuffer, 0, length);
                        thisBuffer.flip();
                        lastBuffer = null;
                    } else {
                        // thisBuffer = ByteBuffer.allocate(length);
                        // thisBuffer.put(readBuffer, 0, length);
                        // thisBuffer.flip();
                        capacity = length;
                        thisBuffer = ByteBuffer.wrap(readBuffer, 0, length);
                    }
                    log.d("testBuffer:length=" + length + ",thiscapacity=" + capacity + ",miniLength=" + miniLength);
//					fileOutputStream.write(readBuffer, 0, length);
                    // 读的数据是不是单条
                    boolean tiao = true;
                    // 读的数据条数
                    int numBars = 0;
                    // int capacity = thisBuffer.capacity();
                    while (tiao) {
                        numBars++;
                        byte[] buffer = new byte[capacity];
                        thisBuffer.get(buffer);
                        if (!readHead) {
                            if (capacity < miniLength) {
                                lastBuffer = ByteBuffer.wrap(buffer);
                                tiao = false;
                                continue;
                            } else {
                                ByteBuffer headBuffer = ByteBuffer.wrap(buffer);
                                readHead = true;
                                int pack = headBuffer.getInt();
                                short head = headBuffer.getShort();
                                short ver = headBuffer.getShort();
                                short type = headBuffer.getShort();
                                lastType = type;
                                int oper = headBuffer.getInt();
                                lastOper = oper;
                                int seq = headBuffer.getInt();
                                lastSeq = seq;
                                long recTimestamp = headBuffer.getLong();
                                int body = pack - head;
                                log.d("testBuffer:pack1=" + pack + ",head1=" + head + ",ver1=" + ver + ",type1="
                                        + type + ",oper1=" + oper + ",seq1=" + seq + ",recTimestamp=" + recTimestamp + ",body1=" + body);
                                if (body == 0) {
                                    // 没有body的时候。是不是比head头大
                                    if (capacity > miniLength) {
                                        lastBuffer = ByteBuffer.allocate(capacity - miniLength);
                                        lastBuffer.put(buffer, miniLength, capacity - miniLength);
                                        lastBuffer.flip();
                                        thisBuffer = lastBuffer;
                                        capacity = thisBuffer.capacity();
                                        log.d("testBuffer:lastBuffer1=" + (lastBuffer.capacity()));
                                        lastBuffer = null;
                                    } else {
                                        tiao = false;
                                    }
                                    onReceiveMeg(type, oper, seq, "");
                                    lastBody = 0;
                                    readHead = false;
                                } else {
                                    if (capacity >= head + body) {
                                        ByteBuffer bodyBuffer = ByteBuffer.wrap(buffer, head, body);
                                        byte[] dst = new byte[body];
                                        bodyBuffer.get(dst);
                                        String msg = new String(dst);
                                        log.d("testBuffer:body:length2=" + dst.length + ",msg=" + msg);
                                        onReceiveMeg(lastType, lastOper, seq, msg);
                                        readHead = false;
                                        if (capacity > pack) {
                                            lastBuffer = ByteBuffer.allocate(capacity - pack);
                                            lastBuffer.put(buffer, pack, capacity - pack);
                                            lastBuffer.flip();
                                            thisBuffer = lastBuffer;
                                            capacity = thisBuffer.capacity();
                                            log.d("testBuffer:lastBuffer2=" + (lastBuffer.capacity()));
                                            lastBuffer = null;
                                        } else {
                                            tiao = false;
                                        }
                                    } else {
                                        lastBody = body;
                                        int capacity2 = capacity - head;
                                        if (capacity2 < 0) {
                                            throw new Exception("pack=" + pack + ",head=" + head);
                                        }
                                        lastBuffer = ByteBuffer.allocate(capacity - head);
                                        lastBuffer.put(buffer, head, capacity - head);
                                        lastBuffer.flip();
                                        // lastBuffer = ByteBuffer.wrap(buffer, miniLength, capacity - miniLength);
                                        log.d("testBuffer:lastBuffer3=" + capacity + ",last="
                                                + (capacity - miniLength));
                                        tiao = false;
                                    }
                                }
                            }
                        } else {
                            if (capacity < lastBody) {
                                lastBuffer = ByteBuffer.wrap(buffer);
                                log.d("testBuffer:lastBuffer4=" + (lastBuffer.capacity()));
                                tiao = false;
                            } else {
                                ByteBuffer bodyBuffer = ByteBuffer.wrap(buffer, 0, lastBody);
                                byte[] dst = new byte[lastBody];
                                bodyBuffer.get(dst);
                                String msg = new String(dst);
                                log.d("testBuffer:length2=" + dst.length + ",msg=" + msg);
                                onReceiveMeg(lastType, lastOper, lastSeq, msg);
                                readHead = false;
                                if (capacity > lastBody) {
                                    lastBuffer = ByteBuffer.allocate(capacity - lastBody);
                                    lastBuffer.put(buffer, lastBody, capacity - lastBody);
                                    lastBuffer.flip();
                                    thisBuffer = lastBuffer;
                                    capacity = thisBuffer.capacity();
                                    log.d("testBuffer:lastBuffer5=" + (lastBuffer.capacity()));
                                    lastBuffer = null;
                                } else {
                                    tiao = false;
                                }
                            }
                        }
                    }
                    log.d("testBuffer:numBars=" + numBars);
//					int size = random.nextInt(222);
//					if (size == 0) {
//						size = 11;
//					}
//					readBuffer = new byte[size];
                }
                log.d("testBuffer:end");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                endEx = e;
                log.e("testBuffer", e);
                if (saveFile != null) {
                    if (receiveMegCallBack != null) {
                        receiveMegCallBack.onReadEnd(inetSocketAddress, e, saveFile);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                endEx = e;
                LiveCrashReport.postCatchedException(new TcpException("testBuffer", e));
                if (saveFile != null) {
                    if (receiveMegCallBack != null) {
                        receiveMegCallBack.onReadException(inetSocketAddress, e, saveFile);
                    }
                }
                log.e("testBuffer", e);
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            if (!isStop && receiveMegCallBack != null) {
                receiveMegCallBack.onDisconnect(inetSocketAddress, endEx, GroupGameTcp.this);
            }
        }
    }
}
