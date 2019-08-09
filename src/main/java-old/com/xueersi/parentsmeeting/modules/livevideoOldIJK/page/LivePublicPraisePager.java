package com.xueersi.parentsmeeting.modules.livevideoOldIJK.page;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.xueersi.common.base.BaseApplication;
import com.xueersi.common.base.BasePager;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassmateEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.LayoutParamsUtil;
import com.xueersi.ui.adapter.XsBaseAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by linyuqiang on 2016/8/17.
 * 公开表扬
 */
public class LivePublicPraisePager extends BasePager {
    String TAG = "LivePublicPraisePager";
    RelativeLayout relativeLayout;
    /** 单个表扬 */
    View vSignalPrise;
    /** 单个表扬文本 */
    TextView tvPublicSignalPraise;
    /** 多个表扬 */
    View vMultiPrise;
    TextView tvPublicMultiPraise;
    GridView gvPublicMultiPraise;
    /** 光荣榜 */
    View vHonor;
    TextView tvHonorPraise;
    GridView gvHonorPraise;
    /** 鼓励 */
    View vEncourage;
    ImageView ivEncourage;
    TextView tvEncourage;
    int lightRedColor, COLOR_333333;
    Animation animationIn, animationOut;
    boolean isHave = false;
    ArrayList<JsonType> runs = new ArrayList<>();
    /** gridview滚动，线性 */
    Method smoothScrollBy;
    String stuId;
    private Activity activity;
    private LiveGetInfo liveGetInfo;

    public LivePublicPraisePager(Context context) {
        super(context);
        activity = (Activity) context;
        Resources resources = context.getResources();
        lightRedColor = resources.getColor(R.color.COLOR_E74C3C);
        COLOR_333333 = resources.getColor(R.color.COLOR_333333);
        initData();
    }

    @Override
    public View initView() {
        relativeLayout = (RelativeLayout) View.inflate(mContext, R.layout.page_livevideo_publicpraise, null);
        vSignalPrise = View.inflate(mContext, R.layout.page_livevideo_publicpraise_signal, null);
        tvPublicSignalPraise = (TextView) vSignalPrise.findViewById(R.id.tv_livevideo_public_signal_praise);
        vMultiPrise = View.inflate(mContext, R.layout.page_livevideo_publicpraise_multi, null);
        tvPublicMultiPraise = (TextView) vMultiPrise.findViewById(R.id.tv_livevideo_public_multi_praise);
        gvPublicMultiPraise = (GridView) vMultiPrise.findViewById(R.id.gv_livevideo_public_multi_praise);
        gvPublicMultiPraise.setVerticalSpacing(10);
        gvPublicMultiPraise.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        vHonor = View.inflate(mContext, R.layout.page_livevideo_publicpraise_honor, null);
        tvHonorPraise = (TextView) vHonor.findViewById(R.id.tv_livevideo_public_multi_praise);
        gvHonorPraise = (GridView) vHonor.findViewById(R.id.gv_livevideo_public_multi_praise);
        gvHonorPraise.setVerticalSpacing(10);
        gvHonorPraise.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        vEncourage = View.inflate(mContext, R.layout.page_livevideo_publicpraise_encourage, null);
        ivEncourage = (ImageView) vEncourage.findViewById(R.id.iv_livevideo_encourage);
        tvEncourage = (TextView) vEncourage.findViewById(R.id.tv_livevideo_encourage);
        return relativeLayout;
    }

    @Override
    public void initData() {
        try {
            smoothScrollBy = AbsListView.class.getDeclaredMethod("smoothScrollBy", Integer.TYPE, Integer.TYPE,
                    Boolean.TYPE);
            smoothScrollBy.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        animationIn = AnimationUtils.loadAnimation(mContext, R.anim.anim_livevideo_praise_in);
        animationOut = AnimationUtils.loadAnimation(mContext, R.anim.anim_livevideo_praise_out);
        animationIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isHave = true;
                logger.i( "animationIn:onAnimationStart");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                logger.i( "animationIn:onAnimationEnd");
                final View child = relativeLayout.getChildAt(0);
                if (child != null) {
                    if (child == vMultiPrise) {
                        relativeLayout.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                final int last = gvPublicMultiPraise.getLastVisiblePosition();
                                final AtomicInteger lastAtomic = new AtomicInteger(last);
                                final int count = gvPublicMultiPraise.getAdapter().getCount();
                                logger.i( "animationIn:End:Multi:last=" + last + ",count=" + count);
                                if (last != count - 1 && gvPublicMultiPraise.getChildCount() > 0) {
                                    int lastrow = (count - lastAtomic.get()) / 5;// 剩余的行数
                                    if ((count - lastAtomic.get()) % 5 > 0) {
                                        lastrow += 1;
                                    }
                                    int line = gvPublicMultiPraise.getChildCount() / 5;//计算现在的view，每一个的高度
                                    if (gvPublicMultiPraise.getChildCount() % 5 > 0) {
                                        line += 1;
                                    }
                                    int duration = lastrow * 500;
                                    int distance = lastrow * gvPublicMultiPraise.getHeight() / line - 10;
                                    View child0 = gvPublicMultiPraise.getChildAt(0);
                                    logger.i( "animationIn:End:Multi:lastrow=" + lastrow + ",Height=" +
                                            child0.getHeight() + ",line=" + line + "," + gvPublicMultiPraise
                                            .getHeight() / line);
                                    try {
                                        smoothScrollBy.invoke(gvPublicMultiPraise, distance, duration, true);
                                    } catch (Exception e) {
                                        gvPublicMultiPraise.smoothScrollBy(distance, duration);
                                    }
                                    relativeLayout.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            logger.i( "animationIn:End:Multi.startAnimation1");
                                            child.startAnimation(animationOut);
                                        }
                                    }, duration + 500);
                                } else {
                                    logger.i( "animationIn:End:Multi.startAnimation2");
                                    child.startAnimation(animationOut);
                                }
                            }
                        }, 1000);
                    } else if (child == vHonor) {
                        relativeLayout.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                final int last = gvHonorPraise.getLastVisiblePosition();
                                final AtomicInteger lastAtomic = new AtomicInteger(last);
                                final int count = gvHonorPraise.getAdapter().getCount();
                                logger.i( "animationIn:End:Honor:last=" + last + ",count=" + count);
                                if (last != count - 1 && gvHonorPraise.getChildCount() > 0) {
                                    int lastrow = (count - lastAtomic.get()) / 5;// 剩余的行数
                                    if ((count - lastAtomic.get()) % 5 > 0) {
                                        lastrow += 1;
                                    }
                                    int duration = lastrow * 500;
                                    int line = gvHonorPraise.getChildCount() / 5;//计算现在的view，每一个的高度
                                    if (gvHonorPraise.getChildCount() % 5 > 0) {
                                        line += 1;
                                    }
                                    int distance = lastrow * gvHonorPraise.getHeight() / line - 10;
                                    View child0 = gvHonorPraise.getChildAt(0);
                                    logger.i( "animationIn:End:Honor:lastrow=" + lastrow + ",Height=" +
                                            child0.getHeight() + ",line=" + line + "," + gvHonorPraise.getHeight() /
                                            line);
                                    try {
                                        smoothScrollBy.invoke(gvHonorPraise, distance, duration, true);
                                    } catch (Exception e) {
                                        gvHonorPraise.smoothScrollBy(distance, duration);
                                    }
                                    relativeLayout.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            logger.i( "animationIn:End:Honor.startAnimation1");
                                            child.startAnimation(animationOut);
                                        }
                                    }, duration + 500);
                                } else {
                                    logger.i( "animationIn:End:Honor.startAnimation2");
                                    child.startAnimation(animationOut);
                                }
                            }
                        }, 1000);
                    } else {
                        logger.i( "animationIn:End:child.startAnimation=" + child.getAnimation());
                        relativeLayout.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                child.startAnimation(animationOut);
                            }
                        }, 3000);
                    }
                } else {
                    logger.i( "animationIn:onAnimationEnd:child=null");//不会发生
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                logger.i( "animationIn:onAnimationRepeat");
            }
        });
        animationOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                logger.i( "animationOut:onAnimationStart");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
//                relativeLayout.removeAllViews();
//                relativeLayout.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        logger.i( "animationOut:onAnimationEnd:time=" + (System.currentTimeMillis() - inStart));
//                        isHave = false;
//                        if (!runs.isSpace()) {
//                            logger.i( "animationOut:onAnimationEnd:runs=" + runs.size());
//                            onPraiseOrEncourage(runs.remove(0));
//                        } else {
//                            logger.i( "animationOut:onAnimationEnd:runs.isSpace");
//                        }
//                    }
//                }, 200);
                isHave = false;
                if (!runs.isEmpty()) {
                    logger.i( "animationOut:onAnimationEnd:runs=" + runs.size());
                    onPraiseOrEncourage(runs.remove(0), false);
                } else {
                    relativeLayout.removeAllViews();
                    logger.i( "animationOut:onAnimationEnd:runs.isSpace");
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                logger.i( "animationOut:onAnimationRepeat");
            }
        });
//        relativeLayout.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (type > 150) {
//                    return;
//                }
//                relativeLayout.postDelayed(this, 1000);
//                int kind = (type++) % 4 + 1;
//                JSONObject jsonObject = new JSONObject();
//                try {
//                    jsonObject.put("kind", kind);
//                    switch (kind) {
//                        case 1: {
//                            JSONArray jsonArray = new JSONArray();
//                            JSONObject object = new JSONObject();
//                            object.put("name", "林玉强");
//                            object.put("id", "" + stuId);
//                            jsonArray.put(object);
//                            jsonObject.put("data", jsonArray);
//                        }
//                        break;
//                        case 2: {
//                            jsonObject.put("num", 100);
//                            JSONArray jsonArray = new JSONArray();
//                            for (int i = 0; i < 35; i++) {
//                                JSONObject object = new JSONObject();
//                                object.put("name", "林玉强" + i);
//                                if (i == 20) {
//                                    object.put("id", "" + stuId);
//                                } else {
//                                    object.put("id", i + "1" + stuId);
//                                }
//                                jsonArray.put(object);
//                            }
//                            jsonObject.put("data", jsonArray);
//                        }
//                        break;
//                        case 3: {
//                            JSONArray jsonArray = new JSONArray();
//                            for (int i = 0; i < 35; i++) {
//                                JSONObject object = new JSONObject();
//                                object.put("name", "林玉强" + i);
//                                if (i == 20) {
//                                    object.put("id", "" + stuId);
//                                } else {
//                                    object.put("id", i + "1" + stuId);
//                                }
//                                jsonArray.put(object);
//                            }
//                            jsonObject.put("data", jsonArray);
//                        }
//                        break;
//                        case 4: {
//                            JSONArray jsonArray = new JSONArray();
//                            JSONObject object = new JSONObject();
//                            object.put("name", "林玉强");
//                            object.put("id", "" + stuId);
//                            jsonArray.put(object);
//                            jsonObject.put("data", jsonArray);
//                        }
//                        break;
//                    }
//                    onPraiseOrEncourage(jsonObject, true);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, 1000);
    }

    public void setGetInfo(LiveGetInfo getInfo) {
        this.liveGetInfo = getInfo;
        stuId = getInfo.getStuId();
    }

    int type = 1;

    /**
     * 表扬或批评
     *
     * @param jsonType
     */
    public void onPraiseOrEncourage(JsonType jsonType, boolean isNew) {
        logger.i( "onPraiseOrEncourage:isHave=" + isHave + ",isNew=" + isNew);
        if (isHave) {
            runs.add(jsonType);
            return;
        }
        boolean isOk = false;
        try {
            if (jsonType.type == JsonType.TYPE_PRAISE) {
                JSONObject jsonObject = jsonType.data;
                int kind = jsonObject.getInt("kind");
                switch (kind) {
                    case 1: {//单个表扬
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        if (jsonArray.length() != 0) {
                            String name = jsonArray.getJSONObject(0).getString("name");
                            signalPraise(name);
                            isOk = true;
                        } else {
                            isOk = false;
                        }
                    }
                    break;
                    case 2: {//指定表扬
                        ArrayList<ClassmateEntity> names = new ArrayList<>();
                        int num = jsonObject.getInt("num");
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            ClassmateEntity classmateEntity = new ClassmateEntity();
                            classmateEntity.setId(object.getString("id"));
                            classmateEntity.setName(object.getString("name"));
                            names.add(classmateEntity);
                        }
                        if (!names.isEmpty()) {
                            multiPraise(num, names);
                            isOk = true;
                        } else {
                            logger.i( "onPraiseOrEncourage:multiPraise.isSpace");
                        }
                    }
                    break;
                    case 3: {//全部表扬
                        ArrayList<ClassmateEntity> names = new ArrayList<>();
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            ClassmateEntity classmateEntity = new ClassmateEntity();
                            classmateEntity.setId(object.getString("id"));
                            classmateEntity.setName(object.getString("name"));
                            names.add(classmateEntity);
                        }
                        if (!names.isEmpty()) {
                            honor(names);
                            isOk = true;
                        } else {
                            logger.i( "onPraiseOrEncourage:honor.isSpace");
                        }
                    }
                    break;
                    case 4: {//私下鼓励
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        ClassmateEntity classmateEntity = null;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            String id = object.getString("id");
                            if (id.equals(stuId)) {
                                classmateEntity = new ClassmateEntity();
                                classmateEntity.setId(id);
                                classmateEntity.setName(object.getString("name"));
                                break;
                            }
                        }
                        if (classmateEntity != null) {
                            isOk = true;
                            encourageWrong(classmateEntity.getName());
                        } else {
                            logger.i( "onPraiseOrEncourage:encourageWrong");
                        }
                    }
                    break;
                    default:
                        logger.i( "onPraiseOrEncourage:type=" + type);
                        break;
                }
            } else if (jsonType.type == JsonType.TYPE_FIGHT) {
                JSONObject jsonObject = jsonType.data;
                JSONObject stuObj = jsonObject.getJSONObject("stu");
                if (stuObj.getInt("rank") == 1) {
                    addFighting(0, jsonObject);
                } else {
                    addFighting(1, jsonObject);
                }
                isOk = true;
            }
        } catch (JSONException e) {
            isOk = false;
            isHave = false;
            UmsAgentManager.umsAgentException(BaseApplication.getContext(), TAG + "onPraiseOrEncourage", e);
        }
        if (!isOk) {
            if (!runs.isEmpty()) {
                logger.i( "onPraiseOrEncourage:runs=" + runs.size());
                onPraiseOrEncourage(runs.remove(0), false);
            } else {
                relativeLayout.removeAllViews();
                logger.i( "onPraiseOrEncourage:runs.isSpace");
            }
        }
    }

    public void addFighting(int type, JSONObject jsonObject) throws JSONException {
        final View fightView;
        isHave = true;
        if (type == 0) {
            JSONObject stuObj = jsonObject.getJSONObject("stu");
            fightView = activity.getLayoutInflater().inflate(R.layout.layout_livevideo_fighting, relativeLayout, false);
            //自己的正确率
            TextView tvFightRight = (TextView) fightView.findViewById(R.id.tv_livevideo_fight_right);
            tvFightRight.setText("正确率 " + stuObj.get("rate") + "%");
        } else {
            JSONObject stuObj = jsonObject.getJSONObject("stu");
            JSONObject firstObj = null;
            if (jsonObject.get("first") instanceof JSONObject) {
                firstObj = jsonObject.getJSONObject("first");
            }
            fightView = activity.getLayoutInflater().inflate(R.layout.layout_livevideo_fightingvs, relativeLayout,
                    false);
            //自己的名次
            TextView tvFfightTip2 = (TextView) fightView.findViewById(R.id.tv_livevideo_fight_tip2);
            //自己的正确率
            TextView tvFightRight = (TextView) fightView.findViewById(R.id.tv_livevideo_fight_right);
            //第一的正确率
            TextView tvFightFirstrightRate = (TextView) fightView.findViewById(R.id.tv_livevideo_fight_firstright_rate);
            tvFfightTip2.setText("" + stuObj.get("rank"));
            tvFightRight.setText("正确率 " + stuObj.get("rate") + "%");
            if (firstObj != null) {
                tvFightFirstrightRate.setText(firstObj.get("rate") + "%");
            } else {
                tvFightFirstrightRate.setText("0%");
            }
            final ImageView head = (ImageView) fightView.findViewById(R.id.civ_livevideo_fight_firsttip);
            String img;
            List<String> headImgUrl = liveGetInfo.getHeadImgUrl();
            if (headImgUrl.isEmpty()) {
                img = liveGetInfo.getStuImg();
            } else {
                img = headImgUrl.get(0) + "/" + liveGetInfo.getHeadImgPath() + "/" + liveGetInfo.getImgSizeType() +
                        "?" + liveGetInfo.getHeadImgVersion();
            }

            RequestOptions options = new RequestOptions();
            options.error(R.drawable.ic_default_head_square)
                    .placeholder(R.drawable
                            .ic_default_head_square)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            ImageLoader.with(activity).load(img).into(head);
        }
        final RelativeLayout content = new RelativeLayout(mContext);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) fightView.getLayoutParams();
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        content.addView(fightView);
        addView(content);
        content.setVisibility(View.INVISIBLE);
        relativeLayout.post(new Runnable() {
            @Override
            public void run() {
                content.setVisibility(View.VISIBLE);
                content.startAnimation(animationIn);
            }
        });
    }

    public void signalPraise(String name) {
        if (relativeLayout.getChildAt(0) != vSignalPrise) {
            addView(vSignalPrise);
        }
        isHave = true;
        logger.i( "signalPraise:name=" + name);
        SpannableString spanttt = new SpannableString(name + "同学");
        CharacterStyle characterStyle = new ForegroundColorSpan(lightRedColor);
        spanttt.setSpan(characterStyle, 0, (name + "同学").length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tvPublicSignalPraise.setText(spanttt);
        tvPublicSignalPraise.append("答对了这道题，\n表现得很棒!");
        vSignalPrise.setVisibility(View.INVISIBLE);
        relativeLayout.post(new Runnable() {
            @Override
            public void run() {
                vSignalPrise.setVisibility(View.VISIBLE);
                vSignalPrise.startAnimation(animationIn);
            }
        });
    }

    public void multiPraise(int num, ArrayList<ClassmateEntity> names) {
        if (relativeLayout.getChildAt(0) != vMultiPrise) {
            addView(vMultiPrise);
        }
        isHave = true;
        logger.i( "multiPraise:num=" + num + ",names=" + names.size());
        tvPublicMultiPraise.setText("本班有");
        SpannableString spanttt = new SpannableString(num + "人");
        CharacterStyle characterStyle = new ForegroundColorSpan(lightRedColor);
        spanttt.setSpan(characterStyle, 0, (num + "人").length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tvPublicMultiPraise.append(spanttt);
        tvPublicMultiPraise.append("答对这道题。\n这次表扬其中的");
        spanttt = new SpannableString(names.size() + "位同学");
        characterStyle = new ForegroundColorSpan(lightRedColor);
        spanttt.setSpan(characterStyle, 0, (names.size() + "位同学").length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tvPublicMultiPraise.append(spanttt);
        tvPublicMultiPraise.append("，\n他们是:");
        gvPublicMultiPraise.setAdapter(new PraiseAdapter(mContext, names));
        relativeLayout.post(new Runnable() {
            @Override
            public void run() {
                try {
                    ViewGroup.LayoutParams params = gvPublicMultiPraise.getLayoutParams();
                    params.height = 2 * (gvPublicMultiPraise.getChildAt(0).getHeight() + 10);
//                    gvPublicMultiPraise.setLayoutParams(params);
                    LayoutParamsUtil.setViewLayoutParams(gvPublicMultiPraise, params);
                } catch (Exception e) {
                    logger.i( "multiPraise:setLayoutParams", e);
                }
            }
        });
        vMultiPrise.setVisibility(View.INVISIBLE);
        relativeLayout.post(new Runnable() {
            @Override
            public void run() {
                vMultiPrise.setVisibility(View.VISIBLE);
                vMultiPrise.startAnimation(animationIn);
            }
        });
    }

    public void honor(ArrayList<ClassmateEntity> names) {
        if (relativeLayout.getChildAt(0) != vHonor) {
            addView(vHonor);
        }
        isHave = true;
        logger.i( "honor:names=" + names.size());
        tvHonorPraise.setText("恭喜本班有");
        SpannableString spanttt = new SpannableString(names.size() + "人");
        CharacterStyle characterStyle = new ForegroundColorSpan(lightRedColor);
        spanttt.setSpan(characterStyle, 0, (names.size() + "人").length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tvHonorPraise.append(spanttt);
        tvHonorPraise.append("答对这道题。\n他们是:");
        gvHonorPraise.setAdapter(new PraiseAdapter(mContext, names));
        relativeLayout.post(new Runnable() {
            @Override
            public void run() {
                try {
                    ViewGroup.LayoutParams params = gvHonorPraise.getLayoutParams();
                    params.height = 3 * (gvHonorPraise.getChildAt(0).getHeight() + 10);
//                    gvHonorPraise.setLayoutParams(params);
                    LayoutParamsUtil.setViewLayoutParams(gvHonorPraise, params);
                } catch (Exception e) {
                    logger.i( "honor:setLayoutParams", e);
                }
            }
        });
        vHonor.setVisibility(View.INVISIBLE);
        relativeLayout.post(new Runnable() {
            @Override
            public void run() {
                vHonor.setVisibility(View.VISIBLE);
                vHonor.startAnimation(animationIn);
            }
        });
    }

    public void encourageWrong(String name) {
        if (relativeLayout.getChildAt(0) != vEncourage) {
            addView(vEncourage);
        }
        isHave = true;
        logger.i( "encourageWrong:name=" + name);
        ivEncourage.setBackgroundResource(R.drawable.bg_web_request_error);
        SpannableString spanttt = new SpannableString(name + "同学");
        CharacterStyle characterStyle = new ForegroundColorSpan(lightRedColor);
        spanttt.setSpan(characterStyle, 0, (name + "同学").length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tvEncourage.setText(spanttt);
        tvEncourage.append("这道题答错了，\n要认真听老师讲解，\n下次加油！");
        vEncourage.setVisibility(View.INVISIBLE);
        relativeLayout.post(new Runnable() {
            @Override
            public void run() {
                vEncourage.setVisibility(View.VISIBLE);
                vEncourage.startAnimation(animationIn);
            }
        });
    }

    /** 答题超时 */
    public void encourageTimeOut(String name) {
        logger.i( "encourageTimeOut:name=" + name);
        if (relativeLayout.getChildAt(0) != vEncourage) {
            addView(vEncourage);
        }
        ivEncourage.setBackgroundResource(R.drawable.bg_livevideo_praise_study);
        SpannableString spanttt = new SpannableString(name);
        CharacterStyle characterStyle = new ForegroundColorSpan(lightRedColor);
        spanttt.setSpan(characterStyle, 0, name.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tvEncourage.setText(spanttt);
        tvEncourage.append("一定要加快\n速度,下次在规定时\n间内完成作答。");
    }

    private class PraiseAdapter extends XsBaseAdapter<ClassmateEntity> {

        public PraiseAdapter(Context context, List<ClassmateEntity> list) {
            super(context, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tvName;
            TextView tvSign;
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.item_livevideo_praise, null);
            }
            tvName = (TextView) convertView.findViewById(R.id.tv_livevideo_praise_name);
            tvSign = (TextView) convertView.findViewById(R.id.tv_livevideo_praise_sign);
            ClassmateEntity classmateEntity = (ClassmateEntity) getItem(position);
            if ((classmateEntity.getId() + "").equals(stuId)) {
                tvName.setTextColor(lightRedColor);
                tvSign.setTextColor(lightRedColor);
            } else {
                tvName.setTextColor(COLOR_333333);
                tvSign.setTextColor(COLOR_333333);
            }
            tvName.setText(classmateEntity.getName());
//            if (position == getCount() - 1) {
//                tvSign.setText("。");
//            } else {
//                tvSign.setText("，");
//            }
            if (position == getCount() - 1) {
                tvSign.setText(".");
            } else {
                tvSign.setText(",");
            }
            return convertView;
        }
    }

    private void addView(View view) {
        relativeLayout.removeAllViews();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        relativeLayout.addView(view, params);
    }

    public static class JsonType {
        public static final int TYPE_PRAISE = 0;
        public static final int TYPE_FIGHT = 1;
        int type;
        JSONObject data;

        public JsonType(int type, JSONObject data) {
            this.type = type;
            this.data = data;
        }
    }
}
