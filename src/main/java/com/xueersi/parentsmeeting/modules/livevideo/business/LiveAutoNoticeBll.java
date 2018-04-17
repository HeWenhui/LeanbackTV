package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.Image;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.text.style.TextAppearanceSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.parentsmeeting.http.HttpCallBack;
import com.xueersi.parentsmeeting.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoActivity;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.widget.SlowHorizontalScrollView;
import com.xueersi.parentsmeeting.modules.loginregisters.business.UserBll;
import com.xueersi.parentsmeeting.sharedata.ShareDataManager;
import com.xueersi.xesalib.utils.log.Loger;
import com.xueersi.xesalib.utils.uikit.ScreenUtils;
import com.xueersi.xesalib.utils.uikit.SizeUtils;
import com.xueersi.xesalib.utils.uikit.imageloader.ImageLoader;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Tang on 2018/3/10.
 */

public class LiveAutoNoticeBll {
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
    private LiveBll mLiveBll;
    private Runnable mRunnable;
    String TAG = this.getClass().getSimpleName();
    Pattern pattern=Pattern.compile("#[0-9]#");
    String[] emojis={"\uD83D\uDE22","\uD83D\uDE44","\uD83D\uDE31","\uD83D\uDE02","\uD83D\uDE15"};
    //int[] emoji={R.drawable.live_emoji_1,R.drawable.live_emoji_2,R.drawable.live_emoji_3};
    /**
     * 文案
     */
    String[] notice={"不要在上课期间发表不合适的言论。",
            "禁止脏话及敏感词汇，你会被禁言。",
            "尴尬了，我会收到你发的被屏蔽的留言，你还要发吗？",
            "你需要找我聊聊人生了。"};
    /*String[][] notice = {{"你不会是手抖输错了吧？据传说集中精神听课能治疗手抖，不信你试试!",
            "你绝对是故意的\uD83D\uDE22（哭脸），错了不要紧，老师讲后懂了就是好样的。我会关注你的哦.",
            "看来这个知识点你掌握的不是很牢固啊，记得下课看回放。"
    }, {"你是如何做到用飞一样的速度做错的？\uD83D\uDE44",
            "正确率才是最重要的。只追求快是无意义的。默读三遍，记在心间。",
            "还是要认真审题，做完检查。才能避免这种很快错了的悲剧。",
            "成为最快做错的同学，你真的开心吗？\uD83D\uDE31",
            "你是如何做到连续5次成为最快做错的同学的。你也太厉害了。",
            "\uD83D\uDE44\uD83D\uDE44\uD83D\uDE44你是根本没有用心做题吧。下课你需要和我聊聊了。"
    }, {"先跟上老师讲课的节奏，没听懂的先标记下，下课再问哦。",
            "看来今天你状态不佳，据说好好听课能提升状态值，你试试看。",
            "你这不仅是状态不佳了，可能运气也不佳啊\uD83D\uDE02 ，坚持听课还有救的。",
            "你今天的互动题正确率已经天下无敌了，下课看回放，问我题吧，我不嫌弃你，心疼的抱抱。"
    }, {"你都全对了，为什么不主动提交呢。我猜你是找不到提交键，嘻嘻😁。",
            "你还没找到提交键吗？要在结束前主动提交。相信自己哦。",
            "下次主动提交吧，错了也没关系。你要相信自己错了也能改对。",
            "做对了也不提交，看来你是故意不让老师表扬你。\uD83D\uDE02"
    }, {"错了没关系，勇于面对自己的问题是更可贵的精神，下次主动提交哦。",
            "在你犹豫的时刻，是不是时间就到了？下回不要犹豫啦。\uD83D\uDE02",
            "好吧，你还是犹豫了，是不是你对这个知识点的掌握不够扎实呢？",
            "老师认为，勇于尝试，错了真的没关系，毕竟你是认真学了的，加油！"
    }, {"是不是网络问题导致你不能主动提交？刷新，退出重进都是解决好办法。",
            "是时间来不急了吗？下回提高做题速度哦。",
            "你又没提交\uD83D\uDE02 ，是不是真的没做完啊，下回做多少交多少。",
            "不敢提交的原因，有没有一丢丢是不会呢？如果有，看回放，问老师。"
    }, {}, {"是网络问题收不到互动题吗？刷新或退出重进试试看，如果都不行，联系我。",
            "如果不是网络问题，那就是你的问题了，认真听课和做题，我会关注你的。",
            "做错不可怕，可怕的是不做，态度不端正，怎么逆袭呢？",
            "今天来了一个假的你吧？四道题不做。看啦你需要下课找我了。"
    }, {"你太牛了，这题的正确率还不到30%，但是你做对了。",
            "两次了，正确率那么低，但是你就是能做对，看来基本功扎实。",
            "厉害了，我的娃，暴击3次低正确率。",
            "老师已经开始膜拜你的能力了，暴击4次低正确率。"
    }, {"状态不错，累计做对多道题了，继续加油！",
            "累计做对的题目又多了，看看你能不能创造更好的记录。",
            "厉害了，我的娃，累计多道题目全对get，你是我的小骄傲。",
            "几乎全部题目都对的娃，膜拜中～给你个特殊大大的表扬。"
    }, {"不要在上课期间发表不合适的言论。",
            "禁止脏话及敏感词汇，你会被禁言。",
            "尴尬了，我会收到你发的被屏蔽的留言，你还要发吗？",
            "你需要找我聊聊人生了。"
    }};*/

    public LiveAutoNoticeBll(Context context, RelativeLayout bottom) {
        this.mContext = context;
        this.bottom = bottom;
        setLayout(1920, 1080);

    }

    public void setLayout(int width, int height) {
        int screenWidth = getScreenParam();
        displayHeight = height;
        displayWidth = screenWidth;
        if (width > 0) {
            wradio = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * width / LiveVideoActivity.VIDEO_WIDTH);
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

    public void setTeacherImg(String teacherImg) {
        this.teacherImg = teacherImg;
    }

    public void setLiveBll(LiveBll liveBll) {
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
            int i = ShareDataManager.getInstance().getInt("LiveAutoNotice_" + liveId, -1, ShareDataManager.SHAREDATA_USER);
            showNotice(name, notice[(i + 1) % 4], head);
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
        if(mRunnable==null){
            mRunnable=new Runnable() {
                @Override
                public void run() {
                    bottom.removeView(root);
                    isShowing=false;
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
            if(!isShowing) {
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
     * 获取智能私信
     *
     * @param isForce
     * @param type
     */
    public void getAutoNotice(int isForce, int type) {
        Loger.i(TAG, "getAutoNotice");
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
                    Loger.i(TAG, "getAutoNotice success" + object.toString());

                    int type = object.optInt("type", -1);
                    int choose = object.optInt("choose", -1);
                    String content=object.optString("text");
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
                Loger.i(TAG, "getAutoNotice fail" + msg);
                umsAgent(0, false);
                //showNotice("老师",notice[1][1],"");
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                super.onPmError(responseEntity);
                Loger.i(TAG, "getAutoNotice fail" + responseEntity.getErrorMsg());
                umsAgent(0, false);
                //showNotice("老师",notice[1][1],"");
            }
        });
    }

    /**
     * 智能私信日志
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
        mLiveBll.umsAgentDebug3("sci_whisper_func", map);
    }
    private CharSequence parseEmoji(String src){
        Matcher matcher=pattern.matcher(src);
        SpannableString sp=new SpannableString(src);
        while (matcher.find()){
            String s=matcher.group(0);
            int e=Integer.parseInt(s.replaceAll("#",""));
            if(e>0&&e<6){
//                ImageSpan span=new ImageSpan(mContext.getResources().getDrawable(emoji[e]));
//                sp.setSpan(span,matcher.start(),matcher.end(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                src=src.replaceAll(s,emojis[e]);
            }else{
                src=src.replaceAll(s,"");
            }
        }
        return src;
    }
    /**脏词入库请求日志*/
    private void umsAgentReq(boolean isSuccess){
        HashMap<String,String> map=new HashMap<>();
        map.put("chattexttype","11");
        map.put("whisperwarningreq",isSuccess?"success":"fail");
        mLiveBll.umsAgentDebug3("sci_whisper_func",map);
    }
}
