package com.xueersi.parentsmeeting.modules.livevideo.message.pager;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.framework.utils.string.RegexUtils;
import com.xueersi.lib.log.Loger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.entity.User;
import com.xueersi.parentsmeeting.modules.livevideo.message.business.LiveMessageEmojiParser;
import com.xueersi.parentsmeeting.modules.livevideo.message.config.LiveMessageConfig;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.evendrive.EvenDriveUtils;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.CenterAlignImageSpan;
import com.xueersi.ui.adapter.AdapterItemInterface;
import com.xueersi.ui.adapter.CommonAdapter;

import java.util.ArrayList;

public class LectureLiveMessagePager extends LiveMessagePager {

    public LectureLiveMessagePager(Context context, BaseLiveMediaControllerBottom liveMediaControllerBottom, ArrayList<LiveMessageEntity> liveMessageEntities, ArrayList<LiveMessageEntity> otherLiveMessageEntities) {
        super(context, liveMediaControllerBottom, liveMessageEntities, otherLiveMessageEntities);
        Resources resources = context.getResources();
        nameColors[0] = resources.getColor(R.color.COLOR_4A82F7);
        nameColors[1] = resources.getColor(R.color.COLOR_5B6169);
        nameColors[2] = resources.getColor(R.color.COLOR_5B6169);
        nameColors[3] = resources.getColor(R.color.COLOR_EB002A);
    }


    @Override
    protected void setMessageLayout() {
        layout = R.layout.page_livevideo_lecture_message;
    }

    @Override
    public void initData() {
        super.initData();
        int screenWidth = ScreenUtils.getScreenWidth();
        int wradio = (int) (LiveVideoConfig.VIDEO_HEAD_WIDTH * screenWidth / LiveVideoConfig.VIDEO_WIDTH);
        int minisize = wradio / 13;
        messageSize = Math.max((int) (ScreenUtils.getScreenDensity() * 12), minisize);
        logger.e("messageSize:" + messageSize);
        logger.e("size:"+ SizeUtils.Dp2Px(mContext,14));
    }

    @Override
    protected void initAdapter() {
        messageAdapter = new CommonAdapter<LiveMessageEntity>(liveMessageEntities) {
            @Override
            public AdapterItemInterface<LiveMessageEntity> getItemView(Object type) {
                return new AdapterItemInterface<LiveMessageEntity>() {
                    TextView tvMessageItem;

                    @Override
                    public int getLayoutResId() {
                        return R.layout.item_livevideo_message;
                    }

                    @Override
                    public void initViews(View root) {
                        tvMessageItem = (TextView) root.findViewById(R.id.tv_livevideo_message_item);
                        tvMessageItem.setTextSize(TypedValue.COMPLEX_UNIT_PX, messageSize);
                        tvMessageItem.setTextColor(mContext.getResources().getColor(R.color.COLOR_212831));
                    }

                    @Override
                    public void bindListener() {

                    }

                    @Override
                    public void updateViews(LiveMessageEntity entity, int position, Object objTag) {
                        String sender = entity.getSender();
                        SpannableString spanttt = new SpannableString(sender + ": ");
                        int color;
                        switch (entity.getType()) {
                            case LiveMessageEntity.EVEN_DRIVE_LIKE:
                            case LiveMessageEntity.EVEN_DRIVE_REPORT:
                            case LiveMessageEntity.MESSAGE_MINE:
                            case LiveMessageEntity.MESSAGE_TEACHER:
                            case LiveMessageEntity.MESSAGE_TIP:
                            case LiveMessageEntity.MESSAGE_CLASS:
                                color = nameColors[entity.getType()];
                                break;
                            default:
                                color = nameColors[0];
                                break;
                        }

                        CharacterStyle characterStyle = new ForegroundColorSpan(color);
                        spanttt.setSpan(characterStyle, 0, sender.length() + 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        if (urlclick == 1 && LiveMessageEntity.MESSAGE_TEACHER == entity.getType()) {
                            SpannableString tSpanttt = new SpannableString("# "+sender + ": ");
                            Drawable drawable =mContext.getResources().getDrawable(R.drawable.live_icon_msg_teacher);
                            drawable.setBounds(0, 0, SizeUtils.Dp2Px(tvMessageItem.getContext(), 28),
                                    SizeUtils.Dp2Px(tvMessageItem.getContext(), 16));
                            CenterAlignImageSpan imageSpan = new CenterAlignImageSpan(drawable);
                            tSpanttt.setSpan(imageSpan, 0, 1, ImageSpan.ALIGN_BASELINE);
                            tSpanttt.setSpan(characterStyle, 1, sender.length() + 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                            tvMessageItem.setAutoLinkMask(Linkify.WEB_URLS);
                            urlClick(tvMessageItem);
                            SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
                            stringBuilder.append(tSpanttt).append(entity.getText());
                            tvMessageItem.setText(stringBuilder);

                        } else if (LiveMessageEntity.EVEN_DRIVE_REPORT == entity.getType()) {
                            //点击连对激励系统的查看排行榜，弹出排行榜的内容
                            tvMessageItem.setText(spanttt);
                            tvMessageItem.append(entity.getText());
                            tvMessageItem.append(clickEvenDrive(entity));
                            tvMessageItem.setMovementMethod(LinkMovementMethod.getInstance());
                            tvMessageItem.setHighlightColor(mContext.getResources().getColor(R.color.COLOR_00000000));
                        } else if (LiveMessageEntity.EVEN_DRIVE_LIKE == entity.getType()) {
                            //显示别人给我点赞的信息
                            tvMessageItem.setText(likeEvenDrive(entity));
                        } else {
                            tvMessageItem.setAutoLinkMask(0);
                            if (getInfo != null && EvenDriveUtils.getAllEvenDriveOpen(getInfo)) {
                                SpannableString itemSpan;
                                SpannableString evenSpan = new SpannableString("icon ");
                                itemSpan = addEvenDriveMessageNum(evenSpan, entity.getEvenNum(), entity.getType());
                                if (itemSpan != null) {
                                    tvMessageItem.setText(itemSpan);
                                    tvMessageItem.append(spanttt);
                                } else {
                                    tvMessageItem.setText(spanttt);
                                }
                            } else {
                                tvMessageItem.setText(spanttt);
                            }
                            tvMessageItem.append(entity.getText());
                            logger.i(tvMessageItem.getText());
                        }
                    }
                };
            }
        };
    }

    @Override
    public void onUserList(String channel, final User[] users) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                tvMessageCount.setText("本教室在线"+peopleCount + "人");
            }
        });
    }

    @Override
    public void onJoin(String target, String sender, String login, String hostname) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                tvMessageCount.setText("本教室在线"+peopleCount + "人");
            }
        });
    }

    @Override
    public void onQuit(String sourceNick, String sourceLogin, String sourceHostname,
                       String reason) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                tvMessageCount.setText("本教室在线"+peopleCount + "人");
            }
        });
    }


    @Override
    public void onMessage(String target, String sender, String login, String hostname, String text, String headurl) {
        Loger.e("LiveMessagerPager", "=====>onMessage called");
        addMessage(hostname, LiveMessageEntity.MESSAGE_TEACHER, text, headurl);
    }

    /**
     * 中学连对激励对应的图片
     *  */
    private final int[] evenDriveNumDrawable = new int[]{
            0,
            0,
            R.drawable.livevideo_evendrive_livemessage_double,
            R.drawable.livevideo_evendrive_livemessage_treble,
            R.drawable.livevideo_evendrive_livemessage_quattuor,
            R.drawable.livevideo_evendrive_livemessage_quintupling,
            R.drawable.livevideo_evendrive_livemessage_sextuple,
            R.drawable.livevideo_evendrive_livemessage_septuple,
//            R.drawable.livevideo_evendrive_livemessage_octuple,
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
        CenterAlignImageSpan verticalImageSpan = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            int evenNum = 0;
            try {
//                if (type != LiveMessageEntity.MESSAGE_MINE) {
                evenNum = Integer.valueOf(sEvenNum);
//                }
//                else {
//                    evenNum = Integer.valueOf(myTest ? "24" : mNowEvenNum);
//                }
                if (evenNum >= 2) {
                    Drawable drawable;
//                    if (evenNum < 8) {
//                        drawable = mContext.getResources().getDrawable(evenDriveNumDrawable[evenNum]);
//                    } else if (evenNum <= 24) {
//                        drawable = mContext.getResources().getDrawable(evenDriveNumDrawable[9]);
//                    } else {
//                        drawable = mContext.getResources().getDrawable(evenDriveNumDrawable[10]);
//                    }
                    if (evenNum >= 2 && evenNum < 8) {
//                                    evenDrawa = mContext.getDrawable(evenNum);
                        drawable = mContext.getResources().getDrawable(evenDriveNumDrawable[evenNum]);
                        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                        verticalImageSpan = new CenterAlignImageSpan(drawable);
                    } else if (8 <= evenNum && evenNum <= 24) {
                        drawable = mContext.getResources().getDrawable(evenDriveNumDrawable[8]);
                        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                        verticalImageSpan = new CenterAlignImageSpan(drawable);
                    } else if (evenNum > 24) {
                        drawable = mContext.getResources().getDrawable(evenDriveNumDrawable[9]);
                        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                        verticalImageSpan = new CenterAlignImageSpan(drawable);
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
    }
}
