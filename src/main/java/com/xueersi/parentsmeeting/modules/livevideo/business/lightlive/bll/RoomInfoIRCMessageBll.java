package com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.bll;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.xueersi.common.base.XrsCrashReport;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.IRCConnection;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XesAtomicInteger;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.MessageAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.User;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.pager.LightLiveRoomInfoPager;
import com.xueersi.parentsmeeting.modules.livevideo.message.config.LiveMessageConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @ProjectName: xueersiwangxiao
 * @Package: com.xueersi.parentsmeeting.modules.livevideo.business.lightlive
 * @ClassName: RoomInfoIRCMessageBll
 * @Description: java类作用描述
 * @Author: WangDe
 * @CreateDate: 2019/11/23 16:19
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/11/23 16:19
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class RoomInfoIRCMessageBll extends LiveBaseBll implements MessageAction {

    private LinearLayout infoLayout;
    private LightLiveRoomInfoPager lightLiveRoomInfoPager;
    private int num;
    private List<String> users = new ArrayList<>();
    /** 讨论人数 */
    protected XesAtomicInteger peopleCount = new XesAtomicInteger(0);
    /** 公告*/
    private String mNotice;
    /** 是否增加人数*/
    private boolean chatSwitch;

    public RoomInfoIRCMessageBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
        lightLiveRoomInfoPager = new LightLiveRoomInfoPager(mContext);
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        if (getInfo != null){
            if (!getInfo.getGentlyNotice().isEmpty()){
                mNotice = "公告: " + getInfo.getGentlyNotice();
                if(lightLiveRoomInfoPager != null){
                    lightLiveRoomInfoPager.setTvNotice(mNotice);
                }
            }
           chatSwitch = getInfo.getChatSwitch() == 1;
        }

    }

    @Override
    public void initView() {
        infoLayout = mContentView.findViewById(R.id.ll_live_room_info);
        if(!mIsLand.get()){
            if (lightLiveRoomInfoPager != null){
                infoLayout.removeAllViews();
                infoLayout.setVisibility(View.VISIBLE);
                infoLayout.addView(lightLiveRoomInfoPager.getRootView());
                ViewGroup.LayoutParams params = lightLiveRoomInfoPager.getRootView().getLayoutParams();
                params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
                params.height = SizeUtils.Dp2Px(mContext,38);
                lightLiveRoomInfoPager.getRootView().setLayoutParams(params);
                lightLiveRoomInfoPager.setTvNotice(mNotice);
            }
        }else {
            infoLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreate(HashMap<String, Object> data) {
        super.onCreate(data);
    }

    @Override
    public void onStartConnect() {

    }

    @Override
    public void onConnect(IRCConnection connection) {

    }

    @Override
    public void onRegister() {

    }

    @Override
    public void onDisconnect(IRCConnection connection, boolean isQuitting) {

    }

    @Override
    public void onMessage(String target, String sender, String login, String hostname, String text) {

    }

    @Override
    public void onPrivateMessage(boolean isSelf, String sender, String login, String hostname, String target, String message) {

    }

    @Override
    public void onChannelInfo(String channel, int userCount, String topic) {

    }

    @Override
    public void onUserList(String channel, User[] users) {
        this.users.clear();
        for (User user : users) {
            String _nick = user.getNick();
            if (_nick != null && (_nick.startsWith(LiveMessageConfig.TEACHER_PREFIX)
                    || _nick.startsWith(LiveMessageConfig.COUNTTEACHER_PREFIX))){
                continue;
            }
            this.users.add(user.getNick());
        }
        peopleCount.set(this.users.size(), new Exception());
        lightLiveRoomInfoPager.setTvCount("在线"+ getShowCount() + "人");
    }

    @Override
    public void onJoin(String target, String sender, String login, String hostname) {
        if (sender!= null && !users.contains(sender) && !sender.startsWith(LiveMessageConfig.TEACHER_PREFIX)
                && !sender.startsWith(LiveMessageConfig.COUNTTEACHER_PREFIX) ) {
            XrsCrashReport.d(TAG, "onJoin:sender=" + sender + ",get=" + peopleCount.get()+ ",users=" + users.size() + ",this=" + this);
            peopleCount.set(peopleCount.get() + 1, new Exception(sender));
            users.add(sender);
            lightLiveRoomInfoPager.setTvCount("在线"+ getShowCount() + "人");
        }
    }

    @Override
    public void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason) {
        if (sourceNick != null && users.contains(sourceNick) && !sourceNick.startsWith(LiveMessageConfig.TEACHER_PREFIX)
                && !sourceNick.startsWith(LiveMessageConfig.COUNTTEACHER_PREFIX)) {
            boolean remove = users.remove(sourceNick);
            XrsCrashReport.d(TAG, "onQuit:sourceNick=" + sourceNick + ",get=" + peopleCount.get() + ",remove=" + remove + ",users=" + users.size() + ",this=" + this);
            if (remove) {
                peopleCount.set(peopleCount.get() - 1, new Exception(sourceNick));
                lightLiveRoomInfoPager.setTvCount("在线"+ getShowCount() + "人");
            }
        }
    }

    @Override
    public void onKick(String target, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason) {

    }

    @Override
    public void onUnknown(String line) {

    }

    private int getShowCount(){
        int count;
        if (chatSwitch){
            count = (int)Math.ceil(peopleCount.get() * 1.25) + 18;
        }else {
            count = peopleCount.get();
        }
        return count;
    }
}
