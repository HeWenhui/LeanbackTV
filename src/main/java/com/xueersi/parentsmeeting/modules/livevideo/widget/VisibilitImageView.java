package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;

/**
 * Created by linyuqiang on 2018/8/19.
 */
public class VisibilitImageView extends ImageView {
    String TAG = "VisibilitImageView";

    public VisibilitImageView(Context context) {
        super(context);
    }

    public VisibilitImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        int visibility = getVisibility();
        Loger.d(TAG, "VisibilitImageView:visibility=" + visibility);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        Loger.d(TAG, "setVisibility:visibility=" + visibility);
    }
}
