package com.xueersi.parentsmeeting.modules.livevideo.achievement.business;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.tal.speech.speechrecognigen.ISpeechRecognitnCall;
import com.tal.speech.speechrecognigen.ISpeechRecognitnGen;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
import com.xueersi.parentsmeeting.modules.livevideo.service.LiveService;

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
    private SpeakerEnrollIvector speakerEnrollIvector;

    public SpeakerRecognitioner(Context context, AtomicBoolean audioRequest) {
        logger.setLogMethod(false);
        this.audioRequest = audioRequest;
        this.context = context;
        logToFile = new LogToFile(context, TAG);
        try {
            Intent intent = new Intent(context, LiveService.class);
            intent.setAction("START_SPEECH_GEN");
            context.bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE);
        } catch (Exception e) {
            LiveCrashReport.postCatchedException(new LiveException(TAG, e));
        }
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
                } catch (Exception e) {
                    e.printStackTrace();
                    LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                }
            } else {
                if (isStart) {
                    try {
                        iSpeechRecognitnGen.startSpeech(speechRecognitnCall);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                        LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                    }
                } else {
                    try {
                        iSpeechRecognitnGen.stopSpeech();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                        LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                    }
                }
                if (check) {
                    check = false;
                    check();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            logger.d("onServiceDisconnected");
        }
    };

    private ISpeechRecognitnCall.Stub speechRecognitnCall = new ISpeechRecognitnCall.Stub() {

        @Override
        public boolean onPredict(String msg) throws RemoteException {
            if (speakerPredict != null) {
                speakerPredict.onPredict(msg);
            }
            return audioRequest.get();
        }

        @Override
        public void enrollIvector(int enrollIvector) throws RemoteException {
            if (speakerEnrollIvector != null) {
                speakerEnrollIvector.enrollIvector(enrollIvector);
            }
        }
    };

    public void setSpeakerPredict(SpeakerPredict speakerPredict) {
        this.speakerPredict = speakerPredict;
    }

    public void setSpeakerEnrollIvector(SpeakerEnrollIvector speakerEnrollIvector) {
        this.speakerEnrollIvector = speakerEnrollIvector;
    }

    private boolean check = false;

    public void check() {
        if (iSpeechRecognitnGen != null) {
            try {
                iSpeechRecognitnGen.check(speechRecognitnCall);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                LiveCrashReport.postCatchedException(new LiveException(TAG, e));
            }
        } else {
            check = true;
        }
    }

    public void start() {
        if (isStart) {
            return;
        }
        isStart = true;
        if (iSpeechRecognitnGen != null) {
            try {
                iSpeechRecognitnGen.startSpeech(speechRecognitnCall);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                LiveCrashReport.postCatchedException(new LiveException(TAG, e));
            }
        }
    }

    public void stop() {
        logToFile.d("stop:isStart=" + isStart);
        isStart = false;
        if (iSpeechRecognitnGen != null) {
            try {
                iSpeechRecognitnGen.stopSpeech();
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                LiveCrashReport.postCatchedException(new LiveException(TAG, e));
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
            } catch (Exception e) {
                e.printStackTrace();
                LiveCrashReport.postCatchedException(new LiveException(TAG, e));
            }
        }
        context.unbindService(serviceConnection);
    }

    public interface SpeakerPredict {
        void onPredict(String predict);
    }
}
