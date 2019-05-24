//package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.utils;
//
//import android.media.MediaCodec;
//import android.media.MediaExtractor;
//import android.media.MediaFormat;
//
//import java.io.IOException;
//import java.nio.ByteBuffer;
//
//public class MediaCodecU1 {
//
//    private MediaExtractor mediaExtractor;
//
//    private MediaCodec mediaDecode;
//
//    private ByteBuffer[] decodeInputBuffers, decodeOutputBuffers;
//    private MediaCodec.BufferInfo decodeBufferInfo;
//
//    /**
//     * 初始化解码器
//     */
//    private void initMediaDecode() {
//        try {
//            mediaExtractor = new MediaExtractor();//此类可分离视频文件的音轨和视频轨道
//            mediaExtractor.setDataSource(srcPath);//媒体文件的位置
//            for (int i = 0; i < mediaExtractor.getTrackCount(); i++) {//遍历媒体轨道 此处我们传入的是音频文件，所以也就只有一条轨道
//                MediaFormat format = mediaExtractor.getTrackFormat(i);
//                String mime = format.getString(MediaFormat.KEY_MIME);
//                if (mime.startsWith("audio")) {//获取音频轨道
////                    format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 200 * 1024);
//                    mediaExtractor.selectTrack(i);//选择此音频轨道
//                    mediaDecode = MediaCodec.createDecoderByType(mime);//创建Decode解码器
//                    mediaDecode.configure(format, null, null, 0);
//                    break;
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        if (mediaDecode == null) {
//            Log.e(TAG, "create mediaDecode failed");
//            return;
//        }
//        mediaDecode.start();//启动MediaCodec ，等待传入数据
//        decodeInputBuffers = mediaDecode.getInputBuffers();//MediaCodec在此ByteBuffer[]中获取输入数据
//        decodeOutputBuffers = mediaDecode.getOutputBuffers();//MediaCodec将解码后的数据放到此ByteBuffer[]中 我们可以直接在这里面得到PCM数据
//        decodeBufferInfo = new MediaCodec.BufferInfo();//用于描述解码得到的byte[]数据的相关信息
//        showLog("buffers:" + decodeInputBuffers.length);
//    }
//}
