package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.CountDownTimer;
import android.os.Environment;
import android.text.TextUtils;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.parentsmeeting.base.AbstractBusinessDataCallBack;
import com.xueersi.parentsmeeting.cloud.XesCloudUploadBusiness;
import com.xueersi.parentsmeeting.cloud.config.CloudDir;
import com.xueersi.parentsmeeting.cloud.config.XesCloudConfig;
import com.xueersi.parentsmeeting.cloud.entity.CloudUploadEntity;
import com.xueersi.parentsmeeting.cloud.entity.XesCloudResult;
import com.xueersi.parentsmeeting.cloud.listener.XesStsUploadListener;
import com.xueersi.parentsmeeting.entity.VideoPointEntity;
import com.xueersi.parentsmeeting.http.HttpCallBack;
import com.xueersi.parentsmeeting.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoActivity;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveVideoView;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveTextureView;
import com.xueersi.parentsmeeting.modules.videoplayer.media.MediaController;
import com.xueersi.parentsmeeting.modules.videoplayer.media.PlayerService;
import com.xueersi.parentsmeeting.modules.videoplayer.media.VideoView;
import com.xueersi.xesalib.adapter.AdapterItemInterface;
import com.xueersi.xesalib.adapter.CommonAdapter;
import com.xueersi.xesalib.utils.app.ContextManager;
import com.xueersi.xesalib.utils.app.XESToastUtils;
import com.xueersi.xesalib.utils.listener.OnUnDoubleClickListener;
import com.xueersi.xesalib.utils.log.Loger;
import com.xueersi.xesalib.utils.uikit.ImageUtils;
import com.xueersi.xesalib.utils.uikit.SizeUtils;
import com.xueersi.xesalib.utils.uikit.imageloader.ImageLoader;
import com.xueersi.xesalib.view.alertdialog.BaseAlertDialog;
import com.xueersi.xesalib.view.alertdialog.ChooseListAlertDialog;
import com.xueersi.xesalib.view.alertdialog.VerifyCancelAlertDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import tv.danmaku.ijk.media.player.FrameInfo;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

import static com.xueersi.parentsmeeting.sharebusiness.config.LocalCourseConfig.CATEGORY_ENGLISH_H5COURSE_WARE;
import static com.xueersi.parentsmeeting.sharebusiness.config.LocalCourseConfig.CATEGORY_EXAM;
import static com.xueersi.parentsmeeting.sharebusiness.config.LocalCourseConfig.CATEGORY_H5COURSE_WARE;
import static com.xueersi.parentsmeeting.sharebusiness.config.LocalCourseConfig.CATEGORY_QUESTION;
import static com.xueersi.parentsmeeting.sharebusiness.config.LocalCourseConfig.CATEGORY_REDPACKET;

/**
 * Created by Tang on 2018/3/5.
 */

public class LiveRemarkBll {
    private Context mContext;
    private PlayerService mPlayerService;
    private String TAG = "LiveRemarkBll";
    private Timer mTimer;
    private long offSet;
    private LiveMediaControllerBottom mLiveMediaControllerBottom;
    private long sysTimeOffset;
    private VideoView mVideoView;
    private int displayHeight;
    private int displayWidth;
    private int wradio;
    private double videoWidth;
    private LiveHttpManager mHttpManager;
    private XesCloudUploadBusiness mCloudUploadBusiness;
    private RelativeLayout bottom;
    private List<VideoPointEntity> mList;
    private ListView lvPoints;
    private LinearLayout llPoints;
    private RelativeLayout rlMask;
    private CommonAdapter mAdapter;
    private TextureView mTextureView;
    private MediaController mController;
    private AbstractBusinessDataCallBack mCallBack;
    private String liveId;
    private int markNum=0;
    private int questionNum=0;
    private int englishH5Num=0;
    private int redPackNum=0;
    private int examNum=0;
    private VerifyCancelAlertDialog mDialog;
    private boolean isVideoReady;
    private boolean isClassReady;
    private boolean isOnChat;
    private boolean isMarking;
    private LiveAndBackDebug mLiveAndBackDebug;

    public LiveRemarkBll(Context context, PlayerService playerService){
        mContext=context;
        mPlayerService=playerService;
        initData();
    }

    public void setLiveMediaControllerBottom(LiveMediaControllerBottom liveMediaControllerBottom) {
        mLiveMediaControllerBottom = liveMediaControllerBottom;

    }

    public void setTextureView(TextureView textureView) {
        mTextureView = textureView;
    }

    public void initData() {
//        if(mLiveMediaControllerBottom!=null){
//            mLiveMediaControllerBottom.getBtMark().setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    XESToastUtils.showToast(mContext,"正在加载视频");
//                }
//            });
//        }
        setVideoReady(false);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (mPlayerService.getPlayer() == null) {
                    return;
                }
                long tcpSpeed = ((IjkMediaPlayer)mPlayerService.getPlayer()).getTcpSpeed();
                float vdfps = ((IjkMediaPlayer)mPlayerService.getPlayer()).getVideoDecodeFramesPerSecond();
                if(mLiveMediaControllerBottom==null){
                    return;
                }
                if (Math.round(vdfps) == 12) {
                    //mTimer.cancel();
                    Loger.i(TAG, "dfps   " + vdfps);
                    FrameInfo frameInfo = ((IjkMediaPlayer)mPlayerService.getPlayer()).native_getFrameInfo();
                    offSet = System.currentTimeMillis()/1000+sysTimeOffset - frameInfo.pkt/1000;
                    Loger.i(TAG, "nowtime  " + frameInfo.nowTime + "   dts     " + frameInfo.pkt_dts
                            + "   pkt   " + frameInfo.pkt + "  cache:" + ((IjkMediaPlayer)mPlayerService.getPlayer()).getVideoCachedDuration());
                    //setBtEnable(true);
                    setVideoReady(true);
                    mTimer.cancel();
                    mLiveMediaControllerBottom.getBtMark().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            if(isMarking){
                                return;
                            }
                            final LiveTextureView liveTextureView = (LiveTextureView) ((Activity) mContext).findViewById(R.id.ltv_course_video_video_texture);
                            if (liveTextureView == null) {
                                return;
                            }
                            if(mPlayerService.getPlayer()==null){
                                XESToastUtils.showToast(mContext,"标记失败");
                                return;
                            }
                            isMarking=true;
                            final LiveVideoView liveVideoView = (LiveVideoView) ((Activity) mContext).findViewById(R.id.vv_course_video_video);
//                liveVideoView.setVisibility(View.INVISIBLE);
                            ((IjkMediaPlayer)mPlayerService.getPlayer()).setSurface(liveTextureView.surface);
                            v.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ((IjkMediaPlayer)mPlayerService.getPlayer()).setDisplay(liveVideoView.getSurfaceHolder());
                                    v.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            ((IjkMediaPlayer)mPlayerService.getPlayer()).setSurface(liveTextureView.surface);
                                            v.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Bitmap bitmap = liveTextureView.getBitmap();
                                                    if (bitmap == null) {
                                                        markFail();
                                                        return;
                                                    }
                                                    bitmap=Bitmap.createBitmap(bitmap,0,0,(int)videoWidth,displayHeight);
                                                    bitmap=Bitmap.createScaledBitmap(bitmap,320,240,true);
                                                    File saveDir = new File(Environment.getExternalStorageDirectory(), "parentsmeeting/save");
                                                    if (!saveDir.exists()) {
                                                        saveDir.mkdirs();
                                                    }
                                                    File file = new File(saveDir, "" + System.currentTimeMillis() + ".png");
                                                    ImageUtils.save(bitmap, file, Bitmap.CompressFormat.JPEG);
                                                    reMark(file);
                                                    ((IjkMediaPlayer)mPlayerService.getPlayer()).setDisplay(liveVideoView.getSurfaceHolder());
                                                }
                                            }, 100);
                                        }
                                    }, 100);
                                }
                            }, 100);
                        }
                    });

                }

            }
        };
        if(mTimer!=null){
            mTimer.cancel();
        }
        mTimer = new Timer();
        mTimer.schedule(task, 1000, 1000);
        mCloudUploadBusiness = new XesCloudUploadBusiness(mContext);
    }
    public void markFail(){
        XESToastUtils.showToast(mContext,"标记失败");
        umsAgentMark(false);
        isMarking=false;
    }

    public void hideBtMark() {
        if (mLiveMediaControllerBottom != null) {
            mLiveMediaControllerBottom.getBtMark().setVisibility(View.GONE);
        }
    }

    public void setVideoReady(boolean videoReady) {
        isVideoReady = videoReady;
        setBtEnable(isClassReady&&isVideoReady&&!isOnChat);
    }

    public void setClassReady(boolean classReady) {
        isClassReady = classReady;
        setBtEnable(isClassReady&&isVideoReady&&!isOnChat);
    }

    public void setOnChat(boolean onChat) {
        isOnChat = onChat;
        setBtEnable(isClassReady&&isVideoReady&&!isOnChat);
    }

    public void setBottom(RelativeLayout bottom) {
        this.bottom = bottom;
    }

    public void setCallBack(AbstractBusinessDataCallBack callBack) {
        mCallBack = callBack;
    }

    public void setLiveId(String liveId) {
        this.liveId = liveId;
    }

    public List<VideoPointEntity> getList() {
        return mList;
    }

    public void setList(List<VideoPointEntity> list) {
        mList = list;
        setEntityNum(mList);
    }


    public void setLiveAndBackDebug(LiveAndBackDebug liveAndBackDebug) {
        mLiveAndBackDebug = liveAndBackDebug;
    }

    public void setHttpManager(LiveHttpManager httpManager) {
        mHttpManager = httpManager;
    }

    public void showBtMark() {
        if (mLiveMediaControllerBottom != null) {
            mLiveMediaControllerBottom.getBtMark().setVisibility(View.VISIBLE);
        }
    }

    public void setController(MediaController controller) {
        mController = controller;
    }

    public void setSysTimeOffset(long sysTimeOffset) {
        this.sysTimeOffset = sysTimeOffset;
    }

    public void setVideoView(VideoView videoView) {
        mVideoView = videoView;
    }
    /**上传标记点*/
    private void reMark(File file) {
        String fileName = file.getAbsolutePath();
        long testTime=0;

        try {
            testTime = ((IjkMediaPlayer)mPlayerService.getPlayer()).native_getFrameInfo().pkt / 1000 - ((IjkMediaPlayer)mPlayerService.getPlayer()).getVideoCachedDuration() / 1000 + offSet;
        }catch (Exception e){
            e.printStackTrace();
        }
        final long time=testTime;
        Loger.i(TAG,"frameTime:"+((IjkMediaPlayer)mPlayerService.getPlayer()).native_getFrameInfo().pkt/1000);
        Loger.i(TAG,"cacheTime:"+((IjkMediaPlayer)mPlayerService.getPlayer()).getVideoCachedDuration()/1000);
        Loger.i(TAG,"offset:"+offSet+"  time:"+time+"   sysTime:"+System.currentTimeMillis());
        if (!TextUtils.isEmpty(fileName)) {
            CloudUploadEntity entity = new CloudUploadEntity();
            entity.setFilePath(fileName);
            entity.setType(XesCloudConfig.UPLOAD_IMAGE);
            entity.setCloudPath(CloudDir.LIVE_MARK);
            mCloudUploadBusiness.asyncUpload(entity, new XesStsUploadListener() {
                @Override
                public void onProgress(XesCloudResult result, int percent) {
                    Loger.i(TAG, "progress " + percent);
                }

                @Override
                public void onSuccess(XesCloudResult result) {
                    Loger.i(TAG, "upCloud Sucess");
                    mHttpManager.saveLiveMark("" + time, result.getHttpPath(), new HttpCallBack(false) {
                        @Override
                        public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                            XESToastUtils.showToast(mContext, "标记成功");
                            isMarking=false;
                            umsAgentMark(true);
                            startCountDown();
                        }

                        @Override
                        public void onPmFailure(Throwable error, String msg) {
                            super.onPmFailure(error, msg);
                            markFail();
                        }

                        @Override
                        public void onPmError(ResponseEntity responseEntity) {
                            super.onPmError(responseEntity);
                            markFail();
                        }
                    });
                }

                @Override
                public void onError(XesCloudResult result) {
                    Loger.i(TAG, result.getErrorMsg());
                }
            });
        } else {
            markFail();
        }

    }
    private void startCountDown(){
        CountDownTimer timer=new CountDownTimer(15200,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Loger.i(TAG,"onTick:"+millisUntilFinished);
                mLiveMediaControllerBottom.getBtMark().setText(((millisUntilFinished)/1000)+"");
                mLiveMediaControllerBottom.getBtMark().setBackgroundResource(R.drawable.shape_oval_black);
            }

            @Override
            public void onFinish() {
                Loger.i(TAG,"onFinish");
                mLiveMediaControllerBottom.getBtMark().setBackgroundResource(R.drawable.bg_bt_live_mark);
                mLiveMediaControllerBottom.getBtMark().setText("");
                setVideoReady(true);
            }
        };
        //mLiveMediaControllerBottom.getBtMark().setText("15");
        mLiveMediaControllerBottom.getBtMark().setBackgroundResource(R.drawable.shape_oval_black);
        setVideoReady(false);
        timer.start();
    }

    public void setLayout(int width, int height) {
        int screenWidth = getScreenParam();
        displayHeight = height;
        displayWidth = screenWidth;
        if (width > 0) {
            wradio = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * width / LiveVideoActivity.VIDEO_WIDTH);
            wradio += (screenWidth - width) / 2;
            if (displayWidth - wradio != videoWidth) {
                videoWidth = displayWidth - wradio;
            }
        }
    }

    private int getScreenParam() {
        final View contentView = ((Activity) mContext).findViewById(android.R.id.content);
        final View actionBarOverlayLayout = (View) contentView.getParent();
        Rect r = new Rect();
        actionBarOverlayLayout.getWindowVisibleDisplayFrame(r);
        return (r.right - r.left);
    }


    /**
     * 获取标记点列表
     */
    public void getMarkPoints(String liveId, final AbstractBusinessDataCallBack callBack) {
        mHttpManager.getMarkPoints(liveId,new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                JSONArray points = ((JSONObject) responseEntity.getJsonObject()).optJSONArray("markpointData");
                if (mList == null) {
                    mList = new ArrayList<>();
                }
                //mList.clear();
                if (points != null) {
                    for (int i = 0; i < points.length(); i++) {
                        VideoPointEntity entity = new VideoPointEntity();
                        entity.setCurTime(points.optJSONObject(i).optLong("cur_time"));
                        entity.setRelativeTime(points.optJSONObject(i).optLong("relativeTime"));
                        entity.setPic(points.optJSONObject(i).optString("image_url"));
                        entity.setBeginTime(points.optJSONObject(i).optLong("image_url"));
                        entity.setType(999);
                        mList.add(entity);
                    }
                }
                if(mList.size()>0){
                    callBack.onDataSucess();
                }
                Collections.sort(mList, new Comparator<VideoPointEntity>() {
                    @Override
                    public int compare(VideoPointEntity o1, VideoPointEntity o2) {
                        return (int)(o1.getRelativeTime()-o2.getRelativeTime());
                    }
                });
                setEntityNum(mList);
                //showMarkPoints();
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                super.onPmError(responseEntity);
            }
        });
    }

    /**
     * 显示标记点列表
     */
    public void showMarkPoints() {
        if ( rlMask== null) {
            rlMask=new RelativeLayout(mContext);
            RelativeLayout.LayoutParams rlParam=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            rlMask.setLayoutParams(rlParam);
            llPoints=new LinearLayout(mContext);
            llPoints.setOrientation(LinearLayout.VERTICAL);
            llPoints.setBackgroundColor(Color.parseColor("#a0000000"));
            TextView tv=new TextView(mContext);
            tv.setText("标记点");
            tv.setTextSize(12);
            tv.setTextColor(Color.WHITE);
            tv.setPadding(0,10,10,10);
            llPoints.addView(tv);
            lvPoints=new ListView(mContext);
            mAdapter =new CommonAdapter<VideoPointEntity>(mList) {
                @Override
                public AdapterItemInterface getItemView(Object o) {
                    return new PointListItem();
                }
            };
            mAdapter.registerDataSetObserver(new DataSetObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    markNum=0;
                    questionNum=0;
                    englishH5Num=0;
                    redPackNum=0;
                }
            });
            lvPoints.setAdapter(mAdapter);

            RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(SizeUtils.Dp2Px(mContext,278), ViewGroup.LayoutParams.MATCH_PARENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            //params.setMargins(0,40,0,0);
            llPoints.setPadding(20,20,0,0);
            llPoints.addView(lvPoints);
            rlMask.addView(llPoints);
            bottom.addView(rlMask);
            llPoints.setLayoutParams(params);
        }
        rlMask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rlMask.getVisibility()==View.VISIBLE){
                    rlMask.setVisibility(View.GONE);
                    if(mCallBack!=null){
                        mCallBack.onDataSucess();
                    }
                }
            }
        });
        rlMask.setVisibility(View.VISIBLE);
        mController.release();
        mAdapter.notifyDataSetChanged();
        umsAgentMarkButton();
    }

    public void hideMarkPoints() {
        if (rlMask != null) {
            rlMask.setVisibility(View.GONE);
        }
        if(mDialog!=null&&mDialog.isDialogShow()){
            mDialog.cancelDialog();
        }
    }
    public void setBtEnable(final boolean enable){
        if(mLiveMediaControllerBottom==null){
            return;
        }
        mLiveMediaControllerBottom.getBtMark().post(new Runnable() {
            @Override
            public void run() {
                if(enable){
                    mLiveMediaControllerBottom.getBtMark().setAlpha(1);
                    mLiveMediaControllerBottom.getBtMark().setEnabled(true);
                }else{
                    mLiveMediaControllerBottom.getBtMark().setAlpha(0.5f);
                    mLiveMediaControllerBottom.getBtMark().setEnabled(false);
                }
            }
        });

    }
    private void deletPoint(final VideoPointEntity entity){
        mHttpManager.deleteMarkPoints(liveId,entity.getCurTime(),new HttpCallBack(){
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                try{
                    mList.remove(entity);
                    setEntityNum(mList);
                    mAdapter.notifyDataSetChanged();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                super.onPmError(responseEntity);
            }
        });
    }
    private void setEntityNum(List<VideoPointEntity> lst){
        if(lst==null||lst.size()==0){
            return;
        }
        questionNum=0;
        redPackNum=0;
        examNum=0;
        englishH5Num=0;
        markNum=0;
        for(VideoPointEntity entity:lst){
            switch (entity.getType()){
                case CATEGORY_QUESTION:
                    entity.setNum(++questionNum);
                    break;
                case CATEGORY_REDPACKET:
                    entity.setNum(++redPackNum);
                    break;
                case CATEGORY_EXAM:
                    entity.setNum(++examNum);
                    break;
                case CATEGORY_H5COURSE_WARE:
                case CATEGORY_ENGLISH_H5COURSE_WARE:
                    entity.setNum(++englishH5Num);
                    break;
                default:
                    entity.setNum(++markNum);
            }
        }
    }


    private class PointListItem implements AdapterItemInterface<VideoPointEntity>{
        private ImageView ivShot;
        private ImageView ivPlay;
        private TextView tvText;
        private VerifyCancelAlertDialog mDialog;
        private View vDelete;
        private View root;
        private View vSig;
        private VideoPointEntity mEntity;
        @Override
        public int getLayoutResId() {
            return R.layout.layout_live_mark_point;
        }

        @Override
        public void initViews(View view) {
            root=view;
            ivShot = (ImageView) view.findViewById(R.id.iv_live_mark_point_shot_pic);
            ivPlay = (ImageView) view.findViewById(R.id.iv_live_mark_point_play);
            tvText = (TextView) view.findViewById(R.id.tv_live_mark_point_text);
            vDelete=view.findViewById(R.id.iv_live_mark_point_delete);
            vSig=view.findViewById(R.id.v_live_mark_point_sig);
        }

        @Override
        public void bindListener() {
            /*root.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(mEntity.getType()!=999){
                        return false;
                    }
                    if(mDialog==null){
                        mDialog=new ChooseListAlertDialog(mContext, ContextManager.getApplication(),false);
                        mDialog.initInfo(new ChooseListAlertDialog.OnChooseItemClickImpl() {
                            @Override
                            public void onItemClick(int ii) {
                                if(ii==R.string.live_mark_point_long_click_tip) {
                                    deletPoint(mEntity);
                                }else{
                                    mDialog.cancelDialog();
                                }
                            }
                        },R.string.live_mark_point_long_click_tip,R.string.live_mark_point_long_click_cancel);
                    }
                    LiveRemarkBll.this.mDialog=mDialog;
                    mDialog.showDialog();
                    return false;
                }
            });*/
            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPlayerService.seekTo(((mEntity.getRelativeTime()>=5&&mEntity.getType()==999)?
                            mEntity.getRelativeTime()-5:mEntity.getRelativeTime())*1000);
                    umsAgentPlay(mEntity.getType());
                    if(LiveRemarkBll.this.mCallBack!=null){
                        LiveRemarkBll.this.mCallBack.onDataSucess();
                    }
                }
            });
            vDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mDialog==null) {
                        mDialog = new VerifyCancelAlertDialog(mContext, ContextManager.getApplication(), false,VerifyCancelAlertDialog.TITLE_MESSAGE_VERIRY_CANCEL_TYPE);
                        mDialog.initInfo("删除标记点", "是否删除标记点？");
                        mDialog.setVerifyBtnListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deletPoint(mEntity);
                                umsAgentDelete();
                            }
                        });
                    }
                    LiveRemarkBll.this.mDialog=mDialog;
                    mDialog.showDialog();
                }
            });
        }

        @Override
        public void updateViews(VideoPointEntity entity, int i, Object o) {
            mEntity=entity;

            ivPlay.setTag(entity.getPic());
            if (!entity.isPlaying()) {
                ivPlay.setVisibility(View.VISIBLE);
            } else {
                ivPlay.setVisibility(View.GONE);
            }
            vDelete.setVisibility(View.GONE);

            StringBuilder sb=new StringBuilder();
            ivShot.setScaleType(ImageView.ScaleType.CENTER);
            switch (entity.getType()){
                case CATEGORY_QUESTION:
                    sb.append("互动题");
                    vSig.setBackgroundResource(R.drawable.shape_corners_4dp_f0773c);
                    ivShot.setImageResource(R.drawable.bg_live_mark_question);
                    break;
                case CATEGORY_REDPACKET:
                    sb.append("红包");
                    vSig.setBackgroundResource(R.drawable.shape_corners_4dp_f13232);
                    ivShot.setImageResource(R.drawable.bg_live_mark_redpack);
                    break;
                case CATEGORY_EXAM:
                    sb.append("测试卷");
                    vSig.setBackgroundResource(R.drawable.shape_corners_4dp_green);
                    ivShot.setImageResource(R.drawable.bg_live_video_mark_exam);
                    break;
                case CATEGORY_H5COURSE_WARE:
                case CATEGORY_ENGLISH_H5COURSE_WARE:
                    sb.append("互动课件");
                    vSig.setBackgroundResource(R.drawable.shape_blue_corners);
                    ivShot.setImageResource(R.drawable.bg_live_video_mark_courceware);
                    break;
                default:
                    ivShot.setScaleType(ImageView.ScaleType.FIT_XY);
                    vDelete.setVisibility(View.VISIBLE);
                    vSig.setBackgroundResource(R.drawable.shape_corners_4dp_f13232);
                    ImageLoader.with(mContext).load(entity.getPic()).placeHolder(R.drawable.bg_default_image).error(R.drawable.bg_default_image).into(ivShot);
                    sb.append("疑问点");
            }
            sb.append(entity.getNum());
            tvText.setText(sb.toString());
            //tvText.setText("疑问点" + (i + 1));
        }
    }
    private void umsAgentMark(boolean success){
        HashMap<String,String> map=new HashMap<>();
        map.put("logtype","clickMark");
        map.put("ex",success?"Y":"N");
        mLiveAndBackDebug.umsAgentDebug2("live_mark",map);
    }
    private void umsAgentMarkButton(){
        HashMap<String,String> map=new HashMap<>();
        map.put("logtype","clickMarkTag");
        mLiveAndBackDebug.umsAgentDebug2("replay_mark",map);
    }
    private void umsAgentPlay(int type){
        HashMap<String,String> map=new HashMap<>();
        map.put("logtype","clickMarkPlay");
        String markType="";
        switch (type){
            case CATEGORY_QUESTION:
                markType="interact";
                break;
            case CATEGORY_REDPACKET:
                markType="redPacket";
                break;
            case CATEGORY_EXAM:
                markType="exam";
                break;
            case CATEGORY_H5COURSE_WARE:
            case CATEGORY_ENGLISH_H5COURSE_WARE:
                markType="other";
                break;
            default:
                markType="query";
                break;
        }
        map.put("marktype",markType);
        mLiveAndBackDebug.umsAgentDebug2("replay_mark",map);
    }
    private void umsAgentDelete(){
        HashMap<String,String> map=new HashMap<>();
        map.put("logtype","clickMarkDelete");
        mLiveAndBackDebug.umsAgentDebug2("replay_mark",map);
    }
}