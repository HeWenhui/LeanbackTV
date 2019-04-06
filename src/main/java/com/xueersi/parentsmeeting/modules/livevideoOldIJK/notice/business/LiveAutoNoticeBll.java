package com.xueersi.parentsmeeting.modules.livevideoOldIJK.notice.business;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.widget.SlowHorizontalScrollView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Tang on 2018/3/10.
 */

public class LiveAutoNoticeBll extends LiveBaseBll {
    private Context mContext;
    private int videoWidth;
    private int displayHeight;
    private int displayWidth;
    private int wradio;
    private View root;
    private SlowHorizontalScrollView mSlowHorizontalScrollView;
    private View vLeft, vRight;
    private ImageView ivAvatar;
    private TextView tvContent;
    private RelativeLayout bottom;
    private boolean isShowing;
    private LiveHttpManager mHttpManager;
    private String classId;
    private String testId;
    private String srcType;
    private String teacherName;
    private String teacherImg;
    private String liveId;
    private LiveAndBackDebug mLiveBll;
    private Runnable mRunnable;
    private int grade;
    String TAG = this.getClass().getSimpleName();
    Pattern pattern = Pattern.compile("#[0-9]#");
    String[] emojis = {"\uD83D\uDE22", "\uD83D\uDE44", "\uD83D\uDE31", "\uD83D\uDE02", "\uD83D\uDE15"};
    //int[] emoji={R.drawable.live_emoji_1,R.drawable.live_emoji_2,R.drawable.live_emoji_3};
    /**
     * 文案
     */
    String[] noticeLowLevel = {"不要在上课期间发表不合适的言论哦。",
            "说不好的话会被禁言哦～",
            "我看到啦，下次注意哦～",
            "小心我禁言哦～"};
    String[] noticeHighLevel = {"不要在上课期间发表不合适的言论。",
            "禁止脏话及敏感词汇，你会被禁言。",
            "我会收到你发的被屏蔽的留言，别发喽！",
            "脏话及敏感词汇会被屏蔽掉，别再发了。"};
    String[] noticeGaosan = {"网校公安局提醒你，请注意言辞！"
            , "警察叔叔还有30秒到达战场，请注意敏感词汇。"
            , "富强民主文明和谐自由平等公正法治……"};


    public LiveAutoNoticeBll(Activity context, LiveBll2 liveBll2, RelativeLayout bottom) {
        super(context, liveBll2);
        this.mContext = context;
        this.bottom = bottom;
        setLayout(1920, 1080);
    }

    public void setLayout(int width, int height) {
        int screenWidth = getScreenParam();
        displayHeight = height;
        displayWidth = screenWidth;
        if (width > 0) {
            wradio = (int) (LiveVideoConfig.VIDEO_HEAD_WIDTH * width / LiveVideoConfig.VIDEO_WIDTH);
            wradio += (screenWidth - width) / 2;
            if (displayWidth - wradio == videoWidth) {
                return;
            } else {
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

    public void setBottom(RelativeLayout bottom) {
        this.bottom = bottom;
    }


    public void setClassId(String classId) {
        this.classId = classId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public void setSrcType(String srcType) {
        this.srcType = srcType;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public void setLiveId(String liveId) {
        this.liveId = liveId;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public void setTeacherImg(String teacherImg) {
        this.teacherImg = teacherImg;
    }

    public void setLiveBll(LiveAndBackDebug liveBll) {
        mLiveBll = liveBll;
    }

    public void setHttpManager(LiveHttpManager httpManager) {
        mHttpManager = httpManager;
    }

    /**
     * 敏感词提示
     *
     * @param name
     * @param head
     */
    public void showNotice(String name, String head) {
        try {
            if (Integer.parseInt(classId) < 0) {
                return;
            }
            String content = null;
            int i = ShareDataManager.getInstance().getInt("LiveAutoNotice_" + liveId, -1, ShareDataManager.SHAREDATA_USER);
            /*if (grade >= 2 && grade <= 3) {
                content = noticeLowLevel[(i + 1) % 4];
            } else if (grade >= 4 && grade <= 7) {
                content = noticeHighLevel[(i + 1) % 4];
            } else*/ if (grade == 13) {
                content = noticeGaosan[(i + 1) % 3];
            } else {
                return;
            }
            showNotice(name, content, head);
            //showNotice(name,parseEmoji("不要#1#说#2#脏话哦#3###"),head);
            umsAgent(3, true);
            ShareDataManager.getInstance().put("LiveAutoNotice_" + liveId, i + 1, ShareDataManager.SHAREDATA_USER);
            mHttpManager.autoNoticeStatisc(classId, new HttpCallBack(false) {
                @Override
                public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                    umsAgentReq(true);
                }

                @Override
                public void onPmFailure(Throwable error, String msg) {
                    super.onPmFailure(error, msg);
                    umsAgentReq(false);
                }

                @Override
                public void onPmError(ResponseEntity responseEntity) {
                    super.onPmError(responseEntity);
                    umsAgentReq(false);
                }
            });
            //showNotice(name, notice[1][0], head);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示智能私信
     *
     * @param name
     * @param s
     * @param head
     */
    public void showNotice(String name, CharSequence s, String head) {
//         if (isShowing) {
//            return;
//        }
//        isShowing = true;
        if (mRunnable == null) {
            mRunnable = new Runnable() {
                @Override
                public void run() {
                    bottom.removeView(root);
                    isShowing = false;
                }
            };
        }
        try {
            if (root == null) {
                if (LiveVideoConfig.isPrimary) {
                    root = View.inflate(mContext, R.layout.layout_live_auto_psnotice, null);
                } else {
                    root = View.inflate(mContext, R.layout.layout_live_auto_notice, null);
                }
                mSlowHorizontalScrollView = (SlowHorizontalScrollView) root.findViewById(R.id.sv_live_auto_notice);
                vLeft = root.findViewById(R.id.v_live_auto_notice_left);
                vRight = root.findViewById(R.id.v_live_auto_notice_right);
                ivAvatar = (ImageView) root.findViewById(R.id.iv_live_auto_notice_avatar);
                tvContent = (TextView) root.findViewById(R.id.tv_live_auto_notice_content);
            } else {
                bottom.removeView(root);
            }
            ImageLoader.with(mContext).load(head).error(R.drawable.ic_default_head_square).into(ivAvatar);
            SpannableString content = new SpannableString(name + "@你  " + s);
            StyleSpan span = new StyleSpan(Typeface.BOLD);
            try {
                content.setSpan(span, 0, name.length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } catch (Exception e) {
                e.printStackTrace();
            }
            tvContent.setText(content);
            RelativeLayout.LayoutParams rootParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rootParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            rootParam.setMargins(0, 0, 0, 40);
//            if (!isShowing) {
//                bottom.addView(root, 1, rootParam);
//                isShowing = true;
//            }
            bottom.addView(root, rootParam);
            LinearLayout.LayoutParams svParam = new LinearLayout.LayoutParams(videoWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
            mSlowHorizontalScrollView.setLayoutParams(svParam);
            LinearLayout.LayoutParams vParam = new LinearLayout.LayoutParams(videoWidth, 1);
            vLeft.setLayoutParams(vParam);
            vRight.setLayoutParams(vParam);
            TextPaint paint = new TextPaint();
            paint.setTextSize(SizeUtils.Dp2Px(mContext, 12));
            int tvWidth = (int) paint.measureText(content.toString());
            LinearLayout.LayoutParams tvParam = new LinearLayout.LayoutParams(tvWidth + 40, ViewGroup.LayoutParams.WRAP_CONTENT);
            tvParam.setMargins(10, 0, 0, 0);
            tvContent.setLayoutParams(tvParam);
            tvContent.setSingleLine();
            mSlowHorizontalScrollView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
            mSlowHorizontalScrollView.setHorizontalScrollBarEnabled(false);
            mSlowHorizontalScrollView.scrollTo(0, 0);
            int last = Math.max(videoWidth, tvWidth) * 4;
            mSlowHorizontalScrollView.smoothScrollToSlow(videoWidth + tvWidth + 200, 0, last);
            mSlowHorizontalScrollView.getHandler().removeCallbacks(mRunnable);
            mSlowHorizontalScrollView.postDelayed(mRunnable, last);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示送礼物成功的文案提示
     *
     * @param s
     * @param head
     */
    public void showGiftSuccessNotice(String s, Drawable head) {
//         if (isShowing) {
//            return;
//        }
//        isShowing = true;
        if (mRunnable == null) {
            mRunnable = new Runnable() {
                @Override
                public void run() {
                    bottom.removeView(root);
                    isShowing = false;
                }
            };
        }
        try {
            if (root == null) {
                root = View.inflate(mContext, R.layout.layout_live_auto_notice, null);
                mSlowHorizontalScrollView = (SlowHorizontalScrollView) root.findViewById(R.id.sv_live_auto_notice);
                vLeft = root.findViewById(R.id.v_live_auto_notice_left);
                vRight = root.findViewById(R.id.v_live_auto_notice_right);
                ivAvatar = (ImageView) root.findViewById(R.id.iv_live_auto_notice_avatar);
                tvContent = (TextView) root.findViewById(R.id.tv_live_auto_notice_content);
            }
            ivAvatar.setImageDrawable(head);
//            ImageLoader.with(mContext).load(head).error(R.drawable.ic_default_head_square).into(ivAvatar);
//            SpannableString content = new SpannableString(name + "@你  " + s);
//            StyleSpan span = new StyleSpan(Typeface.BOLD);
//            try {
//                content.setSpan(span, 0, name.length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            tvContent.setText(s);
            RelativeLayout.LayoutParams rootParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rootParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            rootParam.setMargins(0, 0, 0, 40);
            if (!isShowing) {
                bottom.addView(root, 1, rootParam);
                isShowing = true;
            }
            LinearLayout.LayoutParams svParam = new LinearLayout.LayoutParams(videoWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
            mSlowHorizontalScrollView.setLayoutParams(svParam);
            LinearLayout.LayoutParams vParam = new LinearLayout.LayoutParams(videoWidth, 1);
            vLeft.setLayoutParams(vParam);
            vRight.setLayoutParams(vParam);
            TextPaint paint = new TextPaint();
            paint.setTextSize(SizeUtils.Dp2Px(mContext, 12));
            int tvWidth = (int) paint.measureText(s.toString());
            LinearLayout.LayoutParams tvParam = new LinearLayout.LayoutParams(tvWidth + 40, ViewGroup.LayoutParams.WRAP_CONTENT);
            tvParam.setMargins(10, 0, 0, 0);
            tvContent.setLayoutParams(tvParam);
            tvContent.setSingleLine();
            mSlowHorizontalScrollView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
            mSlowHorizontalScrollView.setHorizontalScrollBarEnabled(false);
            mSlowHorizontalScrollView.scrollTo(0, 0);
            int last = Math.max(videoWidth, tvWidth) * 4;
            mSlowHorizontalScrollView.smoothScrollToSlow(videoWidth + tvWidth + 200, 0, last);
            mSlowHorizontalScrollView.getHandler().removeCallbacks(mRunnable);
            mSlowHorizontalScrollView.postDelayed(mRunnable, last);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取智能私信
     *
     * @param isForce
     * @param type
     */
    public void getAutoNotice(int isForce, int type) {
        logger.i( "getAutoNotice");
        try {
            if (Integer.parseInt(classId) < 0) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mHttpManager.getAutoNotice(classId, testId, srcType, type, isForce, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                try {
                    JSONObject object = (JSONObject) responseEntity.getJsonObject();
                    logger.i( "getAutoNotice success" + object.toString());

                    int type = object.optInt("type", -1);
                    int choose = object.optInt("choose", -1);
                    String content = object.optString("text");
//                    String name = object.optString("teacherName", teacherName);
//                    String imgUrl = object.optString(teacherImg);
                    if (!TextUtils.isEmpty(content) && !TextUtils.isEmpty(teacherName)) {
                        showNotice(teacherName, parseEmoji(content), teacherImg);
                        umsAgent(type, true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
                logger.i( "getAutoNotice fail" + msg);
                umsAgent(0, false);
                //showNotice("老师",notice[1][1],"");
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                super.onPmError(responseEntity);
                logger.i( "getAutoNotice fail" + responseEntity.getErrorMsg());
                umsAgent(0, false);
                //showNotice("老师",notice[1][1],"");
            }
        });
    }

    /**
     * 智能私信日志
     *
     * @param type
     * @param isSuccess
     */
    private void umsAgent(int type, boolean isSuccess) {
        HashMap<String, String> map = new HashMap<>();
        map.put("testid", testId);
        if (isSuccess) {
            map.put("chattexttype", "" + type);
            //map.put("chattextnum", "" + choose);
            if (type == 1) {
                map.put("whisperreq", "success");
                map.put("actiontype", "whisperpraise");
            } else if (type == 0) {
                map.put("actiontype", "whisperencourage");
                map.put("whisperreq", "success");
            } else if (type == 3) {
                map.put("actiontype", "whisperwarning");
                map.put("whisperwarntime", "" + System.currentTimeMillis());
            }
        } else {
            map.put("whisperreq", "fail");
        }
        mLiveBll.umsAgentDebugPv("sci_whisper_func", map);
    }

    private CharSequence parseEmoji(String src) {
        Matcher matcher = pattern.matcher(src);
        SpannableString sp = new SpannableString(src);
        while (matcher.find()) {
            String s = matcher.group(0);
            int e = Integer.parseInt(s.replaceAll("#", ""));
            if (e > 0 && e < 6) {
//                ImageSpan span=new ImageSpan(mContext.getResources().getDrawable(emoji[e]));
//                sp.setSpan(span,matcher.start(),matcher.end(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                src = src.replaceAll(s, emojis[e - 1]);
            } else {
                src = src.replaceAll(s, "");
            }
        }
        return src;
    }

    /** 脏词入库请求日志 */
    private void umsAgentReq(boolean isSuccess) {
        HashMap<String, String> map = new HashMap<>();
        map.put("chattexttype", "3");
        map.put("whisperwarningreq", isSuccess ? "success" : "fail");
        mLiveBll.umsAgentDebugPv("sci_whisper_func", map);
    }
}
