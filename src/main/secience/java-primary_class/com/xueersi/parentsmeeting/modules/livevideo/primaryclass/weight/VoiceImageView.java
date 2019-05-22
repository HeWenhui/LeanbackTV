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

public class VoiceImageView extends ImageView {
    String TAG = "VoiceImageView";
    Logger logger = LoggerFactory.getLogger(TAG);
    Bitmap bg_live_voicewave_bg1;
    int height;
    Rect src;

    public VoiceImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        bg_live_voicewave_bg1 = BitmapFactory.decodeResource(getResources(), R.drawable.bg_live_voicewave_bg2);
        height = bg_live_voicewave_bg1.getHeight();
        src = new Rect(0, bg_live_voicewave_bg1.getHeight(), bg_live_voicewave_bg1.getWidth(), bg_live_voicewave_bg1.getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate((getWidth() - bg_live_voicewave_bg1.getWidth()) / 2, 0);
        canvas.drawBitmap(bg_live_voicewave_bg1, src, src, null);
        canvas.restore();
    }

    public void reset() {
        src.top = bg_live_voicewave_bg1.getHeight();
        postInvalidate();
    }

    public void setVoice(int volume) {
        src.top = (int) ((float) volume * height / 255.0f);
        logger.d("setVoice:volume=" + volume + ",top=" + src.top + ",height=" + height);
        postInvalidate();
    }
}
