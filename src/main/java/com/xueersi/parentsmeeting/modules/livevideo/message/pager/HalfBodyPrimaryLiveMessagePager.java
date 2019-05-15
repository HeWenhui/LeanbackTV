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
import com.xueersi.parentsmeeting.modules.livevideo.business.irc.jibble.pircbot.User;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.message.LiveIRCMessageBll;
import com.xueersi.parentsmeeting.modules.livevideo.message.business.LiveMessageEmojiParser;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.CenterAlignImageSpan;
import com.xueersi.parentsmeeting.modules.livevideo.widget.HalfBodyLiveMsgRecycelView;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveHalfBodyMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveTouchEventLayout;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.ImageScale;
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
 *
 * @author chenkun
 * @version 1.0, 2018/10/23 下午4:09
 */

public class HalfBodyPrimaryLiveMessagePager extends BaseLiveMessagePager {
    private static String TAG = "HalfBodyPrimaryLiveMessagePager";
    private Activity liveVideoActivity;

    public HalfBodyPrimaryLiveMessagePager(Context context, KeyboardUtil.OnKeyboardShowingListener keyboardShowingListener,
                                           LiveAndBackDebug ums, BaseLiveMediaControllerBottom
                                                   liveMediaControllerBottom, ArrayList<LiveMessageEntity>
                                                   liveMessageEntities, ArrayList<LiveMessageEntity>
                                                   otherLiveMessageEntities) {
        super(context);
        liveVideoActivity = (Activity) context;
        initData();
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, getLayoutId(), null);
        etMessageContent = (EditText) mView.findViewById(R.id.et_livevideo_message_content);
        dvMessageDanmaku = mView.findViewById(R.id.dv_livevideo_message_danmaku);
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
    public void initData() {
        super.initData();
        View tpkL_teampk_pkstate_root = mView.findViewById(R.id.tpkL_teampk_pkstate_root);
        ImageView iv_live_primary_class_kuangjia_img_normal = liveVideoActivity.findViewById(R.id.iv_live_primary_class_kuangjia_img_normal);
        ImageScale.setTeamPkRight(tpkL_teampk_pkstate_root, iv_live_primary_class_kuangjia_img_normal);
    }

    @Override
    public void closeChat(boolean close) {

    }

    @Override
    public boolean isCloseChat() {
        return false;
    }

    @Override
    public void addMessage(String sender, int type, String text, String headUrl) {

    }

    @Override
    public CommonAdapter<LiveMessageEntity> getMessageAdapter() {
        return null;
    }

    @Override
    public void setOtherMessageAdapter(CommonAdapter<LiveMessageEntity> otherMessageAdapter) {

    }

    @Override
    public void onStartConnect() {

    }

    @Override
    public void onConnect() {

    }

    @Override
    public void onRegister() {

    }

    @Override
    public void onDisconnect() {

    }

    @Override
    public void onUserList(String channel, User[] users) {

    }

    @Override
    public void onMessage(String target, String sender, String login, String hostname, String text, String headurl) {

    }

    @Override
    public void onPrivateMessage(boolean isSelf, String sender, String login, String hostname, String target, String message) {

    }

    @Override
    public void onJoin(String target, String sender, String login, String hostname) {

    }

    @Override
    public void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason) {

    }

    @Override
    public void onKick(String target, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason) {

    }

    @Override
    public void onDisable(boolean disable, boolean fromNotice) {

    }

    @Override
    public void onopenchat(boolean openchat, String mode, boolean fromNotice) {

    }

    @Override
    public void onOpenbarrage(boolean openbarrage, boolean fromNotice) {

    }

    @Override
    public void onOpenVoicebarrage(boolean openbarrage, boolean fromNotice) {

    }

    @Override
    public void onFDOpenbarrage(boolean open, boolean b) {

    }

    @Override
    public void onTeacherModeChange(String oldMode, String mode, boolean isShowNoticeTips, boolean iszjlkOpenbarrage, boolean isFDLKOpenbarrage) {

    }

    @Override
    public void onOpenVoiceNotic(boolean openVoice, String type) {

    }
}
