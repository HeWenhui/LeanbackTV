package com.xueersi.parentsmeeting.modules.livevideo.message.pager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.event.MiniEvent;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.string.RegexUtils;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.parentsmeeting.modules.livevideo.OtherModulesEnter;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.activity.item.FlowerPortItem;
import com.xueersi.parentsmeeting.modules.livevideo.activity.item.MoreChoiceItem;
import com.xueersi.parentsmeeting.modules.livevideo.business.BaseLiveMessagePager;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.FlowerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.MoreChoice;
import com.xueersi.parentsmeeting.modules.livevideo.entity.User;
import com.xueersi.parentsmeeting.modules.livevideo.message.business.LiveMessageEmojiParser;
import com.xueersi.parentsmeeting.modules.livevideo.message.business.UserGoldTotal;
import com.xueersi.parentsmeeting.modules.livevideo.message.config.LiveMessageConfig;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionStatic;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.VerticalImageSpan;
import com.xueersi.ui.adapter.AdapterItemInterface;
import com.xueersi.ui.adapter.CommonAdapter;
import com.xueersi.ui.dataload.DataErrorManager;
import com.xueersi.ui.dataload.PageDataLoadEntity;
import com.xueersi.ui.widget.button.CompoundButtonGroup;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.dreamtobe.kpswitch.util.KPSwitchConflictUtil;
import cn.dreamtobe.kpswitch.util.KeyboardUtil;
import cn.dreamtobe.kpswitch.widget.KPSwitchFSPanelLinearLayout;

/**
 * Created by linyuqiang on 2016/8/2.
 * 直播聊天竖屏-直播讲座
 */
public class LiveMessagePortPager extends BaseLiveMessagePager {
    private String TAG = "LiveMessagePortPager";
    /** 献花，默认关闭 */
    private Button btMessageFlowers;
    /** 献花，上层10秒倒计时 */
    private TextView tvFlowersDisable;
    /** 聊天，默认打开 */
    private CheckBox cbMessageTeacher;
    /** 聊天，清空数据 */
    private ImageView ivMessageClean;
    /** 聊天人数 */
    private TextView tvMessageCount;
    /** 聊天消息 */
    private ListView lvMessage;
    private Button btMessageExpress;
    private CommonAdapter<LiveMessageEntity> messageAdapter;
    private CommonAdapter<LiveMessageEntity> otherMessageAdapter;
    private boolean isTouch = false;
    /** 聊天字体大小，最多13个汉字 */
    private int messageSize = 0;
    private TextView tvMessageDisable;
    /** 表情布局 */
    private View expressContentView;
    /** 献花布局 */
    private View flowerContentView;
    private TextView tvMessageGoldLable;
    private TextView tvMessageGold;
    private String goldNum;
    /** 上次发送消息时间 */
    private long lastSendMsg;
    private KPSwitchFSPanelLinearLayout switchFSPanelLinearLayout;
    private ImageView ivExpressionCancle;
    private KeyboardUtil.OnKeyboardShowingListener keyboardShowingListener;
    /** 竖屏的时候，也添加横屏的消息 */
    private ArrayList<LiveMessageEntity> otherLiveMessageEntities;
    /** 献花倒计时标记 */
    private String COUNT_TAG_FLO = "flo";
    /** 聊天倒计时标记 */
    private String COUNT_TAG_MSG = "msg";
    /** 立即报名 */
    private Button mApplyButton;
    /** 更多课程 */
    private RelativeLayout mMoreClassLayout;
    private Activity liveVideoActivity;
    private PageDataLoadEntity mPageDataLoadEntity;
    private List<MoreChoice.Choice> mChoices = new ArrayList<>();
    private CommonAdapter<MoreChoice.Choice> mCourseAdapter;
    private ListView mMorecourse;
    private TextView mApplyNum;
    private TextView mTvCoursename;
    private TextView mCourseNum;
    private ImageButton mShutDowm;
    private MoreChoice mData;
    private RelativeLayout mFirstSight;
    private LinearLayout mSecondSight;
    private LinearLayout mAdvance;
    private TextView mLimitnum;
    private ImageView mUnapplyed;
    private Handler mHandler;
    private BroadcastReceiver receiver;

    public LiveMessagePortPager(Context context, KeyboardUtil.OnKeyboardShowingListener keyboardShowingListener,
                                ArrayList<LiveMessageEntity> liveMessageEntities, ArrayList<LiveMessageEntity> otherLiveMessageEntities) {
        super(context);
        liveVideoActivity = (Activity) context;
        this.keyboardShowingListener = keyboardShowingListener;
        this.liveMessageEntities = liveMessageEntities;
        this.otherLiveMessageEntities = otherLiveMessageEntities;
        mHandler = new Handler();
        initListener();
        initData();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("refreshadvertisementlist");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // 04.12 弹出广告的时候，需要刷新广告列表
                mHandler.postDelayed(MoreChoice, 500);
            }
        };
        context.registerReceiver(receiver, intentFilter);
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.page_livevideo_message_port, null);
        mAdvance = (LinearLayout) mView.findViewById(R.id.ll_advance);
        mTvCoursename = (TextView) mView.findViewById(R.id.tv_coursename);
        mCourseNum = (TextView) mView.findViewById(R.id.tv_morecourse_num);
        mFirstSight = (RelativeLayout) mView.findViewById(R.id.ll_all_content);
        mSecondSight = (LinearLayout) mView.findViewById(R.id.ll_detail_list);
        mApplyButton = (Button) mView.findViewById(R.id.bt_to_apply);
        mMoreClassLayout = (RelativeLayout) mView.findViewById(R.id.more_class);
        mApplyNum = (TextView) mView.findViewById(R.id.tv_apply_number);
        mShutDowm = (ImageButton) mView.findViewById(R.id.ib_back);
        mLimitnum = (TextView) mView.findViewById(R.id.tv_limitnum);
        mUnapplyed = (ImageView) mView.findViewById(R.id.iv_unapply);
        mMorecourse = (ListView) mView.findViewById(R.id.morecourse_list);
        tvMessageCount = (TextView) mView.findViewById(R.id.tv_livevideo_message_count);
        lvMessage = (ListView) mView.findViewById(R.id.lv_livevideo_message);
        etMessageContent = (EditText) mView.findViewById(R.id.et_livevideo_message_content);
        tvMessageDisable = (TextView) mView.findViewById(R.id.tv_livevideo_message_disable);
        tvMessageDisable.setTag(MESSAGE_SEND_DEF);
        btMessageFlowers = (Button) mView.findViewById(R.id.bt_livevideo_message_flowers);
        tvFlowersDisable = (TextView) mView.findViewById(R.id.tv_livevideo_message_flowers_disable);
        btMessageExpress = (Button) mView.findViewById(R.id.bt_livevideo_message_express);
        switchFSPanelLinearLayout = (KPSwitchFSPanelLinearLayout) mView.findViewById(R.id
                .rl_livevideo_message_panelroot);
        ivExpressionCancle = (ImageView) mView.findViewById(R.id.iv_livevideo_message_expression_cancle);
        cbMessageTeacher = (CheckBox) mView.findViewById(R.id.cb_livevideo_message_teacher);
        ivMessageClean = (ImageView) mView.findViewById(R.id.iv_livevideo_message_clean);
        return mView;
    }

    @Override
    public void initListener() {
        //献花禁止，写个空点击，挡住下方
        tvFlowersDisable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        tvMessageDisable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        etMessageContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent
                        .KEYCODE_ENTER)) {
                    logger.i( "onClick:time=" + (System.currentTimeMillis() - lastSendMsg));
                    Editable editable = etMessageContent.getText();
                    String msg = editable.toString();
                    if (!StringUtils.isSpace(msg)) {
                        if (getInfo != null && getInfo.getBlockChinese() && isChinese(msg)) {
                            addMessage(SYSTEM_TIP, LiveMessageEntity.MESSAGE_TIP, MESSAGE_CHINESE, "");
                            InputMethodManager mInputMethodManager = (InputMethodManager) mContext.getSystemService(Context
                                    .INPUT_METHOD_SERVICE);
                            mInputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                            return true;
                        }
                        if (ircState.openchat()) {
                            if (System.currentTimeMillis() - lastSendMsg > SEND_MSG_INTERVAL) {
                                boolean send = ircState.sendMessage(msg, "");
                                if (send) {
                                    startCountDown(COUNT_TAG_MSG, (int) (SEND_MSG_INTERVAL / 1000));
                                    etMessageContent.setText("");
                                    addMessage("我", LiveMessageEntity.MESSAGE_MINE, msg, "");
                                    lastSendMsg = System.currentTimeMillis();
                                } else {
                                    XESToastUtils.showToast(mContext, "你已被禁言!");
                                }
                            } else {
                                //暂时去掉3秒发言，信息提示
//                                addMessage("提示", LiveMessageEntity.MESSAGE_TIP, "3秒后才能再次发言，要认真听课哦!");
                                long time;
                                try {
                                    time = Long.parseLong(tvMessageDisable.getText().subSequence(1, 2).toString());
                                } catch (Exception e) {
                                    time = SEND_MSG_INTERVAL / 1000;
                                }
                                XESToastUtils.showToast(mContext, time + "秒后才能再次发言，要认真听课哦!");
                            }
                        } else {
                            XESToastUtils.showToast(mContext, "老师未开启聊天");
                        }
                    } else {
                        addMessage(SYSTEM_TIP, LiveMessageEntity.MESSAGE_TIP, MESSAGE_EMPTY, "");
                    }
                    return true;
                }
                return false;
            }
        });
        btMessageFlowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (goldNum == null) {
                    OtherModulesEnter.requestGoldTotal(mContext);
                }
                QuestionStatic questionStatic = ProxUtil.getProxUtil().get(mContext, QuestionStatic.class);
                if (questionStatic != null && questionStatic.isAnaswer()) {
                    XESToastUtils.showToast(mContext, "正在答题，不能献花");
                    return;
                }
                if (LiveTopic.MODE_CLASS.equals(ircState.getMode())) {
                    if (!ircState.isOpenbarrage()) {
                        XESToastUtils.showToast(mContext, "老师未开启献花");
                        return;
                    }
                } else {
                    XESToastUtils.showToast(mContext, "辅导模式不能献花");
                    return;
                }
                isHaveFlowers = true;
                if (switchFSPanelLinearLayout.getVisibility() == View.VISIBLE && expressContentView.getVisibility()
                        == View.VISIBLE) {
                    expressContentView.setVisibility(View.INVISIBLE);
                    flowerContentView.setVisibility(View.VISIBLE);
                    btMessageExpress.setBackgroundResource(R.drawable.im_input_biaoqing_icon_normal);
                    return;
                }
                expressContentView.setVisibility(View.INVISIBLE);
                flowerContentView.setVisibility(View.VISIBLE);
                boolean switchToPanel = KPSwitchConflictUtil.switchPanelAndKeyboard(switchFSPanelLinearLayout,
                        etMessageContent);
                if (switchToPanel) {
                    btMessageFlowers.setBackgroundResource(R.drawable.im_input_jianpan_icon_normal);
                } else {
                    btMessageFlowers.setBackgroundResource(R.drawable.bg_livevideo_message_flowers_port);
                }
            }
        });
        lvMessage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    isTouch = true;
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent
                        .ACTION_CANCEL) {
                    isTouch = false;
                }
                return false;
            }
        });
        KeyboardUtil.attach((Activity) mContext, switchFSPanelLinearLayout, new KeyboardUtil
                .OnKeyboardShowingListener() {
            @Override
            public void onKeyboardShowing(boolean isShowing) {
                logger.i( "onKeyboardShowing:isShowing=" + isShowing);
                if (!isShowing && switchFSPanelLinearLayout.getVisibility() == View.GONE) {
                    onTitleShow(true);
                }
                keyboardShowing = isShowing;
                if (keyboardShowing) {
                    btMessageExpress.setBackgroundResource(R.drawable.im_input_biaoqing_icon_normal);
                    if ("1".equals(btMessageFlowers.getTag())) {
                        btMessageFlowers.setBackgroundResource(R.drawable.bg_livevideo_message_flowers_port);
                    } else {
                        btMessageFlowers.setBackgroundResource(R.drawable.bg_livevideo_message_flowers_port_disable);
                    }
                }
                keyboardShowingListener.onKeyboardShowing(isShowing);
            }
        });
        btMessageExpress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchFSPanelLinearLayout.getVisibility() == View.VISIBLE && flowerContentView.getVisibility() ==
                        View.VISIBLE) {
                    expressContentView.setVisibility(View.VISIBLE);
                    flowerContentView.setVisibility(View.INVISIBLE);
                    isHaveFlowers = false;
                    if ("1".equals(btMessageFlowers.getTag())) {
                        btMessageFlowers.setBackgroundResource(R.drawable.bg_livevideo_message_flowers_port);
                    } else {
                        btMessageFlowers.setBackgroundResource(R.drawable.bg_livevideo_message_flowers_port_disable);
                    }
                    return;
                }
                expressContentView.setVisibility(View.VISIBLE);
                flowerContentView.setVisibility(View.INVISIBLE);
                isHaveFlowers = false;
                final boolean switchToPanel = KPSwitchConflictUtil.switchPanelAndKeyboard(switchFSPanelLinearLayout,
                        etMessageContent);
                if (switchToPanel) {
                    btMessageExpress.setBackgroundResource(R.drawable.im_input_jianpan_icon_normal);
                    etMessageContent.clearFocus();
                } else {
                    btMessageExpress.setBackgroundResource(R.drawable.im_input_biaoqing_icon_normal);
                    etMessageContent.requestFocus();
                }
            }
        });
        KPSwitchConflictUtil.attach(switchFSPanelLinearLayout, etMessageContent);
        ivMessageClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XESToastUtils.showToast(mContext, "清屏成功！");
                liveMessageEntities.clear();
                otherLiveMessageEntities.clear();
                messageAdapter.notifyDataSetChanged();
                if (otherMessageAdapter != null) {
                    otherMessageAdapter.notifyDataSetChanged();
                }
            }
        });
        cbMessageTeacher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    XESToastUtils.showToast(mContext, "只看老师消息");
                } else {
                    XESToastUtils.showToast(mContext, "接收全部消息");
                }
            }
        });

        // 03.28 展开更多课程的列表
        mMoreClassLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ircState.getMoreChoice(mPageDataLoadEntity, getDataCallBack);
                Animation animation = AnimationUtils.loadAnimation(
                        liveVideoActivity, R.anim.anim_livevideo_lecture_morechoice);
                mSecondSight.startAnimation(animation);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSecondSight.setVisibility(View.VISIBLE);
                        mFirstSight.setVisibility(View.GONE);
                    }
                }, 200);


            }
        });
        // 04.04 关闭更多课程页面
        mShutDowm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFirstSight.setVisibility(View.VISIBLE);
                mSecondSight.setVisibility(View.GONE);
            }
        });
    }

    AbstractBusinessDataCallBack getDataCallBack = new AbstractBusinessDataCallBack() {
        @Override
        public void onDataSucess(Object... objData) {
            // 04.04 获取到数据之后的逻辑处理
            if (objData.length > 0) {
                mData = (MoreChoice) objData[0];
                logger.e( "mData:" + mData);
                mChoices.clear();
                mChoices.addAll(mData.getCases());
                LiveVideoConfig.MORE_COURSE = mChoices.size();
                if (mChoices.size() > 0) {
                    mTvCoursename.setText(mChoices.get(mChoices.size() - 1).getSaleName());
                    mLimitnum.setText(Html.fromHtml("<font color='#999999'>剩余名额</font>" + "<font color='#F13232'>" + "  " + mChoices.get(mChoices.size() - 1).getLimit() + "</font>"));
                    // 04.09 按钮不同状态的维护
                    if (mChoices.get(mChoices.size() - 1).getIsLearn() > 0) {
                        mApplyButton.setText("已报名");
                        mApplyButton.setTextColor(Color.parseColor("#999999"));
                        mApplyButton.setBackgroundResource(R.drawable.bg_applyed);
                    } else {
                        mApplyButton.setText("立即报名");
                        mApplyButton.setTextColor(Color.parseColor("#F13232"));
                        mApplyButton.setBackgroundResource(R.drawable.bg_apply);
                    }
                    if (mChoices.get(mChoices.size() - 1).getLimit() == 0) {
                        mApplyButton.setVisibility(View.GONE);
                        mUnapplyed.setVisibility(View.VISIBLE);
                    } else {
                        mApplyButton.setVisibility(View.VISIBLE);
                        mUnapplyed.setVisibility(View.GONE);
                    }
                    // 04.09 跳转支付页面
                    mApplyButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (mChoices.get(mChoices.size() - 1).getLimit() > 0 && mChoices.get(mChoices.size() - 1).getIsLearn() == 0) {
                                EventBus.getDefault().post(new MiniEvent("Order", mChoices.get(mChoices.size() - 1).getCourseId(), mChoices.get(mChoices.size() - 1).getClassId(), mChoices.get(mChoices.size() - 1).getAdId()));
                            }

                        }
                    });
                    mCourseNum.setText(mChoices.size() + "");
                    mApplyNum.setText(Html.fromHtml("<font color='#333333'>正在报名中</font>" + "<font color='#F13232'>" + "  " + mChoices.size() + "</font>"));
                    mAdvance.setVisibility(View.VISIBLE);
                } else {
                    mAdvance.setVisibility(View.GONE);
                }
                mCourseAdapter.updateData(mChoices);
                if (LiveVideoConfig.isloading) {
                    mFirstSight.setVisibility(View.GONE);
                    mSecondSight.setVisibility(View.VISIBLE);
                    LiveVideoConfig.isloading = !LiveVideoConfig.isloading;
                }
            }
            // 双重校验去除竖屏时抽屉面板的影藏
            EventBus.getDefault().post(new MiniEvent("Invisible", "", "", ""));

        }
    };


    @Override
    public void initData() {
        super.initData();
        mPageDataLoadEntity = new PageDataLoadEntity(mView, R.id.ll_all_content, DataErrorManager.IMG_TIP_BUTTON)
                .setWebErrorTip(R.string.web_error_tip_default).setDataIsEmptyTip("暂无更多课程").setOverrideBackgroundColor();
        liveThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                UserGoldTotal.requestGoldTotal(mContext);
            }
        });
        ivExpressionCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int action = KeyEvent.ACTION_DOWN;
                int code = KeyEvent.KEYCODE_DEL;
                KeyEvent event = new KeyEvent(action, code);
                etMessageContent.onKeyDown(KeyEvent.KEYCODE_DEL, event);
            }
        });
        showExpressionView(true);
        messageSize = (int) (ScreenUtils.getScreenDensity() * 15);
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
                        tvMessageItem.setTextColor(mContext.getResources().getColor(R.color.COLOR_333333));
                    }

                    @Override
                    public void bindListener() {

                    }

                    @Override
                    public void updateViews(LiveMessageEntity entity, int position, Object objTag) {
                        String sender = entity.getSender();
                        SpannableString spanttt = new SpannableString(sender + ": ");
                        int color;
                        if (entity.getType() == LiveMessageEntity.MESSAGE_FLOWERS) {
                            if (entity.isSelf()) {
                                color = nameColors[0];
                            } else {
                                color = nameColors[2];
                            }
                            CharacterStyle characterStyle = new ForegroundColorSpan(color);
                            spanttt.setSpan(characterStyle, 0, sender.length() + 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                            tvMessageItem.setText(spanttt);
                            tvMessageItem.append(flowsTips[entity.getFtype() - 2] + ",献上 ");
                            int id;
                            switch (entity.getFtype()) {
                                case FLOWERS_SMALL:
                                case FLOWERS_MIDDLE:
                                case FLOWERS_BIG:
                                    id = flowsDrawLittleTips[entity.getFtype() - 2];
                                    break;
                                default:
                                    id = R.drawable.ic_app_xueersi_desktop;
                                    break;
                            }
                            SpannableString spannableString = new SpannableString("f");
                            Bitmap bitmap11 = BitmapFactory.decodeResource(mContext.getResources(), id);
                            ImageSpan span = new VerticalImageSpan(mContext, Bitmap.createScaledBitmap(bitmap11,
                                    (int) etMessageContent.getTextSize(), (int) tvMessageItem.getTextSize(), true));
                            spannableString.setSpan(span, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                            tvMessageItem.append(spannableString);
                        } else {
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
                    }
                };
            }
        };
        lvMessage.setAdapter(messageAdapter);
        initDanmaku();
        ArrayList<FlowerEntity> flowerEntities = new ArrayList<>();
        flowerEntities.add(new FlowerEntity(FLOWERS_SMALL, flowsDrawTips[0], "1支玫瑰", 10));
        flowerEntities.add(new FlowerEntity(FLOWERS_MIDDLE, flowsDrawTips[1], "1束玫瑰", 50));
        flowerEntities.add(new FlowerEntity(FLOWERS_BIG, flowsDrawTips[2], "1束蓝色妖姬", 100));
        expressContentView = mView.findViewById(R.id.layout_chat_expression);
        flowerContentView = mView.findViewById(R.id.layout_livevideo_flowers);
        tvMessageGold = (TextView) flowerContentView.findViewById(R.id.tv_livevideo_message_gold);
        tvMessageGoldLable = (TextView) flowerContentView.findViewById(R.id.tv_livevideo_message_gold_lable);
        LinearLayout llMessageFlower = (LinearLayout) mView.findViewById(R.id.ll_livevideo_message_flower);
        LayoutInflater factory = LayoutInflater.from(mContext);
        CompoundButtonGroup group = new CompoundButtonGroup();
        final ArrayList<FlowerPortItem> flowerPortItems = new ArrayList<>();
        for (int i = 0; i < flowerEntities.size(); i++) {
            final FlowerEntity entity = flowerEntities.get(i);
            final FlowerPortItem flowerItem = new FlowerPortItem(mContext);
            flowerPortItems.add(flowerItem);
            View root = factory.inflate(flowerItem.getLayoutResId(), llMessageFlower, false);
            flowerItem.initViews(root);
            flowerItem.updateViews(flowerEntities.get(i), i, null);
            llMessageFlower.addView(root);
            group.addCheckBox((CheckBox) root.findViewById(R.id.ck_livevideo_message_flower), new CompoundButton
                    .OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        flowerItem.root.setBackgroundResource(R.drawable.bg_livevideo_flowerroot_check);
                        flowerContentView.setTag(entity);
                        for (int i = 0; i < flowerPortItems.size(); i++) {
                            FlowerPortItem otherFlowerPortItem = flowerPortItems.get(i);
                            if (otherFlowerPortItem.checkBox != buttonView) {
                                otherFlowerPortItem.checkBox.setChecked(false);
                                otherFlowerPortItem.root.setBackgroundDrawable(null);
                            }
                        }
                    } else {
                        flowerItem.root.setBackgroundDrawable(null);
                        FlowerEntity entity2 = (FlowerEntity) flowerContentView.getTag();
                        if (entity == entity2) {
                            flowerContentView.setTag(null);
                        }
                    }
                }
            });
        }
        flowerContentView.findViewById(R.id.bt_livevideo_message_flowersend).setOnClickListener(new View
                .OnClickListener() {
            @Override
            public void onClick(View v) {
                final FlowerEntity entity = (FlowerEntity) flowerContentView.getTag();
                if (entity != null) {
                    if (LiveTopic.MODE_CLASS.equals(ircState.getMode())) {
                        if (ircState.isOpenbarrage()) {
                            ircState.praiseTeacher("", entity.getFtype() + "", "", new HttpCallBack(false) {
                                @Override
                                public void onPmSuccess(ResponseEntity responseEntity) {
                                    tvFlowersDisable.setVisibility(View.VISIBLE);
                                    Runnable runnable = startCountDown(COUNT_TAG_FLO, 10);
                                    tvFlowersDisable.setTag(runnable);
                                    if (goldNum == null) {
                                        OtherModulesEnter.requestGoldTotal(mContext);
                                    } else {
                                        if (responseEntity.getJsonObject() instanceof JSONObject) {
                                            try {
                                                JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                                                int gold = Integer.parseInt(goldNum);
                                                goldNum = ("" + (gold - jsonObject.getInt("gold")));
                                                onGetMyGoldDataEvent(goldNum);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                    addFlowers("-1", "我", LiveMessageEntity
                                            .MESSAGE_FLOWERS, entity.getFtype());
//                                    addDanmaKuFlowers(entity.getFtype(), getInfo.getStuName());
                                    mView.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            removeFlowers();
                                        }
                                    }, 1000);
                                }

                                @Override
                                public void onPmFailure(Throwable error, String msg) {
                                    removeFlowers();
                                    tvFlowersDisable.setVisibility(View.GONE);
                                }

                                @Override
                                public void onPmError(ResponseEntity responseEntity) {
                                    removeFlowers();
                                    tvFlowersDisable.setVisibility(View.GONE);
                                }
                            });
//                        liveBll.sendFlowerMessage(entity.getFtype());
                        } else {
                            XESToastUtils.showToast(mContext, "老师未开启献花");
                        }
                    } else {
                        XESToastUtils.showToast(mContext, "辅导模式不能献花");
                    }
                } else {
                    XESToastUtils.showToast(mContext, "请选择一束花");
                }
            }
        });
        lvMessage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (keyboardShowing) {
                    KeyboardUtil.hideKeyboard(etMessageContent);
                }
                return false;
            }
        });
        // 更多课程的数据加载
        if (mCourseAdapter == null) {
            mCourseAdapter = new CommonAdapter<MoreChoice.Choice>(mChoices) {
                @Override
                public AdapterItemInterface<MoreChoice.Choice> getItemView(Object type) {
                    MoreChoiceItem morelistItem = new MoreChoiceItem(mContext, mData);
                    return morelistItem;
                }

            };
            mMorecourse.setAdapter(mCourseAdapter);
        }
        mHandler.postDelayed(MoreChoice, 600);
    }

    private Runnable MoreChoice = new Runnable() {
        @Override
        public void run() {
            ircState.getMoreChoice(mPageDataLoadEntity, getDataCallBack);
        }
    };

    /** 添加献花布局 */
    private void addFlowers() {
        expressContentView.setVisibility(View.INVISIBLE);
        flowerContentView.setVisibility(View.VISIBLE);
        KPSwitchConflictUtil.switchPanelAndKeyboard(switchFSPanelLinearLayout, etMessageContent);
    }

    /** 移除献花布局 */
    private void removeFlowers() {
        if (flowerContentView.getVisibility() == View.VISIBLE) {
            isHaveFlowers = false;
            flowerContentView.setVisibility(View.INVISIBLE);
            switchFSPanelLinearLayout.setVisibility(View.GONE);
            KeyboardUtil.hideKeyboard(etMessageContent);
            if ("1".equals(btMessageFlowers.getTag())) {
                btMessageFlowers.setBackgroundResource(R.drawable.bg_livevideo_message_flowers_port);
            } else {
                btMessageFlowers.setBackgroundResource(R.drawable.bg_livevideo_message_flowers_port_disable);
            }
        }
    }

    @Override
    public void onTitleShow(boolean show) {

    }

    @Override
    public void countDown(String tag, int time) {
        if (COUNT_TAG_FLO.equals(tag)) {
            if (time > 0) {
                tvFlowersDisable.setVisibility(View.VISIBLE);
            } else {
                tvFlowersDisable.setVisibility(View.GONE);
            }
            tvFlowersDisable.setText("" + time);
        } else if (COUNT_TAG_MSG.equals(tag)) {
            if ((int) tvMessageDisable.getTag() == MESSAGE_SEND_DEF) {
                if (time > 0) {
                    //etMessageContent.setVisibility(View.GONE);
                    tvMessageDisable.setVisibility(View.VISIBLE);
                } else {
                    //etMessageContent.setVisibility(View.VISIBLE);
                    tvMessageDisable.setVisibility(View.GONE);
                }
                tvMessageDisable.setText("请" + time + "秒后再次发言");
            }
        }
    }

    @Override
    public void closeChat(final boolean close) {
        mView.post(new Runnable() {
            @Override
            public void run() {
                if (close) {
                    cbMessageTeacher.setChecked(true);
                } else {
                    cbMessageTeacher.setChecked(false);
                }
            }
        });
    }

    @Override
    public boolean isCloseChat() {
        return cbMessageTeacher.isChecked();
    }

    /** 聊天开始连接 */
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
        if (haveFlowers) {
            addFlowers();
        } else {
            removeFlowers();
        }
    }

    /** 聊天连上 */
    @Override
    public void onConnect() {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                addMessage(SYSTEM_TIP, LiveMessageEntity.MESSAGE_TIP, CONNECT, "");
            }
        });
    }

    /** 聊天进入房间 */
    @Override
    public void onRegister() {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                isRegister = true;
            }
        });
    }

    /** 聊天断开 */
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
        mView.post(new Runnable() {
            @Override
            public void run() {
                tvMessageCount.setText("在线" + peopleCount + "人");
            }
        });
    }

    @Override
    public void onMessage(String target, String sender, String login, String hostname, String text, String headurl) {
        if (sender.startsWith(LiveMessageConfig.TEACHER_PREFIX)) {
            sender = "主讲老师";
        } else if (sender.startsWith(LiveMessageConfig.COUNTTEACHER_PREFIX)) {
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
        mView.post(new Runnable() {
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
//                        addDanmaKuFlowers(jsonObject.getInt("ftype"), jsonObject.getString("name"));
                        addFlowers(jsonObject.optString("id"), jsonObject.getString("name"), LiveMessageEntity
                                .MESSAGE_FLOWERS, jsonObject.optInt("ftype"));
                    }
                } catch (JSONException e) {
                    addMessage(sender, LiveMessageEntity.MESSAGE_CLASS, message, "");
                }
            }
        });
    }

    @Override
    public void onJoin(String target, String sender, String login, String hostname) {
        mView.post(new Runnable() {
            @Override
            public void run() {
                tvMessageCount.setText("在线" + peopleCount + "人");
            }
        });
    }

    @Override
    public void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason) {
        mView.post(new Runnable() {
            @Override
            public void run() {
                tvMessageCount.setText("在线" + peopleCount + "人");
            }
        });
    }

    @Override
    public void onKick(String target, String kickerNick, String kickerLogin, String kickerHostname, String
            recipientNick, String reason) {

    }

    /** 被禁言 */
    public void onDisable(final boolean disable, final boolean fromNotice) {
        mView.post(new Runnable() {
            @Override
            public void run() {
                if (disable) {
                    if (fromNotice) {
                        XESToastUtils.showToast(mContext, "你被老师禁言了");
                    }
                    //etMessageContent.setVisibility(View.GONE);
                    tvMessageDisable.setVisibility(View.VISIBLE);
                    tvMessageDisable.setText("你被老师禁言了");
                    tvMessageDisable.setTag(MESSAGE_SEND_DIS);
                } else {
                    if (fromNotice) {
                        XESToastUtils.showToast(mContext, "老师解除了你的禁言");
                    }
                    if (ircState.openchat()) {
                        //etMessageContent.setVisibility(View.VISIBLE);
                        tvMessageDisable.setVisibility(View.GONE);
                        tvMessageDisable.setTag(MESSAGE_SEND_DEF);
                    } else {
                        //etMessageContent.setVisibility(View.GONE);
                        tvMessageDisable.setVisibility(View.VISIBLE);
                        tvMessageDisable.setTag(MESSAGE_SEND_CLO);
                        if (LiveTopic.MODE_CLASS.equals(ircState.getMode())) {
                            tvMessageDisable.setText("主讲老师关闭了聊天区");
                        } else {
                            tvMessageDisable.setText("辅导老师关闭了聊天区");
                        }
                    }
                }
            }
        });
    }

    /** 关闭开启聊天 */
    @Override
    public void onopenchat(final boolean openchat, final String mode, final boolean fromNotice) {
        mView.post(new Runnable() {
            @Override
            public void run() {
                if (ircState.isDisable()) {
                    //etMessageContent.setVisibility(View.GONE);
                    tvMessageDisable.setVisibility(View.VISIBLE);
                    tvMessageDisable.setText("你被老师禁言了");
                    tvMessageDisable.setTag(MESSAGE_SEND_DIS);
                } else {
                    if (openchat) {
                        //etMessageContent.setVisibility(View.VISIBLE);
                        tvMessageDisable.setVisibility(View.GONE);
                        tvMessageDisable.setTag(MESSAGE_SEND_DEF);
                    } else {
                        //etMessageContent.setVisibility(View.GONE);
                        tvMessageDisable.setVisibility(View.VISIBLE);
                        tvMessageDisable.setTag(MESSAGE_SEND_CLO);
                        if (LiveTopic.MODE_CLASS.equals(mode)) {
                            tvMessageDisable.setText("主讲老师关闭了聊天区");
                        } else {
                            tvMessageDisable.setText("辅导老师关闭了聊天区");
                        }
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
    public boolean onBack() {
//        ViewGroup parent = (ViewGroup) flowerContentView.getParent();
//        if (parent != null) {
//            removeFlowers();
//            return true;
//        }
        return super.onBack();
    }

    @Override
    public void onModeChange(final String mode) {
        mView.post(new Runnable() {

            @Override
            public void run() {
                // 主讲模式可以献花
                if (LiveTopic.MODE_CLASS.equals(mode)) {
                    if (ircState.isOpenbarrage()) {
                        btMessageFlowers.setTag("1");
                        btMessageFlowers.setBackgroundResource(R.drawable.bg_livevideo_message_flowers_port);
                    } else {
                        btMessageFlowers.setTag("0");
                        btMessageFlowers.setBackgroundResource(R.drawable.bg_livevideo_message_flowers_port_disable);
                    }
                } else {
                    btMessageFlowers.setTag("0");
                    btMessageFlowers.setBackgroundResource(R.drawable.bg_livevideo_message_flowers_port_disable);
                }
            }
        });
    }

    /** 关闭开启弹幕 */
    public void onOpenbarrage(final boolean openbarrage, final boolean fromNotice) {
        mView.post(new Runnable() {
            @Override
            public void run() {
                if (LiveTopic.MODE_CLASS.equals(ircState.getMode())) {
                    if (openbarrage) {
                        if (fromNotice) {
                            XESToastUtils.showToast(mContext, "老师开启了献花");
                        }
                        btMessageFlowers.setTag("1");
                        btMessageFlowers.setBackgroundResource(R.drawable.bg_livevideo_message_flowers_port);
                    } else {
                        if (fromNotice) {
                            XESToastUtils.showToast(mContext, "老师关闭了献花");
                        }
                        btMessageFlowers.setTag("0");
                        btMessageFlowers.setBackgroundResource(R.drawable.bg_livevideo_message_flowers_port_disable);
                    }
                }
            }
        });
    }

    @Override
    public void onOpenVoicebarrage(boolean openbarrage, boolean fromNotice) {

    }

    @Override
    public void onFDOpenbarrage(boolean open, boolean b) {

    }

    @Override
    public void onTeacherModeChange(String oldMode, String mode, boolean b, boolean zjlkOpenbarrage, boolean zjfdOpenbarrage) {

    }

    @Override
    public void onOpenVoiceNotic(boolean openVoice, String type) {

    }

    /*添加聊天信息，超过120，移除60个*/
    @Override
    public void addMessage(final String sender, final int type, final String text, String headUrl) {
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
                        if (otherLiveMessageEntities.size() > 29) {
                            otherLiveMessageEntities.remove(0);
                        }
                        LiveMessageEntity entity = new LiveMessageEntity(sender, type, sBuilder);
                        liveMessageEntities.add(entity);
                        otherLiveMessageEntities.add(entity);
                        messageAdapter.notifyDataSetChanged();
                        if (otherMessageAdapter != null) {
                            otherMessageAdapter.notifyDataSetChanged();
                        }
                        if (!isTouch) {
                            lvMessage.setSelection(lvMessage.getCount() - 1);
                        }
                    }
                });
            }
        });
    }

    public void addFlowers(final String id, final String sender, final int type, final int ftype) {
        mView.post(new Runnable() {
            @Override
            public void run() {
                if (liveMessageEntities.size() > 29) {
                    liveMessageEntities.remove(0);
                }
                boolean self = "-1".equals(id);
                liveMessageEntities.add(new LiveMessageEntity(self, sender, type, ftype));
                messageAdapter.notifyDataSetChanged();
                if (!isTouch) {
                    lvMessage.setSelection(lvMessage.getCount() - 1);
                }
            }
        });
    }

    @Override
    public void onGetMyGoldDataEvent(String goldNum) {
        this.goldNum = goldNum;
        tvMessageGold.setText(goldNum);
        tvMessageGold.setVisibility(View.VISIBLE);
        tvMessageGoldLable.setVisibility(View.VISIBLE);
    }

    @Override
    public void onQuestionHide() {
        mView.findViewById(R.id.rl_livevideo_message_content2).setVisibility(View.VISIBLE);
        mView.findViewById(R.id.rl_livevideo_message_status).setVisibility(View.VISIBLE);
    }

    @Override
    public void onQuestionShow() {
        mView.findViewById(R.id.rl_livevideo_message_content2).setVisibility(View.INVISIBLE);
        mView.findViewById(R.id.rl_livevideo_message_status).setVisibility(View.INVISIBLE);
        KeyboardUtil.hideKeyboard(etMessageContent);
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
    public void onDestroy() {
        super.onDestroy();
        liveVideoActivity.unregisterReceiver(receiver);
    }
}
