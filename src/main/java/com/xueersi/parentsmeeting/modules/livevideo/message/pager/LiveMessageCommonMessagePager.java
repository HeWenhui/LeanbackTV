package com.xueersi.parentsmeeting.modules.livevideo.message.pager;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.widget.ListView;

import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.framework.utils.string.RegexUtils;
import com.xueersi.lib.log.Loger;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.message.business.LiveMessageEmojiParser;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.evendrive.BaseEvenDriveCommonPager;
import com.xueersi.ui.adapter.CommonAdapter;

import java.util.ArrayList;

/**
 * created by zyy 2019/10/30
 */
public abstract class LiveMessageCommonMessagePager extends BaseEvenDriveCommonPager {

    /** 聊天字体大小，最多13个汉字 */
    protected int messageSize = 0;

    /** 竖屏的时候，也添加横屏的消息 */
    protected ArrayList<LiveMessageEntity> otherLiveMessageEntities;

    protected CommonAdapter<LiveMessageEntity> otherMessageAdapter;

    //聊天适配器
    protected CommonAdapter<LiveMessageEntity> messageAdapter;

    protected boolean isTouch = false;

    /** 聊天消息 */
    protected ListView lvMessage;

    protected LiveAndBackDebug liveAndBackDebug;

    public LiveMessageCommonMessagePager(Context context) {
        super(context);
    }

    /*添加聊天信息，超过120，移除60个*/
    @Override
    public void addMessage(final String sender, final int type, final String text, final String headUrl) {
        final Exception e = new Exception();
        logger.i("sender:" + sender + ",text=" + text);
        pool.execute(new Runnable() {
            @Override
            public void run() {
                logger.i("聊天的数量：" + liveMessageEntities.size());
                final SpannableStringBuilder sBuilder = LiveMessageEmojiParser.convertToHtml(RegexUtils
                        .chatSendContentDeal(text), mContext, messageSize);
                mView.post(new Runnable() {
                    @Override
                    public void run() {
                        if (liveMessageEntities.size() > 29) {
                            liveMessageEntities.remove(0);
                        }
                        LiveMessageEntity entity = new LiveMessageEntity(sender, type, sBuilder, headUrl);
                        if (type == LiveMessageEntity.MESSAGE_MINE) {
                            entity.setEvenNum(myTest ? "35" : "" + getEvenNum());
                        }
                        liveMessageEntities.add(entity);
                        logger.i("聊天的数量：" + liveMessageEntities.size() + "，最后一条是" + liveMessageEntities.get(liveMessageEntities.size() - 1));
                        if (otherLiveMessageEntities != null) {
                            if (otherLiveMessageEntities.size() > 29) {
                                otherLiveMessageEntities.remove(0);
                            }
                            otherLiveMessageEntities.add(entity);
                        }
                        if (otherMessageAdapter != null) {
                            otherMessageAdapter.notifyDataSetChanged();
                        }
                        if (messageAdapter != null) {
                            messageAdapter.notifyDataSetChanged();
                        } else {
                            UmsAgentManager.umsAgentException(ContextManager.getContext(), TAG + mContext + "," + sender + "," + type, e);
                        }
                        if (!isTouch) {
                            lvMessage.setSelection(lvMessage.getCount() - 1);
                        }
                    }
                });
            }
        });
        // 03.22 体验课播放器统计用户的发送信息
        if (liveAndBackDebug != null && type == LiveMessageEntity.MESSAGE_MINE) {
            StableLogHashMap logHashMap = new StableLogHashMap("LiveFreePlayUserMsg");
            logHashMap.put("LiveFreePlayUserMsg", text);
            logHashMap.put("eventid", LiveVideoConfig.LIVE_EXPERIENCE_IMMSG);
            liveAndBackDebug.umsAgentDebugInter(LiveVideoConfig.LIVE_EXPERIENCE_IMMSG, logHashMap.getData());
        }
        logger.e("sender:" + sender);
    }

    protected void addEvenDriveMessage(final String sender, final int type,
                                       final String text, final String headUrl,
                                       final String evenDriveNum) {
        if (isOpenStimulation()) {
            final Exception e = new Exception();
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    final SpannableStringBuilder sBuilder = LiveMessageEmojiParser.convertToHtml(RegexUtils
                                    .chatSendContentDeal(text), mContext,
                            messageSize);
                    mView.post(new Runnable() {
                        @Override
                        public void run() {
                            if (liveMessageEntities.size() > 29) {
                                liveMessageEntities.remove(0);
                            }
                            LiveMessageEntity entity = new LiveMessageEntity(sender, type, sBuilder, headUrl);
                            entity.setEvenNum(evenDriveNum);
                            liveMessageEntities.add(entity);
                            if (otherLiveMessageEntities != null) {
                                if (otherLiveMessageEntities.size() > 29) {
                                    otherLiveMessageEntities.remove(0);
                                }
                                otherLiveMessageEntities.add(entity);
                            }
                            if (otherMessageAdapter != null) {
                                otherMessageAdapter.notifyDataSetChanged();
                            }
                            if (messageAdapter != null) {
                                messageAdapter.notifyDataSetChanged();
                            } else {
                                Loger.e(ContextManager.getContext(), TAG, "" + mContext + "," + sender + "," + type, e,
                                        true);
                            }
                            if (!isTouch) {
                                lvMessage.setSelection(lvMessage.getCount() - 1);
                            }
                        }
                    });
                }
            });
        } else {
            addMessage(sender, type, text, headUrl);
        }
        // 03.22 体验课播放器统计用户的发送信息
//        if (debugMsg && type == LiveMessageEntity.MESSAGE_MINE) {
//            StableLogHashMap logHashMap = new StableLogHashMap("LiveFreePlayUserMsg");
//            logHashMap.put("LiveFreePlayUserMsg", text);
//            logHashMap.put("eventid", LiveVideoConfig.LIVE_EXPERIENCE_IMMSG);
//            umsAgentDebugInter(LiveVideoConfig.LIVE_EXPERIENCE_IMMSG, logHashMap.getData());
//        }
//        Loger.e("Duncan", "sender:" + sender);
    }

}
