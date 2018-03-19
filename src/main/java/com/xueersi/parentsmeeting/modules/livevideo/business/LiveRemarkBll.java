package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.xueersi.parentsmeeting.config.FileConfig;
import com.xueersi.parentsmeeting.http.HttpCallBack;
import com.xueersi.parentsmeeting.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LivePlayBackVideoActivity;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoActivity;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveVideoView;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveTextureView;
import com.xueersi.parentsmeeting.modules.videoplayer.media.MIJKMediaPlayer;
import com.xueersi.parentsmeeting.modules.videoplayer.media.MediaController;
import com.xueersi.parentsmeeting.modules.videoplayer.media.PlayerService;
import com.xueersi.parentsmeeting.modules.videoplayer.media.VideoView;
import com.xueersi.parentsmeeting.modules.videoplayer.media.XESMediaPlayer;
import com.xueersi.parentsmeeting.widget.LivePlaybackMediaController;
import com.xueersi.xesalib.adapter.AdapterItemInterface;
import com.xueersi.xesalib.adapter.CommonAdapter;
import com.xueersi.xesalib.adapter.RCommonAdapter;
import com.xueersi.xesalib.adapter.RItemViewInterface;
import com.xueersi.xesalib.adapter.ViewHolder;
import com.xueersi.xesalib.utils.app.ContextManager;
import com.xueersi.xesalib.utils.app.XESToastUtils;
import com.xueersi.xesalib.utils.log.Loger;
import com.xueersi.xesalib.utils.uikit.ImageUtils;
import com.xueersi.xesalib.utils.uikit.SizeUtils;
import com.xueersi.xesalib.utils.uikit.imageloader.ImageLoader;
import com.xueersi.xesalib.view.alertdialog.ChooseListAlertDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import tv.danmaku.ijk.media.player.FrameInfo;

/**
 * Created by Tang on 2018/3/5.
 */

public class LiveRemarkBll {
    private Context mContext;
    private MIJKMediaPlayer mPlayer;
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
    private List<PointEntity> mList;
    private ListView lvPoints;
    private LinearLayout llPoints;
    private RelativeLayout rlMask;
    private RCommonAdapter mAdapter;
    private CommonAdapter mAdapter1;
    private TextureView mTextureView;
    private long lastMarkTime;
    private MediaController mController;
    private AbstractBusinessDataCallBack mCallBack;
    private String liveId;

    public LiveRemarkBll(Context context, XESMediaPlayer player) {
        mContext = context;
        mPlayer = (MIJKMediaPlayer) player;
        initData();
    }
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

    private void initData() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (mPlayer == null) {
                    return;
                }
                long tcpSpeed = mPlayer.getTcpSpeed();
                float vdfps = mPlayer.getVideoDecodeFramesPerSecond();
                if(mLiveMediaControllerBottom==null){
                    return;
                }
                if (Math.round(vdfps) == 12) {
                    //mTimer.cancel();
                    Loger.i(TAG, "dfps   " + vdfps);
                    FrameInfo frameInfo = mPlayer.native_getFrameInfo();
                    offSet = System.currentTimeMillis()/1000+sysTimeOffset - frameInfo.pkt/1000;
                    Loger.i(TAG, "nowtime  " + frameInfo.nowTime + "   dts     " + frameInfo.pkt_dts
                            + "   pkt   " + frameInfo.pkt + "  cache:" + mPlayer.getVideoCachedDuration());
                    mLiveMediaControllerBottom.getBtMark().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            if(System.currentTimeMillis()-lastMarkTime<15000){
                                XESToastUtils.showToast(mContext,"你标记太快了");
                                return;
                            }
                            final LiveTextureView liveTextureView = (LiveTextureView) ((Activity) mContext).findViewById(R.id.ltv_course_video_video_texture);
                            if (liveTextureView == null) {
                                return;
                            }
                            final LiveVideoView liveVideoView = (LiveVideoView) ((Activity) mContext).findViewById(R.id.vv_course_video_video);
//                liveVideoView.setVisibility(View.INVISIBLE);
                            mPlayer.setSurface(liveTextureView.surface);
                            v.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mPlayer.setDisplay(liveVideoView.getSurfaceHolder());
                                    v.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            mPlayer.setSurface(liveTextureView.surface);
                                            v.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Bitmap bitmap = liveTextureView.getBitmap();
                                                    if (bitmap == null) {
                                                        XESToastUtils.showToast(mContext, "标记获取图片失败");
                                                        return;
                                                    }
                                                    bitmap=Bitmap.createBitmap(bitmap,0,0,(int)videoWidth,displayHeight);
                                                    File saveDir = new File(Environment.getExternalStorageDirectory(), "parentsmeeting/save");
                                                    if (!saveDir.exists()) {
                                                        saveDir.mkdirs();
                                                    }
                                                    File file = new File(saveDir, "" + System.currentTimeMillis() + ".png");
                                                    ImageUtils.save(bitmap, file, Bitmap.CompressFormat.JPEG);
                                                    reMark(file);
                                                    mPlayer.setDisplay(liveVideoView.getSurfaceHolder());
                                                }
                                            }, 100);
                                        }
                                    }, 100);
                                }
                            }, 100);
                        }
                    });
                    mTimer.cancel();
                }

            }
        };
        mTimer = new Timer();
        mTimer.schedule(task, 1000, 1000);
        mCloudUploadBusiness = new XesCloudUploadBusiness(mContext);
    }

    public void hideBtMark() {
        if (mLiveMediaControllerBottom != null) {
            mLiveMediaControllerBottom.getBtMark().setVisibility(View.GONE);
        }
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

    public List<PointEntity> getList() {
        return mList;
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
        final long time = mPlayer.native_getFrameInfo().pkt/1000 - mPlayer.getVideoCachedDuration()/1000 + offSet;
        Loger.i(TAG,"frameTime:"+mPlayer.native_getFrameInfo().pkt/1000);
        Loger.i(TAG,"cacheTime:"+mPlayer.getVideoCachedDuration()/1000);
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
                    mHttpManager.saveLiveMark("" + time, result.getHttpPath(), new HttpCallBack() {
                        @Override
                        public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                            XESToastUtils.showToast(mContext, "标记成功");
                            lastMarkTime=System.currentTimeMillis();
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

                @Override
                public void onError(XesCloudResult result) {
                    Loger.i(TAG, result.getErrorMsg());
                }
            });
        } else {
            XESToastUtils.showToast(mContext, "标记上传失败");
        }

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
        mHttpManager.getMarkPoints(liveId,new HttpCallBack() {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                JSONArray points = ((JSONObject) responseEntity.getJsonObject()).optJSONArray("markpointData");
                if (mList == null) {
                    mList = new ArrayList<>();
                }
                mList.clear();
                if (points != null) {
                    for (int i = 0; i < points.length(); i++) {
                        PointEntity entity = new PointEntity();
                        entity.setCurTime(points.optJSONObject(i).optLong("cur_time"));
                        entity.setRelativeTime(points.optJSONObject(i).optLong("relativeTime"));
                        entity.setPic(points.optJSONObject(i).optString("image_url"));
                        entity.setBigenTime(points.optJSONObject(i).optLong("image_url"));
                        mList.add(entity);
                    }
                }
                if(mList.size()>0){
                    callBack.onDataSucess();
                }
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
            mAdapter1=new CommonAdapter<PointEntity>(mList) {
                @Override
                public AdapterItemInterface getItemView(Object o) {
                    return new PointListItem();
                }
            };
            lvPoints.setAdapter(mAdapter1);

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
        mAdapter1.notifyDataSetChanged();
    }

    public void hideMarkPoints() {
        if (rlMask != null) {
            rlMask.setVisibility(View.GONE);
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
    private void deletPoint(final PointEntity entity){
        mHttpManager.deleteMarkPoints(liveId,entity.getCurTime(),new HttpCallBack(){
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                try{
                    mList.remove(entity);
                    mAdapter1.notifyDataSetChanged();
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

    private class PointEntity {
        String pic;
        long curTime;
        long relativeTime;
        long bigenTime;
        boolean isPlaying;

        public long getBigenTime() {
            return bigenTime;
        }

        public void setBigenTime(long bigenTime) {
            this.bigenTime = bigenTime;
        }

        public boolean isPlaying() {
            return isPlaying;
        }

        public void setPlaying(boolean playing) {
            isPlaying = playing;
        }

        public long getRelativeTime() {
            return relativeTime;
        }

        public void setRelativeTime(long relativeTime) {
            this.relativeTime = relativeTime;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public long getCurTime() {
            return curTime;
        }

        public void setCurTime(long time) {
            this.curTime = time;
        }
    }
    private class PointListItem implements AdapterItemInterface<PointEntity>{
        private ImageView ivShot;
        private ImageView ivPlay;
        private TextView tvText;
        private ChooseListAlertDialog mDialog;
        private View root;
        private PointEntity mEntity;
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
        }

        @Override
        public void bindListener() {
            root.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(mDialog==null){
                        mDialog=new ChooseListAlertDialog(mContext, ContextManager.getApplication(),false);
                        mDialog.initInfo(new ChooseListAlertDialog.OnChooseItemClickImpl() {
                            @Override
                            public void onItemClick(int ii) {
                                deletPoint(mEntity);
                            }
                        },R.string.live_mark_point_long_click_tip);
                    }
                    mDialog.showDialog();
                    return false;
                }
            });
            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!mEntity.isPlaying){
                        //ivPlay.setVisibility(View.GONE);

                        mPlayerService.seekTo(mEntity.relativeTime*1000);
                        for(int j=0;j<mList.size();j++){
                                mList.get(j).isPlaying = false;

                        }
                        mEntity.isPlaying=true;
                        mAdapter1.notifyDataSetChanged();
                    }
                }
            });
        }

        @Override
        public void updateViews(PointEntity entity, int i, Object o) {
            mEntity=entity;
            ImageLoader.with(mContext).load(entity.getPic()).error(R.drawable.bg_default_image).into(ivShot);
            ivPlay.setTag(entity.getPic());
            if (!entity.isPlaying) {
                ivPlay.setVisibility(View.VISIBLE);
            } else {
                ivPlay.setVisibility(View.GONE);
            }
            tvText.setText("疑问点" + (i + 1));
        }
    }
}
