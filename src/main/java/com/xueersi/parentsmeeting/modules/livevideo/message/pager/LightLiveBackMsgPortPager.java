package com.xueersi.parentsmeeting.modules.livevideo.message.pager;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.common.base.BasePager;
import com.xueersi.common.business.AppBll;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.util.LoginEnter;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.string.RegexUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.bll.LightLiveBury;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.entity.LPWeChatEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.http.LightLiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.http.LightLiveHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.pager.TeacherWechatDialog;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveBackMsgEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.message.business.LiveMessageEmojiParser;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveMainHandler;
import com.xueersi.ui.dialog.VerifyCancelAlertDialog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @ProjectName: xueersiwangxiao
 * @Package: com.xueersi.parentsmeeting.modules.livevideo.message.pager
 * @ClassName: LightLiveBackMsgPortPager
 * @Description: java类作用描述
 * @Author: WangDe
 * @CreateDate: 2019/12/26 14:19
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/12/26 14:19
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class LightLiveBackMsgPortPager extends BasePager implements IBackMsgpager {

    private RecyclerView msgListView;
    /**
     * 消息集合
     **/
    ArrayList<LiveBackMsgEntity> liveMessageEntities;

    ArrayList<LiveBackMsgEntity> teacherMessageEntities;
    private boolean isTouch = false;
    /**
     * 聊天字体大小，最多13个汉字
     */
    private int messageSize = 0;
    private int[] nameColors;

    LiveMsgAdapter mMsgAdapter;
    LiveMsgAdapter mTeacherMsgAdapter;
    private Drawable dwSysIcon;
    private Drawable dwTeacherIcon;
    private TextView tvMessageDisable;
    private CheckBox cbMessageTeacher;
    private ImageView ivMessageClean;
    private TextView tvTeacherWeChat;
    /**
     * 联系老师实体
     */
    private LPWeChatEntity weChatEntity;
    boolean isShowWeChat;
    private VerifyCancelAlertDialog cleanMessageDialog;
    private LightLiveHttpManager lightLiveHttpManager;
    private LiveHttpManager liveHttpManager;
    private TeacherWechatDialog wechatDialog;
    private IGetLPInfo getLPInfo;

    public LightLiveBackMsgPortPager(Context context, boolean isShowWeChat) {
        super(context);
        this.isShowWeChat = isShowWeChat;
        LiveMainHandler.getMainHandler();
        liveMessageEntities = new ArrayList<>();
        teacherMessageEntities = new ArrayList<>();
        if (isShowWeChat) {
            tvTeacherWeChat.setVisibility(View.VISIBLE);
            LightLiveBury.clickBury(mContext.getResources().getString(R.string.show_03_84_005));
        }
        initListener();
        initData();
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.pager_liveback_message_port_lightlive, null);
        tvMessageDisable = (TextView) mView.findViewById(R.id.tv_livevideo_message_disable);
        cbMessageTeacher = (CheckBox) mView.findViewById(R.id.cb_livevideo_message_teacher);
        ivMessageClean = (ImageView) mView.findViewById(R.id.iv_livevideo_message_clean);
        tvTeacherWeChat = mView.findViewById(R.id.tv_livevideo_teacher_wechat);
        msgListView = mView.findViewById(R.id.lv_livevideo_message);
        msgListView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        return mView;
    }

    @Override
    public void initListener() {
        msgListView.setOnTouchListener(new View.OnTouchListener() {
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
        ivMessageClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LightLiveBury.clickBury(mContext.getResources().getString(R.string.click_03_84_013));
                cleanMessage();
            }
        });
        cbMessageTeacher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    XESToastUtils.showToastAtCenter("只看老师消息");
                    msgListView.setAdapter(mTeacherMsgAdapter);
                    msgListView.scrollToPosition(mTeacherMsgAdapter.getItemCount()-1);
                    LightLiveBury.clickBury(mContext.getResources().getString(R.string.click_03_84_010),1);
                } else {
//                    BuryManager.permission = true;
                    XESToastUtils.showToastAtCenter("接收全部消息");
                    msgListView.setAdapter(mMsgAdapter);
                    msgListView.scrollToPosition(mMsgAdapter.getItemCount()-1);
                    LightLiveBury.clickBury(mContext.getResources().getString(R.string.click_03_84_010),0);
                }
                messageStatus.justShowTeacher(isChecked);
            }
        });
        tvTeacherWeChat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (AppBll.getInstance().isAlreadyLogin()) {
                    if (weChatEntity != null && weChatEntity.hasData()) {
                        showWeChatDialog();
                    } else {
                        getLPInfo.getLPWeChat(new LpWechatCallBack());
                    }
                } else {
                    VerifyCancelAlertDialog goLoginDialog = new VerifyCancelAlertDialog(mContext, mBaseApplication, false, VerifyCancelAlertDialog.MESSAGE_VERIFY_CANCEL_TYPE);
                    //立即登录，查看你的%s，还有丰富福利哦
                    String message = String.format("立即登录，查看你的专属班主任");
                    goLoginDialog.initInfo(message);
                    goLoginDialog.setVerifyBtnListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            LoginEnter.openLogin(mContext, false, new Bundle());
                        }
                    });
                    goLoginDialog.showDialog();
                }

            }
        });
    }

    @Override
    public void initData() {
        nameColors = new int[]{mContext.getResources().getColor(R.color.COLOR_FF5E50), mContext.getResources().getColor(R.color.COLOR_FF5E50),
                mContext.getResources().getColor(R.color.COLOR_666666), mContext.getResources().getColor(R.color.COLOR_FE9B43)};
        initMsgRcyclView();
//        weChatEntity = new LPWeChatEntity();
//        weChatEntity.setTipInfo("tipinfo");
//        weChatEntity.setTeacherWx("00000");
//        weChatEntity.setExistWx(1);
//        weChatEntity.setWxQrUrl("555");
//        weChatEntity.setTeacherName("2222");
//        weChatEntity.setTipType(2);
    }

    private void initMsgRcyclView() {
        messageSize = (int) (ScreenUtils.getScreenDensity() * 15);
        mMsgAdapter = new LiveMsgAdapter(liveMessageEntities);
        mTeacherMsgAdapter = new LiveMsgAdapter(teacherMessageEntities);
        msgListView.setAdapter(mMsgAdapter);
        msgListView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int itemPosition = parent.getChildAdapterPosition(view);
                int left = 0;
                int right = 0;
                int top = 0;
                int bottom = 0;
                if (itemPosition > 0) {
                    top = SizeUtils.Dp2Px(mContext, 8);
                }
                outRect.set(left, top, right, bottom);
            }
        });
    }

    @Override
    public void addMsg(final LiveBackMsgEntity entity) {
        LiveMainHandler.post(new Runnable() {
            @Override
            public void run() {
                SpannableStringBuilder sBuilder = LiveMessageEmojiParser.convertToHtml(RegexUtils
                        .chatSendContentDeal(entity.getText().toString()), mContext, SizeUtils.Dp2Px(mContext, 12));
                entity.setText(sBuilder);
                liveMessageEntities.add(entity);
                if (LiveBackMsgEntity.MESSAGE_TEACHER == entity.getFrom() || LiveBackMsgEntity.MESSAGE_MINE == entity.getFrom()){
                    teacherMessageEntities.add(entity);
                }
                notifyDataSetChanged();
            }
        });

    }

    private void notifyDataSetChanged() {
        mMsgAdapter.notifyDataSetChanged();
        mTeacherMsgAdapter.notifyDataSetChanged();
        if (cbMessageTeacher.isChecked() && !teacherMessageEntities.isEmpty()) {
            msgListView.scrollToPosition(mTeacherMsgAdapter.getItemCount() - 1);
        } else if (!cbMessageTeacher.isChecked() && !liveMessageEntities.isEmpty()) {
            msgListView.scrollToPosition(mMsgAdapter.getItemCount() - 1);
        }
    }

    @Override
    public void removeAllMsg() {
        LiveMainHandler.post(new Runnable() {
            @Override
            public void run() {
                liveMessageEntities.clear();
                teacherMessageEntities.clear();
                mMsgAdapter.notifyDataSetChanged();
                mTeacherMsgAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void removeOverMsg(final long pos) {

        LiveMainHandler.post(new Runnable() {
            @Override
            public void run() {
                Iterator<LiveBackMsgEntity> iterator = liveMessageEntities.iterator();
                while (iterator.hasNext()) {
                    LiveBackMsgEntity entity = iterator.next();
                    if (entity.getId() > pos) {
                        iterator.remove();
                    }
                }
                Iterator<LiveBackMsgEntity> itTeacher = teacherMessageEntities.iterator();
                while (itTeacher.hasNext()) {
                    LiveBackMsgEntity entity = itTeacher.next();
                    if (entity.getId() > pos) {
                        itTeacher.remove();
                    }
                }
                mMsgAdapter.notifyDataSetChanged();
                mTeacherMsgAdapter.notifyDataSetChanged();
            }
        });
    }

    private class MsgItemHolder extends RecyclerView.ViewHolder {
        private TextView tvMsg;

        public MsgItemHolder(View itemView) {
            super(itemView);
            tvMsg = itemView.findViewById(R.id.tv_livevideo_message_item);
            tvMsg.setTextSize(TypedValue.COMPLEX_UNIT_PX, messageSize);
            tvMsg.setTextColor(mContext.getResources().getColor(R.color.COLOR_333333));
        }

        public void bindData(LiveBackMsgEntity entity) {
            String sender = entity.getName();
            SpannableString spanttt;
            if (entity.getFrom() == LiveBackMsgEntity.MESSAGE_MINE){
                sender = "我";
            }else if (entity.getFrom() == LiveBackMsgEntity.MESSAGE_TEACHER){
                sender = "主讲老师";
            } else if (entity.getFrom() == LiveBackMsgEntity.MESSAGE_TIP) {
                sender = "系统提示";
            }
            spanttt = new SpannableString(sender + ": ");

            int color;
            switch (entity.getFrom()) {
                case LiveBackMsgEntity.MESSAGE_MINE:
                case LiveBackMsgEntity.MESSAGE_TEACHER:
                case LiveBackMsgEntity.MESSAGE_TIP:
                case LiveBackMsgEntity.MESSAGE_CLASS:
                    color = nameColors[entity.getFrom()];
                    break;
                default:
                    color = nameColors[0];
                    break;
            }
            CharacterStyle characterStyle = new ForegroundColorSpan(color);
            spanttt.setSpan(characterStyle, 0, sender.length() + 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            tvMsg.setAutoLinkMask(0);
            tvMsg.setText(spanttt);
            if (LiveMessageEntity.MESSAGE_MINE == entity.getFrom()) {
                SpannableString meSpan = new SpannableString(entity.getText());
                CharacterStyle meStyle = new ForegroundColorSpan(color);
                meSpan.setSpan(meStyle, 0, entity.getText().length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                tvMsg.append(meSpan);
            } else {
                tvMsg.append(entity.getText());
            }
        }
    }


    private class LiveMsgAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<LiveBackMsgEntity> mData;

        public LiveMsgAdapter(List<LiveBackMsgEntity> data) {
            mData = data;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MsgItemHolder(View.inflate(parent.getContext(), R.layout.item_livevideo_lightlive_message, null));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((MsgItemHolder) holder).bindData(mData.get(position));
        }

        @Override
        public int getItemCount() {
            return mData == null ? 0 : mData.size();
        }
    }

    private void cleanMessage() {
        if (cleanMessageDialog == null) {
            cleanMessageDialog = new VerifyCancelAlertDialog(mContext, mBaseApplication, false, VerifyCancelAlertDialog.MESSAGE_VERIFY_CANCEL_TYPE);
            cleanMessageDialog.initInfo("需要清空当前所有聊天消息吗？");
            cleanMessageDialog.setVerifyBtnListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    XESToastUtils.showToastAtCenter("清屏成功！");
                    removeAllMsg();
                    LightLiveBury.clickBury(mContext.getResources().getString(R.string.click_03_84_014));
                }
            });
            cleanMessageDialog.setCancelBtnListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LightLiveBury.clickBury(mContext.getResources().getString(R.string.click_03_84_015));
                    cleanMessageDialog.cancelDialog();
                }
            });
        }
        cleanMessageDialog.showDialog();
    }

    /**
     * 显示联系老师弹窗
     */
    private void showWeChatDialog() {
        if (wechatDialog == null) {
            wechatDialog = new TeacherWechatDialog(mContext, mBaseApplication,false, weChatEntity.getTipType());
        }
        wechatDialog.setTeacherHead(weChatEntity.getTeacherImg()).setTeacherName(weChatEntity.getTeacherName())
                .setTeacherWechat(weChatEntity.getTeacherWx()).setQrcode(weChatEntity.getWxQrUrl()).setSubTitle(weChatEntity.getTipInfo());
        wechatDialog.showDialog();
        LightLiveBury.clickBury(mContext.getResources().getString(R.string.click_03_84_005),1);
        LightLiveBury.clickBury(mContext.getResources().getString(R.string.show_03_84_005));
    }

    public void setIGetLPInfo(IGetLPInfo getLPInfo) {
        this.getLPInfo = getLPInfo;
    }
    class LpWechatCallBack extends HttpCallBack{

        @Override
        public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
            LightLiveHttpResponseParser responseParser = new LightLiveHttpResponseParser();
            weChatEntity = responseParser.getLPWeChat(responseEntity);
            if (weChatEntity != null && weChatEntity.hasData()){
                showWeChatDialog();
            }
        }

        @Override
        public void onPmFailure(Throwable error,final String msg) {
            super.onPmFailure(error, msg);
            LiveMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    XESToastUtils.showToastAtCenter(msg);
                }
            });
            LightLiveBury.clickBury(mContext.getResources().getString(R.string.click_03_84_005),2);
        }

        @Override
        public void onPmError(final ResponseEntity responseEntity) {
            super.onPmError(responseEntity);
            LiveMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    XESToastUtils.showToastAtCenter(responseEntity.getErrorMsg());
                }
            });
            LightLiveBury.clickBury(mContext.getResources().getString(R.string.click_03_84_005),3);
        }
    }
    public interface IGetLPInfo {
        void getLPWeChat(HttpCallBack callBack);
    }

    private IMessageStatus  messageStatus ;
    public void setMessageStatus(IMessageStatus  messageStatus){
        this.messageStatus = messageStatus;
    }

    public interface IMessageStatus{

        void clearMessage();

        void justShowTeacher(boolean isShow);

    }
}
