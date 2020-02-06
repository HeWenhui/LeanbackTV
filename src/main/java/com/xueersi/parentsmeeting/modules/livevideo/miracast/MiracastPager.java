package com.xueersi.parentsmeeting.modules.livevideo.miracast;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hpplay.sdk.source.api.IConnectListener;
import com.hpplay.sdk.source.api.ILelinkMirrorManager;
import com.hpplay.sdk.source.api.ILelinkPlayerListener;
import com.hpplay.sdk.source.api.LelinkPlayer;
import com.hpplay.sdk.source.api.LelinkPlayerInfo;
import com.hpplay.sdk.source.browse.api.IBrowseListener;
import com.hpplay.sdk.source.browse.api.ILelinkServiceManager;
import com.hpplay.sdk.source.browse.api.LelinkServiceInfo;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.utils.BuryUtil;

import java.util.ArrayList;
import java.util.List;

import static com.xueersi.lib.framework.are.ContextManager.getApplication;

/**
 * Created by: WangDe on 2019/2/25
 */
public class MiracastPager extends LiveBasePager {
    private TextView mTitleTv;
    private ImageButton mBackIvTtn;

    private ILelinkServiceManager lelinkServiceManager;
    private LelinkPlayer leLinkPlayer;
    private RecyclerView rcDevView;
    boolean isSearch = false;
    private IBrowseListener browserListener;
    private IConnectListener connectListener;
    private ILelinkPlayerListener playerListener;
    private LelinkServiceInfo connectInfo;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 4;
    private List<LelinkServiceInfo> devList;
    private MiracastAdapter miracastAdapter;
    private boolean isConnect = false;
    private String url;
    private boolean isPause = false;
    private WebView mWebView;
    private TextView mSearchLoadingView;

    private TextView mTvPlayerTopTips;

    private RelativeLayout mEmtpySearchGroup;
    private TextView mEmtpyRefreshBtn;
    private TextView mEmptyTipsTv;
    private boolean isSearchSuccess = false;
    private ImageView mRefreshSearchBtn;

    public static final int FROM_PLAYBACK = 1;
    public static final int FROM_LIVE = 2;

    private int mFrom = FROM_LIVE;
    private String mStuCourseId;

    public MiracastPager(Context context, int from, String stuCourseId) {
        super(context);
        mFrom = from;
        mStuCourseId = stuCourseId;
        devList = new ArrayList<LelinkServiceInfo>();
        initListener();
        initData();
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.pager_miracast, null);
        rcDevView = mView.findViewById(R.id.rc_device);
        mBackIvTtn = mView.findViewById(R.id.imgbtn_title_bar_back);
        mTitleTv = mView.findViewById(R.id.tv_title_bar_content);
        mSearchLoadingView = mView.findViewById(R.id.letou_describe_searching);
        mTvPlayerTopTips = mView.findViewById(R.id.tv_player_top_tips);
        mEmtpySearchGroup = mView.findViewById(R.id.letou_describe_empty_rl);
        mEmtpyRefreshBtn = mView.findViewById(R.id.letou_describe_empty_refresh_btn);
        mEmptyTipsTv = mView.findViewById(R.id.letou_describe_empty_tips_tv);
        mEmtpySearchGroup.setVisibility(View.GONE);
        mRefreshSearchBtn = mView.findViewById(R.id.imgbtn_title_bar_refresh);
        initWebView();
        return mView;
    }

    private void initWebView() {
        mWebView = mView.findViewById(R.id.letou_describe_webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.loadUrl("http://xeswxapp.oss-cn-beijing.aliyuncs.com/files/Touping/index.html");
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
        mTitleTv.setText("选择投屏设备");
        mBackIvTtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup parent = (ViewGroup) getRootView().getParent();
                if (parent != null) {
                    parent.removeView(getRootView());
                }
            }
        });
        mEmtpyRefreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFrom == FROM_LIVE) {
                    BuryUtil.click(R.string.click_03_63_046, mStuCourseId);
                } else {
                    BuryUtil.click(R.string.click_03_84_021, mStuCourseId);
                }
                hildEmtpyLayout();
                startSearch();
            }
        });
        mRefreshSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setmLinklist(new ArrayList<LelinkServiceInfo>());
                mView.post(new Runnable() {
                    @Override
                    public void run() {
                        startSearch();
                    }
                });

            }
        });


    }

    public void swtichTips(boolean isWifi) {
        if (isWifi) {
            mTvPlayerTopTips.setText("当前使用WIFI连接");
            Drawable drawable = mContext.getResources().getDrawable(R.drawable.icon_tv_player_stat_wifi);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mTvPlayerTopTips.setCompoundDrawables(drawable, null, null, null);
            mTvPlayerTopTips.setCompoundDrawablePadding(SizeUtils.Dp2Px(mContext, 4));
            // mEmtpySearchGroup.setVisibility(View.GONE);
        } else {
            mTvPlayerTopTips.setText("当前使用流量连接");
            Drawable drawable = mContext.getResources().getDrawable(R.drawable.icon_tv_player_stat_liuliang);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mTvPlayerTopTips.setCompoundDrawables(drawable, null, null, null);
            mTvPlayerTopTips.setCompoundDrawablePadding(SizeUtils.Dp2Px(mContext, 4));
            mEmtpySearchGroup.setVisibility(View.VISIBLE);
            mEmtpyRefreshBtn.setVisibility(View.GONE);
        }
    }


    public void showEmtpyLayout() {
        mEmtpySearchGroup.setVisibility(View.VISIBLE);
        mEmtpyRefreshBtn.setVisibility(View.VISIBLE);
        mEmptyTipsTv.setText("暂未搜索到设备信息");

    }

    public void hildEmtpyLayout() {
        mEmtpySearchGroup.setVisibility(View.GONE);
    }


    public void playTv() {
        if (ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_DENIED) {
            // 同意权限
            LelinkPlayerInfo lelinkPlayerInfo = new LelinkPlayerInfo();
            lelinkPlayerInfo.setType(LelinkPlayerInfo.TYPE_VIDEO);
//                    lelinkPlayerInfo.setActivity((Activity) mContext);
            lelinkPlayerInfo.setLelinkServiceInfo(connectInfo);
//                    lelinkPlayerInfo.setMirrorAudioEnable(true);
            lelinkPlayerInfo.setResolutionLevel(ILelinkMirrorManager.RESOLUTION_AUTO);
            lelinkPlayerInfo.setBitRateLevel(ILelinkMirrorManager.BITRATE_LOW);
            lelinkPlayerInfo.setUrl(url);
            leLinkPlayer.setDataSource(lelinkPlayerInfo);
            leLinkPlayer.start();

        } else {
            // 不同意，则去申请权限
            ActivityCompat.requestPermissions((Activity) mContext, new String[]{
                    Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
        }
    }

    public void startSearch() {
        lelinkServiceManager.browse(ILelinkServiceManager.TYPE_ALL);
        isSearchSuccess = false;
        isSearch = true;
        mSearchLoadingView.setText("设备搜索中...");
        registDelayCheckEmpty();

    }

    public void registDelayCheckEmpty() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isSearchSuccess) {
                    stopSearch();
                    showSearchFaild();
                }
            }
        }, 30000);
    }

    private void showSearchFaild() {
        if (mFrom == FROM_LIVE) {
            BuryUtil.show(R.string.show_03_63_048, mStuCourseId);
        } else {
            BuryUtil.show(R.string.show_03_84_023, mStuCourseId);
        }
        showEmtpyLayout();
    }

    public void setNetworkStatus(boolean isWifi) {
        swtichTips(isWifi);
    }

    public void stopSearch() {
        lelinkServiceManager.stopBrowse();

        isSearch = false;
        mSearchLoadingView.post(new Runnable() {
            @Override
            public void run() {
                mSearchLoadingView.setText("");
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
        if (linklist != null && !linklist.isEmpty()) {
            isSearchSuccess = true;
            if (mFrom == FROM_LIVE) {
                BuryUtil.show(R.string.show_03_63_047, mStuCourseId);
            } else {
                BuryUtil.show(R.string.show_03_84_022, mStuCourseId);
            }
        }
        if (!isConnect) {
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
      /*  if(leLinkPlayer != null){
            if (connectInfo != null){
                leLinkPlayer.disConnect(connectInfo);
            }
            leLinkPlayer.stop();
            leLinkPlayer.release();
        }
        if (lelinkServiceManager!=null){
            lelinkServiceManager.stopBrowse();
        }*/
        super.onDestroy();
    }

    public IMiracastState getiMiracastState() {
        return iMiracastState;
    }

    IOnItemClickListener mOnItemClickListener = new IOnItemClickListener() {
        @Override
        public void onClick(int pos, LelinkServiceInfo info) {

            if (leLinkPlayer != null) {
                leLinkPlayer.connect(info);
                miracastAdapter.notifyDataSetChanged();
            }
            if (mFrom == FROM_LIVE) {
                BuryUtil.click(R.string.click_03_63_045, mStuCourseId);
            } else {
                BuryUtil.click(R.string.click_03_84_027, mStuCourseId);
            }
        }
    };

    private IMiracastState iMiracastState = new IMiracastState() {
        @Override
        public void onConnect() {
            stopSearch();
            playTv();
        }

        @Override
        public void onDisConnect() {


        }

        @Override
        public void onSearch() {

        }

        @Override
        public void onStart() {


        }

        @Override
        public void onPause() {

        }

        @Override
        public void onStop() {

        }
    };
}
