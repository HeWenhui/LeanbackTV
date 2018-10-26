package com.xueersi.parentsmeeting.modules.livevideo.message.pager;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.common.base.BaseApplication;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.string.RegexUtils;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.lib.log.Loger;
import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController;
import com.xueersi.parentsmeeting.modules.livevideo.OtherModulesEnter;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.activity.item.CommonWordItem;
import com.xueersi.parentsmeeting.modules.livevideo.business.BaseLiveMessagePager;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.business.irc.jibble.pircbot.User;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.FlowerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.message.LiveIRCMessageBll;
import com.xueersi.parentsmeeting.modules.livevideo.message.business.LiveMessageEmojiParser;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveHalfBodyMediaControllerBottom;
import com.xueersi.ui.adapter.AdapterItemInterface;
import com.xueersi.ui.adapter.CommonAdapter;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.xutils.view.annotation.ContentView;

import java.util.ArrayList;

import cn.dreamtobe.kpswitch.util.KPSwitchConflictUtil;
import cn.dreamtobe.kpswitch.util.KeyboardUtil;
import cn.dreamtobe.kpswitch.widget.KPSwitchFSPanelLinearLayout;

/**
 * 半身直播 聊天区域
 *
 * @author chenkun
 * @version 1.0, 2018/10/23 下午4:09
 */

public class HalfBodyLiveMessagePager extends BaseLiveMessagePager {

    private static String TAG = "HalfBodyLiveMessagePager";
    /**
     * 聊天，默认开启
     */
    private Button btMesOpen;
    /**
     * 聊天常用语
     */
    private Button btMsgCommon;


    /**
     * 聊天输入框的关闭按钮
     */
    private ImageView ivMessageClose;

    /**
     * 聊天人数
     */
    private TextView tvOnliveNum;
    /**
     * 聊天IRC一下状态，正在连接，在线等
     */
    private ImageView ivMessageOnline;
    /**
     * 聊天消息
     */
    private ListView lvMessage;
    private View rlInfo;
    private View rlMessageContent;
    private Button btMessageSend;
    private Button btMessageExpress;
    /**聊天消息适配器*/
    private CommonAdapter<LiveMessageEntity> messageAdapter;
    private CommonAdapter<LiveMessageEntity> otherMessageAdapter;
    private boolean isTouch = false;
    /**
     * 聊天字体大小，最多13个汉字
     */
    private int messageSize = 0;
    /**
     * 献花
     */
    private PopupWindow mFlowerWindow;
    private String goldNum;
    /**
     * 上次发送消息时间
     */
    private long lastSendMsg;
    private BaseLiveMediaControllerBottom liveMediaControllerBottom;
    private KPSwitchFSPanelLinearLayout switchFSPanelLinearLayout;

    /**表情面板 关闭按钮*/
    private ImageView ivExpressionCancle;
    private Activity liveVideoActivity;
    private KeyboardUtil.OnKeyboardShowingListener keyboardShowingListener;
    /**
     * 竖屏的时候，也添加横屏的消息
     */
    private ArrayList<LiveMessageEntity> otherLiveMessageEntities;
    LiveAndBackDebug liveAndBackDebug;
    private String liveId;
    private String termId;
    private View mFloatView;
    private long mOldTime = 0;
    private View liveMessageContent;
    /**热词*/
    ListView lvCommonWord;
    private PopupWindow mCommonWordWindow;


    public HalfBodyLiveMessagePager(Context context, KeyboardUtil.OnKeyboardShowingListener keyboardShowingListener,
                                    LiveAndBackDebug ums, BaseLiveMediaControllerBottom
                                            liveMediaControllerBottom, ArrayList<LiveMessageEntity>
                                            liveMessageEntities, ArrayList<LiveMessageEntity>
                                            otherLiveMessageEntities) {
        super(context);
        liveVideoActivity = (Activity) context;
        this.liveMediaControllerBottom = liveMediaControllerBottom;
        this.keyboardShowingListener = keyboardShowingListener;
        this.liveAndBackDebug = ums;
        this.liveMessageEntities = liveMessageEntities;
        this.otherLiveMessageEntities = otherLiveMessageEntities;
        Resources resources = context.getResources();
        nameColors[0] = resources.getColor(R.color.COLOR_32B16C);
        nameColors[1] = resources.getColor(R.color.COLOR_E74C3C);
        nameColors[2] = resources.getColor(R.color.COLOR_20ABFF);

        btMesOpen = liveMediaControllerBottom.getBtMesOpen();
        btMsgCommon = liveMediaControllerBottom.getBtMsgCommon();

        if(liveMediaControllerBottom instanceof LiveHalfBodyMediaControllerBottom){
            ((LiveHalfBodyMediaControllerBottom)liveMediaControllerBottom).setControllerStateListener
                    (new LiveHalfBodyMediaControllerBottom.ControllerStateListener(){

                @Override
                public void onSHow() {

                }

                @Override
                public void onHide() {
                    Log.e(TAG,"======> bottomMediaController hide");
                    if(mCommonWordWindow != null){
                        mCommonWordWindow.dismiss();
                    }
                }
            });
        }


        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                initListener();
                initData();
            }
        });
        setVideoLayout(LiveVideoPoint.getInstance());
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.page_livevideo_message_halfbody, null);
        tvOnliveNum = (TextView) mView.findViewById(R.id.tv_livevideo_message_count);
        ivMessageOnline = (ImageView) mView.findViewById(R.id.iv_livevideo_message_online);
        lvMessage = (ListView) mView.findViewById(R.id.lv_livevideo_message);
        dvMessageDanmaku = mView.findViewById(R.id.dv_livevideo_message_danmaku);
        rlInfo = mView.findViewById(R.id.rl_livevideo_info);
        rlMessageContent = mView.findViewById(R.id.rl_livevideo_message_content2);
        etMessageContent = (EditText) mView.findViewById(R.id.et_livevideo_message_content);
        btMessageExpress = (Button) mView.findViewById(R.id.bt_livevideo_message_express);
        btMessageSend = (Button) mView.findViewById(R.id.bt_livevideo_message_send);
        switchFSPanelLinearLayout = (KPSwitchFSPanelLinearLayout) mView.findViewById(R.id
                .rl_livevideo_message_panelroot);
        ivExpressionCancle = (ImageView) mView.findViewById(R.id.iv_livevideo_message_expression_cancle);
        ivMessageClose = mView.findViewById(R.id.iv_livevideo_message_close);
        liveMessageContent = mView.findViewById(R.id.rl_livevideo_halfbody_msgcontent);

        return mView;
    }

    @Override
    public void initListener() {


        // 视频底部控制栏 发言按钮
        btMesOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liveMediaControllerBottom.onChildViewClick(v);
                rlMessageContent.setVisibility(View.VISIBLE);
                KPSwitchConflictUtil.showKeyboard(switchFSPanelLinearLayout, etMessageContent);
            }
        });



        //聊天输入框
        etMessageContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean isSend = (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() ==
                        KeyEvent
                                .KEYCODE_ENTER));
                if (isSend) {
                    btMessageSend.performClick();
                    return true;
                }
                return false;
            }
        });


        //发送按钮
        btMessageSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logger.i("onClick:time=" + (System.currentTimeMillis() - lastSendMsg));
                Editable editable = etMessageContent.getText();
                String msg = editable.toString();
                if (!StringUtils.isSpace(msg)) {
                    if (getInfo != null && getInfo.getBlockChinese() && isChinese(msg)) {
                        addMessage(SYSTEM_TIP, LiveMessageEntity.MESSAGE_TIP, MESSAGE_CHINESE, "");
                        onTitleShow(true);
                        return;
                    }
                    boolean isSend = false;
                    if (ircState.openchat()) {
                        if (System.currentTimeMillis() - lastSendMsg > SEND_MSG_INTERVAL) {
                            boolean send = ircState.sendMessage(msg, getInfo.getStandLiveName());
                            if (send) {
                                etMessageContent.setText("");
                                addMessage("我", LiveMessageEntity.MESSAGE_MINE, msg, getInfo.getHeadImgPath());
                                lastSendMsg = System.currentTimeMillis();
                                onTitleShow(true);
                                isSend = true;
                            } else {
                                addMessage("提示", LiveMessageEntity.MESSAGE_TIP, "你被老师禁言了，请联系老师解除禁言！", "");
                            }
                        } else {
                            //暂时去掉3秒发言，信息提示
                            long timeDelay = (SEND_MSG_INTERVAL - System.currentTimeMillis() + lastSendMsg) / 1000;
                            timeDelay = timeDelay <= 0 ? 1 : timeDelay;
                            XESToastUtils.showToast(mContext, timeDelay + "秒后才能再次发言，要认真听课哦!");
                        }
                    } else {
                        XESToastUtils.showToast(mContext, "老师未开启聊天");
                    }
                } else {
                    addMessage(SYSTEM_TIP, LiveMessageEntity.MESSAGE_TIP, MESSAGE_EMPTY, "");
                }
            }
        });


        // 表情面板/聊天面板切换 设置
        mView.postDelayed(new Runnable() {
            @Override
            public void run() {
                KeyboardUtil.attach((Activity) mContext, switchFSPanelLinearLayout, new KeyboardUtil
                        .OnKeyboardShowingListener() {
                    @Override
                    public void onKeyboardShowing(boolean isShowing) {
                        Log.e(TAG, "onKeyboardShowing:isShowing=" + isShowing);
                        if (!isShowing && switchFSPanelLinearLayout.getVisibility() == View.GONE) {
                            onTitleShow(true);
                        }
                        keyboardShowing = isShowing;
                        keyboardShowingListener.onKeyboardShowing(isShowing);
                        if (keyboardShowing) {
                            btMessageExpress.setBackgroundResource(R.drawable.selector_live_stand_chat_expression);
                        }
                    }
                });
                KPSwitchConflictUtil.attach(switchFSPanelLinearLayout, btMessageExpress, etMessageContent,
                        new KPSwitchConflictUtil.SwitchClickListener() {
                            @Override
                            public void onClickSwitch(boolean switchToPanel) {
                                if (switchToPanel) {
                                    btMessageExpress.setBackgroundResource(R.drawable.selector_live_stand_chat_input);
                                    etMessageContent.clearFocus();
                                } else {
                                    btMessageExpress.setBackgroundResource(R.drawable
                                            .selector_live_stand_chat_expression);
                                    etMessageContent.requestFocus();
                                }
                            }
                        });
            }
        }, 10);


        ivExpressionCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int action = KeyEvent.ACTION_DOWN;
                int code = KeyEvent.KEYCODE_DEL;
                KeyEvent event = new KeyEvent(action, code);
                etMessageContent.onKeyDown(KeyEvent.KEYCODE_DEL, event);
            }
        });



        // 底部控制栏中的热词按钮 点击事件

        btMsgCommon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                LiveMediaController controller = liveMediaControllerBottom.getController();
                controller.show();

               if(mCommonWordWindow.isShowing()){
                   mCommonWordWindow.dismiss();
                   return;
               }

                int[] location = new int[2];
                btMsgCommon.getLocationInWindow(location);
                int offX = location[0] - (mCommonWordWindow.getContentView().getMeasuredWidth()-btMsgCommon.getMeasuredWidth()) / 2;
                int offY = location[1] -  mCommonWordWindow.getContentView().getMeasuredHeight();
                mCommonWordWindow.showAtLocation(btMsgCommon,Gravity.NO_GRAVITY,offX,offY);

            }
        });
    }





    @Override
    public void initData() {
        super.initData();
        // 显示 表情面板
        showExpressionView(true);

        initMsgListView();

        initCommonWord();


    }

    /**
     * 初始化 联通信息
     */
    private void initMsgListView() {
        int screenWidth = ScreenUtils.getScreenWidth();
        int wradio = (int) (LiveVideoConfig.VIDEO_HEAD_WIDTH * screenWidth / LiveVideoConfig.VIDEO_WIDTH);
        int minisize = wradio / 13;
        messageSize = Math.max((int) (ScreenUtils.getScreenDensity() * 12), minisize);
        Log.e(TAG, "initMsgListView:minisize=" + minisize);

        messageAdapter = new CommonAdapter<LiveMessageEntity>(liveMessageEntities) {
            @Override
            public AdapterItemInterface<LiveMessageEntity> getItemView(Object type) {
                return new AdapterItemInterface<LiveMessageEntity>() {
                    TextView tvMessageItem;

                    @Override
                    public int getLayoutResId() {
                        return R.layout.item_livehalfbody_message;
                    }

                    @Override
                    public void initViews(View root) {
                        tvMessageItem = (TextView) root.findViewById(R.id.tv_livevideo_message_item);
                        tvMessageItem.setTextSize(TypedValue.COMPLEX_UNIT_PX, messageSize);
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
                            tvMessageItem.setAutoLinkMask(Linkify.WEB_URLS);
                            tvMessageItem.setText(entity.getText());
                            urlClick(tvMessageItem);
                            CharSequence text = tvMessageItem.getText();
                            tvMessageItem.setText(spanttt);
                            tvMessageItem.append(text);
                        } else {
                            tvMessageItem.setAutoLinkMask(0);
                            tvMessageItem.setText(spanttt);
                            tvMessageItem.append(entity.getText());
                        }
                    }
                };
            }
        };

        lvMessage.setAdapter(messageAdapter);
    }

    @Override
    public void setGetInfo(LiveGetInfo getInfo) {
        super.setGetInfo(getInfo);
        if (getInfo != null) {
            String educationStage = getInfo.getEducationStage();
            liveThreadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    OtherModulesEnter.requestGoldTotal(mContext);
                }
            });
        }
    }



    /**初始化 热词*/
    private void initCommonWord() {

        final ArrayList<String> words = new ArrayList<>();
        words.add("[e]em_1[e]");
        words.add("[e]em_11[e]");
        words.add("[e]em_16[e]");
        words.add("666");
        words.add("2");
        words.add("1");

        View contentView = View.inflate(mContext,R.layout.layout_live_commonwrod_popwindow,null);
        mCommonWordWindow = new PopupWindow(contentView
                ,ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,false);

        mCommonWordWindow.setOutsideTouchable(true);
        mCommonWordWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        lvCommonWord  = contentView.findViewById(R.id.lv_livevideo_halfbody_common_word);
        lvCommonWord.setAdapter(new CommonAdapter<String>(words) {
            @Override
            public AdapterItemInterface<String> getItemView(Object type) {
                return new CommonWordItem(mContext, this);
            }
        });


        lvCommonWord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String msg = words.get(position);
                if (ircState.openchat()) {
                    if (System.currentTimeMillis() - lastSendMsg > SEND_MSG_INTERVAL) {
                        boolean send = ircState.sendMessage(msg, "");
                        if (send) {
                            etMessageContent.setText("");
                            addMessage("我", LiveMessageEntity.MESSAGE_MINE, msg, "");
                            lastSendMsg = System.currentTimeMillis();
                            onTitleShow(true);
                            mCommonWordWindow.dismiss();
                        } else {
                            XESToastUtils.showToast(mContext, "你已被禁言!");
                        }
                    } else {
                        //暂时去掉3秒发言，信息提示
                        long timeDelay = (SEND_MSG_INTERVAL - System.currentTimeMillis() + lastSendMsg) / 1000;
                        timeDelay = timeDelay <= 0 ? 1 : timeDelay;
                        XESToastUtils.showToast(mContext, timeDelay + "秒后才能再次发言，要认真听课哦!");
                    }
                } else {
                    XESToastUtils.showToast(mContext, "老师未开启聊天");
                }
            }
        });
        //提前测量 一次尺寸信息，用于 popWindow 显示定位
        contentView.measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED);
    }

    @Override
    protected SpannableStringBuilder createSpannable(int ftype, String name, Drawable drawable) {
        return super.createSpannable(ftype, name, drawable);
    }


    /**
     * 文科，当老师开启了送花或者送礼物开关后，点击赠送按钮之后的逻辑处理
     *
     * @param entity
     */
    private void logicForOpenbarrage(final FlowerEntity entity) {
        if (ircState.isOpenbarrage()) {
            String educationStage = getInfo.getEducationStage();
            ircState.praiseTeacher(ircState.getMode(), entity.getFtype() + "", educationStage, new HttpCallBack(false) {
                @Override
                public void onPmSuccess(ResponseEntity responseEntity) {
                    if (goldNum == null) {
                        OtherModulesEnter.requestGoldTotal(mContext);
                    } else {
                        if (responseEntity.getJsonObject() instanceof JSONObject) {
                            try {
                                JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                                int gold = Integer.parseInt(goldNum);
                                goldNum = ("" + (gold - jsonObject.getInt("gold")));
                                if (Integer.parseInt(goldNum) <= 0) {
                                    XESToastUtils.showToast(mContext, "您的金币不足啦");
                                    return;
                                }
                                onGetMyGoldDataEvent(goldNum);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    addDanmaKuFlowers(entity.getFtype(), getInfo.getStuName());
                    mView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mFlowerWindow.dismiss();
                        }
                    }, 1000);
                }

                @Override
                public void onPmFailure(Throwable error, String msg) {
                    mFlowerWindow.dismiss();
                }

                @Override
                public void onPmError(ResponseEntity responseEntity) {
                    mFlowerWindow.dismiss();
                }
            });
        } else {

        }
    }

    /**
     * 理科，当老师开启了送花或者送礼物开关后，点击赠送按钮之后的逻辑处理
     *
     * @param entity
     * @param formWhichTeacher
     */
    private void logicForOpenbarrageLike(final FlowerEntity entity, String formWhichTeacher) {
        //主讲或者辅导有任一个开启了献花
        if (ircState.isOpenZJLKbarrage() || ircState.isOpenFDLKbarrage()) {
            String educationStage = getInfo.getEducationStage();
            ircState.praiseTeacher(formWhichTeacher, entity.getFtype() + "", educationStage, new HttpCallBack(false) {
                @Override
                public void onPmSuccess(ResponseEntity responseEntity) {
                    if (goldNum == null) {
                        OtherModulesEnter.requestGoldTotal(mContext);
                    } else {
                        if (responseEntity.getJsonObject() instanceof JSONObject) {
                            try {
                                JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                                int gold = Integer.parseInt(goldNum);
                                goldNum = ("" + (gold - jsonObject.getInt("gold")));
                                if (Integer.parseInt(goldNum) <= 0) {
                                    XESToastUtils.showToast(mContext, "您的金币不足啦");
                                    return;
                                }
                                onGetMyGoldDataEvent(goldNum);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    addDanmaKuFlowers(entity.getFtype(), getInfo.getStuName());
                    mView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mFlowerWindow.dismiss();
                        }
                    }, 1000);
                }

                @Override
                public void onPmFailure(Throwable error, String msg) {
                    mFlowerWindow.dismiss();
                }

                @Override
                public void onPmError(ResponseEntity responseEntity) {
                    mFlowerWindow.dismiss();
                }
            });
        } else {

        }
    }

    /**
     * 文科点击赠送按钮的逻辑
     *
     * @param entity
     */
    private void logicForChOnClickSendFlowerBt(final FlowerEntity entity) {
        if (LiveTopic.MODE_CLASS.equals(ircState.getMode())) {
            logicForOpenbarrage(entity);
        } else {
        }
    }

    @Override
    public void onTitleShow(boolean show) {

        Log.e(TAG,"======>onTitleShow:"+show);
        btMessageExpress.setBackgroundResource(R.drawable.selector_live_stand_chat_expression);
        if (!keyboardShowing && switchFSPanelLinearLayout.getVisibility() != View.GONE) {
            switchFSPanelLinearLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    switchFSPanelLinearLayout.setVisibility(View.GONE);
                }
            }, 10);
        }

        if (rlMessageContent.getVisibility() != View.GONE) {
            InputMethodManager mInputMethodManager = (InputMethodManager) mContext.getSystemService(Context
                    .INPUT_METHOD_SERVICE);
            mInputMethodManager.hideSoftInputFromWindow(etMessageContent.getWindowToken(), 0);
            rlMessageContent.setVisibility(View.GONE);
        }

    }

    @Override
    public void closeChat(final boolean close) {

    }

    @Override
    public boolean isCloseChat() {

        return false;
    }


    @Override
    public void setVideoWidthAndHeight(int width, int height) {
        final View contentView = liveVideoActivity.findViewById(android.R.id.content);
        final View actionBarOverlayLayout = (View) contentView.getParent();
        Rect r = new Rect();
        actionBarOverlayLayout.getWindowVisibleDisplayFrame(r);
        int screenWidth = (r.right - r.left);
        int screenHeight = ScreenUtils.getScreenHeight();
        if (width > 0) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rlInfo.getLayoutParams();
            int wradio = (int) (LiveVideoConfig.VIDEO_HEAD_WIDTH * width / LiveVideoConfig.VIDEO_WIDTH);
            int videoGap = (screenWidth - width) / 2;
            if (videoGap != params.leftMargin) {
                params.leftMargin = videoGap;
                LayoutParamsUtil.setViewLayoutParams(rlInfo, params);
            }
            params = (RelativeLayout.LayoutParams) btMesOpen.getLayoutParams();
            if (params.rightMargin != videoGap) {
                params.rightMargin = videoGap;
                LayoutParamsUtil.setViewLayoutParams(btMesOpen, params);
            }
        }
    }

    /**
     * 聊天开始连接
     */
    @Override
    public void onStartConnect() {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                ivMessageOnline.setImageResource(R.drawable.bg_livevideo_message_offline);
            }
        });
    }

    @Override
    public void setIsRegister(boolean isRegister) {
        super.setIsRegister(isRegister);
        if (isRegister) {
            ivMessageOnline.setImageResource(R.drawable.bg_livevideo_message_online);
        } else {
            ivMessageOnline.setImageResource(R.drawable.bg_livevideo_message_offline);
        }
    }

    @Override
    public void setHaveFlowers(boolean haveFlowers) {
        super.setHaveFlowers(haveFlowers);
    }

    /**
     * 聊天连上
     */
    @Override
    public void onConnect() {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                addMessage(SYSTEM_TIP, LiveMessageEntity.MESSAGE_TIP, CONNECT, "");
                if (!isRegister) {
                    ivMessageOnline.setImageResource(R.drawable.bg_livevideo_message_offline);
                }
            }
        });
    }

    // 03.16 设置模拟的聊天连接
    public void onConnects() {
        addMessage(SYSTEM_TIP, LiveMessageEntity.MESSAGE_TIP, CONNECT, "");
        ivMessageOnline.setImageResource(R.drawable.bg_livevideo_message_online);
    }

    /**
     * 聊天进入房间
     */
    @Override
    public void onRegister() {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                isRegister = true;
                ivMessageOnline.setImageResource(R.drawable.bg_livevideo_message_online);
            }
        });
    }

    /**
     * 聊天断开
     */
    @Override
    public void onDisconnect() {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                isRegister = false;
                addMessage(SYSTEM_TIP, LiveMessageEntity.MESSAGE_TIP, DISCONNECT, "");
                ivMessageOnline.setImageResource(R.drawable.bg_livevideo_message_offline);
            }
        });
    }

    @Override
    public void onUserList(String channel, final User[] users) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (ircState.isSeniorOfHighSchool()) {
                    tvOnliveNum.setText("班内" + peopleCount + "人");
                } else {
                    if (ircState.isHaveTeam()) {
                        tvOnliveNum.setText("组内" + peopleCount + "人");
                    } else {
                        tvOnliveNum.setText(peopleCount + "人正在上课");
                    }
                }
            }
        });
    }

    @Override
    public void onMessage(String target, String sender, String login, String hostname, String text, String headurl) {
        Loger.e("LiveMessagerPager", "=====>onMessage called");
        if (sender.startsWith(LiveIRCMessageBll.TEACHER_PREFIX)) {
            sender = "主讲老师";
        } else if (sender.startsWith(LiveIRCMessageBll.COUNTTEACHER_PREFIX)) {
            sender = "辅导老师";
        }
        addMessage(sender, LiveMessageEntity.MESSAGE_TEACHER, text, headurl);
    }

    @Override
    public void onPrivateMessage(boolean isSelf, final String sender, String login, String hostname, String target,
                                 final String message) {
        if (isCloseChat()) {
            return;
        }
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject(message);
                    int type = jsonObject.getInt("type");
                    if (type == XESCODE.TEACHER_MESSAGE) {
                        addMessage(jsonObject.getString("name"), LiveMessageEntity.MESSAGE_CLASS, jsonObject
                                .getString("msg"), "");
                    } else if (type == XESCODE.FLOWERS) {
                        //{"ftype":2,"name":"林玉强","type":"110"}
                        addDanmaKuFlowers(jsonObject.getInt("ftype"), jsonObject.getString("name"));
                    }
                } catch (JSONException e) {
                    addMessage(sender, LiveMessageEntity.MESSAGE_CLASS, message, "");
                }
            }
        });
    }

    @Override
    public void onJoin(String target, String sender, String login, String hostname) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (ircState.isSeniorOfHighSchool()) {
                    tvOnliveNum.setText("班内" + peopleCount + "人");
                } else {
                    if (ircState.isHaveTeam()) {
                        tvOnliveNum.setText("组内" + peopleCount + "人");
                    } else {
                        tvOnliveNum.setText(peopleCount + "人正在上课");
                    }
                }
            }
        });
    }

    @Override
    public void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (ircState.isSeniorOfHighSchool()) {
                    tvOnliveNum.setText("班内" + peopleCount + "人");
                } else {
                    if (ircState.isHaveTeam()) {
                        tvOnliveNum.setText("组内" + peopleCount + "人");
                    } else {
                        tvOnliveNum.setText(peopleCount + "人正在上课");
                    }
                }
            }
        });
    }

    @Override
    public void onKick(String target, String kickerNick, String kickerLogin, String kickerHostname, String
            recipientNick, String reason) {

    }

    public void setLiveTermId(String liveId, String termId) {
        this.liveId = liveId;
        this.termId = termId;
    }

    /**
     * 被禁言
     */
    @Override
    public void onDisable(final boolean disable, final boolean fromNotice) {

        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (disable) {
                    if (fromNotice) {
                        XESToastUtils.showToast(mContext, "你被老师禁言了");
                    }
                    btMesOpen.setAlpha(0.4f);
                    btMesOpen.setBackgroundResource(R.drawable.bg_livevideo_message_open);
                } else {
                    if (fromNotice) {
                        XESToastUtils.showToast(mContext, "老师解除了你的禁言");
                    }
                    if (ircState.openchat()) {
                        btMesOpen.setAlpha(1.0f);
                        btMesOpen.setBackgroundResource(R.drawable.bg_livevideo_message_open);
                    } else {
                        btMesOpen.setAlpha(0.4f);
                        btMesOpen.setBackgroundResource(R.drawable.bg_livevideo_message_open);
                    }
                }
            }
        });

    }

    /**
     * 关闭开启聊天
     */
    @Override
    public void onopenchat(final boolean openchat, final String mode, final boolean fromNotice) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (ircState.isDisable()) {
                    btMesOpen.setAlpha(0.4f);
                    btMesOpen.setBackgroundResource(R.drawable.bg_livevideo_message_open);
                } else {
                    if (openchat) {
                        btMesOpen.setAlpha(1.0f);
                        btMesOpen.setBackgroundResource(R.drawable.bg_livevideo_message_open);
                    } else {
                        btMesOpen.setAlpha(0.4f);
                        btMesOpen.setBackgroundResource(R.drawable.bg_livevideo_message_open);
                    }
                    if (fromNotice) {
                        if (LiveTopic.MODE_CLASS.equals(mode)) {
                            XESToastUtils.showToast(mContext, "主讲老师" + (openchat ? "打开" : "关闭") + "了聊天区");
                        } else {
                            XESToastUtils.showToast(mContext, "辅导老师" + (openchat ? "打开" : "关闭") + "了聊天区");
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onModeChange(final String mode) {
    }

    /**
     * 关闭开启弹幕
     */
    @Override
    public void onOpenbarrage(final boolean openbarrage, final boolean fromNotice) {
        Loger.i("yzl_fd", ircState.getMode() + "老师" + openbarrage + "了献花 fromNotice = " + fromNotice + " liveBll" +
                ".getLKNoticeMode()" + ircState.getLKNoticeMode());


    }

    @Override
    public void onFDOpenbarrage(boolean open, boolean b) {

    }


    /**
     * 理科，主讲和辅导切换的时候，给出提示（切流）
     *
     * @param oldMode
     * @param newMode
     * @param isShowNoticeTips  为false的时候，默认显示"已切换到 主讲/辅导模式"
     * @param iszjlkOpenbarrage
     * @param isFDLKOpenbarrage
     */
    @Override
    public void onTeacherModeChange(String oldMode, final String newMode, boolean isShowNoticeTips, final boolean
            iszjlkOpenbarrage, final boolean isFDLKOpenbarrage) {
        //理科辅导送礼物功能
        Loger.i("yzl_fd", "onTeacherModeChange 切流，使送礼物面板消失");
        if (LiveTopic.MODE_CLASS.equals(oldMode) && iszjlkOpenbarrage) {
            //主讲老师是开启状态，切辅导，提醒“已切换到主讲/辅导”
            Loger.i("yzl_fd", "主讲老师是开启状态，切辅导，提醒“已切换到" + newMode);
            return;
        }
        if (LiveTopic.MODE_TRANING.equals(oldMode) && isFDLKOpenbarrage) {
            //辅导老师是开启状态，切主讲，提醒“已切换到主讲/辅导”
            Loger.i("yzl_fd", "主讲老师是开启状态，切辅导，提醒“已切换到" + newMode);
            return;
        }

        if (LiveTopic.MODE_CLASS.equals(oldMode) && !iszjlkOpenbarrage) {
            //主讲老师是关闭状态，切辅导
            if (isFDLKOpenbarrage) {
                //如果辅导是开启，提醒：“辅导老师开启了礼物功能”；如果辅导是关闭，不做提醒
                Loger.i("yzl_fd", "如果辅导是开启，提醒：“辅导老师开启了礼物功能”；如果辅导是关闭，不做提醒newMode =" + newMode + " isFDLKOpenbarrage = " +
                        "" + isFDLKOpenbarrage);

            }
            return;
        }
        if (LiveTopic.MODE_TRANING.equals(oldMode) && !isFDLKOpenbarrage) {
            //辅导老师是关闭状态，切主讲
            if (iszjlkOpenbarrage) {
                //如果主讲是开启，提醒：“主讲老师开启了礼物功能”；如果主讲是关闭，不做提醒
                Loger.i("yzl_fd", "如果主讲是开启，提醒：“主讲老师开启了礼物功能”；如果主讲是关闭，不做提醒newMode =" + newMode + " iszjlkOpenbarrage = " +
                        "" + iszjlkOpenbarrage);
            }
            return;
        }
    }


    /*添加聊天信息，超过120，移除60个*/
    @Override
    public void addMessage(final String sender, final int type, final String text, final String headUrl) {
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
                            Loger.e(BaseApplication.getContext(), TAG, "" + mContext + "," + sender + "," + type, e,
                                    true);
                        }
                        if (!isTouch) {
                            lvMessage.setSelection(lvMessage.getCount() - 1);
                        }
                    }
                });
            }
        });
        Log.e(TAG, "sender:" + sender);

    }

    @Override
    public CommonAdapter<LiveMessageEntity> getMessageAdapter() {
        return messageAdapter;
    }

    @Override
    public void setOtherMessageAdapter(CommonAdapter<LiveMessageEntity> otherMessageAdapter) {
        this.otherMessageAdapter = otherMessageAdapter;
    }

    @Override
    public void onGetMyGoldDataEvent(String goldNum) {
        this.goldNum = goldNum;
    }


    public void showPeopleCount(int num) {
        tvOnliveNum.setText(num + "人正在上课");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mCommonWordWindow != null){
            mCommonWordWindow.dismiss();
        }

    }
}
