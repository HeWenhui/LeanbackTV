package com.xueersi.parentsmeeting.modules.livevideoOldIJK.achievement.business;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.RelativeLayout;

import com.tal.speech.language.TalLanguage;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.achievement.page.EnglishSpeekPager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.AudioRequest;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.BaseLiveMessagePager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.message.business.LiveMessageBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.stablelog.EnglishSpeekLog;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.LiveLoggerFactory;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.ProxUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class EnglishSpeekEnBll extends BaseEnglishStandSpeekBll implements EnglishSpeekAction {
    protected Logger logger = LiveLoggerFactory.getLogger("EnglishSpeekEnBll");
    Activity activity;
    LiveGetInfo liveGetInfo;
    private String mode = "";
    String eventId = LiveVideoConfig.LIVE_ENGLISH_SPEEK;
    private EnglishSpeekHttp liveBll;
    private LiveAndBackDebug liveAndBackDebug;
    RelativeLayout bottomContent;
    Handler handler = new Handler(Looper.getMainLooper());

    LiveGetInfo.TotalOpeningLength totalOpeningLength;

    boolean dbStart = false;
    /** 打点开始开口时长 */
    int dbSecond;
    /** 打点开始开口次数 */
    int dbStartEnSegNum;
    /** 打点开始开口次数 */
    int lastEnSegNum;
    int lastdbDuration = 1;
    int totalEn_seg_num;
    String lastduration;
    /** 打点期间时长 */
    int dbDuration = 0;
    long lastDBTime;
    int sendDbDuration = 1;
    int second15;
    /** 上次开口时长 */
    int lastSecond;
    int MAX_SECOND = 15;
    StringBuilder totalEn_seg_len = new StringBuilder();

    public EnglishSpeekEnBll(Activity activity, LiveGetInfo liveGetInfo) {
        this.activity = activity;
        this.liveGetInfo = liveGetInfo;
        liveAndBackDebug = ProxUtil.getProxUtil().get(activity, LiveAndBackDebug.class);
        setTotalOpeningLength(liveGetInfo.getTotalOpeningLength());
    }

    public boolean initView(RelativeLayout bottomContent, String mode, TalLanguage talLanguage, final AtomicBoolean audioRequest, RelativeLayout mContentView) {
        this.mode = mode;
        this.bottomContent = bottomContent;
        logger.d("initView:mode=" + mode + ",Request=" + audioRequest.get());
        if (LiveTopic.MODE_CLASS.equals(mode)) {
            if (!audioRequest.get()) {
                start();
            }
        }
        return true;
    }

    public void setLiveBll(EnglishSpeekHttp liveBll) {
        this.liveBll = liveBll;
    }

    public void setTotalOpeningLength(LiveGetInfo.TotalOpeningLength totalOpeningLength) {
        this.totalOpeningLength = totalOpeningLength;
        int d = (int) totalOpeningLength.duration;
        second15 = d % 60 % 15;
        setEnglishTime(d / 60, d % 60);
//        tv_livevideo_english_prog.setProgress(second15 * 3);
        setTime(MAX_SECOND - second15);
        if (!StringUtils.isEmpty(totalOpeningLength.speakingLen)) {
            totalEn_seg_len.append(totalOpeningLength.speakingLen);
        }
//        lastSecond = (int) totalOpeningLength.duration;
    }

    @Override
    public void onPredict(String predict) {
//        logger.d("onPredict:predict=" + predict);
        try {
            JSONObject jsonObject = new JSONObject(predict);
            int en_seg_num = 0;
            totalEn_seg_num += en_seg_num;
//                                logger.d( "onProcessData:out=" + out);
            final String duration = jsonObject.getString("time");
            if (duration == null || duration.equals(lastduration)) {
                return;
            }
            final String en_seg_len = jsonObject.optString("duration");
            lastduration = duration;
            double time = Double.parseDouble(duration);
            final int totalSecond = (int) time;
            if (dbStart) {
                dbDuration = totalSecond - dbSecond;
                long nowTime = System.currentTimeMillis();
                if (nowTime - lastDBTime >= 3000) {
                    sendDbDuration = dbDuration;
                    liveBll.sendDBStudent(dbDuration);
                    lastDBTime = nowTime;
                    logger.d("onProcessData(sendDBStudent):dbDuration=" + dbDuration);
                }
            }
            second15 += totalSecond - lastSecond;
//            final int oldProgress = tv_livevideo_english_prog.getProgress();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (totalOpeningLength.duration == 0) {
                        setEnglishTime(totalSecond / 60, totalSecond % 60);
                    } else {
                        int d = (int) totalOpeningLength.duration;
                        setEnglishTime((totalSecond + d) / 60, (totalSecond + d) % 60);
                    }
//                    if (second15 * 3 != oldProgress) {
//                        int second = MAX_SECOND - second15;
//                        float startProgress = oldProgress;
//                        float newProgress;
//                        if (second < 0) {
////                                                newProgress = (second15 - 15) * 3;
////                                                setTime(2 * MAX_SECOND - second15);
//                            newProgress = (second15 % MAX_SECOND) * 3;
//                            setTime(MAX_SECOND - second15 % MAX_SECOND);
////                                                        logger.d( "onProcessData(<0):oldProgress=" + oldProgress
//// + ",second15=" + second15);
//                        } else {
//                            newProgress = second15 * 3;
//                            if (second15 != 15) {
//                                setTime(MAX_SECOND - second15);
//                            } else {
//                                setTime(MAX_SECOND);
//                            }
//                        }
//                        logger.d("onProcessData:second=" + second + ",oldProgress=" + oldProgress
//                                + ",newProgress=" + newProgress);
//                        if (newProgress != 45) {
//                            setProg(startProgress, newProgress);
//                        } else {
//                            if (lastValueAnimator != null) {
//                                lastValueAnimator.cancel();
//                                lastValueAnimator = null;
//                            }
//                            tv_livevideo_english_prog.setProgress(0);
//                        }
//                    }
                    if (!"".equals(en_seg_len)) {
                        totalEn_seg_len.append(en_seg_len).append(",");
                    }
                    if (second15 >= 15) {
                        second15 = second15 % MAX_SECOND;
                        double douduration = Double.parseDouble(duration);
//                        int location[] = new int[2];
//                        tv_livevideo_english_prog.getLocationInWindow(location);
                        String speakingLen = totalEn_seg_len.toString();
                        liveBll.setTotalOpeningLength(1000, "" + (douduration + totalOpeningLength
                                        .duration),
                                "" + (totalEn_seg_num + totalOpeningLength.speakingNum), speakingLen,
                                0, 0);
                    }
                    lastSecond = totalSecond;
                    lastEnSegNum = totalEn_seg_num;
                }
            });
        } catch (JSONException e) {
            logger.e("onPredict", e);
        }
    }

    private void setTime(int second) {
//        SpannableString sp;
//        if (!isSmallEnglish) {
//            sp = new SpannableString("再说" + second + "秒获得");
//            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(activity.getResources().getColor(R.color
//                    .COLOR_FFFF00));
//            sp.setSpan(foregroundColorSpan, 2, 2 + ("" + second).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        } else {
//            sp = new SpannableString("继续说" + second + "秒可获得");
//            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(activity.getResources().getColor(R.color
//                    .COLOR_FFFF00));
//            sp.setSpan(foregroundColorSpan, 3, 3 + ("" + second).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        }
//
//        tv_livevideo_english_time2.setText(sp);
    }

    private void setEnglishTime(int min, int second) {
        String mingStr;
        if (min < 10) {
            mingStr = "0" + min;
        } else {
            mingStr = "" + min;
        }
        String secStr;
        if (second < 10) {
            secStr = "0" + second;
        } else {
            secStr = "" + second;
        }
//        tv_livevideo_english_time.setText(mingStr + ":" + secStr);
    }

    @Override
    public void onDBStart() {
        logger.d("onDBStart:dbStart=" + dbStart);
        if (!dbStart) {
            dbStart = true;
            dbSecond = lastSecond;
            dbStartEnSegNum = lastEnSegNum;
            dbDuration = 0;
            sendDbDuration = 0;
            Map<String, String> mData = new HashMap<>();
            mData.put("logtype", "start");
            liveAndBackDebug.umsAgentDebugSys(eventId, mData);
        }
    }

    @Override
    public void onDBStop() {
        logger.d("onDBStop:dbStart=" + dbStart + ",dbDuration=" + dbDuration + ",sendDbDuration=" + sendDbDuration);
        if (dbStart) {
            dbStart = false;
            LiveMessageBll liveMessageBll = ProxUtil.getProxUtil().get(activity, LiveMessageBll.class);
            if (sendDbDuration == 0) {
                liveBll.setNotOpeningNum();
                if (liveMessageBll != null) {
                    liveMessageBll.addMessage(BaseLiveMessagePager.SYSTEM_TIP_STATIC, LiveMessageEntity.MESSAGE_TIP,
                            "大声的说出来，老师很想听到你的声音哦~");
                }
            } else {
                if (lastdbDuration == 0 && liveMessageBll != null) {
                    liveMessageBll.addMessage(BaseLiveMessagePager.SYSTEM_TIP_STATIC, LiveMessageEntity.MESSAGE_TIP,
                            "没错，就是这样，继续坚持下去！");
                }
            }
            lastdbDuration = sendDbDuration;
            Map<String, String> mData = new HashMap<>();
            mData.put("duration", "" + dbDuration);
            mData.put("speakNum", "" + (lastEnSegNum - dbStartEnSegNum));
            mData.put("logtype", "stop");
            liveAndBackDebug.umsAgentDebugSys(eventId, mData);
        }
    }

    @Override
    public TalLanguage getTalLanguage() {
        return null;
    }

    private EnglishSpeekPager englishSpeekPager;

    @Override
    public void praise(int answer) {
        logger.d("praise:dbDuration=" + sendDbDuration + ",answer=" + answer);
        if (sendDbDuration >= answer) {
            EnglishSpeekLog.sendPraise(liveAndBackDebug, "" + answer, "" + sendDbDuration);
            bottomContent.post(new Runnable() {
                @Override
                public void run() {
                    if (englishSpeekPager == null) {
                        englishSpeekPager = new EnglishSpeekPager(activity);
                    } else {
                        //移出之前的弹窗
                        if (englishSpeekPager.getRootView().getParent() == bottomContent) {
                            bottomContent.removeView(englishSpeekPager.getRootView());
                        }
                    }
                    bottomContent.removeCallbacks(removeViewRunnable);
                    View view = englishSpeekPager.getRootView();
                    englishSpeekPager.updateStatus(EnglishSpeekPager.PRAISE);
                    RelativeLayout.LayoutParams lp = englishSpeekPager.getLayoutParams();
                    bottomContent.addView(view, lp);
                    bottomContent.postDelayed(removeViewRunnable, 1000);
                }
            });
        }
    }

    private Runnable removeViewRunnable = new Runnable() {
        @Override
        public void run() {
            if (englishSpeekPager != null && englishSpeekPager.getRootView().getParent() == bottomContent) {
                bottomContent.removeView(englishSpeekPager.getRootView());
            }
        }
    };

    @Override
    public void remind(int answer) {
        logger.d("remind:sendDbDuration=" + sendDbDuration + ",answer=" + answer);
        if (sendDbDuration <= answer) {
            EnglishSpeekLog.sendRemind(liveAndBackDebug, "" + answer, "" + sendDbDuration);
            bottomContent.post(new Runnable() {

                @Override
                public void run() {
                    if (englishSpeekPager == null) {
                        englishSpeekPager = new EnglishSpeekPager(activity);
                    } else {
                        //移出之前的弹窗
                        if (englishSpeekPager.getRootView().getParent() == bottomContent) {
                            bottomContent.removeView(englishSpeekPager.getRootView());
                        }
                    }
                    bottomContent.removeCallbacks(removeViewRunnable);
                    View view = englishSpeekPager.getRootView();
                    englishSpeekPager.updateStatus(EnglishSpeekPager.REMIND);
                    RelativeLayout.LayoutParams lp = englishSpeekPager.getLayoutParams();
                    bottomContent.addView(view, lp);
                    bottomContent.postDelayed(removeViewRunnable, 1000);
                }
            });
        }
    }

    @Override
    public void onModeChange(String mode, boolean audioRequest) {
        logger.d("onModeChange:mode=" + mode + ",audioRequest=" + audioRequest);
        this.mode = mode;
        if (LiveTopic.MODE_TRANING.equals(mode)) {
            stop(null);
        } else {
            if (!audioRequest) {
                start();
            }
        }
    }

    @Override
    public void start() {
        logger.d("start:speakerRecognitioner=null?" + (speakerRecognitioner == null));
        if (speakerRecognitioner != null) {
            speakerRecognitioner.setSpeakerPredict(this);
            speakerRecognitioner.start();
        }
    }

    @Override
    public void stop(AudioRequest.OnAudioRequest onAudioRequest) {
        logger.d("stop:speakerRecognitioner=null?" + (speakerRecognitioner == null));
        if (speakerRecognitioner != null) {
            speakerRecognitioner.setSpeakerPredict(null);
            speakerRecognitioner.stop();
            if (onAudioRequest != null) {
                onAudioRequest.requestSuccess();
            }
        } else {
            if (onAudioRequest != null) {
                onAudioRequest.requestSuccess();
            }
        }
    }

    @Override
    public void destory() {
        if (speakerRecognitioner != null) {
            speakerRecognitioner.stop();
        }
    }
}
