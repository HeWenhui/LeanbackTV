package com.xueersi.parentsmeeting.modules.livevideo.miracast;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import com.hpplay.sdk.source.api.IConnectListener;
import com.hpplay.sdk.source.api.ILelinkMirrorManager;
import com.hpplay.sdk.source.api.ILelinkPlayerListener;
import com.hpplay.sdk.source.api.LelinkPlayer;
import com.hpplay.sdk.source.api.LelinkPlayerInfo;
import com.hpplay.sdk.source.browse.api.IBrowseListener;
import com.hpplay.sdk.source.browse.api.ILelinkServiceManager;
import com.hpplay.sdk.source.browse.api.LelinkServiceInfo;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;

import java.util.ArrayList;
import java.util.List;

import static com.xueersi.lib.framework.are.ContextManager.getApplication;

/**
 * Created by: WangDe on 2019/2/25
 */
public class MiracastPager extends LiveBasePager  {
    private Button btnSearch;
    private Button btnPlay;
    private Button btnPause;
    private Button btnStop;
    private Button btnVolumeUp;
    private Button btnVolumeDown;
    private ILelinkServiceManager lelinkServiceManager;
    private Button btnConnect;
    private LelinkPlayer leLinkPlayer;
    private RecyclerView rcDevView;
    boolean isSearch = false;
    private IBrowseListener browserListener;
    private IConnectListener connectListener;
    private ILelinkPlayerListener playerListener;
    private LelinkServiceInfo connectInfo;
    private static final int REQUEST_MUST_PERMISSION = 1;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 4;
    private List<LelinkServiceInfo> devList;
    private MiracastAdapter miracastAdapter;
    private boolean isConnect = false;
    private String url;
    private boolean isPause = false;

    public MiracastPager(Context context) {
        super(context);
        devList = new ArrayList<LelinkServiceInfo>();
        initListener();
        initData();
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.pager_miracast, null);
        btnSearch = mView.findViewById(R.id.btn_search);
        btnPlay = mView.findViewById(R.id.btn_play);
        btnPause = mView.findViewById(R.id.btn_pause);
        btnStop = mView.findViewById(R.id.btn_stop);
        btnVolumeUp = mView.findViewById(R.id.btn_volume_up);
        btnVolumeDown = mView.findViewById(R.id.btn_volume_down);
        rcDevView = mView.findViewById(R.id.rc_device);
        return mView;
    }

    @Override
    public void initData() {
        miracastAdapter = new MiracastAdapter(mContext);
        rcDevView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        rcDevView.setAdapter(miracastAdapter);
        miracastAdapter.setOnItemClickListener(mOnItemClickListener);
        super.initData();
    }

    @Override
    public void initListener() {
        btnPause.setEnabled(false);
        btnStop.setEnabled(false);
        btnVolumeUp.setEnabled(false);
        btnVolumeDown.setEnabled(false);
        btnPlay.setEnabled(false);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isConnect = false;
                if (ContextCompat.checkSelfPermission(getApplication(),
                        Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_DENIED
                        && ContextCompat.checkSelfPermission(mContext.getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_DENIED) {
                    if (!isSearch) {
                        lelinkServiceManager.browse(ILelinkServiceManager.TYPE_ALL);
                        isSearch = true;
                        btnSearch.setText("停止搜索");
                    } else {
                        lelinkServiceManager.stopBrowse();
                        isSearch = false;
                        btnSearch.setText("搜索");
                    }
                } else {
                    // 若没有授权，会弹出一个对话框（这个对话框是系统的，开发者不能自己定制），用户选择是否授权应用使用系统权限
                    ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_MUST_PERMISSION);
                }
            }
        });
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_DENIED) {
                    // 同意权限
                    LelinkPlayerInfo lelinkPlayerInfo = new LelinkPlayerInfo();
                    lelinkPlayerInfo.setType(LelinkPlayerInfo.TYPE_MIRROR);
                    lelinkPlayerInfo.setActivity((Activity) mContext);
                    lelinkPlayerInfo.setLelinkServiceInfo(connectInfo);
                    lelinkPlayerInfo.setMirrorAudioEnable(true);
                    lelinkPlayerInfo.setResolutionLevel(ILelinkMirrorManager.RESOLUTION_AUTO);
                    lelinkPlayerInfo.setBitRateLevel(ILelinkMirrorManager.BITRATE_LOW);
//                    lelinkPlayerInfo.setUrl(url);
                    leLinkPlayer.setDataSource(lelinkPlayerInfo);
                    leLinkPlayer.start();
                    btnPause.setEnabled(true);
                    btnStop.setEnabled(true);
                    btnVolumeUp.setEnabled(true);
                    btnVolumeDown.setEnabled(true);
                } else {
                    // 不同意，则去申请权限
                    ActivityCompat.requestPermissions((Activity) mContext, new String[]{
                            Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
                }
            }
        });
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPause){
                    leLinkPlayer.resume();
                    mView.post(new Runnable() {
                        @Override
                        public void run() {
                            btnPause.setText("暂停");
                        }
                    });
                }else {
                    leLinkPlayer.pause();
                    mView.post(new Runnable() {
                        @Override
                        public void run() {
                            btnPause.setText("继续");
                        }
                    });
                }
                isPause =  !isPause;
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leLinkPlayer.stop();
                btnPause.setEnabled(false);
                btnStop.setEnabled(false);
                btnVolumeUp.setEnabled(false);
                btnVolumeDown.setEnabled(false);
            }
        });
        btnVolumeUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leLinkPlayer.addVolume();
            }
        });
        btnVolumeDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leLinkPlayer.subVolume();
            }
        });

    }

    public void setILelinkServiceManager(ILelinkServiceManager iLelinkServiceManager) {
        this.lelinkServiceManager = iLelinkServiceManager;
    }

    public void setLeLinkPlayer(LelinkPlayer leLinkPlayer) {
        this.leLinkPlayer = leLinkPlayer;
    }

    public void setmLinklist(final List<LelinkServiceInfo> linklist) {
        if (!isConnect){
            mView.post(new Runnable() {
                @Override
                public void run() {
                    devList = linklist;
                    miracastAdapter.updateDatas(devList);
                    miracastAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void onDestroy() {
        if(leLinkPlayer != null){
            if (connectInfo != null){
                leLinkPlayer.disConnect(connectInfo);
            }
            leLinkPlayer.stop();
            leLinkPlayer.release();
        }
        if (lelinkServiceManager!=null){
            lelinkServiceManager.stopBrowse();
        }
        super.onDestroy();
    }

    public IMiracastState getiMiracastState() {
        return iMiracastState;
    }

    IOnItemClickListener mOnItemClickListener = new IOnItemClickListener() {
        @Override
        public void onClick(int pos, LelinkServiceInfo info) {
            if (leLinkPlayer!= null){
                leLinkPlayer.connect(info);
                miracastAdapter.notifyDataSetChanged();
            }
        }
    };

    private IMiracastState iMiracastState = new IMiracastState() {
        @Override
        public void onConnect() {
            btnPlay.setEnabled(true);
        }

        @Override
        public void onDisConnect() {
            btnPlay.setEnabled(false);
            btnPause.setEnabled(false);
            btnStop.setEnabled(false);
            btnVolumeUp.setEnabled(false);
            btnVolumeDown.setEnabled(false);
        }

        @Override
        public void onSearch() {

        }

        @Override
        public void onStart() {
            btnPause.setEnabled(true);
            btnStop.setEnabled(true);
            btnVolumeUp.setEnabled(true);
            btnVolumeDown.setEnabled(true);
        }

        @Override
        public void onPause() {

        }

        @Override
        public void onStop() {

        }
    };
}
