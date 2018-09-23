package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;

/**
 * Created by linyuqiang on 2018/8/19.
 */
public class VisibilitImageView extends ImageView {
    String TAG = "VisibilitImageView";
    protected Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

    public VisibilitImageView(Context context) {
        super(context);
    }

    public VisibilitImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        int visibility = getVisibility();
        logger.d( "VisibilitImageView:visibility=" + visibility);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        logger.d( "setVisibility:visibility=" + visibility);
    }
}
