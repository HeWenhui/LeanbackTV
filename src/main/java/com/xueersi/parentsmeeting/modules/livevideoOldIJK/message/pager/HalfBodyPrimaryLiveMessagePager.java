package com.xueersi.parentsmeeting.modules.livevideoOldIJK.message.pager;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.config.HalfBodyLiveConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.primaryclass.weight.PrimaryKuangjiaImageView;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.BaseLiveMessagePager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.irc.jibble.pircbot.User;
import com.xueersi.ui.adapter.CommonAdapter;

import java.util.ArrayList;

import cn.dreamtobe.kpswitch.util.KeyboardUtil;

/**
 * 半身小班
 *
 * @author linyuqiang
 * @version 1.0, 2018/10/23 下午4:09
 */
public class HalfBodyPrimaryLiveMessagePager extends BaseLiveMessagePager {
    private static String TAG = "HalfBodyPrimaryLiveMessagePager";
    private Activity liveVideoActivity;
    private int useSkin;
    private PrimaryKuangjiaImageView iv_live_primary_class_kuangjia_img_normal;
    private View tpkL_teampk_pkstate_root;

    public HalfBodyPrimaryLiveMessagePager(Context context, KeyboardUtil.OnKeyboardShowingListener keyboardShowingListener,
                                           LiveAndBackDebug ums, BaseLiveMediaControllerBottom
                                                   liveMediaControllerBottom, ArrayList<LiveMessageEntity>
                                                   liveMessageEntities, ArrayList<LiveMessageEntity>
                                                   otherLiveMessageEntities, int useSkin) {
        super(context, false);
        liveVideoActivity = (Activity) context;
        this.useSkin = useSkin;
        mView = initView();
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
        if (useSkin == HalfBodyLiveConfig.SKIN_TYPE_CH) {
            return R.layout.page_livevideo_message_halfbody_primary_cn;
        }
        return R.layout.page_livevideo_message_halfbody_primary;
    }

    @Override
    public void initData() {
        super.initData();
        tpkL_teampk_pkstate_root = mView.findViewById(R.id.tpkL_teampk_pkstate_root);
        iv_live_primary_class_kuangjia_img_normal = liveVideoActivity.findViewById(R.id.iv_live_primary_class_kuangjia_img_normal);
        setTeamPkRight();
    }

    private PrimaryKuangjiaImageView.OnSizeChange onSizeChange = new PrimaryKuangjiaImageView.OnSizeChange() {
        @Override
        public void onSizeChange(int width, int height) {
            float scale = (float) width / 1334f;
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tpkL_teampk_pkstate_root.getLayoutParams();
            int rightMargin = (int) (237 * scale) + (ScreenUtils.getScreenWidth() - width) / 2;
            int topMargin =  (ScreenUtils.getScreenHeight() - height) / 2 + (int) (21 * scale);
            boolean change = false;
            if (rightMargin != lp.rightMargin || topMargin != lp.topMargin) {
                lp.rightMargin = rightMargin;
                lp.topMargin = topMargin;
                LayoutParamsUtil.setViewLayoutParams(tpkL_teampk_pkstate_root, lp);
                change = true;
            }
            logger.d("setTeamPkRight:right=" + rightMargin + ",top=" + (ScreenUtils.getScreenHeight() - height) + "," + topMargin + ",change=" + change);
        }
    };

    private void setTeamPkRight() {
        iv_live_primary_class_kuangjia_img_normal.addSizeChange(onSizeChange);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        iv_live_primary_class_kuangjia_img_normal.removeSizeChange(onSizeChange);
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
