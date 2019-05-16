package com.xueersi.parentsmeeting.modules.livevideo.primaryclass.weight;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;

import java.util.Random;

public class VoiceImageView extends ImageView {
    String TAG = "VoiceImageView";
    Logger logger = LoggerFactory.getLogger(TAG);
    Random random = new Random();
    Bitmap bg_live_voicewave_bg1;
    int height;
    Rect src;

    public VoiceImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        bg_live_voicewave_bg1 = BitmapFactory.decodeResource(getResources(), R.drawable.bg_live_voicewave_bg2);
        height = bg_live_voicewave_bg1.getHeight();
        src = new Rect(0, 0, bg_live_voicewave_bg1.getWidth(), bg_live_voicewave_bg1.getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bg_live_voicewave_bg1, src, src, null);
    }

    public void setVoice(int volume) {
        src.top = (int) ((float) volume * height / (float) 30);
        logger.d("setVoice:volume=" + volume + ",top=" + src.top + ",height=" + height);
        invalidate();
    }
}
