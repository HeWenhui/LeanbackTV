package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.GoldTeamStatus;
import com.xueersi.xesalib.utils.uikit.imageloader.ImageLoader;
import com.xueersi.xesalib.utils.uikit.imageloader.SingleConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by linyuqiang on 2018/4/10.
 */
public class Top3FrameAnim {
    private String file15 = "Images/redpackage/15_rank_enter";
    private String file16 = "Images/redpackage/16_rank_looper";
    private Context mContext;
    private View rl_livevideo_redpackage_bg;
    private Bitmap headBitmap;
    private HashMap<String, Bitmap> stuHeadBitmap;
    private ArrayList<FrameAnimation> frameAnimations;

    public Top3FrameAnim(Context mContext, View rl_livevideo_redpackage_bg, HashMap<String, Bitmap> stuHeadBitmap, ArrayList<FrameAnimation> frameAnimations) {
        this.mContext = mContext;
        this.rl_livevideo_redpackage_bg = rl_livevideo_redpackage_bg;
        this.stuHeadBitmap = stuHeadBitmap;
        this.frameAnimations = frameAnimations;
    }

    public Bitmap getHeadBitmap() {
        return headBitmap;
    }

    public void setHeadBitmap(Bitmap headBitmap) {
        this.headBitmap = headBitmap;
    }

    public void start(final FrameAnimation.AnimationListener animationListener, final ArrayList<GoldTeamStatus.Student> students) {
        String path = file15;
        FrameAnimation btframeAnimation2 =
                FrameAnimation.createFromAees(mContext, rl_livevideo_redpackage_bg, path, 50, false);
        frameAnimations.add(btframeAnimation2);
        btframeAnimation2.setAnimationListener(new FrameAnimation.AnimationListener() {
            @Override
            public void onAnimationStart() {
                animationListener.onAnimationStart();
            }

            @Override
            public void onAnimationEnd() {
                String path = file16;
                final FrameAnimation btframeAnimation3 =
                        FrameAnimation.createFromAees(mContext, rl_livevideo_redpackage_bg, path, 50, true);
                frameAnimations.add(btframeAnimation3);
                btframeAnimation3.setBitmapCreate(new FrameAnimation.BitmapCreate() {
                    @Override
                    public Bitmap onAnimationCreate(String file) {
                        if (students.isEmpty()) {
                            return null;
                        }
                        return initTeamRankHeadAndGold(students, file, true, btframeAnimation3);
                    }
                });
                rl_livevideo_redpackage_bg.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btframeAnimation3.destory();
                        animationListener.onAnimationEnd();
                    }
                }, 3000);
            }

            @Override
            public void onAnimationRepeat() {

            }
        });
    }

    private Bitmap initTeamRankHeadAndGold(ArrayList<GoldTeamStatus.Student> students, final String file, boolean havename, final FrameAnimation upFrameAnimation) {
        InputStream inputStream = null;
        try {
            inputStream = mContext.getAssets().open(file);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            bitmap.setDensity(160);
            Bitmap canvasBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            canvasBitmap.setDensity(160);
            Canvas canvas = new Canvas(canvasBitmap);
            canvas.drawBitmap(bitmap, 0, 0, null);
            float[] headWidth = {108f, 96f, 96f};
            int mid = bitmap.getWidth() / 2;
            float[][] headLeftAndRights = {{mid - 20, 295}, {mid - 200, 382}, {mid + 170, 398}};
            float[] textTops = {408, 478, 492};
            int[] textColors = {0xffD45F19, 0xff0C719B, 0xffD04715};
            for (int i = 0; i < students.size(); i++) {
                Bitmap head;
                final GoldTeamStatus.Student entity = students.get(i);
                final boolean isMe = entity.isMe();
                if (isMe) {
                    head = headBitmap;
                    if (head == null) {
                        head = stuHeadBitmap.get(entity.getStuId());
                    }
                } else {
                    head = stuHeadBitmap.get(entity.getStuId());
                }
                float[] leftAndRight = headLeftAndRights[i];
                float left = leftAndRight[0];
                int scalHeadWidth = -1;
                if (head != null && !head.isRecycled()) {
                    float scaleWidth = headWidth[i] / head.getHeight();
                    Matrix matrix = new Matrix();
                    matrix.postScale(scaleWidth, scaleWidth);
                    Bitmap scalHeadBitmap = Bitmap.createBitmap(head, 0, 0, head.getWidth(), head.getHeight(), matrix, true);
                    scalHeadBitmap.setDensity(160);
                    if (i == 0) {
                        left = mid - scalHeadBitmap.getWidth() / 2;
                    } else if (i == 1) {
                        left = mid - scalHeadBitmap.getWidth() - 206;
                    } else {
                        left = mid + scalHeadBitmap.getWidth() / 2 + 150;
                    }
                    float top = leftAndRight[1];
                    canvas.drawBitmap(scalHeadBitmap, left, top, null);
                    scalHeadWidth = scalHeadBitmap.getWidth();
                    scalHeadBitmap.recycle();
                } else {
                    Activity activity = (Activity) mContext;
                    if (!activity.isFinishing()) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ImageLoader.with(mContext).load(entity.getAvatar_path()).asCircle().asBitmap(new SingleConfig.BitmapListener() {
                                    @Override
                                    public void onSuccess(Drawable drawable) {
                                        Bitmap headBitmap = ((BitmapDrawable) drawable).getBitmap();
                                        if (isMe) {
                                            Top3FrameAnim.this.headBitmap = headBitmap;
                                        }
                                        stuHeadBitmap.put(entity.getStuId(), headBitmap);
                                        upFrameAnimation.removeBitmapCache(file);
                                    }

                                    @Override
                                    public void onFail() {

                                    }
                                });
                            }
                        });
                    }
                }
                bitmap.recycle();
                //画名字和金币数量
                if (havename) {
                    String gold = "+" + entity.getGold();
                    View layout_live_stand_red_mine1 = LayoutInflater.from(mContext).inflate(R.layout.layout_live_stand_red_mine2, null);
                    ImageView iv_livevideo_redpackage_num = layout_live_stand_red_mine1.findViewById(R.id.iv_livevideo_redpackage_num);
                    iv_livevideo_redpackage_num.setImageResource(R.drawable.bg_live_stand_red_gold_big);
                    TextView tv_livevideo_redpackage_name = layout_live_stand_red_mine1.findViewById(R.id.tv_livevideo_redpackage_name);
                    tv_livevideo_redpackage_name.setText("" + entity.getNickname());
                    TextView tv_livevideo_redpackage_num = layout_live_stand_red_mine1.findViewById(R.id.tv_livevideo_redpackage_num);
                    tv_livevideo_redpackage_num.setText(gold);
//                    if (i == 0) {
//                        tv_livevideo_redpackage_name.setTextColor(textColors[i]);
//                        tv_livevideo_redpackage_num.setTextColor(textColors[i]);
//                    } else if (i == 1) {
//                        tv_livevideo_redpackage_name.setTextColor(0xff0C719B);
//                        tv_livevideo_redpackage_num.setTextColor(0xff0C719B);
//                    } else if (i == 2) {
//                        tv_livevideo_redpackage_name.setTextColor(0xffD04715);
//                        tv_livevideo_redpackage_num.setTextColor(0xffD04715);
//                    }
                    tv_livevideo_redpackage_name.setTextColor(textColors[i]);
                    tv_livevideo_redpackage_num.setTextColor(textColors[i]);
                    tv_livevideo_redpackage_name.setTextSize(TypedValue.COMPLEX_UNIT_PX, 23);
                    tv_livevideo_redpackage_num.setTextSize(TypedValue.COMPLEX_UNIT_PX, 22);
                    layout_live_stand_red_mine1.measure(canvasBitmap.getWidth(), canvasBitmap.getHeight());
                    layout_live_stand_red_mine1.layout(0, 0, canvasBitmap.getWidth(), canvasBitmap.getHeight());
                    canvas.save();
                    float top = textTops[i];
                    float textLeft = left;
                    if (scalHeadWidth != -1) {
                        textLeft = left + scalHeadWidth / 2 - layout_live_stand_red_mine1.getMeasuredWidth() / 2;
                    }
                    canvas.translate(textLeft, top);
                    layout_live_stand_red_mine1.draw(canvas);
                    canvas.restore();
                }
            }
            return canvasBitmap;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
