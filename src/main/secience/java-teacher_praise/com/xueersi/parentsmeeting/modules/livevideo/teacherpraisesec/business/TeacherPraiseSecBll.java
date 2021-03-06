package com.xueersi.parentsmeeting.modules.livevideo.teacherpraisesec.business;

import android.app.Activity;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveEventBus;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.TopicAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.event.TeachPraiseRusltulCloseEvent;
import com.xueersi.parentsmeeting.modules.livevideo.event.TeacherPraiseEvent;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.teacherpraisesec.page.SpeechEnergyPager;
import com.xueersi.parentsmeeting.modules.livevideo.teacherpraise.business.TeacherPraiseBll;
import com.xueersi.parentsmeeting.modules.livevideo.teacherpraisesec.page.SpeechPraisePager;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author chenkun
 * 老师点赞
 */
public class TeacherPraiseSecBll extends LiveBaseBll implements NoticeAction, TopicAction {
    private boolean isAnimStart = false;
    private boolean addEnergy = false;
    private LiveGetInfo getInfo;
    private String voiceId = "";
    HashMap<String, Boolean> show = new HashMap<>();
    TeacherPraiseBll teacherPraiseBll;

    public TeacherPraiseSecBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
        teacherPraiseBll = new TeacherPraiseBll(context, liveBll);
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        this.getInfo = getInfo;
        teacherPraiseBll.onLiveInited(getInfo);
    }

    /**
     * 显示 老师点赞
     */
    public void showTeacherPraise() {
        logger.d("showTeacherPraise:voiceId=" + voiceId);
        post(new Runnable() {
            @Override
            public void run() {
                if (StringUtils.isEmpty(voiceId) || !show.containsKey(voiceId)) {
                    show.put(voiceId, true);
                    LiveEventBus.getDefault(activity).post(new TeacherPraiseEvent(true));
                    if ("1".equals(getInfo.getIsAllowTeamPk())) {
                        if (!addEnergy) {
                            addEnergy = true;
                            final SpeechEnergyPager speechEnergyPager = new SpeechEnergyPager(mContext);
                            addView(speechEnergyPager.getRootView());
                            speechEnergyPager.setOnPagerClose(new LiveBasePager.OnPagerClose() {
                                @Override
                                public void onClose(LiveBasePager basePager) {
                                    removeView(basePager.getRootView());
                                    LiveEventBus.getDefault(activity).post(new TeacherPraiseEvent(false));
                                    TeachPraiseRusltulCloseEvent teachPraiseRusltulCloseEvent = new TeachPraiseRusltulCloseEvent(voiceId + "_1", false);
                                    teachPraiseRusltulCloseEvent.setStartPosition(speechEnergyPager.getEnergyPosition());
                                    EventBus.getDefault().post(teachPraiseRusltulCloseEvent);
                                }
                            });
                        }
                    } else {
                        SpeechPraisePager speechPraisePager = new SpeechPraisePager(mContext, 1 == getInfo.getIsYouJiao());
                        addView(speechPraisePager.getRootView());
                        speechPraisePager.setOnPagerClose(new LiveBasePager.OnPagerClose() {
                            @Override
                            public void onClose(LiveBasePager basePager) {
                                removeView(basePager.getRootView());
                                LiveEventBus.getDefault(activity).post(new TeacherPraiseEvent(false));
                            }
                        });
                    }
                }
            }
        });
    }

    private int[] noticeCodes = {
            XESCODE.TEACHER_VOICE_PRAISE, XESCODE.SPEECH_COLLECTIVE, XESCODE.TEACHER_PRAISE
    };

    @Override
    public void onTopic(LiveTopic liveTopic, JSONObject jsonObject, boolean modeChange) {
//        LiveTopic.RoomStatusEntity mainRoomstatus = liveTopic.getMainRoomstatus();
//        String status = mainRoomstatus.getOnGroupSpeech();
//        if ("on".equals(status)) {
//            voiceId = mainRoomstatus.getGroupSpeechRoom();
//        } else {
//            addEnergy = false;
//            voiceId = "";
//        }
        if (LiveTopic.MODE_CLASS.equals(liveTopic.getMode())) {
            LiveTopic.RoomStatusEntity mainRoomstatus = liveTopic.getMainRoomstatus();
            String status = mainRoomstatus.getOnGroupSpeech();
            if ("on".equals(status)) {
                voiceId = mainRoomstatus.getGroupSpeechRoom();
            } else {
                addEnergy = false;
                voiceId = "";
            }
        } else {
            LiveTopic.RoomStatusEntity coachRoomstatus = liveTopic.getCoachRoomstatus();
            String status = coachRoomstatus.getOnGroupSpeech();
            if ("on".equals(status)) {
                voiceId = coachRoomstatus.getGroupSpeechRoom();
            } else {
                addEnergy = false;
                voiceId = "";
            }
        }
    }

    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {
        switch (type) {
            case XESCODE.TEACHER_VOICE_PRAISE:
                showTeacherPraise();
                break;
            case XESCODE.TEACHER_PRAISE: {
                if (StringUtils.isEmpty(voiceId)) {
                    teacherPraiseBll.showTeacherPraise();
                } else {
                    showTeacherPraise();
                }
            }
            break;
            case XESCODE.SPEECH_COLLECTIVE:
                try {
                    ShareDataManager.getInstance().put("isOnTopic", false, ShareDataManager.SHAREDATA_USER);
                    final String voiceID = data.optString("voiceId");
                    final String status = data.optString("status");
                    final String form = data.getString("from");
                    if (LiveTopic.MODE_CLASS.equals(mLiveBll.getMode()) && "t".equals(form)) {
                        if ("on".equals(status)) {
                            voiceId = voiceID;
                        } else {
                            addEnergy = false;
                            voiceId = "";
                        }
                    } else if (LiveTopic.MODE_TRANING.equals(mLiveBll.getMode()) && "f".equals(form)) {
                        if ("on".equals(status)) {
                            voiceId = voiceID;
                        } else {
                            addEnergy = false;
                            voiceId = "";
                        }
                    }
                } catch (Exception e) {
                    LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        teacherPraiseBll.onDestroy();
    }

    @Override
    public int[] getNoticeFilter() {
        return noticeCodes;
    }
}
