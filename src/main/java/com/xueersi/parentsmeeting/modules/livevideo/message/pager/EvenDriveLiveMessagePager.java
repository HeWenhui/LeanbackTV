package com.xueersi.parentsmeeting.modules.livevideo.message.pager;

import android.content.Context;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;

import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.framework.utils.string.RegexUtils;
import com.xueersi.lib.log.Loger;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.message.business.LiveMessageEmojiParser;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;

import java.util.ArrayList;

public class EvenDriveLiveMessagePager extends LiveMessagePager {
    public EvenDriveLiveMessagePager(Context context, BaseLiveMediaControllerBottom liveMediaControllerBottom, ArrayList<LiveMessageEntity> liveMessageEntities, ArrayList<LiveMessageEntity> otherLiveMessageEntities) {
        super(context, liveMediaControllerBottom, liveMessageEntities, otherLiveMessageEntities);
    }

    protected void addEvenDriveMessageNum(SpannableString spannableString, String evenNum, int type) {

    }

    protected void addEvenDriveMessage(final String sender, final int type, final String text, final String headUrl, final String evenDriveNum) {
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
        // 03.22 体验课播放器统计用户的发送信息
        if (debugMsg && type == LiveMessageEntity.MESSAGE_MINE) {
            StableLogHashMap logHashMap = new StableLogHashMap("LiveFreePlayUserMsg");
            logHashMap.put("LiveFreePlayUserMsg", text);
            logHashMap.put("eventid", LiveVideoConfig.LIVE_EXPERIENCE_IMMSG);
            umsAgentDebugInter(LiveVideoConfig.LIVE_EXPERIENCE_IMMSG, logHashMap.getData());
        }
        Loger.e("Duncan", "sender:" + sender);
    }
}
