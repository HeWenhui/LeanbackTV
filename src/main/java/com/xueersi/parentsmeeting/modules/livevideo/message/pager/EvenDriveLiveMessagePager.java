package com.xueersi.parentsmeeting.modules.livevideo.message.pager;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;

import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.framework.utils.string.RegexUtils;
import com.xueersi.lib.log.Loger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.message.business.LiveMessageEmojiParser;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.VerticalImageSpan;

import java.util.ArrayList;

public class EvenDriveLiveMessagePager extends LiveMessagePager {
    public EvenDriveLiveMessagePager(Context context, BaseLiveMediaControllerBottom liveMediaControllerBottom, ArrayList<LiveMessageEntity> liveMessageEntities, ArrayList<LiveMessageEntity> otherLiveMessageEntities) {
        super(context, liveMediaControllerBottom, liveMessageEntities, otherLiveMessageEntities);
    }


    /** 中学连对激励对应的图片 */
    private final int[] evenDriveNumDrawable = new int[]{
            0,
            0,
            R.drawable.livevideo_evendrive_livemessage_double,
            R.drawable.livevideo_evendrive_livemessage_treble,
            R.drawable.livevideo_evendrive_livemessage_quattuor,
            R.drawable.livevideo_evendrive_livemessage_quintupling,
            R.drawable.livevideo_evendrive_livemessage_sextuple,
            R.drawable.livevideo_evendrive_livemessage_septuple,
            R.drawable.livevideo_evendrive_livemessage_octuple,
//            R.drawable.livevideo_evendrive_livemessage_nonuple,
            R.drawable.livevideo_evendrive_livemessage_king,
            R.drawable.livevideo_evendrive_livemessage_topping};

    /**
     * 中学连对激励系统使用的img
     *
     * @param spanttt
     * @param sEvenNum
     */
    @Override
    protected SpannableString addEvenDriveMessageNum(SpannableString spanttt, String sEvenNum, int type) {
        VerticalImageSpan verticalImageSpan = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            int evenNum = 0;
            try {
                if (type != LiveMessageEntity.MESSAGE_MINE) {
                    evenNum = Integer.valueOf(sEvenNum);
                } else {
                    evenNum = Integer.valueOf(myTest ? "5" : "0");
                }
                if (evenNum >= 2) {
                    Drawable drawable = mContext.getResources().getDrawable(evenDriveNumDrawable[evenNum]);
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                    if (evenNum >= 2 && evenNum < 8) {
//                                    evenDrawa = mContext.getDrawable(evenNum);
                        verticalImageSpan = new VerticalImageSpan(drawable);
                    } else if (8 <= evenNum && evenNum < 24) {
                        verticalImageSpan = new VerticalImageSpan(drawable);
                    } else if (evenNum >= 24) {
                        verticalImageSpan = new VerticalImageSpan(drawable);
                    }
                }
            } catch (Exception e) {
                logger.e(e);
                e.printStackTrace();
            }
        }
        if (verticalImageSpan != null) {
            spanttt.setSpan(verticalImageSpan, 0, "icon".length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        } else {
            spanttt = new SpannableString(spanttt.subSequence("icon".length(), spanttt.length()));
        }
        return spanttt;
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
