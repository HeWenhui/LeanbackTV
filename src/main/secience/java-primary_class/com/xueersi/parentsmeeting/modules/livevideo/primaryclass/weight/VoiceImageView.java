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
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;

public class VoiceImageView extends ImageView {
    String TAG = "VoiceImageView";
    Logger logger = LiveLoggerFactory.getLogger(TAG);
    Bitmap bg_live_voicewave_bg1;
    int height;
    Rect src;
    int uid;
    int itemHeight;

    public VoiceImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        bg_live_voicewave_bg1 = BitmapFactory.decodeResource(getResources(), R.drawable.bg_live_voicewave_bg2);
        height = bg_live_voicewave_bg1.getHeight();
        src = new Rect(0, bg_live_voicewave_bg1.getHeight(), bg_live_voicewave_bg1.getWidth(), bg_live_voicewave_bg1.getHeight());
        itemHeight = height / 6;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(((float) getWidth() - (float) bg_live_voicewave_bg1.getWidth()) / 2, 0);
        canvas.drawBitmap(bg_live_voicewave_bg1, src, src, null);
        canvas.restore();
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public void reset() {
        src.top = bg_live_voicewave_bg1.getHeight();
        postInvalidate();
    }

    public void setVoice(int volume) {
//        volume = 255;
        int top = bg_live_voicewave_bg1.getHeight() - (int) ((float) volume * height / 255.0f);
        int c = top / itemHeight;
        if (top % itemHeight != 0) {
            c++;
        }
        src.top = c * itemHeight;
        logger.d("setVoice:uid=" + uid + ",volume=" + volume + ",top=" + src.top + ",height=" + height + ",c=" + c + ",itemHeight=" + itemHeight);
        postInvalidate();
    }
}
