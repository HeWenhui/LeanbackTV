//package com.xueersi.parentsmeeting.modules.livevideoOldIJK.page;
//
//import android.content.Context;
//import android.view.View;
//
//import com.xueersi.parentsmeeting.modules.livevideo.R;
//import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBll;
//import com.xueersi.parentsmeeting.modules.livevideo.business.VideoChatInter;
//import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassmateEntity;
//import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
//import com.xueersi.xesalib.utils.log.Loger;
//import com.xueersi.xesalib.utils.network.NetWorkHelper;
//
//import java.util.ArrayList;
//
///**
// * Created by linyuqiang on 2017/5/8.
// * 网页接麦
// * 暂时没用
// */
//public class VideoChatPager extends BaseWebviewPager implements VideoChatInter {
//    LiveBll liveBll;
//    LiveGetInfo getInfo;
//    int netWorkType;
//    boolean isFail = false;
//    String url;
//
//    public VideoChatPager(Context context, LiveBll liveBll, LiveGetInfo getInfo) {
//        super(context);
//        this.liveBll = liveBll;
//        this.getInfo = getInfo;
//        netWorkType = NetWorkHelper.getNetWorkState(context);
//        url = "https://test-rtc.xesimg.com:12443/room?id=x_" + liveBll.mLiveType + "_" + getInfo.getId()
//                + "&userId=" + getInfo.getStuId() + "&name=" + getInfo.getStuName()
//                + "&role=presenter&url=" + getInfo.getStuImg() + "&cfrom=android&probeNet=1";
//        initWebView();
//        setErrorTip("接麦失败，请重试");
//        setLoadTip("正在接麦，请稍候");
//        initData();
//    }
//
//    @Override
//    public View initView() {
//        final View view = View.inflate(mContext, R.layout.page_livevideo_videochat_webview, null);
//        view.findViewById(R.id.bt_livevideo_chat_webview_refresh).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                loadUrl(url);
//            }
//        });
//        return view;
//    }
//
//    @Override
//    public void startRecord(String method, String room, String nonce) {
////        https://test-rtc.xesimg.com:12443/room?id=x_3_40233&userId=1434&name=aa&role=admin&url=http://head03.xesimg.com/0/1434.jpg
//        logger.i( "startRecord:url=" + url);
//        loadUrl(url);
//    }
//
//    @Override
//    public void stopRecord() {
//        wvSubjectWeb.post(new Runnable() {
//            @Override
//            public void run() {
//                wvSubjectWeb.destroy();
//            }
//        });
//    }
//
//    @Override
//    public void updateUser(boolean classmateChange, ArrayList<ClassmateEntity> classmateEntities) {
//
//    }
//
//    @Override
//    public void initData() {
//        super.initData();
//    }
//
//    @Override
//    public void onNetWorkChange(int netWorkType) {
//        this.netWorkType = netWorkType;
//        logger.i( "onNetWorkChange:netWorkType=" + netWorkType + ",isFail=" + isFail);
//        if (netWorkType == NetWorkHelper.NO_NETWORK) {
//            isFail = true;
//        } else {
//            if (isFail) {
//                isFail = false;
//                loadUrl(url);
//            }
//        }
//    }
//}
