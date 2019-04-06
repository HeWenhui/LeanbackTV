package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;


/**
 * @author wellcao
 * @date 2018/8/1
 * class introduction:  键盘高度调整帮助类
 */

public class KeyboardPopWindow extends PopupWindow {
    Logger logger = LoggerFactory.getLogger("KeyboardPopWindow");
    private Activity activity;
    private View popupView;
    private int keyboardPortraitHeight;
    private int keyboardLandscapeHeight;
    private int lastHeight = -1;

    //在KeyboardPopWindow.class extends PopWindow.class中
    public KeyboardPopWindow(Activity activity) {
        super(activity);
        this.activity = activity;
        LayoutInflater inflator = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        this.popupView = inflator.inflate(R.layout.layout_livevideo_keyboard_popwindow, null, false);
        setContentView(popupView);
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);//为了当键盘变化时调整PopWindow的大小。
        setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        setWidth(0);
        setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        popupView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (popupView != null) {
                    handleOnGlobalLayout();
                }
            }
        });
    }

    private void handleOnGlobalLayout() {
        Point screenSize = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(screenSize);
        Rect rect = new Rect();
        popupView.getWindowVisibleDisplayFrame(rect);
        int orientation = activity.getResources().getConfiguration().orientation;

        //键盘高度=屏幕高度-popWindow的高度（需要设置   showAtLocation(parentView, Gravity.NO_GRAVITY, 0, 0);）
        int keyboardHeight = screenSize.y - rect.bottom;
        if (keyboardHeight == 0) {
            if (lastHeight != 0) {
                notifyKeyboardHeightChanged(0, orientation);
            }
            lastHeight = 0;
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            this.keyboardPortraitHeight = keyboardHeight;
            if (lastHeight != keyboardHeight) {
                notifyKeyboardHeightChanged(keyboardPortraitHeight, orientation);
            }
            lastHeight = keyboardHeight;
        } else {
            this.keyboardLandscapeHeight = keyboardHeight;
            if (lastHeight != keyboardHeight) {
                notifyKeyboardHeightChanged(keyboardLandscapeHeight, orientation);
            }
            lastHeight = keyboardHeight;
        }
    }

    public interface KeyboardObserver {
        void onKeyboardHeightChanged(int height, int orientation);
    }

    private void notifyKeyboardHeightChanged(int height, int orientation) {
        if (observer != null) {
            logger.d("软键盘状态： " + height);
            observer.onKeyboardHeightChanged(height, orientation);
        }
    }

    private KeyboardObserver observer;

    public void setKeyboardObserver(KeyboardObserver observer) {
        this.observer = observer;
    }
}
