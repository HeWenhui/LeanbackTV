package com.xueersi.parentsmeeting.modules.livevideo.lib;

import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

public class GroupGameTcp {
    private Logger log = LiveLoggerFactory.getLogger("GroupGameTcp");
    private ReceiveMegCallBack receiveMegCallBack;
    /** 测试用，从本地文件读 */
    private boolean readSave = false;
    /** 测试用，存在本地文件 */
    private boolean saveRead = false;
    private String host;
    private int port;
    private Socket socket;
    private int seq = 0;
    private String stuId;
    private String xes_rfh;
    private WriteThread writeThread;

    public GroupGameTcp(String host, int port, String stuId, String xes_rfh) {
        this.host = host;
        this.port = port;
        this.stuId = stuId;
        this.xes_rfh = xes_rfh;
    }

    public void setReceiveMegCallBack(ReceiveMegCallBack receiveMegCallBack) {
        this.receiveMegCallBack = receiveMegCallBack;
    }

    public void start() {
        try {
            socket = new Socket(host, port);
            writeThread = new WriteThread(socket.getOutputStream());
            new Thread(writeThread).start();
            new Thread(new ReadThread(writeThread, socket.getInputStream())).start();
        } catch (IOException e) {
            e.printStackTrace();
            if (receiveMegCallBack != null) {
                receiveMegCallBack.onDisconnect(this);
            }
        }
    }

    public void stop() {
        log.d("stop");
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void send(short type, int operation, String body) {
        if (writeThread != null) {
            writeThread.send(type, operation, body);
        }
    }

    class WriteThread implements Runnable {
        Logger log = LiveLoggerFactory.getLogger("WriteThread");
        OutputStream outputStream;

        WriteThread(OutputStream outputStream) {
            this.outputStream = outputStream;
        }

        public void run() {
            if (!readSave) {
                login();
            }
        }

        private void login() {
            try {
                // 包长度计算
                // package length = PackSize + HeaderSize + VerSize + TypeSize + OperationSize +
                // SeqIdSize + len（body）
                // 包头长度计算
                // header Length = PackSize + HeaderSize + VerSize + TypeSize + OperationSize +
                // SeqIdSize
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("uid", stuId);
                jsonObject.put("role", "1");
                jsonObject.put("xes_rfh", xes_rfh);
                String bodyStr = jsonObject.toString();
                short type = TcpConstants.LOGIN_TYPE;
                int operation = TcpConstants.LOGIN_OPERATION_SEND;
                send(type, operation, bodyStr);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        void send(short type, int operation, String bodyStr) {
            try {
                // 包长度计算
                // package length = PackSize + HeaderSize + VerSize + TypeSize + OperationSize +
                // SeqIdSize + len（body）
                // 包头长度计算
                // header Length = PackSize + HeaderSize + VerSize + TypeSize + OperationSize +
                // SeqIdSize
                int packageLength = TcpConstants.header + bodyStr.getBytes().length;
                log.d("WriteThread:send:packageLength=" + packageLength);
                ByteBuffer b = ByteBuffer.allocate(packageLength);
                b.putInt(packageLength);
                b.putShort(TcpConstants.header);
                b.putShort(TcpConstants.ver);
                b.putShort(type);
                b.putInt(operation);
                seq++;
                log.d("login:seq=" + seq);
                b.putInt(seq);
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
                log.d("WriteThread:header=" + TcpConstants.header / 8);
                short type = TcpConstants.HEAD_TYPE;
                int operation = TcpConstants.HEAD_OPERATION_SEND;
                send(type, operation, "");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    class ReadThread implements Runnable {
        Logger log = LiveLoggerFactory.getLogger("ReadThread");
        WriteThread writeThread;
        InputStream inputStream;
        // 每包最小长度
        int miniLength = TcpConstants.header * 2;
        int readCount = 0;

        ReadThread(WriteThread writeThread, InputStream inputStream) {
            this.writeThread = writeThread;
            this.inputStream = inputStream;
        }

        private void onReceiveMeg(short type, int operation, String msg) {
            log.d("onReceiveMeg:type=" + type + ",operation=" + operation + ",msg=" + msg);
//			if (readCount > 10) {
//				return;
//			}
//			readCount++;
            if (!readSave) {
                if (type == TcpConstants.LOGIN_TYPE) {
                    if (operation == TcpConstants.LOGIN_OPERATION_REC) {
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1500);
                                } catch (InterruptedException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                writeThread.heart();
                            }
                        }.start();
                    }
                } else if (type == TcpConstants.HEAD_TYPE) {
                    if (operation == TcpConstants.HEAD_OPERATION_REC) {
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1500);
                                } catch (InterruptedException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                writeThread.heart();
                            }
                        }.start();
                    }
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
            boolean readHead = false;
            // 上一次的缓存
            ByteBuffer lastBuffer = null;

            FileOutputStream fileOutputStream = null;
            FileInputStream fileInputStream = null;
            try {
                if (readSave || saveRead) {
                    File saveDir = LiveCacheFile.geCacheFile(ContextManager.getContext(), "tcp");
                    if (!saveDir.exists()) {
                        saveDir.mkdirs();
                    }
                    File saveFile = new File(saveDir, "read");
                    log.d("testBuffer:saveFile=" + saveFile.length());
                    if (readSave) {
                        fileInputStream = new FileInputStream(saveFile);
                        inputStream = fileInputStream;
                        saveRead = false;
                    }
                    if (saveRead) {
                        fileOutputStream = new FileOutputStream(saveFile);
                    }
                }
                while ((length = inputStream.read(readBuffer)) != -1) {
                    log.d("testBuffer:readHead=" + readHead + ",lastBuffer=null?" + (lastBuffer == null)
                            + ",lastBody=" + lastBody + ",length=" + length);
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.write(readBuffer, 0, length);
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
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
                                int body = pack - head;
                                log.d("testBuffer:pack1=" + pack + ",head1=" + head + ",ver1=" + ver + ",type1="
                                        + type + ",oper1=" + oper + ",seq1=" + seq + ",body1=" + body);
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
                                    onReceiveMeg(type, oper, "");
                                    lastBody = 0;
                                    readHead = false;
                                } else {
                                    if (capacity >= head + body) {
                                        ByteBuffer bodyBuffer = ByteBuffer.wrap(buffer, head, body);
                                        byte[] dst = new byte[body];
                                        bodyBuffer.get(dst);
                                        String msg = new String(dst);
                                        log.d("testBuffer:body:length2=" + dst.length + ",msg=" + msg);
                                        onReceiveMeg(lastType, lastOper, msg);
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
                                onReceiveMeg(lastType, lastOper, msg);
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
        }
    }
}
