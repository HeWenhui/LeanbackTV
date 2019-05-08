package com.xueersi.parentsmeeting.modules.livevideo.achievement.business;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;

import com.tal.speech.service.SpeechService;
import com.tal.speech.speechrecognigen.ISpeechRecognitnCall;
import com.tal.speech.speechrecognigen.ISpeechRecognitnGen;
import com.tal.speech.speechrecognizer.PCMFormat;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.entity.MyUserInfoEntity;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.service.LiveService;
import com.xueersi.parentsmeeting.speakerrecognition.SpeakerRecognitionerInterface;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by linyuqiang on 2018/9/10.
 * 新的声纹注册
 */
public class SpeakerRecognitioner {
    private String TAG = "SpeakerRecognitioner";
    private final Object lock = new Object();
    private Logger logger = LoggerFactory.getLogger("SpeakerRecognitioner");
    private boolean isStart = false;
    private boolean destory = false;
    private Context context;
    private SpeakerPredict speakerPredict;
    private LogToFile logToFile;
    private AtomicBoolean audioRequest;
    private ISpeechRecognitnGen iSpeechRecognitnGen;

    public SpeakerRecognitioner(Context context, AtomicBoolean audioRequest) {
        logger.setLogMethod(false);
        this.audioRequest = audioRequest;
        this.context = context;
        logToFile = new LogToFile(context, TAG);
        Intent intent = new Intent(context, LiveService.class);
        intent.setAction("START_SPEECH_GEN");
        context.bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            logger.d("onServiceConnected:destory=" + destory + ",isStart=" + isStart);
            iSpeechRecognitnGen = ISpeechRecognitnGen.Stub.asInterface(iBinder);
            if (destory) {
                try {
                    iSpeechRecognitnGen.release();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } else {
                if (isStart) {
                    try {
                        iSpeechRecognitnGen.start(speechRecognitnCall);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (isStart) {
                        try {
                            iSpeechRecognitnGen.stop();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            logger.d("onServiceConnected");
        }
    };

    private ISpeechRecognitnCall.Stub speechRecognitnCall = new ISpeechRecognitnCall.Stub() {

        @Override
        public void onPredict(String msg) throws RemoteException {
            if (speakerPredict != null) {
                speakerPredict.onPredict(msg);
            }
        }
    };

    public void setSpeakerPredict(SpeakerPredict speakerPredict) {
        this.speakerPredict = speakerPredict;
    }

    public void start() {
        if (isStart) {
            return;
        }
        isStart = true;
        if (iSpeechRecognitnGen != null) {
            try {
                iSpeechRecognitnGen.start(speechRecognitnCall);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        logToFile.d("stop:isStart=" + isStart);
        isStart = false;
        if (iSpeechRecognitnGen != null) {
            try {
                iSpeechRecognitnGen.stop();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void destory() {
        logToFile.d("destory:isStart=" + isStart);
        if (iSpeechRecognitnGen != null) {
            try {
                iSpeechRecognitnGen.release();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        context.unbindService(serviceConnection);
    }

    public interface SpeakerPredict {
        void onPredict(String predict);
    }
}
