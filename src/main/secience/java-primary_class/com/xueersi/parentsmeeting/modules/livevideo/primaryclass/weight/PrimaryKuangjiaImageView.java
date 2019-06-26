package com.xueersi.parentsmeeting.modules.livevideo.primaryclass.weight;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;

import java.util.ArrayList;

public class PrimaryKuangjiaImageView extends ImageView {
    private ArrayList<OnSizeChange> onSizeChanges = new ArrayList<>();
    private String TAG = "PrimaryKuangjiaImageView";
    protected Logger logger = LoggerFactory.getLogger(TAG);
    private String mode = LiveTopic.MODE_CLASS;
    private LogToFile logToFile;

    public PrimaryKuangjiaImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        LiveVideoPoint.getInstance().addVideoSizeChange(getContext(), new LiveVideoPoint.VideoSizeChange() {
            @Override
            public void videoSizeChange(LiveVideoPoint liveVideoPoint) {
                notifyChange();
            }
        });
        logger.d("onAttachedToWindow");
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == VISIBLE) {
            notifyChange();
        }
    }

    public void onResume() {
        notifyChange();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        logger.d("onDetachedFromWindow:size=" + onSizeChanges.size());
        onSizeChanges.clear();
    }

    public interface OnSizeChange {
        void onSizeChange(int width, int height);
    }

    private void notifyChange() {
        addSizeChange(new OnSizeChange() {
            @Override
            public void onSizeChange(int width, int height) {
                logger.d("notifyChange:mode=" + mode);
                if (LiveTopic.MODE_CLASS.equals(mode)) {
                    for (int i = 0; i < onSizeChanges.size(); i++) {
                        onSizeChanges.get(i).onSizeChange(width, height);
                    }
                }
            }
        }, false);
    }

    public void addSizeChange(final OnSizeChange onSizeChange) {
        addSizeChange(onSizeChange, true);
    }

    public void removeSizeChange(final OnSizeChange onSizeChange) {
        onSizeChanges.remove(onSizeChange);
    }

    private void addSizeChange(final OnSizeChange onSizeChange, boolean add) {
        if (add) {
            onSizeChanges.add(onSizeChange);
        }
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }
        final Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (logToFile == null) {
                    logToFile = new LogToFile(getContext(), TAG);
                }
                getViewTreeObserver().removeOnPreDrawListener(this);
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) getLayoutParams();
//            lp.height = bitmap.getHeight();
                float radio = (float) bitmap.getWidth() / (float) bitmap.getHeight();
                LiveVideoPoint point = LiveVideoPoint.getInstance();
                float viewRadio = (float) point.screenWidth / (float) point.screenHeight;
                int width;
                int height;
                if (viewRadio > radio) {
                    height = point.screenHeight;
                    width = (int) ((float) height / (float) bitmap.getHeight() * (float) bitmap.getWidth());
                    if (lp.width != width || lp.height != height) {
                        lp.width = width;
                        lp.height = height;
                        LayoutParamsUtil.setViewLayoutParams(PrimaryKuangjiaImageView.this, lp);
                        logToFile.d("setImageViewWidth:width1=" + width + ",height1=" + height);
                    }
                } else {
                    width = point.screenWidth;
                    height = (int) ((float) width / (float) bitmap.getWidth() * (float) bitmap.getHeight());
                    if (lp.width != width || lp.height != height) {
                        lp.width = width;
                        lp.height = height;
                        LayoutParamsUtil.setViewLayoutParams(PrimaryKuangjiaImageView.this, lp);
                        logToFile.d("setImageViewWidth:width2=" + width + ",height2=" + height);
                    }
                }
                onSizeChange.onSizeChange(width, height);
                return false;
            }
        });
    }

}
