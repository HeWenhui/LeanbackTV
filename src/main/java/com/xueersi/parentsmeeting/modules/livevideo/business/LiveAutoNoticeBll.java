package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.media.Image;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoActivity;
import com.xueersi.parentsmeeting.modules.livevideo.widget.SlowHorizontalScrollView;
import com.xueersi.xesalib.utils.uikit.ScreenUtils;
import com.xueersi.xesalib.utils.uikit.imageloader.ImageLoader;

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
    private View vLeft,vRight;
    private ImageView ivAvatar;
    private TextView tvContent;
    private RelativeLayout bottom;
    public LiveAutoNoticeBll(Context context,RelativeLayout bottom){
        this.mContext =context;
        this.bottom=bottom;
        setLayout(1920,1080);
    }
    public void setLayout(int width,int height){
        int screenWidth = getScreenParam();
        displayHeight = height;
        displayWidth = screenWidth;
        if (width > 0) {
            wradio = (int) (LiveVideoActivity.VIDEO_HEAD_WIDTH * width / LiveVideoActivity.VIDEO_WIDTH);
            wradio += (screenWidth - width) / 2;
            if(displayWidth-wradio==videoWidth){
                return;
            }else {
                videoWidth = displayWidth - wradio;
            }
        }
    }
    private int getScreenParam(){
        final View contentView = ((Activity) mContext).findViewById(android.R.id.content);
        final View actionBarOverlayLayout = (View) contentView.getParent();
        Rect r = new Rect();
        actionBarOverlayLayout.getWindowVisibleDisplayFrame(r);
        return (r.right - r.left);
    }

    public void setBottom(RelativeLayout bottom) {
        this.bottom = bottom;
    }

    public void showNotice(String s,String head){
        if(root==null){
            root=View.inflate(mContext, R.layout.layout_live_auto_notice,null);
            mSlowHorizontalScrollView=(SlowHorizontalScrollView)root.findViewById(R.id.sv_live_auto_notice);
            vLeft=root.findViewById(R.id.v_live_auto_notice_left);
            vRight=root.findViewById(R.id.v_live_auto_notice_right);
            ivAvatar=(ImageView)root.findViewById(R.id.iv_live_auto_notice_avatar);
            tvContent=(TextView)root.findViewById(R.id.tv_live_auto_notice_content);
        }
        ImageLoader.with(mContext).load(head).error(R.drawable.ic_default_head_square).into(ivAvatar);
        tvContent.setText(s);
        RelativeLayout.LayoutParams rootParam=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rootParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        rootParam.setMargins(0,0,0,40);
        bottom.addView(root,1,rootParam);
        LinearLayout.LayoutParams svParam=new LinearLayout.LayoutParams(videoWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        mSlowHorizontalScrollView.setLayoutParams(svParam);
        LinearLayout.LayoutParams vParam=new LinearLayout.LayoutParams(videoWidth,1);
        vLeft.setLayoutParams(vParam);
        vRight.setLayoutParams(vParam);
        TextPaint paint=new TextPaint();
        paint.setTextSize(24);
        int tvWidth=(int)paint.measureText(s);
        LinearLayout.LayoutParams tvParam=new LinearLayout.LayoutParams(tvWidth+40, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvContent.setLayoutParams(tvParam);
        tvContent.setSingleLine();
        mSlowHorizontalScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        mSlowHorizontalScrollView.setHorizontalScrollBarEnabled(false);
        mSlowHorizontalScrollView.scrollTo(0,0);
        int last=Math.max(videoWidth,tvWidth)*4;
        mSlowHorizontalScrollView.smoothScrollToSlow(videoWidth+tvWidth+200,0,last);
        mSlowHorizontalScrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                bottom.removeView(root);
            }
        },last);
    }
}
