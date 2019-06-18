package com.xueersi.parentsmeeting.modules.livevideoOldIJK.videochat.page;//package com.xueersi.parentsmeeting.modules.livevideoOldIJK.page;
//
//import android.app.Activity;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.Drawable;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.Looper;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.View;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.example.licodeclient.LicodeConnector;
//import com.example.licodeclient.StreamDescription;
//import com.example.licodeclient.StreamDescriptionInterface;
//import com.example.licodeclient.VideoConnectorInterface;
//import com.xueersi.parentsmeeting.modules.livevideo.BuildConfig;
//import com.xueersi.parentsmeeting.modules.livevideo.R;
//import com.xueersi.common.base.BasePager;
//import com.xueersi.parentsmeeting.modules.livevideo.business.LicodeToken;
//import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBll;
//import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
//import com.xueersi.parentsmeeting.modules.livevideo.videochat.business.VideoChatBll;
//import com.xueersi.parentsmeeting.modules.livevideo.business.VideoChatInter;
//import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassmateEntity;
//import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
//import com.xueersi.parentsmeeting.modules.livevideo.widget.ArcProgressView;
//import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;
//import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;
//import com.xueersi.lib.framework.utils.NetWorkHelper;
//import com.xueersi.lib.imageloader.ImageLoader;
//import com.xueersi.lib.imageloader.SingleConfig;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * Created by Administrator on 2017/5/9.
// */
//
//public class LicodeVideoChatPager extends BasePager implements VideoChatInter {
//    String TAG = "LicodeVideoChatPager";
//    LicodeConnector mConnector;
//    LicodeConnector.PeerConnectionParameters peerConnectionParameters;
//    Activity activity;
//    String stuName;
//    String stuImg;
//    String stuId;
//    LiveBll liveBll;
//    LiveGetInfo getInfo;
//    private LogToFile mLogtf;
//    LinearLayout llChatContent;
//    Handler handler = new Handler(Looper.getMainLooper());
//    /**
//     * map of stream id -> video view
//     */
//    ConcurrentHashMap<String, View> mSurfaceViewRenderer = new ConcurrentHashMap<>();
//    BaseLiveMediaControllerBottom baseLiveMediaControllerBottom;
//    public boolean reconnect = true;
//    VideoChatBll videoChatBll;
//    int netWorkType;
//    boolean startRecord = false;
//    ArrayList<ClassmateEntity> classmateEntities;
//    String room;
//
//    public LicodeVideoChatPager(Activity activity, VideoChatBll videoChatBll, ArrayList<ClassmateEntity>
//            classmateEntities, LiveGetInfo getInfo, LiveBll liveBll, BaseLiveMediaControllerBottom
//                                        baseLiveMediaControllerBottom) {
//        this.activity = activity;
//        this.videoChatBll = videoChatBll;
//        this.classmateEntities = classmateEntities;
//        this.liveBll = ircState;
//        this.baseLiveMediaControllerBottom = baseLiveMediaControllerBottom;
//        this.getInfo = getInfo;
//        stuId = getInfo.getStuId();
//        stuName = getInfo.getStuName();
//        stuImg = getInfo.getStuImg();
//        mLogtf = new LogToFile(TAG, new File(Environment.getExternalStorageDirectory(), "parentsmeeting/log/" + TAG
//                + ".txt"));
//        mLogtf.clear();
//        netWorkType = NetWorkHelper.getNetWorkState(activity);
//        initView();
//        initData();
//    }
//
//    @Override
//    public View initView() {
//        mView = View.inflate(activity, R.layout.page_livevideo_videochat_licode, null);
//        llChatContent = (LinearLayout) mView.findViewById(R.id.ll_livevideo_chat_content);
//        int count = 5;
//        if (BuildConfig.DEBUG) {
//            count = 6;
//        }
//        for (int i = 0; i < count; i++) {
//            View video_layout = getActivity().getLayoutInflater().inflate(R.layout.item_videochat_people,
//                    llChatContent, false);
//            TextView tvPeopleName = (TextView) video_layout.findViewById(R.id.tv_livevideo_chat_people_name);
////            tvPeopleName.setText("" + i);
//            llChatContent.addView(video_layout);
//        }
//        return mView;
//    }
//
//    private StartRecordRun startRecordRun;
//
//    /** 重连 */
//    private class StartRecordRun implements Runnable {
//        String method;
//
//        @Override
//        public void run() {
//            startRecordRun = null;
//            startRecord(method, room);
//        }
//    }
//
//    private void restart(String method) {
//        if (startRecordRun == null) {
//            startRecordRun = new StartRecordRun();
//            startRecordRun.method = method;
//        } else {
//            startRecordRun.method = "old:" + method;
//            handler.removeCallbacks(startRecordRun);
//        }
//        handler.postDelayed(startRecordRun, 1000);
//    }
//
//    @Override
//    public void startRecord(String method, String room) {
//        this.room = room;
//        Log.i(TAG, "startRecord:method=" + method + ",netWorkType=" + netWorkType + ",reconnect=" + reconnect);
//        if (!reconnect) {
//            return;
//        }
//        if (netWorkType == NetWorkHelper.NO_NETWORK) {
//            startRecord = true;
//            return;
//        }
//        mConnector = new LicodeConnector();
//        JSONObject attributesObj = new JSONObject();
//        try {
////            imagUrl : "http://h.xesimg.com/head/11022/small.jpg?1"
////            name   :  "111"
////            role  :  "presenter"
////            userId  :  "11023"
//            attributesObj.put("role", "presenter");
//            attributesObj.put("name", stuName);
//            attributesObj.put("imagUrl", stuImg);
//            attributesObj.put("userId", stuId);
//            attributesObj.put("type", "public");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        peerConnectionParameters = new LicodeConnector.PeerConnectionParameters(false, 320, 240, 10, attributesObj);
//        mConnector.init(activity, stuName, peerConnectionParameters);
//        observer = new XesRoomObserver(mConnector);
//        mConnector.addObserver(observer);
//        liveBll.getToken(new LicodeToken() {
//            @Override
//            public void onToken(String token) {
//                mLogtf.d("onToken:token:reconnect=" + reconnect);
//                if (reconnect) {
//                    mConnector.connect(token);
//                }
//            }
//
//            @Override
//            public void onError(Throwable ex) {
//                restart("onError");
////                    videoChatBll.showToast("连接失败");
//            }
//        });
//    }
//
//    public void stopRecord() {
//        Log.i(TAG, "stopRecord:reconnect=" + reconnect);
//        reconnect = false;
//        mSurfaceViewRenderer.clear();
//        if (mConnector != null) {
//            mConnector.disconnect("stopRecord");
//        }
//    }
//
//    @Override
//    public void updateUser(boolean classmateChange, ArrayList<ClassmateEntity> classmateEntities) {
//        this.classmateEntities = classmateEntities;
//        if (classmateChange) {
//            String msg = "updateUser:" + classmateEntities.size();
//            int nowCount = 0;
//            for (int i = 0; i < llChatContent.getChildCount(); i++) {
//                View video_layout = llChatContent.getChildAt(i);
//                if (video_layout.getTag() != null) {
//                    nowCount++;
//                    StreamDescription stream2 = (StreamDescription) video_layout.getTag();
//                    if ("admin".equals(stream2.getRole())) {
//                        msg += ",admin";
//                        continue;
//                    }
//                    if (getInfo.getStuId().equals(stream2.getUserId())) {
//                        msg += ",me";
//                        continue;
//                    }
//                    String userId = stream2.getUserId();
//                    boolean contain = false;
//                    for (int j = 0; j < classmateEntities.size(); j++) {
//                        ClassmateEntity classmateEntity = classmateEntities.get(j);
//                        if (userId.equals(classmateEntity.getId())) {
//                            contain = true;
//                            break;
//                        }
//                    }
//                    if (!contain) {
//                        video_layout.findViewById(R.id.iv_livevideo_chat_people_head).setVisibility(View.INVISIBLE);
//                        video_layout.findViewById(R.id.iv_livevideo_chat_people_empty_head).setVisibility(View.VISIBLE);
//                        TextView tvPeopleName = (TextView) video_layout.findViewById(R.id
//                                .tv_livevideo_chat_people_name);
//                        tvPeopleName.setText("");
//                        llChatContent.removeView(video_layout);
//                        llChatContent.addView(video_layout);
//                        video_layout.setTag(null);
//                        i--;
//                        mConnector.unsubscribe(stream2.getId());
//                    }
//                    msg += ",contain=" + contain;
//                }
//            }
//            msg += ",nowCount=" + nowCount;
//            mLogtf.d(msg);
//        }
//    }
//
//    @Override
//    public void initData() {
//        final LogToFile mLogtf = new LogToFile("LicodeConnector", new File(Environment.getExternalStorageDirectory(),
//                "parentsmeeting/log/LicodeConnector.txt"));
//        LicodeConnector.logCallback = new LicodeConnector.LogCallback() {
//            @Override
//            public void i(String tag, String msg) {
//                mLogtf.i(msg);
//            }
//
//            @Override
//            public void e(String tag, String msg, Throwable tr) {
//                mLogtf.e(msg, tr);
//            }
//        };
//    }
//
//    protected View makeVideoView(String streamId, StreamDescriptionInterface stream) {
//        mLogtf.i("makeVideoView:streamId=" + streamId);
//        StreamDescription streamDescription = (StreamDescription) stream;
//        if ("admin".equals(streamDescription.getRole())) {
//            mLogtf.i("makeVideoView:admin");
//            if (!BuildConfig.DEBUG) {
//                return null;
//            }
//        }
//        if (mSurfaceViewRenderer.containsKey(streamId)) {
//            return mSurfaceViewRenderer.get(streamId);
//        } else if (getActivity() != null) {
//            View video_layout = null;
//            boolean isMe = false;
//            for (int i = 0; i < llChatContent.getChildCount(); i++) {
//                View child = llChatContent.getChildAt(i);
//                if (child.getTag() != null) {
//                    StreamDescription stream2 = (StreamDescription) child.getTag();
//                    if (stream2.getUserId().equals(((StreamDescription) stream).getUserId())) {
//                        video_layout = child;
//                        if (stream2.getUserId().endsWith(getInfo.getStuId())) {
//                            isMe = true;
//                        }
//                        mLogtf.i("makeVideoView:old:isMe=" + isMe);
//                        break;
//                    }
//                }
//            }
//            if (video_layout == null) {
//                for (int i = 0; i < llChatContent.getChildCount(); i++) {
//                    View child = llChatContent.getChildAt(i);
//                    if (child.getTag() == null) {
//                        video_layout = child;
//                        mLogtf.i("makeVideoView:new");
//                        break;
//                    }
//                }
//            }
//            if (video_layout == null) {
//                mLogtf.i("makeVideoView:null");
//                return null;
//            }
//            final ArcProgressView arcProgressView = (ArcProgressView) video_layout.findViewById(R.id
//                    .iv_livevideo_chat_people_head);
//            TextView tvPeopleName = (TextView) video_layout.findViewById(R.id.tv_livevideo_chat_people_name);
//            if (stream.isLocal() || isMe) {
//                tvPeopleName.setText("我");
//            } else {
//                tvPeopleName.setText("" + streamDescription.getNick());
//            }
//            video_layout.setTag(stream);
//            mSurfaceViewRenderer.put(streamId, video_layout);
//            arcProgressView.setVisibility(View.VISIBLE);
//            tvPeopleName.setVisibility(View.VISIBLE);
//            video_layout.findViewById(R.id.iv_livevideo_chat_people_empty_head).setVisibility(View.GONE);
//            final String imagUrl = streamDescription.getImagUrl();
//            if (!TextUtils.isEmpty(imagUrl)) {
//                ImageLoader.with(activity).load(streamDescription.getImagUrl()).asBitmap(new SingleConfig
//                        .BitmapListener() {
//
//                    @Override
//                    public void onSuccess(Drawable drawable) {
//                        Log.i(TAG, "onResourceReady:imagUrl=" + imagUrl);
//                        if (drawable instanceof BitmapDrawable) {
//                            BitmapDrawable glideBitmapDrawable = (BitmapDrawable) drawable;
//                            arcProgressView.setCenterBitmap(glideBitmapDrawable.getBitmap());
//                        }
//                    }
//
//                    @Override
//                    public void onFail() {
//                        Log.i(TAG, "onLoadFailed:imagUrl=" + imagUrl);
//                    }
//                });
//            }
//            return video_layout;
//        }
//        return null;
//    }
//
//    public Activity getActivity() {
//        return activity;
//    }
//
//    /**
//     * publish
//     */
//    protected void startPublish() {
//        activity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (mConnector != null && mConnector.isConnected()) {
//                    mConnector.publish();
//                }
//            }
//        });
//    }
//
//    @Override
//    public void onNetWorkChange(int netWorkType) {
//        this.netWorkType = netWorkType;
//        if (netWorkType != NetWorkHelper.NO_NETWORK) {
//            Log.i(TAG, "onNetWorkChange:startRecord=" + startRecord);
//            if (startRecord) {
//                startRecord = false;
//                restart("onNetWorkChange");
//            }
//        }
//    }
//
//    VideoConnectorInterface.RoomObserver observer;
//
//    class XesRoomObserver implements VideoConnectorInterface.RoomObserver {
//        LicodeConnector connector;
//
//        XesRoomObserver(LicodeConnector mConnector) {
//            this.connector = mConnector;
//        }
//
//        private void restart(String method) {
//            if (connector == mConnector) {
//                LicodeVideoChatPager.this.restart(method);
//            } else {
//                mLogtf.d("restart:old XesRoomObserver");
//            }
//        }
//
//        @Override
//        public void onTokenError(String createTokenError) {
//            logger.i( "onTokenError:createTokenError=" + createTokenError);
//        }
//
//        @Override
//        public void onRoomConnected(Map<String, StreamDescriptionInterface> streamList) {
//            logger.i( "onRoomConnected:reconnect=" + reconnect);
//            if (reconnect) {
//                startPublish();
//            } else {
//                if (mConnector != null && mConnector.isConnected()) {
//                    mConnector.disconnect("onRoomConnected");
//                }
//            }
//        }
//
//        @Override
//        public void onRoomConnectedFail(Exception err) {
//            restart("onRoomConnectedFail");
//        }
//
//        @Override
//        public void onRoomDisconnected() {
//            mLogtf.d("onRoomDisconnected");
//        }
//
//        @Override
//        public void onDisconnect(boolean fromRemote, Exception e) {
//            mLogtf.d("onDisconnect:reconnect=" + reconnect + ",fromRemote=" + fromRemote);
//            if (fromRemote) {
//                reconnect = false;
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        videoChatBll.stopRecord();
//                    }
//                });
//            } else {
//                mConnector.disconnect("onDisconnect");
//                restart("onDisconnect");
//            }
//        }
//
//        @Override
//        public void onPublish(final String streamId, final StreamDescriptionInterface stream) {
//            mLogtf.d("onPublish:streamId=" + streamId);
//            activity.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    makeVideoView(streamId, stream);
//                }
//            });
//        }
//
//        @Override
//        public void onPublishAllowed() {
//
//        }
//
//        @Override
//        public void onStreamAdded(final StreamDescriptionInterface stream) {
//            StreamDescription streamDescription = (StreamDescription) stream;
//            boolean content = false;
//            if ("admin".equals(streamDescription.getRole())) {
//                content = true;
//            } else {
//                if (streamDescription.getUserId().equals(getInfo.getStuId())) {
//                    content = false;
//                } else {
//                    for (ClassmateEntity entity : classmateEntities) {
//                        if (entity.getId().equals(streamDescription.getUserId())) {
//                            content = true;
//                            break;
//                        }
//                    }
//                }
//            }
//            mLogtf.d("onStreamAdded:" + streamDescription.getRole() + ",userid=" + streamDescription.getUserId() + "," +
//                    "content=" + content);
//            if (!content) {
//                return;
//            }
//            activity.runOnUiThread(new Runnable() {
//
//                @Override
//                public void run() {
//                    mConnector.subscribe("onStreamAdded", stream);
//                }
//            });
//        }
//
//        @Override
//        public void onStreamMediaAvailable(final StreamDescriptionInterface stream) {
//            logger.i( "onStreamMediaAvailable");
//            activity.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    makeVideoView(stream.getId(), stream);
//                }
//            });
//        }
//
//        @Override
//        public void onStreamRemoved(final StreamDescriptionInterface stream) {
//            StreamDescription streamDescription = (StreamDescription) stream;
//            mLogtf.d("onStreamRemoved:role" + streamDescription.getRole() + ",userid=" + streamDescription.getUserId());
//            activity.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    String streamId = stream.getId();
//                    View view = mSurfaceViewRenderer.get(streamId);
//                    if (view != null) {
//                        mSurfaceViewRenderer.remove(streamId);
////                        llChatContent.removeView(view);
////                        view.setTag(null);
////                        view.findViewById(R.id.iv_livevideo_chat_people_head).setVisibility(View.INVISIBLE);
////                        view.findViewById(R.id.iv_livevideo_chat_people_empty_head).setVisibility(View.VISIBLE);
////                        view.findViewById(R.id.tv_livevideo_chat_people_name).setVisibility(View.GONE);
//                        mConnector.destroy("onStreamRemoved", stream);
//                    }
//                }
//            });
//        }
//
//        @Override
//        public void onStreamData(String message, StreamDescriptionInterface stream) {
//
//        }
//
//        @Override
//        public void onRequestRefreshToken() {
//
//        }
//
//        @Override
//        public void audioOutputLevel(final String streamId, final String value) {
////            Log.i(TAG, "audioOutputLevel:streamId=" + streamId + ",value=" + value);
//            activity.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    View view = mSurfaceViewRenderer.get(streamId);
//                    if (view != null) {
//                        ArcProgressView arcProgressView = (ArcProgressView) view.findViewById(R.id
//                                .iv_livevideo_chat_people_head);
//                        int intValue = Math.abs(Integer.parseInt(value));
//                        if (arcProgressView.getMax() < intValue) {
//                            arcProgressView.setMax(intValue);
//                        }
//                        arcProgressView.setProgress(intValue);
//                    }
//                }
//            });
//        }
//    }
//}
