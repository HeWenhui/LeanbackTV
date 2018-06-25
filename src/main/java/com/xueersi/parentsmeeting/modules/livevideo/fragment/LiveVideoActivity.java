package com.xueersi.parentsmeeting.modules.livevideo.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tal.speech.speechrecognizer.Constants;
import com.xueersi.common.business.AppBll;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.entity.FooterIconEntity;
import com.xueersi.common.event.AppEvent;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.logerhelper.MobEnumUtil;
import com.xueersi.common.logerhelper.XesMobAgent;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.common.speech.SpeechEvaluatorUtils;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.parentsmeeting.module.videoplayer.media.PlayerService.VPlayerListener;
import com.xueersi.parentsmeeting.module.videoplayer.media.VP;
import com.xueersi.parentsmeeting.modules.livevideo.LiveVideoEnter;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoActivity2;
import com.xueersi.parentsmeeting.modules.livevideo.business.ActivityStatic;
import com.xueersi.parentsmeeting.modules.livevideo.business.AudioRequest;
import com.xueersi.parentsmeeting.modules.livevideo.business.BaseLiveMessagePager;
import com.xueersi.parentsmeeting.modules.livevideo.business.EnglishH5Cache;
import com.xueersi.parentsmeeting.modules.livevideo.business.EnglishH5CacheAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.EnglishH5CoursewareBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.EnglishSpeekBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.ExpeBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.H5CoursewareBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LearnReportBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAchievementBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAutoNoticeBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveLazyBllCreat;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveMessageBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveRemarkBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveSpeechCreat;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveVoiceAnswerCreat;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.business.QuestionBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.RankBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.RedPackageBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.SpeechFeedBackAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.SpeechFeedBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.VideoAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.VideoChatBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.WeakHandler;
import com.xueersi.parentsmeeting.modules.livevideo.business.WebViewRequest;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.RolePlayConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic.RoomStatusEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PlayServerEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionWebCache;
import com.xueersi.parentsmeeting.modules.livevideo.rollcall.business.RollCallBll;
import com.xueersi.parentsmeeting.modules.livevideo.teacherpraise.business.TeacherPraiseBll;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;
import com.xueersi.parentsmeeting.modules.livevideo.video.LiveVideoBll;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerTop;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveMediaControllerBottom;
import com.xueersi.parentsmeeting.modules.livevideo.widget.LiveTextureView;
import com.xueersi.parentsmeeting.modules.livevideo.widget.VideoFragment;
import com.xueersi.ui.dialog.VerifyCancelAlertDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import tv.danmaku.ijk.media.player.AvformatOpenInputError;

/**
 * 直播
 *
 * @author linyuqiang
 */
public class LiveVideoActivity extends LiveVideoActivityBase {

    private String TAG = "LiveVideoActivityLog";

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEvent(AppEvent event) {

    }

    /**
     * 只在WIFI下使用激活
     *
     * @param onlyWIFIEvent
     * @author zouhao
     * @Create at: 2015-9-24 下午1:57:04
     */
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEvent(AppEvent.OnlyWIFIEvent onlyWIFIEvent) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {
            /** 点击空白位置 隐藏软键盘 */
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onUserBackPressed() {
        liveVideoFragmentBase.onUserBackPressed();
    }

    @Override
    public void onDestroy() {
        AppBll.getInstance().unRegisterAppEvent(this);
        super.onDestroy();
    }

    @Override
    protected LiveVideoFragmentBase getFragment() {
        return new LiveVideoActivity2();
    }

    @Override
    protected void updateIcon() {
        LiveVideoActivity2 liveVideoFragment = (LiveVideoActivity2) liveVideoFragmentBase;
        liveVideoFragment.updateIcon();
    }

    /**
     * 跳转到播放器
     *
     * @param context
     * @param bundle
     * @param requestCode
     */
    public static void intentTo(Activity context, Bundle bundle, int requestCode) {
        Intent intent = new Intent(context, LiveVideoActivity.class);
        intent.putExtras(bundle);
        context.startActivityForResult(intent, requestCode);
    }


}
