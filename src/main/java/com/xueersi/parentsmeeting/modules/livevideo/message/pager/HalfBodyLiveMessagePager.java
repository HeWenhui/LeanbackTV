package com.xueersi.parentsmeeting.modules.livevideo.message.pager;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.text.util.Linkify;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController;
import com.xueersi.parentsmeeting.modules.livevideo.OtherModulesEnter;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.adapter.HalfBodyHotWordAdapter;
import com.xueersi.parentsmeeting.modules.livevideo.adapter.HalfBodyHotWordHolder;
import com.xueersi.parentsmeeting.modules.livevideo.business.BaseLiveMessagePager;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.User;
import com.xueersi.parentsmeeting.modules.livevideo.message.LiveIRCMessageBll;
import com.xueersi.parentsmeeting.modules.livevideo.message.business.LiveMessageEmojiParser;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerTop;
import com.xueersi.parentsmeeting.modules.livevideo.widget.CenterAlignImageSpan;
import com.xueersi.parentsmeeting.modules.livevideo.widget.HalfBodyLiveMediaCtrlTop;
import com.xueersi.parentsmeeting.modules.livevideo.widget.HalfBodyLiveMsgRecycelView;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveHalfBodyMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveTouchEventLayout;
import com.xueersi.parentsmeeting.modules.livevideo.business.ContextLiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.HotWordLog;
import com.xueersi.ui.adapter.CommonAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.dreamtobe.kpswitch.util.KPSwitchConflictUtil;
import cn.dreamtobe.kpswitch.util.KeyboardUtil;
import cn.dreamtobe.kpswitch.widget.KPSwitchFSPanelLinearLayout;

/**
 * 理科半身直播2.0 聊天区域
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
     * 聊天输入框的关闭按钮
     */
    private ImageView ivMessageClose;

    /**
     * 聊天消息
     */
    private View rlInfo;
    private View rlMessageContent;
    private Button btMessageSend;
    private Button btMessageExpress;
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
    private BaseLiveMediaControllerTop liveMediaControllerTop;

    private KPSwitchFSPanelLinearLayout switchFSPanelLinearLayout;

    /**
     * 聊天消息数据源    因为涉及到清空 数据源问题 所以单独有集合管理数据
     */
    private ArrayList<LiveMessageEntity> mLiveMsgList = new ArrayList<>();

    /**
     * 表情面板 关闭按钮
     */
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


    private int mPopWinOffX;
    private int mPopWinOffY;

    /**
     * 聊天消息
     */
    private HalfBodyLiveMsgRecycelView liveMsgReclView;
    private LiveMsgAdapter mMsgAdapter;
    /**
     * 辅导模式下最后一条消息
     */
    private LiveMessageEntity mLastMsg;
    private LiveTouchEventLayout bottomCtrContainer;

    /**
     * 聊天状态控制按钮
     **/
    private Button btMsgState;

    /**
     * 热词列表
     **/
    private RecyclerView rclHotWord;


    /**
     * 所有聊天消息
     */
    private static final int CHAT_SATE_ALL = 1;
    /**
     * 关闭 状态
     */
    private static final int CHAT_SATE_CLOSE = 2;

    /**
     * 只看老师消息
     */
    private static final int CHAT_SATE_TEACHER = 3;

    /**
     * 当前聊天状态
     */
    private int mChatState = CHAT_SATE_ALL;

    /**
     * 是否只看老师消息
     */
    boolean isCloseChat;


    /**
     * 热词资源集
     */
    private int[] mHotWordRes = {
            R.drawable.selector_live_halfbody_hotword_sml,
            R.drawable.selector_live_halfbody_hotword_1,
            R.drawable.selector_live_halfbody_hotword_666,
            R.drawable.selector_live_halfbody_hotword_2,
            R.drawable.selector_live_halfbody_hotword_ok,
            R.drawable.selector_live_halfbody_hotword_cry,
    };

    /**
     * 热词消息指令
     **/
    private String[] mHotwordCmd = {"[e]em_1[e]", "1", "666", "2", "[e]em_16[e]", "[e]em_11[e]"};


    public HalfBodyLiveMessagePager(Context context, KeyboardUtil.OnKeyboardShowingListener keyboardShowingListener,
                                    LiveAndBackDebug ums, BaseLiveMediaControllerBottom
                                            liveMediaControllerBottom,BaseLiveMediaControllerTop controllerTop,
                                    ArrayList<LiveMessageEntity>
                                            liveMessageEntities, ArrayList<LiveMessageEntity>
                                            otherLiveMessageEntities) {
        super(context);
        liveVideoActivity = (Activity) context;
        this.liveMediaControllerBottom = liveMediaControllerBottom;
        this.liveMediaControllerTop = controllerTop;
        this.keyboardShowingListener = keyboardShowingListener;
        this.liveAndBackDebug =  new ContextLiveAndBackDebug(context);
        this.liveMessageEntities = liveMessageEntities;
        this.otherLiveMessageEntities = otherLiveMessageEntities;

        if (liveMessageEntities != null && liveMessageEntities.size() > 0) {
            mLiveMsgList.addAll(liveMessageEntities);
        }

        initBottomControllBtn();


        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                initListener();
                initData();
            }
        });
    }

    private void initBottomControllBtn() {
        btMesOpen = liveMediaControllerBottom.findViewById(R.id.bt_livevideo_message_open);
        btMesOpen.setBackgroundResource(getMsgBtnResId());

        btMsgState = liveMediaControllerBottom.findViewById(R.id.btn_livevideo_halbody_msg_state);
        bottomCtrContainer = liveMediaControllerBottom.findViewById(R.id.ll_livevideo_bottom_controller);
        rclHotWord = liveMediaControllerBottom.findViewById(R.id.rl_livevideo_halbody_hotword);
        initCommonWord();
    }


    /**
     * 获取聊天按钮 资源图片
     *
     * @return
     */
    protected int getMsgBtnResId() {
        return R.drawable.selector_live_halfbody_msg_open;
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, getLayoutId(), null);
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

        liveMsgReclView = mView.findViewById(R.id.rcl_live_halfbody_msg);
        // 从底部添加
        liveMsgReclView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, true));
        return mView;
    }

    /**
     * 获取 布局layout
     *
     * @return
     */
    protected int getLayoutId() {
        return R.layout.page_livevideo_message_halfbody;
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
                        if (!isShowing && switchFSPanelLinearLayout.getVisibility() == View.GONE) {
                            onTitleShow(true);
                        }
                        keyboardShowing = isShowing;
                        keyboardShowingListener.onKeyboardShowing(isShowing);
                        if (keyboardShowing) {
                            btMessageExpress.setBackgroundResource(R.drawable.im_input_biaoqing_icon_normal);
                        }
                    }
                });
                KPSwitchConflictUtil.attach(switchFSPanelLinearLayout, btMessageExpress, etMessageContent,
                        new KPSwitchConflictUtil.SwitchClickListener() {
                            @Override
                            public void onClickSwitch(boolean switchToPanel) {
                                if (switchToPanel) {
                                    btMessageExpress.setBackgroundResource(R.drawable.im_input_jianpan_icon_normal);
                                    etMessageContent.clearFocus();
                                } else {
                                    btMessageExpress.setBackgroundResource(R.drawable.im_input_biaoqing_icon_normal);
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

        //默认显示顶部状态栏
        LiveMediaController controller = liveMediaControllerBottom.getController();
        controller.show();

    }

    @Override
    public void initData() {
        super.initData();
        // 显示 表情面板
        showExpressionView(true);
        initMsgRcyclView();
    }

    private class MsgItemHolder extends RecyclerView.ViewHolder {
        private TextView tvMsg;
        /**
         * 展示带图片的消息
         */
        private TextView tvSysMsg;

        public MsgItemHolder(View itemView) {
            super(itemView);
            tvMsg = itemView.findViewById(R.id.tv_live_halfbody_msg);
            tvSysMsg = itemView.findViewById(R.id.tv_live_halfbody_sys_msg);
        }

        public void bindData(LiveMessageEntity data) {
            Drawable drawable = null;
            if (data.getType() == LiveMessageEntity.MESSAGE_MINE) {
                tvMsg.setTextColor(Color.parseColor("#FFDB5C"));
                tvSysMsg.setTextColor(Color.parseColor("#FFDB5C"));
            } else {
                tvMsg.setTextColor(Color.parseColor("#ffffff"));
                tvSysMsg.setTextColor(Color.parseColor("#ffffff"));
            }
            if (LiveMessageEntity.MESSAGE_TIP == data.getType()) {
                drawable = dwSysIcon;
            } else if (LiveMessageEntity.MESSAGE_TEACHER == data.getType()) {
                drawable = dwTeacherIcon;
            }
            if (drawable != null) {
                tvMsg.setVisibility(View.INVISIBLE);
                SpannableStringBuilder ssb = new SpannableStringBuilder("# ");
                drawable.setBounds(0, 0, SizeUtils.Dp2Px(tvMsg.getContext(), 40), SizeUtils.Dp2Px(tvMsg.getContext(),
                        18));
                CenterAlignImageSpan imageSpan = new CenterAlignImageSpan(drawable);
                ssb.setSpan(imageSpan, 0, 1, ImageSpan.ALIGN_BASELINE);
                tvSysMsg.setVisibility(View.VISIBLE);
                if (urlclick == 1 && LiveMessageEntity.MESSAGE_TEACHER == data.getType()) {
                    tvSysMsg.setAutoLinkMask(Linkify.WEB_URLS);
                    tvSysMsg.setText(data.getText());
                    urlClick(tvSysMsg);
                    tvSysMsg.setText(ssb);
                    tvSysMsg.append(data.getText());
                } else {
                    tvSysMsg.setAutoLinkMask(0);
                    tvSysMsg.setText(ssb);
                    tvSysMsg.append(data.getText());
                }
            } else {
                tvSysMsg.setVisibility(View.INVISIBLE);
                tvMsg.setVisibility(View.VISIBLE);
                tvMsg.setText(data.getSender() + "：");
                tvMsg.append(data.getText());
            }
        }
    }


    private class LiveMsgAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<LiveMessageEntity> mData;

        public LiveMsgAdapter(List<LiveMessageEntity> data) {
            mData = data;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MsgItemHolder(View.inflate(parent.getContext(), R.layout.item_livevideo_halfbody_msg, null));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int dataIndex = (mData.size() - 1) - position;
            ((MsgItemHolder) holder).bindData(mData.get(dataIndex));
        }

        @Override
        public int getItemCount() {
            return mData == null ? 0 : mData.size();
        }
    }

    private Drawable dwSysIcon;
    private Drawable dwTeacherIcon;

    /**
     * 初始化 联通信息
     */
    private void initMsgRcyclView() {
        int screenWidth = ScreenUtils.getScreenWidth();
        int wradio = (int) (LiveVideoConfig.VIDEO_HEAD_WIDTH * screenWidth / LiveVideoConfig.VIDEO_WIDTH);
        int minisize = wradio / 13;
        messageSize = Math.max((int) (ScreenUtils.getScreenDensity() * 12), minisize);
        mLastMsg = null;

        if (mLiveMsgList != null && mLiveMsgList.size() > 0) {
            mLastMsg = mLiveMsgList.remove((mLiveMsgList.size() - 1));
        }

        mMsgAdapter = new LiveMsgAdapter(mLiveMsgList);
        liveMsgReclView.setAdapter(mMsgAdapter);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) liveMsgReclView.getLayoutParams();
        Point point = new Point();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getSize(point);
        int screenHeight = Math.min(point.x, point.y);
        int height = (int) (screenHeight * 0.573);
        int width = (int) (screenWidth * 0.45f);
        params.height = height;
        params.width = width;
        params.bottomMargin = (int) (screenHeight * 0.054f);
        liveMsgReclView.setLayoutParams(params);

        liveMsgReclView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int itemPosition = parent.getChildAdapterPosition(view);
                int left = 0;
                int right = 0;
                int top = 0;
                int bottom = 0;
                if (itemPosition < mLiveMsgList.size()) {
                    top = SizeUtils.Dp2Px(mContext, 9);
                }
                outRect.set(left, top, right, bottom);
            }
        });

        //监听 item淡出动画  动画结束后 清空数据源
        liveMsgReclView.setItemFadeAnimListener(new HalfBodyLiveMsgRecycelView.ItemFadeAnimListener() {
            @Override
            public void onAllItemFadeOut() {
                mLiveMsgList.clear();
                mMsgAdapter.notifyDataSetChanged();
            }
        });

        dwSysIcon = mView.getResources().getDrawable(R.drawable.icon_live_sys_msg);
        dwTeacherIcon = mView.getResources().getDrawable(R.drawable.icon_live_teacher_msg);
        initReclItemState();
    }

    /**
     * 初始化 item 初始状态
     */
    private void initReclItemState() {
        if (mLastMsg != null) {
            liveMsgReclView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mLiveMsgList.add(mLastMsg);
                    mMsgAdapter.notifyItemInserted(0);
                }
            }, 100);
        }
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

    /**
     * 初始化 热词
     */
    private void initCommonWord() {

        rclHotWord.setLayoutManager(new LinearLayoutManager(mContext, LinearLayout.HORIZONTAL, false));
        rclHotWord.setAdapter(new HalfBodyHotWordAdapter(mHotWordRes, new HalfBodyHotWordHolder.ItemClickListener() {
            @Override
            public void onItemClick(View view, int postion) {
                sendHotWord(mHotwordCmd[postion]);
            }
        }));

        rclHotWord.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int itemPosition = parent.getChildAdapterPosition(view);
                int left = 0;
                int right = 0;
                int top = 0;
                int bottom = 0;
                if (itemPosition != 0) {
                    left = SizeUtils.Dp2Px(mContext, 15);
                }
                outRect.set(left, top, right, bottom);
            }
        });

        btMsgState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchChatState();
            }
        });


        //事件监听，控制顶部控制栏显示逻辑
        bottomCtrContainer.setDisPatchTouchEventListener(new LiveTouchEventLayout.DispatchTouchEventListener() {
            @Override
            public void onDispatchTouchEvent(MotionEvent ev) {
                switch (ev.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        isMediaCtrShowing = true;
                        interceptBtmMediaHide(true);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        hideBottomMediaCtr(3000);
                        break;
                    default:
                        break;
                }
            }
        });
    }


    /**
     * 切换聊天状态
     */
    private void switchChatState() {
        if (mChatState == CHAT_SATE_ALL) {
            mChatState = CHAT_SATE_TEACHER;
            btMsgState.setBackgroundResource(R.drawable.selector_live_halfbody_msgstate_teacher);
            addMessage(SYSTEM_TIP, LiveMessageEntity.MESSAGE_TIP, "只看老师聊天消息", "");
        } else if (mChatState == CHAT_SATE_TEACHER) {
            mChatState = CHAT_SATE_CLOSE;
            btMsgState.setBackgroundResource(R.drawable.selector_live_halfbody_msgstate_close);
            addMessage(SYSTEM_TIP, LiveMessageEntity.MESSAGE_TIP, "关闭显示聊天信息", "");
            transBottomMediaCtr(2);
        } else if (mChatState == CHAT_SATE_CLOSE) {
            mChatState = CHAT_SATE_ALL;
            btMsgState.setBackgroundResource(R.drawable.selector_live_halfbody_msgstate_open);
            transBottomMediaCtr(1);
            addMessage(SYSTEM_TIP, LiveMessageEntity.MESSAGE_TIP, "显示全部聊天信息", "");
        }
    }

    /**
     * 移动热词输入面板
     * @param direction 1:向左  2:向右
     */
    private void transBottomMediaCtr(int direction) {
        ObjectAnimator animator = null;
        if (direction == 1) {
            animator = ObjectAnimator.ofFloat(bottomCtrContainer, "translationX",
                    bottomCtrContainer.getMeasuredWidth() * 0.7795f, 0);

        } else if (direction == 2) {
            animator = ObjectAnimator.ofFloat(bottomCtrContainer, "translationX", 0,
                    bottomCtrContainer.getMeasuredWidth() * 0.7795f);


        }
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(300);
        animator.start();

    }


    /**
     * 热词埋点日志
     * @param hotwordCmd  热词指令
     */
    private void upLoadHotWordLog(String hotwordCmd) {
        try {
            if(getInfo != null){
                HotWordLog.hotWordSend(this.liveAndBackDebug,hotwordCmd,HotWordLog.LIVETYPE_NOT_PRESHCOOL,
                        getInfo.getStudentLiveInfo().getClassId(),getInfo.getStudentLiveInfo().getTeamId(),getInfo.getStudentLiveInfo().getCourseId());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    /**
     * 发送热词消息
     *
     * @param msg
     */

    private void sendHotWord(String msg) {
        upLoadHotWordLog(msg);
        hideBottomMediaCtr(0);
        if (ircState.openchat()) {
            if (System.currentTimeMillis() - lastSendMsg > SEND_MSG_INTERVAL) {
                boolean send = ircState.sendMessage(msg, "");
                if (send) {
                    etMessageContent.setText("");
                    addMessage("我", LiveMessageEntity.MESSAGE_MINE, msg, "");
                    lastSendMsg = System.currentTimeMillis();
                    onTitleShow(true);
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



    private boolean isMediaCtrShowing = false;

    Runnable hideBtmMediaCtrTask = new Runnable() {
        @Override
        public void run() {
            interceptBtmMediaHide(false);
            isMediaCtrShowing = false;
            if (liveMediaControllerBottom.getController() != null) {
                liveMediaControllerBottom.onHide();
            }

            if(liveMediaControllerTop != null){
                liveMediaControllerTop.onHide();
            }
        }
    };


    /**
     * 关闭媒体控制栏
     * @param timeDelay  延时多久
     */
    private void hideBottomMediaCtr(long timeDelay) {
        mView.removeCallbacks(hideBtmMediaCtrTask);
        mView.postDelayed(hideBtmMediaCtrTask, timeDelay);
    }

    /**
     * @param interCept
     */
    private void interceptBtmMediaHide(boolean interCept) {
        if (liveMediaControllerBottom.getController() != null &&
                liveMediaControllerBottom instanceof LiveHalfBodyMediaControllerBottom) {
            ((LiveHalfBodyMediaControllerBottom) liveMediaControllerBottom).interceptHideBtmMediaCtr(interCept);
        }

        if(liveMediaControllerTop != null && liveMediaControllerTop instanceof HalfBodyLiveMediaCtrlTop){
            ((HalfBodyLiveMediaCtrlTop) liveMediaControllerTop).interceptHideMediaCtr(interCept);
        }
    }


    @Override
    public void onTitleShow(boolean show) {

        if(mediaCtrShowing()){
            hideBottomMediaCtr(0);
        }


        btMessageExpress.setBackgroundResource(R.drawable.im_input_biaoqing_icon_normal);
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

    private boolean mediaCtrShowing() {
        return isMediaCtrShowing;
    }

    @Override
    public void closeChat(final boolean close) {
        mChatState = close?CHAT_SATE_TEACHER:CHAT_SATE_ALL;
        btMsgState.setBackgroundResource(close?R.drawable.selector_live_halfbody_msgstate_teacher
                :R.drawable.selector_live_halfbody_msgstate_open);
    }


    @Override
    public boolean isCloseChat() {

        return mChatState == CHAT_SATE_TEACHER;
    }

    /**
     * 关闭所有消息，除了系统消息
     * @return
     */
    private boolean isCloseAllMsg() {

        return mChatState == CHAT_SATE_CLOSE;
    }


    @Override
    public void setVideoLayout(LiveVideoPoint liveVideoPoint) {
    }

    /**
     * 聊天开始连接
     */
    @Override
    public void onStartConnect() {

    }

    @Override
    public void setIsRegister(boolean isRegister) {
        super.setIsRegister(isRegister);

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
            }
        });
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
            }
        });
    }

    @Override
    public void onUserList(String channel, final User[] users) {

    }

    @Override
    public void onMessage(String target, String sender, String login, String hostname, String text, String headurl) {

        if(isCloseAllMsg()){
            return;
        }
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
        if (isCloseChat() || isCloseAllMsg()) {
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

    }

    @Override
    public void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason) {

    }

    @Override
    public void onKick(String target, String kickerNick, String kickerLogin, String kickerHostname, String
            recipientNick, String reason) {

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
                    //btMesOpen.setAlpha(0.4f);
                    btMesOpen.setBackgroundResource(getMsgBtnResId());
                } else {
                    if (fromNotice) {
                        XESToastUtils.showToast(mContext, "老师解除了你的禁言");
                    }
                    if (ircState.openchat()) {
                        //btMesOpen.setAlpha(1.0f);
                        btMesOpen.setBackgroundResource(getMsgBtnResId());
                    } else {
                        //btMesOpen.setAlpha(0.4f);
                        btMesOpen.setBackgroundResource(getMsgBtnResId());
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
                    //btMesOpen.setAlpha(0.4f);
                    btMesOpen.setBackgroundResource(getMsgBtnResId());
                } else {
                    if (openchat) {
                        //btMesOpen.setAlpha(1.0f);
                        btMesOpen.setBackgroundResource(getMsgBtnResId());
                    } else {
                        //btMesOpen.setAlpha(0.4f);
                        btMesOpen.setBackgroundResource(getMsgBtnResId());
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
    }

    @Override
    public void onOpenVoicebarrage(boolean openbarrage, boolean fromNotice) {

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
    }

    @Override
    public void onOpenVoiceNotic(boolean openVoice, String type) {

    }

    /*添加聊天信息，超过120，移除60个*/
    @Override
    public void addMessage(final String sender, final int type, final String text, final String headUrl) {
        final Exception e = new Exception();

        pool.execute(new Runnable() {
            @Override
            public void run() {
                final SpannableStringBuilder sBuilder = LiveMessageEmojiParser.convertToHtml(text, mContext,
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

                        if (mLiveMsgList.size() > 29) {
                            mLiveMsgList.remove(0);
                        }
                        mLiveMsgList.add(entity);
                        if (mMsgAdapter != null) {
                            mMsgAdapter.notifyItemInserted(0);
                        }
                    }
                });
            }
        });

    }

    @Override
    public CommonAdapter<LiveMessageEntity> getMessageAdapter() {
        return null;
    }

    @Override
    public void setOtherMessageAdapter(CommonAdapter<LiveMessageEntity> otherMessageAdapter) {
    }

    @Override
    public void onGetMyGoldDataEvent(String goldNum) {
        this.goldNum = goldNum;
    }


    public void showPeopleCount(int num) {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLiveMsgList != null) {
            mLiveMsgList.clear();
        }
        if (mView != null) {
            mView.removeCallbacks(hideBtmMediaCtrTask);
        }
    }

}
