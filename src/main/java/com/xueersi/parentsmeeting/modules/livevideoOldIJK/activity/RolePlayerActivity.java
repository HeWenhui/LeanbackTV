package com.xueersi.parentsmeeting.modules.livevideoOldIJK.activity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioRecord;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.tal.speech.config.SpeechConfig;
import com.tal.speech.speechrecognizer.EvaluatorListener;
import com.tal.speech.speechrecognizer.EvaluatorListenerWithPCM;
import com.tal.speech.speechrecognizer.ResultEntity;
import com.tal.speech.speechrecognizer.SpeechEvaluatorInter;
import com.tal.speech.speechrecognizer.SpeechParamEntity;
import com.tal.speech.utils.SpeechUtils;
import com.xueersi.common.base.XesActivity;
import com.xueersi.common.business.UserBll;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.RolePlayerBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.agora.WorkerThread;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RolePlayerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.page.RolePlayerPager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.agora.rtc.Constants;


/**
 * 学员连麦RolePlayer界面
 * Created by zouhao on 2018/3/29.
 */

public class RolePlayerActivity extends XesActivity {

    RelativeLayout llRoleMain;

    RolePlayerEntity entity;


    List<String> lstTestMsg = new ArrayList<>();

    RelativeLayout rlBubbleMain;

    RolePlayerBll bllRole;
    /**
     * 原始录音数据
     */
    private short[] mPCMBuffer = null;
    /**
     * 声网
     */
    private WorkerThread mWorkerThread;
    protected SpeechUtils mIse;
    private SpeechEvaluatorInter speechEvaluatorInter;
    private File saveVideoFile, dir;
    private int sizeInBytes = 0;
    private AudioRecord mAudioRecorder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roleplayer);
        llRoleMain = findViewById(R.id.ll_roleplayer_main);
        lstTestMsg.add("Is this your handbag?\nHe’s fine, thanks.");
        lstTestMsg.add("Yes, it is.");
        lstTestMsg.add("Here’s your umbrella and your coat!");
        lstTestMsg.add("Thank you sir.");
        lstTestMsg.add("I’m very well, thank you.\nI’m fine thanks.");
        lstTestMsg.add("What are you going to do now ?");
        lstTestMsg.add("What’s she doing ?");
        lstTestMsg.add("She’s sitting under the tree.\n No, I’m not.");
        lstTestMsg.add("What are you going to do with that vase ?");
        lstTestMsg.add("Are there any newspapers behind that vase ?");
        rlBubbleMain = findViewById(R.id.rl_bubble_main);
        bllRole = new RolePlayerBll(mContext, llRoleMain, null, null);
//        bllRole.setOnError(new RolePlayAction.OnError() {
//            @Override
//            public void onError(BaseVideoQuestionEntity testId) {
//                XESToastUtils.showToast(RolePlayerActivity.this,"onError");
//            }
//        });
        mWorkerThread = new WorkerThread(ContextManager.getApplication(), Integer.parseInt(UserBll.getInstance()
                .getMyUserInfoEntity().getStuId()), false, true);
        dir = new File(Environment.getExternalStorageDirectory(), "parentsmeeting/liveSpeech/");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        findViewById(R.id.btn_live_roleplayer_enter).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                view.setVisibility(View.GONE);
                enterRolePlayerClick();
            }
        });

        findViewById(R.id.btn_live_roleplayer_dz).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDZ();
            }
        });

        findViewById(R.id.btn_roleplayer_lingdu).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //领读
                // bllRole.teacherRead("157029", "8155765");
                //bllRole.teacherRead("144976", "8152545");
                bllRole.teacherRead("157586", "8156175", "");
                //bllRole.teacherRead("157586", "8156177");
            }
        });
        if (mIse == null) {
            mIse = SpeechUtils.getInstance(mContext.getApplicationContext());
            mIse.setLanguage(com.tal.speech.speechrecognizer.Constants.ASSESS_PARAM_LANGUAGE_EN);
            saveVideoFile = new File(dir, "roleplayer" + System.currentTimeMillis() + ".mp3");
        }
        mIse.cancel();
        findViewById(R.id.btn_roleplayer_connectsuccess).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //声网
                //开启声网连接mWorkerThread
                mWorkerThread.start();
                mWorkerThread.waitForReady();
                int vProfile = Constants.VIDEO_PROFILE_120P;
                mWorkerThread.configEngine(Constants.CLIENT_ROLE_BROADCASTER, vProfile);
                mWorkerThread.joinChannel(null, "198785", Integer.parseInt(UserBll.getInstance().getMyUserInfoEntity
                        ().getStuId()), new WorkerThread.OnJoinChannel() {
                    @Override
                    public void onJoinChannel(int joinChannel) {

                        logger.i("声网:" + joinChannel);
                    }
                });


//                sizeInBytes = AudioRecord.getMinBufferSize(16000,
//                        1, AudioFormat.ENCODING_PCM_16BIT);
//
//
//
//                if (mAudioRecorder != null) {
//                    mAudioRecorder.release();
//                    mAudioRecorder = null;
//                }
//
//
//                mAudioRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
//                        16000,
//                        1,
//                        AudioFormat.ENCODING_PCM_16BIT,
//                        sizeInBytes);
//
//
//                if (mPCMBuffer == null) {
//                    mPCMBuffer = new short[sizeInBytes];
//                }
//
//                mAudioRecorder.startRecording();
//
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        gatherData();
//                    }
//                }).start();

//                if (!SpeechEvaluatorUtils.isOfflineSuccess()) {
//                    XESToastUtils.showToast(mContext, "模型库加载失败");
//                    return;
//                }
                if (!mIse.isOfflineSuccess()) {
                    XESToastUtils.showToast(mContext, "模型库加载失败");
                    return;
                }
                SpeechParamEntity param = new SpeechParamEntity();
                param.setRecogType(SpeechConfig.SPEECH_ENGLISH_EVALUATOR_OFFLINE);
                param.setStrEvaluator("Welcome come to China");
                param.setLocalSavePath(saveVideoFile.getAbsolutePath());
                param.setMultRef(false);
                param.setPcm(true);
                mIse.startRecog(param, new EvaluatorListenerWithPCM() {
                    @Override
                    public void onBeginOfSpeech() {

                    }

                    @Override
                    public void onResult(ResultEntity resultEntity) {
                        if (resultEntity.getStatus() == ResultEntity.SUCCESS) {
                            XESToastUtils.showToast(mContext, resultEntity.getScore() + "");
                            //提前开始下一条
                        } else if (resultEntity.getStatus() == ResultEntity.ERROR) {
                            XESToastUtils.showToast(mContext, "失败");
                            //提前开始下一条
                        } else if (resultEntity.getStatus() == ResultEntity.EVALUATOR_ING) {
                        }
                    }

                    @Override
                    public void onVolumeUpdate(int volume) {

                    }

                    @Override
                    public void onRecordPCMData(short[] pcmBuffer, int length) {
                        //通过声网走
                        byte[] dest = new byte[length * 2];
                        int count = length;
                        for (int i = 0; i < count; i++) {
                            dest[i * 2] = (byte) (pcmBuffer[i]);
                            dest[i * 2 + 1] = (byte) (pcmBuffer[i] >> 8);
                        }
                        if (mWorkerThread != null && mWorkerThread.getRtcEngine() != null) {
                            mWorkerThread.getRtcEngine().pushExternalAudioFrame(dest, System.currentTimeMillis());
                        }
                    }
                });
//                speechEvaluatorInter = mIse.startEnglishEvaluatorOffline("Welcome come to China", saveVideoFile
//                                .getAbsolutePath(), false,
//                        new RoleEvaluatorListener() {
//                            @Override
//                            public void onBeginOfSpeech() {
//                            }
//
//                            @Override
//                            public void onResult(ResultEntity resultEntity) {
//                                if (resultEntity.getStatus() == ResultEntity.SUCCESS) {
//                                    XESToastUtils.showToast(mContext, resultEntity.getScore() + "");
//                                    //提前开始下一条
//                                } else if (resultEntity.getStatus() == ResultEntity.ERROR) {
//                                    XESToastUtils.showToast(mContext, "失败");
//                                    //提前开始下一条
//                                } else if (resultEntity.getStatus() == ResultEntity.EVALUATOR_ING) {
//                                }
//
//                            }
//
//                            @Override
//                            public void onVolumeUpdate(int volume) {
//                            }
//
//                            @Override
//                            public void onRecordPCMData(short[] shorts, int readSize) {
//                                //通过声网走
//                                byte[] dest = new byte[readSize * 2];
//                                int count = readSize;
//                                for (int i = 0; i < count; i++) {
//                                    dest[i * 2] = (byte) (shorts[i]);
//                                    dest[i * 2 + 1] = (byte) (shorts[i] >> 8);
//                                }
//                                if(mWorkerThread!=null && mWorkerThread.getRtcEngine()!=null) {
//                                    mWorkerThread.getRtcEngine().pushExternalAudioFrame(dest, System
// .currentTimeMillis());
//                                }
//                            }
//                        });
            }
        });

        findViewById(R.id.btn_roleplayer_fati).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                bllRole.teacherPushTest(null);
            }
        });

        findViewById(R.id.btn_roleplayer_fenzu).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

            }
        });
    }

    private void gatherData() {
        //int read = mAudioRecorder.read(mAudioBuffer, 0, sizeInBytes);
        while (true) {
            int read = mAudioRecorder.read(mPCMBuffer, 0, sizeInBytes);
//            if (mAudioBuffer != null) {
//                callback.onAudioDataAvailable(System.currentTimeMillis(), mAudioBuffer);
//            }
            if (mPCMBuffer != null) {
                //通过声网走
                int count = read;
                byte[] dest = new byte[count * 2];

                for (int i = 0; i < count; i++) {
                    dest[i * 2] = (byte) (mPCMBuffer[i]);
                    dest[i * 2 + 1] = (byte) (mPCMBuffer[i] >> 8);
                }
                mWorkerThread.getRtcEngine().pushExternalAudioFrame(dest, System.currentTimeMillis());
            }
        }
    }

    private void showDZ() {
        final View view = View.inflate(mContext, R.layout.layout_livevideo_roleplayer_bubble_message_dz, null);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, R.id.rl_livevideo_roleplayer_bubble_dz);
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL, R.id.rl_livevideo_roleplayer_bubble_dz);

        Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.slide_bubble_out_to_top);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        rlBubbleMain.removeView(view);
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        rlBubbleMain.addView(view, lp);
        view.startAnimation(anim);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    /**
     * 进入RolePlayer
     */
    public void enterRolePlayerClick() {
        entity = new RolePlayerEntity();
        //设置RolePlayer总时长
        entity.setCountDownSecond(180);

        //设置所有的角色信息
        Map<String, RolePlayerEntity.RolePlayerHead> mapRoleHead = new HashMap<>();
        Random rand = new Random();
        int headCount = rand.nextInt(5) + 2;
        for (int i = 0; i < headCount; i++) {
            RolePlayerEntity.RolePlayerHead head = new RolePlayerEntity.RolePlayerHead();
            if (i / 2 == 0) {
                head.setNickName("我是小可爱sdfdsfdsgdg" + (i + 1));
                head.setNickName("我是小可爱sdfdsfdsgdg" + (i + 1));
            } else {
                head.setNickName("我是小可" + (i + 1));
                head.setNickName("我是小可" + (i + 1));
            }
            head.setHeadImg("https://img.qq1234.org/uploads/allimg/150818/8_150818113315_2.jpg");
            head.setRoleName("Lily" + (i + 1));
            head.setRoleId(i + 1);
            mapRoleHead.put(head.getNickName(), head);
            entity.getLstRoleInfo().add(head);
        }
        int selfIndex = rand.nextInt(headCount);
        //设置哪个角色是自己扮演的
        entity.getLstRoleInfo().get(selfIndex).setSelfRole(true);
        entity.getLstRoleInfo().get(selfIndex).setRoleName("我");

        //设置所有的对话消息
        int msgCount = rand.nextInt(3) + 3;

        // boolean isFirstSelf = rand.nextInt(2) == 0;
        boolean isFirstSelf = true;
        for (int i = 0; i < msgCount; i++) {
            RolePlayerEntity.RolePlayerHead head;
            if (i == 0 && isFirstSelf) {
                head = entity.getLstRoleInfo().get(selfIndex);
                head.setSelfRole(true);
            } else {
                head = entity.getLstRoleInfo().get(rand.nextInt(headCount));
                //  head.setSelfRole(true);
            }
            String msgContent = lstTestMsg.get(rand.nextInt(lstTestMsg.size()));
            int maxTime = (msgContent.length() / 5 + 2);
            RolePlayerEntity.RolePlayerMessage msg = new RolePlayerEntity.RolePlayerMessage(head, msgContent, maxTime);
            entity.getLstRolePlayerMessage().add(msg);
        }
        //RolePlayerEntity.RolePlayerMessage emptyMsg = new RolePlayerEntity.RolePlayerMessage(entity.getLstRoleInfo
        // ().get(selfIndex), "What are you going to do now ?", 5);
        //emptyMsg.setMsgStatus(RolePlayerEntity.RolePlayerMessageStatus.EMPTY);
        //entity.getLstRolePlayerMessage().add(emptyMsg);
        RolePlayerBll bll = new RolePlayerBll(mContext, null, null, null);
        bll.setRolePlayEntity(entity);
        mPager = new RolePlayerPager(mContext, entity, true, bll, null);
        bll.setRolePlayPager((RolePlayerPager) mPager);
        mPager.initData();
        llRoleMain.addView(mPager.getRootView());
    }

    public static void openActivity(Context context) {
        try {
            Intent intent = new Intent();
            intent.setClass(context, RolePlayerActivity.class);
            context.startActivity(intent);
        } catch (Exception e) {
            com.xueersi.lib.log.Loger.d("yzl", "exception:  " + e);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWorkerThread != null) {
            try {
                mWorkerThread.leaveChannel(mWorkerThread.getEngineConfig().mChannel, new WorkerThread.OnLeaveChannel() {
                    @Override
                    public void onLeaveChannel(int leaveChannel) {
                        StableLogHashMap logHashMap = new StableLogHashMap("getLeaveChannel");
                        logHashMap.put("status", (leaveChannel == 0 ? "1" : "0"));
                        if (leaveChannel != 0) {
                            logHashMap.put("errcode", "" + leaveChannel);
                        }
                    }
                });
                mWorkerThread.exit();
                try {
                    mWorkerThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mWorkerThread = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //结束评测
        if (mIse != null) {
            mIse.stop();
        }
    }

    interface RoleEvaluatorListener extends EvaluatorListenerWithPCM, EvaluatorListener {

    }

    @Override
    protected void onStop() {
        super.onStop();
        logger.i("onStop 离开连麦界面");
        bllRole.realease();
    }
}
