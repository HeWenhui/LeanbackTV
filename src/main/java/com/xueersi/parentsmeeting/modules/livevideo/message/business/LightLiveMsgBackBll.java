package com.xueersi.parentsmeeting.modules.livevideo.message.business;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.CountDownTimer;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.entity.LPWeChatEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.http.LightLiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.http.LightLiveHttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoLevel;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveBackMsgEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.message.pager.LightLiveBackMsgLandPager;
import com.xueersi.parentsmeeting.modules.livevideo.message.pager.LightLiveBackMsgPortPager;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ProjectName: xueersiwangxiao
 * @Package: com.xueersi.parentsmeeting.modules.livevideo.message.business
 * @ClassName: 轻直播回放聊天
 * @Description: java类作用描述
 * @Author: WangDe
 * @CreateDate: 2019/12/26 16:39
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/12/26 16:39
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class LightLiveMsgBackBll extends LiveBackBaseBll {


    private LightLiveBackMsgPortPager liveBackMsgPortPager;
    private LightLiveBackMsgLandPager liveBackMsgLandPager;
    /** 本次接口返回的所有数据*/
    ArrayList<LiveBackMsgEntity> liveMessageEntitiesAll;
    /** 本次接口返回剩余展示的数据*/
    ArrayList<LiveBackMsgEntity> liveMessageEntities;
    /** 是否第一次请求*/
    boolean isFirstRequest = true;
    /** 本次接口请求是否带有数据*/
    boolean isHasMessage = true;
    /** 判断是否接口请求中*/
    boolean isStartGetMessage = false;
    /** 定时器使用，每次最多请求30分钟数据*/
    private final long REQUEST_MSG_TIME = 30 * 60 * 1000;
    /** 前跳进度大于30S重新请求接口*/
    private final long MIN_POS = 30;
    /** 请求接口时间戳*/
    private long startTime;
    /** 重试次数*/
    AtomicInteger retry = new AtomicInteger();
    /** 上次回调播放进度*/
    private long lastPos;

    private long videoStartTime;
    /** 上次请求接口进度*/
    private long lastRequestPos;
    private final int tipType;
    private LightLiveHttpManager messageHttp;
    private LiveGetInfo liveGetInfo;

    public LightLiveMsgBackBll(Activity activity, LiveBackBll liveBackBll) {
        super(activity, liveBackBll);
        Intent intent = activity.getIntent();
        tipType = intent.getIntExtra("tipType",0);
//        EventBus.getDefault().register(this);

    }

    @Override
    public void initView() {
        if (mIsLand.get()){
            if (liveBackMsgLandPager == null){
                liveBackMsgLandPager = new LightLiveBackMsgLandPager(mContext);
            }
            ViewGroup view = (ViewGroup) liveBackMsgLandPager.getRootView().getParent();
            if (view != null){
                view.removeView(liveBackMsgLandPager.getRootView());
            }
            mRootViewBottom.addView(liveBackMsgLandPager.getRootView());
            liveBackMsgLandPager.onAttach();
//            liveBackMsgLandPager.justShowTeacher(justShowTeacher);
//            LiveVideoPoint liveVideoPoint = LiveVideoPoint.getInstance();
//            liveVideoPoint.addVideoSizeChangeAndCall(mContext, new LiveVideoPoint.VideoSizeChange() {
//                @Override
//                public void videoSizeChange(LiveVideoPoint liveVideoPoint) {
//                    RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) liveBackMsgLandPager.getRootView().getLayoutParams();
//                    param.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//                    param.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//                    int bottomMargin = liveVideoPoint.screenHeight - liveVideoPoint.y4;
//                    int rightMargin = liveVideoPoint.screenWidth - liveVideoPoint.x4;
//                    if (rightMargin >= 0){
//                        param.rightMargin = rightMargin + SizeUtils.Dp2Px(mContext,15);
//                    }
//                    if (bottomMargin >= 0){
//                        param.bottomMargin = bottomMargin + SizeUtils.Dp2Px(mContext,50);
//                    }
//                }
//            });
        } else {
            if(liveBackMsgPortPager == null){
                liveBackMsgPortPager = new LightLiveBackMsgPortPager(mContext, tipType == LPWeChatEntity.TEACHER_WECHAT);
            }
            ViewGroup view = (ViewGroup) liveBackMsgPortPager.getRootView().getParent();
            if (view != null){
                view.removeView(liveBackMsgPortPager.getRootView());
            }
            getLiveViewAction().addView(LiveVideoLevel.LEVEL_MES,liveBackMsgPortPager.getRootView());
        }
    }

    @Override
    public void setVideoLayout(LiveVideoPoint liveVideoPoint) {
        //监听页面布局变化动态设置 页面尺寸信息

    }

    @Override
    public void onCreate(VideoLivePlayBackEntity mVideoEntity, LiveGetInfo liveGetInfo, HashMap<String, Object> businessShareParamMap) {
        super.onCreate(mVideoEntity, liveGetInfo, businessShareParamMap);
        this.liveGetInfo = liveGetInfo;
        liveBackMsgLandPager = new LightLiveBackMsgLandPager(mContext);
        liveBackMsgPortPager = new LightLiveBackMsgPortPager(mContext, tipType == LPWeChatEntity.TEACHER_WECHAT);
        liveMessageEntitiesAll = new ArrayList<>();
        liveMessageEntities = new ArrayList<>();
        messageHttp = new LightLiveHttpManager(liveBackBll.getmHttpManager());
        liveBackMsgPortPager.setIGetLPInfo(lpInfo);
        liveBackMsgPortPager.setMessageStatus(messageStatus);
        videoStartTime = mVideoEntity.getGotoClassTime();
//        videoStartTime =1577261005;
    }

    @Override
    public void onPositionChanged(long position) {
        super.onPositionChanged(position);
        //出现回退进度将以展示当前进度时间点后的消息清掉
        if (lastPos > position && videoStartTime != 0) {
            liveBackMsgLandPager.removeOverMsg((videoStartTime + position) * 1000);
            liveBackMsgPortPager.removeOverMsg((videoStartTime + position) * 1000);
            //恢复回退的消息到待显示消息中
            long lastTime = (lastPos + videoStartTime) * 1000;
            long frontTime =( position + videoStartTime) * 1000;
            for (int i = liveMessageEntitiesAll.size() -1; i >= 0 ; i--) {
                long temp = liveMessageEntitiesAll.get(i).getId();
                if (temp > frontTime && temp < lastTime ) {
                    liveMessageEntities.add(0,liveMessageEntitiesAll.get(i));
                }
            }
        }
        //回退进度在上次请求接口之前重新请求回放数据
        if (lastPos  < lastRequestPos) {
            liveMessageEntities.clear();
            isFirstRequest = true;
            isHasMessage = true;
        }
        lastPos = position;
        //如果接口没数据了就没有新消息
        if (!isHasMessage) {
            return;
        }
        //未获取消息或者消息已全部展示完成
        if (videoStartTime != 0){
            if (liveMessageEntities.isEmpty()) {
                getLiveMsg(position);
            } else {
                showLiveMsg(position);
            }
        }
    }

    CountDownTimer timer = new CountDownTimer(REQUEST_MSG_TIME, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            isHasMessage = true;
        }
    };

    /**
     * 获取消息
     *
     * @param position
     */
    private void getLiveMsg(long position) {
        lastRequestPos = position;
        if (isFirstRequest) {
            isFirstRequest = false;
            startTime = (videoStartTime + position) * 1000;
        } else {
            if (!liveMessageEntitiesAll.isEmpty()) {
                startTime = liveMessageEntitiesAll.get(liveMessageEntitiesAll.size() - 1).getId() + 1;
                lastRequestPos = startTime - videoStartTime;
            } else {
                startTime = (videoStartTime + position) * 1000;
            }
        }
        //获取直播时历史消息
        if (!isStartGetMessage) {
            isStartGetMessage = true;

            messageHttp.getLiveBackMessage(liveGetInfo.getId(),String.valueOf(startTime), new HttpCallBack(false) {
                @Override
                public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                    LightLiveHttpResponseParser parser = new LightLiveHttpResponseParser();
                    ArrayList<LiveBackMsgEntity> entities = parser.parserBackMessageInfo(responseEntity);
                    isStartGetMessage = false;
                    retry.set(0);
                    if (!entities.isEmpty()) {
                        liveMessageEntitiesAll.clear();
                        liveMessageEntities.addAll(entities);
                        liveMessageEntitiesAll.addAll(liveMessageEntities);
                    } else {
                        isHasMessage = false;
                        //本次没获取到数据，过30分钟后再获取
                        if (timer != null) {
                            timer.start();
                        }
                    }
                    // 将请求接口这段时间产生的消息展示出来
                    for (int i = 0; i < liveMessageEntities.size(); i++) {
                        if (LiveBackMsgEntity.MESSAGE_TYPE.equals(liveMessageEntities.get(i).getType()) &&
                                startTime >= liveMessageEntities.get(i).getId()) {
                            liveBackMsgLandPager.addMsg(liveMessageEntities.get(i));
                            liveBackMsgPortPager.addMsg(liveMessageEntities.get(i));
                        }
                    }

                }

                @Override
                public void onPmFailure(Throwable error, String msg) {
                    super.onPmFailure(error, msg);
                    if (retry.getAndAdd(1) < 2) {
                        isStartGetMessage = false;
                    }
                }

                @Override
                public void onPmError(ResponseEntity responseEntity) {
                    super.onPmError(responseEntity);
                    if (retry.getAndAdd(1) < 2) {
                        isStartGetMessage = false;
                    }
                }
            });
        }
    }

    /**
     * 显示消息到消息列表
     *
     * @param position
     */
    private void showLiveMsg(long position) {
        List<LiveBackMsgEntity> tempList = new ArrayList<>();
        Iterator<LiveBackMsgEntity> it=liveMessageEntities.iterator();
        while (it.hasNext()){
            LiveBackMsgEntity entity = it.next();
            if ((videoStartTime + position) * 1000 >= entity.getId()) {
                if (LiveBackMsgEntity.MESSAGE_TYPE.equals(entity.getType())) {
                    logger.i("time: " + entity.getId() + "  msg:" + entity.getText());
                    tempList.add(entity);
                    liveBackMsgPortPager.addMsg(entity);
                    liveBackMsgLandPager.addMsg(entity);
                }
                it.remove();
            }else {
                break;
            }
        }
    }

    LightLiveBackMsgPortPager.IGetLPInfo lpInfo = new LightLiveBackMsgPortPager.IGetLPInfo() {

        @Override
        public void getLPWeChat(HttpCallBack callBack) {
            messageHttp.getWechatInfo(liveGetInfo.getId(),callBack);
        }
    };

    boolean justShowTeacher;
    LightLiveBackMsgPortPager.IMessageStatus messageStatus = new LightLiveBackMsgPortPager.IMessageStatus() {
        @Override
        public void clearMessage() {
            liveBackMsgLandPager.removeAllMsg();
        }

        @Override
        public void justShowTeacher(boolean isShow) {
            justShowTeacher = isShow;
            liveBackMsgLandPager.justShowTeacher(isShow);
        }
    };
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onStartPlayer() {
        super.onStartPlayer();
    }
}
