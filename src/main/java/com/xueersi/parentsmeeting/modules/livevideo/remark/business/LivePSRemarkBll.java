//package com.xueersi.parentsmeeting.modules.livevideo.remark.business;
//
//import android.animation.AnimatorSet;
//import android.animation.ObjectAnimator;
//import android.app.Activity;
//import android.content.Context;
//import android.database.DataSetObserver;
//import android.graphics.Bitmap;
//import android.graphics.Color;
//import android.graphics.Rect;
//import android.os.CountDownTimer;
//import android.os.Environment;
//import android.text.TextUtils;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.tencent.bugly.crashreport.CrashReport;
//import com.xueersi.common.base.AbstractBusinessDataCallBack;
//import com.xueersi.common.config.AppConfig;
//import com.xueersi.common.http.HttpCallBack;
//import com.xueersi.common.http.ResponseEntity;
//import com.xueersi.common.permission.XesPermission;
//import com.xueersi.common.permission.config.PermissionConfig;
//import com.xueersi.common.sharedata.ShareDataManager;
//import com.xueersi.component.cloud.XesCloudUploadBusiness;
//import com.xueersi.component.cloud.config.CloudDir;
//import com.xueersi.component.cloud.config.XesCloudConfig;
//import com.xueersi.component.cloud.entity.CloudUploadEntity;
//import com.xueersi.component.cloud.entity.XesCloudResult;
//import com.xueersi.component.cloud.listener.XesStsUploadListener;
//import com.xueersi.lib.framework.are.ContextManager;
//import com.xueersi.lib.framework.utils.SizeUtils;
//import com.xueersi.lib.framework.utils.XESToastUtils;
//import com.xueersi.lib.framework.utils.image.ImageUtils;
//import com.xueersi.lib.imageloader.ImageLoader;
//import com.xueersi.lib.log.LoggerFactory;
//import com.xueersi.lib.log.logger.Logger;
//import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoPointEntity;
//import com.xueersi.parentsmeeting.module.videoplayer.media.MediaController2;
//import com.xueersi.parentsmeeting.module.videoplayer.media.PlayerService;
//import com.xueersi.parentsmeeting.module.videoplayer.ps.PSIJK;
//import com.xueersi.parentsmeeting.modules.livevideo.R;
//import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
//import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
//import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
//import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
//import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
//import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
//import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;
//import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveTextureView;
//import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveVideoView;
//import com.xueersi.ui.adapter.AdapterItemInterface;
//import com.xueersi.ui.adapter.CommonAdapter;
//import com.xueersi.ui.dialog.VerifyCancelAlertDialog;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import tv.danmaku.ijk.media.player.FrameInfo;
//
//import static com.xueersi.common.business.sharebusiness.config.LocalCourseConfig.CATEGORY_ENGLISH_H5COURSE_WARE;
//import static com.xueersi.common.business.sharebusiness.config.LocalCourseConfig.CATEGORY_EXAM;
//import static com.xueersi.common.business.sharebusiness.config.LocalCourseConfig.CATEGORY_H5COURSE_WARE;
//import static com.xueersi.common.business.sharebusiness.config.LocalCourseConfig.CATEGORY_QUESTION;
//import static com.xueersi.common.business.sharebusiness.config.LocalCourseConfig.CATEGORY_REDPACKET;
//
////import tv.danmaku.ijk.media.player.PSIJK;
//
///**
// * Created by Tang on 2018/3/5.
// */
//
//public class LivePSRemarkBll {
//    private Context mContext;
//    private PlayerService mPlayerService;
//    private String TAG = "LivePSRemarkBll";
//    Logger logger = LoggerFactory.getLogger(TAG);
//    private Timer mTimer;
//    private long offSet;
//    private BaseLiveMediaControllerBottom mLiveMediaControllerBottom;
//    private long sysTimeOffset;
//    private int displayHeight;
//    //    private int displayWidth;
//    private double videoWidth;
//    private LiveHttpManager mHttpManager;
//    private XesCloudUploadBusiness mCloudUploadBusiness;
//    private RelativeLayout bottom;
//    private List<VideoPointEntity> mList;
//    private ListView lvPoints;
//    private LinearLayout llPoints;
//    private RelativeLayout rlMask;
//    private CommonAdapter mAdapter;
//    private MediaController2 mController;
//    private AbstractBusinessDataCallBack mCallBack;
//    private String liveId;
//    private int markNum = 0;
//    private int questionNum = 0;
//    private int englishH5Num = 0;
//    private int redPackNum = 0;
//    private int examNum = 0;
//    private VerifyCancelAlertDialog mDialog;
//    private boolean isVideoReady;
//    private boolean isClassReady;
//    private boolean isOnChat;
//    private boolean isMarking;
//    private boolean isCounting;
//    private LiveAndBackDebug mLiveAndBackDebug;
//    private View vTips;
//    private ImageView ivTipsIcon;
//    private TextView tvTipsContent;
//    private boolean isGaosan;
//    public static final int MARK_TYPE_QUESTION = 101;
//    public static final int MARK_TYPE_INCLUDE = 102;
//    public static final int MARK_TYPE_HIGH_MARK = 103;
//    public static final int MARK_TYPE_PRACTICE = 104;
//    public static final int MARK_TYPE_TEACHER_INCLUDE = 112;
//    public static final int MARK_TYPE_TEACHER_HIGH_MARK = 113;
//    public static final int MARK_TYPE_TEACHER_PRACTICE = 114;
//    private HashMap<Integer, Integer> countMap = new HashMap<>();
//    LogToFile logToFile;
//
//    public LivePSRemarkBll(Context context, PlayerService playerService) {
//        mContext = context;
//        logToFile = new LogToFile(context, TAG);
//        mPlayerService = playerService;
//        mLiveAndBackDebug = ProxUtil.getProxUtil().get(mContext, LiveAndBackDebug.class);
//        initData();
//    }
//
//    public void setLiveMediaControllerBottom(BaseLiveMediaControllerBottom liveMediaControllerBottom) {
//        mLiveMediaControllerBottom = liveMediaControllerBottom;
//
//    }
//
//    public void initData() {
//        setVideoReady(false);
//        TimerTask task = new TimerTask() {
//            @Override
//            public void run() {
//                if (mPlayerService.getPlayer() == null) {
//                    return;
//                }
//                long tcpSpeed;
//                float vdfps;
//                try {
//                    tcpSpeed = mPlayerService.getPlayerInfo().mTcpSpeed;
//                    vdfps = mPlayerService.getPlayerInfo().mDecoderfps;
//                } catch (Exception e) {
//                    return;
//                }
//                if (mLiveMediaControllerBottom == null) {
//                    return;
//                }
//                if (Math.round(vdfps) == 12) {
//                    setVideoOffset(0);
//                    //mTimer.cancel();
//
//
//                }
//
//            }
//        };
//        if (mTimer != null) {
//            mTimer.cancel();
//        }
//        mTimer = new Timer();
//        mTimer.schedule(task, 1000, 1000);
//        mCloudUploadBusiness = new XesCloudUploadBusiness(mContext);
//    }
//
//    private void setVideoOffset(long time) {
//        if (mPlayerService.getPlayer() == null) {
//            return;
//        }
//        FrameInfo frameInfo = ((PSIJK) mPlayerService.getPlayer()).native_getFrameInfo();
//        if (time == 0) {
//            offSet = System.currentTimeMillis() / 1000 + sysTimeOffset - frameInfo.pkt / 1000;
//        } else {
//            offSet = time - frameInfo.pkt / 1000;
//        }
//        logger.i("nowtime  " + frameInfo.nowTime + "   dts     " + frameInfo.pkt_dts
//                + "   pkt   " + frameInfo.pkt + "  cache:" + ((PSIJK) mPlayerService.getPlayer()).getVideoCachedDuration()
//                + " systime:" + (System.currentTimeMillis() / 1000 + sysTimeOffset) + "   nettime:" + time);
//        //setBtEnable(true);
//        setVideoReady(true);
//        mTimer.cancel();
//        mLiveMediaControllerBottom.getBtMark().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(final View v) {
//                if (!XesPermission.checkPermissionNoAlert(mContext, PermissionConfig.PERMISSION_CODE_STORAGE)) {
//                    return;
//                }
//                if (isMarking) {
//                    logToFile.d("getBtMark:isMarking=true");
//                    return;
//                }
//                if (mLiveMediaControllerBottom.getvMarkGuide() != null && mLiveMediaControllerBottom.getvMarkGuide().getVisibility() == View.VISIBLE) {
//                    mLiveMediaControllerBottom.getvMarkGuide().setVisibility(View.GONE);
//                }
//                if (isGaosan) {
//                    logToFile.d("getBtMark:MarkPopMenu=" + mLiveMediaControllerBottom.getLlMarkPopMenu());
//                    if (mLiveMediaControllerBottom.getLlMarkPopMenu() != null) {
//                        mLiveMediaControllerBottom.getLlMarkPopMenu().setVisibility(View.VISIBLE);
//                        if (mLiveMediaControllerBottom.getSwitchFlowView() != null) {
//                            mLiveMediaControllerBottom.getSwitchFlowView().setSwitchFlowPopWindowVisible(false);
//                        }
//                    }
//                } else {
//
//                    if (mLiveMediaControllerBottom.getLlMarkPopMenu() != null) {
//                        mLiveMediaControllerBottom.getLlMarkPopMenu().setVisibility(View.GONE);
//                    }
//                    final LiveTextureView liveTextureView = (LiveTextureView) ((Activity) mContext).findViewById(R.id.ltv_course_video_video_texture);
//
//                    if (liveTextureView == null) {
//                        return;
//                    }
//                    if (mPlayerService.getPlayer() == null) {
//                        markFail("fail1");
//                        return;
//                    }
//                    isMarking = true;
//                    final LiveVideoView liveVideoView = (LiveVideoView) ((Activity) mContext).findViewById(R.id.vv_course_video_video);
////                liveVideoView.setVisibility(View.INVISIBLE);
//                    ((PSIJK) mPlayerService.getPlayer()).setSurface(liveTextureView.surface);
//                    v.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (mPlayerService.getPlayer() == null) {
//                                markFail("fail2");
//                                return;
//                            }
//                            ((PSIJK) mPlayerService.getPlayer()).setDisplay(liveVideoView.getSurfaceHolder());
//                            v.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    if (mPlayerService.getPlayer() == null) {
//                                        markFail("fail3");
//                                        return;
//                                    }
//                                    ((PSIJK) mPlayerService.getPlayer()).setSurface(liveTextureView.surface);
//                                    v.postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            Bitmap bitmap = liveTextureView.getBitmap();
//                                            if (bitmap == null) {
//                                                markFail("fail4");
//                                                return;
//                                            }
//                                            bitmap = Bitmap.createBitmap(bitmap, 0, 0, (int) videoWidth, displayHeight);
//                                            bitmap = Bitmap.createScaledBitmap(bitmap, 320, 240, true);
//                                            File saveDir = new File(Environment.getExternalStorageDirectory(), "parentsmeeting/save");
//                                            if (!saveDir.exists()) {
//                                                saveDir.mkdirs();
//                                            }
//                                            File file = new File(saveDir, "" + System.currentTimeMillis() + ".png");
//                                            ImageUtils.save(bitmap, file, Bitmap.CompressFormat.JPEG);
//                                            reMark(file, "");
//                                            ((PSIJK) mPlayerService.getPlayer()).setDisplay(liveVideoView.getSurfaceHolder());
//                                        }
//                                    }, 100);
//                                }
//                            }, 100);
//                        }
//                    }, 100);
//                }
//            }
//        });
//        if (isGaosan && mLiveMediaControllerBottom.getLlMarkPopMenu() != null) {
//            for (int i = 0; i < mLiveMediaControllerBottom.getLlMarkPopMenu().getChildCount(); i++) {
//                mLiveMediaControllerBottom.getLlMarkPopMenu().getChildAt(i).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(final View v) {
//
//                        mLiveMediaControllerBottom.getLlMarkPopMenu().setVisibility(View.GONE);
//                        final LiveTextureView liveTextureView = (LiveTextureView) ((Activity) mContext).findViewById(R.id.ltv_course_video_video_texture);
//
//                        if (liveTextureView == null) {
//                            logToFile.d("MarkPopMenu.onClick:liveTextureView=null");
//                            return;
//                        }
//                        if (mPlayerService.getPlayer() == null) {
//                            markFail("fail5");
//                            return;
//                        }
//                        isMarking = true;
//                        final LiveVideoView liveVideoView = (LiveVideoView) ((Activity) mContext).findViewById(R.id.vv_course_video_video);
////                liveVideoView.setVisibility(View.INVISIBLE);
//                        ((PSIJK) mPlayerService.getPlayer()).setSurface(liveTextureView.surface);
//                        v.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (mPlayerService.getPlayer() == null) {
//                                    markFail("fail6");
//                                    return;
//                                }
//                                ((PSIJK) mPlayerService.getPlayer()).setDisplay(liveVideoView.getSurfaceHolder());
//                                v.postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        if (mPlayerService.getPlayer() == null) {
//                                            markFail("fail7");
//                                            return;
//                                        }
//                                        ((PSIJK) mPlayerService.getPlayer()).setSurface(liveTextureView.surface);
//                                        v.postDelayed(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                Bitmap bitmap = liveTextureView.getBitmap();
//                                                if (bitmap == null) {
//                                                    markFail("fail8");
//                                                    return;
//                                                }
//                                                bitmap = Bitmap.createBitmap(bitmap, 0, 0, (int) videoWidth, displayHeight);
//                                                bitmap = Bitmap.createScaledBitmap(bitmap, 320, 240, true);
//                                                File saveDir = new File(Environment.getExternalStorageDirectory(), "parentsmeeting/save");
//                                                if (!saveDir.exists()) {
//                                                    saveDir.mkdirs();
//                                                }
//                                                File file = new File(saveDir, "" + System.currentTimeMillis() + ".png");
//                                                ImageUtils.save(bitmap, file, Bitmap.CompressFormat.JPEG);
//                                                reMark(file, (String) v.getTag());
//                                                ((PSIJK) mPlayerService.getPlayer()).setDisplay(liveVideoView.getSurfaceHolder());
//                                            }
//                                        }, 100);
//                                    }
//                                }, 100);
//                            }
//                        }, 100);
//                    }
//                });
//            }
//        }
//    }
//
//    public void markFail(String method) {
//        logToFile.d("markFail:method=" + method);
//        XESToastUtils.showToast(mContext, "标记失败");
//        umsAgentMark(false, 0, 0, 0);
//        isMarking = false;
//    }
//
//    public void showMarkGuide() {
//        if (!isGaosan) {
//            return;
//        }
//        if (mLiveMediaControllerBottom.getvMarkGuide() != null) {
//            int count = ShareDataManager.getInstance().getInt(LiveVideoConfig.SP_LIVEVIDEO_MARK_POINT_COUNT, 0, ShareDataManager.SHAREDATA_USER);
//            if (count <= 3) {
//                mLiveMediaControllerBottom.getvMarkGuide().post(new Runnable() {
//                    @Override
//                    public void run() {
//                        mLiveMediaControllerBottom.getvMarkGuide().setVisibility(View.VISIBLE);
//                        if (mLiveMediaControllerBottom.getSwitchFlowView() != null) {
//                            mLiveMediaControllerBottom.getSwitchFlowView().setSwitchFlowPopWindowVisible(false);
//                        }
//                        mLiveMediaControllerBottom.onShow();
//                        mLiveMediaControllerBottom.getvMarkGuide().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                mLiveMediaControllerBottom.getvMarkGuide().setVisibility(View.GONE);
//                                mLiveMediaControllerBottom.onHide();
//                            }
//                        }, 3000);
//                    }
//                });
//                count++;
//                ShareDataManager.getInstance().put(LiveVideoConfig.SP_LIVEVIDEO_MARK_POINT_COUNT, count, ShareDataManager.SHAREDATA_USER);
//            }
//        }
//    }
//
//    public void hideBtMark() {
//        logger.d("hideBtMark");
//        if (mLiveMediaControllerBottom != null) {
//            mLiveMediaControllerBottom.getBtMark().setVisibility(View.GONE);
//        }
//    }
//
//    private void setIsCounting(boolean counting) {
//        isCounting = counting;
//        setBtEnable(!isCounting && isClassReady && isVideoReady && !isOnChat);
//    }
//
//    public void setVideoReady(boolean videoReady) {
//        isVideoReady = videoReady;
//        setBtEnable(!isCounting && isClassReady && isVideoReady && !isOnChat);
//    }
//
//    public void setClassReady(boolean classReady) {
//        isClassReady = classReady;
//        setBtEnable(!isCounting && isClassReady && isVideoReady && !isOnChat);
//    }
//
//    public void setOnChat(boolean onChat) {
//        isOnChat = onChat;
//        setBtEnable(!isCounting && isClassReady && isVideoReady && !isOnChat);
//    }
//
//    public void setBottom(RelativeLayout bottom) {
//        this.bottom = bottom;
//    }
//
//    public void setCallBack(AbstractBusinessDataCallBack callBack) {
//        mCallBack = callBack;
//    }
//
//    public void setLiveId(String liveId) {
//        this.liveId = liveId;
//    }
//
//    public List<VideoPointEntity> getList() {
//        return mList;
//    }
//
//    public void setList(List<VideoPointEntity> list) {
//        mList = list;
//        setEntityNum(mList);
//        if (AppConfig.isMulLiveBack) {
//            setNewEntityNum(mList);
//        }
//    }
//
//    private void setNewEntityNum(List<VideoPointEntity> lst) {
//        if (lst == null || lst.size() == 0) {
//            return;
//        }
//        questionNum = 0;
//        redPackNum = 0;
//        examNum = 0;
//        englishH5Num = 0;
//        markNum = 0;
//        for (VideoPointEntity entity : lst) {
//            String newType = entity.getNewType();
//            if (newType == null) {
//                continue;
//            }
//            switch (newType) {
//                case "1":
//                case "6":
//                    entity.setNumone(++questionNum);
//                    break;
//                case "2":
//                case "3":
//                case "4":
//                    entity.setNumtwo(++examNum);
//                    break;
//                case "5":
//                case "10":
//                    entity.setNumthree(++englishH5Num);
//                    break;
//                default:
//                    entity.setNum(++markNum);
//            }
//        }
//    }
//
//
//    public void setLiveAndBackDebug(LiveAndBackDebug liveAndBackDebug) {
//        mLiveAndBackDebug = liveAndBackDebug;
//    }
//
//    public void setHttpManager(LiveHttpManager httpManager) {
//        mHttpManager = httpManager;
//    }
//
//    public void showBtMark() {
//        logger.d("showBtMark:mLiveMediaControllerBottom=null?" + (mLiveMediaControllerBottom == null));
//        if (mLiveMediaControllerBottom != null) {
//            mLiveMediaControllerBottom.getBtMark().setVisibility(View.VISIBLE);
//        }
//    }
//
//    public void setController(MediaController2 controller) {
//        mController = controller;
//    }
//
//    public void setSysTimeOffset(long sysTimeOffset) {
//        this.sysTimeOffset = sysTimeOffset;
//    }
//
//    /**
//     * 上传标记点
//     */
//    private void reMark(File file, final String type) {
//        ShareDataManager.getInstance().put(LiveVideoConfig.SP_LIVEVIDEO_MARK_POINT_COUNT, 4, ShareDataManager.SHAREDATA_USER);
//        String fileName = file.getAbsolutePath();
//        try {
//            final long pkt = ((PSIJK) mPlayerService.getPlayer()).native_getFrameInfo().pkt / 1000;
//            final long cache = ((PSIJK) mPlayerService.getPlayer()).getVideoCachedDuration() / 1000;
//            final long time = pkt - cache + offSet - 8;
//            logger.i("frameTime:" + ((PSIJK) mPlayerService.getPlayer()).native_getFrameInfo().pkt / 1000);
//            logger.i("cacheTime:" + ((PSIJK) mPlayerService.getPlayer()).getVideoCachedDuration() / 1000);
//            logger.i("offset:" + offSet + "  time:" + time + "   sysTime:" + System.currentTimeMillis());
//            if (!TextUtils.isEmpty(fileName)) {
//                CloudUploadEntity entity = new CloudUploadEntity();
//                entity.setFilePath(fileName);
//                entity.setType(XesCloudConfig.UPLOAD_IMAGE);
//                entity.setCloudPath(CloudDir.LIVE_MARK);
//                mCloudUploadBusiness.asyncUpload(entity, new XesStsUploadListener() {
//                    @Override
//                    public void onProgress(XesCloudResult result, int percent) {
//                        logger.i("progress " + percent);
//                    }
//
//                    @Override
//                    public void onSuccess(XesCloudResult result) {
//                        logger.i("upCloud Sucess");
//                        mHttpManager.saveLiveMark(liveId, type, "" + time, result.getHttpPath(), new HttpCallBack(false) {
//                            @Override
//                            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
//                                StringBuilder sb = new StringBuilder("标记\"\"成功!看回放时可快速找到哟~");
//                                if (!TextUtils.isEmpty(type)) {
//                                    switch (type) {
//                                        case "1":
//                                            sb.insert(3, "疑问");
//                                            break;
//                                        case "2":
//                                            sb.insert(3, "总结");
//                                            break;
//                                        case "3":
//                                            sb.insert(3, "高分点");
//                                            break;
//                                        case "4":
//                                            sb.insert(3, "要多练");
//                                            break;
//                                        default:
//                                            break;
//                                    }
//                                }
//                                if (isGaosan) {
//                                    XESToastUtils.showToast(mContext, sb.toString());
//                                } else {
//                                    XESToastUtils.showToast(mContext, "标记成功");
//                                }
//                                isMarking = false;
//                                umsAgentMark(true, pkt, cache, offSet);
//                                startCountDown();
//                            }
//
//                            @Override
//                            public void onPmFailure(Throwable error, String msg) {
//                                super.onPmFailure(error, msg);
//                                markFail("fail9_" + msg);
//                            }
//
//                            @Override
//                            public void onPmError(ResponseEntity responseEntity) {
//                                super.onPmError(responseEntity);
//                                markFail("fail10_" + responseEntity.getErrorMsg());
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onError(XesCloudResult result) {
//                        logger.i(result.getErrorMsg());
//                    }
//                });
//            } else {
//                markFail("fail11");
//            }
//        } catch (Exception e) {
//            logToFile.e("reMark", e);
//            e.printStackTrace();
//            CrashReport.postCatchedException(e);
//        }
//
//    }
//
//    private void startCountDown() {
//        CountDownTimer timer = new CountDownTimer(15200, 1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//                logger.i("onTick:" + millisUntilFinished);
//                mLiveMediaControllerBottom.getBtMark().setText(((millisUntilFinished) / 1000) + "");
//                mLiveMediaControllerBottom.getBtMark().setBackgroundResource(R.drawable.shape_oval_black);
//            }
//
//            @Override
//            public void onFinish() {
//                logger.i("onFinish");
//                mLiveMediaControllerBottom.getBtMark().setBackgroundResource(R.drawable.bg_bt_live_mark);
//                mLiveMediaControllerBottom.getBtMark().setText("");
//                setIsCounting(false);
//            }
//        };
//        //mLiveMediaControllerBottom.getBtMark().setText("15");
//        mLiveMediaControllerBottom.getBtMark().setBackgroundResource(R.drawable.shape_oval_black);
//        setIsCounting(true);
//        timer.start();
//    }
//
//    public void setLayout(int width, int height) {
//        int screenWidth = getScreenParam();
//        displayHeight = height;
//        int displayWidth = screenWidth;
//        if (width > 0) {
//            int wradio = (int) (LiveVideoConfig.VIDEO_HEAD_WIDTH * width / LiveVideoConfig.VIDEO_WIDTH);
//            wradio += (screenWidth - width) / 2;
//            if (displayWidth - wradio != videoWidth) {
//                videoWidth = displayWidth - wradio;
//            }
//        }
//    }
//
//    public void setVideoLayout(LiveVideoPoint liveVideoPoint) {
//        displayHeight = liveVideoPoint.videoHeight;
//        int displayWidth = liveVideoPoint.screenWidth;
//        if (liveVideoPoint.videoWidth > 0) {
//            int wradio = liveVideoPoint.getRightMargin();
//            if (displayWidth - wradio != videoWidth) {
//                videoWidth = displayWidth - wradio;
//            }
//        }
//    }
//
//    private int getScreenParam() {
//        final View contentView = ((Activity) mContext).findViewById(android.R.id.content);
//        final View actionBarOverlayLayout = (View) contentView.getParent();
//        Rect r = new Rect();
//        actionBarOverlayLayout.getWindowVisibleDisplayFrame(r);
//        return (r.right - r.left);
//    }
//
//
//    /**
//     * 获取标记点列表
//     */
//    public void getMarkPoints(String liveId, final AbstractBusinessDataCallBack callBack) {
//        mHttpManager.getMarkPoints(liveId, new HttpCallBack(false) {
//            @Override
//            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
//                JSONArray points = ((JSONObject) responseEntity.getJsonObject()).optJSONArray("markpointData");
//                if (mList == null) {
//                    mList = new ArrayList<>();
//                }
//                //mList.clear();
//                if (points != null) {
//                    for (int i = 0; i < points.length(); i++) {
//                        VideoPointEntity entity = new VideoPointEntity();
//                        entity.setCurTime(points.optJSONObject(i).optLong("cur_time"));
//                        entity.setRelativeTime(points.optJSONObject(i).optLong("relativeTime"));
//                        entity.setPic(points.optJSONObject(i).optString("image_url"));
//                        entity.setBeginTime(points.optJSONObject(i).optLong("image_url"));
////                        if (!isGaosan) {
////                            entity.setType(999);
////                        } else {
//                        entity.setType(100 + points.optJSONObject(i).optInt("mark_type"));
////                        }
//                        mList.add(entity);
//                    }
//                }
//                if (mList.size() > 0) {
//                    callBack.onDataSucess();
//                }
//                Collections.sort(mList, new Comparator<VideoPointEntity>() {
//                    @Override
//                    public int compare(VideoPointEntity o1, VideoPointEntity o2) {
//                        return (int) (o1.getRelativeTime() - o2.getRelativeTime());
//                    }
//                });
//                setEntityNum(mList);
//                if (AppConfig.isMulLiveBack) {
//                    setNewEntityNum(mList);
//                }
//                //showMarkPoints();
//            }
//
//            @Override
//            public void onPmFailure(Throwable error, String msg) {
//                super.onPmFailure(error, msg);
//            }
//
//            @Override
//            public void onPmError(ResponseEntity responseEntity) {
//                super.onPmError(responseEntity);
//            }
//        });
//    }
//
//    /**
//     * 显示标记点列表
//     */
//    public void showMarkPoints() {
//        if (rlMask == null) {
//            rlMask = new RelativeLayout(mContext);
//            RelativeLayout.LayoutParams rlParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//            rlMask.setLayoutParams(rlParam);
//            llPoints = new LinearLayout(mContext);
//            llPoints.setOrientation(LinearLayout.VERTICAL);
//            llPoints.setBackgroundColor(Color.parseColor("#a0000000"));
//            TextView tv = new TextView(mContext);
//            tv.setText("标记点");
//            tv.setTextSize(12);
//            tv.setTextColor(Color.WHITE);
//            tv.setPadding(0, 10, 10, 10);
//            llPoints.addView(tv);
//            lvPoints = new ListView(mContext);
//            mAdapter = new CommonAdapter<VideoPointEntity>(mList) {
//                @Override
//                public AdapterItemInterface getItemView(Object o) {
//                    return new PointListItem();
//                }
//            };
//            mAdapter.registerDataSetObserver(new DataSetObserver() {
//                @Override
//                public void onChanged() {
//                    super.onChanged();
//                    markNum = 0;
//                    questionNum = 0;
//                    englishH5Num = 0;
//                    redPackNum = 0;
//                }
//            });
//            lvPoints.setAdapter(mAdapter);
//
//            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(SizeUtils.Dp2Px(mContext, 278),
//                    ViewGroup.LayoutParams.MATCH_PARENT);
//            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//            //params.setMargins(0,40,0,0);
//            llPoints.setPadding(20, 20, 0, 0);
//            llPoints.addView(lvPoints);
//            rlMask.addView(llPoints);
//            bottom.addView(rlMask);
//            llPoints.setLayoutParams(params);
//        }
//        rlMask.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (rlMask.getVisibility() == View.VISIBLE) {
//                    rlMask.setVisibility(View.GONE);
//                    if (mCallBack != null) {
//                        mCallBack.onDataSucess();
//                    }
//                }
//            }
//        });
//        rlMask.setVisibility(View.VISIBLE);
//        mController.release();
//        mAdapter.notifyDataSetChanged();
//        umsAgentMarkButton();
//    }
//
//    public void setGaosan(boolean gaosan) {
//        isGaosan = gaosan;
//    }
//
//    public void showMarkTip(final int type) {
//        //高三才显示
//        if (!isGaosan) {
//            return;
//        }
//        bottom.post(new Runnable() {
//            @Override
//            public void run() {
//                final int width = SizeUtils.Dp2Px(mContext, 170);
//                if (vTips == null) {
//                    vTips = View.inflate(mContext, R.layout.layout_livevideo_mark_point_tip, null);
//                    ivTipsIcon = vTips.findViewById(R.id.iv_livevideo_mark_point_tip_icon);
//                    tvTipsContent = vTips.findViewById(R.id.tv_livevideo_mark_point_tip_content);
//                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                    layoutParams.bottomMargin = SizeUtils.Dp2Px(mContext, 70);
//                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//                    vTips.setTranslationY(layoutParams.bottomMargin = SizeUtils.Dp2Px(mContext, 70) * -1);
//                    bottom.addView(vTips, layoutParams);
//                }
//                switch (100 + type) {
//                    case MARK_TYPE_TEACHER_HIGH_MARK:
//                        ivTipsIcon.setImageResource(R.drawable.livevideo_biaoji_gaofen_icon_normal);
//                        tvTipsContent.setText("高分点请注意");
//                        break;
//                    case MARK_TYPE_TEACHER_INCLUDE:
//                        ivTipsIcon.setImageResource(R.drawable.livevideo_biaoji_zongjie_icon_normal);
//                        tvTipsContent.setText("总结请注意");
//                        break;
//                    case MARK_TYPE_TEACHER_PRACTICE:
//                        ivTipsIcon.setImageResource(R.drawable.livevideo_biaoji_lianxi_icon_normal);
//                        tvTipsContent.setText("要多练请注意");
//                        break;
//                    default:
//                        break;
//                }
//                AnimatorSet animatorSet = new AnimatorSet();
//                ObjectAnimator animatorIn = ObjectAnimator.ofFloat(vTips, "translationX", width * -1, 0);
//                ObjectAnimator animatorOut = ObjectAnimator.ofFloat(vTips, "translationX", 0, width * -1);
//                animatorSet.play(animatorOut).after(3000).after(animatorIn);
//                animatorSet.start();
//            }
//        });
//    }
//
//    public void hideMarkPoints() {
//        if (rlMask != null) {
//            rlMask.setVisibility(View.GONE);
//        }
//        if (mDialog != null && mDialog.isDialogShow()) {
//            mDialog.cancelDialog();
//        }
//    }
//
//    public void setBtEnable(final boolean enable) {
//        logger.i("setBtEnable  " + "video:" + isVideoReady + "   class:" + isClassReady
//                + "   onchat:" + isOnChat);
//        if (mLiveMediaControllerBottom == null) {
//            return;
//        }
//        mLiveMediaControllerBottom.getBtMark().post(new Runnable() {
//            @Override
//            public void run() {
//                if (enable) {
//                    mLiveMediaControllerBottom.getBtMark().setAlpha(1);
//                    mLiveMediaControllerBottom.getBtMark().setEnabled(true);
//                } else {
//                    mLiveMediaControllerBottom.getBtMark().setAlpha(0.5f);
//                    mLiveMediaControllerBottom.getBtMark().setEnabled(false);
//                }
//            }
//        });
//
//    }
//
//    private void deletPoint(final VideoPointEntity entity) {
//        mHttpManager.deleteMarkPoints(liveId, entity.getCurTime(), new HttpCallBack() {
//            @Override
//            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
//                try {
//                    mList.remove(entity);
//                    setEntityNum(mList);
//                    mAdapter.notifyDataSetChanged();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onPmFailure(Throwable error, String msg) {
//                super.onPmFailure(error, msg);
//            }
//
//            @Override
//            public void onPmError(ResponseEntity responseEntity) {
//                super.onPmError(responseEntity);
//            }
//        });
//    }
//
//    private void setEntityNum(List<VideoPointEntity> lst) {
//        if (lst == null || lst.size() == 0) {
//            return;
//        }
//        countMap.clear();
//        for (VideoPointEntity entity : lst) {
//            if (countMap.get(entity.getType()) == null) {
//                countMap.put(entity.getType(), 1);
//            } else {
//                countMap.put(entity.getType(), countMap.get(entity.getType()) + 1);
//            }
//            entity.setNum(countMap.get(entity.getType()));
//        }
//    }
//
//
//    private class PointListItem implements AdapterItemInterface<VideoPointEntity> {
//        private ImageView ivShot;
//        private ImageView ivPlay;
//        private TextView tvText;
//        private VerifyCancelAlertDialog mDialog;
//        private View vDelete;
//        private View root;
//        private View vSig;
//        private VideoPointEntity mEntity;
//
//        @Override
//        public int getLayoutResId() {
//            return R.layout.layout_live_mark_point;
//        }
//
//        @Override
//        public void initViews(View view) {
//            root = view;
//            ivShot = (ImageView) view.findViewById(R.id.iv_live_mark_point_shot_pic);
//            ivPlay = (ImageView) view.findViewById(R.id.iv_live_mark_point_play);
//            tvText = (TextView) view.findViewById(R.id.tv_live_mark_point_text);
//            vDelete = view.findViewById(R.id.iv_live_mark_point_delete);
//            vSig = view.findViewById(R.id.v_live_mark_point_sig);
//        }
//
//        @Override
//        public void bindListener() {
//            root.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mPlayerService.seekTo((mEntity.getRelativeTime() < 0 ? 0 : mEntity.getRelativeTime()) * 1000);
//                    umsAgentPlay(mEntity.getType(), mEntity.getRelativeTime());
//                    if (LivePSRemarkBll.this.mCallBack != null) {
//                        LivePSRemarkBll.this.mCallBack.onDataSucess();
//                    }
//                }
//            });
//            vDelete.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (mDialog == null) {
//                        mDialog = new VerifyCancelAlertDialog(mContext, ContextManager.getApplication(), false, VerifyCancelAlertDialog.TITLE_MESSAGE_VERIRY_CANCEL_TYPE);
//                        mDialog.initInfo("删除标记点", "是否删除标记点？");
//                        mDialog.setVerifyBtnListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                deletPoint(mEntity);
//                                umsAgentDelete();
//                            }
//                        });
//                    }
//                    LivePSRemarkBll.this.mDialog = mDialog;
//                    mDialog.showDialog();
//                }
//            });
//        }
//
//        @Override
//        public void updateViews(VideoPointEntity entity, int i, Object o) {
//            mEntity = entity;
//
//            ivPlay.setTag(entity.getPic());
//            if (!entity.isPlaying()) {
//                ivPlay.setVisibility(View.VISIBLE);
//            } else {
//                ivPlay.setVisibility(View.GONE);
//            }
//            vDelete.setVisibility(View.GONE);
//
//            StringBuilder sb = new StringBuilder();
//            ivShot.setScaleType(ImageView.ScaleType.CENTER);
//            if (entity.getType() == 24 && entity.getNewType() != null) {
//                switch (entity.getNewType()) {
//                    case "1":
//                    case "6":
//                        sb.append("互动题");
//                        sb.append(entity.getNumone());
//                        vSig.setBackgroundResource(R.drawable.shape_corners_4dp_f0773c);
//                        ivShot.setImageResource(R.drawable.bg_live_mark_question);
//                        break;
//                    case "2":
//                    case "3":
//                    case "4":
//                        sb.append("测试卷");
//                        sb.append(entity.getNumtwo());
//                        vSig.setBackgroundResource(R.drawable.shape_corners_4dp_green);
//                        ivShot.setImageResource(R.drawable.bg_live_video_mark_exam);
//                        break;
//                    case "5":
//                    case "10":
//                        sb.append("互动游戏");
//                        sb.append(entity.getNumthree());
//                        vSig.setBackgroundResource(R.drawable.shape_blue_corners);
//                        ivShot.setImageResource(R.drawable.bg_live_video_mark_courceware);
//                        break;
//
//                }
//                tvText.setText(sb.toString());
//            } else {
//                switch (entity.getType()) {
//                    case CATEGORY_QUESTION:
//                        sb.append("互动题");
//                        vSig.setBackgroundResource(R.drawable.shape_corners_4dp_f0773c);
//                        ivShot.setImageResource(R.drawable.bg_live_mark_question);
//                        break;
//                    case CATEGORY_REDPACKET:
//                        sb.append("红包");
//                        vSig.setBackgroundResource(R.drawable.shape_corners_4dp_f13232);
//                        ivShot.setImageResource(R.drawable.bg_live_mark_redpack);
//                        break;
//                    case CATEGORY_EXAM:
//                        sb.append("测试卷");
//                        vSig.setBackgroundResource(R.drawable.shape_corners_4dp_green);
//                        ivShot.setImageResource(R.drawable.bg_live_video_mark_exam);
//                        break;
//                    case CATEGORY_H5COURSE_WARE:
//                    case CATEGORY_ENGLISH_H5COURSE_WARE:
//                        sb.append("互动课件");
//                        vSig.setBackgroundResource(R.drawable.shape_blue_corners);
//                        ivShot.setImageResource(R.drawable.bg_live_video_mark_courceware);
//                        break;
//                    case MARK_TYPE_QUESTION:
//                        ivShot.setScaleType(ImageView.ScaleType.FIT_XY);
//                        vDelete.setVisibility(View.VISIBLE);
//                        vSig.setBackgroundResource(R.drawable.shape_corners_4dp_f13232);
//                        ImageLoader.with(mContext).load(entity.getPic()).placeHolder(R.drawable.bg_default_image).error(R.drawable.bg_default_image).into(ivShot);
//                        sb.append("疑问");
//                        sb.append(entity.getNum());
//                        break;
//                    case MARK_TYPE_INCLUDE:
//                        ivShot.setScaleType(ImageView.ScaleType.FIT_XY);
//                        vDelete.setVisibility(View.VISIBLE);
//                        vSig.setBackgroundResource(R.drawable.shape_corners_4dp_f13232);
//                        ImageLoader.with(mContext).load(entity.getPic()).placeHolder(R.drawable.bg_default_image).error(R.drawable.bg_default_image).into(ivShot);
//                        sb.append("总结");
//                        sb.append(entity.getNum());
//                        break;
//                    case MARK_TYPE_HIGH_MARK:
//                        ivShot.setScaleType(ImageView.ScaleType.FIT_XY);
//                        vDelete.setVisibility(View.VISIBLE);
//                        vSig.setBackgroundResource(R.drawable.shape_corners_4dp_f13232);
//                        ImageLoader.with(mContext).load(entity.getPic()).placeHolder(R.drawable.bg_default_image).error(R.drawable.bg_default_image).into(ivShot);
//                        sb.append("高分点");
//                        sb.append(entity.getNum());
//                        break;
//                    case MARK_TYPE_PRACTICE:
//                        ivShot.setScaleType(ImageView.ScaleType.FIT_XY);
//                        vDelete.setVisibility(View.VISIBLE);
//                        vSig.setBackgroundResource(R.drawable.shape_corners_4dp_f13232);
//                        ImageLoader.with(mContext).load(entity.getPic()).placeHolder(R.drawable.bg_default_image).error(R.drawable.bg_default_image).into(ivShot);
//                        sb.append("要多练");
//                        sb.append(entity.getNum());
//                        break;
//
//                    case MARK_TYPE_TEACHER_INCLUDE:
//                        ivShot.setScaleType(ImageView.ScaleType.FIT_XY);
//                        vSig.setBackgroundResource(R.drawable.shape_corners_4dp_f13232);
//                        ImageLoader.with(mContext).load(entity.getPic()).placeHolder(R.drawable.bg_default_image).error(R.drawable.bg_default_image).into(ivShot);
//                        sb.append("总结(老师发布)");
//                        break;
//                    case MARK_TYPE_TEACHER_HIGH_MARK:
//                        ivShot.setScaleType(ImageView.ScaleType.FIT_XY);
//                        vSig.setBackgroundResource(R.drawable.shape_corners_4dp_f13232);
//                        ImageLoader.with(mContext).load(entity.getPic()).placeHolder(R.drawable.bg_default_image).error(R.drawable.bg_default_image).into(ivShot);
//                        sb.append("高分点(老师发布)");
//                        break;
//                    case MARK_TYPE_TEACHER_PRACTICE:
//                        ivShot.setScaleType(ImageView.ScaleType.FIT_XY);
//                        vSig.setBackgroundResource(R.drawable.shape_corners_4dp_f13232);
//                        ImageLoader.with(mContext).load(entity.getPic()).placeHolder(R.drawable.bg_default_image).error(R.drawable.bg_default_image).into(ivShot);
//                        sb.append("要多练(老师发布)");
//                        break;
//                    default:
//                        ivShot.setScaleType(ImageView.ScaleType.FIT_XY);
//                        vDelete.setVisibility(View.VISIBLE);
//                        vSig.setBackgroundResource(R.drawable.shape_corners_4dp_f13232);
//                        ImageLoader.with(mContext).load(entity.getPic()).placeHolder(R.drawable.bg_default_image).error(R.drawable.bg_default_image).into(ivShot);
//                        sb.append("疑问点");
//                        sb.append(entity.getNum());
//                }
////                sb.append(entity.getNum());
//                tvText.setText(sb.toString());
//                //tvText.setText("疑问点" + (i + 1));
//            }
//        }
//    }
//
//    private void umsAgentMark(boolean success, long pkt, long cache, long offSet) {
//        HashMap<String, String> map = new HashMap<>();
//        map.put("logtype", "clickMark");
//        map.put("ex", success ? "Y" : "N");
//        if (success) {
//            map.put("pkt", pkt + "");
//            map.put("cache", cache + "");
//            map.put("offset", offSet + "");
//            map.put("systime", (System.currentTimeMillis() / 1000 + sysTimeOffset) + "");
//        }
//        mLiveAndBackDebug.umsAgentDebugInter("live_mark", map);
//    }
//
//    private void umsAgentMarkButton() {
//        HashMap<String, String> map = new HashMap<>();
//        map.put("logtype", "clickMarkTag");
//        mLiveAndBackDebug.umsAgentDebugInter("replay_mark", map);
//    }
//
//    private void umsAgentPlay(int type, long time) {
//        HashMap<String, String> map = new HashMap<>();
//        map.put("logtype", "clickMarkPlay");
//        map.put("time", time + "");
//        String markType = "";
//        switch (type) {
//            case CATEGORY_QUESTION:
//                markType = "interact";
//                break;
//            case CATEGORY_REDPACKET:
//                markType = "redPacket";
//                break;
//            case CATEGORY_EXAM:
//                markType = "exampaper";
//                break;
//            case CATEGORY_H5COURSE_WARE:
//            case CATEGORY_ENGLISH_H5COURSE_WARE:
//                markType = "interactware";
//                break;
//            default:
//                markType = "query";
//                break;
//        }
//        map.put("marktype", markType);
//        mLiveAndBackDebug.umsAgentDebugInter("replay_mark", map);
//    }
//
//    private void umsAgentDelete() {
//        HashMap<String, String> map = new HashMap<>();
//        map.put("logtype", "clickMarkDelete");
//        mLiveAndBackDebug.umsAgentDebugInter("replay_mark", map);
//    }
//
//    public void onPause() {
//        if (mTimer != null) {
//            mTimer.cancel();
//        }
//        setVideoReady(false);
//    }
//}